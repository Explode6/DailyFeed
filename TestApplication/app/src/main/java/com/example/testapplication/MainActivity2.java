package com.example.testapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;

import com.example.testapplication.model.datamodel.Channel;
import com.example.testapplication.model.parse.DataCallback;
import com.example.testapplication.model.parse.DataService;

import java.util.List;

public class MainActivity2 extends AppCompatActivity {

    /**
     * 数据服务的引用
     */
    public IMyAidlInterface myAidlInterface;

    String TAG = "MainActivity2";

    /**
     * 完善ServiceConnection接口
     */
    ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, final IBinder service) {
            myAidlInterface = IMyAidlInterface.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) { }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        Intent intent = new Intent(this, DataService.class);
        bindService(intent,conn, Context.BIND_AUTO_CREATE);

    }


    public void bind(View view){
        List<Channel> channels = null;
        try {
            channels = myAidlInterface.getChannel(0,10);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        for(Channel channel : channels){
            Log.d(TAG,channel.getTitle());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 释放服务
        if(conn != null){
            unbindService(conn);
            conn = null;
        }
    }

}