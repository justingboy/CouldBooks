package com.himoo.ydsc.reader.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.net.Uri;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.view.WindowManager;

/**
 * ��װϵͳ����
 * @author why
 */
public class SystemBrightManager {
	
	// �ж��Ƿ������Զ����ȵ���
	public static boolean isAutoBrightness(Activity activity) {
		boolean autoBrightness = false;
		ContentResolver contentResolver = activity.getContentResolver();
		try {
			autoBrightness = Settings.System.getInt(contentResolver,
					Settings.System.SCREEN_BRIGHTNESS_MODE) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
		} catch (SettingNotFoundException e) {
			e.printStackTrace();
		}
		return autoBrightness;
	}
	
	// ��ȡ��ǰϵͳ����ֵ
	public static int getBrightness(Activity activity) {
		int brightValue = 0; 
		ContentResolver contentResolver = activity.getContentResolver();
		try {
			brightValue = Settings.System.getInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS);
		} catch (SettingNotFoundException e) {
			e.printStackTrace();
		}  
		return brightValue;
	}
	
	// �ı���Ļ����
	public static void setBrightness(Activity activity, int brightValue) {
		WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
		lp.screenBrightness = (brightValue <= 0 ? -1.0f : brightValue / 255f);
		activity.getWindow().setAttributes(lp);
	}
	
	// ���������Զ�����ģʽ
	public static void startAutoBrightness(Activity activity) {
		Settings.System.putInt(activity.getContentResolver(),
				Settings.System.SCREEN_BRIGHTNESS_MODE,
				Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
		Uri uri = android.provider.Settings.System.getUriFor("screen_brightness");  
		activity.getContentResolver().notifyChange(uri, null);  
	}
	
	// ֹͣ�Զ�����ģʽ
	public static void stopAutoBrightness(Activity activity) {
		Settings.System.putInt(activity.getContentResolver(),
				Settings.System.SCREEN_BRIGHTNESS_MODE,
				Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
		Uri uri = android.provider.Settings.System.getUriFor("screen_brightness");  
		activity.getContentResolver().notifyChange(uri, null);  
	}
	
	/** 
     * ���õ�ǰ��Ļ���ȵ�ģʽ  
    * SCREEN_BRIGHTNESS_MODE_AUTOMATIC=1 Ϊ�Զ�������Ļ���� 
    * SCREEN_BRIGHTNESS_MODE_MANUAL=0 Ϊ�ֶ�������Ļ���� 
    */  
	public static void setBrightnessMode(Activity activity, int brightMode) {  
        Settings.System.putInt(activity.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, brightMode);  
    }  
}
