package org.androidcare.android.service;

import java.io.IOException;
import java.util.Date;
import java.util.PriorityQueue;
import java.util.Queue;

import org.androidcare.android.R;
import org.androidcare.android.preferences.PreferencesActivity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
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
    private final Queue<Message> pendingMessages = new PriorityQueue<Message>(); // service info
    private final String tag = this.getClass().getName();
    private IntentFilter mNetworkStateChangedFilter;
    private BroadcastReceiver mNetworkStateIntentReceiver;
    
    //intent broadcast receiver
    private ConnectionServiceBroadcastReceiver connectionServiceReceiver = new ConnectionServiceBroadcastReceiver(this);
    private IntentFilter filter = new IntentFilter(ConnectionServiceBroadcastReceiver.ACTION_POST_MESSAGE);

    private int maxPendingMessages = 10;
    private long maxTimeSiceFirstMessage = 900000; // 15 min in milliseconds

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        registerReceiver(connectionServiceReceiver, filter);
        setConnectionStateListener();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mNetworkStateIntentReceiver);
        unregisterReceiver(connectionServiceReceiver);
        this.processMessageQueue();
    }

    private void setConnectionStateListener() {
        mNetworkStateChangedFilter = new IntentFilter();
        mNetworkStateChangedFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        mNetworkStateIntentReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                    NetworkInfo info = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
                    if (info.isAvailable()) {
                        ConnectionService.this.processMessageQueue();
                        Log.i(tag, "Connection is back!");
                    }
                }
            }
        };
        registerReceiver(mNetworkStateIntentReceiver, mNetworkStateChangedFilter);
    }

    // Comentario siempre es preferible usar un lock sobre un detalle de implementación interno
    // del objeto (pendingMessages) que sólo tú vas a tocar que sobre el propio objeto
    // ya que alguien (por ejemplo, el framework donde se ejecute tu código, u otro programador
    // en otra parte del código) podría tomar lock ssobre este objeto por algún otro motivo
    // por ejemplo, Android podría tomar un lock sobre este servicio por algún motivo
    // que no tiene nada que ver con acceder a la cola de mensajes pendientes, y tendríamos un
    // lock más "contended" de lo realmente necesario
    public void processMessageQueue() {
        synchronized (pendingMessages) {
            Message message;
            if (this.pendingMessages.size() == 0 || !isSessionCookieValid()) {
                return;
            }
            if (!isConnectionAvailable()) {
                triggerConnectionErrorNotification();
                return;
            }
            try {
                while ((message = pendingMessages.peek()) != null) {
                    HttpClient client = DefaultHttpClientFactory.getDefaultHttpClient(
                            this.getApplicationContext(), authCookie);
                    HttpRequestBase request = message.getHttpRequestBase();
                    message.onPreSend(request);
                    HttpResponse response = client.execute(request);
                    message.onPostSend(response);
                    pendingMessages.poll();
                }
            }
            catch (ClientProtocolException e) {
                e.printStackTrace();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            catch (InvalidMessageResponseException e) {
                triggerConnectionErrorNotification();
            }
        }
    }

    private boolean isSessionCookieValid() {
        if (this.authCookie == null || this.authCookie.getExpiryDate().compareTo(new Date()) <= 0) {
            try {
                getOauthCookie();
            }
            catch (ConnectionServiceException e) {
                e.printStackTrace();
            }
            return false;
        }
        return true;
    }

    // comentario hacer mock para no necesitar cuenta en el emulador;
    // Yo usaría una variable distinta que la usada para el HttpClient para poder
    // hacer mock de cualquiera de ellos o de ambos
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
        accountManager.getAuthToken(this.googleUser, "ah", // this is the
                                                           // "authTokenType"
                                                           // for google app
                                                           // engine
                true, // notifyAuthFailure
                new GetAuthTokenCallback(), // callback
                null); // a null handler means that it will be handled by the
                       // main thread
    }

    protected void triggerAccountSelectorNotification() {
        CharSequence tickerText = getResources().getString(R.string.error);
        CharSequence contentTitle = getResources().getString(R.string.zero_accounts);
        CharSequence contentText = getResources().getString(R.string.setup_google_account);

        this.displayNotification(tickerText, contentTitle, contentText,
                ConnectionService.NOTIFICATION_SELECT_ACCOUNT, PreferencesActivity.class);
    }

    protected void triggerAccountManagerNotification() {
        CharSequence tickerText = getResources().getString(R.string.error);
        CharSequence contentTitle = getResources().getString(R.string.zero_accounts);
        CharSequence contentText = getResources().getString(R.string.setup_google_account);

        this.displayNotification(tickerText, contentTitle, contentText,
                ConnectionService.NOTIFICATION_ADD_ACCOUNT, Settings.ACTION_SYNC_SETTINGS);
    }

    protected void triggerConnectionErrorNotification() {
        CharSequence tickerText = getResources().getString(R.string.error);
        CharSequence contentTitle = getResources().getString(R.string.not_reachable);
        CharSequence contentText = getResources().getString(R.string.check_internet_connection);

        this.displayNotification(tickerText, contentTitle, contentText,
                ConnectionService.NOTIFICATION_NO_CONNECTION, Settings.ACTION_WIFI_SETTINGS);
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
        if (!networkInfo.isConnected()) return false;
        if (!networkInfo.isAvailable()) return false;
        return true;
    }


    public void pushMessage(Message message) {
        synchronized (pendingMessages) {
            this.pendingMessages.add(message);
            this.processMessageQueue();
        }
    }

    public void pushLowPriorityMessage(Message message) {
        synchronized (pendingMessages) {
            this.pendingMessages.add(message);
            long timeDiff = (new Date()).getTime() - this.pendingMessages.peek().getCreationDate().getTime();
            if (this.pendingMessages.size() > maxPendingMessages || timeDiff > maxTimeSiceFirstMessage) {
                this.processMessageQueue();
            }
        }
    }

    /**
     * google authentication callback class
     */
    private class GetAuthTokenCallback implements AccountManagerCallback<Bundle> {

        public void run(AccountManagerFuture<Bundle> result) {
            try {
                Bundle bundle = (Bundle) result.getResult();
                Intent intent = (Intent) bundle.get(AccountManager.KEY_INTENT);
                if (intent != null) { // user input required
                    startActivity(intent);
                } else {
                    ConnectionService.this.getOauthCookie(bundle);
                }
            }
            
            //nno piloto todavía del tema de log en Android pero ¿no deberíamos usar Log.e?
            catch (IOException e) {
                e.printStackTrace();
            }
            catch (OperationCanceledException e) {
                e.printStackTrace();
            }
            catch (AuthenticatorException e) {
                e.printStackTrace();
            }
            catch (ConnectionServiceException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Callback methid for GetAuthTokenCallback
     */
    public void getOauthCookie(Bundle bundle) throws ConnectionServiceException {
        // 1 - get the auth token
        this.authToken = bundle.getString(AccountManager.KEY_AUTHTOKEN);
        if (this.authToken.isEmpty()) {
            throw new ConnectionServiceException("AuthToken could not be fetched");
        }

        // 2 - get the cookie
        DefaultHttpClient client = new DefaultHttpClient();
        HttpResponse response = null;
        // we don't want to follow redirects
        client.getParams().setBooleanParameter(ClientPNames.HANDLE_REDIRECTS, false);

        // the localhost statement in the url, means that we want to talk with
        // our
        // android device
        HttpGet httpGet = new HttpGet(ConnectionService.APP_URL
                + "_ah/login?continue=http://localhost/&auth=" + this.authToken);

        try {
            response = client.execute(httpGet);
            // the response shuld be a redirect
            if (response.getStatusLine().getStatusCode() != 302) {
                //comentario ¿no deberíamos avisar/hacer algo con el problema en vez de "callar"?
               // return;
                throw new ConnectionServiceException("Response was not a redirection");
            }

            for (Cookie cookoe : client.getCookieStore().getCookies()) {
                // we are looking for the authentication cookie
                if (cookoe.getName().equals("ACSID")) {
                    this.authCookie = cookoe;
                    processMessageQueue();
                    break;
                }
            }
        }
        catch (ClientProtocolException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
