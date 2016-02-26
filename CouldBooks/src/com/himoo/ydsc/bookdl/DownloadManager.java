package com.himoo.ydsc.bookdl;

import java.util.HashMap;

import android.os.AsyncTask;

public class DownloadManager {

	private static DownloadManager mInstance = null;

	public HashMap<String, AsyncTask<?, ?, ?>> taskMap = new HashMap<String, AsyncTask<?, ?, ?>>();

	private DownloadManager() {

	}

	public static DownloadManager getInstance() {
		if (mInstance == null) {
			synchronized (DownloadManager.class) {
				if (mInstance == null) {
					mInstance = new DownloadManager();
				}
			}
		}
		return mInstance;
	}

	/**
	 * 添加下载任务
	 * 
	 * @param task
	 */
	public synchronized void addTask(String bookName, String bookId,
			AsyncTask<?, ?, ?> newTask) {
		taskMap.put(bookName + bookId, newTask);
	}

	/**
	 * 删除下载任务
	 * 
	 * @param newTask
	 */
	public synchronized void deleteTask(String bookName, String bookId) {
		AsyncTask<?, ?, ?> task = taskMap.get(bookName + bookId);
		if (task != null && task.getStatus() == AsyncTask.Status.RUNNING)
			task.cancel(true);
		taskMap.remove(bookName + bookId);
	}

	/**
	 * 判断是否正在下载
	 * 
	 * @param bookName
	 * @return
	 */
	public synchronized boolean isExistTask(String bookName, String bookId) {
		AsyncTask<?, ?, ?> task = taskMap.get(bookName + bookId);
		if (task != null && task.getStatus() == AsyncTask.Status.RUNNING)
			return true;
		return false;
	}

	/**
	 * 判断是否有下载
	 * 
	 * @param bookName
	 * @return
	 */
	public synchronized boolean isExistTask() {
		if (taskMap != null && !taskMap.isEmpty())
			return true;
		return false;
	}

}
