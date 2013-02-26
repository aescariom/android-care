package org.androidcare.android.database;

import java.sql.SQLException;

import org.androidcare.android.reminders.Reminder;
import org.androidcare.android.service.Message;
import org.androidcare.android.service.location.LocationMessage;
import org.androidcare.android.service.reminders.GetRemindersMessage;
import org.androidcare.android.service.reminders.ReminderLogMessage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME = "androidCare.db";
    private static final int DATABASE_VERSION = 5;

    private Dao<Reminder, Integer> reminderDao;
    private Dao<GetRemindersMessage, Integer> getGetRemindersMessageDao;
    private Dao<LocationMessage, Integer> getLocationMessageDao;
    private Dao<ReminderLogMessage, Integer> getReminderLogMessageDao;
    
    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION/*, R.raw.ormlite_config*/);
    }
    
    @Override
    public void onCreate(SQLiteDatabase arg0, ConnectionSource connectionSource) {
        try{
            Log.i(DatabaseHelper.class.getName(), "creating database..");
            TableUtils.createTableIfNotExists(connectionSource, Reminder.class);
            TableUtils.createTableIfNotExists(connectionSource, GetRemindersMessage.class);
            TableUtils.createTableIfNotExists(connectionSource, ReminderLogMessage.class);
            TableUtils.createTableIfNotExists(connectionSource, LocationMessage.class);
        }catch(SQLException ex){
            Log.e(DatabaseHelper.class.getName(), "Can't create database", ex);
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase arg0, ConnectionSource arg1, int arg2, int arg3) {
        try{
            Log.i(DatabaseHelper.class.getName(), "updating database..");
            //TODO copiar los datos de la versi—n anterior
            TableUtils.createTableIfNotExists(connectionSource, Reminder.class);
            TableUtils.createTableIfNotExists(connectionSource, GetRemindersMessage.class);
            TableUtils.createTableIfNotExists(connectionSource, ReminderLogMessage.class);
            TableUtils.createTableIfNotExists(connectionSource, LocationMessage.class);
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

    public void truncateReminderTable() throws SQLException {
        TableUtils.clearTable(connectionSource, Reminder.class);
    }

    public int create(Message message) throws SQLException {
        if(message.getClass() == GetRemindersMessage.class){
            return getGetRemindersMessageDao().create((GetRemindersMessage)message);
        }else if(message.getClass() == LocationMessage.class){
            return getLocationMessageDao().create((LocationMessage)message);
        }else if(message.getClass() == ReminderLogMessage.class){
            return getReminderLogMessageDao().create((ReminderLogMessage)message);
        }
        return -1;
    }

    public int remove(Message message) throws SQLException {
        if(message.getClass() == GetRemindersMessage.class){
            return getGetRemindersMessageDao().delete((GetRemindersMessage)message);
        }else if(message.getClass() == LocationMessage.class){
            return getLocationMessageDao().delete((LocationMessage)message);
        }else if(message.getClass() == ReminderLogMessage.class){
            return getReminderLogMessageDao().delete((ReminderLogMessage)message);
        }
        return -1;
    }

}
