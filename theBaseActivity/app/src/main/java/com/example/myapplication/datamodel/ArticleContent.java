package com.example.myapplication.datamodel;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

/**
  * @ClassName: ArticleContent
  * @Author Von
  * @Date:  2021/4/24
  * @Description: 文章内容类，外部不可调用其数据
*/
public class ArticleContent extends LitePalSupport {

    @Column(unique = true)
    private int id;

    private String content;

    public ArticleContent(){}

    public ArticleContent(String content) {
        this.content = content;
    }

    protected int getId() {
        return id;
    }

    protected String getContent() {
        return content;
    }

    protected void setContent(String content) {
        this.content = content;
    }
}
