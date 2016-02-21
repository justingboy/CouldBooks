package com.himoo.ydsc.fragment.reader;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.himoo.ydsc.R;
import com.himoo.ydsc.activity.BookMarkActivity;
import com.himoo.ydsc.config.BookTheme;
import com.himoo.ydsc.config.SpConstant;
import com.himoo.ydsc.reader.view.PageSeekBar;
import com.himoo.ydsc.ui.utils.ViewSelector;
import com.himoo.ydsc.util.SharedPreferences;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

/**
 * 阅读界面设置
 * 
 */
public class BookSettingFragment1 extends Fragment implements OnClickListener,
		OnSeekBarChangeListener {

	private OnFragment1Listener mListener;

	public int chapterPageCount;
	public int currentPage;
	public int currentPosition;

	@ViewInject(R.id.booksetting_traditional)
	private TextView booksetting_traditional;

	@ViewInject(R.id.booksetting_simplified)
	private TextView booksetting_simplified;

	@ViewInject(R.id.booksetting_share)
	private ImageView booksetting_share;

	@ViewInject(R.id.booksetting_night)
	private static ImageView booksetting_night;

	@ViewInject(R.id.booksetting_bookmrak)
	private ImageView booksetting_bookmrak;

	@ViewInject(R.id.tv_pre_chapter)
	private TextView tv_pre_chapter;

	@ViewInject(R.id.tv_next_chapter)
	private TextView tv_next_chapter;

	@ViewInject(R.id.seekBar_chapter)
	public PageSeekBar seekBar_chapter;

	@ViewInject(R.id.booksetting_textface_1)
	private TextView booksetting_textface_1;

	@ViewInject(R.id.booksetting_textface_2)
	private TextView booksetting_textface_2;

	@ViewInject(R.id.booksetting_textface_3)
	private TextView booksetting_textface_3;

	@ViewInject(R.id.booksetting_textface_4)
	private TextView booksetting_textface_4;

	@ViewInject(R.id.booksetting_textface_5)
	private TextView booksetting_textface_5;
	/** 判断是否是夜间模式 */
	private boolean isNightMode = false;
	private int position;
	private boolean isNigthModeHand;

	public static BookSettingFragment1 newInstance(String bookName,
			int chapterSize, int type, int bookType, String statue, String gid,
			String lastUrl, boolean isNightMode) {
		BookSettingFragment1 fragment = new BookSettingFragment1();
		Bundle bundle = new Bundle();
		bundle.putInt("chapterSize", chapterSize);
		bundle.putString("bookName", bookName);
		bundle.putString("gid", gid);
		bundle.putString("lastUrl", lastUrl);
		bundle.putInt("type", type);
		bundle.putInt("bookType", bookType);
		bundle.putString("statue", statue);
		bundle.putBoolean("mode", isNightMode);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public void onAttach(Context context) {
		// TODO Auto-generated method stub
		super.onAttach(context);
		try {
			mListener = (OnFragment1Listener) context;
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
		View view = inflater.inflate(R.layout.fragment_book_setting1, null);
		return view;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		ViewUtils.inject(this);
		initPageSeekBar();
		initTextTypeSeleted();
		initTextTypeChildrenSeleted();
		// int chapterSize = getArguments().getInt("chapterSize");
		// seekBar_chapter.setMax(chapterSize);
		if (SharedPreferences.getInstance().getBoolean(
				SpConstant.BOOK_SETTING_NIGHT_MODE, false)
				|| SharedPreferences.getInstance().getBoolean(
						SpConstant.BOOK_SETTING_AUTO_NIGHT_MODE_YES, false)) {
			setNightModeImg(true);
		}
		// BookMark bookMark = BookMarkDb.getInstance(getActivity(), "book")
		// .querryReaderPos(getArguments().getString("bookName"));
		// if (bookMark != null)
		// position = bookMark.getPosition();
		// seekBar_chapter.setProgress(position);
		isNigthModeHand = SharedPreferences.getInstance().getBoolean(
				SpConstant.BOOK_SETTING_AUTO_NIGHT, false);
		setListener();
		setBooksettingNight();
		jumpType = getArguments().getInt("type", 1);
		isAutoLoad = SharedPreferences.getInstance().getBoolean(
				SpConstant.BOOK_SETTING_AUTO_LOAD, false);

	}

	public void setListener() {
		booksetting_traditional.setOnClickListener(this);
		booksetting_simplified.setOnClickListener(this);
		tv_pre_chapter.setOnClickListener(this);
		tv_next_chapter.setOnClickListener(this);
		seekBar_chapter.setOnSeekBarChangeListener(this);
		booksetting_share.setOnClickListener(this);
		booksetting_night.setOnClickListener(this);
		booksetting_bookmrak.setOnClickListener(this);
		booksetting_textface_1.setOnClickListener(onTextTypeChangeListener);
		booksetting_textface_2.setOnClickListener(onTextTypeChangeListener);
		booksetting_textface_3.setOnClickListener(onTextTypeChangeListener);
		booksetting_textface_4.setOnClickListener(onTextTypeChangeListener);
		booksetting_textface_5.setOnClickListener(onTextTypeChangeListener);
	}

	public interface OnFragment1Listener {

		public void onTextTypeChange();

		public void onTextTypeChildrenChange();

		public void onPreChapter();

		public void onNextChapter();

		public void onSeekBarChapter(int chapterIndex);

		public void onCloseTitleBarAndBottomBar();

		public void onNightMode();

		public void onUmengShare();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.booksetting_traditional:
			SharedPreferences.getInstance().putInt(
					SpConstant.BOOK_SETTING_TEXT_TYPE, 0);
			booksetting_traditional.setTextColor(getResources().getColor(
					R.color.book_setting_text_type_color));
			booksetting_simplified.setTextColor(getResources().getColor(
					R.color.book_setting_text_color));
			mListener.onTextTypeChange();

			break;
		case R.id.booksetting_simplified:
			booksetting_simplified.setTextColor(getResources().getColor(
					R.color.book_setting_text_type_color));
			booksetting_traditional.setTextColor(getResources().getColor(
					R.color.book_setting_text_color));
			SharedPreferences.getInstance().putInt(
					SpConstant.BOOK_SETTING_TEXT_TYPE, 1);
			mListener.onTextTypeChange();
			break;
		case R.id.booksetting_share:
			mListener.onUmengShare();
			break;
		case R.id.tv_pre_chapter:
			mListener.onPreChapter();

			break;
		case R.id.tv_next_chapter:
			mListener.onNextChapter();

			break;
		case R.id.booksetting_bookmrak:

			Intent intent = new Intent(getActivity(), BookMarkActivity.class);
			intent.putExtra("bookName", getArguments().getString("bookName"));
			intent.putExtra("position", currentPosition);
			intent.putExtra("lastUrl", getArguments().getString("lastUrl"));
			intent.putExtra("type", getArguments().getInt("type"));
			intent.putExtra("bookType", getArguments().getInt("bookType"));
			intent.putExtra("statue", getArguments().getString("statue"));
			intent.putExtra("gid", getArguments().getString("gid"));
			startActivity(intent);
			getActivity().overridePendingTransition(R.anim.dialog_enter_bottom,
					R.anim.dialog_no_animation);
			mListener.onCloseTitleBarAndBottomBar();
			break;
		case R.id.booksetting_night:

			isNightMode = SharedPreferences.getInstance().getBoolean(
					SpConstant.BOOK_SETTING_NIGHT_MODE, false);
			boolean isAutoNightMode = SharedPreferences.getInstance()
					.getBoolean(SpConstant.BOOK_SETTING_AUTO_NIGHT_MODE_YES,
							false);
			if (isAutoNightMode) {
				isNightMode = false;
			} else {
				isNightMode = !isNightMode;
			}
			SharedPreferences.getInstance().putBoolean(
					SpConstant.BOOK_AUTO_COLOR, false);
			setNightModeImg(isNightMode);
			SharedPreferences.getInstance().putBoolean(
					SpConstant.BOOK_SETTING_NIGHT_MODE, isNightMode);
			if (isNigthModeHand) {
				SharedPreferences.getInstance().putBoolean(
						SpConstant.BOOK_SETTING_AUTO_NIGHT, isNightMode);
				SharedPreferences.getInstance().putBoolean(
						SpConstant.BOOK_SETTING_AUTO_NIGHT_MODE_YES,
						isNightMode);
			}
			mListener.onNightMode();
			break;

		default:
			break;
		}
	}

	/**
	 * 初始化简繁体选中的字体颜色
	 */
	private void initTextTypeSeleted() {
		int type = SharedPreferences.getInstance().getInt(
				SpConstant.BOOK_SETTING_TEXT_TYPE, 1);
		if (type == 1) {
			booksetting_simplified.setTextColor(getResources().getColor(
					R.color.book_setting_text_type_color));
			booksetting_traditional.setTextColor(getResources().getColor(
					R.color.book_setting_text_color));
		} else {
			booksetting_traditional.setTextColor(getResources().getColor(
					R.color.book_setting_text_type_color));
			booksetting_simplified.setTextColor(getResources().getColor(
					R.color.book_setting_text_color));
		}

	}

	/**
	 * 换背景监听接口
	 * 
	 */
	private OnClickListener onTextTypeChangeListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			setTextTypeSeleted(v);
		}

	};

	private int jumpType;

	private boolean isAutoLoad;

	/**
	 * 改变图片选中时的图片
	 * 
	 * @param view
	 */
	private void setTextTypeSeleted(View view) {

		booksetting_textface_1.setTextColor(getResources().getColor(
				R.color.book_setting_text_color));
		booksetting_textface_2.setTextColor(getResources().getColor(
				R.color.book_setting_text_color));
		booksetting_textface_3.setTextColor(getResources().getColor(
				R.color.book_setting_text_color));
		booksetting_textface_4.setTextColor(getResources().getColor(
				R.color.book_setting_text_color));
		booksetting_textface_5.setTextColor(getResources().getColor(
				R.color.book_setting_text_color));

		switch (view.getId()) {
		case R.id.booksetting_textface_1:
			SharedPreferences.getInstance().putInt(
					SpConstant.BOOK_SETTING_TEXT_CHILDREN_TYPE, 1);
			booksetting_textface_1.setTextColor(getResources().getColor(
					R.color.book_setting_text_type_color));
			break;
		case R.id.booksetting_textface_2:
			booksetting_textface_2.setTextColor(getResources().getColor(
					R.color.book_setting_text_type_color));
			SharedPreferences.getInstance().putInt(
					SpConstant.BOOK_SETTING_TEXT_CHILDREN_TYPE, 2);
			break;
		case R.id.booksetting_textface_3:
			booksetting_textface_3.setTextColor(getResources().getColor(
					R.color.book_setting_text_type_color));
			SharedPreferences.getInstance().putInt(
					SpConstant.BOOK_SETTING_TEXT_CHILDREN_TYPE, 3);
			break;
		case R.id.booksetting_textface_4:
			booksetting_textface_4.setTextColor(getResources().getColor(
					R.color.book_setting_text_type_color));
			SharedPreferences.getInstance().putInt(
					SpConstant.BOOK_SETTING_TEXT_CHILDREN_TYPE, 4);
			break;
		case R.id.booksetting_textface_5:
			booksetting_textface_5.setTextColor(getResources().getColor(
					R.color.book_setting_text_type_color));
			SharedPreferences.getInstance().putInt(
					SpConstant.BOOK_SETTING_TEXT_CHILDREN_TYPE, 5);
			break;

		default:
			break;
		}
		mListener.onTextTypeChildrenChange();

	}

	/**
	 * 初始化选中的字体 颜色
	 */
	private void initTextTypeChildrenSeleted() {
		int index = SharedPreferences.getInstance().getInt(
				SpConstant.BOOK_SETTING_TEXT_CHILDREN_TYPE, 3);
		switch (index) {
		case 1:
			booksetting_textface_1.setTextColor(getResources().getColor(
					R.color.book_setting_text_type_color));
			break;
		case 2:
			booksetting_textface_2.setTextColor(getResources().getColor(
					R.color.book_setting_text_type_color));
			break;
		case 3:
			booksetting_textface_3.setTextColor(getResources().getColor(
					R.color.book_setting_text_type_color));
			break;
		case 4:
			booksetting_textface_4.setTextColor(getResources().getColor(
					R.color.book_setting_text_type_color));
			break;
		case 5:
			booksetting_textface_5.setTextColor(getResources().getColor(
					R.color.book_setting_text_type_color));
			break;

		default:
			break;
		}

	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		// TODO Auto-generated method stub
		int max = seekBar_chapter.getMax()+1;
		seekBar_chapter.setSeekBarText((progress + 1) + "页 / "
				+ max + "页");

	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		Log.i("msg", "seekBar.getProgress() = "+seekBar.getProgress());
		mListener.onSeekBarChapter(seekBar.getProgress());
	}

	/**
	 * 设置夜间模式的图片
	 */
	private void setNightModeImg(boolean isNightMode) {
		if (isNightMode)
			booksetting_night.setImageResource(R.drawable.iphone_night);
		else
			booksetting_night.setImageResource(R.drawable.iphone_daytime);
	}

	/**
	 * 夜间模式发生改变时，通知改变图片
	 * 
	 */
	public static class ChangeNightReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			booksetting_night.setImageResource(R.drawable.iphone_daytime);
		}

	}

	/**
	 * 设置夜间模式
	 */
	public void setBooksettingNight() {
		if (getArguments().getBoolean("mode")) {
			ViewSelector.setTextViewSelected(getActivity(), tv_next_chapter,
					BookTheme.BOOK_SETTING_PRESS_BG, BookTheme.BOOK_SETTING_BG);
			ViewSelector.setTextViewSelected(getActivity(), tv_pre_chapter,
					BookTheme.BOOK_SETTING_PRESS_BG, BookTheme.BOOK_SETTING_BG);
			ViewSelector.setTextViewSelected(getActivity(),
					booksetting_traditional, BookTheme.BOOK_SETTING_PRESS_BG,
					BookTheme.BOOK_SETTING_BG);
			ViewSelector.setTextViewSelected(getActivity(),
					booksetting_simplified, BookTheme.BOOK_SETTING_PRESS_BG,
					BookTheme.BOOK_SETTING_BG);
		}

	}

	/**
	 * 初始化拖动控件
	 */
	private void initPageSeekBar() {
		seekBar_chapter.setMax(chapterPageCount);
		seekBar_chapter.setProgress(currentPage);
		seekBar_chapter.setSeekBarText((currentPage+1) + "页 / " + (chapterPageCount+1)
				+ "页");

	}

}
