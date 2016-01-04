package com.himoo.ydsc.update;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.himoo.ydsc.R;
import com.himoo.ydsc.activity.HomeActivity;
import com.himoo.ydsc.bean.BaiduBookChapter;
import com.himoo.ydsc.config.SpConstant;
import com.himoo.ydsc.download.BaiduBookDownload;
import com.himoo.ydsc.download.BaiduInfo;
import com.himoo.ydsc.http.BookDetailsTask;
import com.himoo.ydsc.http.HttpConstant;
import com.himoo.ydsc.reader.utils.LocalReaderUtil;
import com.himoo.ydsc.util.FileUtils;
import com.himoo.ydsc.util.NetWorkUtils;
import com.himoo.ydsc.util.SharedPreferences;

/**
 * 检查书的章节更新情况
 * 
 */
public class BookUpdateService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		// creatNotification();
		int netWorkType = SharedPreferences.getInstance().getInt(
				SpConstant.BOOK_UPATE_SETTING_NETTYEP, 0);
		if (netWorkType == 1 && !NetWorkUtils.isMobileConnected(this)) {
			new UpdateAsyncTask(this).execute();
		} else if (netWorkType == 0 && NetWorkUtils.isNetConnected(this)) {
			new UpdateAsyncTask(this).execute();
		}
		return START_STICKY;
	}

	private void creatNotification(LastChapter chapter) {
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				this).setSmallIcon(R.drawable.icon)
				.setContentTitle(chapter.bookName + " 更新啦!")
				.setContentText("最新章节: " + chapter.chapterName + ",赶快去看看吧!")
				.setAutoCancel(true);
		boolean isPlaySound = SharedPreferences.getInstance().getBoolean(
				SpConstant.BOOK_UPATE_SETTING_SOUND, false);
		if (isPlaySound) {
			mBuilder.setDefaults(NotificationCompat.DEFAULT_SOUND);
		}

		// Creates an explicit intent for an Activity in your app
		Intent resultIntent = new Intent(this, HomeActivity.class);
		// The stack builder object will contain an artificial back stack for
		// the
		// started Activity.
		// This ensures that navigating backward from the Activity leads out of
		// your application to the Home screen.
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		// Adds the back stack for the Intent (but not the Intent itself)
		// stackBuilder.addParentStack(ThemeActivity.class);
		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
				PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(resultPendingIntent);
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		// mId allows you to update the notification later on.
		mNotificationManager.notify((int) System.currentTimeMillis(),
				mBuilder.build());
	}

	/**
	 * 检查是否有最新章节更新
	 * 
	 */
	class UpdateAsyncTask extends AsyncTask<Void, LastChapter, LastChapter> {

		Context context;

		public UpdateAsyncTask(Context context) {
			this.context = context;
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}

		@Override
		protected LastChapter doInBackground(Void... params) {
			// TODO Auto-generated method stub
			List<BaiduInfo> list = BaiduBookDownload.getInstance(context)
					.queryNeedUpdate();
			if (list != null && !list.isEmpty()) {
				for (int i = 0; i < list.size(); i++) {
					BaiduInfo book = list.get(i);
					String lastChapter = book.getLastChapterName();
					String content = BookDetailsTask.getInstance()
							.geLasttChapterFormService(context,
									book.getLastUrl());
					if (content != null) {
						String newChapter = parseLastChapterName(content);
						if (newChapter != null && lastChapter != null) {
							if (!newChapter.equals(lastChapter)) {
								// 表示有最新章节，需要更新提醒，同时把最新章节下载下来
								LastChapter chapter = new LastChapter(
										book.getBookName(), newChapter);
								// 下载最新章节
								ArrayList<BaiduBookChapter> listChapter = praseBaiduBookChapter(content);
								if (list != null && !list.isEmpty()) {
									updateNewChapter(book.getBookName(),
											listChapter);
								}
								onProgressUpdate(chapter);
								// 将最新章节的名字更新
								BaiduBookDownload.getInstance(context)
										.updateChapterName(book, newChapter);

							}
						}
					}
				}
			}

			return null;
		}

		@Override
		protected void onProgressUpdate(LastChapter... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
			Log.i("msg", "onProgressUpdate");
			creatNotification(values[0]);

		}

		@Override
		protected void onPostExecute(LastChapter result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (result != null) {
				Toast.makeText(context, result.chapterName, Toast.LENGTH_LONG)
						.show();

			}

		}

	}

	/**
	 * 解析最新章节
	 * 
	 * @param content
	 * @return
	 */
	private String parseLastChapterName(String content) {
		JSONObject json;
		try {
			json = new JSONObject(content);
			if (json.getInt("status") == 1) {
				JSONObject jsonArray = json.getJSONObject("data")
						.getJSONObject("lastChapter");
				String newChapter = jsonArray.getString("text");
				return newChapter;
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			return null;
		}
		return null;
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

	/**
	 * 下载最新章节
	 * 
	 * @param list
	 * @return
	 */
	private void updateNewChapter(String bookName,
			ArrayList<BaiduBookChapter> list) {
		// TODO Auto-generated method stub
		int allChapterLength = list.size();
		File dirFile = new File(FileUtils.mSdRootPath + "/CouldBook/baidu"
				+ File.separator + bookName + File.separator);
		if (!dirFile.exists())
			dirFile.mkdirs();
		ArrayList<BaiduBookChapter> localList = LocalReaderUtil.getInstance()
				.parseLocalBook(bookName, 2);
		ArrayList<String> chapterNameList = new ArrayList<String>();
		int localSize = localList.size();
		int localStartLen = 0;
		if(localSize>12){
			localStartLen = localSize - 11;
		}
		for (int i = localStartLen; i < localSize; i++) {
			chapterNameList.add(localList.get(i).getText().trim());
		}
		int statLen = 0;
		if (localList != null&&localList.size()>12) {
			statLen = localList.size() - 10;
		}
		int pos = localList.size();
		for (int i = statLen; i < allChapterLength; i++) {
			BaiduBookChapter chapter = list.get(i);
			// 判断这大于10章的可有重名的，有则不下载 ,没有则下载
			if (chapterNameList.contains(chapter.getText().trim()
					.replaceAll("/", "|"))) {
				continue;
			}
			String url = getChapterUrl(chapter);
			String chapterName = chapter.getText().trim().replaceAll("/", "|")
					+ "-|" + chapter.getIndex() + "-|" + i + ".txt";
			File chapterFile = new File(dirFile.getAbsolutePath(), chapterName);
			// 如何该章节已经下载则不需要下载,跳过,下载下一个章节
			if (!chapterFile.exists()) {
				String fileName = chapter.getText().trim().replaceAll("/", "|")
						+ "-|" + chapter.getIndex() + "-|" + pos + ".txt";
				File file = new File(dirFile.getAbsolutePath(), fileName);
				com.himoo.ydsc.download.FileUtils.writeTosSd(url, file);
				pos++;
			}
		}
		chapterNameList.clear();
		chapterNameList = null;
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

}
