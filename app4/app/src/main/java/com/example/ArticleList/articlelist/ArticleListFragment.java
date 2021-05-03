package com.example.ArticleList.articlelist;


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
            public void onItemClick(RecyclerView.Adapter adapter, View v, int postion) {
                String name = mArticleAdapter.getArticleBrief(postion);
                Toast.makeText(v.getContext(), "Switch to "+name + "'s activity", Toast.LENGTH_SHORT).show();
            }
        });
        mArticleAdapter.setOnDeleteClickListener(new ArticleAdapter.OnDeleteClickListener() {
            @Override
            public void onDeleteClick(View view, int position) {
                Toast.makeText(view.getContext(), "add this article to my collection", Toast.LENGTH_SHORT).show();
            }
        });


        slideRecyclerView.setLayoutManager(layoutManager);
        slideRecyclerView.addOnScrollListener(new LoadMoreOnScrollListener(){
            @Override
            public void loadMoreArticle() {
                boolean completeLoad = mPresent.loadArticle();
                if(!completeLoad) {
                    Toast.makeText(root.getContext(), "加载成功", Toast.LENGTH_SHORT).show();
                }else{
                    mArticleAdapter.setNotice();
                }
            }
        });

        return root;
    }

    public void showArticleList(List<ArticleBrief> articleBriefList, int begin, int size){
        mArticleAdapter.addArticleList(articleBriefList);
        //这个方法刷新方式是添加，如果可以的话，用这个代替下面的函数，还未尝试
        mArticleAdapter.notifyItemRangeInserted(begin, size);
//        //下面是直接重新加载，保证对，先不用
//        mArticleAdapter.notifyDataSetChanged();
    }

    public void refreshArticleList(List<ArticleBrief> articleBriefList){
        mArticleAdapter.changeData(articleBriefList);
        mArticleAdapter.notifyDataSetChanged();
        mArticleAdapter.setFirstNotice();
    }

}
