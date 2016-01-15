package com.himoo.ydsc.activity.more;

import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.himoo.ydsc.R;
import com.himoo.ydsc.config.BookTheme;
import com.himoo.ydsc.config.SpConstant;
import com.himoo.ydsc.ui.swipebacklayout.SwipeBackActivity;
import com.himoo.ydsc.update.BookUpdateUtil;
import com.himoo.ydsc.update.Constants;
import com.himoo.ydsc.util.DeviceUtil;
import com.himoo.ydsc.util.SharedPreferences;
import com.ios.radiogroup.SegmentedGroup;
import com.ios.switchbutton.Configuration;
import com.ios.switchbutton.SwitchButton;
import com.lidroid.xutils.view.annotation.ViewInject;

/**
 * 自动更新设置界面
 * 
 */
public class UpdateSettingActivity extends SwipeBackActivity implements
		OnCheckedChangeListener, RadioGroup.OnCheckedChangeListener {

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

	@ViewInject(R.id.book_update_space_1)
	private RadioButton book_update_space_1;

	@ViewInject(R.id.book_update_space_3)
	private RadioButton book_update_space_3;

	@ViewInject(R.id.book_update_space_6)
	private RadioButton book_update_space_6;

	@ViewInject(R.id.book_update_space_12)
	private RadioButton book_update_space_12;

	@ViewInject(R.id.book_update_network_wifi)
	private RadioButton book_update_network_wifi;

	@ViewInject(R.id.book_update_network_all)
	private RadioButton book_update_network_all;

	@ViewInject(R.id.book_notice_together)
	private RadioButton book_notice_together;

	@ViewInject(R.id.book_notice_details)
	private RadioButton book_notice_details;

	@ViewInject(R.id.book_notice_none)
	private RadioButton book_notice_none;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_more_update);
		initSeleted();
		initListener();
		boolean isOpen = SharedPreferences.getInstance().getBoolean(
				SpConstant.BOOK_UPATE_SETTING, true);
		boolean isSoundOpen = SharedPreferences.getInstance().getBoolean(
				SpConstant.BOOK_UPATE_SETTING_SOUND, false);
		sbBookUdate.setChecked(isOpen ? true : false);
		sbBookUdateRemind.setChecked(isSoundOpen ? true : false);
		setComponentAlpha(!isOpen);

	}

	@Override
	protected void initTitleBar() {
		// TODO Auto-generated method stub
		setSegmentedGroupColor();
		mTitleBar.setLeftTitle(getResources().getString(R.string.main_more));
		mTitleBar.setTitle(getResources().getString(
				R.string.more_service_update));
		mTitleBar.setRightLogoGone();
	}

	/** 设置颜色 */
	private void setSegmentedGroupColor() {

		sbBookUdate.setConfiguration(
				Configuration.getDefault(DeviceUtil.getDisplayDensity(this)),
				BookTheme.THEME_COLOR);
		sbBookUdateRemind.setConfiguration(
				Configuration.getDefault(DeviceUtil.getDisplayDensity(this)),
				BookTheme.THEME_COLOR);

		segmentUpdateSpaces.setTintColor(BookTheme.THEME_COLOR);
		segmentUpdateNet.setTintColor(BookTheme.THEME_COLOR);
		segmentUpdateNotice.setTintColor(BookTheme.THEME_COLOR);
	}

	private void initListener() {
		sbBookUdate.setOnCheckedChangeListener(this);
		sbBookUdateRemind.setOnCheckedChangeListener(this);
		segmentUpdateSpaces.setOnCheckedChangeListener(this);
		segmentUpdateNet.setOnCheckedChangeListener(this);
		segmentUpdateNotice.setOnCheckedChangeListener(this);
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// TODO Auto-generated method stub
		switch (buttonView.getId()) {
		case R.id.book_update_switch_button:
			setComponentAlpha(!isChecked);
			SharedPreferences.getInstance().putBoolean(
					SpConstant.BOOK_UPATE_SETTING, isChecked);
			if (isChecked) {
				if (!BookUpdateUtil.isServiceRunning(this,
						Constants.BOOKUPDATE_SERVICE)) {
					BookUpdateUtil.startTimerService(this);
				}
			} else {
				BookUpdateUtil.cancleAlarmManager(this);
			}

			break;
		case R.id.book_voice_switch_button:
			SharedPreferences.getInstance().putBoolean(
					SpConstant.BOOK_UPATE_SETTING_SOUND, isChecked);
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

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		// TODO Auto-generated method stub

		switch (checkedId) {
		case R.id.book_update_space_1:
			SharedPreferences.getInstance().putInt(
					SpConstant.BOOK_UPATE_SETTING_SPACE, 1);
			break;
		case R.id.book_update_space_3:
			SharedPreferences.getInstance().putInt(
					SpConstant.BOOK_UPATE_SETTING_SPACE, 3);
			break;
		case R.id.book_update_space_6:
			SharedPreferences.getInstance().putInt(
					SpConstant.BOOK_UPATE_SETTING_SPACE, 6);
			break;
		case R.id.book_update_space_12:
			SharedPreferences.getInstance().putInt(
					SpConstant.BOOK_UPATE_SETTING_SPACE, 12);
			break;
		case R.id.book_update_network_wifi:
			SharedPreferences.getInstance().putInt(
					SpConstant.BOOK_UPATE_SETTING_NETTYEP, 1);

			break;
		case R.id.book_update_network_all:
			SharedPreferences.getInstance().putInt(
					SpConstant.BOOK_UPATE_SETTING_NETTYEP, 0);
			break;
		case R.id.book_notice_together:
			SharedPreferences.getInstance().putInt(
					SpConstant.BOOK_UPATE_SETTING_NOTICE, 1);
			break;
		case R.id.book_notice_details:
			SharedPreferences.getInstance().putInt(
					SpConstant.BOOK_UPATE_SETTING_NOTICE, 2);
			break;
		case R.id.book_notice_none:
			SharedPreferences.getInstance().putInt(
					SpConstant.BOOK_UPATE_SETTING_NOTICE, 3);
			break;

		default:
			break;
		}
	}

	/**
	 * 初始化选中的状态
	 */
	private void initSeleted() {
		int index_Space = SharedPreferences.getInstance().getInt(
				SpConstant.BOOK_UPATE_SETTING_SPACE, 1);
		int index_netType = SharedPreferences.getInstance().getInt(
				SpConstant.BOOK_UPATE_SETTING_NETTYEP, 0);
		int index_Notic = SharedPreferences.getInstance().getInt(
				SpConstant.BOOK_UPATE_SETTING_NOTICE, 2);
		switch (index_Space) {
		case 1:
			book_update_space_1.setChecked(true);
			break;
		case 3:
			book_update_space_3.setChecked(true);
			break;
		case 6:
			book_update_space_6.setChecked(true);
			break;
		case 12:
			book_update_space_12.setChecked(true);
			break;

		default:
			break;
		}

		switch (index_netType) {
		case 0:
			book_update_network_all.setChecked(true);
			break;
		case 1:
			book_update_network_wifi.setChecked(true);
			break;

		default:
			break;
		}
		switch (index_Notic) {
		case 1:
			book_notice_together.setChecked(true);
			break;
		case 2:
			book_notice_details.setChecked(true);
			break;
		case 3:
			book_notice_none.setChecked(true);
			break;

		default:
			break;
		}

	}

}
