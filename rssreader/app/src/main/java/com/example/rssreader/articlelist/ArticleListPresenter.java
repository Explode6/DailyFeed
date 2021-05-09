package com.example.rssreader.articlelist;


import android.content.Context;
import android.os.IBinder;
import android.os.RemoteException;
import android.widget.Toast;

import com.example.rssreader.IMyAidlInterface;
import com.example.rssreader.model.datamodel.ArticleBrief;
import com.example.rssreader.model.datamodel.Channel;
import com.example.rssreader.model.parse.DataCallback;

import java.util.List;

/**
 * The type Article list presenter.
 *
 * @ClassName ArticleListPresenter
 * @Author HaoHaoGe
 * @Date 2021 /4/30
 * @Description
 */
public class ArticleListPresenter implements ArticleListContract.ArticleListPresenter {



    //view层
    private ArticleListContract.View mArticleListView;

    //用于记录从上个界面传来的rss源，用于去数据库加载对应的文章
    private Channel mChannel;

    //model层
    private IMyAidlInterface model;

    //判断数据库是否还有数据，如果没有的话可用于改变FooterView样式
    private boolean notHaveData = false;

    //数据起点，用于记录当前数据个数，从而向数据库读取新的数据
    private int articleListBegin = 0;

    /*不确定是否需要这个接口
     *用来防止如果多次触发上拉加载更多导致数据错乱，只允许同个时间只有一个拉数据的操作。
     */
    private boolean isNotInLoading = true;

    /**
     * Instantiates a new Article list presenter.
     *
     * @param iMyAidlInterface model层对象
     * @param articleListView  view层对象
     * @param channel          当前界面对应的rss源
     */
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
        //
        else{
            return true;
        }
    }

    /**
     * 上拉加载更多调用的函数，返回值用于判断是否数据库中是否还有数据没读取完
     * 这里比正常load多了一个操作，也就是把当前的list置空然后重新加载数据
     */
    @Override
    public boolean reLoadArticle(){
        if(isNotInLoading){
            //把当前缓存的数据清空
            reset();
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

    /** 切换进入具体的文章页面
     * @param articleBrief 想要阅读的文章
     * @param pos 处在文章list的第几项
     */
    @Override
    public void openArticleDetails(ArticleBrief articleBrief, int pos) {
        //先把文章标记为已读
        markRead(pos);
        mArticleListView.showArticleDetails(articleBrief);
    }


    /** 改变文章已读属性（加入数据库），并刷新页面
     * @param articleBrief 改变已读属性的文章
     * @param pos 处在文章list的第几项
     */
    @Override
    public void markRead(int pos) {
        // 还没写好
        // model.setRead()
        mArticleListView.markReadAndRefresh(pos);
    }

    /**添加文章进收藏（加入数据库），并刷新页面
     * @param articleBrief 需要收藏的文章
     * @param pos 处在文章list的第几项
     */
    @Override
    public void addCollection(ArticleBrief articleBrief, final int pos) {
        try {
            model.collectArticle(articleBrief, new DataCallback.Stub() {
                @Override
                //成功时刷新页面
                public void onSuccess() throws RemoteException {
                    mArticleListView.addCollectionAndRefresh(pos);
                }

                @Override
                //失败时弹出错误信息
                public void onFailure() throws RemoteException {
                    mArticleListView.giveWrongMessage("文章已被删除");
                }

                @Override
                public void onError() throws RemoteException {
                    mArticleListView.giveWrongMessage("数据库出错了");
                }
            });
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    //清空当前缓存的数据
    private void reset(){
        articleListBegin = 0;
        notHaveData = false;
    }

}
