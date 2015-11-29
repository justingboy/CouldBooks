package com.himoo.ydsc.listener;


public interface OnRequestCallBack {
	public void onSuccess(String response);
	public void onFailure(String errorMsg);

}
