package com.himoo.ydsc.reader;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;

import com.himoo.ydsc.R;
import com.himoo.ydsc.activity.more.WallActivity;
import com.himoo.ydsc.reader.bean.Chapter;
import com.himoo.ydsc.reader.utils.BookPage;
import com.himoo.ydsc.reader.utils.IOHelper;
import com.himoo.ydsc.reader.view.PageWidget;
import com.himoo.ydsc.ui.utils.Toast;
import com.himoo.ydsc.util.MyLogger;

/**
 * 
 * 阅读小说的界面
 */
public class ReaderActivity extends Activity {

	private PageWidget pageWidget;
	private Bitmap curBitmap, nextBitmap;
	private Canvas curCanvas, nextCanvas;
	private BookPage bookpage;

	private Chapter chapter;
	boolean isMove = false;
	private boolean isFilp = false;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		initChapter();
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int w = dm.widthPixels;
		int h = dm.heightPixels;
		curBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		nextBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);

		curCanvas = new Canvas(curBitmap);
		nextCanvas = new Canvas(nextBitmap);
		bookpage = new BookPage(this, w, h, chapter);

		bookpage.setBgBitmap(BitmapFactory.decodeResource(getResources(),
				R.drawable.book_yellow_background));

		bookpage.draw(this, curCanvas);

		pageWidget = new PageWidget(this, w, h);
		setContentView(pageWidget);
		pageWidget.setBitmaps(curBitmap, curBitmap);
		// pageWidget.setOnTouchListener(this);
		pageWidget.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent e) {
				boolean ret = false;
				if (v == pageWidget) {
					if (e.getAction() == MotionEvent.ACTION_DOWN) {
						pageWidget.abortAnimation();
						pageWidget.calcCornerXY(e.getX(), e.getY());

						bookpage.draw(ReaderActivity.this, curCanvas);
						if (pageWidget.DragToRight()) {
							if (bookpage.prePage()) {
								bookpage.draw(ReaderActivity.this, nextCanvas);
							} else {
								Toast.showShort(ReaderActivity.this, "已经是第一页了");
								return false;
							}
						} else {
							if (bookpage.nextPage()) {
								MyLogger.kLog().d("执行翻页");

								bookpage.draw(ReaderActivity.this, nextCanvas);
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
		});

	}

	/**
	 * 初始化章节
	 */
	private void initChapter() {
		Intent intent = getIntent();
		String chapterName = intent.getStringExtra("chapterName");
		String chapterUrl = intent.getStringExtra("chapterUrl");
		String bookName = intent.getStringExtra("bookName");
		String index = intent.getStringExtra("index");
		chapter = IOHelper.getChapter(index);
	}

	//
	// @Override
	// public boolean onTouch(View v, MotionEvent event) {
	// // TODO Auto-generated method stub
	// int action = event.getAction();
	//
	// switch (action) {
	// case MotionEvent.ACTION_DOWN:
	// Toast.showShort(this, "ACTION_DOWN");
	//
	// break;
	// case MotionEvent.ACTION_MOVE:
	// // Toast.showShort(WallActivity.this, "移动--------------？？？？");
	// isMove = true;
	// boolean ret = false;
	// if (v == pageWidget) {
	// if (event.getAction() == MotionEvent.ACTION_DOWN) {
	// pageWidget.abortAnimation();
	// pageWidget.calcCornerXY(event.getX(), event.getY());
	//
	// bookpage.draw(curCanvas);
	// if (pageWidget.DragToRight()) {
	// if (bookpage.prePage()) {
	// bookpage.draw(nextCanvas);
	// } else {
	// Toast.showShort(ReaderActivity.this, "已经是第一页了");
	// return false;
	// }
	// } else {
	// if (bookpage.nextPage()) {
	// MyLogger.kLog().d("执行翻页");
	// bookpage.draw(nextCanvas);
	// } else {
	// Toast.showShort(ReaderActivity.this, "已经是最后一页了");
	// return false;
	// }
	// }
	// pageWidget.setBitmaps(curBitmap, nextBitmap);
	// }
	// ret = pageWidget.doTouchEvent(event);
	// return ret;
	// }
	// return false;
	//
	// case MotionEvent.ACTION_UP:
	// if(isMove)
	// Toast.showShort(this, "滑动---》");
	// else
	// Toast.showShort(this, "点击-----》");
	// isMove = false;
	// break;
	//
	// default:
	// break;
	// }
	//
	// return true;
	// }
}
