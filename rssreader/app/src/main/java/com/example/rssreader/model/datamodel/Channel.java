package com.example.rssreader.model.datamodel;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Base64;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

import java.io.Serializable;

/**
  * @ClassName： Channel
  * @Author Von
  * @Date： 2021/4/24
  * @Description： 频道类，外部可调用的信息包括：标题，订阅链接，原文链接，最后推送日期，频道简介，频道图片
*/
public class Channel extends LitePalSupport implements Parcelable, Serializable {

    @Column(unique = true)
    private int id;

    //Channel的id
    private String title;

    //Channel的订阅链接
    @Column(unique = true, index = true)
    private String rssLink;

    //Channel的原网站链接
    private String addressLink;

    //rss的最新推送时间
    private String lastBuildDate;

    //Channel的简介
    private String description;

    //Channel的展示顺序，从0开始！
    private int order;

    @Column(nullable = true)
    private String image;

    //private List<ArticleBrief> articleBriefs = new ArrayList<>();

    public Channel() {
        //设置默认值
        this.title = new String("");
        this.rssLink = new String("");
        this.addressLink = new String("");
        this.lastBuildDate = new String("");
        this.description = new String("");
        this.order = -1;
        this.image = null;
    }

    public Channel(String title, String rssLink, String addressLink, String lastBuildDate, String description, byte[] image) {
        this.title = title;
        this.rssLink = rssLink;
        this.addressLink = addressLink;
        this.lastBuildDate = lastBuildDate;
        this.description = description.trim();
        this.setImage(image);
    }


    //序列化专用
    public Channel(int id,String title, String rssLink, String addressLink,
                   String lastBuildDate, String description,int order, String image) {
        this.id = id;
        this.title = title;
        this.rssLink = rssLink;
        this.addressLink = addressLink;
        this.lastBuildDate = lastBuildDate;
        this.description = description;
        this.order = order;
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
        this.description = description.trim();
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public byte[] getImage() {
        if(this.image== null || this.image.isEmpty()) return null;
        return Base64.decode(this.image, Base64.DEFAULT);
    }

    public void setImage(byte[] image) {
        this.image = Base64.encodeToString(image, Base64.DEFAULT);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(rssLink);
        dest.writeString(addressLink);
        dest.writeString(lastBuildDate);
        dest.writeString(description);
        dest.writeInt(order);
        dest.writeString(image);

    }

    public static final Creator<Channel> CREATOR = new Creator<Channel>() {
        @Override
        public Channel createFromParcel(Parcel source) {
            int id = source.readInt();
            String title = source.readString();
            String rssLink = source.readString();
            String addressLink = source.readString();
            String lastBuildDate = source.readString();
            String description = source.readString();
            int order = source.readInt();
            String img = source.readString();
            return new Channel(id,title,rssLink,addressLink,lastBuildDate,description,order,img);
        }

        @Override
        public Channel[] newArray(int size) {
            return new Channel[size];
        }
    };
}
