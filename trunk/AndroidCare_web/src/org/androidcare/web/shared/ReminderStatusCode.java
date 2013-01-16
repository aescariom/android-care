package org.androidcare.web.shared;

public enum ReminderStatusCode {

	REMINDER_DISPLAYED(0, "REMINDER DISPLAYED"),
	REMINDER_DONE(1, "REMINDER CHECKED AS DONE"),
	REMINDER_IGNORED(2, "REMINDER IGNORED"),
	REMINDER_DELAYED(3, "REMINDER DELAYED");
	
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

    public static ReminderStatusCode getByCode(int codeValue)
    {
        for (ReminderStatusCode  type : ReminderStatusCode.values()) {
            if(type.code == codeValue){
            	return type;
            }
        }
		return null;
    }

}
