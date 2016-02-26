package com.himoo.ydsc.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Color;

import com.himoo.ydsc.R;
import com.himoo.ydsc.base.BaseApplication;
import com.himoo.ydsc.base.quickadapter.BaseAdapterHelper;
import com.himoo.ydsc.base.quickadapter.QuickAdapter;
import com.himoo.ydsc.config.BookTheme;
import com.himoo.ydsc.config.SpConstant;
import com.himoo.ydsc.download.BookDownloadInfo;
import com.himoo.ydsc.download.BookDownloadManager;
import com.himoo.ydsc.download.BookDownloadService;
import com.himoo.ydsc.util.RegularUtil;
import com.himoo.ydsc.util.SharedPreferences;
import com.himoo.ydsc.util.TimestampUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

/**
 * 下载书籍的Adapter
 * 
 */
public class BookDownloadAdapter extends QuickAdapter<BookDownloadInfo> {
	/** 下载图片的配置参数 */
	private DisplayImageOptions option;
	public boolean isSelectedState = false;
	public boolean isChoice = false;
	public static Context mContext;
	public BookDownloadManager downloadManager;

	public BookDownloadAdapter(Context context, int layoutResId,
			List<BookDownloadInfo> data) {
		super(context, layoutResId, data);
		// TODO Auto-generated constructor stub
		mContext = context;
		option = BaseApplication.getInstance().displayImageOptionsBuider(
				BookTheme.BOOK_COVER);
		downloadManager = BookDownloadService.getDownloadManager(mContext);
	}

	@Override
	protected void convert(BaseAdapterHelper helper, BookDownloadInfo item) {
		// TODO Auto-generated method stub
		boolean isHorizontal = SharedPreferences.getInstance().getBoolean(
				SpConstant.BOOK_SHELF_DIRECTION, true);
		String imageUrl = RegularUtil.converUrl(item.getBookCoverImageUrl());
		helper.setImageUrl(R.id.shelf_book_image, imageUrl, option);
		boolean isSuccess = item.isAutoResume();
		if (!isSuccess) {
			helper.setAlpha(R.id.shelf_book_image, 0.3f);
		} else {
			helper.setAlpha(R.id.shelf_book_image, 1f);
		}

		helper.setVisible(R.id.book_new_label, item.getBookIsRead() ? false
				: true);

		// helper.setOnOpenBookListener(R.id.bookView, mListener);
		if (isHorizontal) {
			helper.setVisible(R.id.shelf_delected_box, isSelectedState);
			helper.setVisible(R.id.shelf_book_name, true);
			helper.setVisible(R.id.book_shelf_middle_layout, false);
			helper.setVisible(R.id.book_shelf_right_layout, false);
			if (isChoice)
				helper.setImageResource(R.id.shelf_delected_box,
						R.drawable.shelf_left_feedback_help_check);
			else
				helper.setImageResource(R.id.shelf_delected_box,
						R.drawable.help_uncheck);
			helper.setText(R.id.shelf_book_name, item.getBookName());
			helper.setTextColor(R.id.shelf_book_name, BookTheme.THEME_COLOR);
		} else {
			if (isChoice)
				helper.setImageResource(R.id.book_shelf_delected_box,
						R.drawable.shelf_left_feedback_help_check);
			else
				helper.setImageResource(R.id.book_shelf_delected_box,
						R.drawable.help_uncheck);
			helper.setInVisible(R.id.book_shelf_delected_box, isSelectedState);
			helper.setVisible(R.id.shelf_delected_box, false);
			helper.setVisible(R.id.book_shelf_middle_layout, true);
			helper.setVisible(R.id.book_shelf_right_layout, true);
			helper.setVisible(R.id.shelf_book_name, false);
			helper.setText(R.id.shelf_book_name_Vertical, item.getBookName());
			helper.setTextColor(R.id.shelf_book_name_Vertical,
					BookTheme.THEME_COLOR);
			helper.setText(R.id.shelf_book_Author, item.getBookAuthor());
			helper.setText(
					R.id.shelf_book_update_time,
					TimestampUtils.formatTimeDuration(item
							.getBookLastUpdateTime()) + "前更新");
			if (item.getBookReadHository().equals("此书您还没有阅读!")) {
				helper.setTextColor(R.id.shelf_book_current_chapter, Color.RED);
				helper.setText(R.id.shelf_book_current_chapter,
						item.getBookReadHository());
			} else {
				helper.setTextColor(R.id.shelf_book_current_chapter,
						Color.BLACK);
				helper.setText(R.id.shelf_book_current_chapter,
						"读至:" + item.getBookReadHository());
			}
			
			helper.setText(R.id.shelf_book_current_progress,
					item.getLastReaderProgress());

		}

	}
	/**
	 * 刷新 配置
	 */
	public void afreshDisplayOption() {
		this.option = BaseApplication.getInstance().displayImageOptionsBuider(
				BookTheme.BOOK_COVER);
	}

}
