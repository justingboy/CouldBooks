package com.himoo.ydsc.http;

import android.content.Context;

import com.himoo.ydsc.ui.utils.Toast;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public class HttpOperator {
	
	/**
	 * 轮询验证地址正确后保存
	 */
	public static void getCouldBookInfoByPost(final Context context) {
		HttpUtils http = new HttpUtils();
		RequestParams params = new RequestParams();
		params.addBodyParameter("name", "ydsc8.8");
		http.send(HttpMethod.POST, HttpConstant.BASE_URL1, params,
				new RequestCallBack<String>() {

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						// TODO Auto-generated method stub
						Toast.showLong(context, "返回成功 ："
								+ responseInfo.result);

					}

					@Override
					public void onFailure(HttpException error, String msg) {
						// TODO Auto-generated method stub
						Toast.showLong(context, "返回失败 ：" + msg);
					}
				});
	}
	
	
	/**
	 * 拼接Url 传值参数
	 * 
	 * @param keyClass
	 * @param desc
	 * @param packName
	 * @param order
	 * @param page
	 * @param size
	 * @return
	 */
	public static String getRequestHeard(String keyClass, int desc,
			String packName, String order, int page, int size) {
		StringBuilder sb = new StringBuilder();
		sb.append("?")
				.append("class=")
				.append(keyClass).append("&")
				.append("desc=")
				.append(desc).append("&")
				.append("name=")
				.append(packName).append("&")
				.append("order=")
				.append(order).append("&")
				.append("page=")
				.append(page).append("&")
				.append("size=")
				.append(size);

		return sb.toString();

	}
	
	/**
	 * 拼接百度书库请求头
	 * @param page 页数（0 2 4 6 8）
	 * @param bookType 请求类型  （热搜 、排行）
	 * @return String
	 */
	public static String getBaiduRequestHeard(int page, String bookType) {
		StringBuilder sb = new StringBuilder();
		sb.append("?")
				.append("pn=")
				.append(page).append("&")
				.append("tj=")
				.append(bookType);
		return sb.toString();

	}
	
	/**
	 * 拼接小说关键字请求头
	 * @param page 页数
	 * @param size 关键字的个数
	 * @return String
	 */
	public static String getKeyWordRequestHeard(int page, int size) {
		StringBuilder sb = new StringBuilder();
		sb.append("?")
				.append("type=0")
				.append("&")
				.append("page=")
				.append(page)
				.append("&")
				.append("size=")
				.append(size);
		return sb.toString();

	}
}
