package org.androidcare.android.service.alarms.messages;

import android.util.Log;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.table.DatabaseTable;
import org.androidcare.android.alarms.Alarm;
import org.androidcare.android.alarms.GeoPoint;
import org.androidcare.android.database.DatabaseHelper;
import org.androidcare.android.service.ConnectionService;
import org.androidcare.android.service.InvalidMessageResponseException;
import org.androidcare.android.service.Message;
import org.androidcare.android.service.alarms.AlarmManagerService;
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
    private List<Alarm> alarms = new ArrayList();

    private static AlarmManagerService alarmManagerService;

    private final String TAG = this.getClass().getName();

    public GetAlarmsMessage(){
        super();
    }

    public static void setAlarmManagerService (AlarmManagerService service) {
        alarmManagerService = service;
    }

    @Override
    public HttpRequestBase getHttpRequestBase() throws UnsupportedEncodingException {
        HttpGet get = new HttpGet(ConnectionService.getAppUrl() + ALARMS_URL);
        return get;
    }

    @Override
    public void onPostSend(HttpResponse response) throws InvalidMessageResponseException {
        /* aka onAfterSend */
        super.onPostSend(response);

        Log.d(TAG, "Starts processing alarms from server");

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String jsonString = "";
            String aux = "";

            while ((aux = reader.readLine()) != null) {
                jsonString += aux;
            }

            if (jsonString.isEmpty()) {
                throw new InvalidMessageResponseException("No JSON String received");
            } else {
                Log.d(TAG, "Unschedule all alarms");
                if (this.alarmManagerService != null) {
                    this.alarmManagerService.unscheduleAllDatabaseAlarms();
                }

                Log.d(TAG, "delete all alarms");
                this.removeAllAlarms();

                JSONArray array = new JSONArray(jsonString);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    this.alarms.add(new Alarm(obj, null));
                }

                Log.d(TAG, "add new alarms");
                this.addAlarmsToDatabase(this.alarms);
                Log.i(TAG, "Alarms updated from the server");

                this.alarmManagerService.scheduleAlarmsFromDatabase();
            }

        }
        catch (Exception e) {
            Log.e(TAG, "Error when retrieving alarms from the server: " + e.getMessage(), e);
            throw new InvalidMessageResponseException("Error ocurend when parsing JSON String", e);
        }
    }

    @Override
    public void onError(Exception ex){
        super.onError(ex);
        Log.e(TAG, "No alarms could be retrieved from the server: ");
    }

    private void removeAllAlarms() {
        this.removeAllGeoPoints();
        try {
            List<Alarm> alarms = getHelper().getAlarmDao().queryForAll();
            for (Alarm alarm : alarms) {
                getHelper().getAlarmDao().delete(alarm);
            }
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } finally {
            closeDatabaseConnection();
        }
    }

    private void removeAllGeoPoints() {
        try {
            List<GeoPoint> geoPoints = getHelper().getGeoPointDao().queryForAll();
            getHelper().getGeoPointDao().delete(geoPoints);
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } finally {
            closeDatabaseConnection();
        }
    }

    private void addAlarmsToDatabase(List<Alarm> alarms){
        for (Alarm alarm : alarms) {
            try {
                Log.d(TAG, "Alarm data @ GetAlarmsMessage " + alarm.getName() + " (" + alarm.getAlarmStartTime() + " - "
                        + alarm.getAlarmEndTime() + ")");
                getHelper().getAlarmDao().createIfNotExists(alarm);
            }catch (SQLException e) {
                Log.e(TAG, "Could not insert the alarm: " + alarm + "  -> " + e.toString());
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
