package com.example.rssreader.model.parse;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import com.example.rssreader.model.datamodel.ArticleBrief;
import com.example.rssreader.model.datamodel.Channel;
import com.example.rssreader.model.datamodel.DataBaseHelper;

import java.sql.SQLException;
import java.util.List;

public class DataService extends Service {

    //添加AidlBinder
    AidlBinder aidlBinder = new AidlBinder(this);


    /**
     * 获取频道的列表
     *
     * @param offset 查询偏移量
     * @param limit 查询返回的最大数目
     * @return the list<Channel>
     */
    public List<Channel> getChannel(int offset, int limit){
        return DataBaseHelper.getChannel(offset,limit);
    }

    /**
     * 通过文字简介收藏某篇文章
     *
     * @param articleBrief 文章简介
     * @throws SQLException 文章简介对应的文章内容不存在
     */
    public void collectArticle(ArticleBrief articleBrief) throws SQLException {
        DataBaseHelper.collectArticle(articleBrief);
    }
    /**
     * 设置某篇文章为已读
     *
     * @param articleBrief 文章简介
     * @throws SQLException 该文章不存在库中
     */
    public void readArticle(ArticleBrief articleBrief) throws SQLException {
        DataBaseHelper.readArticle(articleBrief);
    }

    /**
     * 根据频道的rssLink查询其上次的推送时间
     *
     * @param rssLink 频道的rssLink
     * @return String 返回频道的上次推送时间
     * @throws SQLException 频道rssLink不存在库中
     */
    public String getChannelDateByRssLink(String rssLink) throws SQLException {
        return DataBaseHelper.getChannelDateByRssLink(rssLink);
    }

    /**
     * 根据频道的rssLink更新其推送时间
     *
     * @param rssLink 频道的rssLink
     * @param lastBuildDate 新的推送时间
     * @throws SQLException 频道rssLink不存在库中
     */
    public void updateChannelDateByRssLink(String rssLink, String lastBuildDate) throws SQLException {
        DataBaseHelper.updateChannelDateByRssLink(rssLink,lastBuildDate);
    }

    /**
     * 获取某源频道的所有已存文章简介，以日期的降序
     *
     * @param resChannel 源频道对象
     * @param offset 查询偏移量
     * @param limit 查询返回的最大数目
     * @return the list<ArticleBrief>
     */
    public List<ArticleBrief> getArticleBriefsFromChannel(Channel resChannel, int offset, int limit){
        List<ArticleBrief> articleBriefs = DataBaseHelper.getArticleBriefsFromChannel(resChannel,offset,limit);
        return articleBriefs;
    }

    /**
     * 获取文章简介对应的文章内容
     *
     * @param articleBrief 文章简介
     * @return String 文章内容
     * @throws SQLException 对应文章内容不存在
     */
    public String getContentOfArticleBrief(ArticleBrief articleBrief) throws SQLException {
        return DataBaseHelper.getContentOfArticleBrief(articleBrief);
    }

    /**
     * 获取目标文章简介所在的源频道对象
     *
     * @param articleBrief 文章简介
     * @return Channel 源频道对象
     */
    public Channel getChannelOfArticle(ArticleBrief articleBrief) throws SQLException {
        return DataBaseHelper.getChannelOfArticle(articleBrief);
    }

    /**
     * 获取收藏
     *
     * @param offset 查询偏移量
     * @param limit 查询返回的最大数目
     * @return list<Collection>
     */


    /**
     * 根据文章简介判断某文章是否被收藏
     *
     * @param articleBrief 文章简介
     * @return boolean
     */
    public boolean isCollect(ArticleBrief articleBrief) throws SQLException {
        return DataBaseHelper.isCollect(articleBrief);
    }

    /**
     * 取消收藏
     *
     * @param collection 目标文章
     * @throws SQLException 删除出现错误
     */

    /**
     * 取消订阅某频道
     *
     * @param channel 目标频道对象
     */
    public void removeChannel(Channel channel){
        DataBaseHelper.removeChannel(channel);
    }

    /**
     * 清除缓存的文章
     */
    public void clearStorage(){
        DataBaseHelper.clearStorage();
    }


    /**
     * 自定义解析线程
     */
    private class parseXmlThread implements Runnable{
        private String url;

        private DataCallback dataCallback;

        parseXmlThread(String url,DataCallback dataCallback){
            this.url = url;
            this.dataCallback = dataCallback;
        }
        @Override
        public void run() {

            try {
                new XmlHandler(url).startParse(dataCallback);
            } catch (RemoteException e) {
                e.printStackTrace();
            }


        }
    }

    /**
     * 下载解析Xml
     * @param url
     */
    public void parseXml(String url,DataCallback dataCallback){
        new Thread(new parseXmlThread(url,dataCallback)).start();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return aidlBinder;
    }


}