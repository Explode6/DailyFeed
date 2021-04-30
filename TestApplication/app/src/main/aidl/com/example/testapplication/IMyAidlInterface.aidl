// IMyAidlInterface.aidl
package com.example.testapplication;

import com.example.testapplication.parse.DataCallback;

import com.example.testapplication.datamodel.Channel;
import com.example.testapplication.datamodel.ArticleBrief;
import com.example.testapplication.datamodel.Collection;

// Declare any non-default types here with import statements

interface IMyAidlInterface {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void downloadParseXml(String url);

    void registerCallback(in DataCallback dataCallback);

    List<Channel> getChannel(int offset, int limit);
    /**
    * 收藏文章
    **/
    void collectArticle(in ArticleBrief articleBrief);

    void readArticle(in ArticleBrief articleBrief);

    String getChannelDateByRssLink(String rssLink);

    void updateChannelDateByRssLink(String rssLink, String lastBuildDate);

    List<ArticleBrief> getArticleBriefsFromChannel(in Channel resChannel, int offset, int limit);

    String getContentOfArticleBrief(in ArticleBrief articleBrief);

    Channel getChannelOfArticle(in ArticleBrief articleBrief);

    List<Collection> getCollection(int offset, int limit);

    boolean isCollect(in ArticleBrief articleBrief);

    void removeCollection(in Collection collection);

    void removeChannel(in Channel channel);

    void clearStorage();


}