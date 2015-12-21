package com.himoo.ydsc.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.himoo.ydsc.R;
import com.himoo.ydsc.adapter.BookQueryAdapter;
import com.himoo.ydsc.bean.BaiduBookChapter;
import com.himoo.ydsc.download.BookDownloadInfo;
import com.himoo.ydsc.download.BookDownloadManager;
import com.himoo.ydsc.download.BookDownloadService;
import com.himoo.ydsc.reader.ReaderActivity;
import com.himoo.ydsc.reader.dao.BookMark;
import com.himoo.ydsc.reader.dao.BookMarkDb;
import com.himoo.ydsc.reader.utils.IOHelper;
import com.himoo.ydsc.reader.utils.LocalReaderUtil;
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
				BookDownloadInfo book = (BookDownloadInfo) parent
						.getItemAtPosition(position);
				new OpenBookAsyncTask(BookQueryActivity.this, book
						.getBookName(), book.getBookStatue(), book
						.getBookSourceType()).execute();

			}
		});
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mCurrentClickPosition = -1;
	}

	/**
	 * 异步初始化本地章节
	 * 
	 */
	public class OpenBookAsyncTask extends AsyncTask<Void, Void, BookMark> {
		public Context mContext;
		public String bookName;
		// 表示书的状态是连载还是完结
		public String statue;
		public int bookType;

		public OpenBookAsyncTask(Context context, String bookName,
				String statue, int bookType) {
			this.mContext = context;
			this.bookName = bookName;
			this.statue = statue;
			this.bookType = bookType;
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			showRefreshDialog("正在打开书籍");

		}

		@Override
		protected BookMark doInBackground(Void... params) {
			// TODO Auto-generated method stub
			ArrayList<BaiduBookChapter> list = LocalReaderUtil.getInstance()
					.parseLocalBook(bookName, bookType);
			IOHelper.getBook(mContext, bookName, list);
			BookMark bookMark = BookMarkDb.getInstance(mContext, "book")
					.querryReaderPos(bookName);

			return bookMark;
		}

		@Override
		protected void onPostExecute(BookMark result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			dismissRefreshDialog();
			Intent intent = new Intent(mContext, ReaderActivity.class);
			intent.putExtra("bookName", bookName);
			// 该Type表示从哪里跳转到阅读界面 */
			intent.putExtra("type", 2);
			intent.putExtra("bookType", bookType);
			intent.putExtra("statue", statue);
			intent.putExtra("index", "1");
			intent.putExtra("position",
					result == null ? 0 : result.getPosition());
			intent.putExtra("currentPage",
					result == null ? -1 : result.getCurrentPage());
			intent.putExtra("pageCount",
					result == null ? -1 : result.getPageCount());
			intent.putExtra("isNeedSave", true);
			startActivity(intent);
		}

	}

}
