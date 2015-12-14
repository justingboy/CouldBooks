package com.himoo.ydsc.listener;

/**
 * 仿Xutils自定义数据返回接口,接口是在子线程中，故不能操作UI 
 *
 */
public interface OnRequestCallBack {
	public void onSuccess(String response);
	public void onFailure(String errorMsg);

}
