package org.androidcare.android.service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.androidcare.android.R;
import org.androidcare.android.database.DatabaseHelper;
import org.androidcare.android.mock.MockHttpClient;
import org.androidcare.android.preferences.PreferencesActivity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.protocol.HttpContext;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.AlarmManager;
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
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;

import com.j256.ormlite.android.apptools.OpenHelperManager;

public class ConnectionService extends Service {
    public static final String APP_URL = "http://androidcare2.appspot.com/";

    private static final int NOTIFICATION_ADD_ACCOUNT = 0;
    private static final int NOTIFICATION_SELECT_ACCOUNT = 1;
    private static final int NOTIFICATION_NO_CONNECTION = 2;

    // auth settings
    private Account googleUser = null;
    private String authToken = "";
    private Cookie authCookie = null;

    // Communication
    private final String tag = this.getClass().getName();
    private IntentFilter mNetworkStateChangedFilter;
    private BroadcastReceiver mNetworkStateIntentReceiver;

    // intent broadcast receiver
    private ConnectionServiceBroadcastReceiver connectionServiceReceiver = 
                                                            new ConnectionServiceBroadcastReceiver(this);
    private IntentFilter filter = new IntentFilter(ConnectionServiceBroadcastReceiver.ACTION_POST_MESSAGE);

    private long timeLapse = 5*60*1000; // 5 min in milliseconds

    // Binder given to clients
    private final IBinder mBinder = new ConnectionServiceBinder();
    
    private boolean isMock = false;
    private boolean loggingIn = false;

    private DatabaseHelper databaseHelper = null;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int result = super.onStartCommand(intent, flags, startId);
        Log.i(tag, "Connection service started");
        registerReceiver(connectionServiceReceiver, filter);
        setConnectionStateListener();
        isMock = getApplicationContext().getResources().getBoolean(R.bool.mock);
        
        scheduleNextSynchronization();
        
        return result;
    }

    private void setConnectionStateListener() {
        mNetworkStateChangedFilter = new IntentFilter();
        mNetworkStateChangedFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        mNetworkStateIntentReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                NetworkInfo info = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
                
                if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                    if (info.isAvailable()) {                        
                        removeConnectionErrorNotification();
                        processMessageQueue();
                        Log.i(tag, "Connection is back!");
                    }
                }
            }
        };
        registerReceiver(mNetworkStateIntentReceiver, mNetworkStateChangedFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mNetworkStateIntentReceiver);
        unregisterReceiver(connectionServiceReceiver);
        
        closeDatabaseConnection();
    }

    public void processMessageQueue() {
        new MessageQueueProcessor().execute((Void)null);
    }

    private boolean isSessionCookieValid() {
        if (!isMock && (authCookie == null || authCookie.getExpiryDate().compareTo(new Date()) <= 0)) {
            if(!loggingIn){
                loggingIn = true;
                try {
                    getOauthCookie();
                }
                catch (ConnectionServiceException e) {
                    triggerAccountSelectorNotification();
                    Log.e(tag, "Error when procesing the MessageQueue: " + e.getMessage(), e);
                }
            }
            // this is always false, because we need to get the authCookie before we can send more messages
            return false;  
        }else{
            return true;
        }
    }

    private void getOauthCookie() throws ConnectionServiceException {
        // 1 - fetching the selected Google account
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        String googleAccount = prefs.getString("account", "");

        if (googleAccount.isEmpty()) {
            throw new ConnectionServiceException("Zero accounts configured");
        }

        // 2 - getting the google accounts information
        AccountManager accountManager = AccountManager.get(getApplicationContext());
        Account[] accounts = accountManager.getAccountsByType("com.google");

        if (accounts.length == 0) {
            triggerAccountManagerNotification();
            throw new ConnectionServiceException("Zero accounts found in the device");
        }

        // 3 - looking for the selected account
        for (Account acc : accounts) {
            if (googleAccount.compareToIgnoreCase(acc.name) == 0) {
                this.googleUser = acc;
                break;
            }
        }

        if (this.googleUser == null) {
            triggerAccountSelectorNotification();
            throw new ConnectionServiceException("The selected account is not being used in the device");
        }

        // 4 - getting the auth token
        Log.i(tag, "Selected account: " + this.googleUser.toString());
        accountManager.getAuthToken(this.googleUser, "ah", // this is the "authTokenType" for google App Engine
                true, // notifyAuthFailure
                new GetAuthTokenCallback(), // callback
                null); // a null handler means that it will be handled by the main thread
    }

    protected void triggerAccountSelectorNotification() {
        CharSequence tickerText = getResources().getString(R.string.error);
        CharSequence contentTitle = getResources().getString(R.string.zero_accounts);
        CharSequence contentText = getResources().getString(R.string.setup_google_account);

        displayNotification(tickerText, contentTitle, contentText,
                ConnectionService.NOTIFICATION_SELECT_ACCOUNT, PreferencesActivity.class);
    }

    protected void triggerAccountManagerNotification() {
        CharSequence tickerText = getResources().getString(R.string.error);
        CharSequence contentTitle = getResources().getString(R.string.zero_accounts);
        CharSequence contentText = getResources().getString(R.string.setup_google_account);

        displayNotification(tickerText, contentTitle, contentText,
                ConnectionService.NOTIFICATION_ADD_ACCOUNT, Settings.ACTION_SYNC_SETTINGS);
    }

    protected void triggerConnectionErrorNotification() {
        CharSequence tickerText = getResources().getString(R.string.error);
        CharSequence contentTitle = getResources().getString(R.string.not_reachable);
        CharSequence contentText = getResources().getString(R.string.check_internet_connection);

        displayNotification(tickerText, contentTitle, contentText,
                ConnectionService.NOTIFICATION_NO_CONNECTION, Settings.ACTION_WIFI_SETTINGS);
    }

    protected void removeConnectionErrorNotification() {
        cancelNotification(ConnectionService.NOTIFICATION_NO_CONNECTION);
    }
    
    protected void cancelNotification(int notifyId) {
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(notifyId);
    }

    protected void displayNotification(CharSequence tickerText, CharSequence contentTitle,
            CharSequence contentText, int notificationId, Object action) {
        // 1 - getting the notification manager
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // 2 - Instantiate the notification
        long when = System.currentTimeMillis();

        Notification notification = new Notification(R.drawable.notification_icon, tickerText, when);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        // 3 - Define the notification's message
        Intent notificationIntent = null;
        if (action instanceof Class<?>) {
            notificationIntent = new Intent(this, (Class<?>) action);
        } else if (action instanceof String) {
            notificationIntent = new Intent((String) action);
        }

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        notification.setLatestEventInfo(getApplicationContext(), contentTitle, contentText, contentIntent);

        // 4 - Sending the notification to the SO
        manager.notify(notificationId, notification);
    }

    protected boolean isConnectionAvailable() {        
        ConnectivityManager conMgr = (ConnectivityManager) getApplicationContext().getSystemService(
                Context.CONNECTIVITY_SERVICE);
        if (conMgr == null) {
            return false;
        }
        NetworkInfo networkInfo = conMgr.getActiveNetworkInfo();
        if (networkInfo == null) return false;
        if ( !networkInfo.isConnected()) return false;
        if ( !networkInfo.isAvailable()) return false;
        return true;
    }

    public void pushMessage(Message message) {
        try {
            getHelper().create(message);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        this.processMessageQueue();
    }

    public void pushLowPriorityMessage(Message message) {
        try {
            getHelper().create(message);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * google authentication callback class
     */
    private class GetAuthTokenCallback implements AccountManagerCallback<Bundle> {

        public void run(AccountManagerFuture<Bundle> result) {
            try {
                Bundle bundle = result.getResult();
                Intent intent = (Intent) bundle.get(AccountManager.KEY_INTENT);
                if (intent != null) { // user input required
                    startActivity(intent);
                    loggingIn = false;
                } else {
                    new GetCookieTask().execute(bundle);
                }
            }
            catch (Exception e) {
                Log.e(tag, "Error when getting the Oauth cookie: " + e.getMessage(), e);
                loggingIn = false;
            }
        }
    }


    /**
     * Callback method for GetAuthTokenCallback
     */
    public void getOauthCookie(Bundle bundle) throws ConnectionServiceException, ClientProtocolException,
            IOException {
        authToken = bundle.getString(AccountManager.KEY_AUTHTOKEN);
        if (authToken.isEmpty()) {
            loggingIn = false;
            throw new ConnectionServiceException("AuthToken could not be fetched");
        }

        // 2 - get the cookie
        DefaultHttpClient client = new DefaultHttpClient();
        HttpResponse response = null;
        // we don't want to follow redirects
        client.getParams().setBooleanParameter(ClientPNames.HANDLE_REDIRECTS, false);

        // the localhost statement in the url, means that we want to talk with our android device
        HttpGet httpGet = new HttpGet(ConnectionService.APP_URL
                + "_ah/login?continue=http://localhost/&auth=" + authToken);

        response = client.execute(httpGet);
        // the response should be a redirect
        if (response.getStatusLine().getStatusCode() != 302) {
            loggingIn = false;
            throw new ConnectionServiceException("Response was not a redirection");
        }

        List<Cookie> cookies = client.getCookieStore().getCookies();
        if(cookies.size() == 0){
            AccountManager accountManager = AccountManager.get(getApplicationContext());
            accountManager.invalidateAuthToken("com.google", authToken);
        }
        for (Cookie cookie : cookies) {
            // we are looking for the authentication cookie
            if (cookie.getName().equals("ACSID")) {
                Log.i(tag, "We are now logged in");
                authCookie = cookie;
                loggingIn = false;
                processMessageQueue();
                break;
            }
        }
        loggingIn = false;
    }
    
    private class GetCookieTask extends AsyncTask<Bundle, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Bundle... params) {
            Bundle bundle = params[0];
            try {
                ConnectionService.this.getOauthCookie(bundle);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }
    }

    private class MessageQueueProcessor extends AsyncTask<Void, Object, Boolean>{

        @Override
        protected Boolean doInBackground(Void... params) {
            try{
                if (!isConnectionAvailable()) {
                    triggerConnectionErrorNotification();
                    return false;
                }
                if (!isSessionCookieValid()) {
                    return false;
                }
                List<Message> messages = getHelper().getMessages();
                for (Message m : messages) {
                    try {

                        HttpClient client = DefaultHttpClientFactory.getDefaultHttpClient(
                                               ConnectionService.this.getApplicationContext(), authCookie);
                        HttpRequestRetryHandler retryHandler = new DefaultHttpRequestRetryHandler(5, false){
                            public boolean retryRequest(IOException ex, int execCount, HttpContext context){
                                if(!super.retryRequest(ex, execCount, context)){
                                    Log.d("HTTP retry-handler", "Won't retry");
                                    return false;
                                }
                                try{
                                    Thread.sleep(2000);
                                }catch(InterruptedException ie){}
                                Log.d("HTTP retry-handler", "Retrying request...");
                                return true;
                            }
                        };
                        HttpRequestBase request = m.getHttpRequestBase();
                        m.onPreSend(request);
                        HttpResponse response = null;
                        Log.d("HTTP client", "sending: " + m);
                        if(client instanceof MockHttpClient){
                            response = client.execute(request);
                        }else{
                            AbstractHttpClient retryClient = (AbstractHttpClient) client;
                            retryClient.setHttpRequestRetryHandler(retryHandler);
                            response = retryClient.execute(request);
                        }
                        m.onPostSend(response);
                        getHelper().remove(m);
                        Log.d("HTTP client", "Message processed: " + m);
                    }
                    catch (Exception e) {
                        Log.e(tag, "Error when procesing the Message: " + m + " -> " + e.getMessage(), e);
                        m.onError(e);
                    }
                }
                return true;
            }catch(SQLException e){
                Log.e(tag, "Error when procesing the Queue -> " + e.getMessage(), e);
            }
            return false;
        }
        
        protected void onPostExecute(Boolean result) {
            PushMessagesReceiver.releaseLock();
        }
        
    }
    
    @Override
    public IBinder onBind(Intent arg0) {
        return mBinder;
    }
    
    public class ConnectionServiceBinder extends Binder {
        public ConnectionService getService() {
            return ConnectionService.this;
        }
    }


    public void scheduleNextSynchronization() {
        Calendar cal = Calendar.getInstance();

        AlarmManager am = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), PushMessagesReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);
        am.cancel(pendingIntent);
        am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis() + this.timeLapse, pendingIntent);
    }
    
    private DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        }
        return databaseHelper;
    }
    
    private void closeDatabaseConnection() {
        if (databaseHelper != null) {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }
    }
}
