package com.example.rssreader.articlelist;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.rssreader.IMyAidlInterface;
import com.example.rssreader.R;
import com.example.rssreader.model.datamodel.Channel;
import com.example.rssreader.model.parse.AidlBinder;
import com.example.rssreader.model.parse.DataService;
import com.example.rssreader.util.ActivityUtil;

/**
 * The type Article list activity.
 */
public class ArticleListActivity extends AppCompatActivity {

    private ArticleListPresenter mArticleListPresenter;



    /**
     * 数据服务的引用
     */
    private IMyAidlInterface myAidlInterface;

    String TAG = "ArticleListActivity";






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.article_list_act);

        Channel channel = (Channel)getIntent().getParcelableExtra("channel");

        Toolbar toolbar = (Toolbar)findViewById(R.id.main_toolbar);

        TextView textView = (TextView)findViewById(R.id.article_list_title);
        textView.setText(channel.getTitle());
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
        mArticleListPresenter = new ArticleListPresenter(AidlBinder.getInstance(), articleListFragment, channel);

    }


}
