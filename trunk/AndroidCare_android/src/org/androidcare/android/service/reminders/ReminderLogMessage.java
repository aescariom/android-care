package org.androidcare.android.service.reminders;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.androidcare.android.reminders.Reminder;
import org.androidcare.android.reminders.ReminderStatusCode;
import org.androidcare.android.service.ConnectionService;
import org.androidcare.android.service.Message;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.message.BasicNameValuePair;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@SuppressWarnings("serial")
@DatabaseTable(tableName = "ReminderLogMessage")
public class ReminderLogMessage extends Message {
    public static final String REMINDERS_LOG_URL = "api/addReminderLog";

    @DatabaseField
    protected int reminderId;
    @DatabaseField
    private int statusCode;

    public ReminderLogMessage() {
        super();
    }

    public ReminderLogMessage(Reminder reminder, ReminderStatusCode statusCode) {
        super();
        this.reminderId = reminder.getId();
        this.statusCode = statusCode.getCode();
    }

    @Override
    public HttpRequestBase getHttpRequestBase() throws UnsupportedEncodingException {
        HttpPost httppost = new HttpPost(ConnectionService.getAppUrl() + REMINDERS_LOG_URL);

        List<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>(2);
        nameValuePairs.add(new BasicNameValuePair("reminderId", String.valueOf(reminderId)));
        nameValuePairs.add(new BasicNameValuePair("statusCode", String.valueOf(statusCode)));
        String date = format.format(creationDate);
        nameValuePairs.add(new BasicNameValuePair("time", date));

        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

        return httppost;
    }

}