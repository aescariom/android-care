package uspceu.logservice;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.util.Log;

public class LogWriter {
	public static void appendLog(String text) {
		File logFile = new File("sdcard/log.file");
		if (!logFile.exists()) {
			try {
				logFile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Log.e("LogWriter", null, e);
				e.printStackTrace();
			}
		}
		try {
			// BufferedWriter for performance, true to set append to file flag
			BufferedWriter buf = new BufferedWriter(new FileWriter(logFile,
					true));
			Long timestampLong = System.currentTimeMillis() / 1000;
			String timestamp = timestampLong.toString();

			buf.append("TIME: " + timestamp + " " + text);
			buf.newLine();
			buf.flush();
			buf.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
