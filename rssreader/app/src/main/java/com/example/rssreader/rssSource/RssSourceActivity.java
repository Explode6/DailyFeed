package com.example.rssreader.rssSource;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.app.SkinAppCompatDelegateImpl;
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
import com.example.rssreader.model.parse.DataCallback;
import com.example.rssreader.util.ActivityUtil;
import com.example.rssreader.util.AlarmUtil;
import com.example.rssreader.util.ApplicationUtil;
import com.example.rssreader.util.ConfigUtil;
import com.google.android.material.navigation.NavigationView;

import org.jetbrains.annotations.NotNull;
import org.litepal.LitePal;

import java.io.ByteArrayInputStream;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import skin.support.SkinCompatManager;

public class RssSourceActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout; //侧滑菜单
    private NavigationView navView; //侧滑菜单的导航栏
    private RssSourceFragment rssSourceFragment;
    private RssSourcePresenterImpl rssSourcePresenter;
    private ConfigUtil configUtil;
    private Toolbar toolbar;
    private ActionBar actionBar;
    public IMyAidlInterface myAidlInterface;

    @NonNull
    @NotNull
    @Override
    public AppCompatDelegate getDelegate() {
        return SkinAppCompatDelegateImpl.get(this, this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //判断进程是否为系统回收，如果被系统回收就新建主活动再将当前活动销毁
        if(savedInstanceState != null){
            Intent intent = new Intent(this, RssSourceActivity.class);
            startActivity(intent);
            finish();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rss_source);
        toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        //为toolbar引入actionbar的功能
        setSupportActionBar(toolbar);
        //获取侧滑菜单
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        //获取标题栏
        actionBar = getSupportActionBar();
        //设置导航按钮
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        //获取侧滑菜单的导航栏
        navView = (NavigationView) findViewById(R.id.nav_view);


        //初始化sharedPreferences
        configUtil = ConfigUtil.getInstance(getApplicationContext());
        //判断用户设置为夜间/日间模式进行不同的初始化
        if(ApplicationUtil.getIsFirstLoad() == true){
            if(configUtil.isDarkMode() == true) {
                //切换为夜间模式
                SkinCompatManager.getInstance().loadSkin("night", SkinCompatManager.SKIN_LOADER_STRATEGY_BUILD_IN);
            }
            else {
                //切换为日间模式
                SkinCompatManager.getInstance().restoreDefaultTheme();
            }
            ApplicationUtil.setIsFirstLoad(false) ;
        }

        //如果后台连接还在，但是因为摁下返回键fragment被销毁，这时要重新初始化fragment和presenter
        if(AidlBinder.getInstance() != null){
            rssSourceFragment = (RssSourceFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
            if (rssSourceFragment == null) {
                rssSourceFragment = RssSourceFragment.newInstance();
                ActivityUtil.addFragmentToActivity(getSupportFragmentManager(), rssSourceFragment, R.id.contentFrame);
            }
            //初始化presenter
            rssSourcePresenter = new RssSourcePresenterImpl(rssSourceFragment, AidlBinder.getInstance());
            //设置侧滑菜单导航栏按钮点击事件
            if (navView != null) {
                rssSourceFragment.setNavClickListener(navView);
            }
        }


        //绑定后台服务
        /*
         * 以下四行代码是创建model有关
         * 从澍豪的代码拔过来的
         */
        ////启动服务，并且将获得的Binder实例放在AidlBinder的静态变量中
        AidlBinder.bindService(new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                AidlBinder.setInstance(IMyAidlInterface.Stub.asInterface(service));
                //新建Fragment
                rssSourceFragment = (RssSourceFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
                if (rssSourceFragment == null) {
                    rssSourceFragment = RssSourceFragment.newInstance();
                    ActivityUtil.addFragmentToActivity(getSupportFragmentManager(), rssSourceFragment, R.id.contentFrame);
                }
                //初始化presenter
                rssSourcePresenter = new RssSourcePresenterImpl(rssSourceFragment, AidlBinder.getInstance());
                //设置侧滑菜单导航栏按钮点击事件
                if (navView != null) {
                    rssSourceFragment.setNavClickListener(navView);
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        }, getApplicationContext());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        if(menu!= null){
            MenuItem menuItem = menu.getItem(0);
            if(configUtil.isDarkMode() == false)
                menuItem.setIcon(ContextCompat.getDrawable(this, R.drawable.add_icon));
            else
                menuItem.setIcon(ContextCompat.getDrawable(this, R.drawable.add_icon_night));
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.addButton:
                //startDataService();
                rssSourceFragment.showAddRssSrcDialog();
                break;
            default:
                break;
        }
        return true;
    }

    //转换为夜间模式时切换部分显示内容
    public void convertToNightMode(){
        //切换导航按钮图片
        actionBar.setHomeAsUpIndicator(ContextCompat.getDrawable(this, R.drawable.menu_night));
        //切换菜单项图片
        Menu menu = toolbar.getMenu();
        if(menu.size()!=0){
            MenuItem menuItem = menu.getItem(0);
            menuItem.setIcon(ContextCompat.getDrawable(this, R.drawable.add_icon_night));
        }
        //切换侧滑菜单图片
        Menu subMenu = navView.getMenu().getItem(1).getSubMenu();
        MenuItem modeConvert = subMenu.getItem(0);
        modeConvert.setIcon(ContextCompat.getDrawable(this, R.drawable.nav_day));
        modeConvert.setTitle("日间模式");
        ApplicationUtil.setStatusBarMode(getWindow(),getResources(),true);
    }

    //转换为日间模式时切换部分显示内容
    public void convertToDayMode(){
        //切换导航按钮图片
        actionBar.setHomeAsUpIndicator(ContextCompat.getDrawable(this, R.drawable.menu));
        //切换菜单项图片
        Menu menu = toolbar.getMenu();
        if(menu.size() != 0){
            MenuItem menuItem = menu.getItem(0);
            menuItem.setIcon(ContextCompat.getDrawable(this, R.drawable.add_icon));
        }
        //切换侧滑菜单图片
        Menu subMenu = navView.getMenu().getItem(1).getSubMenu();
        MenuItem modeConvert = subMenu.getItem(0);
        modeConvert.setIcon(ContextCompat.getDrawable(this, R.drawable.nav_mode));
        modeConvert.setTitle("夜间模式");
        ApplicationUtil.setStatusBarMode(getWindow(),getResources(),false);
    }

    public void closeNavView() {
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
     */
//    public void startDataService() {
//        //这是一个使用共享内存的demo
//        try {
//            byte[] content = new byte[1024*1024];
//            myAidlInterface = AidlBinder.getInstance();
//            ParcelFileDescriptor parcelFileDescriptor = myAidlInterface.getChannel2(0,10);
//            FileDescriptor descriptor = parcelFileDescriptor.getFileDescriptor();
//            FileInputStream fileInputStream = new FileInputStream(descriptor);
//            fileInputStream.read(content);
//            //将内存中的byte数组数据解码为对象（这里的channel需要支持序列化）
//            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(content);
//            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
//            List<Channel> channels = (List<Channel>) objectInputStream.readObject();
//            for(Channel channel:channels){
//                Log.d(TAG,channel.getTitle());
//            }
//        } catch (RemoteException | IOException | ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//    }
}


