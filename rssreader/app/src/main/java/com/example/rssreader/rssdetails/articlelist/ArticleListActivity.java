package com.example.rssreader.rssdetails.articlelist;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.rssreader.IMyAidlInterface;
import com.example.rssreader.R;
import com.example.rssreader.model.datamodel.Channel;
import com.example.rssreader.model.parse.AidlBinder;
import com.example.rssreader.util.ActivityUtil;

/**
 * 当前界面是点击RSS源之后进入的，能够查看该RSS源所有文章的界面
 */
public class ArticleListActivity extends AppCompatActivity {

    private ArticleListPresenter mArticleListPresenter;

    /**
     * 数据服务的引用
     */
    String TAG = "ArticleListActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(savedInstanceState != null){
            finish();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.article_list_act);
        //从第一个界面获取当前是需要展示哪一个RSS源的文章
        Channel channel = (Channel)getIntent().getParcelableExtra("channel");

        Toolbar toolbar = (Toolbar)findViewById(R.id.main_toolbar);
        //为toolbar引入actionbar的功能
        setSupportActionBar(toolbar);

        //把标题栏设置为该RSS源名称
        TextView textView = (TextView)findViewById(R.id.title);
        textView.setText(channel.getTitle());


        //单例模式创建fragment界面并绑定到布局上，Fragment主要是文章的RecyclerView界面
        ArticleListFragment articleListFragment =
                (ArticleListFragment) getSupportFragmentManager().findFragmentById(R.id.showlist_frag);
        if (articleListFragment == null){
            articleListFragment = ArticleListFragment.newInstance();
            ActivityUtil.addFragmentToActivity(getSupportFragmentManager(), articleListFragment, R.id.showlist_frag);
        }

        // Create the presenter
        mArticleListPresenter = new ArticleListPresenter(AidlBinder.getInstance(), articleListFragment, channel);

    }

    @Override
    protected void onDestroy() {
        mArticleListPresenter.closeAct();
        super.onDestroy();
    }
}
