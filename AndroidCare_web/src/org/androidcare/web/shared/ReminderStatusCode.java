package org.androidcare.web.shared;

//@Comentario en algún momento habría que tener un soporte de i18n un poco menos ad hoc
public enum ReminderStatusCode {

	REMINDER_DISPLAYED(0, "REMINDER DISPLAYED", "RECORDATORIO MOSTRADO"),
	REMINDER_DONE(1, "REMINDER CHECKED AS DONE", "RECORDATORIO ACEPTADO"),
	REMINDER_IGNORED(2, "REMINDER IGNORED", "RECORDATORIO IGNORADO"),
	REMINDER_DELAYED(3, "REMINDER DELAYED", "RECORDATORIO RETRASADO");
	
	final int code;
    final String description_en;
    final String description_es;

    private ReminderStatusCode(int code, String description_en, String description_es) {
	    this.code = code;
	    this.description_en = description_en;
	    this.description_es = description_es;
    }
    
    public String getDescription(String lang) {
    	if(lang.contains("es")){
    		return description_es;
    	}else{
    		return description_en;
    	}
    }

    public int getCode() {
    	return code;
    }

    @Override
    public String toString() {
    	return toString("en");
    }
    
    public String toString(String lang){
    	return code + ": " + getDescription(lang);
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
