package com.himoo.ydsc.activity;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.himoo.ydsc.R;
import com.himoo.ydsc.base.BaseActivity;
import com.himoo.ydsc.config.BookTheme;
import com.himoo.ydsc.fragment.bookmark.BookMarkFragment;
import com.himoo.ydsc.fragment.bookmark.CatalogFragment;
import com.himoo.ydsc.ui.utils.ViewSelector;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.viewpagerindicator.TabPageIndicator;

public class BookMarkActivity extends BaseActivity implements OnClickListener {

	@ViewInject(R.id.book_close)
	private ImageView book_close;

	/** Tab标题 */
	private static final String[] TITLE = new String[] { "目录", "书签" };

	/** Fragment */
	private Fragment catalogFragment = null;
	private Fragment bookmarkFragment = null;
	private List<Fragment> fragmentList;

	/** TabPageIndicator */
	@ViewInject(R.id.pager_indicator)
	private TabPageIndicator tabPageIndicator;

	/** NoScrollViewPager */
	@ViewInject(R.id.viewpager_bookmark)
	private ViewPager viewPager;

	private RelativeLayout mTitleBar;

	private String bookName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bookmark);
		ViewUtils.inject(this);
		initView();
		initEvent();
	}

	private void initView() {
		bookName = getIntent().getStringExtra("bookName");
		mTitleBar = (RelativeLayout) this
				.findViewById(R.id.bookmark_tltle_layout);
		mTitleBar.setBackgroundColor(BookTheme.THEME_COLOR);
		book_close.setOnClickListener(this);

	}

	/**
	 * 初始化Fragment
	 */
	private void initFragmentList() {
		fragmentList = new ArrayList<Fragment>();
		fragmentList.add(catalogFragment);
		fragmentList.add(bookmarkFragment);
	}

	@Override
	protected void initEvent() {
		// TODO Auto-generated method stub
		initFragmentList();
		TabPageIndicatorAdapter adapter = new TabPageIndicatorAdapter(
				getSupportFragmentManager());

		viewPager.setAdapter(adapter);
		// 缓存当前界面每一侧的界面数(2个)
		viewPager.setOffscreenPageLimit(1);
		tabPageIndicator.setViewPager(viewPager);// 关联上
		tabPageIndicator.setTextColor(ViewSelector.createColorStateList(
				BookTheme.THEME_COLOR, BookTheme.BOOK_WHITE));

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
				catalogFragment = new CatalogFragment();
				fragmentList.add(catalogFragment);
				return catalogFragment;
			} else if (fragmentList.get(position) == null && position == 1) {
				bookmarkFragment = BookMarkFragment.newInstance(bookName);
				fragmentList.add(bookmarkFragment);
				return bookmarkFragment;
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

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.book_close:
			finish();

			break;

		default:
			break;
		}

	}
}
