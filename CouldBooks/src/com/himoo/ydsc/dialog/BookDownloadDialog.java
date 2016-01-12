package com.himoo.ydsc.dialog;

import java.text.NumberFormat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.Gravity;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.himoo.ydsc.R;
import com.himoo.ydsc.util.DeviceUtil;

public class BookDownloadDialog extends AlertDialog {

	private ProgressBar mProgress;
	private TextView mProgressNumber;
	private TextView mProgressPercent;
	private TextView mProgressMessage;

	private Handler mViewUpdateHandler;
	private int mMax;
	private CharSequence mMessage;
	private boolean mHasStarted;
	private int mProgressVal;

	private String mProgressNumberFormat;
	private NumberFormat mProgressPercentFormat;
	private Context mContext;
	private int resId = -1;
	private boolean isFullWidth = false;

	public BookDownloadDialog(Context context) {
		super(context, R.style.book_download_dialog);
		this.mContext = context;
		initFormats();
	}
	public BookDownloadDialog(Context context, boolean isFullWidth) {
		super(context, R.style.book_download_dialog_afresh);
		this.mContext = context;
		this.isFullWidth = isFullWidth;
		initFormats();
	}

	public BookDownloadDialog(Context context, int resId) {
		super(context, R.style.book_download_dialog);
		this.mContext = context;
		// TODO Auto-generated constructor stub
		initFormats();
		this.resId = resId;
	}
	public BookDownloadDialog(Context context, int resId,boolean isFullWidth) {
		super(context, R.style.book_download_dialog_afresh);
		this.mContext = context;
		this.isFullWidth = isFullWidth;
		initFormats();
		this.resId = resId;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		WindowManager.LayoutParams wmlp = this.getWindow().getAttributes();
		if (isFullWidth) {
			// 设置铺满宽度
			wmlp.width = (int) (DeviceUtil.getWidth((Activity) mContext));
			this.getWindow().setAttributes(wmlp);
			getWindow().setGravity(Gravity.BOTTOM);
		} else {
			wmlp.gravity = Gravity.CENTER;
			wmlp.x = 0; // x position
			wmlp.y = DeviceUtil.dip2px(getContext(), 80); // y position
			getWindow().setAttributes(wmlp);
		}
		setContentView(R.layout.download_progress_dialog);
		if (isFullWidth) {
			RelativeLayout dialog_downbook = (RelativeLayout) findViewById(R.id.dialog_downbook);
			LayoutParams params = dialog_downbook.getLayoutParams();
			params.width = LayoutParams.MATCH_PARENT;
			dialog_downbook.setLayoutParams(params);
		}
		mProgress = (ProgressBar) findViewById(R.id.progress);
		mProgressNumber = (TextView) findViewById(R.id.progress_number);
		mProgressPercent = (TextView) findViewById(R.id.progress_percent);
		mProgressMessage = (TextView) findViewById(R.id.progress_message);
		// LayoutInflater inflater = LayoutInflater.from(getContext());
		setCancelable(false);
		if (resId != -1)
			setTitleLeftDrawable(resId);
		mViewUpdateHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				int progress = mProgress.getProgress();
				int max = mProgress.getMax();
				double dProgress = (double) progress / (double) (1024 * 1024);
				double dMax = (double) max / (double) (1024 * 1024);
				if (mProgressNumberFormat != null) {
					String format = mProgressNumberFormat;
					mProgressNumber.setText(String.format(format, dProgress,
							dMax));
				} else {
					mProgressNumber.setText("");
				}
				if (mProgressPercentFormat != null) {
					double percent = (double) progress / (double) max;
					SpannableString tmp = new SpannableString(
							mProgressPercentFormat.format(percent));
					tmp.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),
							0, tmp.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					mProgressPercent.setText(tmp);
				} else {
					mProgressPercent.setText("");
				}
			}

		};
		// View view = inflater.inflate(R.layout.common_progress_dialog, null);
		// mProgress = (ProgressBar) view.findViewById(R.id.progress);
		// mProgressNumber = (TextView) view.findViewById(R.id.progress_number);
		// mProgressPercent = (TextView)
		// view.findViewById(R.id.progress_percent);
		// setView(view);
		// mProgress.setMax(100);
		onProgressChanged();
		if (mMessage != null) {
			setMessage(mMessage);
		}
		if (mMax > 0) {
			setMax(mMax);
		}
		if (mProgressVal > 0) {
			setProgress(mProgressVal);
		}
	}

	private void initFormats() {
		mProgressNumberFormat = "%1.2fM/%2.2fM";
		mProgressPercentFormat = NumberFormat.getPercentInstance();
		mProgressPercentFormat.setMaximumFractionDigits(0);
	}

	private void onProgressChanged() {
		mViewUpdateHandler.sendEmptyMessage(0);

	}

	public void setProgressStyle(int style) {
		// mProgressStyle = style;
	}

	public int getMax() {
		if (mProgress != null) {
			return mProgress.getMax();
		}
		return mMax;
	}

	public void setMax(int max) {
		if (mProgress != null) {
			mProgress.setMax(max);
			onProgressChanged();
		} else {
			mMax = max;
		}
	}

	public void setIndeterminate(boolean indeterminate) {
		if (mProgress != null) {
			mProgress.setIndeterminate(indeterminate);
		}
		// else {
		// mIndeterminate = indeterminate;
		// }
	}

	public void setProgress(int value) {
		if (mHasStarted) {
			mProgress.setProgress(value);
			onProgressChanged();
		} else {
			mProgressVal = value;
		}
	}

	@Override
	public void setMessage(CharSequence message) {
		// TODO Auto-generated method stub
		// super.setMessage(message);
		if (mProgressMessage != null) {
			mProgressMessage.setText(message);
		} else {
			mMessage = message;
		}
	}

	/**
	 * 设置标题左边的图标
	 * 
	 * @param resId
	 */
	public void setTitleLeftDrawable(int resId) {
		if (mProgressMessage != null) {
			Drawable drawable = mContext.getResources().getDrawable(resId);
			mProgressMessage.setCompoundDrawablesWithIntrinsicBounds(drawable,
					null, null, null);
		}
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		mHasStarted = true;
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		mHasStarted = false;
	}

}
