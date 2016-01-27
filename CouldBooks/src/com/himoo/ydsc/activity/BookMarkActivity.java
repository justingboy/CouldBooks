package com.himoo.ydsc.activity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.himoo.ydsc.R;
import com.himoo.ydsc.animation.AnimationUtils;
import com.himoo.ydsc.base.BaseActivity;
import com.himoo.ydsc.bean.BaiduBook;
import com.himoo.ydsc.bean.BaiduBookChapter;
import com.himoo.ydsc.config.BookTheme;
import com.himoo.ydsc.download.BaiduBookDownload;
import com.himoo.ydsc.fragment.BookShelfFragment;
import com.himoo.ydsc.fragment.BookShelfFragment.BookDownloadReceiver;
import com.himoo.ydsc.fragment.bookmark.BookMarkFragment;
import com.himoo.ydsc.fragment.bookmark.CatalogFragment;
import com.himoo.ydsc.http.BookDetailsTask;
import com.himoo.ydsc.http.HttpConstant;
import com.himoo.ydsc.notification.DownlaodNotification;
import com.himoo.ydsc.reader.dao.BookMark;
import com.himoo.ydsc.reader.dao.BookMarkDb;
import com.himoo.ydsc.reader.utils.IOHelper;
import com.himoo.ydsc.reader.utils.LocalReaderUtil;
import com.himoo.ydsc.ui.utils.Toast;
import com.himoo.ydsc.ui.utils.ViewSelector;
import com.himoo.ydsc.update.BookUpdateTask;
import com.himoo.ydsc.update.BookUpdateTask.OnNewChapterUpdateListener;
import com.himoo.ydsc.update.LastChapter;
import com.himoo.ydsc.util.FileUtils;
import com.himoo.ydsc.util.MyLogger;
import com.himoo.ydsc.util.NetWorkUtils;
import com.himoo.ydsc.util.SP;
import com.himoo.ydsc.util.SharedPreferences;
import com.ios.dialog.AlertDialog;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.viewpagerindicator.TabPageIndicator;
import com.viewpagerindicator.TabPageIndicator.OnTabReselectedListener;

public class BookMarkActivity extends BaseActivity implements OnClickListener,
		OnNewChapterUpdateListener {

	@ViewInject(R.id.book_close)
	private ImageView book_close;

	@ViewInject(R.id.book_refresh)
	private ImageView book_refresh;

	/** Tab标题 */
	private static final String[] TITLE = new String[] { "目录", "书签" };

	/** 通知广播的Action */
	private static final String ACTION = "com.himoo.ydsc.catalog.receiver";

	/** 通知广播的Action 书架 */
	private static final String BOOKSHELF_ACTION = "com.himoo.ydsc.shelf.receiver";

	/** Fragment */
	private CatalogFragment catalogFragment = null;
	private Fragment bookmarkFragment = null;
	private List<Fragment> fragmentList;

	/** TabPageIndicator */
	@ViewInject(R.id.book_pager_indicator)
	private TabPageIndicator tabPageIndicator;

	/** NoScrollViewPager */
	@ViewInject(R.id.viewpager_bookmark)
	private ViewPager viewPager;

	private RelativeLayout mTitleBar;

	private String bookName;

	private String statue;

	private int type;

	private int bookType;

	private boolean isDownload = false;

	private DownlaodNotification downNotification;

	private CatalogFragment.BookUpdateReceiver receiver;
	/** 广播通知已经下载了 */
	private BookDownloadReceiver downloadReceiver;

	private String gid;
	private String lastUrl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bookmark);
		ViewUtils.inject(this);
		initView();
		initEvent();

		registeBookShelfBroadcast();
	}

	private void initView() {
		Intent intent = getIntent();
		bookName = intent.getStringExtra("bookName");
		gid = intent.getStringExtra("gid");
		lastUrl = intent.getStringExtra("lastUrl");
		isDownload = isDownlaod(bookName);
		type = intent.getIntExtra("type", 1);
		bookType = intent.getIntExtra("bookType", 2);
		statue = intent.getStringExtra("statue");
		mTitleBar = (RelativeLayout) this
				.findViewById(R.id.bookmark_tltle_layout);
		mTitleBar.setBackgroundColor(BookTheme.THEME_COLOR);
		book_refresh.setTag("刷新");
		setRightLogo();
		book_close.setOnClickListener(this);
		book_refresh.setOnClickListener(this);

	}

	/**
	 * 判断该书是否下载过
	 * 
	 * @param bookName
	 * @return
	 */
	private boolean isDownlaod(String bookName) {
		return BaiduBookDownload.getInstance(this).isDownload(bookName);
	}

	/**
	 * 初始化Fragment
	 */
	private void initFragmentList() {
		fragmentList = new ArrayList<Fragment>();
		fragmentList.add(catalogFragment);
		fragmentList.add(bookmarkFragment);
	}

	@Override
	protected void initEvent() {
		// TODO Auto-generated method stub
		initFragmentList();
		TabPageIndicatorAdapter adapter = new TabPageIndicatorAdapter(
				getSupportFragmentManager());

		viewPager.setAdapter(adapter);
		// 缓存当前界面每一侧的界面数(2个)
		viewPager.setOffscreenPageLimit(1);
		tabPageIndicator.setViewPager(viewPager);// 关联上
		tabPageIndicator
				.setOnTabReselectedListener(new OnTabReselectedListener() {

					@Override
					public void onTabReselected(int position) {
						// TODO Auto-generated method stub
						if (position == 0) {
							if (!SP.getInstance().getBoolean("isDown", false)) {
								AnimationUtils.setViewRotating(
										BookMarkActivity.this, book_refresh);
							}
							book_refresh.setTag("刷新");
							setRightLogo();
						}

						else if (position == 1) {
							if (!SP.getInstance().getBoolean("isDown", false)) {
								AnimationUtils.cancelAnim(book_refresh);
							}
							book_refresh.setImageResource(R.drawable.ic_delete);
							book_refresh.setEnabled(true);
							book_refresh.setVisibility(View.VISIBLE);
							book_refresh.setTag("删除");
						}
					}
				});
		tabPageIndicator.setTextColor(ViewSelector.createColorStateList(
				BookTheme.THEME_COLOR, BookTheme.BOOK_WHITE));
	}

	/**
	 * ViewPager适配器
	 */
	class TabPageIndicatorAdapter extends FragmentPagerAdapter {
		public TabPageIndicatorAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {

			if (fragmentList.get(position) == null && position == 0) {
				catalogFragment = CatalogFragment.newInstance(bookName, type,
						bookType);
				fragmentList.add(catalogFragment);
				return catalogFragment;
			} else if (fragmentList.get(position) == null && position == 1) {
				bookmarkFragment = BookMarkFragment.newInstance(bookName,
						bookType);
				fragmentList.add(bookmarkFragment);
				return bookmarkFragment;
			} else

				return fragmentList.get(position);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return TITLE[position % TITLE.length];
		}

		@Override
		public int getCount() {
			return TITLE.length;
		}

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.book_close:
			finish();
			overridePendingTransition(0, R.anim.dialog_exit_bottom);
			break;
		case R.id.book_refresh:
			if (book_refresh.getTag().equals("刷新")) {
				if (type == 1) {
					Toast.showShort(this, "下载整本书！");
				} else if (type == 2) {
					int updateType = SharedPreferences.getInstance().getInt(
							"book_update_type", 2);
					if (updateType == 1) {
						new AlertDialog(this)
								.builder()
								.setTitle("提醒")
								.setMsg("您现在选择的是整本更新模式，我们会更新您的整本书，这将会把您本地的书籍全部重新下载,是否继续。")
								.setNegativeButton("取消", new OnClickListener() {

									@Override
									public void onClick(View v) {
										// TODO Auto-generated method stub

									}
								})
								.setPositiveButton("确定", new OnClickListener() {

									@Override
									public void onClick(View v) {
										// TODO Auto-generated method stub
										if (NetWorkUtils
												.isNetConnected(BookMarkActivity.this)) {
											registeBroadcast();
											sendToBookMarkBroadcast(true);
											sendToBookShelfBroadcastSucess(true);
											new DeleteFileTask(
													BookMarkActivity.this,
													bookName)
													.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
										} else {
											Toast.show(BookMarkActivity.this,
													"未连接网络!");
										}

									}
								}).show();
					} else {
						if (NetWorkUtils.isNetConnected(BookMarkActivity.this)) {
							AnimationUtils.setViewRotating(this, book_refresh);
							BookUpdateTask task = new BookUpdateTask(this,
									bookName, 1);
							task.setOnNewChapterListener(this);
							task.execute();
						} else {
							Toast.show(BookMarkActivity.this, "未连接网络!");
						}
					}
				}

			} else if (book_refresh.getTag().equals("删除")) {
				new AlertDialog(this).builder().setTitle("提示")
						.setMsg("您现在可以向右滑动某个条目,出现删除按钮,点击删除即可删除该书签,赶快去试试吧！")
						.setNegativeButton("取消", new OnClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub

							}
						}).setPositiveButton("确定", new OnClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub

							}
						}).show();
			} else if (book_refresh.getTag().equals("下载")) {
				Toast.show(this, "开始下载《" + bookName + "》");
				book_refresh.setImageResource(R.drawable.refresh_book);
				AnimationUtils.setViewRotating(this, book_refresh);
				downNotification = new DownlaodNotification(this);
				try {
					BaiduBookDownload.getInstance(this).addBaiduBookDownload(
							IOHelper.getBaiduBook());
				} catch (DbException e) {
					// TODO Auto-generated catch block
					Log.e("插入数据库失败" + e.getMessage());
				}
				List<BaiduBookChapter> list = IOHelper.getBookChapter();
				new SaveAsyncTask(list, lastUrl, false)
						.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				sendToBookShelfBroadcast();
			}

			break;

		default:
			break;
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			overridePendingTransition(0, R.anim.dialog_exit_bottom);
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 设置右边的图片
	 */
	private void setRightLogo() {
		if (type == 1) {
			if (isDownload) {
				BaiduBook book = IOHelper.getBaiduBook();
				if (book != null) {
					String statue = book.getStatus();
					if (statue != null && statue.endsWith("完结")) {
						book_refresh.setVisibility(View.INVISIBLE);
					} else {
						book_refresh.setImageResource(R.drawable.refresh_book);
						book_refresh.setTag("刷新");
					}
				} else {
					book_refresh.setImageResource(R.drawable.refresh_book);
					book_refresh.setTag("刷新");
				}

			} else {
				book_refresh.setTag("下载");
				book_refresh.setImageResource(R.drawable.buttombar_download);
			}
		} else if (type == 2) {
			if (statue != null && !statue.equals("完结"))
				book_refresh.setImageResource(R.drawable.refresh_book);
			else {
				book_refresh.setEnabled(false);
				book_refresh.setVisibility(View.GONE);
			}

		}
	}

	/**
	 * 注册书架的广播
	 */
	private void registeBookShelfBroadcast() {
		downloadReceiver = new BookShelfFragment.BookDownloadReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(BOOKSHELF_ACTION);
		registerReceiver(downloadReceiver, filter);

	}

	/**
	 * 注册广播书签Fragment
	 */
	private void registeBroadcast() {
		receiver = catalogFragment.new BookUpdateReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION);
		registerReceiver(receiver, filter);
	}

	/**
	 * 发送广播到书签Fragment
	 * 
	 * @param isDown
	 */
	private void sendToBookMarkBroadcast(boolean isDown) {
		Intent intent = new Intent(ACTION);
		intent.putExtra("isDownloading", isDown);
		sendBroadcast(intent);
	}

	/**
	 * 发送广播到书签Fragment
	 * 
	 * @param isDown
	 */
	private void sendToBookShelfBroadcast() {
		Intent intent = new Intent(BOOKSHELF_ACTION);
		intent.putExtra("bookName", bookName);
		String url = HttpConstant.BAIDU_BOOK_DETAILS_URL + "appui=alaxs&gid="
				+ gid + "&dir=1&ajax=1";
		intent.putExtra("dowloadUrl", url);
		sendBroadcast(intent);
	}

	/**
	 * 保存是否正在下载
	 * 
	 * @param isDown
	 */
	private void sendToBookShelfBroadcastSucess(boolean isDown) {
		SP.getInstance().putBoolean("isDown", !isDown);
		SP.getInstance().putBoolean(bookName, !isDown);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (receiver != null)
			unregisterReceiver(receiver);
		if (downloadReceiver != null)
			unregisterReceiver(downloadReceiver);
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
		private boolean isAfreashDownload;

		public SaveAsyncTask(List<BaiduBookChapter> list, String lastUrl,
				boolean isAfreashDownload) {
			this.lastUrl = lastUrl;
			this.isAfreashDownload = isAfreashDownload;
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
					+ File.separator + bookName + File.separator);
			if (!dirFile.exists())
				dirFile.mkdirs();
		}

		@Override
		protected String doInBackground(Void... params) {
			// TODO Auto-generated method stub
			int len = 0;
			int progress = 0;
			if (list == null) {
				String content = BookDetailsTask.getInstance()
						.geLasttChapterFormService(BookMarkActivity.this,
								lastUrl);
				if (content != null) {
					ArrayList<BaiduBookChapter> list = praseBaiduBookChapter(content);
					if (list != null && !list.isEmpty()) {
						// updateNewChapter(bookName, list);
						this.list = list;
					}
				}
			}

			// 保存章节的换源信息
//			ChapterDb.getInstance().createDb(BookMarkActivity.this, bookName);
//			ChapterDb.getInstance().saveBookChapter(list);

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
							downNotification
									.creatNotification(bookName, "开始下载");
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
										downNotification
												.setProgressAndNetSpeed(
														progress,
														downNotification
																.getNetSpeed());
									}
								}

							}
						}
					}

					if (allChapterLength - len == 0) {
						if (downNotification != null) {
							downNotification
									.creatNotification(bookName, "下载完成");
							downNotification.setProgressAndNetSpeed(100,
									downNotification.getNetSpeed());
						}
					}

				}
				if (isAfreashDownload) {

					// 下载之后可能会有新的章节，需要重新初始化章节列表
					IOHelper.getBook(
							BookMarkActivity.this,
							bookName,
							LocalReaderUtil.getInstance().parseLocalBook(
									bookName, 2));
					BookMark bookMark = BookMarkDb.getInstance(
							BookMarkActivity.this, "book").querryReaderPos(
							bookName);
					if (list != null && bookMark != null
							&& list.size() - 1 > bookMark.getPosition()) {
						bookMark.setPosition(list.size() - 1);
						bookMark.setCurrentPage(-1);
						bookMark.setPageCount(-1);
						BookMarkDb.getInstance(BookMarkActivity.this, "book")
								.saveReaderPosition(bookMark);
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
			Toast.show(BookMarkActivity.this, "《" + bookName + "》下载完成");
			AnimationUtils.cancelAnim(book_refresh);
			sendToBookMarkBroadcast(false);
			sendToBookShelfBroadcastSucess(false);
			BaiduBookDownload.getInstance(BookMarkActivity.this).updateDownSuccess(bookName);
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					downNotification.notifiManger.cancelAll();
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
	 * 开启线程删除本地书籍
	 * 
	 */
	public class DeleteFileTask extends AsyncTask<Void, Void, Void> {
		private String bookName;
		private File dirFile;
		private Context context;
		private FileUtils fileUtils;

		public DeleteFileTask(Context context, String bookName) {
			this.bookName = bookName;
			this.context = context;
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			downNotification = new DownlaodNotification(context);
			AnimationUtils.setViewRotating(context, book_refresh);
			Toast.show(context, "正在删除《" + bookName + "》");
			fileUtils = new FileUtils(context);
			dirFile = new File(FileUtils.mSdRootPath + "/CouldBook/baidu"
					+ File.separator + bookName + File.separator);
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
			Toast.show(context, "删除《" + bookName + "》成功");
			new SaveAsyncTask(null, lastUrl, true)
					.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					Toast.show(context, "重新下载《" + bookName + "》");
				}
			}, 2000);

		}
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

	@Override
	public void onUpdateSuccess(LastChapter chapter) {
		// TODO Auto-generated method stub
		registeBroadcast();
		Toast.show(this, "最新章节更新成功 ");
		AnimationUtils.cancelAnim(book_refresh);
	}

	@Override
	public void onUpdateFailure() {
		// TODO Auto-generated method stub
		Toast.show(this, "   暂无更新    ");
		AnimationUtils.cancelAnim(book_refresh);
	}

	@Override
	public void onUpdateProgress(String bookName) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUpdateAllNewChapter() {
		// TODO Auto-generated method stub

	}

}
