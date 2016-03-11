package com.himoo.ydsc.http;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.himoo.ydsc.bean.BaiduBookChapter;
import com.himoo.ydsc.bean.BookDetails;
import com.himoo.ydsc.dialog.BookDetailsDialog;
import com.himoo.ydsc.http.OkHttpClientManager.Param;
import com.himoo.ydsc.listener.OnParseChapterListener;
import com.himoo.ydsc.listener.OnRequestCallBack;
import com.himoo.ydsc.reader.utils.ChapterParseUtil;
import com.himoo.ydsc.ui.utils.Toast;
import com.himoo.ydsc.util.FileUtils;
import com.himoo.ydsc.util.SharedPreferences;
import com.squareup.okhttp.Request;

/**
 * 
 * 请求书的详情信息
 */
public class BookDetailsTask {

	private static BookDetailsTask mInstance = null;

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
	@SuppressWarnings("static-access")
	public void excute(final Context context, int bookId) {
		String url = SharedPreferences.getInstance().getString("host",
				HttpConstant.HOST_URL_TEST)
				+ "getBooksDetail.asp";
		Param params = new Param();
		params.key = "bookID";
		params.value = String.valueOf(bookId);
		
		OkHttpClientManager.getInstance().postAsyn(url, new OkHttpClientManager.ResultCallback<String>() {

			@Override
			public void onError(Request request, Exception e) {
				// TODO Auto-generated method stub
				Toast.showLong(context, "打开失败，请重试");
			}

			@Override
			public void onResponse(String response) {
				// TODO Auto-generated method stub
				Gson gson = new Gson();
				BookDetails bookDetalis = gson.fromJson(
						response.substring(1,
								response.length() - 1),
						BookDetails.class);
				new BookDetailsDialog.Builder(context)
						.setBookDetails(bookDetalis).create().show();
			}
			
		}, params);
		
	}
	

	/**
	 * 百度章节列表
	 * 
	 * @param context
	 * @param gid
	 */
	@SuppressWarnings("static-access")
	public void executeBaidu(final Context context, String gid) {

		String url = HttpConstant.BAIDU_BOOK_DETAILS_URL + "appui=alaxs&gid="
				+ gid + "&dir=1&ajax=1";
		OkHttpClientManager.getInstance().getAsyn(url,
				new OkHttpClientManager.ResultCallback<String>() {

					@Override
					public void onError(Request request, Exception e) {
						// TODO Auto-generated method stub
						if (mListener != null)
							mListener.onParseFailure(e, e.getMessage());
					}

					@Override
					public void onResponse(String response) {
						// TODO Auto-generated method stub
						ArrayList<BaiduBookChapter> list = praseBaiduBookChapter(response);
						if (list != null && !list.isEmpty()) {
							// ArrayList<BaiduBookChapter> newList =
							// getNewList(list);
							if (mListener != null) {
								mListener.onParseSuccess(list);
								// list.clear();
								// list = null;
							}
						} else {
							if (mListener != null)
								mListener.onParseFailure(null, "获取数据为空");
						}
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
	 * 通过HttpUrlConnection发送POST请求
	 * 
	 * @param username
	 * @param password
	 * @return
	 */
	public InputStream executeByGet(String urlString) {

		try {
			URL url = new URL(urlString);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5000);
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			int code = conn.getResponseCode();
			if (code == 200) {
				InputStream is = conn.getInputStream();
				return is;
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * get请求返回数据
	 * 
	 * @param urlString
	 * @param callBack
	 */
	public void send(final String urlString, final String bookName,
			final String bookId, final String index,
			final OnRequestCallBack callBack) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				File dirFile = new File(FileUtils.mSdRootPath
						+ "/CouldBook/baidu" + File.separator + bookName + "_"
						+ bookId + File.separator);

				File file = new File(dirFile, index + ".txt");
				if (file.exists() || file.length() > 0) {
					String content = com.himoo.ydsc.download.FileUtils
							.readFormSd(file);
					if (null != callBack) {
						callBack.onSuccess(content);

					}
				} else {
					try {
						URL url = new URL(urlString);
						HttpURLConnection conn = (HttpURLConnection) url
								.openConnection();
						conn.setConnectTimeout(5000);
						conn.setReadTimeout(5000);
						conn.setRequestMethod("GET");
						conn.setRequestProperty("Content-Type",
								"application/x-www-form-urlencoded");
						int code = conn.getResponseCode();
						if (code == 200) {
							InputStream is = conn.getInputStream();
							String result = streamToString(is);
							if (null != callBack) {
								callBack.onSuccess(result);

							}
						} else {
							if (null != callBack) {
								callBack.onFailure("获取数据失败");
							}
						}
					} catch (Exception e) {
						if (null != callBack) {
							callBack.onFailure("获取数据失败");
						}
					}
				}
			}
		}).start();

	}

	/**
	 * 从后台获取数据
	 * 
	 * @return
	 */
	public String getChapterFormService(Context context, String urlString) {
		try {
			URL url = new URL(urlString);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5000);
			conn.setReadTimeout(5000);
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			int code = conn.getResponseCode();
			if (code == 200) {
				InputStream is = conn.getInputStream();
				String result = streamToString(is);
				return ChapterParseUtil.Parse(context, result);
			} else {
				return null;
			}
		} catch (Exception e) {
			return null;
		}

	}

	/**
	 * 从后台获取数据
	 * 
	 * @return
	 */
	public String geLasttChapterFormService(Context context, String urlString) {
		try {
			URL url = new URL(urlString);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5000);
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			int code = conn.getResponseCode();
			if (code == 200) {
				InputStream is = conn.getInputStream();
				return streamToString(is);
			} else {
				return null;
			}
		} catch (Exception e) {
			return null;
		}

	}

	/**
	 * get方式获取String
	 * 
	 * @param urlString
	 * @return
	 */
	public String getStringFormServe(String urlString) {
		try {
			URL url = new URL(urlString);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5000);
			conn.setReadTimeout(5000);
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			int code = conn.getResponseCode();
			if (code == 200) {
				InputStream is = conn.getInputStream();
				return streamToString(is);
			} else {
				return null;
			}
		} catch (Exception e) {
			return null;
		}

	}

	/**
	 * 将流转换成String
	 * 
	 * @param is
	 * @return
	 * @throws IOException
	 */
	public String streamToString(InputStream is) throws IOException {
		if (is != null) {
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(is));
			StringBuffer sb = new StringBuffer();
			String line = "";
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
			return sb.toString();
		}

		return "";
	}

	/**
	 * 去重复的对象
	 * 
	 * @param list
	 * @return
	 */
	public ArrayList<BaiduBookChapter> getNewList(
			ArrayList<BaiduBookChapter> chapterList) {
		HashMap<String, String> map = new HashMap<String, String>();
		for (int i = 0; i < chapterList.size(); i++) {
			String chapterName = chapterList.get(i).getText().trim();
			if (map.get(chapterName) != null) {
				chapterList.remove(i);
			} else {
				map.put(chapterName, "ok");
			}
		}
		map.clear();
		map = null;
		return chapterList; // 返回集合
	}

}
