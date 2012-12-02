package org.androidcare.android.reminders;

import org.json.JSONArray;

/**
 * This exception will specify which was the problem while trying to get a valid date
 * 
 * @author Alejandro Escario M�ndez
 * 
 */
// @comentario seg�n yo lo entiendo, siempre que se lance una de �stas
// hay un bug en nuestro c�digo. Si es as�, las hacemos RuntimeException
// por otro lado, me arte de ver la NoDateFoundException que no hac�a nada as� que unificamos las dos
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
