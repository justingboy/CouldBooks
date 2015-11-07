package com.himoo.ydsc.fragment.subfragment;

import java.util.ArrayList;

import org.json.JSONObject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.AdapterView.OnItemClickListener;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.himoo.ydsc.R;
import com.himoo.ydsc.activity.BaiduDetailsActivity;
import com.himoo.ydsc.adapter.BaiduBookAdapter;
import com.himoo.ydsc.base.BaseFragment;
import com.himoo.ydsc.bean.BaiduBook;
import com.himoo.ydsc.config.SpConstant;
import com.himoo.ydsc.http.BookRefreshTask;
import com.himoo.ydsc.http.HttpConstant;
import com.himoo.ydsc.http.HttpOperator;
import com.himoo.ydsc.listener.OnTaskRefreshListener;
import com.himoo.ydsc.manager.PageManager;
import com.himoo.ydsc.ui.utils.Toast;
import com.himoo.ydsc.ui.utils.UIHelper;
import com.himoo.ydsc.util.SharedPreferences;
import com.himoo.ydsc.util.TimestampUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

/**
 * 排行小说Fragment
 * 
 */
public class SubHotSearchFragment extends BaseFragment implements
		OnTaskRefreshListener<BaiduBook> {

	// 用来下拉刷新的控件
	private PullToRefreshGridView mPullRefreshGridView;
	// 真正用到的控件，它被隐含到PullToRefreshGridView中，所以需要找出来才能使用
	private GridView mGridView;
	/** BookAdapter */
	private BaiduBookAdapter mAdapter;
	/** 返回书的列表信息 */
	private ArrayList<BaiduBook> mBookList = new ArrayList<BaiduBook>();;
	/** 当前的页数 */
	private int currentPage = 0;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			SharedPreferences sp, Bundle savedInstanceState,
			PageManager pageManager) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_sub_book_display, null);
		Log.d("onCreateView");
		return view;
	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub
		Log.d("initData");
		getBaiBookInfoByGet();
		initPTRGrideView();

	}

	/**
	 * 设置下拉刷新的view，设置双向监听器
	 */
	private void initPTRGrideView() {
		// 得到下拉刷新的GridView
		mPullRefreshGridView = (PullToRefreshGridView) findViewById(R.id.pull_refresh_grid);
		mGridView = mPullRefreshGridView.getRefreshableView();
		initLastRefreshTime(SpConstant.LAST_REF_TIME_SUBHOTSEARCH,
				mPullRefreshGridView);
		mGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				BaiduBook book = (BaiduBook) parent.getItemAtPosition(position);
				UIHelper.startToActivity(getActivity(), book,
						BaiduDetailsActivity.class);
			}
		});

		// 设置监听器，这个监听器是可以监听双向滑动的，这样可以触发不同的事件
		mPullRefreshGridView
				.setOnRefreshListener(new OnRefreshListener2<GridView>() {

					@Override
					public void onPullDownToRefresh(
							PullToRefreshBase<GridView> refreshView) {
						String label = "最后更新 ："
								+ TimestampUtils
										.getTimeState(SpConstant.LAST_REF_TIME_SUBHOTSEARCH);
						// Update the LastUpdatedLabel
						mPullRefreshGridView.getLoadingLayoutProxy()
								.setLastUpdatedLabel(label);
						BookRefreshTask<BaiduBook> task = new BookRefreshTask<BaiduBook>(
								mPullRefreshGridView);
						task.setOnRefreshListener(SubHotSearchFragment.this);
						task.execute(0,
								HttpConstant.BOOK_REQUEST_TYPE_BAIDU_HOTSEARCH);
					}

					@Override
					public void onPullUpToRefresh(
							PullToRefreshBase<GridView> refreshView) {
						BookRefreshTask<BaiduBook> task = new BookRefreshTask<BaiduBook>(
								mPullRefreshGridView);
						task.setOnRefreshListener(SubHotSearchFragment.this);
						task.execute(currentPage,
								HttpConstant.BOOK_REQUEST_TYPE_BAIDU_HOTSEARCH);
					}

				});

	}

	/**
	 * 通过get方式获取服务器的书库信息
	 */
	private void getBaiBookInfoByGet() {
		mAdapter = new BaiduBookAdapter(getActivity(),
				R.layout.gridview_book_item, mBookList);
		String url = HttpConstant.BAIDU_HOTSEARCH_URL
				+ HttpOperator.getBaiduRequestHeard(0,
						HttpConstant.BAIDU_HOTSEARCH_URL);
		HttpUtils http = new HttpUtils();
		http.send(HttpMethod.GET, url, new RequestCallBack<String>() {

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				// TODO Auto-generated method stub
				try {
					JSONObject jsonObject = new JSONObject(responseInfo.result);
					if (jsonObject.getInt("errno") == 0
							&& jsonObject.get("errmsg").equals("ok")) {
						JSONObject subJsonObject = jsonObject
								.getJSONObject("result");
						String json = subJsonObject.getString("recommend");
						Gson gson = new Gson();
						ArrayList<BaiduBook> list = gson.fromJson(json,
								new TypeToken<ArrayList<BaiduBook>>() {
								}.getType());
						mAdapter.addAll(list);
						mGridView.setAdapter(mAdapter);
						mAdapter.notifyDataSetChanged();
						currentPage += 2;
					} else {
						if (getActivity() != null)
							Toast.showLong(getActivity(), "数据库中暂无数据");
					}

				} catch (Exception e) {
					if (getActivity() != null)
						Toast.showLong(getActivity(), "加载数据失败");

				}

			}

			@Override
			public void onFailure(HttpException error, String msg) {
				// TODO Auto-generated method stub
				if (getActivity() != null)
					Toast.showLong(getActivity(), "返回失败 ：" + msg);

			}
		});

	}

	@Override
	public void onPullUpRefreshSucess(ArrayList<BaiduBook> list) {
		// TODO Auto-generated method stub
		currentPage += 2;
		if (list != null && list.size() > 0) {
			if (mAdapter != null)
				mAdapter.addAll(list);
		}
		notifyDataAndRefreshComplete();
		Log.i("currentPage =" + currentPage);

	}

	@Override
	public void onPullDownRefreshSucess(ArrayList<BaiduBook> list) {
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
		Toast.showLong(getActivity(), "加载数据错误 ：" + msg);
		notifyDataAndRefreshComplete();
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

}
