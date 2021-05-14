package com.example.rssreader.rssdetails.collectionlist;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.rssreader.IMyAidlInterface;
import com.example.rssreader.R;
import com.example.rssreader.model.datamodel.Channel;
import com.example.rssreader.model.parse.AidlBinder;
import com.example.rssreader.util.ActivityUtil;

/**
 * @ClassName CollectionListActivity
 * @Author HaoHaoGe
 * @Date 2021/4/30
 * @Description 我的收藏页面
 */
public class CollectionListActivity extends AppCompatActivity {

    private CollectionListPresenter mCollectionListPresenter;

    /**
     * 数据服务的引用
     */
    private IMyAidlInterface myAidlInterface;

    String TAG = "CollectionListActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.collection_list_act);


        Toolbar toolbar = (Toolbar)findViewById(R.id.main_toolbar);
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
}
