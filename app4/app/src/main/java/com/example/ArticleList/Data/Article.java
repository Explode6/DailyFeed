package com.example.ArticleList.Data;

/**
 * @ClassName Article
 * @Author name
 * @Date 2021/4/25
 * @Description
 */
public class Article {
    private String title;

    private int imageId;

    private String brief;

    /**
     * y
     *
     * @param title   the title
     * @param imageId the image id
     * @param brief   the brief
     */
    public Article(String title, int imageId, String brief){
        this.title = title;
        this.imageId = imageId;
        this.brief = brief;
    }

    public String getTitle(){
        return this.title;
    }

    public int getImageId(){
        return this.imageId;
    }

    public String getBrief() {
        return this.brief;
    };
}
