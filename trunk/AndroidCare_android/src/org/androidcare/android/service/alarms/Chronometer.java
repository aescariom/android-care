package org.androidcare.android.service.alarms;

import java.io.Serializable;
import java.util.Date;

public class Chronometer implements Serializable {

    private long elapsedTime;
    private long last;
    private long timeShouldExceed;
    private boolean started;

    public Chronometer(long timeShouldExceed) {
        this.elapsedTime = 0;
        this.last = new Date().getTime();
        this.timeShouldExceed = timeShouldExceed;
        this.started = false;
    }

    public void reset() {
        this.elapsedTime = 0;
        this.last = new Date().getTime();
        this.started = false;
    }

    public void count() {
        long now = new Date().getTime();
        if (started) {
            this.elapsedTime += (now - this.last);
        }
        this.last = now;
        this.started = true;
    }

    public boolean hasABigInterval() {
        return elapsedTime > timeShouldExceed;
    }
}
