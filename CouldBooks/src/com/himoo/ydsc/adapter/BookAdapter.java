package com.himoo.ydsc.adapter;

import java.util.List;

import android.content.Context;

import com.himoo.ydsc.R;
import com.himoo.ydsc.base.BaseApplication;
import com.himoo.ydsc.base.quickadapter.BaseAdapterHelper;
import com.himoo.ydsc.base.quickadapter.QuickAdapter;
import com.himoo.ydsc.bean.Book;
import com.himoo.ydsc.config.BookTheme;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

public class BookAdapter extends QuickAdapter<Book> {
	/**下载图片的配置参数 */
	private DisplayImageOptions option;

	public BookAdapter(Context context, int layoutResId) {
		super(context, layoutResId);
		option = BaseApplication.getInstance().displayImageOptionsBuider(BookTheme.BOOK_COVER);
	}
	

	public BookAdapter(Context context, int layoutResId, List<Book> data) {
		super(context, layoutResId, data);
		// TODO Auto-generated constructor stub
		option = BaseApplication.getInstance().displayImageOptionsBuider(BookTheme.BOOK_COVER);
	}


	@Override
	protected void convert(BaseAdapterHelper helper, Book item) {
		// TODO Auto-generated method stub

		helper.setImageUrl(R.id.book_image, item.getBook_Image(), option);
		helper.setText(R.id.book_name, item.getBook_Name());
		helper.setTextColor(R.id.book_name, BookTheme.THEME_COLOR);
		
	}

}
