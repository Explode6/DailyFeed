package com.example.rssreader.articleCollection;


import com.example.rssreader.model.datamodel.ArticleBrief;
import com.example.rssreader.util.BasePresenter;
import com.example.rssreader.util.BaseView;

import java.util.List;

/**
 * @ClassName ArticleCollectionContract
 * @Author HaoHaoGe
 * @Date 2021/4/30
 * @Description
 */
public interface ArticleCollectionContract {
    interface View extends BaseView<ArticleCollectionPresenter> {

        void showArticleCollection(List<ArticleBrief> articleBriefList, int begin, int size);

        void changeFooterViewStyle();

        void showArticleDetails(ArticleBrief articleBrief);

        void markReadAndRefresh(int position);

        void switchCollectionAndRefresh(int position);

        void giveWrongMessage(String str);

        void giveLoadSuccessfulMessage();
    }
    interface ArticleCollectionPresenter extends BasePresenter {
        void loadArticle();

        void openArticleDetails(ArticleBrief articleBrief, int pos);

        void markRead(int pos);

        void switchCollection(ArticleBrief articleBrief, int pos, boolean isCollect);

        boolean getArticleBriefData();
    }
}
