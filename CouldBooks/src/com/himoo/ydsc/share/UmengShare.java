package com.himoo.ydsc.share;

import android.app.Activity;

import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;
import com.umeng.socialize.media.MailShareContent;
import com.umeng.socialize.media.QQShareContent;
import com.umeng.socialize.media.QZoneShareContent;
import com.umeng.socialize.media.SinaShareContent;
import com.umeng.socialize.media.SmsShareContent;
import com.umeng.socialize.media.TencentWbShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.EmailHandler;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.SmsHandler;
import com.umeng.socialize.sso.TencentWBSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;

/**
 * 友盟分享
 * 
 */
public class UmengShare {
	public static final String DESCRIPTOR = "com.umeng.share";
	private static UmengShare instance = new UmengShare();
	private final UMSocialService mController = UMServiceFactory
			.getUMSocialService(DESCRIPTOR);

	private UmengShare() {

	}

	public static UmengShare getInstance() {
		return instance;
	}

	/**
	 * @功能描述 : 添加微信平台分享
	 * @return
	 */
	private void addWXPlatform(Activity context) {
		// 注意：在微信授权的时候，必须传递appSecret
		// wx967daebe835fbeac是你在微信开发平台注册应用的AppID, 这里需要替换成你注册的AppID
		String appId = "wx967daebe835fbeac";
		String appSecret = "5bb696d9ccd75a38c8a0bfe0675559b3";
		// 添加微信平台
		UMWXHandler wxHandler = new UMWXHandler(context, appId, appSecret);
		wxHandler.addToSocialSDK();

		// 支持微信朋友圈
		UMWXHandler wxCircleHandler = new UMWXHandler(context, appId, appSecret);
		wxCircleHandler.setToCircle(true);
		wxCircleHandler.addToSocialSDK();
	}

	/**
	 * @功能描述 : 添加QQ平台支持 QQ分享的内容， 包含四种类型， 即单纯的文字、图片、音乐、视频. 参数说明 : title, summary,
	 *       image url中必须至少设置一个, targetUrl必须设置,网页地址必须以"http://"开头 . title :
	 *       要分享标题 summary : 要分享的文字概述 image url : 图片地址 [以上三个参数至少填写一个] targetUrl
	 *       : 用户点击该分享时跳转到的目标地址 [必填] ( 若不填写则默认设置为友盟主页 )
	 * @return
	 */
	private void addQQQZonePlatform(Activity context) {
		String appId = "100424468";
		String appKey = "c7394704798a158208a74ab60104f0ba";
		// 添加QQ支持, 并且设置QQ分享内容的target url
		UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(context, appId, appKey);
		qqSsoHandler.setTargetUrl("http://www.umeng.com/social");
		qqSsoHandler.addToSocialSDK();

		// 添加QZone平台
		QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(context, appId,
				appKey);
		qZoneSsoHandler.addToSocialSDK();
	}

	/**
	 * 添加短信平台</br>
	 */
	private void addSMS() {
		// 添加短信
		SmsHandler smsHandler = new SmsHandler();
		smsHandler.addToSocialSDK();
	}

	/**
	 * 添加Email平台</br>
	 */
	private void addEmail() {
		// 添加email
		EmailHandler emailHandler = new EmailHandler();
		emailHandler.addToSocialSDK();
	}

	/**
	 * 添加自定义平台</br>
	 */
	public void addCustomPlatforms(Activity context) {
		// 添加新浪SSO授权
		mController.getConfig().setSsoHandler(new SinaSsoHandler());
		// 添加腾讯微博SSO授权
		mController.getConfig().setSsoHandler(new TencentWBSsoHandler());

		// 添加微信平台
		addWXPlatform(context);
		// 添加QQ平台
		addQQQZonePlatform(context);
		// 添加短信平台
		addSMS();
		// 添加email平台
		addEmail();

		mController.registerListener(new SnsPostListener() {

			@Override
			public void onStart() {
				// Toast.makeText(getActivity(), "share start...", 0).show();
			}

			@Override
			public void onComplete(SHARE_MEDIA platform, int eCode,
					SocializeEntity entity) {
				// Toast.makeText(getActivity(), "code : " + eCode, 0).show();
			}
		});

		mController.getConfig().setPlatforms(SHARE_MEDIA.WEIXIN,
				SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE,
				SHARE_MEDIA.SINA, SHARE_MEDIA.TENCENT, SHARE_MEDIA.SMS,
				SHARE_MEDIA.EMAIL);
		mController.openShare(context, false);
	}

	/**
	 * 根据不同的平台设置不同的分享内容</br>
	 * 
	 * @param context
	 * @param title
	 *            分享的标题
	 * @param imageUrl
	 *            分享图片的url地址
	 * @param content
	 *            分享的内容
	 */
	public void setShareContent(Activity context, String title,
			String imageUrl, String content) {

		// QQ授权分享
		QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(context,
				"100424468", "c7394704798a158208a74ab60104f0ba");
		qZoneSsoHandler.addToSocialSDK();
		mController.setShareContent(content);

		// 微信分享内容设置
		WeiXinShareContent weixinContent = new WeiXinShareContent();
		UMImage urlImage = new UMImage(context, imageUrl);
		weixinContent.setShareContent(content);
		weixinContent.setTitle(title);
		weixinContent.setTargetUrl("http://www.umeng.com/social");
		weixinContent.setShareMedia(urlImage);
		mController.setShareMedia(weixinContent);

		// 设置朋友圈分享的内容
		CircleShareContent circleMedia = new CircleShareContent();
		circleMedia.setShareContent(content);
		circleMedia.setTitle(title);
		circleMedia.setShareMedia(urlImage);
		// circleMedia.setShareMedia(uMusic);
		// circleMedia.setShareMedia(video);
		circleMedia.setTargetUrl("http://www.umeng.com/social");
		mController.setShareMedia(circleMedia);

		// 设置QQ空间分享内容
		QZoneShareContent qzone = new QZoneShareContent();
		qzone.setShareContent(content);
		qzone.setTargetUrl("http://www.umeng.com");
		qzone.setTitle(title);
		qzone.setShareMedia(urlImage);
		// qzone.setShareMedia(uMusic);
		mController.setShareMedia(qzone);

		// 设置QQ分享内容
		QQShareContent qqShareContent = new QQShareContent();
		qqShareContent.setShareContent(content);
		qqShareContent.setTitle(title);
		qqShareContent.setShareMedia(urlImage);
		qqShareContent.setTargetUrl("http://www.umeng.com/social");
		mController.setShareMedia(qqShareContent);

		// 设置腾讯微博分享内容
		TencentWbShareContent tencent = new TencentWbShareContent();
		tencent.setShareContent(content);
		tencent.setTitle(title);
		tencent.setShareMedia(urlImage);
		// 设置tencent分享内容
		mController.setShareMedia(tencent);

		// 设置新浪微博分享内容
		SinaShareContent sinaContent = new SinaShareContent();
		sinaContent.setShareContent(content);
		sinaContent.setShareImage(urlImage);
		sinaContent.setTitle(title);
		mController.setShareMedia(sinaContent);

		// 设置邮件分享内容， 如果需要分享图片则只支持本地图片
		MailShareContent mail = new MailShareContent(urlImage);
		mail.setTitle(title);
		mail.setShareContent(content);
		// 设置tencent分享内容
		mController.setShareMedia(mail);

		// 设置短信分享内容
		SmsShareContent sms = new SmsShareContent();
		sms.setShareContent(content);
		sms.setShareImage(urlImage);
		mController.setShareMedia(sms);

	}
}
