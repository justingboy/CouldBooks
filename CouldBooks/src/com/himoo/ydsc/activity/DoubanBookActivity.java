package com.himoo.ydsc.activity;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.himoo.ydsc.R;
import com.himoo.ydsc.adapter.DouBanBookAdapter;
import com.himoo.ydsc.bean.DoubanBook;
import com.himoo.ydsc.http.HttpConstant;
import com.himoo.ydsc.ui.swipebacklayout.SwipeBackActivity;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.view.annotation.ViewInject;

public class DoubanBookActivity extends SwipeBackActivity {

	@ViewInject(R.id.douban_book_list)
	private PullToRefreshListView mRefrshListView;

	@ViewInject(R.id.tv_book_empty)
	private TextView tv_book_empty;

	/** 标记当前点击的位置 */
	private int mCurrentClickPosition = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_douban_layout);
		initPullToRefrehListView();
		showRefreshDialog("正在加载中");
		getDouBanBookInfo(getIntent().getStringExtra("key"));
	}

	@Override
	protected void initTitleBar() {
		// TODO Auto-generated method stub
		String bookName = getIntent().getStringExtra("key");
		if (bookName == null) {
			bookName = "返回";
		} else {
			bookName = (bookName.length() > 4) ? "返回" : bookName;
		}
		mTitleBar.setTitle("图书列表");
		mTitleBar.setLeftTitle(bookName);
		mTitleBar.setRightLogoGone();

	}

	/**
	 * 初始化ListView
	 */
	private void initPullToRefrehListView() {
		mRefrshListView.setMode(Mode.DISABLED);
		mRefrshListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (mCurrentClickPosition != -1)
					return;
				mCurrentClickPosition = position;
				DoubanBook book = (DoubanBook) parent
						.getItemAtPosition(position);
				startToActivity(book.getBookName(), book.getBookDetailsUrl());

			}
		});
	}

	private void startToActivity(String bookName, String subjectUrl) {

		Intent intent = new Intent(this, DoubanCommentActivity.class);
		intent.putExtra("key1", bookName);
		intent.putExtra("key2", subjectUrl);
		startActivity(intent);
		overridePendingTransition(R.anim.activity_zoom_in, 0);

	}

	/**
	 * 获取豆瓣网上的评书信息
	 * 
	 * @param bookName
	 */
	private void getDouBanBookInfo(String bookName) {
		HttpUtils http = new HttpUtils();
		String url = HttpConstant.DOUBAN_STORYTELLING_URL + "q=" + bookName
				+ "&alt=json";
		http.send(HttpMethod.GET, url, new RequestCallBack<String>() {

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				// TODO Auto-generated method stub
				try {
					ArrayList<DoubanBook> bookList = parseDoubanJson(responseInfo.result);
					if (bookList != null && !bookList.isEmpty()) {
						DouBanBookAdapter mAdapter = new DouBanBookAdapter(
								DoubanBookActivity.this,
								R.layout.adapter_douban_item, bookList);
						dismissRefreshDialog();
						mRefrshListView.setAdapter(mAdapter);
					} else {
						dismissRefreshDialog();
						setEmptyView();
					}

				} catch (JSONException e) {
					dismissRefreshDialog();
					setEmptyView();
					// Toast.showLong(DoubanBookActivity.this,
					// "解析评书信息失败：" + e.getMessage());
				}
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				dismissRefreshDialog();
				setEmptyView();
				showNiftyNotification("获取评书信息失败：" + msg, R.id.Nifty_Lyout);
				// Toast.showLong(DoubanBookActivity.this,"获取评书信息失败：" + msg);
			}
		});
	}

	/**
	 * 无数据的显示View
	 */
	private void setEmptyView() {
		mRefrshListView.setVisibility(View.GONE);
		tv_book_empty.setVisibility(View.VISIBLE);
	}

	/**
	 * 解析豆瓣网返回的json数据
	 * 
	 * @param result
	 * @throws JSONException
	 */
	private ArrayList<DoubanBook> parseDoubanJson(String result)
			throws JSONException {

		ArrayList<DoubanBook> bookList = new ArrayList<DoubanBook>();
		JSONObject jsonObject = new JSONObject(result);
		JSONArray jsonArray = jsonObject.getJSONArray("entry");
		if (jsonArray.length() > 0) {
			for (int i = 0; i < jsonArray.length(); i++) {
				DoubanBook book = new DoubanBook();
				JSONObject json = jsonArray.getJSONObject(i);
				String bookAuthor = "";
				if (!json.isNull("author")) {
					bookAuthor = json.getJSONArray("author").getJSONObject(0)
							.getJSONObject("name").getString("$t");
				}

				String bookName = json.getJSONObject("title").getString("$t");
				String bookDetailsUrl = json.getJSONArray("link")
						.getJSONObject(0).getString("@href");
				String bookCoverUrl = json.getJSONArray("link")
						.getJSONObject(2).getString("@href");
				JSONArray dbArray = json.getJSONArray("db:attribute");
				String bookPublisher = "";
				String bookPubdate = "";
				for (int j = 0; j < dbArray.length(); j++) {
					if (dbArray.getJSONObject(j).getString("@name")
							.equals("pubdate")) {
						bookPubdate = dbArray.getJSONObject(j).getString("$t");
					}
					if (dbArray.getJSONObject(j).getString("@name")
							.equals("publisher")) {
						bookPublisher = dbArray.getJSONObject(j)
								.getString("$t");
					}
				}

				String bookNumRaters = json.getJSONObject("gd:rating")
						.getString("@numRaters") + "";
				String bookAverage = json.getJSONObject("gd:rating").getString(
						"@average")
						+ "";

				book.setBookName(bookName);
				book.setBookAuthor(bookAuthor);
				book.setBookDetailsUrl(bookDetailsUrl);
				book.setCoverImageUrl(bookCoverUrl);
				book.setBookAverage(bookAverage);
				book.setBookNumRaters(bookNumRaters);
				book.setBookPubdate(bookPubdate);
				book.setBookPublisher(bookPublisher);
				bookList.add(book);
			}

		}

		return bookList;
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mCurrentClickPosition = -1;
	}

}
