package com.himoo.ydsc.ui.utils;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.view.View;

public class ButtonSelector extends View {

	public ButtonSelector(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	
	 // 以下这个方法也可以把你的图片数组传过来，以StateListDrawable来设置图片状态，来表现button的各中状态。未选
    // 中，按下，选中效果。
    public StateListDrawable setbg(Drawable[] mImageIds) {
        StateListDrawable bg = new StateListDrawable();
        Drawable normal = mImageIds[0];
        Drawable selected = mImageIds[1];
        Drawable pressed = mImageIds[2];
        bg.addState(View.PRESSED_ENABLED_STATE_SET, pressed);
        bg.addState(View.ENABLED_FOCUSED_STATE_SET, selected);
        bg.addState(View.ENABLED_STATE_SET, normal);
        bg.addState(View.FOCUSED_STATE_SET, selected);
        bg.addState(View.EMPTY_STATE_SET, normal);
        return bg;
    }
	
    /**
     * 设置TextView点击的Selected
     * @param mImageIds
     * @return
     */
    public StateListDrawable setTextViewbg(Drawable[] mImageIds) {
        StateListDrawable bg = new StateListDrawable();
        Drawable pressed = mImageIds[0];
        Drawable normal = mImageIds[1];
        bg.addState(View.PRESSED_ENABLED_STATE_SET, pressed);
        bg.addState(View.EMPTY_STATE_SET, normal);
        return bg;
    }
    
	
}
