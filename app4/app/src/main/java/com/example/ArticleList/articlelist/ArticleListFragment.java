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

import com.example.ArticleList.Data.Article;
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
        mArticleAdapter = new ArticleAdapter(new ArrayList<Article>());
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresent.start();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.article_list_frag, container, false);
        SlideRecyclerView slideRecyclerView = (SlideRecyclerView)root.findViewById(R.id.article_list_slideview);
        slideRecyclerView.setAdapter(mArticleAdapter);
        //可能错了
        LinearLayoutManager layoutManager = new LinearLayoutManager(slideRecyclerView.getContext(), RecyclerView.VERTICAL, false);
        mArticleAdapter.setOnItemClickListener(new ArticleAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView.Adapter adapter, View v, int postion) {
                Toast.makeText(v.getContext(), "Switch to the last activity", Toast.LENGTH_LONG).show();
            }
        });
        mArticleAdapter.setOnDeleteClickListener(new ArticleAdapter.OnDeleteClickListener() {
            @Override
            public void onDeleteClick(View view, int position) {
                Toast.makeText(view.getContext(), "add this article to my collection", Toast.LENGTH_LONG).show();
            }
        });
        slideRecyclerView.setLayoutManager(layoutManager);

        return root;
    }

    public void showArticleList(List<Article> articleList){
        mArticleAdapter.changeData(articleList);
        mArticleAdapter.notifyDataSetChanged();
    }
}
