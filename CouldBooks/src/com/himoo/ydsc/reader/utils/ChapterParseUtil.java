package com.himoo.ydsc.reader.utils;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.himoo.ydsc.bean.BaiduBookChapter;
import com.himoo.ydsc.http.HttpConstant;
import com.himoo.ydsc.ui.utils.Toast;

public class ChapterParseUtil {
	
	/**
	 * 解析章节中内容
	 * @param json
	 * @return
	 */
	public static String Parse(Context context ,String response)
	{
		try {
			JSONObject json = new JSONObject(response);
			if (json.getInt("status") == 1) {
				String bookContent = json
						.getJSONObject("data")
						.getString("content")
						.replaceAll(
								"<p style="
										+ "\"text-indent:2em;\">",
								"\n        ")
						.replaceAll("<div  class=\"content.*<div  style=\"display:inline-block;\"></div>    ", "\n        ")
						.replaceAll("</p>", "\n")
						.replaceAll("<div[\\s]*>", "")
						.replaceAll("</div>", "\n")
						.replaceAll("<br/>", "\n\n")
						.replaceAll("<span style=\".*\">", "")
						.replaceAll("</span>", "\n")
						.replaceAll("<[/]?dd.*>", "\n")
						.replaceAll("<div.*>", "\n")
						.replaceAll("&nbsp; &nbsp;", "      ")
						.replaceAll("<br>", "")
						.replaceAll("readx.* ", "       ")
						.replaceAll("<a  class=\" yi-fontcolor\" .*", "")
						.replaceAll("<p  class=\" .*p\">", "\n         ")
						.replaceAll("<p  class=\".*p\">", "\n        ")
						.replaceAll("<a  class=\".*</a>", "");
				return bookContent;
			} else {

//				Toast.showLong(context, "本章暂无内容");
			}
		} catch (JSONException e1) {
			return null;
//			Toast.showLong(context, "解析章节内容失败");
		}
		
		return null;
	}
	
	/**
	 * 拼接百度=书籍每章的地址
	 * 
	 * @param chapter
	 */
	public static String getChapterUrl(BaiduBookChapter chapter) {
		StringBuilder sb = new StringBuilder();
		sb.append(HttpConstant.BAIDU_CHAPTER_URL).append("src=")
				.append(chapter.getHref()).append("&cid=")
				.append(chapter.getCid()).append("&chapterIndex=")
				.append(chapter.getIndex()).append("&time=&skey=&id=wisenovel");

		return sb.toString();

	}
	
	
	
}
