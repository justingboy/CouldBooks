package com.himoo.ydsc.reader.view;

import java.util.Vector;

import android.graphics.Canvas;

/**
 * 暂不知其用途
 *
 */
public class Path extends android.graphics.Path {
	public Vector<float[]> points = new Vector<float[]>();

	public void draw(Canvas paramCanvas, int paramInt) {
	}

	public void lineTo(float paramFloat1, float paramFloat2) {
		super.lineTo(paramFloat1, paramFloat2);
	}

	public void moveTo(float paramFloat1, float paramFloat2) {
		super.moveTo(paramFloat1, paramFloat2);
	}

	public String toString() {
		return super.toString();
	}
}