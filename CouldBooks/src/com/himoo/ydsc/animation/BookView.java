package com.himoo.ydsc.animation;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.himoo.ydsc.R;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.Animator.AnimatorListener;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;

/**
 * A view to show the cover of a book. With method
 * {@link BookView#startOpenBookAnimation(OpenBookAnimEndListener, ViewParent)}
 * and method {@link BookView#startCloseBookAnimation()} to play opening and
 * closing book animations.
 * 
 */
public class BookView extends RelativeLayout implements AnimatorListener {
	public BookView sOpenedBookView;
	// Opening book animation duration
	private static final int OPEN_ANIMATION_DURATION = 800;
	// Closing book animation duration
	public static final int CLOSE_ANIMATION_DURATION = 800;
	// Animation background scales
	private float mBgScaleX;
	private float mBgScaleY;
	// Animation cover scales
	private float mCoverScaleX;
	private float mCoverScaleY;
	// BookView's location in the screen
	private int[] mLocation = new int[2];
	private WindowManager mWindowManager;
	// Parent of animation views(cover, background and loading icon)
	private FrameLayout mWmRootView;
	// cover
	private ImageView mCover = null;
	// Animation cover
	private ImageView mAnimCover;
	// Animation background
	private ImageView mAnimBackground;
	// Animation loading icon
	// private ImageView mLoadingIcon;
	// If opening animation has played.
	public AtomicBoolean mIsOpen = new AtomicBoolean(false);
	// Listener of opening book animation ending
	private OpenBookAnimEndListener mOpenBookAnimEndListener;
	// Total animations played
	private AtomicInteger mAnimationCount = new AtomicInteger(0);
	// Total opening animations scheduled
	private int mTotalOpenBookAnim;
	// The parent of BookView(maybe a GridView or ListView or others)
	// public static PullToRefreshGridView mGridParent = null;
	public ListView mListParent = null;
	// The opening book animation's end x value.
	private float mOpenBookEndBgX = 0;
	// The opening book animation's end x value. It will always be zero.
	private float mOpenBookEndBgY = 0;
	private Context mContext;
	private OpenBookAnimEndListener mListener;
	private Drawable bg;

	public void setOnOpenBookAnimEndListener(OpenBookAnimEndListener listener) {
		this.mListener = listener;
	}

	public interface OpenBookAnimEndListener {
		public void onOpenBookAnimEnd(BookView bookView);
	}

	public BookView(Context context) {
		this(context, null);
		mContext = context;
		// initListener();
	}

	public BookView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		mContext = context;
		// initListener();
	}

	public BookView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		mContext = context;
		mWindowManager = (WindowManager) getContext().getSystemService(
				Context.WINDOW_SERVICE);
		// initListener();
	}

	// private void initListener() {
	// setOnClickListener(new OnClickListener() {
	//
	// @Override
	// public void onClick(View arg0) {
	// if (!mIsOpen.get()) {
	// sOpenedBookView = BookView.this;
	// // startOpenBookAnimation();
	// }
	// }
	// });
	// }

	public synchronized void startOpenBookAnimation(Drawable drawable) {
		startOpenBookAnimation(null, getParent(), drawable);
	}

	/**
	 * Start opening book animation.
	 * 
	 * @param l
	 *            Listener of opening book animation ending
	 * @param parent
	 */
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	public synchronized void startOpenBookAnimation(OpenBookAnimEndListener l,
			ViewParent parent, Drawable readerBg) {
		bg = readerBg;
		mOpenBookAnimEndListener = l;
		// try {
		// mGridParent = parent != null ? (PullToRefreshGridView) parent : null;
		// } catch (ClassCastException e) {
		// mListParent = parent != null ? (ListView) parent : null;
		// }

		if (!mIsOpen.get()) {
			mCover = (ImageView) findViewById(R.id.shelf_book_image);
			if (mCover == null/* || mBackground == null */) {
				return;
			}

			mWmRootView = new FrameLayout(mContext);
			getLocationInWindow(mLocation);
			mWindowManager.addView(mWmRootView, getDefaultWindowParams());
			// new animation views
			mAnimCover = new ImageView(mContext);
			mAnimCover.setScaleType(mCover.getScaleType());
			mAnimCover.setImageDrawable(mCover.getDrawable());

			mAnimBackground = new ImageView(mContext);
			mAnimBackground.setScaleType(mCover.getScaleType());
			// background
			// Drawable readPageBgDrawable = new ColorDrawable(mContext
			// .getResources().getColor(R.color.loading_book_bg_color));
			mAnimBackground.setBackgroundDrawable(readerBg);
			// loading icon
			// mLoadingIcon = new ImageView(mContext);
			// mLoadingIcon.setBackgroundResource(R.drawable.ic_book_loading);
			// mLoadingIcon.setScaleType(ScaleType.CENTER_CROP);
			FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			lp.gravity = Gravity.CENTER;
			ViewGroup.LayoutParams params = mCover.getLayoutParams();
			// Add view to root. Be careful that the height and width of
			// 'params' should be
			// specified values. WRAP_CONTENT or MATCH_PARENT will lead to wrong
			// effect.
			mWmRootView.addView(mAnimBackground, params);
			mWmRootView.addView(mAnimCover, params);
			// mWmRootView.addView(mLoadingIcon, lp);
			// view scale
			DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
			int screenWidth = dm.widthPixels;
			int screenHeight = dm.heightPixels;
			float scaleW = screenWidth / (float) mCover.getWidth();
			float scaleH = screenHeight / (float) mCover.getHeight();
			float baseScale = Math.max(scaleW, scaleH);
			mBgScaleX = baseScale;
			mBgScaleY = baseScale;
			mCoverScaleX = baseScale / 3;
			mCoverScaleY = baseScale;
			// If scaleH is larger than scaleW, the ending x should be smaller
			// to show the loading icon in the middle of
			// the screen.
			if (scaleW < scaleH) {
				mOpenBookEndBgX = (screenWidth - mCover.getWidth() * scaleH) / 2;
			}

			// start animation
			startFlipCoverAnimation();
		}
	}

	private void startFlipCoverAnimation() {
		ViewHelper.setPivotX(mAnimBackground, 0);
		ViewHelper.setPivotY(mAnimBackground, 0);
		ViewHelper.setPivotX(mAnimCover, 0);
		ViewHelper.setPivotY(mAnimCover, 0);
		// Reset total opening animations scheduled.
		mTotalOpenBookAnim = 0;

		// loading icon
		// startIndividualAnim(mLoadingIcon, "scaleX", 0.0f, 1, true);
		// startIndividualAnim(mLoadingIcon, "scaleY", 0.0f, 1, true);
		// background animation
		startIndividualAnim(mAnimBackground, "translationX", mLocation[0],
				mOpenBookEndBgX, true);
		startIndividualAnim(mAnimBackground, "translationY", mLocation[1],
				mOpenBookEndBgY, true);
		startIndividualAnim(mAnimBackground, "scaleX", 1.0f, mBgScaleX, true);
		startIndividualAnim(mAnimBackground, "scaleY", 1.0f, mBgScaleY, true);
		// cover animation
		startIndividualAnim(mAnimCover, "translationX", mLocation[0],
				mOpenBookEndBgX, true);
		startIndividualAnim(mAnimCover, "translationY", mLocation[1],
				mOpenBookEndBgY, true);
		startIndividualAnim(mAnimCover, "scaleX", 1.0f, mCoverScaleX, true);
		startIndividualAnim(mAnimCover, "scaleY", 1.0f, mCoverScaleY, true);
		startIndividualAnim(mAnimCover, "rotationY", 0, -100, true);
	}

	private WindowManager.LayoutParams getDefaultWindowParams() {
		WindowManager.LayoutParams params = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.MATCH_PARENT, 0, 0,
				WindowManager.LayoutParams.TYPE_APPLICATION_PANEL,
				WindowManager.LayoutParams.FLAG_FULLSCREEN,
				PixelFormat.RGBA_8888);

		return params;
	}

	/**
	 * Close book animation.
	 * 
	 * @param gridview
	 *            BookItemView's parent.
	 */
	public synchronized void startCloseBookAnimation() {
		if (mIsOpen.get()) {
			/** You can change mLocation here. */
			// if (mGridParent != null) {
			// View firstView = mGridParent.getChildAt(0);
			// firstView.getLocationInWindow(mLocation);
			// mGridParent = null;
			// } else if (mListParent != null) {
			// View firstView = mListParent.getChildAt(0);
			// firstView.getLocationInWindow(mLocation);
			// mListParent = null;
			// } else {
			// getLocationInWindow(mLocation);
			// }
			// if (mLoadingIcon != null) {
			// mLoadingIcon.setVisibility(View.GONE);
			// }
			if (CLOSE_ANIMATION_DURATION > 0) {
				// Reset open animation count.
				mTotalOpenBookAnim = 0;
				// loading icon
				// startIndividualAnim(mLoadingIcon, "scaleX", 1, 0.0f, false);
				// startIndividualAnim(mLoadingIcon, "scaleY", 1, 0.0f, false);
				// background animation
				startIndividualAnim(mAnimBackground, "translationX",
						mOpenBookEndBgX, mLocation[0], false);
				startIndividualAnim(mAnimBackground, "translationY",
						mOpenBookEndBgY, mLocation[1], false);
				startIndividualAnim(mAnimBackground, "scaleX", mBgScaleX, 1.0f,
						false);
				startIndividualAnim(mAnimBackground, "scaleY", mBgScaleY, 1.0f,
						false);
				// cover animation
				startIndividualAnim(mAnimCover, "translationX",
						mOpenBookEndBgX, mLocation[0], false);
				startIndividualAnim(mAnimCover, "translationY",
						mOpenBookEndBgY, mLocation[1], false);
				startIndividualAnim(mAnimCover, "scaleX", mCoverScaleX, 1.0f,
						false);
				startIndividualAnim(mAnimCover, "scaleY", mCoverScaleY, 1.0f,
						false);
				startIndividualAnim(mAnimCover, "rotationY", -100, 0, false);
			}
		} else {
			if (mAnimationCount.decrementAndGet() <= 0) {
				removeWindowView();
			}
		}
		if (bg != null) {
			bg.setCallback(null);
			bg = null;
		}
	}

	/**
	 * Play one individual animation.
	 * 
	 * @param target
	 * @param property
	 * @param startValue
	 * @param endValue
	 */
	private void startIndividualAnim(View target, String property,
			float startValue, float endValue, boolean isOpen) {
		// Increase total opening animations scheduled.
		mTotalOpenBookAnim++;

		ObjectAnimator animator = ObjectAnimator.ofFloat(target, property,
				startValue, endValue).setDuration(
				isOpen ? OPEN_ANIMATION_DURATION : CLOSE_ANIMATION_DURATION);
		animator.addListener(this);
		animator.start();
	}

	public AtomicBoolean isOpen() {
		return mIsOpen;
	}

	public void hideLoadingView() {
		// if (mLoadingIcon != null) {
		// mLoadingIcon.setVisibility(View.GONE);
		// }
	}

	public void removeWindowView() {
		mIsOpen.set(false);
		if (mWmRootView != null) {
			mWindowManager.removeView(mWmRootView);
			mWmRootView = null;
		}
	}

	@Override
	public void onAnimationEnd(Animator arg0) {
		if (!mIsOpen.get()) {
			if (mAnimationCount.incrementAndGet() >= mTotalOpenBookAnim) {
				mIsOpen.set(true);
				if (mOpenBookAnimEndListener != null) {
					mOpenBookAnimEndListener.onOpenBookAnimEnd(this);
				} else {
					if (mListener != null) {
						mListener.onOpenBookAnimEnd(this);

					}
				}
			}
		} else {
			if (mAnimationCount.decrementAndGet() <= 0) {
				removeWindowView();
			}
		}
	}

	@Override
	public void onAnimationRepeat(Animator arg0) {
	}

	@Override
	public void onAnimationStart(Animator arg0) {

	}

	@Override
	public void onAnimationCancel(Animator arg0) {
	}

}
