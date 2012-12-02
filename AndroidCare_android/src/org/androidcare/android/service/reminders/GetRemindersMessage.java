package org.androidcare.android.service.reminders;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.androidcare.android.reminders.Reminder;
import org.androidcare.android.service.ConnectionService;
import org.androidcare.android.service.InvalidMessageResponseException;
import org.androidcare.android.service.Message;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

@SuppressWarnings("serial")
public class GetRemindersMessage extends Message {
    public static final String REMINDERS_URL = ConnectionService.APP_URL + "api/retrieveReminders";

    protected ReminderService reminderService;

    public GetRemindersMessage(ReminderService reminderService) {
        super();
        this.url = GetRemindersMessage.REMINDERS_URL;
        this.reminderService = reminderService;
    }

    @Override
    public HttpRequestBase getHttpRequestBase() throws UnsupportedEncodingException {
        HttpGet get = new HttpGet(this.url);
        return get;
    }

    @Override
    public void onPostSend(HttpResponse response) throws InvalidMessageResponseException {
        super.onPostSend(response);
        Reminder[] reminders = null;

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity()
                    .getContent()));
            String jsonString = "";
            String aux = "";
            while ((aux = reader.readLine()) != null) {
                jsonString += aux;
            }

            if (jsonString.isEmpty()) {
                throw new InvalidMessageResponseException("No JSON String received");
            }

            JSONArray array = new JSONArray(jsonString);
            reminders = new Reminder[array.length()];

            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                reminders[i] = new Reminder(obj);
            }
            this.reminderService.schedule(reminders);
        }
        catch (Exception e) {
            Log.e(this.getClass().getName(),
                    "Error when retrieving reminders from the server: " + e.getMessage(), e);
            throw new InvalidMessageResponseException("Error ocurend when parsing JSON String", e);
        }
    }
}
