package com.himoo.ydsc.fragment.reader;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.himoo.ydsc.config.SpConstant;
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

	@ViewInject(R.id.booksetting_traditional)
	private TextView booksetting_traditional;

	@ViewInject(R.id.booksetting_simplified)
	private TextView booksetting_simplified;

	@ViewInject(R.id.booksetting_share)
	private ImageView booksetting_share;

	@ViewInject(R.id.booksetting_bookmrak)
	private ImageView booksetting_bookmrak;

	@ViewInject(R.id.tv_pre_chapter)
	private TextView tv_pre_chapter;

	@ViewInject(R.id.tv_next_chapter)
	private TextView tv_next_chapter;

	@ViewInject(R.id.seekBar_chapter)
	private SeekBar seekBar_chapter;

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

	public static BookSettingFragment1 newInstance(String bookName,int chapterSize) {
		BookSettingFragment1 fragment = new BookSettingFragment1();
		Bundle bundle = new Bundle();
		bundle.putInt("chapterSize", chapterSize);
		bundle.putString("bookName", bookName);
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
		initTextTypeSeleted();
		initTextTypeChildrenSeleted();
		int chapterSize = getArguments().getInt("chapterSize");
		seekBar_chapter.setMax(chapterSize);
		setListener();
	}

	public void setListener() {
		booksetting_traditional.setOnClickListener(this);
		booksetting_simplified.setOnClickListener(this);
		tv_pre_chapter.setOnClickListener(this);
		tv_next_chapter.setOnClickListener(this);
		seekBar_chapter.setOnSeekBarChangeListener(this);
		booksetting_share.setOnClickListener(this);
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
			startActivity(intent);

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
				SpConstant.BOOK_SETTING_TEXT_CHILDREN_TYPE, 1);
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
		mListener.onSeekBarChapter(progress);

	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub

	}

}
