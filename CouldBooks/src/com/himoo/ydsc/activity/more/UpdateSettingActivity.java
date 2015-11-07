package com.himoo.ydsc.activity.more;

import android.os.Bundle;

import com.himoo.ydsc.R;
import com.himoo.ydsc.ui.swipebacklayout.SwipeBackActivity;

public class UpdateSettingActivity extends SwipeBackActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_more_update);

	}

	@Override
	protected void initTitleBar() {
		// TODO Auto-generated method stub
		mTitleBar.setLeftTitle(getResources().getString(R.string.main_more));
		mTitleBar.setTitle(getResources().getString(
				R.string.more_service_update));
		mTitleBar.setRightLogoGone();
	}
}
