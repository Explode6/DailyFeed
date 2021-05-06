package com.example.rssreader.rssSource;

import android.app.AlarmManager;
import android.app.PendingIntent;

import com.example.rssreader.R;
import com.example.rssreader.RssSource;

import java.util.Iterator;
import java.util.List;

public class RssSourcePresenterImpl implements RssSourceContract.RssSourcePresenter {

    //获取对rssSourceView的引用
    private final RssSourceContract.RssSourceView rssSourceView;
    //获取对rssSourceModel的引用
    private final RssSourceModel rssSourceModel;
    //记录是否为第一次加载
    private boolean isFirstLoaded = true;
    //记录当前列表/网格布局是否被选中（默认为网格布局）
    private  boolean listChosen = false;
    private boolean gridChosen = true;
    private boolean canEdit = false;        //是否进入编辑模式
    private List<RssSource> rssSourceList;   //暂存所有RSS源

    private AlarmManager manager;      //时钟管理器
    private PendingIntent pendingIntent;

    public RssSourcePresenterImpl(RssSourceContract.RssSourceView view, RssSourceModel model){
        //presenter绑定view
        this.rssSourceView = view;
        //presenter绑定model
        this.rssSourceModel = model;
        //view绑定presenter
        rssSourceView.setPresenter(this);
    }
    @Override
    public void start() {
        if(isFirstLoaded == true){
            this.rssSourceList = getRssSrcList();
            rssSourceView.loadRecyclerView(rssSourceList);
            isFirstLoaded = false;
        }
    }

    @Override
    public List<RssSource> getRssSrcList() {
        return rssSourceModel.getAllRssSource();
    }

    public void delSelectedItems() {
        Iterator<RssSource> iterator = rssSourceList.iterator();
        //遍历迭代器，从列表中删除被选中的子项
        while(iterator.hasNext()){
            RssSource rssSource = iterator.next();
            if(rssSource.getSelected() == true){
                iterator.remove();
                //从数据库中删除子项
                rssSourceModel.delRssSource();
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

}
