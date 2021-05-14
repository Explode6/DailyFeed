package com.example.rssreader.rssdetails;


import android.app.Activity;

import com.example.rssreader.model.datamodel.ArticleBrief;
import com.example.rssreader.util.BasePresenter;
import com.example.rssreader.util.BaseView;

import java.util.List;

/**
 * @ClassName ShowListContract
 * @Author HaoHaoGe
 * @Date 2021/5/13
 * @Description
 */
public interface ShowListContract {
    interface View extends BaseView<ShowListContract.ShowListPresenter> {

        //把从数据库读取的文章进行展示
        void showArticleList(List<ArticleBrief> articleBriefList, int begin, int size);

        //重新加载文章
        void refreshArticleList(List<ArticleBrief> articleBriefList);

        //点击文章项进入文章详情页的跳转函数
        void showArticleDetails(ArticleBrief articleBrief);

        //把文章标记已读
        void markReadAndRefresh(int position);

        //切换文章已读未读属性
        void switchReadAndRefresh(int position);

        //收藏/取消收藏
        void switchCollectionAndRefresh(int position);

        //弹出消息
        void giveNoteMessage(String str);

        //停止刷新时转动图标
        void stopRefreshUI();

        //数据展示完成时给出提示
        void changeFooterViewStyle();
    }
    interface ShowListPresenter extends BasePresenter {
        //从数据库读取文章列表
        void loadArticle();

        //将文章标记为已读并写进数据库，然后跳转到文章详情页
        void openArticleDetails(ArticleBrief articleBrief, int pos);

        //将文章已读属性写入数据库并刷新页面
        void markRead(ArticleBrief articleBrief, int pos);

        //已读/未读属性写入数据库并刷新页面
        void switchRead(ArticleBrief articleBrief, int pos);
    }
}
