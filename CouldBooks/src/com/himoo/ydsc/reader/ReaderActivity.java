package com.himoo.ydsc.reader;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.himoo.ydsc.R;
import com.himoo.ydsc.animation.AnimationUtils;
import com.himoo.ydsc.base.BaseReaderActivity;
import com.himoo.ydsc.config.BookTheme;
import com.himoo.ydsc.config.SpConstant;
import com.himoo.ydsc.fragment.reader.BookSettingFragment1.OnFragment1Listener;
import com.himoo.ydsc.fragment.reader.BookSettingFragment2.OnFragment2Listener;
import com.himoo.ydsc.fragment.reader.BookSettingFragmentAdapter;
import com.himoo.ydsc.reader.bean.Chapter;
import com.himoo.ydsc.reader.dao.BookMark;
import com.himoo.ydsc.reader.dao.BookMarkDb;
import com.himoo.ydsc.reader.utils.BookPage;
import com.himoo.ydsc.reader.utils.IOHelper;
import com.himoo.ydsc.reader.view.PageWidget;
import com.himoo.ydsc.speech.SpeechReader;
import com.himoo.ydsc.ui.utils.Toast;
import com.himoo.ydsc.util.DeviceUtil;
import com.himoo.ydsc.util.MyLogger;
import com.himoo.ydsc.util.SharedPreferences;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.viewpagerindicator.CirclePageIndicator;

/**
 * 阅读小说的界面
 */
public class ReaderActivity extends BaseReaderActivity implements
		OnClickListener, OnTouchListener, OnFragment1Listener,
		OnFragment2Listener {

	private PageWidget pageWidget;
	private Bitmap curBitmap, nextBitmap;
	private Canvas curCanvas, nextCanvas;
	public BookPage bookpage;

	public Chapter chapter;
	boolean isMove = false;
	private int screenWith;
	private int screenHeight;

	@ViewInject(R.id.booksetting_titlebar)
	private LinearLayout booksetting_titlebar;

	@ViewInject(R.id.bookseeting_bottombar)
	private LinearLayout bookseeting_bottombar;

	@ViewInject(R.id.booksetting_back)
	private ImageView booksetting_back;

	@ViewInject(R.id.booksetting_speech)
	private ImageView booksetting_speech;

	@ViewInject(R.id.booksetting_bookmark)
	private ImageView booksetting_bookmark;

	private boolean isUp = false;
	private View view;
	private int topHight;
	private int bottomHight;
	/** 用于判断是否翻页了 */
	private boolean isFilp = false;
	private String index;
	private int currentPage, pageCount;
	private boolean isNeedSaveProgress = false;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setNetWorkOnMainMode();
		initChapter();
		initReaderBook();
		view = LayoutInflater.from(this)
				.inflate(R.layout.activity_reader, null);
		LinearLayout layout = (LinearLayout) view
				.findViewById(R.id.pagewidget_layout);
		layout.addView(pageWidget);
		setContentView(view);
		ViewUtils.inject(this);
		SpeechReader.getInstance().initSpeech(this);
		setListener();
		initAnimation();
		pageWidget.setBitmaps(curBitmap, curBitmap);
		pageWidget.setOnTouchListener(this);

		if (!SharedPreferences.getInstance().getBoolean(
				SpConstant.BOOK_SETTING_LIGHT_SYSTEM, false)) {
			// 设置屏幕亮度
			// 获取当前亮度的位置
			int brigHtness = SharedPreferences.getInstance().getInt(
					SpConstant.BOOK_SETTING_LIGHT, 10);
			setBrightness(this, brigHtness);
		}

	}

	public void setListener() {
		booksetting_back.setOnClickListener(this);
		booksetting_speech.setOnClickListener(this);
		booksetting_bookmark.setOnClickListener(this);
	}

	/**
	 * 初始化
	 */
	private void initReaderBook() {
		screenWith = DeviceUtil.getWidth(this);
		screenHeight = DeviceUtil.getHeight(this);
		curBitmap = Bitmap.createBitmap(screenWith, screenHeight,
				Bitmap.Config.ARGB_8888);
		nextBitmap = Bitmap.createBitmap(screenWith, screenHeight,
				Bitmap.Config.ARGB_8888);
		curCanvas = new Canvas(curBitmap);
		nextCanvas = new Canvas(nextBitmap);
		bookpage = new BookPage(this, screenWith, screenHeight, chapter,
				currentPage, pageCount);
		bookpage.setBgBitmap(this, BookTheme.READBOOK_BACKGROUND);
		bookpage.draw(this, curCanvas);
		pageWidget = new PageWidget(this, screenWith, screenHeight);

	}

	@Override
	public boolean onTouch(View v, MotionEvent e) {
		boolean ret = false;
		if (v == pageWidget) {
			if (e.getAction() == MotionEvent.ACTION_DOWN) {
				if (isClickable(e)) {
					startAnimation();
					return false;
				} else {
					if (isUp) {
						startAnimation();
						isUp = false;
					}

				}
				pageWidget.abortAnimation();
				pageWidget.calcCornerXY(e.getX(), e.getY());
				bookpage.draw(ReaderActivity.this, curCanvas);
				if (pageWidget.DragToRight()) {
					if (bookpage.prePage()) {
						bookpage.draw(ReaderActivity.this, nextCanvas);
						isFilp = true;
					} else {
						Toast.showShort(ReaderActivity.this, "已经是第一页了");
						return false;
					}
				} else {
					if (bookpage.nextPage()) {
						MyLogger.kLog().d("执行翻页");
						bookpage.draw(ReaderActivity.this, nextCanvas);
						isFilp = true;
					} else {
						Toast.showShort(ReaderActivity.this, "已经是最后一页了");
						return false;
					}
				}
				pageWidget.setBitmaps(curBitmap, nextBitmap);
			}
			ret = pageWidget.doTouchEvent(e);
			MyLogger.kLog().d("doTouchEvent = " + ret);
			return ret;
		}
		return false;
	}

	/**
	 * 初始化章节
	 */
	private void initChapter() {
		Intent intent = getIntent();
		index = intent.getStringExtra("index");
		int position = intent.getIntExtra("position", 0);
		currentPage = intent.getIntExtra("currentPage", -1);
		pageCount = intent.getIntExtra("pageCount", -1);
		isNeedSaveProgress = intent.getBooleanExtra("isNeedSave", false);
		chapter = IOHelper.getChapter(index, position);
	}

	/**
	 * 判断是否可以被点击
	 * 
	 * @param event
	 * @return
	 */
	private boolean isClickable(MotionEvent event) {
		int disWith = (int) Math.abs(event.getX() - screenWith);
		int disHeight = (int) Math.abs(event.getY() - screenHeight);
		if (screenWith / 3 < disWith && disWith < 2 * screenWith / 3
				&& screenHeight / 3 < disHeight
				&& disHeight < 2 * screenHeight / 3) {
			return true;
		}
		return false;

	}

	/**
	 * 设置可以在主线程中进行联网操作
	 */
	private void setNetWorkOnMainMode() {
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);
	}

	/**
	 * 初始化顶部和底部的TitleBar动画
	 */
	private void initAnimation() {
		topHight = DeviceUtil.dip2px(this, 50);
		bottomHight = DeviceUtil.dip2px(this, 260);
		LayoutParams parmas = (LayoutParams) bookseeting_bottombar
				.getLayoutParams();
		parmas.height = bottomHight;
		LayoutParams parmas2 = (LayoutParams) booksetting_titlebar
				.getLayoutParams();
		parmas2.height = topHight;
		bookseeting_bottombar.setLayoutParams(parmas);
		booksetting_titlebar.setLayoutParams(parmas2);
		AnimationUtils.setViewTranslateDownY(bookseeting_bottombar, 0f,
				bottomHight);
		AnimationUtils
				.setViewTranslateDownY(booksetting_titlebar, 0, -topHight);
		initFragmentAdapter();
	}

	/**
	 * 每次都初始化Adapter
	 */
	private void initFragmentAdapter() {

		BookSettingFragmentAdapter mAdapter = new BookSettingFragmentAdapter(
				getSupportFragmentManager(), this,chapter.getBookName(), IOHelper.getChapterLength());

		ViewPager mPager = (ViewPager) findViewById(R.id.pager);
		mPager.setAdapter(mAdapter);

		CirclePageIndicator mIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
		mIndicator.setViewPager(mPager);

	}

	/**
	 * 执行出现顶部和底部的TitleBar动画
	 */
	private void startAnimation() {
		if (!isUp) {
			AnimationUtils.setViewTranslateUpY(booksetting_titlebar, -topHight,
					0f);
			AnimationUtils.setViewTranslateUpY(bookseeting_bottombar,
					bottomHight, 0f);
		} else {
			AnimationUtils.setViewTranslateDownY(bookseeting_bottombar, 0f,
					bottomHight);
			AnimationUtils.setViewTranslateDownY(booksetting_titlebar, 0f,
					-topHight);
		}
		isUp = !isUp;
	}

	@Override
	public void onTextSizChange(int textSize) {
		// TODO Auto-generated method stub
		SharedPreferences.getInstance().putInt(
				SpConstant.BOOK_SETTING_TEXT_SIZE, textSize);
		bookpage.setTextSzie(textSize);
		bookpage.draw(this, isFilp ? nextCanvas : curCanvas);
		pageWidget.invalidate();
	}

	@Override
	public void onPreChapter() {
		// TODO Auto-generated method stub

		bookpage.initPreChapter();
		bookpage.draw(this, isFilp ? nextCanvas : curCanvas);
		pageWidget.invalidate();
	}

	@Override
	public void onNextChapter() {
		// TODO Auto-generated method stub
		bookpage.initNextChapter();
		bookpage.draw(this, isFilp ? nextCanvas : curCanvas);
		pageWidget.invalidate();
	}

	@Override
	public void onSeekBarChapter(int chapterIndex) {
		// TODO Auto-generated method stub
		bookpage.initSeekBarChapter(chapterIndex);
		bookpage.draw(this, isFilp ? nextCanvas : curCanvas);
		pageWidget.invalidate();

	}

	@Override
	public void onTextBackgroundChange() {
		// TODO Auto-generated method stub
		bookpage.setBgBitmap(this, BookTheme.READBOOK_BACKGROUND);
		bookpage.draw(this, isFilp ? nextCanvas : curCanvas);
		pageWidget.invalidate();

	}

	@Override
	public void onTextTypeChange() {
		// TODO Auto-generated method stub
		bookpage.setTextType();
		bookpage.draw(this, isFilp ? nextCanvas : curCanvas);
		pageWidget.invalidate();

	}

	@Override
	public void onTextTypeChildrenChange() {
		// TODO Auto-generated method stub

		bookpage.setTextTypeChildren(false);
		bookpage.draw(this, isFilp ? nextCanvas : curCanvas);
		pageWidget.invalidate();

	}

	@Override
	public void onTextLineSpaceChange() {
		// TODO Auto-generated method stub
		bookpage.setTextLineSpace(false);
		bookpage.draw(this, isFilp ? nextCanvas : curCanvas);
		pageWidget.invalidate();
	}

	/**
	 * 设置亮度
	 * 
	 * @param activity
	 * @param brightness
	 */
	public static void setBrightness(Activity activity, int brightness) {
		WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
		lp.screenBrightness = Float.valueOf(brightness) * (1f / 255f);
		activity.getWindow().setAttributes(lp);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.booksetting_back:
			finish();
			break;
		case R.id.booksetting_speech:
			SpeechReader.getInstance().speechChapter(chapter.getContent());
			break;
		case R.id.booksetting_bookmark:
			new SaveMarkAsyncTask().execute();
			break;

		default:
			break;
		}
	}

	/**
	 * 保存阅读进度
	 */
	private void saveBookReaderProgress() {
		if (isNeedSaveProgress) {
			BookMarkDb db = BookMarkDb.getInstance(this, "book");
			BookMark mark = new BookMark();
			mark.setBookName(bookpage.chapter.getBookName());
			mark.setChapterName(bookpage.chapter.getChapterName());
			mark.setPosition(bookpage.chapter.getPosition());
			mark.setCurrentPage(bookpage.pageNum - 1);
			mark.setPageCount(bookpage.pagesVe.size());
			mark.setType(1);
			db.saveReaderPosition(mark);
		}

	}

	/**
	 * 保存书签
	 */
	private void saveBookReaderMark() {
		BookMarkDb db = BookMarkDb.getInstance(this, "book");
		BookMark mark = new BookMark();
		mark.setBookName(bookpage.chapter.getBookName());
		mark.setChapterName(bookpage.chapter.getChapterName());
		mark.setPosition(bookpage.chapter.getPosition());
		mark.setCurrentPage(bookpage.pageNum - 1);
		mark.setPageCount(bookpage.pagesVe.size());
		mark.setType(2);
		db.saveReaderMark(mark);

	}

	/**
	 * 异步添加书签
	 * 
	 */
	private class SaveMarkAsyncTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			showRefreshDialog("添加书签成功");
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			saveBookReaderMark();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			dismissRefreshDialog();
		}

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		saveBookReaderProgress();
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		SpeechReader.getInstance().clear();
	}

}
