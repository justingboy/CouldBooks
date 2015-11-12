package com.himoo.ydsc.activity;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.himoo.ydsc.R;
import com.himoo.ydsc.adapter.DouBanCommentAdapter;
import com.himoo.ydsc.bean.DouBanBookComment;
import com.himoo.ydsc.ui.swipebacklayout.SwipeBackActivity;
import com.himoo.ydsc.ui.utils.Toast;
import com.himoo.ydsc.ui.utils.UIHelper;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.view.annotation.ViewInject;

public class DoubanCommentActivity extends SwipeBackActivity {

	@ViewInject(R.id.douban_book_list)
	private PullToRefreshListView mRefrshListView;
	/** 标记当前点击的位置 */
	private int mCurrentClickPosition = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_douban_layout);
		initPullToRefrehListView();
		showRefreshDialog("正在加载中");
		getDouBanBookInfo(getIntent().getStringExtra("key2"));
	}

	@Override
	protected void initTitleBar() {
		// TODO Auto-generated method stub
		String bookName = getIntent().getStringExtra("key1");
		mTitleBar.setTitle(bookName);
		if (bookName.length() > 6) {
			mTitleBar.setLeftTitle("返回");
		} else {
			mTitleBar.setLeftTitle("图书列表");
		}
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
				if (mCurrentClickPosition == position)
					return;
				mCurrentClickPosition = position;
				DouBanBookComment comment = (DouBanBookComment) parent
						.getItemAtPosition(position);
				UIHelper.startToActivity(DoubanCommentActivity.this,
						DoubanWebActivity.class, comment.getCommentContent());

			}
		});
	}

	/**
	 * 获取豆瓣网上的评书信息
	 * 
	 * @param bookName
	 */
	private void getDouBanBookInfo(String subUrl) {
		HttpUtils http = new HttpUtils();
		String url = subUrl + "/reviews?alt=json";
		http.send(HttpMethod.GET, url, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				// TODO Auto-generated method stub
				try {
					ArrayList<DouBanBookComment> bookList = parseDoubanJson(responseInfo.result);
					DouBanCommentAdapter mAdapter = new DouBanCommentAdapter(
							DoubanCommentActivity.this,
							R.layout.adapter_douban_comment_item, bookList);
					dismissRefreshDialog();
					mRefrshListView.setAdapter(mAdapter);

				} catch (JSONException e) {
					dismissRefreshDialog();
					Toast.showLong(DoubanCommentActivity.this,
							"解析评书信息失败：" + e.getMessage());
				}
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				dismissRefreshDialog();
				Toast.showLong(DoubanCommentActivity.this, "获取评书信息失败：" + msg);
			}
		});
	}

	/**
	 * 解析豆瓣网返回的json数据
	 * 
	 * @param result
	 * @throws JSONException
	 */
	private ArrayList<DouBanBookComment> parseDoubanJson(String result)
			throws JSONException {

		ArrayList<DouBanBookComment> commentList = new ArrayList<DouBanBookComment>();
		JSONObject jsonObject = new JSONObject(result);
		JSONArray jsonArray = jsonObject.getJSONArray("entry");
		if (jsonArray.length() > 0) {
			for (int i = 0; i < jsonArray.length(); i++) {
				DouBanBookComment commment = new DouBanBookComment();
				JSONObject json = jsonArray.getJSONObject(i);
				String commentDate = json.getJSONObject("updated").getString(
						"$t");
				String commentTitle = json.getJSONObject("title").getString(
						"$t");
				String commentSummary = "";
				if (!json.isNull("summary"))
					commentSummary = json.getJSONObject("summary").getString(
							"$t");
				String commentContent = json.getJSONObject("id")
						.getString("$t");
				JSONArray commentImageArray = json.getJSONObject("author")
						.getJSONArray("link");
				String commentImage = "http://img3.douban.com/icon/u27352958-2.jpg";
				for (int j = 0; j < commentImageArray.length(); j++) {
					if (commentImageArray.getJSONObject(j).getString("@rel")
							.equals("icon")) {
						commentImage = commentImageArray.getJSONObject(j)
								.getString("@href");
					}

				}
				String commentName = "";
				if (!json.isNull("author")) {
					commentName = json.getJSONObject("author")
							.getJSONObject("name").getString("$t");
				}
				commment.setCommentName(commentName);
				commment.setCommentImage(commentImage);
				commment.setCommentDate(commentDate);
				commment.setCommentTitle(commentTitle);
				commment.setCommentSummary(commentSummary);
				commment.setCommentContent(commentContent);

				commentList.add(commment);
			}

		}

		return commentList;
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mCurrentClickPosition = -1;
	}

}
