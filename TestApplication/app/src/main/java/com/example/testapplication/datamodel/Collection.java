package com.example.testapplication.datamodel;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

import java.util.Date;

/**
  * @ClassName:  Collection
  * @Author:  Von
  * @Date:  2021/4/24
  * @Description:  收藏类，外部可调用的数据有：标题，链接，创作者，推送日期，类别，简介，内容
*/
public class Collection extends LitePalSupport {

    @Column(unique = true)
    private int id;

    private String title;

    @Column(unique = true)
    private String link;

    private String creator;

    private Date pubDate;

    private String[] category;

    private String description;

    private String content;

    public Collection() {}

    public Collection(ArticleBrief articleBrief, String content){
        this.title = articleBrief.getTitle();
        this.link = articleBrief.getLink();
        this.creator = articleBrief.getCreator();
        this.pubDate = articleBrief.getPubDate();
        this.category = articleBrief.getCategory();
        this.description = articleBrief.getDescription();
        this.content = content;
    }

    public Collection(String title, String link, String creator, Date pubDate,
                      String[] category, String description, String content) {
        this.title = title;
        this.link = link;
        this.creator = creator;
        this.pubDate = pubDate;
        this.category = category;
        this.description = description;
        this.content = content;
    }

    protected int getId() {
        return id;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
