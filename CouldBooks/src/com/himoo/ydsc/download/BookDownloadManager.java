package com.himoo.ydsc.download;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.himoo.ydsc.base.BaseApplication;
import com.himoo.ydsc.bean.BookDetails;
import com.himoo.ydsc.reader.dao.BookMark;
import com.himoo.ydsc.util.FileUtils;
import com.himoo.ydsc.util.MyLogger;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.db.converter.ColumnConverter;
import com.lidroid.xutils.db.sqlite.ColumnDbType;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.util.LogUtils;

/**
 * 下载管理类
 * 
 */
public class BookDownloadManager {

	/** 下载书籍的集合 */
	private ArrayList<BookDownloadInfo> downloadInfoList;
	/** 下载使用的线程数 */
	private int maxDownloadThread = 1;
	/** 上下文 */
	private Context mContext;
	/** 数据库 */
	private DbUtils db;
	/** 下载的保存路径 */
	private String bookSavePath;

	public BookDownloadManager(Context appContext) {
		FileUtils fileUtils = new FileUtils(appContext);
		bookSavePath = fileUtils.getStorageDirectory();
		// ColumnConverterFactory.registerColumnConverter(HttpHandler.State.class,
		// new HttpHandlerStateConverter());
		mContext = appContext;
		db = DbUtils.create(mContext, "Book");
		db.configAllowTransaction(true);
		try {
			downloadInfoList = (ArrayList<BookDownloadInfo>) db
					.findAll(BookDownloadInfo.class);
		} catch (DbException e) {
			LogUtils.e(e.getMessage(), e);
		}
		if (downloadInfoList == null) {
			downloadInfoList = new ArrayList<BookDownloadInfo>();
		}
	}

	/** 获取所有的书籍 */
	public List<BookDownloadInfo> getAllBook() {
		try {
			return db.findAll(BookDownloadInfo.class);
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	/**
	 * 获取数据库中下载书籍的个数
	 * 
	 * @return int
	 */
	public int getDownloadInfoListCount() {
		return downloadInfoList.size();
	}

	/**
	 * 查询已经阅读过的书
	 * 
	 * @return
	 */
	public int getReaderBookCount() {

		try {
			List<BookDownloadInfo> list = db.findAll(Selector.from(
					BookDownloadInfo.class).where("bookIsRead", "=", true));
			if (list != null)
				return list.size();
		} catch (DbException e) {
			// TODO Auto-generated catch block
			MyLogger.kLog().e(e);
			return getDownloadInfoListCount();
		}

		return getDownloadInfoListCount();
	}

	/**
	 * 获取某个书籍的下载情况
	 * 
	 * @param index
	 * @return
	 */
	public BookDownloadInfo getDownloadInfo(int index) {
		return downloadInfoList.get(index);
	}

	/**
	 * 获取数据中所有下载的书
	 * 
	 * @return
	 */
	public ArrayList<BookDownloadInfo> getAllBookDownloadInfo() {
		Collections.reverse(downloadInfoList);

		return downloadInfoList;
	}

	/**
	 * 删除选中的书籍列表
	 * 
	 * @param list
	 */
	public void deletedBookList(List<BookDownloadInfo> list) {
		try {
			db.deleteAll(list);
		} catch (DbException e) {
			// TODO Auto-generated catch block
			Log.i("msg", "删除失败：" + e.getMessage());
		}

	}

	/**
	 * 刷新数据库
	 */
	public void refreshBookList() {
		try {
			downloadInfoList = (ArrayList<BookDownloadInfo>) db
					.findAll(BookDownloadInfo.class);
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 查询数据库中最新插入一条数据
	 * @param bookName
	 * @param bookId
	 * @param dowloadUrl
	 * @return
	 */
	public List<BookDownloadInfo> findLastBookInfo(String bookName,
			String bookId, String dowloadUrl) {

		try {
			List<BookDownloadInfo> list = db.findAll(Selector
					.from(BookDownloadInfo.class)
					.where("downloadUrl", "=", dowloadUrl)
					.and("bookName", "=", bookName).and("bookId", "=", bookId));
			return list;
		} catch (DbException e) {
			// TODO Auto-generated catch block
			return null;
		}
	}

	/**
	 * 判断数据库中是否已经下载过
	 * 
	 * @param book
	 * @return
	 */
	public boolean isDownload(BookDetails book) {
		try {
			List<BookDownloadInfo> list = db.findAll(Selector
					.from(BookDownloadInfo.class)
					.where("downloadUrl", "=", book.getBook_Download())
					.and("bookId", "=", book.getBook_ID()+"")
					.and("bookName", "=", book.getBook_Name()));
			if (list == null || list.isEmpty())
				return false;
			else
				return true;

		} catch (DbException e) {
			return false;
		}

	}

	/**
	 * 判断数据库中是否已经下载过
	 * @param bookName
	 * @param bookId
	 * @return
	 */
	public BookDownloadInfo querryByBookName(String bookName, String bookId) {
		try {
			List<BookDownloadInfo> list = db.findAll(Selector
					.from(BookDownloadInfo.class)
					.where("bookName", "=", bookName)
					.and("bookId", "=", bookId));
			if (list != null && !list.isEmpty())

				return list.get(0);
			else
				return null;

		} catch (DbException e) {
			return null;
		}

	}

	/**
	 * 根据书名判断该书是否下载过
	 * @param bookName
	 * @param bookId
	 * @return
	 */
	public boolean isDownload(String bookName, String bookId) {
		try {
			List<BookDownloadInfo> list = db.findAll(Selector
					.from(BookDownloadInfo.class)
					.where("bookName", "=", bookName)
					.and("bookId", "=", bookId));
			if (list == null || list.isEmpty())
				return false;
			else
				return true;

		} catch (DbException e) {
			return false;
		}

	}

	/**
	 * 按照关键字查找书籍
	 * 
	 * @param keyword
	 * @return
	 */
	public List<BookDownloadInfo> queryByKeyword(String keyword) {
		List<BookDownloadInfo> resultList = new ArrayList<BookDownloadInfo>();
		try {
			List<BookDownloadInfo> list = db.findAll(BookDownloadInfo.class);
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i).getBookAuthor()
						.contains(keyword.charAt(0) + "")
						|| list.get(i).getBookName()
								.contains(keyword.charAt(0) + "")) {
					resultList.add(list.get(i));
				}
			}

		} catch (DbException e) {
			return resultList;
		}
		return resultList;

	}

	/**
	 * 添加下载书籍
	 * 
	 * @param downloadUrl
	 *            下载的地址
	 * @param bookName
	 *            下载的书名
	 * @param saveFileName
	 *            下载的保存的文件名
	 * @param autoResume
	 *            如果目标文 件存在，接着未完成的部分继续下载
	 * @param autoRename
	 *            如果从请求返回信息中获取到文件名，下载完成后自动重命名
	 * @param callback
	 *            下载的回调接口
	 * @throws DbException
	 *             异常
	 */
	public void addNewBookDownload(BookDetails bookDetails,
			String saveFileName, boolean autoResume, boolean autoRename,
			final RequestCallBack<File> callback) throws DbException {
		final BaiduInfo bookDownloadInfo = new BaiduInfo();
		bookDownloadInfo.setBookId(bookDetails.getBook_ID() + "");
		bookDownloadInfo.setDownloadUrl(bookDetails.getBook_Download());
		bookDownloadInfo.setAutoRename(autoRename);
		bookDownloadInfo.setAutoResume(false);
		bookDownloadInfo.setBookStatue("完结");
		bookDownloadInfo.setDownSuccess(false);
		bookDownloadInfo.setSerialize(false);
		bookDownloadInfo.setBookSourceType(1);
		bookDownloadInfo.setBookName(bookDetails.getBook_Name());
		bookDownloadInfo.setBookAuthor(bookDetails.getBook_Author());
		bookDownloadInfo.setBookLastUpdateTime(System.currentTimeMillis() + "");
		bookDownloadInfo.setBookCoverImageUrl(bookDetails.getBook_Image());
		bookDownloadInfo.setBookIsRead(false);
		bookDownloadInfo.setReaderTime(0L);
		bookDownloadInfo.setBookReadHository("此书您还没有阅读!");
		bookDownloadInfo.setLastReaderProgress("0%");
		bookDownloadInfo.setBookReadProgress(0L);
		bookDownloadInfo.setFileSavePath(bookSavePath + saveFileName);
		db.saveBindingId(bookDownloadInfo);
		BaseApplication.getInstance().putHashMap(
				bookDetails.getBook_Download(), bookDownloadInfo);

	}

	
	/**
	 * 保存阅读进度
	 * @param bookMark
	 * @param chapterNum
	 */
	public void updateReaderProgress(BookMark bookMark, int chapterNum) {
		List<BaiduInfo> list;
		try {
			list = db.findAll(Selector.from(BaiduInfo.class)
					.where("bookName", "=", bookMark.getBookName())
					.and("bookId", "=", bookMark.getBookId()));
			if (list != null && !list.isEmpty()) {
				BaiduInfo bookinfo = list.get(0);
				bookinfo.setBookReadHository(bookMark.getChapterName());
				bookinfo.setBookIsRead(true);
				DecimalFormat df = new DecimalFormat("#.#");
				double progress = 100 * ((double) bookMark.getPosition() / chapterNum);
				bookinfo.setLastReaderProgress(df.format(progress) + "%");
				db.update(bookinfo);
			}
		} catch (DbException e) {
			// TODO Auto-generated catch block
			Log.e("msg", e.getMessage());
		}

	}

	/**
	 * 更新所有的更新时间
	 */
	public void updateAllLastTime() {
		try {
			if (downloadInfoList != null && !downloadInfoList.isEmpty()) {
				for (int i = 0; i < downloadInfoList.size(); i++) {
					BookDownloadInfo book = downloadInfoList.get(i);
					book.setBookLastUpdateTime(System.currentTimeMillis() + "");
					db.update(book);
				}
			}
		} catch (DbException e) {
			// TODO Auto-generated catch block
			Log.e("msg", e.getMessage());
		}

	}


	/**
	 * 设置阅读状态
	 * @param bookName
	 * @param bookId
	 */
	public void setBookReader(String bookName, String bookId) {
		List<BaiduInfo> list;
		try {
			list = db.findAll(Selector.from(BaiduInfo.class)
					.where("bookName", "=", bookName)
					.and("bookId", "=", bookId));
			if (list != null && !list.isEmpty()) {
				BaiduInfo bookinfo = list.get(0);
				bookinfo.setBookIsRead(true);
				db.update(bookinfo);
			}
		} catch (DbException e) {
			Log.e("msg", e.getMessage());
		}

	}

	/**
	 * 恢复下载
	 * 
	 * @param index
	 * @param callback
	 * @throws DbException
	 */
	public void resumeDownload(int index, final RequestCallBack<File> callback)
			throws DbException {
		final BookDownloadInfo BookDownloadInfo = downloadInfoList.get(index);
		resumeDownload(BookDownloadInfo, callback);
	}

	/**
	 * 恢复下载
	 * 
	 * @param BookDownloadInfo
	 * @param callback
	 * @throws DbException
	 */
	public void resumeDownload(BookDownloadInfo BookDownloadInfo,
			final RequestCallBack<File> callback) throws DbException {
		HttpUtils http = new HttpUtils();
		http.configRequestThreadPoolSize(maxDownloadThread);
		HttpHandler<File> handler = http.download(BookDownloadInfo
				.getDownloadUrl(), BookDownloadInfo.getFileSavePath(),
				BookDownloadInfo.isAutoResume(), BookDownloadInfo
						.isAutoRename(), new ManagerCallBack(BookDownloadInfo,
						callback));
		BookDownloadInfo.setHandler(handler);
		BookDownloadInfo.setState(handler.getState());
		db.saveOrUpdate(BookDownloadInfo);
	}

	/**
	 * 取消下载
	 * 
	 * @param index
	 * @throws DbException
	 */
	public void removeDownload(int index) throws DbException {
		BookDownloadInfo BookDownloadInfo = downloadInfoList.get(index);
		removeDownload(BookDownloadInfo);
	}

	/**
	 * 取消下载
	 * 
	 * @param BookDownloadInfo
	 * @throws DbException
	 */
	public void removeDownload(BookDownloadInfo BookDownloadInfo)
			throws DbException {
		HttpHandler<File> handler = BookDownloadInfo.getHandler();
		if (handler != null && !handler.isCancelled()) {
			handler.cancel();
		}
		downloadInfoList.remove(BookDownloadInfo);
		db.delete(BookDownloadInfo);
	}


	/**
	 * 设置下载完成
	 * @param bookName
	 * @param bookId
	 * @param isSuccess
	 */
	public void updateDownSuccess(String bookName, String bookId,
			boolean isSuccess) {
		List<BaiduInfo> list;
		try {
			list = db.findAll(Selector.from(BaiduInfo.class)
					.where("bookName", "=", bookName)
					.and("bookId", "=", bookId));
			if (list != null && !list.isEmpty()) {
				BaiduInfo bookinfo = list.get(0);
				bookinfo.setAutoResume(isSuccess);
				db.update(bookinfo);
			}
		} catch (DbException e) {
			// TODO Auto-generated catch block
			Log.e("msg", e.getMessage());
		}
	}


	/**
	 * 查询状态 下载是否完成
	 * @param bookName
	 * @param bookId
	 * @return
	 */
	public boolean queryBookDownSuccess(String bookName, String bookId) {

		List<BaiduInfo> list;
		try {
			list = db.findAll(Selector.from(BaiduInfo.class)
					.where("bookName", "=", bookName)
					.and("bookId", "=", bookId));
			if (list != null && !list.isEmpty()) {
				BaiduInfo bookinfo = list.get(0);
				return bookinfo.isAutoResume();
			}
		} catch (DbException e) {
			// TODO Auto-generated catch block
			Log.e("msg", e.getMessage());
		}

		return false;
	}

	/**
	 * 暂停下载
	 * 
	 * @param index
	 * @throws DbException
	 */
	public void stopDownload(int index) throws DbException {
		BookDownloadInfo BookDownloadInfo = downloadInfoList.get(index);
		stopDownload(BookDownloadInfo);
	}

	/**
	 * 暂停下载
	 * 
	 * @param BookDownloadInfo
	 * @throws DbException
	 */
	public void stopDownload(BookDownloadInfo BookDownloadInfo)
			throws DbException {
		HttpHandler<File> handler = BookDownloadInfo.getHandler();
		if (handler != null && !handler.isCancelled()) {
			handler.cancel();
		} else {
			BookDownloadInfo.setState(HttpHandler.State.CANCELLED);
		}
		db.saveOrUpdate(BookDownloadInfo);
	}

	/**
	 * 暂停所有的下载
	 * 
	 * @throws DbException
	 */
	public void stopAllDownload() throws DbException {
		for (BookDownloadInfo BookDownloadInfo : downloadInfoList) {
			HttpHandler<File> handler = BookDownloadInfo.getHandler();
			if (handler != null && !handler.isCancelled()) {
				handler.cancel();
			} else {
				BookDownloadInfo.setState(HttpHandler.State.CANCELLED);
			}
		}
		db.saveOrUpdateAll(downloadInfoList);
	}

	/**
	 * 在Activity被销毁的时候备份下载的信息
	 * 
	 * @throws DbException
	 */
	public void backupDownloadInfoList() throws DbException {
		for (BookDownloadInfo BookDownloadInfo : downloadInfoList) {
			HttpHandler<File> handler = BookDownloadInfo.getHandler();
			if (handler != null) {
				BookDownloadInfo.setState(handler.getState());
			}
		}
		db.saveOrUpdateAll(downloadInfoList);
	}

	/**
	 * 获取最大的线程数
	 * 
	 * @return
	 */
	public int getMaxDownloadThread() {
		return maxDownloadThread;
	}

	/**
	 * 设置最大的下载线程数
	 * 
	 * @param maxDownloadThread
	 */
	public void setMaxDownloadThread(int maxDownloadThread) {
		this.maxDownloadThread = maxDownloadThread;
	}

	/**
	 * 下载的回调接口
	 * 
	 */
	public class ManagerCallBack extends RequestCallBack<File> {
		private BookDownloadInfo BookDownloadInfo;
		private RequestCallBack<File> baseCallBack;

		public RequestCallBack<File> getBaseCallBack() {
			return baseCallBack;
		}

		public void setBaseCallBack(RequestCallBack<File> baseCallBack) {
			this.baseCallBack = baseCallBack;
		}

		private ManagerCallBack(BookDownloadInfo BookDownloadInfo,
				RequestCallBack<File> baseCallBack) {
			this.baseCallBack = baseCallBack;
			this.BookDownloadInfo = BookDownloadInfo;
		}

		@Override
		public Object getUserTag() {
			if (baseCallBack == null)
				return null;
			return baseCallBack.getUserTag();
		}

		@Override
		public void setUserTag(Object userTag) {
			if (baseCallBack == null)
				return;
			baseCallBack.setUserTag(userTag);
		}

		@Override
		public void onStart() {
			HttpHandler<File> handler = BookDownloadInfo.getHandler();
			if (handler != null) {
				BookDownloadInfo.setState(handler.getState());
			}
			try {
				db.saveOrUpdate(BookDownloadInfo);
			} catch (DbException e) {
				LogUtils.e(e.getMessage(), e);
			}
			if (baseCallBack != null) {
				baseCallBack.onStart();
			}
		}

		@Override
		public void onCancelled() {
			HttpHandler<File> handler = BookDownloadInfo.getHandler();
			if (handler != null) {
				BookDownloadInfo.setState(handler.getState());
			}
			try {
				db.saveOrUpdate(BookDownloadInfo);
			} catch (DbException e) {
				LogUtils.e(e.getMessage(), e);
			}
			if (baseCallBack != null) {
				baseCallBack.onCancelled();
			}
		}

		@Override
		public void onLoading(long total, long current, boolean isUploading) {
			HttpHandler<File> handler = BookDownloadInfo.getHandler();
			if (handler != null) {
				BookDownloadInfo.setState(handler.getState());
			}
			BookDownloadInfo.setFileLength(total);
			BookDownloadInfo.setProgress(current);
			try {
				db.saveOrUpdate(BookDownloadInfo);
			} catch (DbException e) {
				LogUtils.e(e.getMessage(), e);
			}
			if (baseCallBack != null) {
				baseCallBack.onLoading(total, current, isUploading);
			}
		}

		@Override
		public void onSuccess(ResponseInfo<File> responseInfo) {
			HttpHandler<File> handler = BookDownloadInfo.getHandler();
			if (handler != null) {
				BookDownloadInfo.setState(handler.getState());
			}
			try {
				db.saveOrUpdate(BookDownloadInfo);
			} catch (DbException e) {
				LogUtils.e(e.getMessage(), e);
			}
			if (baseCallBack != null) {
				baseCallBack.onSuccess(responseInfo);
			}
		}

		@Override
		public void onFailure(HttpException error, String msg) {
			HttpHandler<File> handler = BookDownloadInfo.getHandler();
			if (handler != null) {
				BookDownloadInfo.setState(handler.getState());
			}
			try {
				db.saveOrUpdate(BookDownloadInfo);
			} catch (DbException e) {
				LogUtils.e(e.getMessage(), e);
			}
			if (baseCallBack != null) {
				baseCallBack.onFailure(error, msg);
			}
		}
	}

	private class HttpHandlerStateConverter implements
			ColumnConverter<HttpHandler.State> {

		@Override
		public HttpHandler.State getFieldValue(Cursor cursor, int index) {
			return HttpHandler.State.valueOf(cursor.getInt(index));
		}

		@Override
		public HttpHandler.State getFieldValue(String fieldStringValue) {
			if (fieldStringValue == null)
				return null;
			return HttpHandler.State.valueOf(fieldStringValue);
		}

		@Override
		public Object fieldValue2ColumnValue(HttpHandler.State fieldValue) {
			return fieldValue.value();
		}

		@Override
		public ColumnDbType getColumnDbType() {
			return ColumnDbType.INTEGER;
		}
	}
}
