package com.example.rssreader.rssSource;

import android.app.Activity;
import android.content.Context;
import android.os.RemoteException;
import android.view.MenuItem;
import android.view.View;

import androidx.recyclerview.widget.ItemTouchHelper;

import com.example.rssreader.RssSource;
import com.example.rssreader.model.datamodel.Channel;
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
        //刷新recyclerView（会改变adapter中list的引用）
        public void loadAndRefreshRecyclerView(List<RssSource>list);
        //刷新recyclerView
        public void refreshView();
        //设置底部弹窗
        public void setBottomWindow();
        //显示底部弹窗
        public void showBottomWindow();
        //设置底部弹窗按钮监听事件
        public View.OnClickListener setBottomWinClickListener();
        //设置列表按钮背景
        public void setListBtnBackground(int imgColor);
        //设置网格按钮背景
        public void setGridBtnBackground(int imgColor);
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
        //设置添加RSS源弹窗的点击事件
        public void setAddRssSrcListener();
        //关闭添加RSS源的弹窗并清空输入框的内容
        public void closeAndClearAddDialog();
        //通过toast给出提示
        public void giveHint(String hint);
        //转换为夜间模式
        public void switchToNightMode(MenuItem item);
        //转换为日间模式
        public void switchToDayMode(MenuItem item);
        //设置加载框
        public void setProgressBar();
        //显示加载框
        public void showProgressBar();
        //隐藏加载框
        public void hideProgressBar();
        //设置实现拖动功能的帮助类
        public ItemTouchHelper setItemTouchHelper();
    }

    interface RssSourcePresenter extends BasePresenter {
        //获取所有RSS源
        public List<RssSource>getRssSrcList() throws RemoteException;
        //删除选中的RSS源
        public void delSelectedItems() throws RemoteException;
        //设置为列表布局
        public void setListLayout();
        //设置为网格布局
        public void setGridLayout();
        //取消所有被选中的RSS源
        public void cancelSelected();
        //选中所有的RSS源
        public void selectAllRss();
        //添加RSS源
        public void addRssSrc(String rssLink, Activity activity);
        //channel列表转为rssSource列表
        public List<RssSource> channelToRssSrc(List<Channel>list);
        //channel转为rssSource
        public RssSource channelToRssSrc(Channel channel);
        //点击RSS源之后将RSS源信息传递给下一个界面
        public void transferChannel(Context srcActivity, int pos);
        //日间/夜间模式切换
        public void modeSwitching(MenuItem item);
    }
}

