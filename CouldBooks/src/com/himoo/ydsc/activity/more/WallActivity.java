package com.himoo.ydsc.activity.more;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.himoo.ydsc.R;
import com.himoo.ydsc.adapter.TestFragmentAdapter;
import com.himoo.ydsc.animation.AnimationUtils;
import com.himoo.ydsc.fragment.reader.BookSettingFragment1.OnFragment1Listener;
import com.himoo.ydsc.ui.swipebacklayout.SwipeBackActivity;
import com.himoo.ydsc.ui.utils.Toast;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.viewpagerindicator.CirclePageIndicator;

public class WallActivity extends SwipeBackActivity implements OnFragment1Listener{

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
		Button dialog_btn_rate = (Button) this
				.findViewById(R.id.dialog_btn_rate);
		LayoutParams parmas = (LayoutParams) tv_bottom.getLayoutParams();
		parmas.height = 520;
		LayoutParams parmas2 = (LayoutParams) tv_titlebar.getLayoutParams();
		parmas2.height = 100;
		tv_bottom.setLayoutParams(parmas);
		tv_titlebar.setLayoutParams(parmas2);
		AnimationUtils.setViewTranslateDownY(tv_bottom, 0f, 520);
		AnimationUtils.setViewTranslateDownY(tv_titlebar, 0, -100f);

		TestFragmentAdapter mAdapter = new TestFragmentAdapter(
				getSupportFragmentManager(),this);

		ViewPager mPager = (ViewPager) findViewById(R.id.pager);
		mPager.setAdapter(mAdapter);

		CirclePageIndicator mIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
		mIndicator.setViewPager(mPager);

		tv_bottom.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.showShort(WallActivity.this, "可以被点");
			}
		});
		// dialog_btn_rate.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// // int index = (int) (Math.random()*10);
		// // FileUtils fileUtils = new FileUtils(WallActivity.this);
		// // String filePath =
		// // fileUtils.getStorageDirectory()+"1"+index+".txt";
		// // String bookContent = AESCrypt.readBookFile(filePath);
		// // Toast.showLong(WallActivity.this, bookContent);
		// AnimationUtils.setViewTranslateUpY(tv_titlebar, 0, 120f);
		// if (!isUp) {
		//
		// AnimationUtils.setViewTranslateUpY(tv_bottom, 280, 0f);
		// } else {
		// AnimationUtils.setViewTranslateDownY(tv_bottom, 0f, 280);
		//
		// AnimationUtils.setViewTranslateDownY(tv_titlebar, 0, -120f);
		// }
		//
		// isUp = !isUp;
		// }
		//
		// });
		layout_more_rel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				// Toast.showShort(WallActivity.this, "Relative --->可以点击");
				if (!isUp) {
					AnimationUtils.setViewTranslateUpY(tv_titlebar, -100f, 0f);
					AnimationUtils.setViewTranslateUpY(tv_bottom, 520, 0f);
				} else {
					AnimationUtils.setViewTranslateDownY(tv_bottom, 0f, 520);
					AnimationUtils.setViewTranslateDownY(tv_titlebar, 0f, -100);
				}

				isUp = !isUp;
			}
		});

		// layout_more_rel.setOnTouchListener(new OnTouchListener() {
		//
		// private float startX;
		// private float startY;
		//
		// @Override
		// public boolean onTouch(View v, MotionEvent event) {
		// // TODO Auto-generated method stub
		// int action = event.getAction();
		// switch (action) {
		// case MotionEvent.ACTION_DOWN:
		// Toast.showShort(WallActivity.this, "ACTION_DOWN");
		// startX = event.getX();
		// startY = event.getY();
		// break;
		// case MotionEvent.ACTION_MOVE:
		// // Toast.showShort(WallActivity.this,
		// // "移动--------------？？？？");
		// isMove = true;
		// break;
		// case MotionEvent.ACTION_UP:
		//
		// if (Math.abs(event.getX() - startX) < 15
		// || Math.abs(event.getY() - startY)< 15) {
		// Toast.showShort(WallActivity.this, "点击-----》");
		// } else {
		// Toast.showShort(WallActivity.this, "滑动---》");
		// }
		//
		// break;
		//
		// default:
		// break;
		// }
		//
		// return true;
		// }
		// });

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
		// });

	}

	private void startRatingAni() {
		/** 设置位移动画 向右位移150 */
		// TranslateAnimation animation = new TranslateAnimation(
		// Animation.RELATIVE_TO_PARENT, 0, Animation.RELATIVE_TO_PARENT, 0,
		// Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT,
		// 0.5f);
		TranslateAnimation animation = new TranslateAnimation(0, 0, 0, -100);
		animation.setDuration(1000);// 设置动画持续时间
		animation.setFillAfter(true);
		dialog_layout_rating.setAnimation(animation);
		animation.startNow();
		// ratingBar_layout.setVisibility(View.VISIBLE);
		// dialog_btn_layout.setVisibility(View.GONE);
	}

	@Override
	protected void initTitleBar() {
		// TODO Auto-generated method stub
		mTitleBar.setLeftTitle("更多");
		mTitleBar.setTitle("积分墙");
		mTitleBar.setRightLogoGone();
	}

	@Override
	public void onTextSizChange(float textSize) {
		// TODO Auto-generated method stub
		Toast.showLong(this, "字体大小发生了改变！"+textSize);
		
		
	}

	// private void getBitamp(final String urlString) {
	// new Thread(new Runnable() {
	//
	// @Override
	// public void run() {
	// // TODO Auto-generated method stub
	// InputStream in = BookDetailsTask.getInstance().executeByGet(
	// urlString);
	// Message msg = mHandler.obtainMessage();
	// msg.obj = in;
	// mHandler.sendMessage(msg);
	// }
	// }).start();
	// }

}
