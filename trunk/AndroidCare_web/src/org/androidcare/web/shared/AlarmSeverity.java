package org.androidcare.web.shared;

public enum AlarmSeverity {

    INFO(1, "INFO"),
    WARNING(2, "WARNING"),
    SEVERE(3, "SEVERE"),
    VERY_SEVERE(4, "VERY SEVERE");

    private final int id;
    private final String description;

    AlarmSeverity(int id, String description) {
        this.id = id;
        this.description = description;
    }

    public int getId() {
        return this.id;
    }

    public String getDescription() {
        return this.description;
    }

    public static AlarmSeverity getAlarmOf (String description) {
        if ("1: INFO".equals(description)) {
            return AlarmSeverity.INFO;
        } else if ("2: WARNING".equals(description)) {
            return AlarmSeverity.WARNING;
        } else if ("3: SEVERE".equals(description)) {
            return AlarmSeverity.SEVERE;
        } else if ("4: VERY SEVERE".equals(description)) {
            return AlarmSeverity.VERY_SEVERE;
        } else {
            return null;
        }
    }

    public static AlarmSeverity getAlarmOfId (int id) {
        if (id == 1) {
            return AlarmSeverity.INFO;
        } else if (id == 2) {
            return AlarmSeverity.WARNING;
        } else if (id == 3) {
            return AlarmSeverity.SEVERE;
        } else if (id == 4) {
            return AlarmSeverity.VERY_SEVERE;
        } else {
            return null;
        }
    }

    @Override
    public String toString() {
        return id + ": " + description;
    }

}
