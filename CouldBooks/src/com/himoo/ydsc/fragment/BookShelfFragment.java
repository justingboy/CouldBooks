package com.himoo.ydsc.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.himoo.ydsc.R;
import com.himoo.ydsc.base.BaseFragment;
import com.himoo.ydsc.manager.PageManager;

public class BookShelfFragment extends BaseFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,SharedPreferences sp,
			Bundle savedInstanceState, PageManager pageManager) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_bookshelf, null);
		TextView titlebar = (TextView) view.findViewById(R.id.titlebar);
		titlebar.setText("书架");
		return view;
	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub

	}

}
