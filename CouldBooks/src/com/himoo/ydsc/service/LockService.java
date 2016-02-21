package com.himoo.ydsc.service;

import java.util.Timer;
import java.util.TimerTask;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

public class LockService extends Service {
	private Timer mTimer;
	public static final int FOREGROUND_ID = 0;

	private void startTimer() {
		if (mTimer == null) {
			mTimer = new Timer();
			LockTask lockTask = new LockTask(this);
			mTimer.schedule(lockTask, 0L, 1000L);
		}
	}

	public IBinder onBind(Intent intent) {
		return null;
	}

	public void onCreate() {
		super.onCreate();
		startForeground(FOREGROUND_ID, new Notification());
	}

	public int onStartCommand(Intent intent, int flags, int startId) {
		startTimer();
		return super.onStartCommand(intent, flags, startId);
	}

	public void onDestroy() {
		stopForeground(true);
		mTimer.cancel();
		mTimer.purge();
		mTimer = null;
		super.onDestroy();
	}

	public class LockTask extends TimerTask {
		// 是否开启锁
		private boolean isLock = true;
		private Context mContext;
		private String packageName = "com.himoo.ydsc";
		private String className = "com.himoo.ydsc.activity.more.PasswordSettingActivity";
		private ActivityManager mActivityManager;

		public LockTask(Context context) {
			mContext = context;
			mActivityManager = (ActivityManager) context
					.getSystemService("activity");
		}

		@Override
		public void run() {
			@SuppressWarnings("deprecation")
			ComponentName topActivity = mActivityManager.getRunningTasks(1)
					.get(0).topActivity;
			String topPackageName = topActivity.getPackageName();
			String topClassName = topActivity.getClassName();
			if (packageName.equals(topPackageName)&&!isLock&&!className.equals(topClassName)) {
				startToLockActivity(mContext);
			}
			if (packageName.equals(topPackageName)) {
				isLock = true;
			} else {
				isLock = false;
			}
		}
	}

	private void startToLockActivity(Context context) {
		Intent intent = new Intent();
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setClassName("com.himoo.ydsc",
				"com.himoo.ydsc.activity.more.PasswordSettingActivity");
		intent.putExtra("key", "SplashActivity");
		intent.putExtra("unlock", true);
		intent.putExtra("lockService", true);
		context.startActivity(intent);
	}
}