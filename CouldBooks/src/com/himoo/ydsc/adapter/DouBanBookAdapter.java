package com.himoo.ydsc.adapter;

import java.util.List;

import android.content.Context;

import com.himoo.ydsc.R;
import com.himoo.ydsc.base.BaseApplication;
import com.himoo.ydsc.base.quickadapter.BaseAdapterHelper;
import com.himoo.ydsc.base.quickadapter.QuickAdapter;
import com.himoo.ydsc.bean.DoubanBook;
import com.himoo.ydsc.config.BookTheme;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

public class DouBanBookAdapter extends QuickAdapter<DoubanBook> {

	/** 下载图片的配置参数 */
	private DisplayImageOptions option;

	public DouBanBookAdapter(Context context, int layoutResId) {
		super(context, layoutResId);
		option = BaseApplication.getInstance().displayImageOptionsBuider(
				BookTheme.BOOK_COVER);
	}

	public DouBanBookAdapter(Context context, int layoutResId,
			List<DoubanBook> data) {
		super(context, layoutResId, data);
		// TODO Auto-generated constructor stub
		option = BaseApplication.getInstance().displayImageOptionsBuider(
				BookTheme.BOOK_COVER);
	}

	@Override
	protected void convert(BaseAdapterHelper helper, DoubanBook item) {
		// TODO Auto-generated method stub

		helper.setImageUrl(R.id.douban_book_image, item.getCoverImageUrl(),
				option);
		helper.setText(R.id.douban_book_name, item.getBookName());
		helper.setTextColor(R.id.douban_book_name, BookTheme.THEME_COLOR);
		helper.setText(R.id.douban_book_author, item.getBookAuthor().equals("")?"暂无":item.getBookAuthor());
		String bookPublisher = item.getBookPublisher().equals("")?"出版社&时间":item.getBookPublisher();
		helper.setText(R.id.douban_book_publisher, bookPublisher+" "+item.getBookPubdate());
		String scroe = item.getBookAverage().equals("") ? "0" : item.getBookAverage();
		helper.setText(R.id.douban_book_averge,scroe+ "分" + "(基于" + item.getBookNumRaters() + "次评分)");
				
						

	}
}
