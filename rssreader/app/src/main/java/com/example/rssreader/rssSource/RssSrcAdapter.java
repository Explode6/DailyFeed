package com.example.rssreader.rssSource;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.rssreader.R;
import com.example.rssreader.RssSource;

import java.util.Iterator;
import java.util.List;

public class RssSrcAdapter extends RecyclerView.Adapter<RssSrcAdapter.ViewHolder> {
    private Context context;
    private boolean mainType;       //主界面显示类型（列表/网格）
    private boolean canEdit;    //记录是否进入编辑模式
    private List<RssSource> rssSourceList;
    private RssOnClickListener rssOnClickListener;
    private RssLongClickListener rssLongClickListener;

    public RssSrcAdapter(List<RssSource>list, boolean type, boolean canEdit){
        this.mainType = type;
        this.rssSourceList = list;
        this.canEdit = canEdit;
    }
    public void setRssSourceList(List<RssSource>list){
        this.rssSourceList = list;
    }
    //暴露给外部的子项点击事件接口
    public interface RssOnClickListener{
        void rssOnClickListener(int pos);
    }
    //暴露给外部的子项长按事件接口
    public interface RssLongClickListener{
        void rssLongClickListener();
    }

    public void setRssOnClickListener(RssOnClickListener rssOnClickListener){
        this.rssOnClickListener = rssOnClickListener;
    }
    public void setRssLongClickListener(RssLongClickListener rssLongClickListener) {
        this.rssLongClickListener = rssLongClickListener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView rssCheckbox;
        ImageView rssSrcImg;
        TextView rssSrcIntro;
        TextView rssSrcTitle;
        CardView cardView;      //子项的最外层布局（卡片布局）
        public ViewHolder(View view){
            super(view);
            cardView = (CardView)view;
            rssCheckbox = (ImageView)view.findViewById(R.id.rss_checkbox);
            rssSrcImg = (ImageView)view.findViewById(R.id.rss_source_img);
            rssSrcIntro = (TextView)view.findViewById(R.id.rss_source_intro);
            rssSrcTitle = (TextView)view.findViewById(R.id.rss_source_title);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(context == null){
            context = parent.getContext();
        }
        //主界面默认网格布局
        View view;
        //显示网格布局
        if(mainType == true){
            view = LayoutInflater.from(context).inflate(R.layout.rss_item_grid,
                    parent, false);
        }
        //显示列表布局
        else{
            view = LayoutInflater.from(context).inflate(R.layout.rss_item_list,
                    parent, false);
        }
        final ViewHolder holder = new ViewHolder(view);
        //为子项设置点击监听事件
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //编辑模式和非编辑模式下点击事件不同
                if(canEdit == true){
                    if(rssOnClickListener != null){
                        int pos = holder.getAdapterPosition();
                        if(rssSourceList.get(pos).getSelected() == false)
                            rssSourceList.get(pos).setSelected(true);
                        else
                            rssSourceList.get(pos).setSelected(false);
                        rssOnClickListener.rssOnClickListener(pos);
                    }
                }
                else{
                    int pos = holder.getAdapterPosition();
                    rssOnClickListener.rssOnClickListener(pos);
                }
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RssSource rssSource = rssSourceList.get(position);
        //根据item是否被选中，决定显示复选框是否被选中
        if(rssSourceList.get(position).getSelected() == true)
            holder.rssCheckbox.setImageResource(R.drawable.checkbox_checked);
        else
            holder.rssCheckbox.setImageResource(R.drawable.checkbox_unchecked);
        //绑定数据
        if(rssSource.getImage() == null)
            Glide.with(context)
                    .load(R.drawable.nav_day_img)
                    .into(holder.rssSrcImg);
        else {
            RequestOptions options = new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.NONE);
            Glide.with(context)
                    .load(rssSource.getImage())
                    .apply(options)
                    .into(holder.rssSrcImg);
        }
        holder.rssSrcTitle.setText(rssSource.getTitle());
        holder.rssSrcIntro.setText(rssSource.getIntro());
        //如果有子项被长按就显示复选框
        if(canEdit == true)
            holder.rssCheckbox.setVisibility(View.VISIBLE);
        else
            holder.rssCheckbox.setVisibility(View.INVISIBLE);
    }

    @Override
    public int getItemCount() {
        return rssSourceList.size();
    }
    
    public void setCanEdit(boolean canEdit){
        this.canEdit = canEdit;
    }
}


