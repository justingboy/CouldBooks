package com.himoo.ydsc.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.himoo.ydsc.R;
import com.himoo.ydsc.base.BaseApplication;
import com.himoo.ydsc.bean.BookDetails;
import com.himoo.ydsc.share.UmengShare;
import com.himoo.ydsc.util.DeviceUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 点击书列表弹出书的详请Dialog
 * 
 */
public class BookDetailsDialog extends Dialog {
	/** 下载图片的配置参数 */
	private static DisplayImageOptions option;

	public BookDetailsDialog(Context context) {
		super(context);
	}

	public BookDetailsDialog(Context context, int theme) {
		super(context, theme);
		option = BaseApplication.getInstance().displayImageOptionsBuider(
				R.drawable.book_face_default);
	}

	public static class Builder implements View.OnClickListener {
		private Context context; // 上下文对象
		private BookDetails bookDetails;
		private BookDetailsDialog dialog;

		public Builder(Context context) {
			this.context = context;
		}

		public Builder setBookDetails(BookDetails bookDetails) {
			this.bookDetails = bookDetails;
			return this;
		}

		/**
		 * 构建一个Dialog对象
		 * 
		 * @return BookDetailsDialog
		 */
		public BookDetailsDialog create() {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			dialog = new BookDetailsDialog(context, R.style.BookDetailsStyle);
			View layout = inflater.inflate(R.layout.dialog_bookdetails, null);
			dialog.addContentView(layout, new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			ScrollView dialog_summary_layout = (ScrollView) layout
					.findViewById(R.id.dialog_summary_layout);
			TextView book_name = (TextView) layout.findViewById(R.id.book_name);
			TextView book_author = (TextView) layout
					.findViewById(R.id.book_author);
			TextView book_statue = (TextView) layout
					.findViewById(R.id.book_statue);
			TextView book_source = (TextView) layout
					.findViewById(R.id.book_source);
			TextView book_Popularity = (TextView) layout
					.findViewById(R.id.book_Popularity);
			TextView book_score = (TextView) layout
					.findViewById(R.id.book_score);
			TextView book_summary = (TextView) layout
					.findViewById(R.id.book_summary);
			ImageView book_image = (ImageView) layout
					.findViewById(R.id.book_image);
			ImageView dialog_close = (ImageView) layout
					.findViewById(R.id.dialog_close);
			Button dialog_btn_share = (Button) layout
					.findViewById(R.id.dialog_btn_share);

			Button dialog_btn_rate = (Button) layout
					.findViewById(R.id.dialog_btn_rate);

			Button dialog_btn_download = (Button) layout
					.findViewById(R.id.dialog_btn_download);

			dialog_close.setOnClickListener(this);
			dialog_btn_share.setOnClickListener(this);
			dialog_btn_rate.setOnClickListener(this);
			dialog_btn_download.setOnClickListener(this);

			setSummaryHeight(dialog_summary_layout,
					bookDetails.getBook_Summary());
			// 设置值
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
			setTextRateDrawable(context, book_score, rate);
			String result = String.valueOf(rate).substring(0, 3);
			book_score.setText("评分:" + result);
			if (bookDetails.getBook_Summary().startsWith("　　")) {
				book_summary.setText(bookDetails.getBook_Summary().trim());
			} else
				book_summary.setText("　　"
						+ bookDetails.getBook_Summary().trim());
			ImageLoader.getInstance().displayImage(bookDetails.getBook_Image(),
					book_image, option);

			return dialog;

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
			view.getLayoutParams().height = DeviceUtil.dip2px(context,
					ViewHeight);

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
				drawable = context.getResources()
						.getDrawable(R.drawable.rate_0);
			} else if (rate < 2.0) {
				drawable = context.getResources()
						.getDrawable(R.drawable.rate_1);
			} else if (rate < 3.0) {
				drawable = context.getResources()
						.getDrawable(R.drawable.rate_2);
			} else if (rate < 4.0) {
				drawable = context.getResources()
						.getDrawable(R.drawable.rate_3);
			} else if (rate < 5.0) {
				drawable = context.getResources()
						.getDrawable(R.drawable.rate_4);
			} else {
				drawable = context.getResources()
						.getDrawable(R.drawable.rate_5);
			}

			textView.setCompoundDrawablesWithIntrinsicBounds(null, null,
					drawable, null);
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.dialog_close:
				dialog.dismiss();
				break;
			case R.id.dialog_btn_share:
				dialog.dismiss();
				UmengShare.getInstance().setShareContent((Activity) context,
						bookDetails.getBook_Name(),
						bookDetails.getBook_Image(),
						bookDetails.getBook_Summary());
				// 注册友盟分享
				UmengShare.getInstance().addCustomPlatforms((Activity) context);

				break;
			case R.id.dialog_btn_rate:
				dialog.dismiss();
				break;
			case R.id.dialog_btn_download:
				dialog.dismiss();
				break;

			default:
				break;
			}
		}

	}

}
