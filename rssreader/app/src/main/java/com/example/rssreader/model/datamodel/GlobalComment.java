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

    @Column(nullable = false)
    private int articleBrief_id;

    private String comment;

    private Date date;

    public GlobalComment(){
        this.articleBrief_id = -1;
        this.comment = "";
        this.date = null;
    }

    public GlobalComment(int articleBrief_id, String comment, Date date) {
        this.articleBrief_id = articleBrief_id;
        this.comment = comment;
        this.date = date;
    }

    //序列化使用
    public GlobalComment(int id, int articleBrief_id, String comment, Date date){
        this.id = id;
        this.articleBrief_id = articleBrief_id;
        this.comment = comment;
        this.date = date;
    }

    protected int getId() {
        return id;
    }

    protected int getArticleBrief_id() {
        return articleBrief_id;
    }

    protected void setArticleBrief_id(int articleBrief_id) {
        this.articleBrief_id = articleBrief_id;
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
        dest.writeInt(this.articleBrief_id);
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
