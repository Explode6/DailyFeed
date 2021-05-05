package com.example.rssreader.rssSource;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TimePicker;

import com.example.rssreader.R;

public class TimeChooseDialog extends Dialog {
    private View view;
    private Button cancelBtn;
    private Button okBtn;
    private Button openOrCloseBtn;

    private int hour;
    private int minute;
    private TimePicker timePicker;

    public TimeChooseDialog(Context context){
        super(context);
        //将自定义布局加载到dialog
        view = LayoutInflater.from(context).inflate(R.layout.time_choose_dialog, null, false);
        this.addContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        this.setContentView(view);
        //dialog可点击
        this.setCancelable(true);
        //点击外部dialog不会消失
        this.setCanceledOnTouchOutside(false);
        //获取timePicker
        timePicker = (TimePicker)view.findViewById(R.id.time_choose);
        //设定timePicker监听器
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int i, int i1) {
                TimeChooseDialog.this.hour = i;
                TimeChooseDialog.this.minute = i1;
            }
        });
    }
    //设置按钮监听事件
    public void setListener(View.OnClickListener itemsListener){
        cancelBtn = (Button)view.findViewById(R.id.cancel_time_choose_btn);
        cancelBtn.setOnClickListener(itemsListener);
        okBtn = (Button)view.findViewById(R.id.time_chosen_btn);
        okBtn.setOnClickListener(itemsListener);
        openOrCloseBtn = (Button)view.findViewById(R.id.close_time_choose);
        openOrCloseBtn.setOnClickListener(itemsListener);
    }
    //获取当前timePicker的时间
    public int getHour(){
        return this.hour;
    }
    public int getMinute(){
        return this.minute;
    }

}
