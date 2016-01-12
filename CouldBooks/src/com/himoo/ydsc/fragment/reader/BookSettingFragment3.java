package com.himoo.ydsc.fragment.reader;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.himoo.ydsc.R;
import com.himoo.ydsc.base.quickadapter.BaseAdapterHelper;
import com.himoo.ydsc.base.quickadapter.QuickAdapter;
import com.himoo.ydsc.config.SpConstant;
import com.himoo.ydsc.util.SharedPreferences;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

/**
 * 阅读界面设置
 * 
 */
public final class BookSettingFragment3 extends Fragment {

	private static Context mContext;

	@ViewInject(R.id.theme_cover_gridview)
	private GridView themeCoverGridView;

	/** 翻书动画 */
	private String mCurrentTitle = title[1];

	private final static String[] title = { "左手", "仿真", "无动画", "连动", "上下" };

	private final static int[] drawable = { R.drawable.iphone_b_pagemode1,
			R.drawable.iphone_b_pagemode2, R.drawable.iphone_b_pageselected,
			R.drawable.iphone_b_pagemode3p, R.drawable.iphone_b_pagemode4 };

	private OnFragment3Listener mListener;

	private boolean isNightMode = false;

	public static BookSettingFragment3 newInstance(Context context,
			boolean isNightMode) {
		BookSettingFragment3 fragment = new BookSettingFragment3();
		Bundle bundle = new Bundle();
		bundle.putBoolean("mode", isNightMode);
		fragment.setArguments(bundle);
		mContext = context;
		return fragment;
	}

	@Override
	public void onAttach(Context context) {
		// TODO Auto-generated method stub
		super.onAttach(context);
		mContext = context;
		try {
			mListener = (OnFragment3Listener) context;
		} catch (ClassCastException e) {
			throw new ClassCastException(context.toString()
					+ " must implement OnFragment3Listener");
		}

	}

	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_book_setting3, null);
		return view;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		ViewUtils.inject(this);
		initReadBookGridView();
		isNightMode = getArguments().getBoolean("mode");
	}

	private void initReadBookGridView() {
		this.mCurrentTitle = SharedPreferences.getInstance().getString(
				SpConstant.BOOK_TURNPAGE_TITLE_TYPE, title[1]);
		ArrayList<ReaderBookAnimation> readerAniList = new ArrayList<ReaderBookAnimation>();
		for (int i = 0; i < title.length; i++) {
			ReaderBookAnimation ani = new ReaderBookAnimation(title[i],
					drawable[i]);
			readerAniList.add(ani);
		}

		final QuickAdapter<ReaderBookAnimation> coverAdapter = new QuickAdapter<ReaderBookAnimation>(
				mContext, R.layout.adapter_readerbook_setting, readerAniList) {

			@Override
			protected void convert(BaseAdapterHelper helper,
					ReaderBookAnimation item) {
				// TODO Auto-generated method stub

				helper.setBackgroundRes(R.id.theme_cover_image, item.drawableId);
				helper.setText(R.id.theme_cover_text, item.title);
				if (isNightMode)
					helper.setTextColorRes(R.id.theme_cover_text,
							R.color.book_setting_text_color);

				if (item.title.equals(mCurrentTitle))
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
						SpConstant.BOOK_TURNPAGE_TITLE_TYPE, mCurrentTitle);
				if (position != 0)
					SharedPreferences.getInstance().putInt(
							SpConstant.BOOK_TURNPAGE_TYPE, position);
				coverAdapter.notifyDataSetChanged();
				if (mListener != null)
					if (position == 0)
						mListener.onTurnPageLeftMode();
					else
						mListener.onTurnPageChange();

			}
		});

	}

	public static class ReaderBookAnimation {
		public String title;
		public int drawableId;

		public ReaderBookAnimation(String title, int drawableId) {
			this.title = title;
			this.drawableId = drawableId;
		}
	}

	public interface OnFragment3Listener {
		public void onTurnPageChange();

		public void onTurnPageLeftMode();
	}
}
