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
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			SharedPreferences sp, Bundle savedInstanceState,
			PageManager pageManager) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_bookshelf, null);
		initTitleBar(view);
		return view;
	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub

	}

	/**
	 * 初始化TitleBar
	 * 
	 * @param view
	 */
	private void initTitleBar(View view) {
		BookTitleBar titleBar = (BookTitleBar) view
				.findViewById(R.id.book_titleBar);
		titleBar.setLeftDrawable(R.drawable.book_deleted);
		titleBar.setLeftTitle("  ");
		View layout = View.inflate(getActivity(),
				R.layout.titlebar_custom_view, null);
		titleBar.setTitleView(layout);

	}

}
