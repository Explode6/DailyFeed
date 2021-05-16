package com.example.rssreader.articleCollection;


import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rssreader.R;
import com.example.rssreader.articlelist.SlideRecyclerView;
import com.example.rssreader.comments.CommentsActivity;
import com.example.rssreader.model.datamodel.ArticleBrief;

import java.util.ArrayList;
import java.util.List;

/**
 * The type Article collection fragment.
 *
 * @ClassName ArticleCollectionFragment
 * @Author HaoHaoGe
 * @Date 2021 /4/30
 * @Description
 */
public class ArticleCollectionFragment extends Fragment implements ArticleCollectionContract.View {

    //presenter层
    ArticleCollectionContract.ArticleCollectionPresenter mPresent;

    //recyclerView的adpter
    ArticleCollectionAdapter mArticleCollectionAdapter;

    //单例模式获取fragment
    public static ArticleCollectionFragment newInstance(){
        return new ArticleCollectionFragment();
    }

    @Override
    public void setPresenter(ArticleCollectionContract.ArticleCollectionPresenter presenter) {
        mPresent = presenter;
    }

    /*
     * 重写onCreate函数，在一开始时用空的list初始化
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mArticleCollectionAdapter = new ArticleCollectionAdapter(new ArrayList<ArticleBrief>());
    }

    /*
     * 在onResume方法中调用start方法，该方法从数据库中读取数据然后把有数据的list写进adapter
     */
    @Override
    public void onResume() {
        super.onResume();
        try {
            mPresent.start();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.article_collection_frag, container, false);


        //绑定自定义的SlideRecyclerView，实现每一项能够侧滑的功能
        final SlideRecyclerView slideRecyclerView = (SlideRecyclerView)root.findViewById(R.id.article_collection_slideview);
        slideRecyclerView.setAdapter(mArticleCollectionAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(slideRecyclerView.getContext(), RecyclerView.VERTICAL, false);

        //设置每一项的点击事件
        mArticleCollectionAdapter.setOnItemClickListener(new ArticleCollectionAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView.Adapter adapter, View v, int position) {
                ArticleBrief articleBrief = mArticleCollectionAdapter.getArticleBrief(position);
                mPresent.openArticleDetails(articleBrief, position);

            }
        });

        //设置收藏的点击事件
        mArticleCollectionAdapter.setOnDeleteClickListener(new ArticleCollectionAdapter.OnDeleteClickListener() {
            @Override
            public void onDeleteClick(View view, int position) {
                ArticleBrief articleBrief = mArticleCollectionAdapter.getArticleBrief(position);
                mPresent.switchCollection(articleBrief, position, articleBrief.getCollect());
            }
        });

        //设计标记已读的点击事件
        mArticleCollectionAdapter.setOnMarkReadClickListener(new ArticleCollectionAdapter.OnMarkReadClickListener() {
            @Override
            public void onMarkReadClick(View view, int position) {
                mPresent.markRead(position);
            }
        });

        slideRecyclerView.setLayoutManager(layoutManager);

        //注意：这里有问题，加载完成之后还是能够继续刷新
        slideRecyclerView.addOnScrollListener(new SlideRecyclerView.LoadMoreOnScrollListener(){
            @Override
            public void loadMoreArticle() {
                //加载后面10篇文章
                mPresent.loadArticle();
            }
        });

        return root;
    }


    /**把得到的新数据添加在原数据之后
     * @param articleBriefList 得到的新数据
     * @param begin 原数据的末尾
     * @param size 新数据的长度
     */
    @Override
    public void showArticleCollection(List<ArticleBrief> articleBriefList, int begin, int size){
        mArticleCollectionAdapter.addArticleList(articleBriefList);
        mArticleCollectionAdapter.notifyItemRangeInserted(begin, size);
    }

    /**点击某一项从而进入对应的文章内容页
     * @param articleBrief 传入文章详情页的内容
     */
    @Override
    public void showArticleDetails(ArticleBrief articleBrief) {
        Intent intent = new Intent(getContext(), CommentsActivity.class);
        intent.putExtra("articleBrief", articleBrief);
        startActivity(intent);
    }


    /**标记已读后刷新页面
     * @param position 标记出这是第几项
     */
    @Override
    public void markReadAndRefresh(int position){
        mArticleCollectionAdapter.switchRead(position);
    }

    /**添加收藏后刷新页面
     * @param position 标记出这是第几项
     */
    @Override
    public void switchCollectionAndRefresh(int position){
        mArticleCollectionAdapter.switchCollected(position);
    }

    /**
     * 报告错误信息
     * @param message 错误信息
     */
    @Override
    public void giveWrongMessage(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    /**
     * 改变FooterView，显示已经加载所有数据了
     */
    @Override
    public void changeFooterViewStyle() {
        mArticleCollectionAdapter.setLoadCompletely();
    }

    @Override
    public void giveLoadSuccessfulMessage() {
        Toast.makeText(getContext(), "加载成功", Toast.LENGTH_SHORT).show();
    }
}
