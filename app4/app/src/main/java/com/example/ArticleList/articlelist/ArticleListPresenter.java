package com.example.ArticleList.articlelist;

import com.example.ArticleList.Data.Article;
import com.example.ArticleList.Data.ShuhaoJiekou;

import java.util.List;

/**
 * @ClassName ArticleListPresenter
 * @Author HaoHaoGe
 * @Date 2021/4/30
 * @Description
 */
public class ArticleListPresenter implements ArticleListContract.Presenter {

    private ShuhaoJiekou mshuHaoJiekou;

    private ArticleListContract.View mArticleListView;

    public ArticleListPresenter(ShuhaoJiekou shuhaoJiekou, ArticleListContract.View articleListView){
        mArticleListView = articleListView;
        mshuHaoJiekou = shuhaoJiekou;

        mArticleListView.setPresenter(this);
    }

    @Override
    public void start() {
        List<Article> articleList = ShuhaoJiekou.getRandomData();
        mArticleListView.showArticleList(articleList);
    }
}
