package com.example.ArticleList.articlelist;

import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ArticleList.Data.Article;
import com.example.ArticleList.R;

import java.util.List;

/**
 * @ClassName ArticleAdapter
 * @Author HaoHaoGe
 * @Date 2021/4/25
 * @Description
 */
public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ViewHolder> implements View.OnClickListener {

    private List<Article> mArticleList;

    private OnDeleteClickListener mDeleteClickListener;
    private OnItemClickListener mListener;
    @Override
    public void onClick(View view) {
        if(mListener != null){
            mListener.onItemClick(this, view, (Integer)view.getTag());
        }
    }
    public void setOnItemClickListener(OnItemClickListener listener){
        this.mListener = listener;
    }


    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView articleImage = null;
        TextView articleTitle = null;
        TextView articleBrief = null;
        Button deleteCollection = null;

        private SparseArray<View> mViews;

        public ViewHolder(View view){
            super(view);

            mViews = new SparseArray<>();

            articleImage = (ImageView) view .findViewById(R.id.artitle_image);
            articleTitle = (TextView) view.findViewById(R.id.artitle_title);
            articleBrief = (TextView) view.findViewById(R.id.artitle_brief);
            deleteCollection = (Button) view.findViewById(R.id.delete_collection);
        }

        View getView(int viewId){
            View view = mViews.get(viewId);
            if(view == null){
                view = itemView.findViewById(viewId);
                mViews.put(viewId, view);
            }
            return view;
        }
    };

    public ArticleAdapter(List<Article> articleList){
        mArticleList = articleList;
    }

    public void changeData(List<Article> articleList){
        mArticleList = articleList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.article_item,
                parent,
                false);

        view.setOnClickListener(this);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Article article = mArticleList.get(position);
        holder.itemView.setTag(position);
        holder.articleImage.setImageResource(article.getImageId());
        holder.articleTitle.setText(article.getTitle());
        holder.articleBrief.setText(article.getBrief());

        View view = holder.getView(R.id.delete_collection);
        view.setTag(position);
        if(!view.hasOnClickListeners()){
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mDeleteClickListener != null){
                        mDeleteClickListener.onDeleteClick(view, (Integer) view.getTag());
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mArticleList.size();
    }

    public interface OnItemClickListener{
        void onItemClick(RecyclerView.Adapter adapter, View v, int postion);
    }


    public void setOnDeleteClickListener(OnDeleteClickListener listener){
        this.mDeleteClickListener = listener;
    }

    public interface OnDeleteClickListener{
        void onDeleteClick(View view, int position);
    }
}
