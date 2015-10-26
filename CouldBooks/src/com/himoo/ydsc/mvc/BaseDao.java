package com.himoo.ydsc.mvc;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
/**
 * 数据库Dao层的基类
 */
public class BaseDao {
	public SQLiteDatabase db;
	public BaseDao(SQLiteOpenHelper helper){
		db = helper.getWritableDatabase();
	}
	public void execSql(String sql){
		db.execSQL(sql);
	}
}