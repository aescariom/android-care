package org.androidcare.android.service.alarms.messages;

import android.util.Log;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.androidcare.android.alarms.Alarm;
import org.androidcare.android.service.ConnectionService;
import org.androidcare.android.service.Message;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.message.BasicNameValuePair;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
@DatabaseTable(tableName = "SendEmailMessage")
public class SendEmailMessage extends Message {
    public static final String SENDMAIL_URL = "api/sendMailAlarm";

    @DatabaseField
    protected long alarmId;

    public SendEmailMessage() {
        super();
    }

    public SendEmailMessage(Alarm alarm) {
        super();
        this.alarmId = alarm.getId();
    }

    @Override
    public HttpRequestBase getHttpRequestBase() throws UnsupportedEncodingException {
        HttpPost httppost = new HttpPost(ConnectionService.getAppUrl() + SENDMAIL_URL);

        List<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>(2);
        nameValuePairs.add(new BasicNameValuePair("alarmId", String.valueOf(alarmId)));
        String date = format.format(creationDate);
        nameValuePairs.add(new BasicNameValuePair("time", date));

        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

        Log.e("TEST", "Enviamos un correo SendEmailMessage");

        return httppost;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;

        SendEmailMessage that = (SendEmailMessage) o;

        if (alarmId != that.alarmId) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (alarmId ^ (alarmId >>> 32));
    }
}