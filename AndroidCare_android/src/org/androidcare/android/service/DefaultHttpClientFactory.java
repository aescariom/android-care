package org.androidcare.android.service;

import org.androidcare.android.R;
import org.androidcare.android.mock.MockHttpClient;
import org.apache.http.client.HttpClient;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.util.Log;

public class DefaultHttpClientFactory {

    public static HttpClient getDefaultHttpClient(Context context, Cookie authCookie) {
        HttpClient client = null;
        boolean isMock = context.getResources().getBoolean(R.bool.mock);
        if (isMock) {
            client = new MockHttpClient(context);
            Log.w(DefaultHttpClientFactory.class.getName(), "Using MockHttpClient");
        } else {
            client = new DefaultHttpClient();
            ((DefaultHttpClient) client).getCookieStore().addCookie(authCookie);
        }
        return client;
    }
}
