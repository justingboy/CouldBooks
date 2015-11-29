package com.himoo.ydsc.adapter;

import java.util.List;

import android.content.Context;

import com.himoo.ydsc.R;
import com.himoo.ydsc.base.BaseApplication;
import com.himoo.ydsc.base.quickadapter.BaseAdapterHelper;
import com.himoo.ydsc.base.quickadapter.QuickAdapter;
import com.himoo.ydsc.config.BookTheme;
import com.himoo.ydsc.download.BookDownloadInfo;
import com.himoo.ydsc.util.TimestampUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

public class BookQueryAdapter extends QuickAdapter<BookDownloadInfo> {

	/** 下载图片的配置参数 */
	private DisplayImageOptions option;

	public BookQueryAdapter(Context context, int layoutResId) {
		super(context, layoutResId);
		option = BaseApplication.getInstance().displayImageOptionsBuider(
				BookTheme.BOOK_COVER);
	}

	public BookQueryAdapter(Context context, int layoutResId,
			List<BookDownloadInfo> data) {
		super(context, layoutResId, data);
		// TODO Auto-generated constructor stub
		option = BaseApplication.getInstance().displayImageOptionsBuider(
				BookTheme.BOOK_COVER);
	}

	@Override
	protected void convert(BaseAdapterHelper helper, BookDownloadInfo item) {
		// TODO Auto-generated method stub

		helper.setImageUrl(R.id.query_book_image, item.getBookCoverImageUrl(),option);
		helper.setText(R.id.query_book_name, item.getBookName());
		helper.setTextColor(R.id.query_book_name, BookTheme.THEME_COLOR);
		helper.setText(R.id.query_book_author, item.getBookAuthor().equals("")?"暂无":item.getBookAuthor());
		helper.setText(R.id.query_read_hository, item.getBookReadHository());
		helper.setText(R.id.query_update_time, TimestampUtils.formatTimeDuration(item.getBookLastUpdateTime())+"前更新");
				
						

	}
}
