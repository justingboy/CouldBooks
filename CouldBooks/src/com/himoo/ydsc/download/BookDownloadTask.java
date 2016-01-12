package com.himoo.ydsc.download;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.himoo.ydsc.bean.BaiduBookChapter;
import com.himoo.ydsc.db.ChapterDb;
import com.himoo.ydsc.http.BookDetailsTask;
import com.himoo.ydsc.http.HttpConstant;
import com.himoo.ydsc.notification.DownlaodNotification;
import com.himoo.ydsc.util.FileUtils;
import com.himoo.ydsc.util.MyLogger;
import com.himoo.ydsc.util.SP;

public class BookDownloadTask extends AsyncTask<Void, String, String> {

	private File dirFile;
	private List<BaiduBookChapter> list = null;
	private String lastUrl;
	private String bookName;
	private Context context;
	private DownlaodNotification downNotification;
	private OnBookDownloadListener listener;

	public BookDownloadTask(Context context, List<BaiduBookChapter> list,
			String bookName, String lastUrl, OnBookDownloadListener listener) {
		this.context = context;
		this.lastUrl = lastUrl;
		this.bookName = bookName;
		this.listener = listener;
		if (list != null) {
			String index = list.get(0).getIndex();
			if (Integer.valueOf(index) > 1) {
				Collections.reverse(list);
			}
			this.list = list;
		}

	}

	@Override
	protected void onPreExecute() {
		downNotification = new DownlaodNotification(context);
		dirFile = new File(FileUtils.mSdRootPath + "/CouldBook/baidu"
				+ File.separator + bookName + File.separator);
		if (!dirFile.exists())
			dirFile.mkdirs();
		if (listener != null)
			listener.onPreDownlaod();
	}

	@Override
	protected String doInBackground(Void... params) {
		// TODO Auto-generated method stub
		int len = 0;
		int progress = 0;
		if (list == null) {
			String content = BookDetailsTask.getInstance()
					.geLasttChapterFormService(context, lastUrl);

			if (content != null) {
				ArrayList<BaiduBookChapter> list = praseBaiduBookChapter(content);
				if (list != null && !list.isEmpty()) {
					// updateNewChapter(bookName, list);
					this.list = list;
				}
			}
		}
		// 保存章节的换源信息
		ChapterDb.getInstance().createDb(context, bookName);
		ChapterDb.getInstance().saveBookChapter(list);

		if (downNotification != null) {
			downNotification.creatNotification(bookName, "开始下载");
			downNotification.setProgressAndNetSpeed(progress,
					downNotification.getNetSpeed());
		}
		try {

			int allChapterLength = list.size();
			float partProgress = (float) allChapterLength / 100;
			for (int i = 0; i < allChapterLength; i++) {
				BaiduBookChapter chapter = list.get(i);
				String url = getChapterUrl(chapter);
				String chapterName = chapter.getText().trim()
						.replaceAll("/", "|")
						+ "-|" + chapter.getIndex() + "-|" + i + ".txt";
				File chapterFile = new File(dirFile.getAbsolutePath(),
						chapterName);
				// 如何该章节已经下载则不需要下载,跳过,下载下一个章节
				if (!chapterFile.exists()) {
					try {
						chapterFile.createNewFile();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						// e.printStackTrace();
						MyLogger.kLog().e("下载章节，创建文件出错:" + e);
					}
					com.himoo.ydsc.download.FileUtils.writeTosSd(url,
							chapterFile);
				}
				len++;
				if (partProgress <= 1) {
					progress = getPartprogress(partProgress);
					progress *= len;
					if (downNotification != null) {
						downNotification.creatNotification(bookName, "开始下载");
						downNotification.setProgressAndNetSpeed(progress,
								downNotification.getNetSpeed());
					}

				} else {
					if (len % (int) partProgress == 0) {
						progress++;
						if (progress <= 99) {
							if (progress % 2 == 0) {
								if (downNotification != null) {
									downNotification.creatNotification(
											bookName, "开始下载");
									downNotification.setProgressAndNetSpeed(
											progress,
											downNotification.getNetSpeed());
								}
							}

						}
					}
				}

				if (allChapterLength - len == 0) {
					if (downNotification != null) {
						downNotification.creatNotification(bookName, "下载完成");
						downNotification.setProgressAndNetSpeed(100,
								downNotification.getNetSpeed());
					}
				}

			}
		} catch (Exception e) {

			MyLogger.kLog().e(e);
			SP.getInstance().putBoolean(bookName, true);
		}

		return null;
	}

	@Override
	protected void onPostExecute(String result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		if (listener != null)
			listener.onCompleteDownlaod();
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				downNotification.notifiManger.cancelAll();
			}
		}, 1000);

	}

	/**
	 * 获取下载几章为进度的1%
	 * 
	 * @param part
	 * @return
	 */
	private int getPartprogress(float part) {
		int partProgress = 0;
		if (1 == (int) part)
			partProgress = 1;
		String num = String.valueOf(part);
		int secondNum = Character.getNumericValue(num.charAt(2));
		switch (secondNum) {
		case 1:
			partProgress = 10;
			break;
		case 2:
			partProgress = 5;
			break;
		case 3:
			partProgress = 3;
			break;
		case 4:
			partProgress = 2;
			break;
		case 5:
			partProgress = 2;
			break;
		default:
			partProgress = 1;
			break;
		}

		return partProgress;
	}

	/**
	 * 拼接百度=书籍每章的地址
	 * 
	 * @param chapter
	 */
	protected String getChapterUrl(BaiduBookChapter chapter) {
		StringBuilder sb = new StringBuilder();
		sb.append(HttpConstant.BAIDU_CHAPTER_URL).append("src=")
				.append(chapter.getHref()).append("&cid=")
				.append(chapter.getCid()).append("&chapterIndex=")
				.append(chapter.getIndex()).append("&time=&skey=&id=wisenovel");

		return sb.toString();

	}

	/**
	 * 解析百度章节信息
	 * 
	 * @param jsonString
	 * @return
	 */
	private ArrayList<BaiduBookChapter> praseBaiduBookChapter(String jsonString) {
		try {
			Gson gson = new Gson();
			JSONObject jsonObject = new JSONObject(jsonString);
			if (jsonObject.getInt("status") == 1) {
				JSONObject subJsonObject = jsonObject.getJSONObject("data");
				String json = subJsonObject.getString("group");
				return gson.fromJson(json,
						new TypeToken<ArrayList<BaiduBookChapter>>() {
						}.getType());

			}
		} catch (Exception e) {
			// TODO: handle exception
			return null;
		}
		return null;

	}

	public interface OnBookDownloadListener {

		public void onPreDownlaod();

		public void onCompleteDownlaod();

	}

}
