package com.example.ArticleList.articlelist;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ArticleList.Data.ShuhaoJiekou;
import com.example.ArticleList.R;
import com.example.ArticleList.util.ActivityUtils;

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
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), articleListFragment, R.id.artlist_frag);
        }

        // Create the presenter
        mArticleListPresenter = new ArticleListPresenter(shuhaoJiekou, articleListFragment);

    }

}
