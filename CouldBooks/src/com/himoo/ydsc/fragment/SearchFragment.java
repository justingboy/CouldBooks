package com.himoo.ydsc.fragment;

import java.util.ArrayList;
import java.util.Random;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.himoo.ydsc.R;
import com.himoo.ydsc.base.BaseFragment;
import com.himoo.ydsc.bean.BookKeyWord;
import com.himoo.ydsc.http.HttpConstant;
import com.himoo.ydsc.http.HttpOperator;
import com.himoo.ydsc.manager.PageManager;
import com.himoo.ydsc.ui.utils.Toast;
import com.himoo.ydsc.ui.view.KeywordsFlow;
import com.ios.edittext.SearchEditText;
import com.ios.edittext.SearchEditText.OnSearchClickListener;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.view.annotation.ViewInject;

/**
 * 
 * 小說搜索功能模块
 * 
 */
public class SearchFragment extends BaseFragment implements
		OnSearchClickListener {

	/** Title */
	@ViewInject(R.id.titlebar)
	private TextView titleBar;

	/** 搜索 EditText */
	@ViewInject(R.id.more_book_search)
	private SearchEditText bookSearch;

	/** 飞入飞出效果View ：KeywordsFlow */
	@ViewInject(R.id.search_keywordFlow)
	private KeywordsFlow keywordsFlow;

	/** 　默认的关键字 */
	public static String[] keywords = { "花千骨", "盗墓笔记", "华胥引", "重生", "大主宰",
			"校花的贴身高手", "唐七公子", "凡人修仙传", "魔天记", "换一换" };

	/** 请求关键字的的页数 */
	private int mCurrentPage = 1;
	/** XUtils http 请求 */
	private HttpUtils http;
	/** 用于判断换一换是否可以被点击 ，主要是当网络不好时，点击会被多次调用 */
	private boolean isClickable = true;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			SharedPreferences sp, Bundle savedInstanceState,
			PageManager pageManager) {
		// TODO Auto-generated method stub
		View view = inflater
				.inflate(R.layout.fragment_search, container, false);

		return view;
	}

	@Override
	public void initData() {
		http = new HttpUtils();
		titleBar.setText(getActivity().getResources().getString(
				R.string.main_search));
		initKeyWordFlow();
		bookSearch.setOnSearchClickListener(this);

	}

	/**
	 * 初始化KeywordsFlow
	 */
	private void initKeyWordFlow() {
		keywordsFlow.setTextColor("#FF00B64F");
		keywordsFlow.setDuration(800l);
		keywordsFlow.setOnItemClickListener(this);
		feedKeywordsFlow(keywordsFlow, keywords);
		keywordsFlow.go2Show(KeywordsFlow.ANIMATION_IN);
	}

	/**
	 * 添加关键字
	 * 
	 * @param keywordsFlow
	 * @param keyWordArray
	 */
	private void feedKeywordsFlow(KeywordsFlow keywordsFlow,
			String[] keyWordArray) {
		for (int i = 0; i < keyWordArray.length; i++) {
			keywordsFlow.feedKeyword(keyWordArray[i]);
		}
	}

	@Override
	public void onSearchClick(View view) {
		// TODO Auto-generated method stub
		Toast.showShort(getActivity(), "去搜索小说了！");
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		String keyword = ((TextView) v).getText().toString();
		if (keyword.equals("换一换")) {
			if (isClickable) {
				isClickable = false;
				getKeyWordRequest(mCurrentPage, 9);
			}
		} else {

			Toast.showShort(getActivity(), keyword);
		}

	}

	/**
	 * 点击（换一换）TextView飞入飞出效果
	 */
	private void changeBook(ArrayList<BookKeyWord> list) {
		int size = list.size();
		for (int i = 0; i < size; i++) {
			keywords[i] = list.get(i).getKeyword();
		}
		keywordsFlow.rubKeywords();
		// keywordsFlow.rubAllViews();
		feedKeywordsFlow(keywordsFlow, keywords);
		Random random = new Random();
		if (random.nextInt(30) % 2 == 0) {
			keywordsFlow.go2Show(KeywordsFlow.ANIMATION_IN);
		} else {
			keywordsFlow.go2Show(KeywordsFlow.ANIMATION_OUT);
		}

		isClickable = true;
	}

	/**
	 * 请求关键字
	 * 
	 * @param page
	 * @param size
	 */
	private void getKeyWordRequest(int page, int size) {
		if (http == null)
			http = new HttpUtils();
		String url = HttpConstant.BASE_URL_KEYWORD
				+ HttpOperator.getKeyWordRequestHeard(page, size);
		http.send(HttpMethod.GET, url, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				// TODO Auto-generated method stub
				Gson gson = new Gson();
				ArrayList<BookKeyWord> list = gson.fromJson(
						responseInfo.result,
						new TypeToken<ArrayList<BookKeyWord>>() {
						}.getType());
				changeBook(list);
				mCurrentPage++;
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				// TODO Auto-generated method stub
				isClickable = true;
				if(getActivity()!=null)
				Toast.showShort(getActivity(), "请求关键字失败 ：" + msg);
			}
		});
	}
}
