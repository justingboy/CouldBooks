package com.himoo.ydsc.reader.utils;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.himoo.ydsc.reader.dao.BookMark;
import com.himoo.ydsc.reader.dao.BookMarkDb;

public class BookMarkUtils {

	private static BookMarkUtils mInstance = null;

	public List<BookMark> bookMarkList;

	private BookMarkUtils() {

	}

	public static BookMarkUtils getInstance() {
		if (mInstance == null) {
			synchronized (BookMarkUtils.class) {
				if (mInstance == null) {
					mInstance = new BookMarkUtils();
				}
			}
		}
		return mInstance;
	}

	/**
	 * 初始化书签，在打开书的时候初始化
	 * 
	 * @param context
	 * @param bookName
	 * @param bookId
	 * @return
	 */
	public List<BookMark> getBookMark(Context context, String bookName,
			String bookId) {
		BookMarkDb db = BookMarkDb.getInstance(context, "book");
		bookMarkList = db.querryReaderMark(bookName);
		if (bookMarkList == null) {
			bookMarkList = new ArrayList<BookMark>();
		}

		return bookMarkList;
	}

	/**
	 * 当前页是否添加了书签
	 * 
	 * @param mark
	 * @return
	 */
	public boolean isExistBookMark(BookMark mark) {
		return bookMarkList.contains(mark);

	}

	/**
	 * 添加书签
	 * 
	 * @param mark
	 */
	public void addBookMark(BookMark mark) {
		bookMarkList.add(mark);
	}

	/**
	 * 删除书签
	 * 
	 * @param mark
	 */
	public void deleteBookMark(BookMark mark) {
		bookMarkList.remove(mark);
	}

}