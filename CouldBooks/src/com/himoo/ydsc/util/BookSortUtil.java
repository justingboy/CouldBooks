package com.himoo.ydsc.util;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import com.himoo.ydsc.download.BookDownloadInfo;

/**
 * 小说排序工具类
 * 
 */
public class BookSortUtil {
	/** 　替换字符串中的特殊字符 */
	private static final String REGULAR = "[0-9《`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
	/** 　判断字符串是不是英文的 */
	private static final String regex = "^[a-zA-Z]*";
	private static HanyuPinyinOutputFormat format;

	public static void initHanyuPinyin() {
		format = new HanyuPinyinOutputFormat();

		// UPPERCASE：大写 (ZHONG)
		// LOWERCASE：小写 (zhong)
		format.setCaseType(HanyuPinyinCaseType.LOWERCASE);

		// WITHOUT_TONE：无音标 (zhong)
		// WITH_TONE_NUMBER：1-4数字表示英标 (zhong4)
		// WITH_TONE_MARK：直接用音标符（必须WITH_U_UNICODE否则异常） (zhòng)
		format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);

		// WITH_V：用v表示ü (nv)
		// WITH_U_AND_COLON：用"u:"表示ü (nu:)
		// WITH_U_UNICODE：直接用ü (nü)
		format.setVCharType(HanyuPinyinVCharType.WITH_V);

	}

	/**
	 * 将汉字转换成拼音
	 * 
	 * @param value
	 * @return
	 */
	public static String TransforToPingyin(String value) {
		if (format == null) {
			initHanyuPinyin();
		}
		StringBuilder sb = new StringBuilder();
		// char ch[] = new char[value.trim().length()];
		// for (int i = 0; i < ch.length; i++) {
		// ch[i] = value.charAt(i);
		if (value != null && value.matches(regex)) {
			return value;
		}
		try {
			String[] pinyin = PinyinHelper.toHanyuPinyinStringArray(value
					.trim().charAt(0), format);
			sb.append(pinyin[0]);
		} catch (BadHanyuPinyinOutputFormatCombination e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// }
		return sb.toString();

	}

	/**
	 * 按照书名排序
	 * 
	 * @param list
	 * @return
	 */
	public static List<BookDownloadInfo> sortByBookName(
			List<BookDownloadInfo> list) {

		Collections.sort(list, new Comparator<BookDownloadInfo>() {

			public int compare(BookDownloadInfo o1, BookDownloadInfo o2) {

				return TransforToPingyin(
						o1.getBookName().replaceAll(REGULAR, "")).compareTo(
						TransforToPingyin(o2.getBookName().replaceAll(REGULAR,
								"")));
			}
		});

		return list;
	}

	/**
	 * 按照作者排序
	 * 
	 * @param list
	 * @return
	 */
	public static List<BookDownloadInfo> sortByBookAuthor(
			List<BookDownloadInfo> list) {

		Collections.sort(list, new Comparator<BookDownloadInfo>() {

			public int compare(BookDownloadInfo o1, BookDownloadInfo o2) {

				return TransforToPingyin(
						o1.getBookAuthor().replaceAll(REGULAR, "")).compareTo(
						TransforToPingyin(o2.getBookAuthor().replaceAll(
								REGULAR, "")));
			}
		});

		return list;
	}

	/**
	 * 按照更新时间
	 * 
	 * @param list
	 * @return
	 */
	public static List<BookDownloadInfo> sortByBookUpdate(
			List<BookDownloadInfo> list) {

		Collections.sort(list, new Comparator<BookDownloadInfo>() {

			public int compare(BookDownloadInfo o1, BookDownloadInfo o2) {

				return (Long.valueOf(o2.getBookLastUpdateTime()))
						.compareTo(Long.valueOf(o1.getBookLastUpdateTime()));
			}
		});

		return list;
	}

}
