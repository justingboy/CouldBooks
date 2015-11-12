package com.himoo.ydsc.adapter;

import java.util.List;

import android.content.Context;

import com.himoo.ydsc.R;
import com.himoo.ydsc.base.BaseApplication;
import com.himoo.ydsc.base.quickadapter.BaseAdapterHelper;
import com.himoo.ydsc.base.quickadapter.QuickAdapter;
import com.himoo.ydsc.bean.DouBanBookComment;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

public class DouBanCommentAdapter extends QuickAdapter<DouBanBookComment> {

	/** 下载图片的配置参数 */
	private DisplayImageOptions option;

	public DouBanCommentAdapter(Context context, int layoutResId) {
		super(context, layoutResId);
		option = BaseApplication.getInstance().displayImageOptionsBuider(
				R.drawable.book_face_default);
	}

	public DouBanCommentAdapter(Context context, int layoutResId,
			List<DouBanBookComment> data) {
		super(context, layoutResId, data);
		// TODO Auto-generated constructor stub
		option = BaseApplication.getInstance().displayImageOptionsBuider(
				R.drawable.book_face_default);
	}

	@Override
	protected void convert(BaseAdapterHelper helper, DouBanBookComment item) {
		// TODO Auto-generated method stub

		helper.setImageUrl(R.id.douban_comment_image, item.getCommentImage(),option);
		helper.setText(R.id.douban_comment_name, item.getCommentTitle());
		helper.setText(R.id.douban_comment_title, item.getCommentName());
		String date = item.getCommentDate().substring(0, item.getCommentDate().indexOf("+")).replace("T", " ");
		helper.setText(R.id.douban_comment_date, date);
		helper.setText(R.id.douban_comment_summary,item.getCommentSummary().trim());
				
						

	}
}
