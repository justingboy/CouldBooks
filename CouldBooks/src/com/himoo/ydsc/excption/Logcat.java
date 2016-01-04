package com.himoo.ydsc.excption;

import com.himoo.ydsc.BuildConfig;

import android.util.Log;

public class Logcat {

	static String className;
	static String methodName;
	static int lineNumber;

	private Logcat() {
		/* Protect from instantiations */
	}

	private static boolean isDebuggable() {
		return BuildConfig.DEBUG;
	}

	private static String createLog(String log) {

		StringBuffer buffer = new StringBuffer();
		buffer.append("[");
		buffer.append(methodName);
		buffer.append(":");
		buffer.append(lineNumber);
		buffer.append("]");
		buffer.append(log);

		return buffer.toString();
	}

	private synchronized static void getMethodNames(
			StackTraceElement[] sElements) {
		className = sElements[1].getFileName();
		methodName = sElements[1].getMethodName();
		lineNumber = sElements[1].getLineNumber();
	}

	public static void e(String message) {
		if (!isDebuggable())
			return;

		// Throwable instance must be created before any methods
		getMethodNames(new Throwable().getStackTrace());
		Log.e(className, createLog(message));
	}

	public static void i(String message) {
		if (!isDebuggable())
			return;

		getMethodNames(new Throwable().getStackTrace());
		Log.i(className, createLog(message));
	}

	public static void d(String message) {
		if (!isDebuggable())
			return;

		getMethodNames(new Throwable().getStackTrace());
		Log.d(className, createLog(message));
	}

	public static void v(String message) {
		if (!isDebuggable())
			return;

		getMethodNames(new Throwable().getStackTrace());
		Log.v(className, createLog(message));
	}

	public static void w(String message) {
		if (!isDebuggable())
			return;

		getMethodNames(new Throwable().getStackTrace());
		Log.w(className, createLog(message));
	}

	public static void wtf(String message) {
		if (!isDebuggable())
			return;

		getMethodNames(new Throwable().getStackTrace());
		Log.wtf(className, createLog(message));
	}

	public static void e(String tag, String message) {
		if (!isDebuggable())
			return;

		// Throwable instance must be created before any methods

		Log.e(tag, message);
	}

	public static void i(String tag, String message) {
		if (!isDebuggable())
			return;

		Log.i(tag, message);
	}

	public static void d(String tag, String message) {
		if (!isDebuggable())
			return;

		Log.d(tag, message);
	}

	public static void v(String tag, String message) {
		if (!isDebuggable())
			return;

		Log.v(tag, message);
	}

	public static void w(String tag, String message) {
		if (!isDebuggable())
			return;

		Log.w(tag, message);
	}

	public static void wtf(String tag, String message) {
		if (!isDebuggable())
			return;

		Log.wtf(tag, message);
	}

}