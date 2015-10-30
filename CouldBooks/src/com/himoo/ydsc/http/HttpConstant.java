package com.himoo.ydsc.http;

/**
 * 定义网络请求相关常量
 * 
 */
public interface HttpConstant {
	/** 自己服务器请求类型 */
	public static final int BOOK_REQUEST_TYPE_ME = 1;
	/** 百度排行请求类型 */
	public static final int BOOK_REQUEST_TYPE_BAIDU_RANKING = 2;
	/** 百度排热搜请求类型 */
	public static final int BOOK_REQUEST_TYPE_BAIDU_HOTSEARCH = 3;

	/** 　三个地址轮询测试，看哪个可以用 */
	public static final String BASE_URL1 = "http://www.51devapp.com/bookinfo/getapps.asp";
	public static final String BASE_URL2 = "http://hk.51devapp.com/bookinfo/";
	public static final String BASE_URL3 = "http://www.iosjihuo.com/bookinfo/";

	/** 请求的主地址 Host这个暂时替代Ping 出来的地址 */
	public static final String HOST_URL = "http://www.51devapp.com/bookinfo0/getBooksList.asp";
	/** 百度排行地址 */
	public static final String BAIDU_RANKINGF_URL = "http://m.baidu.com/book/data/rank";
	/** 百度热搜地址 */
	public static final String BAIDU_HOTSEARCH_URL = "http://m.baidu.com/book/data/recommend";

	/** 小说关键字请求地址 */
	public static final String BASE_URL_KEYWORD = "http://www.51devapp.com/bookinfo/getkeyword.asp";

	
	
}
