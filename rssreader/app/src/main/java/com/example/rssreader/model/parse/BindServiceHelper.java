package com.example.rssreader.model.parse;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;

import com.example.rssreader.IMyAidlInterface;

/**
 * @ClassName： BindServiceHelper
 * @Author SH
 * @Date： 2021/5/7
 * @Description：
 */
public class BindServiceHelper {
    public static IMyAidlInterface aidlInterface = null;

    public static void bindService(ServiceConnection conn,Context context){
        Intent intent = new Intent(context.getApplicationContext(), DataService.class);
        context.bindService(intent,conn,Context.BIND_AUTO_CREATE);
    }
}
