package com.himoo.ydsc.activity.more;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.GridView;

import com.himoo.ydsc.R;
import com.himoo.ydsc.base.quickadapter.BaseAdapterHelper;
import com.himoo.ydsc.base.quickadapter.QuickAdapter;
import com.himoo.ydsc.bean.ThemeSkin;
import com.himoo.ydsc.config.BookTheme;
import com.himoo.ydsc.config.SpConstant;
import com.himoo.ydsc.ui.swipebacklayout.SwipeBackActivity;
import com.himoo.ydsc.util.DeviceUtil;
import com.himoo.ydsc.util.SharedPreferences;
import com.ios.switchbutton.Configuration;
import com.ios.switchbutton.SwitchButton;
import com.lidroid.xutils.view.annotation.ViewInject;

public class ThemeActivity extends SwipeBackActivity implements
		OnCheckedChangeListener {

	@ViewInject(R.id.theme_color_gridview)
	private GridView themeSkinGridView;

	@ViewInject(R.id.theme_cover_gridview)
	private GridView themeCoverGridView;

	@ViewInject(R.id.theme_switch_button)
	private SwitchButton switchButton;

	/** 　皮肤 */
	private int[] skinDrawable = { R.drawable.theme_shape_red,
			R.drawable.theme_shape_blue, R.drawable.theme_shape_green,
			R.drawable.theme_shape_yellow, R.drawable.theme_shape_gray };
	/** 　封面 */
	private int[] coverDrawable = { R.drawable.book_face_default,
			R.drawable.no_cover, R.drawable.default_sign_free_book_cover,
			R.drawable.fm_big};
	private String[] title = { "封面一", "封面二", "封面三", "封面四" };

	/** 当前选择皮肤的颜色 */
	private int mCurrentSkinColor = R.drawable.theme_shape_green;

	/** 当前选择皮的小说默认封面 */
	private String mCurrentTitle = title[0];

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_more_theme);

		mCurrentSkinColor = SharedPreferences.getInstance().getInt(
				SpConstant.BOOK_SKIN_TYPE, R.drawable.theme_shape_green);
		mCurrentTitle = SharedPreferences.getInstance().getString(
				SpConstant.BOOK_COVER_TYPE, title[0]);

		initSkinGridView();
		initCoverGridView();
		switchButton.setOnCheckedChangeListener(this);

	}

	@Override
	protected void initTitleBar() {
		// TODO Auto-generated method stub
		mTitleBar.setLeftTitle(getResources().getString(R.string.main_more));
		mTitleBar.setTitle(getResources().getString(R.string.more_topic));
		mTitleBar.setRightLogoGone();
		switchButton.setConfiguration(
				Configuration.getDefault(DeviceUtil.getDisplayDensity(this)),
				BookTheme.THEME_COLOR);
		boolean isAutoNightMode = SharedPreferences.getInstance().getBoolean(
				SpConstant.BOOK_SETTING_AUTO_NIGHT, false);
		switchButton.setChecked(isAutoNightMode);

	}

	private void initSkinGridView() {
		ArrayList<ThemeSkin> themeList = new ArrayList<ThemeSkin>();
		for (int i = 0; i < skinDrawable.length; i++) {
			ThemeSkin skin = new ThemeSkin();
			skin.setDrawableId(skinDrawable[i]);
			themeList.add(skin);
		}
		final QuickAdapter<ThemeSkin> skinAdapter = new QuickAdapter<ThemeSkin>(
				this, R.layout.adapter_them_item, themeList) {

			@Override
			protected void convert(BaseAdapterHelper helper, ThemeSkin item) {
				// TODO Auto-generated method stub
				helper.setBackgroundRes(R.id.theme_skin_image,
						item.getDrawableId());
				if (item.getDrawableId() == mCurrentSkinColor)
					helper.setVisible(R.id.theme_skinChoice_image, true);
				else
					helper.setVisible(R.id.theme_skinChoice_image, false);
			}
		};

		themeSkinGridView.setAdapter(skinAdapter);
		themeSkinGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				mCurrentSkinColor = skinDrawable[position];
				skinAdapter.notifyDataSetChanged();
				SharedPreferences.getInstance().putInt(
						SpConstant.BOOK_SKIN_TYPE, mCurrentSkinColor);
				SharedPreferences.getInstance().putInt(
						SpConstant.BOOK_SKIN_INDEX, position + 1);
				BookTheme.setThemeColor(position + 1);
				setThemeChange();
				BookTheme.setChangeTheme(true);

			}
		});
	}

	private void initCoverGridView() {
		ArrayList<ThemeSkin> themeList = new ArrayList<ThemeSkin>();
		for (int i = 0; i < coverDrawable.length; i++) {
			ThemeSkin skin = new ThemeSkin();
			skin.setTitle(title[i]);
			skin.setDrawableId(coverDrawable[i]);
			themeList.add(skin);
		}
		final QuickAdapter<ThemeSkin> coverAdapter = new QuickAdapter<ThemeSkin>(
				this, R.layout.adapter_them_coveritem, themeList) {

			@Override
			protected void convert(BaseAdapterHelper helper, ThemeSkin item) {
				// TODO Auto-generated method stub
				helper.setBackgroundRes(R.id.theme_cover_image,
						item.getDrawableId());
				helper.setText(R.id.theme_cover_text, item.getTitle());
				if (item.getTitle().equals(mCurrentTitle))
					helper.setVisible(R.id.theme_coverChoice_image, true);
				else
					helper.setVisible(R.id.theme_coverChoice_image, false);

			}
		};

		themeCoverGridView.setAdapter(coverAdapter);
		themeCoverGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				mCurrentTitle = title[position];
				SharedPreferences.getInstance().putString(
						SpConstant.BOOK_COVER_TYPE, mCurrentTitle);
				SharedPreferences.getInstance().putInt(
						SpConstant.BOOK_COVER_INDEX, position);
				BookTheme.setBookCover(position);
				coverAdapter.notifyDataSetChanged();

			}
		});

	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// TODO Auto-generated method stub
		SharedPreferences.getInstance().putBoolean(
				SpConstant.BOOK_SETTING_AUTO_NIGHT, isChecked);
		SharedPreferences.getInstance().putBoolean(
				SpConstant.BOOK_SETTING_AUTO_NIGHT_MODE_YES, isChecked);
		SharedPreferences.getInstance().putBoolean(
				SpConstant.BOOK_SETTING_NITGHT_HAND , isChecked);
	}

	/**
	 * 设置新的主题背景色
	 */
	protected void setThemeChange() { // 通知栏所需颜色
		tintManager.setStatusBarTintColor(BookTheme.THEME_COLOR);
		mTitleBar.setBackgroundColor(BookTheme.THEME_COLOR);
		switchButton.setConfiguration(
				Configuration.getDefault(DeviceUtil.getDisplayDensity(this)),
				BookTheme.THEME_COLOR);

	}

}
