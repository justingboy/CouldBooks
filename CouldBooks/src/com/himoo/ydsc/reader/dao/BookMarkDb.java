package com.himoo.ydsc.reader.dao;

import java.util.List;

import android.content.Context;

import com.himoo.ydsc.db.BookDb;
import com.himoo.ydsc.util.MyLogger;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;

public class BookMarkDb {
	private static BookMarkDb mInstance = null;
	private static DbUtils db;

	private BookMarkDb() {

	}

	public static BookMarkDb getInstance(Context context, String dbName) {
		if (mInstance == null) {
			synchronized (BookDb.class) {
				if (mInstance == null) {
					mInstance = new BookMarkDb();
					db = DbUtils.create(context, dbName);
					db.configAllowTransaction(true);
				}
			}
		}
		return mInstance;
	}

	/**
	 * 保存阅读的位置
	 */
	public void saveReaderPosition(BookMark mark) {
		try {
			List<BookMark> list = db.findAll(Selector.from(BookMark.class)
					.where("bookName", "=", mark.getBookName())
					.and("type", "=", 1));
			if (list != null && !list.isEmpty()) {
				BookMark bookMark = list.get(0);
				bookMark.setPosition(mark.getPosition());
				bookMark.setChapterName(mark.getChapterName());
				bookMark.setPageCount(mark.getPageCount());
				bookMark.setCurrentPage(mark.getCurrentPage());
				db.update(bookMark);
			} else {
				db.save(mark);
			}

		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * 保存阅读的位置
	 */
	public void saveReaderMark(BookMark mark) {
		try {
			List<BookMark> list = db.findAll(Selector.from(BookMark.class)
					.where("bookName", "=", mark.getBookName())
					.and("chapterName", "=", mark.getChapterName())
					.and("type", "=", 2)
					.and("currentPage", "=", mark.getCurrentPage()));
			if (list != null && !list.isEmpty()) {
				return;
			} else {
				db.save(mark);
			}

		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * 是否已经保存
	 * 
	 * @param bookName
	 * @return
	 */
	public boolean isExist(String bookName) {

		try {
			List<BookMark> list = db.findAll(Selector.from(BookMark.class)
					.where("bookName", "=", bookName));
			if (list != null && !list.isEmpty()) {
				return true;
			} else {
				return false;
			}
		} catch (DbException e) {
			// TODO Auto-generated catch block
			return false;

		}
	}

	/**
	 * 查询上次读的位置
	 * 
	 * @param bookName
	 * @return
	 */
	public BookMark querryReaderPos(String bookName) {
		try {
			List<BookMark> list = db.findAll(Selector.from(BookMark.class)
					.where("bookName", "=", bookName).and("type", "=", 1));
			if (list != null && !list.isEmpty()) {
				return list.get(0);
			} else {
				return null;
			}
		} catch (DbException e) {
			// TODO Auto-generated catch block
			return null;
		}
	}

	/**
	 * 查询所有的书签
	 * 
	 * @param bookName
	 * @return
	 */
	public List<BookMark> querryReaderMark(String bookName) {
		try {
			List<BookMark> list = db.findAll(Selector.from(BookMark.class)
					.where("bookName", "=", bookName).and("type", "=", 2));
			if (list != null && !list.isEmpty()) {
				return list;
			} else {
				return null;
			}
		} catch (DbException e) {
			// TODO Auto-generated catch block
			return null;
		}
	}

	/**
	 * 删除书签
	 * 
	 * @param bookMarko
	 */
	public void deletBookMark(BookMark bookMark) {
		try {
			db.delete(bookMark);
		} catch (DbException e) {
			// TODO Auto-generated catch block
			MyLogger.kLog().e(e);
		}
	}

}
