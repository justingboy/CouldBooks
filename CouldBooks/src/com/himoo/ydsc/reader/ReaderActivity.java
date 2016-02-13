package com.himoo.ydsc.reader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.himoo.ydsc.R;
import com.himoo.ydsc.activity.ChangeSourceActivity;
import com.himoo.ydsc.animation.AnimationUtils;
import com.himoo.ydsc.base.BaseReaderActivity;
import com.himoo.ydsc.bean.BaiduBookChapter;
import com.himoo.ydsc.config.BookTheme;
import com.himoo.ydsc.config.SpConstant;
import com.himoo.ydsc.dialog.ColorPickerDialog;
import com.himoo.ydsc.dialog.SpeechPopupWindow;
import com.himoo.ydsc.download.BaiduBookDownload;
import com.himoo.ydsc.download.BookDownloadManager;
import com.himoo.ydsc.download.BookDownloadService;
import com.himoo.ydsc.download.BookDownloadTask;
import com.himoo.ydsc.download.BookDownloadTask.OnBookDownloadListener;
import com.himoo.ydsc.fragment.BookShelfFragment;
import com.himoo.ydsc.fragment.BookShelfFragment.BookDownloadReceiver;
import com.himoo.ydsc.fragment.reader.BookSettingFragment1.OnFragment1Listener;
import com.himoo.ydsc.fragment.reader.BookSettingFragment2.OnFragment2Listener;
import com.himoo.ydsc.fragment.reader.BookSettingFragment3.OnFragment3Listener;
import com.himoo.ydsc.fragment.reader.BookSettingFragmentAdapter;
import com.himoo.ydsc.http.HttpConstant;
import com.himoo.ydsc.listener.NoDoubleClickListener;
import com.himoo.ydsc.reader.bean.Chapter;
import com.himoo.ydsc.reader.config.BitmapConfig;
import com.himoo.ydsc.reader.dao.BookMark;
import com.himoo.ydsc.reader.dao.BookMarkDb;
import com.himoo.ydsc.reader.utils.BookPage;
import com.himoo.ydsc.reader.utils.IOHelper;
import com.himoo.ydsc.reader.utils.IOHelper.OnGetChapterListener;
import com.himoo.ydsc.reader.view.ColorPickerView.OnColorChangedListener;
import com.himoo.ydsc.reader.view.PageWidget;
import com.himoo.ydsc.reader.view.PageWidget.Mode;
import com.himoo.ydsc.share.UmengShare;
import com.himoo.ydsc.speech.SpeechReader;
import com.himoo.ydsc.ui.utils.Toast;
import com.himoo.ydsc.update.BookUpdateTask;
import com.himoo.ydsc.update.BookUpdateTask.OnNewChapterUpdateListener;
import com.himoo.ydsc.update.LastChapter;
import com.himoo.ydsc.util.DeviceUtil;
import com.himoo.ydsc.util.FileUtils;
import com.himoo.ydsc.util.MyLogger;
import com.himoo.ydsc.util.NetWorkUtils;
import com.himoo.ydsc.util.SP;
import com.himoo.ydsc.util.SharedPreferences;
import com.himoo.ydsc.util.TimestampUtils;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SynthesizerListener;
import com.ios.radiogroup.SegmentedGroup;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.viewpagerindicator.CirclePageIndicator;

/**
 * 阅读小说的界面
 */
public class ReaderActivity extends BaseReaderActivity implements
		OnClickListener, OnFragment1Listener, OnFragment2Listener,
		OnFragment3Listener, OnNewChapterUpdateListener,
		OnColorChangedListener, OnGetChapterListener,
		RadioGroup.OnCheckedChangeListener, OnBookDownloadListener,
		OnTouchListener {
	/** 通知广播的Action */
	private static final String ACTION = "com.himoo.ydsc.shelf.receiver";
	private BookDownloadManager downloadManager;
	private PageWidget pageWidget;
	private Bitmap curBitmap, nextBitmap;
	private Canvas curCanvas, nextCanvas;
	public BookPage bookpage;

	private Drawable refreashDrawable;
	private Drawable defaultRefreashDrawable;
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

	@ViewInject(R.id.layout_more_rel)
	private RelativeLayout parentView;

	private SegmentedGroup segment_speech;

	@ViewInject(R.id.layout_mogo)
	private LinearLayout layout_mogo;
	private BookSettingFragmentAdapter mAdapter;
	private final static String[] title = { "左手", "仿真", "无动画", "连动", "上下" };
	private boolean isUp = false;
	private boolean isSpeech = false;
	private View view;
	private int topHight;
	private int bottomHight;
	/** 用于判断是否翻页了 */
	private boolean isFilp = false;
	/** 用于判断是否是第一章或者是最后一章 */
	private boolean isLastOrFistChapetr = false;
	private String index;
	private int currentPage, pageCount;
	private boolean isNeedSaveProgress = false;
	/** 该type表示是从哪个界面跳转的 1 表示从详情界面，2表示从书架上 */
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
	/** 是否在调用上一章和下一章 */
	private boolean isAutoNextChapter = false;
	private View speech_view;
	private SpeechPopupWindow popupWindow;
	private boolean isSaveInstanceState = false;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setNetWorkOnMainMode();
		downloadManager = BookDownloadService.getDownloadManager(this);
		if (savedInstanceState != null) {
			getSaveParm(savedInstanceState);
			isSaveInstanceState = true;
		} else {
			isSaveInstanceState = false;
			initIntent();
		}
		view = LayoutInflater.from(this)
				.inflate(R.layout.activity_reader, null);
		layout = (LinearLayout) view.findViewById(R.id.pagewidget_layout);
		setContentView(view);
		refreashDrawable = getResources().getDrawable(
				R.drawable.reader_refreash);
		defaultRefreashDrawable = getResources().getDrawable(
				R.drawable.iphone_update);

		ViewUtils.inject(this);
		int speed = SharedPreferences.getInstance().getInt(
				SpConstant.BOOK_READER_SPEED, 50);
		SpeechReader.getInstance().initSpeech(this, speed);
		setListener();
		setTitleBarNight();
		initChapter();
		if (isAutoLoad && jumpType == 1) {
			initReaderBook(2);
		} else {
			initReaderBook(jumpType);
		}
		layout.addView(pageWidget);
		initAnimation();
		pageWidget.setBitmaps(curBitmap, curBitmap);
		if (isAutoLoad && jumpType == 1 || jumpType == 2) {
			pageWidget.setOnTouchListener(this);
		}
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
		initAgChapter();
		initUpdateImageVIew();
	}

	public void setListener() {
		IOHelper.setOnChapterListener(this);
		booksetting_back.setOnClickListener(this);
		booksetting_speech.setOnClickListener(this);
		booksetting_bookmark.setOnClickListener(this);
		// booksetting_source.setOnClickListener(this);
		booksetting_source.setOnClickListener(new NoDoubleClickListener() {

			@Override
			public void onNoDoubleClick(View v) {
				if (NetWorkUtils.isNetConnected(ReaderActivity.this)) {
					startToActivity();
				} else {
					Toast.show(ReaderActivity.this, "未连接网络");
				}
			}
		});
		booksetting_update.setOnClickListener(this);
	}

	/**
	 * 初始化
	 */
	private void initReaderBook(int jumpType) {
		screenWith = DeviceUtil.getWidth(this);
		int type = SharedPreferences.getInstance()
				.getInt("book_update_type", 2);
		boolean isNeedMogoAd = false;
		if (type == 1) {
			if (NetWorkUtils.isNetConnected(this)) {
				isNeedMogoAd = true;
			}
		}
		screenHeight = DeviceUtil.getHeight(this);
		curBitmap = BitmapConfig.getInstace().createCurBitmap(screenWith,
				screenHeight);
		nextBitmap = BitmapConfig.getInstace().createNextBitmap(screenWith,
				screenHeight);
		curCanvas = new Canvas(curBitmap);
		nextCanvas = new Canvas(nextBitmap);
		if (jumpType != 1 || (jumpType == 1 && isAutoLoad)) {
			bookpage = new BookPage(this, screenWith, screenHeight, chapter,
					currentPage, pageCount, bookType, isNeedMogoAd);
		} else {
			bookpage = new BookPage(this, screenWith, screenHeight,
					isNeedMogoAd);

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
		pageWidget = new PageWidget(this, screenWith, screenHeight, null,
				isNeedMogoAd);
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
//					if (pageWidget.mMode == Mode.TURN_UPANDDOWN) {
//						if (!pageWidget.DragToUp()) {
//							if (jumpType == 1 && !isAutoLoad) {
//								if (bookpage.isFirstPage()) {
//									isToNextPage = false;
//									IOHelper.getChapter(1, chapter.getIndex(),
//											chapter.getPosition() - 1, bookType);
//									return false;
//								}
//							}
//							if (bookpage.prePage()) {
//								isLastOrFistChapetr = false;
//								bookpage.draw(ReaderActivity.this, nextCanvas);
//								isFilp = true;
//							} else {
//								Toast.showShort(ReaderActivity.this, "已经是第一页了");
//								isLastOrFistChapetr = true;
//								return false;
//							}
//						} else {
//							// 表示从百度详情界面跳转过来
//							if (jumpType == 1 && !isAutoLoad) {
//								if (bookpage.isLastPage()) {
//									isToNextPage = true;
//									IOHelper.getChapter(1, chapter.getIndex(),
//											chapter.getPosition() + 1, bookType);
//									return false;
//								}
//							}
//							if (bookpage.nextPage()) {
//								isLastOrFistChapetr = false;
//								bookpage.draw(ReaderActivity.this, nextCanvas);
//								isFilp = true;
//							} else {
//								isLastOrFistChapetr = true;
//								Toast.showShort(ReaderActivity.this, "已经是最后一页了");
//								return false;
//							}
//
//						}
//
//					} else {
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
								isLastOrFistChapetr = false;
								bookpage.draw(ReaderActivity.this, nextCanvas);
								isFilp = true;
							} else {
								isLastOrFistChapetr = true;
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
								isLastOrFistChapetr = false;
								bookpage.draw(ReaderActivity.this, nextCanvas);
								isFilp = true;
							} else {
								isLastOrFistChapetr = true;
								Toast.showShort(ReaderActivity.this, "已经是最后一页了");
								return false;
							}
						}
//					}
				} else {
//					if (pageWidget.mMode == Mode.TURN_UPANDDOWN) {
//						if (pageWidget.DragToUp()) {
//							if (jumpType == 1 && !isAutoLoad) {
//								if (bookpage.isFirstPage()) {
//									isToNextPage = false;
//									IOHelper.getChapter(1, chapter.getIndex(),
//											chapter.getPosition() - 1, bookType);
//									return false;
//								}
//							}
//							if (bookpage.prePage()) {
//								isLastOrFistChapetr = false;
//								bookpage.draw(ReaderActivity.this, nextCanvas);
//								isFilp = true;
//							} else {
//								isLastOrFistChapetr = true;
//								Toast.showShort(ReaderActivity.this, "已经是第一页了");
//								return false;
//							}
//						} else {
//							// 表示从百度详情界面跳转过来
//							if (jumpType == 1 && !isAutoLoad) {
//								if (bookpage.isLastPage()) {
//									isToNextPage = true;
//									IOHelper.getChapter(1, chapter.getIndex(),
//											chapter.getPosition() + 1, bookType);
//									return false;
//								}
//							}
//							if (bookpage.nextPage()) {
//								isLastOrFistChapetr = false;
//								bookpage.draw(ReaderActivity.this, nextCanvas);
//								isFilp = true;
//							} else {
//								isLastOrFistChapetr = true;
//								Toast.showShort(ReaderActivity.this, "已经是最后一页了");
//								return false;
//							}
//
//						}
//
//					} else {

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
//						}
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
		if (jumpType == 1 && !isAutoLoad) {
			chapter = IOHelper.getChapter(1, index, position, bookType);
		} else {
			chapter = IOHelper.getChapter(2, index, position, bookType);
		}
	}

	/**
	 * 初始化章节
	 */
	private void initAgChapter() {
		if (jumpType == 1 && !isAutoLoad) {
			chapter = IOHelper.getChapter(1, index, position, bookType);
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
		int extraHeight = DeviceUtil.dip2px(this, 40);
		int disWith = (int) Math.abs(event.getX() - screenWith);
		int disHeight = (int) Math.abs(event.getY() - screenHeight);
		if ((extraWidth + screenWith / 3) < disWith
				&& disWith < (2 * screenWith / 3 - extraWidth)
				&& (extraHeight + screenHeight / 3) < disHeight
				&& disHeight < (2 * screenHeight / 3 - extraHeight)) {
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
		if (bookType == 1) {
			booksetting_source.setVisibility(View.INVISIBLE);
			booksetting_source.setClickable(false);
			booksetting_update.setVisibility(View.INVISIBLE);
			booksetting_update.setClickable(false);

		}
	}

	/**
	 * 每次都初始化Adapter
	 */
	private void initFragmentAdapter() {

		mAdapter = new BookSettingFragmentAdapter(getSupportFragmentManager(),
				this, chapter.getBookName(), IOHelper.getChapterLength(),
				jumpType, bookType, statue, gid, lastUrl, isMnightMode);

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
		int pageNum = bookpage.pageNum;
		int pageCount = bookpage.pagesVe.size();
		SharedPreferences.getInstance().putInt(
				SpConstant.BOOK_SETTING_TEXT_SIZE, textSize);
		bookpage.setTextSzie(textSize);
		bookpage.updateCurrentpageNum(pageNum, pageCount);
		bookpage.draw(this, isFilp ? nextCanvas : curCanvas);
		pageWidget.invalidate();
	}

	@Override
	public void onPreChapter() {
		// TODO Auto-generated method stub
		if (jumpType == 1 && !isAutoLoad) {
			isAutoNextChapter = true;
			isToNextPage = false;
			chapter = IOHelper.getChapter(1, index, chapter.getPosition() - 1,
					bookType);
		} else {
			bookpage.initPreChapter();
			bookpage.draw(this, isFilp ? nextCanvas : curCanvas);
			pageWidget.invalidate();
		}
	}

	@Override
	public void onNextChapter() {
		// TODO Auto-generated method stub
		// 表示联网获取章节信息
		if (jumpType == 1 && !isAutoLoad) {
			isToNextPage = true;
			isAutoNextChapter = true;
			chapter = IOHelper.getChapter(1, index, chapter.getPosition() + 1,
					bookType);
		} else {
			bookpage.initNextChapter();
			bookpage.draw(this, isFilp ? nextCanvas : curCanvas);
			pageWidget.invalidate();
		}
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
		int pageNum = bookpage.pageNum;
		int pageCount = bookpage.pagesVe.size();
		bookpage.setTextType();
		bookpage.updateCurrentpageNum(pageNum, pageCount);
		bookpage.draw(this, isFilp ? nextCanvas : curCanvas);
		pageWidget.invalidate();

	}

	@Override
	public void onTextTypeChildrenChange() {
		// TODO Auto-generated method stub
		int pageNum = bookpage.pageNum;
		int pageCount = bookpage.pagesVe.size();
		bookpage.setTextTypeChildren(false);
		bookpage.updateCurrentpageNum(pageNum, pageCount);
		pageWidget.initNightMode();
		bookpage.draw(this, isFilp ? nextCanvas : curCanvas);
		pageWidget.invalidate();

	}

	@Override
	public void onTextLineSpaceChange() {
		// TODO Auto-generated method stub
		int pageNum = bookpage.pageNum;
		int pageCount = bookpage.pagesVe.size();
		bookpage.setTextLineSpace(false);
		bookpage.updateCurrentpageNum(pageNum, pageCount);
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
			if (jumpType == 2) {
				// String filePath = Environment.getExternalStorageDirectory() +
				// "/DCIM/"
				// + "aaaa.png";
				// ScreenShot.shoot(this, new File(filePath));
				overridePendingTransition(0, R.anim.fade_out);
			}
			break;
		case R.id.booksetting_speech:
			// 联网的情况下才可以使用该功能
			if (NetWorkUtils.isNetConnected(this)) {

				if (speech_view == null) {
					speech_view = View.inflate(this, R.layout.dialog_speech,
							null);
					segment_speech = (SegmentedGroup) speech_view
							.findViewById(R.id.segment_speech);
					setSegmentedGroupColor();
				}
				isSpeech = !isSpeech;
				booksetting_speech
						.setImageResource(isSpeech ? R.drawable.iphone_yuyin_speech
								: R.drawable.iphone_yuyin);
				if (isSpeech) {
					if (popupWindow == null)
						popupWindow = new SpeechPopupWindow(this, speech_view);
					popupWindow.showAtLocation(parentView, Gravity.TOP, 0,
							DeviceUtil.dip2px(this, 50));
					SpeechReader.getInstance().speechChapter(
							bookpage.getCurPage(), mSynListener);
				} else {
					SpeechReader.getInstance().clear();
					popupWindow.dismiss();
				}
			} else {
				Toast.show(this, "未连接网络!");
			}
			break;
		case R.id.booksetting_bookmark:
			if (jumpType == 1) {
				Toast.show(this, "在线看书，不可添加书签");
				return;
			}
			new SaveMarkAsyncTask().execute();
			break;
		case R.id.booksetting_source:
			if (bookType == 1) {
				booksetting_source.setVisibility(View.INVISIBLE);
				booksetting_source.setClickable(false);
				// Toast.show(this, "该书已下载,无需换源!");
				return;
			} else {
				/*
				 * BaiduInfo book = BaiduBookDownload.getInstance(this)
				 * .queryNeedUpdate(bookpage.chapter.getBookName());
				 * BaiduBookDownload.getInstance(this).updateChapterName(book,
				 * "连夜启程"); Toast.show(this, "插入最新章节成功!");
				 */
				if (NetWorkUtils.isNetConnected(this)) {
					startToActivity();
				} else {
					Toast.show(this, "未连接网络");
				}
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
			booksetting_update.setImageDrawable(defaultRefreashDrawable);
			AnimationUtils.cancelAnim(booksetting_update);
			return;
		} else {
			// 在线看书，并且没有下载的书显示下载图标
			if (jumpType == 1 && !isDownload) {
				downLoadBook();
			} else {

				// int type = SharedPreferences.getInstance().getInt(
				// "book_update_type", 2);
				// if (type == 1) {
				// new AlertDialog(this)
				// .builder()
				// .setTitle("提醒")
				// .setMsg("您现在选择的是整本更新模式，我们会更新您的整本书，这将会把您本地的书籍全部重新下载,是否继续。")
				// .setNegativeButton("取消", new OnClickListener() {
				//
				// @Override
				// public void onClick(View v) {
				// // TODO Auto-generated method stub
				//
				// }
				// }).setPositiveButton("确定", new OnClickListener() {
				//
				// @Override
				// public void onClick(View v) {
				// // TODO Auto-generated method stub
				// booksetting_update
				// .setImageDrawable(refreashDrawable);
				// AnimationUtils.setViewRotating(
				// ReaderActivity.this,
				// booksetting_update);
				// BookUpdateTask task = new BookUpdateTask(
				// ReaderActivity.this,
				// bookpage.chapter.getBookName(), 1);
				// task.setOnNewChapterListener(ReaderActivity.this);
				// task.execute();
				// }
				// }).show();
				// } else if (type == 2) {
				booksetting_update.setImageDrawable(refreashDrawable);
				AnimationUtils.setViewRotating(ReaderActivity.this,
						booksetting_update);

				BookUpdateTask task = new BookUpdateTask(this,
						bookpage.chapter.getBookName(), 1);
				task.setOnNewChapterListener(this);
				task.execute();

				// }
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
		booksetting_update.setImageDrawable(defaultRefreashDrawable);
		AnimationUtils.cancelAnim(booksetting_update);
	}

	@Override
	public void onUpdateFailure() {
		// TODO Auto-generated method stub
		booksetting_update.setImageDrawable(defaultRefreashDrawable);
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
		SharedPreferences.getInstance().putBoolean(
				SpConstant.BOOK_AUTO_COLOR_FIRST, false);
		SharedPreferences.getInstance().putInt(SpConstant.BOOK_AUTO_COLOR_BG,
				color);
		bookpage.setBgColor(color);
		bookpage.draw(this, isFilp ? nextCanvas : curCanvas);
		pageWidget.invalidate();
	}

	@Override
	public void onTextColorChanged(int color, String hexStrColor) {
		// TODO Auto-generated method stub
		SharedPreferences.getInstance().putBoolean(
				SpConstant.BOOK_AUTO_COLOR_FIRST, false);
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
				/*
				 * String filePath = Environment.getExternalStorageDirectory() +
				 * "/DCIM/" + "aaaa.png"; ScreenShot.shoot(this, new
				 * File(filePath));
				 */
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
			String content = intent.getStringExtra("chapter");
			String chapterName = intent.getStringExtra("chapterName");
			int position2 = intent.getIntExtra("position", -1);
			if (content != null) {
				startAnimation(50);
				chapter.setContent(content);
				chapter.setChapterName(chapterName);
				chapter.setPosition(position2);
				bookpage.currentChapter(chapter);
				bookpage.nextPage();
				bookpage.draw(this, isFilp ? nextCanvas : curCanvas);
				pageWidget.invalidate();

			} else {

				index = intent.getStringExtra("index");
				int position = intent.getIntExtra("position", 0);
				currentPage = intent.getIntExtra("currentPage", -1);
				pageCount = intent.getIntExtra("pageCount", -1);
				if (currentPage <= -2)
					currentPage = -1;
				chapter = IOHelper.getChapter(jumpType, index, position,
						bookType);
				// 下载后的跳转
				if (jumpType == 2) {
					if (pageCount > 0)
						bookpage.initChapter(chapter, currentPage, pageCount,
								bookType);
					else
						bookpage.currentChapter(chapter);
					bookpage.nextPage();
					bookpage.draw(this, isFilp ? nextCanvas : curCanvas);
					pageWidget.invalidate();
				}
			}

		}

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub

		if (receiver != null)
			unregisterReceiver(receiver);
		if (isSpeech) {
			SpeechReader.getInstance().clear();
		}
		boolean isNigthModeHand = SharedPreferences.getInstance().getBoolean(
				SpConstant.BOOK_SETTING_NITGHT_HAND, false);
		if (isNigthModeHand) {
			SharedPreferences.getInstance().putBoolean(
					SpConstant.BOOK_SETTING_AUTO_NIGHT, true);
		}
		pageWidget.destroy();
		bookpage.destory();
		System.gc();
		super.onDestroy();
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
			pageWidget.setOnTouchListener(this);
			Log.i("msg", "currentPage = " + currentPage + "pageCount = "
					+ pageCount);
			isFirstloading = false;
		} else {
			Log.i("msg", "第二次加载");
			this.chapter = chapter;
			bookpage.currentAsyncChapter(this.chapter);
			//恢复时记录的当前页数
			if(isSaveInstanceState)
				bookpage.setCurrentPage(currentPage);
			if (isToNextPage) {
				if (!isAutoNextChapter) {
					if (pageWidget.mMode == Mode.TURN_UPANDDOWN) {
						pageWidget.startUpAnimation();
					} else
						pageWidget.startAnimation();
					isAutoNextChapter = false;
				}
				bookpage.nextPage();
			} else {
				if (!isAutoNextChapter) {
					if (pageWidget.mMode == Mode.TURN_UPANDDOWN) {
						pageWidget.startUpAnimation();
					} else
						pageWidget.startAnimation();
					isAutoNextChapter = false;
				}
				bookpage.setAsyPageNum();
				bookpage.prePage();
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

	/** 设置颜色 */
	private void setSegmentedGroupColor() {
		segment_speech.setOnCheckedChangeListener(this);
		segment_speech.setTintColor(BookTheme.BOOK_TEXT_WHITE);
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		// TODO Auto-generated method stub
		SpeechReader.getInstance().clear();
		switch (checkedId) {
		case R.id.speech_1:
			SpeechReader.getInstance().initSpeech(this, 10);
			SharedPreferences.getInstance().putInt(
					SpConstant.BOOK_READER_SPEED, 10);
			break;
		case R.id.speech_2:
			SpeechReader.getInstance().initSpeech(this, 30);
			SharedPreferences.getInstance().putInt(
					SpConstant.BOOK_READER_SPEED, 30);
			break;
		case R.id.speech_3:
			SpeechReader.getInstance().initSpeech(this, 50);
			SharedPreferences.getInstance().putInt(
					SpConstant.BOOK_READER_SPEED, 50);
			break;
		case R.id.speech_4:
			SpeechReader.getInstance().initSpeech(this, 70);
			SharedPreferences.getInstance().putInt(
					SpConstant.BOOK_READER_SPEED, 70);
			break;
		case R.id.speech_5:
			SpeechReader.getInstance().initSpeech(this, 100);
			SharedPreferences.getInstance().putInt(
					SpConstant.BOOK_READER_SPEED, 100);
			break;

		default:
			break;
		}
		SpeechReader.getInstance().speechChapter(
				bookpage.getCurPage().toString(), mSynListener);
	}

	// 合成监听器
	private SynthesizerListener mSynListener = new SynthesizerListener() {

		// 缓冲进度回调
		// percent为缓冲进度0~100，beginPos为缓冲音频在文本中开始位置，endPos表示缓冲音频在文本中结束位置，info为附加信息。
		public void onBufferProgress(int percent, int beginPos, int endPos,
				String info) {
		}

		// 开始播放
		public void onSpeakBegin() {
			Toast.show(ReaderActivity.this, "开始播报语音");
		}

		// 暂停播放
		public void onSpeakPaused() {

		}

		// 播放进度回调
		// percent为播放进度0~100,beginPos为播放音频在文本中开始位置，endPos表示播放音频在文本中结束位置.
		public void onSpeakProgress(int percent, int beginPos, int endPos) {
			Log.i("msg", "percent =" + percent + "|beginPos = " + beginPos
					+ "|endPos=" + endPos);
		}

		// 恢复播放回调接口
		public void onSpeakResumed() {
		}

		// 会话事件回调接口
		public void onEvent(int arg0, int arg1, int arg2, Bundle arg3) {
		}

		// 会话结束回调接口，没有错误时，error为null
		@Override
		public void onCompleted(SpeechError error) {
			// TODO Auto-generated method stub
			if (error == null) {
				Toast.show(ReaderActivity.this, "语音结束时发生错误");
			} else {
				Toast.show(ReaderActivity.this, "语音播报完成");
			}

		}
	};
	private boolean isDownload;
	private BookDownloadReceiver receiver;

	/**
	 * 跳转到换源的界面
	 */
	protected void startToActivity() {
		Intent intent = new Intent(this, ChangeSourceActivity.class);
		if (jumpType == 2) {
			File dirFile = new File(FileUtils.mSdRootPath + "/CouldBook/baidu"
					+ File.separator + bookpage.chapter.getBookName() + File.separator);
			String chapterName = bookpage.chapter.getChapterName().trim()
					.replaceAll("/", "|")
					+ "-|"
					+ bookpage.chapter.getIndex()
					+ "-|"
					+ bookpage.chapter.getPosition() + ".txt";
			String filePath = dirFile.getAbsolutePath() + File.separator
					+ chapterName;
			intent.putExtra("filePath", filePath);
			intent.putExtra("lastUrl", lastUrl);
			intent.putExtra("position", bookpage.chapter.getPosition());
		} else {
			
			intent.putExtra("src", bookpage.chapter.getSrc());
			intent.putExtra("cid", bookpage.chapter.getCid());
			intent.putExtra("position", bookpage.chapter.getPosition());
		}
		intent.putExtra("chapterName",bookpage.chapter.getChapterName());
		intent.putExtra("index", bookpage.chapter.getIndex());
		startActivity(intent);
		overridePendingTransition(R.anim.dialog_enter_bottom,
				R.anim.dialog_no_animation);

	}

	/**
	 * 更新的图标的设置
	 */
	private void initUpdateImageVIew() {
		isDownload = BaiduBookDownload.getInstance(this).isDownload(
				chapter.getBookName());
		if (jumpType == 1 && !isDownload) {
			booksetting_update.setImageDrawable(getResources().getDrawable(
					R.drawable.iphone_download));
		}

	}

	/**
	 * 下载书籍
	 */
	private void downLoadBook() {

		try {
			BaiduBookDownload.getInstance(this).addBaiduBookDownload(
					IOHelper.getBaiduBook());
			SP.getInstance().putBoolean(chapter.getBookName(), false);
		} catch (DbException e) {
			MyLogger.kLog().e(e);
		}
		List<BaiduBookChapter> list = IOHelper.getBookChapter();
		new BookDownloadTask(this, list, chapter.getBookName(), lastUrl, this)
				.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	/**
	 * 保存是否正在下载
	 * 
	 * @param isDown
	 */
	private void sendToBookShelfBroadcastSucess(boolean isDown) {
		SP.getInstance().putBoolean("isDown", !isDown);
	}

	@Override
	public void onPreDownlaod() {
		// TODO Auto-generated method stub
		Toast.show(this, "开始下载《" + chapter.getBookName() + "》");
		registeBroadcast();
		sendToBookShelfBroadcastSucess(true);
		booksetting_update.setImageDrawable(refreashDrawable);
		AnimationUtils.setViewRotating(ReaderActivity.this, booksetting_update);
	}

	@Override
	public void onCompleteDownlaod() {
		// TODO Auto-generated method stub
		SP.getInstance().putBoolean(chapter.getBookName(), true);
		Toast.show(this, "《" + chapter.getBookName() + "》下载完成");
		sendToBookShelfBroadcastSucess(false);
		booksetting_update.setImageDrawable(defaultRefreashDrawable);
		AnimationUtils.cancelAnim(booksetting_update);

	}

	/**
	 * 注册广播
	 */
	private void registeBroadcast() {
		receiver = new BookShelfFragment.BookDownloadReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION);
		registerReceiver(receiver, filter);
		Intent intent = new Intent(ACTION);
		intent.putExtra("bookName", chapter.getBookName());
		String url = HttpConstant.BAIDU_BOOK_DETAILS_URL + "appui=alaxs&gid="
				+ gid + "&dir=1&ajax=1";
		intent.putExtra("dowloadUrl", url);
		sendBroadcast(intent);

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub

		outState.putString("bookName", chapter.getBookName());
		outState.putInt("jumpType", jumpType);
		outState.putString("index", chapter.getIndex());
		outState.putInt("position", chapter.getPosition());
		outState.putInt("bookType", bookType);
		outState.putInt("pageNum", bookpage.pageNum);
		outState.putBoolean("isAutoLoad", isAutoLoad);
		outState.putString("lastUrl", lastUrl);
		outState.putString("gid", gid);
		outState.putString("statue", statue);
		outState.putInt("pageCount", bookpage.pagesVe.size());
		outState.putBoolean("isNeedSave", isNeedSaveProgress);
		outState.putParcelableArrayList("list", IOHelper.getBookChapter());
		super.onSaveInstanceState(outState);

	}

	public void getSaveParm(Bundle savedInstanceState) {
		jumpType = savedInstanceState.getInt("jumpType", 1);
		String bookName = savedInstanceState.getString("bookName");
		// BaiduBook baiduBook = savedInstanceState.getParcelable("baiduBook");
		ArrayList<BaiduBookChapter> chapterList = savedInstanceState
				.getParcelableArrayList("list");
		IOHelper.getBook(this, bookName, chapterList);

		index = savedInstanceState.getString("index");
		isAutoLoad = savedInstanceState.getBoolean("isAutoLoad", false);
		lastUrl = savedInstanceState.getString("lastUrl");
		gid = savedInstanceState.getString("gid");
		position = savedInstanceState.getInt("position", 0);
		currentPage = savedInstanceState.getInt("pageNum", -1)-1;
		if (currentPage <= -2)
			currentPage = -1;
		bookType = savedInstanceState.getInt("bookType", 1);
		statue = savedInstanceState.getString("statue");
		pageCount = savedInstanceState.getInt("pageCount", -1);
		isNeedSaveProgress = savedInstanceState.getBoolean("isNeedSave", false);
	}
	


}
