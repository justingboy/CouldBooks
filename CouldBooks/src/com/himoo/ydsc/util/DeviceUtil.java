package com.himoo.ydsc.util;

import android.app.Activity;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;

/**
 * 分辨率转换工具
 */
public class DeviceUtil {

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 sp,字体的转换
	 */
	public static int px2sp(Context context, float pxValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (pxValue / fontScale + 0.5f);
	}

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 获取DisplayMetrics，包括屏幕高宽，密度等
	 * 
	 * @param context
	 * @return
	 */
	public static DisplayMetrics getDisplayMetrics(Activity context) {
		DisplayMetrics dm = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm;
	}

	/**
	 * 获得屏幕宽度 px
	 * 
	 * @param context
	 * @return
	 */
	public static int getWidth(Activity context) {
		DisplayMetrics dm = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm.widthPixels;
	}

	/**
	 * 获得屏幕高度 px
	 * 
	 * @param context
	 * @return
	 */
	public static int getHeight(Activity context) {
		DisplayMetrics dm = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm.heightPixels;
	}

	/**
	 * 获取IMSI
	 * 
	 * @param context
	 * @return String
	 */
	public static String getIMSI(Context context) {
		try {
			if (context == null) {
				return "";
			}
			TelephonyManager telephonyManager = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			String imsi = telephonyManager.getSubscriberId();
			if (imsi != null && !imsi.equals("")) {
				return imsi;
			}

		} catch (Exception exception1) {
			return "";
		}
		return "";
	}

	/**
	 * 获取IMEI
	 * 
	 * @param context
	 * @return
	 */
	public static String getIMEI(Context context) {
		try {
			if (context == null) {
				return "";
			}
			TelephonyManager telephonyManager = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			String imei = telephonyManager.getDeviceId();
			if (imei != null && !imei.equals("")) {
				return imei;
			}
		} catch (Exception exception1) {
			return "";
		}

		return "";
	}

	/**
	 * 检测Sdcard是否存在
	 * 
	 * @return
	 */
	public static boolean isExitsSdcard() {
		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED))
			return true;
		else
			return false;
	}

}
