package com.example.ArticleList.articlelist;

import com.example.ArticleList.Data.Article;
import com.example.ArticleList.util.BasePresenter;
import com.example.ArticleList.util.BaseView;

import java.util.List;

/**
 * @ClassName ArticleListContract
 * @Author HaoHaoGe
 * @Date 2021/4/30
 * @Description
 */
public interface ArticleListContract {
    interface View extends BaseView<Presenter> {
        void showArticleList(List<Article> articleList);
    }
    interface Presenter extends BasePresenter {
    }
}
