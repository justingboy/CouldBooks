package com.himoo.ydsc.reader.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;

import com.himoo.ydsc.bean.BaiduBookChapter;
import com.himoo.ydsc.http.BookDetailsTask;
import com.himoo.ydsc.reader.bean.BookOperation;
import com.himoo.ydsc.reader.bean.Chapter;
import com.himoo.ydsc.util.FileUtils;

/**
 * 设计此类的目的是便于统一管理，从资源文件中读取数据。 app中的所有数据来源都可以通过这个类来提供，这样在将来重用代码的时候，也方便修改。
 * 而且在各个类的逻辑任务上也清晰可见。 其目前所做的任务包括，初始化Book类，从资源文件中读取章节内容来初始化Chapter对象。
 * 
 * @author MJZ
 * 
 */
public class IOHelper {
	private static BookOperation book;
	// private static Chapter chapter;
	private static String[] chapterPaths;
	private static Resources res;
	private static Context mContext;

	/**
	 * 初始化Book类的唯一对象。 这个函数一般只会调用一次。
	 * 
	 * @param context
	 *            由于从文件中读取资源，则需要通过Activity 来提供。因此在Activity调用此函数的时候，会传入 this。
	 * @return
	 */
	public static BookOperation getBook(Context context, String bookName,
			ArrayList<BaiduBookChapter> chapterList) {
		book = BookOperation.getInstance();
		mContext = context;
		book.setChapterList(chapterList);

		// res = context.getResources();
		// String booklists[] = res.getStringArray(R.array.booklists);
		// chapterPaths = res.getStringArray(R.array.content_path);
		//
		// // 设置Book 对象的信息
		// book.setAuthor(res.getString(R.string.author));
		book.setBookname(bookName);
		//
		// // 下面這個if语句是因为出现个bug而写的，它其实不应该存在。
		// // 猜测的原因可能是在软件退出的时候，Book类对象没有被销毁，则再次启动软件的时候
		// // 又给它添加了一次章节信息
		// if (book.getChapterList().size() != booklists.length)
		// for (int i = 0; i < booklists.length; ++i)
		// book.addChapter(booklists[i]);

		return book;
	}

	/**
	 * 得到章节内容。
	 * 
	 * @param order
	 *            要读取的章节的顺序。
	 * @param context
	 *            通过context来得到 Resources 对象，从而获取资源。
	 * @return
	 */
	public static Chapter getChapter(String index, int position) {
		int length = book.getChapterList().size();
		if (position - length == 0 || position == -1)
			return null;
		Chapter chapter = new Chapter();
		chapter.setPosition(position);
		chapter.setIndex(book.getChapterList().get(position).getIndex());
		chapter.setBookName(book.getBookname());
		BaiduBookChapter bookChapter = book.getChapterList().get(position);
		String chapterName = bookChapter.getText().trim();
		String chapterUrl = ChapterParseUtil.getChapterUrl(bookChapter);
		chapter.setChapterName(chapterName);
		chapter.setChapterUrl(chapterUrl);

		File dirFile = new File(FileUtils.mSdRootPath + "/CouldBook/baidu"
				+ File.separator + book.getBookname() + File.separator);

		File file = new File(dirFile, chapterName + "-"
				+ book.getChapterList().get(position).getIndex() + "-"
				+ position + ".txt");
		if (file.exists() || file.length() > 0) {
			final String content = com.himoo.ydsc.download.FileUtils
					.readFormSd(file);

			chapter.setContent(content);

		} else {
			String chapterContent = BookDetailsTask.getInstance()
					.getChapterFormService(mContext, chapterUrl);
			if (chapterContent != null && !chapterContent.equals(""))
				chapter.setContent(chapterContent);
			else {
				if (Integer.valueOf(index) > 0) {
					InputStream is;
					try {
						is = mContext.getAssets().open("error.txt");
						int size = is.available();
						byte[] buffer = new byte[size];
						is.read(buffer);
						is.close();
						String content = new String(buffer, "gbk");
						chapter.setContent(content);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						return null;
					}
				} else
					return null;
			}

		}

		return chapter;

	}

	/**
	 * 获取章节的大小
	 * 
	 * @return
	 */
	public static int getChapterLength() {
		return book.getChapterList().size() - 1;
	}

	/**
	 * 获得目录列表
	 * @return
	 */
	public static List<BaiduBookChapter> getBookChapter() {
		return book.getChapterList();
	}

}
