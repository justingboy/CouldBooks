package com.himoo.ydsc.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 书的详细信息
 * 
 */
public class BookDetails implements Parcelable {
	/** 书名 */
	private String Book_Name;
	/** 书的ID */
	private int Book_ID;
	/** 书上线的时间 */
	private String Book_AddTime;
	/** 书的评价 */
	private int Book_Rate;
	/** 书的名气 */
	private int Book_Popularity;
	/** 书的分类ID */
	private int Book_Class_ID;
	/** 书的简介 */
	private String Book_Summary;
	/** 书的封面图片资源 */
	private String Book_Image;
	/** 书的下载地址 */
	private String Book_Download;
	/** 书的作者 */
	private String Book_Author;
	/** 书名 */
	private String Book_Source;
	/** 书的评分 */
	private int Book_RateNum;

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

	public void setBook_Rate(int Book_Rate) {
		this.Book_Rate = Book_Rate;
	}

	public int getBook_Rate() {
		return this.Book_Rate;
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

	public void setBook_Summary(String Book_Summary) {
		this.Book_Summary = Book_Summary;
	}

	public String getBook_Summary() {
		return this.Book_Summary;
	}

	public void setBook_Image(String Book_Image) {
		this.Book_Image = Book_Image;
	}

	public String getBook_Image() {
		return this.Book_Image;
	}

	public void setBook_Download(String Book_Download) {
		this.Book_Download = Book_Download;
	}

	public String getBook_Download() {
		return this.Book_Download;
	}

	public void setBook_Author(String Book_Author) {
		this.Book_Author = Book_Author;
	}

	public String getBook_Author() {
		return this.Book_Author;
	}

	public void setBook_Source(String Book_Source) {
		this.Book_Source = Book_Source;
	}

	public String getBook_Source() {
		return this.Book_Source;
	}

	public void setBook_RateNum(int Book_RateNum) {
		this.Book_RateNum = Book_RateNum;
	}

	public int getBook_RateNum() {
		return this.Book_RateNum;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		// 然后按写入的顺序读取出来
		dest.writeString(Book_Name);
		dest.writeInt(Book_ID);
		dest.writeString(Book_AddTime);
		dest.writeInt(Book_Rate);
		dest.writeInt(Book_Popularity);
		dest.writeInt(Book_Class_ID);
		dest.writeString(Book_Summary);
		dest.writeString(Book_Image);
		dest.writeString(Book_Download);
		dest.writeString(Book_Author);
		dest.writeString(Book_Source);
		dest.writeInt(Book_RateNum);

	}

	public static final Parcelable.Creator<BookDetails> CREATOR = new Parcelable.Creator<BookDetails>() {
		public BookDetails createFromParcel(Parcel in) {
			BookDetails book = new BookDetails();
			book.setBook_Name(in.readString());
			book.setBook_ID(in.readInt());
			book.setBook_AddTime(in.readString());
			book.setBook_Rate(in.readInt());
			book.setBook_Popularity(in.readInt());
			book.setBook_Class_ID(in.readInt());
			book.setBook_Summary(in.readString());
			book.setBook_Image(in.readString());
			book.setBook_Download(in.readString());
			book.setBook_Author(in.readString());
			book.setBook_Source(in.readString());
			book.setBook_RateNum(in.readInt());
			return book;
		}

		public BookDetails[] newArray(int size) {
			return new BookDetails[size];
		}
	};

}