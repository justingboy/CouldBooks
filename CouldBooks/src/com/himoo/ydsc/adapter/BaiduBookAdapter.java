package com.himoo.ydsc.adapter;

import java.util.List;

import android.content.Context;

import com.himoo.ydsc.R;
import com.himoo.ydsc.base.BaseApplication;
import com.himoo.ydsc.base.quickadapter.BaseAdapterHelper;
import com.himoo.ydsc.base.quickadapter.QuickAdapter;
import com.himoo.ydsc.bean.BaiduBook;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

public class BaiduBookAdapter extends QuickAdapter<BaiduBook>{

	/**下载图片的配置参数 */
	private DisplayImageOptions option;

	public BaiduBookAdapter(Context context, int layoutResId) {
		super(context, layoutResId);
		option = BaseApplication.getInstance().displayImageOptionsBuider(R.drawable.book_face_default);
	}
	

	public BaiduBookAdapter(Context context, int layoutResId, List<BaiduBook> data) {
		super(context, layoutResId, data);
		// TODO Auto-generated constructor stub
		option = BaseApplication.getInstance().displayImageOptionsBuider(R.drawable.book_face_default);
	}


	@Override
	protected void convert(BaseAdapterHelper helper, BaiduBook item) {
		// TODO Auto-generated method stub

		helper.setImageUrl(R.id.book_image, item.getCoverImage(), option);
		helper.setText(R.id.book_name, item.getTitle());
		helper.setTextColorRes(R.id.book_name,R.color.main_bottom_textcolor_press );
		
	}

}
