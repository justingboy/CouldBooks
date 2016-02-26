package com.himoo.ydsc.bookdl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.NumberFormat;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;

import com.himoo.ydsc.download.BookDownloadService;
import com.himoo.ydsc.http.OnAfreshDownloadListener;
import com.himoo.ydsc.notification.DownlaodNotification;
import com.himoo.ydsc.ui.utils.Toast;
import com.himoo.ydsc.util.SP;

public class DownLoaderTask2 extends AsyncTask<Void, Integer, Long> {
	private final String TAG = "DownLoaderTask";
	private URL mUrl;
	private File mFile;
	private int mProgress = 0;
	private ProgressReportingOutputStream mOutputStream;
	private String bookName;
	private String bookId;
	private Activity mContext;
	private OnAfreshDownloadListener listener;
	private DownlaodNotification downNotification;
	private NumberFormat mProgressPercentFormat;
	private int lastPro;
	private boolean isNeedSendReceiver = false;

	public DownLoaderTask2(String url, String bookName, String bookId,String out,
			Activity context,boolean isNeedSendReceiver, OnAfreshDownloadListener listener) {
		super();
		mProgressPercentFormat = NumberFormat.getPercentInstance();
		this.listener = listener;
		this.bookName = bookName;
		this.bookId = bookId;
		this.mContext = context;
		this.isNeedSendReceiver = isNeedSendReceiver;
		downNotification = new DownlaodNotification(context);
		try {
			mUrl = new URL(url);
			mFile = new File(out);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		// super.onPreExecute();
		if (downNotification != null) {
			downNotification.creatNotification(bookName, "开始下载");
			downNotification.setProgressAndNetSpeed(0,
					downNotification.getNetSpeed());
		}
	}

	@Override
	protected Long doInBackground(Void... params) {
		// TODO Auto-generated method stub
		
		
		return download();
	}

	@Override
	protected void onPostExecute(Long result) {
		// TODO Auto-generated method stub
		// super.onPostExecute(result);
		if (downNotification != null) {
			// doZipExtractorWork(mContext, mFile.getAbsolutePath(),
			// mFile.getParent()
			// + "/" + bookName);
			downNotification.creatNotification(bookName, "下载完成");
			downNotification.setProgressAndNetSpeed(100,
					downNotification.getNetSpeed());
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					downNotification.notifiManger.cancel(downNotification.NOTIFI_ID);
				}
			}, 1000);
		}
		
		SP.getInstance().putBookDownSuccess(bookName+bookId, true);
		if (listener != null) {
			listener.onPostDownloadSuccess(bookName,bookId);
		}
		Toast.showLong("《" + bookName + "》" + "下载完成");
		DownloadManager.getInstance().deleteTask(bookName, bookId);
		if(isNeedSendReceiver)
		sendDownloadSuccessReceiver(bookName);
		BookDownloadService.getDownloadManager(
				mContext).updateDownSuccess(
						bookName,bookId,true);
		
		if (isCancelled())
			return;

	}

	private long download() {
		URLConnection connection = null;
		int bytesCopied = 0;
		try {
			connection = mUrl.openConnection();
			int length = connection.getContentLength();
			if (mFile.exists() && length == mFile.length()) {
				Log.d(TAG, "file " + mFile.getName() + " already exits!!");
				return 0l;
			}
			mOutputStream = new ProgressReportingOutputStream(mFile,length);
			bytesCopied = copy(connection.getInputStream(), mOutputStream);
			if (bytesCopied != length && length != -1) {
				Log.e(TAG, "Download incomplete bytesCopied=" + bytesCopied
						+ ", length" + length);
			}
			mOutputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		doZipExtractorWork(mContext, mFile.getAbsolutePath(), mFile.getParent()
				+ "/" + bookName+"_"+bookId);
		return bytesCopied;
	}

	private int copy(InputStream input, OutputStream output) {
		byte[] buffer = new byte[1024 * 8];
		BufferedInputStream in = new BufferedInputStream(input, 1024 * 8);
		BufferedOutputStream out = new BufferedOutputStream(output, 1024 * 8);
		int count = 0, n = 0;
		try {
			while ((n = in.read(buffer, 0, 1024 * 8)) != -1) {
				if (isCancelled()) {
					downNotification.notifiManger.cancel(downNotification.NOTIFI_ID);
					SP.getInstance().remove(bookName,bookId);
					return 0;
				}
				out.write(buffer, 0, n);
				count += n;
			}
			out.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				in.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return count;
	}

	private final class ProgressReportingOutputStream extends FileOutputStream {

	
		public int fileCount;
		public ProgressReportingOutputStream(File file,int fileCount)
				throws FileNotFoundException {
			super(file);
			this.fileCount = fileCount;
		}

		@Override
		public void write(byte[] buffer, int byteOffset, int byteCount)
				throws IOException {
			// TODO Auto-generated method stub
			super.write(buffer, byteOffset, byteCount);
			mProgress += byteCount;
			
			double percent = (double) mProgress / (double) fileCount;
			SpannableString tmp = new SpannableString(
					mProgressPercentFormat.format(percent));
			tmp.setSpan(new StyleSpan(android.graphics.Typeface.NORMAL),
					0, tmp.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			int pro = Integer.valueOf(tmp.toString().replace("%", ""));
			if(pro-lastPro>=1)
				downNotification.setProgressAndNetSpeed(pro,
						downNotification.getNetSpeed());
			lastPro = pro;
		}

	}
	
	private void sendDownloadSuccessReceiver(String bookName) {
	    String ACTION = "com.himoo.ydsc.shelf.receiver";
		Intent intent = new Intent(ACTION);
		intent.putExtra("success", true);
		intent.putExtra("bookName", bookName);
		intent.putExtra("bookId", bookId);
		mContext.sendBroadcast(intent);
	}
	
	 /**
	 * 加压zip文件
	 *
	 * @param inPath
	 * @param outPath
	 */
	 public void doZipExtractorWork(Activity activity, String inPath,
	 String outPath) {
	 ZipExtractorTask2 task = new ZipExtractorTask2(bookName, inPath, outPath,
	 activity, true, listener);
	 task.unzip();
	 }

}
