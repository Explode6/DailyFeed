package com.example.rssreader.comments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rssreader.R;
import com.example.rssreader.model.datamodel.GlobalComment;

import java.util.List;

public class GlobalCommentsAdapter extends RecyclerView.Adapter<GlobalCommentsAdapter.MyViewHolder>  {
    private List<GlobalComment> mList;
    private Context mContext;
    public GlobalCommentsAdapter(Context mContext, List<GlobalComment> itemsList) {
        this.mContext = mContext;
        this.mList = itemsList;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.globecommentitem, parent, false);
        MyViewHolder holder = new MyViewHolder(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        holder.itemView. setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(v,position);

            }
        });
        holder.globe_date.setText(mList.get(position).getDate().toString());
        holder.globe_comment.setText(mList.get(position).getComment());


    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView globe_comment;
        private  TextView globe_date;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            globe_date = itemView.findViewById(R.id.globe_comment_date);
            globe_comment = itemView.findViewById(R.id.globe_comment_Item);
        }
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener= onItemClickListener;
    }

     public interface OnItemClickListener {
        void onItemClick(View view, int position);
  }

  //根据position删除一项
    public void deleteItem(int position){
        mList.remove(position);
        notifyItemRemoved(position);
        this.notifyItemRangeChanged(position, mList.size());
    }

    //setTwoComments
    public void setTwoComments(TextView date1, TextView comment1, TextView date2, TextView comment2){
        if(mList.size()==0){
            date1.setText("ohhhhh");
            date2.setText("ohhhhh");
            comment1.setText("该评论空空如也，说点什么吧");
            comment2.setText("该评论空空如也，说点什么吧");
        }
        else if(mList.size()==1){
            comment1.setText(mList.get(0).getComment());
            date2.setText("ohhhhh");
            comment2.setText("该评论空空如也，说点什么吧");
        }
        else if(mList.size()>=2){
            date1.setText(mList.get(0).getDate().toString());
            comment1.setText(mList.get(0).getComment());
            date2.setText(mList.get(1).getDate().toString());
            comment2.setText(mList.get(1).getComment());
        }
    }
}


