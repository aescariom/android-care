package org.androidcare.android.service;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpRequestBase;

@SuppressWarnings("serial")
public abstract class Message implements Serializable, Comparable<Message> {
    protected String url;
    protected Date creationDate;

    public Message() {
        creationDate = new Date();
    }

    public abstract HttpRequestBase getHttpRequestBase() throws UnsupportedEncodingException;

    public void onPreSend(HttpRequestBase request) {
    }

    public void onPostSend(HttpResponse response) throws InvalidMessageResponseException {
    }

    public final int compareTo(Message m) {
        return this.creationDate.compareTo(m.getCreationDate());
    }

    public Date getCreationDate() {
        return this.creationDate;
    }
}
