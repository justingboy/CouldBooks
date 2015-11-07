package com.himoo.ydsc.listener;

import java.util.ArrayList;

import com.himoo.ydsc.bean.BaiduBookChapter;

/**
 * 解析章节接口
 * 
 */
public interface OnParseChapterListener {

	void onParseSuccess(ArrayList<BaiduBookChapter> list);

	void onParseFailure(Exception ex, String msg);

}
