package com.example.rssreader.util;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;


/**
 * @ClassName: ShareUtil
 * @Author: Von
 * @Date: 2021/5/11
 * @Description: 向外部应用分享的工具类
 */
public class ShareUtil {

    private static Intent shareIntent;

    /**
     * 保存并向外部应用分享图片
     *
     * @param context 上下文
     * @param bmp 待分享的位图
     * @param needRecycle 位图是否回收
     */
    public static void shareImg(Context context, final Bitmap bmp, boolean needRecycle){
        if(bmp == null) return;
        shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        //保存图片并获取其Uri
        shareIntent.setType("image/*");
        Uri imgUri = saveImg(context, bmp, needRecycle);
        shareIntent.putExtra(Intent.EXTRA_STREAM, imgUri);
        //拉起系统分享，挑选分享应用
        context.startActivity(Intent.createChooser(shareIntent, "图片分享："));
    }

    /**
     * 向外部分享网页信息
     *
     * @param context 上下文
     * @param title 网页的标题
     * @param link 网页的链接
     */
    public static void shareUrl(Context context, String title, String link){
        if(link==null||link.isEmpty()) return;
        shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        //添加文本的标题和链接内容
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Daily Feed 日报分享！\n" +title + " \n链接：" + link);
        shareIntent.putExtra(Intent.EXTRA_TITLE, "日报分享");
        shareIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(Intent.createChooser(shareIntent, "文章分享"));
    }

    /**
     * 仅保存图片
     *
     * @param context 上下文
     * @param bmp 待保存的位图
     * @param needRecycle 是否回收位图
     * @return the Uri 位图保存后的本地Uri
     */
    public static Uri saveImg(Context context, final Bitmap bmp, boolean needRecycle){
        //适配7.0以上
        Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(context.getContentResolver(), bmp, "Daily Feed",null));
        //回收内存
        if(needRecycle) bmp.recycle();
        return uri;
    }
}
