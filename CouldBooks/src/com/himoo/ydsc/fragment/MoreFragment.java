package com.himoo.ydsc.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.himoo.ydsc.R;
import com.himoo.ydsc.base.BaseFragment;
import com.himoo.ydsc.manager.PageManager;
import com.himoo.ydsc.ui.utils.Toast;
import com.lidroid.xutils.view.annotation.ViewInject;

public class MoreFragment extends BaseFragment {

	/** 主题皮肤设置 */
	@ViewInject(R.id.more_topic)
	private TextView more_topic;

	/** 后台自动更新 */
	@ViewInject(R.id.more_timeUpdate)
	private TextView more_timeUpdate;

	/** 解锁功能 */
	@ViewInject(R.id.more_unlock)
	private TextView more_unLock;

	/** 阅读统计 */
	@ViewInject(R.id.more_statistics)
	private TextView more_Statistics;

	/** 密码保护 */
	@ViewInject(R.id.more_passwordProtect)
	private TextView more_PasswordProtect;

	/** 用户反馈 */
	@ViewInject(R.id.more_feedback)
	private TextView more_FeedBack;
	
	/** ids */
	public int ids[] = { R.id.more_topic, R.id.more_timeUpdate,
			R.id.more_unlock, R.id.more_statistics, R.id.more_passwordProtect,
			R.id.more_feedback };

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			SharedPreferences sp, Bundle savedInstanceState,
			PageManager pageManager) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_more, null);

		return view;
	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub

	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		switch (v.getId()) {
		case R.id.more_topic:
			Toast.showLong(getActivity(), "皮肤设置");
			break;
		case R.id.more_timeUpdate:
			Toast.showLong(getActivity(), "自动更新设置");
			break;
		case R.id.more_unlock:
			Toast.showLong(getActivity(), "解锁功能");
			break;
		case R.id.more_statistics:
			Toast.showLong(getActivity(), "阅读统计");
			break;
		case R.id.more_passwordProtect:
			Toast.showLong(getActivity(), "密码保护");
			break;
		case R.id.more_feedback:
			Toast.showLong(getActivity(), "用户反馈");
			break;
		default:
			break;
		}
		
		
		
		
	}

}
