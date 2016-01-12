package com.himoo.ydsc.fragment;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.himoo.ydsc.R;
import com.himoo.ydsc.base.BaseFragment;
import com.himoo.ydsc.config.BookTheme;
import com.himoo.ydsc.fragment.subfragment.ClassifySubDetailsFragment;
import com.himoo.ydsc.fragment.subfragment.ClassifySubHotFragment;
import com.himoo.ydsc.manager.PageManager;
import com.himoo.ydsc.ui.utils.ViewSelector;
import com.himoo.ydsc.util.SharedPreferences;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.viewpagerindicator.TabPageIndicator;

public class ClassifyFragment extends BaseFragment {

	/** Tab标题 */
	private static final String[] TITLE = new String[] { "热门分类", "详细分类" };

	/** Fragment */
	private Fragment subHotClassifyFragment = null;
	private Fragment subDetailsClassifyFragment = null;
	private List<Fragment> fragmentList;

	/** TabPageIndicator */
	@ViewInject(R.id.book_pager_indicator)
	private TabPageIndicator tabPageIndicator;

	/** NoScrollViewPager */
	@ViewInject(R.id.viewpager_classify)
	private ViewPager viewPager;

	private RelativeLayout mTitleBar;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			SharedPreferences sp, Bundle savedInstanceState,
			PageManager pageManager) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_classify, null);
		mTitleBar = (RelativeLayout) view
				.findViewById(R.id.classify_tltle_layout);
		mTitleBar.setBackgroundColor(BookTheme.THEME_COLOR);

		return view;
	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub
		initFragmentList();
		TabPageIndicatorAdapter adapter = new TabPageIndicatorAdapter(
				getChildFragmentManager());
		viewPager.setAdapter(adapter);
		// 缓存当前界面每一侧的界面数(2个)
		viewPager.setOffscreenPageLimit(1);
		tabPageIndicator.setViewPager(viewPager);// 关联上
		tabPageIndicator.setTextColor(ViewSelector.createColorStateList(
				BookTheme.THEME_COLOR, BookTheme.BOOK_WHITE));
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (BookTheme.isThemeChange) {
			mTitleBar.setBackgroundColor(BookTheme.THEME_COLOR);
			tabPageIndicator.setTextColor(ViewSelector.createColorStateList(
					BookTheme.THEME_COLOR, BookTheme.BOOK_WHITE));
		}
	}

	/**
	 * 初始化Fragment
	 */
	private void initFragmentList() {
		fragmentList = new ArrayList<Fragment>();
		fragmentList.add(subHotClassifyFragment);
		fragmentList.add(subDetailsClassifyFragment);
	}

	/**
	 * ViewPager适配器
	 */
	class TabPageIndicatorAdapter extends FragmentPagerAdapter {
		public TabPageIndicatorAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			if (fragmentList.get(position) == null && position == 0) {
				subHotClassifyFragment = new ClassifySubHotFragment();
				fragmentList.add(subHotClassifyFragment);
				return subHotClassifyFragment;
			} else if (fragmentList.get(position) == null && position == 1) {
				subDetailsClassifyFragment = new ClassifySubDetailsFragment();
				fragmentList.add(subDetailsClassifyFragment);
				return subDetailsClassifyFragment;
			} else

				return fragmentList.get(position);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return TITLE[position % TITLE.length];
		}

		@Override
		public int getCount() {
			return TITLE.length;
		}

	}
}
