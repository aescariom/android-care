package org.androidcare.android.service;

import java.io.IOException;
import java.util.Date;
import java.util.PriorityQueue;
import java.util.Queue;

import org.androidcare.android.PreferencesActivity;
import org.androidcare.android.R;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;

public abstract class ConnectionService extends Service {
	public static final String APP_URL = "http://androidcare2.appspot.com/";

	private static final int NOTIFICATION_ADD_ACCOUNT = 0;
	private static final int NOTIFICATION_SELECT_ACCOUNT = 1;
	private static final int NOTIFICATION_NO_CONNECTION = 2;
	
	// auth settings
	protected Account googleUser = null;
	protected String authToken = "";
	private Cookie authCookie = null;
	
	// communcation
	private final Queue<Message> pendingMessages = new PriorityQueue<Message>();	// service info
	private final String tag = this.getClass().getName();
	private IntentFilter mNetworkStateChangedFilter;
    private BroadcastReceiver mNetworkStateIntentReceiver;

	private int maxPendingMessages = 10;
	private long maxTimeSiceFirstMessage = 900000;//15 min in milliseconds
	
	@Override
	public void onStart(Intent intent, int startId){
		super.onStart(intent, startId);
		
		setConnectionStateListener();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mNetworkStateIntentReceiver);
    }
	
	private void setConnectionStateListener() {
		mNetworkStateChangedFilter = new IntentFilter();
		mNetworkStateChangedFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);

		mNetworkStateIntentReceiver = new BroadcastReceiver() {
		    @Override
		    public void onReceive(Context context, Intent intent) {
				if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
				    NetworkInfo info = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
				    if(info.isAvailable()){
				    	ConnectionService.this.processMessageQueue();
				    	Log.i(tag, "Connection is back!");
				    }
				}
		    }
		};
		registerReceiver(mNetworkStateIntentReceiver, mNetworkStateChangedFilter);
	}

	private void getOauthCookie() throws ConnectionServiceException{
		//1 - fetching the selected Google account
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		
		String googleAccount = prefs.getString("account", "");
		
		if(googleAccount.isEmpty()){
			throw new ConnectionServiceException("Zero accounts configured");
		}
		
		//2 - getting the google accounts information
		AccountManager accountManager = AccountManager.get(getApplicationContext());
		Account[] accounts = accountManager.getAccountsByType("com.google");
		
		if(accounts.length <= 0){
			triggerAccountManagerNotification();
			throw new ConnectionServiceException("Zero accounts found in the device");
		}
		
		//3 - looking for the selected account
		for(Account acc : accounts){
			if(googleAccount.compareToIgnoreCase(acc.name) == 0){
				this.googleUser = acc;
				break;
			}
		}
		
		if(this.googleUser == null){
			triggerAccountSelectorNotification();
			throw new ConnectionServiceException("The selected account is not being used in the device");
		}
		
		//4 - getting the auth token
		Log.i(tag, "Selected account: " + this.googleUser.toString());
		accountManager.getAuthToken(this.googleUser, 
				"ah", // this is the "authTokenType" for google app engine 
				true, // notifyAuthFailure
				new GetAuthTokenCallback(), // callback
				null); // a null handler means that it will be handled by the main thread
	}
	
	/**
	 * this method processes the new session data and creates the cookie
	 * @param bundle
	 * @throws ConnectionServiceException
	 */
	public void getOauthCookie(Bundle bundle) throws ConnectionServiceException {
		//1 - get the auth token
		this.authToken = bundle.getString(AccountManager.KEY_AUTHTOKEN);
		if(this.authToken.isEmpty()){
			throw new ConnectionServiceException("AuthToken could not be fetched");
		}
		
		//2 - get the cookie
		DefaultHttpClient client = new DefaultHttpClient();
		HttpResponse response = null;
		// we don't want to follow redirects
		client.getParams().setBooleanParameter(ClientPNames.HANDLE_REDIRECTS, false);
		
		// the localhost statement in the url, means that we want to talk with our 
		// android device
		HttpGet httpGet = new HttpGet(ConnectionService.APP_URL + 
				"_ah/login?continue=http://localhost/&auth=" + this.authToken);
		
		try {
			response = client.execute(httpGet);
			
			// the response shuld be a redirect
			if(response.getStatusLine().getStatusCode() != 302){
				return;
			}
			
			for(Cookie c : client.getCookieStore().getCookies()){
				// we are looking for the authentication cookie
				if(c.getName().equals("ACSID")){
					this.authCookie = c;
					processMessageQueue();
					break;
				}
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public synchronized void pushMessage(Message m){
		this.pendingMessages.add(m);
		this.processMessageQueue();
	}
	
	public synchronized void pushLowPriorityMessage(Message m){
		this.pendingMessages.add(m);
		long timeDiff = (new Date()).getTime() - this.pendingMessages.peek().getCreationDate().getTime();
		if(this.pendingMessages.size() > maxPendingMessages ||
				timeDiff > maxTimeSiceFirstMessage){
			this.processMessageQueue();
		}
	}

	public synchronized void processMessageQueue() {
		Message m;
		if(this.pendingMessages.size() <= 0 || !isSessionCookieValid()){
			return;
		}
		if(!isConnectionAvailable()){
			triggerConnectionErrorNotification();
			return;
		}
		try {
			while((m = pendingMessages.peek()) != null){
				DefaultHttpClient client = getDefaultHttpClient();
				m.onPreSend();
				HttpRequestBase request = m.getHttpRequestBase();
				HttpResponse response = client.execute(request);
				m.onPostSend(response);
				pendingMessages.poll();
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidMessageResponseException e) {
			triggerConnectionErrorNotification();
		}
	}

	private DefaultHttpClient getDefaultHttpClient() {
		DefaultHttpClient client = new DefaultHttpClient();
		client.getCookieStore().addCookie(this.authCookie);
		return client;
	}

	private boolean isSessionCookieValid() {
		if(this.authCookie == null || this.authCookie.getExpiryDate().compareTo(new Date()) <= 0){
			try {
				getOauthCookie();
			} catch (ConnectionServiceException e) {
				e.printStackTrace();
			}
			return false;
		}
		return true;
	}
	
	protected void displayNotification(CharSequence tickerText,
			CharSequence contentTitle, 
			CharSequence contentText,
			int notificationId,
			Object action){
		//1 - getting the notification manager
		NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		
		//2 - Instantiate the notification
		long when = System.currentTimeMillis();
		
		Notification notification = new Notification(R.drawable.notification_icon, tickerText, when);
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		
		//3 - Define the notification's message
		Intent notificationIntent = null;
		if(action instanceof Class<?>){
			notificationIntent = new Intent(this, (Class<?>)action);
		}else if (action instanceof String){
			notificationIntent = new Intent((String)action);
		}
		
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
		notification.setLatestEventInfo(getApplicationContext(), contentTitle, contentText, contentIntent);
		
		//4 - Sending the notification to the SO
		manager.notify(notificationId, notification);
	}
	
	protected void triggerAccountSelectorNotification(){
		CharSequence tickerText = getResources().getString(R.string.error);
		CharSequence contentTitle = getResources().getString(R.string.zero_accounts);
		CharSequence contentText = getResources().getString(R.string.setup_google_account);
		
		this.displayNotification(tickerText, contentTitle, contentText, 
				ConnectionService.NOTIFICATION_SELECT_ACCOUNT, PreferencesActivity.class);
	}
	
	protected void triggerAccountManagerNotification(){
		CharSequence tickerText = getResources().getString(R.string.error);
		CharSequence contentTitle = getResources().getString(R.string.zero_accounts);
		CharSequence contentText = getResources().getString(R.string.setup_google_account);
		
		this.displayNotification(tickerText, contentTitle, contentText, 
				ConnectionService.NOTIFICATION_ADD_ACCOUNT, Settings.ACTION_SYNC_SETTINGS);
	}
	
	protected void triggerConnectionErrorNotification(){
		CharSequence tickerText = getResources().getString(R.string.error);
		CharSequence contentTitle = getResources().getString(R.string.not_reachable);
		CharSequence contentText = getResources().getString(R.string.check_internet_connection);
		
		this.displayNotification(tickerText, contentTitle, contentText, 
				ConnectionService.NOTIFICATION_NO_CONNECTION, Settings.ACTION_WIFI_SETTINGS);
	}
	
	protected boolean isConnectionAvailable(){
		ConnectivityManager conMgr =  (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
		if (conMgr == null) {
			return false;
		}
		NetworkInfo i = conMgr.getActiveNetworkInfo();
		if (i == null)
			return false;
		if (!i.isConnected())
			return false;
		if (!i.isAvailable())
			return false;
		return true;
	}
	
	/**
	 * google authentication callback class
	 */
	private class GetAuthTokenCallback implements AccountManagerCallback<Bundle>{

		public void run(AccountManagerFuture<Bundle> result) {
			try{
				Bundle bundle = (Bundle)result.getResult();
				Intent intent = (Intent) bundle.get(AccountManager.KEY_INTENT);
				if(intent != null){ // user input required
					startActivity(intent);
				}else{
					ConnectionService.this.getOauthCookie(bundle);
				}
			} catch(IOException e){
				e.printStackTrace();
			} catch (OperationCanceledException e) {
				e.printStackTrace();
			} catch (AuthenticatorException e) {
				e.printStackTrace();
			} catch (ConnectionServiceException e) {
				e.printStackTrace();
			}
		}
	}
}
