package com.himoo.ydsc.update;

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

import com.himoo.ydsc.R;
import com.himoo.ydsc.config.SpConstant;
import com.himoo.ydsc.download.BaiduBookDownload;
import com.himoo.ydsc.download.BaiduInfo;
import com.himoo.ydsc.http.BookDetailsTask;
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
		Log.i("msg", "onStartCommand");
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
				.setContentTitle(chapter.bookName)
				.setContentText(chapter.chapterName).setAutoCancel(true);
		boolean isPlaySound = SharedPreferences.getInstance().getBoolean(
				SpConstant.BOOK_UPATE_SETTING_SOUND, false);
		if (isPlaySound) {
			mBuilder.setDefaults(NotificationCompat.DEFAULT_SOUND);
		}

		// Creates an explicit intent for an Activity in your app
		Intent resultIntent = new Intent();
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
								LastChapter chapter = new LastChapter(
										book.getBookName(), newChapter);
								onProgressUpdate(chapter);
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

	public class LastChapter {
		String bookName;
		String chapterName;

		public LastChapter(String bookName, String chapterName) {
			this.bookName = bookName;
			this.chapterName = chapterName;
		}
	}

}
