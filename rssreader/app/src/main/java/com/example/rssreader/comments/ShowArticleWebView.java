package com.example.rssreader.comments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.annotation.RequiresApi;

import java.util.List;

/**
 * @ClassName: ShowArticleWebView
 * @Author: Von
 * @Date: 2021 /4/27
 * @Description: 展示文章内容的WebView
 */
public class ShowArticleWebView extends WebView {

    //绑定的文章内容
    private String text;
    //设定的文字大小，默认值100
    private int textSize = 100;
    //设定的文字颜色，默认值0x000000（黑色）
    private int textColor = 0x000000;
    //设定的文字间隔，默认值0
    private int textSpacing = 0;
    //设置WebView的对象
    private WebSettings webSettings;
    //封装了JavaScript代码的内部类
    private JS js;

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
     * @param textSpacing 文字间隔
     */
    @SuppressLint("SetJavaScriptEnabled")
    public void initWebView(int textSize, int textColor, int textSpacing){

        //用设置清单中的配置初始化WebView的文字效果
        this.textSize = textSize;
        this.textColor = textColor;
        this.textSpacing = textSpacing;
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
        //设置背景色透明
        this.setBackgroundColor(0);
    }

    /**
     * 绑定WebView展现的内容，并进行渲染及展示
     *
     * @param content 文章内容
     */
    public void showContent(String content){
        //绑定内容
        this.text = content;
        //获取渲染并展示
        content = this.js.getHtml(this);
        this.setTextSize(this.textSize);
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
        this.showContent(this.text);
    }

    /**
     * 设置字体间距
     *
     * @param spacing 字体间距
     */
    public void setTextSpacing(int spacing){
        this.textSpacing = spacing;
        this.showContent(this.text);
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
           // 加入自定义的菜单
            for(String s:actionList) menu.add(1,1,2,s);
            //设置点击事件
            for(int i = 0; i < actionList.size(); i++){
                MenuItem item = menu.getItem(i);
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
    public Bitmap getPicture(){
        //设定尺寸布局
        //public final void measure(int widthMeasureSpec, int heightMeasureSpec)
        this.measure(MeasureSpec.makeMeasureSpec(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        this.layout(0, 0, this.getMeasuredWidth(), this.getMeasuredHeight());
        //创建Bitmap对象承载图象，尺寸应与WebView内容一致
        Bitmap longImage = Bitmap.createBitmap(this.getMeasuredWidth(),
                this.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
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
         * @param testColor   文字颜色
         * @param testSpacing 文字间距
         * @return 设定样式的Html5代码
         */
        public String getStyle(int testColor , int testSpacing){
            //类型转换，注意int转颜色类型
            String stringTestColor = String.format("#%06X", 0xFFFFFF & testColor);
            String stringTestSpacing = Integer.toString(testSpacing);
            return "<style>*{color: " + stringTestColor + "; letter-spacing:"+ stringTestSpacing+"px ;}</style>";
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
                    + "<style>img{max-width: 100%; width:auto; height:auto!important;}</style>"
                    + getStyle(webView.textColor, webView.textSpacing)
                    + "</head>";
            String jquery = "<script src=\"https://cdn.staticfile.org/jquery/3.2.1/jquery.min.js\"></script>";
            String body = "<body style = \" border-top:1px solid rgba(0,0,0,.2);\">"
                    + webView.text  + jquery
                    + "</body>";
            return "<html>" + head + body + "</html>";
        }

        /**
         * 获取选中内容的JavaScript代码
         *
         * @return String类型的JavaScript文本
         */
        public String getGetSelectedJs(){ return "javascript:" + this.selectedJs;}
    }

}
