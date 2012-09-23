/** 
 * This library/program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library/program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this library/program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package org.androidcare.common;

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
