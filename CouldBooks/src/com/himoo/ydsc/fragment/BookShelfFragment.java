package com.himoo.ydsc.fragment;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.himoo.ydsc.R;
import com.himoo.ydsc.activity.BookQueryActivity;
import com.himoo.ydsc.adapter.BookDownloadAdapter;
import com.himoo.ydsc.animation.AnimationUtils;
import com.himoo.ydsc.animation.BookView;
import com.himoo.ydsc.animation.BookView.OpenBookAnimEndListener;
import com.himoo.ydsc.base.BaseFragment;
import com.himoo.ydsc.bean.BaiduBookChapter;
import com.himoo.ydsc.bookdl.DownloadManager;
import com.himoo.ydsc.config.BookTheme;
import com.himoo.ydsc.config.SpConstant;
import com.himoo.ydsc.dialog.BookDeletePopupWindow;
import com.himoo.ydsc.dialog.BookDeletePopupWindow.OnPopupClickListener;
import com.himoo.ydsc.download.BookDownloadInfo;
import com.himoo.ydsc.download.BookDownloadManager;
import com.himoo.ydsc.download.BookDownloadService;
import com.himoo.ydsc.http.AfreshDownMeBookTask;
import com.himoo.ydsc.http.AfreshDownloadTask;
import com.himoo.ydsc.http.OnAfreshDownloadListener;
import com.himoo.ydsc.manager.PageManager;
import com.himoo.ydsc.reader.ReaderActivity;
import com.himoo.ydsc.reader.dao.BookMark;
import com.himoo.ydsc.reader.dao.BookMarkDb;
import com.himoo.ydsc.reader.utils.BookMarkUtils;
import com.himoo.ydsc.reader.utils.IOHelper;
import com.himoo.ydsc.reader.utils.LocalReaderUtil;
import com.himoo.ydsc.share.UmengShare;
import com.himoo.ydsc.ui.utils.Toast;
import com.himoo.ydsc.ui.utils.UIHelper;
import com.himoo.ydsc.ui.utils.ViewSelector;
import com.himoo.ydsc.ui.view.BookTitleBar;
import com.himoo.ydsc.update.BookUpdateTask;
import com.himoo.ydsc.update.BookUpdateTask.OnNewChapterUpdateListener;
import com.himoo.ydsc.update.LastChapter;
import com.himoo.ydsc.util.BookSortUtil;
import com.himoo.ydsc.util.FileUtils;
import com.himoo.ydsc.util.NetWorkUtils;
import com.himoo.ydsc.util.SP;
import com.himoo.ydsc.util.SharedPreferences;
import com.ios.dialog.ActionSheetDialog;
import com.ios.dialog.AlertDialog;
import com.ios.dialog.ActionSheetDialog.OnSheetItemClickListener;
import com.ios.edittext.SearchEditText;
import com.ios.edittext.SearchEditText.OnEditTextFocuseChangListener;
import com.ios.edittext.SearchEditText.OnSearchClickListener;
import com.ios.radiogroup.SegmentedGroup;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 用于展示下载及阅读过的书
 * 
 */
public class BookShelfFragment extends BaseFragment implements
		OnPopupClickListener, OnSearchClickListener,
		OnEditTextFocuseChangListener, OnCheckedChangeListener,
		OnNewChapterUpdateListener, OnItemClickListener,
		OnAfreshDownloadListener, OnScrollListener {

	private HashMap<String, String> haskMap = new HashMap<String, String>();
	/**  */
	private boolean isScrollStateFling = false;
	/** 标题栏 */
	private BookTitleBar titleBar;
	/** 判断是否处于删除状态 */
	private boolean isDelectStute = false;
	private Button bookSort;
	/*** 在返回书架界面是否需要刷新adapter */
	private boolean isNeedRefreshAdapter = false;
	private int mCurrentScrollPosition;
	private PullToRefreshGridView mPullToGridView;
	private static GridView mGridView;
	private static BookDownloadManager downloadManager;
	private static BookDownloadAdapter mAdapter;

	private static ArrayList<BookDownloadInfo> mDownloadList;
	private static ImageView shelf_empty_image;

	private SearchEditText searchText;
	private BookDeletePopupWindow popupWindow;
	private RadioButton rb_horizontal;
	private RadioButton rb_vertical;
	private SegmentedGroup segment_bookshelf;
	/** 更新 */
	private ImageView imgRefresh;
	/** 打开动画的底部图片 */
	private Drawable readerBg;

	/** 是否可以搜索了 */
	private boolean isKeyEnterDown = false;

	/** 要删除的书籍集合list */
	private List<BookDownloadInfo> deletedList = new ArrayList<BookDownloadInfo>();
	/** 记录默认排序的List集合 */
	private static List<BookDownloadInfo> mDefaultSortList = new ArrayList<BookDownloadInfo>();
	/** 记录打开书的位置 */
	private int mCurrentClickPosition = -1;
	private BookDownloadInfo book;
	public BookView bookView;
	/** 　是否正在下载，下载中不可点击 */
	public static boolean isDownloading = false;
	/** 是否正在刷新中，刷新中不可点击　 */
	private boolean isRefresh = false;
	/** 需要下载的书籍的个数，默认是1　 */
	private int needDownBookCount = 1;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			SharedPreferences sp, Bundle savedInstanceState,
			PageManager pageManager) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_bookshelf, null);
		initTitleBar(view);
		initData();
		initPullToGrdView(view);
		return view;
	}

	@Override
	public void initData() {
		SharedPreferences.getInstance().putBoolean(
				SpConstant.BOOK_SHELF_DIRECTION, true);
		downloadManager = BookDownloadService.getDownloadManager(getActivity());
		mDownloadList = downloadManager.getAllBookDownloadInfo();
		mDefaultSortList.clear();
		mDefaultSortList.addAll(mDownloadList);

	}

	/**
	 * 得到没有下载的书籍
	 */
	private ArrayList<BookDownloadInfo> getHasNoDownloadBook() {
		ArrayList<BookDownloadInfo> list = new ArrayList<BookDownloadInfo>();
		if (mDownloadList != null) {
			for (BookDownloadInfo bookDownloadInfo : mDownloadList) {
				if (!bookDownloadInfo.isAutoResume())
					list.add(bookDownloadInfo);
			}
		}
		return list;
	}

	/**
	 * 初始化TitleBar
	 * 
	 * @param view
	 */
	private void initTitleBar(View view) {
		bookSort = (Button) view.findViewById(R.id.btn_book_name_sort);
		ViewSelector.setButtonStrokeSelector(getActivity(), bookSort);
		bookSort.setTextColor(BookTheme.THEME_COLOR);
		titleBar = (BookTitleBar) view.findViewById(R.id.book_titleBar);
		titleBar.setBackgroundColor(BookTheme.THEME_COLOR);
		titleBar.setLeftDrawable(R.drawable.book_deleted);
		titleBar.setLeftTitle("  ");
		View layout = View.inflate(getActivity(),
				R.layout.titlebar_custom_view, null);
		segment_bookshelf = (SegmentedGroup) layout
				.findViewById(R.id.segment_bookshelf);
		rb_horizontal = (RadioButton) layout
				.findViewById(R.id.book_sort_horizontal);
		rb_vertical = (RadioButton) layout.findViewById(R.id.book_sort_vertial);
		setRadioButtonDrawableSelector();
		segment_bookshelf.setOnCheckedChangeListener(this);
		titleBar.setTitleView(layout);
		searchText = (SearchEditText) view.findViewById(R.id.book_shelf_search);
		searchText.setTextColor(BookTheme.THEME_COLOR);
		searchText.setOnSearchClickListener(this);
		searchText.setOnFocuesChangeListener(this);
		searchText.setIconLeft(true);
		titleBar.getLeftTextView().setOnClickListener(this);
		bookSort.setOnClickListener(this);
		imgRefresh = titleBar.getRightLogo();
		imgRefresh.setOnClickListener(this);

	}

	/**
	 * 初始化GridView
	 * 
	 * @param view
	 */
	private void initPullToGrdView(View view) {
		mPullToGridView = (PullToRefreshGridView) view
				.findViewById(R.id.shelf_book_gridview);
		shelf_empty_image = (ImageView) view
				.findViewById(R.id.shelf_empty_image);
		mPullToGridView.setMode(Mode.DISABLED);
		if (mDownloadList == null || mDownloadList.isEmpty()) {
			shelf_empty_image.setVisibility(View.VISIBLE);
		} else {
			shelf_empty_image.setVisibility(View.GONE);
		}
		mGridView = mPullToGridView.getRefreshableView();
		mGridView.setOnScrollListener(this);
		mGridView.setOnItemClickListener(this);
		mGridView.setOnItemLongClickListener(this);
		mAdapter = new BookDownloadAdapter(getActivity(),
				R.layout.adapter_bookshelf_item, mDownloadList);
		sortBookList(mDownloadList);
		mGridView.setAdapter(mAdapter);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		switch (v.getId()) {
		case R.id.titlebar_left_title:
			if (deletedList != null)
				deletedList.clear();

			if (!isDelectStute) {
				if (mAdapter != null) {
					mAdapter.isChoice = false;
					mAdapter.isSelectedState = true;
					mAdapter.notifyDataSetChanged();
				}

				titleBar.setLeftDrawable(R.drawable.book_deleted_press);
				isDelectStute = true;
				if (popupWindow == null) {
					View popupView = View.inflate(getActivity(),
							R.layout.dialog_book_deleted, null);
					popupWindow = new BookDeletePopupWindow(
							BookShelfFragment.this, popupView);
					popupWindow.setOnPopupClickListener(this);
				}
				popupWindow.showAtLocation(titleBar, Gravity.BOTTOM, 0, 0);

			} else {

				if (mAdapter != null) {
					mAdapter.isChoice = false;
					mAdapter.isSelectedState = false;
					mAdapter.notifyDataSetChanged();
				}
				titleBar.setLeftDrawable(R.drawable.book_deleted);
				isDelectStute = false;
				if (popupWindow != null)
					popupWindow.dismiss();

			}
			break;

		case R.id.titlebar_refresh:
			if (isRefresh)
				return;
			if (NetWorkUtils.isNetConnected(getActivity())) {
				if (!DownloadManager.getInstance().isExistTask()) {
					ArrayList<BookDownloadInfo> noDownloadBook = getHasNoDownloadBook();
					if (noDownloadBook.size() > 0) {
						downloadBook();
					} else {
						AfreshBookChapter();
					}
				} else {
					Toast.show(getActivity(), "下载中请稍后更新");
				}
			} else {
				Toast.showError(getActivity(), "未连接网络");
			}
			break;

		case R.id.btn_book_name_sort:
			showBookSortDialog();
			break;

		default:
			break;
		}

	}

	/**
	 * 下载内置的书
	 */
	private void downloadBook() {
		if (!NetWorkUtils.isWifiConnected(getActivity())) {
			new AlertDialog(getActivity()).builder().setTitle("提醒")
					.setMsg("当前为网络数据流量，下载需要消耗本地流量，是否继续下载？")
					.setNegativeButton("取消", new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							AfreshBookChapter();
						}
					}).setPositiveButton("确定", new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							ArrayList<BookDownloadInfo> noDownloadBook = getHasNoDownloadBook();
							needDownBookCount = noDownloadBook.size();
							for (int i = 0; i < noDownloadBook.size(); i++) {
								BookDownloadInfo book = noDownloadBook.get(i);
								AfreshDownLoadBook(book);
							}
						}
					}).show();
		} else {
			new AlertDialog(getActivity()).builder().setTitle("提醒")
					.setMsg("当前网络为wifi状态，本地有书未下载，是否下载？")
					.setNegativeButton("取消", new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							AfreshBookChapter();
						}
					}).setPositiveButton("确定", new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							ArrayList<BookDownloadInfo> noDownloadBook = getHasNoDownloadBook();
							needDownBookCount = noDownloadBook.size();
							for (int i = 0; i < noDownloadBook.size(); i++) {
								BookDownloadInfo book = noDownloadBook.get(i);
								Log.i(book.getBookName());
								AfreshDownLoadBook(book);
							}
						}
					}).show();
		}
	}

	/**
	 * 更新最新章节
	 */
	private void AfreshBookChapter() {
		AnimationUtils.setViewRotating(getActivity(), imgRefresh);
		isRefresh = true;
		BookUpdateTask task = new BookUpdateTask(getActivity(), null, null, 2);
		task.setOnNewChapterListener(BookShelfFragment.this);
		task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	/**
	 * 重新下載
	 * 
	 * @param book
	 */
	private void AfreshDownLoadBook(BookDownloadInfo book) {
		if (book.getBookSourceType() == 1) {
			AfreshDownMeBookTask task = new AfreshDownMeBookTask(getActivity(),
					book.getBookName(), book.getBookId(),
					book.getDownloadUrl(),
					(book.isAutoResume() ? false : true),
					BookShelfFragment.this);
			task.execute();

		} else if (book.getBookSourceType() == 2) {
			DownloadManager.getInstance().deleteTask(book.getBookName(),
					book.getBookId());
			// 重新下载这本书,书是百度服务器上的
			AfreshDownloadTask task = new AfreshDownloadTask(getActivity(),
					book.getDownloadUrl(), book.getBookName(),
					book.getBookId(), BookShelfFragment.this);
			task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		}
	}

	/**
	 * 书籍展示的排序
	 */
	private void showBookSortDialog() {
		new ActionSheetDialog(getActivity())
				.builder()
				.setTitle("书架排列顺序")
				.setCancelable(false)
				.setCanceledOnTouchOutside(false)
				.setTitleCancelColor(BookTheme.THEME_COLOR)
				.setDefaultBackground(true)
				.setCurrentItemIndex(
						SharedPreferences.getInstance().getInt("book_sort", 1))
				.addSheetItem("默认排序", BookTheme.THEME_COLOR, sheetListener)
				.addSheetItem("更新时间", BookTheme.THEME_COLOR, sheetListener)
				.addSheetItem("书名排序", BookTheme.THEME_COLOR, sheetListener)
				.addSheetItem("作者排序", BookTheme.THEME_COLOR, sheetListener)
				.show();
	}

	private OnSheetItemClickListener sheetListener = new ActionSheetDialog.OnSheetItemClickListener() {

		@Override
		public void onClick(TextView v, int which) {
			// TODO Auto-generated method stub
			SharedPreferences.getInstance().putInt("book_sort", which);
			mAdapter.clear();
			switch (which) {
			case 1:
				bookSort.setText("默认排序");
				mAdapter.addAll(mDownloadList);
				break;
			case 2:
				bookSort.setText("更新时间");
				mAdapter.addAll(BookSortUtil.sortByBookUpdate(mDownloadList));
				break;
			case 3:
				bookSort.setText("书名排序");
				mAdapter.addAll(BookSortUtil.sortByBookName(mDownloadList));
				break;
			case 4:
				bookSort.setText("作者排序");
				mAdapter.addAll(BookSortUtil.sortByBookAuthor(mDownloadList));
				break;

			default:
				break;
			}
			mAdapter.notifyDataSetChanged();

		}

	};

	/**
	 * 内部类,用于接受下载过来的通知
	 * 
	 */
	public static class BookDownloadReceiver extends BroadcastReceiver {

		@Override
		public synchronized void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			boolean downloadSuccess = intent.getBooleanExtra("success", false);
			String bookName = intent.getStringExtra("bookName");
			String bookId = intent.getStringExtra("bookId");
			if (downloadSuccess) {
				DownloadManager.getInstance().deleteTask(bookName, bookId);
				SP.getInstance().putBookDownSuccess(bookName + bookId, false);
				updateBookAuResume(bookName, bookId);
				mAdapter.notifyDataSetChanged();
			} else {
				// 保存下载状态，false表示正在下载
				SP.getInstance().putBookDownSuccess(bookName + bookId, false);
				String dowloadUrl = intent.getStringExtra("dowloadUrl");
				List<BookDownloadInfo> list = downloadManager.findLastBookInfo(
						bookName, bookId, dowloadUrl);
				if (list != null && list.size() > 0) {
					if (shelf_empty_image != null)
						shelf_empty_image.setVisibility(View.GONE);
					mAdapter.addFirstN(list.get(0));
					mDownloadList.add(mDownloadList.size() == 0 ? 0
							: mDownloadList.size(), list.get(0));
					mGridView.setSelection(0);
					mAdapter.notifyDataSetChanged();
				}
			}
		}
	}

	@Override
	public void onPopupClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.btn_allSelected:
			deletedList.clear();
			if (mAdapter != null) {
				mAdapter.isChoice = true;
				mAdapter.notifyDataSetChanged();
				deletedList.addAll(mDownloadList);
			}

			break;
		case R.id.btn_allNotSelected:
			if (!deletedList.isEmpty()) {
				if (mAdapter != null) {
					mAdapter.isChoice = false;
					deletedList.removeAll(mDownloadList);
					mAdapter.notifyDataSetChanged();
				}
			}

			break;
		case R.id.btn_deleted:

			if (deletedList.isEmpty()) {
				Toast.showShort(getActivity(), "您还没有选择要删除的书籍！！");
				return;
			}

			downloadManager.deletedBookList(deletedList);
			for (int i = 0; i < deletedList.size(); i++) {
				String bookName = deletedList.get(i).getBookName();
				String bookId = deletedList.get(i).getBookId();
				if (haskMap.containsKey(bookId)) {
					haskMap.remove(bookId);
					AnimationUtils.cancelAnim(imgRefresh);
				}
				BookMarkDb.getInstance(getActivity(), "book").deletBookMark(
						bookName, bookId);

			}
			if (popupWindow != null)
				popupWindow.dismiss();
			titleBar.setLeftDrawable(R.drawable.book_deleted);
			isDelectStute = false;
			if (mAdapter != null) {
				mAdapter.isSelectedState = false;
				mDownloadList.removeAll(deletedList);
				mAdapter.remove(deletedList);
			}
			new DeleteBookTask(null, null, 1, true).execute();

			break;
		case R.id.btn_cancel:
			if (popupWindow != null)
				popupWindow.dismiss();
			titleBar.setLeftDrawable(R.drawable.book_deleted);
			isDelectStute = false;
			if (mAdapter != null) {
				mAdapter.isSelectedState = false;
				mAdapter.isChoice = false;
				deletedList.removeAll(mDownloadList);
				mAdapter.notifyDataSetChanged();
			}
			if (deletedList != null && !deletedList.isEmpty())
				deletedList.clear();
			break;

		default:
			break;
		}

	}

	/**
	 * 加载设置好的排序列表
	 * 
	 * @param list
	 */
	private void sortBookList(List<BookDownloadInfo> list) {
		int which = SharedPreferences.getInstance().getInt("book_sort", 1);
		if (mAdapter != null)
			mAdapter.clear();
		switch (which) {
		case 1:
			bookSort.setText("默认排序");
			mAdapter.addAll(list);
			break;
		case 2:
			bookSort.setText("更新时间");
			mAdapter.addAll(BookSortUtil.sortByBookUpdate(list));
			break;
		case 3:
			bookSort.setText("书名排序");
			mAdapter.addAll(BookSortUtil.sortByBookName(list));
			break;
		case 4:
			bookSort.setText("作者排序");
			mAdapter.addAll(BookSortUtil.sortByBookAuthor(list));
			break;

		default:
			break;
		}
	}

	@Override
	public void onSearchClick(View view) {
		// TODO Auto-generated method stub
		if (isKeyEnterDown) {
			if (TextUtils.isEmpty(searchText.getText())) {
				Toast.showLong(getActivity(), "请输入要查找的书名或作者");
				isKeyEnterDown = false;
				return;
			}
			List<BookDownloadInfo> resultList = downloadManager
					.queryByKeyword(searchText.getText().toString());
			if (resultList != null && !resultList.isEmpty()) {
				UIHelper.startToActivity(getActivity(),
						BookQueryActivity.class, searchText.getText()
								.toString());

			} else {
				Toast.showShort(getActivity(), "暂无查询到结果");
			}
			isKeyEnterDown = false;
		}
	}

	@Override
	public void onFocuseChange() {
		// TODO Auto-generated method stub
		isKeyEnterDown = true;
	}

	/**
	 * 书籍展示的排序
	 */
	private void showBookShareDialog(BookDownloadInfo book) {
		new ActionSheetDialog(getActivity())
				.builder()
				.setTitle(
						"《" + book.getBookName() + "》\n" + "使用\"重新下载\"可以修复出错书籍")
				.setCancelable(true)
				.setCanceledOnTouchOutside(true)
				.setTitleCancelColor(BookTheme.THEME_COLOR)
				.setDefaultBackground(true)
				.addSheetItem("重新下载", BookTheme.BOOK_RED,
						new ShareSheetItemClickListener(book))
				.addSheetItem("书籍分享", BookTheme.THEME_COLOR,
						new ShareSheetItemClickListener(book))
				.addSheetItem("删除本书", BookTheme.THEME_COLOR,
						new ShareSheetItemClickListener(book)).show();
	}

	public class ShareSheetItemClickListener implements
			OnSheetItemClickListener {
		BookDownloadInfo book;

		public ShareSheetItemClickListener(BookDownloadInfo book) {
			this.book = book;
		}

		@Override
		public void onClick(TextView v, int which) {
			// TODO Auto-generated method stub
			switch (which) {
			case 1:
				if (NetWorkUtils.isNetConnected(getActivity())) {
					AfreshDownLoadBook(book);
				} else {
					Toast.showError(getActivity(), "未连接网络");
				}

				break;
			case 2:
				// 注册友盟分享
				UmengShare.getInstance().setShareContent(getActivity(),
						book.getBookName(), book.getBookCoverImageUrl(),
						book.getBookName(), book.getDownloadUrl());
				UmengShare.getInstance().addCustomPlatforms(getActivity());
				break;
			case 3:
				DownloadManager.getInstance().deleteTask(book.getBookName(),
						book.getBookId());
				new DeleteBookTask(book, book.getBookName(),
						book.getBookSourceType(), false).execute();

				break;

			default:
				break;
			}
		}

	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (BookTheme.isThemeChange) {
			setRadioButtonDrawableSelector();
			titleBar.setBackgroundColor(BookTheme.THEME_COLOR);
			ViewSelector.setButtonStrokeSelector(getActivity(), bookSort);
			bookSort.setTextColor(BookTheme.THEME_COLOR);
			searchText.setTextColor(BookTheme.THEME_COLOR);
			mAdapter.afreshDisplayOption();
			mAdapter.notifyDataSetChanged();
		}
		if (null != bookView && isNeedRefreshAdapter) {
			bookView.startCloseBookAnimation(null);
			bookView = null;
		}
		if (isNeedRefreshAdapter) {
			updateReadProgress();
		}
		mCurrentClickPosition = -1;
		isNeedRefreshAdapter = false;
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
			BookShelfFragment.this.bookView = bookView;
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
					Toast.showBg(getActivity(), "未下载，'长按'进行重新下载");
					return;
				}
				list.clear();
				list = null;
				dismissRefreshDialog();
				readerBg = getActivity().getResources().getDrawable(
						BookTheme.READBOOK_BACKGROUND);

				if (isReaderNightMode()) {
					readerBg = null;
					readerBg = getActivity().getResources().getDrawable(
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
						if (!book.getBookIsRead()) {
							int index = mDownloadList.indexOf(book);
							book.setBookIsRead(true);
							book.setAutoResume(true);
							mDownloadList.set(index, book);
							downloadManager.setBookReader(bookName, bookId);
							mAdapter.notifyDataSetChanged();
						}
						startToActivity(bookName, bookId, bookType, lastUrl,
								statue, result);
						isNeedRefreshAdapter = true;
					}
				}, bookView.getParent(), readerBg, R.id.shelf_book_image);

			} catch (Exception e) {
				Log.e(e);
			}
		}
	}

	private void startToActivity(String bookName, String bookId, int bookType,
			String lastUrl, String statue, BookMark result) {

		Intent intent = new Intent(getActivity(), ReaderActivity.class);
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
		getActivity().overridePendingTransition(R.anim.fade_in, 0);
	}

	/**
	 * 设置RadioButton选择器
	 */
	private void setRadioButtonDrawableSelector() {
		StateListDrawable h_Drawable = ViewSelector.creatWidgetSelector(
				getActivity(), BookTheme.BOOK_SHELF_HORIZONTAL,
				R.drawable.book_horzial_white);
		StateListDrawable v_Drawable = ViewSelector.creatWidgetSelector(
				getActivity(), BookTheme.BOOK_SHELF_VERTICAL,
				R.drawable.book_vertica_white);
		// 设置上
		rb_horizontal.setCompoundDrawablesWithIntrinsicBounds(h_Drawable, null,
				null, null);
		rb_vertical.setCompoundDrawablesWithIntrinsicBounds(v_Drawable, null,
				null, null);
	}

	@Override
	public void onCheckedChanged(RadioGroup radiogroup, int id) {
		// TODO Auto-generated method stub
		switch (id) {
		case R.id.book_sort_horizontal:
			SharedPreferences.getInstance().putBoolean(
					SpConstant.BOOK_SHELF_DIRECTION, true);
			mGridView.setNumColumns(3);
			mGridView.setSelection(mCurrentScrollPosition);
			mAdapter.notifyDataSetChanged();
			afreshAgain();
			break;
		case R.id.book_sort_vertial:
			SharedPreferences.getInstance().putBoolean(
					SpConstant.BOOK_SHELF_DIRECTION, false);
			mGridView.setNumColumns(1);
			mGridView.setSelection(mCurrentScrollPosition);
			mAdapter.notifyDataSetChanged();
			afreshAgain();
			break;

		default:
			break;
		}

	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		// TODO Auto-generated method stub
		if (!haskMap.isEmpty()) {
			Toast.showLong("有书下载中，请稍后...");
		} else {
			BookDownloadInfo book = (BookDownloadInfo) parent
					.getItemAtPosition(position);
			showBookShareDialog(book);
		}
		return true;

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		BookView bookView = (BookView) view.findViewById(R.id.bookView);
		book = (BookDownloadInfo) parent.getItemAtPosition(position);
		if (mAdapter.isSelectedState) {
			ImageView deletedImage = (ImageView) view
					.findViewById(R.id.shelf_delected_box);
			ImageView deletedBookImage = (ImageView) view
					.findViewById(R.id.book_shelf_delected_box);
			if (deletedImage.getTag() == null
					|| deletedImage.getTag().equals("AA")) {
				deletedImage.setTag("BB");
				deletedImage
						.setImageResource(R.drawable.shelf_left_feedback_help_check);
				deletedList.add(book);
			} else {
				deletedImage.setTag("AA");
				deletedImage.setImageResource(R.drawable.help_uncheck);
				deletedList.remove(book);
			}
			if (deletedBookImage.getTag() == null
					|| deletedBookImage.getTag().equals("AA")) {
				deletedBookImage.setTag("BB");
				deletedBookImage
						.setImageResource(R.drawable.shelf_left_feedback_help_check);
				deletedList.add(book);
			} else {
				deletedBookImage.setTag("AA");
				deletedBookImage.setImageResource(R.drawable.help_uncheck);
				deletedList.remove(book);
			}

		} else {
			if (mCurrentClickPosition != -1)
				return;
			mCurrentClickPosition = position;
			// 已经下载完成
			if (book.isAutoResume()) {
				if (DownloadManager.getInstance().isExistTask(
						book.getBookName(), book.getBookId())) {
					Toast.showBg(getActivity(), "整本更新中,请稍后打开");
					mCurrentClickPosition = -1;
					return;
				}

				if (!isRefresh) {
					new OpenBookAsyncTask(getActivity(), position,
							book.getBookName(), book.getBookId(),
							book.getBookStatue(), book.getBookSourceType(),
							book.getLastUrl(), bookView).execute();
				} else {
					Toast.showBg(getActivity(), "更新中,请稍后打开");
					mCurrentClickPosition = -1;
				}
			} else {
				if (DownloadManager.getInstance().isExistTask(
						book.getBookName(), book.getBookId())) {
					Toast.showBg(getActivity(), "正在下载,请稍后打开");
				} else {
					Toast.showBg(getActivity(), "未完成下载,'长按'重新下载!");
				}
				mCurrentClickPosition = -1;
			}

		}
	}

	@Override
	public void onUpdateSuccess(LastChapter chapter) {
		// TODO Auto-generated method stub

		Toast.show(getActivity(), " 全部更新成功  ");
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				AnimationUtils.cancelAnim(imgRefresh);
			}
		}, 1000);
		isRefresh = false;
	}

	@Override
	public void onUpdateProgress(final String bookName) {
		// TODO Auto-generated method stub
		if (bookName.contains("本")) {
			getActivity().runOnUiThread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					Toast.show(getActivity(), bookName);
				}
			});

		} else {
			getActivity().runOnUiThread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					Toast.show(getActivity(), bookName + "  更新成功  ");
				}
			});

		}

	}

	@Override
	public void onUpdateFailure() {
		// TODO Auto-generated method stub
		Toast.show(getActivity(), "更新失败,请重试");
		AnimationUtils.cancelAnim(imgRefresh);
		isRefresh = false;
	}

	@Override
	public void onUpdateAllNewChapter() {
		// TODO Auto-generated method stub
		Toast.show(getActivity(), "     全部为最新     ");
		AnimationUtils.cancelAnim(imgRefresh);
		isRefresh = false;
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
	public void onPreDeleted(String bookName, String bookId) {
		// TODO Auto-generated method stub
		haskMap.put(bookId, bookId);
		AnimationUtils.setViewRotating(getActivity(), imgRefresh);
	}

	@Override
	public void onPreDownload() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCancelDownload() {
		// TODO Auto-generated method stub
		AnimationUtils.cancelAnim(imgRefresh);
	}

	@Override
	public void onPostDownloadSuccess(String bookName, String bookId) {
		// TODO Auto-generated method stub
		if (needDownBookCount > 1) {
			needDownBookCount--;
		} else {
			AnimationUtils.cancelAnim(imgRefresh);
			needDownBookCount = 1;
		}
		haskMap.remove(bookId);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		ImageLoader.getInstance().clearMemoryCache();
		if (readerBg != null)
			readerBg = null;
		if (mAdapter != null)
			mAdapter.destory();
		super.onDestroy();

	}

	/**
	 * 删除本地书
	 * 
	 */
	public class DeleteBookTask extends AsyncTask<Void, Void, Void> {
		public String bookName;
		public int downloadType;
		public boolean isDeleteAll;
		public BookDownloadInfo book;

		public DeleteBookTask(BookDownloadInfo book, String bookName,
				int downloadType, boolean isDeleteAll) {
			this.book = book;
			this.bookName = bookName;
			this.downloadType = downloadType;
			this.isDeleteAll = isDeleteAll;

		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			showRefreshDialog("正在删除中 ");

		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			// 删除本书
			FileUtils fileUtils = new FileUtils(getActivity());
			if (isDeleteAll) {

				downloadManager.deletedBookList(deletedList);
				for (int i = 0; i < deletedList.size(); i++) {
					// 删除标签
					String bookName = deletedList.get(i).getBookName();
					String bookId = deletedList.get(i).getBookId();
					DownloadManager.getInstance().deleteTask(bookName, bookId);
					downloadType = deletedList.get(i).getBookSourceType();
					BookMarkDb.getInstance(getActivity(), "book")
							.deletBookMark(bookName, bookId);

					if (downloadType == 1) {
						fileUtils.deleteMeBook(bookName, bookId);

					} else if (downloadType == 2) {
						// 删除百度的书籍本地数据，主要保存了用于查找换源
						fileUtils.deleteChapterDbByName(bookName);
						fileUtils.deleteBaiduBook(bookName, bookId);
					}

				}

			} else {

				if (downloadType == 1) {
					fileUtils.deleteMeBook(bookName, book.getBookId());

				} else if (downloadType == 2) {
					// 删除百度的书籍本地数据，主要保存了用于查找换源
					fileUtils.deleteChapterDbByName(bookName);
					fileUtils.deleteBaiduBook(bookName, book.getBookId());
				}
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			if (isDeleteAll) {
			} else {

				deletedList.add(book);
				downloadManager.deletedBookList(deletedList);
				for (int i = 0; i < deletedList.size(); i++) {
					BookMarkDb.getInstance(getActivity(), "book")
							.deletBookMark(deletedList.get(i).getBookName(),
									deletedList.get(i).getBookId());
				}
				mDownloadList.remove(book);
				mAdapter.remove(deletedList);
				deletedList.clear();
			}

			if (getActivity() != null) {
				Toast.show(getActivity(), " 删除完成 ");
				dismissRefreshDialog();
			}
			if (mDownloadList.isEmpty()) {
				shelf_empty_image.setVisibility(View.VISIBLE);
			}

		}
	}

	public Bitmap getLoacalBitmap(String url) {
		try {
			FileInputStream fis = new FileInputStream(url);
			return BitmapFactory.decodeStream(fis); // /把流转化为Bitmap图片

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 查找需要更新的Book
	 * 
	 * @param bookName
	 * @return
	 */
	private static BookDownloadInfo getNeedUpateBook(String bookName,
			String bookId) {
		for (BookDownloadInfo book : mDownloadList) {
			if (book.getBookName().equals(bookName)
					&& book.getBookId().equals(bookId))
				return book;
		}
		return null;
	}

	/**
	 * 更新下载完成的状态
	 * 
	 * @param bookName
	 */
	private static void updateBookAuResume(String bookName, String bookId) {
		BookDownloadInfo book = getNeedUpateBook(bookName, bookId);
		int index = mDownloadList.indexOf(book);
		book.setBookIsRead(false);
		book.setAutoResume(true);
		mDownloadList.set(index, book);
	}

	/**
	 * 更新阅读进度
	 */
	private void updateReadProgress() {
		try {
			BookDownloadInfo oldBook = mAdapter.getItem(mCurrentClickPosition);
			BookDownloadInfo newBook = downloadManager.querryByBookName(
					oldBook.getBookName(), oldBook.getBookId());
			mAdapter.set(oldBook, newBook);
			mGridView.setSelection(mCurrentClickPosition);
			mAdapter.notifyDataSetChanged();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
		switch (scrollState) {
		// 空闲状态
		case OnScrollListener.SCROLL_STATE_IDLE:
			isScrollStateFling = false;
			break;
		// 滚动状态
		case OnScrollListener.SCROLL_STATE_FLING:
			isScrollStateFling = true;
			break;
		// 触摸后滚动
		case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
			isScrollStateFling = false;
			break;
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		mCurrentScrollPosition = firstVisibleItem;
	}

	/**
	 * 再次刷新列表
	 */
	private void afreshAgain() {
		if (isScrollStateFling) {
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					mAdapter.notifyDataSetChanged();
				}
			}, 100);
		}
	}

}