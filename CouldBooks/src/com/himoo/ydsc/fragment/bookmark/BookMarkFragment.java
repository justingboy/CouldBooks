package com.himoo.ydsc.fragment.bookmark;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.baoyz.swipemenulistview.SwipeMenuListView.OnMenuItemClickListener;
import com.himoo.ydsc.R;
import com.himoo.ydsc.base.BaseFragment;
import com.himoo.ydsc.base.quickadapter.BaseAdapterHelper;
import com.himoo.ydsc.base.quickadapter.QuickAdapter;
import com.himoo.ydsc.manager.PageManager;
import com.himoo.ydsc.reader.ReaderActivity;
import com.himoo.ydsc.reader.dao.BookMark;
import com.himoo.ydsc.reader.dao.BookMarkDb;
import com.himoo.ydsc.util.DeviceUtil;
import com.himoo.ydsc.util.SharedPreferences;
import com.lidroid.xutils.view.annotation.ViewInject;

public class BookMarkFragment extends BaseFragment {

	@ViewInject(R.id.tv_bookmark_empty)
	private TextView tv_bookmark_empty;

	@ViewInject(R.id.listview_bookmak)
	private SwipeMenuListView listView;
	private String bookName;
	/** 当前点击的Item位置 */
	private int mCurrentClickPosition = -1;
	/** 书签的集合list */
	private List<BookMark> bookMarklist;

	private BookMarkAdapter adapter;

	private BookMarkDb db;

	public static BookMarkFragment newInstance(String bookName, int bookType) {
		BookMarkFragment fragment = new BookMarkFragment();
		Bundle bundle = new Bundle();
		bundle.putString("bookName", bookName);
		bundle.putInt("bookType", bookType);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			SharedPreferences sp, Bundle savedInstanceState,
			PageManager pageManager) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_bookmark, container,
				false);
		return view;
	}

	@Override
	public void initData() {
		db = BookMarkDb.getInstance(getActivity(), "book");
		bookName = getArguments().getString("bookName");
		bookMarklist = db.querryReaderMark(bookName);

		if (bookMarklist != null && !bookMarklist.isEmpty()) {
			adapter = new BookMarkAdapter(getActivity(),
					R.layout.adapter_bookmark_item, bookMarklist);
			listView.setAdapter(adapter);
			listView.setOnItemClickListener(this);
		} else {
			tv_bookmark_empty.setVisibility(View.VISIBLE);
			listView.setVisibility(View.GONE);
		}
		initSwipListView();

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (mCurrentClickPosition != -1)
			return;
		mCurrentClickPosition = position;
		BookMark bookMark = (BookMark) parent.getItemAtPosition(position);
		Intent intent = new Intent(getActivity(), ReaderActivity.class);
		intent.putExtra("bookName", bookName);
		intent.putExtra("index", "1");
		int bookType = getArguments().getInt("bookType");
		intent.putExtra("bookType", bookType);
		intent.putExtra("position", bookMark.getPosition());
		intent.putExtra("currentPage", bookMark.getCurrentPage());
		intent.putExtra("pageCount", bookMark.getPageCount());
		startActivity(intent);
	}

	public class BookMarkAdapter extends QuickAdapter<BookMark> {

		public BookMarkAdapter(Context context, int layoutResId,
				List<BookMark> data) {
			super(context, layoutResId, data);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected void convert(BaseAdapterHelper helper, BookMark item) {
			// TODO Auto-generated method stub
			helper.setText(R.id.tv_bookmark_chapterName, item.getChapterName());
			helper.setText(
					R.id.tv_bookmark_progress,
					"第" + (item.getCurrentPage() + 2) + "/"
							+ item.getPageCount() + "页");
		}

	}

	/** 初始化SwipListView */
	private void initSwipListView() {
		// step 1. create a MenuCreator
		SwipeMenuCreator creator = new SwipeMenuCreator() {

			@Override
			public void create(SwipeMenu menu) {
				// create "delete" item
				SwipeMenuItem deleteItem = new SwipeMenuItem(getActivity());
				// set item background
				deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
						0x3F, 0x25)));
				// deleteItem.setBackground(getResources().getDrawable(
				// R.drawable.bg_new_common_title));
				// set item width
				deleteItem.setWidth(DeviceUtil.dip2px(getActivity(), 100));
				// set a icon
				deleteItem.setIcon(R.drawable.ic_delete);
				// add to menu
				menu.addMenuItem(deleteItem);
			}
		};
		// set creator
		listView.setMenuCreator(creator);
		listView.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public void onMenuItemClick(int position, SwipeMenu menu, int index) {
				BookMark bookMark = bookMarklist.get(position);
				adapter.remove(bookMark);
				adapter.notifyDataSetChanged();
				db.deletBookMark(bookMark);
			}
		});

	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mCurrentClickPosition = -1;
	}
}
