package com.himoo.ydsc.ui.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;

/**
 * 通过代码来改变View的selector效果
 * 
 */
public class ViewSelector {

	/**
	 * 
	 * @param context 上下文
	 * @param noFocusedResId 没有获取焦点时的图片
	 * @param focusedResId   获取焦点时的图片
	 * @param pressResId	   点击时的图片
	 */
	public static void setWidgetSelector(Context context, int noFocusedResId,
			int focusedResId, int pressResId) {
		StateListDrawable drawable = new StateListDrawable();
		// Non focused states
		drawable.addState(
				new int[] { -android.R.attr.state_focused,
						-android.R.attr.state_selected,
						-android.R.attr.state_pressed },
				getDrawable(context, noFocusedResId));
		drawable.addState(new int[] { -android.R.attr.state_focused,
				android.R.attr.state_selected, -android.R.attr.state_pressed },
				getDrawable(context, noFocusedResId));
		
		// Focused states
		drawable.addState(
				new int[] { android.R.attr.state_focused,
						-android.R.attr.state_selected,
						-android.R.attr.state_pressed },
				getDrawable(context, focusedResId));
		drawable.addState(new int[] { android.R.attr.state_focused,
				android.R.attr.state_selected, -android.R.attr.state_pressed },
				getDrawable(context, focusedResId));
		
		// Pressed
		drawable.addState(new int[] { android.R.attr.state_selected,
				android.R.attr.state_pressed },
				getDrawable(context, pressResId));
		drawable.addState(new int[] { android.R.attr.state_pressed },
				getDrawable(context, pressResId));
	}

	/**
	 * 获取本地图片Drawable
	 * 
	 * @param context
	 * @param resId
	 * @return
	 */
	private static Drawable getDrawable(Context context, int resId) {
		return context.getDrawable(resId);
	}

}
