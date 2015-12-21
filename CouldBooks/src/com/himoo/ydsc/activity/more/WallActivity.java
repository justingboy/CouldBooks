package com.himoo.ydsc.activity.more;

import java.util.ArrayList;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.himoo.ydsc.R;
import com.himoo.ydsc.aescrypt.AESCrypt;
import com.himoo.ydsc.animation.AnimationUtils;
import com.himoo.ydsc.reader.utils.ZipExtractorTask;
import com.himoo.ydsc.ui.swipebacklayout.SwipeBackActivity;
import com.himoo.ydsc.ui.utils.Toast;
import com.himoo.ydsc.util.FileUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.umeng.onlineconfig.OnlineConfigAgent;

public class WallActivity extends SwipeBackActivity {

	@ViewInject(R.id.tv_bottombar)
	private LinearLayout tv_bottom;

	@ViewInject(R.id.tv_titlebar)
	private LinearLayout tv_titlebar;

	@ViewInject(R.id.layout_more_rel)
	private RelativeLayout layout_more_rel;

	private boolean isUp = false;
	boolean isMove = false;

	// @ViewInject(R.id.iv)
	// private ImageView iv;

	// int count = 0;
	// int progress = 0;
	// ArrowDownloadButton button;
	//
	// public Handler mHandler = new Handler() {
	// public void handleMessage(android.os.Message msg) {
	// Bitmap bm = BitmapFactory.decodeStream((InputStream) msg.obj);
	// if (bm == null) {
	// Log.d("bm ==null");
	// iv.setImageResource(R.drawable.book_face_default);
	// } else {
	// Log.d("正常");
	// iv.setImageBitmap(bm);
	// }
	//
	// }
	// };

	private LinearLayout dialog_layout_rating;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_more_wall);
		dialog_layout_rating = (LinearLayout) this
				.findViewById(R.id.dialog_layout_rating);
		final Button dialog_btn_rate = (Button) this
				.findViewById(R.id.dialog_btn_rate);
		LayoutParams parmas = (LayoutParams) tv_bottom.getLayoutParams();
		parmas.height = 520;
		LayoutParams parmas2 = (LayoutParams) tv_titlebar.getLayoutParams();
		parmas2.height = 100;
		tv_bottom.setLayoutParams(parmas);
		tv_titlebar.setLayoutParams(parmas2);

		// BookSettingFragmentAdapter mAdapter = new BookSettingFragmentAdapter(
		// getSupportFragmentManager(),this);

		// ViewPager mPager = (ViewPager) findViewById(R.id.pager);
		// mPager.setAdapter(mAdapter);
		//
		// CirclePageIndicator mIndicator = (CirclePageIndicator)
		// findViewById(R.id.indicator);
		// mIndicator.setViewPager(mPager);

		tv_bottom.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.showShort(WallActivity.this, "可以被点");
			}
		});
		dialog_btn_rate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				AnimationUtils.setViewRotating(WallActivity.this,dialog_btn_rate);
				
//				String value = OnlineConfigAgent.getInstance().getConfigParams(
//						WallActivity.this, "adstatue");
//				if (value.equals("1")) {
//					Toast.showLong(WallActivity.this, "在线参数为" + value);
//				}
//				int index = (int) (Math.random() * 10);
//				FileUtils fileUtils = new FileUtils(WallActivity.this);
//				String filePath = fileUtils.getStorageDirectory()
//						+ "一问一世界-2334/index" + ".txt";
//				// String inPath = fileUtils.getStorageDirectory() +
//				// "一问一世界-2334.zip";
//				// String outPath = fileUtils.getStorageDirectory() +
//				// "一问一世界-2334";
//				// doZipExtractorWork(inPath, outPath);
//				String bookContent = AESCrypt.readBookFile(filePath);
//				ArrayList<MyChapter> list = new ArrayList<MyChapter>();
//				String chapter[] = bookContent.split("\n");
//				for (int i = 0; i < chapter.length; i++) {
//					MyChapter ch = new MyChapter();
//					String[] str = chapter[i].split("\\|\\|\\|");
//					ch.setChapterName(str[0]);
//					ch.setPosition(Integer.valueOf(str[1].split("\\.")[0].trim()));
//					list.add(ch);
//				}
//				Log.i("chapter = " + list.size());
//
//				Toast.showLong(WallActivity.this, bookContent);

			}

			// BookView bookView = (BookView) findViewById(R.id.book);
			// bookView.setImageResource(R.drawable.book_face_default);
			// // String url =
			// //
			// "http://bj.bs.baidu.com/wise-novel-authority-logo/4d6be3fc2772e80faf13b69dc8557fa3.jpg";
			// // String url2 =
			// //
			// "http://bj.bs.baidu.com/wise-novel-authority-logo/88022e5b48ce48b636ed1b24aba09653.jpg";
			// String url3 =
			// "http://bj.bs.baidu.com/wise-novel-authority-logo/adf66ea1f9f6c7a3f73b924161d5c793.jpg";
			// getBitamp(url3);
			//
			// button = (ArrowDownloadButton)
			// findViewById(R.id.arrow_download_button);
			// button.setOnClickListener(new View.OnClickListener() {
			// @Override
			// public void onClick(View v) {
			// if ((count % 2) == 0) {
			// button.startAnimating();
			// Timer timer = new Timer();
			// timer.schedule(new TimerTask() {
			// @Override
			// public void run() {
			// runOnUiThread(new Runnable() {
			// @Override
			// public void run() {
			// progress = progress + 1;
			// button.setProgress(progress);
			// }
			// });
			// }
			// }, 800, 500);
			// } else {
			// button.reset();
			// }
			// count++;
			// }
		});

	}

	@Override
	protected void initTitleBar() {
		// TODO Auto-generated method stub
		mTitleBar.setLeftTitle("更多");
		mTitleBar.setTitle("积分墙");
		mTitleBar.setRightLogoGone();
	}

	/**
	 * 加压zip文件
	 * 
	 * @param inPath
	 * @param outPath
	 */
	public void doZipExtractorWork(String inPath, String outPath) {
		ZipExtractorTask task = new ZipExtractorTask(inPath, outPath, this,
				true);
		task.execute();
	}

	public class MyChapter {
		private String chapterName;
		private int position;

		public String getChapterName() {
			return chapterName;
		}

		public void setChapterName(String chapterName) {
			this.chapterName = chapterName;
		}

		public int getPosition() {
			return position;
		}

		public void setPosition(int position) {
			this.position = position;
		}

	}

}
