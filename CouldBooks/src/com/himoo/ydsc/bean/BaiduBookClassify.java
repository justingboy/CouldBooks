package com.himoo.ydsc.bean;

/**
 * 百度书库分类
 * 
 */
public class BaiduBookClassify {
	/** 分类的Id */
	private int cateid;
	/** 分类的名字 */
	private String catename;
	/** 最新小说书名 */
	private String firstnovel;

	public int getCateid() {
		return cateid;
	}

	public void setCateid(int cateid) {
		this.cateid = cateid;
	}

	public String getCatename() {
		return catename;
	}

	public void setCatename(String catename) {
		this.catename = catename;
	}

	public String getFirstnovel() {
		return firstnovel;
	}

	public void setFirstnovel(String firstnovel) {
		this.firstnovel = firstnovel;
	}

}
