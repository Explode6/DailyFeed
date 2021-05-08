package com.example.rssreader.rssSource;


import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;


import com.example.rssreader.IMyAidlInterface;
import com.example.rssreader.R;
import com.example.rssreader.model.datamodel.ArticleBrief;
import com.example.rssreader.model.datamodel.Channel;
import com.example.rssreader.model.datamodel.DataBaseHelper;
import com.example.rssreader.model.parse.AidlBinder;
import com.example.rssreader.util.ActivityUtil;
import com.google.android.material.navigation.NavigationView;

import org.litepal.LitePal;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

public class RssSourceActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout; //侧滑菜单
    private NavigationView navView; //侧滑菜单的导航栏
    RssSourceFragment rssSourceFragment;
    private RssSourcePresenterImpl rssSourcePresenter;


    String TAG = "RssSourceActivity";
    /*
     * 以下代码是和model有关，从澍豪代码迁移过来
     */
    public IMyAidlInterface myAidlInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rss_source);
        //获取数据库
        LitePal.initialize(this);
        LitePal.getDatabase();
        Toolbar toolbar = (Toolbar)findViewById(R.id.main_toolbar);
        //为toolbar引入actionbar的功能
        setSupportActionBar(toolbar);
        //获取侧滑菜单
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        //获取标题栏
        ActionBar actionBar = getSupportActionBar();
        //设置导航按钮
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            //获取导航按钮图片
            Drawable navBtnImg = ContextCompat.getDrawable(this, R.drawable.menu);
            //显示导航按钮图片
            actionBar.setHomeAsUpIndicator(navBtnImg);
        }
        //获取侧滑菜单的导航栏
        navView = (NavigationView)findViewById(R.id.nav_view);

       //新建Fragment
        rssSourceFragment = (RssSourceFragment)getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if(rssSourceFragment == null){
            rssSourceFragment = RssSourceFragment.newInstance();
            ActivityUtil.addFragmentToActivity(getSupportFragmentManager(),rssSourceFragment, R.id.contentFrame);
        }
       //初始化presenter
        rssSourcePresenter = new RssSourcePresenterImpl(rssSourceFragment, new RssSourceModel());
        //设置侧滑菜单导航栏按钮点击事件
        if(navView != null){
            rssSourceFragment.setNavClickListener(navView);
        }


        /*
         * 以下四行代码是创建model有关
         * 从澍豪的代码拔过来的
         */
        //Intent intent = new Intent(this, DataService.class);
        //bindService(intent,conn, Context.BIND_AUTO_CREATE);
        ////启动服务，并且将获得的Binder实例放在AidlBinder的静态变量中
        AidlBinder.bindService(new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                AidlBinder.setInstance(IMyAidlInterface.Stub.asInterface(service));
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        }, getApplicationContext());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.addButton:
                rssSourceFragment.showAddRssSrcDialog();
                //startDataService();
                break;
            default:
                break;
        }
        return true;
    }

    public void closeNavView(){
        mDrawerLayout.closeDrawer(GravityCompat.START);
    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 以下所有代码都是跟数据有关的代码，暂时放在这里
     *
     */

    /**
     * 数据服务测试
     *
     */
    public void startDataService(){
        try {
            myAidlInterface =  AidlBinder.getInstance();
            /*
             * 世伟看这里
             * 这是第一步，添加rss源之后先调用这个函数解析这个url，然后写进数据库
             * 这时候还没有刷新recyclerView，所以你需要重新用下面部分的函数刷新recyclerView
             */
//            myAidlInterface.aidlInterface.downloadParseXml("https://tobiasahlin.com/feed.xml", new DataCallback.Stub() {
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

            /*
             * 如果数据已经写进数据库了才能调用这个函数
             * 这是第二步
             * 这里的函数从数据库取数据然后再拿去刷新recyclerView
             */
            List<Channel> channels =  myAidlInterface.getChannel(0,10);
            for(Channel channel : channels){
                Log.d(TAG,channel.getTitle());
            }
            List<ArticleBrief> articleBriefs = myAidlInterface.getArticleBriefsFromChannel(channels.get(0),0,10);
            //myAidlInterface.collectArticle(articleBriefs.get(2));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }




    /**
     * 删库跑路
     * （HaoHaoGe）我觉得是没用的
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
        FileOutputStream os = this.openFileOutput("Cache_test.xml", Context.MODE_PRIVATE);
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
