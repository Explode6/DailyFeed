package com.example.rssreader.rssSource;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.rssreader.R;

public class AddRssSourceDialog extends Dialog {
    private View view;
    private Button closeBtn;
    private Button addBtn;
    private EditText inputRssSource;

    public AddRssSourceDialog(Context context){
        super(context);
        //将自定义布局加载到dialog
        view = LayoutInflater.from(context).inflate(R.layout.add_rss, null, false);
        this.addContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        this.setContentView(view);
        //dialog可点击
        this.setCancelable(true);
        //点击外部dialog不会消失
        this.setCanceledOnTouchOutside(false);
        //获取输入框
        this.inputRssSource = (EditText)view.findViewById(R.id.rss_link);
    }
    //设置按钮监听事件
    public void setListener(View.OnClickListener itemsListener){
        closeBtn = (Button)view.findViewById(R.id.close_add_rss_dialog_btn);
        closeBtn.setOnClickListener(itemsListener);
        addBtn = (Button)view.findViewById(R.id.add_rss_btn);
        addBtn.setOnClickListener(itemsListener);
    }

    //获取输入的RSS链接
    public String getInputRss(){
        return this.inputRssSource.getText().toString();
    }
}
