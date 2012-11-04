package org.androidcare.web.shared;

/**
 * 
 * @author Alejandro Escario MŽndez
 *
 */
public enum AlertStatusCode {

	ALERT_DISPLAYED(0, "ALERT DISPLAYED"),
	ALERT_DONE(1, "ALERT CHECKED AS DONE"),
	ALERT_IGNORED(2, "ALERT IGNORED");
	
	final int code;
    final String description;

    /**
     * 
     * @param code
     * @param description
     */
    private AlertStatusCode(int code, String description) {
	    this.code = code;
	    this.description = description;
    }

    /**
     * 
     * @return
     */
    public String getDescription() {
    	return description;
    }

    /**
     * 
     * @return
     */
    public int getCode() {
    	return code;
    }

    /**
     * 
     */
    @Override
    public String toString() {
    	return code + ": " + description;
    }
    
    /**
     * 
     * @param codeValue
     * @return
     */
    public static AlertStatusCode getByCode(int codeValue)
    {
        for (AlertStatusCode  type : AlertStatusCode.values()) {
            if(type.code == codeValue){
            	return type;
            }
        }
		return null;
    }

}
