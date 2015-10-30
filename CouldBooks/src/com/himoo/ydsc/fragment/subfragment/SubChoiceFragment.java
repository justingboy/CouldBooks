package com.himoo.ydsc.fragment.subfragment;

import java.util.ArrayList;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.himoo.ydsc.R;
import com.himoo.ydsc.adapter.BookAdapter;
import com.himoo.ydsc.base.BaseFragment;
import com.himoo.ydsc.bean.Book;
import com.himoo.ydsc.config.SpConstant;
import com.himoo.ydsc.http.BookRefreshTask;
import com.himoo.ydsc.http.HttpConstant;
import com.himoo.ydsc.http.HttpOperator;
import com.himoo.ydsc.listener.OnTaskRefreshListener;
import com.himoo.ydsc.manager.PageManager;
import com.himoo.ydsc.ui.utils.Toast;
import com.himoo.ydsc.util.TimestampUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

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
	private void getCouldBookInfoByGet() {
		mAdapter = new BookAdapter(getActivity(), R.layout.gridview_book_item,
				mBookList);
		String heardParams = HttpOperator.getRequestHeard("", 1, "ydsc8.8",
				"Book_Popularity", 1, 40);
		String url = HttpConstant.HOST_URL + heardParams;
		HttpUtils http = new HttpUtils();
		http.send(HttpMethod.GET, url, new RequestCallBack<String>() {

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				// TODO Auto-generated method stub
				Gson gson = new Gson();
				ArrayList<Book> list = gson.fromJson(responseInfo.result,
						new TypeToken<ArrayList<Book>>() {
						}.getType());
				mAdapter.addAll(list);
				mGridView.setAdapter(mAdapter);
				mAdapter.notifyDataSetChanged();
				currentPage++;

			}

			@Override
			public void onFailure(HttpException error, String msg) {
				// TODO Auto-generated method stub
				if (getActivity() != null)
					Toast.showLong(getActivity(), "返回失败 ：" + msg);

			}
		});

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
		Log.i("currentPage =" + currentPage);

	}

	@Override
	public void onPullDownRefreshSucess(ArrayList<Book> list) {
		// TODO Auto-generated method stub
		if (list != null && list.size() > 0) {
			if (mAdapter != null) {
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
			Toast.showLong(getActivity(), "加载数据错误 ：" + msg);
			notifyDataAndRefreshComplete();
		}

	}

}