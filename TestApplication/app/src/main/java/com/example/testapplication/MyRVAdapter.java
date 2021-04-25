package com.example.testapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MyRVAdapter extends RecyclerView.Adapter<MyRVAdapter.ViewHolder> {

    private List<RvItem> itemList;

    public MyRVAdapter(List <RvItem> rvItemList){
        itemList = rvItemList;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView name;
        Button rvbutton;
        public ViewHolder (View view){
            super(view);
            name = (TextView) view.findViewById(R.id.itemname);
            rvbutton = (Button) view.findViewById(R.id.itembutton);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_item,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        RvItem item = itemList.get(position);
        holder.name.setText(item.getName());
        holder.rvbutton.setText("pick");
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}
