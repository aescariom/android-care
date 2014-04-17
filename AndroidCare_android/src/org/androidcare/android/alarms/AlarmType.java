package org.androidcare.android.alarms;

public enum AlarmType {
    WAKE_UP(1, "Wake up"),
    RED_ZONE(2, "Red zone"),
    FELL_OFF(3, "Fell off");

    private int id;
    private String description;

    AlarmType(int id, String description) {
        this.id = id;
        this.description = description;
    }

    public int getId() {
        return this.id;
    }

    public String getDescription() {
        return this.description;
    }

    public static AlarmType getAlarmType(String v) {
        if ((AlarmType.WAKE_UP.toString()).equals(v)) {
            return AlarmType.WAKE_UP;
        } else if ((AlarmType.RED_ZONE.toString()).equals(v)) {
            return AlarmType.RED_ZONE;
        } else if ((AlarmType.FELL_OFF.toString()).equals(v)) {
            return AlarmType.FELL_OFF;
        } else {
            return null;
        }
    }

    public static AlarmType getAlarmType(int id) {
        if (id == AlarmType.WAKE_UP.getId()) {
            return WAKE_UP;
        } else if (id == AlarmType.RED_ZONE.getId()) {
            return AlarmType.RED_ZONE;
        } else if (id == AlarmType.FELL_OFF.getId()) {
            return AlarmType.FELL_OFF;
        } else {
            return null;
        }
    }

    @Override
    public String toString() {
        return id + ": " + description;
    }

}
