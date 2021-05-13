package com.example.rssreader.articlelist;


import android.app.Activity;

import com.example.rssreader.model.datamodel.ArticleBrief;
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
    interface View extends BaseView<ArticleListPresenter> {

        void showArticleList(List<ArticleBrief> articleBriefList, int begin, int size);

        void refreshArticleList(List<ArticleBrief> articleBriefList);

        void showArticleDetails(ArticleBrief articleBrief);

        void markReadAndRefresh(int position);

        void switchReadAndRefresh(int position);

        void switchCollectionAndRefresh(int position);

        void giveWrongMessage(String str);

        //停止刷新时转动图标
        void stopRefreshUI();
    }
    interface ArticleListPresenter extends BasePresenter {
        boolean loadArticle();

        boolean reLoadArticle();

        void openArticleDetails(ArticleBrief articleBrief, int pos);

        void markRead(ArticleBrief articleBrief, int pos);

        void switchRead(ArticleBrief articleBrief, int pos);

        void switchCollection(ArticleBrief articleBrief, int pos);

        void refreshChannel(Activity activity);
    }
}
