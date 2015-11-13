package com.himoo.ydsc.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.himoo.ydsc.R;
import com.himoo.ydsc.aescrypt.AESCrypt;
import com.himoo.ydsc.animation.mesh.BitmapMesh;
import com.himoo.ydsc.base.BaseApplication;
import com.himoo.ydsc.bean.BookDetails;
import com.himoo.ydsc.share.UmengShare;
import com.himoo.ydsc.util.DeviceUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

/**
 * 展示自己服务器的书籍详情
 * 
 */
public class BookDialogActivity extends FragmentActivity implements
		OnClickListener {

	private ScrollView dialog_summary_layout;
	private TextView book_name;
	private TextView book_author;
	private TextView book_statue;
	private TextView book_source;
	private TextView book_Popularity;
	private TextView book_score;
	private TextView book_summary;
	private ImageView book_image;
	private ImageView dialog_close;
	private Button dialog_btn_share;
	private Button dialog_btn_rate;
	private Button dialog_btn_download;
	private BookDetails bookDetails;
	private DisplayImageOptions option;
	private FrameLayout ani_mesh_layout;
	private BitmapMesh.SampleView mSampleView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_bookdetails);
		option = BaseApplication.getInstance().displayImageOptionsBuider(
				R.drawable.book_face_default);
		findViewById();
		initListener();
		initData();

	}

	private void findViewById() {
		dialog_summary_layout = (ScrollView) this
				.findViewById(R.id.dialog_summary_layout);
		book_name = (TextView) this.findViewById(R.id.book_name);
		book_author = (TextView) this.findViewById(R.id.book_author);
		book_statue = (TextView) this.findViewById(R.id.book_statue);
		book_source = (TextView) this.findViewById(R.id.book_source);
		book_Popularity = (TextView) this.findViewById(R.id.book_Popularity);
		book_score = (TextView) this.findViewById(R.id.book_score);
		book_summary = (TextView) this.findViewById(R.id.book_summary);
		ani_mesh_layout = (FrameLayout) this.findViewById(R.id.ani_mesh_layout);
		book_image = (ImageView) this.findViewById(R.id.book_image);
		dialog_close = (ImageView) this.findViewById(R.id.dialog_close);
		dialog_btn_share = (Button) this.findViewById(R.id.dialog_btn_share);
		dialog_btn_rate = (Button) this.findViewById(R.id.dialog_btn_rate);
		dialog_btn_download = (Button) this
				.findViewById(R.id.dialog_btn_download);

	}

	private void initListener() {
		dialog_close.setOnClickListener(this);
		dialog_btn_share.setOnClickListener(this);
		dialog_btn_rate.setOnClickListener(this);
		dialog_btn_download.setOnClickListener(this);
	}

	private void initData() {

		bookDetails = (BookDetails) getIntent().getExtras().getParcelable(
				"book");
		setSummaryHeight(dialog_summary_layout, bookDetails.getBook_Summary());
		// 设置值
		ImageLoader.getInstance().displayImage(bookDetails.getBook_Image(),
				book_image, option, new SimpleImageLoadingListener() {
					@Override
					public void onLoadingComplete(String imageUri, View view,
							Bitmap loadedImage) {
						// TODO Auto-generated method stub
						super.onLoadingComplete(imageUri, view, loadedImage);
						addAnimView(loadedImage);
					}

					@Override
					public void onLoadingFailed(String imageUri, View view,
							FailReason failReason) {
						// TODO Auto-generated method stub
						super.onLoadingFailed(imageUri, view, failReason);
						addAnimView(null);
					}

				});

		book_name.setText(bookDetails.getBook_Name());
		setTextBookNameSize(book_name, bookDetails.getBook_Name());
		book_author.setText("作者:" + bookDetails.getBook_Author());
		String statue = (bookDetails.getBook_Name().endsWith("更")) ? "连载"
				: "已完结";
		book_statue.setText("状态:" + statue);
		book_source.setText("来源:" + bookDetails.getBook_Source());
		book_Popularity.setText("人气:" + bookDetails.getBook_Popularity()
				+ "次下载");
		float rate = (float) bookDetails.getBook_Rate()
				/ bookDetails.getBook_RateNum();
		setTextRateDrawable(this, book_score, rate);
		String result = String.valueOf(rate).substring(0, 3);
		book_score.setText("评分:" + result);
		if (bookDetails.getBook_Summary().startsWith("　　")) {
			book_summary.setText(bookDetails.getBook_Summary().trim());
		} else
			book_summary.setText("　　" + bookDetails.getBook_Summary().trim());
	}

	/**
	 * 设置小说内容简介的高度
	 * 
	 * @param view
	 * @param bookSummary
	 */
	private void setSummaryHeight(View view, String bookSummary) {
		int length = bookSummary.length();
		int ViewHeight = 180;
		if (length < 100) {
			ViewHeight = 120;
		} else if (bookDetails.getBook_Summary().length() < 180) {
			ViewHeight = 150;
		}
		view.getLayoutParams().height = DeviceUtil.dip2px(this, ViewHeight);

	}

	/**
	 * 根据书名的长度设置字体的大小
	 * 
	 * @param textView
	 * @param bookName
	 */
	private void setTextBookNameSize(TextView textView, String bookName) {
		if (bookName != null) {
			int textSize = 18;
			int length = bookName.length();
			if (length < 10) {
				textSize = 28;
			} else if (length < 12) {
				textSize = 22;
			} else if (length < 15) {
				textSize = 18;
			} else if (length < 17) {
				textSize = 16;
			} else {
				textSize = 14;
			}
			textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize); // 22SP
		}

	}

	/**
	 * 设置评分等级图
	 * 
	 * @param textView
	 * @param rate
	 */
	private void setTextRateDrawable(Context context, TextView textView,
			float rate) {
		Drawable drawable = null;
		if (rate < 1.0) {
			drawable = context.getResources().getDrawable(R.drawable.rate_0);
		} else if (rate < 2.0) {
			drawable = context.getResources().getDrawable(R.drawable.rate_1);
		} else if (rate < 3.0) {
			drawable = context.getResources().getDrawable(R.drawable.rate_2);
		} else if (rate < 4.0) {
			drawable = context.getResources().getDrawable(R.drawable.rate_3);
		} else if (rate < 5.0) {
			drawable = context.getResources().getDrawable(R.drawable.rate_4);
		} else {
			drawable = context.getResources().getDrawable(R.drawable.rate_5);
		}

		textView.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable,
				null);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.dialog_close:
			finish();
			break;
		case R.id.dialog_btn_share:
			UmengShare.getInstance().setShareContent(this,
					bookDetails.getBook_Name(), bookDetails.getBook_Image(),
					bookDetails.getBook_Summary());
			// 注册友盟分享
			UmengShare.getInstance().addCustomPlatforms(this);

			break;
		case R.id.dialog_btn_rate:
			break;
		case R.id.dialog_btn_download:

			String content = AESCrypt.decryptBook();
			if (mSampleView != null) {
				mSampleView.setVisibility(View.VISIBLE);
				mSampleView.startAnimation(false);
			}
			Log.d("msg", content);
			Log.d("msg", bookDetails.getBook_Download());
			break;

		default:
			break;
		}
	}

	private void addAnimView(Bitmap bitmap) {
		if (bitmap == null)
			bitmap = BitmapFactory.decodeResource(getResources(),
					R.drawable.book_face_default);

		mSampleView = new BitmapMesh.SampleView(this, bitmap,
				DeviceUtil.dip2px(this, 104), DeviceUtil.dip2px(this, 136));
		mSampleView.setLayoutParams(new RelativeLayout.LayoutParams(-1, -1));
		int padding = DeviceUtil.dip2px(this, 2);
		mSampleView.setPadding(padding, padding, padding, padding);
		ani_mesh_layout.addView(mSampleView);
		mSampleView.setVisibility(View.GONE);

	}

}
