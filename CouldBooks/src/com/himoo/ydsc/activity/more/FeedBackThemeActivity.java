package com.himoo.ydsc.activity.more;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.himoo.ydsc.R;
import com.himoo.ydsc.ui.swipebacklayout.SwipeBackActivity;
import com.lidroid.xutils.view.annotation.ViewInject;

public class FeedBackThemeActivity extends SwipeBackActivity implements
		OnItemClickListener {

	private String[] title = { "问题", "需求", "程序错误报告", "其他" };

	@ViewInject(R.id.feedback_listview)
	private ListView listView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feedback_theme);
		intListView();
	}

	@Override
	protected void initTitleBar() {
		// TODO Auto-generated method stub
		mTitleBar.setLeftTitle(getResources().getString(
				R.string.more_user_feedback).substring(2));
		mTitleBar.setTitle(getResources().getString(
				R.string.feedback_theme_text));
		mTitleBar.setRightLogoGone();
	}

	private void intListView() {
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				R.layout.android_adapter_textview_feedback, title);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

		String title = (String) parent.getItemAtPosition(position);
		Intent intent = new Intent(this, FeedBackActivity.class);
		intent.putExtra("title", title);
		setResult(RESULT_OK, intent);// resultCode用于标识此intent
		finish();

	}
}
