package com.himoo.ydsc.fragment.threefragment;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.himoo.ydsc.R;
import com.himoo.ydsc.adapter.BookClassifyAdapter;
import com.himoo.ydsc.bean.BookClassify;
import com.himoo.ydsc.config.BookTheme;
import com.himoo.ydsc.db.BookDb;
import com.himoo.ydsc.http.HttpConstant;
import com.himoo.ydsc.http.OkHttpClientManager;
import com.himoo.ydsc.ui.utils.Toast;
import com.himoo.ydsc.util.SharedPreferences;
import com.squareup.okhttp.Request;

/**
 * 
 * 小说列表Fragment
 * 
 */
public class BookListFragment extends ListFragment {

	private ArrayList<BookClassify> list = new ArrayList<BookClassify>();
	private int mCurrentClickPosition = 0;
	private BookDb db;
	private BookClassifyAdapter mAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		return inflater.inflate(R.layout.fragment_listview, null);

	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		db = BookDb.getInstance(getActivity(), "Book");
		getBookClassList();
	}

	@Override
	public void onListItemClick(ListView listView, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(listView, v, position, id);
		if (mCurrentClickPosition != position) {
			showClassFragment(listView, position);
			mCurrentClickPosition = position;
		}

	}

	/**
	 * 获取书库分类的列表数据
	 */
	@SuppressWarnings("static-access")
	private void getBookClassList() {
		String url = SharedPreferences.getInstance().getString("host",
				HttpConstant.HOST_URL_TEST)
				+ "getBooksClass.asp";
		OkHttpClientManager.getInstance().getAsyn(url,
				new OkHttpClientManager.ResultCallback<String>() {

					@Override
					public void onError(Request request, Exception e) {
						// TODO Auto-generated method stub
						List<BookClassify> bookList = db
								.queryBookClassify(BookClassify.class);
						if (bookList == null) {
							Toast.showLong(getActivity(),
									"获取书籍分类错误,请检查网络状态是否正常！");
							return;
						}
						list.addAll(bookList);
						initAdapter(list);
					}

					@Override
					public void onResponse(String response) {
						// TODO Auto-generated method stub
						parseJson(response);
					}

				});

	}

	/**
	 * 解析json 数据
	 * 
	 * @param json
	 */
	private void parseJson(String json) {
		try {
			Gson gosn = new Gson();
			ArrayList<BookClassify> bookList = gosn.fromJson(json,
					new TypeToken<ArrayList<BookClassify>>() {
					}.getType());
			list.addAll(bookList);
			db.saveBookClassify(list);
			initAdapter(list);
		} catch (Exception e) {
			Toast.showLong(getActivity(), "解析书库分类出现错误!");
		}

	}

	/**
	 * 初始化Adapter
	 * 
	 * @param list
	 */
	private void initAdapter(ArrayList<BookClassify> list) {

		mAdapter = new BookClassifyAdapter(getActivity(),
				R.layout.fragment_list_texti_item, list);
		setListAdapter(mAdapter);
	}

	/**
	 * 展示具体分类的书籍的列表
	 * 
	 * @param listView
	 * @param position
	 */
	private void showClassFragment(ListView listView, int position) {
		FragmentManager manager = getParentFragment().getChildFragmentManager();
		FragmentTransaction transaction = manager.beginTransaction();
		BookClassFragment bookClassFragment = new BookClassFragment();
		// 记住：这个地方必须用replace，而不是用add
		transaction.replace(R.id.classify_book_datails, bookClassFragment);
		// 将中间的item的内容放到Bundle对象当中，然后放到最右侧Fragment的参数当中
		Bundle args = new Bundle();
		args.putString("class", list.get(position).getClass_ID() + "");
		bookClassFragment.setArguments(args);
		transaction.commit();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (BookTheme.isThemeChange)
			if (mAdapter != null) {
				mAdapter.notifyDataSetChanged();
			}
	}

	@Override
	public void onDestroy() {
		if (mAdapter != null)
			mAdapter.destory();
		super.onDestroy();
	}
}
