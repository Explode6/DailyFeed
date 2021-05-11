package com.example.rssreader.rssSource;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class AlarmUtil {
    public static void startNoticeService(Context context, long seconds, Class<?> cls, String action) {
        //获取AlarmManager系统服务
        AlarmManager manager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        //初始化intent用来执行service
        Intent intent = new Intent(context, cls);
        intent.setAction(action);
        //利用intent初始化pendingIntent用于启动service
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,0,intent, PendingIntent.FLAG_UPDATE_CURRENT);
        //service的开始时间
        long triggerAtTime = seconds;
        //设置时钟
        manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtTime, pendingIntent);
    }

    public static void stopNoticeService(Context context, Class<?> cls, String action) {
        //获取AlarmManager系统服务
        AlarmManager manager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        //初始化intent用来执行service
        Intent intent = new Intent(context, cls);
        intent.setAction(action);
        intent.putExtra("mykey","更新已完成");
        //利用intent初始化pendingIntent用于启动service
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,0,intent, PendingIntent.FLAG_UPDATE_CURRENT);
        manager.cancel(pendingIntent);

    }
}
