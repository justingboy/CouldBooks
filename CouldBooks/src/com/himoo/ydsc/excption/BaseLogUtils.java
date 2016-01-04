package com.himoo.ydsc.excption;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public abstract class BaseLogUtils {
	private long SECONDOFDAY = 3600 * 24 * 1000;

	protected abstract String getLogPath();

	protected abstract String LoggerName();

	public boolean isDebug() {
		return false;
	}

	private static DateFormat df = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss ");
	private static DateFormat ff = new SimpleDateFormat("yyyy-MM-dd");

	long day_time = 0;

	private File file = null;

	private File getFile() {
		long current = System.currentTimeMillis();

		if (current - day_time < 0 || current - day_time > SECONDOFDAY) {
			Calendar c = Calendar.getInstance();
			c.set(Calendar.HOUR_OF_DAY, 0);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.SECOND, 0);
			File folder = new File(getLogPath());
			if (!folder.exists()) {
				folder.mkdirs();
			}

			file = new File(folder, ff.format(c.getTime()) + ".txt");

		}

		return file;
	}

	public synchronized void write(String s) {
		Date now = new Date();
		String currentTime = df.format(now);
		Logcat.e(s);
		FileWriter aWriter = null;
		try {
			aWriter = new FileWriter(getFile(), true);
			aWriter.write("※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※");
			aWriter.write("\n");
			aWriter.write(currentTime);
			aWriter.write(" ：");
			aWriter.write(s);
			aWriter.write("\n");
			aWriter.write("\n");
			aWriter.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (aWriter != null) {
				try {
					aWriter.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	public synchronized void write(String tag, String s) {
		write(tag + ":" + s);
	}

	public void write(Exception ex) {
		if (ex != null) {
//			final String msg = ex.getLocalizedMessage();
			try {
				Writer info = new StringWriter();
				PrintWriter printWriter = new PrintWriter(info);
				ex.printStackTrace(printWriter);

				Throwable cause = ex.getCause();
				while (cause != null) {
					cause.printStackTrace(printWriter);
					cause = cause.getCause();
				}
				String result = info.toString();
				printWriter.close();
				info.close();
				write(result);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void writeException(Exception ex) {
		write(ex);
	}

}
