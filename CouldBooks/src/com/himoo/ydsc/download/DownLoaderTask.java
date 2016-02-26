package com.himoo.ydsc.download;

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

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.os.AsyncTask;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;

import com.himoo.ydsc.dialog.BookDownloadDialog;
import com.himoo.ydsc.http.OnAfreshDownloadListener;
import com.himoo.ydsc.reader.utils.ZipExtractorTask;
import com.himoo.ydsc.util.SP;
import com.ios.dialog.AlertDialog;

public class DownLoaderTask extends AsyncTask<Void, Integer, Long> {
	private final String TAG = "DownLoaderTask";
	private URL mUrl;
	private File mFile;
	private BookDownloadDialog mDialog;
	private int mProgress = 0;
	private ProgressReportingOutputStream mOutputStream;
	private Activity mContext;
	private String bookName;
	private String bookId;
	private OnAfreshDownloadListener listener;
	private boolean isFullWidth;
	private boolean isHasToast = false;

	public DownLoaderTask(String url, String bookName, String bookId,String out,
			Activity context, OnAfreshDownloadListener listener,
			boolean isFullWidth) {
		super();
		this.mContext = context;
		this.listener = listener;
		this.isFullWidth = isFullWidth;
		this.bookName = bookName;
		this.bookId = bookId;
		if (context != null) {
			if (isFullWidth)
				mDialog = new BookDownloadDialog(context, isFullWidth);
			else
				mDialog = new BookDownloadDialog(context);
		} else {
			mDialog = null;
		}
		if (mDialog != null) {
			mDialog.setOnKeyListener(keylistener);
		}
		try {
			mUrl = new URL(url);
			mFile = new File(out);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public OnKeyListener keylistener = new DialogInterface.OnKeyListener() {
		public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
			if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
				if (!isHasToast) {
					isHasToast = true;
					new AlertDialog(mContext).builder().setTitle("提示")
							.setMsg("您确定要取消下载?")
							.setNegativeButton("取消", new OnClickListener() {

								@Override
								public void onClick(View v) {
									// TODO Auto-generated method stub
									isHasToast = false;
									
								}
							}).setPositiveButton("确定", new OnClickListener() {

								@Override
								public void onClick(View v) {
									// TODO Auto-generated method stub
									DownLoaderTask.this.cancel(true);
									mDialog.dismiss();
									SP.getInstance().remove(bookName,bookId);
									BookDownloadService.getDownloadManager(
											mContext).updateDownSuccess(
											bookName,bookId,false);
									if(listener!=null)
										listener.onCancelDownload();
								}

							}).show();
				}
				return true;
			} else {
				return false;
			}
		}
	};

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		// super.onPreExecute();
		if (mDialog != null) {
			mDialog.setMessage(mFile.getName());
			mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			/*
			 * mDialog.setOnCancelListener(new OnCancelListener() {
			 * 
			 * @Override public void onCancel(DialogInterface dialog) { // TODO
			 * Auto-generated method stub cancel(false); } });
			 */
			mDialog.show();
		}
	}

	@Override
	protected Long doInBackground(Void... params) {
		// TODO Auto-generated method stub
		return download();
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		// TODO Auto-generated method stub
		// super.onProgressUpdate(values);
		if (mDialog == null)
			return;
		if (values.length > 1) {
			int contentLength = values[1];
			if (contentLength == -1) {
				mDialog.setIndeterminate(true);
			} else {
				mDialog.setMax(contentLength);

			}
		} else {
			mDialog.setProgress(values[0].intValue());
		}
	}

	@Override
	protected void onPostExecute(Long result) {
		// TODO Auto-generated method stub
		// super.onPostExecute(result);
		if (mDialog != null && mDialog.isShowing()) {
			mDialog.dismiss();
		}
		if (isCancelled())
			return;
		doZipExtractorWork(mContext, mFile.getAbsolutePath(), mFile.getParent()
				+ "/" + bookName);
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
			mOutputStream = new ProgressReportingOutputStream(mFile);
			publishProgress(0, length);
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
		return bytesCopied;
	}

	private int copy(InputStream input, OutputStream output) {
		byte[] buffer = new byte[1024 * 8];
		BufferedInputStream in = new BufferedInputStream(input, 1024 * 8);
		BufferedOutputStream out = new BufferedOutputStream(output, 1024 * 8);
		int count = 0, n = 0;
		try {
			while ((n = in.read(buffer, 0, 1024 * 8)) != -1) {
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

		public ProgressReportingOutputStream(File file)
				throws FileNotFoundException {
			super(file);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void write(byte[] buffer, int byteOffset, int byteCount)
				throws IOException {
			// TODO Auto-generated method stub
			super.write(buffer, byteOffset, byteCount);
			mProgress += byteCount;
			publishProgress(mProgress);
		}

	}

	/**
	 * 加压zip文件
	 * 
	 * @param inPath
	 * @param outPath
	 */
	public void doZipExtractorWork(Activity activity, String inPath,
			String outPath) {
		ZipExtractorTask task = new ZipExtractorTask(bookName,bookId, inPath, outPath,
				activity, true, listener, isFullWidth);
		task.execute();
	}

}
