package com.himoo.ydsc.base;

import java.io.File;
import java.lang.reflect.Field;
import java.util.List;

import com.himoo.ydsc.mvc.BaseModel;
import com.himoo.ydsc.mvc.ServiceListener;
import com.himoo.ydsc.util.MyLogger;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.SparseArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.CompoundButton;
import android.widget.PopupWindow;
import android.widget.Spinner;
/**
 * 弹窗的页面基类
 * @author 
 */
public abstract class BasePopWindow extends PopupWindow implements OnItemSelectedListener,OnCheckedChangeListener, OnItemClickListener, OnItemLongClickListener, OnClickListener, OnLongClickListener,ServiceListener, OnPageChangeListener {
	private View view;
	private BaseFragment frag;
	public MyLogger Log;
	
	public BasePopWindow(View view,int width,int height,boolean focusable){
		super(view, width, height, focusable);
		Log = MyLogger.kLog();
		this.view = view;
		findViews();
		setListener();
		initData();
	}
	public BasePopWindow(BaseFragment frag,View view,int width,int height,boolean focusable){
		super(view, width, height, focusable);
		Log = MyLogger.kLog();
		this.frag = frag;
		this.view = view;
		findViews();
		setListener();
		initData();
	}
	public abstract void findViews();
	public abstract void initData();
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
	public void setFrag(BaseFragment frag) {
		this.frag = frag;
	}
	/**
	 * 获取窗体索依附的Activity
	 * 如果fragment存在返回fragment所依附的Activity  如果不存在返回null    
	 * 可以调用setFrag()方法设置Fragment
	 * @return
	 */
	public Context getActivity(){
		if(frag != null)
			return frag.getActivity();
		else
			return view.getContext();
	}
	/**
	 * 获取PopWindow上显示的View
	 * @return
	 */
	public View getPopView(){
		return view;
	}
	@SuppressWarnings("unchecked")
	public <T extends View> T findViewById(int id) {
		return (T) view.findViewById(id);
	}
	@Override
	public boolean onLongClick(View v) {
		return false;
	}
	@Override
	public void onClick(View v) {
		
	}
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		return false;
	}
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
	}
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
	}
	@Override
	public void onNothingSelected(AdapterView<?> parent){
	}
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
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
	@Override
	public void onPageScrollStateChanged(int arg0) {
		
	}
	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		
	}
	@Override
	public void onPageSelected(int arg0) {
		
	}
}
