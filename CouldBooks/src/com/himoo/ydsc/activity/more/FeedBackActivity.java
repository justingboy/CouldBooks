package com.himoo.ydsc.activity.more;

import android.os.Bundle;
import android.widget.ImageView;

import com.himoo.ydsc.R;
import com.himoo.ydsc.ui.swipebacklayout.SwipeBackActivity;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.ImageLoader;

public class FeedBackActivity extends SwipeBackActivity {

	@ViewInject(R.id.test_image)
	private ImageView testIamge;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_more_feedback);
		String url = "http://timg01.baidu-img.cn/timg?pa&amp;di=c5c5224d9b9c71197f0818ac78efbb78&amp;sec=1396053279&amp;size=%40tpl_size%40&amp;src=http%3A%2F%2Fbj.bs.baidu.com%2Fwise-novel-authority-logo%2F20ea3d4308c41757a6f5a9744aedde34.jpg%3Fsign%3DMBO%3AukiZg0s7VRDjqbuWyVZfM4dXK1rXmASXl2w%3ARSvcKVgQ88aF176wCvthoN3cP3Q%253D";
		ImageLoader.getInstance().displayImage(url, testIamge);
	}

	@Override
	protected void initTitleBar() {
		// TODO Auto-generated method stub
		mTitleBar.setLeftTitle(getResources().getString(R.string.main_more));
		mTitleBar.setTitle(getResources()
				.getString(R.string.more_user_feedback));
		mTitleBar.setRightLogoDrawable(R.drawable.feedback_email_send);
	}
}
