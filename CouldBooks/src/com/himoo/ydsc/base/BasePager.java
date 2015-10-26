package com.himoo.ydsc.base;

import java.io.File;
import java.lang.reflect.Field;
import java.util.List;

import com.himoo.ydsc.config.SPContants;
import com.himoo.ydsc.manager.PageManager;
import com.himoo.ydsc.manager.SizeOf;
import com.himoo.ydsc.mvc.BaseModel;
import com.himoo.ydsc.mvc.ServiceListener;
import com.himoo.ydsc.util.MyLogger;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Spinner;

/**
 * 如果启动该类。数据会优化到极致，整个项目只会有一个Activity.Fragment  剩下的全是以该类作为基类的入口
 */
public abstract class BasePager extends SizeOf implements OnItemSelectedListener, OnItemClickListener, OnItemLongClickListener, OnClickListener,OnCheckedChangeListener, OnLongClickListener,ServiceListener, OnPageChangeListener {
	private View view;
	public MyLogger Log;
	private BaseFragment frag;
	/**
	 * @param frag 当前Page所依附的Fragment
	 */
	public BasePager(BaseFragment frag){
		if(frag == null){
			throw new NullPointerException("当前Page并不能找到它的依附Fragment");
		}
		this.frag = frag;
		SharedPreferences sp = frag.getActivity().getSharedPreferences(SPContants.CONFIG, Context.MODE_PRIVATE);
		Log = MyLogger.kLog();
		view = initView(sp);
		findViews();
		setListener();
		initData();
	}
	public abstract View initView(SharedPreferences sp);
	public abstract void findViews();
	public abstract void initData();
	/**
	 * 获取当前页所依附的Fragment
	 * @return
	 */
	public BaseFragment getFragment(){
		return frag;
	}
	@SuppressWarnings("deprecation")
	private void setListener() {
		try {
			Field field = getClass().getField("ids");
			int[] ids =  (int[]) field.get(this);
			if(ids != null && ids.length > 0)
				for(int id : ids){
					View view = findViewById(id);
					if(view instanceof ViewPager){
						((ViewPager)view).setOnPageChangeListener(this);
						continue;
					}
					if(view instanceof CompoundButton){
						((CompoundButton)view).setOnCheckedChangeListener(this);
						continue;
					}
					if(view instanceof AdapterView){
						if(view instanceof Spinner){
							((Spinner)view).setOnItemSelectedListener(this);
							continue;
						}
						((AdapterView<?>)view).setOnItemClickListener(this);
						((AdapterView<?>)view).setOnItemLongClickListener(this);
						continue;
					}
					view.setOnClickListener(this);
					view.setOnLongClickListener(this);
				}
		} catch (Exception e) {
		}
	}
	@SuppressWarnings("unchecked")
	public <T extends View> T findViewById(int resId){
		return (T) view.findViewById(resId);
	}
	public View getView(){
		return view;
	}
	/**
	 * 普通控件点击事件
	 */
	@Override
	public void onClick(View v){
	}
	/**
	 * 普通控件长按点击事件
	 */
	@Override
	public boolean onLongClick(View v){
		return false;
	}
	/**
	 * ListView的item点击事件
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id){
		
	}
	/**
	 * ListView的item长按事件监听
	 */
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
	public void onPageScrollStateChanged(int arg0){
	}
	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2){
	}
	@Override
	public void onPageSelected(int arg0) {
	}

	/**
	 * 处理返回键逻辑和其他系统按键逻辑
	 * @param keyCode
	 * @param event
	 */
	protected boolean onKeyDownMethod(int keyCode, KeyEvent event) {
		return PageManager.getInstance(getFragment()).goBack();
	}
	public BasePager newInstance(){
		return this;
	}
	@Override
	public void handlerIntent(BaseModel model, int dataInstruction){
	}
	@Override
	public void handlerIntent(List<? extends BaseModel> models,
			int dataInstruction){
	}
	@Override
	public void handlerIntent(SparseArray<? extends BaseModel> models,
			int dataInstruction) {
	}
	@Override
	public void handlerIntent(File file, int dataInstruction) {	
	}
	public Context getContext() {
		return getFragment().getActivity();
	}
	public void setArguments(Bundle data) {
		if(data == null || data.isEmpty()){
			throw new IllegalStateException("当前没有收到任何从Fragment带过来的数据");
		}
	}
}