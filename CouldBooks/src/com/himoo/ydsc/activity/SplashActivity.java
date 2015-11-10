package com.himoo.ydsc.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.himoo.ydsc.R;
import com.himoo.ydsc.activity.more.PasswordSettingActivity;
import com.himoo.ydsc.http.HttpOperator;
import com.himoo.ydsc.ui.utils.UIHelper;
import com.himoo.ydsc.util.SharedPreferences;

/**
 * 启动Activity 不需要继承
 */
public class SplashActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getWindow().setBackgroundDrawableResource(R.drawable.splash_bg);

		// 验证服务器正常的网址
		HttpOperator.validateHostUrlWork(this);
		// 开启了密码保护
		if (!(SharedPreferences.getInstance().getString("password", null) == null)) {
			UIHelper.startToActivity(this, PasswordSettingActivity.class,
					"SplashActivity");
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
