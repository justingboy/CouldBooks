package com.himoo.ydsc.dialog;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;

import com.himoo.ydsc.R;
import com.himoo.ydsc.base.BaseFragment;
import com.himoo.ydsc.base.BasePopWindow;
import com.himoo.ydsc.ui.utils.ViewSelector;
import com.himoo.ydsc.util.DeviceUtil;

public class BookDeletePopupWindow extends BasePopWindow implements
		android.view.View.OnClickListener {

	private Button btn_allSelected;
	private Button btn_allNotSelected;
	private Button btn_deleted;
	private Button btn_cancel;
	private OnPopupClickListener mListener;

	public BookDeletePopupWindow(BaseFragment frag, View view) {
		this(frag, view, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
				true);
	}

	public BookDeletePopupWindow(BaseFragment frag, View view, int width,
			int height, boolean focusable) {
		super(frag, view, width, height, focusable);
		this.setWidth(DeviceUtil.getWidth((Activity) getActivity()));
		initView();
	}

	@SuppressWarnings("deprecation")
	private void initView() {
		// 使其聚集
		this.setFocusable(false);
		// 设置允许在外点击消失
		this.setOutsideTouchable(false);
		// 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景
		this.setBackgroundDrawable(new BitmapDrawable());
	}

	@Override
	public void findViews() {
		btn_allSelected = (Button) view.findViewById(R.id.btn_allSelected);
		btn_allNotSelected = (Button) view
				.findViewById(R.id.btn_allNotSelected);
		btn_deleted = (Button) view.findViewById(R.id.btn_deleted);
		btn_cancel = (Button) view.findViewById(R.id.btn_cancel);

		ViewSelector.setButtonSelector(getActivity(), btn_allSelected);
		ViewSelector.setButtonSelector(getActivity(), btn_allNotSelected);
		ViewSelector.setButtonSelector(getActivity(), btn_deleted);
		ViewSelector.setButtonSelector(getActivity(), btn_cancel);
	}

	@Override
	protected void setListener() {
		// TODO Auto-generated method stub
		btn_allSelected.setOnClickListener(this);
		btn_allNotSelected.setOnClickListener(this);
		btn_deleted.setOnClickListener(this);
		btn_cancel.setOnClickListener(this);
	}

	@Override
	public void initData() {
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (mListener != null)
			mListener.onPopupClick(v);
	}

	public interface OnPopupClickListener {
		public void onPopupClick(View view);

	}

	public void setOnPopupClickListener(OnPopupClickListener listener) {
		this.mListener = listener;
	}

}
