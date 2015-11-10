package com.himoo.ydsc.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.himoo.ydsc.R;

public class RefreshDialog extends Dialog {
	/** 刷新提示信息 */
	private TextView refresh_msg;

	public RefreshDialog(Context context) {
		super(context, R.style.Refresh_Dialog);

		LayoutInflater mInflater = LayoutInflater.from(getContext());
		View view = mInflater.inflate(R.layout.dialog_book_refresh, null);
		refresh_msg = (TextView) view.findViewById(R.id.book_refresh_msg);
		setCancelable(false);
		setContentView(view);
	}

	public void setMessage(CharSequence message) {
		// TODO Auto-generated method stub
		// super.setMessage(message);
		refresh_msg.setText(message);
	}

}
