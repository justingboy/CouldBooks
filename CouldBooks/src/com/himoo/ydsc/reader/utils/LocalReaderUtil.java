package com.himoo.ydsc.reader.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

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
	 */
	public ArrayList<BaiduBookChapter>  parseLocalBook(String bookName) {
		ArrayList<BaiduBookChapter> list = new ArrayList<BaiduBookChapter>();
		ArrayList<File> chapterFile = new ArrayList<File>();
		File bookFile = new File(FileUtils.mSdRootPath + "/CouldBook/baidu"
				+ File.separator + bookName + File.separator);
		if (bookFile != null && bookFile.exists()) {
			File[] chapters = bookFile.listFiles();
			for (int i = 0; i < chapters.length; i++) {
				chapterFile.add(chapters[i]);
			}
			chapters = null;

			Collections.sort(chapterFile, new Comparator<File>() {

				@Override
				public int compare(File lhs, File rhs) {
					// TODO Auto-generated method stub
					return ((Long) lhs.lastModified()).compareTo(rhs
							.lastModified());
				}
			});

			for (int i = 0; i < chapterFile.size(); i++) {
				String path = chapterFile.get(i).getName();
				String[] str = path.split("-");
				BaiduBookChapter chapter = new BaiduBookChapter();
				chapter.setText(str[0]);
				chapter.setIndex(str[1]);
				list.add(chapter);

			}

		}
		return list;

	}

}
