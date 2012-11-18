package org.androidcare.android.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.androidcare.android.util.Reminder;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GetSchedulableRemindersMessage extends Message {
	public static final String REMINDERS_URL = ConnectionService.APP_URL + "api/retrieveReminders";
	
	protected ReminderService reminderService;

	public GetSchedulableRemindersMessage(ReminderService reminderService){
		super();
		this.url = GetSchedulableRemindersMessage.REMINDERS_URL;
		this.reminderService = reminderService;
	}
	
	@Override
	public HttpRequestBase getHttpRequestBase() throws UnsupportedEncodingException {
		HttpGet get = new HttpGet(this.url);
		return get;
	}
	
	@Override
	public void onPostSend(HttpResponse response) throws InvalidMessageResponseException{
		super.onPostSend(response);
		Reminder[] reminders = null;
		
		try {
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(
							response.getEntity().getContent()));
			String jsonString = "";
			String aux = "";
			while((aux = reader.readLine()) != null){
				jsonString += aux;
			}
			
			if(jsonString.isEmpty()){
				throw new InvalidMessageResponseException();
			}
			
			JSONArray array = new JSONArray(jsonString);
			reminders = new Reminder[array.length()];
			
			for(int i = 0; i < array.length(); i++){
				JSONObject obj = array.getJSONObject(i);
				reminders[i] = new Reminder(obj);
			}
			
			this.reminderService.schedule(reminders);
			
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
