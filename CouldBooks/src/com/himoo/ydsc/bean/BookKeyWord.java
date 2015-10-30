package com.himoo.ydsc.bean;

/**
 * 关键字查找小说
 * 
 */
public class BookKeyWord {
	private int keyword_count;
	private String Keyword;
	private int tempRowNumber;
	private int Keyword_ID;
	private int tempColumn;
	private int keyword_type;

	public void setKeyword_count(int keyword_count) {
		this.keyword_count = keyword_count;
	}

	public int getKeyword_count() {
		return this.keyword_count;
	}

	public void setKeyword(String Keyword) {
		this.Keyword = Keyword;
	}

	public String getKeyword() {
		return this.Keyword;
	}

	public void setTempRowNumber(int tempRowNumber) {
		this.tempRowNumber = tempRowNumber;
	}

	public int getTempRowNumber() {
		return this.tempRowNumber;
	}

	public void setKeyword_ID(int Keyword_ID) {
		this.Keyword_ID = Keyword_ID;
	}

	public int getKeyword_ID() {
		return this.Keyword_ID;
	}

	public void setTempColumn(int tempColumn) {
		this.tempColumn = tempColumn;
	}

	public int getTempColumn() {
		return this.tempColumn;
	}

	public void setKeyword_type(int keyword_type) {
		this.keyword_type = keyword_type;
	}

	public int getKeyword_type() {
		return this.keyword_type;
	}

}