package com.himoo.ydsc.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.himoo.ydsc.R;
import com.himoo.ydsc.base.BaseApplication;
import com.himoo.ydsc.base.quickadapter.BaseAdapterHelper;
import com.himoo.ydsc.base.quickadapter.QuickAdapter;
import com.himoo.ydsc.bean.BaiduBook;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

public class BaiduBookAdapter extends QuickAdapter<BaiduBook> {

	/** 下载图片的配置参数 */
	private DisplayImageOptions option;
	private ArrayList<String> coverErrorList;

	public BaiduBookAdapter(Context context, int layoutResId) {
		super(context, layoutResId);
		option = BaseApplication.getInstance().displayImageOptionsBuider(
				R.drawable.book_face_default);
		initList();
	}

	public BaiduBookAdapter(Context context, int layoutResId,
			List<BaiduBook> data) {
		super(context, layoutResId, data);
		// TODO Auto-generated constructor stub
		option = BaseApplication.getInstance().displayImageOptionsBuider(
				R.drawable.book_face_default);
		initList();
	}

	@Override
	protected void convert(BaseAdapterHelper helper, BaiduBook item) {
		// TODO Auto-generated method stub
		if (coverErrorList.contains(item.getTitle())) {
			helper.setImageResource(R.id.book_image,
					R.drawable.book_face_default);
		} else {
			helper.setImageUrl(R.id.book_image, item.getCoverImage(), option);
		}
		helper.setText(R.id.book_name, item.getTitle());
		helper.setTextColorRes(R.id.book_name,
				R.color.main_bottom_textcolor_press);

	}

	/**
	 * 初始化屏蔽图片为空白的书名
	 */
	private void initList() {
		coverErrorList = new ArrayList<String>();
		coverErrorList.add("深渊主宰");
		coverErrorList.add("只求安心");
		coverErrorList.add("武祖血帝");
		coverErrorList.add("天之武神");
		coverErrorList.add("斗战西游");
		coverErrorList.add("北宋闲王");
		coverErrorList.add("重生尹志平");
		coverErrorList.add("契妻只欢不爱");
		coverErrorList.add("闪婚老公不靠谱");
		coverErrorList.add("穿越古代江湖行");
		coverErrorList.add("最强修真高手");
		coverErrorList.add("重生之先婚再爱");
		coverErrorList.add("最后一个阴阳师");
		coverErrorList.add("爱劫难桃总裁独家盛宠");
		coverErrorList.add("全帝国都知道将军要离..");
	}

}
