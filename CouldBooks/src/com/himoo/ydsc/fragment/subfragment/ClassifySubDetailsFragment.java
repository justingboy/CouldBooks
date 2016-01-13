package com.himoo.ydsc.fragment.subfragment;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.himoo.ydsc.R;
import com.himoo.ydsc.base.BaseFragment;
import com.himoo.ydsc.fragment.threefragment.BaiduBookClassFragment;
import com.himoo.ydsc.fragment.threefragment.BaiduBookListFragment;
import com.himoo.ydsc.manager.PageManager;
import com.himoo.ydsc.util.SharedPreferences;

public class ClassifySubDetailsFragment extends BaseFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			SharedPreferences sp, Bundle savedInstanceState,
			PageManager pageManager) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_classify_hot, container,
				false);

		return view;
	}

	@Override
	public void initData() {
		FragmentManager fragmentManger = getChildFragmentManager();
		FragmentTransaction transaction = fragmentManger.beginTransaction();
		BaiduBookListFragment fragment = new BaiduBookListFragment();
		BaiduBookClassFragment bookClassFragment = new BaiduBookClassFragment();
		transaction.add(R.id.classify_book_list, fragment);
		transaction.add(R.id.classify_book_datails, bookClassFragment);
		transaction.commitAllowingStateLoss();

	}
	
	
	
}
