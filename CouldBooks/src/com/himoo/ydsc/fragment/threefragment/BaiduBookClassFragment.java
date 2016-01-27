package com.himoo.ydsc.fragment.threefragment;

import java.util.ArrayList;

import org.json.JSONObject;

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
import com.himoo.ydsc.activity.BaiduDetailsActivity;
import com.himoo.ydsc.adapter.BaiduBookAdapter;
import com.himoo.ydsc.base.BaseFragment;
import com.himoo.ydsc.bean.BaiduBook;
import com.himoo.ydsc.config.BookTheme;
import com.himoo.ydsc.config.SpConstant;
import com.himoo.ydsc.http.BookRefreshTask;
import com.himoo.ydsc.http.HttpConstant;
import com.himoo.ydsc.http.HttpOperator;
import com.himoo.ydsc.listener.OnTaskRefreshListener;
import com.himoo.ydsc.manager.PageManager;
import com.himoo.ydsc.ui.utils.Toast;
import com.himoo.ydsc.ui.utils.UIHelper;
import com.himoo.ydsc.util.NetWorkUtils;
import com.himoo.ydsc.util.SharedPreferences;
import com.himoo.ydsc.util.TimestampUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public class BaiduBookClassFragment extends BaseFragment implements
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
	/** 书的类别ID */
	private String cateid = "1";
	/** 标记当前点击Item的位置 */
	private int mCurrentClickPosition = -1;
	/** 惰性控件，提高效率 */
	private ViewStub stub;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			SharedPreferences sp, Bundle savedInstanceState,
			PageManager pageManager) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_sub_book_display,
				container, false);
		return view;
	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub
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
		mGridView.setNumColumns(2);
		mGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (mCurrentClickPosition != -1)
					return;
				mCurrentClickPosition = position;
				BaiduBook book = (BaiduBook) parent.getItemAtPosition(position);
				UIHelper.startToActivity(getActivity(), book,
						BaiduDetailsActivity.class);
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
						BookRefreshTask<BaiduBook> task = new BookRefreshTask<BaiduBook>(
								mPullRefreshGridView);
						task.setOnRefreshListener(BaiduBookClassFragment.this);
						task.execute(cateid, 0,
								HttpConstant.BOOK_REQUEST_TYPE_BAIDU_CLASSIFY);

					}

					@Override
					public void onPullUpToRefresh(
							PullToRefreshBase<GridView> refreshView) {
						BookRefreshTask<BaiduBook> task = new BookRefreshTask<BaiduBook>(
								mPullRefreshGridView);
						task.setOnRefreshListener(BaiduBookClassFragment.this);
						task.execute(cateid, currentPage,
								HttpConstant.BOOK_REQUEST_TYPE_BAIDU_CLASSIFY);
					}

				});

	}

	/**
	 * 通过get方式获取服务器的书库信息
	 */
	private void getCouldBookInfoByGet() {
		if (getArguments() != null) {
			cateid = getArguments().getString("cateid");

		}
		mAdapter = new BaiduBookAdapter(getActivity(),
				R.layout.gridview_book_item, mBookList);
		String heardParams = HttpOperator.getBaiduClassifyRequestHeard(
				currentPage, cateid);
		String url = HttpConstant.BAIDU_BOOK_CATE_URL + heardParams;
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
						String json = subJsonObject.getString("cate");
						Gson gson = new Gson();
						ArrayList<BaiduBook> list = gson.fromJson(json,
								new TypeToken<ArrayList<BaiduBook>>() {
								}.getType());
						dismissRefreshDialog();
						mAdapter.addAll(list);
						mGridView.setAdapter(mAdapter);
						mAdapter.notifyDataSetChanged();
						currentPage += 2;
					} else {
						dismissRefreshDialog();
						if (getActivity() != null)
							Toast.showLong(getActivity(), "数据库中暂无数据");
					}

				} catch (Exception e) {
					Log.e(e);
					dismissRefreshDialog();
					if (getActivity() != null)
						Toast.showLong(getActivity(), "加载数据失败");

				}

			}

			@Override
			public void onFailure(HttpException error, String msg) {
				// TODO Auto-generated method stub
				Log.e(error + "msg=" + msg);
				dismissRefreshDialog();
				stub = (ViewStub) findViewById(R.id.viewstub);
				stub.inflate();

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
			if (!NetWorkUtils.isNetConnected(getActivity())) {
				Toast.showLong(getActivity(), "网络未连接");
			} else {
				Toast.showLong(getActivity(), "刷新数据失败");
			}
			notifyDataAndRefreshComplete();
		}

	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		mCurrentClickPosition = -1;
		if (BookTheme.isThemeChange)
			if (mAdapter != null)
				mAdapter.notifyDataSetChanged();
		super.onResume();
	}

	@Override
	public void onDestroy() {
		if (mAdapter != null)
			mAdapter.destory();
		super.onDestroy();
	}
}
