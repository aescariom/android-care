package org.androidcare.android.alarms;

import android.util.Log;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;

@DatabaseTable(tableName = "GeoPoints")
public class GeoPoint implements Serializable {

    @DatabaseField(generatedId = true)
    private Long id;
    @DatabaseField
    private Long alarmIsReferedTo;
    @DatabaseField
    private Double latitude;
    @DatabaseField
    private Double longitude;

    public GeoPoint() {}

    public GeoPoint (JSONObject jsonObj, Long alarmId) throws NumberFormatException, JSONException, ParseException {
        this.latitude = Double.parseDouble(jsonObj.getString("latitude"));
        this.longitude = Double.parseDouble(jsonObj.getString("longitude"));
        this.alarmIsReferedTo = alarmId;
        Log.e("TEST", "Creado objeto GeoPoint " + latitude + ";" + longitude + " alarmId " + alarmId);
    }

    public GeoPoint (Double latitude, Double longitude, Long alarmId) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.alarmIsReferedTo = alarmId;
    }

    public Double getLatitude() {
        return this.latitude;
    }

    public Double getLongitude() {
        return this.longitude;
    }

    public Long getAlarmIdIsReferedTo() {
        return this.alarmIsReferedTo;
    }

    @Override
    public String toString() {
        return latitude + "; " + longitude;
    }

}
