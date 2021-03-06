package com.himoo.ydsc.reader.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.util.Log;

import com.himoo.ydsc.R;
import com.himoo.ydsc.dialog.BookDownloadDialog;
import com.himoo.ydsc.download.BookDownloadService;
import com.himoo.ydsc.http.OnAfreshDownloadListener;
import com.himoo.ydsc.ui.utils.Toast;
import com.himoo.ydsc.util.SP;

public class ZipExtractorTask extends AsyncTask<Void, Integer, Long> {
	private final String TAG = "ZipExtractorTask";
	private final File mInput;
	private final File mOutput;
	private final BookDownloadDialog mDialog;
	private int mProgress = 0;
	private final Context mContext;
	private boolean mReplaceAll;
	private String bookName;
	private String bookId;
	private OnAfreshDownloadListener listener;

	public ZipExtractorTask(String bookName,String bookId, String in, String out,
			Context context, boolean replaceAll,
			OnAfreshDownloadListener listener, boolean isFullWidth) {
		super();
		this.bookName = bookName;
		this.bookId = bookId;
		this.listener = listener;
		mInput = new File(in);
		mOutput = new File(out);
		if (!mOutput.exists()) {
			if (!mOutput.mkdirs()) {
				Log.e(TAG,
						"Failed to make directories:"
								+ mOutput.getAbsolutePath());
			}
		}
		if (context != null) {
			if (isFullWidth) {
				mDialog = new BookDownloadDialog(context, R.drawable.actionbar_import,true);
			} else {
				mDialog = new BookDownloadDialog(context,R.drawable.actionbar_import);
						
			}
		} else {
			mDialog = null;
		}
		mContext = context;
		mReplaceAll = replaceAll;
	}

	@Override
	protected Long doInBackground(Void... params) {
		// TODO Auto-generated method stub
		return unzip();
	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		// super.onPreExecute();
		if (mDialog != null) {
			mDialog.setTitle("正在解压文件");
			mDialog.setMessage(mInput.getName());
			mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			mDialog.setOnCancelListener(new OnCancelListener() {

				@Override
				public void onCancel(DialogInterface dialog) {
					// TODO Auto-generated method stub
					cancel(false);
				}
			});
			mDialog.show();
		}
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		// TODO Auto-generated method stub
		// super.onProgressUpdate(values);
		if (mDialog == null)
			return;
		if (values.length > 1) {
			int max = values[1];
			mDialog.setMax(max);
		} else
			mDialog.setProgress(values[0].intValue());
	}

	@Override
	protected void onPostExecute(Long result) {
		// TODO Auto-generated method stub
		// super.onPostExecute(result);
		if (mDialog != null && mDialog.isShowing()) {
			mDialog.dismiss();
		}
		// BaiduBookDownload.getInstance(mContext).updateDownlaodstatue(bookName);
		SP.getInstance().putBookDownSuccess(bookName+bookId, true);
		if (listener != null) {
			listener.onPostDownloadSuccess(bookName,bookId);
		}
		Toast.showLong(mContext, "《" + bookName + "》" + "下载完成");
		BookDownloadService.getDownloadManager(
				mContext).updateDownSuccess(
						bookName,bookId,true);

		if (isCancelled())
			return;
	}

	@SuppressWarnings("unchecked")
	private long unzip() {
		long extractedSize = 0L;
		Enumeration<ZipEntry> entries;
		ZipFile zip = null;
		try {
			zip = new ZipFile(mInput);
			long uncompressedSize = getOriginalSize(zip);
			publishProgress(0, (int) uncompressedSize);

			entries = (Enumeration<ZipEntry>) zip.entries();
			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();
				if (entry.isDirectory()) {
					continue;
				}
				File destination = new File(mOutput, entry.getName());
				if (!destination.getParentFile().exists()) {
					Log.e(TAG, "make="
							+ destination.getParentFile().getAbsolutePath());
					destination.getParentFile().mkdirs();
				}
				if (destination.exists() && mContext != null && !mReplaceAll) {

				}
				ProgressReportingOutputStream outStream = new ProgressReportingOutputStream(
						destination);
				extractedSize += copy(zip.getInputStream(entry), outStream);
				outStream.close();
			}
		} catch (ZipException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (zip != null)
					zip.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return extractedSize;
	}

	private long getOriginalSize(ZipFile file) {
		@SuppressWarnings("unchecked")
		Enumeration<ZipEntry> entries = (Enumeration<ZipEntry>) file.entries();
		long originalSize = 0l;
		while (entries.hasMoreElements()) {
			ZipEntry entry = entries.nextElement();
			if (entry.getSize() >= 0) {
				originalSize += entry.getSize();
			}
		}
		return originalSize;
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
}