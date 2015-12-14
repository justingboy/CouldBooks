package com.himoo.ydsc.fragment.bookmark;

import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.himoo.ydsc.R;
import com.himoo.ydsc.base.BaseFragment;
import com.himoo.ydsc.base.quickadapter.BaseAdapterHelper;
import com.himoo.ydsc.base.quickadapter.QuickAdapter;
import com.himoo.ydsc.manager.PageManager;
import com.himoo.ydsc.reader.dao.BookMark;
import com.himoo.ydsc.reader.dao.BookMarkDb;
import com.himoo.ydsc.util.SharedPreferences;
import com.lidroid.xutils.view.annotation.ViewInject;

public class BookMarkFragment extends BaseFragment {

	@ViewInject(R.id.listview_catalog)
	private ListView listView;

	public static BookMarkFragment newInstance(String bookName) {
		BookMarkFragment fragment = new BookMarkFragment();
		Bundle bundle = new Bundle();
		bundle.putString("bookName", bookName);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			SharedPreferences sp, Bundle savedInstanceState,
			PageManager pageManager) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_catalog, container,
				false);
		return view;
	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub
		BookMarkDb db = BookMarkDb.getInstance(getActivity(), "book");
		String bookName = getArguments().getString("bookName");
		List<BookMark> bookMarklist = db.querryReaderMark(bookName);
		BookMarkAdapter adapter = new BookMarkAdapter(getActivity(),
				R.layout.adapter_bookmark_item, bookMarklist);
		listView.setAdapter(adapter);

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
			helper.setText(R.id.tv_bookmark_progress,
					"第" + item.getCurrentPage() + "/" + item.getPageCount()
							+ "页");
		}

	}

}
