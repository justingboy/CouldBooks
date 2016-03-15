package com.himoo.ydsc.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.himoo.ydsc.R;
import com.himoo.ydsc.adapter.HotwordsAdapter;
import com.himoo.ydsc.bean.BookKeyWord;
import com.himoo.ydsc.http.HttpConstant;
import com.himoo.ydsc.http.HttpOperator;
import com.himoo.ydsc.http.OkHttpClientManager;
import com.himoo.ydsc.ui.swipebacklayout.SwipeBackActivity;
import com.himoo.ydsc.ui.utils.Toast;
import com.himoo.ydsc.util.NetWorkUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.squareup.okhttp.Request;

public class HotwordsActivity extends SwipeBackActivity implements
		OnItemClickListener, OnRefreshListener2<GridView> {

	@ViewInject(R.id.gridView_hotword)
	private PullToRefreshGridView gridView_hotword;

	/** 请求关键字的的页数 */
	private int mCurrentPage = 1;
	private int mCurrentPosition = -1;

	private HotwordsAdapter mAdapter;

	/** 用于判断是否是刷新 */
	private boolean isRefresh = false;

	/** 每次加载关键字的个数 */
	private static final int KEYWORD_COUNT = 40;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hotwords);
		showRefreshDialog("  正在加载  ");
		getKeyWordRequest(mCurrentPage, KEYWORD_COUNT);
	}

	@Override
	protected void initTitleBar() {
		// TODO Auto-generated method stub
		mTitleBar.setLeftTitle("返回");
		mTitleBar.setTitle("更多搜索热词");
		mTitleBar.setRightLogoGone();
	}

	public void initAdapter(List<String> list) {
		mAdapter = new HotwordsAdapter(this, R.layout.adapter_hotwords, list);
		gridView_hotword.setAdapter(mAdapter);
		gridView_hotword.setOnItemClickListener(this);
		gridView_hotword.setOnRefreshListener(this);
	}

	private void loadMore(List<String> list) {
		mAdapter.addAll(list);
		notifyDataAndRefreshComplete();
	}

	/**
	 * 请求关键字
	 * 
	 * @param page
	 * @param size
	 *            请求的个数
	 */
	@SuppressWarnings("static-access")
	private void getKeyWordRequest(int page, int size) {
		String url = HttpConstant.BASE_URL_KEYWORD
				+ HttpOperator.getKeyWordRequestHeard(page, size);

		OkHttpClientManager.getInstance().getAsyn(url,
				new OkHttpClientManager.ResultCallback<String>() {

					@Override
					public void onError(Request request, Exception e) {
						// TODO Auto-generated method stub
						Log.e(e);
						dismissRefreshDialog();
						if (NetWorkUtils.isNetConnected(HotwordsActivity.this)) {
							Toast.showBg(HotwordsActivity.this, "加载关键字失败 ");
						} else {
							Toast.showBg(HotwordsActivity.this, "未连接网络");
						}

					}

					@Override
					public void onResponse(String response) {
						// TODO Auto-generated method stub
						Gson gson = new Gson();
						ArrayList<BookKeyWord> list = gson.fromJson(response,
								new TypeToken<ArrayList<BookKeyWord>>() {
								}.getType());
						if (list != null && !list.isEmpty()) {
							List<String> data = new ArrayList<String>();
							for (int i = 0; i < list.size(); i++) {
								data.add(list.get(i).getKeyword());
							}
							if (isRefresh) {
								if (mAdapter != null) {
									mAdapter.clear();
									mAdapter.addAll(data);
									notifyDataAndRefreshComplete();
								}
								isRefresh = false;
							} else {
								if (mAdapter == null) {
									initAdapter(data);
									dismissRefreshDialog();
								} else {
									loadMore(data);

								}
							}
							mCurrentPage++;

						} else {
							notifyDataAndRefreshComplete();
							Toast.showBg(HotwordsActivity.this, "加载关键字失败");
						}
					}

				});

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		if (mCurrentPosition != -1)
			return;
		mCurrentPosition = position;

		if (NetWorkUtils.isNetConnected(this)) {
			String keyWord = (String) parent.getItemAtPosition(position);
			startToActivity(keyWord);
		} else {
			Toast.showBg(this, "未连接网络");
		}

	}

	/**
	 * 跳转到搜索结果的界面
	 * 
	 * @param keyWord
	 */
	protected void startToActivity(String keyWord) {
		Intent intent = new Intent(this, SearchResultActivity.class);
		intent.putExtra("keyWord", keyWord);
		startActivity(intent);
		overridePendingTransition(R.anim.activity_zoom_in, 0);

	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {
		// TODO Auto-generated method stub
		isRefresh = true;
		mCurrentPage = 1;
		getKeyWordRequest(mCurrentPage, KEYWORD_COUNT);
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView) {
		// TODO Auto-generated method stub
		getKeyWordRequest(mCurrentPage, KEYWORD_COUNT);

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
		gridView_hotword.onRefreshComplete();

	}


	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (mAdapter != null)
			mAdapter.clear();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		mCurrentPosition = -1;
		super.onResume();
	}
}
