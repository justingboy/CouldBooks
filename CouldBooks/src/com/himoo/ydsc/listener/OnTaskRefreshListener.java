package com.himoo.ydsc.listener;

import java.util.ArrayList;

/**
 * 上来刷新，下拉加载更多接口
 */
public interface OnTaskRefreshListener<T>{
	/**　上拉加载更多　*/
	void onPullUpRefreshSucess(ArrayList<T> list);
	/** 下拉刷新   */
	void onPullDownRefreshSucess(ArrayList<T> list);
	/** 下拉刷新   */
	void onPullToRefreshFailure(Exception error, String msg);

}
