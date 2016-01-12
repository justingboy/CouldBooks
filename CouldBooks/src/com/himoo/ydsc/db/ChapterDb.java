package com.himoo.ydsc.db;

import java.util.List;

import android.content.Context;
import android.os.Environment;

import com.himoo.ydsc.bean.BaiduBookChapter;
import com.himoo.ydsc.util.MyLogger;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;

public class ChapterDb {

	private static ChapterDb mInstance = null;
	private DbUtils db;

	private ChapterDb() {

	}

	public static ChapterDb getInstance() {
		if (mInstance == null) {
			synchronized (BookDb.class) {
				if (mInstance == null) {
					mInstance = new ChapterDb();
				}
			}
		}
		return mInstance;
	}

	/**
	 * 创建数据库
	 * 
	 * @param context
	 * @param bookName
	 */
	public void createDb(Context context, String bookName) {

		db = DbUtils.create(context, Environment.getExternalStorageDirectory()
				.getPath() + "/CouldBook/db/", bookName);
		db.configAllowTransaction(true);
	}

	/**
	 * 保存搜索的记录
	 * 
	 * @param bookName
	 */
	public void saveBookChapter(List<BaiduBookChapter> chapterList) {

		try {
			db.saveAll(chapterList);
		} catch (DbException e) {
			// TODO Auto-generated catch block
			MyLogger.kLog().e(e);
		}
	}

	/**
	 * 更据Id查到chapter
	 * 
	 * @param position
	 * @return
	 */
	public BaiduBookChapter queryChapterByPosition(int position) {

		try {
			return db.findById(BaiduBookChapter.class, position + 1);
		} catch (DbException e) {
			// TODO Auto-generated catch block
			MyLogger.kLog().e(e);
		}

		return null;
	}

	/**
	 * 删除指定的数据库
	 * 
	 * @param bookName
	 */
	public void deleteChapterDb(String bookName) {
		try {
			db.dropDb();
		} catch (DbException e) {
			// TODO Auto-generated catch block
			MyLogger.kLog().e(e);
		}
	}

}
