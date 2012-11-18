package org.androidcare.android.service;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.message.BasicNameValuePair;

import android.location.Location;

public class GeoMessage extends Message {
	public static final String POSITION_LOG_URL = ConnectionService.APP_URL + "api/addPosition";
	
	Location location;
	
	public GeoMessage(Location location){
		super();
		this.url = GeoMessage.POSITION_LOG_URL;
		this.location = location;
	}

	@Override
	public HttpRequestBase getHttpRequestBase()
			throws UnsupportedEncodingException {
		//1 - Set Connection
		HttpPost httppost = new HttpPost(this.url);
		
		//2 - Add your data
		List<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>(2);
		nameValuePairs.add(new BasicNameValuePair("latitude", String.valueOf(this.location.getLatitude())));
		nameValuePairs.add(new BasicNameValuePair("longitude", String.valueOf(this.location.getLongitude())));
			
		httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		
		return httppost;
	}

}
