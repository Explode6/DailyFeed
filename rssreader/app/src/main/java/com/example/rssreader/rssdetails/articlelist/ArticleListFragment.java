package com.example.rssreader.rssdetails.articlelist;


import android.content.Intent;
import android.os.Bundle;
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
import com.example.rssreader.rssdetails.ShowListAdapter;
import com.example.rssreader.rssdetails.ShowListContract;
import com.example.rssreader.rssdetails.SlideRecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName ArticleListFragment
 * @Author HaoHaoGe
 * @Date 2021/4/30
 * @Description 该页面用于展示RSS源下的所有文章内容，主体是一个RecyclerView展示文章列表
 */
public class ArticleListFragment extends Fragment implements ShowListContract.View {

    //presenter层
    ArticleListPresenter mPresent;

    //recyclerView的adpter
    ShowListAdapter mShowListAdapter;

    SwipeRefreshLayout mSwipeRefreshLayout;

    //单例模式获取fragment
    public static ArticleListFragment newInstance(){
        return new ArticleListFragment();
    }

    @Override
    public void setPresenter(ShowListContract.ShowListPresenter presenter) {
        mPresent = (ArticleListPresenter) presenter;
    }

    /*
     * 重写onCreate函数，在一开始时用空的list初始化
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mShowListAdapter = new ShowListAdapter(new ArrayList<ArticleBrief>());
    }

    /*
     * 在onResume方法中调用start方法，该方法从数据库中读取数据然后把有数据的list写进adapter
     */
    @Override
    public void onResume() {
        super.onResume();
        mPresent.start();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.article_list_frag, container, false);

        //绑定SwipeRefreshLayout，实现下拉刷新功能
        mSwipeRefreshLayout = root.findViewById(R.id.refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //下拉刷新时，根据该RSS源的地址检查有没有更新，然后将最新的文章展示
                mPresent.refreshChannel(getActivity());
            }
        });

        //绑定自定义的SlideRecyclerView，实现每一项能够侧滑的功能
        final SlideRecyclerView slideRecyclerView = (SlideRecyclerView)root.findViewById(R.id.slideview);
        slideRecyclerView.setAdapter(mShowListAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(slideRecyclerView.getContext(), RecyclerView.VERTICAL, false);

        //设置每一项的点击事件，用于点击文章进入到文章具体内容页面
        mShowListAdapter.setOnItemClickListener(new ShowListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView.Adapter adapter, View v, int position) {
                ArticleBrief articleBrief = mShowListAdapter.getArticleBrief(position);
                mPresent.openArticleDetails(articleBrief, position);

            }
        });

        //设置收藏的点击事件，用于将文章加入收藏或者是取消收藏
        mShowListAdapter.setOnCollectClickListener(new ShowListAdapter.OnCollectClickListener() {
            @Override
            public void onCollectClick(View view, int position) {
                ArticleBrief articleBrief = mShowListAdapter.getArticleBrief(position);
                mPresent.switchCollection(articleBrief, position);
            }
        });

        //设计标记已读的点击事件，用于将文章标记已读或者是取消已读
        mShowListAdapter.setOnMarkReadClickListener(new ShowListAdapter.OnMarkReadClickListener() {
            @Override
            public void onMarkReadClick(View view, int position) {
                ArticleBrief articleBrief = mShowListAdapter.getArticleBrief(position);
                mPresent.switchRead(articleBrief, position);
            }
        });

        slideRecyclerView.setLayoutManager(layoutManager);

        //上拉加载更多的10篇文章
        slideRecyclerView.addOnScrollListener(new SlideRecyclerView.LoadMoreOnScrollListener(){
            @Override
            public void loadMoreArticle() {
                mPresent.loadArticle();
            }
        });

        return root;
    }


    /**将从数据库中获取的articleBriefList展示到当前界面中（放到原先的文章后面）
     * @param articleBriefList 从数据库取得的数据
     * @param begin 原数据的末尾
     * @param size 新数据的长度
     */
    @Override
    public void showArticleList(List<ArticleBrief> articleBriefList, int begin, int size){
        mShowListAdapter.addArticleList(articleBriefList);
        mShowListAdapter.notifyItemRangeInserted(begin+1, size);
    }

    /**如果重新刷新，需要把数据全都换了，并且调用setChanged直接重新加载recyclerView
     * @param articleBriefList 新的数据list
     */
    public void refreshArticleList(List<ArticleBrief> articleBriefList){
        mShowListAdapter.changeData(articleBriefList);
        mShowListAdapter.notifyDataSetChanged();
        mShowListAdapter.setLoadFirstly();
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


    /**标记已读后刷新页面，用于点击文章进入文章内容页面之前，先把该文章标记为已读
     * @param position 标记出这是第几项
     */
    @Override
    public void switchReadAndRefresh(int position){
        mShowListAdapter.switchRead(position);
    }

    /**切换已读后刷新页面
     * @param position 标记出这是第几项
     */
    @Override
    public void markReadAndRefresh(int position){
        mShowListAdapter.markRead(position);
    }

    /**添加收藏后刷新页面
     * @param position 标记出这是第几项
     */
    @Override
    public void switchCollectionAndRefresh(int position){
        mShowListAdapter.switchCollected(position);
    }

    /**
     * 弹出信息
     * @param message 信息
     */
    @Override
    public void giveNoteMessage(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    /**
     * 下拉刷新对应的操作完成后，将刷新的进度条隐藏
     */
    public void stopRefreshUI() {
        if(mSwipeRefreshLayout != null){
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    /**
     * 文章全部展示时给出提示
     */
    @Override
    public void changeFooterViewStyle() {
        mShowListAdapter.setLoadCompletely();
    }

}
