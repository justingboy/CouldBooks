package com.himoo.ydsc.adapter;

import java.util.List;

import android.content.Context;

import com.himoo.ydsc.R;
import com.himoo.ydsc.base.BaseApplication;
import com.himoo.ydsc.base.quickadapter.BaseAdapterHelper;
import com.himoo.ydsc.base.quickadapter.QuickAdapter;
import com.himoo.ydsc.bean.BaiduBook;
import com.himoo.ydsc.config.BookTheme;
import com.himoo.ydsc.util.RegularUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

/**
 * 搜索结果返回的数据 Adapter
 * 
 */
public class SearchBaiduBookAdapter extends QuickAdapter<BaiduBook> {

	/** 下载图片的配置参数 */
	private DisplayImageOptions option;

	public SearchBaiduBookAdapter(Context context, int layoutResId) {
		super(context, layoutResId);
		option = BaseApplication.getInstance().displayImageOptionsBuider(
				BookTheme.BOOK_COVER);
	}

	public SearchBaiduBookAdapter(Context context, int layoutResId,
			List<BaiduBook> data) {
		super(context, layoutResId, data);
		// TODO Auto-generated constructor stub
		option = BaseApplication.getInstance().displayImageOptionsBuider(
				BookTheme.BOOK_COVER);
	}

	@Override
	protected void convert(BaseAdapterHelper helper, BaiduBook item) {
		// TODO Auto-generated method stub
		
		String imageUrl = RegularUtil.converUrl(item.getCoverImage());
		Log.d(imageUrl);

		helper.setImageUrl(R.id.search_book_image, imageUrl, option);
		helper.setText(R.id.search_book_name, item.getTitle());
		helper.setTextColor(R.id.search_book_name, BookTheme.THEME_COLOR);
		helper.setText(R.id.search_book_author, item.getAuthor());
		helper.setText(R.id.search_book_summary, item.getSummary().trim());
		if (item.getStatus().equals("完结")) {
			helper.setImageResource(R.id.book_statue,
					R.drawable.story_state_finished);
		} else {
			helper.setImageResource(R.id.book_statue,
					R.drawable.story_state_writting);
		}

	}
}
