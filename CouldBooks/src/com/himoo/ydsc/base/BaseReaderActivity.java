package com.himoo.ydsc.base;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;

import com.himoo.ydsc.dialog.RefreshDialog;

public class BaseReaderActivity extends FragmentActivity {
	/** 展示 刷新Dialog */
	private static final int REFRESH_DIALOG_SHOW = 0;
	/** 关闭 刷新Dialog */
	private static final int REFRESH_DIALOG_DIMISS = 1;

	private RefreshDialog mDialog;

	public Handler refreshHandler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case REFRESH_DIALOG_SHOW:
				if (mDialog == null) {
					mDialog = new RefreshDialog(BaseReaderActivity.this);
					mDialog.setBackground();
					mDialog.setTextColor();
					mDialog.setIndeterminateDrawable();
					mDialog.setCancelable(false);

				}
				mDialog.setMessage(msg.obj.toString());
				if (!mDialog.isShowing()) {
					mDialog.show();
				}
				break;
			case REFRESH_DIALOG_DIMISS:
				if (mDialog != null)
					if (mDialog.isShowing()) {
						mDialog.dismiss();
					}
				break;

			default:
				break;
			}

		};
	};

	/**
	 * 　显示 刷新Dialog
	 * 
	 * @param string
	 */
	protected void showRefreshDialog(String string) {
		Message msg = refreshHandler.obtainMessage();
		msg.what = REFRESH_DIALOG_SHOW;
		msg.obj = string;
		refreshHandler.sendMessage(msg);
	}

	/** 　关闭 刷新Dialog */
	protected void dismissRefreshDialog() {
		Message msg = refreshHandler.obtainMessage();
		msg.what = REFRESH_DIALOG_DIMISS;
		refreshHandler.sendMessage(msg);
	}

	@Override
	public Resources getResources() {
		Resources res = super.getResources();
		Configuration config = new Configuration();
		config.setToDefaults();
		res.updateConfiguration(config, res.getDisplayMetrics());
		return res;
	}


}
