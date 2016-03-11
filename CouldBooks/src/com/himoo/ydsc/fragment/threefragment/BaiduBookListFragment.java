package com.himoo.ydsc.fragment.threefragment;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

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
import com.himoo.ydsc.adapter.BaiduBookClassifyAdapter;
import com.himoo.ydsc.bean.BaiduBookClassify;
import com.himoo.ydsc.config.BookTheme;
import com.himoo.ydsc.db.BookDb;
import com.himoo.ydsc.http.HttpConstant;
import com.himoo.ydsc.http.OkHttpClientManager;
import com.himoo.ydsc.ui.utils.Toast;
import com.squareup.okhttp.Request;

/**
 * 
 * 小说列表Fragment
 * 
 */
public class BaiduBookListFragment extends ListFragment {

	/** 分类-精选部分书库资源数据 */
	private ArrayList<BaiduBookClassify> list = new ArrayList<BaiduBookClassify>();
	/** 当前点击的ListView Item的位置 */
	private int mCurrentClickPosition = -1;
	private BookDb db;
	private BaiduBookClassifyAdapter mAdapter;

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
		String url = HttpConstant.BAIDU_BOOK_CLASSIFY_URL;
		OkHttpClientManager.getInstance().getAsyn(url,
				new OkHttpClientManager.ResultCallback<String>() {

					@Override
					public void onError(Request request, Exception e) {
						// TODO Auto-generated method stub
						List<BaiduBookClassify> bookList = db
								.queryBookClassify(BaiduBookClassify.class);
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
			JSONObject jsonObject = new JSONObject(json);
			if (jsonObject.getInt("errno") == 0
					&& jsonObject.get("errmsg").equals("ok")) {
				JSONObject subJsonObject = jsonObject.getJSONObject("result");
				String result = subJsonObject.getString("cates");
				Gson gosn = new Gson();
				ArrayList<BaiduBookClassify> bookList = gosn.fromJson(result,
						new TypeToken<ArrayList<BaiduBookClassify>>() {
						}.getType());
				list.addAll(bookList);
				db.saveBaiduBookClassify(bookList);
				initAdapter(list);
			}
		} catch (Exception e) {
			Toast.showLong(getActivity(), "解析书库分类出现错误！");
		}

	}

	/**
	 * 初始化Adapter
	 * 
	 * @param list
	 */
	private void initAdapter(ArrayList<BaiduBookClassify> list) {

		mAdapter = new BaiduBookClassifyAdapter(getActivity(),
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
		BaiduBookClassFragment bookClassFragment = new BaiduBookClassFragment();
		// 记住：这个地方必须用replace，而不是用add
		transaction.replace(R.id.classify_book_datails, bookClassFragment);
		// 将中间的item的内容放到Bundle对象当中，然后放到最右侧Fragment的参数当中
		Bundle args = new Bundle();
		args.putString("cateid", list.get(position).getCateid() + "");
		bookClassFragment.setArguments(args);
		transaction.commit();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (BookTheme.isThemeChange)
			if (mAdapter != null)
				mAdapter.notifyDataSetChanged();
	}

	@Override
	public void onDestroy() {
		if (mAdapter != null)
			mAdapter.destory();
		super.onDestroy();
	}
}
