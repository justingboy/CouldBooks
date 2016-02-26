package com.himoo.ydsc.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.himoo.ydsc.R;
import com.himoo.ydsc.adapter.BookQueryAdapter;
import com.himoo.ydsc.animation.BookView;
import com.himoo.ydsc.animation.BookView.OpenBookAnimEndListener;
import com.himoo.ydsc.bean.BaiduBookChapter;
import com.himoo.ydsc.bookdl.DownloadManager;
import com.himoo.ydsc.config.BookTheme;
import com.himoo.ydsc.config.SpConstant;
import com.himoo.ydsc.download.BookDownloadInfo;
import com.himoo.ydsc.download.BookDownloadManager;
import com.himoo.ydsc.download.BookDownloadService;
import com.himoo.ydsc.reader.ReaderActivity;
import com.himoo.ydsc.reader.dao.BookMark;
import com.himoo.ydsc.reader.dao.BookMarkDb;
import com.himoo.ydsc.reader.utils.BookMarkUtils;
import com.himoo.ydsc.reader.utils.IOHelper;
import com.himoo.ydsc.reader.utils.LocalReaderUtil;
import com.himoo.ydsc.ui.swipebacklayout.SwipeBackActivity;
import com.himoo.ydsc.ui.utils.Toast;
import com.himoo.ydsc.util.SharedPreferences;
import com.lidroid.xutils.view.annotation.ViewInject;

/**
 * 书架界面的搜索结果展示界面
 */
public class BookQueryActivity extends SwipeBackActivity {

	@ViewInject(R.id.query_book_list)
	private PullToRefreshListView mRefrshListView;
	private BookDownloadManager downloadManager;
	private int mCurrentClickPosition = -1;
	private BookQueryAdapter mAdapter;
	public BookView bookView;
	/** 打开动画的底部图片 */
	private Drawable readerBg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_book_query);
		downloadManager = BookDownloadService.getDownloadManager(this);
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
		mAdapter = new BookQueryAdapter(this, R.layout.adaapter_book_query,
				list);
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
				if (DownloadManager.getInstance().isExistTask(
						book.getBookName(), book.getBookId())) {
					Toast.showBg(BookQueryActivity.this,  "正在下载,请稍后打开");
					return;
				}
				BookView bookView = (BookView) view.findViewById(R.id.bookView);
				if (book.isAutoResume()) {
					new OpenBookAsyncTask(BookQueryActivity.this, position,
							book.getBookName(), book.getBookId(), book
									.getBookStatue(), book.getBookSourceType(),
							book.getLastUrl(), bookView).execute();

				} else {
					Toast.showShort(BookQueryActivity.this, "打开失败，请重新下载");
					mCurrentClickPosition = -1;
				}

			}
		});
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (null != bookView) {
			bookView.startCloseBookAnimation(null);
			bookView = null;
		}
		updateReadProgress();
		mCurrentClickPosition = -1;
	}

	/**
	 * 更新阅读进度
	 */
	private void updateReadProgress() {
		if (mCurrentClickPosition != -1) {
			BookDownloadInfo oldBook = mAdapter.getItem(mCurrentClickPosition);
			BookDownloadInfo newBook = downloadManager.querryByBookName(
					oldBook.getBookName(), oldBook.getBookId());
			mAdapter.set(oldBook, newBook);
			mAdapter.notifyDataSetChanged();
		}
	}

	/**
	 * 异步初始化本地章节
	 * 
	 */
	public class OpenBookAsyncTask extends AsyncTask<Void, Void, BookMark> {
		public int position;
		public Context mContext;
		public String bookName;
		public String bookId;
		// 表示书的状态是连载还是完结
		public String statue;
		public int bookType;
		public String lastUrl;
		ArrayList<BaiduBookChapter> list;

		public OpenBookAsyncTask(Context context, int position,
				String bookName, String bookId, String statue, int bookType,
				String lastUrl, BookView bookView) {
			this.mContext = context;
			this.position = position;
			this.bookName = bookName;
			this.bookId = bookId;
			this.statue = statue;
			this.bookType = bookType;
			this.lastUrl = lastUrl;
			BookQueryActivity.this.bookView = bookView;
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
			BookMarkUtils.getInstance().getBookMark(mContext, bookName, bookId);
			list = LocalReaderUtil.getInstance().parseLocalBook(bookName,
					bookId, bookType);
			if (list == null || list.isEmpty()) {
				return null;
			} else {
				IOHelper.getBook(mContext, bookName, bookId, list);
				BookMark bookMark = BookMarkDb.getInstance(mContext, "book")
						.querryReaderPos(bookName, bookId);
				return bookMark;
			}
		}

		@Override
		protected void onPostExecute(final BookMark result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			try {

				if (result == null && list == null || list.isEmpty()) {
					dismissRefreshDialog();
					mCurrentClickPosition = -1;
					Toast.showBg(BookQueryActivity.this, "该书为完成下载");
					return;
				}
				list.clear();
				list = null;
				dismissRefreshDialog();
				readerBg = mContext.getResources().getDrawable(
						BookTheme.READBOOK_BACKGROUND);

				if (isReaderNightMode()) {
					readerBg = null;
					readerBg = mContext.getResources().getDrawable(
							R.drawable.book_setting_night);
				} else if (SharedPreferences.getInstance().getBoolean(
						SpConstant.BOOK_AUTO_COLOR, false)) {
					int color = SharedPreferences.getInstance().getInt(
							SpConstant.BOOK_AUTO_COLOR_BG, Color.BLACK);
					readerBg = null;
					readerBg = new ColorDrawable(color);
				}
				bookView.startOpenBookAnimation(new OpenBookAnimEndListener() {

					@Override
					public void onOpenBookAnimEnd(BookView bookView) {
						// TODO Auto-generated method stub
						startToActivity(bookName, bookId, bookType, lastUrl,
								statue, result);
					}
				}, bookView.getParent(), readerBg, R.id.query_book_image);

			} catch (Exception e) {
				Log.e(e);
			}
		}
	}

	private void startToActivity(String bookName, String bookId, int bookType,
			String lastUrl, String statue, BookMark result) {

		Intent intent = new Intent(this, ReaderActivity.class);
		intent.putExtra("bookName", bookName);
		intent.putExtra("bookId", bookId);
		// 该Type表示从哪里跳转到阅读界面 */
		intent.putExtra("type", 2);
		intent.putExtra("bookType", bookType);
		intent.putExtra("lastUrl", lastUrl);
		intent.putExtra("statue", statue);
		intent.putExtra("index", "1");
		intent.putExtra("position", result == null ? 0 : result.getPosition());
		intent.putExtra("currentPage",
				result == null ? -1 : result.getCurrentPage());
		intent.putExtra("pageCount",
				result == null ? -1 : result.getPageCount());
		intent.putExtra("isNeedSave", true);
		startActivity(intent);
		overridePendingTransition(R.anim.fade_in, 0);
	}

	private boolean isReaderNightMode() {
		boolean isNightMode = SharedPreferences.getInstance().getBoolean(
				SpConstant.BOOK_SETTING_NIGHT_MODE, false);
		boolean isAutoNightMode = SharedPreferences.getInstance().getBoolean(
				SpConstant.BOOK_SETTING_AUTO_NIGHT_MODE_YES, false);
		if (isNightMode || isAutoNightMode) {
			return true;
		}
		return false;

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if (readerBg != null)
			readerBg = null;
		if (mAdapter != null)
			mAdapter.destory();
		super.onDestroy();
	}

}
