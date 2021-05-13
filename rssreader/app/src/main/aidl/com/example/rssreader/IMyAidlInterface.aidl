// IMyAidlInterface.aidl
package com.example.rssreader;

import com.example.rssreader.model.parse.DataCallback;
import com.example.rssreader.model.parse.XmlCallback;

import com.example.rssreader.model.datamodel.Channel;
import com.example.rssreader.model.datamodel.ArticleBrief;
import com.example.rssreader.model.datamodel.GlobalComment;
import com.example.rssreader.model.datamodel.LocalComment;
import com.example.rssreader.model.datamodel.AidlDate;

import android.os.ParcelFileDescriptor;

// Declare any non-default types here with import statements

interface IMyAidlInterface {

    void updateSource(int offset,int limit);

    void updateChannels(in List<Channel> channels);

    void downloadParseXml(String url,in XmlCallback xmlCallback);

    List<Channel> getChannel(int offset, int limit);

    ParcelFileDescriptor getChannel2(int offset,int limit);

    void collectArticle(in ArticleBrief articleBrief,in DataCallback dataCallback);

    void addGlobalCommentToArticle(in ArticleBrief articleBrief,in String comment,in AidlDate aidlDate,in DataCallback dataCallback);

    void addLocalCommentToArticle(in ArticleBrief articleBrief,in String localContent,in String comment,in AidlDate aidlDate,in DataCallback dataCallback);

    void readArticle(in ArticleBrief articleBrief,in DataCallback dataCallback);

    void unreadArticle(in ArticleBrief articleBrief,in DataCallback dataCallback);

    String getChannelDateByRssLink(String rssLink,in DataCallback dataCallback);

    void updateChannelDateByRssLink(String rssLink, String lastBuildDate,in DataCallback dataCallback);

    List<ArticleBrief> getArticleBriefsFromChannel(in Channel resChannel, int offset, int limit);

    String getContentOfArticleBrief(in ArticleBrief articleBrief,in DataCallback dataCallback);

    Channel getChannelOfArticle(in ArticleBrief articleBrief,in DataCallback dataCallback);

    List<ArticleBrief> getCollection(int offset, int limit);

    List<ArticleBrief> searchCollection(String vagueTitle);

    List<GlobalComment> getGlobalCommentsOfArticle(in ArticleBrief articleBrief,in DataCallback dataCallback);

    List<LocalComment> getLocalCommentsOfArticle(in ArticleBrief articleBrief,in DataCallback dataCallback);

    void removeCollection(in ArticleBrief articleBrief,in DataCallback dataCallback);

    void removeChannel(in Channel channel);

    void deleteGlobalComment(in GlobalComment globalComment,in DataCallback dataCallback);

    void deleteLocalComment(in LocalComment localComment,in DataCallback dataCallback);

    void clearStorage();

}