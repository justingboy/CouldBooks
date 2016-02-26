package com.himoo.ydsc.dialog;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import com.himoo.ydsc.R;
import com.himoo.ydsc.base.BasePopWindow;
import com.himoo.ydsc.config.SpConstant;
import com.himoo.ydsc.util.DeviceUtil;
import com.himoo.ydsc.util.SharedPreferences;
import com.ios.radiogroup.SegmentedGroup;

/**
 * 语音速度提示框
 * 
 */
public class SpeechPopupWindow extends BasePopWindow {

	public SegmentedGroup segment_speech;
	private RadioButton speech_1, speech_2, speech_3, speech_4, speech_5;
	private LinearLayout layout_speech;

	public SpeechPopupWindow(Context context, View view, int width,
			int height, boolean focusable) {
		super(context, view, width, height, focusable);
		// TODO Auto-generated constructor stub
	}

	public SpeechPopupWindow(Context context, View view) {
		this(context, view, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT,
				true);
		initView();
		this.setWidth(11 * DeviceUtil.getWidth((Activity) context) / 12);
	}

	@SuppressWarnings("deprecation")
	private void initView() {
		// 使其聚集
		this.setFocusable(false);
		// 设置允许在外点击消失
		this.setOutsideTouchable(true);
		// 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景
		this.setBackgroundDrawable(new BitmapDrawable());
	}

	@Override
	protected void findViews() {
		layout_speech = (LinearLayout) view
				.findViewById(R.id.layout_speech);
		segment_speech = (SegmentedGroup) view
				.findViewById(R.id.segment_speech);
		speech_1 = (RadioButton) view.findViewById(R.id.speech_1);
		speech_2 = (RadioButton) view.findViewById(R.id.speech_2);
		speech_3 = (RadioButton) view.findViewById(R.id.speech_3);
		speech_4 = (RadioButton) view.findViewById(R.id.speech_4);
		speech_5 = (RadioButton) view.findViewById(R.id.speech_5);
	}

	@Override
	protected void setListener() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub
		initSeleted();
		setTitleBarNight();
	}

	/**
	 * 初始化选中的状态
	 */
	private void initSeleted() {
		int speech_speed = SharedPreferences.getInstance().getInt(
				SpConstant.BOOK_READER_SPEED, 50);
		switch (speech_speed) {
		case 10:
			speech_1.setChecked(true);
			break;
		case 30:
			speech_2.setChecked(true);
			break;
		case 50:
			speech_3.setChecked(true);
			break;
		case 70:
			speech_4.setChecked(true);
			break;
		case 100:
			speech_5.setChecked(true);
			break;

		default:
			break;
		}

	}
	
	/**
	 * 夜间模式
	 */
	@SuppressWarnings("deprecation")
	private void setTitleBarNight() {

		boolean isNightMode = SharedPreferences.getInstance().getBoolean(
				SpConstant.BOOK_SETTING_NIGHT_MODE, false);
		boolean isAutoNightMode = SharedPreferences.getInstance().getBoolean(
				SpConstant.BOOK_SETTING_AUTO_NIGHT_MODE_YES, false);
		if (isNightMode || isAutoNightMode) {
			layout_speech.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.speech_bg));
		}else{
			segment_speech.setTintColor(Color.parseColor("#2AAA97"),Color.WHITE);
		}

	}

}
