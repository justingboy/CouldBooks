package com.himoo.ydsc.activity;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.himoo.ydsc.R;
import com.himoo.ydsc.adapter.BookSourceAdapter;
import com.himoo.ydsc.base.BaseActivity;
import com.himoo.ydsc.bean.BaiduBookChapter;
import com.himoo.ydsc.bean.BookSource;
import com.himoo.ydsc.config.BookTheme;
import com.himoo.ydsc.http.BookDetailsTask;
import com.himoo.ydsc.http.HttpConstant;
import com.himoo.ydsc.reader.ReaderActivity;
import com.himoo.ydsc.ui.view.BookTitleBar;
import com.himoo.ydsc.util.MyLogger;

public class ChangeSourceActivity extends BaseActivity implements
		OnClickListener, OnItemClickListener {

	private BookTitleBar mTitleBar;
	private PullToRefreshListView ptrListView;
	private TextView tv_current_booksource;
	private TextView tv_other_booksource;
	private View booksource_line;
	private int mCurrentIndex = -1;
	private String gid;
	private int position;
	private String cid;
	private String chapterIndex;
	private String filePath;
	private String lastUrl;
	private String chapterName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_source);
		initTitleBar();
		initEvent();
	}

	@Override
	protected void initEvent() {
		// TODO Auto-generated method stub
		showRefreshDialog("正在换源中");

		Intent intent = getIntent();
		gid = intent.getStringExtra("gid");
		lastUrl = intent.getStringExtra("lastUrl");
		filePath = intent.getStringExtra("filePath");
		position = intent.getIntExtra("position", -1);
		String src = intent.getStringExtra("src");
		cid = intent.getStringExtra("cid");
		chapterIndex = intent.getStringExtra("index");
		chapterName = intent.getStringExtra("chapterName");

		String url = spiltBookSourceUrl(gid, src, cid, chapterIndex);
		getBookSourceForServe(url);

	}

	protected void initTitleBar() {

		ptrListView = (PullToRefreshListView) this
				.findViewById(R.id.book_source_list);
		tv_current_booksource = (TextView) this
				.findViewById(R.id.tv_current_booksource);
		booksource_line = (View) this.findViewById(R.id.booksource_line);
		tv_other_booksource = (TextView) this
				.findViewById(R.id.tv_other_booksource);
		ptrListView.setMode(Mode.DISABLED);
		ptrListView.setOnItemClickListener(this);
		mTitleBar = (BookTitleBar) this.findViewById(R.id.book_titleBar);
		mTitleBar.setBackgroundColor(BookTheme.THEME_COLOR);
		mTitleBar.setTitle("书籍换源");
		mTitleBar.setRightLogoGone();
		mTitleBar.setLeftDrawable(R.drawable.book_details_close);
		mTitleBar.getLeftTextView().setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		finish();
		overridePendingTransition(0, R.anim.dialog_exit_bottom);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			overridePendingTransition(0, R.anim.dialog_exit_bottom);
		}

		return true;
	}

	/**
	 * 初始化Adapter
	 * 
	 * @param list
	 */
	private void initAdapter(ArrayList<BookSource> list) {
		if (list != null && !list.isEmpty()) {
			tv_other_booksource.setVisibility(View.VISIBLE);
			booksource_line.setVisibility(View.VISIBLE);
			tv_other_booksource.setText("共搜索到" + list.size() + "个网站");
		}
		BookSourceAdapter mAdapter = new BookSourceAdapter(this,
				R.layout.adapter_booksource, list);
		ptrListView.setAdapter(mAdapter);
	}

	/**
	 * 获取来源信息
	 * 
	 * @param url
	 */
	public void getBookSourceForServe(final String chapteUrl) {

		new AsyncTask<Void, Void, String>() {

			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
			}

			@Override
			protected String doInBackground(Void... params) {
				String result = null;
				if (lastUrl != null) {
					BaiduBookChapter chapter = getNeedChangeChapter(lastUrl,
							position, chapterIndex, chapterName);
					String url = spiltBookSourceUrl(gid, chapter.getHref(),
							cid, chapterIndex);
					result = BookDetailsTask.getInstance().getStringFormServe(
							url);

				} else {
					result = BookDetailsTask.getInstance().getStringFormServe(
							chapteUrl);
				}
				return result;
			}

			@Override
			protected void onPostExecute(String result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
				if (result != null) {
					JSONObject json;
					try {
						json = new JSONObject(result);
						if (json.getInt("status") == 1) {
							String str = json.getJSONObject("data").getString(
									"domain");
							tv_current_booksource.setText("当前："
									+ (TextUtils.isEmpty(str) ? "无" : str));
							JSONArray jsonArray = json.getJSONObject("data")
									.getJSONArray("replacements");
							try {
								Gson gson = new Gson();
								ArrayList<BookSource> list = gson.fromJson(
										jsonArray.toString(),
										new TypeToken<ArrayList<BookSource>>() {
										}.getType());
								dismissRefreshDialog();
								initAdapter(list);
							} catch (Exception e) {
								dismissRefreshDialog();
							}

						} else {
							tv_current_booksource.setText("当前：无");
							dismissRefreshDialog();
						}

					} catch (JSONException e) {
						// TODO Auto-generated catch block
						tv_current_booksource.setText("当前：无");
						dismissRefreshDialog();
					}
				} else {
					dismissRefreshDialog();
				}
			}
		}.execute();

	}

	/**
	 * 拼接换源地址
	 * 
	 * @param gid
	 * @param src
	 * @param cid
	 * @return
	 */
	public String spiltBookSourceUrl(String gid, String src, String cid,
			String chapterIndex) {

		String hostUrl = "http://m.baidu.com/from=undefined/"
				+ "ssid=undefined/baiduid=EFA1528BB9AD3365F45CC64E43B7B383/bd_page_type=1/tc?"
				+ "srd=1&appui=alaxs&ajax=1&gid=" + gid
				+ "&pageType=source&src=" + src + "&cid=" + cid
				+ "&chapterIndex=" + chapterIndex
				+ "&router=replacements&time=&skey=&id=wisenovel";
		return hostUrl;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (mCurrentIndex != -1)
			return;
		mCurrentIndex = position;

		BookSource booksource = (BookSource) parent.getItemAtPosition(position);
		BaiduBookChapter chapter = new BaiduBookChapter();
		chapter.setHref(booksource.getUrl());
		chapter.setCid(cid);
		chapter.setIndex(chapterIndex);
		ChangeSourceAsyncTask task = new ChangeSourceAsyncTask(booksource,
				getChapterUrl(chapter), filePath != null ? true : false);
		task.execute();

	}

	/**
	 * 异步加载换源章节线程
	 * 
	 */
	public class ChangeSourceAsyncTask extends AsyncTask<Void, Void, String> {

		public String chapterUrl;
		public BookSource booksource;
		public boolean isNeedToSdCard;

		public ChangeSourceAsyncTask(BookSource booksource, String chapterUrl,
				boolean isNeedToSdCard) {
			this.chapterUrl = chapterUrl;
			this.booksource = booksource;
			this.isNeedToSdCard = isNeedToSdCard;

		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			showRefreshDialog("正在加载中");
		}

		@Override
		protected String doInBackground(Void... params) {

			String chapterContent = BookDetailsTask.getInstance()
					.getChapterFormService(ChangeSourceActivity.this,
							chapterUrl);
			if (isNeedToSdCard) {
				File file = new File(filePath);
				if (file != null && file.exists()
						&& !TextUtils.isEmpty(chapterContent)) {
					// 重新写到文件中
					BufferedWriter bw = null;
					try {
						bw = new BufferedWriter(new FileWriter(file));
						bw.write(chapterContent);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						MyLogger.kLog().e(e);

					} finally {
						if (bw != null)
							try {
								bw.close();
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
					}
				}
			}
			return chapterContent;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			dismissRefreshDialog();

			Intent intent = new Intent(ChangeSourceActivity.this,
					ReaderActivity.class);
			intent.putExtra("chapter", result);
			intent.putExtra("position", position);
			intent.putExtra("chapterName", booksource.getChapter_title());
			startActivity(intent);
		}

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mCurrentIndex = -1;
	}

	/**
	 * 拼接百度=书籍每章的地址
	 * 
	 * @param chapter
	 */
	public String getChapterUrl(BaiduBookChapter chapter) {
		StringBuilder sb = new StringBuilder();
		sb.append(HttpConstant.BAIDU_CHAPTER_URL).append("src=")
				.append(chapter.getHref()).append("&cid=")
				.append(chapter.getCid()).append("&chapterIndex=")
				.append(chapter.getIndex()).append("&time=&skey=&id=wisenovel");

		return sb.toString();

	}

	/**
	 * 获取章节
	 * 
	 * @param lastUrl
	 * @param position
	 * @param index
	 * @return
	 */
	public BaiduBookChapter getNeedChangeChapter(String lastUrl, int position,
			String index, String chapterName) {
		try {

			String content = BookDetailsTask.getInstance()
					.geLasttChapterFormService(this, lastUrl);
			if (content != null) {
				ArrayList<BaiduBookChapter> list = praseBaiduBookChapter(content);
				if (list != null && !list.isEmpty()) {
					// updateNewChapter(bookName, list);
					int newChapterSize = list.size();
					if (position >= newChapterSize
							|| !list.get(position).getText()
									.equals(chapterName))
						return getChapter(list, chapterName);
					else
						return list.get(position);
				}
			}
		} catch (Exception e) {
			Log.e(e);
		}
		return null;
	}

	/**
	 * 解析百度章节信息
	 * 
	 * @param jsonString
	 * @return
	 */
	private ArrayList<BaiduBookChapter> praseBaiduBookChapter(String jsonString) {
		try {
			Gson gson = new Gson();
			JSONObject jsonObject = new JSONObject(jsonString);
			if (jsonObject.getInt("status") == 1) {
				JSONObject subJsonObject = jsonObject.getJSONObject("data");
				String json = subJsonObject.getString("group");
				return gson.fromJson(json,
						new TypeToken<ArrayList<BaiduBookChapter>>() {
						}.getType());

			}
		} catch (Exception e) {
			// TODO: handle exception
			return null;
		}
		return null;

	}

	/**
	 * 处理本地的章节数大于后台章节数
	 * 
	 * @param list
	 * @param chapterName
	 * @return
	 */
	private BaiduBookChapter getChapter(ArrayList<BaiduBookChapter> list,
			String chapterName) {
		BaiduBookChapter chapter = null;
		for (int i = 0; i < list.size(); i++) {
			if (chapterName.equals(list.get(i).getText())){
				chapter = list.get(i);
				break;
			}
		}
		return chapter;
	}

}
