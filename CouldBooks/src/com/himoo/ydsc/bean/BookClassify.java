package com.himoo.ydsc.bean;

import com.himoo.ydsc.db.bean.EntityBase;
import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Table;

/**
 * 书的请求分类实体类
 * 
 */
@Table(name = "book_classify")
public class BookClassify extends EntityBase {
	/** 分类的名字 */
	@Column(column = "Class_Name")
	private String Class_Name;

	/** 分类的ID */
	@Column(column = "Class_ID")
	private int Class_ID;

	/** 各个类别中书的个数 */
	@Column(column = "booksCount")
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
