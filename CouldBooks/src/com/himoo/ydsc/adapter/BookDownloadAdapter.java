package com.himoo.ydsc.adapter;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.List;

import android.content.Context;

import com.himoo.ydsc.R;
import com.himoo.ydsc.base.BaseApplication;
import com.himoo.ydsc.base.quickadapter.BaseAdapterHelper;
import com.himoo.ydsc.base.quickadapter.QuickAdapter;
import com.himoo.ydsc.config.BookTheme;
import com.himoo.ydsc.download.BookDownloadInfo;
import com.himoo.ydsc.download.BookDownloadManager;
import com.himoo.ydsc.ui.utils.Toast;
import com.himoo.ydsc.util.RegularUtil;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
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
		option = BaseApplication.getInstance().displayImageOptionsBuider(
				BookTheme.BOOK_COVER);
		mContext = context;
	}

	@Override
	protected void convert(BaseAdapterHelper helper, BookDownloadInfo item) {
		// TODO Auto-generated method stub
		String imageUrl = RegularUtil.converUrl(item.getBookCoverImageUrl());
		helper.setImageUrl(R.id.shelf_book_image,imageUrl,
				option);
		helper.setVisible(R.id.shelf_delected_box, isSelectedState);
		if (isChoice)
			helper.setImageResource(R.id.shelf_delected_box,
					R.drawable.shelf_left_feedback_help_check);
		else
			helper.setImageResource(R.id.shelf_delected_box,
					R.drawable.help_uncheck);
		helper.setText(R.id.shelf_book_name, item.getBookName());
		int progress = 0;
		if (item.getFileLength() > 0) {
			progress = (int) (item.getProgress() * 100 / item.getFileLength());
		}
		helper.setProgress(R.id.book_download_pb, progress);
		HttpHandler<File> handler = item.getHandler();
		if (handler != null) {
			Log.d("handler");
			@SuppressWarnings("rawtypes")
			RequestCallBack callBack = handler.getRequestCallBack();
			if (callBack instanceof BookDownloadManager.ManagerCallBack) {
				BookDownloadManager.ManagerCallBack managerCallBack = (BookDownloadManager.ManagerCallBack) callBack;
				if (managerCallBack.getBaseCallBack() == null) {
					managerCallBack
							.setBaseCallBack(new DownloadRequestCallBack(item));
				}
			}
			callBack.setUserTag(new WeakReference<BaseAdapterHelper>(helper));
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
				
				android.util.Log.i("msg","progress="+progress);
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
			Toast.showLong(mContext, "《"+item.getBookName()+"》下载完成");
		}

		@Override
		public void onFailure(HttpException error, String msg) {
			refreshListItem();
			Toast.showLong(mContext, "《"+item.getBookName()+"》下载失败,请查看网络！");
		}

		@Override
		public void onCancelled() {
			refreshListItem();
		}
	}

}
