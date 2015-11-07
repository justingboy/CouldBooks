package com.himoo.ydsc.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegularUtil {

	/** 　抓取图片的正则表达式 */
	public static final String REGULAR_IMAGE_URL = "(http\\://)?([a-zA-Z0-9_-]+\\.)+(com|net|cn|org){1}(\\/[a-zA-Z0-9_-]+)*\\.(jpg|jpeg|gif|png)";

	/**
	 * 转换抓取Url
	 * 
	 * @param url
	 * @return
	 */
	public static String converUrl(String url) {
		String resultUrl = "";
		try {
			resultUrl = url = URLDecoder.decode(url, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return resultUrl;
		}
		Pattern p = Pattern.compile(REGULAR_IMAGE_URL);
		Matcher m = p.matcher(resultUrl);
		if (m.find()) {
			resultUrl = m.group();
		}

		return resultUrl;
	}

}
