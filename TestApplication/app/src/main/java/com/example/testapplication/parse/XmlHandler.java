package com.example.testapplication.parse;


import android.util.Log;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

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
 * @Description： 此方法对Xml进行处理，解析相关Xml操作
 */
public class XmlHandler {
    private String url;

    private static final String TAG = "XmlHandler";

    public XmlHandler(String url){
        this.url = url ;
    }

    /**
     * 下载xml并且解析
     */
    public void startParse(){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url).build();
        try {
            Response response = client.newCall(request).execute();

            //检测
            BufferedInputStream responseData = new BufferedInputStream(response.body().byteStream());

            String decode = charsetDetect(responseData);

            FilterInputStreamReader responseDataReader = new FilterInputStreamReader(responseData,decode);

            try{
                XmlParse(responseDataReader);
            }catch (DocumentException e) {
                e.printStackTrace();

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    /**
     * 提供对XML的解析
     * @param isReader :InputStreamReader
     */
    public void XmlParse (InputStreamReader isReader) throws IOException, DocumentException {
        //创建Reader对象
        SAXReader reader = new SAXReader();


        Document document = reader.read(isReader);
        Element channel = document.getRootElement().element("channel");
        //获取节点
        Iterator iterator = channel.elementIterator();

        while(iterator.hasNext()) {
            Element channelAtr = (Element) iterator.next();
            List<Attribute> attributes = channelAtr.attributes();
            Log.d(TAG, "节点名" + channelAtr.getName());
            Log.d(TAG, "====解析属性===");
            for (Attribute attribute : attributes) {
                Log.d(TAG, attribute.getName() + ":" + attribute.getValue());
            }
            Log.d(TAG, "节点text:" + channelAtr.getText());
            Log.d(TAG, "遍历子节点");
            Iterator itemIterator = channelAtr.elementIterator();
            while (itemIterator.hasNext()) {
                Element item = (Element) itemIterator.next();
                Log.d(TAG, "子节点："+item.getName());
                if(item.getName() == "description"){
                    Log.d(TAG,"description:"+item.getText());
                }
            }
        }
    }



    /**
     * 检测xml输入流的编码格式
     * @param in: The BufferedInputStream to be Detect
     * @return 编码格式名称
     */
    public String charsetDetect(BufferedInputStream in) {

        String _charset="";
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
