package com.example.testapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;

import com.example.testapplication.datamodel.ArticleBrief;
import com.example.testapplication.datamodel.Channel;
import com.example.testapplication.datamodel.Collection;
import com.example.testapplication.datamodel.DataBaseHelper;
import com.example.testapplication.parse.DataCallback;
import com.example.testapplication.parse.DataService;
import com.example.testapplication.parse.HandleXmlService;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.litepal.LitePal;

/**
 * @ClassName： MainActivity
 * @Author SH
 * @Date： 2021/4/23
 * @Description： 主界面
 */
public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "com.example.testapplication.MESSAGE";

    private List<RvItem> itemList = new ArrayList<>();

    /**
     * 数据服务的引用
     */
    //private DataService dataService;
    public IMyAidlInterface myAidlInterface;

    String TAG = "PARSE XML" ;

    //使用dom4j解析xml
    public void XmlParse (String path) throws FileNotFoundException, UnsupportedEncodingException {
        //创建Reader对象
        SAXReader reader = new SAXReader();
        //加载xml
        InputStream is = this.openFileInput(path);
        InputStreamReader isreader = new InputStreamReader(is,"gbk");
        try {

            Document document = reader.read(isreader);
            Element channel = document.getRootElement().element("channel");
            //获取节点
            Iterator iterator = channel.elementIterator();

            while(iterator.hasNext()){
                Element channelAtr = (Element) iterator.next();
                List<Attribute> attributes = channelAtr.attributes();
                Log.d(TAG,"节点名"+channelAtr.getName());
                Log.d(TAG,"====解析属性===");
                for (Attribute attribute : attributes){
                    Log.d(TAG,attribute.getName()+":"+attribute.getValue());
                }
                Log.d(TAG,"节点text:"+channelAtr.getText());
                Log.d(TAG,"遍历子节点");
                Iterator itemIterator = channelAtr.elementIterator();
                while(itemIterator.hasNext()){
                    Element item = (Element)itemIterator.next();
                    Log.d(TAG,item.getStringValue());
                }

            }

        } catch (DocumentException e) {
            e.printStackTrace();
        }


    }

    /**
     * 完善ServiceConnection接口
     */
    ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, final IBinder service) {
            myAidlInterface = IMyAidlInterface.Stub.asInterface(service);
            try {
                myAidlInterface.registerCallback(new DataCallback.Stub() {
                    @Override
                    public void onSuccess() throws RemoteException {
                        Log.d(TAG,"Success");
                    }

                    @Override
                    public void onFailure() throws RemoteException {
                        Log.d(TAG,"Failure");
                    }

                    @Override
                    public void onError() throws RemoteException {

                    }
                });
            } catch (RemoteException e) {
                e.printStackTrace();
            }
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

        initItemList();
        RecyclerView RV = (RecyclerView)findViewById(R.id.RecyclerView);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        //LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        RV.setLayoutManager(layoutManager);
        MyRVAdapter adapter = new MyRVAdapter(itemList);
        RV.setAdapter(adapter);
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
            //myAidlInterface.downloadParseXml("https://tobiasahlin.com/feed.xml");
            //myAidlInterface.downloadParseXml("https://journeybunnies.com/feed/");
            List<Channel> channels =  myAidlInterface.getChannel(0,10);
            for(Channel channel : channels){
                Log.d(TAG,channel.getTitle());
            }
            List<ArticleBrief> articleBriefs = myAidlInterface.getArticleBriefsFromChannel(channels.get(0),0,10);
            myAidlInterface.collectArticle(articleBriefs.get(2));
            List<Collection> collections =  myAidlInterface.getCollection(0,10);
            for(Collection collection:collections){
                Log.d(TAG,collection.getTitle());
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
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
     * 处理XMl 打开处理Xml的服务
     * @param view
     */
    public void startHandleXml(View view){
        Intent startIntent = new Intent(this, HandleXmlService.class);
        startService(startIntent);
    }

    /**
     * 渲染列表
     */
    private void initItemList(){
        for(int i = 0;i<20;i++) {
            RvItem newItem = new RvItem(randomString());
            itemList.add(newItem);
        }
    }


    /**
     * 随机生成列表内容
     * @return
     */
    private String randomString(){
        Random random = new Random();
        int number = random.nextInt(20)+1;
        StringBuilder builder = new StringBuilder();
        for (int i =0;i<number;i++){
            builder.append(""+number);
        }
        return builder.toString();
    }

    /**
     * 打开新窗口输出内容
     * @param view
     */
    public void sendMessage(View view) {
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        startActivity(intent);
        //EditText editText = (EditText) findViewById(R.id.editText);
        //String message = editText.getText().toString();
//        try {
//            FileInputStream inputStream = this.openFileInput("Cache_test.xml");
//            ByteArrayOutputStream stream = new ByteArrayOutputStream();
//            byte[] buffer = new byte[1024];
//            int length = -1;
//            inputStream.read(buffer);
//            stream.write(buffer,0,1024);
//            //while((length = inputStream.read(buffer))!=-1){
//            //    stream.write(buffer,0,length);
//            //}
//            stream.close();
//            inputStream.close();
//            message = stream.toString();
//
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        //intent.putExtra(EXTRA_MESSAGE,message);

    }

    /**
     * 打开webView
     * @param view
     */
    public void openWebView(View view){
        Intent intent = new Intent(this,WebViewActivity.class);
        startActivity(intent);
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
