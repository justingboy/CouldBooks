package com.himoo.ydsc.reader.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.util.Log;

import com.himoo.ydsc.activity.more.WallActivity.MyChapter;
import com.himoo.ydsc.aescrypt.AESCrypt;
import com.himoo.ydsc.bean.BaiduBookChapter;
import com.himoo.ydsc.util.FileUtils;

public class LocalReaderUtil {

	private static LocalReaderUtil mInstance = null;

	private LocalReaderUtil() {
	}

	public static LocalReaderUtil getInstance() {
		if (mInstance == null)
			synchronized (LocalReaderUtil.class) {
				if (mInstance == null)
					mInstance = new LocalReaderUtil();

			}
		return mInstance;
	}

	/**
	 * 解析本地章节
	 * 
	 * @param bookName
	 * @param bookType
	 *            1:自己服务器上的书 2:是百度书籍
	 * @return
	 */
	public ArrayList<BaiduBookChapter> parseLocalBook(String bookName,
			int bookType) {
		ArrayList<BaiduBookChapter> list = new ArrayList<BaiduBookChapter>();
		if (bookType == 2) {
			ArrayList<File> chapterFile = new ArrayList<File>();
			File bookFile = new File(FileUtils.mSdRootPath + "/CouldBook/baidu"
					+ File.separator + bookName + File.separator);
			if (bookFile != null && bookFile.exists()) {
				File[] chapters = bookFile.listFiles();
				for (int i = 0; i < chapters.length; i++) {
					chapterFile.add(chapters[i]);
				}
				chapters = null;

				for (int i = 0; i < chapterFile.size(); i++) {
					String path = chapterFile.get(i).getName();
					String[] str = path.split("-\\|");
					BaiduBookChapter chapter = new BaiduBookChapter();
					chapter.setText(str[0]);
					chapter.setIndex(str[1]);
					String pos = str[2];
					chapter.setPosition(Integer.valueOf((pos.substring(0,
							pos.indexOf('.')))));
					list.add(chapter);

				}
				chapterFile.clear();
				chapterFile = null;

				Collections.sort(list, new Comparator<BaiduBookChapter>() {

					@Override
					public int compare(BaiduBookChapter lhs,
							BaiduBookChapter rhs) {
						// TODO Auto-generated method stub
						return ((Integer) lhs.getPosition()).compareTo(rhs
								.getPosition());
					}
				});

			}

		} else if (bookType == 1) {
			File bookFile = new File(FileUtils.mSdRootPath
					+ "/CouldBook/download" + File.separator + bookName
					+ File.separator + "index.txt");
			if (bookFile != null && bookFile.exists()) {
				String bookContent = AESCrypt.readBookFile(bookFile
						.getAbsolutePath());
				String chapter[] = bookContent.split("\n");
				for (int i = 0; i < chapter.length; i++) {
					BaiduBookChapter baiduBookChapter = new BaiduBookChapter();
					String[] str = chapter[i].split("\\|\\|\\|");
					baiduBookChapter.setText(str[0].trim());
					baiduBookChapter.setIndex(str[1].split("\\.")[0].trim());
					baiduBookChapter.setPosition(i);
					list.add(baiduBookChapter);
				}
				chapter = null;
			}

		}
		return list;

	}

}
