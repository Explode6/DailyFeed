package com.example.myapplication.datamodel;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

import java.util.Date;

/**
  * @ClassName： ArticleBrief
  * @Author: Von
  * @Date： 2021/4/24
  * @Description： 文章简介类，外部可调用的信息有：标题，链接，创作者，推送日期，类别，简介，已读标记
*/
public class ArticleBrief extends LitePalSupport {

    @Column(unique = true)
    private int id;

    private String title;

    @Column(unique = true)
    private String link;

    private String creator;

    private Date pubDate;

    private String[] category;

    private String description;

    private Boolean isRead;

    private int content_id;

    private int channel_id;

    public ArticleBrief() {}

    public ArticleBrief(String title, String link, String creator, Date pubDate, String[] category,
                        String description) {
        this.title = title;
        this.link = link;
        this.creator = creator;
        this.pubDate = pubDate;
        this.category = category;
        this.description = description;
        //默认未读
        this.isRead = false;
    }

    protected int getId() {
        return id;
    }

    protected void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Date getPubDate() {
        return pubDate;
    }

    public void setPubDate(Date pubDate) {
        this.pubDate = pubDate;
    }

    public String[] getCategory() {
        return category;
    }

    public void setCategory(String[] category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    protected int getContent_id() {
        return content_id;
    }

    protected void setContent_id(int content_id) {
        this.content_id = content_id;
    }

    public Boolean getRead() {
        return isRead;
    }

    public void setRead(Boolean read) {
        isRead = read;
    }

    protected int getChannel_id() {
        return channel_id;
    }

    protected void setChannel_id(int channel_id) {
        this.channel_id = channel_id;
    }
}
