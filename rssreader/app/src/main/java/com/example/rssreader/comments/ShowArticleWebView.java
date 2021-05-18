package com.example.rssreader.comments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.RequiresApi;

import com.example.rssreader.R;
import com.example.rssreader.util.ConfigUtil;

import java.util.List;

/**
 * @ClassName: ShowArticleWebView
 * @Author: Von
 * @Date: 2021 /4/27
 * @Description: 展示文章内容的WebView
 */
public class ShowArticleWebView extends WebView {

    //绑定文章标题
    private String title;
    //绑定文章作者
    private String author;
    //绑定的文章内容
    private String text;
    //设定的文字大小，默认值100
    private int textSize = 100;
    //设定的文字颜色，默认值0x000000（黑色）
    private int textColor = 0x000000;
    //设定的行间距离，默认值100
    private int lineHeight = 110;
    //设置WebView的对象
    private WebSettings webSettings;
    //封装了JavaScript代码的内部类
    private JS js;
    private ShowArticleWebView wv;

    //WebView信息交互的的回调接口
    interface getSelectedDataCallBack{
        void onReceiveData(String data);
    }

    interface onActionClickListener{
        void onActionClick(String title, String text);
    }

    /**
     * Instantiates a new Show article web view.
     *
     * @param context the context
     */
    public ShowArticleWebView(Context context) {
        super(context);
    }

    /**
     * Instantiates a new Show article web view.
     *
     * @param context      the context
     * @param attrs        the attrs
     * @param defStyleAttr the def style attr
     */
    public ShowArticleWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * Instantiates a new Show article web view.
     *
     * @param context the context
     * @param attrs   the attrs
     */
    public ShowArticleWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    /**
     * 用全局设置中的文字版式初始化对象
     *
     * @param textSize    文字大小
     * @param textColor   文字颜色
     * @param lineHeight  行间距离
     */
    @SuppressLint("SetJavaScriptEnabled")
    public void initWebView(int textSize, int textColor, int lineHeight){

        //用设置清单中的配置初始化WebView的文字效果
        this.textSize = textSize;
        this.textColor = textColor;
        this.lineHeight = lineHeight;
        //获取设置对象和JS工具对象
        webSettings = this.getSettings();
        this.js = new JS();
        //使用JS
        this.webSettings.setJavaScriptEnabled(true);
        this.webSettings.setDomStorageEnabled(true);
        //默认编码方式
        this.webSettings.setDefaultTextEncodingName("utf-8");
        //禁止缩放
        webSettings.setSupportZoom(false);
        webSettings.setDisplayZoomControls(false);
        //阻塞网页加载
        webSettings.setBlockNetworkImage(true);
        this.setWebViewClient(getClient());
        //设置背景色透明
        this.setBackgroundColor(0);
        wv = this;
    }

    /**
     * 获取网页加载图片的代理
     *
     * @return the WebViewClient
     */
    private WebViewClient getClient(){
        return new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                view.setVisibility(View.VISIBLE);
                //开始加载网页
                wv.webSettings.setBlockNetworkImage(false);
                if (!wv.webSettings.getLoadsImagesAutomatically()) {
                    wv.webSettings.setLoadsImagesAutomatically(true);
                }
                wv.webSettings.setBlockNetworkImage(true);
            }
        };
    }


    /**
     * 绑定WebView展现的内容，并进行渲染及展示
     *
     * @param title 文章标题
     * @param author 文章作者
     * @param content 文章内容
     */
    public void showContent(String title, String author, String content){
        //绑定信息
        this.title = title;
        this.author = author;
        this.text = content;
        //获取渲染并展示
        content = this.js.getHtml(this);
        this.loadDataWithBaseURL(null, content,"text/html","utf-8",null);
    }

    /**
     * 设置字体大小
     *
     * @param type 字体大小
     */
    public void setTextSize(int type){
        this.textSize = type;
        this.webSettings.setTextZoom(type);
    }

    /**
     * 设置字体颜色
     *
     * @param color 字体颜色
     */
    public void setTextColor(int color){
        this.textColor = color;
        this.showContent(this.title, this.author, this.text);
    }

    /**
     * 设置行间距
     *
     * @param lineHeight 行间距
     */
    public void setTextSpacing(int lineHeight){
        this.lineHeight = lineHeight;
        this.showContent(this.title, this.author, this.text);
    }

    /**
     * 获取选中内容
     *
     * @param callBack 返回选中内容的回调对象
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void getSelectedData(final getSelectedDataCallBack callBack){
        //在WebView中插入JavaScript代码并设定返回值的回调CallBack
        this.evaluateJavascript(this.js.getGetSelectedJs(), new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String text) {
                //去除文本中的换行符
                text.replace("\n", "");
                //回调传递JS获得的内容
                callBack.onReceiveData(text);
            }
        });
    }

    /**
     * 在用户选中文本时，添加自定义的ActionMode对象
     *
     * @param actionMode 系统原ActionMode
     * @param actionList 封装的自定义列表
     * @param listener   ActionMode被点击的监听器，用于回调返回选中的内容和点击的按钮标题
     * @return 重新封装的自定义ActionMode
     */
    public ActionMode getActionMode(final ActionMode actionMode,
                                    List<String> actionList,
                                    final onActionClickListener listener){
        if(actionMode != null){
            Menu menu = actionMode.getMenu();
            //记录Web Search的id
            int id = menu.getItem(menu.size()-1).getItemId();
           // 加入自定义的菜单, 设定order偏移量为6，在该页中从第4个开始展示
            for(int i =0; i<actionList.size(); ++i) menu.add(1, i, i+6, actionList.get(i));
            //设置点击事件
            for(int i = 3; i < actionList.size()+4; i++){
                MenuItem item = menu.getItem(i);
                //跳过Web Search
                if(item.getItemId()==id) continue;
                item.setShowAsAction(6);
                item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        //获取标题
                        final String title = menuItem.getTitle().toString();
                        getSelectedData(new getSelectedDataCallBack() {
                            @Override
                            public void onReceiveData(String data) {
                                actionMode.finish();
                                //回调获取按钮的标题以及选中的内容
                                listener.onActionClick(title, data);
                            }
                        });
                        return true;
                    }
                });
            }
        }
        return actionMode;
    }

    /**
     * 获得网页长图
     *
     * @return BitMap类型的网页内容截图
     */
    @SuppressLint("ResourceAsColor")
    public Bitmap getPicture(){
        //设定尺寸布局
        //public final void measure(int widthMeasureSpec, int heightMeasureSpec)
        this.measure(MeasureSpec.makeMeasureSpec(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        this.layout(0, 0, this.getMeasuredWidth(), this.getMeasuredHeight());
        //创建Bitmap对象承载图象，尺寸应与WebView内容一致
        Bitmap longImage = Bitmap.createBitmap(this.getMeasuredWidth(),
                this.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        //填充背景色
        longImage.eraseColor(R.color.dayWhiteNightBlack);
        //创建Canvas对象作为作图工具
        Canvas canvas = new Canvas(longImage);
        Paint paint = new Paint();
        canvas.drawBitmap(longImage, 0, this.getMeasuredHeight(), paint);
        //调用WebView的方法将内容输出到画布上
        this.draw(canvas);
        return longImage;
    }


    //JavaScrip工具代码
    private class JS{
        //用于获取用户在WebView中选中的内容
        private String selectedJs = "function selectEnable(document) {"
                + "    document.oncontextmenu = new Function(\"if(event){event.returnValue=true;}\");"
                + "    document.onselectstart = new Function(\"if(event){event.returnValue=true;}\");"
                + "    let frames = document.getElementsByTagName(\"iframe\");"
                + "    if (frames.length > 0) {"
                + "        for (var i = 0; i < frames.length; i++) {"
                + "            document = frames[i].contentDocument;"
                + "            selectEnable(document);"
                + "        }"
                + "    }"
                + "}"
                + "function selectTxt(document) {"
                + "    let rtnTxt = \"\";"
                + "    rtnTxt = document.getSelection ? document.getSelection().toString() : document.selection.createRange().text;"
                + "    let frames = document.getElementsByTagName(\"iframe\");"
                + "    if (frames.length > 0) {"
                + "        for (var i = 0; i < frames.length; i++) {"
                + "            document = frames[i].contentDocument;"
                + "            let sltTxt = selectTxt(document);"
                + "            if (sltTxt != \"\") {"
                + "                rtnTxt = sltTxt;"
                + "            }"
                + "        }"
                + "    }"
                + "    return rtnTxt;"
                + "}"
                + "(function(){"
                + "    selectEnable(document);"
                + "    return selectTxt(document);"
                + "})()";

        /**
         * 获取设定文字颜色和间距的Html5代码
         *
         * @param textSize    文字大小
         * @param textColor   文字颜色
         * @param lineHeight 文字间距
         * @return 设定样式的Html5代码
         */
        public String getStyle(int textSize,  int textColor , int lineHeight){
            //类型转换，注意int转颜色类型
            String stringTestColor = String.format("#%06X", 0xFFFFFF & textColor);
            return "<style id=\"localStyle\"  type=\"text/css\">*{"
                    //+"color: " + stringTestColor + " !important; "
                    + "line-height:"+ lineHeight + "% !important;"
                    //+ "font-size:" + textSize +"% !important;"
                    + "}</style>";
        }

        public String getTitle(String title, String author){
            return "<div style = \"border-bottom:1px solid rgba(0,0,0,.2); margin:0px;\">"
                    + "<h2>" + title + "</h2>"
                    +"<div align = \"right\"><p style=\"margin-bottom:1px; margin-right:5px;\">" +author+"</p></div>"
                    +"</div>";
        }

        /**
         * 获取封装了文章内容的完整Html代码（**添加标题和作者等资料
         *
         * @param webView  从ShowArticleWebView对象中获取文章的内容、标题、作者
         * @return String类型的Html文本
         */
        public String getHtml(ShowArticleWebView webView){
            String head = "<head>"
                    + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, user-scalable=no\"> "
                    + "<style>img{max-width: 100% !important; width:auto !important; height:auto !important;}</style>"
                    + "<style>video{max-width: 100% !important; width:auto !important; height:auto !important;}</style>"
                    + "<style>a{max-width: 100% !important; width:auto !important; height:auto !important;}</style>"
                    + getStyle(webView.textSize, webView.textColor, webView.lineHeight)
                    + "</head>";
            String jquery = "<script src=\"https://cdn.staticfile.org/jquery/3.2.1/jquery.min.js\"></script>";
            String body = "<body style = \"margin:5px;\">"
                    + webView.text  + jquery
                    + "</body>";
            return "<html>" + head + getTitle(webView.title, webView.author) + body + "</html>";
        }

        /**
         * 获取选中内容的JavaScript代码
         *
         * @return String类型的JavaScript文本
         */
        public String getGetSelectedJs(){ return "javascript:" + this.selectedJs;}
    }

}
