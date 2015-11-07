package com.himoo.ydsc.fragment.threefragment;

import java.util.ArrayList;

import org.json.JSONObject;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.himoo.ydsc.R;
import com.himoo.ydsc.bean.BaiduBookClassify;
import com.himoo.ydsc.http.HttpConstant;
import com.himoo.ydsc.ui.utils.Toast;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

/**
 * 
 * 小说列表Fragment
 * 
 */
public class BaiduBookListFragment extends ListFragment {
	
	/** 分类-精选部分书库资源数据 */
	private ArrayList<BaiduBookClassify> list;
	/** 当前点击的ListView Item的位置 */
	private int mCurrentClickPosition = -1;

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
	private void getBookClassList() {
		HttpUtils http = new HttpUtils();
		String url = HttpConstant.BAIDU_BOOK_CLASSIFY_URL;
		http.send(HttpMethod.GET, url, new RequestCallBack<String>() {

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				// TODO Auto-generated method stub
				parseJson(responseInfo.result);
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				// TODO Auto-generated method stub
				Toast.showLong(getActivity(), "获取数据错误：" + msg);
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
				list = gosn.fromJson(result,
						new TypeToken<ArrayList<BaiduBookClassify>>() {
						}.getType());

				String[] bookArray = new String[list.size()];
				for (int i = 0; i < bookArray.length; i++) {
					bookArray[i] = list.get(i).getCatename();
				}
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(
						getActivity(), R.layout.fragment_list_texti_item,
						bookArray);
				setListAdapter(adapter);
			}
		} catch (Exception e) {
			Toast.showLong(getActivity(), "解析百度书库分类出现错误！");
		}

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
}
