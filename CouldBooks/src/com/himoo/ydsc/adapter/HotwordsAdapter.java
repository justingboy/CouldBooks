package com.himoo.ydsc.adapter;

import java.util.List;

import android.content.Context;

import com.himoo.ydsc.R;
import com.himoo.ydsc.base.quickadapter.BaseAdapterHelper;
import com.himoo.ydsc.base.quickadapter.QuickAdapter;
import com.himoo.ydsc.config.BookTheme;

public class HotwordsAdapter extends QuickAdapter<String> {

	public HotwordsAdapter(Context context, int layoutResId, List<String> data) {
		super(context, layoutResId, data);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void convert(BaseAdapterHelper helper, String item) {
		// TODO Auto-generated method stub
		if (helper.getPosition() % 2 == 0) {
			helper.setTextRightDrawable(R.id.tv_hotword,
					R.drawable.line_vertical);
		}else{
			helper.setTextRightDrawable(R.id.tv_hotword,-1);
					
		}
		helper.setText(R.id.tv_hotword, item);
		helper.setTextColor(R.id.tv_hotword, BookTheme.THEME_COLOR);

	}

}
