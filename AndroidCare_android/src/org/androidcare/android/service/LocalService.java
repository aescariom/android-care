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
import java.util.Calendar;
import java.util.Date;

import org.androidcare.android.ReminderReceiver;
import org.androidcare.android.util.NoDateFoundException;
import org.androidcare.android.util.Reminder;
import org.androidcare.android.util.TimeManager;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
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
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

/**
 * Main Local Service
 * @author Alejandro Escario M始dez
 *
 */
public class LocalService extends Service {
	public static final String APP_URL = "http://androidcare2.appspot.com/";
	public static final String ALERTS_URL = APP_URL + "api/retrieveAlerts";
	public static final String ALERT_LOG_URL = APP_URL + "api/addAlertLog";
	
    protected AccountManager accountManager;
    protected Account user = null;
    private DefaultHttpClient httpClient = new DefaultHttpClient();
    private final IBinder binder = new LocalServiceBinder();

	protected final String tag="AndroidCare service";
	
	/**
	 * this class will allow us to connect activities with a running instance of the service
	 * @author Alejandro Escario M始dez
	 *
	 */
	public class LocalServiceBinder extends Binder 
    { 
		public LocalService getService() 
        { 
            return LocalService.this; 
        } 
    } 
	
	/**
	 * 
	 */
	@Override
	public void onCreate() {
	    super.onCreate();
	    Log.i(tag, "Service created...");

	    //1 - retrieving all the google accounts
        accountManager = AccountManager.get(getApplicationContext());
	    Account[] accounts = accountManager.getAccountsByType("com.google");
	    
	    //2 - by default we select the first one
	    if(accounts.length > 0){
	    	user = accounts[0];
	    	Log.i("Account", user.toString());
	    	this.askForAlerts();
	    }else{
	    	Log.e(tag, "Set up a google account, please...");
	    }
	}
	
	/**
	 * this method will ask for new alerts (in order to do that, we will have to use the selected credentials)
	 */
	private void askForAlerts() {
        accountManager.getAuthToken(this.user, "ah", false, new GetAuthTokenCallback(), null); // "ah" retrieves the authToken for Google App Engine. "lh2" is for Picasa & youtube
	}
    
	/**
	 * 
	 * @author Alejandro Escario M始dez
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
     * @author Alejandro Escario M始dez
     *
     */
    private class GetCookieTask extends AsyncTask<Object, Object, Object> {
    	
    	/**
    	 * 
    	 */
        protected Boolean doInBackground(Object... tokens) {
            try {
                    // Don't follow redirects
                    getHttpClient().getParams().setBooleanParameter(ClientPNames.HANDLE_REDIRECTS, false);
                    
                    HttpGet httpGet = new HttpGet(LocalService.APP_URL + "_ah/login?continue=http://localhost/&auth=" + tokens[0].toString()); // we use localhost because we want to talk with our android device
                    HttpResponse response;
                    response = getHttpClient().execute(httpGet);
                    if(response.getStatusLine().getStatusCode() != 302)
                            // Response should be a redirect
                            return false;
                    
                    for(Cookie cookie : getHttpClient().getCookieStore().getCookies()) {
                            if(cookie.getName().equals("ACSID")) // checking the authentication cookie
                                    return true;
                    }
            } catch (ClientProtocolException e) {
                    e.printStackTrace();
            } catch (IOException e) {
                    e.printStackTrace();
            } finally {
                    getHttpClient().getParams().setBooleanParameter(ClientPNames.HANDLE_REDIRECTS, true);
            }
            return false;
        }
        
        /**
         * 
         */
        protected void onPostExecute(Object result) {
                new AuthenticatedRequestTask().execute(LocalService.ALERTS_URL);
        }
    };
    
    /**
     * 
     * @author Alejandro Escario M始dez
     *
     */
    private class AuthenticatedRequestTask extends AsyncTask<Object, Object, Object> {
    	
    	/**
    	 * 
    	 */
        @Override
        protected HttpResponse doInBackground(Object... arg) {
                try {
                        HttpGet httpGet = new HttpGet(arg[0].toString());
                        return getHttpClient().execute(httpGet);
                } catch (ClientProtocolException e) {
                        e.printStackTrace();
                } catch (IOException e) {
                        e.printStackTrace();
                }
                return null;
        }
        
        /**
         * 
         */
        protected void onPostExecute(Object result) {
        	Reminder[] alerts = null;
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(((HttpResponse)result).getEntity().getContent()));
                String response = "";
                String aux = "";
                while((aux = reader.readLine()) != null){
                   	response += aux;
                }
                if(response.isEmpty()){ 
                	Log.e("Request", "Empty response, this means that the user is not logged in.");
                	return;
                }
                JSONArray array = new JSONArray(response);
                alerts = new Reminder[array.length()];
                        
                for(int i = 0; i < array.length(); i++){
                   	JSONObject obj = array.getJSONObject(i);
                   	alerts[i] = new Reminder(obj);
                }
                schedule(alerts);        
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
            	e.printStackTrace();
			}
        }
    };

    /**
     * schedules a series of alerts using the notification center
     * @param alerts
     */
	private void schedule(Reminder[] alerts) {
		for(Reminder a : alerts){
			schedule(a);
		}
	}
	
	/**
	 * schedules an alert using the notification center
	 * @param a
	 */
	public void schedule(Reminder a){
		try {
			Calendar cal = Calendar.getInstance();
			Date next = TimeManager.getNextTimeLapse(a, cal.getTime());
			cal.setTime(next);
			
			Intent intent = new Intent(this.getApplicationContext(), ReminderReceiver.class);
			intent.setData(Uri.parse("timer:" + a.getTitle()));

			intent.putExtra("alert", a);
			// In reality, you would want to have a static variable for the request code instead of 192837
			PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent, Intent.FLAG_GRANT_READ_URI_PERMISSION);
			 
			 // Get the AlarmManager service
			 AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);

			 am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), sender);
		     Log.i("AlertScheduling",  "Alert scheduled: " + a.getTitle() + " @ " + cal.getTime().toString());
		} catch (NoDateFoundException e) {
	        Log.i("AlertScheduling",  "Alert not scheduled: " + a.getTitle());
		}
	}
	
	/**
	 * 
	 */
	@Override
	public void onStart(Intent intent, int startId) {      
	    super.onStart(intent, startId);
	    Log.i(tag, "Service started...");
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
