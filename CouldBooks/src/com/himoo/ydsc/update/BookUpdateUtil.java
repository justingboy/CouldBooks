package com.himoo.ydsc.update;

import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.himoo.ydsc.config.SpConstant;
import com.himoo.ydsc.util.MyLogger;
import com.himoo.ydsc.util.SharedPreferences;

public class BookUpdateUtil {

	/**
	 * 判断Service 是否在运行状态
	 * @param context
	 * @param className
	 * @return
	 */
    public static boolean isServiceRunning(Context context, String className) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceInfos = activityManager.getRunningServices(Constants.RETRIVE_SERVICE_COUNT);

        if(null == serviceInfos || serviceInfos.size() < 1) {
            return false;
        }
        for(int i = 0; i < serviceInfos.size(); i++) {
            if(serviceInfos.get(i).service.getClassName().contains(className)) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }
    
    /**
     * 开启定时任务
     * @param context
     */
    public static void startTimerService(Context context){
        PendingIntent alarmSender = null;
        Intent startIntent = new Intent(context, BookUpdateService.class);
        startIntent.setAction(Constants.BOOKUPDATE_SERVICE_ACTION);
        try {
            alarmSender = PendingIntent.getService(context, 0, startIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        } catch (Exception e) {
           MyLogger.kLog().e(e);
        }
       int timeSpace = SharedPreferences.getInstance().getInt(SpConstant.BOOK_UPATE_SETTING_SPACE, 1);
        AlarmManager am = (AlarmManager) context.getSystemService(Activity.ALARM_SERVICE);
        am.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), timeSpace*Constants.ELAPSED_TIME, alarmSender);
    }

    /**
     * 取消定时任务
     * @param context
     */
    public static void cancleAlarmManager(Context context){
        Intent intent = new Intent(context,BookUpdateService.class);
    	intent.setAction(Constants.BOOKUPDATE_SERVICE_ACTION);
        PendingIntent pendingIntent=PendingIntent.getService(context, 0, intent,PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarm=(AlarmManager)context.getSystemService(Activity.ALARM_SERVICE);
        alarm.cancel(pendingIntent);
    }
}
