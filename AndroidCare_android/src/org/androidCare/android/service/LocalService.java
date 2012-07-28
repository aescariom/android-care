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

package org.androidCare.android.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Date;

import org.androidCare.android.AlarmReceiver;
import org.androidCare.android.exceptions.NoDateFoundException;
import org.androidCare.android.miscellaneous.Constants;
import org.androidCare.android.objects.Alert;
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
	
    protected AccountManager accountManager;
    protected Account user = null;
    private DefaultHttpClient http_client = new DefaultHttpClient();
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
        accountManager.getAuthToken(this.user, "ah", false, new GetAuthTokenCallback(), null);
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
    private class GetCookieTask extends AsyncTask {
    	
    	/**
    	 * 
    	 */
        protected Boolean doInBackground(Object... tokens) {
            try {
                    // Don't follow redirects
                    getHttp_client().getParams().setBooleanParameter(ClientPNames.HANDLE_REDIRECTS, false);
                    
                    HttpGet http_get = new HttpGet(Constants.APP_URL + "_ah/login?continue=http://localhost/&auth=" + tokens[0].toString());
                    HttpResponse response;
                    response = getHttp_client().execute(http_get);
                    if(response.getStatusLine().getStatusCode() != 302)
                            // Response should be a redirect
                            return false;
                    
                    for(Cookie cookie : getHttp_client().getCookieStore().getCookies()) {
                            if(cookie.getName().equals("ACSID"))
                                    return true;
                    }
            } catch (ClientProtocolException e) {
                    e.printStackTrace();
            } catch (IOException e) {
                    e.printStackTrace();
            } finally {
                    getHttp_client().getParams().setBooleanParameter(ClientPNames.HANDLE_REDIRECTS, true);
            }
            return false;
        }
        
        /**
         * 
         */
        protected void onPostExecute(Object result) {
                new AuthenticatedRequestTask().execute(Constants.ALERTS_URL);
        }
    };
    
    /**
     * 
     * @author Alejandro Escario M始dez
     *
     */
    private class AuthenticatedRequestTask extends AsyncTask {
    	
    	/**
    	 * 
    	 */
        @Override
        protected HttpResponse doInBackground(Object... arg) {
                try {
                        HttpGet http_get = new HttpGet(arg[0].toString());
                        return getHttp_client().execute(http_get);
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
        	Alert[] alerts = null;
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
                alerts = new Alert[array.length()];
                        
                for(int i = 0; i < array.length(); i++){
                   	JSONObject obj = array.getJSONObject(i);
                   	alerts[i] = new Alert(obj);
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
	private void schedule(Alert[] alerts) {
		for(Alert a : alerts){
			schedule(a);
		}
	}
	
	/**
	 * schedules an alert using the notification center
	 * @param a
	 */
	public void schedule(Alert a){
		try {
			Calendar cal = Calendar.getInstance();
			Date next = a.getNextTimeLapse(cal.getTime());
			cal.setTime(next);
			
			Intent intent = new Intent(this.getApplicationContext(), AlarmReceiver.class);
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

	public DefaultHttpClient getHttp_client() {
		return http_client;
	}
}
