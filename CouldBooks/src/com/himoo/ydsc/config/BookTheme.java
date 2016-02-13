package com.himoo.ydsc.config;

import java.util.ArrayList;

import com.himoo.ydsc.R;
import com.himoo.ydsc.util.SharedPreferences;

import android.graphics.Color;

/**
 * 皮肤主题配置
 * 
 */
public class BookTheme {

	/** 设置那些图片是白色的书名 */
	public static final ArrayList<String> coverErrorList = new ArrayList<String>();

	/** 五种主题皮肤 的变量 */
	public final static int RED_THEME = 1;
	public final static int BLUE_THEME = 2;
	public final static int GREEN_THEME = 3;
	public final static int YELLOW_THEME = 4;
	public final static int GRAY_THEME = 5;

	/** 主题皮肤类型 */
	public static int THEME_COLOR_TYPE = 2;

	/** 五种主题皮肤 */
	public static final int BOOK_RED = Color.parseColor("#FFff3333");
	public static final int BOOK_BLUE = Color.parseColor("#FF13a9ef");
	public static final int BOOK_GREEN = Color.parseColor("#FF2aa924");
	public static final int BOOK_YELLOW = Color.parseColor("#FFf8b500");
	public static final int BOOK_GRAY = Color.parseColor("#FF333333");
	public static final int BOOK_WHITE = Color.parseColor("#FFFFFFFF");
	public static final int BOOK_TEXT_WHITE = Color.parseColor("#FFA6A5A2");

	/** 主题颜色默认 */
	public static int THEME_COLOR = BookTheme.BOOK_GREEN;
	/** 底部导航栏文字颜色默认 */
	public static int THEME_COLOR_DEFAULT = Color.parseColor("#FF7E7D7B");
	/** 主题颜色默认 */
	public static int BUTTON_COLOR_PRESS = BookTheme.BOOK_GREEN_PRESS;
	/** 设置背景色，白天和夜间*/
	public static int BOOK_SETTING_BG = Color.parseColor("#FF5F5F5C");
	/** 设置背景色，白天和夜间*/
	public static int BOOK_SETTING_PRESS_BG = Color.parseColor("#FF7F7F7F");

	/** Button 选择器的绿色背景色 */
	public static final int BOOK_RED_PRESS = Color.parseColor("#FF1000");
	public static final int BOOK_BLUE_PRESS = Color.parseColor("#00A1BC");
	public static final int BOOK_GREEN_PRESS = Color.parseColor("#007F45");
	public static final int BOOK_YELLOW_PRESS = Color.parseColor("#F9C60E");
	public static final int BOOK_GRAY_PRESS = Color.parseColor("#342808");

	/** 　书架横着图片 */
	public static final int[] BOOK_SHELF_HDRAWABLE = {
			R.drawable.book_horzial_red, R.drawable.book_horzial_blue,
			R.drawable.book_horzial_green, R.drawable.book_horzial_yellow,
			R.drawable.book_horzial_black };

	/** 　书架竖着图片 */
	public static final int[] BOOK_SHELF_VDRAWABLE = {
			R.drawable.book_vertica_red, R.drawable.book_vertica_blue,
			R.drawable.book_vertica_green, R.drawable.book_vertica_yellow,
			R.drawable.book_vertica_black };

	/** 　封面 */
	public static final int[] coverDrawable = { R.drawable.book_face_default,
			R.drawable.no_cover, R.drawable.default_sign_free_book_cover,
			R.drawable.book_face_default };

	/** 书籍默认封面 */
	public static int BOOK_COVER = coverDrawable[1];

	/** 夜间模式的背景图片 */
	public static int BOOK_SETTING_NIGHT_DRAWABLE = R.drawable.book_setting_night;

	/** 是否需要在onResume中改变主题 */
	public static boolean isThemeChange = false;

	/** 　精选 */
	public static final int[] CHOICE_DRAWABLE = {
			R.drawable.main_bottom_choice_red,
			R.drawable.main_bottom_choice_blue,
			R.drawable.main_bottom_choice_green,
			R.drawable.main_bottom_choice_yellow,
			R.drawable.main_bottom_choice_gray };

	/** 分类 */
	public static final int[] CLASSIFY_DRAWABLE = {
			R.drawable.main_bottom_classify_red,
			R.drawable.main_bottom_classify_blue,
			R.drawable.main_bottom_classify_green,
			R.drawable.main_bottom_classify_yellow,
			R.drawable.main_bottom_classify_gray };

	/** 　搜索 */
	public static final int[] SEARCH_DRAWABLE = {
			R.drawable.mian_bottom_search_red,
			R.drawable.mian_bottom_search_blue,
			R.drawable.mian_bottom_search_green,
			R.drawable.mian_bottom_search_yellow,
			R.drawable.mian_bottom_search_gray };

	/** 　书架 */
	public static final int[] BOOKSHELF_DRAWABLE = {
			R.drawable.main_bottom_bookshelf_red,
			R.drawable.main_bottom_bookshelf_blue,
			R.drawable.main_bottom_bookshelf_green,
			R.drawable.main_bottom_bookshelf_yellow,
			R.drawable.main_bottom_bookshelf_gray };

	/** 　更多 */
	public static final int[] MORE_DRAWABLE = {
			R.drawable.mian_bottom_more_rea, R.drawable.mian_bottom_more_blue,
			R.drawable.mian_bottom_more_green,
			R.drawable.mian_bottom_more_yellow,
			R.drawable.mian_bottom_more_gray };

	/** 　底部导航栏的颜色 */
	public static int MAIN_CHOICE_DRAWABLE = CHOICE_DRAWABLE[1];
	public static int MAIN_CLASSIFY_DRAWABLE = CLASSIFY_DRAWABLE[1];
	public static int MAIN_SEARCH_DRAWABLE = SEARCH_DRAWABLE[1];
	public static int MAIN_BOOKSHELF_DRAWABLE = BOOKSHELF_DRAWABLE[1];
	public static int MAIN_MORE_DRAWABLE = MORE_DRAWABLE[1];

	/** 　书架书的排列顺序 */
	public static int BOOK_SHELF_HORIZONTAL = BOOKSHELF_DRAWABLE[1];
	public static int BOOK_SHELF_VERTICAL = MORE_DRAWABLE[1];

	/** 阅读节界面的背景 */
	public static int READBOOK_BACKGROUND = R.drawable.book_yellow_background;

	/** 阅读节界面的背景集合 */
	public static final int[] READBOOK_BACKGROUND_ID = { R.drawable.read_bg_7,
			R.drawable.read_bg_1, R.drawable.book_yellow_background,
			R.drawable.read_mode_soft_bg, R.drawable.book_yellow_background };

	/**
	 * 配置主题顏色
	 * 
	 * @param themeColor
	 */
	public static void setThemeColor(int themeColor) {

		MAIN_CHOICE_DRAWABLE = CHOICE_DRAWABLE[themeColor - 1];
		MAIN_CLASSIFY_DRAWABLE = CLASSIFY_DRAWABLE[themeColor - 1];
		MAIN_SEARCH_DRAWABLE = SEARCH_DRAWABLE[themeColor - 1];
		MAIN_BOOKSHELF_DRAWABLE = BOOKSHELF_DRAWABLE[themeColor - 1];
		MAIN_MORE_DRAWABLE = MORE_DRAWABLE[themeColor - 1];
		BOOK_SHELF_HORIZONTAL = BOOK_SHELF_HDRAWABLE[themeColor - 1];
		BOOK_SHELF_VERTICAL = BOOK_SHELF_VDRAWABLE[themeColor - 1];
		switch (themeColor) {
		case RED_THEME:
			THEME_COLOR = BookTheme.BOOK_RED;
			BUTTON_COLOR_PRESS = BOOK_RED_PRESS;

			break;

		case BLUE_THEME:
			THEME_COLOR = BookTheme.BOOK_BLUE;
			BUTTON_COLOR_PRESS = BOOK_BLUE_PRESS;
			MAIN_CLASSIFY_DRAWABLE = CLASSIFY_DRAWABLE[1];
			break;

		case GREEN_THEME:
			THEME_COLOR = BookTheme.BOOK_GREEN;
			BUTTON_COLOR_PRESS = BOOK_GREEN_PRESS;
			break;

		case YELLOW_THEME:
			THEME_COLOR = BookTheme.BOOK_YELLOW;
			BUTTON_COLOR_PRESS = BOOK_YELLOW_PRESS;
			break;

		case GRAY_THEME:
			THEME_COLOR = BookTheme.BOOK_GRAY;
			BUTTON_COLOR_PRESS = BOOK_GRAY_PRESS;
			break;
		}
	}

	/**
	 * 设置默认封面
	 * 
	 * @param bookCover
	 */
	public static void setBookCover(int bookCover) {

		switch (bookCover) {
		case 0:
			BOOK_COVER = coverDrawable[0];
			break;
		case 1:
			BOOK_COVER = coverDrawable[1];
			break;
		case 2:
			BOOK_COVER = coverDrawable[2];
			break;
		case 3:
			BOOK_COVER = coverDrawable[3];
			break;

		default:
			break;
		}

	}

	/**
	 * 设置是否要通知主题发生改变
	 * 
	 * @param isChange
	 */
	public static void setChangeTheme(boolean isChange) {
		isThemeChange = isChange;
	}

	/**
	 * 初始化屏蔽图片为空白的书名
	 */
	public static ArrayList<String> initList() {
		coverErrorList.add("大影帝");
		coverErrorList.add("楚王妃");
		coverErrorList.add("鬼藏人");
		coverErrorList.add("状元辣妻");
		coverErrorList.add("乡土之王");
		coverErrorList.add("深渊主宰");
		coverErrorList.add("只求安心");
		coverErrorList.add("武祖血帝");
		coverErrorList.add("天之武神");
		coverErrorList.add("天之神武");
		coverErrorList.add("绝脉武神");
		coverErrorList.add("斗战西游");
		coverErrorList.add("北宋闲王");
		coverErrorList.add("得分之王");
		coverErrorList.add("御用特工");
		coverErrorList.add("暗黑大宋");
		coverErrorList.add("重生尹志平");
		coverErrorList.add("超级电脑系统");
		coverErrorList.add("独家披露");
		coverErrorList.add("天才农家妻");
		coverErrorList.add("容华盛景");
		coverErrorList.add("孽债2");
		coverErrorList.add("一品夫人");
		coverErrorList.add("大侠林平之");
		coverErrorList.add("重生之废后夺权");
		coverErrorList.add("王跃文作品精选");
		coverErrorList.add("千古传奇之大宋奇侠");
		coverErrorList.add("又厚又黑红楼梦");
		coverErrorList.add("单身女孩");
		coverErrorList.add("最牛国医妃");
		coverErrorList.add("至尊透视眼");
		coverErrorList.add("穿越神戒");
		coverErrorList.add("最宠老婆大人");
		coverErrorList.add("契妻只欢不爱");
		coverErrorList.add("闪婚老公不靠谱");
		coverErrorList.add("穿越古代江湖行");
		coverErrorList.add("最强修真高手");
		coverErrorList.add("修仙之女主难为");
		coverErrorList.add("花都公子");
		coverErrorList.add("唐末传奇");
		coverErrorList.add("姑苏慕容世家");
		coverErrorList.add("重生之先婚再爱");
		coverErrorList.add("回到三国变成蟒");
		coverErrorList.add("最后一个阴阳师");
		coverErrorList.add("史上最强内线");
		coverErrorList.add("爱劫难桃总裁独家盛宠");
		coverErrorList.add("龙帝的萌狐妖妻");
		coverErrorList.add("御兽女少主逆世小王妃");
		coverErrorList.add("英雄联盟之点券召唤师");
		coverErrorList.add("废材九小姐毒医邪飞");
		coverErrorList.add("无限之配角的逆袭");
		coverErrorList.add("绯闻总裁老婆复婚吧");
		coverErrorList.add("全帝国都知道将军要离..");

		return coverErrorList;
	}

	/**
	 * 判断是否包含该书名
	 * 
	 * @param bookName
	 * @return
	 */
	public static boolean isContainBookName(String bookName) {
		if (coverErrorList != null && coverErrorList.isEmpty())
			initList();
		return coverErrorList.contains(bookName);

	}

	/**
	 * 设置阅读界面的背景
	 */
	public static void setReaderBookTextBg(int index) {
		SharedPreferences.getInstance().putInt(SpConstant.BOOK_SETTING_TEXT_BG,
				index);
		switch (index) {
		case 1:
			READBOOK_BACKGROUND = READBOOK_BACKGROUND_ID[0]; 
			break;
		case 2:
			READBOOK_BACKGROUND = READBOOK_BACKGROUND_ID[1];
			break;
		case 3:
			READBOOK_BACKGROUND = READBOOK_BACKGROUND_ID[2];
			break;
		case 4:
			READBOOK_BACKGROUND = READBOOK_BACKGROUND_ID[3];
			break;
		case 5:
			READBOOK_BACKGROUND = READBOOK_BACKGROUND_ID[4];
			break;

		default:
			break;
		}

	}

}
