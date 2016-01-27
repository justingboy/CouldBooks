package com.himoo.ydsc.reader.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Region;
import android.graphics.drawable.GradientDrawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Scroller;

import com.himoo.ydsc.R;
import com.himoo.ydsc.config.SpConstant;
import com.himoo.ydsc.util.DeviceUtil;
import com.himoo.ydsc.util.SharedPreferences;

/**
 * 该类只实现了翻书效果
 * 
 * 
 */
public class PageWidget extends View {

	// 记录上一次滑动的点
	private int aniTouchX, aniTouchY, aniDx, aniDy;
	private boolean isMove = false;

	private int mShadowWidth = 20;
	private float firstDownX = 0;
	private float firstDownY = 0;

	private int duration = 450;

	private int mWidth;// 宽度
	private int mHeight;// 高度
	private int mCornerX = 0; // 拖拽点对应的页脚
	private int mCornerY = 0;// 拖拽点对应的页脚
	private Path mPath0;
	private Path mPath1;
	// 当前的Bitmap
	Bitmap mCurPageBitmap = null; // 当前页
	// 下一页的Bitmap
	Bitmap mNextPageBitmap = null;

	PointF mTouch = new PointF(); // 拖拽点
	PointF mBezierStart1 = new PointF(); // 贝塞尔曲线起始点
	PointF mBezierControl1 = new PointF(); // 贝塞尔曲线控制点
	PointF mBeziervertex1 = new PointF(); // 贝塞尔曲线顶点
	PointF mBezierEnd1 = new PointF(); // 贝塞尔曲线结束点

	PointF mBezierStart2 = new PointF(); // 另一条贝塞尔曲线
	PointF mBezierControl2 = new PointF();// 贝塞尔曲线的的控制点
	PointF mBeziervertex2 = new PointF();// 贝塞尔曲线的顶点
	PointF mBezierEnd2 = new PointF();// 贝塞尔曲线的终点

	float mMiddleX;
	float mMiddleY;// 中间坐标
	float mDegrees;// 角度，用于绘制翻起页面的
	float mTouchToCornerDis;// 触摸位置与拐角点的位置间的距离
	ColorMatrixColorFilter mColorMatrixFilter;// 颜色矩阵（20）
	Matrix mMatrix;
	float[] mMatrixArray = { 0, 0, 0, 0, 0, 0, 0, 0, 1.0f };
	/** 拐角点的位置是否属于右上左下 */
	boolean mIsRTandLB; // 拐角点的位置是否属于右上左下
	// 返回三角形最长的边
	float mMaxLength = 0;

	int[] mBackShadowColors;// 前景色
	int[] mFrontShadowColors;// 背景色

	// 渐变图片，共有8中方向，3种颜色的变化组合
	private GradientDrawable mBackShadowDrawableLR;
	private GradientDrawable mBackShadowDrawableRL;
	private GradientDrawable mFolderShadowDrawableLR;
	private GradientDrawable mFolderShadowDrawableRL;

	private GradientDrawable mFrontShadowDrawableHBT;
	private GradientDrawable mFrontShadowDrawableHTB;
	private GradientDrawable mFrontShadowDrawableVLR;
	private GradientDrawable mFrontShadowDrawableVRL;

	Paint mPaint;

	Scroller mScroller;
	private boolean isNightMode;
	private boolean isAutoNightMode;
	private Context mContext;
	private boolean isTouchOnMove = false;

	public static enum Mode {
		TURN_LEFT(0), TURN_CURL(1), TURN_NO(2), TURN_MOVE(3), TURN_UPANDDOWN(4);

		public int Auto;

		Mode(int auto) {
			Auto = auto;
		}
	}

	/** 默认动画的类型 */
	public Mode mMode = Mode.TURN_CURL;

	public OnTouchClickListener listener;

	public interface OnTouchClickListener {
		public void onReaderOnClick();

		public boolean onReaderFilpPage(MotionEvent event);
	}

	public PageWidget(Context context, int w, int h,
			OnTouchClickListener listener, boolean isNeedMogoAd) {
		super(context);
		// TODO Auto-generated constructor stub
		this.mContext = context;
		this.mWidth = w;
		if (isNeedMogoAd) {
			int mogoAdHeight = (int) mContext.getResources().getDimension(
					R.dimen.mogoAd_height);
			this.mHeight = h - mogoAdHeight;
		} else {
			this.mHeight = h;
		}
		mMaxLength = (float) Math.hypot(mWidth, mHeight);
		mShadowWidth = DeviceUtil.dip2px(context, 10);
		this.listener = listener;
		mPath0 = new Path();
		mPath1 = new Path();
		createDrawable();
		setDrawingCacheEnabled(true);

		mPaint = new Paint();
		mPaint.setStyle(Paint.Style.FILL);

		ColorMatrix cm = new ColorMatrix();
		float array[] = { 0.55f, 0, 0, 0, 80.0f, 0, 0.55f, 0, 0, 80.0f, 0, 0,
				0.55f, 0, 80.0f, 0, 0, 0, 0.2f, 0 };
		cm.set(array);

		mColorMatrixFilter = new ColorMatrixColorFilter(cm);
		mMatrix = new Matrix();
		mScroller = new Scroller(context, new LinearInterpolator());

		mTouch.x = 0.01f; // 不让x,y为0,否则在点计算时会有问题
		mTouch.y = 0.01f;
		initMode();
	}

	/**
	 * 初始化夜间模式
	 */
	public void initNightMode() {
		isNightMode = SharedPreferences.getInstance().getBoolean(
				SpConstant.BOOK_SETTING_NIGHT_MODE, false);
		isAutoNightMode = SharedPreferences.getInstance().getBoolean(
				SpConstant.BOOK_SETTING_AUTO_NIGHT_MODE_YES, false);
	}

	/**
	 * 计算拖拽点对应的拖拽脚 表示在哪个拐角处，上下左右共四个点
	 */
	public void calcCornerXY(float x, float y) {
		if (x <= mWidth / 2)
			mCornerX = 0;
		else
			mCornerX = mWidth;
		if (y <= mHeight / 2)
			mCornerY = 0;
		else
			mCornerY = mHeight;

		// 表示拐角点在
		if ((mCornerX == 0 && mCornerY == mHeight)
				|| (mCornerX == mWidth && mCornerY == 0))
			mIsRTandLB = true;
		else
			mIsRTandLB = false;
	}

	/**
	 * 处理触摸时坐标的改变
	 * 
	 * @param event
	 * @return
	 */
	public boolean doTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub

		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			isTouchOnMove = false;
			mTouch.x = event.getX();
			mTouch.y = event.getY();
			firstDownX = mTouch.x;
			firstDownY = mTouch.y;
			isMove = false;
			calcCornerXY(mTouch.x, mTouch.y);
			this.postInvalidate();
		}

		if (event.getAction() == MotionEvent.ACTION_MOVE) {

			mTouch.x = event.getX();
			mTouch.y = event.getY();
			this.postInvalidate();

		}

		if (event.getAction() == MotionEvent.ACTION_UP) {
			isMove = false;
			isTouchOnMove = false;
			if (canDragOver()) {
				if (mMode == Mode.TURN_UPANDDOWN) {
					startUpdownAnimation(duration);
				} else {
					startAnimation(duration);
				}
			} else {
				mTouch.x = mCornerX - 0.09f;
				mTouch.y = mCornerY - 0.09f;
			}
			this.postInvalidate();
		}
		// return super.onTouchEvent(event);
		return true;
	}

	/**
	 * 求解直线P1P2和直线P3P4的交点坐标
	 * 
	 * @param P1
	 * @param P2
	 * @param P3
	 * @param P4
	 * @return
	 */
	public PointF getCross(PointF P1, PointF P2, PointF P3, PointF P4) {
		PointF CrossP = new PointF();
		// 二元函数通式： y=ax+b，斜率，+b
		float a1 = (P2.y - P1.y) / (P2.x - P1.x);
		float b1 = ((P1.x * P2.y) - (P2.x * P1.y)) / (P1.x - P2.x);

		float a2 = (P4.y - P3.y) / (P4.x - P3.x);
		float b2 = ((P3.x * P4.y) - (P4.x * P3.y)) / (P3.x - P4.x);
		// 两条直线交点
		CrossP.x = (b2 - b1) / (a1 - a2);
		CrossP.y = a1 * CrossP.x + b1;
		return CrossP;
	}

	/**
	 * 计算个各个点的位置坐标
	 */
	private void calcPoints() {
		// 中点（触摸点与拐角点的连线的中点）
		mMiddleX = (mTouch.x + mCornerX) / 2;
		mMiddleY = (mTouch.y + mCornerY) / 2;
		// 两条贝塞尔曲线的控制点,计算贝塞尔曲线的控制点，按照三角形的角的互补原理，y/x= mCornerDis/y -->mCornerDis
		// = y*y/x
		mBezierControl1.x = mMiddleX - (mCornerY - mMiddleY)
				* (mCornerY - mMiddleY) / (mCornerX - mMiddleX);
		mBezierControl1.y = mCornerY;
		// 同理
		mBezierControl2.x = mCornerX;
		mBezierControl2.y = mMiddleY - (mCornerX - mMiddleX)
				* (mCornerX - mMiddleX) / (mCornerY - mMiddleY);

		// 贝塞尔曲线的起点
		mBezierStart1.x = mBezierControl1.x - (mCornerX - mBezierControl1.x)
				/ 2;
		mBezierStart1.y = mCornerY;

		// 当mBezierStart1.x < 0或者mBezierStart1.x > 480时
		// 如果继续翻页，会出现BUG故在此限制
		if (mTouch.x > 0 && mTouch.x < mWidth) {
			// Log.d("msg", "mTouch.x ="+mTouch.x+"mWidth = "+mWidth);
			if (mBezierStart1.x < 0 || mBezierStart1.x > mWidth) {
				if (mBezierStart1.x < 0)
					mBezierStart1.x = mWidth - mBezierStart1.x;

				float f1 = Math.abs(mCornerX - mTouch.x);
				float f2 = mWidth * f1 / mBezierStart1.x;
				mTouch.x = Math.abs(mCornerX - f2);

				float f3 = Math.abs(mCornerX - mTouch.x)
						* Math.abs(mCornerY - mTouch.y) / f1;
				mTouch.y = Math.abs(mCornerY - f3);

				mMiddleX = (mTouch.x + mCornerX) / 2;
				mMiddleY = (mTouch.y + mCornerY) / 2;

				mBezierControl1.x = mMiddleX - (mCornerY - mMiddleY)
						* (mCornerY - mMiddleY) / (mCornerX - mMiddleX);
				mBezierControl1.y = mCornerY;

				mBezierControl2.x = mCornerX;
				mBezierControl2.y = mMiddleY - (mCornerX - mMiddleX)
						* (mCornerX - mMiddleX) / (mCornerY - mMiddleY);

				mBezierStart1.x = mBezierControl1.x
						- (mCornerX - mBezierControl1.x) / 2;
			}
		}
		mBezierStart2.x = mCornerX;
		mBezierStart2.y = mBezierControl2.y - (mCornerY - mBezierControl2.y)
				/ 2;

		// 触摸位置与拐角点的位置间的距离,直线长度sprt(x*x+y*y)
		mTouchToCornerDis = (float) Math.hypot((mTouch.x - mCornerX),
				(mTouch.y - mCornerY));

		// 贝塞尔曲线的终点
		mBezierEnd1 = getCross(mTouch, mBezierControl1, mBezierStart1,
				mBezierStart2);
		mBezierEnd2 = getCross(mTouch, mBezierControl2, mBezierStart1,
				mBezierStart2);

		/*
		 * mBeziervertex1.x 推导
		 * ((mBezierStart1.x+mBezierEnd1.x)/2+mBezierControl1.x)/2 化简等价于
		 * (mBezierStart1.x+ 2*mBezierControl1.x+mBezierEnd1.x) / 4
		 */
		// 贝塞尔曲线的顶点
		mBeziervertex1.x = (mBezierStart1.x + 2 * mBezierControl1.x + mBezierEnd1.x) / 4;
		mBeziervertex1.y = (2 * mBezierControl1.y + mBezierStart1.y + mBezierEnd1.y) / 4;
		mBeziervertex2.x = (mBezierStart2.x + 2 * mBezierControl2.x + mBezierEnd2.x) / 4;
		mBeziervertex2.y = (2 * mBezierControl2.y + mBezierStart2.y + mBezierEnd2.y) / 4;
	}

	/**
	 * 绘制当前页
	 * 
	 * @param canvas
	 * @param bitmap
	 * @param path
	 */
	private void drawCurrentPageArea(Canvas canvas, Bitmap bitmap, Path path) {
		mPath0.reset();
		mPath0.moveTo(mBezierStart1.x, mBezierStart1.y);
		mPath0.quadTo(mBezierControl1.x, mBezierControl1.y, mBezierEnd1.x,
				mBezierEnd1.y);
		mPath0.lineTo(mTouch.x, mTouch.y);
		mPath0.lineTo(mBezierEnd2.x, mBezierEnd2.y);
		mPath0.quadTo(mBezierControl2.x, mBezierControl2.y, mBezierStart2.x,
				mBezierStart2.y);
		mPath0.lineTo(mCornerX, mCornerY);
		mPath0.close();

		canvas.save();
		canvas.clipPath(mPath0, Region.Op.XOR);// 异或
		canvas.drawBitmap(bitmap, 0, 0, null);
		canvas.restore();
	}

	/**
	 * 绘制下一页与阴影
	 * 
	 * @param canvas
	 * @param bitmap
	 */
	private void drawNextPageAreaAndShadow(Canvas canvas, Bitmap bitmap) {
		mPath1.reset();
		mPath1.moveTo(mBezierStart1.x, mBezierStart1.y);
		mPath1.lineTo(mBeziervertex1.x, mBeziervertex1.y);
		mPath1.lineTo(mBeziervertex2.x, mBeziervertex2.y);
		mPath1.lineTo(mBezierStart2.x, mBezierStart2.y);
		mPath1.lineTo(mCornerX, mCornerY);
		mPath1.close();
		// 角度
		mDegrees = (float) Math.toDegrees(Math.atan2(mBezierControl1.x
				- mCornerX, mBezierControl2.y - mCornerY));
		int leftx;
		int rightx;
		GradientDrawable mBackShadowDrawable;
		if (mIsRTandLB) {
			leftx = (int) (mBezierStart1.x);
			rightx = (int) (mBezierStart1.x + mTouchToCornerDis / 5);
			mBackShadowDrawable = mBackShadowDrawableLR;
		} else {
			leftx = (int) (mBezierStart1.x - mTouchToCornerDis / 5);
			rightx = (int) mBezierStart1.x;
			mBackShadowDrawable = mBackShadowDrawableRL;
		}
		canvas.save();
		canvas.clipPath(mPath0);
		canvas.clipPath(mPath1, Region.Op.INTERSECT);
		canvas.drawBitmap(bitmap, 0, 0, null);
		canvas.rotate(mDegrees, mBezierStart1.x, mBezierStart1.y);
		mBackShadowDrawable.setBounds(leftx, (int) mBezierStart1.y, rightx,
				(int) (mMaxLength + mBezierStart1.y));
		mBackShadowDrawable.draw(canvas);
		canvas.restore();
	}

	/**
	 * 无动画的翻到下一页
	 * 
	 * @param canvas
	 * @param bitmap
	 */
	private void drawNextPageNoAnim(Canvas canvas, Bitmap bitmap) {
		canvas.save();
		canvas.drawBitmap(bitmap, 0, 0, null);
		canvas.restore();
	}

	/**
	 * 无动画的绘制下一页
	 * 
	 * @param canvas
	 * @param bitmap
	 */
	private void drawCurrentPageNoAnim(Canvas canvas, Bitmap bitmap) {
		canvas.save();
		canvas.drawBitmap(bitmap, 0, 0, null);
		canvas.restore();
	}

	/**
	 * 左右联动动画 绘制当前页
	 * 
	 * @param canvas
	 * @param bitmap
	 */
	private void drawCurrentPageMoveAnim(Canvas canvas, Bitmap bitmap) {
		canvas.save();
		if (firstDownX > mWidth / 2) {
			canvas.drawBitmap(bitmap, mTouch.x - mWidth, 0.0F, null);
			drawShade(canvas, -mWidth + (int) mTouch.x, false);
		} else {
			drawShade(canvas, -mWidth + (int) mTouch.x, true);
			canvas.drawBitmap(bitmap, this.mTouch.x, 0.0F, null);
		}

		canvas.restore();
	}

	/**
	 * 覆盖动画绘制下一页
	 * 
	 * @param canvas
	 * @param pointLeft
	 */
	private void drawNextPageMoveAnim(Canvas canvas, Bitmap nextBitmap) {
		canvas.save();
		canvas.drawBitmap(nextBitmap, 0.0F, 0.0F, null);
		canvas.restore();
	}

	/**
	 * 上和下联动动画 绘制当前页
	 * 
	 * @param canvas
	 * @param bitmap
	 */
	private void drawCurrentPageUpAnim(Canvas canvas, Bitmap bitmap) {
		canvas.save();
		if (firstDownY > mHeight / 2) {
			canvas.drawBitmap(bitmap, 0.0F, mTouch.y - mHeight, null);
		} else {
			canvas.drawBitmap(bitmap, 0.0F, this.mTouch.y, null);
		}

		canvas.restore();
	}

	/**
	 * 上和下联动动画 绘制下一页
	 * 
	 * @param canvas
	 * @param mCurPageBitmap2
	 */
	private void drawNextPageUpAnim(Canvas canvas, Bitmap nextBitmap) {
		// TODO Auto-generated method stub
		canvas.save();
		canvas.drawBitmap(nextBitmap, 0.0F, 0.0F, null);
		canvas.restore();
	}

	/**
	 * 设置书页的当前Bitmap和下一个Bitmap
	 * 
	 * @param bm1
	 * @param bm2
	 */
	public void setBitmaps(Bitmap bm1, Bitmap bm2) {
		mCurPageBitmap = bm1;
		mNextPageBitmap = bm2;
	}

	/**
	 * 设置读书界面的高和宽
	 * 
	 * @param w
	 * @param h
	 */
	public void setScreen(int w, int h) {
		mWidth = w;
		mHeight = h;
	}

	/**
	 * 设置翻书动画
	 */
	public void setTurnPageType() {
		initMode();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (SharedPreferences.getInstance().getBoolean(
				SpConstant.BOOK_AUTO_COLOR, false)) {
			canvas.drawColor(5 + SharedPreferences.getInstance().getInt(
					SpConstant.BOOK_AUTO_COLOR_BG, 0xFFAAAAAA));
		} else {
			if (isNightMode || isAutoNightMode) {
				canvas.drawColor(0xFF535352);
			} else {
				canvas.drawColor(0xFFAAAAAA);
			}
		}
		calcPoints();
		perforTurnPageAnim(canvas);

	}

	/**
	 * 执行翻书动画
	 */
	public void perforTurnPageAnim(Canvas canvas) {
		switch (mMode) {
		case TURN_LEFT:
			break;
		case TURN_CURL:
			drawCurrentPageArea(canvas, mCurPageBitmap, mPath0);
			drawNextPageAreaAndShadow(canvas, mNextPageBitmap);
			drawCurrentPageShadow(canvas);
			drawCurrentBackArea(canvas, mCurPageBitmap);
			break;
		case TURN_NO:
			drawCurrentPageNoAnim(canvas, mCurPageBitmap);
			drawNextPageNoAnim(canvas, mNextPageBitmap);
			break;
		case TURN_MOVE:
			drawNextPageMoveAnim(canvas, mNextPageBitmap);
			drawCurrentPageMoveAnim(canvas, mCurPageBitmap);
			break;
		case TURN_UPANDDOWN:
			drawNextPageUpAnim(canvas, mNextPageBitmap);
			drawCurrentPageUpAnim(canvas, mCurPageBitmap);
			break;

		default:
			break;
		}

	}

	/**
	 * 初始化翻书模式
	 */
	public void initMode() {
		int type = SharedPreferences.getInstance().getInt(
				SpConstant.BOOK_TURNPAGE_TYPE, 1);
		switch (type) {
		case 0:
			mMode = Mode.TURN_LEFT;
			break;
		case 1:
			mMode = Mode.TURN_CURL;
			duration = 400;
			break;
		case 2:
			mMode = Mode.TURN_NO;
			break;
		case 3:
			mMode = Mode.TURN_MOVE;
			duration = 600;
			break;
		case 4:
			mMode = Mode.TURN_UPANDDOWN;
			duration = 600;
			break;

		default:
			break;
		}

	}

	/**
	 * 创建阴影的GradientDrawable,渐变图形
	 */
	private void createDrawable() {
		int[] color = { 0x333333, 0x7F333333 };
		mFolderShadowDrawableRL = new GradientDrawable(
				GradientDrawable.Orientation.RIGHT_LEFT, color);
		mFolderShadowDrawableRL
				.setGradientType(GradientDrawable.LINEAR_GRADIENT);

		mFolderShadowDrawableLR = new GradientDrawable(
				GradientDrawable.Orientation.LEFT_RIGHT, color);
		mFolderShadowDrawableLR
				.setGradientType(GradientDrawable.LINEAR_GRADIENT);

		mBackShadowColors = new int[] { 0xAF111111, 0x111111 };
		mBackShadowDrawableRL = new GradientDrawable(
				GradientDrawable.Orientation.RIGHT_LEFT, mBackShadowColors);
		mBackShadowDrawableRL.setGradientType(GradientDrawable.LINEAR_GRADIENT);

		mBackShadowDrawableLR = new GradientDrawable(
				GradientDrawable.Orientation.LEFT_RIGHT, mBackShadowColors);
		mBackShadowDrawableLR.setGradientType(GradientDrawable.LINEAR_GRADIENT);

		mFrontShadowColors = new int[] { 0x7F111111, 0x111111 };
		mFrontShadowDrawableVLR = new GradientDrawable(
				GradientDrawable.Orientation.LEFT_RIGHT, mFrontShadowColors);
		mFrontShadowDrawableVLR
				.setGradientType(GradientDrawable.LINEAR_GRADIENT);

		mFrontShadowDrawableVRL = new GradientDrawable(
				GradientDrawable.Orientation.RIGHT_LEFT, mFrontShadowColors);
		mFrontShadowDrawableVRL
				.setGradientType(GradientDrawable.LINEAR_GRADIENT);

		mFrontShadowDrawableHTB = new GradientDrawable(
				GradientDrawable.Orientation.TOP_BOTTOM, mFrontShadowColors);
		mFrontShadowDrawableHTB
				.setGradientType(GradientDrawable.LINEAR_GRADIENT);

		mFrontShadowDrawableHBT = new GradientDrawable(
				GradientDrawable.Orientation.BOTTOM_TOP, mFrontShadowColors);
		mFrontShadowDrawableHBT
				.setGradientType(GradientDrawable.LINEAR_GRADIENT);
	}

	/**
	 * 绘制每页右边的阴影
	 * 
	 * @param canvas
	 */
	private void drawCurrentPageShadow(Canvas canvas) {
		double degree;
		if (mIsRTandLB) {
			degree = Math.PI
					/ 4
					- Math.atan2(mBezierControl1.y - mTouch.y, mTouch.x
							- mBezierControl1.x);
		} else {
			degree = Math.PI
					/ 4
					- Math.atan2(mTouch.y - mBezierControl1.y, mTouch.x
							- mBezierControl1.x);
		}
		// 翻起页阴影顶点与touch点的距离
		double d1 = (float) mShadowWidth * 1.414 * Math.cos(degree);
		double d2 = (float) mShadowWidth * 1.414 * Math.sin(degree);
		float x = (float) (mTouch.x + d1);
		float y;
		if (mIsRTandLB) {
			y = (float) (mTouch.y + d2);
		} else {
			y = (float) (mTouch.y - d2);
		}
		mPath1.reset();
		mPath1.moveTo(x, y);
		mPath1.lineTo(mTouch.x, mTouch.y);
		mPath1.lineTo(mBezierControl1.x, mBezierControl1.y);
		mPath1.lineTo(mBezierStart1.x, mBezierStart1.y);
		mPath1.close();
		float rotateDegrees;
		canvas.save();

		canvas.clipPath(mPath0, Region.Op.XOR);
		canvas.clipPath(mPath1, Region.Op.INTERSECT);
		int leftx;
		int rightx;
		GradientDrawable mCurrentPageShadow;
		if (mIsRTandLB) {
			leftx = (int) (mBezierControl1.x);
			rightx = (int) mBezierControl1.x + mShadowWidth;
			mCurrentPageShadow = mFrontShadowDrawableVLR;
		} else {
			leftx = (int) (mBezierControl1.x - mShadowWidth);
			rightx = (int) mBezierControl1.x + 1;
			mCurrentPageShadow = mFrontShadowDrawableVRL;
		}

		rotateDegrees = (float) Math.toDegrees(Math.atan2(mTouch.x
				- mBezierControl1.x, mBezierControl1.y - mTouch.y));
		canvas.rotate(rotateDegrees, mBezierControl1.x, mBezierControl1.y);
		mCurrentPageShadow.setBounds(leftx,
				(int) (mBezierControl1.y - mMaxLength), rightx,
				(int) (mBezierControl1.y));
		mCurrentPageShadow.draw(canvas);
		canvas.restore();

		mPath1.reset();
		mPath1.moveTo(x, y);
		mPath1.lineTo(mTouch.x, mTouch.y);
		mPath1.lineTo(mBezierControl2.x, mBezierControl2.y);
		mPath1.lineTo(mBezierStart2.x, mBezierStart2.y);
		mPath1.close();
		canvas.save();
		canvas.clipPath(mPath0, Region.Op.XOR);
		canvas.clipPath(mPath1, Region.Op.INTERSECT);
		if (mIsRTandLB) {
			leftx = (int) (mBezierControl2.y);
			rightx = (int) (mBezierControl2.y + mShadowWidth);
			mCurrentPageShadow = mFrontShadowDrawableHTB;
		} else {
			leftx = (int) (mBezierControl2.y - mShadowWidth);
			rightx = (int) (mBezierControl2.y + 1);
			mCurrentPageShadow = mFrontShadowDrawableHBT;
		}
		rotateDegrees = (float) Math.toDegrees(Math.atan2(mBezierControl2.y
				- mTouch.y, mBezierControl2.x - mTouch.x));
		canvas.rotate(rotateDegrees, mBezierControl2.x, mBezierControl2.y);
		float temp;
		if (mBezierControl2.y < 0)
			temp = mBezierControl2.y - mHeight;
		else
			temp = mBezierControl2.y;

		int hmg = (int) Math.hypot(mBezierControl2.x, temp);
		if (hmg > mMaxLength) {

			mCurrentPageShadow.setBounds(
					(int) (mBezierControl2.x - mShadowWidth) - hmg, leftx,
					(int) (mBezierControl2.x + mMaxLength) - hmg, rightx);
		} else {

			mCurrentPageShadow.setBounds(
					(int) (mBezierControl2.x - mMaxLength), leftx,
					(int) (mBezierControl2.x), rightx);
		}
		mCurrentPageShadow.draw(canvas);
		canvas.restore();
	}

	/**
	 * 绘制翻起页背面
	 * 
	 * @param canvas
	 * @param bitmap
	 */
	private void drawCurrentBackArea(Canvas canvas, Bitmap bitmap) {
		int i = (int) (mBezierStart1.x + mBezierControl1.x) / 2;
		float f1 = Math.abs(i - mBezierControl1.x);
		int i1 = (int) (mBezierStart2.y + mBezierControl2.y) / 2;
		float f2 = Math.abs(i1 - mBezierControl2.y);
		float f3 = Math.min(f1, f2);
		mPath1.reset();
		mPath1.moveTo(mBeziervertex2.x, mBeziervertex2.y);
		mPath1.lineTo(mBeziervertex1.x, mBeziervertex1.y);
		mPath1.lineTo(mBezierEnd1.x, mBezierEnd1.y);
		mPath1.lineTo(mTouch.x, mTouch.y);
		mPath1.lineTo(mBezierEnd2.x, mBezierEnd2.y);
		mPath1.close();// 闭合曲线,连接到到起点，这段线路就是翻起也区域
		GradientDrawable mFolderShadowDrawable;
		int left;
		int right;
		if (mIsRTandLB) {
			left = (int) (mBezierStart1.x - 1);
			right = (int) (mBezierStart1.x + f3 + 1);
			mFolderShadowDrawable = mFolderShadowDrawableLR;
		} else {
			left = (int) (mBezierStart1.x - f3 - 1);
			right = (int) (mBezierStart1.x + 1);
			mFolderShadowDrawable = mFolderShadowDrawableRL;
		}
		canvas.save();
		canvas.clipPath(mPath0);
		canvas.clipPath(mPath1, Region.Op.INTERSECT);// 交集

		mPaint.setColorFilter(mColorMatrixFilter);

		float dis = (float) Math.hypot(mCornerX - mBezierControl1.x,
				mBezierControl2.y - mCornerY);
		float f8 = (mCornerX - mBezierControl1.x) / dis;
		float f9 = (mBezierControl2.y - mCornerY) / dis;
		mMatrixArray[0] = 1 - 2 * f9 * f9;
		mMatrixArray[1] = 2 * f8 * f9;
		mMatrixArray[3] = mMatrixArray[1];
		mMatrixArray[4] = 1 - 2 * f8 * f8;
		mMatrix.reset();
		mMatrix.setValues(mMatrixArray);
		mMatrix.preTranslate(-mBezierControl1.x, -mBezierControl1.y);
		mMatrix.postTranslate(mBezierControl1.x, mBezierControl1.y);
		canvas.drawBitmap(bitmap, mMatrix, mPaint);
		mPaint.setColorFilter(null);
		canvas.rotate(mDegrees, mBezierStart1.x, mBezierStart1.y);
		mFolderShadowDrawable.setBounds(left, (int) mBezierStart1.y, right,
				(int) (mBezierStart1.y + mMaxLength));
		mFolderShadowDrawable.draw(canvas);
		canvas.restore();
	}

	/**
	 * 实现子View的平滑滚动
	 */
	@Override
	public void computeScroll() {
		super.computeScroll();
		if (mScroller.computeScrollOffset()) {
			float x = mScroller.getCurrX();
			float y = mScroller.getCurrY();
			// Log.i("mm", "x = " + x + "y = " + y);
			mTouch.x = x;
			mTouch.y = y;
			postInvalidate();
		}
	}

	/**
	 * 开始动画
	 * 
	 * @param delayMillis
	 */
	private void startAnimation(int delayMillis) {
		if (!this.mScroller.isFinished())
			this.mScroller.abortAnimation();
		int dx, dy;
		if (mCornerX > 0) {
			dx = -(int) (mWidth + mTouch.x);
		} else {
			dx = (int) (mWidth - mTouch.x + mWidth);
		}
		if (mCornerY > 0) {
			dy = (int) (mHeight - mTouch.y);
		} else {
			dy = (int) (1 - mTouch.y); // 防止mTouch.y最终变为0
		}
		aniTouchX = (int) mTouch.x;
		aniTouchY = (int) mTouch.y;
		aniDx = dx;
		aniDy = dy;
		mScroller.startScroll((int) mTouch.x, (int) mTouch.y, dx, dy,
				delayMillis);

		//
		// float f =200;
		// this.mScroller.startScroll((int) this.mTouch.x, 0,
		// (int) (-this.mWidth - f), 0,
		// (int) (400.0F * Math.abs(-this.mWidth - f) / this.mWidth));
		// // if (paramBoolean) {
		//
		// return true;
		// }
		// this.mScroller.startScroll((int) this.movePoint.x, 0,
		// (int) (this.width - f), 0,
		// (int) (400.0F * Math.abs(this.width - f) / this.width));
		//
	}

	/**
	 * 开始动画
	 * 
	 * @param delayMillis
	 */
	private void startUpdownAnimation(int delayMillis) {
		if (!this.mScroller.isFinished())
			this.mScroller.abortAnimation();
		int dx, dy;
		if (firstDownY > mHeight / 2) {
			dy = -(int) mTouch.y;
		} else {
			dy = (int) (mHeight - mTouch.y);
		}
		aniTouchX = (int) mTouch.x;
		aniTouchY = (int) mTouch.y;
		aniDy = dy;
		mScroller.startScroll(0, (int) mTouch.y, 0, dy, delayMillis);
	}

	/**
	 * 执行默认翻书动画
	 */
	public void startAnimation() {
		if (mScroller != null) {
			if (!this.mScroller.isFinished())
				this.mScroller.abortAnimation();
			mScroller.startScroll(aniTouchX, aniTouchY, aniDx, aniDy, duration);
		}
	}

	/**
	 * 执行默认翻书动画
	 */
	public void startUpAnimation() {
		if (mScroller != null) {
			if (!this.mScroller.isFinished())
				this.mScroller.abortAnimation();
			mScroller.startScroll(0, (int) mTouch.y, 0, aniDy, duration);
		}
	}

	/**
	 * 停止滚动的动画
	 */
	public void abortAnimation() {
		if (!mScroller.isFinished()) {
			mScroller.abortAnimation();
		}
	}

	/**
	 * 判断是否可以翻页
	 * 
	 * @return
	 */
	public boolean canDragOver() {
		if (mTouchToCornerDis > mWidth / 40)
			return true;
		return false;
	}

	/**
	 * 是否从左边翻向右边
	 */
	public boolean DragToRight() {
		if (mCornerX > 0)
			return false;
		return true;
	}

	/**
	 * 是否向上滑动
	 * 
	 * @return
	 */
	public boolean DragToUp() {
		if (firstDownY > mHeight / 2) {
			return true;
		}
		return false;
	}

	/**
	 * 关闭夜间模式
	 */
	public void setAutoNightFalse() {
		isAutoNightMode = false;
		isNightMode = false;
	}

	/**
	 * 绘制覆盖动画的阴影
	 * 
	 * @param canvas
	 * @param shadeWidth
	 * @param isLeft
	 */
	private void drawShade(Canvas canvas, int shadeWidth, boolean isLeft) {
		int i = this.mWidth / 2;
		int j = this.mWidth / 20;
		int k = this.mWidth / 50;
		int m = this.mWidth / 100;
		GradientDrawable.Orientation orientation = GradientDrawable.Orientation.LEFT_RIGHT;
		if (isLeft) {
			orientation = GradientDrawable.Orientation.RIGHT_LEFT;
		}
		GradientDrawable gradient1 = new GradientDrawable(orientation,
				new int[] { 0x99000000, 0x66000000 });

		GradientDrawable gradient2 = new GradientDrawable(orientation,
				new int[] { 0x66000000, 0x9333333 });
		int n = shadeWidth + this.mWidth;
		if (Math.abs(n) > m) {
			if (isLeft) {
				gradient1.setBounds(n - m, 0, n, this.mHeight);
			} else {

				gradient1.setBounds(n, 0, n + m, this.mHeight);
			}
			gradient1.draw(canvas);
			int i1 = j - Math.abs(n - i) * (j - k) / i;
			int i2 = n + m;
			if (isLeft) {
				i2 = n - m;
				gradient2.setBounds(i2 - i1, 0, i2, this.mHeight);
			} else {

				gradient2.setBounds(i2, 0, i2 + i1, this.mHeight);
			}
			gradient2.draw(canvas);
		}
	}

	/**
	 * 释放资源
	 */
	public void destroy() {
		destroyDrawingCache();
	}

	/**
	 * 判断是否可以被点击
	 * 
	 * @param event
	 * @return
	 */
	private boolean isClickable(MotionEvent event) {
		int extraWidth = DeviceUtil.dip2px(mContext, 25);
		int disWith = (int) Math.abs(event.getX() - mWidth);
		int disHeight = (int) Math.abs(event.getY() - mHeight);
		if ((extraWidth + mWidth / 3) < disWith
				&& disWith < (2 * mWidth / 3 - extraWidth)
				&& (extraWidth + mHeight / 3) < disHeight
				&& disHeight < (2 * mHeight / 3 - extraWidth)) {
			return true;
		}
		return false;

	}

}
