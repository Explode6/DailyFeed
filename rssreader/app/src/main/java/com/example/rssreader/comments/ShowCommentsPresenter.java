package com.example.rssreader.comments;

import android.content.Context;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.rssreader.IMyAidlInterface;
import com.example.rssreader.model.datamodel.AidlDate;
import com.example.rssreader.model.datamodel.ArticleBrief;
import com.example.rssreader.model.datamodel.GlobalComment;
import com.example.rssreader.model.datamodel.LocalComment;
import com.example.rssreader.model.parse.DataCallback;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/*
*实现评论
 */
public class ShowCommentsPresenter implements CommentsContract.CommentsPresenter{

    private final CommentsContract.CommentsView mCommentsView;      //绑定一个commentsView

    private ArticleBrief particleBrief;                           //用于绑定ArticleBrief

    private IMyAidlInterface model;                             //model层

    private  List<LocalComment> mLocalList;              //局部recyclerView展示的数据

    private  androidx.appcompat.widget.Toolbar mToolbar;

    private int windows_height;
    private int windows_width;

     //创建ShowCommentsPresenter对象，将CommentsView 与fragment绑定
    public ShowCommentsPresenter(IMyAidlInterface iMyAidlInterface,@NonNull CommentsContract.CommentsView commentsView, ArticleBrief articleBrief,androidx.appcompat.widget.Toolbar mToolbar,int heigth,int width){
         particleBrief = articleBrief;
         mCommentsView = commentsView;
         model = iMyAidlInterface;
         windows_height = heigth;
         windows_width = width;
         mCommentsView.setPresenter(this);
         this.mToolbar = mToolbar;
    }
    @Override
    public void start() {
    }


    @Override
    public void showLocalAdd(String content) {
        mCommentsView.loadLocalAdd(content);
    }

    @Override
    public void setSecondEdit() {
        mCommentsView.saveComment();
    }

    @Override
    public void fill_webview() {
        try {
            String html = model.getContentOfArticleBrief(particleBrief, new DataCallback.Stub() {
                @Override
                public void onSuccess() throws RemoteException {

                }

                @Override
                public void onFailure() throws RemoteException {

                }

                @Override
                public void onError() throws RemoteException {

                }
            });
            //获得文章内容后展示
            mCommentsView.showWebview(particleBrief.getTitle(), particleBrief.getCreator(), html);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showPopUpWindow() {
        mCommentsView.loadPopUpWindow(windows_height-300);
    }

    @Override
    public void fakeShowPopUpwindow() {
        mCommentsView.loadFakePopUpWindow();
    }


    //调用函数获取该文章所有的评论，初始化adapter
    @Override
    public void createAdapter() {
        try {
            //获得所有globalcomments 并且传入adapter中
            List<GlobalComment> mGlobeList = model.getGlobalCommentsOfArticle(particleBrief, new DataCallback.Stub() {
                @Override
                public void onSuccess() throws RemoteException {

                }

                @Override
                public void onFailure() throws RemoteException {

                }

                @Override
                public void onError() throws RemoteException {

                }

            });
        mCommentsView.setGlobalList(mGlobeList);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        mCommentsView.initializeAdapter();
    }

    @Override
    public void closePopWindow() {
        mCommentsView.popWindowClose();
    }

    @Override
    public void showPartPopUpWindow(View contenView) {
       mCommentsView.loadLocalWin();
    }

    //添加全局评论
    @Override
    public void addGlobalComment(String comment) {


        Date date = new Date(System.currentTimeMillis());

        AidlDate aidlDate = new AidlDate(date);
        try {
            model.addGlobalCommentToArticle(particleBrief, comment,aidlDate, new DataCallback.Stub() {
                @Override
                public void onSuccess() throws RemoteException {

                }

                @Override
                public void onFailure() throws RemoteException {

                }

                @Override
                public void onError() throws RemoteException {

                }
            });
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void deleteGlobalComment(GlobalComment globalComment) {

        try {
            model.deleteGlobalComment(globalComment, new DataCallback.Stub() {
                @Override
                public void onSuccess() throws RemoteException {

                }

                @Override
                public void onFailure() throws RemoteException {

                }

                @Override
                public void onError() throws RemoteException {

                }
            });
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    //关闭底部导航栏窗口
    @Override
    public void closeNavigationWin() {
          mCommentsView.onCloseNavigationWin();
    }

    @Override
    public void increaseTextSize() {
        mCommentsView.increaseText();
    }

    @Override
    public void decreaseTextsize() {
        mCommentsView.decreaseText();
    }

    @Override
    public void increaseSpaceSize() {
        mCommentsView.increaseSpace();
    }

    public void decreaseSpaceSize() {
        mCommentsView.decreaseSpace();
    }


    //删除一条全局评论
    @Override
    public void deleteGlobalCommentFromView(int position) {
        mCommentsView.onDeleteGlobalCommentFromView(position);
    }

    @Override
    public void setTwoComments(TextView date1, TextView comment1, TextView date2, TextView comment2) {
        mCommentsView.onSetTwoComments(date1, comment1,date2, comment2);
    }

    //添加局部评论进入数据库
    @Override
    public void addLocalComment(EditText editText,String localcomment, String content) {
        String localComment = editText.getText().toString();
        Date date = new Date(System.currentTimeMillis());
        AidlDate aidlDate = new AidlDate(date);
        try {
            model.addLocalCommentToArticle(particleBrief, content, localcomment, aidlDate, new DataCallback.Stub() {
                @Override
                public void onSuccess() throws RemoteException {

                }

                @Override
                public void onFailure() throws RemoteException {

                }

                @Override
                public void onError() throws RemoteException {

                }
            });
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void createLocalAdapter() {
        try {
            List<LocalComment> mlist = model.getLocalCommentsOfArticle(particleBrief, new DataCallback.Stub() {
                @Override
                public void onSuccess() throws RemoteException {

                }

                @Override
                public void onFailure() throws RemoteException {

                }

                @Override
                public void onError() throws RemoteException {

                }
            });

            mCommentsView.setLocalList(mlist);

        } catch (RemoteException e) {
            e.printStackTrace();
        }
        mCommentsView.initialLocalAdapter();

    }

    //detele a piece of local comment
    @Override
    public void deleteLocalComment(LocalComment localComment) {
        try {
            model.deleteLocalComment(localComment, new DataCallback.Stub() {
                @Override
                public void onSuccess() throws RemoteException {
                    
                }
     
                @Override
                public void onFailure() throws RemoteException {
     
                }
     
                @Override
                public void onError() throws RemoteException {
     
                }
            });
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteLocalCommentFromView(int position) {
        mCommentsView.onDeleteLocalCommentFromView(position);
    }

    @Override
    public void testClose() {
        mToolbar.setVisibility(View.GONE);
    }

    @Override
    public void testOpen() {
        mToolbar.setVisibility(View.VISIBLE);
    }

    @Override
    public void showGlbC() {
        mCommentsView.showEditpop();
    }

    //展示设置大小win以及关闭底部win
    @Override
    public void showProgressWin() {
    mCommentsView.showPwinAndDisNwin();
    }

}
