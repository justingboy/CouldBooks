package com.himoo.ydsc.base;

import java.io.File;
import java.lang.reflect.Field;
import java.util.List;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CompoundButton;

import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.himoo.ydsc.manager.PageManager;
import com.himoo.ydsc.mvc.BaseModel;
import com.himoo.ydsc.mvc.ServiceListener;
import com.himoo.ydsc.util.MyLogger;
import com.himoo.ydsc.util.SharedPreferences;
import com.himoo.ydsc.util.TimestampUtils;
import com.lidroid.xutils.ViewUtils;

/**
 * Fragment的基类
 * 
 * 无需为view设置监听操作，只需要声明一个int[]类型的ids变量，把所有要监听的控件id放到该数组中即可，同时需要继承该类
 * 对BasePager提供了支持
 */
public abstract class BaseFragment extends Fragment implements OnClickListener,
		OnLongClickListener, OnItemClickListener, OnItemLongClickListener,
		OnItemSelectedListener, CompoundButton.OnCheckedChangeListener,
		ServiceListener, OnPageChangeListener {
	private SharedPreferences sp;
	public FragmentManager manager;
	public View view;
	public MyLogger Log;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log = MyLogger.kLog();
		manager = getFragmentManager();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = onCreateView(inflater, container, sp, savedInstanceState,
				PageManager.getInstance(this));
		return view;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
		ViewUtils.inject(this);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initData();
		setListener();
	}

	public void addToPageData(String tag, Bundle data) {
		BasePager pager = PageManager.getInstance(this).getPager(tag);
		addToPageData(pager, data);
	}

	public void addToPageData(BasePager pager, Bundle data) {
		pager.setArguments(data);
	}

	

	@Override
	public void onDestroy() {
		super.onDestroy();
		BaseApplication.getInstance().clearAsyncTask();
	}

	/**
	 * 监听事件重写了，只需要你手动声明一个以ids为变量名的int[] 该方法会自动为在这个数组里的控件设置上事件监听
	 */
	private void setListener() {
		try {
			Field field = getClass().getField("ids");
			int[] ids = (int[]) field.get(this);
			if (ids != null && ids.length > 0)
				for (int id : ids) {
					View view = findViewById(id);
			/*		if (view instanceof ViewPager) {
						((ViewPager) view).setOnPageChangeListener(this);
						continue;
					}
					if (view instanceof CompoundButton) {
						((CompoundButton) view)
								.setOnCheckedChangeListener(this);
						continue;
					}
					if (view instanceof AdapterView) {
						if (view instanceof Spinner) {
							((Spinner) view).setOnItemSelectedListener(this);
							continue;
						}
						((AdapterView<?>) view).setOnItemClickListener(this);
						((AdapterView<?>) view)
								.setOnItemLongClickListener(this);
						continue;
					}*/
					view.setOnClickListener(this);
//					view.setOnLongClickListener(this);
				}
		} catch (Exception e) {

		}
	}

	public abstract View onCreateView(LayoutInflater inflater,
			ViewGroup container, SharedPreferences sp,
			Bundle savedInstanceState, PageManager pageManager);

	public abstract void initData();

	@SuppressWarnings("unchecked")
	public <T extends View> T findViewById(int resId) {
		return (T) view.findViewById(resId);
	}

	@Override
	public void onClick(View v) {
	}

	@Override
	public boolean onLongClick(View v) {
		return false;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		return false;
	}

	/**
	 * Spinner控件的事件监听
	 */
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}

	@Override
	public void onPageSelected(int arg0) {
	}

	/**
	 * 显示Page视图
	 * 
	 * @param page
	 */
	public void showPage(BasePager page) {
		PageManager.getInstance(this).showPage(page);
	}

	/**
	 * 获取当前显示的Page视图
	 * 
	 * @return
	 */
	public BasePager getCurrentPage() {
		return PageManager.getInstance(this).getCurrentPage();
	}

	@Override
	public void handlerIntent(BaseModel model, int dataInstruction) {
	}

	@Override
	public void handlerIntent(List<? extends BaseModel> models,
			int dataInstruction) {
	}

	@Override
	public void handlerIntent(SparseArray<? extends BaseModel> models,
			int dataInstruction) {
	}

	@Override
	public void handlerIntent(File file, int dataInstruction) {
	}

	/**
	 * 初始化上次刷新时间
	 * 
	 * @param saveLastRefreshkey
	 * @param mPullRefreshGridView
	 */
	protected void initLastRefreshTime(String saveLastRefreshkey,
			PullToRefreshGridView mPullRefreshGridView) {
		String label = "最后更新 ： ";
		if (com.himoo.ydsc.util.SharedPreferences.getInstance()
				.getString(saveLastRefreshkey, "").equals("")) {
			label += TimestampUtils.getTimeState(saveLastRefreshkey);

		} else {
			label += TimestampUtils.getTimeState(
					com.himoo.ydsc.util.SharedPreferences.getInstance()
							.getString(saveLastRefreshkey, ""),
					"yyyy-MM-dd HH:mm:ss");
			;
		}
		// Update the LastUpdatedLabel
		mPullRefreshGridView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
	}
}
