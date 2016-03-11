package com.himoo.ydsc.http;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.json.JSONObject;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.himoo.ydsc.bean.BaiduBook;
import com.himoo.ydsc.bean.Book;
import com.himoo.ydsc.listener.OnTaskRefreshListener;
import com.himoo.ydsc.util.SharedPreferences;
import com.squareup.okhttp.Request;

public class BookRefreshTask<T> {

	/** PullToRefreshGridView */
	private PullToRefreshBase<?> mGridView;

	/** 刷新接口 */
	private OnTaskRefreshListener<T> mListener = null;

	/** 设置上拉和下拉触发的监听接口 */
	public void setOnRefreshListener(OnTaskRefreshListener<T> listener) {
		this.mListener = listener;
	}

	public BookRefreshTask(PullToRefreshBase<?> gridView) {
		this.mGridView = gridView;

	}

	/**
	 * 重载方法，用于精选界面数据的上拉加载下拉刷新
	 * 
	 * @param nextPage
	 * @param bookRequestTyep
	 */
	public void execute(int nextPage, final int bookRequestTyep) {
		this.execute("", nextPage, bookRequestTyep);
	}

	/**
	 * 执行刷新任务 用于分类Fragment 刷新数据
	 * 
	 * @param nextPage
	 *            刷新的页数
	 * @param bookRequestTyep
	 *            请求的类型
	 */
	@SuppressWarnings("static-access")
	public void execute(String classId, int nextPage, final int bookRequestTyep) {
		// 请求书库地址
		String url = getRequestBookUrl(classId, nextPage, bookRequestTyep);
		Log.i("msg", url);
		OkHttpClientManager.getInstance().getAsyn(url,
				new OkHttpClientManager.ResultCallback<String>() {

					@Override
					public void onError(Request request, Exception e) {
						// TODO Auto-generated method stub
						if (mListener != null)
							mListener.onPullToRefreshFailure(e, e.getMessage());
					}

					@Override
					public void onResponse(String response) {
						// TODO Auto-generated method stub
						Gson gson = new Gson();
						ArrayList<T> bookList = null;
						if (bookRequestTyep == HttpConstant.BOOK_REQUEST_TYPE_ME) {
							bookList = gson.fromJson(response,
									new TypeToken<ArrayList<Book>>() {
									}.getType());
						} else if (bookRequestTyep == HttpConstant.BOOK_REQUEST_TYPE_BAIDU_RANKING) {
							bookList = parseBaiduBook(gson, response, "rank");

						} else if (bookRequestTyep == HttpConstant.BOOK_REQUEST_TYPE_BAIDU_HOTSEARCH) {
							bookList = parseBaiduBook(gson, response, "recommend");
						} else if (bookRequestTyep == HttpConstant.BOOK_REQUEST_TYPE_BAIDU_CLASSIFY) {
							bookList = parseBaiduBook(gson, response, "cate");
						} else if (bookRequestTyep == HttpConstant.BOOK_REQUEST_TYPE_BAIDU_SEARCH) {
							bookList = parseBaiduBook(gson, response, "search");
						}

						if (mListener != null)
							if (mGridView != null
									&& mGridView.getCurrentMode() == Mode.PULL_FROM_START)
								mListener.onPullDownRefreshSucess(bookList);
							else if (mGridView != null
									&& mGridView.getCurrentMode() == Mode.PULL_FROM_END)
								mListener.onPullUpRefreshSucess(bookList);
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
	private String getRequestBookUrl(String classId, int nextPage,
			int bookRequestTyep) {
		String url = "";
		switch (bookRequestTyep) {
		case HttpConstant.BOOK_REQUEST_TYPE_ME:
			url = getCouldRequestUrl(classId, nextPage);
			break;
		case HttpConstant.BOOK_REQUEST_TYPE_BAIDU_RANKING:
			url = getBaiduRankingRequestUrl(nextPage);
			break;
		case HttpConstant.BOOK_REQUEST_TYPE_BAIDU_HOTSEARCH:
			url = getBaiduHotSearchRequestUrl(nextPage);
			break;
		case HttpConstant.BOOK_REQUEST_TYPE_BAIDU_CLASSIFY:
			url = getBaiduClassifyRequestUrl(classId, nextPage);
			break;
		case HttpConstant.BOOK_REQUEST_TYPE_BAIDU_SEARCH:
			url = getBaiduSearchRequestUrl(classId, nextPage);
			break;

		default:
			break;
		}
		return url;

	}

	/**
	 * 解析分类界面百度小说的内容
	 * 
	 * @param gson
	 * @param responseInfo
	 * @return
	 */
	public ArrayList<T> parseBaiduBook(Gson gson,
			String responseInfo, String keyRequest) {
		try {
			JSONObject jsonObject = new JSONObject(responseInfo);
			if (jsonObject.getInt("errno") == 0
					&& jsonObject.get("errmsg").equals("ok")) {
				JSONObject subJsonObject = jsonObject.getJSONObject("result");
				String json = subJsonObject.getString(keyRequest);
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
	private String getCouldRequestUrl(String classId, int page) {
		String heardParams = HttpOperator.getRequestHeard(classId, 1,
				"ydsc8.8", "Book_Popularity", page, 40);
		String header = SharedPreferences.getInstance().getString("host",
				HttpConstant.HOST_URL_TEST);
		String url = header + "getBooksList.asp" + heardParams;
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

	/**
	 * 获取百度热搜请求地址
	 * 
	 * @param page
	 * @return
	 */
	private String getBaiduClassifyRequestUrl(String classId, int page) {

		String heardParams = HttpOperator.getBaiduClassifyRequestHeard(page,
				classId);
		String url = HttpConstant.BAIDU_BOOK_CATE_URL + heardParams;
		return url;
	}

	/**
	 * 获取百度搜索请求地址
	 * 
	 * @param page
	 * @return
	 */
	private String getBaiduSearchRequestUrl(String keyWord, int page) {

		String url = null;
		try {
			url = HttpConstant.BAIDU_BOOK_SEARCH_URL + "pageno=" + page
					+ "&keyword=" + URLEncoder.encode(keyWord, "utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return url;
	}

}
