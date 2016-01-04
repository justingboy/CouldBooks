package com.himoo.ydsc.reader;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.himoo.ydsc.R;
import com.himoo.ydsc.animation.AnimationUtils;
import com.himoo.ydsc.base.BaseReaderActivity;
import com.himoo.ydsc.config.BookTheme;
import com.himoo.ydsc.config.SpConstant;
import com.himoo.ydsc.dialog.ColorPickerDialog;
import com.himoo.ydsc.download.BaiduBookDownload;
import com.himoo.ydsc.download.BaiduInfo;
import com.himoo.ydsc.download.BookDownloadManager;
import com.himoo.ydsc.download.BookDownloadService;
import com.himoo.ydsc.fragment.reader.BookSettingFragment1.OnFragment1Listener;
import com.himoo.ydsc.fragment.reader.BookSettingFragment2.OnFragment2Listener;
import com.himoo.ydsc.fragment.reader.BookSettingFragment3.OnFragment3Listener;
import com.himoo.ydsc.fragment.reader.BookSettingFragmentAdapter;
import com.himoo.ydsc.reader.bean.Chapter;
import com.himoo.ydsc.reader.dao.BookMark;
import com.himoo.ydsc.reader.dao.BookMarkDb;
import com.himoo.ydsc.reader.utils.BookPage;
import com.himoo.ydsc.reader.utils.IOHelper;
import com.himoo.ydsc.reader.utils.IOHelper.OnGetChapterListener;
import com.himoo.ydsc.reader.view.ColorPickerView.OnColorChangedListener;
import com.himoo.ydsc.reader.view.PageWidget;
import com.himoo.ydsc.share.UmengShare;
import com.himoo.ydsc.speech.SpeechReader;
import com.himoo.ydsc.ui.utils.Toast;
import com.himoo.ydsc.update.BookUpdateTask;
import com.himoo.ydsc.update.BookUpdateTask.OnNewChapterUpdateListener;
import com.himoo.ydsc.update.LastChapter;
import com.himoo.ydsc.util.DeviceUtil;
import com.himoo.ydsc.util.MyLogger;
import com.himoo.ydsc.util.NetWorkUtils;
import com.himoo.ydsc.util.SharedPreferences;
import com.himoo.ydsc.util.TimestampUtils;
import com.ios.dialog.AlertDialog;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.viewpagerindicator.CirclePageIndicator;

/**
 * 阅读小说的界面
 */
public class ReaderActivity extends BaseReaderActivity implements
		OnClickListener, OnTouchListener, OnFragment1Listener,
		OnFragment2Listener, OnFragment3Listener, OnNewChapterUpdateListener,
		OnColorChangedListener, OnGetChapterListener {
	private BookDownloadManager downloadManager;
	private PageWidget pageWidget;
	private Bitmap curBitmap, nextBitmap;
	private Canvas curCanvas, nextCanvas;
	public BookPage bookpage;

	public Chapter chapter;
	public boolean isMove = false;
	public boolean isFirstloading = true;

	private int screenWith;
	private int screenHeight;

	@ViewInject(R.id.booksetting_titlebar)
	private LinearLayout booksetting_titlebar;

	@ViewInject(R.id.booksetting_source)
	private ImageView booksetting_source;

	@ViewInject(R.id.booksetting_update)
	private ImageView booksetting_update;

	@ViewInject(R.id.bookseeting_bottombar)
	private LinearLayout bookseeting_bottombar;

	@ViewInject(R.id.booksetting_back)
	private ImageView booksetting_back;

	@ViewInject(R.id.booksetting_speech)
	private ImageView booksetting_speech;

	@ViewInject(R.id.booksetting_bookmark)
	private ImageView booksetting_bookmark;

	@ViewInject(R.id.layout_mogo)
	private LinearLayout layout_mogo;

	private final static String[] title = { "左手", "仿真", "无动画", "上下", "连动" };
	private boolean isUp = false;
	private boolean isSpeech = false;
	private View view;
	private int topHight;
	private int bottomHight;
	/** 用于判断是否翻页了 */
	private boolean isFilp = false;
	private String index;
	private int currentPage, pageCount;
	private boolean isNeedSaveProgress = false;
	/** 该type表示是从哪个界面跳转的 */
	private int jumpType;
	/** 书的状态，连载还是完结 */
	private String statue;
	/** 2是百度的书还 1是自己的书 */
	private int bookType;
	/** 跳转目录中下载需要的Gid */
	private String gid;
	private String lastUrl;
	/** 阅读时间　 */
	private long readerTime = 0l;
	/** 判断是否是左手模式 */
	private boolean isLeftHanderMode = false;
	/** 夜间模式 */
	private boolean isMnightMode = false;
	private LinearLayout layout;
	private int position;
	/** 表示向后滑还是向前滑 */
	private boolean isToNextPage = true;
	/** 是否开启了在主线程中加载章节内容 */
	private boolean isAutoLoad;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setNetWorkOnMainMode();
		downloadManager = BookDownloadService.getDownloadManager(this);
		initIntent();
		view = LayoutInflater.from(this)
				.inflate(R.layout.activity_reader, null);
		layout = (LinearLayout) view.findViewById(R.id.pagewidget_layout);
		setContentView(view);
		ViewUtils.inject(this);
		SpeechReader.getInstance().initSpeech(this);
		setListener();
		setTitleBarNight();
		// if (type != 1) {
		initChapter();
		if (isAutoLoad && jumpType == 1) {
			initReaderBook(2);
		} else {
			initReaderBook(jumpType);
		}
		layout.addView(pageWidget);
		initAnimation();
		pageWidget.setBitmaps(curBitmap, curBitmap);
		pageWidget.setOnTouchListener(this);
		// }
		// 默认是更随系统亮度的
		if (!SharedPreferences.getInstance().getBoolean(
				SpConstant.BOOK_SETTING_LIGHT_SYSTEM, true)) {
			// 设置屏幕亮度
			// 获取当前亮度的位置
			int brigHtness = SharedPreferences.getInstance().getInt(
					SpConstant.BOOK_SETTING_LIGHT, 10);
			setBrightness(this, brigHtness);
		}
		setMogoAdVisible();
		initChapter();
	}

	public void setListener() {
		IOHelper.setOnChapterListener(this);
		booksetting_back.setOnClickListener(this);
		booksetting_speech.setOnClickListener(this);
		booksetting_bookmark.setOnClickListener(this);
		booksetting_source.setOnClickListener(this);
		booksetting_update.setOnClickListener(this);
	}

	/**
	 * 初始化
	 */
	private void initReaderBook(int jumpType) {
		screenWith = DeviceUtil.getWidth(this);
		int type = SharedPreferences.getInstance()
				.getInt("book_update_type", 2);
		if (type == 1) {
			if (NetWorkUtils.isNetConnected(this)) {
				screenHeight = (int) (DeviceUtil.getHeight(this) - getResources()
						.getDimension(R.dimen.mogoAd_height));
			} else {
				screenHeight = DeviceUtil.getHeight(this);
			}

		} else {
			screenHeight = DeviceUtil.getHeight(this);
		}
		curBitmap = Bitmap.createBitmap(screenWith, screenHeight,
				Bitmap.Config.ARGB_8888);
		nextBitmap = Bitmap.createBitmap(screenWith, screenHeight,
				Bitmap.Config.ARGB_8888);
		curCanvas = new Canvas(curBitmap);
		nextCanvas = new Canvas(nextBitmap);
		if (jumpType != 1 || (jumpType == 1 && isAutoLoad)) {
			bookpage = new BookPage(this, screenWith, screenHeight, chapter,
					currentPage, pageCount, bookType);
		} else {
			bookpage = new BookPage(this, screenWith, screenHeight);

		}
		String mode = SharedPreferences.getInstance().getString(
				SpConstant.BOOK_TURNPAGE_TITLE_TYPE, title[1]);
		if (mode.equals(title[0])) {
			isLeftHanderMode = true;
		}

		boolean isNightMode = SharedPreferences.getInstance().getBoolean(
				SpConstant.BOOK_SETTING_NIGHT_MODE, false);
		boolean isAutoNightMode = SharedPreferences.getInstance().getBoolean(
				SpConstant.BOOK_SETTING_AUTO_NIGHT, false);
		bookpage.setBgBitmap(this,
				isNightMode ? BookTheme.BOOK_SETTING_NIGHT_DRAWABLE
						: BookTheme.READBOOK_BACKGROUND);
		if (isAutoNightMode) {
			boolean isChangeToNightMode = false;
			int hour = TimestampUtils.getCurrentHour();
			if (hour >= 19 || hour <= 7) {
				isChangeToNightMode = true;
				bookpage.setBgBitmap(this,
						BookTheme.BOOK_SETTING_NIGHT_DRAWABLE);
			}
			SharedPreferences.getInstance().putBoolean(
					SpConstant.BOOK_SETTING_AUTO_NIGHT_MODE_YES,
					isChangeToNightMode);
		}
		if (jumpType != 1 || (jumpType == 1 && isAutoLoad)) {
			bookpage.draw(this, curCanvas);
		} else {
			bookpage.draw(curCanvas);
		}
		pageWidget = new PageWidget(this, screenWith, screenHeight);
		pageWidget.initNightMode();

	}

	/**
	 * 设置广告是否展示
	 */
	private void setMogoAdVisible() {
		int type = SharedPreferences.getInstance()
				.getInt("book_update_type", 2);
		if (type == 2 || !NetWorkUtils.isNetConnected(this)) {
			layout_mogo.removeAllViews();
			layout_mogo.setVisibility(View.GONE);
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent e) {
		boolean ret = false;
		if (v == pageWidget) {
			if (e.getAction() == MotionEvent.ACTION_DOWN) {
				if (isClickable(e)) {
					startAnimation(250);
					return false;
				} else {
					if (isUp) {
						startAnimation(250);
						isUp = false;
					}

				}
				pageWidget.abortAnimation();
				pageWidget.calcCornerXY(e.getX(), e.getY());
				bookpage.draw(ReaderActivity.this, curCanvas);
				if (!isLeftHanderMode) {
					if (pageWidget.DragToRight()) {

						if (jumpType == 1 && !isAutoLoad) {
							if (bookpage.isFirstPage()) {
								isToNextPage = false;
								IOHelper.getChapter(1, chapter.getIndex(),
										chapter.getPosition() - 1, bookType);
								return false;
							}
						}
						if (bookpage.prePage()) {
							bookpage.draw(ReaderActivity.this, nextCanvas);
							isFilp = true;
						} else {
							Toast.showShort(ReaderActivity.this, "已经是第一页了");
							return false;
						}
					} else {
						// 表示从百度详情界面跳转过来
						if (jumpType == 1 && !isAutoLoad) {
							if (bookpage.isLastPage()) {
								isToNextPage = true;
								IOHelper.getChapter(1, chapter.getIndex(),
										chapter.getPosition() + 1, bookType);
								return false;
							}
						}
						if (bookpage.nextPage()) {
							MyLogger.kLog().d("执行翻页");
							bookpage.draw(ReaderActivity.this, nextCanvas);
							isFilp = true;
						} else {
							Toast.showShort(ReaderActivity.this, "已经是最后一页了");
							return false;
						}
					}
				} else {
					if (!pageWidget.DragToRight()) {
						if (jumpType == 1 && !isAutoLoad) {
							if (bookpage.isFirstPage()) {
								isToNextPage = false;
								IOHelper.getChapter(1, chapter.getIndex(),
										chapter.getPosition() - 1, bookType);
								return false;
							}
						}

						if (bookpage.prePage()) {
							bookpage.draw(ReaderActivity.this, nextCanvas);
							isFilp = true;
						} else {
							Toast.showShort(ReaderActivity.this, "已经是第一页了");
							return false;
						}
					} else {
						// 表示从百度详情界面跳转过来
						if (jumpType == 1 && !isAutoLoad) {
							if (bookpage.isLastPage()) {
								isToNextPage = true;
								IOHelper.getChapter(1, chapter.getIndex(),
										chapter.getPosition() + 1, bookType);
								return false;
							}
						}
						if (bookpage.nextPage()) {
							MyLogger.kLog().d("执行翻页");
							bookpage.draw(ReaderActivity.this, nextCanvas);
							isFilp = true;
						} else {
							Toast.showShort(ReaderActivity.this, "已经是最后一页了");
							return false;
						}
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
		// Intent intent = getIntent();
		// index = intent.getStringExtra("index");
		// lastUrl = intent.getStringExtra("lastUrl");
		// gid = intent.getStringExtra("gid");
		// int position = intent.getIntExtra("position", 0);
		// currentPage = intent.getIntExtra("currentPage", -1);
		// if (currentPage <= -2)
		// currentPage = -1;
		// type = intent.getIntExtra("type", 1);
		// bookType = intent.getIntExtra("bookType", 1);
		// statue = intent.getStringExtra("statue");
		// pageCount = intent.getIntExtra("pageCount", -1);
		// isNeedSaveProgress = intent.getBooleanExtra("isNeedSave", false);
		if (jumpType == 1 && !isAutoLoad) {
			chapter = IOHelper.getChapter(1, index, position, bookType);
		} else {
			chapter = IOHelper.getChapter(2, index, position, bookType);
		}
	}

	public void initIntent() {
		Intent intent = getIntent();
		index = intent.getStringExtra("index");
		isAutoLoad = intent.getBooleanExtra("isAutoLoad", false);
		lastUrl = intent.getStringExtra("lastUrl");
		gid = intent.getStringExtra("gid");
		position = intent.getIntExtra("position", 0);
		currentPage = intent.getIntExtra("currentPage", -1);
		if (currentPage <= -2)
			currentPage = -1;
		jumpType = intent.getIntExtra("type", 1);
		bookType = intent.getIntExtra("bookType", 1);
		statue = intent.getStringExtra("statue");
		pageCount = intent.getIntExtra("pageCount", -1);
		isNeedSaveProgress = intent.getBooleanExtra("isNeedSave", false);
	}

	/**
	 * 判断是否可以被点击
	 * 
	 * @param event
	 * @return
	 */
	private boolean isClickable(MotionEvent event) {
		int extraWidth = DeviceUtil.dip2px(this, 25);
		int disWith = (int) Math.abs(event.getX() - screenWith);
		int disHeight = (int) Math.abs(event.getY() - screenHeight);
		if ((extraWidth + screenWith / 3) < disWith
				&& disWith < (2 * screenWith / 3 - extraWidth)
				&& (extraWidth + screenHeight / 3) < disHeight
				&& disHeight < (2 * screenHeight / 3 - extraWidth)) {
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
		bottomHight = DeviceUtil.dip2px(this, 255);
		LayoutParams parmas = (LayoutParams) bookseeting_bottombar
				.getLayoutParams();
		parmas.height = bottomHight;
		LayoutParams parmas2 = (LayoutParams) booksetting_titlebar
				.getLayoutParams();
		parmas2.height = topHight;
		bookseeting_bottombar.setLayoutParams(parmas);
		booksetting_titlebar.setLayoutParams(parmas2);
		AnimationUtils.setViewTranslateDownY(50, bookseeting_bottombar, 0f,
				bottomHight);
		AnimationUtils.setViewTranslateDownY(50, booksetting_titlebar, 0,
				-topHight);
		initFragmentAdapter();
	}

	/**
	 * 每次都初始化Adapter
	 */
	private void initFragmentAdapter() {

		BookSettingFragmentAdapter mAdapter = new BookSettingFragmentAdapter(
				getSupportFragmentManager(), this, chapter.getBookName(),
				IOHelper.getChapterLength(), jumpType, bookType, statue, gid,
				lastUrl, isMnightMode);

		ViewPager mPager = (ViewPager) findViewById(R.id.pager);
		mPager.setAdapter(mAdapter);

		CirclePageIndicator mIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
		mIndicator.setViewPager(mPager);

	}

	/**
	 * 执行出现顶部和底部的TitleBar动画
	 */
	private void startAnimation(int duration) {
		if (!isUp) {
			AnimationUtils.setViewTranslateUpY(duration, booksetting_titlebar,
					-topHight, 0f);
			AnimationUtils.setViewTranslateUpY(duration, bookseeting_bottombar,
					bottomHight, 0f);
		} else {
			AnimationUtils.setViewTranslateDownY(duration,
					bookseeting_bottombar, 0f, bottomHight);
			AnimationUtils.setViewTranslateDownY(duration,
					booksetting_titlebar, 0f, -topHight);
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
	public void onTextBackgroundChange(int textColor) {
		// TODO Auto-generated method stub
		bookpage.setBgBitmap(this, BookTheme.READBOOK_BACKGROUND);
		bookpage.changeTextColor(textColor == -1 ? Color.BLACK : textColor);
		// pageWidget.initNightMode();
		pageWidget.setAutoNightFalse();
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
		pageWidget.initNightMode();
		bookpage.draw(this, isFilp ? nextCanvas : curCanvas);
		pageWidget.invalidate();

	}

	@Override
	public void onTextLineSpaceChange() {
		// TODO Auto-generated method stub
		bookpage.setTextLineSpace(false);
		pageWidget.initNightMode();
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
			if (jumpType == 2)
				overridePendingTransition(0, R.anim.fade_out);
			break;
		case R.id.booksetting_speech:
			isSpeech = !isSpeech;
			booksetting_speech
					.setImageResource(isSpeech ? R.drawable.iphone_yuyin_speech
							: R.drawable.iphone_yuyin);
			if (isSpeech)
				SpeechReader.getInstance().speechChapter(
						bookpage.getCurPage().toString());
			else
				SpeechReader.getInstance().clear();
			break;
		case R.id.booksetting_bookmark:
			new SaveMarkAsyncTask().execute();
			break;
		case R.id.booksetting_source:
			if (bookType == 1) {
				Toast.show(this, "该书已下载,无需换源!");
				return;
			} else {
				BaiduInfo book = BaiduBookDownload.getInstance(this)
						.queryNeedUpdate(bookpage.chapter.getBookName());
				BaiduBookDownload.getInstance(this).updateChapterName(book,
						"连夜启程");
				Toast.show(this, "插入最新章节成功!");
			}
			break;
		case R.id.booksetting_update:
			if (NetWorkUtils.isNetConnected(ReaderActivity.this)) {
				updateBook();
			} else {
				Toast.show(this, "未连接网络!");
			}
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
	 * 保存书签
	 */
	private void updateBookReaderProgress() {
		BookMark mark = new BookMark();
		mark.setBookName(bookpage.chapter.getBookName());
		mark.setChapterName(bookpage.chapter.getChapterName());
		mark.setPosition(bookpage.chapter.getPosition());
		downloadManager.updateReaderProgress(mark,
				IOHelper.getChapterLength() + 1);

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
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			saveBookReaderMark();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			Toast.show(getApplicationContext(), "添加书签成功!");
		}

	}

	@Override
	public void onCloseTitleBarAndBottomBar() {
		// TODO Auto-generated method stub
		startAnimation(50);
	}

	@Override
	public void onNightMode() {
		// TODO Auto-generated method stub
		bookpage.setNightMode();
		pageWidget.initNightMode();
		bookpage.draw(this, isFilp ? nextCanvas : curCanvas);
		pageWidget.invalidate();

	}

	@Override
	public void onUmengShare() {
		// TODO Auto-generated method stub
		UmengShare.getInstance().setShareContent(this,
				bookpage.chapter.getBookName(),
				bookpage.chapter.getChapterUrl(),
				bookpage.chapter.getBookName(),
				bookpage.chapter.getChapterUrl());
		UmengShare.getInstance().addCustomPlatforms(this);
	}

	@Override
	public void onTurnPageChange() {
		// TODO Auto-generated method stub
		isLeftHanderMode = false;
		pageWidget.setTurnPageType();
	}

	@Override
	public void onTurnPageLeftMode() {
		// TODO Auto-generated method stub
		isLeftHanderMode = true;
	}

	/**
	 * 更新书籍
	 */
	private void updateBook() {
		if (bookType == 1) {
			Toast.show(this, "该书已经完结!");
			AnimationUtils.cancelAnim(booksetting_update);
			return;
		} else {

			int type = SharedPreferences.getInstance().getInt(
					"book_update_type", 2);
			if (type == 1) {
				new AlertDialog(this)
						.builder()
						.setTitle("提醒")
						.setMsg("您现在选择的是整本更新模式，我们会更新您的整本书，这将会把您本地的书籍全部重新下载,是否继续。")
						.setNegativeButton("取消", new OnClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub

							}
						}).setPositiveButton("确定", new OnClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								AnimationUtils
										.setViewRotating(ReaderActivity.this,
												booksetting_update);
								BookUpdateTask task = new BookUpdateTask(
										ReaderActivity.this, bookpage.chapter
												.getBookName(), 1);
								task.setOnNewChapterListener(ReaderActivity.this);
								task.execute();
							}
						}).show();
			} else if (type == 2) {
				AnimationUtils.setViewRotating(ReaderActivity.this,
						booksetting_update);
				BookUpdateTask task = new BookUpdateTask(this,
						bookpage.chapter.getBookName(), 1);
				task.setOnNewChapterListener(this);
				task.execute();

			}
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		readerTime = System.currentTimeMillis();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		saveBookReaderProgress();
		updateBookReaderProgress();
		long lastReaderTime = SharedPreferences.getInstance().getLong(
				SpConstant.BOOK_READER_TIME, 0L);
		SharedPreferences.getInstance().putLong(SpConstant.BOOK_READER_TIME,
				lastReaderTime + (System.currentTimeMillis() - readerTime));

		super.onPause();
	}

	@Override
	public void onUpdateSuccess(LastChapter chapter) {
		// TODO Auto-generated method stub
		Toast.show(this, "最新章节更新成功");
		AnimationUtils.cancelAnim(booksetting_update);
	}

	@Override
	public void onUpdateFailure() {
		// TODO Auto-generated method stub
		AnimationUtils.cancelAnim(booksetting_update);
		Toast.show(this, "  暂无更新     ");
	}

	@Override
	public void onUpdateProgress(String bookName) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUpdateAllNewChapter() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTextColorBgChange() {
		ColorPickerDialog dialog = new ColorPickerDialog(this);
		dialog.show();
		dialog.getPickerColorView().setOnColorChangedListenner(this);

	}

	@Override
	public void onbgColorChanged(int color, String hexStrColor) {
		// TODO Auto-generated method stub
		SharedPreferences.getInstance().putInt(SpConstant.BOOK_AUTO_COLOR_BG,
				color);
		bookpage.setBgColor(color);
		bookpage.draw(this, isFilp ? nextCanvas : curCanvas);
		pageWidget.invalidate();
	}

	@Override
	public void onTextColorChanged(int color, String hexStrColor) {
		// TODO Auto-generated method stub
		SharedPreferences.getInstance().putInt(SpConstant.BOOK_AUTO_COLOR_TEXT,
				color);
		bookpage.changeTextColor(color);
		bookpage.draw(this, isFilp ? nextCanvas : curCanvas);
		pageWidget.invalidate();
	}

	/**
	 * 夜间模式
	 */
	private void setTitleBarNight() {

		boolean isNightMode = SharedPreferences.getInstance().getBoolean(
				SpConstant.BOOK_SETTING_NIGHT_MODE, false);
		boolean isAutoNightMode = SharedPreferences.getInstance().getBoolean(
				SpConstant.BOOK_SETTING_AUTO_NIGHT_MODE_YES, false);
		if (isNightMode || isAutoNightMode) {
			this.isMnightMode = true;
			booksetting_titlebar.setBackgroundColor(BookTheme.BOOK_SETTING_BG);
			bookseeting_bottombar.setBackgroundColor(BookTheme.BOOK_SETTING_BG);
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (jumpType == 2) {
			if (keyCode == KeyEvent.KEYCODE_BACK) {
				finish();

				overridePendingTransition(0, R.anim.fade_out);
			}
		}

		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		if (intent != null) {
			index = intent.getStringExtra("index");
			int position = intent.getIntExtra("position", 0);
			currentPage = intent.getIntExtra("currentPage", -1);
			if (currentPage <= -2)
				currentPage = -1;
			chapter = IOHelper.getChapter(jumpType, index, position, bookType);
			if (jumpType == 2) {
				bookpage.currentChapter(chapter);
				bookpage.nextPage();
				bookpage.draw(this, isFilp ? nextCanvas : curCanvas);
				pageWidget.invalidate();
			}

		}

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		SpeechReader.getInstance().clear();
		boolean isNigthModeHand = SharedPreferences.getInstance().getBoolean(
				SpConstant.BOOK_SETTING_NITGHT_HAND, false);
		if (isNigthModeHand) {
			SharedPreferences.getInstance().putBoolean(
					SpConstant.BOOK_SETTING_AUTO_NIGHT, true);
		}
		pageWidget.destroy();
		bookpage.destory();
		if (curBitmap != null) {
			curBitmap.recycle();
			curBitmap = null;
		}
		if (nextBitmap != null) {
			nextBitmap.recycle();
			nextBitmap = null;
		}
		System.gc();

	}

	@Override
	public void onGetChapterPre() {
		// TODO Auto-generated method stub
		showRefreshDialog("玩命加载中");

	}

	@Override
	public boolean onGetChapterSuccess(Chapter chapter) {
		// TODO Auto-generated method stub

		if (isFirstloading) {
			this.chapter = chapter;
			bookpage.initChapter(chapter, currentPage, pageCount, bookType);
			isFirstloading = false;
		} else {

			this.chapter = chapter;
			bookpage.currentAsyncChapter(this.chapter);
			if (isToNextPage) {
				pageWidget.startAnimation();
				bookpage.nextPage();
			} else {
				pageWidget.startAnimation();
				bookpage.setCurrentPageNum();
				// bookpage.prePage();
			}
			dismissRefreshDialog();
			bookpage.draw(this, isFilp ? nextCanvas : curCanvas);
			pageWidget.invalidate();
		}

		return true;
	}

	@Override
	public void onGetChapterFailure() {
		// TODO Auto-generated method stub

	}

}
