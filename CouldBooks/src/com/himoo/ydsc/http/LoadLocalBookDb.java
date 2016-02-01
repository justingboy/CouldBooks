package com.himoo.ydsc.http;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.AsyncTask;

import com.himoo.ydsc.download.BookDownloadService;
import com.himoo.ydsc.util.MyLogger;

/**
 * 加载本地数据库，内置书
 * 
 */
public class LoadLocalBookDb {

	// 数据库存储路径
	private static String filePath = "data/data/com.himoo.ydsc/databases";
	private static LoadLocalBookDb mInstance = null;

	private LoadLocalBookDb() {
	};

	public static LoadLocalBookDb getInstance() {
		if (mInstance == null)
			synchronized (LoadLocalBookDb.class) {
				if (mInstance == null)
					mInstance = new LoadLocalBookDb();
			}
		return mInstance;
	}

	/**
	 * 加载数据库
	 * 
	 * @param context
	 * @param listener
	 */
	public void loadDb(final Context context,
			final OnLoadDbSuccessListener listener) {
		String url = "http://justingboy.oss-cn-beijing.aliyuncs.com/bookDb/Book.db";
		new BookDbFormNetWork(context, url, listener).execute();

	}

	/**
	 * 服务器上获取失败，则加载本地上的
	 * 
	 * @param context
	 */
	private void loadNetWorkDatabase(InputStream iStream, Context context) {

		try {
			File dir = new File(filePath);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			// 数据库文件
			File jhPath = new File(dir, "Book");
			if (!jhPath.exists()) {
				jhPath.createNewFile();
			}
			// 用输出流写到SDcard上面
			FileOutputStream fos = new FileOutputStream(jhPath);
			// 创建byte数组 用于1KB写一次
			byte[] buffer = new byte[1024];
			int count = 0;
			while ((count = iStream.read(buffer)) > 0) {
				fos.write(buffer, 0, count);
			}
			// 最后关闭就可以了
			fos.flush();
			fos.close();
			iStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			MyLogger.kLog().e(e);
		}
	}

	/**
	 * 服务器上获取失败，则加载本地上的
	 * 
	 * @param context
	 */
	private void loadLocalDatabase(Context context) {

		try {
			File jhPath = new File(filePath);
			// 得到资源
			AssetManager am = context.getAssets();
			// 得到数据库的输入流
			InputStream iStream = am.open("Book.db");
			// 用输出流写到SDcard上面
			FileOutputStream fos = new FileOutputStream(jhPath);
			// 创建byte数组 用于1KB写一次
			byte[] buffer = new byte[1024];
			int count = 0;
			while ((count = iStream.read(buffer)) > 0) {
				fos.write(buffer, 0, count);
			}
			// 最后关闭就可以了
			fos.flush();
			fos.close();
			iStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			MyLogger.kLog().e(e);
		}
	}

	/**
	 * 从阿里云获取数据库
	 * 
	 * @param urlString
	 * @return
	 */
	public InputStream getBookDbFormNetWork(String urlString) {
		try {

			URL url = new URL(urlString);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(1000 * 3);
			conn.setReadTimeout(1000 * 3);
			conn.setRequestMethod("GET");
			if (conn.getResponseCode() == 200) {
				return conn.getInputStream();
			}
		} catch (Exception e) {
			return null;
		}
		return null;
	}

	public class BookDbFormNetWork extends AsyncTask<Void, Void, Void> {
		String url;
		Context context;
		OnLoadDbSuccessListener listener;

		private BookDbFormNetWork(Context context, String url,
				OnLoadDbSuccessListener listener) {
			this.context = context;
			this.url = url;
			this.listener = listener;
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			InputStream iStream = getBookDbFormNetWork(url);
			if (iStream != null)
				loadNetWorkDatabase(iStream, context);
			else
				loadLocalDatabase(context);

			BookDownloadService.getDownloadManager(context).updateAllLastTime();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (listener != null)
				listener.onLoadSuccess();
		}
	}

	public interface OnLoadDbSuccessListener {
		public void onLoadSuccess();
	}

}
