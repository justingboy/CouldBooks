package com.himoo.ydsc.ui.utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.himoo.ydsc.R;
import com.himoo.ydsc.bean.BaiduBook;
import com.himoo.ydsc.bean.BookDetails;

public class UIHelper {

	private static final int REQUEST_CODE = 0;

	/**
	 * 跳转到详情界面去
	 * 
	 * @param book
	 * @param className
	 */
	public static void startToActivity(Activity context, BaiduBook book,
			Class<?> className) {
		Intent intent = new Intent(context, className);
		Bundle bundle = new Bundle();
		bundle.putParcelable("book", book);
		intent.putExtras(bundle);
		context.startActivity(intent);
		context.overridePendingTransition(R.anim.activity_zoom_in, 0);

	}

	/**
	 * 跳转到详情界面去
	 * 
	 * @param book
	 * @param className
	 */
	public static void startToActivity(Activity context, BookDetails book,
			Class<?> className) {
		Intent intent = new Intent(context, className);
		Bundle bundle = new Bundle();
		bundle.putParcelable("book", book);
		intent.putExtras(bundle);
		context.startActivity(intent);

	}
	
	/**
	 * 跳转到详情界面去
	 * 
	 * @param book
	 * @param className
	 */
	public static void startToActivity(Activity context, int bookId,
			Class<?> className) {
		Intent intent = new Intent(context, className);
		intent.putExtra("bookId", bookId);
		context.startActivity(intent);

	}

	/**
	 * 跳转到详情界面去
	 * 
	 * @param book
	 * @param className
	 */
	public static void startToActivity(Activity context, Class<?> className) {

		Intent intent = new Intent(context, className);
		context.startActivity(intent);
		// context.overridePendingTransition(R.anim.activity_zoom_in, 0);

	}

	/**
	 * 跳转到详情界面去
	 * 
	 * @param book
	 * @param className
	 */
	public static void startToActivity(Activity context, Class<?> className,
			String value) {

		Intent intent = new Intent(context, className);
		intent.putExtra("key", value);
		context.startActivity(intent);
		context.overridePendingTransition(R.anim.activity_zoom_in, 0);

	}

	/**
	 * 跳转到详情界面去
	 * 
	 * @param book
	 * @param className
	 */
	public static void startForReseltToActivity(Activity context,
			Class<?> className) {
		Intent intent = new Intent(context, className);
		context.startActivityForResult(intent, REQUEST_CODE);
		context.overridePendingTransition(R.anim.activity_zoom_in, 0);

	}

}
