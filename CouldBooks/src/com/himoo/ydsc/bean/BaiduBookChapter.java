package com.himoo.ydsc.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.himoo.ydsc.db.bean.EntityBase;
import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Table;
import com.lidroid.xutils.db.annotation.Transient;

/**
 * 百度小说章节
 * 
 */
// 建议加上注解， 混淆后表名不受影响
@Table(name = "book_chapter")
public class BaiduBookChapter extends EntityBase implements Parcelable {

	@Transient
	private String create_time;

	@Transient
	private String index;

	@Transient
	private String rank;

	@Column(column = "text")
	private String text;

	@Column(column = "href")
	private String href;

	@Column(column = "cid")
	private String cid;

	@Transient
	private int position;

	@Transient
	private String chapterName;

	@Transient
	private String chapterUrl;

	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}

	public String getCreate_time() {
		return this.create_time;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	public String getIndex() {
		return this.index;
	}

	public void setRank(String rank) {
		this.rank = rank;
	}

	public String getRank() {
		return this.rank;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getText() {
		return this.text;
	}

	public void setHref(String href) {
		this.href = href;
	}

	public String getHref() {
		return this.href;
	}

	public void setCid(String cid) {
		this.cid = cid;
	}

	public String getCid() {
		return this.cid;
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

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		// 然后按写入的顺序读取出来
		dest.writeString(create_time);
		dest.writeString(index);
		dest.writeString(rank);
		dest.writeString(text);
		dest.writeString(href);
		dest.writeString(cid);
		dest.writeInt(position);
		dest.writeString(chapterName);
		dest.writeString(chapterUrl);
	}

	public static final Parcelable.Creator<BaiduBookChapter> CREATOR = new Parcelable.Creator<BaiduBookChapter>() {
		public BaiduBookChapter createFromParcel(Parcel in) {
			BaiduBookChapter book = new BaiduBookChapter();
			book.setCreate_time(in.readString());
			book.setIndex(in.readString());
			book.setRank(in.readString());
			book.setText(in.readString());
			book.setHref(in.readString());
			book.setCid(in.readString());
			book.setPosition(in.readInt());
			book.setChapterName(in.readString());
			book.setChapterUrl(in.readString());
			return book;
		}

		public BaiduBookChapter[] newArray(int size) {
			return new BaiduBookChapter[size];
		}
	};

}