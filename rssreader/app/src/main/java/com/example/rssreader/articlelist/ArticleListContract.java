package com.example.rssreader.articlelist;

import com.example.rssreader.Data.ArticleBrief;
import com.example.rssreader.util.BasePresenter;
import com.example.rssreader.util.BaseView;

import java.util.List;

/**
 * @ClassName ArticleListContract
 * @Author HaoHaoGe
 * @Date 2021/4/30
 * @Description
 */
public interface ArticleListContract {
    interface View extends BaseView<Presenter> {
        void showArticleList(List<ArticleBrief> articleBriefList, int begin, int size);
        void refreshArticleList(List<ArticleBrief> articleBriefList);
    }
    interface Presenter extends BasePresenter {
        boolean loadArticle();
        boolean reLoadArticle();
        void reLoadFromNet();
    }
}
