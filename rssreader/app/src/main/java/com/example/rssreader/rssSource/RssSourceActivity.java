package com.example.rssreader.rssSource;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

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

public class RssSourceActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout; //侧滑菜单
    private NavigationView navView; //侧滑菜单的导航栏
    private RssSourcePresenterImpl rssSourcePresenter;
    private RssSourceFragment rssSourceFragment;
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
            Drawable navBtnImg = ContextCompat.getDrawable(this,R.drawable.menu);
            //显示导航按钮图片
            actionBar.setHomeAsUpIndicator(navBtnImg);
        }
        //获取侧滑菜单的导航栏
        navView = (NavigationView)findViewById(R.id.nav_view);

       //新建Fragment
        rssSourceFragment = (RssSourceFragment)getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if(rssSourceFragment == null){
            rssSourceFragment = RssSourceFragment.newInstance();
            ActivityUtil.addFragmentToActivity(getSupportFragmentManager(),rssSourceFragment,R.id.contentFrame);
        }
       //初始化presenter
        rssSourcePresenter = new RssSourcePresenterImpl(rssSourceFragment, new RssSourceModel());
        //设置侧滑菜单导航栏按钮点击事件
        if(navView != null){
            rssSourceFragment.setNavClickListener(navView);
        }
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
                break;
            default:
                break;
        }
        return true;
    }

    public void closeNavView(){
        mDrawerLayout.closeDrawer(GravityCompat.START);
    }
}


