package com.himoo.ydsc.util;

import com.himoo.ydsc.base.BaseApplication;

import android.content.Context;
import android.content.SharedPreferences.Editor;

/**
 * 封装类
 *SharedPreferences
 *
 */
public class SharedPreferences {
	//保存的键值对文件的名字
	private static final String SP_NAME = "ydsc";
	//单例类
	private static SharedPreferences mInstance = null;
	//private权限不可外部类被创建
	private SharedPreferences() {
	}

	public static SharedPreferences getInstance() {
		if (mInstance == null) {
			synchronized (SharedPreferences.class) {
				if (mInstance == null)
					mInstance = new SharedPreferences();
			}
		}
		return mInstance;
	}

	private android.content.SharedPreferences getSp() {
		return BaseApplication.getInstance().getSharedPreferences(SP_NAME,
				Context.MODE_PRIVATE);
	}

	public int getInt(String key, int def) {
		try {
			android.content.SharedPreferences sp = getSp();
			if (sp != null)
				def = sp.getInt(key, def);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return def;
	}

	public void putInt(String key, int val) {
		try {
			android.content.SharedPreferences sp = getSp();
			if (sp != null) {
				Editor e = sp.edit();
				e.putInt(key, val);
				e.commit();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public long getLong(String key, long def) {
		try {
			android.content.SharedPreferences sp = getSp();
			if (sp != null)
				def = sp.getLong(key, def);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return def;
	}

	public void putLong(String key, long val) {
		try {
			android.content.SharedPreferences sp = getSp();
			if (sp != null) {
				Editor e = sp.edit();
				e.putLong(key, val);
				e.commit();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getString(String key, String def) {
		try {
			android.content.SharedPreferences sp = getSp();
			if (sp != null)
				def = sp.getString(key, def);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return def;
	}

	public void putString(String key, String val) {
		try {
			android.content.SharedPreferences sp = getSp();
			if (sp != null) {
				Editor e = sp.edit();
				e.putString(key, val);
				e.commit();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
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

	public void remove(String key) {
		try {
			android.content.SharedPreferences sp = getSp();
			if (sp != null) {
				Editor e = sp.edit();
				e.remove(key);
				e.commit();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
