package com.example.rssreader.rssSource;

<<<<<<< HEAD

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

=======
>>>>>>> HaoHaoGe
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

<<<<<<< HEAD

import com.example.rssreader.IMyAidlInterface;
import com.example.rssreader.R;
import com.example.rssreader.model.datamodel.ArticleBrief;
import com.example.rssreader.model.datamodel.Channel;
import com.example.rssreader.model.datamodel.Collection;
import com.example.rssreader.model.datamodel.DataBaseHelper;
import com.example.rssreader.model.parse.DataCallback;
import com.example.rssreader.model.parse.DataService;
import com.example.rssreader.util.ActivityUtil;
import com.google.android.material.navigation.NavigationView;

import org.litepal.LitePal;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

=======
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.rssreader.R;
import com.example.rssreader.util.ActivityUtil;
import com.google.android.material.navigation.NavigationView;

>>>>>>> HaoHaoGe
public class RssSourceActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout; //侧滑菜单
    private NavigationView navView; //侧滑菜单的导航栏
    private RssSourcePresenterImpl rssSourcePresenter;
<<<<<<< HEAD


    String TAG = "RssSourceActivity";
    /*
     * 以下代码是和model有关，从澍豪代码迁移过来
     */
    public IMyAidlInterface myAidlInterface;
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


=======
    private RssSourceFragment rssSourceFragment;
>>>>>>> HaoHaoGe
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rss_source);
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
<<<<<<< HEAD
            Drawable navBtnImg = ContextCompat.getDrawable(this, R.drawable.menu);
=======
            Drawable navBtnImg = ContextCompat.getDrawable(this,R.drawable.menu);
>>>>>>> HaoHaoGe
            //显示导航按钮图片
            actionBar.setHomeAsUpIndicator(navBtnImg);
        }
        //获取侧滑菜单的导航栏
        navView = (NavigationView)findViewById(R.id.nav_view);

       //新建Fragment
<<<<<<< HEAD
        RssSourceFragment rssSourceFragment = (RssSourceFragment)getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if(rssSourceFragment == null){
            rssSourceFragment = RssSourceFragment.newInstance();
            ActivityUtil.addFragmentToActivity(getSupportFragmentManager(),rssSourceFragment, R.id.contentFrame);
=======
        rssSourceFragment = (RssSourceFragment)getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if(rssSourceFragment == null){
            rssSourceFragment = RssSourceFragment.newInstance();
            ActivityUtil.addFragmentToActivity(getSupportFragmentManager(),rssSourceFragment,R.id.contentFrame);
>>>>>>> HaoHaoGe
        }
       //初始化presenter
        rssSourcePresenter = new RssSourcePresenterImpl(rssSourceFragment, new RssSourceModel());
        //设置侧滑菜单导航栏按钮点击事件
        if(navView != null){
            rssSourceFragment.setNavClickListener(navView);
        }
<<<<<<< HEAD


        /*
         * 以下四行代码是创建model有关
         * 从澍豪的代码拔过来的
         */
        LitePal.initialize(this);
        LitePal.getDatabase();

        Intent intent = new Intent(this, DataService.class);
        bindService(intent,conn, Context.BIND_AUTO_CREATE);
=======
>>>>>>> HaoHaoGe
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
<<<<<<< HEAD
                startDataService();
=======
                rssSourceFragment.showAddRssSrcDialog();
>>>>>>> HaoHaoGe
                break;
            default:
                break;
        }
        return true;
    }

    public void closeNavView(){
        mDrawerLayout.closeDrawer(GravityCompat.START);
    }
<<<<<<< HEAD




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
     * 以下所有代码都是跟数据有关的代码，暂时放在这里
     *
     */

    /**
     * 数据服务测试
     *
     */
    public void startDataService(){
        try {
            /*
             * 世伟看这里
             * 这是第一步，添加rss源之后先调用这个函数解析这个url，然后写进数据库
             * 这时候还没有刷新recyclerView，所以你需要重新用下面部分的函数刷新recyclerView
             */
            myAidlInterface.downloadParseXml("https://tobiasahlin.com/feed.xml", new DataCallback.Stub() {
                @Override
                public void onSuccess() throws RemoteException {

                }

                @Override
                public void onFailure() throws RemoteException {

                }

                @Override
                public void onError() throws RemoteException {

                }
            });
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
=======
}


>>>>>>> HaoHaoGe