package com.himoo.ydsc.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.himoo.ydsc.R;
import com.himoo.ydsc.config.BookTheme;
import com.himoo.ydsc.util.DeviceUtil;

public class RefreshDialog extends Dialog {
	/** 刷新提示信息 */
	private TextView refresh_msg;
	public LinearLayout layout_dialog;
	private Context mContext;
	private ProgressBar progressBar;

	public RefreshDialog(Context context) {
		super(context, R.style.Refresh_Dialog);
		mContext = context;
		LayoutInflater mInflater = LayoutInflater.from(getContext());
		View view = mInflater.inflate(R.layout.dialog_book_refresh, null);
		layout_dialog = (LinearLayout) view.findViewById(R.id.layout_dialog);
		progressBar = (ProgressBar) view.findViewById(R.id.refresh_progressBar);
		refresh_msg = (TextView) view.findViewById(R.id.book_refresh_msg);
		setCancelable(true);
		setContentView(view);
	}

	public void setBackground() {
		
		layout_dialog.setBackgroundResource(R.drawable.alert_bg);
		int padding = DeviceUtil.dip2px(mContext, 20);
		layout_dialog.setPaddingRelative(padding, padding,padding, padding);
//		layout_dialog.setBackgroundDrawable(mContext.getResources()
//				.getDrawable(R.drawable.alert_bg));
	}

	public void setTextColor() {
		refresh_msg.setTextColor(BookTheme.THEME_COLOR);
	}

	public void setIndeterminateDrawable() {
		Drawable drawable = null;
		if (BookTheme.THEME_COLOR == BookTheme.BOOK_BLUE) {
			drawable = mContext.getResources().getDrawable(
					R.drawable.dialog_progress_blue);
		} else if (BookTheme.THEME_COLOR == BookTheme.BOOK_GREEN) {
			drawable = mContext.getResources().getDrawable(
					R.drawable.dialog_progress_green);
		} else if (BookTheme.THEME_COLOR == BookTheme.BOOK_YELLOW) {
			drawable = mContext.getResources().getDrawable(
					R.drawable.dialog_progress_yellow);
		} else if (BookTheme.THEME_COLOR == BookTheme.BOOK_RED) {
			drawable = mContext.getResources().getDrawable(
					R.drawable.dialog_progress_red);
		} else if (BookTheme.THEME_COLOR == BookTheme.BOOK_GRAY) {
			drawable = mContext.getResources().getDrawable(
					R.drawable.dialog_progress_black);
		}
		progressBar.setIndeterminateDrawable(drawable);
	}

	public void setMessage(CharSequence message) {
		// TODO Auto-generated method stub
		// super.setMessage(message);
		refresh_msg.setText(message);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			this.dismiss();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
