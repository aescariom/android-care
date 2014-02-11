package org.androidcare.android.service.alarms;

import android.util.Log;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.table.DatabaseTable;
import org.androidcare.android.alarms.Alarm;
import org.androidcare.android.database.DatabaseHelper;
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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
@DatabaseTable(tableName = "GetAlarmsMessage")
public class GetAlarmsMessage extends Message {

    public static final String ALARMS_URL = "api/retrieveAlarms";

    private DatabaseHelper databaseHelper = null;
    protected static AlarmService alarmService;
    private List<Alarm> alarms = new ArrayList();

    private final String TAG = this.getClass().getName();

    public GetAlarmsMessage(){
        super();
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
            this.addAlarmsToDatabase(this.alarms);

            Log.i(TAG, "Alarms updated from the server");
        }
        catch (Exception e) {
            Log.e(TAG, "Error when retrieving alarms from the server: " + e.getMessage(), e);
            throw new InvalidMessageResponseException("Error ocurend when parsing JSON String", e);
        }
    }
    
    @Override
    public void onError(Exception ex){
        super.onError(ex);
        this.addAlarmsToDatabase(this.alarms);
        Log.e(TAG, "No alarms could be retrieved from the server: ");
    }

    private void addAlarmsToDatabase(List<Alarm> alarms){
        for (Alarm alarm : alarms) {
            try {
                getHelper().getAlarmDao().createIfNotExists(alarm);
            }catch (SQLException e) {
                Log.e(TAG, "Could not insert the alarm: " + alarm + " -> " + e.toString());
            }
        }
        closeDatabaseConnection();
    }

    private DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(null, DatabaseHelper.class);
        }
        return databaseHelper;
    }

    private void closeDatabaseConnection() {
        if (databaseHelper != null) {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }
    }

}
