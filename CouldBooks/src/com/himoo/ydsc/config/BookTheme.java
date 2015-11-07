package com.himoo.ydsc.config;

import android.content.Context;
import android.graphics.Color;

/**
 * 皮肤主题配置
 * 
 */
public class BookTheme {

	public Context context = null;
	// 默认色
	public static int backGroundColor = BookTheme.color;
	
	public final static int REDTHEME = 1;
	public final static int BLUETHEME = 2;
	public final static int ORANGETHEME = 3;
	public final static int GREENTHEME = 4;
	public final static int WHITETHEME = 5;
	public static int themeColor = 3;
	
	/** 绿色背景色*/
	public static final int BOOK_GREEN = Color.parseColor("#FF00B64F");
	
	public static final int BOOK_GREEN_PRESS = Color.parseColor("#007F45");
	
	
	
	

	public final static int BLACKTRAN = Color.parseColor("#cc000000");
	public final static int BLUETEXTCOLOR = Color.BLUE;
	public final static int REDTEXTCOLOR = Color.RED;

	public final static int WHITETEXTCOLOR = Color.WHITE;
	public final static int BLACKTEXTCOLOR = Color.BLACK;

	private final static int GRAYBGCOLOR = Color.parseColor("#cdcbcc");
	public final static int GRAY2BGCOLOR = Color.parseColor("#f0eff4");
	public final static int GRAY3BGCOLOR = Color.parseColor("#f9f9f9");

	private final static int GRAY2TEXTCOLOR = Color.parseColor("#484747");

	public final static int TITLEBGCOLOR = Color.parseColor("#fafafa");

	public final static int GRAYLINECOLOR = Color.parseColor("#cfced3");

	private final static int BLUEHELPBGCOLOR = Color.parseColor("#0180a4");
	private final static int BLUEHELPPPRESSBGCOLOR = Color
			.parseColor("#01bbf0");

	private final static int GREENHELPBGCOLOR = Color.parseColor("#4a9e08");
	private final static int GREENHELPPPRESSBGCOLOR = Color
			.parseColor("#6de113");

	private final static int ORANGEHELPBGCOLOR = Color.parseColor("#d13e02");
	private final static int ORANGEHELPPRESSBGCOLOR = Color
			.parseColor("#ff6d01");

	private final static int REDHELPBGCOLOR = Color.parseColor("#b9010d");
	private final static int REDHELPPPRESSBGCOLOR = Color.parseColor("#ff0000");

	private final static int WHITEHELPBGCOLOR = Color.parseColor("#f9f9f9");
	private final static int WHITEHELPPPRESSBGCOLOR = Color
			.parseColor("#ff0000");

	public static boolean isStroke = false;

	// 主题颜色
	public static int color = Color.parseColor("#ff4a01");
	// 总额文字颜色
	public static int coinColor = WHITETEXTCOLOR;
	// 帮助背景颜色
	public static int helpBgColor = ORANGEHELPBGCOLOR;
	// 帮助按下背景颜色
	public static int helpPressBgColor = ORANGEHELPPRESSBGCOLOR;
	// 彩球文字颜色
	public static int ballColor = WHITETEXTCOLOR;
	// 默认文字颜色
	public static int defaultTextcolor = WHITETEXTCOLOR;

	// selectView文字颜色
	public static int selectViewBgColor = WHITETEXTCOLOR;

	// hint文字颜色（浅色）
	public static int hintTextcolor = GRAYBGCOLOR;

	// 被选中的文字颜色
	public static int reblindTextColor = WHITETEXTCOLOR;

	// 被选中的文字颜色
	public static int selectedColor = color;

	// blindPhone的文字颜色
	public static int blindPhoneColor = BLACKTEXTCOLOR;
	// 未被选中的文字颜色
	public static int unSelectedColor = BLACKTEXTCOLOR;
	// 标题栏中的文字颜色
	public static int titleColor = WHITETEXTCOLOR;

	// 线颜色
	public static int lineColor = WHITETEXTCOLOR;

	// 标题栏中背景颜色
	public static int titleBgColor = WHITETEXTCOLOR;

	// 背景色
	public static int backgroundColor = WHITETEXTCOLOR;
	// 选区颜色
	public static int selectionColor = GRAYBGCOLOR;

	// enable颜色
	public static int unableColor = GRAYBGCOLOR;

	// 关键字颜色
	public static int keyWordColor = REDTEXTCOLOR;

	// 关键字颜色
	public static int confirmColor = WHITETEXTCOLOR;

	// 使用颜色色值
	public final static int COMMONBTNCOLOR = Color.parseColor("#02b936");
	public final static int COMMONBTNCOLORPRESSED = Color.parseColor("#03d94e");

	// 橙色配色方案色值
	private final static int ORANGEBTNCOLOR = Color.parseColor("#ff4a00");
	private final static int ORANGEBTNCOLORPRESSED = Color
			.parseColor("#ff4a00");

	// 红色配色方案
	private final static int REDBTNCOLOR = Color.parseColor("#e70917");
	private final static int REDBTNCOLORPRESSED = Color.parseColor("#ff0000");

	// 蓝色配色方案
	private final static int BLUEBTNCOLOR = Color.parseColor("#0ba4d0");
	private final static int BLUEBTNCOLORPRESSED = Color.parseColor("#01bbf0");

	// 绿色配色方案
	private final static int GREENBTNCOLOR = Color.parseColor("#74c831");
	private final static int GREENBTNCOLORPRESSED = Color.parseColor("#6de113");

	// 白色配色方案
	private final static int WHITEBTNCOLOR = Color.parseColor("#f9f9f9");
	// not used ?
	// private final static int WHITEBTNCOLORPRESSED =
	// Color.parseColor("#e33f40");

	public static int POSITIVEBTNCOLOR = ORANGEBTNCOLOR;
	public static int POSITIVEBTNCOLORPRESSED = ORANGEBTNCOLORPRESSED;

	public static int NEGATIVEBTNCOLOR = COMMONBTNCOLOR;
	public static int NEGATIVEBTNCOLORPRESSED = COMMONBTNCOLORPRESSED;

	public static void setThemeColor(int themeColor) {
		BookTheme.themeColor = themeColor;
		switch (themeColor) {
		case REDTHEME:
			isStroke = false;
			reblindTextColor = WHITETEXTCOLOR;
			helpBgColor = REDHELPBGCOLOR;
			helpPressBgColor = REDHELPPPRESSBGCOLOR;
			hintTextcolor = GRAY2TEXTCOLOR;
			ballColor = WHITETEXTCOLOR;
			defaultTextcolor = WHITETEXTCOLOR;
			keyWordColor = REDTEXTCOLOR;
			backgroundColor = WHITETEXTCOLOR;
			selectionColor = GRAYBGCOLOR;
			unableColor = GRAYBGCOLOR;
			color = REDBTNCOLOR;
			backGroundColor = color;
			selectedColor = color;
			coinColor = color;
			lineColor = color;
			blindPhoneColor = BLACKTEXTCOLOR;
			unSelectedColor = BLACKTEXTCOLOR;
			confirmColor = color;
			selectViewBgColor = color;
			titleBgColor = color;
			titleColor = WHITETEXTCOLOR;
			POSITIVEBTNCOLOR = REDBTNCOLOR;
			POSITIVEBTNCOLORPRESSED = REDBTNCOLORPRESSED;
			NEGATIVEBTNCOLOR = COMMONBTNCOLOR;
			NEGATIVEBTNCOLORPRESSED = COMMONBTNCOLORPRESSED;
			break;
		case BLUETHEME:
			isStroke = false;
			reblindTextColor = WHITETEXTCOLOR;
			helpBgColor = BLUEHELPBGCOLOR;
			helpPressBgColor = BLUEHELPPPRESSBGCOLOR;
			hintTextcolor = GRAY2TEXTCOLOR;
			ballColor = WHITETEXTCOLOR;
			defaultTextcolor = WHITETEXTCOLOR;
			keyWordColor = BLUEBTNCOLOR;
			backgroundColor = WHITETEXTCOLOR;
			unableColor = GRAYBGCOLOR;
			selectionColor = GRAYBGCOLOR;
			color = BLUEBTNCOLOR;
			selectedColor = color;
			backGroundColor = color;
			confirmColor = color;
			lineColor = color;
			coinColor = color;
			selectViewBgColor = color;
			titleBgColor = color;
			blindPhoneColor = BLACKTEXTCOLOR;
			unSelectedColor = BLACKTEXTCOLOR;
			titleColor = WHITETEXTCOLOR;
			POSITIVEBTNCOLOR = BLUEBTNCOLOR;
			POSITIVEBTNCOLORPRESSED = BLUEBTNCOLORPRESSED;
			NEGATIVEBTNCOLOR = COMMONBTNCOLOR;
			NEGATIVEBTNCOLORPRESSED = COMMONBTNCOLORPRESSED;
			break;
		case ORANGETHEME:
			isStroke = false;
			reblindTextColor = WHITETEXTCOLOR;
			helpBgColor = ORANGEHELPBGCOLOR;
			helpPressBgColor = ORANGEHELPPRESSBGCOLOR;
			hintTextcolor = GRAY2TEXTCOLOR;
			ballColor = WHITETEXTCOLOR;
			defaultTextcolor = WHITETEXTCOLOR;
			keyWordColor = ORANGEBTNCOLOR;
			backgroundColor = WHITETEXTCOLOR;
			unableColor = GRAYBGCOLOR;
			selectionColor = GRAYBGCOLOR;
			color = ORANGEBTNCOLOR;
			selectedColor = color;
			backGroundColor = color;
			confirmColor = color;
			lineColor = color;
			coinColor = color;
			selectViewBgColor = color;
			titleBgColor = color;
			blindPhoneColor = BLACKTEXTCOLOR;
			unSelectedColor = BLACKTEXTCOLOR;
			titleColor = WHITETEXTCOLOR;
			POSITIVEBTNCOLOR = ORANGEBTNCOLOR;
			POSITIVEBTNCOLORPRESSED = ORANGEBTNCOLORPRESSED;
			NEGATIVEBTNCOLOR = COMMONBTNCOLOR;
			NEGATIVEBTNCOLORPRESSED = COMMONBTNCOLORPRESSED;
			break;
		case GREENTHEME:
			isStroke = false;
			reblindTextColor = WHITETEXTCOLOR;
			helpBgColor = GREENHELPBGCOLOR;
			helpPressBgColor = GREENHELPPPRESSBGCOLOR;
			hintTextcolor = GRAY2TEXTCOLOR;
			ballColor = WHITETEXTCOLOR;
			defaultTextcolor = WHITETEXTCOLOR;
			keyWordColor = GREENBTNCOLOR;
			backgroundColor = WHITETEXTCOLOR;
			unableColor = GRAYBGCOLOR;
			selectionColor = GRAYBGCOLOR;
			color = GREENBTNCOLOR;
			coinColor = color;
			backGroundColor = color;
			confirmColor = color;
			lineColor = color;
			selectedColor = color;
			titleBgColor = color;
			selectViewBgColor = color;
			blindPhoneColor = BLACKTEXTCOLOR;
			unSelectedColor = BLACKTEXTCOLOR;
			titleColor = WHITETEXTCOLOR;
			POSITIVEBTNCOLOR = GREENBTNCOLOR;
			POSITIVEBTNCOLORPRESSED = GREENBTNCOLORPRESSED;
			NEGATIVEBTNCOLOR = REDBTNCOLOR;
			NEGATIVEBTNCOLORPRESSED = REDBTNCOLORPRESSED;
			break;
		case WHITETHEME:
			isStroke = true;
			helpBgColor = WHITEHELPBGCOLOR;
			helpPressBgColor = WHITEHELPPPRESSBGCOLOR;
			reblindTextColor = WHITETEXTCOLOR;
			lineColor = GRAYLINECOLOR;
			coinColor = REDTEXTCOLOR;
			hintTextcolor = GRAY2TEXTCOLOR;
			ballColor = WHITETEXTCOLOR;
			defaultTextcolor = REDTEXTCOLOR;
			selectViewBgColor = REDTEXTCOLOR;
			keyWordColor = REDTEXTCOLOR;
			backgroundColor = GRAY2BGCOLOR;
			unableColor = GRAYBGCOLOR;
			selectionColor = WHITETEXTCOLOR;
			color = WHITETEXTCOLOR;
			backGroundColor = color;
			blindPhoneColor = BLACKTEXTCOLOR;
			confirmColor = BLACKTEXTCOLOR;
			selectedColor = REDTEXTCOLOR;
			unSelectedColor = BLACKTEXTCOLOR;
			titleColor = BLACKTEXTCOLOR;
			titleBgColor = TITLEBGCOLOR;
			POSITIVEBTNCOLOR = WHITETEXTCOLOR;
			POSITIVEBTNCOLORPRESSED = WHITEBTNCOLOR;
			NEGATIVEBTNCOLOR = WHITETEXTCOLOR;
			NEGATIVEBTNCOLORPRESSED = REDBTNCOLOR;
			break;
		}
	}

}
