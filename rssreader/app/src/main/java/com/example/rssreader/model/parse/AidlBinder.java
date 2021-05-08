package com.example.rssreader.model.parse;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import com.example.rssreader.IMyAidlInterface;
import com.example.rssreader.model.datamodel.AidlDate;
import com.example.rssreader.model.datamodel.ArticleBrief;
import com.example.rssreader.model.datamodel.Channel;
import com.example.rssreader.model.datamodel.DataBaseHelper;
import com.example.rssreader.model.datamodel.GlobalComment;
import com.example.rssreader.model.datamodel.LocalComment;

import java.sql.SQLException;
import java.util.List;

/**
 * @ClassName： AidlBinder
 * @Author SH
 * @Date： 2021/4/29
 * @Description： 获得AidlBinder类后，可以调用类中的方法控制DataService。
 */
public class AidlBinder extends IMyAidlInterface.Stub {
    private DataService dataService;

    public static IMyAidlInterface aidlBinder =null;

    /**
     * 绑定服务，由第一个activity调用。
     * 其中conn为回调函数接口，绑定成功后会在接口中的onServiceConnected函数内回调。
     * @param conn 回调函数接口
     * @param context 传入应用的上下文
     */
    public static void bindService(ServiceConnection conn, Context context){
        if(aidlBinder==null){
            Intent intent = new Intent(context.getApplicationContext(),DataService.class);
            context.bindService(intent,conn,Context.BIND_AUTO_CREATE);
        }
    }

    /**
     * 设置Binder实例
     * @param myAidlInterface 被设置的实例
     */
    public static void setInstance(IMyAidlInterface myAidlInterface){
        aidlBinder = myAidlInterface;
    }

    /**
     * 获取Binder实例
     * @return Binder实例
     */
    public static IMyAidlInterface getInstance(){
        return aidlBinder;
    }



    public AidlBinder(DataService service){
        this.dataService = service;
    }


    /**
     * 下载解析Xml，解析后数据会进入数据库
     * @param url
     * @param dataCallback
     * @throws RemoteException
     */
    @Override
    public void downloadParseXml(String url,DataCallback dataCallback) throws RemoteException {
        dataService.parseXml(url,dataCallback);
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
     * @param dataCallback
     * @throws RemoteException
     */
    @Override
    public void collectArticle(ArticleBrief articleBrief,DataCallback dataCallback) throws RemoteException {
        try {
            dataService.collectArticle(articleBrief);
            dataCallback.onSuccess();
        } catch (SQLException throwables) {
            dataCallback.onFailure();
            throwables.printStackTrace();
        }
    }

    /**
     * 添加文章的全局评论到数据库中
     * @param articleBrief
     * @param comment
     * @param aidlDate
     * @param dataCallback
     * @throws RemoteException
     */
    @Override
    public void addGlobalCommentToArticle(ArticleBrief articleBrief, String comment, AidlDate aidlDate,DataCallback dataCallback) throws RemoteException {
        try{
            dataService.addGlobalCommentToArticle(articleBrief,comment,aidlDate);
            dataCallback.onSuccess();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            dataCallback.onFailure();
        }
    }

    /**
     * 添加对文章的部分内容的局部评论到数据库中
     * @param articleBrief
     * @param localContent
     * @param comment
     * @param aidlDate
     * @param dataCallback
     * @throws RemoteException
     */
    @Override
    public void addLocalCommentToArticle(ArticleBrief articleBrief, String localContent, String comment, AidlDate aidlDate,DataCallback dataCallback) throws RemoteException {
        try {
            dataService.addLocalCommentToArticle(articleBrief,localContent,comment,aidlDate);
            dataCallback.onSuccess();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            dataCallback.onFailure();
        }
    }

    /**
     * 设置某篇文章为已读
     * @param articleBrief
     * @param dataCallback
     * @throws RemoteException
     */
    @Override
    public void readArticle(ArticleBrief articleBrief,DataCallback dataCallback) throws RemoteException {
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
     * @param dataCallback
     * @return
     * @throws RemoteException
     */
    @Override
    public String getChannelDateByRssLink(String rssLink,DataCallback dataCallback) throws RemoteException {
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
     * @param dataCallback
     * @throws RemoteException
     */
    @Override
    public void updateChannelDateByRssLink(String rssLink, String lastBuildDate,DataCallback dataCallback) throws RemoteException {
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
     * @param dataCallback
     * @return
     * @throws RemoteException
     */
    @Override
    public String getContentOfArticleBrief(ArticleBrief articleBrief,DataCallback dataCallback) throws RemoteException {
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
    public Channel getChannelOfArticle(ArticleBrief articleBrief,DataCallback dataCallback) throws RemoteException {
        try {
            Channel res = dataService.getChannelOfArticle(articleBrief);
            dataCallback.onSuccess();
            return res;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            dataCallback.onFailure();
            return null;
        }
    }

    /**
     * 获取收藏
     * @param offset
     * @param limit
     * @return
     * @throws RemoteException
     */
    @Override
    public List<ArticleBrief> getCollection(int offset, int limit) throws RemoteException {
        return dataService.getCollection(offset,limit);
    }

    /**
     * 根据文章简介判断某文章是否被收藏
     * @param articleBrief
     * @return 是否收藏
     * @throws RemoteException
     */
    @Override
    public boolean isCollect(ArticleBrief articleBrief,DataCallback dataCallback) throws RemoteException {
        try {
            boolean res = dataService.isCollect(articleBrief);
            dataCallback.onSuccess();
            return res;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            dataCallback.onFailure();
            return false;
        }
    }

    /**
     * 根据模糊的文章标题查询收藏
     *
     * @param vagueTitle 部分标题
     * @return the List<ArticleBrief>
     */
    @Override
    public List<ArticleBrief> searchCollection(String vagueTitle) throws RemoteException {
        return dataService.searchCollection(vagueTitle);
    }

    /**
     * 返回对应文章的所有局部评论
     *
     * @param articleBrief 对应的文章简介
     * @param dataCallback 回调
     * @return the List<LocalComment>
     * @throws RemoteException
     */
    @Override
    public List<GlobalComment> getGlobalCommentsOfArticle(ArticleBrief articleBrief,DataCallback dataCallback) throws RemoteException {
        try {
            List<GlobalComment> globalComments =  dataService.getGlobalCommentsOfArticle(articleBrief);
            dataCallback.onSuccess();
            return globalComments;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            dataCallback.onFailure();
            return null;
        }
    }

    /**
     * 返回对应文章的所有局部评论
     *
     * @param articleBrief 对应的文章简介
     * @return the List<LocalComment>
     * @throws RemoteException 对应的文章简介不存在
     */
    @Override
    public List<LocalComment> getLocalCommentsOfArticle(ArticleBrief articleBrief,DataCallback dataCallback) throws RemoteException {
        try {
            List<LocalComment> comments = dataService.getLocalCommentsOfArticle(articleBrief);
            dataCallback.onSuccess();
            return comments;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            dataCallback.onFailure();
            return null;
        }

    }


    /**
     * 取消收藏
     * @param articleBrief
     * @param dataCallback
     * @throws RemoteException
     */
    @Override
    public void removeCollection(ArticleBrief articleBrief,DataCallback dataCallback) throws RemoteException {
        try {
            dataService.removeCollection(articleBrief);
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
     * 删除某个全局评论
     * @param globalComment
     * @param dataCallback
     * @throws RemoteException
     */
    @Override
    public void deleteGlobalComment(GlobalComment globalComment,DataCallback dataCallback) throws RemoteException {
        try {
            dataService.deleteGlobalComment(globalComment);
            dataCallback.onSuccess();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            dataCallback.onFailure();
        }
    }

    /**
     * 删除某个局部评论
     * @param localComment
     * @param dataCallback
     * @throws RemoteException
     */
    @Override
    public void deleteLocalComment(LocalComment localComment,DataCallback dataCallback) throws RemoteException {
        try {
            dataService.deleteLocalComment(localComment);
            dataCallback.onSuccess();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            dataCallback.onFailure();
        }
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
