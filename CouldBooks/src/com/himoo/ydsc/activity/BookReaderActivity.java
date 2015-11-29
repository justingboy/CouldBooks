package com.himoo.ydsc.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.himoo.ydsc.R;
import com.himoo.ydsc.http.BookDetailsTask;
import com.himoo.ydsc.listener.OnRequestCallBack;
import com.himoo.ydsc.ui.swipebacklayout.SwipeBackActivity;
import com.himoo.ydsc.ui.utils.Toast;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.view.annotation.ViewInject;

public class BookReaderActivity extends SwipeBackActivity implements
		OnRequestCallBack {

	private String chapterUrl;
	@ViewInject(R.id.tv_chapter)
	private TextView tv_chapter;
	private String index;
	private String bookName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_read_chapter);
		// paserBookChapter(chapterUrl);
		showRefreshDialog("正在加载中");

		BookDetailsTask.getInstance().send(chapterUrl, bookName, index, this);

	}

	@Override
	protected void initTitleBar() {
		// TODO Auto-generated method stub
		Intent intent = getIntent();
		String chapterName = intent.getStringExtra("chapterName");
		chapterUrl = intent.getStringExtra("chapterUrl");
		bookName = intent.getStringExtra("bookName");
		index = intent.getStringExtra("index");

		mTitleBar.setTitle(chapterName);
		mTitleBar.setLeftTitle("");
		mTitleBar.setRightLogoGone();

	}

	/**
	 * 解析每章节的内容
	 * 
	 * @param url
	 * @return
	 */
	protected void paserBookChapter(String url) {
		HttpUtils http = new HttpUtils();
		http.send(HttpMethod.GET, url, new RequestCallBack<String>() {

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				// TODO Auto-generated method stub
				JSONObject json = new JSONObject();
				try {
					if (json.getInt("status") == 1) {
						String bookContent = json.getJSONObject("data")
								.getString("content");
						tv_chapter.setText(bookContent);
					} else {
						Toast.showLong(BookReaderActivity.this, "本章暂无内容");
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

			@Override
			public void onFailure(HttpException error, String msg) {
				// TODO Auto-generated method stub
				Toast.showLong(BookReaderActivity.this, "本章暂无内容");
			}
		});

	}

	@Override
	public void onSuccess(final String response) {
		// TODO Auto-generated method stub
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				dismissRefreshDialog();
				if (!response.contains("status")) {
					tv_chapter.setText(response);
				} else {
					try {
						JSONObject json = new JSONObject(response);
						if (json.getInt("status") == 1) {
							String bookContent = json
									.getJSONObject("data")
									.getString("content")
									.replaceAll(
											"<p style="
													+ "\"text-indent:2em;\">",
											"\n        ")
									.replaceAll("<div  class=\"content.*<div  style=\"display:inline-block;\"></div>    ", "\n        ")
									.replaceAll("</p>", "\n")
									.replaceAll("<div[\\s]*>", "")
									.replaceAll("</div>", "\n")
									.replaceAll("<br/>", "\n\n")
									.replaceAll("<span style=\".*\">", "")
									.replaceAll("</span>", "\n")
									.replaceAll("<[/]?dd.*>", "\n")
									.replaceAll("<div.*>", "\n")
									.replaceAll("&nbsp; &nbsp;", "      ")
									.replaceAll("<br>", "")
									.replaceAll("readx.* ", "       ")
									.replaceAll("<a  class=\" yi-fontcolor\" .*", "")
									.replaceAll("<p  class=\" .*p\">", "\n         ")
									.replaceAll("<p  class=\".*p\">", "\n        ");
							
							tv_chapter.setText(bookContent);
						} else {

							Toast.showLong(BookReaderActivity.this, "本章暂无内容");
						}
					} catch (JSONException e1) {
						// TODO Auto-generated catch block

						Toast.showLong(BookReaderActivity.this, "解析章节内容失败");
					}
				}
			}
		});

	}

	@Override
	public void onFailure(String errorMsg) {
		// TODO Auto-generated method stub
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				dismissRefreshDialog();
				Toast.showLong(BookReaderActivity.this, "本章暂无内容");
			}
		});
	}

}
