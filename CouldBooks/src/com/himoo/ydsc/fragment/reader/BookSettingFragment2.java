package com.himoo.ydsc.fragment.reader;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.himoo.ydsc.R;
import com.himoo.ydsc.config.BookTheme;
import com.himoo.ydsc.config.SpConstant;
import com.himoo.ydsc.fragment.reader.BookSettingFragment1.ChangeNightReceiver;
import com.himoo.ydsc.reader.utils.SystemBrightManager;
import com.himoo.ydsc.ui.utils.ViewSelector;
import com.himoo.ydsc.util.DeviceUtil;
import com.himoo.ydsc.util.SharedPreferences;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

/**
 * 阅读界面设置
 * 
 */
public final class BookSettingFragment2 extends Fragment implements
		OnClickListener {

	private static final String ACTION = "com.himoo.ydsc.booksettingfragment1.receiver";
	private boolean isFollowSystem = false;

	private OnFragment2Listener mListener;

	private Context mContext;

	@ViewInject(R.id.seekBar_light)
	private SeekBar seekBar_light;

	@ViewInject(R.id.light_xitong)
	private TextView tv_SystemLight;

	@ViewInject(R.id.textsiz_reduce)
	private ImageView textsiz_reduce;

	@ViewInject(R.id.textsiz_increase)
	private ImageView textsiz_increase;

	@ViewInject(R.id.booksetting_iv_bg1)
	private ImageView booksetting_iv_bg1;

	@ViewInject(R.id.booksetting_iv_bg2)
	private ImageView booksetting_iv_bg2;

	@ViewInject(R.id.booksetting_iv_bg3)
	private ImageView booksetting_iv_bg3;

	@ViewInject(R.id.booksetting_iv_bg4)
	private ImageView booksetting_iv_bg4;

	@ViewInject(R.id.booksetting_iv_bg5)
	private ImageView booksetting_iv_bg5;

	@ViewInject(R.id.booksetting_linespace_1)
	private ImageView booksetting_linespace_1;

	@ViewInject(R.id.booksetting_linespace_2)
	private ImageView booksetting_linespace_2;

	@ViewInject(R.id.booksetting_linespace_3)
	private ImageView booksetting_linespace_3;

	@ViewInject(R.id.booksetting_linespace_4)
	private ImageView booksetting_linespace_4;
	/** 默认字体的大小 */
	private int textSize = 20;
	/** 是否是手动开启夜间模式 */
	private boolean isNigthModeHand;

	private ChangeNightReceiver receiver;

	public static BookSettingFragment2 newInstance() {
		BookSettingFragment2 fragment = new BookSettingFragment2();

		return fragment;
	}

	@Override
	public void onAttach(Context context) {
		// TODO Auto-generated method stub
		super.onAttach(context);
		mContext = context;
		try {
			mListener = (OnFragment2Listener) context;
		} catch (ClassCastException e) {
			throw new ClassCastException(context.toString()
					+ " must implement OnFragment1Listener");
		}

	}

	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_book_setting2, null);

		return view;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		ViewUtils.inject(this);
		textSize = SharedPreferences.getInstance().getInt(
				SpConstant.BOOK_SETTING_TEXT_SIZE,
				DeviceUtil.dip2px(getActivity(), 20));
		setBookImageSeleted();
		setImageLineSeleted();
		setListener();
		isNigthModeHand = SharedPreferences.getInstance().getBoolean(
				SpConstant.BOOK_SETTING_AUTO_NIGHT, false);
		if (!SharedPreferences.getInstance().getBoolean(
				SpConstant.BOOK_SETTING_LIGHT_SYSTEM, false)) {
			ViewSelector.setButtonStrokeSelector(getActivity(), tv_SystemLight,
					BookTheme.BOOK_GRAY);
			setScreenLight();
			isFollowSystem = false;
		} else {
			isFollowSystem = true;
			ViewSelector.setButtonStrokeSelector(getActivity(), tv_SystemLight,
					BookTheme.THEME_COLOR);
		}
		registeBroadcast();

	}

	public void setListener() {
		textsiz_reduce.setOnClickListener(this);
		textsiz_increase.setOnClickListener(this);
		tv_SystemLight.setOnClickListener(this);
		booksetting_iv_bg1.setOnClickListener(onTextBgChangeListener);
		booksetting_iv_bg2.setOnClickListener(onTextBgChangeListener);
		booksetting_iv_bg3.setOnClickListener(onTextBgChangeListener);
		booksetting_iv_bg4.setOnClickListener(onTextBgChangeListener);
		booksetting_iv_bg5.setOnClickListener(onTextBgChangeListener);

		booksetting_linespace_1.setOnClickListener(onLineSpaceListener);
		booksetting_linespace_2.setOnClickListener(onLineSpaceListener);
		booksetting_linespace_3.setOnClickListener(onLineSpaceListener);
		booksetting_linespace_4.setOnClickListener(onLineSpaceListener);

		seekBar_light.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				if (progress < 5) {
				} else {
					setBrightness((Activity) mContext, progress);
					SharedPreferences.getInstance().putInt(
							SpConstant.BOOK_SETTING_LIGHT, progress);
				}
			}
		});
	}

	public interface OnFragment2Listener {
		public void onTextSizChange(int textSize);

		public void onTextBackgroundChange();

		public void onTextLineSpaceChange();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		// changeNightMode();
		switch (v.getId()) {
		case R.id.textsiz_reduce:
			if (textSize < 20)
				return;
			mListener.onTextSizChange(--textSize);

			break;
		case R.id.textsiz_increase:
			if (textSize > 120)
				return;
			mListener.onTextSizChange(++textSize);

			break;
		case R.id.light_xitong:

			isFollowSystem = !isFollowSystem;
			SharedPreferences.getInstance().putBoolean(
					SpConstant.BOOK_SETTING_LIGHT_SYSTEM, isFollowSystem);
			if (isFollowSystem) {
				ViewSelector.setButtonStrokeSelector(getActivity(),
						tv_SystemLight, BookTheme.THEME_COLOR);

				boolean isAutoBrightness = SystemBrightManager
						.isAutoBrightness(getActivity());
				if (isAutoBrightness) { // 自动调整亮度
					SystemBrightManager.setBrightness(getActivity(), -1);
				} else {
					int brightValue = SystemBrightManager
							.getBrightness(getActivity());
					SystemBrightManager.setBrightness(getActivity(),
							brightValue);
				}
			} else {
				int brigHtness = SharedPreferences.getInstance().getInt(
						SpConstant.BOOK_SETTING_LIGHT, 10);
				setBrightness(getActivity(), brigHtness);
				setScreenLight();
				ViewSelector.setButtonStrokeSelector(getActivity(),
						tv_SystemLight, BookTheme.BOOK_GRAY);
			}

			break;

		default:
			break;
		}

	}

	/**
	 * 换背景监听接口
	 * 
	 */
	private OnClickListener onTextBgChangeListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			setImageViewSeleted(v);
		}

	};

	/**
	 * 改变图片选中时的图片
	 * 
	 * @param view
	 */
	private void setImageViewSeleted(View view) {

		booksetting_iv_bg1.setImageResource(R.drawable.reading_bg_eye);
		booksetting_iv_bg2.setImageResource(R.drawable.reading_bg_kraft);
		booksetting_iv_bg3.setImageResource(R.drawable.reading_bg_default);
		booksetting_iv_bg4.setImageResource(R.drawable.reading_bg_5);
		// booksetting_iv_bg5.setImageResource(R.drawable.reading_bg_4);
		switch (view.getId()) {
		case R.id.booksetting_iv_bg1:
			booksetting_iv_bg1
					.setImageResource(R.drawable.reading_bg_eye_select);
			BookTheme.setReaderBookTextBg(1);
			break;
		case R.id.booksetting_iv_bg2:
			BookTheme.setReaderBookTextBg(2);
			booksetting_iv_bg2
					.setImageResource(R.drawable.reading_bg_kraft_select);

			break;
		case R.id.booksetting_iv_bg3:
			BookTheme.setReaderBookTextBg(3);
			booksetting_iv_bg3
					.setImageResource(R.drawable.reading_bg_default_select);
			break;
		case R.id.booksetting_iv_bg4:
			BookTheme.setReaderBookTextBg(4);
			booksetting_iv_bg4.setImageResource(R.drawable.reading_bg_5_select);
			break;
		case R.id.booksetting_iv_bg5:
			BookTheme.setReaderBookTextBg(5);
			// booksetting_iv_bg5.setImageResource(R.drawable.reading_bg_4_select);
			break;

		default:
			break;
		}

		changeNightMode();
		mListener.onTextBackgroundChange();
		sendBrocastReceiver();

	}

	/**
	 * 初始化选中的ImageView 图片
	 */
	public void setBookImageSeleted() {
		int index = SharedPreferences.getInstance().getInt(
				SpConstant.BOOK_SETTING_TEXT_BG, 2);
		switch (index) {
		case 1:
			booksetting_iv_bg1
					.setImageResource(R.drawable.reading_bg_eye_select);
			break;
		case 2:
			booksetting_iv_bg2
					.setImageResource(R.drawable.reading_bg_kraft_select);
			break;
		case 3:
			booksetting_iv_bg3
					.setImageResource(R.drawable.reading_bg_default_select);
			break;
		case 4:
			booksetting_iv_bg4.setImageResource(R.drawable.reading_bg_5_select);
			break;
		case 5:
			// booksetting_iv_bg5.setImageResource(R.drawable.reading_bg_4_select);
			break;

		default:
			break;
		}

	}

	/**
	 * 设置亮度
	 * 
	 * @param activity
	 * @param brightness
	 */
	public static void setBrightness(Activity activity, int brightness) {
		// Settings.System.putInt(activity.getContentResolver(),
		// Settings.System.SCREEN_BRIGHTNESS_MODE,
		// Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
		WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
		lp.screenBrightness = Float.valueOf(brightness) * (1f / 255f);
		activity.getWindow().setAttributes(lp);
	}

	/**
	 * 获取屏幕的亮度
	 * 
	 * @param activity
	 * @return
	 */
	public static int getScreenBrightness(Activity activity) {
		int nowBrightnessValue = 0;
		ContentResolver resolver = activity.getContentResolver();
		try {
			nowBrightnessValue = android.provider.Settings.System.getInt(
					resolver, Settings.System.SCREEN_BRIGHTNESS);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return nowBrightnessValue;
	}

	/**
	 * 保存亮度设置状态
	 * 
	 * @param resolver
	 * @param brightness
	 */
	public static void saveBrightness(ContentResolver resolver, int brightness) {
		Uri uri = android.provider.Settings.System
				.getUriFor("screen_brightness");
		android.provider.Settings.System.putInt(resolver, "screen_brightness",
				brightness);
		resolver.notifyChange(uri, null);
	}

	/**
	 * 行间距点击效果
	 * 
	 * @param view
	 */
	public void setTextImageSeleted(View view) {
		booksetting_linespace_1.setImageResource(R.drawable.liner_space_0_2);
		booksetting_linespace_2
				.setImageResource(R.drawable.liner_space_default);
		booksetting_linespace_3.setImageResource(R.drawable.liner_space_1);
		booksetting_linespace_4.setImageResource(R.drawable.liner_space_1_5);

		switch (view.getId()) {
		case R.id.booksetting_linespace_1:
			booksetting_linespace_1
					.setImageResource(R.drawable.liner_space_0_2_checked);
			SharedPreferences.getInstance().putInt(
					SpConstant.BOOK_SETTING_TEXT_LINE, 1);
			break;
		case R.id.booksetting_linespace_2:
			booksetting_linespace_2
					.setImageResource(R.drawable.liner_space_default_checked);
			SharedPreferences.getInstance().putInt(
					SpConstant.BOOK_SETTING_TEXT_LINE, 2);
			break;
		case R.id.booksetting_linespace_3:
			booksetting_linespace_3
					.setImageResource(R.drawable.liner_space_1_checekd);
			SharedPreferences.getInstance().putInt(
					SpConstant.BOOK_SETTING_TEXT_LINE, 3);
			break;
		case R.id.booksetting_linespace_4:
			booksetting_linespace_4
					.setImageResource(R.drawable.liner_space_1_5_checked);
			SharedPreferences.getInstance().putInt(
					SpConstant.BOOK_SETTING_TEXT_LINE, 4);
			break;

		default:
			break;
		}
		mListener.onTextLineSpaceChange();

	}

	/**
	 * 初始化选中的ImageView 图片
	 */
	public void setImageLineSeleted() {
		int index = SharedPreferences.getInstance().getInt(
				SpConstant.BOOK_SETTING_TEXT_LINE, 1);
		switch (index) {
		case 1:
			booksetting_linespace_1
					.setImageResource(R.drawable.liner_space_0_2_checked);
			break;
		case 2:
			booksetting_linespace_2
					.setImageResource(R.drawable.liner_space_default_checked);
			break;
		case 3:
			booksetting_linespace_3
					.setImageResource(R.drawable.liner_space_1_checekd);
			break;
		case 4:
			booksetting_linespace_4
					.setImageResource(R.drawable.liner_space_1_5_checked);
			break;

		default:
			break;
		}

	}

	/**
	 * 改变行间距
	 */
	private OnClickListener onLineSpaceListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			setTextImageSeleted(v);
		}
	};


	/**
	 * 设置亮度
	 */
	private void setScreenLight() {
		int brigHtness = SharedPreferences.getInstance().getInt(
				SpConstant.BOOK_SETTING_LIGHT, 10);
		seekBar_light.setProgress(brigHtness);

	}

	/**
	 * 改变夜间模式
	 */
	public void changeNightMode() {
		if (isNigthModeHand) {
			SharedPreferences.getInstance().putBoolean(
					SpConstant.BOOK_SETTING_AUTO_NIGHT, false);
			SharedPreferences.getInstance().putBoolean(
					SpConstant.BOOK_SETTING_AUTO_NIGHT_MODE_YES, false);
		}
		SharedPreferences.getInstance().putBoolean(
				SpConstant.BOOK_SETTING_NIGHT_MODE, false);
	}

	/**
	 * 注册广播
	 */
	private void registeBroadcast() {
		receiver = new BookSettingFragment1.ChangeNightReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION);
		getActivity().registerReceiver(receiver, filter);

	}

	/**
	 * 发送广播
	 */
	private void sendBrocastReceiver() {
		Intent intent = new Intent(ACTION);
		getActivity().sendBroadcast(intent);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (receiver != null)
			getActivity().unregisterReceiver(receiver);
	}
}
