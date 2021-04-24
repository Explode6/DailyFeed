package com.example.myapplication.datamodel;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
  * @ClassName： Channel
  * @Author Von
  * @Date： 2021/4/24
  * @Description： 频道类，外部可调用的信息包括：标题，订阅链接，原文链接，最后推送日期，频道简介，频道图片
*/
public class Channel extends LitePalSupport {

    @Column(unique = true)
    private int id;

    private String title;

    @Column(unique = true)
    private String rssLink;

    private String addressLink;

    private Date lastBuildDate;

    private String description;

    @Column(nullable = true, defaultValue = "")
    private String image;

    private List<ArticleBrief> articleBriefs = new ArrayList<>();

    public Channel() {}

    public Channel(String title, String rssLink, String addressLink, Date lastBuildDate, String description, String image) {
        this.title = title;
        this.rssLink = rssLink;
        this.addressLink = addressLink;
        this.lastBuildDate = lastBuildDate;
        this.description = description;
        this.image = image;
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

    public String getRssLink() {
        return rssLink;
    }

    public void setRssLink(String rssLink) {
        this.rssLink = rssLink;
    }

    public String getAddressLink() {
        return addressLink;
    }

    public void setAddressLink(String addressLink) {
        this.addressLink = addressLink;
    }

    public Date getLastBuildDate() {
        return lastBuildDate;
    }

    public void setLastBuildDate(Date lastBuildDate) {
        this.lastBuildDate = lastBuildDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
