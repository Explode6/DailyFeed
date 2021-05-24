package com.example.rssreader.rssSource;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.rssreader.IMyAidlInterface;
import com.example.rssreader.R;
import com.example.rssreader.model.parse.AidlBinder;
import com.example.rssreader.util.AlarmUtil;

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
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //绑定|启动后台服务
        if(AidlBinder.getInstance()==null){
            AidlBinder.bindService(new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    AidlBinder.setInstance(IMyAidlInterface.Stub.asInterface(service));
                    IMyAidlInterface model =  AidlBinder.getInstance();
                    try {
                        model.updateSource(0,100);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                        Log.d("NoticeService","update fail");
                    }
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {

                }
            },getApplicationContext());
        }else{ //后台服务存在
            IMyAidlInterface model =  AidlBinder.getInstance();
            try {
                model.updateSource(0,100);
            } catch (RemoteException e) {
                e.printStackTrace();
                Log.d("NoticeService","update fail");
            }
        }
        initNotification("更新成功");
        showNotification();
        //关闭闹钟
        AlarmUtil.stopNoticeService(getApplicationContext(), ClockBroadcastReceiver.class,"com.example.rssreader.rssNoticeBroadcast");
        long oneDaySec = 24*60*60*1000;
        //一天之后重新通知
        AlarmUtil.startNoticeService(getApplicationContext(), System.currentTimeMillis()+oneDaySec, ClockBroadcastReceiver.class, "com.example.rssreader.rssNoticeBroadcast");
        return super.onStartCommand(intent, flags, startId);
    }


    /**
     * 初始化通知
     * @param content   通知内容
     */
    private void initNotification(String content){
        Intent activityIntent = new Intent(this,RssSourceActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,activityIntent,0);
        notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        //判断Android版本来进行不同的初始化,Android8.0开始要指定渠道
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            //设置渠道号和渠道名
            String channelId = "dailyFeedId";
            String channelName = "定时更新通知";
            NotificationChannel channel = new NotificationChannel(channelId
                    , channelName, NotificationManager.IMPORTANCE_DEFAULT);
            //创建渠道
            notificationManager.createNotificationChannel(channel);
            builder= new NotificationCompat.Builder(this,channelId);
        }else{
            builder= new NotificationCompat.Builder(this);
        }
        //设置通知内容
        builder.setSmallIcon(R.drawable.add_icon)
                .setContentTitle("通知")
                .setWhen(System.currentTimeMillis())
                .setContentText(content)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
    }

    private void showNotification(){
        //显示通知
        notificationManager.notify(1, builder.build());
    }

}
