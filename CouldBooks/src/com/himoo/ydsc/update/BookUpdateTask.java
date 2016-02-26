package com.himoo.ydsc.update;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.himoo.ydsc.bean.BaiduBookChapter;
import com.himoo.ydsc.download.BaiduBookDownload;
import com.himoo.ydsc.download.BaiduInfo;
import com.himoo.ydsc.http.BookDetailsTask;
import com.himoo.ydsc.http.HttpConstant;
import com.himoo.ydsc.reader.utils.IOHelper;
import com.himoo.ydsc.reader.utils.LocalReaderUtil;
import com.himoo.ydsc.ui.utils.Toast;
import com.himoo.ydsc.util.FileUtils;

public class BookUpdateTask extends AsyncTask<Void, String, LastChapter> {

	private Context mContext;

	private String mBookName;
	private String bookId;

	private OnNewChapterUpdateListener mListener;

	private File dirFile;

	/** 更新模式 1单本书更新，2所有书都更新 */
	private int mUpdateType;

	/**
	 * 默认所有章节都是最新
	 */
	private boolean isAllNewChapter = true;

	/**
	 * 
	 * @param context
	 *            上下文
	 * @param bookName
	 *            书名
	 * @param updateType
	 *            更新模式 1单本书更新，2所有书都更新
	 */
	public BookUpdateTask(Context context, String bookName, String bookId,int updateType) {
		this.mContext = context;
		this.mBookName = bookName;
		this.bookId = bookId;
		this.mUpdateType = updateType;
	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();

		Toast.show(mContext, mUpdateType == 1 ? "正在检查更新" : "检查所有图书中");

	}

	@Override
	protected LastChapter doInBackground(Void... params) {
		// TODO Auto-generated method stub
		if (mUpdateType == 1) {
			BaiduInfo book = BaiduBookDownload.getInstance(mContext)
					.queryNeedUpdate(mBookName,this.bookId);
			if (book != null) {
				String lastChapter = book.getLastChapterName();
				String content = BookDetailsTask.getInstance()
						.geLasttChapterFormService(mContext, book.getLastUrl());
				if (content != null) {
					String newChapter = parseLastChapterName(content);
					if (newChapter != null && lastChapter != null) {
						// 表示有新的章节，需要下载
						if (!newChapter.equals(lastChapter)) {
							ArrayList<BaiduBookChapter> list = praseBaiduBookChapter(content);
							if (list != null && !list.isEmpty()) {
								updateNewChapter(mBookName,book.getBookId(),list);
							}

							BaiduBookDownload.getInstance(mContext)
									.updateChapterName(book, newChapter);
							IOHelper.getBook(mContext, mBookName,book.getBookId(),
									LocalReaderUtil.getInstance()
											.parseLocalBook(mBookName, book.getBookId(),2));
							LastChapter chapter = new LastChapter(
									book.getBookName(), newChapter);
							return chapter;

						}
					}
				}
			} else {
				return null;
			}
		} else if (mUpdateType == 2) {
			List<BaiduInfo> list = BaiduBookDownload.getInstance(mContext)
					.queryNeedUpdate();
			int bookCount = BaiduBookDownload.getInstance(mContext)
					.queryBookCount();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (list == null || list.isEmpty()) {
				onProgressUpdate("  0本/ " + bookCount + "本          ");
			}
			if (list != null && !list.isEmpty()) {
				onProgressUpdate("      " + list.size() + "本/" + bookCount
						+ "本       ");
				for (int i = 0; i < list.size(); i++) {
					BaiduInfo book = list.get(i);
					String lastChapter = book.getLastChapterName();
					String content = BookDetailsTask.getInstance()
							.geLasttChapterFormService(mContext,
									book.getLastUrl());
					if (content != null) {
						String newChapter = parseLastChapterName(content);
						if (newChapter != null && lastChapter != null) {
							if (!newChapter.equals(lastChapter)) {
								isAllNewChapter = false;
								ArrayList<BaiduBookChapter> listChapter = praseBaiduBookChapter(content);
								if (list != null && !list.isEmpty()) {
									updateNewChapter(book.getBookName(),book.getBookId(),
											listChapter);
								}
								BaiduBookDownload.getInstance(mContext)
										.updateChapterName(book, newChapter);
								try {
									Thread.sleep(1000);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								onProgressUpdate(book.getBookName());

							}
						}
					}
				}
				return null;
			}

		}

		return null;
	}

	@Override
	protected void onProgressUpdate(String... values) {
		// TODO Auto-generated method stub
		super.onProgressUpdate(values);
		if (mListener != null)
			mListener.onUpdateProgress(values[0]);

	}

	@Override
	protected void onPostExecute(LastChapter result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		if (mListener != null) {
			if (mUpdateType == 2) {
				if (isAllNewChapter)
					mListener.onUpdateAllNewChapter();
				else
					mListener.onUpdateSuccess(result);
			} else {
				if (result != null)
					mListener.onUpdateSuccess(result);
				else
					mListener.onUpdateFailure();
			}
		}

	}

	/**
	 * 设置书籍更新监听器
	 * 
	 * @param listener
	 */
	public void setOnNewChapterListener(OnNewChapterUpdateListener listener) {
		this.mListener = listener;
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
	private void updateNewChapter(String bookName,String bookId,
			ArrayList<BaiduBookChapter> list) {
		// TODO Auto-generated method stub
		int allChapterLength = list.size();
		dirFile = new File(FileUtils.mSdRootPath + "/CouldBook/baidu"
				+ File.separator + bookName+"_"+bookId + File.separator);
		if (!dirFile.exists())
			dirFile.mkdirs();
		ArrayList<BaiduBookChapter> localList = LocalReaderUtil.getInstance()
				.parseLocalBook(bookName,bookId, 2);
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
			//判断这大于10章的可有重名的，有则不下载 ,没有则下载
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

	public interface OnNewChapterUpdateListener {

		public void onUpdateSuccess(LastChapter chapter);

		public void onUpdateProgress(String bookName);

		public void onUpdateFailure();

		public void onUpdateAllNewChapter();
	}

}
