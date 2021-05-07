package com.example.rssreader.articlelist;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.rssreader.Data.ShuhaoJiekou;
import com.example.rssreader.IMyAidlInterface;
import com.example.rssreader.R;
import com.example.rssreader.model.parse.DataService;
import com.example.rssreader.util.ActivityUtil;

/**
 * The type Article list activity.
 */
public class ArticleListActivity extends AppCompatActivity {

    private ArticleListPresenter mArticleListPresenter;
    private ShuhaoJiekou shuhaoJiekou;



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
        setContentView(R.layout.article_list_act);

        Toolbar toolbar = (Toolbar)findViewById(R.id.main_toolbar);
        //为toolbar引入actionbar的功能
        setSupportActionBar(toolbar);


        //单例模式创建fragment界面并绑定到布局上
        ArticleListFragment articleListFragment =
                (ArticleListFragment) getSupportFragmentManager().findFragmentById(R.id.artlist_frag);
        if (articleListFragment == null){
            articleListFragment = ArticleListFragment.newInstance();
            ActivityUtil.addFragmentToActivity(getSupportFragmentManager(), articleListFragment, R.id.artlist_frag);
        }

        // Create the presenter
        mArticleListPresenter = new ArticleListPresenter(shuhaoJiekou, articleListFragment);




        Intent intent = new Intent(this, DataService.class);
        bindService(intent,conn, Context.BIND_AUTO_CREATE);
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
