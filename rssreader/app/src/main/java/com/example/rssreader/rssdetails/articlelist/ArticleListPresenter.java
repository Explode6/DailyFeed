package com.example.rssreader.rssdetails.articlelist;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.rssreader.IMyAidlInterface;
import com.example.rssreader.model.datamodel.ArticleBrief;
import com.example.rssreader.model.datamodel.Channel;
import com.example.rssreader.model.parse.DataCallback;
import com.example.rssreader.model.parse.XmlCallback;
import com.example.rssreader.rssdetails.ShowListContract;
import com.example.rssreader.rssdetails.ShowListPresenter;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * @ClassName ArticleListPresenter
 * @Author HaoHaoGe
 * @Date 2021/5/13
 * @Description
 */
class ArticleListPresenter extends ShowListPresenter {

    Channel mChannel;

    Handler mHandler;

    private boolean isActive;

    static final int RELOAD = 1;
    static final int STOPREFRESH = 2;
    static final int GIVEMESSAGE = 3;

    /**
     * Instantiates a new Article list presenter.
     *
     * @param model model层对象
     * @param articleListView  view层对象
     */
    public ArticleListPresenter(IMyAidlInterface model, final ShowListContract.View articleListView, Channel channel) {
        super(model, articleListView);
        mChannel = channel;
        isActive = true;
    }

    @Override
    public void loadArticle() {
        //isNotInLoading防止同时有多个操作请求数据从而拿到了错误的数据
        //notHaveData用于标志是否已经把所有文章都展示出来了，如果所有文章都展示的话就不再请求数据库
        if(!notHaveData || isNotInLoading){
            isNotInLoading = false;

            List<ArticleBrief> articleBriefList = null;
            try {
                articleBriefList = model.getArticleBriefsFromChannel(mChannel, showListBegin, 10);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            int size = articleBriefList.size();
            //因为每次取10个数据，如果数据小于10的话说明数据库已经取完了
            if(size < 10) {
                notHaveData = true;
                mShowListView.changeFooterViewStyle();
            }

            //如果已经没有更新数据的话需要做优化
            if (size == 0) {
            } else {
                mShowListView.showArticleList(articleBriefList, showListBegin, size);
                showListBegin += size;
                //mShowListView.giveNoteMessage("加载成功");
            }
            isNotInLoading = true;
        }
    }


    public void reLoadArticle() {
        //isNotInLoading防止同时有多个操作请求数据从而拿到了错误的数据
        if(isNotInLoading) {
            //把当前缓存的数据清空
            reset();
            List<ArticleBrief> articleBriefList = null;
            try {
                articleBriefList = model.getArticleBriefsFromChannel(mChannel, showListBegin, 10);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            int size = articleBriefList.size();
            if (size < 10) {
                notHaveData = true;
                mShowListView.changeFooterViewStyle();
            }
            mShowListView.refreshArticleList(articleBriefList);
            showListBegin = size;
            isNotInLoading = true;
        }
    }

    /**
     * 检查当前RSS源有无更新，如果有的话把最新的数据写进数据库，并且展示最新的文章
     * @param activity 传来当前的Activity从而能够在UI线程中改变UI
     */
    public void refreshChannel(final Activity activity) {
        try {
            model.downloadParseXml(mChannel.getRssLink(), new XmlCallback.Stub() {
                @Override
                public void onLoadXmlSuccess() throws RemoteException {


                    if(isActive) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                reLoadArticle();
                                mShowListView.stopRefreshUI();
                                mShowListView.giveNoteMessage("RSS源更新成功");
                            }
                        });
                    }
                }

                @Override
                public void onUrlTypeError() throws RemoteException {
                    if (isActive){
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                loadArticle();
                                mShowListView.stopRefreshUI();
                                mShowListView.giveNoteMessage("RSS源源地址格式错误");
                            }
                        });
                    }
                }

                @Override
                public void onParseError() throws RemoteException {
                    if (isActive) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                loadArticle();
                                mShowListView.stopRefreshUI();
                                mShowListView.giveNoteMessage("RSS源解析失败");
                            }
                        });
                    }
                }

                @Override
                public void onSourceError() throws RemoteException {
                    if (isActive) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mShowListView.stopRefreshUI();
                                mShowListView.giveNoteMessage("RSS源地址已失效");
                            }
                        });
                    }
                }

                @Override
                public void onLoadImgError() throws RemoteException {
                }

                @Override
                public void onLoadImgSuccess() throws RemoteException {
                }
            });
        } catch (RemoteException e) {
            e.printStackTrace();
            mShowListView.giveNoteMessage("解析失败");
            mShowListView.stopRefreshUI();
        }
    }

    /**添加文章进收藏（加入数据库），并刷新页面
     * @param articleBrief 需要收藏的文章
     * @param pos 处在文章list的第几项
     */
    public void switchCollection(@NotNull ArticleBrief articleBrief, final int pos) {
        if(articleBrief.getCollect()){
            try {
                model.removeCollection(articleBrief, new DataCallback.Stub() {
                    @Override
                    //成功时刷新页面
                    public void onSuccess() throws RemoteException {
                        mShowListView.switchCollectionAndRefresh(pos);
                    }

                    @Override
                    //失败时弹出错误信息
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
        else{
            try {
                model.collectArticle(articleBrief, new DataCallback.Stub() {
                    @Override
                    //成功时刷新页面
                    public void onSuccess() throws RemoteException {
                        mShowListView.switchCollectionAndRefresh(pos);
                    }

                    @Override
                    //失败时弹出错误信息
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
    }

    //清空当前缓存的数据
    private void reset(){
        showListBegin = 0;
        notHaveData = false;
    }

    public void closeAct(){
        isActive = false;
    }
}
