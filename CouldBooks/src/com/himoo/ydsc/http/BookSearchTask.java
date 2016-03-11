package com.himoo.ydsc.http;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.himoo.ydsc.http.OkHttpClientManager.Param;
import com.himoo.ydsc.util.SharedPreferences;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.squareup.okhttp.Request;

/**
 * 关键字搜索
 * 
 */
public class BookSearchTask {

	/** 自己的服务器 */
	public static final int OWN = 0;
	/** 百度的服务器 */
	public static final int BAIDU = 1;

	/** 上拉加载更多 */
	public static final int TYPE_PULL_UP_LOAD = 0;

	/** 下拉刷新 */
	public static final int TYPE_PULL_DOWN_UPDATE = 1;

	private static BookSearchTask mInstance = null;

	/** 　xutils 网络请求工具类 */
	public HttpUtils httpBaidu;

	HttpUtils httpMe;
	private OnSearchListener mSearchListener;

	private BookSearchTask() {

	}

	public static BookSearchTask getInstance() {
		if (mInstance == null) {
			synchronized (BookSearchTask.class) {
				if (mInstance == null)
					mInstance = new BookSearchTask();
			}
		}
		return mInstance;
	}

	public void setonSearchListener(OnSearchListener listener) {
		this.mSearchListener = listener;
	}

	/**
	 * 
	 * @param context
	 * @param bookId
	 */
	@SuppressWarnings("static-access")
	public void executeMe(String keyWord, String page,
			final PullToRefreshListView listView, final boolean isFirst) {
		try {
			String url = SharedPreferences.getInstance().getString("host",
					HttpConstant.HOST_URL_TEST)
					+ "getBooksSearch.asp";
			Param[] params = createParam(keyWord, page);
			OkHttpClientManager.getInstance().postAsyn(url,
					new OkHttpClientManager.ResultCallback<String>() {

						@Override
						public void onError(Request request, Exception e) {
							// TODO Auto-generated method stub
							if (mSearchListener != null)
								 mSearchListener.onSearcFailure(e, e.getMessage(), BAIDU);
						}

						@Override
						public void onResponse(String response) {
							// TODO Auto-generated method stub
							if (mSearchListener != null)
								if (listView.getCurrentMode() == Mode.PULL_FROM_START
										|| isFirst)
									mSearchListener.onSearchSucess(
											response, OWN,
											TYPE_PULL_DOWN_UPDATE, isFirst);
								else if (listView.getCurrentMode() == Mode.PULL_FROM_END)
									mSearchListener.onSearchSucess(
											response, OWN,
											TYPE_PULL_UP_LOAD, isFirst);

						}
					}, params);
		} catch (Exception e) {
			// TODO: handle exception
		}

		
	}


	/**
	 * 
	 * @param context
	 * @param bookId
	 */
	@SuppressWarnings("static-access")
	public void executeBaidu(String keyWord, String page) {
		String url = null;
		try {
			url = HttpConstant.BAIDU_BOOK_SEARCH_URL + "pageno="
					+ (20 * Integer.valueOf(page)) + "&keyword="
					+ URLEncoder.encode(keyWord, "utf-8");

			OkHttpClientManager.getInstance().getAsyn(url,
					new OkHttpClientManager.ResultCallback<String>() {

						@Override
						public void onError(Request request, Exception e) {
							// TODO Auto-generated method stub
							if (mSearchListener != null)
								mSearchListener.onSearcFailure(e,
										e.getMessage(), BAIDU);
						}

						@Override
						public void onResponse(String response) {
							// TODO Auto-generated method stub
							if (mSearchListener != null)
								mSearchListener.onSearchSucess(response, BAIDU,
										TYPE_PULL_DOWN_UPDATE, true);
						}

					});

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			if (mSearchListener != null)
				mSearchListener.onSearcFailure(e, "转码错误", BAIDU);
		}
	}

	/**
	 * 配置请求参数
	 * 
	 * @param keyWord
	 * @param page
	 * @return
	 */
	public RequestParams createNameValuePair(String keyWord, String page) {
		RequestParams params = new RequestParams();
		NameValuePair nameValuePair1 = new BasicNameValuePair("desc", "1");
		NameValuePair nameValuePair2 = new BasicNameValuePair("keyword",
				keyWord);
		NameValuePair nameValuePair3 = new BasicNameValuePair("order",
				"Book_Popularity");
		NameValuePair nameValuePair4 = new BasicNameValuePair("page", page);
		NameValuePair nameValuePair5 = new BasicNameValuePair("size", "40");

		ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(nameValuePair1);
		pairs.add(nameValuePair2);
		pairs.add(nameValuePair3);
		pairs.add(nameValuePair4);
		pairs.add(nameValuePair5);

		params.addBodyParameter(pairs);
		return params;
	}

	/**
	 * 配置请求参数
	 * 
	 * @param keyWord
	 * @param page
	 * @return
	 */
	private Param[] createParam(String keyWord, String page) {
		Param[] params = new Param[5];
		String[] keys = { "desc", "keyword", "order", "page", "size" };
		String[] values = { "1", keyWord, "Book_Popularity", page, "40" };
		for (int i = 0; i < params.length; i++) {
			params[i] = new Param(keys[i], values[i]);
		}
		return params;
	}

	public interface OnSearchListener {

		public void onSearchSucess(String json, int whoservice,
				int refreshType, boolean isFirst);

		public void onSearcFailure(Exception error, String msg, int whoservice);

	}
}
