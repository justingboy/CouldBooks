package com.himoo.ydsc.adapter;

import java.util.List;

import android.content.Context;

import com.himoo.ydsc.R;
import com.himoo.ydsc.base.BaseApplication;
import com.himoo.ydsc.base.quickadapter.BaseAdapterHelper;
import com.himoo.ydsc.base.quickadapter.QuickAdapter;
import com.himoo.ydsc.bean.BookSearch;
import com.himoo.ydsc.config.BookTheme;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

/**
 * 搜索结果返回的数据 Adapter
 * 
 */
public class SearchBookAdapter extends QuickAdapter<BookSearch> {

	/** 下载图片的配置参数 */
	private DisplayImageOptions option;

	public SearchBookAdapter(Context context, int layoutResId) {
		super(context, layoutResId);
		option = BaseApplication.getInstance().displayImageOptionsBuider(
				BookTheme.BOOK_COVER);
	}

	public SearchBookAdapter(Context context, int layoutResId,
			List<BookSearch> data) {
		super(context, layoutResId, data);
		// TODO Auto-generated constructor stub
		option = BaseApplication.getInstance().displayImageOptionsBuider(
				BookTheme.BOOK_COVER);
	}

	@Override
	protected void convert(BaseAdapterHelper helper, BookSearch item) {
		// TODO Auto-generated method stub

		Log.d(item.getBook_Image());
		helper.setImageUrl(R.id.search_book_image, item.getBook_Image(),option);
		helper.setText(R.id.search_book_name, item.getBook_Name());
		helper.setTextColor(R.id.search_book_name, BookTheme.THEME_COLOR);
		helper.setText(R.id.search_book_author, item.getBook_Author());
		helper.setText(R.id.search_book_summary, item.getBook_Summary()==null?"暂无简介":item.getBook_Summary().replaceAll("　　", ""));
		if (item.getBook_Yellow()==0) {
			helper.setImageResource(R.id.book_statue,
					R.drawable.story_state_finished); 
		} else {
			helper.setImageResource(R.id.book_statue,
					R.drawable.story_state_writting);
		}

	}
}
