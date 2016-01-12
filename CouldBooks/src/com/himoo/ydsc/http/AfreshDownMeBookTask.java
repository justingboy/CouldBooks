package com.himoo.ydsc.http;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;

import com.himoo.ydsc.download.DownLoaderTask;
import com.himoo.ydsc.ui.utils.Toast;
import com.himoo.ydsc.util.FileUtils;

public class AfreshDownMeBookTask extends AsyncTask<Void, Void, Void> {

	private Context context;
	private String bookName;
	private String dowmloadUrl;
	private FileUtils fileUtils;
	private File dirFile;
	private File zipFile;
	private OnAfreshDownloadListener mListener;

	public AfreshDownMeBookTask(Context context, String bookName,
			String dowmloadUrl, OnAfreshDownloadListener listner) {
		this.context = context;
		this.bookName = bookName;
		this.dowmloadUrl = dowmloadUrl;
		this.mListener = listner;

	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
		if (mListener != null)
			mListener.onPreDeleted(bookName);
		Toast.show(context, "正在删除《" + bookName+"》");
		fileUtils = new FileUtils(context);
		dirFile = new File(FileUtils.mSdRootPath + "/CouldBook/download"
				+ File.separator + bookName + File.separator);
		zipFile = new File(FileUtils.mSdRootPath + "/CouldBook/download"
				+ File.separator + bookName + ".zip");
	}

	@Override
	protected Void doInBackground(Void... params) {
		// TODO Auto-generated method stub
		if (dirFile != null && dirFile.exists()) {
			fileUtils.deleteFile(dirFile);
		}
		if (zipFile != null && zipFile.exists()) {
			fileUtils.deleteFile(zipFile);
		}

		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		if (mListener != null)
			mListener.onPreDeleted(bookName);
		Toast.show(context, "删除《"+ bookName + "》成功");
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Toast.show(context, "重新下载《" + bookName + "》");
				doDownLoadWork(dowmloadUrl, bookName,mListener);
			}
		}, 2000);

	}

	/**
	 * 下载书籍,自己服务器中的书籍
	 * 
	 * @param downloadUrl
	 * @param bookName
	 */
	private void doDownLoadWork(String downloadUrl, String bookName,OnAfreshDownloadListener listener) {

		String filePath = fileUtils.getStorageDirectory() + bookName + ".zip";
		DownLoaderTask task = new DownLoaderTask(downloadUrl, bookName,
				filePath, (Activity) context,listener,true);
		task.execute();
	}
}
