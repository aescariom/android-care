package org.androidCare.android;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.Calendar;

import org.androidCare.android.exceptions.NoDateFoundException;
import org.androidCare.android.miscellaneous.Constants;
import org.androidCare.android.objects.Alert;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.AlarmManager;
import android.app.ListActivity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 
 * @author alejandro
 *
 */
public class AlertList extends ListActivity {
    DefaultHttpClient http_client = new DefaultHttpClient();

    /**
     * 
     */
    @Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	}

    /**
     * 
     */
    @Override
    protected void onResume() {
            super.onResume();
            Intent intent = getIntent();
            AccountManager accountManager = AccountManager.get(getApplicationContext());
            Account account = (Account)intent.getExtras().get("account");
            accountManager.getAuthToken(account, "ah", false, new GetAuthTokenCallback(), null);
    }
    
    /**
     * 
     * @author alejandro
     *
     */
    private class GetAuthTokenCallback implements AccountManagerCallback {
    	
    	/**
    	 * 
    	 */
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
                    // TODO Auto-generated catch block
                    e.printStackTrace();
            } catch (AuthenticatorException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
            } catch (IOException e) {
                    // TODO Auto-generated catch block
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
     * @author alejandro
     *
     */
    private class GetCookieTask extends AsyncTask {
    	
        protected Boolean doInBackground(Object... tokens) {
                try {
                        // Don't follow redirects
                        http_client.getParams().setBooleanParameter(ClientPNames.HANDLE_REDIRECTS, false);
                        
                        HttpGet http_get = new HttpGet(Constants.APP_URL + "_ah/login?continue=http://localhost/&auth=" + tokens[0].toString());
                        HttpResponse response;
                        response = http_client.execute(http_get);
                        if(response.getStatusLine().getStatusCode() != 302)
                                // Response should be a redirect
                                return false;
                        
                        for(Cookie cookie : http_client.getCookieStore().getCookies()) {
                                if(cookie.getName().equals("ACSID"))
                                        return true;
                        }
                } catch (ClientProtocolException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                } finally {
                        http_client.getParams().setBooleanParameter(ClientPNames.HANDLE_REDIRECTS, true);
                }
                return false;
        }
        
        protected void onPostExecute(Object result) {
                new AuthenticatedRequestTask().execute(Constants.ALERTS_URL);
        }
    };
    
    /**
     * 
     * @author alejandro
     *
     */
    private class AuthenticatedRequestTask extends AsyncTask {
    	
    	/**
    	 * 
    	 */
        @Override
        protected HttpResponse doInBackground(Object... arg0) {
                try {
                        HttpGet http_get = new HttpGet(arg0[0].toString());
                        return http_client.execute(http_get);
                } catch (ClientProtocolException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                } catch (IOException e) {
                        // TODO Auto-generated catch block
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
                JSONArray array = new JSONArray(response);
                alerts = new Alert[array.length()];
                        
                for(int i = 0; i < array.length(); i++){
                   	JSONObject obj = array.getJSONObject(i);
                   	alerts[i] = new Alert(obj);
                }
                fillList(alerts);        
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
     * 
     * @param alerts
     */
    protected void fillList(Alert[] alerts){
	    this.setListAdapter(new ArrayAdapter<Alert>(this, R.layout.simple_item_layout, alerts));        
    }
    
    /**
     * 
     */
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
    	Alert a = (Alert)getListView().getItemAtPosition(position);
		try {
			Date next = a.getNextTimeLapse(Calendar.getInstance().getTime());
			Calendar cal = Calendar.getInstance();
			cal.setTime(next);
			schedule(cal);
	        Toast.makeText(this, "Planificado: " + next, Toast.LENGTH_SHORT).show();
		} catch (NoDateFoundException e) {
	        Toast.makeText(this, "No se planificr‡", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
        
    }

    /**
     * 
     * @param cal
     */
	private void schedule(Calendar cal) {
		 Intent intent = new Intent(this.getApplicationContext(), AlarmReceiver.class);
		 intent.putExtra("alarm_message", "O'Doyle Rules!");
		 // In reality, you would want to have a static variable for the request code instead of 192837
		 PendingIntent sender = PendingIntent.getBroadcast(this, 192837, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		 
		 // Get the AlarmManager service
		 AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
		 am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), sender);
		 try {
			playSound(this.getApplicationContext());
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param context
	 * @throws IllegalArgumentException
	 * @throws SecurityException
	 * @throws IllegalStateException
	 * @throws IOException
	 */
	public void playSound(Context context) throws IllegalArgumentException, SecurityException, IllegalStateException,
    												IOException {
		Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		MediaPlayer mMediaPlayer = new MediaPlayer();
		mMediaPlayer.setDataSource(context, soundUri);
		final AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
		    mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
		    mMediaPlayer.setLooping(false);
		    mMediaPlayer.prepare();
		    mMediaPlayer.start();
		}
	}
}