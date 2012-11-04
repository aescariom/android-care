package org.androidcare.web.shared;

/**
 * 
 * @author Alejandro Escario MŽndez
 *
 */
public class Strings {
	
	/**
	 * 
	 * @param format
	 * @param args
	 * @return
	 */
	public static String format(final String format, final String... args) {
        String[] split = format.split("%s");
        final StringBuffer msg = new StringBuffer();
        for (int pos = 0; pos < split.length - 1; pos += 1) {
            msg.append(split[pos]);
            msg.append(args[pos]);
        }
        msg.append(split[split.length - 1]);
        return msg.toString();
    }
}
