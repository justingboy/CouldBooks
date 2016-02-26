package com.himoo.ydsc.http;

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
import com.himoo.ydsc.bookdl.DownloadManager;
import com.himoo.ydsc.download.BaiduBookDownload;
import com.himoo.ydsc.notification.DownlaodNotification;
import com.himoo.ydsc.reader.dao.BookMark;
import com.himoo.ydsc.reader.dao.BookMarkDb;
import com.himoo.ydsc.ui.utils.Toast;
import com.himoo.ydsc.util.FileUtils;
import com.himoo.ydsc.util.MyLogger;
import com.himoo.ydsc.util.SP;

/**
 * 开启线程删除本地书籍
 * 
 */
public class AfreshDownloadTask extends AsyncTask<Void, Void, Void> {
	private String bookName;
	private String bookId;
	private File dirFile;
	private Context context;
	private FileUtils fileUtils;
	private DownlaodNotification downNotification;
	private OnAfreshDownloadListener mListener;
	private String lastChapterUrl;

	public AfreshDownloadTask(Context context, String lastChapterUrl,
			String bookName, String bookId,OnAfreshDownloadListener listener) {
		this.bookName = bookName;
		this.bookId = bookId;
		this.context = context;
		this.mListener = listener;
		this.lastChapterUrl = lastChapterUrl;
	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
		downNotification = new DownlaodNotification(context);
		if (mListener != null) {
			mListener.onPreDeleted(bookName,bookId);
		}
		Toast.show(context, "正在删除 《" + bookName + "》");
		fileUtils = new FileUtils(context);
		dirFile = new File(FileUtils.mSdRootPath + "/CouldBook/baidu"
				+ File.separator + bookName+"_"+bookId + File.separator);
	}

	@Override
	protected Void doInBackground(Void... params) {
		// TODO Auto-generated method stub
		if (dirFile != null && dirFile.exists()) {
			fileUtils.deleteFile(dirFile);
		}

		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		Toast.show(context, "删除 《" + bookName + "》成功");
		SaveAsyncTask downloadTask = new SaveAsyncTask(null, lastChapterUrl);
		DownloadManager.getInstance().addTask(bookName,bookId, downloadTask);
		downloadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Toast.show(context, "重新下载《" + bookName + "》");
			}
		}, 2000);

	}

	/**
	 * 异步任务 缓存小说的所有章节
	 * 
	 * 
	 */
	class SaveAsyncTask extends AsyncTask<Void, String, String> {
		private File dirFile;
		private List<BaiduBookChapter> list = null;
		private String lastUrl;

		public SaveAsyncTask(List<BaiduBookChapter> list, String lastUrl) {
			this.lastUrl = lastUrl;
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
			dirFile = new File(FileUtils.mSdRootPath + "/CouldBook/baidu"
					+ File.separator + bookName+"_"+bookId + File.separator);
			if (!dirFile.exists())
				dirFile.mkdirs();
			if (mListener != null) {
				mListener.onPreDownload();
			}
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
			if (downNotification != null) {
				downNotification.creatNotification(bookName, "开始下载");
				downNotification.setProgressAndNetSpeed(progress,
						downNotification.getNetSpeed());
			}
			int allChapterLength = list.size();
			float partProgress = (float) allChapterLength / 100;
			for (int i = 0; i < allChapterLength; i++) {
				if (isCancelled()) {
					downNotification.notifiManger.cancel(downNotification.NOTIFI_ID);
					SP.getInstance().remove(bookName,bookId);
					if(mListener!=null)
						mListener.onCancelDownload();
					return null;
				}
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
			// 记录的阅读位置可能大于章节总数，会报数组越界异常，故在此处理
			BookMark bookMark = BookMarkDb.getInstance(context, "book")
					.querryReaderPos(bookName,bookId);
			if (list != null && bookMark != null
					&&   bookMark.getPosition()>list.size() - 1&&bookMark.getPosition()>0) {
				bookMark.setPosition(list.size() - 1);
				bookMark.setCurrentPage(-1);
				bookMark.setPageCount(-1); 
				BookMarkDb.getInstance(context, "book").saveReaderPosition(
						bookMark);
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			Toast.show(context, "《" + bookName + "》下载完成");
			BaiduBookDownload.getInstance(context).updateDownSuccess(bookName,bookId);
			if (mListener != null) {
				mListener.onPostDownloadSuccess(bookName,bookId);
			}
			DownloadManager.getInstance().deleteTask(bookName, bookId);
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					downNotification.notifiManger.cancel(downNotification.NOTIFI_ID);
				}
			}, 3000);

		}

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

	

}
