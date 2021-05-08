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

    ArticleListContract.ArticleListPresenter mPresent;

    ArticleListAdapter mArticleListAdapter;

    public static ArticleListFragment newInstance(){
        return new ArticleListFragment();
    }

    @Override
    public void setPresenter(ArticleListContract.ArticleListPresenter presenter) {
        mPresent = presenter;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mArticleListAdapter = new ArticleListAdapter(new ArrayList<ArticleBrief>());
    }

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
        slideRecyclerView.setAdapter(mArticleListAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(slideRecyclerView.getContext(), RecyclerView.VERTICAL, false);

        mArticleListAdapter.setOnItemClickListener(new ArticleListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView.Adapter adapter, View v, int position) {
                String name = mArticleListAdapter.getArticleBrief(position);
                markReadAndRefresh(position);

                Intent intent = new Intent(root.getContext(), LastActivity.class);
                intent.putExtra("title", name);
                startActivity(intent);

            }
        });
        mArticleListAdapter.setOnDeleteClickListener(new ArticleListAdapter.OnDeleteClickListener() {
            @Override
            public void onDeleteClick(View view, int position) {
                addCollectionAndRefresh(position);
            }
        });
        mArticleListAdapter.setOnMarkReadClickListener(new ArticleListAdapter.OnMarkReadClickListener() {
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
                    mArticleListAdapter.setLoadCompletely();
                }
            }
        });

        return root;
    }

    public void showArticleList(List<ArticleBrief> articleBriefList, int begin, int size){
        mArticleListAdapter.addArticleList(articleBriefList);
        mArticleListAdapter.notifyItemRangeInserted(begin, size);
    }

    public void refreshArticleList(List<ArticleBrief> articleBriefList){
        mArticleListAdapter.changeData(articleBriefList);
        mArticleListAdapter.notifyDataSetChanged();
        mArticleListAdapter.setLoadFirstly();
    }


    /*
     * 还没有修改数据库
     */
    public void markReadAndRefresh(int position){
        mArticleListAdapter.switchRead(position);
    }

    public void addCollectionAndRefresh(int position){
        mArticleListAdapter.switchCollected(position);
    }
}
