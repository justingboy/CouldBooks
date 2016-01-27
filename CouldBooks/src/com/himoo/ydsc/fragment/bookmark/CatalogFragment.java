package com.himoo.ydsc.fragment.bookmark;

import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.himoo.ydsc.R;
import com.himoo.ydsc.base.BaseFragment;
import com.himoo.ydsc.base.quickadapter.BaseAdapterHelper;
import com.himoo.ydsc.base.quickadapter.QuickAdapter;
import com.himoo.ydsc.bean.BaiduBookChapter;
import com.himoo.ydsc.http.HttpConstant;
import com.himoo.ydsc.manager.PageManager;
import com.himoo.ydsc.reader.ReaderActivity;
import com.himoo.ydsc.reader.dao.BookMark;
import com.himoo.ydsc.reader.dao.BookMarkDb;
import com.himoo.ydsc.reader.utils.IOHelper;
import com.himoo.ydsc.ui.utils.Toast;
import com.himoo.ydsc.util.SharedPreferences;
import com.lidroid.xutils.view.annotation.ViewInject;

public class CatalogFragment extends BaseFragment {

	@ViewInject(R.id.listview_catalog)
	private ListView listView;
	private String bookName;
	private BookMark bookMark;
	/** 标记当前点击Item的位置 */
	private int mCurrentClickPosition = -1;
	/** 通知广播的Action */
	private static final String ACTION = "com.himoo.ydsc.catalog.receiver";
	private boolean isDownloading;
	private BookCatalogdapter mAdapter;

	public static CatalogFragment newInstance(String bookName, int type,
			int bookType) {
		CatalogFragment fragment = new CatalogFragment();
		Bundle bundle = new Bundle();
		bundle.putString("bookName", bookName);
		bundle.putInt("type", type);
		bundle.putInt("bookType", bookType);
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
		List<BaiduBookChapter> list = IOHelper.getBookChapter();
		bookName = getArguments().getString("bookName");
		bookMark = BookMarkDb.getInstance(getActivity(), "book")
				.querryReaderPos(bookName);
		mAdapter = new BookCatalogdapter(getActivity(),
				R.layout.adapter_catalog_item, list);
		listView.setAdapter(mAdapter);
		if (bookMark != null) {
			int pos = bookMark.getPosition();
			listView.setSelection(pos > 10 ? pos - 5 : 0);
		}
		listView.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (!isDownloading) {
			if (mCurrentClickPosition != -1)
				return;
			mCurrentClickPosition = position;
			BaiduBookChapter chapter = (BaiduBookChapter) parent
					.getItemAtPosition(position);
			startToReaderActivity(chapter, position);
		} else {
			Toast.show(getActivity(), "下载中,请稍后打开");
		}

	}

	public class BookCatalogdapter extends QuickAdapter<BaiduBookChapter> {

		public BookCatalogdapter(Context context, int layoutResId,
				List<BaiduBookChapter> data) {
			super(context, layoutResId, data);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected void convert(BaseAdapterHelper helper, BaiduBookChapter item) {
			// TODO Auto-generated method stub
			helper.setText(R.id.tv_bookCatalog, item.getText().trim());

			if (bookMark != null
					&& bookMark.getChapterName().equals(item.getText().trim())) {
				Log.i(item.getText().trim());
				helper.setTextRightDrawable(R.id.tv_bookCatalog,
						R.drawable.book_mark);
				helper.setTextColor(R.id.tv_bookCatalog, Color.RED);
			} else {
				helper.setTextRightDrawable(R.id.tv_bookCatalog,
						R.drawable.iphone_right_arrow);

				helper.setTextColorRes(R.id.tv_bookCatalog,
						R.color.main_bottom_textcolor_default);
			}

		}

	}

	private void startToReaderActivity(BaiduBookChapter chapter, int position) {
		Intent intent = new Intent(getActivity(), ReaderActivity.class);
		intent.putExtra("bookName", bookName);
		intent.putExtra("chapterName", chapter.getText().trim());
		intent.putExtra("index", chapter.getIndex());
		intent.putExtra("chapterUrl", getChapterUrl(chapter));
		int type = getArguments().getInt("type");
		int bookType = getArguments().getInt("bookType");
		intent.putExtra("bookType", bookType);
		intent.putExtra("isNeedSave", type == 1 ? false : true);
		intent.putExtra("position", position);
		startActivity(intent);

	}

	/**
	 * 拼接百度=书籍每章的地址
	 * 
	 * @param chapter
	 */
	protected String getChapterUrl(BaiduBookChapter chapter) {
		StringBuilder sb = new StringBuilder();
		sb.append(HttpConstant.BAIDU_CHAPTER_URL).append("src=")
				.append(chapter.getHref()).append("&cid=")
				.append(chapter.getCid()).append("&chapterIndex=")
				.append(chapter.getIndex()).append("&time=&skey=&id=wisenovel");

		return sb.toString();

	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mCurrentClickPosition = -1;
	}

	
	@Override
	public void onDestroy() {
		if(mAdapter!=null)
			mAdapter.destory();
		super.onDestroy();
	}
	
	
	public class BookUpdateReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub

			if (intent.getAction().equals(ACTION)) {
				isDownloading = intent.getBooleanExtra("isDownloading", false);
				if (!isDownloading) {
					initData();
				}
			}

		}

	}

}
