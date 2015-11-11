package com.himoo.ydsc.activity;

import android.os.Bundle;

import com.himoo.ydsc.R;
import com.himoo.ydsc.ui.swipebacklayout.SwipeBackActivity;

public class DoubanBookActivity extends SwipeBackActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_douban_layout);

	}

	@Override
	protected void initTitleBar() {
		// TODO Auto-generated method stub
		String bookName = getIntent().getStringExtra("key");
		if (bookName == null) {
			bookName = "返回";
		} else {
			bookName = (bookName.length() > 4) ? "返回" : bookName;
		}
		mTitleBar.setTitle("图书列表");
		mTitleBar.setLeftTitle(bookName);
		mTitleBar.setRightLogoGone();

	}
}
