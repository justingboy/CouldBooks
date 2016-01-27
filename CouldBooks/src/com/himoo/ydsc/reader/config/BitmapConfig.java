package com.himoo.ydsc.reader.config;

import android.graphics.Bitmap;

public class BitmapConfig {

	
	private static BitmapConfig mInstacne = new BitmapConfig();
	private Bitmap currentBitmap;
	private Bitmap nextBitmap;

	private BitmapConfig() {}


	public static BitmapConfig getInstace() {
		return mInstacne;
	}

	public Bitmap createCurBitmap(int screenWith, int screenHeight) {
		if (currentBitmap == null)
			currentBitmap = Bitmap.createBitmap(screenWith, screenHeight,
					Bitmap.Config.ARGB_8888);
		return currentBitmap;
	}
	public Bitmap createNextBitmap(int screenWith, int screenHeight) {
		if (nextBitmap == null)
			nextBitmap = Bitmap.createBitmap(screenWith, screenHeight,
					Bitmap.Config.ARGB_8888);
		return nextBitmap;
	}

	public void destory() {
		if (currentBitmap != null && !currentBitmap.isRecycled()) {
			currentBitmap.isRecycled();
			currentBitmap = null;
		}
		if (nextBitmap != null && !nextBitmap.isRecycled()) {
			nextBitmap.isRecycled();
			nextBitmap = null;
		}
	}
}
