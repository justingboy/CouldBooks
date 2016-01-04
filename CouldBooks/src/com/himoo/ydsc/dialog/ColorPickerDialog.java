package com.himoo.ydsc.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.himoo.ydsc.R;
import com.himoo.ydsc.reader.view.ColorPickerView;
import com.himoo.ydsc.util.DeviceUtil;

public class ColorPickerDialog extends Dialog {

	/** Dialog 最外层布局 */
	private LinearLayout mLayout;
	private Context mContext;
	private ColorPickerView pickerView;

	public ColorPickerDialog(Context context) {
		super(context, R.style.PickerColorTheme);
		// TODO Auto-generated constructor stub
		this.mContext = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		initView(getContext());
		setContentView(mLayout);
		// 设置铺满宽度
		WindowManager.LayoutParams lp = this.getWindow().getAttributes();
		lp.width = (int) (DeviceUtil.getWidth((Activity) mContext)); // 设置宽度
		this.getWindow().setAttributes(lp);
		getWindow().setGravity(Gravity.BOTTOM);

		// ColorPickerView colorPickerDisk = (ColorPickerView)
		// this.findViewById(R.id.colorPickerDisk);
		// colorPickerDisk
		// .setOnColorChangedListennerD(new OnColorChangedListenerD() {
		//
		// @Override
		// public void onbgColorChanged(int color, String hexStrColor) {
		// // // TODO Auto-generated method stub
		// // btnColorDisk.setBackgroundColor(color);
		// // btnColorDisk.setText("当前文字的颜色：魔天记");
		// }
		//
		// @Override
		// public void onTextColorChanged(int color, String hexStrColor) {
		// // TODO Auto-generated method stub
		// // // textColor.setBackgroundColor(color);
		// // btnColorDisk.setTextColor(color);
		// // btnColorDisk.setText("当前文字的颜色：魔天记");
		//
		// }
		// });

	}

	/**
	 * 初始化布局
	 */
	private void initView(Context context) {
		// 最外层布局LinearLayout
		this.mLayout = new LinearLayout(context);
		this.mLayout.setOrientation(LinearLayout.VERTICAL);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		this.mLayout.setLayoutParams(params);
		this.mLayout.setBackgroundColor(Color.parseColor("#D6D6D6"));
		// 分享的Title
		TextView mTitleView = new TextView(context);
		mTitleView.setGravity(Gravity.CENTER);
		mTitleView.setText("拖动调整背景和文字颜色");
		mTitleView.setTextColor(Color.parseColor("#7F7F7F"));
		mTitleView.setTextSize(DeviceUtil.sp2px(context, 8));
		LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, DeviceUtil.dip2px(context, 45));
		this.mLayout.addView(mTitleView, titleParams);

		pickerView = new ColorPickerView(context);
		LinearLayout.LayoutParams pickerParams = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		this.mLayout.addView(pickerView, pickerParams);

	}

	public ColorPickerView getPickerColorView() {
		return pickerView;
	}

}
