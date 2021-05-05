package com.example.rssreader.articlelist;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.rssreader.Data.ShuhaoJiekou;
import com.example.rssreader.R;
import com.example.rssreader.util.ActivityUtil;

/**
 * The type Article list activity.
 */
public class ArticleListActivity extends AppCompatActivity {

    private ArticleListPresenter mArticleListPresenter;
    private ShuhaoJiekou shuhaoJiekou;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.article_list_act);

        //单例模式创建fragment界面并绑定到布局上
        ArticleListFragment articleListFragment =
                (ArticleListFragment) getSupportFragmentManager().findFragmentById(R.id.artlist_frag);
        if (articleListFragment == null){
            articleListFragment = ArticleListFragment.newInstance();
            ActivityUtil.addFragmentToActivity(getSupportFragmentManager(), articleListFragment, R.id.artlist_frag);
        }

        // Create the presenter
        mArticleListPresenter = new ArticleListPresenter(shuhaoJiekou, articleListFragment);

    }

}
