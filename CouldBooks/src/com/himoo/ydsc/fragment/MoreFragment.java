package com.himoo.ydsc.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.adsmogo.offers.MogoOffer;
import com.adsmogo.offers.MogoOfferChooserAdapter;
import com.adsmogo.offers.MogoOfferListCallback;
import com.adsmogo.offers.MogoOfferPointCallBack;
import com.himoo.ydsc.R;
import com.himoo.ydsc.activity.more.FeedBackActivity;
import com.himoo.ydsc.activity.more.PasswordLockActivity;
import com.himoo.ydsc.activity.more.ReadstatisticsActivity;
import com.himoo.ydsc.activity.more.ThemeActivity;
import com.himoo.ydsc.activity.more.UpdateSettingActivity;
import com.himoo.ydsc.base.BaseFragment;
import com.himoo.ydsc.config.BookTheme;
import com.himoo.ydsc.config.SpConstant;
import com.himoo.ydsc.manager.PageManager;
import com.himoo.ydsc.ui.utils.Toast;
import com.himoo.ydsc.ui.view.BookTitleBar;
import com.himoo.ydsc.util.AppUtils;
import com.himoo.ydsc.util.SharedPreferences;
import com.ios.radiogroup.SegmentedGroup;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.umeng.onlineconfig.OnlineConfigAgent;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;

public class MoreFragment extends BaseFragment implements UmengUpdateListener,
		RadioGroup.OnCheckedChangeListener, MogoOfferPointCallBack,
		MogoOfferListCallback {
	/** 芒果ID */
	public static String mogoID = "c31290cab9c649b79b9f2751b25e535a";

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

	/** 书籍更新设置 */
	@ViewInject(R.id.segmented_book_update)
	private SegmentedGroup segment_book_update;

	/** 整本书籍都更新 */
	@ViewInject(R.id.rb_all_book)
	private RadioButton rb_all_book;

	/** 只更新最新章节 */
	@ViewInject(R.id.rb_last_chapter)
	private RadioButton rb_last_chapter;

	/** 标识是否开启自动更新设置 */
	@ViewInject(R.id.more_book_update_identifiy)
	private TextView bookUpdateIdentify;

	/** 用于防止多次点击 */
	private boolean isClickable = false;

	/** ids */
	public int ids[] = { R.id.more_topic, R.id.more_timeUpdate,
			R.id.more_unlock, R.id.more_statistics, R.id.more_passwordProtect,
			R.id.more_feedback, R.id.more_update };

	private BookTitleBar titleBar;

	private int scoreLevel = 1000;

	@Override
	public void onAttach(Context context) {
		// TODO Auto-generated method stub
		super.onAttach(context);
		MogoOffer.init(context, mogoID);
		MogoOffer.addPointCallBack(this);
		MogoOffer.setOfferListTitle("获取积分");
		MogoOffer.setOfferEntranceMsg("商城");
		MogoOffer.setMogoOfferScoreVisible(false);
		MogoOffer.setMogoOfferListCallback(this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			SharedPreferences sp, Bundle savedInstanceState,
			PageManager pageManager) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_more, null);
		titleBar = (BookTitleBar) view.findViewById(R.id.book_titleBar);
		titleBar.setBackgroundColor(BookTheme.THEME_COLOR);
		titleBar.setShowSingleTile();
		titleBar.setTitle(getResources().getString(R.string.main_more));
		return view;
	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub
		String value = OnlineConfigAgent.getInstance().getConfigParams(
				getActivity(), "adstatue");
		try {

			scoreLevel = Integer.valueOf(OnlineConfigAgent.getInstance()
					.getConfigParams(getActivity(), "scorelevel"));
		} catch (Exception e) {
			Log.e(e);
			scoreLevel = 1000;
		}
		
		if (value != null && value.equals("1"))
			more_unLock.setText("积分商城（获取"+scoreLevel+"去掉广告）");
		// 设置监听器
		segment_book_update.setOnCheckedChangeListener(this);
		// 初始化书籍更新类型
		initBookUpdateType();
		// 设置版本信息
		String versionCode = AppUtils.getVersionName(getActivity());
		more_version.setText(getActivity().getString(
				R.string.more_bottom_thinks)
				+ versionCode + " " + SpConstant.BUILD_CODE);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		if (isClickable)
			return;
		isClickable = true;
		switch (v.getId()) {
		case R.id.more_topic:
			startToActivity(ThemeActivity.class);
			break;
		case R.id.more_timeUpdate:
			startToActivity(UpdateSettingActivity.class);
			break;
		case R.id.more_unlock:
			initMogoWall();
			isClickable = false;
			// startToActivity(WallActivity.class);
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
		case R.id.more_update:
			showRefreshDialog("检查版本更新中");
			UmengUpdateAgent.forceUpdate(getActivity());
			UmengUpdateAgent.setUpdateListener(this);
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
			break;
		case R.id.rb_last_chapter:
			SharedPreferences.getInstance().putInt("book_update_type",
					SpConstant.BOOK_UPDATE_TYPE_CHAPTER);
			break;

		default:
			break;
		}
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MogoOffer.RefreshPoints(getActivity());
		if (BookTheme.isThemeChange)
			titleBar.setBackgroundColor(BookTheme.THEME_COLOR);
		boolean isOpenUpdate = SharedPreferences.getInstance().getBoolean(
				SpConstant.BOOK_UPATE_SETTING, true);
		segment_book_update.setTintColor(BookTheme.THEME_COLOR);
		bookUpdateIdentify.setTextColor(BookTheme.THEME_COLOR);
		bookUpdateIdentify.setText(isOpenUpdate ? "已开启" : "已关闭");
		isClickable = false;

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

	/**
	 * 初始化书籍更新类型
	 */
	private void initBookUpdateType() {
		int type = SharedPreferences.getInstance()
				.getInt("book_update_type", 2);
		if (type == 1) {
			rb_all_book.setChecked(true);
		} else if (type == 2) {
			rb_last_chapter.setChecked(true);

		}

	}

	/**
	 * 初始化Mogo积分墙设置
	 */
	private void initMogoWall() {
		MogoOffer.setOfferListTitle("获取积分");
		MogoOffer.setOfferEntranceMsg("商城");
		MogoOffer.setMogoOfferScoreVisible(false);
		MogoOffer.addPointCallBack(this);
		MogoOffer.setMogoOfferListCallback(this);
		MogoOffer.showOffer(getActivity());
	}

	@Override
	public void showOfferListDialog(final Context context, String dialogTitle,
			String[] tips) {
		// TODO Auto-generated method stub
		final AlertDialog dialog = new AlertDialog.Builder(context).create();
		MogoOfferChooserAdapter adapter = new MogoOfferChooserAdapter(context,
				tips);
		dialog.setTitle(dialogTitle);

		ListView listView = new ListView(context);
		listView.setBackgroundColor(0xffffffff);
		listView.setPadding(0, 0, 0, 0);
		listView.setCacheColorHint(0x00000000);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int which,
					long arg3) {
				// TODO Auto-generated method stub

				if (dialog != null) {
					dialog.dismiss();
				}

				MogoOffer.showSingleOffer(context, which);
			}
		});
		dialog.setView(listView);
		dialog.getWindow().setBackgroundDrawableResource(
				android.R.color.transparent);
		dialog.show();
	}

	@Override
	public void updatePoint(long ponit) {
		// TODO Auto-generated method stub
		if (ponit >=scoreLevel) {
			SharedPreferences.getInstance().putBoolean(SpConstant.MOGOAD_SHOW,
					false);
		}
		if(ponit>100)
		more_unLock.setText("积分商城（当前积分：" + ponit + "分）");
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		MogoOffer.clear(getActivity());
		super.onDestroy();
	}

	@Override
	public void onUpdateReturned(int code, UpdateResponse arg1) {
		// TODO Auto-generated method stub
		dismissRefreshDialog();
		switch (code) {
		case 0:

			break;
		case 1:
			Toast.showLong(getActivity(), "已经是最新版本");
			break;
		case 2:

			break;
		case 3:
			Toast.showLong(getActivity(), "未连接网络");
			break;

		default:
			break;
		}
		UmengUpdateAgent.setUpdateListener(null);
		isClickable = false;
	}

}
