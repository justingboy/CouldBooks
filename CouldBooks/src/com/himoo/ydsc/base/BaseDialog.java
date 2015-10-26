package com.himoo.ydsc.base;

import java.lang.reflect.Field;
import java.util.List;

import com.himoo.ydsc.mvc.BaseModel;
import com.himoo.ydsc.mvc.ServiceListener;
import com.himoo.ydsc.util.MyLogger;

import android.app.Dialog;
import android.content.Context;
import android.util.SparseArray;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;
/**
 * 集成了事件自动监听的机制
 * 至于方法的注释可以参照BaseActivity的一些方法注释理解
 */
public abstract class BaseDialog extends Dialog implements OnItemSelectedListener, OnItemClickListener, OnItemLongClickListener, android.view.View.OnClickListener, OnLongClickListener,ServiceListener {
	public MyLogger Log;
	public BaseDialog(Context context,View view) {
		super(context);
		Log = MyLogger.kLog();
		setContentView(view);
		setListener();
	}
	public BaseDialog(Context context,int layoutId) {
		super(context);
		Log = MyLogger.kLog();
		setContentView(layoutId);
		setListener();
	}
	private void setListener() {
		try {
			Field field = getClass().getField("ids");
			int[] ids =  (int[]) field.get(this);
			if(ids != null && ids.length > 0)
				for(int id : ids){
					View view = findViewById(id);
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
	public <T extends View> T findViewToId(int resid){
		return (T) super.findViewById(resid);
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
	public void onNothingSelected(AdapterView<?> parent) {
	}
	@Override
	public void handlerIntent(BaseModel model, int dataInstruction) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void handlerIntent(List<? extends BaseModel> models,
			int dataInstruction) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void handlerIntent(SparseArray<? extends BaseModel> models,
			int dataInstruction) {
		// TODO Auto-generated method stub
		
	}
}
