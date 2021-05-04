package com.example.ArticleList.articlelist;


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

import com.example.ArticleList.Data.ArticleBrief;
import com.example.ArticleList.R;
import com.example.ArticleList.lastactivity.LastActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName ArticleListFragment
 * @Author HaoHaoGe
 * @Date 2021/4/30
 * @Description
 */
public class ArticleListFragment extends Fragment implements ArticleListContract.View {

    ArticleListContract.Presenter mPresent;

    ArticleAdapter mArticleAdapter;

    public static ArticleListFragment newInstance(){
        return new ArticleListFragment();
    }

    @Override
    public void setPresenter(ArticleListContract.Presenter presenter) {
        mPresent = presenter;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mArticleAdapter = new ArticleAdapter(new ArrayList<ArticleBrief>());
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresent.start();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.article_list_frag, container, false);

        final SwipeRefreshLayout swipeRefreshLayout = root.findViewById(R.id.refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresent.reLoadArticle();
                Toast.makeText(root.getContext(), "刷新成功", Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        final SlideRecyclerView slideRecyclerView = (SlideRecyclerView)root.findViewById(R.id.article_list_slideview);
        slideRecyclerView.setAdapter(mArticleAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(slideRecyclerView.getContext(), RecyclerView.VERTICAL, false);

        mArticleAdapter.setOnItemClickListener(new ArticleAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView.Adapter adapter, View v, int position) {
                String name = mArticleAdapter.getArticleBrief(position);
                markReadAndRefresh(position);

                Intent intent = new Intent(root.getContext(), LastActivity.class);
                intent.putExtra("title", name);
                startActivity(intent);

            }
        });
        mArticleAdapter.setOnDeleteClickListener(new ArticleAdapter.OnDeleteClickListener() {
            @Override
            public void onDeleteClick(View view, int position) {
                addCollectionAndRefresh(position);
            }
        });
        mArticleAdapter.setOnMarkReadClickListener(new ArticleAdapter.OnMarkReadClickListener() {
            @Override
            public void onMarkReadClick(View view, int position) {
                markReadAndRefresh(position);
            }
        });

        slideRecyclerView.setLayoutManager(layoutManager);
        slideRecyclerView.addOnScrollListener(new SlideRecyclerView.LoadMoreOnScrollListener(){
            @Override
            public void loadMoreArticle() {
                boolean completeLoad = mPresent.loadArticle();
                if(!completeLoad) {
                    Toast.makeText(root.getContext(), "加载成功", Toast.LENGTH_SHORT).show();
                }else{
                    mArticleAdapter.setLoadCompletely();
                }
            }
        });

        return root;
    }

    public void showArticleList(List<ArticleBrief> articleBriefList, int begin, int size){
        mArticleAdapter.addArticleList(articleBriefList);
        mArticleAdapter.notifyItemRangeInserted(begin, size);
    }

    public void refreshArticleList(List<ArticleBrief> articleBriefList){
        mArticleAdapter.changeData(articleBriefList);
        mArticleAdapter.notifyDataSetChanged();
        mArticleAdapter.setLoadFirstly();
    }

    public void markReadAndRefresh(int position){
        mArticleAdapter.setRead(position);
        mArticleAdapter.notifyItemChanged(position);
    }

    public void addCollectionAndRefresh(int position){
        mArticleAdapter.setCollected(position);
        mArticleAdapter.notifyItemRemoved(position);
    }
}
