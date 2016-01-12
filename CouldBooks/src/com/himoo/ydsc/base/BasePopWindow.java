package com.himoo.ydsc.base;

import android.content.Context;
import android.view.View;
import android.widget.PopupWindow;

import com.himoo.ydsc.util.MyLogger;

/**
 * 弹窗的页面基类
 * 
 * @author
 */
public abstract class BasePopWindow extends PopupWindow {
	protected View view;
	protected BaseFragment frag;
	protected Context context;
	public MyLogger Log;

	public BasePopWindow(View view, int width, int height, boolean focusable) {
		super(view, width, height, focusable);
		Log = MyLogger.kLog();
		this.view = view;
		findViews();
		setListener();
		initData();
	}

	public BasePopWindow(BaseFragment frag, View view, int width, int height,
			boolean focusable) {
		super(view, width, height, focusable);
		Log = MyLogger.kLog();
		this.frag = frag;
		this.view = view;
		findViews();
		setListener();
		initData();
	}
	public BasePopWindow(Context context, View view, int width, int height,
			boolean focusable) {
		super(view, width, height, focusable);
		Log = MyLogger.kLog();
		this.context = context;
		this.view = view;
		findViews();
		setListener();
		initData();
	}

	protected abstract void findViews();

	protected abstract void setListener();

	protected abstract void initData();

	public void setFrag(BaseFragment frag) {
		this.frag = frag;
	}

	/**
	 * 获取窗体索依附的Activity 如果fragment存在返回fragment所依附的Activity 如果不存在返回null
	 * 可以调用setFrag()方法设置Fragment
	 * 
	 * @return
	 */
	public Context getActivity() {
		if (frag != null)
			return frag.getActivity();
		else
			return view.getContext();
	}

	/**
	 * 获取PopWindow上显示的View
	 * 
	 * @return
	 */
	public View getPopView() {
		return view;
	}

	@SuppressWarnings("unchecked")
	public <T extends View> T findViewById(int id) {
		return (T) view.findViewById(id);
	}
}
