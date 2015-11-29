package com.himoo.ydsc.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Environment;

/**
 * 文件工具类
 * 
 * @author asus1
 * 
 */
public class FileUtils {
	public static String mSdRootPath = Environment
			.getExternalStorageDirectory().getPath();
	private static String mDataRootPath = null;
	private final static String FOLDER_DOWNLOAD_BOOK = "/CouldBook/download/";

	public FileUtils(Context context) {
		mDataRootPath = context.getCacheDir().getPath();
	}

	public String getStorageDirectory() {
		return Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED) ? mSdRootPath + FOLDER_DOWNLOAD_BOOK
				: mDataRootPath + FOLDER_DOWNLOAD_BOOK;
	}

	public void savaBitmap(String fileName, Bitmap bitmap) throws IOException {
		if (bitmap == null) {
			return;
		}
		String path = getStorageDirectory();
		File folderFile = new File(path);
		if (!folderFile.exists()) {
			folderFile.mkdir();
		}
		File file = new File(path + File.separator + fileName);
		file.createNewFile();
		FileOutputStream fos = new FileOutputStream(file);
		bitmap.compress(CompressFormat.JPEG, 100, fos);
		fos.flush();
		fos.close();
	}

	public Bitmap getBitmap(String fileName) {
		return BitmapFactory.decodeFile(getStorageDirectory() + File.separator
				+ fileName);
	}

	public boolean isFileExists(String fileName) {
		return new File(getStorageDirectory() + File.separator + fileName)
				.exists();
	}

	public long getFileSize(String fileName) {
		return new File(getStorageDirectory() + File.separator + fileName)
				.length();
	}

	public void deleteFile() {
		File dirFile = new File(getStorageDirectory());
		if (!dirFile.exists()) {
			return;
		}
		if (dirFile.isDirectory()) {
			String[] children = dirFile.list();
			for (int i = 0; i < children.length; i++) {
				new File(dirFile, children[i]).delete();
			}
		}
		dirFile.delete();
	}

	public static byte[] getFileToByteArray(File file) {
		ByteArrayOutputStream bos = null;
		FileInputStream fis = null;
		byte[] buffer = null;
		try {
			bos = new ByteArrayOutputStream();
			fis = new FileInputStream(file);
			byte[] buf = new byte[1024];
			int len = 0;
			while ((len = fis.read(buf)) != -1) {
				bos.write(buf, 0, len);
			}
			buffer = bos.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (bos != null)
					bos.close();
			} catch (IOException e) {
				e.printStackTrace();
				bos = null;
				System.gc();
			}
			try {
				if (fis != null)
					fis.close();
			} catch (IOException e) {
				e.printStackTrace();
				fis = null;
				System.gc();
			}
		}
		return buffer;
	}
	
	
	/**
	 * 文本文件转换为指定编码的字符串
	 * 
	 * @param file
	 *            文本文件
	 * @param encoding
	 *            编码类型
	 * @return 转换后的字符串
	 * @throws IOException
	 */
	public static String file2String(File file, String encoding) {

		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
			StringBuilder sb = new StringBuilder();
			String str = "";
			while ((str = br.readLine()) != null) {
				sb.append(str);
			}
			return sb.toString();

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return "";

	}
}
