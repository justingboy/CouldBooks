package com.himoo.ydsc.bean;

/**
 * 百度小说章节 
 *
 */
public class BaiduBookChapter {
	private String create_time;
	private String index;
	private String rank;
	private String text;
	private String href;
	private String cid;

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

}