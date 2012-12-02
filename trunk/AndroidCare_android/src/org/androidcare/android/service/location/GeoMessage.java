package org.androidcare.android.service.location;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.androidcare.android.service.ConnectionService;
import org.androidcare.android.service.Message;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.message.BasicNameValuePair;

import android.location.Location;

@SuppressWarnings("serial")
public class GeoMessage extends Message {
    public static final String POSITION_LOG_URL = ConnectionService.APP_URL + "api/addPosition";

    Location location;

    public GeoMessage(Location location) {
        super();
        this.url = GeoMessage.POSITION_LOG_URL;
        this.location = location;
    }

    @Override
    public HttpRequestBase getHttpRequestBase() throws UnsupportedEncodingException {
        HttpPost httppost = new HttpPost(this.url);

        List<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>(2);
        nameValuePairs.add(new BasicNameValuePair("latitude", String.valueOf(location.getLatitude())));
        nameValuePairs.add(new BasicNameValuePair("longitude", String.valueOf(location.getLongitude())));

        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

        return httppost;
    }
}
