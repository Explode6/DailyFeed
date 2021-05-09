package com.example.rssreader.model.parse;


import android.os.RemoteException;
import android.text.Html;
import android.util.Log;

import com.example.rssreader.model.datamodel.ArticleBrief;
import com.example.rssreader.model.datamodel.Channel;
import com.example.rssreader.model.datamodel.DataBaseHelper;

import org.apache.commons.text.StringEscapeUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import java.net.URLDecoder;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * @ClassName： XmlHandler
 * @Author SH
 * @Date： 2021/4/25
 * @Description： 此类对Xml进行处理，解析相关Xml操作
 */
public class XmlHandler {
    private String url;

    private static final String TAG = "XmlHandler";

    /**
     * 内部类ImgDownloader
     * 下载图片线程
     */
    private class ImgDownloadThread implements Runnable{

        private Channel channel;
        private String url;

        ImgDownloadThread(Channel channel,String url){
            this.channel = channel;
            this.url = url;
        }

        @Override
        public void run() {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url).build();
            try {
                Response response = client.newCall(request).execute();
                byte[] bytes = response.body().bytes();
                channel.setImage(bytes);
                DataBaseHelper.addChannel(channel);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public XmlHandler(String url){
        this.url = url ;
    }

    /**
     * 下载xml并且解析，将相关数据存放在数据库之中
     */
    public void startParse(DataCallback dataCallback) throws RemoteException {

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url).build();
        try {
            //测试速度
            long start = System.currentTimeMillis();

            Response response = client.newCall(request).execute();

            //检测
            BufferedInputStream responseData = new BufferedInputStream(response.body().byteStream());

            String decode = charsetDetect(responseData);

            FilterInputStreamReader responseDataReader = new FilterInputStreamReader(responseData,decode);

            long diff =  System.currentTimeMillis()-start;
            Log.d(TAG,"下载使用："+ diff + "毫秒");

            try{
                //测试速度
                start = System.currentTimeMillis();

                XmlParse(responseDataReader);

                diff =  System.currentTimeMillis()- start;
                Log.d(TAG,"解析使用："+ diff + "毫秒");

                dataCallback.onSuccess();
            }catch (DocumentException e) {
                e.printStackTrace();
                dataCallback.onFailure();

            } catch (SQLException e) {
                e.printStackTrace();
                dataCallback.onFailure();
            }

        } catch (IOException e) {
            e.printStackTrace();
            dataCallback.onFailure();
        }

    }

    /**
     * 提供对XML的解析
     * @param isReader :InputStreamReader
     */
    private void XmlParse (InputStreamReader isReader) throws IOException, DocumentException, SQLException {


        //创建Reader对象
        SAXReader reader = new SAXReader();

        Document document = reader.read(isReader);
        //获取channel节点
        Element channelNode = document.getRootElement().element("channel");

        Channel channel = new Channel();

        //获取channel的子节点
        Iterator iterator = channelNode.elementIterator();

        while(iterator.hasNext()) {
            Element channelSon = (Element) iterator.next();
            switch (channelSon.getQualifiedName()){
                //标题
                case "title":{
                    channel.setTitle(channelSon.getText());
                    break;
                }
                //原文链接
                case "link" :{
                    channel.setAddressLink(channelSon.getText());
                    break;
                }
                //描述
                case "description" :{
                    channel.setDescription(channelSon.getText());
                    break;
                }
                //rss链接
                case "atom:link":{
                    channel.setRssLink(channelSon.attribute(0).getValue());
                    DataBaseHelper.addChannel(channel);
                    break;
                }
                //最后建立日期
                case "lastBuildDate":{
                    channel.setLastBuildDate(channelSon.getText());
                    break;
                }
                //图片 (设置值为其url)
                case "image":{
                    Element url = channelSon.element("url");
                    new Thread(new ImgDownloadThread(channel,url.getText())).start();
                    break;
                }
                //文章内容项
                case "entry":
                case "item" :{
                    ArticleBrief articleBrief = new ArticleBrief();
                    Iterator itemIterator = channelSon.elementIterator();
                    List<String> categoryList = new ArrayList<>();
                    String content = "";

                    while(itemIterator.hasNext()){
                        Element articleSon = (Element) itemIterator.next();
                        switch (articleSon.getQualifiedName()){
                            case "title":{
                                articleBrief.setTitle(articleSon.getText());
                                break;
                            }
                            case "link":{
                                articleBrief.setLink(articleSon.getText());
                                break;
                            }
                            case "dc:creator":{
                                articleBrief.setCreator(articleSon.getText());
                                break;
                            }
                            case "category":{
                                categoryList.add(articleSon.getText());
                                break;
                            }
                            case "description":{
                                articleBrief.setCategory(categoryList.toArray(new String[0]));
                                content = articleSon.getText();
                                //转码
                                content = StringEscapeUtils.unescapeHtml4(content);
                                String description = content;
                                //正则匹配
                                Pattern p = Pattern.compile("(<(\\S*?)[^>]*>.*?|<.*? />)");
                                Matcher m = p.matcher(description);
                                description = m.replaceAll("");
                                articleBrief.setDescription(description);
                                break;
                            }
                            case "content":
                            case "content:encoded":{
                                content = StringEscapeUtils.unescapeHtml4(articleSon.getText());
                                break;
                            }

                        }
                    }
                    //找img标签中图片的内容
                    Pattern imgp = Pattern.compile("<img.*?src=\"(.+?)\"");
                    Matcher imgm = imgp.matcher(content);
                    if(imgm.find()){
                        articleBrief.setFirstPhoto(imgm.group(1));
                    }
                    //最后添加
                    DataBaseHelper.addArticle(articleBrief,content,channel);
                    break;
                }

            }
        }
        DataBaseHelper.addChannel(channel);

    }


    /**
     * 检测xml输入流的编码格式
     * @param in: The BufferedInputStream to be Detect
     * @return 编码格式名称
     */
    private String charsetDetect(BufferedInputStream in) {

        String _charset="UTF-8";
        try {

            byte[] buffer = new byte[100];
            in.mark(110);
            in.read(buffer);
            in.reset();
            String checkStr = new String(buffer);
            Pattern pattern = Pattern.compile("(encoding=\").*?(\")");
            Matcher matcher = pattern.matcher(checkStr);
            if(matcher.find()){
                _charset = matcher.group(0).replaceAll("encoding=\"","").replaceAll("\"","");
            }
        }
        catch (IOException e) {e.printStackTrace(); }
        return _charset;
    }
}
