package com.example.rssreader.rssSource;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

<<<<<<< HEAD
import com.example.rssreader.BottomPopupWindow;
import com.example.rssreader.R;
import com.example.rssreader.RssSource;
import com.example.rssreader.RssSrcAdapter;
=======
import com.example.rssreader.R;
import com.example.rssreader.RssSource;
>>>>>>> HaoHaoGe
import com.example.rssreader.articlelist.ArticleListActivity;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class RssSourceFragment extends Fragment implements RssSourceContract.RssSourceView {
    private RssSourceContract.RssSourcePresenter rssSourcePresenter;
    private RssSrcAdapter rssSrcAdapter;    //显示RSS源的适配器
    private GridLayoutManager layoutManager;   //布局管理器
    private BottomPopupWindow bottomPopupWindow;    //底部弹窗
<<<<<<< HEAD
=======
    private AddRssSourceDialog addRssSourceDialog;  //添加RSS源的弹窗
>>>>>>> HaoHaoGe
    private RecyclerView rssView;
    private boolean canEdit = false;    //是否进入编辑模式
    private Button listBtn;   //选择列表布局按钮
    private Button gridBtn;    //选择网格布局按钮
    private Button editBtn;     //RSS源编辑按钮

    //空的构造函数
    public RssSourceFragment(){

    }

    //提供初始化实例的方法
    public static RssSourceFragment newInstance(){
        return new RssSourceFragment();
    }

    //设置presenter层的接口
    public void setPresenter(RssSourceContract.RssSourcePresenter presenter){
        this.rssSourcePresenter = presenter;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //新建适配器
        rssSrcAdapter = new RssSrcAdapter( new ArrayList<RssSource>(0),
                true, false);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.rss_source_frag, container, false);
        //初始化recyclerview
        rssView = (RecyclerView)root.findViewById(R.id.all_Rss_recyclerview);
        GridLayoutManager layoutManager = new GridLayoutManager(this.getContext(), 2);
        rssView.setLayoutManager(layoutManager);
        rssView.setAdapter(rssSrcAdapter);
        //添加动画效果
        rssView.setItemAnimator(new DefaultItemAnimator());
        rssSrcAdapter.setRssOnClickListener(new RssSrcAdapter.RssOnClickListener() {
            @Override
            public void rssOnClickListener(int pos) {
                if(canEdit == true) {
                    //提高性能，一次点击只通知适配器一个item发生变化
                    rssSrcAdapter.notifyItemChanged(pos);
                }else{
                    Intent intent = new Intent(getContext(), ArticleListActivity.class);
                    startActivity(intent);
                }
            }
        });
        //绑定按钮
        listBtn = (Button)root.findViewById(R.id.listBtn);
        gridBtn = (Button)root.findViewById(R.id.gridBtn);
        editBtn = (Button)root.findViewById(R.id.edit_rss_source_btn);
        //绑定底部弹窗
        setBottomWindow();
<<<<<<< HEAD
=======
        //绑定添加RSS源的弹窗
        setAddRssSrcDialog();
>>>>>>> HaoHaoGe
        //监听按钮点击事件
        listBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //切换为列表模式
                rssSourcePresenter.setListLayout();
            }
        });
        gridBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //切换为网格模式
                rssSourcePresenter.setGridLayout();
            }
        });
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(canEdit == false){
                   enterEditMode();
                }
            }
        });
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        //加载recyclerview中的数据
        rssSourcePresenter.start();
    }


    @Override
    public void refreshView() {
        this.rssSrcAdapter.notifyDataSetChanged();
    }

    @Override
    public void setBottomWindow() {
        bottomPopupWindow = new BottomPopupWindow(this.getContext(), setBottomWinClickListener());
    }

    @Override
    public void showBottomWindow() {
        bottomPopupWindow.showAtLocation(this.getView(), Gravity.BOTTOM, 0, 0);
    }


    @Override
    public void setListBtnBackground(int imgId) {
        listBtn.setBackground(ContextCompat.
                getDrawable(getContext(), imgId));
    }

    @Override
    public void setGridBtnBackground(int imgId) {
        gridBtn.setBackground(ContextCompat.
                getDrawable(getContext(), imgId));
    }

    @Override
<<<<<<< HEAD
    public void convertToList(List<RssSource> list) {
=======
    public void convertToList(List<RssSource>list) {
>>>>>>> HaoHaoGe
        layoutManager = new GridLayoutManager(this.getContext(), 1);
        rssView.setLayoutManager(layoutManager);
        rssSrcAdapter = new RssSrcAdapter(list, false, canEdit);
        //为适配器添加点击监听函数
        rssSrcAdapter.setRssOnClickListener(new RssSrcAdapter.RssOnClickListener() {
            @Override
            public void rssOnClickListener(int pos) {
                if(canEdit == true) {
                    //提高性能，一次点击只通知适配器一个item发生变化
                    rssSrcAdapter.notifyItemChanged(pos);
                }else{
                    Intent intent = new Intent(getContext(), ArticleListActivity.class);
                    startActivity(intent);
                }
            }
        });
        //设置适配器
        rssView.setAdapter(rssSrcAdapter);
    }

    @Override
<<<<<<< HEAD
    public void convertToGrid(List<RssSource> list) {
=======
    public void convertToGrid(List<RssSource>list) {
>>>>>>> HaoHaoGe
        layoutManager = new GridLayoutManager(this.getContext(), 2);
        rssView.setLayoutManager(layoutManager);
        rssSrcAdapter = new RssSrcAdapter(list, true, canEdit);
        //为适配器添加点击监听函数
        rssSrcAdapter.setRssOnClickListener(new RssSrcAdapter.RssOnClickListener() {
            @Override
            public void rssOnClickListener(int pos) {
                if(canEdit == true) {
                    //提高性能，一次点击只通知适配器一个item发生变化
                    rssSrcAdapter.notifyItemChanged(pos);
                }else{
                    Intent intent = new Intent(getContext(), ArticleListActivity.class);
                    startActivity(intent);
                }
            }
        });
        //设置适配器
        rssView.setAdapter(rssSrcAdapter);
    }

    @Override
    public void enterEditMode() {
        //进入编辑模式
        canEdit = true;
        rssSrcAdapter.setCanEdit(canEdit);
        //进入编辑模式界面
        refreshView();
        showBottomWindow();
    }

    @Override
<<<<<<< HEAD
    public void loadRecyclerView(List<RssSource> list) {
=======
    public void loadRecyclerView(List<RssSource>list) {
>>>>>>> HaoHaoGe
        this.rssSrcAdapter.setRssSourceList(list);
        refreshView();
    }

    @Override
    public void exitEditMode() {
        canEdit = false;
        rssSourcePresenter.cancelSelected();
        rssSrcAdapter.setCanEdit(false);
        refreshView();
        bottomPopupWindow.dismiss();
    }

    @Override
    public View.OnClickListener setBottomWinClickListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()){
                    //取消按钮
                    case R.id.cancel_btn:
                        exitEditMode();
                        break;
                    //删除按钮
                    case R.id.del_rss_btn:
                        rssSourcePresenter.delSelectedItems();
                        break;
                    //全选按钮
                    case R.id.select_all_btn:
                        rssSourcePresenter.selectAllRss();
                        refreshView();
                }
            }
        };
    }

    @Override
    public void setNavClickListener(final NavigationView navView) {
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    //定时更新的时间设置
                    case R.id.update_time_setting:
                        RssSourceActivity activity = (RssSourceActivity)getActivity();
                        activity.closeNavView();
                        final TimeChooseDialog dialog = new TimeChooseDialog(getActivity());
                        dialog.show();
                        dialog.setListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                switch (view.getId()){
                                    case R.id.time_chosen_btn:
                                        Calendar calendar = Calendar.getInstance();
                                        //设置为当前系统时间
                                        calendar.setTimeInMillis(System.currentTimeMillis());
                                        //设置为用户选择的时间
                                        calendar.set(Calendar.HOUR, dialog.getHour());
                                        calendar.set(Calendar.MINUTE, dialog.getMinute());
<<<<<<< HEAD
                                        AlarmUtil.startNoticeService(getContext(),calendar.getTimeInMillis(), NoticeService.class,"com.ryantang.service.PollingService");
                                        dialog.dismiss();
                                        break;
                                    case R.id.cancel_time_choose_btn:
=======
                                        AlarmUtil.startNoticeService(getContext(),calendar.getTimeInMillis(),NoticeService.class,"com.ryantang.service.PollingService");
                                        dialog.dismiss();
                                        break;
                                    case R.id.close_time_choose_dialog_btn:
>>>>>>> HaoHaoGe
                                        dialog.dismiss();
                                        break;
                                }
                            }
                        });
                        break;
                    //主题设置，夜间/日间模式切换
                    case R.id.theme_setting:
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                        getActivity().recreate();
                        break;
                }
                return true;
            }
        });
    }
<<<<<<< HEAD
=======

    @Override
    public void setAddRssSrcDialog() {
        addRssSourceDialog = new AddRssSourceDialog(getActivity());
    }

    @Override
    public void showAddRssSrcDialog() {
        addRssSourceDialog.show();
    }
>>>>>>> HaoHaoGe
}