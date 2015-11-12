package com.himoo.ydsc.db;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.himoo.ydsc.db.bean.BookSearchRecords;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;

/**
 * 保存搜索的记录
 * 
 */
public class BookDb {

	private static BookDb mInstance = null;
	private static DbUtils db;

	private BookDb() {

	}

	public static BookDb getInstance(Context context, String dbName) {
		if (mInstance == null) {
			synchronized (BookDb.class) {
				if (mInstance == null) {
					mInstance = new BookDb();
					db = DbUtils.create(context, dbName);
					db.configAllowTransaction(true);
				}
			}
		}
		return mInstance;
	}

	/**
	 * 保存搜索的记录
	 * 
	 * @param bookName
	 */
	public void saveBookSearch(BookSearchRecords book) {

		try {
			// 先判断数据库的个数是否超过15条,防止数据过大，查询速度慢
			List<BookSearchRecords> bookAlllist = querryAll();
			{
				if (bookAlllist != null && bookAlllist.size() > 9) {
					db.delete(bookAlllist.get(0));
				}
			}

			// 先判断数据库中是否已经存在
			List<BookSearchRecords> list = db.findAll(Selector.from(
					BookSearchRecords.class).where("record", "=",
					book.getRecord()));
			if (list == null || list.isEmpty())
				db.save(book);
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 查询历史记录
	 * 
	 * @return
	 */
	public ArrayList<BookSearchRecords> querryAll() {
		try {
			ArrayList<BookSearchRecords> records = (ArrayList<BookSearchRecords>) db
					.findAll(BookSearchRecords.class);
			return records;
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;

	}

	/**
	 * 数据库中的个数
	 * 
	 * @return count
	 */
	public int querryCount() {
		ArrayList<BookSearchRecords> records = querryAll();
		if (records == null)
			return 0;
		return records.size();
	}

	/**
	 * 判断是否有历史记录
	 * 
	 * @return
	 */
	public boolean isEmpty() {
		ArrayList<BookSearchRecords> records = querryAll();
		if (records == null)
			return true;
		else if (records.size() == 0)
			return true;
		else
			return false;
	}

	/**
	 * 清除历史记录
	 */
	public void deleteRecords(Class<?> className) {

		try {
			db.deleteAll(className);

		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
