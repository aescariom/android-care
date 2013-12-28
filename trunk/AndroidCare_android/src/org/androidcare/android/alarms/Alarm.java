package org.androidcare.android.alarms;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

@DatabaseTable(tableName = "alarms")
public class Alarm implements Serializable {

    @DatabaseField(id = true)
    private int id;

    @DatabaseField
    private int alarmSeverity;
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

    public Alarm () {}

    public Alarm (JSONObject jsonObj) throws NumberFormatException, JSONException, ParseException {
        id = Integer.parseInt(jsonObj.getString("id"));
        alarmSeverity = Integer.parseInt(jsonObj.getString("alarmSeverity"));

        initiateCall = Boolean.parseBoolean(jsonObj.getString("initiateCall"));
        sendSMS = Boolean.parseBoolean(jsonObj.getString("sendSMS"));
        sendEmail = Boolean.parseBoolean(jsonObj.getString("sendEmail"));
        logInServer = Boolean.parseBoolean(jsonObj.getString("logInServer"));

        phoneNumber = jsonObj.getString("phoneNumber");
        emailAddress = jsonObj.getString("emailAddress");

        alarmStartTime = parseDate(jsonObj.getString("alarmStartTime"));
        alarmEndTime = parseDate(jsonObj.getString("alarmEndTime"));

        onlyFireAtHome = Boolean.parseBoolean(jsonObj.getString("onlyFireAtHome"));
        onlyFireAtLocation = Boolean.parseBoolean(jsonObj.getString("onlyFireAtLocation"));
    }

    public Alarm (int id, AlarmSeverity severity, boolean initiateCall, boolean sendSMS, boolean sendEmail, boolean logInServer,
                  String phoneNumber, String emailAddress, Date alarmStartTime, Date alarmEndTime, boolean onlyFireAtHome,
                  boolean onlyFireAtLocation, double latitude, double longitude) {
        this.id = id;

        this.alarmSeverity = severity.getId();
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

    private Date parseDate(String str) throws ParseException {
        try{
            return dateFormat.parse(str);
        }catch(ParseException ex){
            dateFormatUTC.setTimeZone(TimeZone.getTimeZone("UTC"));
            return dateFormatUTC.parse(str);
        }
    }



}
