package com.himoo.ydsc.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.himoo.ydsc.bean.BaiduBookChapter;
import com.himoo.ydsc.bean.BookDetails;
import com.himoo.ydsc.dialog.BookDetailsDialog;
import com.himoo.ydsc.listener.OnParseChapterListener;
import com.himoo.ydsc.ui.utils.Toast;
import com.himoo.ydsc.util.MyLogger;
import com.himoo.ydsc.util.SharedPreferences;
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

	private OnParseChapterListener mListener;

	public void setOnParseChapterListener(OnParseChapterListener listener) {
		this.mListener = listener;
	}

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

	/**
	 * 请求自己服务器的书的详情界面信息
	 * 
	 * @param context
	 * @param bookId
	 */
	public void excute(final Context context, int bookId) {
		if (http == null) {
			http = new HttpUtils();
		}
		RequestParams params = new RequestParams();
		NameValuePair nameValuePair = new BasicNameValuePair("bookID",
				String.valueOf(bookId));
		params.addBodyParameter(nameValuePair);
		String url = SharedPreferences.getInstance().getString("host",
				HttpConstant.HOST_URL_TEST)
				+ "getBooksDetail.asp";
		http.send(HttpMethod.POST, url, params, new RequestCallBack<String>() {

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				// TODO Auto-generated method stub
				Gson gson = new Gson();
				BookDetails bookDetalis = gson.fromJson(
						responseInfo.result.substring(1,
								responseInfo.result.length() - 1),
						BookDetails.class);
				new BookDetailsDialog.Builder(context)
						.setBookDetails(bookDetalis).create().show();
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				// TODO Auto-generated method stub
				Toast.showLong(context, "获取详情失败：" + msg);
			}

		});
	}

	/**
	 * 请求自己服务器的书的详情界面信息
	 * 
	 * @param context
	 * @param bookId
	 */
	public void executeBaidu(final Context context, String gid) {
		if (http == null) {
			http = new HttpUtils();
		}

		String url = HttpConstant.BAIDU_BOOK_DETAILS_URL + "appui=alaxs&gid="
				+ gid + "&dir=1&ajax=1";
		http.send(HttpMethod.GET, url, new RequestCallBack<String>() {

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				// TODO Auto-generated method stub
				ArrayList<BaiduBookChapter> list = praseBaiduBookChapter(responseInfo.result);
				if (list != null && list.size() > 0) {
					if (mListener != null)
						mListener.onParseSuccess(list);
				} else {
					if (mListener != null)
						mListener.onParseFailure(null, "获取数据为空");
				}
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				// TODO Auto-generated method stub
				if (mListener != null)
					mListener.onParseFailure(error, msg);
			}

		});
	}

	/**
	 * 解析百度章节信息
	 * 
	 * @param jsonString
	 * @return
	 */
	private ArrayList<BaiduBookChapter> praseBaiduBookChapter(String jsonString) {
		try {
			Gson gson = new Gson();
			JSONObject jsonObject = new JSONObject(jsonString);
			if (jsonObject.getInt("status") == 1) {
				JSONObject subJsonObject = jsonObject.getJSONObject("data");
				String json = subJsonObject.getString("group");
				return gson.fromJson(json,
						new TypeToken<ArrayList<BaiduBookChapter>>() {
						}.getType());

			}
		} catch (Exception e) {
			// TODO: handle exception
			if (mListener != null)
				mListener.onParseFailure(e, "服务器暂无数据");
		}
		return null;

	}

	/**
	 * post方式获取书的详请信息
	 * 
	 * @param context
	 */
	public void requestBookDetails(Context context, int bookId) {

		new BookAsyncTask(context).execute(bookId);
	}

	private class BookAsyncTask extends AsyncTask<Integer, Void, String> {
		private Context context;

		public BookAsyncTask(Context context) {
			this.context = context;
		}

		@Override
		protected String doInBackground(Integer... params) {
			// TODO Auto-generated method stub
			return executeByPost(params[0]);
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			Toast.showLong(context, result);
		}
	}

	/**
	 * 通过HttpUrlConnection发送POST请求
	 * 
	 * @param username
	 * @param password
	 * @return
	 */
	private String executeByPost(int bookId) {

		String path = HttpConstant.BASE_URL_BOOK_DETAILS;
		try {
			URL url = new URL(path);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5000);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			String data = "bookID=" + bookId;
			conn.setRequestProperty("Content-Length", data.length() + "");
			// POST方式，其实就是浏览器把数据写给服务器
			conn.setDoOutput(true); // 设置可输出流
			OutputStream os = conn.getOutputStream(); // 获取输出流
			os.write(data.getBytes()); // 将数据写给服务器
			int code = conn.getResponseCode();
			if (code == 200) {
				InputStream is = conn.getInputStream();
				return streamToString(is);
			} else {
				return "网络访问失败";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "网络访问失败";
		}
	}

	/**
	 * 将流转换成String
	 * 
	 * @param is
	 * @return
	 */
	private String streamToString(InputStream is) {
		if (is != null) {
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(is));
			StringBuffer sb = new StringBuffer();
			String line;
			try {
				while ((line = reader.readLine()) != null) {
					sb.append(line);

				}
				return sb.toString();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				MyLogger.kLog().e(e);
				return "";
			}
		}

		return "";
	}
}
