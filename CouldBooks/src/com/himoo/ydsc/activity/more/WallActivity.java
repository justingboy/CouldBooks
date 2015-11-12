package com.himoo.ydsc.activity.more;

import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

import com.himoo.ydsc.R;
import com.himoo.ydsc.http.BookDetailsTask;
import com.himoo.ydsc.ui.swipebacklayout.SwipeBackActivity;
import com.lidroid.xutils.view.annotation.ViewInject;

public class WallActivity extends SwipeBackActivity {
	@ViewInject(R.id.iv)
	private ImageView iv ;
	public Handler mHandler = new Handler()
	{
		public void handleMessage(android.os.Message msg) {
			Bitmap bm = BitmapFactory.decodeStream((InputStream)msg.obj);
			if(bm ==null)
			{
				Log.d("bm ==null");
				iv.setImageResource(R.drawable.book_face_default);	
			}else
			{
				Log.d("正常");
				iv.setImageBitmap(bm);	
			}
				
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_more_wall);
//		String url = "http://bj.bs.baidu.com/wise-novel-authority-logo/4d6be3fc2772e80faf13b69dc8557fa3.jpg";
//		String url2 = "http://bj.bs.baidu.com/wise-novel-authority-logo/88022e5b48ce48b636ed1b24aba09653.jpg";
		String url3 = "http://bj.bs.baidu.com/wise-novel-authority-logo/adf66ea1f9f6c7a3f73b924161d5c793.jpg";
		getBitamp(url3);
		
		
		

	}

	@Override
	protected void initTitleBar() {
		// TODO Auto-generated method stub
		mTitleBar.setLeftTitle("更多");
		mTitleBar.setTitle("积分墙");
		mTitleBar.setRightLogoGone();
	}
	
	private void getBitamp(final String urlString)
	{
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				InputStream in = BookDetailsTask.getInstance().executeByGet(urlString);
				Message msg = mHandler.obtainMessage();
				msg.obj = in;
				mHandler.sendMessage(msg);
			}
		}).start();
	}

	
}
