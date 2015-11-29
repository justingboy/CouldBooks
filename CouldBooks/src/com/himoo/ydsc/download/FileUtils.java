package com.himoo.ydsc.download;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.DecimalFormat;

import org.json.JSONObject;

import com.himoo.ydsc.util.MyLogger;

import android.content.Context;
import android.util.Log;

public class FileUtils {

	public static final int SIZETYPE_B = 1;// 获取文件大小单位为B的double值
	public static final int SIZETYPE_KB = 2;// 获取文件大小单位为KB的double值
	public static final int SIZETYPE_MB = 3;// 获取文件大小单位为MB的double值
	public static final int SIZETYPE_GB = 4;// 获取文件大小单位为GB的double值

	/**
	 * 获取文件指定文件的指定单位的大小
	 * 
	 * @param filePath
	 *            文件路径
	 * @param sizeType
	 *            获取大小的类型1为B、2为KB、3为MB、4为GB
	 * @return double值的大小
	 */
	public static double getFileOrFilesSize(String filePath, int sizeType) {
		File file = new File(filePath);
		long blockSize = 0;
		try {
			if (file.isDirectory()) {
				blockSize = getFileSizes(file);
			} else {
				blockSize = getFileSize(file);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("获取文件大小", "获取失败!");
		}
		return FormetFileSize(blockSize, sizeType);
	}

	/**
	 * 调用此方法自动计算指定文件或指定文件夹的大小
	 * 
	 * @param filePath
	 *            文件路径
	 * @return 计算好的带B、KB、MB、GB的字符串
	 */
	public static String getAutoFileOrFilesSize(String filePath) {
		File file = new File(filePath);
		long blockSize = 0;
		try {
			if (file.isDirectory()) {
				blockSize = getFileSizes(file);
			} else {
				blockSize = getFileSize(file);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("获取文件大小", "获取失败!");
		}
		return FormetFileSize(blockSize);
	}

	/**
	 * 获取指定文件大小
	 * 
	 * @param f
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("resource")
	private static long getFileSize(File file) throws Exception {
		long size = 0;
		if (file.exists()) {
			FileInputStream fis = null;
			fis = new FileInputStream(file);
			size = fis.available();
		} else {
			file.createNewFile();
			Log.e("获取文件大小", "文件不存在!");
		}
		return size;
	}

	/**
	 * 获取指定文件夹
	 * 
	 * @param f
	 * @return
	 * @throws Exception
	 */
	private static long getFileSizes(File f) throws Exception {
		long size = 0;
		File flist[] = f.listFiles();
		for (int i = 0; i < flist.length; i++) {
			if (flist[i].isDirectory()) {
				size = size + getFileSizes(flist[i]);
			} else {
				size = size + getFileSize(flist[i]);
			}
		}
		return size;
	}

	/**
	 * 转换文件大小
	 * 
	 * @param fileS
	 * @return
	 */
	private static String FormetFileSize(long fileS) {
		DecimalFormat df = new DecimalFormat("#.00");
		String fileSizeString = "";
		String wrongSize = "0B";
		if (fileS == 0) {
			return wrongSize;
		}
		if (fileS < 1024) {
			fileSizeString = df.format((double) fileS) + "B";
		} else if (fileS < 1048576) {
			fileSizeString = df.format((double) fileS / 1024) + "KB";
		} else if (fileS < 1073741824) {
			fileSizeString = df.format((double) fileS / 1048576) + "MB";
		} else {
			fileSizeString = df.format((double) fileS / 1073741824) + "GB";
		}
		return fileSizeString;
	}

	/**
	 * 转换文件大小,指定转换的类型
	 * 
	 * @param fileS
	 * @param sizeType
	 * @return
	 */
	private static double FormetFileSize(long fileS, int sizeType) {
		DecimalFormat df = new DecimalFormat("#.00");
		double fileSizeLong = 0;
		switch (sizeType) {
		case SIZETYPE_B:
			fileSizeLong = Double.valueOf(df.format((double) fileS));
			break;
		case SIZETYPE_KB:
			fileSizeLong = Double.valueOf(df.format((double) fileS / 1024));
			break;
		case SIZETYPE_MB:
			fileSizeLong = Double.valueOf(df.format((double) fileS / 1048576));
			break;
		case SIZETYPE_GB:
			fileSizeLong = Double.valueOf(df
					.format((double) fileS / 1073741824));
			break;
		default:
			break;
		}
		return fileSizeLong;
	}

	/**
	 * 清除缓存文件
	 */
	public static boolean clear(File mCacheDir) {
		boolean isDeleteOk = true;
		File[] files = mCacheDir.listFiles();
		for (int i = 0; i < files.length; i++) {
			isDeleteOk = files[i].delete();
			if (!isDeleteOk)
				break;
		}
		return isDeleteOk;
	}

	/**
	 * 保存文件内容（文件存储方式,保存在data/data/packagename/file下）
	 * 
	 * @param c
	 * @param fileName
	 *            文件名称
	 * @param content
	 *            内容
	 */
	public static void writeFiles(Context c, String fileName, String content,
			int mode) throws Exception {
		// 打开文件获取输出流，文件不存在则自动创建
		FileOutputStream fos = c.openFileOutput(fileName, mode);
		fos.write(content.getBytes());
		fos.close();
	}

	/**
	 * 读取文件内容（内容在data/data/packagename/file下）
	 * 
	 * @param c
	 * @param fileName
	 *            文件名称
	 * @return 返回文件内容
	 */
	public static String readFiles(Context c, String fileName) throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		FileInputStream fis = c.openFileInput(fileName);
		byte[] buffer = new byte[1024];
		int len = 0;
		while ((len = fis.read(buffer)) != -1) {
			baos.write(buffer, 0, len);
		}
		String content = baos.toString();
		fis.close();
		baos.close();
		return content;
	}

	/**
	 * 向sd文件中写入内容
	 * 
	 * @param url
	 * @param file
	 */
	public static void writeTosSd(String url, File file) {
		InputStream is = null;
		BufferedWriter bw = null;
		try {
			String result = HttpUtils.getStringFormSer(url);
			JSONObject json = new JSONObject(result);
			if (json.getInt("status") == 1) {
				String bookContent = json
						.getJSONObject("data")
						.getString("content")
						.replaceAll(
											"<p style="
													+ "\"text-indent:2em;\">",
											"\n        ")
									.replaceAll("<div  class=\"content.*<div  style=\"display:inline-block;\"></div>    ", "\n        ")
									.replaceAll("</p>", "\n")
									.replaceAll("<div[\\s]*>", "")
									.replaceAll("</div>", "\n")
									.replaceAll("<br/>", "\n\n")
									.replaceAll("<span style=\".*\">", "")
									.replaceAll("</span>", "\n")
									.replaceAll("<[/]?dd.*>", "\n")
									.replaceAll("<div.*>", "\n")
									.replaceAll("&nbsp; &nbsp;", "      ")
									.replaceAll("<br>", "")
									.replaceAll("readx.* ", "       ")
									.replaceAll("<a  class=\" yi-fontcolor\" .*", "")
									.replaceAll("<p  class=\" .*p\">", "\n         ")
									.replaceAll("<p  class=\".*p\">", "\n        ");
				bw = new BufferedWriter(new FileWriter(file));
				bw.write(bookContent);

			}
		} catch (Exception e) {
			MyLogger.kLog().e("下载出错" + "出错章节 ：url =" + url + e.getMessage());

		} finally {
			if (null != bw)
				try {
					bw.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			if (null != is)
				try {
					is.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}

	/**
	 * 从sd文件中读取数据
	 * 
	 * @param file
	 * @return
	 */
	public static String readFormSd(File file) {
		StringBuilder sb = new StringBuilder();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
			String line = null;
			while ((line = br.readLine()) != null) {
				sb.append(line);
				sb.append("\r\n");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (null != br)
				try {
					br.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}

		return sb.toString();

	}

	/**
	 * 将流写到文件中
	 * 
	 * @param is
	 * @param os
	 */
	public static void copyStream(InputStream is, OutputStream os) {
		final int buffer_size = 1024;
		byte[] bytes = new byte[buffer_size];
		int len = 0;
		try {
			while ((len = is.read(bytes, 0, buffer_size)) != -1) {
				os.write(bytes, 0, len);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * 将流转化为字符串
	 * 
	 * @param is
	 * @return
	 */
	public static String convertStreamToString(InputStream is) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return sb.toString();
	}

}
