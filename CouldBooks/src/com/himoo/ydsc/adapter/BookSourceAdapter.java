package com.himoo.ydsc.adapter;

import java.util.List;

import android.content.Context;

import com.himoo.ydsc.R;
import com.himoo.ydsc.base.quickadapter.BaseAdapterHelper;
import com.himoo.ydsc.base.quickadapter.QuickAdapter;
import com.himoo.ydsc.bean.BookSource;

public class BookSourceAdapter extends QuickAdapter<BookSource> {

	public BookSourceAdapter(Context context, int layoutResId) {
		super(context, layoutResId);
		// TODO Auto-generated constructor stub
	}

	public BookSourceAdapter(Context context, int layoutResId,
			List<BookSource> data) {
		super(context, layoutResId, data);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void convert(BaseAdapterHelper helper, BookSource item) {
		// TODO Auto-generated method stub
		helper.setText(R.id.book_source_net, item.getDomain());
		helper.setText(R.id.book_source_chapter, item.getChapter_title());
		
	}

}
