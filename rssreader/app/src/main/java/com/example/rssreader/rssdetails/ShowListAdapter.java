package com.example.rssreader.rssdetails;

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
import com.bumptech.glide.request.RequestOptions;
import com.example.rssreader.R;
import com.example.rssreader.model.datamodel.ArticleBrief;

import java.util.List;

/**
 * @ClassName ShowListAdapter
 * @Author HaoHaoGe
 * @Date 2021/5/13
 * @Description
 */
public class ShowListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {

    private List<ArticleBrief> mArticleBriefList;

    private static int ITEM_TYPE = 0;
    private static int ITEM_TYPE_LOAD = 1;
    private static int ITEM_TYPE_END = 2;

    /*
     * 三个按钮对应的点击监听接口
     */
    private OnCollectClickListener mCollectClickListener;
    private OnItemClickListener mListener;
    private OnMarkReadClickListener mMarkReadClickListener;
    private OnShareClickListener mShareClickListener;

    //FooterView最下面显示是否有文章
    private FooterHolder mFooterHolder;

    private Context mContext;

    @Override
    public void onClick(View view) {
        if(mListener != null){
            mListener.onItemClick(this, view, (Integer)view.getTag());
        }
    }


    /**
     * 这里是recyclerView中的文章项
     * 其中包括了文章的标题title、简介brief、文章图片、还有三个按钮
     */
    static public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView articleImage = null;
        TextView articleTitle = null;
        TextView articleBrief = null;
        Button collectArticle = null;
        Button markRead = null;

        private SparseArray<View> mViews;

        public ViewHolder(View view){
            super(view);

            mViews = new SparseArray<>();

            articleImage = (ImageView) view .findViewById(R.id.artitle_image);
            articleTitle = (TextView) view.findViewById(R.id.artitle_title);
            articleBrief = (TextView) view.findViewById(R.id.artitle_brief);
            collectArticle = (Button) view.findViewById(R.id.collect_article);
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


    /**
     * FooterView用于提示是否还有文章没显示
     */
    public static class FooterHolder extends RecyclerView.ViewHolder{
        TextView textView = null;
        public FooterHolder(View view) {
            super(view);
            textView = view.findViewById(R.id.footer_text);
        }
    }

    /*
     * adapter维护一个对应的list，能够在缓存中修改数据
     */
    public ShowListAdapter(List<ArticleBrief> articleBriefList){
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
                    R.layout.showlist_footer,
                    parent,
                    false);
            mFooterHolder = new FooterHolder(footerView);
            return mFooterHolder;
        }
        //加载正常的文章item
        else{
            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.showlist_item,
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


            /*
             * 设置ArticleBrief的图片样式
             * 如果有图片的话需要用url加载图片
             */
            if(articleBrief.getFirstPhoto()!=null) {
                String url = articleBrief.getFirstPhoto();
                ((ViewHolder) holder).articleImage.setVisibility(View.VISIBLE);
                RequestOptions options = new RequestOptions()
                        //还未加载完成时，用isLoading作为占位图
                        .placeholder(R.drawable.article_list_image_isloading)
                        //加载失败用error图占位
                        .error(R.drawable.article_list_image_error);
                Glide.with(mContext)
                        .load(articleBrief.getFirstPhoto())
                        .apply(options)
                        .into(((ViewHolder) holder).articleImage);
            }
            //如果这篇文章没有图片，则不需显示图片
            else{
                ((ViewHolder) holder).articleImage.setVisibility(View.GONE);

                /*
                 * UI界面小尝试
                 */
                ((ViewHolder) holder).markRead.setVisibility(View.GONE);
            }



            /*
             * 如果文章已读的话，把字体颜色变灰，区分已读未读
             */
            if(articleBrief.getRead()){
                ((ViewHolder) holder).articleTitle.setTextColor(mContext.getResources().getColor(R.color.brief_or_isread_gray));
            }else{
                //((ViewHolder) holder).articleTitle.setTextColor(mContext.getResources().getColor(R.color.dayBlackNightGray));
            }
            ((ViewHolder)holder).articleTitle.setText(articleBrief.getTitle());
            ((ViewHolder)holder).articleBrief.setText(articleBrief.getDescription());


            /*
             * 收藏和已读按钮也会跟随文章的属性变化
             */
            if(articleBrief.getCollect()) {
                ((ViewHolder) holder).collectArticle.setText("取消收藏");
            }else{
                ((ViewHolder) holder).collectArticle.setText("收藏");
            }

            if(articleBrief.getRead()) {
                ((ViewHolder) holder).markRead.setText("已读");
            }else{
                ((ViewHolder) holder).markRead.setText("未读");
            }
            /**
             * 绑定删除按钮的点击事件，需要在view层实现
             *
             */
            View collectionView = ((ViewHolder)holder).getView(R.id.collect_article);
            collectionView.setTag(position);
            if (!collectionView.hasOnClickListeners()) {
                collectionView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mCollectClickListener != null) {
                            mCollectClickListener.onCollectClick(view, (Integer) view.getTag());
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

            View shareView = ((ViewHolder)holder).getView(R.id.share_article_list);
            shareView.setTag(position);
            if (!shareView.hasOnClickListeners()) {
                shareView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mShareClickListener != null) {
                            mShareClickListener.onShareClick(view, (Integer) view.getTag());
                        }
                    }
                });
            }
        }else if(holder instanceof FooterHolder){
            if(mArticleBriefList.size() < 10) {
                ((FooterHolder) holder).textView.setText("没有更多文章了哦");
            }
        }
    }

    @Override
    public int getItemCount() {
        return mArticleBriefList.size() + 1;
    }


    /**
     * 暴露删除的点击事件给view层，让view层自定义删除操作
     *
     * @param listener 也就是下方定义的OnCollectClickListener接口
     */
    public void setOnCollectClickListener(OnCollectClickListener listener){
        this.mCollectClickListener = listener;
    }
    public interface OnCollectClickListener{
        void onCollectClick(View view, int position);
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
     * 暴露分享的点击事件给view层，让view层实现点击文章进入文章详情页
     *
     * @param listener 也就是下方定义的OnShareClickListener接口
     */
    public void setOnShareClickListener(OnShareClickListener listener){
        this.mShareClickListener = listener;
    }
    public interface OnShareClickListener{
        void onShareClick(View v, int postion);
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
    public ArticleBrief getArticleBrief(int position){
        return mArticleBriefList.get(position);
    }

    /*
     * 改变FooterView样式
     */
    public void setLoadCompletely(){
        if(mFooterHolder != null) {
            mFooterHolder.textView.setText("没有更多文章了哦");
        }
    }
    public void setLoadFirstly() {
        if (mFooterHolder != null) {
            mFooterHolder.textView.setText("加载更多");
        }
    }


    /*
     * 点击按钮时对应的变化，需要修改界面以及写进数据库两个部分
     * 其中这个部分是view的变化，也就是先把显示的界面修改
     */
    public void switchRead(int position){
        ArticleBrief articleBrief = mArticleBriefList.get(position);
        boolean isRead = articleBrief.getRead();
        articleBrief.setRead(!isRead);
        this.notifyItemChanged(position);
    }
    public void markRead(int position){
        mArticleBriefList.get(position).setRead(true);
        this.notifyItemChanged(position);
    }
    public void switchCollected(int position){
        ArticleBrief articleBrief = mArticleBriefList.get(position);
        boolean isCollect = articleBrief.getCollect();
        articleBrief.setCollect(!isCollect);
        this.notifyItemChanged(position);
    }


    /** 取消收藏，采用notifyItemRemoved能够显示删除的动画，如果用notifyDataSetChanged只能整个页面重新刷新，可能还会有闪烁
     *  但是ItemRemoved有bug，删除之后并不会将那一项后面的内容重新绑定，所以后面的内容仍然保留着原先的position，如果这时候用position对那几项访问时，会出现错位
     *  错位的内容只会出现在所有可视的item中，在removed的之后的item
     *  所以需要用notifyItemRangeChanged把后面的内容全都重新绑定position
     * @param position
     */
    public void cancelCollected(int position){
        ArticleBrief articleBrief = mArticleBriefList.get(position);
        articleBrief.setCollect(false);
        this.notifyItemRemoved(position);
        mArticleBriefList.remove(position);
        this.notifyItemRangeChanged(position, mArticleBriefList.size());
    }
}
