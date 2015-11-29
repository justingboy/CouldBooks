package com.himoo.ydsc.animation;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.Animator.AnimatorListener;
import com.nineoldandroids.animation.ObjectAnimator;

import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * 对于NineOldAnimation动画效果封装调用
 * 
 */
public class AnimationUtils {

	/**
	 * 设置Y轴的移动动画
	 * 
	 * @param view
	 */
	public static void setViewTranslateUpY(final View view,float formY,float toY) {

		 ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view, "translationY",
				 formY, toY);
		objectAnimator.setDuration(300);
		objectAnimator.setInterpolator(new LinearInterpolator());
		objectAnimator.addListener(new AnimatorListener() {

			@Override
			public void onAnimationStart(Animator animation) {
				// TODO Auto-generated method stub
					view.setVisibility(View.VISIBLE);
			}

			@Override
			public void onAnimationRepeat(Animator animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animator animation) {
				// TODO Auto-generated method stub
				// if (!isUp) {
				// view.setVisibility(View.GONE);
				// }
			}

			@Override
			public void onAnimationCancel(Animator animation) {
				// TODO Auto-generated method stub

			}
		});
		objectAnimator.start();

	}

	/**
	 * 向下的动画
	 * @param view
	 * @param isUp
	 */
	public static void setViewTranslateDownY(final View view,float formY,float toY) {

		ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view,
				"translationY", formY, toY);
		objectAnimator.setDuration(300);
		objectAnimator.setInterpolator(new LinearInterpolator());
		objectAnimator.addListener(new AnimatorListener() {

			@Override
			public void onAnimationStart(Animator animation) {
				// TODO Auto-generated method stub
//					view.setVisibility(View.VISIBLE);
			}

			@Override
			public void onAnimationRepeat(Animator animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animator animation) {
				// TODO Auto-generated method stub
				// if (!isUp) {
				// view.setVisibility(View.GONE);
				// }
			}

			@Override
			public void onAnimationCancel(Animator animation) {
				// TODO Auto-generated method stub

			}
		});
		objectAnimator.start();

	}

}
