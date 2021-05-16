package com.example.rssreader.comments;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.rssreader.model.datamodel.GlobalComment;
import com.example.rssreader.model.datamodel.LocalComment;
import com.example.rssreader.util.BasePresenter;
import com.example.rssreader.util.BaseView;

import java.util.List;

public interface CommentsContract {
    interface CommentsView extends BaseView<CommentsPresenter> {

        void showWebview(String html);//根据html加载webview

        void loadPopUpWindow(View contentView);//加载pupupwindow

        void initializeAdapter();//初始化全局评论的adapter

        void popWindowClose();//关闭全局


        void loadLocalPopUpWindow(View contenView);//加载局部评论的popupwindow

        void showPwinAndDisNwin();

        void onCloseNavigationWin();

        void increaseText();

        void decreaseText();

        void increaseSpace();

        void decreaseSpace();

        void onAddlocalcomments(String localComment);

        //删除一条全局评论
        void onDeleteGlobalCommentFromView(int position);

        //删除一条局部评论
        void onDeleteLocalCommentFromView(int position);

        void onAddGlobalComment(List<GlobalComment> newItemList);

        void setGlobalList(List<GlobalComment> globalList);

        void setLocalList(List<LocalComment> localList);

        //设置两条评论
        void onSetTwoComments(TextView date1, TextView comment1, TextView date2, TextView comment2);

        //展示局部评论
        void loadLocalWin();

        //初始化localAdapter
        void initialLocalAdapter();



    }
    interface CommentsPresenter extends BasePresenter {

        void fill_webview();

        void showPopUpWindow(View contentView);

        void createAdapter();

        void closePopWindow();

        void showProgressWin();          //展示设置界面

        void showPartPopUpWindow(View contenView);

        void closeNavigationWin();//关闭底部导航栏

        void increaseTextSize();

        void decreaseTextsize();

        void increaseSpaceSize();

        void decreaseSpaceSize();

        //添加一条全局评论进数据库
        void addGlobalComment(String comment);


        //删除一条全局评论
        void deleteGlobalComment(GlobalComment globalComment);

        //从Fragment中删除评论
        void deleteGlobalCommentFromView(int position);

        //设置两条评论
        void setTwoComments(TextView date1, TextView comment1, TextView date2, TextView comment2);

        //添加一条局部评论进数据库
        void addLocalComment(EditText editText, String localcomment, String conntent);

        //本地评论的adapter
        void createLocalAdapter();

        //删除一条局部评论
        void deleteLocalComment(LocalComment localComment);

        //从Fragment中删除评论
        void deleteLocalCommentFromView(int position);

    }
}