package com.example.rssreader.comments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rssreader.R;
import com.example.rssreader.model.datamodel.LocalComment;

import java.util.List;

public class LocalCommentsAdapter extends RecyclerView.Adapter<LocalCommentsAdapter.MyViewHolder>  {
    private List<LocalComment> mList;
    private Context mContext;
    public LocalCommentsAdapter(Context mContext, List<LocalComment> itemsList) {
        this.mContext = mContext;
        this.mList = itemsList;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.localcommentitem, parent, false);
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
        holder.local_date.setText(mList.get(position).getDate().toString());
        holder.local_content.setText(mList.get(position).getLocalContent());
        holder.local_comment.setText(mList.get(position).getComment());


    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView local_comment;
        private  TextView local_date;
        private  TextView  local_content;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            local_date= itemView.findViewById(R.id.local_comment_date);
            local_comment = itemView.findViewById(R.id.local_comment_Item);
            local_content= itemView.findViewById(R.id.local_content_Item);
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
}


