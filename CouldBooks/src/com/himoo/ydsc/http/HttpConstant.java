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

	/** 百度分类请求类型 */
	public static final int BOOK_REQUEST_TYPE_BAIDU_CLASSIFY = 4;

	/** 百度数据源搜索类型 */
	public static final int BOOK_REQUEST_TYPE_BAIDU_SEARCH = 5;

	/** 自己服务器库中搜索的类型 */
	public static final int BOOK_REQUEST_TYPE_ME_SEARCH = 6;

	/** 　三个地址轮询测试，看哪个可以用 */
	public static final String BASE_URL1 = "http://www.51devapp.com/";
	public static final String BASE_URL2 = "http://hk.51devapp.com/";
	public static final String BASE_URL3 = "http://www.iosjihuo.com/";
	/** 　正式地址　 */
	public static final String BASE_URL_FORMAL = "bookinfo/getapps.asp";
	/** 　测试地址　 */
	public static final String BASE_URL_TEST = "bookinfo0/";

	/** 请求的主地址 Host这个暂时替代Ping 出来的地址 */
	public static final String HOST_URL_TEST = "http://www.51devapp.com/bookinfo0/";
	public static final String HOST_URL = "http://www.51devapp.com/bookinfo0/getBooksList.asp";

	/** 百度排行地址 */
	public static final String BAIDU_RANKINGF_URL = "http://m.baidu.com/book/data/rank";

	/** 百度热搜地址 */
	public static final String BAIDU_HOTSEARCH_URL = "http://m.baidu.com/book/data/recommend";

	/** 小说关键字请求地址 */
	public static final String BASE_URL_KEYWORD = "http://www.51devapp.com/bookinfo/getkeyword.asp";

	/** 自己服务器小说详情请求地址 */
	public static final String BASE_URL_BOOK_DETAILS = "http://www.51devapp.com/bookinfo0/getBooksDetail.asp";
	// public static final String BASE_URL_BOOK_DETAILS =
	// "http://www.51devapp.com/bookinfo0/getBooksDetail.asp?bookID=";

	/** 百度小说库分类请求地址 */
	public static final String BAIDU_BOOK_CLASSIFY_URL = "http://m.baidu.com/book/data/cates";

	/** 百度小说库单个分类请求地址 */
	public static final String BAIDU_BOOK_CATE_URL = "http://m.baidu.com/book/data/cate";

	/** 百度关键字搜索地址 */
	public static final String BAIDU_BOOK_SEARCH_URL = "http://www.51devapp.com/bookinfo/baidubooksearch.asp?";

	/** 百度详情页面请求地址 */
	public static final String BAIDU_BOOK_DETAILS_URL = "http://m.baidu.com/tc?";

	/** 豆瓣评书请求地址 */
	public static final String DOUBAN_STORYTELLING_URL = "http://api.douban.com/book/subjects?";

	/** 评分地址 */
	public static final String BOOK_RATING_URL = "http://www.51devapp.com/bookinfo0/bookrate.asp";

	/** 书籍下载计算 地址 */
	public static final String BOOK_DOWNLOAD_COUNT_URL = "http://www.51devapp.com/bookinfo0/getdl.asp";

	/** 百度书籍章节 地址 */
	public static final String BAIDU_CHAPTER_URL = "http://m.baidu.com/ssid=0/from=0/bd_page_type=1/uid=undefined/pu=sz@1320_1001/t=iphone/tc?srd=1&appui=alaxs&ajax=1&alals=1&";

}
