// XmlCallback.aidl
package com.example.rssreader.model.parse;

// Declare any non-default types here with import statements

interface XmlCallback {
    //读取XML成功
    void onLoadXmlSuccess();
    //格式错误
    void onUrlTypeError();
    //解析错误
    void onParseError();
    //源错误|网络错误
    void onSourceError();
    //读取图片错误
    void onLoadImgError();
    //读取图片成功
    void onLoadImgSuccess();

}