package com.himoo.ydsc.util;


import java.io.File;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * 图片工具类
 */
public class ImageUtil {

	/**
	 * 描述：缩放图片.压缩
	 * 
	 * @param file
	 *            File对象
	 * @param desiredWidth
	 *            新图片的宽
	 * @param desiredHeight
	 *            新图片的高
	 * @return Bitmap 新图片
	 */
	public static Bitmap getScaleBitmap(File file, int desiredWidth,
			int desiredHeight) {

		BitmapFactory.Options opts = new BitmapFactory.Options();
		// 设置为true,decodeFile先不创建内存 只获取一些解码边界信息即图片大小信息
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(file.getPath(), opts);
		// 设置缩放比
		int scale = getBitmapScale(opts, desiredWidth, desiredHeight);
		opts.inSampleSize = scale;
		// 默认为ARGB_8888.
		opts.inPreferredConfig = Bitmap.Config.RGB_565;
		// 以下两个字段需一起使用：
		// 产生的位图将得到像素空间，如果系统gc，那么将被清空。当像素再次被访问,
		// 如果Bitmap已经decode，那么将被自动重新解码
		opts.inPurgeable = true;
		// 位图可以共享一个参考输入数据(InputStream、阵列等)
		opts.inInputShareable = true;
		// 创建内存
		opts.inJustDecodeBounds = false;
		// 使图片不抖动
		opts.inDither = false;

		// 得到縮放過後的Bitmap,縮放后的Bitmap的大小和实际ImageView的宽度相接近
		Bitmap resizeBmp = BitmapFactory.decodeFile(file.getPath(), opts);

		return resizeBmp;
	}

	/**
	 * 获得缩放比例
	 * 
	 * @param opts
	 *            Options参数
	 * @param desiredWidth
	 *            需要的宽度
	 * @param desiredHeight
	 *            需要的高度
	 * @return
	 */
	private static int getBitmapScale(BitmapFactory.Options opts,
			int desiredWidth, int desiredHeight) {
		// 获取图片的原始宽度高度
		int srcWidth = opts.outWidth;
		int srcHeight = opts.outHeight;

		int scale = 1;// 展示的图片高和宽都不大于控件的宽高
		int scaleX = Math.round(srcWidth / desiredWidth);
		int scaleY = Math.round(srcHeight / desiredHeight);

		if (scaleX >= scaleY && scaleX > 1)
			scale = scaleX;

		if (scaleY > scaleX && scaleY > 1)
			scale = scaleY;
		return scale;
	}

	/**
	 * 获取压缩后的图片宽和高
	 * 
	 * @param file
	 * @param desWith
	 * @param desHeight
	 * @return
	 */
	public static int[] getScaleBitmapWithAndHeight(File file, int desWith,
			int desHeight) {
		int[] wh = new int[2];

		BitmapFactory.Options opts = new BitmapFactory.Options();
		// 设置为true,decodeFile先不创建内存 只获取一些解码边界信息即图片大小信息
		opts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeFile(file.getPath(), opts);

		// 获取图片的原始宽度高度
		int srcWidth = opts.outWidth;
		int srcHeight = opts.outHeight;

		float scale = 1;// 展示的图片高和宽都不大于控件的宽高
		float scaleX = srcWidth / desWith;
		float scaleY = srcHeight / desHeight;

		if (scaleX >= scaleY && scaleX > 1)
			scale = scaleX;

		if (scaleY > scaleX && scaleY > 1)
			scale = scaleY;

		if (scale == 1) {
			wh[0] = opts.outWidth;
			wh[1] = opts.outHeight;
		} else {

			wh[0] = (int) (opts.outWidth / scale);
			wh[1] = (int) (opts.outHeight / scale);
		}

		releaseBitmap(bitmap);

		return wh;

	}

	/**
	 * 释放Bitmap对象.
	 * 
	 * @param bitmap
	 *            要释放的Bitmap
	 */
	public static void releaseBitmap(Bitmap bitmap) {
		if (bitmap != null) {
			try {
				if (!bitmap.isRecycled()) {
					bitmap.recycle();
				}
			} catch (Exception e) {
			}
			bitmap = null;
			System.gc();
		}
	}

}