package com.himoo.ydsc.base;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.app.Application;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.util.SparseArray;

import com.himoo.ydsc.common.AppException;
import com.himoo.ydsc.ui.utils.Toast;
import com.himoo.ydsc.util.MyLogger;

/**
 * 做了一些处理的Application的类，为了维持该框架的运行特意写的一个类
 * 
 */
public class BaseApplication extends Application {
	/** 开启性能测试，检测应用程序所有有可能发生超时的操作，可以在Logcat中看到此类操作 */
	private static final boolean DEVELOPER_MODE = true;
	/** 需要退出提示的activity集合 */
	private SparseArray<String> UIS = new SparseArray<String>();
	/** 　异步任务的集合 */
	private List<AsyncTask<?, ?, ?>> asyncs = new ArrayList<AsyncTask<?, ?, ?>>();
	/** Application的唯一实例 */
	private static BaseApplication instance;
	/** Log */
	public MyLogger Log;
	/** 标记键值 */
	private int SparseArray_flag = 1;
	/** 界面集合 */
	private SparseArray<Activity> activitys = new SparseArray<Activity>();

	/**
	 * 获取该类的实例
	 * 
	 * @return
	 */
	public static BaseApplication getInstance() {
		return instance;
	}

	@Override
	public void onCreate() {
		if (DEVELOPER_MODE) {
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
					.detectDiskReads().detectDiskWrites().detectNetwork()
					.penaltyLog().build());
			StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
					.detectLeakedSqlLiteObjects().penaltyLog().penaltyDeath()
					.build());
		}
		super.onCreate();
		instance = this;
		//注册App异常崩溃处理器
		registerUncaughtExceptionHandler();
		Log = MyLogger.kLog();
	}

	/**
	 * 添加Activity到自定义回退栈中
	 * 
	 * @param activity
	 */
	public void addActivity(Activity activity) {
		if (activitys.indexOfValue(activity) == -1)
			activitys.put(SparseArray_flag, activity);
		else {
			activitys.remove(activitys.keyAt(activitys.indexOfValue(activity)));
			activitys.put(SparseArray_flag, activity);
		}
		SparseArray_flag++;
	}

	/**
	 * 退出该应用程序
	 */
	public void exit() {
		for (int i = 0; i < activitys.size(); i++) {
			Activity activity = activitys.valueAt(i);
			activity.finish();
		}
		activitys.clear();
		SparseArray_flag = 1;
//		android.os.Process.killProcess(android.os.Process.myPid());
	}

	/**
	 * 从自定义回退栈中删除activity并执行finish()方法
	 * 
	 * @param activity
	 */
	public void delActivity(Activity activity) {
		activitys.remove(activitys.keyAt(activitys.indexOfValue(activity)));
		activity.finish();
	}

	
	
	@Override
	public void onLowMemory() {
		super.onLowMemory();
		Toast.showLong(this, "当前应用已经内存不足，请注意。。。");
	}

	public SparseArray<String> getUIS() {
		return UIS;
	}

	/**
	 * 获取异步请求池对象
	 * 
	 * @return
	 */
	public List<AsyncTask<?, ?, ?>> getAsyncs() {
		return asyncs;
	}

	/**
	 * 将一个异步任务放入异步请求池
	 * 
	 * @param async
	 */
	public void add(AsyncTask<?, ?, ?> async) {
		asyncs.add(async);
	}

	/**
	 * 清除所有的网络异步请求任务
	 */
	protected void clearAsyncTask() {
		Iterator<AsyncTask<?, ?, ?>> iterator = asyncs.iterator();
		while (iterator.hasNext()) {
			AsyncTask<?, ?, ?> asyncTask = iterator.next();
			if (asyncTask != null && !asyncTask.isCancelled()) {
				asyncTask.cancel(true);
			}
		}
		asyncs.clear();
	}

	/**
	 * 注册App异常崩溃处理器
	 */
	private void registerUncaughtExceptionHandler() {
		Thread.setDefaultUncaughtExceptionHandler(AppException
				.getAppExceptionHandler());
	}

}
