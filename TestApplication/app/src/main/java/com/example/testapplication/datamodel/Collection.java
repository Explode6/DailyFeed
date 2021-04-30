package com.example.testapplication.datamodel;

import android.os.Parcel;
import android.os.Parcelable;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

import java.util.Date;

/**
  * @ClassName:  Collection
  * @Author:  Von
  * @Date:  2021/4/24
  * @Description:  收藏类，外部可调用的数据有：标题，链接，创作者，收藏日期，类别，简介，内容
*/
public class Collection extends LitePalSupport implements Parcelable {

    @Column(unique = true)
    private int id;

    private String title;

    @Column(unique = true)
    private String link;

    private String creator;

    private String category;

    private Date collectDate;

    private String description;

    private String content;

    public Collection() {}

    public Collection(ArticleBrief articleBrief, String content, Date collectDate){
        this.title = articleBrief.getTitle();
        this.link = articleBrief.getLink();
        this.creator = articleBrief.getCreator();
        this.setCategory(articleBrief.getCategory());
        this.description = articleBrief.getDescription();
        this.collectDate = collectDate;
        this.content = content;
    }

    public Collection(String title, String link, String creator,
                      String[] category, String description, String content) {
        this.title = title;
        this.link = link;
        this.creator = creator;
        this.setCategory(category);
        this.description = description;
        this.content = content;
    }

    //parcel专用
    public Collection(int id,String title, String link, String creator,
                      String category, Date date,String description, String content) {
        this.id = id;
        this.title = title;
        this.link = link;
        this.creator = creator;
        this.category = category;
        this.collectDate = date;
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
        return this.category.split(",");
    }

    public void setCategory(String[] category) {
        this.category = new String("");
        for(String s:category) this.category += s + ",";
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
        dest.writeValue(collectDate);
        dest.writeString(description);
        dest.writeString(content);
    }

    public static final Creator<Collection> CREATOR = new Creator<Collection>() {
        @Override
        public Collection createFromParcel(Parcel source) {
            int id = source.readInt();
            String title = source.readString();
            String link = source.readString();
            String creator = source.readString();
            String category = source.readString();
            Date collectDate = (Date) source.readValue(Date.class.getClassLoader());
            String description = source.readString();
            String content = source.readString();
            return new Collection(id,title,link,creator,category,collectDate,description,content);
        }

        @Override
        public Collection[] newArray(int size) {
            return new Collection[size];
        }
    };
}
