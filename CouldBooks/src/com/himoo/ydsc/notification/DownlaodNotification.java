package com.himoo.ydsc.notification;

import com.himoo.ydsc.R;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.TrafficStats;
import android.widget.RemoteViews;

/**
 * 下载小说的通知类 关于Notification
 * 
 */
public class DownlaodNotification {
	private long lastTotalRxBytes = 0;
	private long lastTimeStamp = 0;
	public NotificationManager notifiManger;
	private Context mContext;
	private Notification notification;
	private RemoteViews remoteViews;
	private int progress = 0;
	private static final int NOTIFI_ID = 0;
	private String bookName = "凡人修仙传";

	public DownlaodNotification(Context context) {
		this.mContext = context;
		notifiManger = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
	}

	@SuppressWarnings("deprecation")
	public void creatNotification(String bookName, String tickText) {
		this.bookName = bookName;
		if (notification == null) {

			notification = new Notification(
					android.R.drawable.stat_sys_download_done, bookName
							+ tickText, System.currentTimeMillis());
			remoteViews = new RemoteViews(mContext.getPackageName(),
					R.layout.book_download_notification_view);
			notification.contentView = remoteViews;
			Intent intent = new Intent();
			PendingIntent pIntent = PendingIntent.getActivity(mContext, 0,
					intent, 0);
			notification.contentIntent = pIntent;
			notification.flags = Notification.FLAG_AUTO_CANCEL;
			notifiManger.notify(NOTIFI_ID, notification);
		}
		remoteViews.setTextViewText(R.id.download_notication_title, bookName
				+ "   " + progress + "%");
		// 在sdk版本0之前的背景是白色的，使得白色的字体无法显示
		if (android.os.Build.VERSION.SDK_INT <= 9) {
			remoteViews.setTextColor(R.id.download_notication_title, mContext
					.getResources().getColor(R.color.alarm_playback_color));
			remoteViews.setTextColor(R.id.tv_progress, mContext.getResources()
					.getColor(R.color.alarm_playback_color));
		}
		remoteViews.setProgressBar(R.id.progress, 100, progress, false);

		

	}

	private long getTotalRxBytes() {
		return TrafficStats.getUidRxBytes(mContext.getApplicationInfo().uid) == TrafficStats.UNSUPPORTED ? 0
				: (TrafficStats.getTotalRxBytes() / 1024);// 转为KB
	}

	public Long getNetSpeed() {

		long nowTotalRxBytes = getTotalRxBytes();
		long nowTimeStamp = System.currentTimeMillis();
		// 毫秒转换
		long speed = ((nowTotalRxBytes - lastTotalRxBytes) * 1000 / (nowTimeStamp - lastTimeStamp));
		lastTimeStamp = nowTimeStamp;
		lastTotalRxBytes = nowTotalRxBytes;

		return speed;
	}

	/**
	 * 设置下载进度和当前的网速
	 * 
	 * @param progress
	 * @param netSpeed
	 */
	public void setProgressAndNetSpeed(int progress, long netSpeed) {
		remoteViews.setTextViewText(R.id.download_notication_title, bookName
				+ "   " + progress + "%");
		remoteViews.setProgressBar(R.id.progress, 100, progress, false);
		remoteViews.setTextViewText(R.id.tv_progress, netSpeed + "kb/s");
		notifiManger.notify(NOTIFI_ID, notification);
		lastTotalRxBytes = getTotalRxBytes();
		lastTimeStamp = System.currentTimeMillis();
	}

	/**
	 * 下载线程
	 */
	class DownloadThread extends Thread {

		@Override
		public void run() {
			int now_progress = 0;
			while (now_progress <= 100) {
				progress = now_progress;

				try {
					Thread.sleep(1 * 500);
					setProgressAndNetSpeed(now_progress, getNetSpeed());
				} catch (InterruptedException e) {
				}
				now_progress += 5;
			}

			// 下载完成
			creatNotification(bookName, "下载完成!");

			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			notifiManger.cancel(NOTIFI_ID);

		}
	}

}
