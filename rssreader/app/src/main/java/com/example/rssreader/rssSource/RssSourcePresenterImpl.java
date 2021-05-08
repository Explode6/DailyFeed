package com.example.rssreader.rssSource;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Base64;
import android.util.Log;
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

    public void delSelectedItems() {
        Iterator<RssSource>iterator = rssSourceList.iterator();
        //遍历迭代器，从列表中删除被选中的子项
        while(iterator.hasNext()){
            RssSource rssSource = iterator.next();
            if(rssSource.getSelected() == true){
                iterator.remove();
                //从数据库中删除子项
                //rssSourceModel.delRssSource();
                continue;
            }
        }
        rssSourceView.exitEditMode();
    }

    @Override
    public void setListLayout() {
        //切换为列表模式
        if (gridChosen == true && listChosen == false) {
            rssSourceView.setListBtnBackground(R.drawable.test1);
            rssSourceView.setGridBtnBackground(R.drawable.add_icon);
            listChosen = true;
            gridChosen = false;
            rssSourceView.convertToList(rssSourceList);
        }
    }

    @Override
    public void setGridLayout() {
        if (gridChosen == false && listChosen == true){
            rssSourceView.setListBtnBackground(R.drawable.add_icon);
            rssSourceView.setGridBtnBackground(R.drawable.test1);
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
    public void addRssSrc(String rssLink) {
        //首先对RSS链接进行解析加入数据库，并实现回调接口
       try {
           myAidlInterface.downloadParseXml(rssLink, new DataCallback.Stub() {
               @Override
               public void onSuccess() throws RemoteException {
                    //如果成功就获取这个RSS源并刷新RecyclerView
                   channelList = myAidlInterface.getChannel(0, 100);
                   //把channel列表中的最后一项rssSource列表
                   rssSourceList.add(channelToRssSrc(channelList.get(channelList.size()-1)));
                   rssSourceView.closeAndClearAddDialog();
                   rssSourceView.refreshView();
                   rssSourceView.giveHint("添加成功");
               }

               @Override
               public void onFailure() throws RemoteException {
                   rssSourceView.giveHint("添加失败");
               }

               @Override
               public void onError() throws RemoteException {

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
}
