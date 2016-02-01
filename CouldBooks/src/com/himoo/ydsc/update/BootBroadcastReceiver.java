package com.himoo.ydsc.update;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

import com.himoo.ydsc.config.SpConstant;
import com.himoo.ydsc.util.SharedPreferences;

/**
 * 开机广播,用于开机后，自动开启定时任务
 * 
 */
public class BootBroadcastReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(final Context context, Intent intent) {
		if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
			boolean isOpenUpdate = SharedPreferences.getInstance().getBoolean(
					SpConstant.BOOK_UPATE_SETTING, true);
			{
				if (isOpenUpdate) {
					Handler handler = new Handler(Looper.getMainLooper());
					// 1分钟后开启定时任务
					handler.postDelayed(new Runnable() {
						@Override
						public void run() {
							if (!BookUpdateUtil.isServiceRunning(context,
									Constants.BOOKUPDATE_SERVICE)) {
								BookUpdateUtil.startTimerService(context);
								
							}
						}
					}, Constants.BROADCAST_ELAPSED_TIME_DELAY);
				}
			}

		}
	}
}
