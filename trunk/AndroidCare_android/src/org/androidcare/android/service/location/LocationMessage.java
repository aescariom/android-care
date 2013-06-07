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

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import android.location.Location;

@SuppressWarnings("serial")
@DatabaseTable(tableName = "LocationMessage")
public class LocationMessage extends Message {
    public static final String POSITION_LOG_URL = ConnectionService.getAppUrl() + "api/addPosition";

    @DatabaseField
    double latitude;
    @DatabaseField
    double longitude;

    public LocationMessage(){
        super();
        this.url = LocationMessage.POSITION_LOG_URL;
    }
    
    public LocationMessage(Location location) {
        super();
        this.url = LocationMessage.POSITION_LOG_URL;
        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();
    }

    @Override
    public HttpRequestBase getHttpRequestBase() throws UnsupportedEncodingException {
        HttpPost httppost = new HttpPost(this.url);

        List<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>(2);
        nameValuePairs.add(new BasicNameValuePair("latitude", String.valueOf(latitude)));
        nameValuePairs.add(new BasicNameValuePair("longitude", String.valueOf(longitude)));
        nameValuePairs.add(new BasicNameValuePair("time", format.format(creationDate)));

        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

        return httppost;
    }
    @Override
    public String toString(){
        return "Location message created at " + creationDate + "; latitude: " + latitude + "; latitude: " + latitude;
    }
}
