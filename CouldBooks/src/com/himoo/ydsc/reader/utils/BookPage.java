package com.himoo.ydsc.reader.utils;

import java.util.Vector;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Typeface;
import android.text.format.Time;
import android.util.Log;

import com.himoo.ydsc.R;
import com.himoo.ydsc.config.BookTheme;
import com.himoo.ydsc.config.SpConstant;
import com.himoo.ydsc.reader.bean.Chapter;
import com.himoo.ydsc.util.DeviceUtil;
import com.himoo.ydsc.util.JccUtil;
import com.himoo.ydsc.util.SharedPreferences;
import com.himoo.ydsc.util.TimestampUtils;

/**
 * 这个类的目的是为在看书翻页时，需要进行的动作提供接口。
 * 包括翻向下一页，翻向上一页。在翻到每章最后一页时，如果后面还有章节就继续翻向下一章节，没有就向用户显示已读完。
 * 在翻向上一章节时，如果前面还有章节，就翻到上一章节，没有就向用户显示，这已经是第一章节。
 * 
 * 在直觉上认为这个应该只设置成一个接口，因为只需向视图层提供动作接口，也就是本类应属于模型层。则其设置为一个借口可能也合适。
 * 但是如果设置成一个接口，那么接口的实现类，有多个都要保存的数据。那么为了代码重用，抽象类可能比接口更加合适。 上面是个人分析，可能不是很合适。
 * 
 */
public class BookPage {
	/** 表示打开的1是 自己服务器上的书 2是百度的书还是 */
	private int bookType = 2;
	private int currentSeekBarPos = 0;
	// configuration information
	private int screenWidth; // 屏幕宽度
	private int screenHeight; // 屏幕高度
	private int fontSize; // 字体大小
	private int lineHgight; // 每行的高度
	private int marginWidth = 50; // 左右与边缘的距离
	private int marginHeight = 50; // 上下与边缘的距离
	private int textColor = Color.BLACK;; // 字体颜色
	private Bitmap bgBitmap; // 背景图片
	private int bgColor; // 背景颜色

	private Paint paint;
	private Paint paintBottom;
	private int visibleWidth; // 屏幕中可显示文本的宽度
	private int visibleHeight;
	public Chapter chapter; // 需要处理的章节对象
	private Vector<String> linesVe; // 将章节內容分成行，并将每页按行存储到vector对象中
	private int lineCount; // 一个章节在当前配置下一共有多少行

	private String content;
	private int chapterLen; // 章节的长度
	// private int curCharPos; // 当前字符在章节中所在位置
	public int charBegin; // 每一页第一个字符在章节中的位置
	public int charEnd; // 每一页最后一个字符在章节中的位置
	public boolean isfirstPage;
	public boolean islastPage;

	public Vector<Vector<String>> pagesVe;
	public int pageNum;

	private int textType = 1;
	private Context mContext;
	private Typeface typeface;
	private int typefaceIndex;
	private int mogoAdHeight = 0;

	/**
	 * 在新建一个BookPage对象时，需要向其提供数据，以支持屏幕翻页功能。
	 * 
	 * @param screenWidth
	 *            屏幕宽度，用来计算每行显示多少字
	 * @param screenHeight
	 *            屏幕高度，用来计算每页显示多少行
	 * @param chapter
	 *            章节对象
	 */
	public BookPage(Context context, int screenWidth, int screenHeight,
			Chapter chapter, int currentPage, int pageCount, int bookType,
			boolean isNeedMogoAd) {
		mContext = context;
		this.bookType = bookType;
		this.screenHeight = screenHeight;
		this.screenWidth = screenWidth;
		if (isNeedMogoAd)
			mogoAdHeight = (int) mContext.getResources().getDimension(
					R.dimen.mogoAd_height);

		// IOHelper.setOnChapterListener(this);
		this.chapter = chapter;
		initSetting();
		setTextTypeChildren(true);
		setTextLineSpace(true);
		init(context);
		if (currentPage != -1 && pageCount != -1)
			pageNum = (currentPage * (pagesVe.size())) / pageCount;
	}

	public void initChapter(Chapter chapter, int currentPage, int pageCount,
			int bookType) {
		this.bookType = bookType;
		this.chapter = chapter;
		initSetting();
		setTextTypeChildren(true);
		setTextLineSpace(true);
		init(mContext);
		if (currentPage != -1 && pageCount != -1)
			pageNum = ((currentPage * (pagesVe.size())) / pageCount) + 1;

	}

	/**
	 * 联网加载章节
	 * 
	 * @param context
	 * @param screenWidth
	 * @param screenHeight
	 */
	public BookPage(Context context, int screenWidth, int screenHeight,
			boolean isNeedMogoAd) {
		mContext = context;
		this.screenHeight = screenHeight;
		this.screenWidth = screenWidth;
		if (isNeedMogoAd)
			mogoAdHeight = (int) mContext.getResources().getDimension(
					R.dimen.mogoAd_height);
	}

	/**
	 * 初始化已经设置好
	 */
	private void initSetting() {
		textType = SharedPreferences.getInstance().getInt(
				SpConstant.BOOK_SETTING_TEXT_TYPE, 1);
	}

	/**
	 * 初始最好按照定义变量的顺序来初始化，统一。在将来需要修改某个变量的时候，容易找到。 对代码维护应该也很有用吧。
	 */
	protected void init(Context context) {
		// bgBitmap = null;
		boolean isNightMode = SharedPreferences.getInstance().getBoolean(
				SpConstant.BOOK_SETTING_NIGHT_MODE, false);
		boolean isAutoNightMode = SharedPreferences.getInstance().getBoolean(
				SpConstant.BOOK_SETTING_AUTO_NIGHT, false);
		textColor = isNightMode ? BookTheme.BOOK_TEXT_WHITE : Color.BLACK;
		if (!isNightMode && isAutoNightMode) {
			int hour = TimestampUtils.getCurrentHour();
			if (hour >= 19 || hour <= 7) {
				textColor = BookTheme.BOOK_TEXT_WHITE;
			}
		}

		bgColor = 0xffff9e85;
		if (chapter == null)
			return;
		content = textType == 1 ? JccUtil.changeToSimplified(chapter
				.getContent()) : JccUtil.changeToTraditional(chapter
				.getContent());
		if (typefaceIndex == 3)
			content = content.replaceAll("        ", "    ");
		chapterLen = content.length();
		// curCharPos = 0;
		charBegin = 0;
		charEnd = 0;
		marginWidth = DeviceUtil.dip2px(context, 20);
		marginHeight = DeviceUtil.dip2px(context, 20);
		fontSize = SharedPreferences.getInstance().getInt(
				SpConstant.BOOK_SETTING_TEXT_SIZE,
				DeviceUtil.dip2px(context, 22));
		// lineHgight = fontSize + DeviceUtil.dip2px(context, 4);
		linesVe = new Vector<String>();

		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setTextAlign(Align.LEFT);
		paint.setTextSize(fontSize);

		if (typeface != null)
			paint.setTypeface(typeface);

		paintBottom = new Paint(Paint.ANTI_ALIAS_FLAG);
		paintBottom.setTextAlign(Align.LEFT);
		paintBottom.setTextSize(fontSize / 2);
		if (SharedPreferences.getInstance().getBoolean(
				SpConstant.BOOK_AUTO_COLOR, false)) {
			int color = SharedPreferences.getInstance().getInt(
					SpConstant.BOOK_AUTO_COLOR_TEXT, Color.BLACK);
			paint.setColor(color);
			paintBottom.setColor(color);
		} else {
			paint.setColor(textColor);
			paintBottom.setColor(textColor);
		}

		if (typeface != null)
			paintBottom.setTypeface(typeface);

		visibleWidth = screenWidth - marginWidth * 2;
		visibleHeight = screenHeight - marginHeight * 2 - mogoAdHeight;
		lineCount = visibleHeight / lineHgight - 2;
		isfirstPage = true;
		islastPage = false;
		pagesVe = new Vector<Vector<String>>();
		pageNum = -1;

		slicePage();
	}

	/**
	 * 获取当前页及后后面页数的内容
	 * 
	 * @return
	 */
	public String getCurPage() {
		StringBuilder sb = new StringBuilder();
		for (int i = pageNum; i < pagesVe.size(); i++) {
			sb.append(pagesVe.get(i).toString());

		}
		return sb.toString();
	}

	public Vector<String> getNextPage() {
		return pagesVe.get(++pageNum);
	}

	/**
	 * 将每章节的内容分页
	 */
	protected void slicePage() {
		try {

			pagesVe.clear();
			int curPos = 0;
			while (curPos < chapterLen) {
				Vector<String> lines = new Vector<String>();
				charBegin = curPos;
				while (lines.size() < lineCount && curPos < chapterLen) {
					int i = content.indexOf("\n", curPos);
					String paragraphStr = content.substring(curPos, i);
					// curCharPos += i;
					if (curPos == i)
						lines.add("");

					while (paragraphStr.length() > 0) {
						int horSize = paint.breakText(paragraphStr, true,
								visibleWidth, null);
						lines.add(paragraphStr.substring(0, horSize));
						paragraphStr = paragraphStr.substring(horSize);
						curPos += horSize;
						if (lines.size() > lineCount)
							break;
					}
					// 如果是把一整段读取完的话，需要给当前位置加1
					if (paragraphStr.length() == 0)
						curPos += "\n".length();
				}
				pagesVe.add(lines);
			}
		} catch (Exception e) {
			// TODO: handle exception
			Log.i("msg", e.getMessage());
		}

	}

	/**
	 * 翻到下一页
	 */
	public boolean nextPage() {
		if (isLastPage()) {
			if (!nextChapter()) // 如果已经到本书末尾，那么不能继续执行翻页代码
				return false;
		}
		/*
		 * Vector<String> lines = new Vector<String>(); charBegin = charEnd;
		 * while (lines.size() < lineCount && charEnd < chapterLen) { int i =
		 * content.indexOf("\n", charEnd); String paragraphStr =
		 * content.substring(charEnd, i); // curCharPos += i; if (charEnd == i)
		 * lines.add("");
		 * 
		 * while (paragraphStr.length() > 0) { int horSize =
		 * paint.breakText(paragraphStr, true, visibleWidth, null);
		 * lines.add(paragraphStr.substring(0, horSize)); paragraphStr =
		 * paragraphStr.substring(horSize); charEnd += horSize; if (lines.size()
		 * > lineCount) break; } // 如果是把一整段读取完的话，需要给当前位置加1 if
		 * (paragraphStr.length() == 0) charEnd += "\n".length(); } linesVe =
		 * lines;
		 */
		linesVe = pagesVe.get(++pageNum);
		return true;
	}

	/**
	 * 翻到上一页
	 */
	public boolean prePage() {
		if (isFirstPage()) {
			if (!preChapter()) // 如果已经到本书第一章，就不能继续执行翻页代码
				return false;
		}
		/*
		 * Vector<String> lines = new Vector<String>(); String backStr =
		 * content.substring(0, charBegin); charEnd = charBegin;
		 * 
		 * while (lines.size() < lineCount && charBegin > 0) { int i =
		 * backStr.lastIndexOf("\n"); if(i == -1) i = 0; String paragraphStr =
		 * backStr.substring(i, charBegin); Vector<String> vet = new
		 * Vector<String>(lines);
		 * 
		 * // if(charBegin == i)lines.add("");
		 * 
		 * while (paragraphStr.length() > 0) { int horSize =
		 * paint.breakText(paragraphStr, true, visibleWidth, null);
		 * lines.add(paragraphStr.substring(0, horSize)); paragraphStr =
		 * paragraphStr.substring(horSize); charBegin -= horSize; if
		 * (lines.size() > lineCount) break; }
		 * 
		 * backStr = content.substring(0, charBegin); int j = -1; for (String
		 * line : vet) lines.insertElementAt(line, ++j); } linesVe = lines;
		 */
		linesVe = pagesVe.get(--pageNum);
		return true;
	}

	/**
	 * 跳到下一章，若返回值为false，则当前章节已经为最后一章
	 */
	public boolean nextChapter() {
		if (chapter == null)
			return false;
		String index = chapter.getIndex();
		int position = chapter.getPosition() + 1;
		int jumpType = chapter.getJumpType();
		Chapter tempChapter = IOHelper.getChapter(jumpType, index, position,
				bookType);

		if (tempChapter == null)
			return false;
		chapter = tempChapter;
		content = textType == 1 ? JccUtil.changeToSimplified(chapter
				.getContent()) : JccUtil.changeToTraditional(chapter
				.getContent());
		if (typefaceIndex == 3)
			content = content.replaceAll("        ", "    ");
		chapterLen = content.length();
		// curCharPos = 0;
		charBegin = 0;
		charEnd = 0;
		slicePage();
		pageNum = -1;
		return true;
	}

	/**
	 * 跳到下一章，若返回值为false，则当前章节已经为最后一章
	 */
	public void currentChapter(Chapter mchapter) {
		chapter = mchapter;
		// String index = chapter.getIndex();
		// int position = chapter.getPosition();
		// int jumpType = chapter.getJumpType();
		// // Chapter tempChapter = IOHelper.getChapter(jumpType, index,
		// position,
		// // bookType);
		// chapter = tempChapter;
		content = textType == 1 ? JccUtil.changeToSimplified(chapter
				.getContent()) : JccUtil.changeToTraditional(chapter
				.getContent());
		if (typefaceIndex == 3)
			content = content.replaceAll("        ", "    ");
		chapterLen = content.length();
		// curCharPos = 0;
		charBegin = 0;
		charEnd = 0;
		slicePage();
		pageNum = -1;
	}

	/**
	 * 跳到下一章，若返回值为false，则当前章节已经为最后一章
	 */
	public void currentAsyncChapter(Chapter mchapter) {
		chapter = mchapter;
		content = textType == 1 ? JccUtil.changeToSimplified(chapter
				.getContent()) : JccUtil.changeToTraditional(chapter
				.getContent());
		if (typefaceIndex == 3)
			content = content.replaceAll("        ", "    ");
		chapterLen = content.length();
		// curCharPos = 0;
		charBegin = 0;
		charEnd = 0;
		slicePage();
		pageNum = -1;
	}

	/**
	 * 跳到上一章,若返回值为false，则当前章节已经为第一章
	 */
	public boolean preChapter() {
		if (chapter == null)
			return false;
		String index = chapter.getIndex();
		int position = chapter.getPosition();
		int jumpType = chapter.getJumpType();
		Chapter tempChapter = IOHelper.getChapter(jumpType, index,
				position - 1, bookType);
		if (tempChapter == null)
			return false;
		chapter = tempChapter;
		content = textType == 1 ? JccUtil.changeToSimplified(chapter
				.getContent()) : JccUtil.changeToTraditional(chapter
				.getContent());
		if (typefaceIndex == 3)
			content = content.replaceAll("        ", "    ");
		chapterLen = content.length();
		// curCharPos = chapterLen;
		charBegin = chapterLen;
		charEnd = chapterLen;
		slicePage();
		pageNum = pagesVe.size();

		return true;
	}

	public boolean isFirstPage() {
		if (pageNum <= 0)
			return true;
		return false;
	}

	public boolean isLastPage() {
		if (pageNum >= pagesVe.size() - 1)
			return true;
		return false;
	}

	public void draw(Canvas c) {

		if (SharedPreferences.getInstance().getBoolean(
				SpConstant.BOOK_AUTO_COLOR, false)) {
			c.drawColor(SharedPreferences.getInstance().getInt(
					SpConstant.BOOK_AUTO_COLOR_BG, Color.CYAN));
		} else {
			c.drawBitmap(bgBitmap, 0, 0, null);
		}
	}

	/**
	 * 绘制每页的你内容
	 * 
	 * @param context
	 * @param c
	 */
	public void draw(Context context, Canvas c) {
		if (linesVe.size() == 0)
			nextPage();
		if (linesVe.size() > 0) {
			if (SharedPreferences.getInstance().getBoolean(
					SpConstant.BOOK_AUTO_COLOR, false)) {
				c.drawColor(SharedPreferences.getInstance().getInt(
						SpConstant.BOOK_AUTO_COLOR_BG, Color.CYAN));
			} else {
				// if (bgBitmap == null)
				// c.drawColor(bgColor);
				// else
				c.drawBitmap(bgBitmap, 0, 0, null);
			}

			int y = marginHeight;
			for (String line : linesVe) {
				y += lineHgight;
				c.drawText(line, marginWidth, y, paint);
			}
		}
		String percetStr = "第" + (pageNum + 1) + "/" + pagesVe.size() + "页";
		Time time = new Time();
		time.setToNow();
		String timeStr;
		if (time.minute < 10)
			timeStr = "" + time.hour + ((typefaceIndex == 3) ? ":0" : " : 0")
					+ time.minute;
		else
			timeStr = "" + time.hour + ((typefaceIndex == 3) ? " :" : " : ")
					+ time.minute;

		int pSWidth = (int) paintBottom.measureText("第12/10页")
				+ DeviceUtil.dip2px(context, 3);
		int titWidth = (int) paintBottom.measureText(chapter.getBookName());

		c.drawText(timeStr, marginWidth / 2, screenHeight - mogoAdHeight - 5,
				paintBottom);
		c.drawText(chapter.getBookName(), screenWidth / 2 - titWidth / 2,
				screenHeight - mogoAdHeight - DeviceUtil.dip2px(context, 4),
				paintBottom);
		c.drawText(chapter.getChapterName(), DeviceUtil.dip2px(context, 20),
				DeviceUtil.dip2px(context, 15), paintBottom);
		c.drawText(percetStr, screenWidth - pSWidth, screenHeight
				- mogoAdHeight - DeviceUtil.dip2px(context, 4), paintBottom);
	}

	/**
	 * 设置小说的阅读背景图片
	 * 
	 * @param context
	 * @param resId
	 */
	public void setBgBitmap(Context context, int resId) {
		if (bgBitmap != null && !bgBitmap.isRecycled())
			bgBitmap.recycle();
		bgBitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeStream(context
				.getResources().openRawResource(resId)), screenWidth,
				screenHeight, true);
	}

	/**
	 * 设置字体大小
	 * 
	 * @param textSize
	 */
	public void setTextSzie(int textSize) {
		fontSize = textSize;
		init(mContext);
	}

	/**
	 * 设置简繁体
	 */
	public void setTextType() {
		textType = SharedPreferences.getInstance().getInt(
				SpConstant.BOOK_SETTING_TEXT_TYPE, 1);
		init(mContext);
	}

	/**
	 * 设置字体间的行间距
	 */
	public void setTextLineSpace(boolean isFirstInit) {
		int index = SharedPreferences.getInstance().getInt(
				SpConstant.BOOK_SETTING_TEXT_LINE, 1);
		fontSize = SharedPreferences.getInstance().getInt(
				SpConstant.BOOK_SETTING_TEXT_SIZE,
				DeviceUtil.dip2px(mContext, 22));
		switch (index) {
		case 1:
			lineHgight = fontSize + DeviceUtil.dip2px(mContext, 6);
			break;
		case 2:
			lineHgight = fontSize + DeviceUtil.dip2px(mContext, 10);
			break;
		case 3:
			lineHgight = fontSize + DeviceUtil.dip2px(mContext, 20);
			break;
		case 4:
			lineHgight = fontSize + DeviceUtil.dip2px(mContext, 30);
			break;

		default:
			break;
		}
		if (!isFirstInit)
			init(mContext);

	}

	/**
	 * 设置楷体还是宋体
	 */
	public void setTextTypeChildren(boolean isFirstInit) {
		typefaceIndex = SharedPreferences.getInstance().getInt(
				SpConstant.BOOK_SETTING_TEXT_CHILDREN_TYPE, 3);
		switch (typefaceIndex) {
		case 1:
			typeface = null;
			break;
		case 2:
			typeface = Typeface.createFromAsset(mContext.getResources()
					.getAssets(), "texttype/jian.ttf");
			break;
		case 3:
			typeface = Typeface.createFromAsset(mContext.getResources()
					.getAssets(), "texttype/kai.ttf");
			break;
		case 4:
			typeface = Typeface.createFromAsset(mContext.getResources()
					.getAssets(), "texttype/xkai.ttf");
			break;
		case 5:
			typeface = Typeface.createFromAsset(mContext.getResources()
					.getAssets(), "texttype/wei.ttf");
			break;

		default:
			break;
		}
		if (!isFirstInit)
			init(mContext);
	}

	/**
	 * 下一章
	 */
	public void initNextChapter() {
		nextChapter();
		init(mContext);

	}

	/**
	 * 上一章
	 */
	public void initPreChapter() {
		preChapter();
		init(mContext);

	}

	public void initSeekBarChapter(int position) {
		chapter = IOHelper.getChapter(2, "1", position, bookType);
		if (currentSeekBarPos < position) {
			nextChapter();
		} else {
			preChapter();
		}
		init(mContext);
		currentSeekBarPos = position;
	}

	/**
	 * 设置夜间模式
	 */
	public void setNightMode() {
		if (SharedPreferences.getInstance().getBoolean(
				SpConstant.BOOK_SETTING_NIGHT_MODE, false)) {
			setBgBitmap(mContext, BookTheme.BOOK_SETTING_NIGHT_DRAWABLE);
			changeTextColor(BookTheme.BOOK_TEXT_WHITE);
		} else {
			setBgBitmap(mContext, BookTheme.READBOOK_BACKGROUND);
			changeTextColor(Color.BLACK);
		}

	}

	/**
	 * 设置颜色背景 Color
	 * 
	 * @param color
	 */
	public void setBgColor(int color) {
		this.bgColor = color;
		if (bgBitmap != null && !bgBitmap.isRecycled()) {
			bgBitmap.recycle();
			bgBitmap = null;
		}

	}

	/**
	 * 改变字体的颜色
	 */
	public void changeTextColor(int color) {
		// if (!SharedPreferences.getInstance().getBoolean(
		// SpConstant.BOOK_AUTO_COLOR, false)) {
		textColor = color;
		paint.setColor(textColor);
		paintBottom.setColor(textColor);
		// }
	}

	public void destory() {
		if (this.bgBitmap != null) {
			bgBitmap.recycle();
			bgBitmap = null;
		}

		if (pagesVe != null) {
			pagesVe.clear();
			pagesVe = null;
		}
		if (linesVe != null) {
			linesVe.clear();
			linesVe = null;
		}

	}

	/**
	 * 设置当前页
	 */
	public void setCurrentPageNum() {
		this.pageNum = pagesVe.size() - 1;
	}

	// @Override
	// public void onGetChapterPre() {
	// // TODO Auto-generated method stub
	//
	// }
	//
	// @Override
	// public boolean onGetChapterSuccess(Chapter mChapter) {
	// // TODO Auto-generated method stub
	// Chapter tempChapter = mChapter;
	// if (tempChapter == null)
	// return false;
	// chapter = tempChapter;
	// content = textType == 1 ? JccUtil.changeToSimplified(chapter
	// .getContent()) : JccUtil.changeToTraditional(chapter
	// .getContent());
	// if (typefaceIndex == 3)
	// content = content.replaceAll("        ", "    ");
	// chapterLen = content.length();
	// // curCharPos = 0;
	// charBegin = 0;
	// charEnd = 0;
	// slicePage();
	// pageNum = -1;
	// return true;
	// }
	//
	// @Override
	// public void onGetChapterFailure() {
	// // TODO Auto-generated method stub
	//
	// }

	/**
	 * 更新当前的页数
	 */
	public void updateCurrentpageNum(int currentPage, int pageCount) {
		pageNum = ((currentPage * (pagesVe.size())) / pageCount)-1;
	}

}
