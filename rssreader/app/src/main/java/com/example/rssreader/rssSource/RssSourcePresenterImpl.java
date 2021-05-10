package com.example.rssreader.rssSource;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.example.rssreader.IMyAidlInterface;
import com.example.rssreader.R;
import com.example.rssreader.RssSource;
import com.example.rssreader.articlelist.ArticleListActivity;
import com.example.rssreader.model.datamodel.Channel;
import com.example.rssreader.model.parse.AidlBinder;
import com.example.rssreader.model.parse.DataCallback;
import com.example.rssreader.util.BasePresenter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import kotlin.jvm.internal.Ref;

public class RssSourcePresenterImpl implements RssSourceContract.RssSourcePresenter {

    //获取对rssSourceView的引用
    private final RssSourceContract.RssSourceView rssSourceView;
    //获取对rssSourceModel的引用
    private IMyAidlInterface myAidlInterface;
    //记录是否为第一次加载
    private boolean isFirstLoaded = true;
    //记录当前列表/网格布局是否被选中（默认为网格布局）
    private  boolean listChosen = false;
    private boolean gridChosen = true;
    private boolean canEdit = false;        //是否进入编辑模式
    private boolean isNightMode = false;            //是否进入了夜间模式
    private List<RssSource>rssSourceList;   //暂存所有RSS源，用于recyclerView的显示
    private List<Channel>channelList;       //暂存所有channel

    private  AlarmManager manager;      //时钟管理器
    private PendingIntent pendingIntent;

    public RssSourcePresenterImpl(RssSourceContract.RssSourceView view, IMyAidlInterface myAidlInterface){
        //presenter绑定view
        this.rssSourceView = view;
        //presenter绑定model
        this.myAidlInterface = myAidlInterface;
        //view绑定presenter
        rssSourceView.setPresenter(this);
    }
    @Override
    public void start() throws RemoteException {
        if(isFirstLoaded == true){
            this.rssSourceList = getRssSrcList();
            rssSourceView.loadAndRefreshRecyclerView(rssSourceList);
            isFirstLoaded = false;
        }
    }

    @Override
    public List<RssSource> getRssSrcList() throws RemoteException {
        List<RssSource>list;
        try {
                //获取所有的RSS源并将它转为rssSource
                channelList = myAidlInterface.getChannel(0,100);
                list = channelToRssSrc(channelList);
                return list;
        }catch (RemoteException e){
                e.printStackTrace();
        }
        return null;
    }

    public void delSelectedItems() throws RemoteException {
        //获取Rss源和channel列表的迭代器
        Iterator<RssSource>rssSrcIterator = rssSourceList.iterator();
        Iterator<Channel>channelIterator = channelList.iterator();
        //遍历迭代器，从列表中删除被选中的子项
        while(rssSrcIterator.hasNext() && channelIterator.hasNext()){
            RssSource rssSource = rssSrcIterator.next();
            Channel channel = channelIterator.next();
            if(rssSource.getSelected() == true){
                rssSrcIterator.remove();
                //从数据库中删除这个RSS源
                myAidlInterface.removeChannel(channel);
                channelIterator.remove();
                continue;
            }
        }
        //退出编辑模式
        rssSourceView.exitEditMode();
    }

    @Override
    public void setListLayout() {
        //切换为列表模式
        if (gridChosen == true && listChosen == false) {
            rssSourceView.setListBtnBackground(Color.parseColor("#808080"));
            rssSourceView.setGridBtnBackground(Color.parseColor("#F5F5F5"));
            listChosen = true;
            gridChosen = false;
            rssSourceView.convertToList(rssSourceList);
        }
    }

    @Override
    public void setGridLayout() {
        //切换为网格模式
        if (gridChosen == false && listChosen == true){
            rssSourceView.setListBtnBackground(Color.parseColor("#F5F5F5"));
            rssSourceView.setGridBtnBackground(Color.parseColor("#808080"));
            listChosen = false;
            gridChosen = true;
            rssSourceView.convertToGrid(rssSourceList);
        }
    }

    @Override
    public void cancelSelected() {
        for(RssSource rssSource : rssSourceList)
            if(rssSource.getSelected() == true)
                rssSource.setSelected(false);
    }

    @Override
    public void selectAllRss() {
        for(RssSource rssSource : rssSourceList)
            if(rssSource.getSelected() == false)
                rssSource.setSelected(true);
    }

    @Override
    public void addRssSrc(String rssLink, final Activity activity) {
        //首先检查是否添加了重复的RSS源
       try {
           channelList = myAidlInterface.getChannel(0, 100);
           for(Channel channel : channelList){
               if(channel.getRssLink().equals(rssLink)){
                   rssSourceView.giveHint("请不要重复添加");
                   return;
               }
           }
           //对RSS链接进行解析加入数据库，并实现回调接口
           myAidlInterface.downloadParseXml(rssLink, new DataCallback.Stub() {
               @Override
               public void onSuccess() throws RemoteException {
                    //如果成功就获取这个RSS源并刷新RecyclerView
                   // channelList = myAidlInterface.getChannel(0, 100);
                   //把channel列表中的最后一项rssSource列表
                   rssSourceList.add(channelToRssSrc(channelList.get(channelList.size()-1)));
                   //主线程更新UI
                   activity.runOnUiThread(new Runnable() {
                       @Override
                       public void run() {
                           rssSourceView.refreshView();
                           rssSourceView.closeAndClearAddDialog();
                           rssSourceView.giveHint("添加成功");
                       }
                   });
               }

               //连接服务失败
               @Override
               public void onFailure() throws RemoteException {
                   activity.runOnUiThread(new Runnable() {
                       @Override
                       public void run() {
                           rssSourceView.giveHint("添加失败，请检查网络");
                       }
                   });
               }

               @Override
               public void onError() throws RemoteException {
                   activity.runOnUiThread(new Runnable() {
                       @Override
                       public void run() {
                           rssSourceView.giveHint("添加失败，请输入合法URL");
                       }
                   });
               }
           });
       }catch (RemoteException e){
           e.printStackTrace();
       }
    }

    @Override
    public List<RssSource> channelToRssSrc(List<Channel> list) {
        List<RssSource> rssSources = new ArrayList<>();
        for(Channel c : list){
            if(c.getImage() == null)
                rssSources.add(new RssSource(c.getTitle(), c.getDescription(), null, false));
            else
                rssSources.add(new RssSource(c.getTitle(), c.getDescription(), c.getImage(), false));
        }
        return rssSources;
    }

    @Override
    public RssSource channelToRssSrc(Channel c) {
        return new RssSource(c.getTitle(), c.getDescription(), c.getImage(), false);
    }

    @Override
    public void transferChannel(Context srcActivity, int pos) {
        Intent intent = new Intent(srcActivity, ArticleListActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable("channel", channelList.get(pos));
        intent.putExtras(bundle);
        srcActivity.startActivity(intent);
    }

    @Override
    public void modeSwitching(MenuItem item) {
        //如果现在为日间模式
        if(isNightMode == false){
            //界面切换为夜间模式
            rssSourceView.switchToNightMode(item);
            isNightMode = true;
            //将模式写入配置文件
        }else{
            rssSourceView.switchToDayMode(item);
            isNightMode = false;
            //将模式写入配置文件
        }
    }
}
