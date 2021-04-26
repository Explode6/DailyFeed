package com.example.myapplication.datamodel;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

import java.util.Date;

/**
  * @ClassName:  Collection
  * @Author:  Von
  * @Date:  2021/4/24
  * @Description:  收藏类，外部可调用的数据有：标题，链接，创作者，收藏日期，类别，简介，内容
*/
public class Collection extends LitePalSupport {

    @Column(unique = true)
    private int id;

    private String title;

    @Column(unique = true)
    private String link;

    private String creator;

    private String[] category;

    private Date collectDate;

    private String description;

    private String content;

    public Collection() {}

    public Collection(ArticleBrief articleBrief, String content, Date collectDate){
        this.title = articleBrief.getTitle();
        this.link = articleBrief.getLink();
        this.creator = articleBrief.getCreator();
        this.category = articleBrief.getCategory();
        this.description = articleBrief.getDescription();
        this.collectDate = collectDate;
        this.content = content;
    }

    public Collection(String title, String link, String creator,
                      String[] category, String description, String content) {
        this.title = title;
        this.link = link;
        this.creator = creator;
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

    public String[] getCategory() {
        return category;
    }

    public void setCategory(String[] category) {
        this.category = category;
    }

    public Date getCollectDate() {
        return collectDate;
    }

    public void setCollectDate(Date collectDate) {
        this.collectDate = collectDate;
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
