package com.himoo.ydsc.fragment.subfragment;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.himoo.ydsc.R;
import com.himoo.ydsc.activity.BookDialogActivity;
import com.himoo.ydsc.adapter.BookAdapter;
import com.himoo.ydsc.base.BaseFragment;
import com.himoo.ydsc.bean.Book;
import com.himoo.ydsc.bean.BookDetails;
import com.himoo.ydsc.config.BookTheme;
import com.himoo.ydsc.config.SpConstant;
import com.himoo.ydsc.http.BookRefreshTask;
import com.himoo.ydsc.http.HttpConstant;
import com.himoo.ydsc.http.HttpOperator;
import com.himoo.ydsc.http.OkHttpClientManager;
import com.himoo.ydsc.http.OkHttpClientManager.Param;
import com.himoo.ydsc.listener.OnTaskRefreshListener;
import com.himoo.ydsc.manager.PageManager;
import com.himoo.ydsc.ui.utils.Toast;
import com.himoo.ydsc.ui.utils.UIHelper;
import com.himoo.ydsc.util.NetWorkUtils;
import com.himoo.ydsc.util.SharedPreferences;
import com.himoo.ydsc.util.TimestampUtils;
import com.squareup.okhttp.Request;

/**
 * 精选小说Fragment
 */
public class SubChoiceFragment extends BaseFragment implements
		OnTaskRefreshListener<Book> {

	// 用来下拉刷新的控件
	private PullToRefreshGridView mPullRefreshGridView;
	// 真正用到的控件，它被隐含到PullToRefreshGridView中，所以需要找出来才能使用
	private GridView mGridView;
	/** BookAdapter */
	private BookAdapter mAdapter;
	/** 返回书的列表信息 */
	private ArrayList<Book> mBookList = new ArrayList<Book>();;
	/** 当前的页数 */
	private int currentPage = 1;
	/** 当前点击的Item位置 */
	private int mCurrentClickPosition = -1;
	private ViewStub stub;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			SharedPreferences sp, Bundle savedInstanceState,
			PageManager pageManager) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_sub_book_display,
				container, false);
		Log.d("onCreateView");
		return view;
	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub
		Log.d("initData");
		showRefreshDialog("正在加载中");
		getCouldBookInfoByGet();
		initPTRGrideView();

	}

	/**
	 * 设置下拉刷新的view，设置双向监听器
	 */
	private void initPTRGrideView() {
		// 得到下拉刷新的GridView
		mPullRefreshGridView = (PullToRefreshGridView) findViewById(R.id.pull_refresh_grid);
		mGridView = mPullRefreshGridView.getRefreshableView();
		mGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				if (mCurrentClickPosition != -1)
					return;
				mCurrentClickPosition = position;
				Book book = (Book) parent.getItemAtPosition(position);
				showRefreshDialog("正在加载中");
				getBookDetailsInfo(getActivity(), book.getBook_ID());
			}
		});
		initLastRefreshTime(SpConstant.LAST_REF_TIME_SUBCHOICE,
				mPullRefreshGridView);
		// 设置监听器，这个监听器是可以监听双向滑动的，这样可以触发不同的事件
		mPullRefreshGridView
				.setOnRefreshListener(new OnRefreshListener2<GridView>() {

					@Override
					public void onPullDownToRefresh(
							PullToRefreshBase<GridView> refreshView) {
						String label = "最后更新 ："
								+ TimestampUtils
										.getTimeState(SpConstant.LAST_REF_TIME_SUBCHOICE);
						// Update the LastUpdatedLabel
						mPullRefreshGridView.getLoadingLayoutProxy()
								.setLastUpdatedLabel(label);
						BookRefreshTask<Book> task = new BookRefreshTask<Book>(
								mPullRefreshGridView);
						task.setOnRefreshListener(SubChoiceFragment.this);
						task.execute(1, HttpConstant.BOOK_REQUEST_TYPE_ME);
					}

					@Override
					public void onPullUpToRefresh(
							PullToRefreshBase<GridView> refreshView) {
						BookRefreshTask<Book> task = new BookRefreshTask<Book>(
								mPullRefreshGridView);
						task.setOnRefreshListener(SubChoiceFragment.this);
						task.execute(currentPage,
								HttpConstant.BOOK_REQUEST_TYPE_ME);
					}

				});

	}

	/**
	 * 通过get方式获取服务器的书库信息
	 */
	@SuppressWarnings("static-access")
	private void getCouldBookInfoByGet() {
		mAdapter = new BookAdapter(getActivity(), R.layout.gridview_book_item,
				mBookList);
		String heardParams = HttpOperator.getRequestHeard("", 1, "ydsc8.8",
				"Book_Popularity", 1, 40);

		String url = SharedPreferences.getInstance().getString("host",
				HttpConstant.HOST_URL_TEST)
				+ "getBooksList.asp" + heardParams;
		Log.i("请求地址：" + url);

		OkHttpClientManager.getInstance().getAsyn(url,
				new OkHttpClientManager.ResultCallback<String>() {

					@Override
					public void onError(Request request, Exception e) {
						// TODO Auto-generated method stub
						dismissRefreshDialog();
						stub = (ViewStub) findViewById(R.id.viewstub);
						stub.inflate();
						if (!NetWorkUtils.isNetConnected(getActivity())) {
							Toast.showLong(getActivity(), "网络未连接");
						} else {

							Toast.showLong(getActivity(), "服务器繁忙,请重试");
						}
					}

					@Override
					public void onResponse(String response) {
						// TODO Auto-generated method stub
						try {

							Gson gson = new Gson();
							ArrayList<Book> list = gson.fromJson(response,
									new TypeToken<ArrayList<Book>>() {
									}.getType());
							dismissRefreshDialog();
							mAdapter.addAll(list);
							mGridView.setAdapter(mAdapter);
							mAdapter.notifyDataSetChanged();
							currentPage++;
						} catch (Exception e) {
							dismissRefreshDialog();
							stub = (ViewStub) findViewById(R.id.viewstub);
							stub.inflate();
						}
					}

				});

	}

	/**
	 * 请求自己服务器的书的详情界面信息
	 * 
	 * @param context
	 * @param bookId
	 */
	@SuppressWarnings("static-access")
	private void getBookDetailsInfo(final Context context, int bookId) {

		String url = SharedPreferences.getInstance().getString("host",
				HttpConstant.HOST_URL_TEST)
				+ "getBooksDetail.asp";
		Param params = new Param();
		params.key = "bookID";
		params.value = String.valueOf(bookId);

		OkHttpClientManager.getInstance().postAsyn(url,
				new OkHttpClientManager.ResultCallback<String>() {

					@Override
					public void onError(Request request, Exception e) {
						// TODO Auto-generated method stub
						try {
							Toast.showLong(context, "打开失败，请重试");
							dismissRefreshDialog();
							mCurrentClickPosition = -1;
						} catch (Exception ex) {
							// TODO: handle exception
						}
					}

					@Override
					public void onResponse(String response) {
						// TODO Auto-generated method stub
						try {
							Gson gson = new Gson();
							BookDetails bookDetalis = gson.fromJson(response
									.substring(1, response.length() - 1),
									BookDetails.class);
							dismissRefreshDialog();
							UIHelper.startToActivity(getActivity(),
									bookDetalis, BookDialogActivity.class);
						} catch (Exception ex) {
							Log.e(ex);
							mCurrentClickPosition = -1;
						}
					}

				}, params);

	}

	/**
	 * 通知数据改变，并且数据刷新完毕
	 */
	private void notifyDataAndRefreshComplete() {
		if (mAdapter != null) {
			// 通知数据改变了
			mAdapter.notifyDataSetChanged();
			// 加载完成后停止刷新
		}
		mPullRefreshGridView.onRefreshComplete();

	}

	@Override
	public void onPullUpRefreshSucess(ArrayList<Book> list) {
		// TODO Auto-generated method stub
		currentPage++;
		if (list != null && list.size() > 0) {
			if (mAdapter != null)
				mAdapter.addAll(list);
		}
		notifyDataAndRefreshComplete();

	}

	@Override
	public void onPullDownRefreshSucess(ArrayList<Book> list) {
		// TODO Auto-generated method stub
		if (list != null && list.size() > 0) {
			if (mAdapter != null) {
				if (stub != null)
					stub.setVisibility(View.GONE);
				mAdapter.clear();
				mAdapter.addAll(list);
				mGridView.setAdapter(mAdapter);
				currentPage = 2;
			}
		}
		notifyDataAndRefreshComplete();
	}

	@Override
	public void onPullToRefreshFailure(Exception error, String msg) {
		// TODO Auto-generated method stub
		if (getActivity() != null) {
			// Toast.showLong(getActivity(), "加载数据错误 ：" + msg);
			notifyDataAndRefreshComplete();

		}

	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mCurrentClickPosition = -1;
		if (BookTheme.isThemeChange)
			if (mAdapter != null) {
				mAdapter.afreshDisplayOption();
				mAdapter.notifyDataSetChanged();
			}
	}

	@Override
	public void onDestroy() {
		if (mAdapter != null)
			mAdapter.destory();
		super.onDestroy();
	}
}
