package com.example.myapplication.datamodel;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

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

    public GlobalComment(){
        this.articleBriefId = -1;
        this.comment = "";
    }

    public GlobalComment(int articleBriefId, String comment) {
        this.articleBriefId = articleBriefId;
        this.comment = comment;
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
}
