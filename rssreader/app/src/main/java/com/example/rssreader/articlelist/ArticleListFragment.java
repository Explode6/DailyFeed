package com.example.rssreader.articlelist;


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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.rssreader.R;
import com.example.rssreader.lastactivity.LastActivity;
import com.example.rssreader.model.datamodel.ArticleBrief;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName ArticleListFragment
 * @Author HaoHaoGe
 * @Date 2021/4/30
 * @Description
 */
public class ArticleListFragment extends Fragment implements ArticleListContract.View {

    //presenter层
    ArticleListContract.ArticleListPresenter mPresent;

    //recyclerView的adpter
    ArticleListAdapter mArticleListAdapter;

    //单例模式获取fragment
    public static ArticleListFragment newInstance(){
        return new ArticleListFragment();
    }

    @Override
    public void setPresenter(ArticleListContract.ArticleListPresenter presenter) {
        mPresent = presenter;
    }

    /*
     * 重写onCreate函数，在一开始时用空的list初始化
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mArticleListAdapter = new ArticleListAdapter(new ArrayList<ArticleBrief>());
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
        final View root = inflater.inflate(R.layout.article_list_frag, container, false);

        //绑定SwipeRefreshLayout，实现下拉刷新功能
        final SwipeRefreshLayout swipeRefreshLayout = root.findViewById(R.id.refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresent.reLoadArticle();
                Toast.makeText(root.getContext(), "刷新成功", Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        //绑定自定义的SlideRecyclerView，实现每一项能够侧滑的功能
        final SlideRecyclerView slideRecyclerView = (SlideRecyclerView)root.findViewById(R.id.article_list_slideview);
        slideRecyclerView.setAdapter(mArticleListAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(slideRecyclerView.getContext(), RecyclerView.VERTICAL, false);

        //设置每一项的点击事件
        mArticleListAdapter.setOnItemClickListener(new ArticleListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView.Adapter adapter, View v, int position) {
                ArticleBrief articleBrief = mArticleListAdapter.getArticleBrief(position);
                mPresent.openArticleDetails(articleBrief, position);

            }
        });

        //设置收藏的点击事件
        mArticleListAdapter.setOnDeleteClickListener(new ArticleListAdapter.OnDeleteClickListener() {
            @Override
            public void onDeleteClick(View view, int position) {
                ArticleBrief articleBrief = mArticleListAdapter.getArticleBrief(position);
                mPresent.addCollection(articleBrief, position);
            }
        });

        //设计标记已读的点击事件
        mArticleListAdapter.setOnMarkReadClickListener(new ArticleListAdapter.OnMarkReadClickListener() {
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
                boolean completeLoad = mPresent.loadArticle();
                if(!completeLoad) {
                    Toast.makeText(root.getContext(), "加载成功", Toast.LENGTH_SHORT).show();
                }
                //如果全都加载完成了，改变FooterView样式
                else{
                    mArticleListAdapter.setLoadCompletely();
                }
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
    public void showArticleList(List<ArticleBrief> articleBriefList, int begin, int size){
        mArticleListAdapter.addArticleList(articleBriefList);
        mArticleListAdapter.notifyItemRangeInserted(begin, size);
    }

    /**如果重新刷新，需要把数据全都换了，并且调用setChanged直接重新加载recyclerView
     * @param articleBriefList 新的数据list
     */
    @Override
    public void refreshArticleList(List<ArticleBrief> articleBriefList){
        mArticleListAdapter.changeData(articleBriefList);
        mArticleListAdapter.notifyDataSetChanged();
        mArticleListAdapter.setLoadFirstly();
    }

    /**点击某一项从而进入对应的文章内容页
     * @param articleBrief 传入文章详情页的内容
     */
    @Override
    public void showArticleDetails(ArticleBrief articleBrief) {
        Intent intent = new Intent(getContext(), LastActivity.class);
        intent.putExtra("articleBrief", articleBrief);
        startActivity(intent);
    }


    /**标记已读后刷新页面
     * @param position 标记出这是第几项
     */
    @Override
    public void markReadAndRefresh(int position){
        mArticleListAdapter.switchRead(position);
    }

    /**添加收藏后刷新页面
     * @param position 标记出这是第几项
     */
    @Override
    public void addCollectionAndRefresh(int position){
        mArticleListAdapter.switchCollected(position);
    }

    /**
     * 报告错误信息
     * @param message 错误信息
     */
    @Override
    public void giveWrongMessage(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}
