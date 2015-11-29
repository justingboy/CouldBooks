package com.himoo.ydsc.fragment.reader;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.himoo.ydsc.R;

/**
 * 阅读界面设置
 * 
 */
public final class BookSettingFragment2 extends Fragment {

	public static BookSettingFragment2 newInstance() {
		BookSettingFragment2 fragment = new BookSettingFragment2();

		return fragment;
	}

	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_book_setting2, null);

		return view;
	}

}
