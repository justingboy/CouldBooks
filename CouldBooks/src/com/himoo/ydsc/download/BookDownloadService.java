package com.himoo.ydsc.download;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.util.LogUtils;

import java.util.List;

/**
 * 下载书籍的Service
 * 
 */
public class BookDownloadService extends Service {
	/** 下载管理类 */
	public static BookDownloadManager DOWNLOAD_MANAGER;

	/**
	 * 获得BookDownloadManager实例
	 * @param appContext
	 * @return
	 */
	public static BookDownloadManager getDownloadManager(Context appContext) {
		if (!BookDownloadService.isServiceRunning(appContext)) {
			Intent downloadIntent = new Intent("book.download.service.action");
			downloadIntent.setPackage("com.himoo.ydsc");
			appContext.startService(downloadIntent);
		}
		if (BookDownloadService.DOWNLOAD_MANAGER == null) {
			BookDownloadService.DOWNLOAD_MANAGER = new BookDownloadManager(
					appContext);
		}
		return DOWNLOAD_MANAGER;
	}

	public BookDownloadService() {
		super();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {
		if (DOWNLOAD_MANAGER != null) {
			try {
				DOWNLOAD_MANAGER.stopAllDownload();
				DOWNLOAD_MANAGER.backupDownloadInfoList();
			} catch (DbException e) {
				LogUtils.e(e.getMessage(), e);
			}
		}
		super.onDestroy();
	}

	/**
	 * 判断这个服务是否在运行
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isServiceRunning(Context context) {
		boolean isRunning = false;

		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> serviceList = activityManager
				.getRunningServices(Integer.MAX_VALUE);

		if (serviceList == null || serviceList.size() == 0) {
			return false;
		}

		for (int i = 0; i < serviceList.size(); i++) {
			if (serviceList.get(i).service.getClassName().equals(
					BookDownloadService.class.getName())) {
				isRunning = true;
				break;
			}
		}
		return isRunning;
	}
}
