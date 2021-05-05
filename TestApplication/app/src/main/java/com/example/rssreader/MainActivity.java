package com.example.rssreader;

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

import com.example.rssreader.model.datamodel.ArticleBrief;
import com.example.rssreader.model.datamodel.Channel;
import com.example.rssreader.model.datamodel.Collection;
import com.example.rssreader.model.datamodel.DataBaseHelper;
import com.example.rssreader.model.parse.DataCallback;
import com.example.rssreader.model.parse.DataService;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import org.litepal.LitePal;

/**
 * @ClassName： MainActivity
 * @Author SH
 * @Date： 2021/4/23
 * @Description： 主界面
 */
public class MainActivity extends AppCompatActivity {

    /**
     * 数据服务的引用
     */
    public IMyAidlInterface myAidlInterface;

    String TAG = "PARSE XML" ;

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
        setContentView(R.layout.activity_main);

        LitePal.initialize(this);
        LitePal.getDatabase();

        Intent intent = new Intent(this, DataService.class);
        bindService(intent,conn,Context.BIND_AUTO_CREATE);
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

    /**
     * 数据服务测试
     * @param view
     */
    public void startDataService(View view){
        try {
//            myAidlInterface.downloadParseXml("https://tobiasahlin.com/feed.xml", new DataCallback.Stub() {
//                @Override
//                public void onSuccess() throws RemoteException {
//
//                }
//
//                @Override
//                public void onFailure() throws RemoteException {
//
//                }
//
//                @Override
//                public void onError() throws RemoteException {
//
//                }
//            });
            //myAidlInterface.downloadParseXml("https://journeybunnies.com/feed/");
            List<Channel> channels =  myAidlInterface.getChannel(0,10);
            for(Channel channel : channels){
                Log.d(TAG,channel.getTitle());
            }
            List<ArticleBrief> articleBriefs = myAidlInterface.getArticleBriefsFromChannel(channels.get(0),0,10);
            //myAidlInterface.collectArticle(articleBriefs.get(2));
            List<Collection> collections =  myAidlInterface.getCollection(0,10);
            for(Collection collection:collections){
                Log.d(TAG,collection.getTitle());
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 测试两个activity能否同时绑定一个service
     * @param view
     */
    public void openNewActivity(View view){
        Intent intent = new Intent(this,MainActivity2.class);
        startActivity(intent);
    }


    /**
     * 删库跑路
     * @param view
     */
    public void deleteData(View view){
        DataBaseHelper dataBaseHelper = new DataBaseHelper();
        List<Channel> channels = dataBaseHelper.getChannel(0,10);
        for(Channel channel : channels){
            dataBaseHelper.removeChannel(channel);
        }
    }



    /**
     * 下载Xml （废弃）
     * @param path 下载Xml的地址
     * @throws IOException
     */
    public void downloadXml (String path) throws IOException {
        URL url = new URL(path);
        URLConnection conn = url.openConnection();
        InputStream is = conn.getInputStream();

        //截取文件格式
        String end = path.substring(path.lastIndexOf("."));
        //打开手机对应的输出流,输出到文件中
        //File file = new File("Cache_test"+end);
        //FileOutputStream os = new FileOutputStream(file);
        FileOutputStream os = this.openFileOutput("Cache_test.xml",Context.MODE_PRIVATE);
        byte[] buffer = new byte[1024];
        int len = 0;
        //从输入中读取数据,读到缓冲区中
        while((len = is.read(buffer)) > 0)
        {
            os.write(buffer,0,len);
        }
        //关闭输入输出流
        is.close();
        os.close();
    }
}
