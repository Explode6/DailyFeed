package com.example.rssreader.rssdetails.collectionlist;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.rssreader.IMyAidlInterface;
import com.example.rssreader.R;
import com.example.rssreader.model.datamodel.Channel;
import com.example.rssreader.model.parse.AidlBinder;
import com.example.rssreader.rssSource.RssSourceActivity;
import com.example.rssreader.util.ActivityUtil;

/**
 * @ClassName CollectionListActivity
 * @Author HaoHaoGe
 * @Date 2021/4/30
 * @Description 我的收藏页面
 */
public class CollectionListActivity extends AppCompatActivity {

    private CollectionListPresenter mCollectionListPresenter;

    private SearchListPresenter mSearchListPresenter;

    private final String TAG = "CollectionListActivity";
    /**
     * 数据服务的引用
     */
    private IMyAidlInterface myAidlInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.collection_list_act);


        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        //为toolbar引入actionbar的功能
        setSupportActionBar(toolbar);


        //单例模式创建fragment界面并绑定到布局上
        CollectionListFragment collectionListFragment =
                (CollectionListFragment) getSupportFragmentManager().findFragmentById(R.id.showlist_frag);
        if (collectionListFragment == null){
            collectionListFragment = CollectionListFragment.newInstance();
            ActivityUtil.addFragmentToActivity(getSupportFragmentManager(), collectionListFragment, R.id.showlist_frag);
        }

        // Create the presenter
        mCollectionListPresenter = new CollectionListPresenter(AidlBinder.getInstance(), collectionListFragment);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.collection_toobar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.search_button:
                replaceFragment();
                break;
            case R.id.home:

            default:
        }
        return true;
    }


    private void replaceFragment(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        SearchListFragment searchListFragment = SearchListFragment.newInstance();
        mSearchListPresenter = new SearchListPresenter(AidlBinder.getInstance(), searchListFragment);
        ActivityUtil.replaceFragment(fragmentManager, searchListFragment, R.id.showlist_frag);
    }
}
