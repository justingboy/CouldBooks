package com.himoo.ydsc.http;

public interface OnAfreshDownloadListener {
	
	public void onPreDeleted(String bookName);

	public void onPreDownload();
	
	public void onCancelDownload();

	public void onPostDownloadSuccess(String bookName);
}