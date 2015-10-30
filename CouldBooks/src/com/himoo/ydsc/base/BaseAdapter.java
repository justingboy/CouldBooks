package com.himoo.ydsc.base;

import java.util.ArrayList;
import java.util.List;
import android.view.View;
import android.view.ViewGroup;
/**
 * 为了减轻代码的负担，创建BaseAdapter的子类时推荐继承该类
 * @param <D> 适配器所承载的数据类型
 */
public abstract class BaseAdapter<D> extends android.widget.BaseAdapter {
	public List<D> data = new ArrayList<D>();
	public BaseAdapter(List<D> data){
		this.data.clear();
		this.data.addAll(data);
	}
	@Override
	public int getCount(){
		return data.size();
	}
	@Override
	public D getItem(int position){
		return data.get(position);
	}
	@Override
	public long getItemId(int position){
		return position;
	}
	@Override
	public abstract View getView(int position, View convertView, ViewGroup parent);
}