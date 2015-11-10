package com.himoo.ydsc.activity.more;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.himoo.ydsc.R;
import com.himoo.ydsc.ui.swipebacklayout.SwipeBackActivity;
import com.himoo.ydsc.ui.utils.UIHelper;
import com.himoo.ydsc.util.SharedPreferences;
import com.lidroid.xutils.view.annotation.ViewInject;

public class PasswordLockActivity extends SwipeBackActivity implements
		OnClickListener {

	@ViewInject(R.id.password_open)
	private TextView password_open;

	@ViewInject(R.id.password_close)
	private TextView password_close;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_more_password);
		initData();
		initListener();

	}

	@Override
	protected void initTitleBar() {
		// TODO Auto-generated method stub
		mTitleBar.setLeftTitle(getResources().getString(R.string.main_more));
		mTitleBar.setTitle(getResources().getString(
				R.string.more_password_protect));
		mTitleBar.setRightLogoGone();
	}

	private void initData() {
		String password = SharedPreferences.getInstance().getString("password",
				null);
		if (password == null) {
			password_open.setText("开启密码");
		} else {
			password_open.setText("关闭密码");
		}

	}

	private void initListener() {
		password_open.setOnClickListener(this);
		if (password_open.getText().equals("关闭密码")) {
			password_close.setOnClickListener(this);
		} else {
			password_close.setOnClickListener(null);
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.password_open:
			UIHelper.startForReseltToActivity(this,
					PasswordSettingActivity.class);
			break;
		case R.id.password_close:
			
			Intent intent = new Intent(this, PasswordSettingActivity.class);
			intent.putExtra("fixPw", true);
			startActivity(intent);
			overridePendingTransition(R.anim.activity_zoom_in, 0);

			break;

		default:
			break;
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 0 && resultCode == RESULT_OK) {
			initData();
			initListener();
		}

	}
}
