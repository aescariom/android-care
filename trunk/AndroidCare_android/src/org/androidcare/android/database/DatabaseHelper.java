package org.androidcare.android.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import org.androidcare.android.alarms.Alarm;
import org.androidcare.android.alarms.GeoPoint;
import org.androidcare.android.reminders.Reminder;
import org.androidcare.android.service.Message;
import org.androidcare.android.service.alarms.GetAlarmsMessage;
import org.androidcare.android.service.alarms.SendEmailMessage;
import org.androidcare.android.service.location.LocationMessage;
import org.androidcare.android.service.reminders.GetRemindersMessage;
import org.androidcare.android.service.reminders.ReminderLogMessage;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME = "androidCare.db";
    private static final int DATABASE_VERSION = 5;

    private Dao<Reminder, Integer> reminderDao;
    private Dao<GetRemindersMessage, Integer> getGetRemindersMessageDao;
    private Dao<LocationMessage, Integer> getLocationMessageDao;
    private Dao<ReminderLogMessage, Integer> getReminderLogMessageDao;
    private Dao<Alarm, Integer> alarmDao;
    private Dao<GetAlarmsMessage, Integer> getAlarmMessageDao;
    private Dao<SendEmailMessage, Integer> sendEmailMessagesDao;
    private Dao<GeoPoint, Integer> geoPointDao;

    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    
    @Override
    public void onCreate(SQLiteDatabase arg0, ConnectionSource connectionSource) {
        try{
            Log.i(DatabaseHelper.class.getName(), "creating database..");
            TableUtils.createTableIfNotExists(connectionSource, Reminder.class);
            TableUtils.createTableIfNotExists(connectionSource, GetRemindersMessage.class);
            TableUtils.createTableIfNotExists(connectionSource, ReminderLogMessage.class);
            TableUtils.createTableIfNotExists(connectionSource, LocationMessage.class);
            TableUtils.createTableIfNotExists(connectionSource, Alarm.class);
            TableUtils.createTableIfNotExists(connectionSource, GetAlarmsMessage.class);
            TableUtils.createTableIfNotExists(connectionSource, SendEmailMessage.class);
            TableUtils.createTableIfNotExists(connectionSource, GeoPoint.class);
        }catch(SQLException ex){
            Log.e(DatabaseHelper.class.getName(), "Can't create database", ex);
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase arg0, ConnectionSource arg1, int arg2, int arg3) {
        try{
            Log.i(DatabaseHelper.class.getName(), "updating database..");
            //comentario no hace falta, no tenemos usuarios reales
            //TODO copiar los datos de la versi�n anterior
            TableUtils.createTableIfNotExists(connectionSource, Reminder.class);
            TableUtils.createTableIfNotExists(connectionSource, GetRemindersMessage.class);
            TableUtils.createTableIfNotExists(connectionSource, ReminderLogMessage.class);
            TableUtils.createTableIfNotExists(connectionSource, LocationMessage.class);
            TableUtils.createTableIfNotExists(connectionSource, Alarm.class);
            TableUtils.createTableIfNotExists(connectionSource, GetAlarmsMessage.class);
            TableUtils.createTableIfNotExists(connectionSource, SendEmailMessage.class);
            TableUtils.createTableIfNotExists(connectionSource, GeoPoint.class);
        }catch(SQLException ex){
            Log.e(DatabaseHelper.class.getName(), "Can't create database", ex);
            throw new RuntimeException(ex);
        }
    }
    
    public Dao<Reminder, Integer> getReminderDao() throws SQLException {
        if (reminderDao == null) {
            reminderDao = getDao(Reminder.class);
        }
        return reminderDao;
    }

    private Dao<GetRemindersMessage, Integer> getGetRemindersMessageDao() throws SQLException{
        if (getGetRemindersMessageDao == null) {
            getGetRemindersMessageDao = getDao(GetRemindersMessage.class);
        }
        return getGetRemindersMessageDao;
    }

    private Dao<ReminderLogMessage, Integer> getReminderLogMessageDao() throws SQLException{
        if (getReminderLogMessageDao == null) {
            getReminderLogMessageDao = getDao(ReminderLogMessage.class);
        }
        return getReminderLogMessageDao;
    }
    
    private Dao<LocationMessage, Integer> getLocationMessageDao() throws SQLException{
        if (getLocationMessageDao == null) {
            getLocationMessageDao = getDao(LocationMessage.class);
        }
        return getLocationMessageDao;
    }

    public Dao<Alarm, Integer> getAlarmDao() throws SQLException {
        if (alarmDao == null) {
            alarmDao = getDao(Alarm.class);
        }
        return alarmDao;
    }

    public Dao<GetAlarmsMessage, Integer> getAlarmsMessagesDao() throws SQLException {
        if (getAlarmMessageDao == null) {
            getAlarmMessageDao = getDao(GetAlarmsMessage.class);
        }
        return getAlarmMessageDao;
    }

    public Dao<SendEmailMessage, Integer> getSendEmailMessagesDao() throws SQLException {
        if (sendEmailMessagesDao == null) {
            sendEmailMessagesDao = getDao(SendEmailMessage.class);
        }
        return sendEmailMessagesDao;
    }

    public Dao<GeoPoint, Integer> getGeoPointDao() throws SQLException {
        if (geoPointDao == null) {
            geoPointDao = getDao(GeoPoint.class);
        }
        return geoPointDao;
    }

    public void truncateReminderTable() throws SQLException {
        TableUtils.clearTable(connectionSource, Reminder.class);

        Log.i(DatabaseHelper.class.getName(), "Reminders table successfully cleared");
    }

    public int create(Message message) throws SQLException {
        if(message.getClass() == GetRemindersMessage.class){
            // only one message of this type should exists
            TableUtils.clearTable(connectionSource, GetRemindersMessage.class);
            return getGetRemindersMessageDao().create((GetRemindersMessage)message);
        }else if(message.getClass() == LocationMessage.class){
            return getLocationMessageDao().create((LocationMessage)message);
        }else if(message.getClass() == ReminderLogMessage.class){
            return getReminderLogMessageDao().create((ReminderLogMessage)message);
        } else if (message.getClass() == GetAlarmsMessage.class) {
            // only one message of this type should exists
            TableUtils.clearTable(connectionSource, GetAlarmsMessage.class);
            return getAlarmsMessagesDao().create((GetAlarmsMessage) message);
        } else if (message.getClass() == SendEmailMessage.class) {
            // only one message of this type should exists
            TableUtils.clearTable(connectionSource, SendEmailMessage.class);
            return getSendEmailMessagesDao().create((SendEmailMessage) message);
        }
        return -1;
    }

    public int remove(Message message) throws SQLException {
        if (message.getClass() == GetRemindersMessage.class) {
            return getGetRemindersMessageDao().delete((GetRemindersMessage)message);
        } else if (message.getClass() == LocationMessage.class) {
            return getLocationMessageDao().delete((LocationMessage)message);
        } else if (message.getClass() == ReminderLogMessage.class) {
            return getReminderLogMessageDao().delete((ReminderLogMessage)message);
        } else if (message.getClass() == GetAlarmsMessage.class) {
            return getAlarmsMessagesDao().delete((GetAlarmsMessage) message);
        } else if (message.getClass() == SendEmailMessage.class) {
            return getSendEmailMessagesDao().delete((SendEmailMessage) message);
        }
        return -1;
    }

    public List<Message> getMessages() throws SQLException {
        List<Message> messages = new ArrayList<Message>();
        Log.d(DatabaseHelper.class.getName(), "Enviando mensaje de correo de alarma");
        messages.addAll(getSendEmailMessagesDao().queryForAll());
        Log.d(DatabaseHelper.class.getName(), "Enviando mensaje de recordatorios");
        messages.addAll(getGetRemindersMessageDao().queryForAll());
        Log.d(DatabaseHelper.class.getName(), "Enviando mensaje de log de recordatorios");
        messages.addAll(getReminderLogMessageDao().queryForAll());
        Log.d(DatabaseHelper.class.getName(), "Enviando mensaje de alarmas");
        messages.addAll(getAlarmsMessagesDao().queryForAll());
        Log.d(DatabaseHelper.class.getName(), "Enviando mensaje de posicionamiento");
        messages.addAll(getLocationMessageDao().queryForAll());
        return messages;
    }

    public List<GeoPoint> getGeoPointsFor(long id) throws SQLException {
        List<GeoPoint> geoPoints = new ArrayList<GeoPoint>();
        Map<String, Object> whereClause = new HashMap<String, Object>();
        whereClause.put("alarmIsReferedTo", id);
        geoPoints.addAll(getGeoPointDao().queryForFieldValues(whereClause));
        return geoPoints;
    }

    public void deleteGeoPointsReferedTo(long id) throws SQLException {
        List<GeoPoint> points = getGeoPointsFor(id);
        getGeoPointDao().delete(points);
    }

}
