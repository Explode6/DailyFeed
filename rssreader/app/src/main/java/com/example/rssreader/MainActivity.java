package com.example.rssreader;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    //记录当前列表/网格布局是否被选中（默认为网格布局）
    private  boolean listChosen = false;
    private boolean gridChosen = true;
    private Button listBtn;   //选择列表布局按钮
    private Button gridBtn;    //选择网格布局按钮
    private DrawerLayout mDrawerLayout; //侧滑菜单
    private NavigationView navView; //侧滑菜单的导航栏
    private RssSrcAdapter rssSrcAdapter;    //显示RSS源的适配器
    private List<RssSource>rssSrcList = new ArrayList<RssSource>();     //RSS源列表
    private RecyclerView rssView;   //显示RSS源的recyclerview
    private BottomPopupWindow bottomPopupWindow; //底部弹窗
    private PopupWindow popupWindow;  //添加RSS源弹窗
    private boolean longPressed = false;    //是否有子项被长按
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar)findViewById(R.id.main_toolbar);
        //为toolbar引入actionbar的功能
        setSupportActionBar(toolbar);
        //获取侧滑菜单
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        //获取标题栏
        ActionBar actionBar = getSupportActionBar();
        //设置导航按钮
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            //获取导航按钮图片
            Drawable navBtnImg = ContextCompat.getDrawable(MainActivity.this,R.drawable.menu);
            //显示导航按钮图片
            actionBar.setHomeAsUpIndicator(navBtnImg);
        }
        //实现网格和列表切换按钮的点击效果
        listBtn = (Button)findViewById(R.id.listBtn);
        gridBtn = (Button)findViewById(R.id.gridBtn);
        //列表切换按钮点击事件
        listBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(gridChosen == true && listChosen == false){
                    listBtn.setBackground(ContextCompat.
                            getDrawable(MainActivity.this,R.drawable.test1));
                    gridBtn.setBackground(ContextCompat.
                            getDrawable(MainActivity.this,R.drawable.add_icon));
                    listChosen = true;
                    gridChosen = false;
                    //显示RSS源的内容
                    showRecyclerview(1);
                }
            }
        });
        //网格切换按钮点击事件
        gridBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(gridChosen == false && listChosen == true){
                    listBtn.setBackground(ContextCompat.
                            getDrawable(MainActivity.this,R.drawable.add_icon));
                    gridBtn.setBackground(ContextCompat.
                            getDrawable(MainActivity.this,R.drawable.test1));
                    listChosen = false;
                    gridChosen = true;
                    //显示RSS源的内容
                    showRecyclerview(2);
                }
            }
        });
        //获取侧滑菜单的导航栏
        navView = (NavigationView)findViewById(R.id.nav_view);
        //设置侧滑菜单导航栏按钮点击事件
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                mDrawerLayout.closeDrawers();
                return true;
            }
        });

        //显示RSS源的内容
        if(rssSrcList.size()==0)
            initRssSrc();
        showRecyclerview(2);
    }

    /**
     * 新建添加RSS源的悬浮框
     */
    private void addRssPopWindow(){
        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.add_rss,null,false);
        //构造悬浮框,设置加载的view、宽度和高度，并将悬浮框设置为焦点
        popupWindow = new PopupWindow(view,
                ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT, true);
        //设置悬浮框可点击
        popupWindow.setTouchable(true);
        //为悬浮框设置背景
        popupWindow.setBackgroundDrawable(new ColorDrawable(0xffff0000));
        //设置悬浮框显示位置
        popupWindow.showAtLocation(findViewById(R.id.main_view), Gravity.CENTER, 0,0);
        //设置添加按钮事件
        Button addRssBtn = (Button)view.findViewById(R.id.add_rss_btn);
        addRssBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
                //添加RSS订阅源子项
                rssSrcAdapter.addItem(rssSrcList.size(),new RssSource("dsvsdbsdsdbsdbs",R.drawable.add_icon) );
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.addButton:
                addRssPopWindow();
                break;
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * 显示底部弹窗
     */
    public void showBottomWindow(){
        bottomPopupWindow = new BottomPopupWindow(this, onClickListener);
        bottomPopupWindow.showAtLocation(findViewById(R.id.main_view), Gravity.BOTTOM, 0, 0);

    }

    //初始化RSS源的内容（测试用，之后会删除）
    private void initRssSrc(){
        rssSrcList.clear();
        rssSrcList.add(new RssSource("lsw111111111",R.drawable.add_icon));
        rssSrcList.add(new RssSource("lsw222222222",R.drawable.add_icon));
        rssSrcList.add(new RssSource("lsw333333333",R.drawable.add_icon));
        rssSrcList.add(new RssSource("lsw444444444",R.drawable.add_icon));
    }

    /**
     * 取消所有RSS源的被选中状态
     */
    private void cancelSelected(){
        for(RssSource rssSource : rssSrcList)
            if(rssSource.getSelected() == true)
                rssSource.setSelected(false);
    }

    /**
     * 选中所有RSS源
     */
    private void selectAllRss(){
        for(RssSource rssSource : rssSrcList)
            if(rssSource.getSelected() == false)
                rssSource.setSelected(true);
    }
    /**
     * 利用recyclerview展示所有RSS源
     * @param spanCount 网格的列数
     */
    public void showRecyclerview(int spanCount){
        rssView = (RecyclerView)findViewById(R.id.all_Rss_recyclerview);
        GridLayoutManager layoutManager = new GridLayoutManager(this, spanCount);
        rssView.setLayoutManager(layoutManager);
        if(gridChosen == true)
            rssSrcAdapter = new RssSrcAdapter(rssSrcList, true, longPressed);
        else
            rssSrcAdapter = new RssSrcAdapter(rssSrcList, false, longPressed);
        //为适配器添加点击监听函数
        rssSrcAdapter.setRssOnClickListener(new RssSrcAdapter.RssOnClickListener() {
            @Override
            public void rssOnClickListener(int pos) {
                if(longPressed == true)
                    //提高性能，一次点击只通知适配器一个item发生变化
                    rssSrcAdapter.notifyItemChanged(pos);
            }
        });
        //为适配器添加长按监听函数
        rssSrcAdapter.setRssLongClickListener(new RssSrcAdapter.RssLongClickListener() {
            @Override
            public void rssLongClickListener() {
                longPressed = true;
                rssSrcAdapter.notifyDataSetChanged();
                showBottomWindow();
            }
        });

        rssView.setAdapter(rssSrcAdapter);
        //添加动画效果
        rssView.setItemAnimator(new DefaultItemAnimator());
    }

    //底部弹窗按钮监听事件
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                //取消按钮
                case R.id.cancel_btn:
                    longPressed = false;
                    cancelSelected();
                    rssSrcAdapter.setCanEdit(longPressed);
                    rssSrcAdapter.notifyDataSetChanged();
                    bottomPopupWindow.dismiss();
                    break;
                //删除按钮
                case R.id.del_rss_btn:
                    rssSrcAdapter.delSelectedItems();
                    longPressed = false;
                    cancelSelected();
                    rssSrcAdapter.setCanEdit(longPressed);
                    rssSrcAdapter.notifyDataSetChanged();
                    bottomPopupWindow.dismiss();
                    break;
                //全选按钮
                case R.id.select_all_btn:
                    selectAllRss();
                    rssSrcAdapter.notifyDataSetChanged();
            }
        }
    };
}
