package com.example.rssreader.rssdetails.collectionlist;

import android.os.RemoteException;

import com.example.rssreader.IMyAidlInterface;
import com.example.rssreader.model.datamodel.ArticleBrief;
import com.example.rssreader.model.parse.DataCallback;
import com.example.rssreader.rssdetails.ShowListContract;
import com.example.rssreader.rssdetails.ShowListPresenter;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @ClassName SearchListPresenter
 * @Author HaoHaoGe
 * @Date 2021/5/17
 * @Description
 */
class SearchListPresenter extends ShowListPresenter {
    /**
     /**
     * Instantiates a new Article list presenter.
     *
     * @param model model层对象
     * @param SearchListView  view层对象
     */

    SearchListFragment mSearchListFragment;
    public SearchListPresenter(IMyAidlInterface model, ShowListContract.View SearchListView) {
        super(model, SearchListView);
        mSearchListFragment = (SearchListFragment) mShowListView;
    }

    /**
     * 下拉刷新调用的函数，返回值用于判断是否数据库中是否还有数据没读取完
     * 如果读取完了返回true，否则返回false
     */
    @Override
    public void loadArticle() {
    }

    public void searchArticle(String word){
        if(isNotInLoading){
            isNotInLoading = false;

            List<ArticleBrief> articleBriefList = null;
            try {
                articleBriefList = model.searchCollection(word);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            isNotInLoading = true;

            int size = articleBriefList.size();
            if(size == 0){
                mShowListView.giveNoteMessage("什么都没有找到TaT");
            }else{
                mShowListView.giveNoteMessage("文章加载成功");
            }

            mShowListView.refreshArticleList(articleBriefList);
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
