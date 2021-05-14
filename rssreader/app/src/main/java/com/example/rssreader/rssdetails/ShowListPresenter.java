package com.example.rssreader.rssdetails;

import android.app.Activity;
import android.os.RemoteException;

import com.example.rssreader.IMyAidlInterface;
import com.example.rssreader.model.datamodel.ArticleBrief;
import com.example.rssreader.model.parse.DataCallback;

import org.jetbrains.annotations.NotNull;

/**
 * @ClassName ShowListPresenter
 * @Author HaoHaoGe
 * @Date 2021/5/13
 * @Description 我的收藏页面以及RSSdetail页面的功能基本一致，所以这里采用两个view共用一个presenter的思路
 * 不过由于presenter需要的参数，以及读取数据时做的操作不一致，所以没法直接共用，所以采用把共有的部分抽出来，把相同方法但实现不同的写为Abstract方法，并把这个presenter作为抽象类
 * 在两个页面分别用各自的presenter继承这个类并Override需要的方法
 */
public abstract class ShowListPresenter implements ShowListContract.ShowListPresenter {

    //view层
    protected ShowListContract.View mShowListView;

    //model层
    protected IMyAidlInterface model;

    //判断数据库是否还有数据，如果没有的话可用于改变FooterView样式
    protected boolean notHaveData = false;

    //数据起点，用于记录当前数据个数，从而向数据库读取新的数据
    protected int showListBegin = 0;

    /*不确定是否需要这个接口
     *用来防止如果多次触发上拉加载更多导致数据错乱，只允许同个时间只有一个拉数据的操作。
     */
    protected boolean isNotInLoading = true;


    /**
     * Instantiates a new Article list presenter.
     *
     * @param iMyAidlInterface model层对象
     * @param showListView  view层对象
     */
    public ShowListPresenter(IMyAidlInterface iMyAidlInterface, ShowListContract.View showListView){
        mShowListView = showListView;

        model = iMyAidlInterface;

        mShowListView.setPresenter(this);

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
    public abstract void loadArticle();
    /** 切换进入具体的文章页面
     * @param articleBrief 想要阅读的文章
     * @param pos 处在文章list的第几项
     */
    @Override
    public void openArticleDetails(ArticleBrief articleBrief, int pos) {
        //先把文章标记为已读
        markRead(articleBrief, pos);
        mShowListView.showArticleDetails(articleBrief);
    }


    /** 改变文章已读属性（加入数据库），并刷新页面
     * @param pos 处在文章list的第几项
     */
    @Override
    public void markRead(ArticleBrief articleBrief, final int pos) {
        try {
            model.readArticle(articleBrief, new DataCallback.Stub() {
                @Override
                public void onSuccess() throws RemoteException {
                    mShowListView.markReadAndRefresh(pos);
                }

                @Override
                public void onFailure() throws RemoteException {
                    mShowListView.giveNoteMessage("文章已被删除");
                }

                @Override
                public void onError() throws RemoteException {
                    mShowListView.giveNoteMessage("数据库出错了");
                }
            });
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }




    /**切换文章已读/未读属性（加入数据库），并刷新页面
     * @param articleBrief 需要收藏的文章
     * @param pos 处在文章list的第几项
     */
    @Override
    public void switchRead(@NotNull ArticleBrief articleBrief, final int pos) {
        if(articleBrief.getRead()){
            try {
                model.collectArticle(articleBrief, new DataCallback.Stub() {
                    @Override
                    public void onSuccess() throws RemoteException {
                        mShowListView.switchReadAndRefresh(pos);
                    }

                    @Override
                    public void onFailure() throws RemoteException {
                        mShowListView.giveNoteMessage("文章已被删除");
                    }

                    @Override
                    public void onError() throws RemoteException {
                        mShowListView.giveNoteMessage("数据库出错了");
                    }
                });
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }else{
            markRead(articleBrief, pos);
        }
    }

}
