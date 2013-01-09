package org.androidcare.android.reminders;

public enum ReminderStatusCode {

    ALERT_DISPLAYED(0, "ALERT DISPLAYED"), ALERT_DONE(1, "ALERT CHECKED AS DONE"), ALERT_IGNORED(2,
            "ALERT IGNORED");

    final int code;
    final String description;

    private ReminderStatusCode(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public int getCode() {
        return code;
    }

    @Override
    public String toString() {
        return code + ": " + description;
    }

    public static ReminderStatusCode getByCode(int codeValue) {
        for (ReminderStatusCode type : ReminderStatusCode.values()) {
            if (type.code == codeValue) {
                return type;
            }
        }
        return null;
    }

}
