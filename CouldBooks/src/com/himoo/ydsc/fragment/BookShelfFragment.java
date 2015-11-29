package com.himoo.ydsc.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.himoo.ydsc.R;
import com.himoo.ydsc.activity.BookQueryActivity;
import com.himoo.ydsc.adapter.BookDownloadAdapter;
import com.himoo.ydsc.base.BaseFragment;
import com.himoo.ydsc.config.BookTheme;
import com.himoo.ydsc.dialog.BookDeletePopupWindow;
import com.himoo.ydsc.dialog.BookDeletePopupWindow.OnPopupClickListener;
import com.himoo.ydsc.download.BookDownloadInfo;
import com.himoo.ydsc.download.BookDownloadManager;
import com.himoo.ydsc.download.BookDownloadService;
import com.himoo.ydsc.manager.PageManager;
import com.himoo.ydsc.share.UmengShare;
import com.himoo.ydsc.ui.utils.Toast;
import com.himoo.ydsc.ui.utils.UIHelper;
import com.himoo.ydsc.ui.utils.ViewSelector;
import com.himoo.ydsc.ui.view.BookTitleBar;
import com.himoo.ydsc.util.BookSortUtil;
import com.himoo.ydsc.util.SharedPreferences;
import com.ios.dialog.ActionSheetDialog;
import com.ios.dialog.ActionSheetDialog.OnSheetItemClickListener;
import com.ios.dialog.AlertDialog;
import com.ios.edittext.SearchEditText;
import com.ios.edittext.SearchEditText.OnEditTextFocuseChangListener;
import com.ios.edittext.SearchEditText.OnSearchClickListener;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.util.LogUtils;

/**
 * 用于展示下载及阅读过的书
 * 
 */
public class BookShelfFragment extends BaseFragment implements
		OnPopupClickListener, OnSearchClickListener,
		OnEditTextFocuseChangListener {

	/** 标题栏 */
	private BookTitleBar titleBar;
	/** 判断是否处于删除状态 */
	private boolean isDelectStute = false;
	private Button bookSort;

	private PullToRefreshGridView mPullToGridView;
	private GridView mGridView;
	private static BookDownloadManager downloadManager;
	private static BookDownloadAdapter mAdapter;

	private ArrayList<BookDownloadInfo> mDownloadList;
	private static ImageView shelf_empty_image;

	private SearchEditText searchText;
	private BookDeletePopupWindow popupWindow;

	/** 是否可以搜索了 */
	private boolean isKeyEnterDown = false;

	/** 要删除的书籍集合list */
	private List<BookDownloadInfo> deletedList = new ArrayList<BookDownloadInfo>();
	/*** 记录默认排序的List集合 */
	private static List<BookDownloadInfo> mDefaultSortList = new ArrayList<BookDownloadInfo>();

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
		titleBar.setTitleView(layout);
		searchText = (SearchEditText) view.findViewById(R.id.book_shelf_search);
		searchText.setTextColor(BookTheme.THEME_COLOR);
		searchText.setOnSearchClickListener(this);
		searchText.setOnFocuesChangeListener(this);
		searchText.setIconLeft(true);
		titleBar.getLeftTextView().setOnClickListener(this);
		bookSort.setOnClickListener(this);
		titleBar.getRightLogo().setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
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

								}
							}).show();
				} else if (type == 2) {
					Toast.showShort(getActivity(), "只更新最新章节");
				}
			}
		});

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
		mGridView.setOnItemLongClickListener(this);
		mGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				BookDownloadInfo book = (BookDownloadInfo) parent
						.getItemAtPosition(position);
				if (mAdapter.isSelectedState) {
					ImageView deletedImage = (ImageView) view
							.findViewById(R.id.shelf_delected_box);
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

				} else {

					if (book.getProgress() < book.getFileLength()
							|| book.getProgress() == 0) {
						try {
							Toast.showShort(getActivity(), "继续下载");
							downloadManager
									.resumeDownload(
											book,
											new BookDownloadAdapter.DownloadRequestCallBack(
													book));
						} catch (DbException e) {
							// TODO Auto-generated catch block
							android.util.Log.i("msg",
									"DbException" + e.getMessage());
						}
						// mAdapter.notifyDataSetChanged();
					}

				}

			}
		});
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

	@Override
	public void onDestroy() {
		try {
			if (mAdapter != null && downloadManager != null) {
				downloadManager.backupDownloadInfoList();
			}
		} catch (DbException e) {
			LogUtils.e(e.getMessage(), e);
		}
		super.onDestroy();
	}

	/**
	 * 内部类,用于接受下载过来的通知
	 * 
	 */
	public static class BookDownloadReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String bookName = intent.getStringExtra("bookName");
			String dowloadUrl = intent.getStringExtra("dowloadUrl");
			List<BookDownloadInfo> list = downloadManager.findLastBookInfo(
					bookName, dowloadUrl);
			if (list != null && list.size() > 0) {
				if (shelf_empty_image != null)
					shelf_empty_image.setVisibility(View.GONE);
				mAdapter.addFirst(list.get(0));
				try {
					downloadManager.resumeDownload(list.get(0),
							new BookDownloadAdapter.DownloadRequestCallBack(
									list.get(0)));
					mDefaultSortList.clear();
					mDefaultSortList.addAll(downloadManager.getAllBook());
				} catch (DbException e) {
					// TODO Auto-generated catch block
					android.util.Log.i("msg", "DbException" + e.getMessage());
				}
				android.util.Log.i("msg", "notifyDataSetChanged");
				mAdapter.notifyDataSetChanged();
			}

		}
	}

	@Override
	public void onPopupClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.btn_allSelected:
			if (mAdapter != null) {
				mAdapter.isChoice = true;
				mAdapter.notifyDataSetChanged();
				deletedList.addAll(mDownloadList);
			}

			break;
		case R.id.btn_allNotSelected:
			if (mAdapter != null) {
				mAdapter.isChoice = false;
				deletedList.removeAll(mDownloadList);
				mAdapter.notifyDataSetChanged();
			}

			break;
		case R.id.btn_deleted:
			if (deletedList.isEmpty()) {
				Toast.showShort(getActivity(), "您还没有选择要删除的书籍！！");
				return;
			}
			downloadManager.deletedBookList(deletedList);
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

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		// TODO Auto-generated method stub
		BookDownloadInfo book = (BookDownloadInfo) parent
				.getItemAtPosition(position);
		showBookShareDialog(book);

		return false;

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
				mDownloadList.remove(book);
				mAdapter.remove(deletedList);

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
		if (BookTheme.isThemeChange)
		{
			titleBar.setBackgroundColor(BookTheme.THEME_COLOR);
			ViewSelector.setButtonStrokeSelector(getActivity(), bookSort);
			bookSort.setTextColor(BookTheme.THEME_COLOR);
			searchText.setTextColor(BookTheme.THEME_COLOR);
		}
	}
}
