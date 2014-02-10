package org.androidcare.android.alarms;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

@DatabaseTable(tableName = "alarms")
public class Alarm implements Serializable {

    @DatabaseField(id = true)
    private int id;

    @DatabaseField
    private int alarmSeverity;
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
    private long alarmStartTime;
    @DatabaseField
    private long alarmEndTime;

    @DatabaseField
    private boolean onlyFireAtHome = false;
    @DatabaseField
    private boolean onlyFireAtLocation = false;

    @DatabaseField
    private double latitude;
    @DatabaseField
    private double longitude;

    // default date time format
    private final static DateFormat dateFormat = new SimpleDateFormat("HH:mm",
            Locale.UK);
    // android 2.3.3 and below
    private final static DateFormat dateFormatUTC = new SimpleDateFormat("HH:mm 'UTC'",
            Locale.UK);

    public Alarm () {}

    public Alarm (JSONObject jsonObj) throws NumberFormatException, JSONException, ParseException {
        id = Integer.parseInt(jsonObj.getString("id"));
        name = jsonObj.getString("name");
        alarmSeverity = Integer.parseInt(jsonObj.getString("alarmSeverity"));

        initiateCall = Boolean.parseBoolean(jsonObj.getString("initiateCall"));
        sendSMS = Boolean.parseBoolean(jsonObj.getString("sendSMS"));
        sendEmail = Boolean.parseBoolean(jsonObj.getString("sendEmail"));
        logInServer = Boolean.parseBoolean(jsonObj.getString("logInServer"));

        phoneNumber = jsonObj.getString("phoneNumber");
        emailAddress = jsonObj.getString("emailAddress");

        alarmStartTime = Long.parseLong(jsonObj.getString("alarmStartTime"));
        alarmEndTime = Long.parseLong(jsonObj.getString("alarmEndTime"));

        onlyFireAtHome = Boolean.parseBoolean(jsonObj.getString("onlyFireAtHome"));
        onlyFireAtLocation = Boolean.parseBoolean(jsonObj.getString("onlyFireAtLocation"));
    }

    public Alarm (int id, String name, AlarmSeverity severity, boolean initiateCall, boolean sendSMS, boolean sendEmail, boolean logInServer,
                  String phoneNumber, String emailAddress, long alarmStartTime, long alarmEndTime, boolean onlyFireAtHome,
                  boolean onlyFireAtLocation, double latitude, double longitude) {
        this.id = id;
        this.name = name;

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
        return AlarmSeverity.getAlarmOfId(alarmSeverity).getDescription();
    }

    public String getName() {
        return name;
    }
}
