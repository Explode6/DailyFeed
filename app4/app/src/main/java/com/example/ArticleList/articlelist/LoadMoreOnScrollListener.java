package com.example.ArticleList.articlelist;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @ClassName testOnScrollListener
 * @Author HaoHaoGe
 * @Date 2021/5/2
 * @Description
 */
public abstract class LoadMoreOnScrollListener extends RecyclerView.OnScrollListener {
    private boolean isSlidingUpward = false;

    @Override
    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        LinearLayoutManager layoutManager = (LinearLayoutManager)recyclerView.getLayoutManager();;

        if(newState == RecyclerView.SCROLL_STATE_IDLE){
            int lastItemPosition = layoutManager.findLastCompletelyVisibleItemPosition();
            int itemCount = layoutManager.getItemCount();

            if(lastItemPosition == itemCount - 1 && isSlidingUpward){
                loadMoreArticle();
            }
        }
    }

    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        isSlidingUpward = dy > 0;
    }

    public abstract void loadMoreArticle();
}
