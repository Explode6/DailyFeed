package com.example.rssreader.articleCollection;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.rssreader.IMyAidlInterface;
import com.example.rssreader.R;

import com.example.rssreader.model.datamodel.Channel;
import com.example.rssreader.model.parse.AidlBinder;
import com.example.rssreader.util.ActivityUtil;

/**
 * The type Article list activity.
 */
public class ArticleCollectionActivity extends AppCompatActivity {

    private ArticleCollectionPresenter mArticleCollectionPresenter;



    /**
     * 数据服务的引用
     */
    private IMyAidlInterface myAidlInterface;

    String TAG = "ArticleCollectionActivity";






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.article_collection_act);


        Toolbar toolbar = (Toolbar)findViewById(R.id.article_collection_toolbar);

        //为toolbar引入actionbar的功能
        setSupportActionBar(toolbar);


        //单例模式创建fragment界面并绑定到布局上
        ArticleCollectionFragment articleCollectionFragment =
                (ArticleCollectionFragment) getSupportFragmentManager().findFragmentById(R.id.artcollection_frag);
        if (articleCollectionFragment == null){
            articleCollectionFragment = ArticleCollectionFragment.newInstance();
            ActivityUtil.addFragmentToActivity(getSupportFragmentManager(), articleCollectionFragment, R.id.artcollection_frag);
        }

        // Create the presenter
        mArticleCollectionPresenter = new ArticleCollectionPresenter(AidlBinder.getInstance(), articleCollectionFragment);

    }


}
