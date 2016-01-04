package com.himoo.ydsc.util;

import com.himoo.ydsc.base.BaseApplication;

import android.content.Context;
import android.content.SharedPreferences.Editor;

/**
 * 专门用于保存下载的状态
 */
public class SP {
	private static final String SP_NAME = "book_statue";

	private android.content.SharedPreferences getSp() {
		return BaseApplication.getInstance().getSharedPreferences(SP_NAME,
				Context.MODE_PRIVATE);
	}

	// 单例类
	private static SP mInstance = null;

	// private权限不可外部类被创建
	private SP() {
	}

	public static SP getInstance() {
		if (mInstance == null) {
			synchronized (SP.class) {
				if (mInstance == null)
					mInstance = new SP();
			}
		}
		return mInstance;
	}

	public boolean getBoolean(String key, boolean def) {
		try {
			android.content.SharedPreferences sp = getSp();
			if (sp != null)
				def = sp.getBoolean(key, def);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return def;
	}

	public void putBoolean(String key, boolean val) {
		try {
			android.content.SharedPreferences sp = getSp();
			if (sp != null) {
				Editor e = sp.edit();
				e.putBoolean(key, val);
				e.commit();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
