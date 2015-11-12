package com.himoo.ydsc.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.himoo.ydsc.R;
import com.himoo.ydsc.activity.more.FeedBackActivity;
import com.himoo.ydsc.activity.more.PasswordLockActivity;
import com.himoo.ydsc.activity.more.ReadstatisticsActivity;
import com.himoo.ydsc.activity.more.ThemeActivity;
import com.himoo.ydsc.activity.more.UpdateSettingActivity;
import com.himoo.ydsc.activity.more.WallActivity;
import com.himoo.ydsc.base.BaseFragment;
import com.himoo.ydsc.config.SpConstant;
import com.himoo.ydsc.manager.PageManager;
import com.himoo.ydsc.ui.view.BookTitleBar;
import com.himoo.ydsc.util.AppUtils;
import com.himoo.ydsc.util.SharedPreferences;
import com.ios.radiogroup.SegmentedGroup;
import com.lidroid.xutils.view.annotation.ViewInject;

public class MoreFragment extends BaseFragment implements
		RadioGroup.OnCheckedChangeListener {

	/** 主题皮肤设置 */
	@ViewInject(R.id.more_topic)
	private TextView more_topic;

	/** 后台自动更新 */
	@ViewInject(R.id.more_timeUpdate)
	private TextView more_timeUpdate;

	/** 解锁功能 */
	@ViewInject(R.id.more_unlock)
	private TextView more_unLock;

	/** 阅读统计 */
	@ViewInject(R.id.more_statistics)
	private TextView more_Statistics;

	/** 密码保护 */
	@ViewInject(R.id.more_passwordProtect)
	private TextView more_PasswordProtect;

	/** 用户反馈 */
	@ViewInject(R.id.more_feedback)
	private TextView more_FeedBack;

	/** 版本信息 */
	@ViewInject(R.id.more_version_code)
	private TextView more_version;

	/** 版本信息 */
	@ViewInject(R.id.segmented_book_update)
	private SegmentedGroup segment_book_update;

	/** 标识是否开启自动更新设置 */
	@ViewInject(R.id.more_book_update_identifiy)
	private TextView bookUpdateIdentify;

	/** ids */
	public int ids[] = { R.id.more_topic, R.id.more_timeUpdate,
			R.id.more_unlock, R.id.more_statistics, R.id.more_passwordProtect,
			R.id.more_feedback };

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			SharedPreferences sp, Bundle savedInstanceState,
			PageManager pageManager) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_more, null);
		BookTitleBar titleBar = (BookTitleBar) view
				.findViewById(R.id.book_titleBar);
		titleBar.setShowSingleTile();
		titleBar.setTitle(getResources().getString(R.string.main_more));
		return view;
	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub

		// 设置监听器
		segment_book_update.setOnCheckedChangeListener(this);
		// 设置版本信息
		String versionCode = AppUtils.getVersionName(getActivity());
		more_version.setText(getActivity().getString(
				R.string.more_bottom_thinks)
				+ versionCode + " 201601081851");
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		switch (v.getId()) {
		case R.id.more_topic:
			startToActivity(ThemeActivity.class);
			break;
		case R.id.more_timeUpdate:
			startToActivity(UpdateSettingActivity.class);
			break;
		case R.id.more_unlock:
			startToActivity(WallActivity.class);
			break;
		case R.id.more_statistics:
			startToActivity(ReadstatisticsActivity.class);
			break;
		case R.id.more_passwordProtect:
			startToActivity(PasswordLockActivity.class);
			break;
		case R.id.more_feedback:
			startToActivity(FeedBackActivity.class);
			break;
		default:
			break;
		}

	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		// TODO Auto-generated method stub
		switch (checkedId) {
		case R.id.rb_all_book:
			SharedPreferences.getInstance().putInt("book_update_type",
					SpConstant.BOOK_UPDATE_TYPE_ALL);
			Log.d("rb_all_book");
			break;
		case R.id.rb_last_chapter:
			SharedPreferences.getInstance().putInt("book_update_type",
					SpConstant.BOOK_UPDATE_TYPE_CHAPTER);
			Log.d("rb_last_chapter");
			break;

		default:
			break;
		}
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		boolean isOpenUpdate = SharedPreferences.getInstance().getBoolean(
				SpConstant.BOOK_UPATE_SETTING, false);
		bookUpdateIdentify.setText(isOpenUpdate ? "已开启" : "未开启");
	}

	/**
	 * 跳转到搜索结果的界面
	 * 
	 * @param keyWord
	 */
	protected void startToActivity(Class<?> className) {
		Intent intent = new Intent(getActivity(), className);
		getActivity().startActivity(intent);
		getActivity().overridePendingTransition(R.anim.activity_zoom_in, 0);

	}

}
