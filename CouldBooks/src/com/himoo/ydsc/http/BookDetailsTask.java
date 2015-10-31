package com.himoo.ydsc.http;

import android.content.Context;

import com.himoo.ydsc.ui.utils.Toast;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

/**
 * 
 * 请求书的详情信息
 */
public class BookDetailsTask {

	private static BookDetailsTask mInstance = null;
	/** 　Xutils 网络请求工具类 */
	public HttpUtils http;

	private BookDetailsTask() {

	}

	public static BookDetailsTask getInstance() {
		if (mInstance == null) {
			synchronized (BookDetailsTask.class) {

				if (mInstance == null)
					mInstance = new BookDetailsTask();
			}
		}
		return mInstance;
	}

	public void excute(final Context context, String value) {
		if (http == null) {
			http = new HttpUtils();
		}
		RequestParams params = new RequestParams();
		params.addBodyParameter("bookID ", value);
		http.send(HttpMethod.POST, HttpConstant.HOST_URL, params,
				new RequestCallBack<String>() {

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						// TODO Auto-generated method stub

						Toast.showLong(context, responseInfo.result);
					}

					@Override
					public void onFailure(HttpException error, String msg) {
						// TODO Auto-generated method stub
						Toast.showLong(context, "获取详情失败：" + msg);
					}

				});
	}

}
