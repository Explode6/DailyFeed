package com.example.ArticleList.articlelist;

import com.example.ArticleList.Data.ArticleBrief;
import com.example.ArticleList.Data.ShuhaoJiekou;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;

/**
 * @ClassName ArticleListPresenter
 * @Author HaoHaoGe
 * @Date 2021/4/30
 * @Description
 */
public class ArticleListPresenter implements ArticleListContract.Presenter {

    private ShuhaoJiekou mshuHaoJiekou;

    private ArticleListContract.View mArticleListView;

    private boolean notHaveData = false;

    private int articleListBegin = 0;

    /*不确定是否需要这个接口
     *用来防止如果多次触发上拉加载更多导致数据错乱，只允许同个时间只有一个拉数据的操作。
     */
    private boolean isNotInLoading = true;

    public ArticleListPresenter(ShuhaoJiekou shuhaoJiekou, ArticleListContract.View articleListView){
        mArticleListView = articleListView;
        mshuHaoJiekou = shuhaoJiekou;

        mArticleListView.setPresenter(this);
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

            List<ArticleBrief> articleBriefList = ShuhaoJiekou.getData(articleListBegin, 10);
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
            List<ArticleBrief> articleBriefList = ShuhaoJiekou.getData(articleListBegin, 10);
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
