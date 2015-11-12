package com.himoo.ydsc.bean;

/**
 * 豆瓣评书信息
 * 
 */
public class DoubanBook {
	/** 书的封面地址 */
	private String coverImageUrl;
	/** 书名 */
	private String bookName;
	/** 书的作者 */
	private String bookAuthor;
	/** 书的出版地 */
	private String bookPublisher;
	/** 书的出版时间 */
	private String bookPubdate;
	/** 平均评分 */
	private String bookAverage;
	/** 书评分次数 */
	private String bookNumRaters;
	/** 书评第二个界面的地址 */
	private String bookDetailsUrl;

	public String getBookPubdate() {
		return bookPubdate;
	}

	public void setBookPubdate(String bookPubdate) {
		this.bookPubdate = bookPubdate;
	}

	public String getCoverImageUrl() {
		return coverImageUrl;
	}

	public void setCoverImageUrl(String coverImageUrl) {
		this.coverImageUrl = coverImageUrl;
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

	public String getBookPublisher() {
		return bookPublisher;
	}

	public void setBookPublisher(String bookPublisher) {
		this.bookPublisher = bookPublisher;
	}

	public String getBookAverage() {
		return bookAverage;
	}

	public void setBookAverage(String bookAverage) {
		this.bookAverage = bookAverage;
	}

	public String getBookNumRaters() {
		return bookNumRaters;
	}

	public void setBookNumRaters(String bookNumRaters) {
		this.bookNumRaters = bookNumRaters;
	}

	public String getBookDetailsUrl() {
		return bookDetailsUrl;
	}

	public void setBookDetailsUrl(String bookDetailsUrl) {
		this.bookDetailsUrl = bookDetailsUrl;
	}

}
