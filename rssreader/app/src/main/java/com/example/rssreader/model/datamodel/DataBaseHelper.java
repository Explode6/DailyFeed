package com.example.rssreader.model.datamodel;


import org.litepal.LitePal;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * @ClassName： DataBaseHelper
 * @Author Von
 * @Date： 2021/4/24
 * @Description： 数据库使用的工具类
 */
public class DataBaseHelper {

    /**
     * 添加频道
     *
     * @param channel 待添加的频道类
     */
    public static void addChannel(Channel channel){
        //开启事务进行Channel的添加
        try {
            LitePal.beginTransaction();
            //查询该Channel是否已经存在
            List<Channel> channels = LitePal
                    .where("rssLink = ?", channel.getRssLink())
                    .find(Channel.class);
            //对新添加的Channel要设置其order值
            if(channels.isEmpty()){
                int length = LitePal.findAll(Channel.class).size();
                channel.setOrder(length);
                if(channel.save()) LitePal.setTransactionSuccessful();
            }
            //当channel的rssLink已存在，对其进行更新
            else{
                if(channel.update(channels.get(0).getId()) == 1)
                    LitePal.setTransactionSuccessful();
            }
        }finally {
            LitePal.endTransaction();
        }

    }

    /**
     * 向指定频道源中添加文章简介以及文章内容
     *
     * @param articleBrief 文章除内容外的简介内容
     * @param content      文章的内容
     * @param resChannel   文章的源频道
     * @throws SQLException 文章源频道不存在或不唯一
     */
    public static void addArticle(ArticleBrief articleBrief, String content, Channel resChannel) throws SQLException {
        //开启事务保证内部一致性
        try {
            LitePal.beginTransaction();

            //获取文章的来源channel对象
            List<Channel> channels = LitePal.where("rssLink = ?", resChannel.getRssLink())
                    .find(Channel.class);
            if(channels.isEmpty()) throw new SQLException("Channel 不存在");
            else if(channels.size() > 1){
                throw new SQLException("Channel 不唯一");
            }
            else {
                //将文章内容content封装成ArticleContent
                ArticleContent articleContent = new ArticleContent(content);
                //文章保存时要排除重复的可能性
                List<ArticleBrief> articleBriefs = LitePal.where("link = ?", articleBrief.getLink())
                        .find(ArticleBrief.class);
                if(!articleBriefs.isEmpty()){
                    //文章已存在, 如果是该源曾经被收藏的文章，重新链接上外键
                    if(articleBriefs.get(0).getCollect() && articleBriefs.get(0).getChannel_id() == -1){
                        articleBriefs.get(0).setChannel_id(channels.get(0).getId());
                        articleBriefs.get(0).setPubTime((int)System.currentTimeMillis()/(60*60*24*1000));
                        //操作成功，提交事务结果
                        if(articleBriefs.get(0).update(articleBriefs.get(0).getId()) == 1)
                            LitePal.setTransactionSuccessful();
                    }else{
                        //已存在的文章跳过
                        LitePal.setTransactionSuccessful();
                    }
                    return;
                }else {
                    //文章不存在, 添加
                    if(articleContent.save()){
                        articleBrief.setChannel_id(channels.get(0).getId());
                        articleBrief.setContent_id(articleContent.getId());
                        //以24小时为间隔
                        articleBrief.setPubTime((int)System.currentTimeMillis()/(60*60*24*1000));
                        //操作完成，提交事务
                        if(articleBrief.save()) LitePal.setTransactionSuccessful();
                        else throw new SQLException("操作失败");
                    }
                }
            }
        }finally {
            LitePal.endTransaction();
        }
    }

    /**
     * 通过文章简介收藏某篇文章
     *
     * @param articleBrief 对应的文章简介
     * @throws SQLException 文章简介对应的文章内容不存在
     */
    public static void collectArticle(ArticleBrief articleBrief) throws SQLException {
        try{
            LitePal.beginTransaction();
            //查表找到文章简介
            ArticleBrief articleBrief1 = LitePal.find(ArticleBrief.class, articleBrief.getId());
            if(articleBrief1 == null) throw new SQLException("对应的文章简介不存在");
            //更新对应的收藏标识
            articleBrief1.setCollect(true);
            if(articleBrief1.update(articleBrief.getId()) == 1)
                LitePal.setTransactionSuccessful();
            else throw new SQLException("操作失败");
        }finally {
            LitePal.endTransaction();
        }
    }

    /**
     * 添加文章的全局评论到数据库中
     *
     * @param articleBrief 对应的文章简介
     * @param comment 评论内容
     * @param date 评论时间
     * @throws SQLException 对应的文章简介不在数据库中
     */
    public static void addGlobalCommentToArticle(ArticleBrief articleBrief, String comment, Date date) throws SQLException {
        try{
            LitePal.beginTransaction();
            //查表确认对应的ArticleBrief
            ArticleBrief articleBrief1 = LitePal.find(ArticleBrief.class, articleBrief.getId());
            if(articleBrief1 == null) throw new SQLException("文章不存在");
            //封装评论添加到库中
            GlobalComment globalComment = new GlobalComment(articleBrief1.getId(), comment, date);
            if(globalComment.save()) LitePal.setTransactionSuccessful();
            else throw new SQLException("操作失败");
        }finally {
            LitePal.endTransaction();
        }
    }

    /**
     * 添加对文章的部分内容的局部评论到数据库中
     *
     * @param articleBrief 对应的文章简介
     * @param localContent 选中的文章部分内容
     * @param comment 评论的内容
     * @param date 评论时间
     * @throws SQLException 对应的文章简介不在数据库中
     */
    public static void addLocalCommentToArticle(ArticleBrief articleBrief,
                                                String localContent, String comment, Date date) throws SQLException{
        try{
            LitePal.beginTransaction();
            //查表确认对应的ArticleBrief
            ArticleBrief articleBrief1 = LitePal.find(ArticleBrief.class, articleBrief.getId());
            if(articleBrief1 == null) throw new SQLException("文章不存在");
            //封装评论添加到库中
            LocalComment localComment = new LocalComment(articleBrief1.getId(), localContent, comment, date);
            if(localComment.save()) LitePal.setTransactionSuccessful();
            else throw new SQLException("操作失败");
        }finally {
            LitePal.endTransaction();
        }
    }

    /**
     * 设置某篇文章为已读
     *
     * @param articleBrief 对应的文章简介
     * @throws SQLException 该文章不存在库中
     */
    public static void readArticle(ArticleBrief articleBrief) throws SQLException {
        try{
            LitePal.beginTransaction();
            //查询表中是否有对应的文章
            ArticleBrief articleBrief1 = LitePal.find(ArticleBrief.class, articleBrief.getId());
            if (articleBrief1==null) throw new SQLException("该文章不存在");
                //将库中对应的文章设置为已读
            else {
                articleBrief1.setRead(true);
                if(articleBrief1.update(articleBrief.getId()) == 1 )
                    LitePal.setTransactionSuccessful();
                else throw new SQLException("操作失败");
            }
        }finally {
            LitePal.endTransaction();
        }
    }

    /**
     * 设置某篇文章为未读
     *
     * @param articleBrief 对应的文章简介
     * @throws SQLException 该文章不在库中
     */
    public static void unreadArticle(ArticleBrief articleBrief) throws SQLException{
        try{
            LitePal.beginTransaction();
            //查询表中是否有对应的文章
            ArticleBrief articleBrief1 = LitePal.find(ArticleBrief.class, articleBrief.getId());
            if (articleBrief1==null) throw new SQLException("该文章不存在");
                //将库中对应的文章设置为未读
            else{
                articleBrief1.setToDefault("isRead");
                if(articleBrief1.update(articleBrief.getId()) == 1)
                    LitePal.setTransactionSuccessful();
                else throw new SQLException("操作失败");
            }
        }finally {
            LitePal.endTransaction();
        }
    }

    /**
     * 获取频道的列表
     *
     * @param offset 查询偏移量
     * @param limit 查询返回的最大数目
     * @return the list<Channel>
     */
    public static List<Channel> getChannel(int offset, int limit){
        return LitePal
                .order("order")
                .offset(offset)
                .limit(limit)
                .find(Channel.class);
    }

    /**
     * 根据频道的rssLink查询其上次的推送时间
     *
     * @param rssLink 频道的rssLink
     * @return String 返回频道的上次推送时间
     * @throws SQLException 频道rssLink不存在库中
     */
    public static String getChannelDateByRssLink(String rssLink) throws SQLException {
        try{
            LitePal.beginTransaction();
            List<Channel> channels = LitePal
                    .select("lastBuildDate")
                    .where("rssLink = ?", rssLink)
                    .find(Channel.class);
            if (channels.isEmpty()) throw new SQLException("不存在该频道");
            else{
                LitePal.setTransactionSuccessful();
                return channels.get(0).getLastBuildDate();
            }
        }finally {
            LitePal.endTransaction();
        }
    }

    /**
     * 根据频道的rssLink更新其推送时间
     *
     * @param rssLink 频道的rssLink
     * @param lastBuildDate 新的推送时间
     * @throws SQLException 频道rssLink不存在库中
     */
    public static void updateChannelDateByRssLink(String rssLink, String lastBuildDate) throws SQLException {
        try{
            LitePal.beginTransaction();
            List<Channel> channels = LitePal
                    .where("rssLink = ?", rssLink)
                    .find(Channel.class);
            if (channels.isEmpty()) throw new SQLException("不存在该频道");
            else {
                channels.get(0).setLastBuildDate(lastBuildDate);
                if(channels.get(0).update(channels.get(0).getId()) == 1)
                    LitePal.setTransactionSuccessful();
                else throw new SQLException("操作失败");
            }
        }finally {
            LitePal.endTransaction();
        }
    }

    /**
     * 获取某源频道的所有已存文章简介，以日期的降序
     *
     * @param resChannel 源频道对象
     * @param offset 查询偏移量
     * @param limit 查询返回的最大数目
     * @return the list<ArticleBrief>
     */
    public static List<ArticleBrief> getArticleBriefsFromChannel(Channel resChannel, int offset, int limit){
        //更新日期降序，最后按id升序返回对应channel的ArticleBrief列表
        return LitePal.where("channel_id = ?", Integer.toString(resChannel.getId()))
                .order("pubTime desc, id")
                .offset(offset)
                .limit(limit)
                .find(ArticleBrief.class);
    }

    /**
     * 获取文章简介对应的文章内容
     *
     * @param articleBrief 文章简介
     * @return String 文章内容
     * @throws SQLException 对应文章内容不存在
     */
    public static String getContentOfArticleBrief(ArticleBrief articleBrief) throws SQLException {
        try{
            LitePal.beginTransaction();
            ArticleContent articleContent = LitePal.find(ArticleContent.class, articleBrief.getContent_id());
            if(articleContent==null) throw new SQLException("文章内容不存在");
            else return articleContent.getContent();
        }finally {
            LitePal.setTransactionSuccessful();
            LitePal.endTransaction();
        }
    }

    /**
     * 获取目标文章简介所在的源频道对象
     *
     * @param articleBrief 文章简介
     * @return Channel 源频道对象
     * @throws SQLException 对应的文章简介不存在
     */
    public static Channel getChannelOfArticle(ArticleBrief articleBrief) throws SQLException {
        try{
            LitePal.beginTransaction();
            //查询对应库中的文章简介
            ArticleBrief articleBrief1 = LitePal.find(ArticleBrief.class, articleBrief.getId());
            if(articleBrief1 == null ) throw new SQLException("对应文章简介不存在");
            if(articleBrief1.getChannel_id() == -1) return null;
            return LitePal.find(Channel.class, articleBrief.getChannel_id());
        }finally {
            LitePal.setTransactionSuccessful();
            LitePal.endTransaction();
        }
    }

    /**
     * 获取收藏
     *
     * @param offset 查询偏移量
     * @param limit 查询返回的最大数目
     * @return list<ArticleBrief>
     */
    public static List<ArticleBrief> getCollection(int offset, int limit){
        return LitePal.offset(offset)
                .limit(limit)
                .where("isCollect = 1")
                .find(ArticleBrief.class);
    }


    /**
     * 根据模糊的文章标题查询收藏
     *
     * @param vagueTitle 部分标题
     * @return the List<ArticleBrief>
     */
    public static List<ArticleBrief> searchCollection(String vagueTitle){
        return LitePal.where("title like ? and isCollect = 1", "%"+vagueTitle+"%")
                .find(ArticleBrief.class);
    }

    /**
     * 查找对应文章的所有全局评论
     *
     * @param articleBrief 对应文章简介
     * @return the List<GlobalComment>
     * @throws SQLException 对应的文章简介不存在
     */
    public static List<GlobalComment> getGlobalCommentsOfArticle(ArticleBrief articleBrief) throws SQLException {
        try{
            LitePal.beginTransaction();
            //查表确认对应的ArticleBrief
            ArticleBrief articleBrief1 = LitePal.find(ArticleBrief.class, articleBrief.getId());
            if(articleBrief1 == null) throw new SQLException("文章不存在");
            return LitePal.where("articleBrief_id = ?", Integer.toString(articleBrief1.getId()))
                    .find(GlobalComment.class);
        }finally {
            LitePal.setTransactionSuccessful();
            LitePal.endTransaction();
        }
    }

    /**
     * 返回对应文章的所有局部评论
     *
     * @param articleBrief 对应的文章简介
     * @return the List<LocalComment>
     * @throws SQLException 对应的文章简介不存在
     */
    public static List<LocalComment> getLocalCommentsOfArticle(ArticleBrief articleBrief) throws SQLException{
        try{
            LitePal.beginTransaction();
            //查表确认对应的ArticleBrief
            ArticleBrief articleBrief1 = LitePal.find(ArticleBrief.class, articleBrief.getId());
            if(articleBrief1 == null) throw new SQLException("文章不存在");
            return LitePal.where("articleBrief_id = ?", Integer.toString(articleBrief1.getId()))
                    .find(LocalComment.class);
        }finally {
            LitePal.setTransactionSuccessful();
            LitePal.endTransaction();
        }
    }


    /**
     * 取消收藏
     *
     * @param articleBrief 目标文章简介
     * @throws SQLException 删除出现错误
     */
    public static void removeCollection(ArticleBrief articleBrief) throws SQLException {
        try{
            LitePal.beginTransaction();
            //查找库中对应的文章简介
            ArticleBrief articleBrief1 = LitePal.find(ArticleBrief.class, articleBrief.getId());
            if(articleBrief1 == null) throw new SQLException("对应文章简介不存在");
            if(articleBrief1.getChannel_id() < 0){
                //清除channel已经被取消订阅的ArticleBrief对应的内容和评论
                LitePal.delete(ArticleContent.class, articleBrief1.getContent_id());
                LitePal.deleteAll(GlobalComment.class, "articleBrief_id = ?", Integer.toString(articleBrief1.getId()));
                LitePal.deleteAll(LocalComment.class, "articleBrief_id = ?", Integer.toString(articleBrief1.getId()));
                LitePal.delete(ArticleBrief.class, articleBrief1.getId());
            }else {
                //取消收藏
                articleBrief1.setToDefault("isCollect");
                articleBrief1.update(articleBrief.getId());
            }
        }finally {
            LitePal.setTransactionSuccessful();
            LitePal.endTransaction();
        }
    }

    /**
     * 取消订阅某频道
     *
     * @param channel 目标频道对象
     */
    public static void removeChannel(Channel channel){
        try {
            LitePal.beginTransaction();
            //找到对应频道的所有未被收藏的文章简介
            List<ArticleBrief> articleBriefs = LitePal.select("id","content_id")
                    .where("channel_id = ? and isCollect = 0", Integer.toString(channel.getId()))
                    .find(ArticleBrief.class);
            //初始化对应频道被收藏文章的channelId，保持数据库一致性
            ArticleBrief articleBrief1 = new ArticleBrief();
            articleBrief1.setChannel_id(-1);
            articleBrief1.updateAll("channel_id = ? and isCollect = 1", Integer.toString(channel.getId()));
            //清除未被收藏的文章
            for(ArticleBrief articleBrief: articleBriefs){
                //删除对应文章内容
                LitePal.delete(ArticleContent.class, articleBrief.getContent_id());
                //删除对应文章的评论
                LitePal.deleteAll(GlobalComment.class, "articleBrief_id = ?", Integer.toString(articleBrief.getId()));
                LitePal.deleteAll(LocalComment.class, "articleBrief_id = ?", Integer.toString(articleBrief.getId()));
                //删除对应简介
                LitePal.delete(ArticleBrief.class, articleBrief.getId());
            }
            //处理order出现的断层
            String index = Integer.toString(channel.getOrder());
            List<Channel> channelsOrders = LitePal.where("order > ?", index).find(Channel.class);
            for(Channel channelOrder : channelsOrders){
                channelOrder.setOrder(channelOrder.getOrder()-1);
                channelOrder.update(channelOrder.getId());
            }
            //删除频道
            LitePal.delete(Channel.class, channel.getId());
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            LitePal.setTransactionSuccessful();
            LitePal.endTransaction();
        }
    }

    /**
     * 删除某个全局评论
     *
     * @param globalComment 对应的全局评论
     * @throws SQLException 对应的全局评论不存在
     */
    public static void deleteGlobalComment(GlobalComment globalComment) throws SQLException {
        try{
            LitePal.beginTransaction();
            //查表确认对应的GlobalComment
            GlobalComment globalComment1 = LitePal.find(GlobalComment.class, globalComment.getId());
            if(globalComment1 == null) throw new SQLException("该评论不存在");
            //删除全局评论
            if(LitePal.delete(GlobalComment.class, globalComment1.getId()) == 1)
                LitePal.setTransactionSuccessful();
            else throw new SQLException("操作失败");
        }finally {
            LitePal.endTransaction();
        }
    }

    /**
     * 删除某个局部评论
     *
     * @param localComment 对应的局部评论
     * @throws SQLException 对应的局部评论不存在
     */
    public static void deleteLocalComment(LocalComment localComment)throws SQLException{
        try{
            LitePal.beginTransaction();
            //查表确认对应的LocalComment
            LocalComment localComment1 = LitePal.find(LocalComment.class, localComment.getId());
            if(localComment1 == null) throw new SQLException("该评论不存在");
            //删除局部评论
            if(LitePal.delete(LocalComment.class, localComment1.getId()) == 1)
                LitePal.setTransactionSuccessful();
            else throw new SQLException("操作失败");
        }finally {
            LitePal.endTransaction();
        }
    }

    /**
     * 清除未被收藏的缓存文章
     */
    public static void clearStorage(){
        try {
            LitePal.beginTransaction();
            //清除所有未收藏的文章及其对应的评论
            List<ArticleBrief> articleBriefs = LitePal.where("isCollect = 0").find(ArticleBrief.class);
            for (ArticleBrief articleBrief:articleBriefs){
                //清除内容
                LitePal.delete(ArticleContent.class,articleBrief.getContent_id());
                //清除评论
                LitePal.deleteAll(GlobalComment.class, "articleBrief_id = ?", Integer.toString(articleBrief.getId()));
                LitePal.deleteAll(LocalComment.class, "articleBrief_id = ?", Integer.toString(articleBrief.getId()));
                //清除简介
                LitePal.delete(ArticleBrief.class,articleBrief.getId());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        finally {
            LitePal.setTransactionSuccessful();
            LitePal.endTransaction();
        }
    }
}
