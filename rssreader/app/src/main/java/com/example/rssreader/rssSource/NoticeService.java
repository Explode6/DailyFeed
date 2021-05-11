package com.example.rssreader.rssSource;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.rssreader.R;

public class NoticeService extends Service {

    private NotificationCompat.Builder builder;  //通知栏
    private NotificationManager notificationManager;    //通知栏管理器

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(this, "service创建成功",Toast.LENGTH_SHORT).show();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initNotification(intent);
        showNotification();
        AlarmUtil.stopNoticeService(getApplicationContext(), ClockBroadcastReceiver.class,"com.example.rssreader.rssNoticeBroadcast");
        //AlarmUtil.startNoticeService(getApplicationContext(), System.currentTimeMillis()+10*1000, NoticeService.class, "com.ryantang.service.PollingService");
        Intent intent1 = new Intent(this, NoticeService.class);
        stopService(intent1);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "service停止运行",Toast.LENGTH_SHORT).show();
    }

    //初始化通知内容
    private void initNotification(Intent intent){
        notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel("2222"
                    , "name", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }
        builder= new NotificationCompat.Builder(this,"2222")
                .setSmallIcon(R.drawable.add_icon)
                .setContentTitle("通知")
                .setWhen(System.currentTimeMillis())
                .setContentText(intent.getStringExtra("mykey"));
    }

    private void showNotification(){
        notificationManager.notify(1, builder.build());
    }

}
