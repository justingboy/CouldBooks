package com.himoo.ydsc.reader.bean;

/**
 * 每章节的内容
 * 
 */
public class Chapter {
	/** 标题 */
	private String bookName;
	/** 每章的内容 */
	private String content;
	/** 顺序 */
	private String index;
	/** 每章节的位置 */
	private int position;
	/** 章节的名字 */
	private String chapterName;
	/** 章节内容的地址 */
	private String chapterUrl;
	/** 书的封面地址 */
	private String coverImageUrl;

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}

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

	public String getChapterUrl() {
		return chapterUrl;
	}

	public void setChapterUrl(String chapterUrl) {
		this.chapterUrl = chapterUrl;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public String getCoverImageUrl() {
		return coverImageUrl;
	}

	public void setCoverImageUrl(String coverImageUrl) {
		this.coverImageUrl = coverImageUrl;
	}

}
