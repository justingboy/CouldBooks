package com.himoo.ydsc.reader.dao;

import com.himoo.ydsc.db.bean.EntityBase;
import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Table;

@Table(name = "book_mark")
public class BookMark extends EntityBase{
	
	@Column(column = "bookName")
	private String bookName;
	
	@Column(column = "chapterName")
	private String chapterName;
	
	@Column(column = "position")
	private int position;
	
	@Column(column = "currentPage")
	private int currentPage;
	
	@Column(column = "pageCount")
	private int pageCount;
	
	/** type = 1,表示保存的时阅读的进度  type=2表示书签 */
	@Column(column = "type")
	private int type;

	public String getBookName() {
		return bookName;
	}

	public void setBookName(String bookName) {
		this.bookName = bookName;
	}

	public String getChapterName() {
		return chapterName;
	}

	public void setChapterName(String chapterName) {
		this.chapterName = chapterName;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public int getPageCount() {
		return pageCount;
	}

	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	
	

}
