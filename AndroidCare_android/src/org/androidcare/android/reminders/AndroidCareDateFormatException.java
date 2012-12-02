package org.androidcare.android.reminders;

import org.json.JSONArray;

/**
 * This exception will specify which was the problem while trying to get a valid date
 * 
 * @author Alejandro Escario MŽndez
 * 
 */
// @comentario según yo lo entiendo, siempre que se lance una de éstas
// hay un bug en nuestro código. Si es así, las hacemos RuntimeException
// por otro lado, me arte de ver la NoDateFoundException que no hacía nada así que unificamos las dos
public class AndroidCareDateFormatException extends RuntimeException {

    private static final long serialVersionUID = -4089538220368903835L;
    private JSONArray jSONArray;

    public AndroidCareDateFormatException(String str) {
        super(str);
    }

    public AndroidCareDateFormatException(String str, JSONArray jSONArray) {
        super(str);
        this.jSONArray = jSONArray;
    }

    public JSONArray getjSONArray() {
        return jSONArray;
    }

}
