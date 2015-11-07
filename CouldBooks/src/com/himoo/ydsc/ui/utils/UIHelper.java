package com.himoo.ydsc.ui.utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.himoo.ydsc.R;
import com.himoo.ydsc.bean.BaiduBook;

public class UIHelper {

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

}
