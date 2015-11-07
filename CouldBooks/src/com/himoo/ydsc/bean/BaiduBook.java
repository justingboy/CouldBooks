package com.himoo.ydsc.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 百度书籍类
 */
public class BaiduBook implements Parcelable {

	private String summary;
	private String reason;
	private String hasNew;
	private String gid;
	private String author;
	private String listurl;
	private String title;
	private String rec_con;
	private String curtitle;
	private String cursrc;
	private String rec_type;
	private String coverImage;
	private String category;
	private String status;
	private LastChapter lastChapter;

	public LastChapter getLastChapter() {
		return lastChapter;
	}

	public void setLastChapter(LastChapter lastChapter) {
		this.lastChapter = lastChapter;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getSummary() {
		return this.summary;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getReason() {
		return this.reason;
	}

	public void setHasNew(String hasNew) {
		this.hasNew = hasNew;
	}

	public String getHasNew() {
		return this.hasNew;
	}

	public void setGid(String gid) {
		this.gid = gid;
	}

	public String getGid() {
		return this.gid;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getAuthor() {
		return this.author;
	}

	public void setListurl(String listurl) {
		this.listurl = listurl;
	}

	public String getListurl() {
		return this.listurl;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return this.title;
	}

	public void setRec_con(String rec_con) {
		this.rec_con = rec_con;
	}

	public String getRec_con() {
		return this.rec_con;
	}

	public void setCurtitle(String curtitle) {
		this.curtitle = curtitle;
	}

	public String getCurtitle() {
		return this.curtitle;
	}

	public void setCursrc(String cursrc) {
		this.cursrc = cursrc;
	}

	public String getCursrc() {
		return this.cursrc;
	}

	public void setRec_type(String rec_type) {
		this.rec_type = rec_type;
	}

	public String getRec_type() {
		return this.rec_type;
	}

	public void setCoverImage(String coverImage) {
		this.coverImage = coverImage;
	}

	public String getCoverImage() {
		return this.coverImage;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getCategory() {
		return this.category;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStatus() {
		return this.status;
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
		dest.writeString(summary);
		dest.writeString(reason);
		dest.writeString(hasNew);
		dest.writeString(gid);
		dest.writeString(author);
		dest.writeString(listurl);
		dest.writeString(title);
		dest.writeString(rec_con);
		dest.writeString(curtitle);
		dest.writeString(rec_type);
		dest.writeString(coverImage);
		dest.writeString(category);
		dest.writeString(status);
		dest.writeValue(lastChapter);

	}

	public static final Parcelable.Creator<BaiduBook> CREATOR = new Parcelable.Creator<BaiduBook>() {
		public BaiduBook createFromParcel(Parcel in) {
			BaiduBook book = new BaiduBook();
			book.setSummary(in.readString());
			book.setReason(in.readString());
			book.setHasNew(in.readString());
			book.setGid(in.readString());
			book.setAuthor(in.readString());
			book.setListurl(in.readString());
			book.setTitle(in.readString());
			book.setRec_con(in.readString());
			book.setCurtitle(in.readString());
			book.setRec_type(in.readString());
			book.setCoverImage(in.readString());
			book.setCategory(in.readString());
			book.setStatus(in.readString());
			book.setLastChapter((LastChapter) in.readValue(LastChapter.class
					.getClassLoader()));
			return book;
		}

		public BaiduBook[] newArray(int size) {
			return new BaiduBook[size];
		}
	};

}
