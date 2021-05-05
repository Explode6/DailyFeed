package com.example.rssreader.model.datamodel;

import android.os.Parcel;
import android.os.Parcelable;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

/**
  * @ClassName： ArticleBrief
  * @Author: Von
  * @Date： 2021/4/24
  * @Description： 文章简介类，外部可调用的信息有：标题，链接，创作者，类别，简介，已读标记
*/
public class ArticleBrief extends LitePalSupport implements Parcelable {

    @Column(unique = true)
    private int id;

    private String title;

    @Column(unique = true, index = true)
    private String link;

    private String creator;

    private String category;

    private String description;

    private Boolean isRead;

    private long pubTime;

    private int content_id;

    private int channel_id;

    public ArticleBrief() {
        this.title = new String("");
        this.link = new String("");
        this.creator = new String("");
        this.category = new String("");
        this.description = new String("");
        this.isRead = false;
    }

    public ArticleBrief(String title, String link, String creator, String[] category,
                        String description) {
        this.title = title;
        this.link = link;
        this.creator = creator;
        this.setCategory(category);
        this.description = description;
        //默认未读
        this.isRead = false;
    }

    //序列化使用
    public ArticleBrief(int id,String title, String link, String creator, String category,
                        String description,Boolean isRead,long pubTime,int content_id,int channel_id) {
        this.id = id;
        this.title = title;
        this.link = link;
        this.creator = creator;
        this.category = category;
        this.description = description;
        //默认未读
        this.isRead = isRead;
        this.pubTime = pubTime;
        this.content_id = content_id;
        this.channel_id = channel_id;

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

    protected long getPubTime() {
        return pubTime;
    }

    protected void setPubTime(long pubTime) {
        this.pubTime = pubTime;
    }

    public String[] getCategory() {
        return this.category.split(",");
    }

    public void setCategory(String[] category) {
        this.category = new String("");
        for(String s:category) this.category += s + ",";
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(link);
        dest.writeString(creator);
        dest.writeString(category);
        dest.writeString(description);
        dest.writeBoolean(isRead);
        dest.writeLong(pubTime);
        dest.writeInt(content_id);
        dest.writeInt(channel_id);
    }

    public static final Creator<ArticleBrief> CREATOR = new Creator<ArticleBrief>() {
        @Override
        public ArticleBrief createFromParcel(Parcel source) {
            int id = source.readInt();
            String title = source.readString();
            String link = source.readString();
            String creator = source.readString();
            String category = source.readString();
            String description = source.readString();
            Boolean isRead = source.readBoolean();
            Long pubTime = source.readLong();
            int content_id = source.readInt();
            int channel_id = source.readInt();
            return new ArticleBrief(id,title,link,creator,category,description,isRead,pubTime,content_id,channel_id);
        }

        @Override
        public ArticleBrief[] newArray(int size) {
            return new ArticleBrief[size];
        }
    };
}
