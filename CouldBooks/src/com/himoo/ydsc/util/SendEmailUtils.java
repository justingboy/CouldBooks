package com.himoo.ydsc.util;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class SendEmailUtils {
	private SendEmailUtils() {
	}

	private static SendEmailUtils instance;

	public static SendEmailUtils getInstance() {
		if (instance == null) {
			synchronized (SendEmailUtils.class) {
				if (instance == null) {
					instance = new SendEmailUtils();
				}
			}
		}
		return instance;
	}

	/**
	 * 不支持带附件，多人，抄送发送等。
	 * 
	 * @param context
	 */
	public void sendEmail(Context context) {
		Intent intent = new Intent();
		intent.setData(Uri.parse("mailto:"));
		/* 设置邮件的标题 */
		intent.putExtra(Intent.EXTRA_SUBJECT, "别紧张，这仅仅是一个测试！");
		/* 设置邮件的内容 */
		intent.putExtra(Intent.EXTRA_TEXT, "测试打开系统邮箱并将发送的标题和内容自动填充到邮箱，并发送邮件，");
		// 开始调用
		context.startActivity(intent);
	}

	/**
	 * 发邮件，带抄送，和密送，并带上个附件
	 * 
	 * @param context
	 */
	public void sendEmail(Context context, String title, String content,
			String imagePath) {

		Intent intent = new Intent();
		intent.setData(Uri.parse("mailto:"));
		String[] tos = { "532326627@qq.com" };
		// String[] ccs = { "yw.2@163.com" };
		// String[] bccs = { "yw.3@163.com" };
		intent.putExtra(Intent.EXTRA_EMAIL, tos); // 收件者
		// intent.putExtra(Intent.EXTRA_CC, ccs); // 抄送这
		// intent.putExtra(Intent.EXTRA_BCC, bccs); // 密送这
		intent.putExtra(Intent.EXTRA_SUBJECT, title);
		intent.putExtra(Intent.EXTRA_TEXT, content);// "file:///mnt/sdcard/test.jpg")
		if (imagePath != null && !imagePath.equals("")) {
			intent.putExtra(Intent.EXTRA_STREAM,
					Uri.parse("file://" + imagePath));
			intent.setType("image/*");
		}
		intent.setType("message/rfc882");
		Intent.createChooser(intent, "Choose Email Client");
		context.startActivity(intent);

	}

	/**
	 * 多附件发送
	 * 
	 * @param conext
	 */
	public void sendFujian(Context conext) {
		Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
		String[] tos = { "way.ping.li@gmail.com" };
		String[] ccs = { "way.ping.li@gmail.com" };
		intent.putExtra(Intent.EXTRA_EMAIL, tos);
		intent.putExtra(Intent.EXTRA_CC, ccs);
		intent.putExtra(Intent.EXTRA_TEXT, "body");
		intent.putExtra(Intent.EXTRA_SUBJECT, "subject");

		// List<Uri> imageUris = new ArrayList<Uri>();
		// imageUris.add(Uri.parse("file:///mnt/sdcard/a.jpg"));
		// imageUris.add(Uri.parse("file:///mnt/sdcard/b.jpg"));
		// intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);
		intent.setType("image/*");
		intent.setType("message/rfc882");
		Intent.createChooser(intent, "Choose Email Client");
		conext.startActivity(intent);
	}

}