package com.himoo.ydsc.ui.swipebacklayout;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.himoo.ydsc.R;
import com.himoo.ydsc.dialog.RefreshDialog;
import com.himoo.ydsc.ui.view.BookTitleBar;
import com.himoo.ydsc.util.MyLogger;
import com.lidroid.xutils.ViewUtils;
import com.readystatesoftware.systembartint.SystemBarTintManager;

public abstract class SwipeBackActivity extends FragmentActivity implements
		SwipeBackActivityBase {
	private SwipeBackActivityHelper mHelper;
	public MyLogger Log;
	protected BookTitleBar mTitleBar;
	protected SystemBarTintManager tintManager;
	
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
					mDialog = new RefreshDialog(SwipeBackActivity.this);
					mDialog.setCancelable(false);
				}
				mDialog.setMessage(msg.obj.toString());
				if (!mDialog.isShowing()) {
					mDialog.dismiss();
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
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		mHelper = new SwipeBackActivityHelper(this);
		mHelper.onActivityCreate();
		Log = MyLogger.kLog();

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			setTranslucentStatus();
		}
		tintManager = new SystemBarTintManager(this);
		tintManager.setStatusBarTintEnabled(true);
		tintManager.setStatusBarTintResource(R.color.status_bar_bg);// 通知栏所需颜色
	}

	/**
	 * 重写setContentView()方法，添加自定义标题栏
	 */
	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);
		mTitleBar = (BookTitleBar) this.findViewById(R.id.book_titleBar);
		ViewUtils.inject(this);
		initTitleBar();

	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mHelper.onPostCreate();
	}

	@Override
	public View findViewById(int id) {
		View v = super.findViewById(id);
		if (v == null && mHelper != null)
			return mHelper.findViewById(id);
		return v;
	}

	@Override
	public SwipeBackLayout getSwipeBackLayout() {
		return mHelper.getSwipeBackLayout();
	}

	@Override
	public void setSwipeBackEnable(boolean enable) {
		getSwipeBackLayout().setEnableGesture(enable);
		
         
	}

	@Override
	public void scrollToFinishActivity() {
		Utils.convertActivityToTranslucent(this);
		getSwipeBackLayout().scrollToFinishActivity();
	}

	@TargetApi(19)
	private void setTranslucentStatus() {
		Window window = getWindow();
		// Translucent status bar
		window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
				WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		// Translucent navigation bar
		window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
				WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
	}

	/**
	 * 对标题栏进行设置
	 */
	protected abstract void initTitleBar();

}
