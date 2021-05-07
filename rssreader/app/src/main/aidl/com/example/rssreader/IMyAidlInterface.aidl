// IMyAidlInterface.aidl
package com.example.rssreader;

import com.example.rssreader.model.parse.DataCallback;

import com.example.rssreader.model.datamodel.Channel;
import com.example.rssreader.model.datamodel.ArticleBrief;
import com.example.rssreader.model.datamodel.GlobalComment;
import com.example.rssreader.model.datamodel.LocalComment;
import com.example.rssreader.model.datamodel.AidlDate;

// Declare any non-default types here with import statements

interface IMyAidlInterface {

    void downloadParseXml(String url,in DataCallback dataCallback);

    List<Channel> getChannel(int offset, int limit);
    /**
    * 收藏文章
    **/
    void collectArticle(in ArticleBrief articleBrief,in DataCallback dataCallback);

    void addGlobalCommentToArticle(in ArticleBrief articleBrief,in String comment,in AidlDate aidlDate,in DataCallback dataCallback);

    void addLocalCommentToArticle(in ArticleBrief articleBrief,in String localContent,in String comment,in AidlDate aidlDate,in DataCallback dataCallback);

    void readArticle(in ArticleBrief articleBrief,in DataCallback dataCallback);

    String getChannelDateByRssLink(String rssLink,in DataCallback dataCallback);

    void updateChannelDateByRssLink(String rssLink, String lastBuildDate,in DataCallback dataCallback);

    List<ArticleBrief> getArticleBriefsFromChannel(in Channel resChannel, int offset, int limit);

    String getContentOfArticleBrief(in ArticleBrief articleBrief,in DataCallback dataCallback);

    Channel getChannelOfArticle(in ArticleBrief articleBrief,in DataCallback dataCallback);

    List<ArticleBrief> getCollection(int offset, int limit);

    boolean isCollect(in ArticleBrief articleBrief,in DataCallback dataCallback);

    List<ArticleBrief> searchCollection(String vagueTitle);

    List<GlobalComment> getGlobalCommentsOfArticle(in ArticleBrief articleBrief,in DataCallback dataCallback);

    List<LocalComment> getLocalCommentsOfArticle(in ArticleBrief articleBrief,in DataCallback dataCallback);

    void removeCollection(in ArticleBrief articleBrief,in DataCallback dataCallback);

    void removeChannel(in Channel channel);

    void deleteGlobalComment(in GlobalComment globalComment,in DataCallback dataCallback);

    void deleteLocalComment(in LocalComment localComment,in DataCallback dataCallback);

    void clearStorage();


}