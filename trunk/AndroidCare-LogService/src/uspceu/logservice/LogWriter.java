package uspceu.logservice;

import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class LogWriter {
	public static void appendLog(String text) {
        Log.d("DATA: ", text);
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

			buf.append(text);
			buf.newLine();
			buf.flush();
			buf.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
