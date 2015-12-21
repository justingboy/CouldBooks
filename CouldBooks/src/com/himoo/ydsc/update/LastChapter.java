package com.himoo.ydsc.update;

public class LastChapter {
	String bookName;
	String chapterName;

	public LastChapter() {

	}

	public LastChapter(String bookName, String chapterName) {
		this.bookName = bookName;
		this.chapterName = chapterName;
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

}