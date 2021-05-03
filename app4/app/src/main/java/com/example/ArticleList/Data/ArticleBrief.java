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

    private int imageId;

    private String brief;

    /**
     * y
     *
     * @param title   the title
     * @param imageId the image id
     * @param brief   the brief
     */
    public ArticleBrief(String title, int imageId, String brief){
        this.title = title;
        this.imageId = imageId;
        this.brief = brief;
    }

    public String getTitle(){
        return this.title;
    }

    //如果图片采用本地存储的话用这个函数
    public int getImageId(){
        return this.imageId;
    }

//    //如果只存图片的url的话，用下面的形式
//    public URL getImageId(){
//        String str = "https://pic4.zhimg.com/v2-952eb86670a2f12a7a7ef8632d758b35_720w.jpg?rss\n";
//        URL url = null;
//        try {
//            url = new URL(str);
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        }
//        return url;
//    }

    public String getDescription() {
        return this.brief;
    };
}
