package com.himoo.ydsc.config;

public interface SpConstant {
	public static final String CONFIG = "config"; // sp文件名称
	public static final String AUTO_UPLOAD = "autoupload"; // 自动更新标识

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
	
	/** 更新小说的时间间隔*/
	public static final String BOOK_UPATE_SETTING_SPACE = "book_update_setting_space";
	
	/** 更新小说网络类型*/
	public static final String BOOK_UPATE_SETTING_NETTYEP = "book_update_setting_nettype";
	
	/** 更新小说的通知内容*/
	public static final String BOOK_UPATE_SETTING_NOTICE = "book_update_setting_notic";
	
	
	
	
	
	
	
	
	/** 主题小说封面设置*/
	public static final String BOOK_COVER_TYPE = "book_cover_type";
	
	/** 主题皮肤颜色设置*/
	public static final String BOOK_SKIN_TYPE = "book_skin_type";
	
	/** 主题对应int*/
	public static final String BOOK_SKIN_INDEX = "book_skin_index";
	/** 封面对应int*/
	public static final String BOOK_COVER_INDEX = "book_cover_index";
	
	/** 阅读界面的背景*/
	public static final String BOOK_SETTING_TEXT_BG= "book_setting_text_bg";
	/** 阅读界面的背景*/
	public static final String BOOK_SETTING_TEXT_SIZE= "book_setting_text_size";
	
	/** 字体的类型，简体和繁体*/
	public static final String BOOK_SETTING_TEXT_TYPE= "book_setting_text_type";
	
	/** 行间距*/
	public static final String BOOK_SETTING_TEXT_LINE= "book_setting_text_line";
	
	/** 亮度*/
	public static final String BOOK_SETTING_LIGHT= "book_setting_light";
	
	/** 跟随系统的亮度*/
	public static final String BOOK_SETTING_LIGHT_SYSTEM= "book_setting_light_system";
	
	/** 楷体 ，宋体等字体*/
	public static final String BOOK_SETTING_TEXT_CHILDREN_TYPE= "book_setting_text_children_type";
}
