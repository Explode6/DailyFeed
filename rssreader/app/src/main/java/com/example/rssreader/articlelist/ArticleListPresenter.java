package com.example.rssreader.articlelist;


import android.os.RemoteException;

import com.example.rssreader.IMyAidlInterface;
import com.example.rssreader.model.datamodel.ArticleBrief;
import com.example.rssreader.model.datamodel.Channel;

import java.util.List;

/**
 * @ClassName ArticleListPresenter
 * @Author HaoHaoGe
 * @Date 2021/4/30
 * @Description
 */
public class ArticleListPresenter implements ArticleListContract.ArticleListPresenter {



    private ArticleListContract.View mArticleListView;

    private Channel mChannel;

    private IMyAidlInterface model;

    private boolean notHaveData = false;

    private int articleListBegin = 0;

    /*不确定是否需要这个接口
     *用来防止如果多次触发上拉加载更多导致数据错乱，只允许同个时间只有一个拉数据的操作。
     */
    private boolean isNotInLoading = true;

    public ArticleListPresenter(IMyAidlInterface iMyAidlInterface, ArticleListContract.View articleListView, Channel channel){
        mArticleListView = articleListView;

        model = iMyAidlInterface;

        mArticleListView.setPresenter(this);

        mChannel = channel;
    }

    @Override
    public void start() {
        loadArticle();
    }

    /**
     * 下拉刷新调用的函数，还需要重写
     */
    @Override
    public boolean loadArticle(){
        //防止同时拉取数据
        if(isNotInLoading){
            isNotInLoading = false;

            List<ArticleBrief> articleBriefList = null;
            try {
                articleBriefList = model.getArticleBriefsFromChannel(mChannel, articleListBegin, 5);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            int size = articleBriefList.size();
            if(size < 10) {
                notHaveData = true;
            }

            //如果已经没有更新数据的话需要做优化
            if (size == 0) {
            } else {
                mArticleListView.showArticleList(articleBriefList, articleListBegin, size);
                articleListBegin += size;
            }

            isNotInLoading = true;

            return notHaveData;
        }else{
            return true;
        }
    }

    @Override
    public void reLoadFromNet() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Thread.sleep(3000);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public boolean reLoadArticle(){
        if(isNotInLoading){
            reLoadFromNet();
            reset();
            articleListBegin = 0;
            List<ArticleBrief> articleBriefList = null;
            try {
                articleBriefList = model.getArticleBriefsFromChannel(mChannel, articleListBegin, 10);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            int size = articleBriefList.size();
            if(size < 10) {
                notHaveData = true;
            }
            mArticleListView.refreshArticleList(articleBriefList);
            articleListBegin = size;
            isNotInLoading = true;
            return notHaveData;
        }else{
            return true;
        }
    }

    private void reset(){
        articleListBegin = 0;
        notHaveData = false;
    }
}
