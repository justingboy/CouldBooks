package com.himoo.ydsc.reader.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;

import com.himoo.ydsc.R;
import com.himoo.ydsc.util.DeviceUtil;

public class PageSeekBar extends SeekBar {
	private PopupWindow mPopupWindow;

	private LayoutInflater mInflater;
	private View mView;
	private int[] mPosition;

	private final int mThumbWidth = 25;
	private TextView mTvProgress;
	private int moffLeftX = 100;

	public PageSeekBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		if (DeviceUtil.getDisplayDensity((Activity) context) >= 2.75) {
			moffLeftX = DeviceUtil.dip2px(context, 52)+72;
		} else
			moffLeftX = DeviceUtil.dip2px(context, 52);
		mInflater = LayoutInflater.from(context);
		mView = mInflater.inflate(R.layout.popwindow_layout_page, null);
		mTvProgress = (TextView) mView.findViewById(R.id.tvPop);
		mPopupWindow = new PopupWindow(mView, mView.getWidth(),
				mView.getHeight(), true);
		mPopupWindow.setOutsideTouchable(true);
		mPopupWindow.setFocusable(false);
		mPosition = new int[2];
	}

	public void setSeekBarText(String str) {
		mTvProgress.setText(str);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			this.getLocationOnScreen(mPosition);
			mPopupWindow.showAtLocation(this, Gravity.CENTER, 0, moffLeftX);

			break;
		case MotionEvent.ACTION_CANCEL:
			mPopupWindow.dismiss();
			break;
		case MotionEvent.ACTION_UP:
			mPopupWindow.dismiss();
			break;
		}

		return super.onTouchEvent(event);
	}

	private int getViewWidth(View v) {
		int w = View.MeasureSpec.makeMeasureSpec(0,
				View.MeasureSpec.UNSPECIFIED);
		int h = View.MeasureSpec.makeMeasureSpec(0,
				View.MeasureSpec.UNSPECIFIED);
		v.measure(w, h);
		return v.getMeasuredWidth();
	}

	private int getViewHeight(View v) {
		int w = View.MeasureSpec.makeMeasureSpec(0,
				View.MeasureSpec.UNSPECIFIED);
		int h = View.MeasureSpec.makeMeasureSpec(0,
				View.MeasureSpec.UNSPECIFIED);
		v.measure(w, h);
		return v.getMeasuredHeight();
	}

	@Override
	protected synchronized void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		int thumb_x = 0;
		try {
			thumb_x = this.getProgress() * (this.getWidth() - mThumbWidth)
					/ this.getMax();
		} catch (Exception e) {
			Log.e("msg", e.getMessage() + "");
		}
		super.onDraw(canvas);

		if (mPopupWindow != null) {
			try {
				this.getLocationOnScreen(mPosition);
				mPopupWindow.update(thumb_x + mPosition[0] - (2 * moffLeftX)
						- getViewWidth(mView) / 2 + mThumbWidth / 2, moffLeftX,
						getViewWidth(mView), getViewHeight(mView));
				Log.i("msg", "getViewHeight(mView) = " + getViewHeight(mView)
						+ "");
			} catch (Exception e) {

			}
		}

	}

}
