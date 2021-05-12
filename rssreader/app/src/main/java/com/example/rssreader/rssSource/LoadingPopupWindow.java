package com.example.rssreader.rssSource;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.rssreader.R;

public class LoadingPopupWindow extends PopupWindow {
    private View view;
    private ProgressBar progressBar;    //进度条
    private TextView loadingText;       //进度条下方显示文字

    public LoadingPopupWindow(Context context){
        this.view = LayoutInflater.from(context).inflate(R.layout.rss_loading_popupwindow, null);
        this.progressBar = (ProgressBar)view.findViewById(R.id.rss_loading_progressBar);
        this.loadingText = (TextView)view.findViewById(R.id.loading_text);
        //设置视图
        this.setContentView(view);
        //设置弹窗的长和宽
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        //设置背景色
        this.setBackgroundDrawable(context.getResources().getDrawable(android.R.color.transparent));
        //设置窗口为焦点
        this.setFocusable(false);
        //设置窗口不可点击
        this.setTouchable(false);
        //点击外部弹窗不会消失
        this.setOutsideTouchable(false);
    }

    //设置文本
    public void setLoadingText(String text){
        this.loadingText.setText(text);
    }
}
