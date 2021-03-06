package com.example.rssreader.rssSource;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.MenuItem;

import androidx.recyclerview.widget.ItemTouchHelper;

import com.example.rssreader.IMyAidlInterface;
import com.example.rssreader.RssSource;
import com.example.rssreader.rssdetails.articlelist.ArticleListActivity;
import com.example.rssreader.model.datamodel.Channel;
import com.example.rssreader.model.parse.XmlCallback;
import com.example.rssreader.util.AlarmUtil;
import com.example.rssreader.util.ApplicationUtil;
import com.example.rssreader.util.ConfigUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class RssSourcePresenterImpl implements RssSourceContract.RssSourcePresenter {

    //获取对rssSourceView的引用
    private final RssSourceContract.RssSourceView rssSourceView;
    //获取对rssSourceModel的引用
    private IMyAidlInterface myAidlInterface;
    //获取配置文件工具类的引用
    ConfigUtil configUtil;
    //记录是否为第一次加载
    private boolean isFirstLoaded = true;
    //记录当前列表/网格布局是否被选中（默认为网格布局）
    private boolean listChosen = false;
    private boolean gridChosen = true;
    private List<RssSource> rssSourceList;   //暂存所有RSS源，用于recyclerView的显示
    private List<Channel> channelList;       //暂存所有channel
    private boolean addBtnLock = false;        //添加按钮锁，防止用户多次点击按钮出错
    private int minDragIndex = 1000000;   //记录每次拖动的最小下标
    private int maxDragIndex = -1;   //记录每次拖动的最大下标

    public RssSourcePresenterImpl(RssSourceContract.RssSourceView view, IMyAidlInterface myAidlInterface) {
        //presenter绑定view
        this.rssSourceView = view;
        //presenter绑定model
        this.myAidlInterface = myAidlInterface;
        //view绑定presenter
        rssSourceView.setPresenter(this);
        //获取配置文件工具类的实例
        configUtil = ConfigUtil.getInstance(ApplicationUtil.getContext());
    }

    @Override
    public void start() throws RemoteException {
        if (isFirstLoaded == true) {
            this.rssSourceList = getRssSrcList();
            rssSourceView.loadAndRefreshRecyclerView(rssSourceList);
            isFirstLoaded = false;
        }
        if(configUtil.isDarkMode()==true)
            rssSourceView.switchToNightMode();
        else
            rssSourceView.switchToDayMode();
    }

    @Override
    public List<RssSource> getRssSrcList() throws RemoteException {
        List<RssSource> list;
        try {
            //获取所有的RSS源并将它转为rssSource
            channelList = myAidlInterface.getChannel(0, 100);
            list = channelToRssSrc(channelList);
            return list;
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void delSelectedItems() throws RemoteException {
        //获取Rss源和channel列表的迭代器
        Iterator<RssSource> rssSrcIterator = rssSourceList.iterator();
        Iterator<Channel> channelIterator = channelList.iterator();
        //遍历迭代器，从列表中删除被选中的子项
        while (rssSrcIterator.hasNext() && channelIterator.hasNext()) {
            RssSource rssSource = rssSrcIterator.next();
            Channel channel = channelIterator.next();
            if (rssSource.getSelected() == true) {
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
            //如果是日间模式
            if (configUtil.isDarkMode() == false) {
                rssSourceView.setListBtnBackground(Color.parseColor("#808080"));
                rssSourceView.setGridBtnBackground(Color.parseColor("#F5F5F5"));
            }
            //如果为夜间模式
            else {
                rssSourceView.setListBtnBackground(Color.parseColor("#F5F5F5"));
                rssSourceView.setGridBtnBackground(Color.parseColor("#808080"));
            }
            listChosen = true;
            gridChosen = false;
            rssSourceView.convertToList(rssSourceList);
        }
    }

    @Override
    public void setGridLayout() {
        //切换为网格模式
        if (gridChosen == false && listChosen == true) {
            if (configUtil.isDarkMode() == false) {
                rssSourceView.setListBtnBackground(Color.parseColor("#F5F5F5"));
                rssSourceView.setGridBtnBackground(Color.parseColor("#808080"));
            } else {
                rssSourceView.setListBtnBackground(Color.parseColor("#808080"));
                rssSourceView.setGridBtnBackground(Color.parseColor("#F5F5F5"));
            }
            listChosen = false;
            gridChosen = true;
            rssSourceView.convertToGrid(rssSourceList);
        }
    }

    @Override
    public void cancelSelected() {
        for (RssSource rssSource : rssSourceList)
            if (rssSource.getSelected() == true)
                rssSource.setSelected(false);
    }

    @Override
    public void selectAllRss() {
        for (RssSource rssSource : rssSourceList)
            if (rssSource.getSelected() == false)
                rssSource.setSelected(true);
    }

    @Override
    public void addRssSrc(String rssLink, final Activity activity) {
        try {
            //给添加按钮加锁防止多时间内多次点击
            if (addBtnLock == true) {
                rssSourceView.giveHint("加载中，请勿重复点击");
                return;
            }
            addBtnLock = true;
            //检查是否添加了重复的RSS源
            channelList = myAidlInterface.getChannel(0, 100);
            for (Channel channel : channelList) {
                if (channel.getRssLink().equals(rssLink)) {
                    rssSourceView.giveHint("请不要添加重复RSS源");
                    rssSourceView.closeAndClearAddDialog();
                    addBtnLock = false;
                    return;
                }
            }
            rssSourceView.showProgressBar();
            //对RSS链接进行解析加入数据库，并实现回调接口
            myAidlInterface.downloadParseXml(rssLink, new XmlCallback.Stub() {
                @Override
                public void onLoadXmlSuccess() throws RemoteException {
                    //如果成功就获取这个RSS源并刷新RecyclerView
                    channelList = myAidlInterface.getChannel(0, 100);
                    //判断是否添加了重复项
                    if (channelList.size() == rssSourceList.size()) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                rssSourceView.giveHint("请不要添加重复RSS源");
                                rssSourceView.closeAndClearAddDialog();
                            }
                        });
                        addBtnLock = false;
                        rssSourceView.hideProgressBar();
                        return;
                    }
                    //把channel列表中的最后一项加到rssSource列表
                    rssSourceList.add(channelToRssSrc(channelList.get(channelList.size() - 1)));
                    addBtnLock = false;
                    //主线程更新UI
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            rssSourceView.refreshView();
                            rssSourceView.hideProgressBar();
                            rssSourceView.closeAndClearAddDialog();
                        }
                    });
                }

                @Override
                public void onUrlTypeError() throws RemoteException {
                    addBtnLock = false;
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            rssSourceView.hideProgressBar();
                            rssSourceView.giveHint("添加失败，格式错误");
                        }
                    });
                }

                @Override
                public void onParseError() throws RemoteException {
                    addBtnLock = false;
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            rssSourceView.hideProgressBar();
                            rssSourceView.giveHint("添加失败，解析错误");
                        }
                    });
                }

                @Override
                public void onSourceError() throws RemoteException {
                    addBtnLock = false;
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            rssSourceView.hideProgressBar();
                            rssSourceView.giveHint("源错误|网络错误");
                        }
                    });
                }

                @Override
                public void onLoadImgError() throws RemoteException {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            rssSourceView.hideProgressBar();
                            rssSourceView.giveHint("读取图片错误");
                        }
                    });
                    addBtnLock = false;
                }

                @Override
                public void onLoadImgSuccess() throws RemoteException {
                    channelList = myAidlInterface.getChannel(0, 100);
                    //判断是否获得了最新的channel，如果没有就返回
                    if (rssSourceList.size() < channelList.size()) {
                        return;
                    }
                    rssSourceList.get(rssSourceList.size() - 1).setImage(channelList.get(channelList.size() - 1).getImage());
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            rssSourceView.refreshView();
                        }
                    });
                }
            });
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<RssSource> channelToRssSrc(List<Channel> list) {
        List<RssSource> rssSources = new ArrayList<>();
        for (Channel c : list) {
            if (c.getImage() == null)
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
        if (configUtil.isDarkMode() == false) {
            //将模式写入配置文件
            configUtil.setMode(ConfigUtil.MODE_DARK);
            //界面切换为夜间模式
            rssSourceView.switchToNightMode();
        }
        else {
            //将模式写入配置文件
            configUtil.setMode(ConfigUtil.MODE_LIGHT);
            rssSourceView.switchToDayMode();
        }
    }

    @Override
    public int setMovementFlags() {
        int dragFlags = 0;
        //如果是网格布局那么可以上下左右拖动
        if (gridChosen == true && listChosen == false) {
            dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
        }
        //如果是列表布局那么可以上下拖动
        else if (gridChosen == false && listChosen == true)
            dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        return dragFlags;
    }


    @Override
    public boolean setMoving(int srcPos, int desPos) {
        if (rssSourceList.size() == 0)
            return false;
        //循环交换两个item的位置
        if (srcPos < desPos) {
            for (int i = srcPos; i < desPos; i++) {
                //更改两个子项的次序属性
                channelList.get(i).setSequence(i+2);
                channelList.get(i+1).setSequence(i+1);
                //交换缓存中对应的子项数据
                Collections.swap(rssSourceList, i, i + 1);
                Collections.swap(channelList, i, i+1);
                //更新发生变化的最大/最小下标
                minDragIndex = Math.min(minDragIndex, srcPos);
                maxDragIndex = Math.max(maxDragIndex, desPos);
            }
        } else {
            for (int i = srcPos; i > desPos; i--) {
                channelList.get(i).setSequence(i);
                channelList.get(i-1).setSequence(i+1);
                Collections.swap(rssSourceList, i, i - 1);
                Collections.swap(channelList, i, i - 1);
                //更新发生变化的最大/最小下标
                minDragIndex = Math.min(minDragIndex, desPos);
                maxDragIndex = Math.max(maxDragIndex, srcPos);
            }
        }
        //刷新界面
        rssSourceView.refreshAfterMove(srcPos, desPos);
        return true;
    }

    /**
     * 完成一次拖拽后执行的事件
     */
    @Override
    public void afterOneDrag(){
        //初始化要传递给后台的发生变化的channel
        List<Channel>dragChannel = new ArrayList<>();
        for(int i=minDragIndex; i<=maxDragIndex; i++){
            dragChannel.add(channelList.get(i));
        }
        minDragIndex = 1000000;
        maxDragIndex = -1;
        try {
            myAidlInterface.updateChannels(dragChannel);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isTimedUpdateEnabled() {
        if (configUtil.getHour() == -1)
            return false;
        else
            return true;
    }

    /**
     * 设置定时更新
     */
    @Override
    public void setRegularUpdate() {
        long oneDaySec = 24 * 60 * 60 * 1000;
        Calendar calendar = Calendar.getInstance();
        //设置为当前系统时间
        calendar.setTimeInMillis(System.currentTimeMillis());
        //设置为用户选择的时间
        calendar.set(Calendar.HOUR_OF_DAY, rssSourceView.getChosenHour());
        calendar.set(Calendar.MINUTE, rssSourceView.getChosenMinute());
        //将设置时间存入配置文件
        configUtil.setHour(rssSourceView.getChosenHour());
        configUtil.setMinute(rssSourceView.getChosenMinute());

        rssSourceView.giveHint(String.valueOf(rssSourceView.getChosenHour()));
        rssSourceView.giveHint(String.valueOf(rssSourceView.getChosenMinute()));
        //开启闹钟，如果用户选择的时间比当前时间小就要把闹钟开启时间延后一天
        if (calendar.getTimeInMillis() + 1000 < System.currentTimeMillis()) {
            calendar.set(Calendar.SECOND, 0);
            AlarmUtil.startNoticeService(ApplicationUtil.getContext(), calendar.getTimeInMillis() + oneDaySec, ClockBroadcastReceiver.class, "com.example.rssreader.rssNoticeBroadcast");
        } else {
            calendar.set(Calendar.SECOND, 0);
            AlarmUtil.startNoticeService(ApplicationUtil.getContext(), calendar.getTimeInMillis(), ClockBroadcastReceiver.class, "com.example.rssreader.rssNoticeBroadcast");
        }

    }

    @Override
    public void openOrCloseRegularUpdate() {
        //如果是要打开定时通知服务
        if (isTimedUpdateEnabled() == false) {
            rssSourceView.openTimeChooseView();
            //将设定时间设置为默认时间
            rssSourceView.setDefaultTime();
            setRegularUpdate();
        } else {
            rssSourceView.closeTimeChooseView();
            //关闭闹钟
            AlarmUtil.stopNoticeService(ApplicationUtil.getContext(), ClockBroadcastReceiver.class, "com.example.rssreader.rssNoticeBroadcast");
            configUtil.setHour(-1);
            configUtil.setMinute(-1);
        }
    }

    @Override
    public void whenCloseTimeChooseDialog() {
        //如果定时更新处于关闭状态直接关闭弹窗
        if (isTimedUpdateEnabled() == false) {
            rssSourceView.hideTimeChooseDialog();
        } else {
            setRegularUpdate();
            rssSourceView.hideTimeChooseDialog();
        }
    }

    @Override
    public void listAndGridBtnModeSwitch() {
        //如果现在为列表布局
        if (gridChosen == false && listChosen == true) {
            //如果为日间模式
            if (configUtil.isDarkMode() == false) {
                rssSourceView.setListBtnBackground(Color.parseColor("#808080"));
                rssSourceView.setGridBtnBackground(Color.parseColor("#F5F5F5"));
            } else {
                rssSourceView.setListBtnBackground(Color.parseColor("#F5F5F5"));
                rssSourceView.setGridBtnBackground(Color.parseColor("#808080"));
            }
        } else if (gridChosen == true && listChosen == false) {
            if (configUtil.isDarkMode() == false) {
                rssSourceView.setListBtnBackground(Color.parseColor("#F5F5F5"));
                rssSourceView.setGridBtnBackground(Color.parseColor("#808080"));
            } else {
                rssSourceView.setListBtnBackground(Color.parseColor("#808080"));
                rssSourceView.setGridBtnBackground(Color.parseColor("#F5F5F5"));
            }
        }
    }
}
