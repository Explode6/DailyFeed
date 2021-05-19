package com.example.rssreader.rssdetails.collectionlist;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.rssreader.R;
import com.example.rssreader.comments.CommentsActivity;
import com.example.rssreader.model.datamodel.ArticleBrief;
import com.example.rssreader.rssdetails.ShowListAdapter;
import com.example.rssreader.rssdetails.ShowListContract;
import com.example.rssreader.rssdetails.SlideRecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName SearchListFragment
 * @Author HaoHaoGe
 * @Date 2021/5/17
 * @Description
 */
public class SearchListFragment extends Fragment implements ShowListContract.View{

    //presenter层
    SearchListPresenter mPresent;

    //recyclerView的adpter
    ShowListAdapter mShowListAdapter;

    Context mContext;

    SwipeRefreshLayout mSwipeRefreshLayout;

    EditText searchWord;

    Button searchButton;

    //单例模式获取fragment
    public static SearchListFragment newInstance(){
        return new SearchListFragment();
    }

    @Override
    public void setPresenter(ShowListContract.ShowListPresenter presenter) {
        mPresent = (SearchListPresenter) presenter;
    }

    /*
     * 重写onCreate函数，在一开始时用空的list初始化
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mShowListAdapter = new ShowListAdapter(new ArrayList<ArticleBrief>());
        mContext = getContext();
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
        final View root = inflater.inflate(R.layout.search_list_frag, container, false);

        searchWord = (EditText)root.findViewById(R.id.search_word);

        searchButton = (Button)root.findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String word = String.valueOf(searchWord.getText());
                mPresent.searchArticle(word);
            }
        });

        //绑定自定义的SlideRecyclerView，实现每一项能够侧滑的功能
        final SlideRecyclerView slideRecyclerView = (SlideRecyclerView)root.findViewById(R.id.slideview);
        slideRecyclerView.setAdapter(mShowListAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(slideRecyclerView.getContext(), RecyclerView.VERTICAL, false);

        //设置每一项的点击事件
        mShowListAdapter.setOnItemClickListener(new ShowListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView.Adapter adapter, View v, int position) {
                ArticleBrief articleBrief = mShowListAdapter.getArticleBrief(position);
                mPresent.openArticleDetails(articleBrief, position);

            }
        });

        //设置收藏的点击事件
        mShowListAdapter.setOnCollectClickListener(new ShowListAdapter.OnCollectClickListener() {
            @Override
            public void onCollectClick(View view, int position) {
                ArticleBrief articleBrief = mShowListAdapter.getArticleBrief(position);
                mPresent.cancelCollection(articleBrief, position);
            }
        });

        //设计标记已读的点击事件
        mShowListAdapter.setOnMarkReadClickListener(new ShowListAdapter.OnMarkReadClickListener() {
            @Override
            public void onMarkReadClick(View view, int position) {
                ArticleBrief articleBrief = mShowListAdapter.getArticleBrief(position);
                mPresent.switchRead(articleBrief, position);
            }
        });

        mShowListAdapter.setOnShareClickListener(new ShowListAdapter.OnShareClickListener() {
            @Override
            public void onShareClick(View v, int postion) {
                ArticleBrief articleBrief = mShowListAdapter.getArticleBrief(postion);
                mPresent.shareArticle(mContext, articleBrief);
            }
        });

        slideRecyclerView.setLayoutManager(layoutManager);

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
    public void showArticleList(List<ArticleBrief> articleBriefList, int begin, int size){
        mShowListAdapter.addArticleList(articleBriefList);
        mShowListAdapter.notifyItemRangeInserted(begin+1, size);
    }

    @Override
    public void refreshArticleList(List<ArticleBrief> articleBriefList) {
        mShowListAdapter.changeData(articleBriefList);
        mShowListAdapter.notifyDataSetChanged();
        mShowListAdapter.setLoadFirstly();
    }

    /**点击某一项从而进入对应的文章内容页
     * @param articleBrief 传入文章详情页的内容
     */
    @Override
    public void showArticleDetails(ArticleBrief articleBrief) {
        Intent intent = new Intent(mContext, CommentsActivity.class);
        intent.putExtra("articleBrief", articleBrief);
        startActivity(intent);
    }


    /**标记已读后刷新页面
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

    /**取消收藏后刷新页面
     * @param position 标记出这是第几项
     */
    @Override
    public void switchCollectionAndRefresh(int position){
        mShowListAdapter.cancelCollected(position);
    }

    /**
     * 报告信息
     * @param message 信息
     */
    @Override
    public void giveNoteMessage(String message) {
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void stopRefreshUI() {
        if(mSwipeRefreshLayout != null){
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void changeFooterViewStyle() {
        mShowListAdapter.setLoadCompletely();
    }


}


