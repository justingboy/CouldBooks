package com.himoo.ydsc.bean;

/**
 * 百度书籍类
 */
public class BaiduBook {
	public LastChapter getLastChapter() {
		return lastChapter;
	}

	public void setLastChapter(LastChapter lastChapter) {
		this.lastChapter = lastChapter;
	}

	private String summary;
	private String reason;
	private int hasNew;
	private long gid;
	private String author;
	private String listurl;
	private String title;
	private int rec_con;
	private String curtitle;
	private String cursrc;
	private int rec_type;
	private String coverImage;
	private String category;
	private String status;
	private LastChapter lastChapter;

	/** 最后一个章节 信息 */
	public class LastChapter {
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

	public void setHasNew(int hasNew) {
		this.hasNew = hasNew;
	}

	public int getHasNew() {
		return this.hasNew;
	}

	public void setGid(long gid) {
		this.gid = gid;
	}

	public long getGid() {
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

	public void setRec_con(int rec_con) {
		this.rec_con = rec_con;
	}

	public int getRec_con() {
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

	public void setRec_type(int rec_type) {
		this.rec_type = rec_type;
	}

	public int getRec_type() {
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

}