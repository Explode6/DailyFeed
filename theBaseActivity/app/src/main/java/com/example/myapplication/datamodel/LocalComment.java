package com.example.myapplication.datamodel;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

/**
 * @ClassName: LocalComment
 * @Author: Von
 * @Date: 2021/5/6
 * @Description: 对文章部分内容的局部评论
 */
public class LocalComment extends LitePalSupport {

    @Column(unique = true)
    private int id;

    private int articleBriefId;

    private String localContent;

    private String comment;

    public LocalComment(){
        this.articleBriefId = -1;
        this.localContent = "";
        this.comment = "";
    }

    public LocalComment(int articleBriefId, String localContent, String comment) {
        this.articleBriefId = articleBriefId;
        this.localContent = localContent;
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
}
