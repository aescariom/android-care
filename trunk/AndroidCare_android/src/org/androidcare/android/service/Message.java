package org.androidcare.android.service;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpRequestBase;

import com.j256.ormlite.field.DatabaseField;

@SuppressWarnings("serial")
public abstract class Message implements Serializable, Comparable<Message> {
    
    @DatabaseField(generatedId = true)
    public int id;
    @DatabaseField
    protected String url;
    @DatabaseField
    protected Date creationDate;


    protected static final SimpleDateFormat format = new SimpleDateFormat("E MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
    
    public Message() {
        creationDate = new Date();
    }

    public abstract HttpRequestBase getHttpRequestBase() throws UnsupportedEncodingException;

    public void onPreSend(HttpRequestBase request) {
    }

    public void onPostSend(HttpResponse response) throws InvalidMessageResponseException { 
    }
    
    public void onError(Exception ex){
    }

    public final int compareTo(Message m) {
        return this.creationDate.compareTo(m.getCreationDate());
    }

    public Date getCreationDate() {
        return this.creationDate;
    }
}
