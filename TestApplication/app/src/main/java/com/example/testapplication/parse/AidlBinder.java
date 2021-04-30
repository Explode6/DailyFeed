package com.example.testapplication.parse;

import android.os.RemoteException;

import com.example.testapplication.IMyAidlInterface;
import com.example.testapplication.datamodel.ArticleBrief;
import com.example.testapplication.datamodel.Channel;
import com.example.testapplication.datamodel.Collection;
import com.example.testapplication.datamodel.DataBaseHelper;

import java.sql.SQLException;
import java.util.List;

/**
 * @ClassName： AidlBinder
 * @Author SH
 * @Date： 2021/4/29
 * @Description： 获得AidlBinder类后，可以调用类中的方法控制DataService。
 */
class AidlBinder extends IMyAidlInterface.Stub {
    private DataService dataService;

    private DataCallback dataCallback;

    public AidlBinder(DataService service){
        this.dataService = service;
    }

    /**
     * 下载解析Xml，解析后数据会进入数据库
     * @param url
     * @throws RemoteException
     */
    @Override
    public void downloadParseXml(String url) throws RemoteException {
        dataService.parseXml(url);
    }

    /**
     * 注册回调函数
     * 注意！！！！！
     * 在使用此类前需要调用这个方法，将回调函数注册，否则会出现闪退情况。
     * @param dataCallback
     * @throws RemoteException
     */
    @Override
    public void registerCallback(DataCallback dataCallback) throws RemoteException {
        if(dataCallback == null) return;
        this.dataCallback = dataCallback;
    }

    /**
     * 获取频道的列表
     * @param offset
     * @param limit
     * @return
     */
    @Override
    public List<Channel> getChannel(int offset, int limit){
        return dataService.getChannel(offset,limit);
    }

    /**
     * 通过文字简介收藏某篇文章
     * @param articleBrief
     * @throws RemoteException
     */
    @Override
    public void collectArticle(ArticleBrief articleBrief) throws RemoteException {
        try {
            dataService.collectArticle(articleBrief);
            dataCallback.onSuccess();
        } catch (SQLException throwables) {
            dataCallback.onFailure();
            throwables.printStackTrace();
        }
    }

    /**
     * 设置某篇文章为已读
     * @param articleBrief
     * @throws RemoteException
     */
    @Override
    public void readArticle(ArticleBrief articleBrief) throws RemoteException {
        try {
            dataService.readArticle(articleBrief);
            dataCallback.onSuccess();
        } catch (SQLException throwables) {
            dataCallback.onFailure();
            throwables.printStackTrace();
        }
    }

    /**
     * 根据频道的rssLink查询其上次的推送时间
     * @param rssLink
     * @return
     * @throws RemoteException
     */
    @Override
    public String getChannelDateByRssLink(String rssLink) throws RemoteException {
        try {
            String res = dataService.getChannelDateByRssLink(rssLink);
            dataCallback.onSuccess();
            return res;
        } catch (SQLException throwables) {
            dataCallback.onFailure();
            throwables.printStackTrace();
            return null;
        }

    }

    /**
     * 根据频道的rssLink更新其推送时间
     * @param rssLink
     * @param lastBuildDate
     * @throws RemoteException
     */
    @Override
    public void updateChannelDateByRssLink(String rssLink, String lastBuildDate) throws RemoteException {
        try {
            dataService.updateChannelDateByRssLink(rssLink,lastBuildDate);
            dataCallback.onSuccess();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            dataCallback.onFailure();
        }
    }

    /**
     * 获取某源频道的所有已存文章简介，以日期的降序
     * @param resChannel
     * @param offset
     * @param limit
     * @return
     * @throws RemoteException
     */
    @Override
    public List<ArticleBrief> getArticleBriefsFromChannel(Channel resChannel, int offset, int limit) throws RemoteException {
        return dataService.getArticleBriefsFromChannel(resChannel,offset,limit);
    }

    /**
     * 获取文章简介对应的文章内容
     * @param articleBrief
     * @return
     * @throws RemoteException
     */
    @Override
    public String getContentOfArticleBrief(ArticleBrief articleBrief) throws RemoteException {
        try {
            String res = dataService.getContentOfArticleBrief(articleBrief);
            dataCallback.onSuccess();
            return res;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            dataCallback.onFailure();
            return null;
        }
    }

    /**
     * 获取目标文章简介所在的源频道对象
     * @param articleBrief
     * @return
     * @throws RemoteException
     */
    @Override
    public Channel getChannelOfArticle(ArticleBrief articleBrief) throws RemoteException {
        return dataService.getChannelOfArticle(articleBrief);
    }

    /**
     * 获取收藏
     * @param offset
     * @param limit
     * @return
     * @throws RemoteException
     */
    @Override
    public List<Collection> getCollection(int offset, int limit) throws RemoteException {
        return dataService.getCollection(offset,limit);
    }

    /**
     * 根据文章简介判断某文章是否被收藏
     * @param articleBrief
     * @return
     * @throws RemoteException
     */
    @Override
    public boolean isCollect(ArticleBrief articleBrief) throws RemoteException {
        return dataService.isCollect(articleBrief);
    }

    /**
     * 取消收藏
     * @param collection
     * @throws RemoteException
     */
    @Override
    public void removeCollection(Collection collection) throws RemoteException {
        try {
            dataService.removeCollection(collection);
            dataCallback.onSuccess();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            dataCallback.onFailure();
        }
    }

    /**
     * 取消订阅某频道
     * @param channel
     * @throws RemoteException
     */
    @Override
    public void removeChannel(Channel channel) throws RemoteException {
        dataService.removeChannel(channel);
    }

    /**
     * 清除缓存的文章
     * @throws RemoteException
     */
    @Override
    public void clearStorage() throws RemoteException {
        dataService.clearStorage();
    }


}
