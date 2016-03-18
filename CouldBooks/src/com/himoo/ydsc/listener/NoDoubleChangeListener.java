package com.himoo.ydsc.listener;

import java.util.Calendar;

import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public abstract class NoDoubleChangeListener implements OnCheckedChangeListener {

	public static final int MIN_CLICK_DELAY_TIME = 200;
	public long lastClickTime = 0;
	
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		// TODO Auto-generated method stub
		long currentTime = Calendar.getInstance().getTimeInMillis();
		if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
			lastClickTime = currentTime;
			onNoDoubleCheckedChanged( group, checkedId);
		}
	}


	public abstract void  onNoDoubleCheckedChanged(RadioGroup group, int checkedId);

}
