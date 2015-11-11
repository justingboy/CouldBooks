package com.himoo.ydsc.activity;

import java.util.ArrayList;
import java.util.Collections;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.himoo.ydsc.R;
import com.himoo.ydsc.base.BaseApplication;
import com.himoo.ydsc.bean.BaiduBook;
import com.himoo.ydsc.bean.BaiduBookChapter;
import com.himoo.ydsc.http.BookDetailsTask;
import com.himoo.ydsc.listener.OnParseChapterListener;
import com.himoo.ydsc.share.UmengShare;
import com.himoo.ydsc.ui.swipebacklayout.SwipeBackActivity;
import com.himoo.ydsc.ui.utils.Toast;
import com.himoo.ydsc.ui.utils.UIHelper;
import com.himoo.ydsc.ui.utils.ViewSelector;
import com.himoo.ydsc.util.RegularUtil;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.socialize.bean.SocializeConfig;
import com.umeng.socialize.sso.UMSsoHandler;

public class BaiduDetailsActivity extends SwipeBackActivity implements
		OnScrollListener, OnParseChapterListener, OnClickListener {
	/** 下载图片的配置参数 */
	private DisplayImageOptions option;

	@ViewInject(R.id.book_chapter_listview)
	private ListView listView;

	@ViewInject(R.id.book_chapter_count)
	private TextView chapter_count;

	private BaiduBook book;

	/** 悬浮的TextView */
	private TextView floowView;

	/** 用于判断是否是倒序 */
	private boolean isReverse = false;

	private ArrayList<BaiduBookChapter> bookList;

	private String chapters[];

	private ArrayAdapter<String> adapter;

	private Button bookDownload;

	private Button bookEvaluation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_baidu_book_details);
		initListView();
		option = BaseApplication.getInstance().displayImageOptionsBuider(
				R.drawable.book_face_default);
		initListener();
		initBookChapter(book.getGid());
	}

	@Override
	protected void initTitleBar() {
		book = (BaiduBook) getIntent().getExtras().getParcelable("book");
		mTitleBar.setLeftDrawable(R.drawable.book_details_close);
		mTitleBar.setTitle(book.getTitle());
		mTitleBar.setRightLogoDrawable(R.drawable.book_share);
		mTitleBar.getRightLogo().setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				UmengShare.getInstance().setShareContent(
						BaiduDetailsActivity.this, book.getTitle(),
						book.getCoverImage(), book.getSummary());
				// 注册友盟分享
				UmengShare.getInstance().addCustomPlatforms(
						BaiduDetailsActivity.this);
			}
		});
	}

	/**
	 * 初始化事件
	 */
	private void initListener() {
		chapter_count.setOnClickListener(this);
		bookDownload.setOnClickListener(this);
		bookEvaluation.setOnClickListener(this);
		floowView.setOnClickListener(this);
		listView.setOnScrollListener(this);
	}

	/**
	 * 初始化ListView
	 * 
	 */
	private void initListView() { // 头部内容
		View headerView = View.inflate(this, R.layout.activity_header_view,
				null);
		floowView = (TextView) View.inflate(this, R.layout.activity_floow_view,
				null);
		ImageView bookCoverImg = (ImageView) headerView
				.findViewById(R.id.baidu_book_image);
		TextView bookAuthor = (TextView) headerView
				.findViewById(R.id.baidu_book_author);
		TextView bookStatue = (TextView) headerView
				.findViewById(R.id.baidu_book_statue);
		TextView bookCategory = (TextView) headerView
				.findViewById(R.id.baidu_book_category);
		TextView bookSummary = (TextView) headerView
				.findViewById(R.id.baidu_book_summary);
		bookDownload = (Button) headerView
				.findViewById(R.id.baidu_book_download);
		bookEvaluation = (Button) headerView
				.findViewById(R.id.baidu_book_evaluation);

		ViewSelector.setButtonSelector(this, bookDownload);
		ViewSelector.setButtonSelector(this, bookEvaluation);
		String imageUrl = RegularUtil.converUrl(book.getCoverImage());
		Log.i("CoverImage = "+imageUrl);
		ImageLoader.getInstance().displayImage(imageUrl, bookCoverImg, option);
		bookAuthor.setText("作者 ：" + book.getAuthor());
		bookStatue.setText("状态 ：" + book.getStatus());
		bookCategory.setText("类别 ：" + book.getCategory());
		if (book.getSummary().startsWith("　　")) {
			bookSummary.setText(book.getSummary().trim());
		} else
			bookSummary.setText("　　" + book.getSummary().trim());

		listView.addHeaderView(headerView);
		listView.addHeaderView(floowView);

	}

	/**
	 * 联网获取章节
	 * 
	 * @param gid
	 */
	private void initBookChapter(String gid) {
		showRefreshDialog("正在加载中");
		BookDetailsTask.getInstance().executeBaidu(this, gid);
		BookDetailsTask.getInstance().setOnParseChapterListener(this);

	}

	/**
	 * 对章节数据进行初始化
	 * 
	 * @param list
	 */
	private void initData(ArrayList<BaiduBookChapter> list) {
		bookList = list;
		chapter_count.setText("共有" + bookList.size() + "个章节");
		floowView.setText("共有" + bookList.size() + "个章节");
		chapters = new String[bookList.size()];
		for (int i = 0; i < bookList.size(); i++) {
			chapters[i] = bookList.get(i).getText().toString().trim();
		}
		adapter = new ArrayAdapter<String>(this,
				R.layout.android_adapter_textview, chapters);
		listView.setAdapter(adapter);
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub
		if (firstVisibleItem >= 1) {
			chapter_count.setVisibility(View.VISIBLE);

		} else {

			chapter_count.setVisibility(View.GONE);
		}
	}

	@Override
	public void onParseSuccess(ArrayList<BaiduBookChapter> list) {
		// TODO Auto-generated method stub
		dismissRefreshDialog();
		initData(list);
	}

	@Override
	public void onParseFailure(Exception ex, String msg) {
		// TODO Auto-generated method stub
		dismissRefreshDialog();
		Toast.showLong(this, "解析章节错误：" + msg);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		if (v == chapter_count || v == floowView) {

			Drawable drawable = null;
			isReverse = !isReverse;
			if (!isReverse) {
				drawable = getResources()
						.getDrawable(R.drawable.menu_book_down);

			} else {
				drawable = getResources().getDrawable(R.drawable.menu_book_up);
			}
			chapter_count.setCompoundDrawablesWithIntrinsicBounds(null, null,
					drawable, null);
			floowView.setCompoundDrawablesWithIntrinsicBounds(null, null,
					drawable, null);

			Collections.reverse(bookList);
			for (int i = 0; i < bookList.size(); i++) {
				chapters[i] = bookList.get(i).getText().toString().trim();
			}

			adapter.notifyDataSetChanged();
			// 下载书籍
		} else if (v == bookDownload) {
			// 豆瓣评书
		} else if (v == bookEvaluation) {
			UIHelper.startToActivity(this, DoubanBookActivity.class,
					book.getTitle());

		}

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		UMSsoHandler ssoHandler = SocializeConfig.getSocializeConfig()
				.getSsoHandler(requestCode);
		if (ssoHandler != null) {
			ssoHandler.authorizeCallBack(requestCode, resultCode, data);
		}
	}

}
