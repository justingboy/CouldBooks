package com.himoo.ydsc.activity.more;

import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.himoo.ydsc.R;
import com.himoo.ydsc.config.SpConstant;
import com.himoo.ydsc.ui.swipebacklayout.SwipeBackActivity;
import com.himoo.ydsc.ui.utils.Toast;
import com.himoo.ydsc.util.SharedPreferences;
import com.ios.radiogroup.SegmentedGroup;
import com.ios.switchbutton.SwitchButton;
import com.lidroid.xutils.view.annotation.ViewInject;

/**
 * 自动更新设置界面
 * 
 */
public class UpdateSettingActivity extends SwipeBackActivity implements
		OnCheckedChangeListener {

	@ViewInject(R.id.book_update_switch_button)
	private SwitchButton sbBookUdate;

	@ViewInject(R.id.book_voice_switch_button)
	private SwitchButton sbBookUdateRemind;

	@ViewInject(R.id.segment_update_space)
	private SegmentedGroup segmentUpdateSpaces;

	@ViewInject(R.id.segment_update_network)
	private SegmentedGroup segmentUpdateNet;

	@ViewInject(R.id.segment_book_notice)
	private SegmentedGroup segmentUpdateNotice;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_more_update);
		initListener();
		boolean isOpen = SharedPreferences.getInstance().getBoolean(
				SpConstant.BOOK_UPATE_SETTING, false);
		sbBookUdate.setChecked(isOpen ? true : false);
		setComponentAlpha(!isOpen);

	}

	@Override
	protected void initTitleBar() {
		// TODO Auto-generated method stub
		mTitleBar.setLeftTitle(getResources().getString(R.string.main_more));
		mTitleBar.setTitle(getResources().getString(
				R.string.more_service_update));
		mTitleBar.setRightLogoGone();
	}

	private void initListener() {
		sbBookUdate.setOnCheckedChangeListener(this);
		sbBookUdateRemind.setOnCheckedChangeListener(this);
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// TODO Auto-generated method stub
		switch (buttonView.getId()) {
		case R.id.book_update_switch_button:
			setComponentAlpha(!isChecked);
			SharedPreferences.getInstance().putBoolean(
					SpConstant.BOOK_UPATE_SETTING, isChecked);
			break;
		case R.id.book_voice_switch_button:
			Toast.showShort(this, isChecked ? "开启声音" : "关闭声音");

			break;

		default:
			break;
		}

	}

	/**
	 * 设置控件的透明度和是否可以被点击
	 * 
	 * @param isUpdateClose
	 */
	private void setComponentAlpha(boolean isUpdateClose) {
		float alpha = 1.0f;
		boolean isClickable = true;
		if (isUpdateClose) {
			isClickable = false;
			alpha = 0.6f;
		}
		sbBookUdateRemind.setAlpha(alpha);
		sbBookUdateRemind.setEnabled(isClickable);
		segmentUpdateSpaces.setAlpha(alpha);
		segmentUpdateNet.setAlpha(alpha);
		segmentUpdateNotice.setAlpha(alpha);

		segmentUpdateSpaces.setEnabled(isClickable);
		for (int i = 0; i < segmentUpdateSpaces.getChildCount(); i++) {
			segmentUpdateSpaces.getChildAt(i).setEnabled(isClickable);
		}
		for (int i = 0; i < segmentUpdateNet.getChildCount(); i++) {
			segmentUpdateNet.getChildAt(i).setEnabled(isClickable);
		}
		for (int i = 0; i < segmentUpdateNotice.getChildCount(); i++) {
			segmentUpdateNotice.getChildAt(i).setEnabled(isClickable);
		}

		segmentUpdateNet.setEnabled(isClickable);
		segmentUpdateNotice.setEnabled(isClickable);
	}
}
