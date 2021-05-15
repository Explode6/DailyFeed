package com.example.rssreader.rssSource;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class ClockBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //启动闹钟服务，执行更新和发送消息的动作
        Intent i  = new Intent(context, NoticeService.class);
        context.startService(i);
    }
}
