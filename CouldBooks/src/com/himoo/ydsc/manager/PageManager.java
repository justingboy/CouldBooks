package com.himoo.ydsc.manager;

import java.util.LinkedList;

import com.himoo.ydsc.R;
import com.himoo.ydsc.base.BaseFragment;
import com.himoo.ydsc.base.BasePager;

import android.content.Context;
import android.util.LruCache;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

/**
 * Page页面管理工具
 */
public class PageManager {
	private static PageManager instance;
	private static int SIZE = 50;
	private LinkedList<String> HISTORY = new LinkedList<String>();// 用户操作的历史记录
	private BasePager currentPage;// 当前正在展示
//	private Map<String, BasePager> VIEWCACHE = new LinkedHashMap<String, BasePager>(SIZE,0.75f,true);//
	// 利用手机内存空间，换应用的运行速度
	private LruCache<String, BasePager> VIEWCACHE = new LruCache<String, BasePager>(SIZE){
		@Override
		protected int sizeOf(String key, BasePager value) {
			try {
				return (int) value.newInstance().size();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return 0;
		}
	};

	
	private PageManager(BaseFragment frag) {
		setMiddle(frag);
	}
	public static PageManager getInstance(BaseFragment frag) {
		if(instance != null)
			return instance;
		else{
			instance = new PageManager(frag);
			return instance;
		}
	}

	private ViewGroup middle;

	public void setMiddle(BaseFragment frag) {
		if(frag != null)
			this.middle = (ViewGroup) frag.getView();
		else 
			throw new NullPointerException("当前Pager未设置Fragment，框架使用出现异常!");
	}

	public BasePager getPager(String tag){
		return VIEWCACHE.get(tag);
	}


	/**
	 * 获取当前正在显示的Page页面
	 * @return
	 */
	public BasePager getCurrentPage() {
		return currentPage;
	}

	/**
	 * 切换界面
	 * 
	 * @param ui
	 */
	public void showPage(BasePager page) {
		if(currentPage != null && currentPage == page){
			return;
		}
		String key = page.getClass().getSimpleName();
		if(VIEWCACHE.get(key) != null){
			page = VIEWCACHE.get(key);
			HISTORY.remove(key);
		}
		if(middle != null){
			// 切换界面的核心代码
			middle.removeAllViews();
			// FadeUtil.fadeOut(child1, 2000);
			View child = page.getView();
			middle.addView(child);
			child.startAnimation(AnimationUtils.loadAnimation(getContext(),
					R.anim.zoom_enter));
			// FadeUtil.fadeIn(child, 2000, 1000);
			VIEWCACHE.put(key, page);
			HISTORY.addFirst(key);
			currentPage = page;
		}
	}

	public Context getContext() {
		return currentPage.getContext();
	}
	/**
	 * 返回键处理
	 * @return
	 */
	public boolean goBack() {
		if (HISTORY.size() > 0) {
			if (HISTORY.size() == 1) {
				return false;
			}
			HISTORY.removeFirst();
			if (HISTORY.size() > 0 && middle != null) {
				String key = HISTORY.getFirst();
				BasePager targetUI = VIEWCACHE.get(key);
				middle.removeAllViews();
				if(targetUI != null){
					middle.addView(targetUI.getView());
					currentPage = targetUI;
					return true;
				}
			}
		}
		return false;
	}
}
