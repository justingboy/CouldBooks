package com.himoo.ydsc.activity.more;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.himoo.ydsc.R;
import com.himoo.ydsc.config.SpConstant;
import com.himoo.ydsc.ui.swipebacklayout.SwipeBackActivity;
import com.himoo.ydsc.ui.utils.Toast;
import com.himoo.ydsc.ui.utils.UIHelper;
import com.himoo.ydsc.util.AppUtils;
import com.himoo.ydsc.util.DeviceUtil;
import com.himoo.ydsc.util.ImageUtil;
import com.himoo.ydsc.util.SendEmailUtils;
import com.ios.dialog.ActionSheetDialog;
import com.ios.dialog.ActionSheetDialog.OnSheetItemClickListener;
import com.ios.dialog.ActionSheetDialog.SheetItemColor;
import com.lidroid.xutils.view.annotation.ViewInject;

public class FeedBackActivity extends SwipeBackActivity implements
		OnClickListener {
	/** 　返回的bitmap */
	private Bitmap photo;
	/** bitmap的保存路径 */
	private File mPhotoFile;
	/** 　调用系统相机请求码 */
	private int CAMERA_RESULT = 100;
	/** 　调用系统相册请求码 */
	private int RESULT_LOAD_IMAGE = 200;
	/** 　拍照的图片保存路径　 */
	private String saveDir = Environment.getExternalStorageDirectory()
			.getPath() + "/CouldBook/Camera";

	@ViewInject(R.id.feedback_question)
	private RelativeLayout feedback_question;

	@ViewInject(R.id.feedback_question_type)
	private TextView feedback_question_type;

	@ViewInject(R.id.feedback_select_picture)
	private TextView feedback_select_picture;

	@ViewInject(R.id.feedback_device)
	private TextView feedback_device;

	@ViewInject(R.id.feedback_device_system)
	private TextView feedback_device_system;

	@ViewInject(R.id.feedback_app_name)
	private TextView feedback_app_name;

	@ViewInject(R.id.feedback_app_version)
	private TextView feedback_app_version;

	@ViewInject(R.id.feedback_app_build)
	private TextView feedback_app_build;

	@ViewInject(R.id.feedback_content)
	private EditText feedback_content;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		setContentView(R.layout.activity_more_feedback);
		initDate();
		initListener();
	}

	@Override
	protected void initTitleBar() {
		// TODO Auto-generated method stub
		mTitleBar.setLeftTitle(getResources().getString(R.string.main_more));
		mTitleBar.setTitle(getResources()
				.getString(R.string.more_user_feedback).substring(2));
		mTitleBar.setRightLogoDrawable(R.drawable.feedback_email_send);
		mTitleBar.titlebar_refresh.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SendEmailUtils.getInstance().sendEmail(
						FeedBackActivity.this,
						feedback_question_type.getText().toString(),
						createErrorMessge(),
						mPhotoFile == null ? null : mPhotoFile
								.getAbsolutePath(),false);

			}
		});
	}

	private void initListener() {
		feedback_question.setOnClickListener(this);
		feedback_select_picture.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		switch (v.getId()) {
		case R.id.feedback_question:
			UIHelper.startForReseltToActivity(this, FeedBackThemeActivity.class);
			break;
		case R.id.feedback_select_picture:
			new ActionSheetDialog(this)
					.builder()
					.setCancelable(true)
					.setCanceledOnTouchOutside(true)
					.addSheetItem("拍照", SheetItemColor.Blue,
							new OnSheetItemClickListener() {
								@Override
								public void onClick(TextView v, int which) {

									takePhoto();
								}

							})
					.addSheetItem("从相册中选取", SheetItemColor.Blue,
							new OnSheetItemClickListener() {
								@Override
								public void onClick(TextView v, int which) {
									Intent intent = new Intent(
											Intent.ACTION_PICK,
											android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
									startActivityForResult(intent,
											RESULT_LOAD_IMAGE);

								}
							}).show();
			break;

		default:
			break;
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
			feedback_question_type.setText(data.getStringExtra("title"));
		}
		if (requestCode == CAMERA_RESULT && resultCode == RESULT_OK) {
			if (mPhotoFile != null && mPhotoFile.exists()) {

				Bitmap bitmap = ImageUtil.getScaleBitmap(mPhotoFile,
						DeviceUtil.dip2px(this, 100),
						DeviceUtil.dip2px(this, 80));

				feedback_select_picture
						.setCompoundDrawablesWithIntrinsicBounds(
								new BitmapDrawable(bitmap), null, null, null);
			}
		}
		if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK
				&& null != data) {
			Uri selectedImage = data.getData();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };

			Cursor cursor = getContentResolver().query(selectedImage,
					filePathColumn, null, null, null);
			if (cursor == null) {
				Toast.showLong(FeedBackActivity.this, "选取图片失败,请换一张试试!");
				return;
			}
			cursor.moveToFirst();

			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String picturePath = cursor.getString(columnIndex);
			mPhotoFile = new File(picturePath);
			cursor.close();
			Bitmap bitmap = ImageUtil.getScaleBitmap(mPhotoFile,
					DeviceUtil.dip2px(this, 100), DeviceUtil.dip2px(this, 80));
			feedback_select_picture.setCompoundDrawablesWithIntrinsicBounds(
					new BitmapDrawable(bitmap), null, null, null);
		}

	}

	public void initDate() {
		feedback_device.setText(android.os.Build.MODEL);
		feedback_device_system.setText(android.os.Build.VERSION.RELEASE);
		feedback_app_name.setText(AppUtils.getAppName(this));
		feedback_app_version.setText(AppUtils.getVersionName(this));
		feedback_app_build.setText(SpConstant.BUILD_CODE);

	}

	/**
	 * 调用相机拍照
	 */
	private void takePhoto() {
		destoryImage();
		String state = Environment.getExternalStorageState();
		if (state.equals(Environment.MEDIA_MOUNTED)) {
			File file = new File(saveDir);
			if (!file.exists())
				file.mkdirs();
			mPhotoFile = new File(saveDir, "temp.jpg");
			mPhotoFile.delete();
			if (!mPhotoFile.exists()) {
				try {
					mPhotoFile.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
					Toast.showLong(FeedBackActivity.this, "照片创建失败!");
					return;
				}
			}
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mPhotoFile));
			startActivityForResult(intent, CAMERA_RESULT);
		} else {
			Toast.showLong(FeedBackActivity.this, "sdcard无效或没有插入!");
		}
	}

	private void destoryImage() {
		if (photo != null) {
			photo.recycle();
			photo = null;
		}
	}

	@Override
	protected void onDestroy() {
		destoryImage();
		super.onDestroy();
	}

	/**
	 * 拼接错误信息
	 * 
	 * @return
	 */
	private String createErrorMessge() {
		StringBuilder sb = new StringBuilder();
		sb.append("设备：" + feedback_device.getText() + "|\n")
				.append("Android系统 ：" + feedback_device_system.getText()
						+ "|\n")
				.append("应用名字：" + feedback_app_name.getText() + "|\n")
				.append("版本号：" + feedback_app_version.getText() + "|\n")
				.append("Build :" + feedback_app_build.getText() + "|\n")
				.append("错误描述：" + "\n")
				.append(feedback_content.getText() + "\n");

		return sb.toString();
	}
}
