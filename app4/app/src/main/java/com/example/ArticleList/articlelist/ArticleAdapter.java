package com.example.ArticleList.articlelist;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;
import com.example.ArticleList.Data.ArticleBrief;
import com.example.ArticleList.R;

import java.util.List;

import static com.example.ArticleList.R.color.colorPrimary;

/**
 * @ClassName ArticleAdapter
 * @Author HaoHaoGe
 * @Date 2021/4/25
 * @Description 文章适配器用于展示文章简介页面的recyclerView，
 * 其中implements View.OnClickListener是用于感知每一项的点击，从而判断是否为侧滑
 */
public class ArticleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {

    private List<ArticleBrief> mArticleBriefList;

    private static int ITEM_TYPE = 0;
    private static int ITEM_TYPE_LOAD = 1;
    private static int ITEM_TYPE_END = 2;

    private OnDeleteClickListener mDeleteClickListener;
    private OnItemClickListener mListener;
    private OnMarkReadClickListener mMarkReadClickListener;

    private FooterHolder mFooterHolder;

    private Context mContext;

    @Override
    public void onClick(View view) {
        if(mListener != null){
            mListener.onItemClick(this, view, (Integer)view.getTag());
        }
    }

    static public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView articleImage = null;
        TextView articleTitle = null;
        TextView articleBrief = null;
        Button deleteCollection = null;
        Button markRead = null;

        private SparseArray<View> mViews;

        public ViewHolder(View view){
            super(view);

            mViews = new SparseArray<>();

            articleImage = (ImageView) view .findViewById(R.id.artitle_image);
            articleTitle = (TextView) view.findViewById(R.id.artitle_title);
            articleBrief = (TextView) view.findViewById(R.id.artitle_brief);
            deleteCollection = (Button) view.findViewById(R.id.delete_collection);
            markRead = (Button)view.findViewById(R.id.mark_as_read);
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

    public static class FooterHolder extends RecyclerView.ViewHolder{
        TextView textView = null;
        public FooterHolder(View view) {
            super(view);
            textView = view.findViewById(R.id.footer_text);
        }
    }

    public ArticleAdapter(List<ArticleBrief> articleBriefList){
        mArticleBriefList = articleBriefList;
    }



    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(mContext == null){
            mContext = parent.getContext();
        }
        //文章加载完了，显示footerView
        if(viewType == ITEM_TYPE_END){
            View footerView = LayoutInflater.from(mContext).inflate(
                    R.layout.article_footer,
                    parent,
                    false);
            mFooterHolder = new FooterHolder(footerView);
            return mFooterHolder;
        }
        //加载正常的文章item
        else{
            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.article_item,
                    parent,
                    false);

            view.setOnClickListener(this);
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof ViewHolder){

            ArticleBrief articleBrief = mArticleBriefList.get(position);
            holder.itemView.setTag(position);


            //设置ArticleBrief的图片样式
            //如果有图片的话需要用url加载图片
            if(articleBrief.isHavingImage()) {
                ((ViewHolder) holder).articleImage.setVisibility(View.VISIBLE);
                RequestOptions options = new RequestOptions()
                        //还未加载完成时，用isLoading作为占位图
                        .placeholder(R.drawable.article_image_isloading)
                        //加载失败用error图占位
                        .error(R.drawable.article_image_error);
                Glide.with(mContext)
                        .load(articleBrief.getImageId())
                        .apply(options)
                        .into(((ViewHolder) holder).articleImage);
            }
            //如果这篇文章没有图片，则不需显示图片
            else{
                ((ViewHolder) holder).articleImage.setVisibility(View.GONE);
            }

            if(articleBrief.isRead()){
                ((ViewHolder) holder).articleTitle.setTextColor(mContext.getResources().getColor(R.color.brief_or_isread_gray));
            }else{
                ((ViewHolder) holder).articleTitle.setTextColor(mContext.getResources().getColor(R.color.colorPrimaryDark));
            }
            ((ViewHolder)holder).articleTitle.setText(articleBrief.getTitle());
            ((ViewHolder)holder).articleBrief.setText(articleBrief.getDescription());

            if(articleBrief.isCollected()) {
                ((ViewHolder) holder).deleteCollection.setText("取消收藏");
            }else{
                ((ViewHolder) holder).deleteCollection.setText("收藏");
            }

            if(articleBrief.isRead()) {
                ((ViewHolder) holder).markRead.setText("已读");
            }else{
                ((ViewHolder) holder).markRead.setText("未读");
            }
            /**
             * 绑定删除按钮的点击事件，需要在view层实现
             *
             */
            View collectionView = ((ViewHolder)holder).getView(R.id.delete_collection);
            collectionView.setTag(position);
            if (!collectionView.hasOnClickListeners()) {
                collectionView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mDeleteClickListener != null) {
                            mDeleteClickListener.onDeleteClick(view, (Integer) view.getTag());
                        }
                    }
                });
            }


            View markView = ((ViewHolder)holder).getView(R.id.mark_as_read);
            markView.setTag(position);
            if (!markView.hasOnClickListeners()) {
                markView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mMarkReadClickListener != null) {
                            mMarkReadClickListener.onMarkReadClick(view, (Integer) view.getTag());
                        }
                    }
                });
            }
        }else if(holder instanceof FooterHolder){
            //test应该不需要这句
            //((FooterHolder)holder).textView.setText("下拉加载更多");
        }
    }

    @Override
    public int getItemCount() {
        return mArticleBriefList.size() + 1;
    }


    /**
     * 暴露删除的点击事件给view层，让view层自定义删除操作
     *
     * @param listener 也就是下方定义的OnDeleteClickListener接口
     */
    public void setOnDeleteClickListener(OnDeleteClickListener listener){
        this.mDeleteClickListener = listener;
    }
    public interface OnDeleteClickListener{
        void onDeleteClick(View view, int position);
    }

    /**
     * 暴露文章的点击事件给view层，让view层实现点击文章进入文章详情页
     *
     * @param listener 也就是下方定义的OItemClickListener接口
     */
    public void setOnItemClickListener(OnItemClickListener listener){
        this.mListener = listener;
    }
    public interface OnItemClickListener{
        void onItemClick(RecyclerView.Adapter adapter, View v, int postion);
    }

    /**
     * 暴露标记已读的点击事件给view层，让view层实现点击文章进入文章详情页
     *
     * @param listener 也就是下方定义的OnMarkReadClickListener接口
     */
    public void setOnMarkReadClickListener(OnMarkReadClickListener listener){
        this.mMarkReadClickListener = listener;
    }
    public interface OnMarkReadClickListener{
        void onMarkReadClick(View v, int postion);
    }





    /**
     * 第一次加载数据时，直接替换原有的list然后刷新recyclerView
     * 用于刷新时重新加载数据
     *
     * @param articleBriefList onresume方法调用时，得到的最开始的10篇文章
     */
    public void changeData(List<ArticleBrief> articleBriefList){
        mArticleBriefList = articleBriefList;
    }

    /**
     * 下拉加载更多时，拉取到新的数据，直接append在list后面
     *
     * @param articleBriefList the article list
     */
    public void addArticleList(List<ArticleBrief> articleBriefList){
        mArticleBriefList.addAll(articleBriefList);
    }



    /**
     * 重写getItemViewType，用来判定是否为recyclerView底部
     * 注意：这里的position是不是下标？用不用-1？
     */
    @Override
    public int getItemViewType(int position) {
        //如果已经在底部，将viewType置为1
        if(position == mArticleBriefList.size() - 1){
            return ITEM_TYPE_LOAD;
        }else if(position == mArticleBriefList.size()){
            return ITEM_TYPE_END;
        }else{
            return ITEM_TYPE;
        }
    }


    /*
     *获取点击时的文章
     */
    public String getArticleBrief(int position){
        return mArticleBriefList.get(position).getTitle();
    }

    public void setLoadCompletely(){
        if(mFooterHolder != null) {
            mFooterHolder.textView.setText("我也是有底线的");
        }
    }
    public void setLoadFirstly(){
        if(mFooterHolder != null) {
            mFooterHolder.textView.setText("加载更多");
        }
    }


    public void setRead(int position){
        mArticleBriefList.get(position).switchRead();
    }

    public void setCollected(int position){
        mArticleBriefList.get(position).switchCollected();
    }
}
