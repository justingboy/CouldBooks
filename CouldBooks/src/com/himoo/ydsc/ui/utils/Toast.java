package com.himoo.ydsc.ui.utils;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.himoo.ydsc.R;
import com.himoo.ydsc.config.BookTheme;
import com.himoo.ydsc.util.DeviceUtil;

/**
 * Toast统一管理类
 */
public class Toast {

	private Toast() {
		/* cannot be instantiated */
		throw new UnsupportedOperationException("cannot be instantiated");
	}

	public static boolean isShow = true;
	private static android.widget.Toast toast;
	private static TextView tv;

	/**
	 * 短时间显示Toast
	 * 
	 * @param context
	 * @param message
	 */
	public static void showShort(Context context, CharSequence message) {
		if (isShow)
			android.widget.Toast.makeText(context, message,
					android.widget.Toast.LENGTH_SHORT).show();
	}

	/**
	 * 短时间显示Toast
	 * 
	 * @param context
	 * @param message
	 */
	public static void showShort(Context context, int message) {
		if (isShow)
			android.widget.Toast.makeText(context, message,
					android.widget.Toast.LENGTH_SHORT).show();
	}

	/**
	 * 长时间显示Toast
	 * 
	 * @param context
	 * @param message
	 */
	public static void showLong(Context context, CharSequence message) {
		if (isShow)
			android.widget.Toast.makeText(context, message,
					android.widget.Toast.LENGTH_LONG).show();
	}

	/**
	 * 长时间显示Toast
	 * 
	 * @param context
	 * @param message
	 */
	public static void showLong(Context context, int message) {
		if (isShow)
			android.widget.Toast.makeText(context, message,
					android.widget.Toast.LENGTH_LONG).show();
	}

	/**
	 * 自定义显示Toast时间
	 * 
	 * @param context
	 * @param message
	 * @param duration
	 */
	public static void show(Context context, CharSequence message, int duration) {
		if (isShow)
			android.widget.Toast.makeText(context, message, duration).show();
	}

	/**
	 * 自定义显示Toast时间
	 * 
	 * @param context
	 * @param message
	 * @param duration
	 */
	public static void show(Context context, int message, int duration) {
		if (isShow)
			android.widget.Toast.makeText(context, message, duration).show();
	}

	/**
	 * 带有图片的Toast
	 * 
	 * @param context
	 * @param message
	 */
	public static void show(Context context, String message) {
		if (toast == null) {
			toast = new android.widget.Toast(context);
			LayoutInflater inflater = LayoutInflater.from(context);
			View layout = inflater.inflate(R.layout.toast_custom_view, null,false);
			tv = (TextView) layout.findViewById(R.id.tv_toast_msg);
			toast.setGravity(Gravity.CENTER, 0, -DeviceUtil.dip2px(context, 25));
			toast.setDuration(android.widget.Toast.LENGTH_SHORT);
			toast.setView(layout);
		}
		if (tv != null) {
			tv.setText(message);
			tv.setTextColor(BookTheme.THEME_COLOR);
		}
		toast.show();
	}

}