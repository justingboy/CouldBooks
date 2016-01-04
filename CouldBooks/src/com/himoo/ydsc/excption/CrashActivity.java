package com.himoo.ydsc.excption;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;

import com.himoo.ydsc.R;
import com.himoo.ydsc.config.SpConstant;
import com.himoo.ydsc.ui.swipebacklayout.SwipeBackActivity;
import com.himoo.ydsc.ui.utils.Toast;
import com.himoo.ydsc.util.AppUtils;
import com.himoo.ydsc.util.SendEmailUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

public class CrashActivity extends SwipeBackActivity implements OnClickListener {

	@ViewInject(R.id.layout_out)
	private RelativeLayout layout_out;

	@ViewInject(R.id.layout_send)
	private RelativeLayout layout_send;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_crash);
		initListener();
	}

	@Override
	protected void initTitleBar() {
		// TODO Auto-generated method stub
		mTitleBar.setTitle("错误报告");
		mTitleBar.setRightLogoGone();
		mTitleBar.setLeftDrawable(R.drawable.book_details_close);
	}

	private void initListener() {
		layout_out.setOnClickListener(this);
		layout_send.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.layout_out:
			finish();
			break;
		case R.id.layout_send:

			File file = new File(BookLog.INSTANCE_LOG_PATH);
			if (file.exists()) {
				File[] f = file.listFiles();
				if (f.length > 0) {
					ArrayList<File> listFile = new ArrayList<File>();
					for (int i = 0; i < f.length; i++) {
						listFile.add(f[i]);
					}
					Collections.sort(listFile, new Comparator<File>() {

						@Override
						public int compare(File lhs, File rhs) {
							// TODO Auto-generated method stub
							return ((Long) rhs.lastModified()).compareTo(lhs
									.lastModified());
						}
					});
					SendEmailUtils.getInstance().sendEmail(this, "程序错误报告",
							createErrorMessge(),
							listFile.get(0).getAbsolutePath(), true);
				} else {
					Toast.show(this, "无日志文件,无需发送！");
				}
			}

			break;

		default:
			break;
		}
	}

	/**
	 * 拼接错误信息
	 * 
	 * @return
	 */
	private String createErrorMessge() {
		StringBuilder sb = new StringBuilder();
		sb.append("设备：" + android.os.Build.MODEL + "|\n")
				.append("Android系统 ：" + android.os.Build.VERSION.RELEASE
						+ "|\n")
				.append("应用名字：" + AppUtils.getAppName(this) + "|\n")
				.append("版本号：" + AppUtils.getVersionName(this) + "|\n")
				.append("Build :" + SpConstant.BUILD_CODE + "|\n")
				.append("错误描述：" + "\n").append("错误请查看附件" + "\n");

		return sb.toString();
	}
}
