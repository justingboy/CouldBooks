package com.himoo.ydsc.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.himoo.ydsc.R;
import com.himoo.ydsc.base.BaseApplication;
import com.himoo.ydsc.bean.BaiduBook;
import com.himoo.ydsc.bean.BaiduBookChapter;
import com.himoo.ydsc.config.BookTheme;
import com.himoo.ydsc.download.BaiduBookDownload;
import com.himoo.ydsc.http.BookDetailsTask;
import com.himoo.ydsc.http.HttpConstant;
import com.himoo.ydsc.listener.OnParseChapterListener;
import com.himoo.ydsc.notification.DownlaodNotification;
import com.himoo.ydsc.reader.ReaderActivity;
import com.himoo.ydsc.reader.utils.IOHelper;
import com.himoo.ydsc.share.UmengShare;
import com.himoo.ydsc.ui.swipebacklayout.SwipeBackActivity;
import com.himoo.ydsc.ui.utils.Toast;
import com.himoo.ydsc.ui.utils.UIHelper;
import com.himoo.ydsc.ui.utils.ViewSelector;
import com.himoo.ydsc.util.FileUtils;
import com.himoo.ydsc.util.RegularUtil;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.socialize.bean.SocializeConfig;
import com.umeng.socialize.sso.UMSsoHandler;

public class BaiduDetailsActivity extends SwipeBackActivity implements
		OnScrollListener, OnParseChapterListener, OnClickListener,
		OnItemClickListener {
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

	private ArrayList<BaiduBookChapter> bookList = new ArrayList<BaiduBookChapter>();

	private String chapters[];

	private ArrayAdapter<String> adapter;

	private Button bookDownload;

	private Button bookEvaluation;

	private DownlaodNotification downNotification;

	/** 当前点击的Item位置 */
	private int mCurrentClickPosition = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_baidu_book_details);
		BookTheme.setChangeTheme(false);
		if (BookTheme.isContainBookName(book.getTitle()))
			option = BaseApplication.getInstance().displayImageOptionsBuider(
					BookTheme.BOOK_COVER);
		initListView();
		initListener();
		// new ChapterAsyTask(book.getListurl()).execute();
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
						book.getCoverImage(), book.getTitle(),
						book.getListurl());
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
		ViewSelector.setViewBackGround(floowView);
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
		Log.i("CoverImage1 = " + book.getCoverImage());
		String imageUrl = RegularUtil.converUrl(book.getCoverImage());
		bookCoverImg.setImageResource(BookTheme.BOOK_COVER);
		if (TextUtils.isEmpty(imageUrl)
				|| BookTheme.isContainBookName(book.getTitle())) {
			bookCoverImg.setImageResource(BookTheme.BOOK_COVER);
		} else {

			ImageLoader.getInstance().displayImage(imageUrl, bookCoverImg,
					option);
			Log.i("CoverImage2－－option = " + imageUrl);
		}
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
	public void initBookChapter(String gid) {
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
		bookList.clear();
		bookList.addAll(list);
		ViewSelector.setViewBackGround(chapter_count);
		chapter_count.setText("共有" + bookList.size() + "个章节");
		floowView.setText("共有" + bookList.size() + "个章节");
		chapters = new String[bookList.size()];
		for (int i = 0; i < bookList.size(); i++) {
			chapters[i] = bookList.get(i).getText().trim();
		}
		adapter = new ArrayAdapter<String>(this,
				R.layout.android_adapter_textview, chapters);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);
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
		IOHelper.getBook(this, book.getTitle(), list);
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
				chapters[i] = bookList.get(i).getText().trim();
			}

			adapter.notifyDataSetChanged();
			// 下载书籍
		} else if (v == bookDownload) {
			downNotification = new DownlaodNotification(this);
			try {
				BaiduBookDownload.getInstance(this).addBaiduBookDownload(book);
			} catch (DbException e) {
				// TODO Auto-generated catch block
				Log.e("插入数据库失败" + e.getMessage());
			}
			new SaveAsyncTask(bookList).execute();

			// 豆瓣评书
		} else if (v == bookEvaluation) {
			UIHelper.startToActivity(this, DoubanBookActivity.class,
					book.getTitle());
			bookEvaluation.setClickable(false);

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

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mCurrentClickPosition = -1;
		bookEvaluation.setClickable(true);
	}

	/**
	 * 异步任务 缓存小说的所有章节
	 * 
	 * 
	 */
	class SaveAsyncTask extends AsyncTask<Void, String, String> {
		private File dirFile;
		private ArrayList<BaiduBookChapter> list = null;

		public SaveAsyncTask(ArrayList<BaiduBookChapter> list) {

			String index = list.get(0).getIndex();
			if (Integer.valueOf(index) > 1) {
				Collections.reverse(list);
			}
			this.list = list;

		}

		@Override
		protected void onPreExecute() {
			dirFile = new File(FileUtils.mSdRootPath + "/CouldBook/baidu"
					+ File.separator + book.getTitle() + File.separator);
			if (!dirFile.exists())
				dirFile.mkdirs();
		}

		@Override
		protected String doInBackground(Void... params) {
			// TODO Auto-generated method stub
			int len = 0;
			int progress = 0;
			int allChapterLength = list.size();
			float partProgress = (float) allChapterLength / 100;
			String bookName = book.getTitle();
			for (int i = 0; i < allChapterLength; i++) {
				BaiduBookChapter chapter = list.get(i);
				String url = getChapterUrl(chapter);
				String chapterName = chapter.getText().trim() + "-"
						+ chapter.getIndex() + "-" + i + ".txt";
				File chapterFile = new File(dirFile.getAbsolutePath(),
						chapterName);
				// 如何该章节已经下载则不需要下载,跳过,下载下一个章节
				if (!chapterFile.exists() || chapterFile.length() == 0)
					com.himoo.ydsc.download.FileUtils.writeTosSd(url,
							chapterFile);
				len++;
				if (partProgress <= 1) {
					progress = getPartprogress(partProgress);
					progress *= len;
					if (downNotification != null) {
						downNotification.creatNotification(bookName, "开始下载");
						downNotification.setProgressAndNetSpeed(progress,
								downNotification.getNetSpeed());
					}

				} else {
					if (len % (int) partProgress == 0) {
						progress++;
						if (progress <= 99) {
							if (progress % 2 == 0) {
								if (downNotification != null) {
									downNotification.creatNotification(
											bookName, "开始下载");
									downNotification.setProgressAndNetSpeed(
											progress,
											downNotification.getNetSpeed());
								}
							}

						}
					}
				}

				if (allChapterLength - len == 0) {
					if (downNotification != null) {
						downNotification.creatNotification(bookName, "下载完成");
						downNotification.setProgressAndNetSpeed(100,
								downNotification.getNetSpeed());
					}
				}

			}

			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			Toast.showLong(BaiduDetailsActivity.this, "《" + book.getTitle()
					+ "》下载完成");
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					downNotification.notifiManger.cancelAll();
				}
			}, 3000);

		}

	}

	/**
	 * 获取下载几章为进度的1%
	 * 
	 * @param part
	 * @return
	 */
	private int getPartprogress(float part) {
		int partProgress = 0;
		if (1 == (int) part)
			partProgress = 1;
		String num = String.valueOf(part);
		int secondNum = Character.getNumericValue(num.charAt(2));
		switch (secondNum) {
		case 1:
			partProgress = 10;
			break;
		case 2:
			partProgress = 5;
			break;
		case 3:
			partProgress = 3;
			break;
		case 4:
			partProgress = 2;
			break;
		case 5:
			partProgress = 2;
			break;
		default:
			partProgress = 1;
			break;
		}

		return partProgress;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		if (mCurrentClickPosition != -1)
			return;
		mCurrentClickPosition = position;

		int size = bookList.size();
		BaiduBookChapter chapter = bookList.get(position - 2);
		Intent intent = new Intent(this, ReaderActivity.class);
		intent.putExtra("bookName", book.getTitle());
		intent.putExtra("chapterName", chapter.getText().trim());
		intent.putExtra("chapterUrl", getChapterUrl(chapter));
		intent.putExtra("index", chapter.getIndex());
		intent.putExtra("position", isReverse ? size - position + 1
				: position - 2);
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

}
