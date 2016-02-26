package com.himoo.ydsc.reader.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.himoo.ydsc.aescrypt.AESCrypt;
import com.himoo.ydsc.bean.BaiduBook;
import com.himoo.ydsc.bean.BaiduBookChapter;
import com.himoo.ydsc.config.SpConstant;
import com.himoo.ydsc.http.BookDetailsTask;
import com.himoo.ydsc.reader.bean.BookOperation;
import com.himoo.ydsc.reader.bean.Chapter;
import com.himoo.ydsc.util.FileUtils;
import com.himoo.ydsc.util.SharedPreferences;
import com.umeng.socialize.utils.Log;

/**
 * 设计此类的目的是便于统一管理，从资源文件中读取数据。 app中的所有数据来源都可以通过这个类来提供，这样在将来重用代码的时候，也方便修改。
 * 而且在各个类的逻辑任务上也清晰可见。 其目前所做的任务包括，初始化Book类，从资源文件中读取章节内容来初始化Chapter对象。
 * 
 * @author MJZ
 * 
 */
public class IOHelper {
	private static BookOperation book;
	// private static Chapter chapter;
	private static String[] chapterPaths;
	private static Resources res;
	private static Context mContext;
	private static OnGetChapterListener mListener;

	/**
	 * 初始化Book类的唯一对象。 这个函数一般只会调用一次。
	 * 
	 * @param context
	 *            由于从文件中读取资源，则需要通过Activity 来提供。因此在Activity调用此函数的时候，会传入 this。
	 * @return
	 */
	public static BookOperation getBook(Context context, String bookName,String bookId,
			ArrayList<BaiduBookChapter> chapterList) {
		book = BookOperation.getInstance();
		mContext = context;
		book.setChapterList(chapterList);

		// res = context.getResources();
		// String booklists[] = res.getStringArray(R.array.booklists);
		// chapterPaths = res.getStringArray(R.array.content_path);
		//
		// // 设置Book 对象的信息
		// book.setAuthor(res.getString(R.string.author));
		book.setBookname(bookName);
		book.setBookId(bookId);
		//
		// // 下面這個if语句是因为出现个bug而写的，它其实不应该存在。
		// // 猜测的原因可能是在软件退出的时候，Book类对象没有被销毁，则再次启动软件的时候
		// // 又给它添加了一次章节信息
		// if (book.getChapterList().size() != booklists.length)
		// for (int i = 0; i < booklists.length; ++i)
		// book.addChapter(booklists[i]);

		return book;
	}

	/**
	 * 初始化Book类的唯一对象。 这个函数一般只会调用一次。
	 * 
	 * @param context
	 *            由于从文件中读取资源，则需要通过Activity 来提供。因此在Activity调用此函数的时候，会传入 this。
	 * @return
	 */
	public static BookOperation getBook(Context context, String bookName,String bookId,
			ArrayList<BaiduBookChapter> chapterList, BaiduBook baiduBook) {
		book = BookOperation.getInstance();
		mContext = context;
		book.setBaiduBook(baiduBook);
		book.setBookId(bookId);
		book.setChapterList(chapterList);

		// res = context.getResources();
		// String booklists[] = res.getStringArray(R.array.booklists);
		// chapterPaths = res.getStringArray(R.array.content_path);
		//
		// // 设置Book 对象的信息
		// book.setAuthor(res.getString(R.string.author));
		book.setBookname(bookName);
		//
		// // 下面這個if语句是因为出现个bug而写的，它其实不应该存在。
		// // 猜测的原因可能是在软件退出的时候，Book类对象没有被销毁，则再次启动软件的时候
		// // 又给它添加了一次章节信息
		// if (book.getChapterList().size() != booklists.length)
		// for (int i = 0; i < booklists.length; ++i)
		// book.addChapter(booklists[i]);

		return book;
	}

	/**
	 * 得到章节内容。
	 * 
	 * @param jumpType
	 *            跳转的类型，1表示从百度详情界面跳转过来，2表示从书架界面跳转过来
	 * @param index
	 * @param position
	 *            要读取的章节的顺序。
	 * @param bookType
	 *            1表示自己服务器上的书,2表示百度的书
	 * @return
	 */
	public static Chapter getChapter(int jumpType, String index, int position,
			int bookType) {
		if (book == null)
			return null;
		if (book.getChapterList() == null)
			return null;
		int length = book.getChapterList().size();
		if (position - length == 0 || position == -1)
			return null;
		position = position >= length ? length - 1 : position;
		Chapter chapter = new Chapter();
		chapter.setPosition(position);
		BaiduBookChapter bdchapter = book.getChapterList().get(position);
		chapter.setIndex(bdchapter.getIndex());
		chapter.setGid(bdchapter.getCid());
		chapter.setSrc(bdchapter.getHref());
		chapter.setBookName(book.getBookname());
		BaiduBookChapter bookChapter = book.getChapterList().get(position);
		String chapterName = bookChapter.getText().trim();
		chapter.setChapterName(chapterName);
		if (bookType == 2) {
			String chapterUrl = ChapterParseUtil.getChapterUrl(bookChapter);
			chapter.setChapterUrl(chapterUrl);
			if (jumpType == 1) {
				chapter.setJumpType(1);
				new GetChapterAsyncTask(mContext, chapterUrl, index, chapter)
						.execute();

			} else {
				chapter.setJumpType(2);
				File dirFile = new File(FileUtils.mSdRootPath
						+ "/CouldBook/baidu" + File.separator
						+ book.getBookname()+"_"+book.getBookId() + File.separator);

				File file = new File(dirFile, chapterName.replaceAll("/", "|")
						+ "-|" + book.getChapterList().get(position).getIndex()
						+ "-|" + position + ".txt");
				if (file.exists()) {
					final String content = com.himoo.ydsc.download.FileUtils
							.readFormSd(file);
					if (content == null || TextUtils.isEmpty(content)) {
						InputStream is;
						try {
							is = mContext.getAssets().open("error.txt");
							int size = is.available();
							byte[] buffer = new byte[size];
							is.read(buffer);
							is.close();
							String chapterContent = new String(buffer, "gbk");
							chapter.setContent(chapterContent);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							return null;
						}
					} else {
						chapter.setContent(content);
					}
				} else {
					if (SharedPreferences.getInstance().getBoolean(
							SpConstant.BOOK_SETTING_AUTO_LOAD, false)) {
						chapter.setContent(getChapterContent(index, chapterUrl));
						return chapter;
					}
				}
			}
			return chapter;
		} else if (bookType == 1) {
			File dirFile = new File(FileUtils.mSdRootPath
					+ "/CouldBook/download" + File.separator
					+ book.getBookname()+"_"+book.getBookId() + File.separator
					+ bookChapter.getIndex() + ".txt");
			if (dirFile != null && dirFile.exists()) {
				String bookContent = AESCrypt.readBookFile(dirFile
						.getAbsolutePath());
				if (bookContent == null || TextUtils.isEmpty(bookContent)) {
					InputStream is;
					try {
						is = mContext.getAssets().open("error.txt");
						int size = is.available();
						byte[] buffer = new byte[size];
						is.read(buffer);
						is.close();
						String chapterContent = new String(buffer, "gbk");
						chapter.setContent(chapterContent);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						Log.e("加载error.txt文件出错: " + e.getMessage());
					}
				} else {
					chapter.setContent(bookContent.replaceAll("　　    ",
							"        "));
				}

			}
			return chapter;

		}
		return null;

	}

	/**
	 * 获取章节的大小
	 * 
	 * @return
	 */
	public static int getChapterLength() {
		return book.getChapterList().size() - 1;
	}

	/**
	 * 获得目录列表
	 * 
	 * @return
	 */
	public static ArrayList<BaiduBookChapter> getBookChapter() {
		return book.getChapterList();
	}

	/**
	 * 百度书籍
	 * 
	 * @return
	 */
	public static BaiduBook getBaiduBook() {
		return book.getBaiduBook();
	}

	/**
	 * 开启线程获取小说章节, 防止在网络的不好的情况下出现应用程序无响应的现象
	 * 
	 */
	public static class GetChapterAsyncTask extends
			AsyncTask<Void, Void, String> {

		public Context mContext;
		public String mChapterUrl;
		public String mIndex;
		public Chapter mChapter;

		public GetChapterAsyncTask(Context context, String chapterUrl,
				String index, Chapter chapter) {
			this.mContext = context;
			this.mChapterUrl = chapterUrl;
			this.mIndex = index;
			this.mChapter = chapter;
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			if (mListener != null)
				mListener.onGetChapterPre();

		}

		@Override
		protected String doInBackground(Void... params) {
			// TODO Auto-generated method stub

			return getChapterContent(mIndex, mChapterUrl);
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			mChapter.setContent(result);
			if (mListener != null)
				mListener.onGetChapterSuccess(mChapter);
		}
	}

	/**
	 * 联网获取章节内容
	 * 
	 * @param index
	 * @param chapterUrl
	 * @return
	 */
	private static String getChapterContent(String index, String chapterUrl) {
		String chapterContent = BookDetailsTask.getInstance()
				.getChapterFormService(mContext, chapterUrl);
		if (!TextUtils.isEmpty(chapterContent)
				&& chapterContent.indexOf("\n", 0) != -1) {
			return chapterContent;
		} else {
			if (Integer.valueOf(index) >= 0) {
				try {
					InputStream is = mContext.getAssets().open("error.txt");
					int size = is.available();
					byte[] buffer = new byte[size];
					is.read(buffer);
					is.close();
					String content = new String(buffer, "gbk");
					return content;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					return null;
				}
			} else
				return null;
		}
	}

	public interface OnGetChapterListener {
		public void onGetChapterPre();

		public boolean onGetChapterSuccess(Chapter chapter);

		public void onGetChapterFailure();
	}

	public static void setOnChapterListener(OnGetChapterListener listener) {
		mListener = listener;
	}

}
