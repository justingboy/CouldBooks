package com.himoo.ydsc.bean;

public class Book {
	/*** 书名 */
	private String Book_Name;
	/*** 书的ID */
	private int Book_ID;
	/*** 书添加的时间 */
	private String Book_AddTime;
	/*** 书的名气 */
	private int Book_Popularity;
	/*** 书名分类ID */
	private int Book_Class_ID;
	/*** 书的封面图片 */
	private String Book_Image;

	public void setBook_Name(String Book_Name) {
		this.Book_Name = Book_Name;
	}

	public String getBook_Name() {
		return this.Book_Name;
	}

	public void setBook_ID(int Book_ID) {
		this.Book_ID = Book_ID;
	}

	public int getBook_ID() {
		return this.Book_ID;
	}

	public void setBook_AddTime(String Book_AddTime) {
		this.Book_AddTime = Book_AddTime;
	}

	public String getBook_AddTime() {
		return this.Book_AddTime;
	}

	public void setBook_Popularity(int Book_Popularity) {
		this.Book_Popularity = Book_Popularity;
	}

	public int getBook_Popularity() {
		return this.Book_Popularity;
	}

	public void setBook_Class_ID(int Book_Class_ID) {
		this.Book_Class_ID = Book_Class_ID;
	}

	public int getBook_Class_ID() {
		return this.Book_Class_ID;
	}

	public void setBook_Image(String Book_Image) {
		this.Book_Image = Book_Image;
	}

	public String getBook_Image() {
		return this.Book_Image;
	}

}
