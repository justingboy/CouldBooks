package com.himoo.ydsc.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.himoo.ydsc.R;
import com.himoo.ydsc.base.BaseFragment;
import com.himoo.ydsc.manager.PageManager;
import com.himoo.ydsc.ui.view.BookTitleBar;
import com.himoo.ydsc.util.SharedPreferences;

public class BookShelfFragment extends BaseFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,SharedPreferences sp,
			Bundle savedInstanceState, PageManager pageManager) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_bookshelf, null);
		BookTitleBar titleBar = (BookTitleBar) view.findViewById(R.id.book_titleBar);
		titleBar.setShowSingleTile();
		titleBar.setTitle(getResources().getString(R.string.main_bookshelf));
		return view;
	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub

	}

}
