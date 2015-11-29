package com.himoo.ydsc.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * 通知主题改变的广播
 *
 */
public class BookThemeReceiver extends BroadcastReceiver {

	public OnThemeChangeListener listener;

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		if (listener != null)
			listener.onThemeChange();

	}

	/**
	 * 设置主题改变监听器
	 * 
	 * @param listener
	 */
	public void setonThemeChangeListener(OnThemeChangeListener listener) {
		this.listener = listener;
	}

	public interface OnThemeChangeListener {
		public void onThemeChange();
	}

}
