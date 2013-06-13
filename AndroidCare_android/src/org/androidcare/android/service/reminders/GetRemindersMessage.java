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

import com.j256.ormlite.table.DatabaseTable;

import android.util.Log;

@SuppressWarnings("serial")
@DatabaseTable(tableName = "GetRemindersMessage")
public class GetRemindersMessage extends Message {
    public static final String REMINDERS_URL = "api/retrieveReminders";

    protected static ReminderService reminderService;

    public GetRemindersMessage(){
        super();
    }
    
    public GetRemindersMessage(ReminderService reminderService) {
        super();
        GetRemindersMessage.reminderService = reminderService;
    }
    
    public static void setReminderService(ReminderService reminderService){
        GetRemindersMessage.reminderService = reminderService;
    }

    @Override
    public HttpRequestBase getHttpRequestBase() throws UnsupportedEncodingException {
        HttpGet get = new HttpGet(ConnectionService.getAppUrl() + REMINDERS_URL);
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
            GetRemindersMessage.reminderService.schedule(reminders);
            Log.i(GetRemindersMessage.class.getName(), "Reminders updated from the server");
        }
        catch (Exception e) {
            Log.e(this.getClass().getName(),
                    "Error when retrieving reminders from the server: " + e.getMessage(), e);
            throw new InvalidMessageResponseException("Error ocurend when parsing JSON String", e);
        }
    }
    
    @Override
    public void onError(Exception ex){
        super.onError(ex);
        GetRemindersMessage.reminderService.scheduleFromDatabase();
        Log.e(this.getClass().getName(), "No reminders could be retrieved from the server: ");
    }
}
