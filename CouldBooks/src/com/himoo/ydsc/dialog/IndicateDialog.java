package com.himoo.ydsc.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.himoo.ydsc.R;

/**
 * 
 * 半透明提示dialog
 */
public class IndicateDialog extends Dialog {

	public OnDialogDismissListener listener;
	private ImageView mIndicateIV;

	public IndicateDialog(Context context, OnDialogDismissListener listener) {
		super(context, R.style.Dialog_Fullscreen_Toast);
		this.listener = listener;
	}

	public IndicateDialog(Context context) {
		super(context, R.style.Dialog_Fullscreen_Toast);
		// TODO Auto-generated constructor stub
	}

	public IndicateDialog(Context context, int theme) {
		super(context, theme);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_indicate);
		mIndicateIV = (ImageView) this.findViewById(R.id.iv_dialog_indicate);
		mIndicateIV.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (listener != null)
					listener.onDialogDismiss();
				dismiss();
			}
		});

		// 设置全屏
		getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT);

	}

	public void setIndicateDrawable(int resId) {
		mIndicateIV.setBackgroundResource(resId);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public interface OnDialogDismissListener {
		public void onDialogDismiss();
	}

}
