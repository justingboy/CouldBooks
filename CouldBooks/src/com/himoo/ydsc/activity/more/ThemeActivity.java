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
import com.himoo.ydsc.ui.swipebacklayout.SwipeBackActivity;
import com.himoo.ydsc.ui.utils.Toast;
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
			R.drawable.book_face_default, R.drawable.book_face_default,
			R.drawable.book_face_default };
	private String[] title = { "封面一", "封面二", "封面三", "封面四" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_more_theme);
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
	}

	private void initSkinGridView() {
		ArrayList<ThemeSkin> themeList = new ArrayList<ThemeSkin>();
		for (int i = 0; i < skinDrawable.length; i++) {
			ThemeSkin skin = new ThemeSkin();
			skin.setDrawableId(skinDrawable[i]);
			themeList.add(skin);
		}
		QuickAdapter<ThemeSkin> skinAdapter = new QuickAdapter<ThemeSkin>(this,
				R.layout.adapter_them_item, themeList) {

			@Override
			protected void convert(BaseAdapterHelper helper, ThemeSkin item) {
				// TODO Auto-generated method stub
				helper.setBackgroundRes(R.id.theme_skin_image,
						item.getDrawableId());
				if (item.getDrawableId() == R.drawable.theme_shape_green)
					helper.setVisible(R.id.theme_skinChoice_image, true);
			}
		};

		themeSkinGridView.setAdapter(skinAdapter);
		themeSkinGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub

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
		QuickAdapter<ThemeSkin> coverAdapter = new QuickAdapter<ThemeSkin>(
				this, R.layout.adapter_them_coveritem, themeList) {

			@Override
			protected void convert(BaseAdapterHelper helper, ThemeSkin item) {
				// TODO Auto-generated method stub
				helper.setBackgroundRes(R.id.theme_cover_image,
						item.getDrawableId());
				helper.setText(R.id.theme_cover_text, item.getTitle());
				if (item.getTitle().equals("封面一"))
					helper.setVisible(R.id.theme_coverChoice_image, true);

			}
		};

		themeCoverGridView.setAdapter(coverAdapter);
		themeCoverGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub

			}
		});

	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// TODO Auto-generated method stub
		Toast.showShort(this, isChecked ? "开启夜间模式" : "关闭夜间模式");
	}

}
