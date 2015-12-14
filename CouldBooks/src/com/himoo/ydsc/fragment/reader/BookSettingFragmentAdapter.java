package com.himoo.ydsc.fragment.reader;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.viewpagerindicator.IconPagerAdapter;

public class BookSettingFragmentAdapter extends FragmentPagerAdapter implements
		IconPagerAdapter {
	private List<Fragment> fragmentList = new ArrayList<Fragment>();
	private int mCount = 3;

	public BookSettingFragmentAdapter(FragmentManager fm, Context context,String bookName,int position) {
		super(fm);
		fragmentList.add(BookSettingFragment1.newInstance(bookName,position));
		fragmentList.add(BookSettingFragment2.newInstance());
		fragmentList.add(BookSettingFragment3.newInstance(context));
	}

	@Override
	public Fragment getItem(int position) {
		return fragmentList.get(position);
	}

	@Override
	public int getCount() {
		return mCount;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return "";
	}

	@Override
	public int getIconResId(int index) {
		return 0;
	}

	public void setCount(int count) {
		if (count > 0 && count <= 10) {
			mCount = count;
			notifyDataSetChanged();
		}
	}
}