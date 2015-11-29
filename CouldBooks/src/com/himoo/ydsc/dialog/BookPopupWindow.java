package com.himoo.ydsc.dialog;

import java.util.ArrayList;
import java.util.Collections;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.himoo.ydsc.R;
import com.himoo.ydsc.activity.SearchResultActivity;
import com.himoo.ydsc.base.BaseFragment;
import com.himoo.ydsc.base.BasePopWindow;
import com.himoo.ydsc.db.BookDb;
import com.himoo.ydsc.db.bean.BookSearchRecords;
import com.himoo.ydsc.ui.utils.ViewSelector;
import com.himoo.ydsc.util.DeviceUtil;

/**
 * 搜索历史记录弹出窗
 * 
 */
public class BookPopupWindow extends BasePopWindow implements OnClickListener,
		OnItemClickListener {

	/** 展示搜索记录 */
	private ListView pop_listview;
	/** 清除历史记录 */
	private Button popup_btn_clear;
	/** 关闭 */
	private Button popup_btn_cancel;
	private BookDb bookDb;

	private BaseFragment fragment;
	public ArrayAdapter<String> adapter;
	private String[] bookArrays;

	public BookPopupWindow(BaseFragment frag, View view) {
		this(frag, view, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
				true);
		this.fragment = frag;
	}

	public BookPopupWindow(BaseFragment frag, View view, int width, int height,
			boolean focusable) {
		super(frag, view, width, height, focusable);
		this.fragment = frag;
		this.setWidth(DeviceUtil.dip2px(getActivity(), 280));
		initView();
	}

	@SuppressWarnings("deprecation")
	private void initView() {
		// 使其聚集
		this.setFocusable(false);
		// 设置允许在外点击消失
		this.setOutsideTouchable(true);
		// 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景
		this.setBackgroundDrawable(new BitmapDrawable());
	}

	@Override
	public void findViews() {
		pop_listview = (ListView) view.findViewById(R.id.pop_listview);
		popup_btn_clear = (Button) view.findViewById(R.id.popup_btn_clear);
		popup_btn_cancel = (Button) view.findViewById(R.id.popup_btn_cancel);
		ViewSelector.setButtonSelector(getActivity(), popup_btn_clear);
		ViewSelector.setButtonSelector(getActivity(), popup_btn_cancel);
		
	}

	@Override
	protected void setListener() {
		// TODO Auto-generated method stub
		pop_listview.setOnItemClickListener(this);
		popup_btn_clear.setOnClickListener(this);
		popup_btn_cancel.setOnClickListener(this);
	}

	@Override
	public void initData() {
		notifiyDataChange();
		adapter = new ArrayAdapter<String>(getActivity(),
				R.layout.popup_listview_item, bookArrays);
		pop_listview.setAdapter(adapter);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		String keyWord = (String) parent.getItemAtPosition(position);
		startToActivity(keyWord);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.popup_btn_clear:
			bookDb.deleteRecords(BookSearchRecords.class);
			this.dismiss();
			break;
		case R.id.popup_btn_cancel:
			this.dismiss();
			break;

		default:
			break;
		}
	}

	/**
	 * 跳转到搜索结果的界面
	 * 
	 * @param keyWord
	 */
	protected void startToActivity(String keyWord) {
		Intent intent = new Intent(getActivity(), SearchResultActivity.class);
		intent.putExtra("keyWord", keyWord);
		this.fragment.getActivity().startActivity(intent);
		this.fragment.getActivity().overridePendingTransition(
				R.anim.activity_zoom_in, 0);

	}

	/**
	 * 通知数据改变
	 */
	public void notifiyDataChange() {
		bookDb = BookDb.getInstance(getActivity(), "Book");
		ArrayList<BookSearchRecords> records = bookDb.querryAll();
		if (records != null) {
			Collections.reverse(records);
			bookArrays = new String[records.size()];
			for (int i = 0; i < records.size(); i++) {
				bookArrays[i] = records.get(i).getRecord();
			}
		}
	}
}
