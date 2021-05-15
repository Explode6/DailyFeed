package com.example.rssreader.rssSource;

import android.app.NotificationChannel;
import android.app.NotificationManager;
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
        initNotification(intent, "更新成功");
        showNotification();
        //关闭闹钟
        AlarmUtil.stopNoticeService(getApplicationContext(), ClockBroadcastReceiver.class,"com.example.rssreader.rssNoticeBroadcast");
        long oneDaySec = 24*60*60*1000;
        //一天之后重新通知
        AlarmUtil.startNoticeService(getApplicationContext(), System.currentTimeMillis()+oneDaySec, ClockBroadcastReceiver.class, "com.ryantang.service.PollingService");
        return super.onStartCommand(intent, flags, startId);
    }
    

    //初始化通知内容
    private void initNotification(Intent intent, String content){
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
                .setContentText(content);
    }

    private void showNotification(){
        notificationManager.notify(1, builder.build());
    }

}
