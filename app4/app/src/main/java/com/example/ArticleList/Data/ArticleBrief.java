package com.example.ArticleList.Data;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @ClassName Article
 * @Author name
 * @Date 2021/4/25
 * @Description
 */
public class ArticleBrief {
    private String title;

    private String imageId;

    private String brief;

    private boolean havingImage;

    private boolean isRead;

    private boolean isCollected;
    /**
     * y
     *
     * @param title   the title
     * @param imageId the image id
     * @param brief   the brief
     */
    public ArticleBrief(String title, String imageId, String brief){
        this.title = title;
        this.imageId = imageId;
        this.brief = brief;
        //test
        this.havingImage = true;
        //test
        this.isRead = false;
        this.isCollected = false;
    }
    public ArticleBrief(String title, String brief){
        this.title = title;
        this.imageId = imageId;
        this.brief = brief;
        //test
        this.havingImage = false;
        //test
        this.isRead = true;
        this.isCollected = true;
    }



    public String getTitle(){
        return this.title;
    }

//    //如果图片采用本地存储的话用这个函数
//    public int getImageId(){
//        return this.imageId;
//    }

    //如果只存图片的url的话，用下面的形式
    public String getImageId() {
        return this.imageId;
    }

    public String getDescription() {
        return this.brief;
    };

    public boolean isHavingImage() {
        return havingImage;
    }

    public boolean isRead(){
        return this.isRead;
    }

    public void switchRead(){
        this.isRead = !this.isRead;
    }

    public boolean isCollected(){
        return this.isCollected;
    }

    public void switchCollected(){
        this.isCollected = !this.isCollected;
    }
}
