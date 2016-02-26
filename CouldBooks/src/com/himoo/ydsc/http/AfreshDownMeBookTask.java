package com.himoo.ydsc.http;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;

import com.himoo.ydsc.bookdl.DownLoaderTask2;
import com.himoo.ydsc.bookdl.DownloadManager;
import com.himoo.ydsc.ui.utils.Toast;
import com.himoo.ydsc.util.FileUtils;

public class AfreshDownMeBookTask extends AsyncTask<Void, Void, Void> {

	private Context context;
	private String bookName;
	private String bookId;
	private String dowmloadUrl;
	private FileUtils fileUtils;
	private File dirFile;
	private File zipFile;
	private OnAfreshDownloadListener mListener;
	private boolean isBookFileExist = false;
	private boolean isNeedSendReceiver = false;
	
	
	public AfreshDownMeBookTask(Context context, String bookName,
			String bookId, String dowmloadUrl,boolean isNeedSendReceiver, OnAfreshDownloadListener listner) {
		this.context = context;
		this.bookName = bookName;
		this.bookId = bookId;
		this.dowmloadUrl = dowmloadUrl;
		this.isNeedSendReceiver = isNeedSendReceiver;
		this.mListener = listner;

	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
		if (mListener != null)
			mListener.onPreDeleted(bookName,bookId);
		fileUtils = new FileUtils(context);
		dirFile = new File(FileUtils.mSdRootPath + "/CouldBook/download"
				+ File.separator + bookName + "_" + bookId + File.separator);
		if (dirFile.exists()) {
			isBookFileExist = true;
			Toast.show(context, "正在删除《" + bookName + "》");
		}
		zipFile = new File(FileUtils.mSdRootPath + "/CouldBook/download"
				+ File.separator + bookName + "_" + bookId + ".zip");
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
			mListener.onPreDeleted(bookName,bookId);
		if (isBookFileExist)
			Toast.show(context, "删除《" + bookName + "》成功");
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (isBookFileExist)
					Toast.show(context, "重新下载《" + bookName + "》");
				doDownLoadWork(dowmloadUrl, bookName, bookId,context,isNeedSendReceiver, mListener);
			}
		}, 2000);

	}

	/**
	 * 下载书籍
	 * 
	 * @param downloadUrl
	 * @param bookName
	 */
	private void doDownLoadWork(String downloadUrl, String bookName,
			String bookId, Context context,boolean isNeedSendReceiver,OnAfreshDownloadListener listener) {

		File file = new File(fileUtils.getStorageDirectory());
		if (file != null && !file.exists())
			file.mkdirs();
		String filePath = fileUtils.getStorageDirectory() + bookName + "_"
				+ bookId + ".zip";
		DownLoaderTask2 task = new DownLoaderTask2(downloadUrl, bookName,
				bookId, filePath, (Activity) context,false, listener);
		DownloadManager.getInstance().addTask(bookName, bookId, task);
		task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

}
