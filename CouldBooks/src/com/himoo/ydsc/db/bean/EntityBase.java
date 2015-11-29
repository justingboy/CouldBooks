package com.himoo.ydsc.db.bean;

import com.lidroid.xutils.db.annotation.Column;

/**
 * 关于数据库使用的实体类，继承该类可省去id再次创建
 */
public abstract class EntityBase{

	// @Id // 如果主键没有命名名为id或_id的时，需要为主键添加此注解
	// @NoAutoIncrement // int,long类型的id默认自增，不想使用自增时添加此注解
	@Column(column = "id")
	protected int id;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
