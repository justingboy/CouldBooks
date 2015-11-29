package com.himoo.ydsc.adapter;

import java.util.List;

import android.R;
import android.content.Context;

import com.himoo.ydsc.base.quickadapter.BaseAdapterHelper;
import com.himoo.ydsc.base.quickadapter.QuickAdapter;
import com.himoo.ydsc.bean.BaiduBookClassify;
import com.himoo.ydsc.config.BookTheme;

/**
 * 书籍分类Adapter
 *
 */
public class BaiduBookClassifyAdapter extends QuickAdapter<BaiduBookClassify>{


	public BaiduBookClassifyAdapter(Context context, int layoutResId,
			List<BaiduBookClassify> data) {
		super(context, layoutResId, data);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void convert(BaseAdapterHelper helper, BaiduBookClassify item) {
		// TODO Auto-generated method stub
		helper.setText(R.id.text1, item.getCatename());
		helper.setTextColor(R.id.text1, BookTheme.THEME_COLOR);
		
	}

}
