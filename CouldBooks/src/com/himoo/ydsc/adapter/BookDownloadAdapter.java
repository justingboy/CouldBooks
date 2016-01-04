package com.himoo.ydsc.adapter;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;

import com.himoo.ydsc.R;
import com.himoo.ydsc.base.quickadapter.BaseAdapterHelper;
import com.himoo.ydsc.base.quickadapter.QuickAdapter;
import com.himoo.ydsc.config.BookTheme;
import com.himoo.ydsc.config.SpConstant;
import com.himoo.ydsc.download.BookDownloadInfo;
import com.himoo.ydsc.reader.utils.ZipExtractorTask;
import com.himoo.ydsc.ui.utils.Toast;
import com.himoo.ydsc.util.FileUtils;
import com.himoo.ydsc.util.RegularUtil;
import com.himoo.ydsc.util.SharedPreferences;
import com.himoo.ydsc.util.TimestampUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
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


	public BookDownloadAdapter(Context context, int layoutResId,
			List<BookDownloadInfo> data) {
		super(context, layoutResId, data);
		// TODO Auto-generated constructor stub
		option = displayImageOptionsBuider(BookTheme.BOOK_COVER);
		mContext = context;
	}

	public DisplayImageOptions displayImageOptionsBuider(int resId) {
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showImageOnLoading(resId).showImageForEmptyUri(resId)
				.showImageOnFail(resId).cacheInMemory(true).cacheOnDisk(true)
				.considerExifParams(true).bitmapConfig(Bitmap.Config.ARGB_8888)
				.build();
		return options;
	}

	@Override
	protected void convert(BaseAdapterHelper helper, BookDownloadInfo item) {
		// TODO Auto-generated method stub
		boolean isHorizontal = SharedPreferences.getInstance().getBoolean(
				SpConstant.BOOK_SHELF_DIRECTION, true);
		String imageUrl = RegularUtil.converUrl(item.getBookCoverImageUrl());
		helper.setImageUrl(R.id.shelf_book_image, imageUrl, option);
		// helper.setOnOpenBookListener(R.id.bookView, mListener);
		helper.setVisible(R.id.book_new_label, item.getBookIsRead() ? false
				: true);
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
			} else {
				helper.setTextColor(R.id.shelf_book_current_chapter,
						Color.BLACK);
			}
			helper.setText(R.id.shelf_book_current_chapter,
					"读至:" + item.getBookReadHository());
			helper.setText(R.id.shelf_book_current_progress,
					item.getLastReaderProgress());

		}

	}

	/**
	 * 下载回掉
	 * 
	 */
	public static class DownloadRequestCallBack extends RequestCallBack<File> {
		BookDownloadInfo item;

		public DownloadRequestCallBack(BookDownloadInfo item) {
			this.item = item;
		}

		private void refreshListItem() {
			if (userTag == null)
				return;
			@SuppressWarnings("unchecked")
			WeakReference<BaseAdapterHelper> tag = (WeakReference<BaseAdapterHelper>) userTag;
			BaseAdapterHelper holder = tag.get();
			if (holder != null) {
				int progress = 0;
				if (item.getFileLength() > 0) {
					progress = (int) (item.getProgress() * 100 / item
							.getFileLength());
				}
				holder.setProgress(R.id.book_download_pb, progress);

				android.util.Log.i("msg", "progress=" + progress);
			}
		}

		@Override
		public void onStart() {
			refreshListItem();
		}

		@Override
		public void onLoading(long total, long current, boolean isUploading) {
			refreshListItem();

		}

		@Override
		public void onSuccess(ResponseInfo<File> responseInfo) {
			refreshListItem();
			FileUtils fileUtils = new FileUtils(mContext);
			String inPath = fileUtils.getStorageDirectory()
					+ item.getBookName() + ".zip";
			String outPath = fileUtils.getStorageDirectory()
					+ item.getBookName();
			doZipExtractorWork(item.getBookName(), inPath, outPath);
			Toast.showLong(mContext, "《" + item.getBookName() + "》下载完成");
		}

		@Override
		public void onFailure(HttpException error, String msg) {
			refreshListItem();
			Toast.showLong(mContext, "《" + item.getBookName() + "》下载失败,请查看网络！");
			FileUtils fileUtils = new FileUtils(mContext);
			String inPath = fileUtils.getStorageDirectory()
					+ item.getBookName() + ".zip";
			String outPath = fileUtils.getStorageDirectory()
					+ item.getBookName();
			doZipExtractorWork(item.getBookName(), inPath, outPath);
		}

		@Override
		public void onCancelled() {
			refreshListItem();
		}
	}

	/**
	 * 加压zip文件
	 * 
	 * @param inPath
	 * @param outPath
	 */
	public static void doZipExtractorWork(String bookName, String inPath,
			String outPath) {
		ZipExtractorTask task = new ZipExtractorTask(bookName, inPath, outPath,
				mContext, true);
		task.execute();
	}

}
