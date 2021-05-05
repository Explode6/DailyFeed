// IMyAidlInterface.aidl
package com.example.rssreader;

import com.example.rssreader.model.parse.DataCallback;

import com.example.rssreader.model.datamodel.Channel;
import com.example.rssreader.model.datamodel.ArticleBrief;
import com.example.rssreader.model.datamodel.Collection;

// Declare any non-default types here with import statements

interface IMyAidlInterface {

    void downloadParseXml(String url,in DataCallback dataCallback);

    List<Channel> getChannel(int offset, int limit);
    /**
    * 收藏文章
    **/
    void collectArticle(in ArticleBrief articleBrief,in DataCallback dataCallback);

    void readArticle(in ArticleBrief articleBrief,in DataCallback dataCallback);

    String getChannelDateByRssLink(String rssLink,in DataCallback dataCallback);

    void updateChannelDateByRssLink(String rssLink, String lastBuildDate,in DataCallback dataCallback);

    List<ArticleBrief> getArticleBriefsFromChannel(in Channel resChannel, int offset, int limit);

    String getContentOfArticleBrief(in ArticleBrief articleBrief,in DataCallback dataCallback);

    Channel getChannelOfArticle(in ArticleBrief articleBrief);

    List<Collection> getCollection(int offset, int limit);

    boolean isCollect(in ArticleBrief articleBrief);

    void removeCollection(in Collection collection,in DataCallback dataCallback);

    void removeChannel(in Channel channel);

    void clearStorage();


}