package com.himoo.ydsc.excption;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;

import com.himoo.ydsc.base.BaseApplication;

/**
 * UncaughtException处理类,当程序发生Uncaught异常的时候,有该类 来接管程序,并记录 发送错误报告.
 */
public class CrashHandler implements UncaughtExceptionHandler {
	/**
	 * Debug Log tag
	 */
	public static final String TAG = "CrashHandler";
	/**
	 * 是否开启日志输出,在Debug状态下开启, 在Release状态下关闭以提示程序性能
	 */
	public static final boolean DEBUG = true;
	/**
	 * 系统默认的UncaughtException处理类
	 */
	public Thread.UncaughtExceptionHandler mDefaultHandler;
	/**
	 * CrashHandler实例
	 */
	private static CrashHandler INSTANCE;
	/**
	 * 程序的Context对象
	 */
	private Context mContext;
	private BaseApplication application;

	/**
	 * 使用Properties来保存设备的信息和错误堆栈信息
	 */
	private static final String VERSION_NAME = "versionName";
	private static final String VERSION_CODE = "versionCode";
//	private static final String STACK_TRACE = "STACK_TRACE";

	/**
	 * 保证只有一个CrashHandler实例
	 */
	private CrashHandler() {
	}

	/**
	 * 获取CrashHandler实例 ,单例模式
	 */
	public static CrashHandler getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new CrashHandler();
		}
		return INSTANCE;
	}

	/**
	 * 初始化,注册Context对象, 获取系统默认的UncaughtException处理器, 设置该CrashHandler为程序的默认处理器
	 * 
	 * @param ctx
	 */

	public void init(Context ctx) {
		mContext = ctx;
		application = (BaseApplication) mContext.getApplicationContext();
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	/**
	 * 当UncaughtException发生时会转入该函数来处理
	 */
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		handleException(ex);
		android.os.Process.killProcess(android.os.Process.myPid());
	}

	/**
	 * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成. 开发者可以根据自己的情况来自定义异常处理逻辑
	 * 
	 * @param ex
	 * @return true:如果处理了该异常信息;否则返回false
	 */
	private boolean handleException(Throwable ex) {

		try {
			if (ex != null) {
//				final String msg = ex.getLocalizedMessage();
				// 使用Toast来显示异常信
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
					write(result);
					printWriter.close();
					info.close();
				} catch (IOException e) {

				}
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.fillInStackTrace();
		}
		Intent intent = new Intent(mContext, CrashActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		application.startActivity(intent);

		return true;
	}

	/**
	 * 收集程序崩溃的设备信息
	 * 
	 * @param ctx
	 */
	public String collectCrashDeviceInfo(Context ctx) {
		String result = "";
		try {

			PackageManager pm = ctx.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(),
					PackageManager.GET_ACTIVITIES);
			if (pi != null) {
				result += VERSION_NAME
						+ (pi.versionName == null ? "not set" : pi.versionName);
				result += (VERSION_CODE + pi.versionCode);
			}
		} catch (NameNotFoundException e) {
		}
		// 使用反射来收集设备信息.在Build类中包含各种设备信息,
		// 例如: 系统版本号,设备生产商 等帮助调试程序的有用信息
		Field[] fields = Build.class.getDeclaredFields();
		for (Field field : fields) {
			try {
				field.setAccessible(true);
				result += (field.getName() + field.get(null));
			} catch (Exception e) {

			}

		}
		return result;
	}

	private void write(String result) {
		BookLog.log().write(result);
	}

}
