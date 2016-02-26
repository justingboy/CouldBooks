package com.himoo.ydsc.http;

public interface OnAfreshDownloadListener {
	
	public void onPreDeleted(String bookName,String bookId);

	public void onPreDownload();
	
	public void onCancelDownload();

	public void onPostDownloadSuccess(String bookName,String bookId);
}