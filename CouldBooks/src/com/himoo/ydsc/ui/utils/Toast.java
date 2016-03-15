package com.himoo.ydsc.ui.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.himoo.ydsc.R;
import com.himoo.ydsc.base.BaseApplication;
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
	private static android.widget.Toast toastError;
	private static android.widget.Toast toastBg;
	private static TextView tv;
	private static TextView tvBg;

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
	public static void showLong(CharSequence message) {
		if (isShow)
			android.widget.Toast.makeText(
					BaseApplication.getInstance().getApplicationContext(),
					message, android.widget.Toast.LENGTH_LONG).show();
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
			View layout = inflater.inflate(R.layout.toast_custom_view, null,
					false);
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

	/**
	 * 带有图片的Toast
	 * 
	 * @param context
	 * @param message
	 */
	public static void showError(Context context, String message) {
		if (toastError == null) {
			toastError = new android.widget.Toast(context);
			LayoutInflater inflater = LayoutInflater.from(context);
			View layout = inflater.inflate(R.layout.toast_custom_view, null,
					false);
			tv = (TextView) layout.findViewById(R.id.tv_toast_msg);
			Drawable img = context.getResources().getDrawable(R.drawable.dialog_close);
			img.setBounds(0, 0, img.getMinimumWidth(), img.getMinimumHeight());
			tv.setCompoundDrawables(null, img, null, null);
			toastError.setGravity(Gravity.CENTER, 0, -DeviceUtil.dip2px(context, 25));
			toastError.setDuration(android.widget.Toast.LENGTH_SHORT);
			toastError.setView(layout);
		}
		if (tv != null) {
			tv.setText(message);
			tv.setTextColor(BookTheme.THEME_COLOR);
		}
		toastError.show();
	}

	
	/**
	 * 带有图片的Toast
	 * 
	 * @param context
	 * @param message
	 */
	@SuppressWarnings("deprecation")
	public static void showBg(Context context, String message) {
		if (toastBg == null) {
			toastBg = new android.widget.Toast(context);
			LayoutInflater inflater = LayoutInflater.from(context);
			View layout = inflater.inflate(R.layout.toast_custom_view, null,
					false);
			LinearLayout layout_toast = (LinearLayout) layout
					.findViewById(R.id.layout_toast);
			layout_toast.setBackgroundDrawable(context.getResources()
					.getDrawable(R.drawable.bg_toast));
			tvBg = (TextView) layout.findViewById(R.id.tv_toast_msg);
			// tvBg.setCompoundDrawables(null, null, null, null);
			toastBg.setGravity(Gravity.CENTER, 0,
					-DeviceUtil.dip2px(context, 35));
			toastBg.setDuration(android.widget.Toast.LENGTH_SHORT);
			toastBg.setView(layout);
		}
		if (tvBg != null) {
			tvBg.setText(message);
			tvBg.setTextColor(Color.WHITE);
		}
		toastBg.show();
	}

}