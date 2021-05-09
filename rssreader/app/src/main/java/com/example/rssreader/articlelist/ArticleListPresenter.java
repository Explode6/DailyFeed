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
 * @Description presenter层，用于view层和model层的连接，从model获取数据并改变view层
 */
public class ArticleListPresenter implements ArticleListContract.ArticleListPresenter {


    //View层
    private ArticleListContract.View mArticleListView;

    //用于记录从第一个界面得到的channel，从而能够去数据库读取相应的文章
    private Channel mChannel;

    //model层
    private IMyAidlInterface model;

    //记录数据库的数据是否已经读完，读完之后需改变FooterView样式
    private boolean notHaveData = false;

    //数据起点，用于记录当前数据个数，从而向数据库读取新的数据
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

    /*
     * 该方法在fragment中的resume方法中调用，用于读取数据后初始化界面
     */
    @Override
    public void start() {
        loadArticle();
    }

    /**
     * 下拉刷新调用的函数，返回值用于判断是否数据库中是否还有数据没读取完
     * 如果读取完了返回true，否则返回false
     */
    @Override
    public boolean loadArticle(){
        //防止同时拉取数据
        if(isNotInLoading){
            //从数据库取数据
            return getArticleData();
        }
        //
        else{
            return true;
        }
    }

    /**
     * 上拉加载更多的函数，返回值用于判断是否数据库中是否还有数据没读取完
     * 这里比正常load多了一个操作，也就是把当前的list置空然后重新加载数据
     */
    @Override
    public boolean reLoadArticle(){
        if(isNotInLoading){
            //把当前数据清空
            reset();
            return getArticleData();
        }else{
            return true;
        }
    }

    /*
     * 从数据库读取文章的list
     */
    private boolean getArticleData(){
        isNotInLoading = false;

        List<ArticleBrief> articleBriefList = null;
        try {
            articleBriefList = model.getArticleBriefsFromChannel(mChannel, articleListBegin, 10);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        int size = articleBriefList.size();
        //因为每次取10个数据，如果数据小于10的话说明数据库已经取完了
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
    }

    //清空当前缓存的数据
    private void reset(){
        articleListBegin = 0;
        notHaveData = false;
    }
}
