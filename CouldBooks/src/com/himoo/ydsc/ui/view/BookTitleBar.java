package com.himoo.ydsc.ui.view;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.himoo.ydsc.R;
import com.himoo.ydsc.util.DeviceUtil;

public class BookTitleBar extends LinearLayout {
	/** The m context. */
	private Activity mActivity;
	/** 左边Title */
	private TextView leftTitle;
	/** 中间的Title */
	private TextView title;
	/** 右边的刷新view */
	public ImageView titlebar_refresh;
	/** TitleBar父布局 */
	private View view;
	/** Title父布局 */
	private LinearLayout titlebar_content;

	public BookTitleBar(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		ininTitleBar(context);
	}

	public BookTitleBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		ininTitleBar(context);
	}

	/**
	 * Inin title bar.
	 * 
	 * @param context
	 *            the context
	 */
	public void ininTitleBar(Context context) {

		mActivity = (Activity) context;
		// 水平排列
		this.setOrientation(LinearLayout.HORIZONTAL);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, DeviceUtil.dip2px(mActivity, 55));
		// this.setLayoutParams(params);
		this.setGravity(Gravity.CENTER);
		LayoutInflater mInflater = LayoutInflater.from(context);
		view = mInflater.inflate(R.layout.activity_titlebar_layout, null);
		leftTitle = (TextView) view.findViewById(R.id.titlebar_left_title);
		titlebar_content = (LinearLayout) view
				.findViewById(R.id.titlebar_content);
		title = (TextView) view.findViewById(R.id.titlebar_title);
		titlebar_refresh = (ImageView) view.findViewById(R.id.titlebar_refresh);
		leftTitle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mActivity.finish();
			}
		});
		this.addView(view, params);
	}

	/**
	 * 设置左边的Title
	 * 
	 * @param text
	 */
	public void setLeftTitle(String text) {
		leftTitle.setText(text);
	}

	/**
	 * 设置中间的Title
	 * 
	 * @param text
	 */
	public void setTitle(String text) {
		title.setText(text);
	}

	/**
	 * 获取右边的logo
	 * 
	 * @return
	 */
	public ImageView getRightLogo() {
		titlebar_refresh.setEnabled(true);
		return titlebar_refresh;
	}

	/**
	 * 设置右边logo的图片
	 * 
	 * @param resId
	 */
	public void setRightLogoDrawable(int resId) {
		titlebar_refresh.setImageDrawable(mActivity.getResources().getDrawable(
				resId));

	}

	/**
	 * 设置宽度和高度
	 * @param width
	 * @param height
	 */
	public void setRightWidthAndHeight(int width, int height) {
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) titlebar_refresh.getLayoutParams();
		params.height = DeviceUtil.dip2px(mActivity, height);
		params.width = DeviceUtil.dip2px(mActivity, width);
		titlebar_refresh.setLayoutParams(params);
	}

	/**
	 * 设置TitleBar的布局背景色
	 * 
	 * @param color
	 */
	public void setTitlebarBackgroundColor(int color) {
		view.setBackgroundColor(color);
	}

	/**
	 * 这显示标题
	 */
	public void setShowSingleTile() {
		leftTitle.setVisibility(View.GONE);
		titlebar_refresh.setVisibility(View.GONE);

	}

	/**
	 * 设置右边View为 GONE 状态
	 */
	public void setRightLogoGone() {
		titlebar_refresh.setEnabled(false);
		titlebar_refresh.setVisibility(View.GONE);
	}

	/**
	 * 设置右边View为 GONE 状态
	 */
	public void setRightLogoVisible() {
		titlebar_refresh.setEnabled(true);
		titlebar_refresh.setVisibility(View.VISIBLE);
	}

	/**
	 * 设置左边的图片
	 * 
	 * @param resId
	 */
	public void setLeftDrawable(int resId) {
		leftTitle.setText(" ");
		leftTitle.setCompoundDrawablesWithIntrinsicBounds(null, null,
				getResources().getDrawable(resId), null);
	}

	/**
	 * 设置Title 是否可见
	 * 
	 * @param resId
	 */
	public void setTitleVisible(boolean isVisible) {
		title.setVisibility(isVisible ? View.VISIBLE : View.GONE);

	}

	/**
	 * 设置Title的自定义布局
	 * 
	 * @param resId
	 */
	public void setTitleView(View view) {
		setTitleVisible(false);
		titlebar_content.addView(view);

	}

	/**
	 * 得到左边的TextView
	 * 
	 * @param resId
	 */
	public TextView getLeftTextView() {

		return leftTitle;

	}

}
