package com.himoo.ydsc.download;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.himoo.ydsc.bean.BaiduBook;
import com.himoo.ydsc.http.HttpConstant;
import com.himoo.ydsc.util.MyLogger;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
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
	 * 下载百度书籍
	 * 
	 * @param book
	 * @throws DbException
	 */
	public void addBaiduBookDownload(BaiduBook book) throws DbException {
		BaiduInfo bookDownloadInfo = new BaiduInfo();
		bookDownloadInfo.setSerialize(!book.getStatus().equals("完结"));
		bookDownloadInfo.setBookSourceType(2);
		bookDownloadInfo.setBookName(book.getTitle());
		bookDownloadInfo.setBookAuthor(book.getAuthor());
		bookDownloadInfo.setBookLastUpdateTime(System.currentTimeMillis() + "");
		bookDownloadInfo.setBookCoverImageUrl(book.getCoverImage());
		bookDownloadInfo.setBookIsRead(false);
		bookDownloadInfo.setBookReadHository("您还没阅读这本书呢");
		bookDownloadInfo.setBookReadProgress(100L);
		bookDownloadInfo.setLastChapterName(book.getLastChapter().getText());
		String url = HttpConstant.BAIDU_BOOK_DETAILS_URL + "appui=alaxs&gid="
				+ book.getGid() + "&dir=1&ajax=1";
		bookDownloadInfo.setLastUrl(url);
		db.saveBindingId(bookDownloadInfo);

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

}
