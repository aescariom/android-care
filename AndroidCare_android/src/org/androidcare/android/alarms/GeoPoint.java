package org.androidcare.android.alarms;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;

@DatabaseTable(tableName = "geo_points")
public class GeoPoint implements Serializable {

    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = "alarmIsReferedTo")
    private Alarm alarm;
    @DatabaseField
    private Double latitude;
    @DatabaseField
    private Double longitude;

    public GeoPoint() {}

    public GeoPoint (JSONObject jsonObj, Alarm alarm) throws NumberFormatException, JSONException, ParseException {
        this.latitude = Double.parseDouble(jsonObj.getString("latitude"));
        this.longitude = Double.parseDouble(jsonObj.getString("longitude"));
        this.alarm = alarm;
    }

    public int getId() {
        return this.id;
    }

    public Double getLatitude() {
        return this.latitude;
    }

    public Double getLongitude() {
        return this.longitude;
    }

    public Alarm getAlarm() {
        return this.alarm;
    }

    @Override
    public String toString() {
        return latitude + "; " + longitude;
    }

}
