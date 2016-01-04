package com.himoo.ydsc.download;

import java.util.List;

import android.content.Context;

import com.himoo.ydsc.bean.BaiduBook;
import com.himoo.ydsc.http.HttpConstant;
import com.himoo.ydsc.util.MyLogger;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.DbUtils.DbUpgradeListener;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;

public class BaiduBookDownload {

	private static BaiduBookDownload instance = null;
	private static DbUtils db;

	public static BaiduBookDownload getInstance(Context context) {
		if (instance == null)
			synchronized (BaiduBookDownload.class) {
				if (instance == null)
					instance = new BaiduBookDownload();
				db = DbUtils.create(context, "Book");
				db.configAllowTransaction(true);
			}
		return instance;
	}

	/**
	 * 数据库升级
	 * 
	 * @param context
	 * @param DbVersion
	 * @param dbUpgradeListener
	 * @return BaiduBookDownload
	 */
	public static BaiduBookDownload getInstance(Context context, int DbVersion,
			DbUpgradeListener dbUpgradeListener) {
		if (instance == null)
			synchronized (BaiduBookDownload.class) {
				if (instance == null)
					instance = new BaiduBookDownload();
				db = DbUtils.create(context, "Book", 2, dbUpgradeListener);
				db.configAllowTransaction(true);
			}
		return instance;
	}

	/**
	 * 下载百度书籍
	 * 
	 * @param book
	 * @throws DbException
	 */
	public void addBaiduBookDownload(BaiduBook book) throws DbException {

		try {
			List<BaiduInfo> list = db.findAll(Selector.from(BaiduInfo.class)
					.where("bookName", "=", book.getTitle())
					.and("bookAuthor", "=", book.getAuthor()));
			if (list != null && !list.isEmpty()) {
				BaiduInfo bookinfo = list.get(0);
				bookinfo.setLastChapterName(book.getLastChapter().getText());
				bookinfo.setBookLastUpdateTime(System.currentTimeMillis() + "");
				db.update(bookinfo);
			} else {
				BaiduInfo bookDownloadInfo = new BaiduInfo();
				bookDownloadInfo.setSerialize(!book.getStatus().equals("完结"));
				bookDownloadInfo.setBookSourceType(2);
				bookDownloadInfo.setBookName(book.getTitle());
				bookDownloadInfo.setReaderTime(0L);
				bookDownloadInfo.setBookStatue(book.getStatus());
				bookDownloadInfo.setBookAuthor(book.getAuthor());
				bookDownloadInfo.setBookLastUpdateTime(System
						.currentTimeMillis() + "");
				bookDownloadInfo.setBookCoverImageUrl(book.getCoverImage());
				bookDownloadInfo.setBookIsRead(false);
				bookDownloadInfo.setAutoResume(false);
				bookDownloadInfo.setBookReadHository("此书您还没有阅读!");
				bookDownloadInfo.setBookReadProgress(100L);
				bookDownloadInfo.setLastReaderProgress("0%");
				bookDownloadInfo.setLastChapterName(book.getLastChapter()
						.getText());
				String url = HttpConstant.BAIDU_BOOK_DETAILS_URL
						+ "appui=alaxs&gid=" + book.getGid() + "&dir=1&ajax=1";
				bookDownloadInfo.setLastUrl(url);
				bookDownloadInfo.setDownloadUrl(url);
				db.saveBindingId(bookDownloadInfo);
			}

		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * 更新下载状态
	 */
	public void updateDownlaodstatue(String bookName) {
		try {
			List<BaiduInfo> list = db.findAll(Selector.from(BaiduInfo.class)
					.where("bookName", "=", bookName));
			if (list != null && !list.isEmpty()) {
				BaiduInfo book = list.get(0);
				book.setAutoResume(true);// 下载完成
				db.update(book);
			}

		} catch (DbException e) {
			MyLogger.kLog().e(e);
		}

	}

	/**
	 * 查询下载的状态
	 * @param bookName
	 * @return
	 */
	public boolean queryBookDownloadStatue(String bookName) {
		try {
			List<BaiduInfo> list = db.findAll(Selector.from(BaiduInfo.class)
					.where("bookName", "=", bookName));
			if (list != null && !list.isEmpty()) {
				BaiduInfo book = list.get(0);
				return book.isAutoResume();
			}

		} catch (DbException e) {
			MyLogger.kLog().e(e);
			return false;
		}
		return false;

	}

	/**
	 * 查询数据库中共有多少本书
	 * 
	 * @return
	 */
	public int queryBookCount() {
		try {
			List<BaiduInfo> list = db.findAll(BaiduInfo.class);
			if (list != null)
				return list.size();
			else
				return 0;
		} catch (DbException e) {
			// TODO Auto-generated catch block
			return 0;
		}

	}

	/**
	 * 查找出需要更新的书籍
	 * 
	 * @return
	 */
	public List<BaiduInfo> queryNeedUpdate() {
		try {
			List<BaiduInfo> list = db.findAll(Selector.from(BaiduInfo.class)
					.where("isSerialize", "=", true)
					.and("bookSourceType", "=", 2));
			return list;
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * 检查需要更新的书
	 * 
	 * @param bookName
	 * @return
	 */
	public BaiduInfo queryNeedUpdate(String bookName) {
		try {
			List<BaiduInfo> list = db.findAll(Selector.from(BaiduInfo.class)
					.where("isSerialize", "=", true)
					.and("bookName", "=", bookName)
					.and("bookSourceType", "=", 2));
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
	 * 更新最新章节的名字
	 * 
	 * @param chapterName
	 */
	public void updateChapterName(BaiduInfo book, String newChapterName) {
		try {
			book.setLastChapterName(newChapterName);
			db.update(book);
		} catch (DbException e) {
			// TODO Auto-generated catch block
			MyLogger.kLog().e(e);
		}
	}

	/**
	 * 根据书名判断该书是否已下载
	 * 
	 * @param bookName
	 * @return
	 */
	public boolean isDownload(String bookName) {
		try {
			List<BaiduInfo> list = db.findAll(Selector.from(BaiduInfo.class)
					.where("bookName", "=", bookName));
			if (list == null || list.isEmpty())
				return false;
			else
				return true;

		} catch (DbException e) {
			return false;
		}

	}

}
