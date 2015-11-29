package com.himoo.ydsc.fragment.reader;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.himoo.ydsc.R;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

/**
 * 阅读界面设置
 * 
 */
public class BookSettingFragment1 extends Fragment implements OnClickListener {

	private OnFragment1Listener mListener;

	@ViewInject(R.id.booksetting_share)
	private ImageView booksetting_share;

	public static BookSettingFragment1 newInstance() {
		BookSettingFragment1 fragment = new BookSettingFragment1();

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
		setListener();
	}

	public void setListener() {
		booksetting_share.setOnClickListener(this);
	}

	public interface OnFragment1Listener {
		public void onTextSizChange(float textSize);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.booksetting_share:
			mListener.onTextSizChange(100f);
			
			break;

		default:
			break;
		}
	}

}
