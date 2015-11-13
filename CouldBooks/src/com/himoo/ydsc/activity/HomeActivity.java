package com.himoo.ydsc.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.res.ColorStateList;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.himoo.ydsc.R;
import com.himoo.ydsc.base.BaseActivity;
import com.himoo.ydsc.fragment.BookShelfFragment;
import com.himoo.ydsc.fragment.ChoiceFragment;
import com.himoo.ydsc.fragment.ClassifyFragment;
import com.himoo.ydsc.fragment.MoreFragment;
import com.himoo.ydsc.fragment.SearchFragment;
import com.himoo.ydsc.http.HttpConstant;
import com.himoo.ydsc.ui.utils.ViewSelector;
import com.himoo.ydsc.util.SharedPreferences;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

public class HomeActivity extends BaseActivity {

	/** RadioGroup */
	@ViewInject(R.id.main_bottom_group)
	private RadioGroup rGroup;
	/** 精选 */
	@ViewInject(R.id.main_bottom_choice)
	private RadioButton rBChoice;
	/** 分类 */
	@ViewInject(R.id.main_bottom_classify)
	private RadioButton rBClassify;
	/** 搜索 */
	@ViewInject(R.id.main_bottom_search)
	private RadioButton rBSearch;
	/** 书架 */
	@ViewInject(R.id.main_bottom_bookshelf)
	private RadioButton rBBookshelf;
	/** 更多 */
	@ViewInject(R.id.main_bottom_more)
	private RadioButton rBMore;

	/** FrameLayout id */
	@ViewInject(R.id.main_content)
	private FrameLayout main_content;

	/** Fragment管理类 */
	FragmentManager fragmentManager;

	/** Fragment */
	private Fragment choiceFragment = new ChoiceFragment();
	private Fragment classifyFragment = new ClassifyFragment();
	private Fragment searchFragment = new SearchFragment();
	private Fragment bookshelfFragment = new BookShelfFragment();
	private Fragment moreFragment = new MoreFragment();
	private List<Fragment> fragmentList;

	/** 标记当前点击的RadioButton */
	private int mCurrentSelected = -1;

	/** 点击的RadioButton */
	public static final int RB_VIEW_CHOICE = 0;
	public static final int RB_VIEW_CLASSIFY = 1;
	public static final int RB_VIEW_SEARCH = 2;
	public static final int RB_VIEW_BOOKSHELF = 3;
	public static final int RB_VIEW_MORE = 4;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		ViewUtils.inject(this);
		initEvent();
		setRadioButtonDrawableSelector();
		setRadioButtonTextColorSelector();
		this.fragmentManager = getSupportFragmentManager();
		initFragmentList();
		setCurrentClickPoint(RB_VIEW_CHOICE);
		addFirstToast(this.getActivityName());

		Log.d(SharedPreferences.getInstance().getString("host",
				HttpConstant.HOST_URL_TEST));

	}

	@Override
	protected void initEvent() {
		// TODO Auto-generated method stub
		rGroup.setOnCheckedChangeListener(new OnRadioGroupCheckedChangeListener());
	}

	/**
	 * RadioGroup点击实现类
	 */
	private class OnRadioGroupCheckedChangeListener implements
			OnCheckedChangeListener {

		@Override
		public void onCheckedChanged(RadioGroup radiogroup, int i) {
			// TODO Auto-generated method stub
			switch (i) {
			case R.id.main_bottom_choice:
				setCurrentClickPoint(0);
				break;
			case R.id.main_bottom_classify:
				setCurrentClickPoint(1);
				break;
			case R.id.main_bottom_search:
				setCurrentClickPoint(2);
				break;
			case R.id.main_bottom_bookshelf:
				setCurrentClickPoint(3);
				break;
			case R.id.main_bottom_more:
				setCurrentClickPoint(4);
				break;

			default:
				break;
			}
		}

	}

	/**
	 * 初始化Fragment
	 */
	private void initFragmentList() {
		fragmentList = new ArrayList<Fragment>();
		fragmentList.add(choiceFragment);
		fragmentList.add(classifyFragment);
		fragmentList.add(searchFragment);
		fragmentList.add(bookshelfFragment);
		fragmentList.add(moreFragment);
	}

	/**
	 * 设置当前点击的位置，及切换Fragment
	 * 
	 * @param index
	 */
	public void setCurrentClickPoint(int index) {
		if (mCurrentSelected == index)
			return;
		mCurrentSelected = index;
		addFragmentToStack();
	}

	/**
	 * 将Fragment添加到栈中
	 */
	private void addFragmentToStack() {
		FragmentTransaction fragmentTransaction = fragmentManager
				.beginTransaction();
		if (mCurrentSelected == RB_VIEW_CHOICE) {
			if (!choiceFragment.isAdded()) {
				fragmentTransaction.add(R.id.main_content, choiceFragment);
			}
		} else if (mCurrentSelected == RB_VIEW_CLASSIFY) {
			if (!classifyFragment.isAdded()) {
				fragmentTransaction.add(R.id.main_content, classifyFragment);
			}
		} else if (mCurrentSelected == RB_VIEW_SEARCH) {
			if (!searchFragment.isAdded()) {
				fragmentTransaction.add(R.id.main_content, searchFragment);
			}
		} else if (mCurrentSelected == RB_VIEW_BOOKSHELF) {
			if (!bookshelfFragment.isAdded()) {
				fragmentTransaction.add(R.id.main_content, bookshelfFragment);
			}
		} else if (mCurrentSelected == RB_VIEW_MORE) {
			if (!moreFragment.isAdded()) {
				fragmentTransaction.add(R.id.main_content, moreFragment);
			}
		}
		toggleFragment(fragmentTransaction);
		fragmentTransaction.commit();
	}

	/**
	 * 切换Fragment
	 * 
	 * @param fragmentTransaction
	 */
	private void toggleFragment(FragmentTransaction fragmentTransaction) {
		for (int i = 0; i < fragmentList.size(); i++) {
			Fragment f = fragmentList.get(i);
			if (i == mCurrentSelected && f.isAdded()) {
				fragmentTransaction.show(f);
			} else if (f != null && f.isAdded() && f.isVisible()) {
				fragmentTransaction.hide(f);
			}
		}
	}
	
	/**
	 * 设置RadioButton选择器
	 */
	private void setRadioButtonDrawableSelector() {
		StateListDrawable choiceDrawable = ViewSelector.creatWidgetSelector(
				this, R.drawable.main_bottom_choice_green,
				R.drawable.mian_bottom_choice);
		StateListDrawable classifyDrawable = ViewSelector.creatWidgetSelector(
				this, R.drawable.main_bottom_classify_green,
				R.drawable.main_bottom_classify);
		StateListDrawable searchDrawable = ViewSelector.creatWidgetSelector(
				this, R.drawable.mian_bottom_search_green,
				R.drawable.mian_bottom_search);
		StateListDrawable bookShelfDrawable = ViewSelector.creatWidgetSelector(
				this, R.drawable.main_bottom_bookshelf_green,
				R.drawable.main_bottom_bookshelf);
		StateListDrawable moreDrawable = ViewSelector.creatWidgetSelector(this,
				R.drawable.mian_bottom_more_green, R.drawable.mian_bottom_more);
		// 设置上
		rBChoice.setCompoundDrawablesWithIntrinsicBounds(null, choiceDrawable,
				null, null);
		rBClassify.setCompoundDrawablesWithIntrinsicBounds(null,
				classifyDrawable, null, null);
		rBSearch.setCompoundDrawablesWithIntrinsicBounds(null, searchDrawable,
				null, null);
		rBBookshelf.setCompoundDrawablesWithIntrinsicBounds(null,
				bookShelfDrawable, null, null);
		rBMore.setCompoundDrawablesWithIntrinsicBounds(null, moreDrawable,
				null, null);
	}

	/**
	 * 设置文字颜色的selector
	 */
	private void setRadioButtonTextColorSelector() {

		ColorStateList colorstate = ViewSelector.creatTextColorSelector(this);

		rBChoice.setTextColor(colorstate);
		rBClassify.setTextColor(colorstate);
		rBSearch.setTextColor(colorstate);
		rBBookshelf.setTextColor(colorstate);
		rBMore.setTextColor(colorstate);
	}

	
	
	
	

}
