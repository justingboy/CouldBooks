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
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.himoo.ydsc.R;
import com.himoo.ydsc.adapter.SearchBaiduBookAdapter;
import com.himoo.ydsc.adapter.SearchBookAdapter;
import com.himoo.ydsc.animation.AnimationUtils;
import com.himoo.ydsc.bean.BaiduBook;
import com.himoo.ydsc.bean.BookSearch;
import com.himoo.ydsc.http.BookRefreshTask;
import com.himoo.ydsc.http.BookSearchTask;
import com.himoo.ydsc.http.BookSearchTask.OnSearchListener;
import com.himoo.ydsc.http.HttpConstant;
import com.himoo.ydsc.listener.OnTaskRefreshListener;
import com.himoo.ydsc.ui.swipebacklayout.SwipeBackActivity;
import com.himoo.ydsc.ui.utils.Toast;
import com.himoo.ydsc.ui.utils.UIHelper;
import com.lidroid.xutils.view.annotation.ViewInject;

public class SearchResultActivity extends SwipeBackActivity implements
		OnSearchListener, OnTaskRefreshListener<BaiduBook> {

	/** 最先返回的服务器 0 表示自己的 1表示百度的 */
	public int firstReSuccess = -1;

	/** 百度的页数 0,2,4,6 */
	public int mCurrentBaiduPage = 0;
	/** 自己服务器的页数 1,2,3,4,5 */
	public int mCurrentMePage = 1;

	@ViewInject(R.id.tv_search_empty)
	private TextView tv_search_empty;

	@ViewInject(R.id.pull_refresh_list)
	private PullToRefreshListView mRefrshListView;

	/** 标记当前点击Item的位置 */
	private int mCurrentClickPosition = -1;

	/** 标题 */
	private String title;

	private ArrayList<BookSearch> bookList;

	private ArrayList<BaiduBook> baiduBookList;

	private SearchBaiduBookAdapter mBaiduAdapter;

	private SearchBookAdapter mBookAdapter;

	private ImageView imgRefersh;

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
		// 设置点击事件
		mRefrshListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (mCurrentClickPosition != -1)
					return;
				mCurrentClickPosition = position;
				if (firstReSuccess == 0) {
					BookSearch book = (BookSearch) parent
							.getItemAtPosition(position);
					UIHelper.startToActivity(SearchResultActivity.this, book,
							BookDialogActivity.class);
				} else if (firstReSuccess == 1) {
					BaiduBook book = (BaiduBook) parent
							.getItemAtPosition(position);
					UIHelper.startToActivity(SearchResultActivity.this, book,
							BaiduDetailsActivity.class);
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
						if (firstReSuccess == 0) {
							BookSearchTask.getInstance().executeMe(title, "1",
									mRefrshListView, false);
						} else if (firstReSuccess == 1) {
							BookRefreshTask<BaiduBook> task = new BookRefreshTask<BaiduBook>(
									mRefrshListView);
							task.setOnRefreshListener(SearchResultActivity.this);
							task.execute(title, 0,
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
							task.execute(title, mCurrentBaiduPage,
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
		BookSearchTask.getInstance().setonSearchListener(this);
		BookSearchTask.getInstance().executeBaidu(title, "0");
		BookSearchTask.getInstance().executeMe(title, "1", mRefrshListView,
				true);
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
					Gson gosn = new Gson();
					bookList = gosn.fromJson(json,
							new TypeToken<ArrayList<BookSearch>>() {
							}.getType());
					dismissRefreshDialog();
					if (bookList != null && !bookList.isEmpty()) {
						tv_search_empty.setVisibility(View.GONE);
						mRefrshListView.setVisibility(View.VISIBLE);
						mTitleBar.setRightLogoGone();
						mBookAdapter = new SearchBookAdapter(
								SearchResultActivity.this,
								R.layout.adapter_search_item, bookList);
						mRefrshListView.setAdapter(mBookAdapter);
						mCurrentMePage++;
					} else {
						tv_search_empty.setVisibility(View.VISIBLE);
						mRefrshListView.setVisibility(View.GONE);
						mTitleBar.setRightLogoVisible();
					}

				} else {
					mCurrentBaiduPage += 2;
					baiduBookList = parseBaiduJson(json);
					dismissRefreshDialog();
					if (baiduBookList != null && !baiduBookList.isEmpty()) {
						tv_search_empty.setVisibility(View.GONE);
						mRefrshListView.setVisibility(View.VISIBLE);
						mTitleBar.setRightLogoGone();
						mBaiduAdapter = new SearchBaiduBookAdapter(
								SearchResultActivity.this,
								R.layout.adapter_search_item, baiduBookList);
						mRefrshListView.setAdapter(mBaiduAdapter);
					} else {
						tv_search_empty.setVisibility(View.VISIBLE);
						mRefrshListView.setVisibility(View.GONE);
						mTitleBar.setRightLogoVisible();
					}
				}
			}
		} else {

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
				mCurrentMePage++;
				if (bookLists != null && bookLists.size() > 0) {
					if (mBookAdapter != null)
						mBookAdapter.addAll(bookLists);
				}
			}
			notifyDataAndRefreshComplete();
		}
	}

	@Override
	public void onSearcFailure(Exception error, String msg, int whoservice) {
		// TODO Auto-generated method stub
		dismissRefreshDialog();
		AnimationUtils.cancelAnim(imgRefersh);
//		Toast.showLong(this, "搜索失败 :" + msg);
		firstReSuccess = whoservice;
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
				mBaiduAdapter.addAll(list);
				mCurrentBaiduPage += 2;
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
				mCurrentBaiduPage = 2;
			}
		}
		notifyDataAndRefreshComplete();
	}

	@Override
	public void onPullToRefreshFailure(Exception error, String msg) {
		// TODO Auto-generated method stub
		Toast.showLong(this, "加载数据错误 ：" + msg);
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
