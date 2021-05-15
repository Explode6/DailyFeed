package com.example.rssreader.rssSource;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.rssreader.R;
import com.example.rssreader.util.ApplicationUtil;
import com.example.rssreader.util.ConfigUtil;

public class TimeChooseDialog extends Dialog {
    private View view;
    private Button closeBtn;
    public Button okBtn;
    public Button openOrCloseBtn;
    public TextView chosenTime;
    public TextView noticeTextView;
    private int hour;
    private int minute;
    private TimePicker timePicker;
    private ConfigUtil configUtil;

    public TimeChooseDialog(Context context){
        super(context);
        //将自定义布局加载到dialog
        view = LayoutInflater.from(context).inflate(R.layout.time_choose_dialog, null, false);
        this.addContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        this.setContentView(view);
        //获取configUtil实例
        configUtil = ConfigUtil.getInstance(ApplicationUtil.getContext());
        //dialog可点击
        this.setCancelable(true);
        //点击外部dialog不会消失
        this.setCanceledOnTouchOutside(false);
        //获取timePicker
        timePicker = (TimePicker)view.findViewById(R.id.time_choose);
        //获取显示时间的textview
        chosenTime = (TextView)view.findViewById(R.id.chosen_time_textview);
        //如果定时通知开启则显示设置的时间
        if(configUtil.getHour()>=0){
            this.hour = configUtil.getHour();
            this.minute = configUtil.getMinute();
            changeTime(hour, minute);
        }
        noticeTextView = (TextView)view.findViewById(R.id.notice_textview);
        //设定timePicker监听器
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int i, int i1) {
                TimeChooseDialog.this.hour = i;
                TimeChooseDialog.this.minute = i1;
                changeTime(i, i1);
            }
        });
    }
    //设置按钮监听事件
    public void setListener(View.OnClickListener itemsListener){
        closeBtn = (Button)view.findViewById(R.id.close_time_choose_dialog_btn);
        closeBtn.setOnClickListener(itemsListener);
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
    //改变textview显示的时间
    public void changeTime(int h, int m){
        String timeText = String.valueOf(h);
        timeText += ":";
        timeText += String.valueOf(m);
        chosenTime.setText(timeText);
    }
    //设置默认时间
    public void setDefaultTime(){
        this.hour = 10;
        this.minute = 30;
        changeTime(10, 30);
    }
}
