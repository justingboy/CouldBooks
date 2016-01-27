package com.himoo.ydsc.download;

import java.io.File;

import com.himoo.ydsc.db.bean.EntityBase;
import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Table;
import com.lidroid.xutils.db.annotation.Transient;
import com.lidroid.xutils.http.HttpHandler;

/**
 * 下载书的信息 添加注解,混淆后表名/列名不受影响,
 */

@Table(name = "book_BookDownloadInfo")
public class BookDownloadInfo extends EntityBase {

	@Column(column = "bookName")
	private String bookName;

	@Column(column = "bookAuthor")
	private String bookAuthor;

	@Column(column = "bookStatue")
	private String bookStatue;

	@Column(column = "bookReadProgress")
	private long bookReadProgress;
	
	@Column(column = "isDownSuccess")
	private boolean isDownSuccess;

	@Column(column = "bookIsRead")
	private boolean bookIsRead;

	/** 阅读时间（总长） */
	@Column(column = "readerTime")
	private long readerTime;

	@Column(column = "bookLastChapter")
	private String bookLastChapter;

	@Column(column = "bookCoverImageUrl")
	private String bookCoverImageUrl;

	@Column(column = "bookLastUpdateTime")
	private String bookLastUpdateTime;

	@Column(column = "bookReadHository")
	private String bookReadHository;

	@Transient
	private HttpHandler<File> handler;

	@Column(column = "state")
	private HttpHandler.State state;

	@Column(column = "downloadUrl")
	private String downloadUrl;

	@Column(column = "fileSavePath")
	private String fileSavePath;

	@Column(column = "progress")
	private long progress;

	@Column(column = "fileLength")
	private long fileLength;

	@Column(column = "autoResume")
	private boolean autoResume;

	@Column(column = "autoRename")
	private boolean autoRename;

	/** 　1表示自己服务器下载，不需要更新，2表示百度, */
	@Column(column = "bookSourceType")
	private int bookSourceType;

	/** 是不是连载的状态 */
	@Column(column = "isSerialize")
	private boolean isSerialize;

	/** 书的地址，主要解析最新章节那块数据 */
	@Column(column = "lastUrl")
	private String lastUrl;

	/** 最新阅读的位置 */
	@Column(column = "lastReaderProgress")
	private String lastReaderProgress;

	/** 最新章节的名字 */
	@Column(column = "lastChapterName")
	private String lastChapterName;

	public HttpHandler<File> getHandler() {
		return handler;
	}

	public void setHandler(HttpHandler<File> handler) {
		this.handler = handler;
	}

	public HttpHandler.State getState() {
		return state;
	}

	public void setState(HttpHandler.State state) {
		this.state = state;
	}

	public String getDownloadUrl() {
		return downloadUrl;
	}

	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}

	public String getFileSavePath() {
		return fileSavePath;
	}

	public void setFileSavePath(String fileSavePath) {
		this.fileSavePath = fileSavePath;
	}

	public long getProgress() {
		return progress;
	}

	public void setProgress(long progress) {
		this.progress = progress;
	}

	public long getFileLength() {
		return fileLength;
	}

	public void setFileLength(long fileLength) {
		this.fileLength = fileLength;
	}

	public boolean isAutoResume() {
		return autoResume;
	}

	public void setAutoResume(boolean autoResume) {
		this.autoResume = autoResume;
	}

	public boolean isAutoRename() {
		return autoRename;
	}

	public String getLastUrl() {
		return lastUrl;
	}

	public void setLastUrl(String lastUrl) {
		this.lastUrl = lastUrl;
	}

	public String getLastChapterName() {
		return lastChapterName;
	}

	public void setLastChapterName(String lastChapterName) {
		this.lastChapterName = lastChapterName;
	}

	public void setAutoRename(boolean autoRename) {
		this.autoRename = autoRename;
	}

	public String getBookName() {
		return bookName;
	}

	public void setBookName(String bookName) {
		this.bookName = bookName;
	}

	public String getBookAuthor() {
		return bookAuthor;
	}

	public void setBookAuthor(String bookAuthor) {
		this.bookAuthor = bookAuthor;
	}

	public long getBookReadProgress() {
		return bookReadProgress;
	}

	public void setBookReadProgress(long bookReadProgress) {
		this.bookReadProgress = bookReadProgress;
	}

	public boolean getBookIsRead() {
		return bookIsRead;
	}

	public void setBookIsRead(boolean bookIsRead) {
		this.bookIsRead = bookIsRead;
	}

	public String getBookLastChapter() {
		return bookLastChapter;
	}

	public void setBookLastChapter(String bookLastChapter) {
		this.bookLastChapter = bookLastChapter;
	}

	public String getBookLastUpdateTime() {
		return bookLastUpdateTime;
	}

	public void setBookLastUpdateTime(String bookLastUpdateTime) {
		this.bookLastUpdateTime = bookLastUpdateTime;
	}

	public String getBookCoverImageUrl() {
		return bookCoverImageUrl;
	}

	public void setBookCoverImageUrl(String bookCoverImageUrl) {
		this.bookCoverImageUrl = bookCoverImageUrl;
	}

	public String getBookReadHository() {
		return bookReadHository;
	}

	public void setBookReadHository(String bookReadHository) {
		this.bookReadHository = bookReadHository;
	}

	public int getBookSourceType() {
		return bookSourceType;
	}

	public void setBookSourceType(int bookSourceType) {
		this.bookSourceType = bookSourceType;
	}

	public boolean isSerialize() {
		return isSerialize;
	}

	public void setSerialize(boolean isSerialize) {
		this.isSerialize = isSerialize;
	}

	public String getBookStatue() {
		return bookStatue;
	}

	public void setBookStatue(String bookStatue) {
		this.bookStatue = bookStatue;
	}

	public String getLastReaderProgress() {
		return lastReaderProgress;
	}

	public void setLastReaderProgress(String lastReaderProgress) {
		this.lastReaderProgress = lastReaderProgress;
	}

	public long getReaderTime() {
		return readerTime;
	}

	public void setReaderTime(long readerTime) {
		this.readerTime = readerTime;
	}


	public boolean isDownSuccess() {
		return isDownSuccess;
	}

	public void setDownSuccess(boolean isDownSuccess) {
		this.isDownSuccess = isDownSuccess;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof BookDownloadInfo))
			return false;

		BookDownloadInfo that = (BookDownloadInfo) o;

		if (id != that.id)
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		return (int) (id ^ (id >>> 32));
	}

}
