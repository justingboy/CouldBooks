package com.himoo.ydsc.adapter;

import java.util.List;

import android.R;
import android.content.Context;

import com.himoo.ydsc.base.quickadapter.BaseAdapterHelper;
import com.himoo.ydsc.base.quickadapter.QuickAdapter;
import com.himoo.ydsc.bean.BookClassify;
import com.himoo.ydsc.config.BookTheme;

/**
 * 书籍分类Adapter
 *
 */
public class BookClassifyAdapter extends QuickAdapter<BookClassify>{


	public BookClassifyAdapter(Context context, int layoutResId,
			List<BookClassify> data) {
		super(context, layoutResId, data);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void convert(BaseAdapterHelper helper, BookClassify item) {
		// TODO Auto-generated method stub
		helper.setText(R.id.text1, item.getClass_Name());
		helper.setTextColor(R.id.text1, BookTheme.THEME_COLOR);
		
	}

}
