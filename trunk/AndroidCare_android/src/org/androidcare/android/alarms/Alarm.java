package org.androidcare.android.alarms;

import android.util.Log;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.androidcare.android.database.DatabaseHelper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

@DatabaseTable(tableName = "alarms")
public class Alarm implements Serializable {

    private static final long serialVersionUID = 1L;

    @DatabaseField(id = true)
    private long id;

    @DatabaseField
    private AlarmType alarmType;
    @DatabaseField
    private AlarmSeverity alarmSeverity;
    @DatabaseField
    private String name;

    @DatabaseField
    private boolean initiateCall = false;
    @DatabaseField
    private boolean sendSMS = false;
    @DatabaseField
    private boolean sendEmail = false;
    @DatabaseField
    private boolean logInServer = false;

    @DatabaseField
    private String phoneNumber;
    @DatabaseField
    private String emailAddress;

    @DatabaseField
    private Date alarmStartTime;
    @DatabaseField
    private Date alarmEndTime;

    @DatabaseField
    private boolean onlyFireAtHome = false;
    @DatabaseField
    private boolean onlyFireAtLocation = false;

    @DatabaseField
    private double latitude;
    @DatabaseField
    private double longitude;

    // default date time format
    private final static DateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss z yyyy",
            Locale.UK);
    // android 2.3.3 and below
    private final static DateFormat dateFormatUTC = new SimpleDateFormat("EEE MMM d HH:mm:ss 'UTC' yyyy",
            Locale.UK);
//Comentario ¿trabajo en proceso?
    private final static DateFormat timeFormat = new SimpleDateFormat("HH:mm",Locale.UK);
    private final static DateFormat timeFormatUTC = new SimpleDateFormat("HH:mm 'UTC'", Locale.UK);

    private DatabaseHelper databaseHelper;

    public Alarm () {}

    public Alarm (JSONObject jsonObj) throws NumberFormatException, JSONException, ParseException, SQLException {
        id = Long.parseLong(jsonObj.getString("id"));
        name = jsonObj.getString("name");

        alarmSeverity = AlarmSeverity.getAlarmOfId(Integer.parseInt(jsonObj.getJSONObject("alarmSeverity").
                getString("id")));
        alarmType = AlarmType.getAlarmType(Integer.parseInt(jsonObj.getJSONObject("alarmType").
                getString("id")));

        JSONArray array = new JSONArray(jsonObj.getString("positions"));
        for (int i = 0; i < array.length(); i++) {
            JSONObject geoPointJSON = array.getJSONObject(i);
            createGeoPoint(geoPointJSON, id);
        }
        closeDatabaseConnection();

        initiateCall = Boolean.parseBoolean(jsonObj.getString("initiateCall"));
        sendSMS = Boolean.parseBoolean(jsonObj.getString("sendSMS"));
        sendEmail = Boolean.parseBoolean(jsonObj.getString("sendEmail"));
        logInServer = Boolean.parseBoolean(jsonObj.getString("logInServer"));

        phoneNumber = jsonObj.getString("phoneNumber");
        emailAddress = jsonObj.getString("emailAddress");

        alarmStartTime = parseDate(jsonObj.getString("alarmStartTime"));
        alarmEndTime = parseDate(jsonObj.getString("alarmEndTime"));
    }

    public Alarm (long id, String name, AlarmSeverity severity, AlarmType type, boolean initiateCall, boolean sendSMS, boolean sendEmail, boolean logInServer,
                  String phoneNumber, String emailAddress, Date alarmStartTime, Date alarmEndTime, boolean onlyFireAtHome,
                  boolean onlyFireAtLocation, double latitude, double longitude) {
        this.id = id;
        this.name = name;

        this.alarmSeverity = severity;
        this.alarmType = type;
        this.initiateCall = initiateCall;
        this.sendSMS = sendSMS;
        this.sendEmail = sendEmail;
        this.logInServer = logInServer;

        this.phoneNumber = phoneNumber;
        this.emailAddress = emailAddress;

        this.alarmStartTime = alarmStartTime;
        this.alarmEndTime = alarmEndTime;

        this.onlyFireAtHome = onlyFireAtHome;
        this.onlyFireAtLocation = onlyFireAtLocation;

        this.latitude = latitude;
        this.longitude = longitude;
    }

    public boolean isInitiateCall() {
        return initiateCall;
    }

    public boolean isSendSMS() {
        return sendSMS;
    }

    public boolean isSendEmail() {
        return sendEmail;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public String getAlarmSeverity() {
        return alarmSeverity.getDescription();
    }

    public String getName() {
        return name;
    }

    public Date getAlarmStartTime() {
        return alarmStartTime;
    }

    public Date getAlarmEndTime() {
        return alarmEndTime;
    }

    public Long getId() {
        return id;
    }

    public AlarmType getAlarmType() {
        return alarmType;
    }

    private Date parseDate(String str) throws ParseException {
        try{
            return dateFormat.parse(str);
        }catch(ParseException ex){
            dateFormatUTC.setTimeZone(TimeZone.getTimeZone("UTC"));
            return dateFormatUTC.parse(str);
        }
    }

    private void createGeoPoint(JSONObject geoPointJSON, long id) throws SQLException, JSONException, ParseException {
        GeoPoint geoPoint = new GeoPoint(geoPointJSON, id);
        getHelper().getGeoPointDao().create(geoPoint);
        Log.e("TEST", "GeoPoint que guardamos en la base de datos: " + geoPoint);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(getName()).append(" - ");

        if (alarmType == AlarmType.WAKE_UP) {
            builder.append(AlarmType.WAKE_UP.getDescription());
        } else if (alarmType == AlarmType.RED_ZONE) {
            builder.append(AlarmType.RED_ZONE.getDescription());
        } else if (alarmType == AlarmType.FELL_OFF) {
            builder.append(AlarmType.FELL_OFF.getDescription());
        } else {
            builder.append("no idea");
        }

        return  builder.toString();
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