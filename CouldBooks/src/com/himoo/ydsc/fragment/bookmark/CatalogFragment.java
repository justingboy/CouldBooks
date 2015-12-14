package com.himoo.ydsc.fragment.bookmark;

import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.himoo.ydsc.R;
import com.himoo.ydsc.base.BaseFragment;
import com.himoo.ydsc.bean.BaiduBookChapter;
import com.himoo.ydsc.manager.PageManager;
import com.himoo.ydsc.reader.utils.IOHelper;
import com.himoo.ydsc.util.SharedPreferences;
import com.lidroid.xutils.view.annotation.ViewInject;

public class CatalogFragment extends BaseFragment {

	
	@ViewInject(R.id.listview_catalog)
	private ListView listView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			SharedPreferences sp, Bundle savedInstanceState,
			PageManager pageManager) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_catalog, container,
				false);
		return view;
	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub
		List<BaiduBookChapter> list = IOHelper.getBookChapter();
		String[] chapters = new String[list.size()];
		for (int i = 0; i < list.size(); i++) {
			chapters[i] = list.get(i).getText().trim();
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
				R.layout.adapter_catalog_item, chapters);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);
	}

}
