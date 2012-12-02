package org.androidcare.android.service;

@SuppressWarnings("serial")
public class InvalidMessageResponseException extends Exception {

    public InvalidMessageResponseException(String msg) {
        super(msg);
    }

    public InvalidMessageResponseException(String msg, Exception e) {
        super(msg, e);
    }

}
