package com.himoo.ydsc.bean;

import com.himoo.ydsc.db.bean.EntityBase;
import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Table;

/**
 * 百度书库分类
 * 
 */
@Table(name = "book_baidu_classify")
public class BaiduBookClassify extends EntityBase {
	/** 分类的Id */
	@Column(column = "cateid")
	private int cateid;

	/** 分类的名字 */
	@Column(column = "catename")
	private String catename;

	/** 最新小说书名 */
	@Column(column = "firstnovel")
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
