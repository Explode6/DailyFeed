package com.example.rssreader.articleCollection;


import android.os.RemoteException;

import com.example.rssreader.IMyAidlInterface;
import com.example.rssreader.model.datamodel.ArticleBrief;
import com.example.rssreader.model.datamodel.Channel;
import com.example.rssreader.model.parse.DataCallback;

import java.util.List;

/**
 * The type Article list presenter.
 *
 * @ClassName ArticleCollectionPresenter
 * @Author HaoHaoGe
 * @Date 2021 /4/30
 * @Description
 */
public class ArticleCollectionPresenter implements ArticleCollectionContract.ArticleCollectionPresenter {



    //view层
    private ArticleCollectionContract.View mArticleCollectionView;


    //model层
    private IMyAidlInterface model;

    //判断数据库是否还有数据，如果没有的话可用于改变FooterView样式
    private boolean notHaveData = false;

    //数据起点，用于记录当前数据个数，从而向数据库读取新的数据
    private int articleCollectionBegin = 0;

    /*不确定是否需要这个接口
     *用来防止如果多次触发上拉加载更多导致数据错乱，只允许同个时间只有一个拉数据的操作。
     */
    private boolean isNotInLoading = true;

    /**
     * Instantiates a new Article list presenter.
     *
     * @param iMyAidlInterface model层对象
     * @param articleCollectionView  view层对象
     */
    public ArticleCollectionPresenter(IMyAidlInterface iMyAidlInterface, ArticleCollectionContract.View articleCollectionView){
        mArticleCollectionView = articleCollectionView;

        model = iMyAidlInterface;

        mArticleCollectionView.setPresenter(this);

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
    public void loadArticle(){
        //防止同时拉取数据
        if(getArticleBriefData()){
            mArticleCollectionView.changeFooterViewStyle();
        }else{
            mArticleCollectionView.giveLoadSuccessfulMessage();
        }
    }

    @Override
    public boolean getArticleBriefData() {
        //防止同时拉取数据
        if(isNotInLoading){
            isNotInLoading = false;

            List<ArticleBrief> articleBriefList = null;
            try {
                articleBriefList = model.getCollection(articleCollectionBegin, 10);
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
                mArticleCollectionView.showArticleCollection(articleBriefList, articleCollectionBegin, size);
                articleCollectionBegin += size;
            }

            isNotInLoading = true;

            return notHaveData;
        }
        //
        else{
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
        mArticleCollectionView.showArticleDetails(articleBrief);
    }


    /** 改变文章已读属性（加入数据库），并刷新页面
     *
     * @param pos 处在文章list的第几项
     */
    @Override
    public void markRead(int pos) {
        // 还没写好
        // model.setRead()
        mArticleCollectionView.markReadAndRefresh(pos);
    }

    /**添加文章进收藏（加入数据库），并刷新页面
     * @param articleBrief 需要收藏的文章
     * @param pos 处在文章list的第几项
     */
    @Override
    public void switchCollection(ArticleBrief articleBrief, final int pos, boolean isCollect) {
        if(isCollect){
            try {
                model.removeCollection(articleBrief, new DataCallback.Stub() {
                    @Override
                    //成功时刷新页面
                    public void onSuccess() throws RemoteException {
                        mArticleCollectionView.switchCollectionAndRefresh(pos);
                        articleCollectionBegin--;
                    }

                    @Override
                    //失败时弹出错误信息
                    public void onFailure() throws RemoteException {
                        mArticleCollectionView.giveWrongMessage("文章已被删除");
                    }

                    @Override
                    public void onError() throws RemoteException {
                        mArticleCollectionView.giveWrongMessage("数据库出错了");
                    }
                });
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }else {
            try {
                model.collectArticle(articleBrief, new DataCallback.Stub() {
                    @Override
                    //成功时刷新页面
                    public void onSuccess() throws RemoteException {
                        mArticleCollectionView.switchCollectionAndRefresh(pos);
                    }

                    @Override
                    //失败时弹出错误信息
                    public void onFailure() throws RemoteException {
                        mArticleCollectionView.giveWrongMessage("文章已被删除");
                    }

                    @Override
                    public void onError() throws RemoteException {
                        mArticleCollectionView.giveWrongMessage("数据库出错了");
                    }
                });
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    //清空当前缓存的数据
    private void reset(){
        articleCollectionBegin = 0;
        notHaveData = false;
    }

}
