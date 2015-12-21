package com.himoo.ydsc.animation;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;

import com.himoo.ydsc.R;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.Animator.AnimatorListener;
import com.nineoldandroids.animation.ObjectAnimator;

/**
 * 对于NineOldAnimation动画效果封装调用
 * 
 */
public class AnimationUtils {

	private static Animation operatingAnim;

	/**
	 * 设置Y轴的移动动画
	 * 
	 * @param view
	 */
	public static void setViewTranslateUpY(int duration, final View view,
			float formY, float toY) {

		ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view,
				"translationY", formY, toY);
		objectAnimator.setDuration(duration);
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
	 * 
	 * @param view
	 * @param isUp
	 */
	public static void setViewTranslateDownY(int duration, final View view,
			float formY, float toY) {

		ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view,
				"translationY", formY, toY);
		objectAnimator.setDuration(duration);
		objectAnimator.setInterpolator(new LinearInterpolator());
		objectAnimator.addListener(new AnimatorListener() {

			@Override
			public void onAnimationStart(Animator animation) {
				// TODO Auto-generated method stub
				// view.setVisibility(View.VISIBLE);
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
	 * 设置View的旋转动画
	 * 
	 * @param view
	 */
	public static void setViewRotating(Context context, View view) {

		operatingAnim = android.view.animation.AnimationUtils.loadAnimation(
				context, R.anim.anim_img_refresh);
		LinearInterpolator lin = new LinearInterpolator();
		operatingAnim.setInterpolator(lin);
		view.startAnimation(operatingAnim);

	}

	/**
	 * 停止动画
	 * 
	 * @param view
	 */
	public static void cancelAnim(View view) {
		if (operatingAnim != null) {
			operatingAnim.cancel();
			view.clearAnimation();
		}
	}

}
