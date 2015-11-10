package com.himoo.ydsc.activity.more;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.himoo.ydsc.R;
import com.himoo.ydsc.ui.swipebacklayout.SwipeBackActivity;
import com.himoo.ydsc.ui.utils.ViewSelector;
import com.lidroid.xutils.view.annotation.ViewInject;

public class ReadstatisticsActivity extends SwipeBackActivity {

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

		read_time.setText("共阅读0小时10分钟40秒");
		read_book_count.setText("共看过11本书");
		read_download_count.setText("共下载过4本书");
		read_flow.setText("3G使用流量 ：0B");
		ViewSelector.setButtonSelector(this, read_clear_btn);

	}

}
