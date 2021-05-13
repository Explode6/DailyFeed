package com.example.rssreader.rssSource;

import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rssreader.R;
import com.example.rssreader.RssSource;
import com.example.rssreader.articleCollection.ArticleCollectionActivity;
import com.example.rssreader.articlelist.ArticleListActivity;
import com.google.android.material.navigation.NavigationView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class RssSourceFragment extends Fragment implements RssSourceContract.RssSourceView {
    private RssSourceContract.RssSourcePresenter rssSourcePresenter;
    private RssSrcAdapter rssSrcAdapter;    //显示RSS源的适配器
    private GridLayoutManager layoutManager;   //布局管理器
    private ItemTouchHelper itemTouchHelper;    //实现拖拽功能的帮助类
    private BottomPopupWindow bottomPopupWindow;    //底部弹窗
    private AddRssSourceDialog addRssSourceDialog;  //添加RSS源的弹窗
    private LoadingPopupWindow loadingPopupWindow;  //加载弹窗
    private RecyclerView rssView;
    private boolean canEdit = false;    //是否进入编辑模式
    private Button listBtn;   //选择列表布局按钮
    private Button gridBtn;    //选择网格布局按钮
    private Button editBtn;     //RSS源编辑按钮
    private int touchCount = 0;

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
        //绑定itemTouchHelper
        itemTouchHelper = setItemTouchHelper();
        itemTouchHelper.attachToRecyclerView(rssView);
        //添加动画效果
        rssView.setItemAnimator(new DefaultItemAnimator());
        rssSrcAdapter.setRssOnClickListener(new RssSrcAdapter.RssOnClickListener() {
            @Override
            public void rssOnClickListener(int pos) {
                if(canEdit == true) {
                    //提高性能，一次点击只通知适配器一个item发生变化
                    rssSrcAdapter.notifyItemChanged(pos);
                }else{
                    //将RSS源信息传递给下一个Activity
                    rssSourcePresenter.transferChannel(getActivity(),pos);
                }
            }
        });
        //绑定按钮
        listBtn = (Button)root.findViewById(R.id.listBtn);
        gridBtn = (Button)root.findViewById(R.id.gridBtn);
        editBtn = (Button)root.findViewById(R.id.edit_rss_source_btn);
        //绑定底部弹窗
        setBottomWindow();
        //绑定添加RSS源的弹窗
        setAddRssSrcDialog();
        //绑定加载弹窗
        setProgressBar();
        //设置添加RSS源的弹窗相关的点击函数
        setAddRssSrcListener();
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
                }else{
                    exitEditMode();
                }
            }
        });
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        //加载recyclerview中的数据
        try {
            rssSourcePresenter.start();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
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
    public void setListBtnBackground(int imgColor) {
        listBtn.setBackgroundColor(imgColor);
    }

    @Override
    public void setGridBtnBackground(int imgColor) {
        gridBtn.setBackgroundColor(imgColor);
    }

    @Override
    public void convertToList(List<RssSource>list) {
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
                    //将RSS源信息传递给下一个Activity
                    rssSourcePresenter.transferChannel(getActivity(),pos);
                }
            }
        });
        //设置适配器
        rssView.setAdapter(rssSrcAdapter);
        itemTouchHelper = setItemTouchHelper();
        //itemTouchHelper.attachToRecyclerView(rssView);
    }

    @Override
    public void convertToGrid(List<RssSource>list) {

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
                    //将RSS源信息传递给下一个Activity
                    rssSourcePresenter.transferChannel(getActivity(),pos);
                }
            }
        });
        //设置适配器
        rssView.setAdapter(rssSrcAdapter);
        itemTouchHelper = setItemTouchHelper();
        //itemTouchHelper.attachToRecyclerView(rssView);
    }

    @Override
    public void enterEditMode() {
        //进入编辑模式
        canEdit = true;
        editBtn.setText("取消");
        rssSrcAdapter.setCanEdit(canEdit);
        //进入编辑模式界面
        refreshView();
        showBottomWindow();
    }

    @Override
    public void loadAndRefreshRecyclerView(List<RssSource>list) {
        this.rssSrcAdapter.setRssSourceList(list);
        refreshView();
    }

    @Override
    public void exitEditMode() {
        canEdit = false;
        editBtn.setText("编辑");
        rssSourcePresenter.cancelSelected();
        rssSrcAdapter.setCanEdit(false);
        refreshView();
        bottomPopupWindow.dismiss();
    }

    //设置底部弹窗相关的点击事件
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
                        try {
                            rssSourcePresenter.delSelectedItems();
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
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
                                        calendar.set(Calendar.HOUR_OF_DAY, dialog.getHour());
                                        calendar.set(Calendar.MINUTE, dialog.getMinute());
                                        Toast.makeText(getContext(), String.valueOf(dialog.getHour()), Toast.LENGTH_SHORT).show();
                                        Toast.makeText(getContext(), String.valueOf(dialog.getMinute()), Toast.LENGTH_SHORT).show();
                                        calendar.set(Calendar.SECOND, 0);
                                        //开启广播
                                        AlarmUtil.startNoticeService(getContext(),calendar.getTimeInMillis(),ClockBroadcastReceiver.class,"com.example.rssreader.rssNoticeBroadcast");
                                        dialog.dismiss();
                                        break;
                                    case R.id.close_time_choose_dialog_btn:
                                        dialog.dismiss();
                                        break;
                                }
                            }
                        });
                        break;

                    //主题设置，夜间/日间模式切换
                    case R.id.theme_setting:
                        rssSourcePresenter.modeSwitching(menuItem);
                        break;

                    case R.id.my_collection:
                        Intent intent = new Intent(getContext(), ArticleCollectionActivity.class);
                        startActivity(intent);
                        break;
                }
                return true;
            }
        });
    }

    @Override
    public void setAddRssSrcDialog() {
        addRssSourceDialog = new AddRssSourceDialog(getActivity());
    }

    @Override
    public void showAddRssSrcDialog() {
        addRssSourceDialog.show();
        //设置弹窗显示宽度
        WindowManager windowManager = getActivity().getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = addRssSourceDialog.getWindow().getAttributes();
        lp.width = (int)(display.getWidth());
        addRssSourceDialog.getWindow().setAttributes(lp);
    }

    @Override
    public void closeAndClearAddDialog() {
        addRssSourceDialog.dismiss();
        addRssSourceDialog.clearInput();
    }

    //设置添加RSS源弹窗有关的点击事件
    @Override
    public void setAddRssSrcListener() {
        addRssSourceDialog.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()){
                    //点击右上角关闭按钮
                    case R.id.close_add_rss_dialog_btn:
                        //关闭弹窗并清空输入内容
                        closeAndClearAddDialog();
                        break;
                    //点击添加按钮
                    case R.id.add_rss_btn:
                        rssSourcePresenter.addRssSrc(addRssSourceDialog.getInputRss(), getActivity());
                        break;
                }
            }
        });
    }

    @Override
    public void giveHint(String hint) {
        Toast.makeText(getContext(), hint, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void switchToNightMode(MenuItem item) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        getActivity().recreate();
    }

    @Override
    public void switchToDayMode(MenuItem item) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        getActivity().recreate();
    }

    @Override
    public void setProgressBar() {
        loadingPopupWindow = new LoadingPopupWindow(this.getContext());
    }

    @Override
    public void showProgressBar() {
        addRssSourceDialog.showProgressBar();
    }

    @Override
    public void hideProgressBar() {
        addRssSourceDialog.hideProgressBar();
    }

    @Override
    public ItemTouchHelper setItemTouchHelper() {
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull @NotNull RecyclerView recyclerView, @NonNull @NotNull RecyclerView.ViewHolder viewHolder) {
                return makeMovementFlags(rssSourcePresenter.setMovementFlags(), 0);
            }

            @Override
            public boolean onMove(@NonNull @NotNull RecyclerView recyclerView, @NonNull @NotNull RecyclerView.ViewHolder viewHolder, @NonNull @NotNull RecyclerView.ViewHolder target) {
                return rssSourcePresenter.setMoving(viewHolder.getAdapterPosition(), target.getAdapterPosition());
            }

            @Override
            public void onSwiped(@NonNull @NotNull RecyclerView.ViewHolder viewHolder, int direction) {}

            @Override
            public boolean isLongPressDragEnabled() {
                return true;
            }

            @Override
            public boolean isItemViewSwipeEnabled() {
                return false;
            }

            @Override
            public void onChildDraw(@NonNull @NotNull Canvas c, @NonNull @NotNull RecyclerView recyclerView, @NonNull @NotNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }


            @Override
            public void clearView(@NonNull @NotNull RecyclerView recyclerView, @NonNull @NotNull RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
            }

        });
        return itemTouchHelper;
    }


    @Override
    public void refreshAfterMove(int srcPos, int desPos) {
        rssSrcAdapter.notifyItemMoved(srcPos, desPos);
        rssSrcAdapter.notifyItemRangeChanged(Math.min(srcPos, desPos), Math.abs(srcPos-desPos)+1);
    }
}

