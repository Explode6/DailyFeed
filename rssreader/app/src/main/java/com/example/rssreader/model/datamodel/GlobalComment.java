package com.example.rssreader.model.datamodel;

import android.os.Parcel;
import android.os.Parcelable;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

import java.util.Date;

/**
 * @ClassName: GlobalComment
 * @Author: Von
 * @Date: 2021/5/6
 * @Description: 对文章的全局评论
 */
public class GlobalComment extends LitePalSupport implements Parcelable {

    @Column(unique = true)
    private int id;

    private int articleBriefId;

    private String comment;

    private Date date;

    public GlobalComment(){
        this.articleBriefId = -1;
        this.comment = "";
        this.date = null;
    }

    public GlobalComment(int articleBriefId, String comment, Date date) {
        this.articleBriefId = articleBriefId;
        this.comment = comment;
        this.date = date;
    }

    //序列化使用
    public GlobalComment(int id,int articleBriefId,String comment,Date date){
        this.id = id;
        this.articleBriefId = articleBriefId;
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
        dest.writeInt(this.id);
        dest.writeInt(this.articleBriefId);
        dest.writeString(this.comment);
        dest.writeValue(this.date);
    }

    public static final Creator<GlobalComment> CREATOR = new Creator<GlobalComment>() {
        @Override
        public GlobalComment createFromParcel(Parcel source) {
            int id = source.readInt();
            int articleBriefId = source.readInt();
            String comment = source.readString();
            Date date = (Date) source.readValue(Date.class.getClassLoader());
            return new GlobalComment(id,articleBriefId,comment,date);
        }

        @Override
        public GlobalComment[] newArray(int size) {
            return new GlobalComment[size];
        }
    };
}
