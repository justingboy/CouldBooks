package com.himoo.ydsc.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class LastChapter implements Parcelable {

	private String index;
	private String rank;
	private String text;
	private String href;

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

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeString(index);
		dest.writeString(rank);
		dest.writeString(text);
		dest.writeString(href);

	}

	public static final Parcelable.Creator<LastChapter> CREATOR = new Parcelable.Creator<LastChapter>() {
		public LastChapter createFromParcel(Parcel in) {
			LastChapter chapter = new LastChapter();
			chapter.setIndex(in.readString());
			chapter.setRank(in.readString());
			chapter.setText(in.readString());
			chapter.setHref(in.readString());

			return chapter;
		}

		public LastChapter[] newArray(int size) {
			return new LastChapter[size];
		}
	};

}