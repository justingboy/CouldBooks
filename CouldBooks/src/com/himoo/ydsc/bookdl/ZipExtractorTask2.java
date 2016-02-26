package com.himoo.ydsc.bookdl;



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

import android.content.Context;
import android.util.Log;

import com.himoo.ydsc.http.OnAfreshDownloadListener;

public class ZipExtractorTask2 {
	private final String TAG = "ZipExtractorTask";
	private final File mInput;
	private final File mOutput;
	private final Context mContext;
	private boolean mReplaceAll;
	private String bookName;
	private OnAfreshDownloadListener listener;

	public ZipExtractorTask2(String bookName, String in, String out,
			Context context, boolean replaceAll,
			OnAfreshDownloadListener listener) {
		super();
		this.bookName = bookName;
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
	
		mContext = context;
		mReplaceAll = replaceAll;
	}



//	@Override
//	protected void onPostExecute(Long result) {
//		// TODO Auto-generated method stub
//		// super.onPostExecute(result);
//		// BaiduBookDownload.getInstance(mContext).updateDownlaodstatue(bookName);
//		SP.getInstance().putBoolean(bookName, true);
//		if (listener != null) {
//			listener.onPostDownloadSuccess(bookName);
//		}
//		Toast.showLong(mContext, "《" + bookName + "》" + "下载完成");
//		BookDownloadService.getDownloadManager(
//				mContext).updateDownSuccess(
//						bookName,true);
//
//		if (isCancelled())
//			return;
//	}

	@SuppressWarnings("unchecked")
	public long unzip() {
		long extractedSize = 0L;
		Enumeration<ZipEntry> entries;
		ZipFile zip = null;
		try {
			zip = new ZipFile(mInput);
			long uncompressedSize = getOriginalSize(zip);
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
		}

	}
}