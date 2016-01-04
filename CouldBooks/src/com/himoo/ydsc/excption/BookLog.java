package com.himoo.ydsc.excption;

import android.os.Environment;

public class BookLog extends BaseLogUtils {
	private static BookLog log;
	/** 收集日志的目录 */
	public static final String INSTANCE_LOG_PATH = Environment
			.getExternalStorageDirectory().getAbsolutePath()
			+ "/CouldBook/logs/";

	public static synchronized BookLog log() {
		if (log == null) {
			log = new BookLog();
		}
		return log;
	}

	@Override
	protected String getLogPath() {
		// TODO Auto-generated method stub
		return INSTANCE_LOG_PATH;
	}

	@Override
	protected String LoggerName() {
		// TODO Auto-generated method stub
		return "book";
	}

}