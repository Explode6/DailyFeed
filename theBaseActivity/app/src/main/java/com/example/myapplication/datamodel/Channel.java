package com.example.myapplication.datamodel;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
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

    @Column(unique = true, index = true)
    private String rssLink;

    private String addressLink;

    private String lastBuildDate;

    private String description;

    @Column(nullable = true)
    private byte[] image;

    private List<ArticleBrief> articleBriefs = new ArrayList<>();

    public Channel() {
        //设置默认值
        this.title = new String("");
        this.rssLink = new String("");
        this.addressLink = new String("");
        this.lastBuildDate = new String("");
        this.description = new String("");
        this.image = new byte[]{};
    }

    public Channel(String title, String rssLink, String addressLink, String lastBuildDate, String description, Bitmap image) {
        this.title = title;
        this.rssLink = rssLink;
        this.addressLink = addressLink;
        this.lastBuildDate = lastBuildDate;
        this.description = description;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, baos);
        this.image = baos.toByteArray();
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

    public String getLastBuildDate() {
        return lastBuildDate;
    }

    public void setLastBuildDate(String lastBuildDate) {
        this.lastBuildDate = lastBuildDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Bitmap getImage() {
        if(this.image.length == 0) return null;
        return BitmapFactory.decodeByteArray(this.image,0,this.image.length);
    }

    public void setImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, baos);
        this.image = baos.toByteArray();
    }
}
