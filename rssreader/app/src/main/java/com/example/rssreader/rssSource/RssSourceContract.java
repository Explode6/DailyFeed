package com.example.rssreader.rssSource;

import android.content.Context;
import android.view.View;

import com.example.rssreader.RssSource;
import com.example.rssreader.util.BasePresenter;
import com.example.rssreader.util.BaseView;
import com.google.android.material.navigation.NavigationView;

import java.util.Iterator;
import java.util.List;

/**
 * RSS源管理的view层和presenter层接口声明
 */
public interface RssSourceContract {
    interface RssSourceView extends BaseView<RssSourcePresenter>{

        public void loadRecyclerView(List<RssSource>list);
        //刷新recyclerView
        public void refreshView();
        //设置底部弹窗
        public void setBottomWindow();
        //显示底部弹窗
        public void showBottomWindow();
        //设置底部弹窗按钮监听事件
        public View.OnClickListener setBottomWinClickListener();
        //设置列表按钮背景
        public void setListBtnBackground(int imgId);
        //设置网格按钮背景
        public void setGridBtnBackground(int imgId);
        //转换为列表布局
        public void convertToList(List<RssSource>list);
        //转换为网格布局
        public void convertToGrid(List<RssSource>list);
        //进入编辑模式
        public void enterEditMode();
        //退出编辑模式
        public void exitEditMode();
        //设置侧滑菜单点击函数
        public void setNavClickListener(NavigationView navView);
        //设置添加RSS源的弹窗
        public void setAddRssSrcDialog();
        //显示添加RSS源的弹窗
        public void showAddRssSrcDialog();
    }

    interface RssSourcePresenter extends BasePresenter {
        //获取所有RSS源
        public List<RssSource>getRssSrcList();
        //删除选中的RSS源
        public void delSelectedItems();
        //设置为列表布局
        public void setListLayout();
        //设置为网格布局
        public void setGridLayout();
        //取消所有被选中的RSS源
        public void cancelSelected();
        //选中所有的RSS源
        public void selectAllRss();
    }
}

