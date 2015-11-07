package com.himoo.ydsc.db.bean;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Table;

/**
 * 搜索记录 查找小说的历史记录
 * 
 */
// 建议加上注解， 混淆后表名不受影响
@Table(name = "book_search_records")
public class BookSearchRecords extends EntityBase {

	/** 记录的内容 */
	@Column(column = "record")
	public String record;

	public String getRecord() {
		return record;
	}

	public void setRecord(String record) {
		this.record = record;
	}
	
	

}
