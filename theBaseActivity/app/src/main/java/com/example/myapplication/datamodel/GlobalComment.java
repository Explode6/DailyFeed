package com.example.myapplication.datamodel;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

import java.util.Date;

/**
 * @ClassName: GlobalComment
 * @Author: Von
 * @Date: 2021/5/6
 * @Description: 对文章的全局评论
 */
public class GlobalComment extends LitePalSupport {

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
}
