package com.example.rssreader.rssSource;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;

import com.example.rssreader.R;

public class BottomPopupWindow extends PopupWindow {
    private View view;
    //分别为全选按钮，取消按钮，删除按钮
    private Button selectAllBtn, cancelBtn, delBtn;

    public BottomPopupWindow(Context mContext, View.OnClickListener itemsOnClick){
        this.view = LayoutInflater.from(mContext).inflate(R.layout.rss_bottom_window, null);
        selectAllBtn = (Button)view.findViewById(R.id.select_all_btn);
        cancelBtn = (Button)view.findViewById(R.id.cancel_btn);
        delBtn = (Button)view.findViewById(R.id.del_rss_btn);
        //取消按钮监听事件
        cancelBtn.setOnClickListener(itemsOnClick);
        //利用外部传入的接口设置按钮点击事件
        selectAllBtn.setOnClickListener(itemsOnClick);
        delBtn.setOnClickListener(itemsOnClick);
        /* 设置底部弹窗的特征*/
        //设置视图
        this.setContentView(this.view);
        //设置弹窗的长和宽
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        //设置窗口为焦点
        this.setFocusable(false);
        //设置窗口可点击
        this.setTouchable(true);
        //点击外部弹窗不会消失
        this.setOutsideTouchable(false);
        //设置背景色
        this.setBackgroundDrawable(new ColorDrawable(0xb0000000));
        //设置弹窗动画效果
        this.setAnimationStyle(R.style.bottomWindowAnim);
    }
}
