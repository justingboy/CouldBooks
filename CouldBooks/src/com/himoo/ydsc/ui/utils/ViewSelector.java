package com.himoo.ydsc.ui.utils;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.XmlResourceParser;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.view.View;
import android.widget.Button;

import com.himoo.ydsc.R;
import com.himoo.ydsc.config.BookTheme;
import com.himoo.ydsc.util.DeviceUtil;

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

	/**
	 * 设置圆角颜色背景图片
	 * 
	 * @param color
	 * @return
	 */
	public static Drawable getColorDrawable(int roundRadius, int color) {
		// int strokeWidth = 5; // 3dp
		// int roundRadius = roundRadius; // 8dp
		// int strokeColor = Color.parseColor("#2E3135");
		// int fillColor = Color.parseColor(color);
		GradientDrawable colorDrawable = new GradientDrawable();
		colorDrawable.setColor(color);
		colorDrawable.setCornerRadius(roundRadius);
		// colorDrawable.setStroke(strokeWidth, strokeColor);
		return colorDrawable;
	}

	/**
	 * 设置圆角颜色背景图片
	 * 
	 * @param color
	 * @return
	 */
	public static Drawable getStrokColorDrawable(Context context,
			int roundRadius, int color) {
		// int strokeWidth = 5; // 3dp
		// int roundRadius = roundRadius; // 8dp
		// int strokeColor = Color.parseColor("#2E3135");
		// int fillColor = Color.parseColor(color);
		GradientDrawable colorDrawable = new GradientDrawable();
		// colorDrawable.setColor(color);
		colorDrawable.setStroke(DeviceUtil.dip2px(context, 1), color);
		colorDrawable.setCornerRadius(roundRadius);
		// colorDrawable.setStroke(strokeWidth, strokeColor);
		return colorDrawable;
	}

	/**
	 * 设置Button点击selector
	 * 
	 * @param button
	 */
	@SuppressWarnings("deprecation")
	public static void setButtonSelector(Context context, Button button) {
		Drawable[] mButtonStateRed_down = {
				getColorDrawable(8, BookTheme.THEME_COLOR),
				getColorDrawable(8, BookTheme.BUTTON_COLOR_PRESS),
				getColorDrawable(8, BookTheme.BUTTON_COLOR_PRESS) };
		button.setBackgroundDrawable(new ButtonSelector(context)
				.setbg(mButtonStateRed_down));
	}

	/**
	 * 设置Button点击selector，描边
	 * 
	 * @param button
	 */
	@SuppressWarnings("deprecation")
	public static void setButtonStrokeSelector(Context context, Button button) {
		Drawable[] mButtonStateRed_down = {
				getStrokColorDrawable(context, 8, BookTheme.THEME_COLOR),
				getStrokColorDrawable(context, 8, BookTheme.BUTTON_COLOR_PRESS),
				getStrokColorDrawable(context, 8, BookTheme.BUTTON_COLOR_PRESS) };
		button.setBackgroundDrawable(new ButtonSelector(context)
				.setbg(mButtonStateRed_down));
	}

	/**
	 * 设置描边的背景色
	 * 
	 * @param view
	 */
	@SuppressWarnings("deprecation")
	public static void setViewBackGround(View view) {
		int strokeWidth = 1; // 3dp 边框宽度
		// int roundRadius = 15; // 8dp 圆角半径
		int strokeColor = Color.WHITE;// 边框颜色
		int fillColor = BookTheme.THEME_COLOR;// 内部填充颜色
		// 创建drawable
		GradientDrawable gd = new GradientDrawable();
		gd.setColor(fillColor);
		// gd.setCornerRadius(roundRadius);
		gd.setStroke(strokeWidth, strokeColor);
		view.setBackgroundDrawable(gd);
	}

	/** 对TextView设置不同状态时其文字颜色。 */
	public static ColorStateList createColorStateList(int pressedColor,
			int defaultColor) {
		int[] colors = new int[] { pressedColor, pressedColor, pressedColor,
				defaultColor };
		int[][] states = new int[4][];
		states[0] = new int[] { android.R.attr.state_selected };
		states[1] = new int[] { android.R.attr.state_pressed };
		states[2] = new int[] { android.R.attr.state_focused };
		states[3] = new int[] {};
		ColorStateList colorList = new ColorStateList(states, colors);
		return colorList;
	}

	/** 对TextView设置不同状态时其文字颜色。 */
	public static ColorStateList createTextColorStateList(int pressedColor,
			int defaultColor) {
		int[] colors = new int[] { pressedColor, defaultColor, defaultColor };
		int[][] states = new int[3][];
		states[0] = new int[] { android.R.attr.state_checked };
		states[1] = new int[] { -android.R.attr.state_pressed };
		states[2] = new int[] {};
		ColorStateList colorList = new ColorStateList(states, colors);
		return colorList;
	}
}
