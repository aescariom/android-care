/** 
 * This library/program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library/program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this library/program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package org.androidcare.android.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.androidcare.android.PreferencesActivity;
import org.androidcare.android.R;
import org.androidcare.android.ReminderReceiver;
import org.androidcare.android.util.NoDateFoundException;
import org.androidcare.android.util.Reminder;
import org.androidcare.android.util.TimeManager;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;

/**
 * Main Local Service
 * @author Alejandro Escario Méndez
 *
 */
public class ReminderService extends Service {
	public static final String APP_URL = "http://androidcare2.appspot.com/";
	public static final String REMINDERS_URL = APP_URL + "api/retrieveAlerts";
	public static final String REMINDERS_LOG_URL = APP_URL + "api/addAlertLog";
	private static final int REMINDER_REQUEST_CODE = 0;

	private static final int NOTIFICATION_ADD_ACCOUNT = 0;
	private static final int NOTIFICATION_SELECT_ACCOUNT = 1;
	private static final int NOTIFICATION_NO_CONNECTION = 2;
	
    protected AccountManager accountManager;
    protected Account user = null;
    private DefaultHttpClient httpClient = new DefaultHttpClient();
    private final IBinder binder = new LocalServiceBinder();

	protected final String tag="AndroidCare service";
	private final List<Intent> reminderIntents = new ArrayList<Intent>();
	
	/**
	 * this class will allow us to connect activities with a running instance of the service
	 * @author Alejandro Escario Méndez
	 *
	 */
	public class LocalServiceBinder extends Binder 
    { 
		public ReminderService getService() 
        { 
            return ReminderService.this; 
        } 
    } 
	
	/**
	 * 
	 */
	@Override
	public void onCreate() {
	    super.onCreate();
	    Log.i(tag, "Service created...");
	}
	
	private void authenticate() throws ReminderServiceException {	    
	    //1 - getting the google account that is used to run the service
	    Context ctx = getApplicationContext();
	    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
	    
	    String googleAccount = prefs.getString("account", "");
	    
	    if(googleAccount.trim().compareTo("") == 0){
	    	throw new ReminderServiceException("Zero accounts configured");
	    }else{
		    //1 - retrieving all the google accounts
	        accountManager = AccountManager.get(getApplicationContext());
		    Account[] accounts = accountManager.getAccountsByType("com.google");
		    
		    //2 - by default we select the first one
		    if(accounts.length > 0){
		    	for(Account a : accounts){
		    		String name = a.name;
		    		if(name.compareToIgnoreCase(googleAccount) == 0){
				    	user = a;
				    	break;
		    		}
		    	}
		    	if(user == null){
			    	displayAccountSelectorNotification();
			    	throw new ReminderServiceException("Zero accounts configured");
		    	}else{
		    		Log.i("Account", user.toString());
		    		accountManager.getAuthToken(this.user, "ah", false, new GetAuthTokenCallback(), null); // "ah" retrieves the authToken for Google App Engine. "lh2" is for Picasa & youtube
		    	}
		    }else{
		    	displayAccountManagerNotification();
		    	throw new ReminderServiceException("Zero accounts found");
		    }
	    }
	    
	}
	
	private void displayNotification(CharSequence tickerText, CharSequence contentTitle, CharSequence contentText, int notificationId, Object action) {
		//1 - getting the notification manager
		NotificationManager notificationManager =
			    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		
		//2 - Instantiate the notification
		int icon = R.drawable.notification_icon;
		long when = System.currentTimeMillis();

		Notification notification = new Notification(icon, tickerText, when);
		
		// let's make the notification to disappear when we click on it
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		
		//3 - define the notification's message
		Context context = getApplicationContext();
		Intent notificationIntent = null;
		if(action instanceof Class<?>){
			notificationIntent = new Intent(this, (Class<?>)action);
		}else if(action instanceof String){
			notificationIntent = new Intent((String)action);
		}
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

		notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);

		//4 - sending the notification
		notificationManager.notify(notificationId, notification);
	}

	private void displayAccountSelectorNotification() {
		CharSequence tickerText = getResources().getString(R.string.error);
		CharSequence contentTitle = getResources().getString(R.string.zero_accounts);
		CharSequence contentText = getResources().getString(R.string.setup_google_account);
		this.displayNotification(tickerText, contentTitle, contentText, ReminderService.NOTIFICATION_SELECT_ACCOUNT, PreferencesActivity.class);
	}

	private void displayAccountManagerNotification() {
		CharSequence tickerText = getResources().getString(R.string.error);
		CharSequence contentTitle = getResources().getString(R.string.zero_accounts);
		CharSequence contentText = getResources().getString(R.string.setup_google_account);
		this.displayNotification(tickerText, contentTitle, contentText, ReminderService.NOTIFICATION_ADD_ACCOUNT, Settings.ACTION_SYNC_SETTINGS);
	}

	private void setConnectionErrorNotification() {
		CharSequence tickerText = getResources().getString(R.string.error);
		CharSequence contentTitle = getResources().getString(R.string.not_reachable);
		CharSequence contentText = getResources().getString(R.string.check_internet_connection);
		this.displayNotification(tickerText, contentTitle, contentText, ReminderService.NOTIFICATION_NO_CONNECTION, Settings.ACTION_WIFI_SETTINGS);
	}

	/**
	 * this method will ask for new alerts (in order to do that, we will have to use the selected credentials)
	 */
	private void askForReminders() {
        new AuthenticatedRequestTask().execute(ReminderService.REMINDERS_URL);
	}
    
	/**
	 * 
	 * @author Alejandro Escario Méndez
	 *
	 */
    private class GetAuthTokenCallback implements AccountManagerCallback {
        public void run(AccountManagerFuture result) {
            Bundle bundle;
            try {
                    bundle = (Bundle) result.getResult();
                    Intent intent = (Intent)bundle.get(AccountManager.KEY_INTENT);
                    if(intent != null) {
                            // User input required
                            startActivity(intent);
                    } else {
                            onGetAuthToken(bundle);
                    }
            } catch (OperationCanceledException e) {
                    e.printStackTrace();
            } catch (AuthenticatorException e) {
                    e.printStackTrace();
            } catch (IOException e) {
                    e.printStackTrace();
            }
        }
    };
    
    /**
     * 
     * @param bundle
     */
    protected void onGetAuthToken(Bundle bundle) {
        String auth_token = bundle.getString(AccountManager.KEY_AUTHTOKEN);
        new GetCookieTask().execute(auth_token);
    }
    
    /**
     * 
     * @author Alejandro Escario Méndez
     *
     */
    private class GetCookieTask extends AsyncTask<Object, Object, Object> {
    	
    	/**
    	 * 
    	 */
        protected Boolean doInBackground(Object... tokens) {
    		DefaultHttpClient hc = getHttpClient();
    		HttpResponse response = null;
            try {
                    // Don't follow redirects
                    hc.getParams().setBooleanParameter(ClientPNames.HANDLE_REDIRECTS, false);
                    
                    HttpGet httpGet = new HttpGet(ReminderService.APP_URL + "_ah/login?continue=http://localhost/&auth=" + tokens[0].toString()); // we use localhost because we want to talk with our android device
                    
                    response = hc.execute(httpGet);
                    if(response.getStatusLine().getStatusCode() != 302)
                            // Response should be a redirect
                            return false;
                    
                    for(Cookie cookie : hc.getCookieStore().getCookies()) {
                            if(cookie.getName().equals("ACSID")){ // checking the authentication cookie
                            		Log.i("ReminderService", "Session started");
                                    return true;
                            }
                    }
            } catch (ClientProtocolException e) {
                    e.printStackTrace();
            } catch (IOException e) {
        		connectionFailed();
                Log.e("ReminderService", e.getMessage());
            } finally {
                hc.getParams().setBooleanParameter(ClientPNames.HANDLE_REDIRECTS, true);
                clearResponse(response);
                onAuth();
            }
            return false;
        }
    };

	private void clearResponse(HttpResponse response) {
		if(response != null){
        	try {
				EntityUtils.toString(response.getEntity());
			} catch (ParseException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
        }
	}
    
    /**
     * 
     * @author Alejandro Escario Méndez
     *
     */
    private class AuthenticatedRequestTask extends AsyncTask<Object, Object, HttpResponse> {
    	
    	/**
    	 * 
    	 */
        @Override
        protected HttpResponse doInBackground(Object... arg) {
        		HttpResponse rp = null;
                try {
                        HttpGet httpGet = new HttpGet(arg[0].toString());
                        rp = getHttpClient().execute(httpGet);
                } catch (ClientProtocolException e) {
                        e.printStackTrace();
                } catch (IOException e) {
            		connectionFailed();
                    Log.e("ReminderService", e.getMessage());
                }
                return rp;
        }
        
        /**
         * 
         */
        protected void onPostExecute(HttpResponse result) {
        	Reminder[] reminders = null;
        	if(result != null){
	            try {
	                BufferedReader reader = new BufferedReader(new InputStreamReader((result.getEntity().getContent())));	                
	                String aux = "";
	                String response = "";
	                while((aux = reader.readLine()) != null){
	                   	response += aux;
	                }
	                if(response.trim() == "[]" || response.trim() == "[]"){ 
	                	Log.e("Request", "Empty list, this may mean that the user is not logged in.");
	                	return;
	                }
	                JSONArray array = new JSONArray(response);
	                reminders = new Reminder[array.length()];
	                        
	                for(int i = 0; i < array.length(); i++){
	                   	JSONObject obj = array.getJSONObject(i);
	                   	reminders[i] = new Reminder(obj);
	                }
	                schedule(reminders);        
	            } catch (IllegalStateException e) {
	                e.printStackTrace();
	            } catch (IOException e) {
	                e.printStackTrace();
	            } catch (JSONException e) {
	            	e.printStackTrace();
				}finally{
	                // we don not need to clear the response, it already is
				}
        	}else{
        		connectionFailed();
        	}
        }
    };

	private void connectionFailed() {

		setConnectionErrorNotification();
	}
	
	private void cancelAllReminders() {
		while(this.reminderIntents.size() > 0){
			Intent i = this.reminderIntents.get(0);
			PendingIntent sender = PendingIntent.getBroadcast(this, ReminderService.REMINDER_REQUEST_CODE, i, Intent.FLAG_GRANT_READ_URI_PERMISSION);
			sender.cancel();
			this.reminderIntents.remove(0);
		}
	}

    /**
     * schedules a series of reminder using the notification center
     * @param reminders
     */
	private void schedule(Reminder[] reminders) {
		cancelAllReminders();
		for(Reminder a : reminders){
			schedule(a);
		}
	}

	/**
	 * schedules a reminder using the notification center
	 * @param a
	 */
	public void schedule(Reminder a){
		try {
			Calendar cal = Calendar.getInstance();
			Date next = TimeManager.getNextTimeLapse(a, cal.getTime());
			cal.setTime(next);
			
			Intent intent = new Intent(this.getApplicationContext(), ReminderReceiver.class);
			intent.setData(Uri.parse("androidCare://" + a.getId() + ".- " + a.getTitle()));

			intent.putExtra("reminder", a);
			intent.putExtra("prueba", "prueba");
			PendingIntent sender = PendingIntent.getBroadcast(this, ReminderService.REMINDER_REQUEST_CODE, intent, Intent.FLAG_GRANT_READ_URI_PERMISSION);
			
			 // Get the AlarmManager service
			 AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
			 
			 am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), sender);
			 //am.set(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis() + 60000, sender);
		     Log.i("ReminderScheduling",  "Reminder scheduled: " + a.getTitle() + " @ " + cal.getTime().toString());
		     this.reminderIntents.add(intent);
		} catch (NoDateFoundException e) {
	        Log.i("ReminderScheduling",  "Reminder not scheduled: " + a.getTitle());
		}
	}

	/**
	 * 
	 */
	@Override
	public void onStart(Intent intent, int startId) {      
	    super.onStart(intent, startId);
	    Log.i(tag, "Service started...");
	    
	    try {
			authenticate();
		} catch(ReminderServiceException ex){
	    	Log.e(tag, ex.description);
	    	try {
				this.finalize();
			} catch (Throwable e) {
		    	Log.e(tag, "The service couldn't be finished");
				e.printStackTrace();
			}
	    }
	}
	
	protected void onAuth(){
		askForReminders();
	}
	
	/**
	 * 
	 */
	@Override
	public void onDestroy() {
	    super.onDestroy();
	    Log.i(tag, "Service destroyed...");
	}

	/**
	 * 
	 */
	@Override
	public IBinder onBind(Intent intent) {
	    return binder;
	}

	public DefaultHttpClient getHttpClient() {
		return httpClient;
	}
}
