package org.androidcare.android.service.alarms;

import android.util.Log;
import com.j256.ormlite.table.DatabaseTable;
import org.androidcare.android.alarms.Alarm;
import org.androidcare.android.service.ConnectionService;
import org.androidcare.android.service.InvalidMessageResponseException;
import org.androidcare.android.service.Message;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
@DatabaseTable(tableName = "GetAlarmsMessage")
public class GetAlarmsMessage extends Message {
    public static final String ALARMS_URL = "api/retrieveAlarms";

    protected static AlarmDownloadService alarmDownloadService;
    private List<Alarm> alarms = new ArrayList();

    public GetAlarmsMessage(){
        super();
    }

    public GetAlarmsMessage(AlarmDownloadService alarmService) {
        super();
        GetAlarmsMessage.alarmDownloadService = alarmService;
    }
    
    public static void setAlarmDownloadService(AlarmDownloadService alarmDownloadService){
        GetAlarmsMessage.alarmDownloadService = alarmDownloadService;
    }

    @Override
    public HttpRequestBase getHttpRequestBase() throws UnsupportedEncodingException {
        HttpGet get = new HttpGet(ConnectionService.getAppUrl() + ALARMS_URL);
        return get;
    }

    @Override
    public void onPostSend(HttpResponse response) throws InvalidMessageResponseException {
        super.onPostSend(response);
        Alarm[] alarms = null;

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String jsonString = "";
            String aux = "";

            while ((aux = reader.readLine()) != null) {
                jsonString += aux;
            }

            if (jsonString.isEmpty()) {
                throw new InvalidMessageResponseException("No JSON String received");
            }

            JSONArray array = new JSONArray(jsonString);
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                this.alarms.add(new Alarm(obj));
            }
            GetAlarmsMessage.alarmDownloadService.addAlarmsToDatabase(this.alarms);

            Log.i(GetAlarmsMessage.class.getName(), "Alarms updated from the server");
        }
        catch (Exception e) {
            Log.e(this.getClass().getName(),
                    "Error when retrieving alarms from the server: " + e.getMessage(), e);
            throw new InvalidMessageResponseException("Error ocurend when parsing JSON String", e);
        }
    }
    
    @Override
    public void onError(Exception ex){
        super.onError(ex);
        GetAlarmsMessage.alarmDownloadService.addAlarmsToDatabase(this.alarms);
        Log.e(this.getClass().getName(), "No alarms could be retrieved from the server: ");
    }
}
