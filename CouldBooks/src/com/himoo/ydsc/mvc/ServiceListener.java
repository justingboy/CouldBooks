package com.himoo.ydsc.mvc;

import java.io.File;
import java.util.List;
import android.util.SparseArray;

/**
 * MVC模式中Service跟V(视图层)通讯的回调接口
 */
public interface ServiceListener{
	/**
	 * Service跟V视图层通讯接口，当Serivice层解析完数据后会调用该方法把数据带到视图层
	 * @param model 服务层Service解析完之后所生成的序列化对象
	 * @param dataInstruction  该数据的标志性常量
	 */
	void handlerIntent(BaseModel model,int dataInstruction);
	
	void handlerIntent(List<? extends BaseModel> models,int dataInstruction);
	
	void handlerIntent(SparseArray<? extends BaseModel> models,int dataInstruction);
	
	void handlerIntent(File file,int dataInstruction);
}
