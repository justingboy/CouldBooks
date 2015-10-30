package com.himoo.ydsc.http;

import java.util.ArrayList;

import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.himoo.ydsc.bean.BaiduBook;
import com.himoo.ydsc.bean.Book;
import com.himoo.ydsc.listener.OnTaskRefreshListener;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public class BookRefreshTask<T> {

	/** PullToRefreshGridView */
	private PullToRefreshGridView mGridView;

	/** 刷新接口 */
	private OnTaskRefreshListener<T> mListener = null;

	/** 设置上拉和下拉触发的监听接口 */
	public void setOnRefreshListener(OnTaskRefreshListener<T> listener) {
		this.mListener = listener;
	}

	public BookRefreshTask(PullToRefreshGridView gridView) {
		this.mGridView = gridView;

	}

	/**
	 * 执行刷新任务
	 * 
	 * @param nextPage
	 *            刷新的页数
	 * @param bookRequestTyep
	 *            请求的类型
	 */
	public void execute(int nextPage, final int bookRequestTyep) {
		// 请求书库地址
		String url = getRequestBookUrl(nextPage, bookRequestTyep);
		HttpUtils http = new HttpUtils();
		http.send(HttpMethod.GET, url, new RequestCallBack<String>() {

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				// TODO Auto-generated method stub
				Gson gson = new Gson();
				ArrayList<T> bookList = null;
				if (bookRequestTyep == HttpConstant.BOOK_REQUEST_TYPE_ME) {
					bookList = gson.fromJson(responseInfo.result,
							new TypeToken<ArrayList<Book>>() {
							}.getType());
				} else if (bookRequestTyep == HttpConstant.BOOK_REQUEST_TYPE_BAIDU_RANKING) {
					bookList = parseRankingResult(gson, responseInfo);

				} else if (bookRequestTyep == HttpConstant.BOOK_REQUEST_TYPE_BAIDU_HOTSEARCH) {
					bookList = parseHotSearchResult(gson, responseInfo);
				}

				if (mListener != null)
					if (mGridView != null
							&& mGridView.getCurrentMode() == Mode.PULL_FROM_START)
						mListener.onPullDownRefreshSucess(bookList);
					else if (mGridView != null
							&& mGridView.getCurrentMode() == Mode.PULL_FROM_END)
						mListener.onPullUpRefreshSucess(bookList);

			}

			@Override
			public void onFailure(HttpException error, String msg) {
				// TODO Auto-generated method stub
				if (mListener != null)
					mListener.onPullToRefreshFailure(error, msg);
			}
		});

	}

	/**
	 * 获取请求书库的地址Url
	 * 
	 * @param nextPage
	 * @param bookRequestTyep
	 * @return
	 */
	private String getRequestBookUrl(int nextPage, int bookRequestTyep) {
		String url = "";
		switch (bookRequestTyep) {
		case HttpConstant.BOOK_REQUEST_TYPE_ME:
			url = getCouldRequestUrl(nextPage);
			break;
		case HttpConstant.BOOK_REQUEST_TYPE_BAIDU_RANKING:
			url = getBaiduRankingRequestUrl(nextPage);
			break;
		case HttpConstant.BOOK_REQUEST_TYPE_BAIDU_HOTSEARCH:
			url = getBaiduHotSearchRequestUrl(nextPage);
			break;

		default:
			break;
		}
		return url;

	}

	/**
	 * 解析排行小说的内容
	 * 
	 * @param gson
	 * @param responseInfo
	 * @return
	 */
	private ArrayList<T> parseRankingResult(Gson gson,
			ResponseInfo<String> responseInfo) {
		try {
			JSONObject jsonObject = new JSONObject(responseInfo.result);
			if (jsonObject.getInt("errno") == 0
					&& jsonObject.get("errmsg").equals("ok")) {
				JSONObject subJsonObject = jsonObject.getJSONObject("result");
				String json = subJsonObject.getString("rank");
				return gson.fromJson(json,
						new TypeToken<ArrayList<BaiduBook>>() {
						}.getType());

			}
		} catch (Exception e) {
			// TODO: handle exception
			if (mListener != null)
				mListener.onPullToRefreshFailure(e, "服务器暂无数据");
		}
		return null;
	}

	/**
	 * 解析热搜小说的内容
	 * 
	 * @param gson
	 * @param responseInfo
	 * @return
	 */
	public ArrayList<T> parseHotSearchResult(Gson gson,
			ResponseInfo<String> responseInfo) {
		try {
			JSONObject jsonObject = new JSONObject(responseInfo.result);
			if (jsonObject.getInt("errno") == 0
					&& jsonObject.get("errmsg").equals("ok")) {
				JSONObject subJsonObject = jsonObject.getJSONObject("result");
				String json = subJsonObject.getString("recommend");
				return gson.fromJson(json,
						new TypeToken<ArrayList<BaiduBook>>() {
						}.getType());

			}
		} catch (Exception e) {
			// TODO: handle exception
			if (mListener != null)
				mListener.onPullToRefreshFailure(e, "服务器暂无数据");
		}
		return null;
	}

	/**
	 * 获取自己服务器地址
	 * 
	 * @param page
	 * @return
	 */
	private String getCouldRequestUrl(int page) {
		String heardParams = HttpOperator.getRequestHeard("", 1, "ydsc8.8",
				"Book_Popularity", page, 40);
		String url = HttpConstant.HOST_URL + heardParams;
		return url;
	}

	/**
	 * 获取百度排行请求地址
	 * 
	 * @param page
	 * @return
	 */
	private String getBaiduRankingRequestUrl(int page) {
		String heardParams = HttpOperator.getBaiduRequestHeard(page,
				"book_rank_b5");
		String url = HttpConstant.BAIDU_RANKINGF_URL + heardParams;
		return url;
	}

	/**
	 * 获取百度热搜请求地址
	 * 
	 * @param page
	 * @return
	 */
	private String getBaiduHotSearchRequestUrl(int page) {
		String heardParams = HttpOperator.getBaiduRequestHeard(page,
				"book_recommend_b5");
		String url = HttpConstant.BAIDU_HOTSEARCH_URL + heardParams;
		return url;
	}

}
