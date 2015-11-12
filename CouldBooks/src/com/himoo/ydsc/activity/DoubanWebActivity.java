package com.himoo.ydsc.activity;

import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.himoo.ydsc.R;
import com.himoo.ydsc.ui.swipebacklayout.SwipeBackActivity;
import com.lidroid.xutils.view.annotation.ViewInject;

public class DoubanWebActivity extends SwipeBackActivity {

	@ViewInject(R.id.douban_comment_web)
	private WebView mWebView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_douban_comment);
		showRefreshDialog("正在加载中");
		initView();
		initData();
	}

	@Override
	protected void initTitleBar() {
		// TODO Auto-generated method stub
		mTitleBar.setTitle("详细评论");
		mTitleBar.setLeftTitle("返回");
		mTitleBar.setRightLogoGone();

	}

	protected void initView() {

		mWebView.setInitialScale(4);
		mWebView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
		mWebView.setHapticFeedbackEnabled(false);
		mWebView.setWebViewClient(new WebViewClient());
		mWebView.setWebChromeClient(new WebChromeClient() {

			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				// TODO Auto-generated method stub
				super.onProgressChanged(view, newProgress);
				if (newProgress > 45) {
					dismissRefreshDialog();
				}
			}

		});

	}

	@SuppressWarnings("deprecation")
	protected void initData() {
		String url = getIntent().getStringExtra("key").replace("api", "www");
		// 设置支持JavaScript等
		WebSettings mWebSettings = mWebView.getSettings();
		// 设置默认缩放方式尺寸是far
		mWebSettings.setDefaultZoom(WebSettings.ZoomDensity.FAR);
		mWebSettings.setJavaScriptEnabled(true);
		mWebSettings.setBuiltInZoomControls(true);
		mWebSettings.setLightTouchEnabled(true);
		mWebSettings.setSupportZoom(true);
		mWebSettings.setUseWideViewPort(true);
		mWebView.loadUrl(url);

	}

	// /**
	// * 按键响应，在WebView中查看网页时，按返回键的时候按浏览历史退回,如果不做此项处理则整个WebView返回退出
	// */
	// @Override
	// public boolean onKeyDown(int keyCode, KeyEvent event) {
	// // Check if the key event was the Back button and if there's history
	// if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
	// // 返回键退回
	// mWebView.goBack();
	// return true;
	// }
	// // If it wasn't the Back key or there's no web page history, bubble up
	// // to the default
	// // system behavior (probably exit the activity)
	// return super.onKeyDown(keyCode, event);
	//
	// }

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub

		this.mWebView.loadUrl("about:blank");
		this.mWebView.stopLoading();
		this.mWebView.setWebChromeClient(null);
		this.mWebView.setWebViewClient(null);
		this.mWebView.clearCache(true);
		this.mWebView.clearHistory();
		this.mWebView.clearFormData();
		this.mWebView.destroy();
		this.mWebView = null;

		super.onDestroy();
	}

}
