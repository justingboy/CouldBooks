package com.himoo.ydsc.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.himoo.ydsc.R;
import com.himoo.ydsc.base.BaseFragment;
import com.himoo.ydsc.manager.PageManager;
import com.himoo.ydsc.ui.utils.Toast;
import com.himoo.ydsc.ui.view.BookTitleBar;
import com.himoo.ydsc.util.SharedPreferences;
import com.ios.dialog.ActionSheetDialog;
import com.ios.dialog.ActionSheetDialog.OnSheetItemClickListener;
import com.ios.dialog.ActionSheetDialog.SheetItemColor;

/**
 * 用于展示下载及阅读过的书
 * 
 */
public class BookShelfFragment extends BaseFragment {

	/** 标题栏 */
	private BookTitleBar titleBar;
	/** 判断是否处于删除状态 */
	private boolean isDelectStute = false;
	private Button bookSort;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			SharedPreferences sp, Bundle savedInstanceState,
			PageManager pageManager) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_bookshelf, null);
		initTitleBar(view);
		return view;
	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub

	}

	/**
	 * 初始化TitleBar
	 * 
	 * @param view
	 */
	private void initTitleBar(View view) {
		bookSort = (Button) view.findViewById(R.id.btn_book_name_sort);
		titleBar = (BookTitleBar) view.findViewById(R.id.book_titleBar);
		titleBar.setLeftDrawable(R.drawable.book_deleted);
		titleBar.setLeftTitle("  ");
		View layout = View.inflate(getActivity(),
				R.layout.titlebar_custom_view, null);
		titleBar.setTitleView(layout);

		titleBar.getLeftTextView().setOnClickListener(this);
		bookSort.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		switch (v.getId()) {
		case R.id.titlebar_left_title:
			if (!isDelectStute) {
				titleBar.setLeftDrawable(R.drawable.book_deleted_press);
				isDelectStute = true;
			} else {
				titleBar.setLeftDrawable(R.drawable.book_deleted);
				isDelectStute = false;
			}
			break;

		case R.id.btn_book_name_sort:
			showBookSortDialog();
			break;

		default:
			break;
		}

	}

	/**
	 * 书籍展示的排序
	 */
	private void showBookSortDialog() {
		new ActionSheetDialog(getActivity())
				.builder()
				.setTitle("请选择操作")
				.setCancelable(false)
				.setCanceledOnTouchOutside(false)
				.addSheetItem("条目一", SheetItemColor.Blue,
						new OnSheetItemClickListener() {
							@Override
							public void onClick(int which) {

							}
						})
				.addSheetItem("条目二", SheetItemColor.Blue,
						new OnSheetItemClickListener() {
							@Override
							public void onClick(int which) {

							}
						})
				.addSheetItem("条目三", SheetItemColor.Blue,
						new OnSheetItemClickListener() {
							@Override
							public void onClick(int which) {

							}
						})
				.addSheetItem("条目四", SheetItemColor.Blue,
						new OnSheetItemClickListener() {
							@Override
							public void onClick(int which) {

							}
						})
				.addSheetItem("条目五", SheetItemColor.Blue,
						new OnSheetItemClickListener() {
							@Override
							public void onClick(int which) {

							}
						}).show();
	}

}
