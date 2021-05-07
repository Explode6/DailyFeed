package com.example.rssreader.wxapi;

import android.content.Context;
import android.graphics.Bitmap;

import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.io.ByteArrayOutputStream;

/**
 * @ClassName: WXShareUtil
 * @Author: Von
 * @Date: 2021/5/7
 * @Description: 分享到微信的工具类
 */
public class WXShareUtil {

    //申请的合法appId
    public static final String APP_ID = "";

    //缩略图大小
    public static final int THUMB_SIZE = 90;

    //IWXAPI是第三方app和微信通信的openApi接口
    private IWXAPI api;
    private Context context;
    public static WXShareUtil wxShareUtil;

    /**
     * 获取WXShareUtil对象
     *
     * @param context
     */
    public static WXShareUtil getInstance(Context context){
        //单例模式
        if(wxShareUtil == null){
            wxShareUtil = new WXShareUtil();
        }
        if(wxShareUtil.api != null){
            wxShareUtil.api.unregisterApp();
        }
        wxShareUtil.context = context;
        //注册应用到微信
        wxShareUtil.api = WXAPIFactory.createWXAPI(wxShareUtil.context, APP_ID,true);
        wxShareUtil.api.registerApp(APP_ID);
        return wxShareUtil;
    }

    /**
     * 分享图片到微信
     *
     * @param bitmap 待分享的图片
     * @return
     */
    public boolean sharePic(Bitmap bitmap) throws Exception {
        //图片数据不能超过10MB
        byte[] imgData = bmpToByteArray(bitmap, false);
        if (imgData.length > (10*1024*1024)) throw new Exception("图片过大");

        //初始化 WXImageObject 和 WXMediaMessage 对象
        WXImageObject imgObj = new WXImageObject(imgData);
        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = imgObj;

        //设置缩略图
        Bitmap thumbBmp = Bitmap.createScaledBitmap(bitmap, THUMB_SIZE, THUMB_SIZE, true);
        bitmap.recycle();
        msg.thumbData = bmpToByteArray(thumbBmp, true);

        //构造一个Req, 填入WXMediaMessage内容
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.message = msg;
        //分享到微信对话
        req.scene = SendMessageToWX.Req.WXSceneSession;
        //调用api接口，发送数据到微信
        return api.sendReq(req);
    }

    /**
     * 分享网页到微信
     *
     * @param url 网页url地址(不超过10KB)
     * @param title 网页标题(不超过512B)
     * @param thumb 网页缩略图(不超过32KB)
     * @param description 网页简介(不超过1KB)
     * @return
     */
    public boolean shareURL(String url, String title, Bitmap thumb, String description){
        //初始化一个WXWebPageObject对象, 写入网页的url
        WXWebpageObject webObj = new WXWebpageObject();
        webObj.webpageUrl = url;

        //初始化WXMediaMessage对象, 写入标题，缩略图，简介
        WXMediaMessage msg = new WXMediaMessage(webObj);
        msg.title = title;
        msg.description = description;
        msg.thumbData = bmpToByteArray(thumb, true);

        //构造一个Req, 填入WXMediaMessage内容
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.message = msg;
        req.scene = SendMessageToWX.Req.WXSceneSession;
        return api.sendReq(req);
    }

    /**
     * 将待分享的BitMap图转为byte[]格式
     *
     * @param bmp 待分享的图
     * @param needRecycle 是否释放图片资源
     * @return the byte[]
     */
    private byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, output);
        if (needRecycle) {
            bmp.recycle();
        }
        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}
