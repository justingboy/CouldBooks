package com.himoo.ydsc.reader.dao;

import com.himoo.ydsc.db.bean.EntityBase;
import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Table;

@Table(name = "book_mark")
public class BookMark extends EntityBase{
	
	@Column(column = "bookId")
	private String bookId;
	
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

	public String getBookId() {
		return bookId;
	}

	public void setBookId(String bookId) {
		this.bookId = bookId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((bookId == null) ? 0 : bookId.hashCode());
		result = prime * result
				+ ((bookName == null) ? 0 : bookName.hashCode());
		result = prime * result
				+ ((chapterName == null) ? 0 : chapterName.hashCode());
		result = prime * result + currentPage;
		result = prime * result + pageCount;
		result = prime * result + position;
		result = prime * result + type;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BookMark other = (BookMark) obj;
		if (bookId == null) {
			if (other.bookId != null)
				return false;
		} else if (!bookId.equals(other.bookId))
			return false;
		if (bookName == null) {
			if (other.bookName != null)
				return false;
		} else if (!bookName.equals(other.bookName))
			return false;
		if (chapterName == null) {
			if (other.chapterName != null)
				return false;
		} else if (!chapterName.equals(other.chapterName))
			return false;
		if (currentPage != other.currentPage)
			return false;
		if (pageCount != other.pageCount)
			return false;
		if (position != other.position)
			return false;
		if (type != other.type)
			return false;
		return true;
	}

	
	

}
