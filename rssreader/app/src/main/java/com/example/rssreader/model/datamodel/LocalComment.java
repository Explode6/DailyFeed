package com.example.rssreader.model.datamodel;

import android.os.Parcel;
import android.os.Parcelable;
import android.provider.ContactsContract;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

import java.util.Date;

/**
 * @ClassName: LocalComment
 * @Author: Von
 * @Date: 2021/5/6
 * @Description: 对文章部分内容的局部评论
 */
public class LocalComment extends LitePalSupport implements Parcelable {

    @Column(unique = true)
    private int id;

    private int articleBriefId;

    //选中的内容
    private String localContent;

    //评论内容
    private String comment;

    private Date date;

    public LocalComment(){
        this.articleBriefId = -1;
        this.localContent = "";
        this.comment = "";
        this.date = null;
    }

    public LocalComment(int articleBriefId, String localContent, String comment, Date date) {
        this.articleBriefId = articleBriefId;
        this.localContent = localContent;
        this.comment = comment;
        this.date = date;
    }

    //序列化使用
    public LocalComment(int id,int articleBriefId, String localContent, String comment, Date date) {
        this.id = id;
        this.articleBriefId = articleBriefId;
        this.localContent = localContent;
        this.comment = comment;
        this.date = date;
    }


    protected int getId() {
        return id;
    }

    protected int getArticleBriefId() {
        return articleBriefId;
    }

    protected void setArticleBriefId(int articleBriefId) {
        this.articleBriefId = articleBriefId;
    }

    public String getLocalContent() {
        return localContent;
    }

    public void setLocalContent(String localContent) {
        this.localContent = localContent;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(articleBriefId);
        dest.writeString(localContent);
        dest.writeString(comment);
        dest.writeValue(date);
    }

    public static final Creator<LocalComment> CREATOR = new Creator<LocalComment>() {
        @Override
        public LocalComment createFromParcel(Parcel source) {
            int id = source.readInt();
            int articleBriefId = source.readInt();
            String localContent = source.readString();
            String comment = source.readString();
            Date date = (Date) source.readValue(Date.class.getClassLoader());
            return new LocalComment(id,articleBriefId,localContent,comment,date);
        }

        @Override
        public LocalComment[] newArray(int size) {
            return new LocalComment[size];
        }
    };
}
