package com.example.testapplication.parse;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class HandleXmlService extends Service {
    public HandleXmlService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        new Thread(new Runnable() {

            @Override
            public void run() {
                //new XmlHandler("https://www.zhihu.com/rss").startParse();
                //new XmlHandler("http://news.163.com/special/00011K6L/rss_newsattitude.xml").startParse();
                //new XmlHandler("https://tobiasahlin.com/feed.xml").startParse();
                new XmlHandler("https://journeybunnies.com/feed/").startParse();
                //new XmlHandler("https://www.wangyurui.top/index.php/feed/").startParse();
            }
        }).start();
//        new Thread(new Runnable() {
//
//            @Override
//            public void run() {
//                //new XmlHandler("https://www.zhihu.com/rss").startParse();
//                //new XmlHandler("http://news.163.com/special/00011K6L/rss_newsattitude.xml").startParse();
//                //new XmlHandler("https://tobiasahlin.com/feed.xml").startParse();
//                //new XmlHandler("https://journeybunnies.com/feed/").startParse();
//                //new XmlHandler("https://www.wangyurui.top/index.php/feed/").startParse();
//            }
//        }).start();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }



    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
