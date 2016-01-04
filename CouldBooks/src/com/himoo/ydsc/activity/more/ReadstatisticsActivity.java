package com.himoo.ydsc.activity.more;

import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.TrafficStats;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.himoo.ydsc.R;
import com.himoo.ydsc.config.SpConstant;
import com.himoo.ydsc.download.BookDownloadManager;
import com.himoo.ydsc.download.BookDownloadService;
import com.himoo.ydsc.ui.swipebacklayout.SwipeBackActivity;
import com.himoo.ydsc.ui.utils.ViewSelector;
import com.himoo.ydsc.util.SharedPreferences;
import com.himoo.ydsc.util.TimestampUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

/**
 * 阅读统计类
 * 
 */
public class ReadstatisticsActivity extends SwipeBackActivity implements
		OnClickListener {

	@ViewInject(R.id.read_time)
	private TextView read_time;

	@ViewInject(R.id.read_book_count)
	private TextView read_book_count;

	@ViewInject(R.id.read_download_count)
	private TextView read_download_count;

	@ViewInject(R.id.read_flow)
	private TextView read_flow;

	@ViewInject(R.id.read_clear_btn)
	private Button read_clear_btn;

	private BookDownloadManager downloadManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_more_staticis);
		initData();

	}

	@Override
	protected void initTitleBar() {
		// TODO Auto-generated method stub
		mTitleBar.setLeftTitle(getResources().getString(R.string.main_more));
		mTitleBar.setTitle(getResources().getString(
				R.string.more_read_statistics));
		mTitleBar.setRightLogoGone();
	}

	private void initData() {
		downloadManager = BookDownloadService.getDownloadManager(this);
		long readerTime = SharedPreferences.getInstance().getLong(
				SpConstant.BOOK_READER_TIME, 0l);
		read_time
				.setText("共阅读" + TimestampUtils.formatTimeDuration(readerTime));
		read_book_count.setText("共看过" + downloadManager.getReaderBookCount()
				+ "本书");
		int bookCount = downloadManager.getDownloadInfoListCount();
		read_download_count.setText("共下载过" + bookCount + "本书");
		// 2G/3G 流量，上传+下载的总和
		read_flow.setText("上网流量 ：" + getMobleTraffic());
		ViewSelector.setButtonSelector(this, read_clear_btn);
		read_clear_btn.setOnClickListener(this);
	}

	/**
	 * 获取应用使用的流量
	 * 
	 * @return
	 */
	private String getMobleTraffic() {
		long mobleTraffic = 0L;
		int uid = getAppUid();
		if (uid == -1) {
			return "0.00M";
		} else {
			mobleTraffic = (TrafficStats.getUidRxBytes(uid) == TrafficStats.UNSUPPORTED) ? 0
					: TrafficStats.getUidRxBytes(uid);
			mobleTraffic += (TrafficStats.getUidTxBytes(uid) == TrafficStats.UNSUPPORTED) ? 0
					: TrafficStats.getUidTxBytes(uid);
			String traffic = mobleTraffic / (float) (1024 * 1024) + "M";
			return traffic.substring(0, traffic.indexOf(".") + 3) + "M";
		}

	}

	/**
	 * 获取UID
	 * 
	 * @return
	 */
	private int getAppUid() {
		PackageManager pm = getPackageManager();
		try {
			int appUid = pm.getApplicationInfo(getPackageName(), 0).uid;
			return appUid;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			return -1;
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		SharedPreferences.getInstance().putLong(SpConstant.BOOK_READER_TIME, 0L);
		read_time.setText("共阅读0分钟");
	}
}
