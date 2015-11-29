package com.himoo.ydsc.activity;

import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.himoo.ydsc.R;
import com.himoo.ydsc.adapter.BookQueryAdapter;
import com.himoo.ydsc.download.BookDownloadInfo;
import com.himoo.ydsc.download.BookDownloadManager;
import com.himoo.ydsc.download.BookDownloadService;
import com.himoo.ydsc.ui.swipebacklayout.SwipeBackActivity;
import com.lidroid.xutils.view.annotation.ViewInject;
/**
 * 书架界面的搜索结果展示界面
 */
public class BookQueryActivity extends SwipeBackActivity {

	@ViewInject(R.id.query_book_list)
	private PullToRefreshListView mRefrshListView;

	private int mCurrentClickPosition = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_book_query);
		initPullToRefrehListView();
		initData();
	}

	@Override
	protected void initTitleBar() {
		mTitleBar.setTitle("检索结果");
		mTitleBar.setLeftTitle("返回");
		mTitleBar.setRightLogoGone();
	}

	public void initData() {
		String keyword = getIntent().getStringExtra("key");
		BookDownloadManager downloadManager = BookDownloadService
				.getDownloadManager(this);
		List<BookDownloadInfo> list = downloadManager.queryByKeyword(keyword);
		BookQueryAdapter mAdapter = new BookQueryAdapter(this,
				R.layout.adaapter_book_query, list);
		mRefrshListView.setAdapter(mAdapter);

	}

	/**
	 * 初始化ListView
	 */
	private void initPullToRefrehListView() {
		mRefrshListView.setMode(Mode.DISABLED);
		mRefrshListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				if (mCurrentClickPosition != -1)
					return;
				mCurrentClickPosition = position;

			}
		});
	}
	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mCurrentClickPosition = -1;
	}

}
