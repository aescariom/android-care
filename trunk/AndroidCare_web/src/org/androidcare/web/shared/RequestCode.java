package org.androidcare.web.shared;

/**
 * 
 * @author Alejandro Escario MŽndez
 *
 */
public enum RequestCode {

	OK(0, "OK"),
	ERROR(1, "ERROR"),
	WARNING(2, "WARNING"),
	INVALID_INPUT(3, "INVALID INPUT DATA"),
	MISSING_ARGUMENTS(4, "MISSING ARGUMENTS");
	
	final int code;
    final String description;

    /**
     * 
     * @param code
     * @param description
     */
    private RequestCode(int code, String description) {
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
    public static RequestCode getByCode(int codeValue)
    {
        for (RequestCode  type : RequestCode.values()) {
            if(type.code == codeValue){
            	return type;
            }
        }
		return null;
    }
}
