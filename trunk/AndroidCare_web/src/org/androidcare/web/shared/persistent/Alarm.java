package org.androidcare.web.shared.persistent;

import org.androidcare.web.shared.AlarmSeverity;
import org.androidcare.web.shared.AlarmType;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import java.io.Serializable;
import java.util.Date;

@PersistenceCapable
public class Alarm implements Serializable {

    private static final long serialVersionUID = 750596504539903183L;

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Long id;

    @Persistent
    private String name;
    @Persistent
    private int alarmSeverity;
    @Persistent
    private int alarmType;

    @Persistent
    private boolean initiateCall = false;
    @Persistent
    private boolean sendSMS = false;
    @Persistent
    private boolean sendEmail = false;
    @Persistent
    private boolean logInServer = false;

    @Persistent
    private String phoneNumber;
    @Persistent
    private String emailAddress;

    @Persistent
    private long alarmStartTime;
    @Persistent
    private long alarmEndTime;

    @Persistent
    private boolean onlyFireAtHome = false;
    @Persistent
    private boolean onlyFireAtLocation = false;

    @Persistent
    private double latitude;
    @Persistent
    private double longitude;

    @Persistent
    private String owner;

    public Alarm(){}

    public Alarm(Alarm alarm) {
        this.id = alarm.getId();
        this.name = alarm.getName();
        this.alarmSeverity = alarm.getAlarmSeverity().getId();
        this.alarmType = alarm.getAlarmType().getId();
        this.initiateCall = alarm.getInitiateCall();
        this.sendSMS = alarm.getSendSMS();
        this.sendEmail = alarm.getSendEmail();
        this.logInServer = alarm.getLogInServer();
        this.phoneNumber = alarm.getPhoneNumber();
        this.emailAddress = alarm.getEmailAddress();
        this.alarmStartTime = alarm.getAlarmStartTime().getTime();
        this.alarmEndTime = alarm.getAlarmEndTime().getTime();
        this.onlyFireAtHome = alarm.getFireOnlyAtHome();
        this.onlyFireAtLocation = alarm.getFireAtLocation();
        this.latitude = alarm.getLatitude();
        this.longitude = alarm.getLongitude();
        this.owner = alarm.getOwner();
    }

    public Alarm(String name, AlarmSeverity alarmSeverity, AlarmType alarmType, String phoneNumber, String emailAddress) {
        this.name = name;
        this.alarmType = alarmType.getId();
        this.alarmSeverity = alarmSeverity.getId();
        this.phoneNumber = phoneNumber;
        this.emailAddress = emailAddress;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAlarmType (AlarmType alarmType) {
    	this.alarmType = alarmType.getId();
    }
    
    public void setAlarmSeverity (AlarmSeverity alarmSeverity) {
        this.alarmSeverity = alarmSeverity.getId();
    }

    public void setAlarmStartTime (Date alarmStartTime) {
        this.alarmStartTime = alarmStartTime.getTime();
    }

    public void setAlarmEndTime (Date alarmEndTime) {
        this.alarmEndTime = alarmEndTime.getTime();
    }

    public void setPhoneNumber (String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setEmailAddress (String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public void initiateCallOnAlarm(boolean fire) {
        this.initiateCall = fire;
    }

    public void sendSMSOnAlarm(boolean fire) {
        this.sendSMS = fire;
    }

    public void sendEmailOnAlarm(boolean fire) {
        this.sendEmail = fire;
    }

    public void logInServerOnAlarm(boolean fire) {
        this.logInServer = fire;
    }

    public void onlyFireAtHome() {
        this.onlyFireAtLocation = false;
        this.onlyFireAtHome = true;
    }

    public void setOnlyFireAtLocation(double latitude, double longitude) {
        this.onlyFireAtHome = false;
        this.onlyFireAtLocation = true;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public AlarmSeverity getAlarmSeverity() {
        return AlarmSeverity.getAlarmOfId(this.alarmSeverity);
    }
    
    public AlarmType getAlarmType() {
    	return AlarmType.getAlarmType(this.alarmType);
    }
    
    public boolean getInitiateCall() {
        return this.initiateCall;
    }

    public boolean getSendSMS() {
        return this.sendSMS;
    }

    public boolean getSendEmail() {
        return this.sendEmail;
    }

    public boolean getLogInServer() {
        return this.logInServer;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public String getEmailAddress() {
        return this.emailAddress;
    }

    public Date getAlarmStartTime() {
        return new Date (this.alarmStartTime);
    }

    public Date getAlarmEndTime() {
        return new Date(this.alarmEndTime);
    }

    public boolean getFireOnlyAtHome() {
        return this.onlyFireAtHome;
    }

    public boolean getFireAtLocation() {
        return this.onlyFireAtLocation;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public String getOwner() {
        return owner;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}
