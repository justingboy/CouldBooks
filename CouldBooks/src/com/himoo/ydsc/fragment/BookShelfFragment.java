package com.himoo.ydsc.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
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
import com.himoo.ydsc.config.BookTheme;
import com.himoo.ydsc.config.SpConstant;
import com.himoo.ydsc.dialog.BookDeletePopupWindow;
import com.himoo.ydsc.dialog.BookDeletePopupWindow.OnPopupClickListener;
import com.himoo.ydsc.download.BookDownloadInfo;
import com.himoo.ydsc.download.BookDownloadManager;
import com.himoo.ydsc.download.BookDownloadService;
import com.himoo.ydsc.manager.PageManager;
import com.himoo.ydsc.reader.ReaderActivity;
import com.himoo.ydsc.reader.dao.BookMark;
import com.himoo.ydsc.reader.dao.BookMarkDb;
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
import com.himoo.ydsc.util.NetWorkUtils;
import com.himoo.ydsc.util.SP;
import com.himoo.ydsc.util.SharedPreferences;
import com.ios.dialog.ActionSheetDialog;
import com.ios.dialog.ActionSheetDialog.OnSheetItemClickListener;
import com.ios.dialog.AlertDialog;
import com.ios.edittext.SearchEditText;
import com.ios.edittext.SearchEditText.OnEditTextFocuseChangListener;
import com.ios.edittext.SearchEditText.OnSearchClickListener;
import com.ios.radiogroup.SegmentedGroup;

/**
 * 用于展示下载及阅读过的书
 * 
 */
public class BookShelfFragment extends BaseFragment implements
		OnPopupClickListener, OnSearchClickListener,
		OnEditTextFocuseChangListener, OnCheckedChangeListener,
		OnNewChapterUpdateListener, OnItemClickListener {

	/** 标题栏 */
	private BookTitleBar titleBar;
	/** 判断是否处于删除状态 */
	private boolean isDelectStute = false;
	private Button bookSort;

	private PullToRefreshGridView mPullToGridView;
	private static GridView mGridView;
	private static BookDownloadManager downloadManager;
	private static BookDownloadAdapter mAdapter;

	private ArrayList<BookDownloadInfo> mDownloadList;
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
	/** 判断是否是最新下载 */
	private static boolean isLastDownload = false;

	private BookDownloadInfo book;

	public BookView bookView;
	/** 　正在下载的书名　 */
	private static String downlaodBookName = "";
	/** 　是否正在下载，下载中不可点击 */
	public static boolean isDownloading = false;
	/**   是否正在刷新中，刷新中不可点击　 */
	private boolean isRefresh = false;

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
			int type = SharedPreferences.getInstance().getInt(
					"book_update_type", 2);
			if (type == 1) {
				new AlertDialog(getActivity())
						.builder()
						.setTitle("提醒")
						.setMsg("您现在选择的是整本更新模式，我们会更新您的整本书，这将会把您本地的书籍全部重新下载,是否继续。")
						.setNegativeButton("取消", new OnClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub

							}
						}).setPositiveButton("确定", new OnClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								if (NetWorkUtils.isNetConnected(getActivity())) {
									AnimationUtils.setViewRotating(
											getActivity(), imgRefresh);
									isRefresh = true;
									BookUpdateTask task = new BookUpdateTask(
											getActivity(), null, 2);
									task.setOnNewChapterListener(BookShelfFragment.this);
									task.execute();
								} else {
									Toast.show(getActivity(), "未连接网络!");
								}
							}
						}).show();
			} else if (type == 2) {
				if (NetWorkUtils.isNetConnected(getActivity())) {
					AnimationUtils.setViewRotating(getActivity(), imgRefresh);
					isRefresh = true;
					BookUpdateTask task = new BookUpdateTask(getActivity(),
							null, 2);
					task.setOnNewChapterListener(BookShelfFragment.this);
					task.execute();
				} else {
					Toast.show(getActivity(), "未连接网络!");
				}
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
			isLastDownload = false;
			switch (which) {
			case 1:
				bookSort.setText("默认排序");
				mDefaultSortList.removeAll(deletedList);
				mAdapter.addAll(mDefaultSortList);
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
				Log.d("更新时间：" + mDownloadList.get(0).getBookLastUpdateTime());
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
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String bookName = intent.getStringExtra("bookName");
			downlaodBookName = bookName;
			String dowloadUrl = intent.getStringExtra("dowloadUrl");
			List<BookDownloadInfo> list = downloadManager.findLastBookInfo(
					bookName, dowloadUrl);
			if (list != null && list.size() > 0) {
				isLastDownload = true;
				if (shelf_empty_image != null)
					shelf_empty_image.setVisibility(View.GONE);
				mAdapter.addFirstN(list.get(0));
				mGridView.setSelection(0);
				mAdapter.notifyDataSetChanged();
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
				BookMarkDb.getInstance(getActivity(), "book").deletBookMark(
						deletedList.get(i).getBookName());
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
			break;
		case R.id.btn_cancel:
			if (popupWindow != null)
				popupWindow.dismiss();
			titleBar.setLeftDrawable(R.drawable.book_deleted);
			isDelectStute = false;
			if (mAdapter != null) {
				mAdapter.isSelectedState = false;
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
				// deletedList.add(book);
				break;
			case 2:
				// 注册友盟分享
				UmengShare.getInstance().setShareContent(getActivity(),
						book.getBookName(), book.getBookCoverImageUrl(),
						book.getBookName(), book.getDownloadUrl());
				UmengShare.getInstance().addCustomPlatforms(getActivity());
				break;
			case 3:
				// 删除本书
				deletedList.add(book);
				downloadManager.deletedBookList(deletedList);
				for (int i = 0; i < deletedList.size(); i++) {
					BookMarkDb.getInstance(getActivity(), "book")
							.deletBookMark(deletedList.get(i).getBookName());
				}
				mDownloadList.remove(book);
				mAdapter.remove(deletedList);
				deletedList.clear();
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
			mAdapter.notifyDataSetChanged();
		}
		if (!SharedPreferences.getInstance().getBoolean(
				SpConstant.BOOK_SHELF_DIRECTION, true)) {
			if (mAdapter != null) {
				if (mCurrentClickPosition != -1) {
					BookDownloadInfo oldBook = null;
					if (isLastDownload) {
						if (isLastDownload) {
							oldBook = mAdapter.getFirst();
							isLastDownload = false;
						}
					} else {
						// sortBookList(mDownloadList);
						oldBook = book;
					}
					if (oldBook != null) {
						BookDownloadInfo newBook = downloadManager
								.querryByBookName(oldBook.getBookName());
						mAdapter.set(oldBook, newBook);
						mGridView.setSelection(mCurrentClickPosition);
						mAdapter.notifyDataSetChanged();
					}
				}
			}
		} else if (SharedPreferences.getInstance().getBoolean(
				SpConstant.BOOK_SHELF_DIRECTION, true)) {
			if (mAdapter != null) {
				if (mCurrentClickPosition != -1) {
					BookDownloadInfo oldBook = null;
					if (isLastDownload) {
						oldBook = mAdapter.getFirst();
						isLastDownload = false;
					} else {
						oldBook = book;
					}
					if (oldBook != null) {
						BookDownloadInfo newBook = downloadManager
								.querryByBookName(oldBook.getBookName());
						mAdapter.set(oldBook, newBook);
						mGridView.setSelection(mCurrentClickPosition);
						mAdapter.notifyDataSetChanged();
					}
				}
			}
		}
		mCurrentClickPosition = -1;
		if (null != bookView) {
			bookView.startCloseBookAnimation();
			bookView = null;
		}
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
		public String lastUrl;

		public OpenBookAsyncTask(Context context, String bookName,
				String statue, int bookType, String lastUrl, BookView bookView) {
			this.mContext = context;
			this.bookName = bookName;
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

			ArrayList<BaiduBookChapter> list = LocalReaderUtil.getInstance()
					.parseLocalBook(bookName, bookType);
			IOHelper.getBook(mContext, bookName, list);
			list.clear();
			list = null;
			BookMark bookMark = BookMarkDb.getInstance(mContext, "book")
					.querryReaderPos(bookName);

			return bookMark;
		}

		@Override
		protected void onPostExecute(final BookMark result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			dismissRefreshDialog();
			// readerBg = getActivity().getResources().getDrawable(
			// BookTheme.READBOOK_BACKGROUND);

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
					startToActivity(bookName, bookType, lastUrl, statue, result);
				}
			}, bookView.getParent(), readerBg);

		}

	}

	private void startToActivity(String bookName, int bookType, String lastUrl,
			String statue, BookMark result) {

		Intent intent = new Intent(getActivity(), ReaderActivity.class);
		intent.putExtra("bookName", bookName);
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
			mAdapter.notifyDataSetChanged();
			break;
		case R.id.book_sort_vertial:
			SharedPreferences.getInstance().putBoolean(
					SpConstant.BOOK_SHELF_DIRECTION, false);
			mGridView.setNumColumns(1);
			mAdapter.notifyDataSetChanged();
			break;

		default:
			break;
		}

	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		// TODO Auto-generated method stub
		BookDownloadInfo book = (BookDownloadInfo) parent
				.getItemAtPosition(position);
		showBookShareDialog(book);
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
			if (SP.getInstance().getBoolean(downlaodBookName, true)
					&& SP.getInstance().getBoolean("isDown", true)&&!isRefresh) {
				new OpenBookAsyncTask(getActivity(), book.getBookName(),
						book.getBookStatue(), book.getBookSourceType(),
						book.getLastUrl(), bookView).execute();
			} else {
				
				Toast.show(getActivity(), isRefresh?"更新中,请稍后打开":"下载中,请稍后打开");
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
		Log.i("onUpdateProgress" + Thread.currentThread().getName());
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
}