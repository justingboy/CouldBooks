package com.himoo.ydsc.reader.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.himoo.ydsc.R;
import com.himoo.ydsc.util.SharedPreferences;

public class ColorPickerView extends View {

	private String TAG = "colorPicker";
	private Context mContext;
	private Paint mRightPaint; // 画笔
	private int mHeight; // view高
	private int mWidth; // view宽
	private int[] mRightColors;
	private int LEFT_WIDTH;
	private Bitmap mLeftBitmap;
	private Bitmap mLeftBitmap2;
	private Bitmap mTextBitmap;
	private Paint mBitmapPaint;
	private Paint mMyPaint;
	private PointF mLeftSelectPoint;
	private PointF mTextSelectPoint;
	private OnColorChangedListener mChangedListenerD;
	private boolean mLeftMove = false;
	private float mLeftBitmapRadius;
	private float mTextBitmapRadius;
	private Bitmap mGradualChangeBitmap;
	private Bitmap mOutBitmap;
	private Bitmap bitmapTemp;
	public int newWidgth;
	public int newHeigh;
	public static String hexColor = "FFFFFF";
	public static int ColorText = 0;

	public boolean isFirstCanvas = true;
	public boolean isBottonOrUp = false;

	private PointF textPoint = new PointF(150, 150);
	private PointF leftPoint = new PointF(300, 150);
	private PointF lastPoint = new PointF(0, 0);

	public ColorPickerView(Context context) {
		this(context, null);
	}

	public ColorPickerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init();
	}

	public void setOnColorChangedListenner(OnColorChangedListener listener) {
		mChangedListenerD = listener;
	}

	private void init() {

		textPoint.x = SharedPreferences.getInstance().getFloat("textPointX", 150);
		textPoint.y = SharedPreferences.getInstance().getFloat("textPointY", 150);
		leftPoint.x = SharedPreferences.getInstance().getFloat("leftPointX", 300);
		leftPoint.y = SharedPreferences.getInstance().getFloat("leftPointY", 150);
		
		
		bitmapTemp = BitmapFactory.decodeResource(getResources(),
				R.drawable.seban);

		mRightPaint = new Paint();
		mMyPaint = new Paint();
		mMyPaint.setColor(Color.BLACK);
		mRightPaint.setStyle(Paint.Style.FILL);
		mRightPaint.setStrokeWidth(1);

		mRightColors = new int[3];
		mRightColors[0] = Color.WHITE;
		mRightColors[2] = Color.BLACK;

		mBitmapPaint = new Paint();

		mLeftBitmap = BitmapFactory.decodeResource(mContext.getResources(),
				R.drawable.bj_tuodongshi);
		// mLeftBitmap2 = BitmapFactory.decodeResource(mContext.getResources(),
		// R.drawable.reading__color_view__button_press);

		mTextBitmap = BitmapFactory.decodeResource(mContext.getResources(),
				R.drawable.wz_xuanzhong);

		mTextBitmapRadius = mTextBitmap.getWidth() / 2;
		mLeftBitmapRadius = mLeftBitmap.getWidth() / 2;
		Log.i("msg", "Text->width=" + mTextBitmap.getWidth());
		Log.i("msg", "Text->height=" + mTextBitmap.getHeight());
		Log.i("msg", "Left->width=" + mLeftBitmap.getWidth());
		Log.i("msg", "Left->height=" + mLeftBitmap.getHeight());
		mLeftSelectPoint = new PointF(0, 0);
		mTextSelectPoint = new PointF(0, 0);

		newWidgth = BitmapFactory.decodeResource(getResources(),
				R.drawable.seban).getWidth();
		newHeigh = BitmapFactory.decodeResource(getResources(),
				R.drawable.seban).getHeight();

	}

	// important patient please!!!
	@Override
	protected void onDraw(Canvas canvas) {
		// mCan=canvas;
		canvas.drawBitmap(getOutBitmap(), null, new Rect(0, 0, LEFT_WIDTH,
				mHeight), mMyPaint);
		canvas.drawBitmap(getGradual(), null, new Rect(40, 40, LEFT_WIDTH,
				mHeight), mBitmapPaint);
		if (isFirstCanvas) {
			canvas.drawBitmap(mLeftBitmap, leftPoint.x, leftPoint.y,
					mBitmapPaint);
			canvas.drawBitmap(mTextBitmap, textPoint.x, textPoint.y,
					mBitmapPaint);
			Log.i("msg", "----->isFirstCanvas");

		} else {
			if (!hexColor.equals("ffffff")) {
				System.out.println(TAG + "draw2");
				if (mLeftMove) {

					canvas.drawBitmap(mLeftBitmap, leftPoint.x, leftPoint.y,
							mBitmapPaint);

					canvas.drawBitmap(mTextBitmap, mTextSelectPoint.x
							- mTextBitmapRadius, mTextSelectPoint.y
							- mTextBitmapRadius, mBitmapPaint);

				} else {
					try {

						canvas.drawBitmap(mLeftBitmap, mTextSelectPoint.x
								- mLeftBitmapRadius, mTextSelectPoint.y
								- mLeftBitmapRadius, mBitmapPaint);

						canvas.drawBitmap(mTextBitmap, textPoint.x,
								textPoint.y, mBitmapPaint);

					} catch (Exception e) {
						// TODO: handle exception
					}
				}

			}
		}

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int width = MeasureSpec.getSize(widthMeasureSpec);
		int height = MeasureSpec.getSize(heightMeasureSpec);
		if (widthMode == MeasureSpec.EXACTLY) {
			mWidth = width;
		} else {
			mWidth = newHeigh;
		}
		if (heightMode == MeasureSpec.EXACTLY) {
			mHeight = height;
		} else {
			mHeight = newHeigh;
		}
		LEFT_WIDTH = mWidth;
		setMeasuredDimension(mWidth, mHeight);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();
		isFirstCanvas = false;
		isTouchTextBitmap(x, y);
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			ColorText = getLeftColor(x, y);
			// System.out.println("color num="+getLeftColor(x, y));
			if (getLeftColor(x, y) != -1)
				invalidate();
		case MotionEvent.ACTION_MOVE:

		{
			try {
				// mLeftMove = true;
				if (getLeftColor(x, y) != -1) {
					ColorText = getLeftColor(x, y);
					proofLeft(x, y);
					int rmove = Color.red(ColorText);
					int gmove = Color.green(ColorText);
					int bmove = Color.blue(ColorText);
					// System.out.println("color rgb");
					String r11 = Integer.toHexString(rmove);
					String g11 = Integer.toHexString(gmove);
					String b11 = Integer.toHexString(bmove);
					String colorStr1 = r11 + g11 + b11; // 十六进制的颜色字符串。
					// System.out.println("color="+colorStr1);
					hexColor = colorStr1;
					if (mChangedListenerD != null) {

						if (!mLeftMove)
							mChangedListenerD.onbgColorChanged(ColorText,
									colorStr1);
						else
							mChangedListenerD.onTextColorChanged(ColorText,
									colorStr1);
					}
					// changeBGLIS.onColorChanged(ColorText);
					invalidate();
				}
			} catch (Exception e) {
				// TODO: handle exception
				// invalidate();
			}

		}
			break;
		case MotionEvent.ACTION_UP:
			try {
				if (getLeftColor(x, y) != -1) {
					ColorText = getLeftColor(x, y);
					// System.out.println("color="+ColorText);
					// mLeftMove = false;
					int rup = Color.red(ColorText);
					int gup = Color.green(ColorText);
					int bup = Color.blue(ColorText);
					// System.out.println("color rgb");
					String rupStr = Integer.toHexString(rup);
					String gupStr = Integer.toHexString(gup);
					String bupStr = Integer.toHexString(bup);
					String colorUpStr = rupStr + gupStr + bupStr; // 十六进制的颜色字符串。
					System.out.println("color=" + colorUpStr);
					hexColor = colorUpStr;
					if (mChangedListenerD != null) {
						if (!mLeftMove)
							mChangedListenerD.onbgColorChanged(ColorText,
									colorUpStr);
						else

							mChangedListenerD.onTextColorChanged(ColorText,
									colorUpStr);
					}
					if (mLeftMove) {
						textPoint.x = x - mTextBitmap.getWidth() / 2;
						textPoint.y = y - mTextBitmap.getHeight() / 2;
						SharedPreferences.getInstance().putFloat("textPointX", textPoint.x);
						SharedPreferences.getInstance().putFloat("textPointY", textPoint.y);
						
						
					} else {
						leftPoint.x = x - mLeftBitmap.getWidth() / 2;
						leftPoint.y = y - mLeftBitmap.getHeight() / 2;
						SharedPreferences.getInstance().putFloat("leftPointX", leftPoint.x);
						SharedPreferences.getInstance().putFloat("leftPointY", leftPoint.y);
					}

					invalidate();
				}
			} catch (Exception e) {
				// TODO: handle exception
				// invalidate();
			}

		}
		return true;
	}

	@Override
	protected void onDetachedFromWindow() {
		if (mGradualChangeBitmap != null
				&& mGradualChangeBitmap.isRecycled() == false) {
			mGradualChangeBitmap.recycle();
		}
		if (mLeftBitmap != null && mLeftBitmap.isRecycled() == false) {
			mLeftBitmap.recycle();
		}
		if (mLeftBitmap2 != null && mLeftBitmap2.isRecycled() == false) {
			mLeftBitmap2.recycle();
		}
		if (mTextBitmap != null && mTextBitmap.isRecycled() == false) {
			mTextBitmap.recycle();
		}
		super.onDetachedFromWindow();
	}

	private Bitmap getGradual() {
		if (mOutBitmap == null) {
			Paint leftPaint = new Paint();
			leftPaint.setStrokeWidth(1);
			mOutBitmap = Bitmap.createBitmap(LEFT_WIDTH + 40, mHeight + 40,
					Config.RGB_565);
			mGradualChangeBitmap.eraseColor(Color.BLACK);
			Canvas canvas = new Canvas(mOutBitmap);
			canvas.drawBitmap(bitmapTemp, null, new Rect(0, 0, LEFT_WIDTH,
					mHeight), mBitmapPaint);
		}
		return mOutBitmap;
	}

	private Bitmap getOutBitmap() {
		if (mGradualChangeBitmap == null) {
			Paint leftPaint = new Paint();
			leftPaint.setStrokeWidth(1);
			mGradualChangeBitmap = Bitmap.createBitmap(LEFT_WIDTH, mHeight,
					Config.RGB_565);
			mGradualChangeBitmap.eraseColor(Color.WHITE);
		}
		return mGradualChangeBitmap;
	}

	// 校正xy
	private void proofLeft(float x, float y) {
		if (x < 40) {
			isBottonOrUp = true;
			lastPoint.x = 50;
			mLeftSelectPoint.x = 50;
			mTextSelectPoint.x = 50;
			// leftPoint.x = 40;
			// textPoint.x = 40;

		} else if (x > (LEFT_WIDTH - 40)) {
			isBottonOrUp = true;
			mLeftSelectPoint.x = LEFT_WIDTH - 50;
			mTextSelectPoint.x = LEFT_WIDTH - 50;
			lastPoint.x = LEFT_WIDTH - 50;
			// leftPoint.x = LEFT_WIDTH - 40;
			// textPoint.x = LEFT_WIDTH - 40;

		} else {
			isBottonOrUp = false;
			mLeftSelectPoint.x = x;
			mTextSelectPoint.x = x;
		}
		if (y < 40) {
			isBottonOrUp = true;
			Log.i("HH", "y < 0 = " + y + "--" + mTextBitmap.getHeight() / 2);
			mLeftSelectPoint.y = 50;
			mTextSelectPoint.y = 50;
			lastPoint.y = 50;
			// leftPoint.y = 40;
			// textPoint.y = 40;

		} else if (y > (mHeight - 40)) {
			isBottonOrUp = true;
			Log.i("HH", "y > 0 = " + y);
			mLeftSelectPoint.y = mHeight - 50;
			mTextSelectPoint.y = mHeight - 50;
			lastPoint.y = mHeight - 50;
			// leftPoint.y = mHeight - 40;
			// textPoint.y = mHeight - 40;
		} else {
			isBottonOrUp = false;
			mLeftSelectPoint.y = y;
			mTextSelectPoint.y = y;
		}
	}

	private int getLeftColor(float x, float y) {
		Bitmap temp = getGradual();
		// 为了防止越界
		int intX = (int) x;
		int intY = (int) y;
		if (intX < 0)
			intX = 0;
		if (intY < 0)
			intY = 0;
		if (intX >= temp.getWidth()) {
			intX = temp.getWidth() - 1;
		}
		if (intY >= temp.getHeight()) {
			intY = temp.getHeight() - 1;
		}

		System.out.println("leftColor" + temp.getPixel(intX, intY));
		return temp.getPixel(intX, intY);
	}

	// ### 内部类 ###
	public interface OnColorChangedListener {
		public void onbgColorChanged(int color, String hexStrColor);

		public void onTextColorChanged(int color, String hexStrColor);
	}

	/**
	 * 根据x y的位置判断是在触摸哪个按钮
	 * 
	 * @param pointX
	 * @param pointY
	 */
	public void isTouchTextBitmap(float pointX, float pointY) {

		if (textPoint.x < pointX
				&& pointX < textPoint.x + mTextBitmap.getWidth()
				&& textPoint.y < pointY
				&& pointY < textPoint.y + mTextBitmap.getHeight()) {
			// textPoint.x = pointX;
			// textPoint.y = pointY;

			mLeftMove = true;
		} else if (leftPoint.x < pointX
				&& pointX < leftPoint.x + mLeftBitmap.getWidth()
				&& leftPoint.y < pointY
				&& pointY < leftPoint.y + mLeftBitmap.getHeight()) {
			// leftPoint.x = pointX;
			// leftPoint.y = pointY;

			mLeftMove = false;
		}
	}

	@Override
	protected void onVisibilityChanged(View changedView, int visibility) {
		// TODO Auto-generated method stub
		if (visibility == View.INVISIBLE) {
			SharedPreferences.getInstance().putFloat("textPointX", textPoint.x);
			SharedPreferences.getInstance().putFloat("textPointY", textPoint.y);
			SharedPreferences.getInstance().putFloat("leftPointX", leftPoint.x);
			SharedPreferences.getInstance().putFloat("leftPointY", leftPoint.x);
		}
		super.onVisibilityChanged(changedView, visibility);

	}

}
