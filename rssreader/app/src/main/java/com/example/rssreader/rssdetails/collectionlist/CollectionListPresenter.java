package com.example.rssreader.rssdetails.collectionlist;

import android.app.Activity;
import android.os.RemoteException;

import com.example.rssreader.IMyAidlInterface;
import com.example.rssreader.model.datamodel.ArticleBrief;
import com.example.rssreader.model.datamodel.Channel;
import com.example.rssreader.model.parse.DataCallback;
import com.example.rssreader.model.parse.XmlCallback;
import com.example.rssreader.rssdetails.ShowListContract;
import com.example.rssreader.rssdetails.ShowListPresenter;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @ClassName ArticleListPresenter
 * @Author HaoHaoGe
 * @Date 2021/5/13
 * @Description
 */
public class CollectionListPresenter extends ShowListPresenter {

    /**
     * Instantiates a new Article list presenter.
     *
     * @param model model层对象
     * @param collectionListView  view层对象
     */
    public CollectionListPresenter(IMyAidlInterface model, ShowListContract.View collectionListView) {
        super(model, collectionListView);
    }

    /**
     * 下拉刷新调用的函数，返回值用于判断是否数据库中是否还有数据没读取完
     * 如果读取完了返回true，否则返回false
     */
    @Override
    public void loadArticle() {
        //防止同时拉取数据
        if(!notHaveData || isNotInLoading){
            isNotInLoading = false;

            List<ArticleBrief> articleBriefList = null;
            try {
                articleBriefList = model.getCollection(showListBegin, 10);
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
                //mShowListView.giveNoteMessage("加载成功");
                showListBegin += size;
            }

            isNotInLoading = true;

        }
    }

    /**取消收藏（写入数据库），并刷新页面
     * @param articleBrief 需要取消收藏的文章
     * @param pos 处在文章list的第几项
     */
    public void cancelCollection(@NotNull ArticleBrief articleBrief, final int pos) {
        try {
            model.removeCollection(articleBrief, new DataCallback.Stub() {
                @Override
                //成功时刷新页面
                public void onSuccess() throws RemoteException {
                    showListBegin--;
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
        if(showListBegin < 3){
            loadArticle();
        }
    }
}
