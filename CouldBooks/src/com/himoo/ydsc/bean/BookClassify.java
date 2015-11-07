package com.himoo.ydsc.bean;

/**
 * 书的请求分类实体类
 * 
 */
public class BookClassify {
	/** 分类的名字 */
	private String Class_Name;
	/** 分类的ID */
	private int Class_ID;
	/** 各个类别中书的个数 */
	private int booksCount;

	public String getClass_Name() {
		return Class_Name;
	}

	public void setClass_Name(String class_Name) {
		Class_Name = class_Name;
	}

	public int getClass_ID() {
		return Class_ID;
	}

	public void setClass_ID(int class_ID) {
		Class_ID = class_ID;
	}

	public int getBooksCount() {
		return booksCount;
	}

	public void setBooksCount(int booksCount) {
		this.booksCount = booksCount;
	}

}
