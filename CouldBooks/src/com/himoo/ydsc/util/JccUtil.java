package com.himoo.ydsc.util;

import java.io.IOException;

import taobe.tec.jcc.JChineseConvertor;

/**
 * 简繁体转换工具类
 * 
 */
public class JccUtil {

	/**
	 * 简体转成繁体
	 * 
	 * @param changeText
	 * @return
	 */
	public static String changeToTraditional(String changeText) {
		try {
			JChineseConvertor jChineseConvertor = JChineseConvertor
					.getInstance();
			changeText = jChineseConvertor.s2t(changeText);

		} catch (IOException e) {
			e.printStackTrace();
		}
		return changeText;
	}

	/**
	 * 繁体转成简体
	 * 
	 * @param changeText
	 * @return
	 */
	public static String changeToSimplified (String changeText) {
		try {
			JChineseConvertor jChineseConvertor = JChineseConvertor
					.getInstance();
			changeText = jChineseConvertor.t2s(changeText);

		} catch (IOException e) {
			e.printStackTrace();
		}
		return changeText;
	}

}
