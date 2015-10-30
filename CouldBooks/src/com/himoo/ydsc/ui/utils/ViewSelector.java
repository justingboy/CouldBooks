package com.himoo.ydsc.ui.utils;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParserException;

import com.himoo.ydsc.R;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;

/**
 * 通过代码来改变View的selector效果
 * 
 */
public class ViewSelector {

	/**
	 * 
	 * @param context
	 *            上下文
	 * @param checkResId
	 *            选中时的图片
	 * @param noCheckResId
	 *            默认的图片
	 */
	public static StateListDrawable creatWidgetSelector(Context context,
			int checkResId, int noCheckResId) {
		StateListDrawable drawable = new StateListDrawable();

		// check
		drawable.addState(new int[] { android.R.attr.state_enabled,
				android.R.attr.state_checked },
				getDrawable(context, checkResId));
		// default
		drawable.addState(new int[] {}, getDrawable(context, noCheckResId));
		return drawable;
	}

	/**
	 * 
	 * @param context
	 *            上下文
	 * @param checkResId
	 *            选中时的图片
	 * @param noCheckResId
	 *            默认的图片
	 * @return StateListDrawable
	 */
	public static ColorStateList creatTextColorSelector(Context context) {
		XmlResourceParser xrp = context.getResources().getXml(
				R.color.main_rb_textcolor_selector);
		ColorStateList colors = null;
		try {
			colors = ColorStateList.createFromXml(context.getResources(), xrp);
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return colors;
	}

	/**
	 * 获取本地图片Drawable
	 * 
	 * @param context
	 * @param resId
	 * @return Drawable
	 */
	private static Drawable getDrawable(Context context, int resId) {
		return context.getResources().getDrawable(resId);
	}

}
