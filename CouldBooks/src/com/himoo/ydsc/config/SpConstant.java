package com.himoo.ydsc.config;

public interface SpConstant {
	public static final String CONFIG = "config"; // sp文件名称
	public static final String AUTO_UPLOAD = "autoupload"; // 自动更新标识
	public static final String BUILD_CODE = "2016-01-23"; // 自动更新标识

	/** 精选Fragment界面刷新时间 */
	public static final String LAST_REF_TIME_SUBCHOICE = "sub_Choice";
	/** 排行Fragment界面刷新时间 */
	public static final String LAST_REF_TIME_SUBRANKING = "sub_Ranking";
	/** 热搜Fragment界面刷新时间 */
	public static final String LAST_REF_TIME_SUBHOTSEARCH = "sub_HotSearch";

	/** 小说更新类型-- 整本更新 */
	public static final int BOOK_UPDATE_TYPE_ALL = 1;
	/** 小说更新类型-- 最新章节 */
	public static final int BOOK_UPDATE_TYPE_CHAPTER = 2;

	/** 标记是否开启小说更新设置 */
	public static final String BOOK_UPATE_SETTING = "book_update_setting";

	/** 标记是否开启更新小说的声音提醒 */
	public static final String BOOK_UPATE_SETTING_SOUND = "book_update_setting_sound";

	/** 更新小说的时间间隔 */
	public static final String BOOK_UPATE_SETTING_SPACE = "book_update_setting_space";

	/** 更新小说网络类型 */
	public static final String BOOK_UPATE_SETTING_NETTYEP = "book_update_setting_nettype";

	/** 更新小说的通知内容 */
	public static final String BOOK_UPATE_SETTING_NOTICE = "book_update_setting_notic";

	/** 主题小说封面设置 */
	public static final String BOOK_COVER_TYPE = "book_cover_type";

	/** 主题皮肤颜色设置 */
	public static final String BOOK_SKIN_TYPE = "book_skin_type";

	/** 主题对应int */
	public static final String BOOK_SKIN_INDEX = "book_skin_index";

	/** 封面对应int */
	public static final String BOOK_COVER_INDEX = "book_cover_index";

	/** 书架的排列的方向 */
	public static final String BOOK_SHELF_DIRECTION = "book_shelf_direction";

	/** 阅读界面的背景 */
	public static final String BOOK_SETTING_TEXT_BG = "book_setting_text_bg";

	/** 阅读界面的背景 */
	public static final String BOOK_SETTING_TEXT_SIZE = "book_setting_text_size";

	/** 字体的类型，简体和繁体 */
	public static final String BOOK_SETTING_TEXT_TYPE = "book_setting_text_type";

	/** 行间距 */
	public static final String BOOK_SETTING_TEXT_LINE = "book_setting_text_line";

	/** 亮度 */
	public static final String BOOK_SETTING_LIGHT = "book_setting_light";

	/** 夜间模式 */
	public static final String BOOK_SETTING_NIGHT_MODE = "book_setting_night_mode";

	/** 自动夜间模式夜定点（19-7） */
	public static final String BOOK_SETTING_AUTO_NIGHT_MODE_YES = "book_setting_auto_night_mode_yes";

	/** 自动夜间模式是否是手动 */
	public static final String BOOK_SETTING_NITGHT_HAND = "book_setting_nitght_hand";

	/** 自动夜间模式19-7 */
	public static final String BOOK_SETTING_AUTO_NIGHT = "book_setting_auto_night";
	
	/** 在线看书，有无加载提示框 */
	public static final String BOOK_SETTING_AUTO_LOAD = "book_setting_auto_load";

	/** 跟随系统的亮度 */
	public static final String BOOK_SETTING_LIGHT_SYSTEM = "book_setting_light_system";

	/** 楷体 ，宋体等字体 */
	public static final String BOOK_SETTING_TEXT_CHILDREN_TYPE = "book_setting_text_children_type";

	/** 保存阅读时间 */
	public static final String BOOK_READER_TIME = "book_reader_time";

	/** 翻书动画类型 int */
	public static final String BOOK_TURNPAGE_TYPE = "book_turnpage_type";

	/** 翻书动画类型,String */
	public static final String BOOK_TURNPAGE_TITLE_TYPE = "book_turnpage_title_type";

	/** 是否是第一次点击自定义按钮 */
	public static final String BOOK_AUTO_COLOR_FIRST = "book_auto_color_first";

	/** 是否是自定义颜色 */
	public static final String BOOK_AUTO_COLOR = "book_auto_color";
	
	/** 保存自定义字体的颜色 */
	public static final String BOOK_AUTO_COLOR_TEXT = "book_auto_color_text";
	
	/** 保存自定义字阅读背景的颜色 */
	public static final String BOOK_AUTO_COLOR_BG = "book_auto_color_bg";
}
