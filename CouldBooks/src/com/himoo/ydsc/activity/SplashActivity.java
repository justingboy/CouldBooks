package com.himoo.ydsc.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.himoo.ydsc.R;
import com.himoo.ydsc.activity.more.PasswordSettingActivity;
import com.himoo.ydsc.http.HttpOperator;
import com.himoo.ydsc.http.LoadLocalBookDb;
import com.himoo.ydsc.http.LoadLocalBookDb.OnLoadDbSuccessListener;
import com.himoo.ydsc.service.LockService;
import com.himoo.ydsc.ui.utils.UIHelper;
import com.himoo.ydsc.update.BookUpdateUtil;
import com.himoo.ydsc.update.Constants;
import com.himoo.ydsc.util.MyLogger;
import com.himoo.ydsc.util.NetWorkUtils;
import com.himoo.ydsc.util.SharedPreferences;

/**
 * 启动Activity 不需要继承
 */
public class SplashActivity extends Activity implements OnLoadDbSuccessListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getWindow().setBackgroundDrawableResource(R.drawable.splash_bg);
		SharedPreferences.getInstance().putInt("mCurrentSelected", 3);
		// Log.i("msg",
		// DeviceUtil.getWidth(this) + "*" + DeviceUtil.getHeight(this));
		// Log.i("msg", "Density = " + DeviceUtil.getDisplayDensity(this));
		if (SharedPreferences.getInstance().getBoolean("laoddb", true)) {
			LoadLocalBookDb.getInstance().loadDb(this, this);
		} else {

			// 验证服务器正常的网址,网络状态正常时进行
			if (NetWorkUtils.isNetConnected(this)) {
				try {
					HttpOperator.validateHostUrlWork(this);
				} catch (Exception e) {
					// TODO: handle exception
					MyLogger.kLog().d(e);
				}
			}

			// 开启了密码保护
			if (!(SharedPreferences.getInstance().getString("password", null) == null)) {
				UIHelper.startToActivity(this, PasswordSettingActivity.class,
						"SplashActivity");
				if (!BookUpdateUtil.isServiceRunning(this,
						Constants.LOCK_SERVICE)) {
					startService(new Intent(SplashActivity.this,
							LockService.class));
				}

				finish();

			} else {

				// 停止800ms
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						startActivity(new Intent(SplashActivity.this,
								HomeActivity.class));
						finish();
					}
				}, 600);
			}
		}

	}

	@Override
	public void onLoadSuccess() {
		// TODO Auto-generated method stub
		// 验证服务器正常的网址,网络状态正常时进行
		SharedPreferences.getInstance().putBoolean("laoddb", false);
		if (NetWorkUtils.isNetConnected(this)) {
			try {
				HttpOperator.validateHostUrlWork(this);
			} catch (Exception e) {
				// TODO: handle exception
				MyLogger.kLog().d(e);
			}
		}
		startActivity(new Intent(SplashActivity.this, HomeActivity.class));
		finish();
	}

}
