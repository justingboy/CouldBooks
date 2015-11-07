package com.ios.edittext;

import com.handmark.pulltorefresh.library.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * 仿IOS右边可删除的
 * 
 */
public class SearchEditText extends EditText implements OnFocusChangeListener,
		OnKeyListener, TextWatcher {
	/**
	 * 图标是否默认在左边
	 */
	private boolean isIconLeft = false;
	/**
	 * 是否点击软键盘搜索
	 */
	private boolean pressSearch = false;
	/**
	 * 软键盘搜索键监听
	 */
	private OnSearchClickListener listener;

	/**
	 * 软键盘搜索键监听
	 */
	private OnEditTextFocuseChangListener focuseListener;

	private Drawable[] drawables; // 控件的图片资源
	private Drawable drawableLeft, drawableDel; // 搜索图标和删除按钮图标
	private int eventX, eventY; // 记录点击坐标
	private Rect rect; // 控件区域
	private Context mContext;

	private boolean isPopup = true;

	public void setOnSearchClickListener(OnSearchClickListener listener) {
		this.listener = listener;
	}

	public void setOnFocuesChangeListener(OnEditTextFocuseChangListener listener) {
		this.focuseListener = listener;
	}

	public interface OnSearchClickListener {
		void onSearchClick(View view);
	}

	public interface OnEditTextFocuseChangListener {
		void onFocuseChange();
	}

	public SearchEditText(Context context) {
		this(context, null);
		init();
		this.mContext = context;
	}

	public SearchEditText(Context context, AttributeSet attrs) {
		this(context, attrs, android.R.attr.editTextStyle);
		this.mContext = context;
		init();
	}

	public SearchEditText(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		this.mContext = context;
		init();
	}

	private void init() {
		setOnFocusChangeListener(this);
		setOnKeyListener(this);
		addTextChangedListener(this);
	}

	@Override
	protected void onDraw(Canvas canvas) {

		if (isIconLeft) { // 如果是默认样式，直接绘制
			if (length() < 1) {
				drawableDel = null;
			}
			this.setCompoundDrawablesWithIntrinsicBounds(drawableLeft, null,
					drawableDel, null);
			super.onDraw(canvas);
		} else { // 如果不是默认样式，需要将图标绘制在中间
			if (drawables == null)
				drawables = getCompoundDrawables();
			if (drawableLeft == null)
				drawableLeft = drawables[0];
			float textWidth = getPaint().measureText(
					(getHint() == null) ? "" : getHint().toString());
			int drawablePadding = getCompoundDrawablePadding();
			int drawableWidth = drawableLeft == null ? 0 : drawableLeft
					.getIntrinsicWidth();
			float bodyWidth = textWidth + drawableWidth + drawablePadding;
			canvas.translate(
					(getWidth() - bodyWidth - getPaddingLeft() - getPaddingRight()) / 2,
					0);
			super.onDraw(canvas);
		}
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		// 被点击时，恢复默认样式
		if (!pressSearch && TextUtils.isEmpty(getText().toString())) {
			isIconLeft = hasFocus;
		}

	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		
		pressSearch = (keyCode == KeyEvent.KEYCODE_ENTER);
		if (pressSearch && listener != null) {
			/* 隐藏软键盘 */
			InputMethodManager imm = (InputMethodManager) v.getContext()
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			if (imm.isActive()) {
				imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
				listener.onSearchClick(v);
			}
		}
		return false;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// 清空edit内容
		if (drawableDel != null && event.getAction() == MotionEvent.ACTION_UP) {
			eventX = (int) event.getRawX();
			eventY = (int) event.getRawY();
			if (rect == null)
				rect = new Rect();
			getGlobalVisibleRect(rect);
			rect.left = rect.right - drawableDel.getIntrinsicWidth()
					- dip2px(mContext, 5);
			if (rect.contains(eventX + dip2px(mContext, 5), eventY)) {
				setText("");
				isPopup = false;
			} else {

			}
		}
		// 删除按钮被按下时改变图标样式
		if (drawableDel != null && event.getAction() == MotionEvent.ACTION_DOWN) {
			eventX = (int) event.getRawX();
			eventY = (int) event.getRawY();
			if (rect == null)
				rect = new Rect();
			getGlobalVisibleRect(rect);
			rect.left = rect.right - drawableDel.getIntrinsicWidth();
			if (rect.contains(eventX, eventY))
				drawableDel = this.getResources().getDrawable(
						R.drawable.edit_delete_pressed_icon);
		} else {
			drawableDel = this.getResources().getDrawable(
					R.drawable.edit_delete_icon);
		}
		if (isPopup) {
			if (focuseListener != null)
				focuseListener.onFocuseChange();
		}
		isPopup = true;

		return super.onTouchEvent(event);
	}

	@Override
	public void afterTextChanged(Editable arg0) {
		if (this.length() < 1) {
			drawableDel = null;
		} else {
			drawableDel = this.getResources().getDrawable(
					R.drawable.edit_delete_icon);
		}
	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
			int arg3) {
	}

	@Override
	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		isIconLeft = true;
	}

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

}
