package com.himoo.ydsc.activity;

import java.util.ArrayList;

import org.json.JSONObject;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.himoo.ydsc.R;
import com.himoo.ydsc.adapter.SearchBaiduBookAdapter;
import com.himoo.ydsc.adapter.SearchBookAdapter;
import com.himoo.ydsc.animation.AnimationUtils;
import com.himoo.ydsc.bean.BaiduBook;
import com.himoo.ydsc.bean.BookSearch;
import com.himoo.ydsc.db.BookDb;
import com.himoo.ydsc.db.bean.BookSearchRecords;
import com.himoo.ydsc.http.BookRefreshTask;
import com.himoo.ydsc.http.BookSearchTask;
import com.himoo.ydsc.http.BookSearchTask.OnSearchListener;
import com.himoo.ydsc.http.HttpConstant;
import com.himoo.ydsc.listener.OnTaskRefreshListener;
import com.himoo.ydsc.ui.swipebacklayout.SwipeBackActivity;
import com.himoo.ydsc.ui.utils.Toast;
import com.himoo.ydsc.ui.utils.UIHelper;
import com.himoo.ydsc.util.NetWorkUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

public class SearchResultActivity extends SwipeBackActivity implements
		OnSearchListener, OnTaskRefreshListener<BaiduBook> {

	/** 最先返回的服务器 0 表示自己的 1表示百度的 */
	public int firstReSuccess = -1;

	/** 百度的页数 20,40,60 */
	public int mCurrentBaiduPage = 0;
	/** 自己服务器的页数 （1,2,3,4,5 ）*20 */
	public int mCurrentMePage = 1;

	@ViewInject(R.id.tv_search_empty)
	private TextView tv_search_empty;

	@ViewInject(R.id.pull_refresh_list)
	private PullToRefreshListView mRefrshListView;

	/** 是否已经执行了错误 */
	private boolean isHasFilure = false;

	/** 标记当前点击Item的位置 */
	private int mCurrentClickPosition = -1;

	/** 标题 */
	private String title;

	private ArrayList<BookSearch> bookList;

	private ArrayList<BaiduBook> baiduBookList;

	private SearchBaiduBookAdapter mBaiduAdapter;

	private SearchBookAdapter mBookAdapter;

	private ImageView imgRefersh;
	/** 表示是否搜索到了书 */
	private boolean isHasSearchBook = false;
	/** 　返回的总的数 */
	private int bookTotal;

	private int loadingBookCount = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_layout);
		initPullToRefeshListView();
		showRefreshDialog("正在加载中");
		exeTask();
	}

	/**
	 * 设置PullToRefeshListView 事件监听
	 */
	private void initPullToRefeshListView() {
		mRefrshListView.setMode(Mode.BOTH);
		// 设置点击事件
		mRefrshListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				try {

					if (mCurrentClickPosition != -1)
						return;
					mCurrentClickPosition = position;
					if (firstReSuccess == 0) {
						BookSearch book = (BookSearch) parent
								.getItemAtPosition(position);
						UIHelper.startToActivity(SearchResultActivity.this,
								book, BookDialogActivity.class);
					} else if (firstReSuccess == 1) {
						BaiduBook book = (BaiduBook) parent
								.getItemAtPosition(position);
						UIHelper.startToActivity(SearchResultActivity.this,
								book, BaiduDetailsActivity.class);
					}
				} catch (Exception e) {
					Log.e(e);
				}
			}
		});
		// 设置滑动事件
		mRefrshListView
				.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {

					@Override
					public void onPullDownToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						// TODO Auto-generated method stub
						isHasSearchBook = false;
						if (firstReSuccess == 0) {
							BookSearchTask.getInstance().executeMe(title, "1",
									mRefrshListView, false);
						} else if (firstReSuccess == 1) {
							BookRefreshTask<BaiduBook> task = new BookRefreshTask<BaiduBook>(
									mRefrshListView);
							task.setOnRefreshListener(SearchResultActivity.this);
							task.execute(title, 20,
									HttpConstant.BOOK_REQUEST_TYPE_BAIDU_SEARCH);
						}

					}

					@Override
					public void onPullUpToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						// 加载更多数据
						if (firstReSuccess == 1) {
							BookRefreshTask<BaiduBook> task = new BookRefreshTask<BaiduBook>(
									mRefrshListView);
							task.setOnRefreshListener(SearchResultActivity.this);
							task.execute(title, mCurrentBaiduPage * 20,
									HttpConstant.BOOK_REQUEST_TYPE_BAIDU_SEARCH);
							// 去自己服务器搜索数据
						} else if (firstReSuccess == 0) {
							BookSearchTask.getInstance()
									.executeMe(title, mCurrentMePage + "",
											mRefrshListView, false);

						}

					}

				});

	}

	/**
	 * 两个服务请求同时执行
	 */
	private void exeTask() {
		save(title);
		BookSearchTask.getInstance().setonSearchListener(this);
		BookSearchTask.getInstance().executeBaidu(title, "0");
		BookSearchTask.getInstance().executeMe(title, "1", mRefrshListView,
				true);
	}

	private void save(String keyword) {
		BookDb bookDb = BookDb.getInstance(this, "Book");
		BookSearchRecords record = new BookSearchRecords();
		record.setRecord(keyword);
		bookDb.saveBookSearch(record);
	}

	@Override
	protected void initTitleBar() {
		title = getIntent().getStringExtra("keyWord");
		mTitleBar.setTitle(title);
		mTitleBar.setRightLogoGone();
		imgRefersh = mTitleBar.getRightLogo();
		imgRefersh.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				firstReSuccess = -1;
				showRefreshDialog("正在加载中");
				AnimationUtils.setViewRotating(SearchResultActivity.this,
						imgRefersh);
				BookSearchTask.getInstance().executeBaidu(title, "0");

			}
		});
	}

	@Override
	public void onSearchSucess(String json, int whoservice, int refreshType,
			boolean isFirst) {
		// TODO Auto-generated method stub
		AnimationUtils.cancelAnim(imgRefersh);
		if (isFirst) {
			if (firstReSuccess == -1) {
				firstReSuccess = whoservice;
				if (firstReSuccess == 0) {
					try {
						Gson gosn = new Gson();
						bookList = gosn.fromJson(json,
								new TypeToken<ArrayList<BookSearch>>() {
								}.getType());

						if (bookList != null && !bookList.isEmpty()) {
							dismissRefreshDialog();
							tv_search_empty.setVisibility(View.GONE);
							mRefrshListView.setVisibility(View.VISIBLE);
							mTitleBar.setRightLogoGone();
							mBookAdapter = new SearchBookAdapter(
									SearchResultActivity.this,
									R.layout.adapter_search_item, bookList);
							mRefrshListView.setAdapter(mBookAdapter);
							mCurrentMePage++;
							isHasSearchBook = true;

						} else {
							loadingBookCount++;
							firstReSuccess = -1;
							isHasSearchBook = true;
							if (loadingBookCount == 2) {
								dismissRefreshDialog();
								Toast.showLong(SearchResultActivity.this,
										"未搜到该书");
							}
						}
					} catch (Exception e) {
						Toast.showLong(SearchResultActivity.this, "加载失败");
					}
				} else {
					mCurrentBaiduPage += 1;
					baiduBookList = parseBaiduJson(json);
					if (baiduBookList != null && !baiduBookList.isEmpty()) {
						dismissRefreshDialog();
						tv_search_empty.setVisibility(View.GONE);
						mRefrshListView.setVisibility(View.VISIBLE);
						mTitleBar.setRightLogoGone();
						mBaiduAdapter = new SearchBaiduBookAdapter(
								SearchResultActivity.this,
								R.layout.adapter_search_item, baiduBookList);
						mRefrshListView.setAdapter(mBaiduAdapter);
						isHasSearchBook = true;
					} else {
						loadingBookCount++;
						firstReSuccess = -1;
						isHasSearchBook = true;
						if (loadingBookCount == 2) {
							dismissRefreshDialog();
							Toast.showLong(SearchResultActivity.this, "未搜到该书");
						}
					}
				}
			}

		} else {
			try {

				Gson gosn = new Gson();
				ArrayList<BookSearch> bookLists = gosn.fromJson(json,
						new TypeToken<ArrayList<BookSearch>>() {
						}.getType());

				if (refreshType == BookSearchTask.TYPE_PULL_DOWN_UPDATE) {

					if (bookLists != null && bookLists.size() > 0) {
						AnimationUtils.cancelAnim(imgRefersh);
						if (mBookAdapter != null) {
							mBookAdapter.clear();
							mBookAdapter.addAll(bookLists);
							mRefrshListView.setAdapter(mBookAdapter);
							mCurrentMePage = 2;
						}
					}

				} else if (refreshType == BookSearchTask.TYPE_PULL_UP_LOAD) {
					if (bookLists != null && bookLists.size() > 0) {
						mCurrentMePage++;
						if (mBookAdapter != null)
							mBookAdapter.addAll(bookLists);
					} else {
						Toast.showShort(SearchResultActivity.this, "已加载完毕");
					}
				}
				notifyDataAndRefreshComplete();
			} catch (Exception e) {
				Toast.showLong(SearchResultActivity.this, "加载失败");
			}
		}
	}

	@Override
	public void onSearcFailure(Exception error, String msg, int whoservice) {
		// TODO Auto-generated method stub
		if (isHasFilure)
			return;
		if (isHasSearchBook || whoservice == 1) {
			dismissRefreshDialog();
			AnimationUtils.cancelAnim(imgRefersh);
			mRefrshListView.onRefreshComplete();
			if (!NetWorkUtils.isNetConnected(this)) {
				Toast.showBg(this, "未连接网络");
			} else {
				Toast.showBg(this, "获取数据失败");
			}
		}
		isHasFilure = true;
	}

	/**
	 * 解析百度字符串json
	 * 
	 * @param json
	 * @param request
	 * @return
	 */
	private ArrayList<BaiduBook> parseBaiduJson(String json) {
		ArrayList<BaiduBook> list = null;
		try {
			JSONObject jsonObject = new JSONObject(json);
			Gson gson = new Gson();
			if (jsonObject.getInt("errno") == 0
					&& jsonObject.get("errmsg").equals("ok")) {
				bookTotal = Integer.valueOf(jsonObject.getString("total"));
				JSONObject subJsonObject = jsonObject.getJSONObject("result");
				String result = subJsonObject.getString("search");
				list = gson.fromJson(result,
						new TypeToken<ArrayList<BaiduBook>>() {
						}.getType());
			}
		} catch (Exception e) {
			Log.e("解析百度书库出错：" + e.getMessage());
			return null;
		}
		return list;
	}

	@Override
	public void onPullUpRefreshSucess(ArrayList<BaiduBook> list) {
		// TODO Auto-generated method stub

		if (list != null && list.size() > 0) {
			if (mBaiduAdapter != null) {
				if (mBaiduAdapter.getCount() < bookTotal) {
					mBaiduAdapter.addAll(list);
					mCurrentBaiduPage += 1;
				} else {
					Toast.showShort(this, "已加载完毕");
					notifyDataAndRefreshComplete();
				}
			}

		}
		notifyDataAndRefreshComplete();

	}

	@Override
	public void onPullDownRefreshSucess(ArrayList<BaiduBook> list) {
		// TODO Auto-generated method stub
		if (list != null && list.size() > 0) {
			if (mBaiduAdapter != null) {
				mBaiduAdapter.clear();
				mBaiduAdapter.addAll(list);
				mRefrshListView.setAdapter(mBaiduAdapter);
				mCurrentBaiduPage = 1;
			}
		}
		notifyDataAndRefreshComplete();
	}

	@Override
	public void onPullToRefreshFailure(Exception error, String msg) {
		// TODO Auto-generated method stub
		if (!NetWorkUtils.isNetConnected(this)) {
			Toast.showBg(this, "未连接网络");
		} else {
			loadingBookCount++;
			if (loadingBookCount == 2)
				Toast.showBg(this, "获取数据失败");
		}
		AnimationUtils.cancelAnim(imgRefersh);
		notifyDataAndRefreshComplete();
	}

	/**
	 * 通知数据改变，并且数据刷新完毕
	 */
	private void notifyDataAndRefreshComplete() {
		if (firstReSuccess == 0) {
			if (mBookAdapter != null) {
				// 通知数据改变了
				mBookAdapter.notifyDataSetChanged();
				// 加载完成后停止刷新
			}
		} else if (firstReSuccess == 1) {
			if (mBaiduAdapter != null) {
				// 通知数据改变了
				mBaiduAdapter.notifyDataSetChanged();
				// 加载完成后停止刷新
			}
		}
		mRefrshListView.onRefreshComplete();

	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mCurrentClickPosition = -1;
	}

}
