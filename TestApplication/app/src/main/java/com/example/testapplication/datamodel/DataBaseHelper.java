package com.example.testapplication.datamodel;

import org.litepal.LitePal;

import java.sql.SQLException;
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
        //当channel的rssLink已存在，不会对数据库有改变
        channel.save();
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
        //获取文章的来源channel对象
        List<Channel> channels = LitePal.where("rssLink = ?", resChannel.getRssLink())
                .find(Channel.class);
        if(channels.size() != 1){
            throw new SQLException("Channel 不唯一");
        }
        else {
            //将文章内容content封装成ArticleContent
            ArticleContent articleContent = new ArticleContent(content);
            //文章保存时要排除重复的可能性，应以最后插入的内容为准
            List<ArticleBrief> articleBriefs = LitePal.where("link = ?", articleBrief.getLink())
                                                        .find(ArticleBrief.class);
            if(!articleBriefs.isEmpty()){
                articleBrief.setContent_id(articleBriefs.get(0).getContent_id());
                articleBrief.setChannel_id(articleBriefs.get(0).getChannel_id());
                articleContent.update(articleBrief.getContent_id());
                LitePal.delete(ArticleBrief.class, articleBriefs.get(0).getId());
            }else {
                articleContent.save();
                articleBrief.setChannel_id(channels.get(0).getId());
                articleBrief.setContent_id(articleContent.getId());
            }
            articleBrief.save();
            //同步收藏表中的内容
            if(LitePal.where("link = ?", articleBrief.getLink())
                    .find(Collection.class)
                    .isEmpty())
                return;
            LitePal.deleteAll("link = ?",articleBrief.getLink());
            Collection collection = new Collection(articleBrief, content);
            collection.save();
        }
    }

    /**
     * 通过文字简介收藏某篇文章
     *
     * @param articleBrief 文章简介
     * @throws SQLException 文章简介对应的文章内容不存在
     */
    public static void collectArticle(ArticleBrief articleBrief) throws SQLException {
        //查表找到文章的内容
        ArticleContent articleContent = LitePal.find(ArticleContent.class, articleBrief.getContent_id());
        if(articleContent == null) throw new SQLException("文章内容不存在");
        String content  = articleContent.getContent();
        //构造收藏类对象
        Collection collection = new Collection(articleBrief, content);
        //将收藏类保存入库
        collection.save();
    }

    /**
     * 设置某篇文章为已读
     *
     * @param articleBrief 文章简介
     */
    public static void readArticle(ArticleBrief articleBrief){
        //将库中对应的文章设置为已读
        articleBrief.setRead(true);
        articleBrief.update(articleBrief.getId());
    }

    /**
     * 获取频道的列表
     *
     * @param offset 查询偏移量
     * @param limit 查询返回的最大数目
     * @return the list<Channel>
     */
    public static List<Channel> getChannel(int offset, int limit){
        return LitePal.offset(offset)
                .limit(limit)
                .find(Channel.class);
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
        //按日期降序返回对应channel的ArticleBrief列表
        return LitePal.where("channel_id = ?", Integer.toString(resChannel.getId()))
                .order("date desc")
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
        ArticleContent articleContent = LitePal.find(ArticleContent.class, articleBrief.getContent_id());
        if(articleContent==null) throw new SQLException("文章内容不存在");
        return articleContent.getContent();
    }

    /**
     * 获取目标文章简介所在的源频道对象
     *
     * @param articleBrief 文章简介
     * @return Channel 源频道对象
     */
    public static Channel getChannelOfArticle(ArticleBrief articleBrief){
        return LitePal.find(Channel.class, articleBrief.getChannel_id());
    }

    /**
     * 获取收藏
     *
     * @param offset 查询偏移量
     * @param limit 查询返回的最大数目
     * @return list<Collection>
     */
    public static List<Collection> getCollection(int offset, int limit){
        return LitePal.offset(offset)
                .limit(limit)
                .find(Collection.class);
    }

    /**
     * 根据文章简介判断某文章是否被收藏
     *
     * @param articleBrief 文章简介
     * @return the boolean
     */
    public static Boolean isCollect(ArticleBrief articleBrief){
        List<Collection> collections = LitePal.where("link = ?", articleBrief.getLink())
                .find(Collection.class);
        return !collections.isEmpty();
    }

    /**
     * 取消收藏
     *
     * @param collection 目标文章
     * @throws SQLException 删除出现错误
     */
    public static void removeCollection(Collection collection) throws SQLException {
        int rows = LitePal.delete(Collection.class, collection.getId());
        //当被删除的item数不为1时，均属于出错
        if(rows != 1) throw new SQLException("删除出错");
    }

    /**
     * 取消订阅某频道
     *
     * @param channel 目标频道对象
     */
    public static void removeChannel(Channel channel){
        try {
            //找到对应频道的所有文章简介
            List<ArticleBrief> articleBriefs = LitePal.select("id","content_id")
                    .where("channel_id = ?", Integer.toString(channel.getId()))
                    .find(ArticleBrief.class);
            //删除所有文章内容和文章简介
            for(ArticleBrief articleBrief: articleBriefs){
                LitePal.delete(ArticleContent.class, articleBrief.getContent_id());
                LitePal.delete(ArticleBrief.class, articleBrief.getId());
            }
            //删除频道
            LitePal.delete(Channel.class, channel.getId());
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 清除缓存的文章
     */
    public static void clearStorage(){
        //清除所有未收藏的文章
        try {
            LitePal.deleteAll(ArticleBrief.class);
            LitePal.deleteAll(ArticleContent.class);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
