package com.example.rssreader.util;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.view.Window;

import com.example.rssreader.R;

import skin.support.SkinCompatManager;
import skin.support.app.SkinAppCompatViewInflater;
import skin.support.app.SkinCardViewInflater;
import skin.support.constraint.app.SkinConstraintViewInflater;
import skin.support.design.app.SkinMaterialViewInflater;

/**
 * @ClassName: ApplicationUtil
 * @Author: shiwei
 * @Date: 2021/5/13
 * @Description: 用于获取全局context
 */
public class ApplicationUtil extends Application {
    private static Context context;
    private static boolean isFirstLoad = true;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        SkinCompatManager.withoutActivity(this)
                .addInflater(new SkinAppCompatViewInflater())           // 基础控件换肤初始化
                .addInflater(new SkinMaterialViewInflater())            // material design 控件换肤初始化[可选]
                .addInflater(new SkinConstraintViewInflater())          // ConstraintLayout 控件换肤初始化[可选]
                .addInflater(new SkinCardViewInflater())                // CardView v7 控件换肤初始化[可选]
                .loadSkin();
    }

    public static Context getContext(){
        return context;
    }

    public static void setIsFirstLoad(boolean is){
        isFirstLoad = is;
    }

    /**
     * 设置状态栏背景
     * @param window 当前窗口
     * @param resources 应用资源
     * @param isNight   当前是否为夜间模式
     */
    public static void setStatusBarMode(Window window, Resources resources, boolean isNight){
        if(isNight == true){
            //切换状态栏背景色
            window.setStatusBarColor(resources.getColor(R.color.colorPrimaryDark_night));
            //切换状态栏字体颜色
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        }else{
            //切换状态栏背景色
            window.setStatusBarColor(resources.getColor(R.color.colorPrimaryDark));
            //切换状态栏字体颜色
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }
    public static boolean getIsFirstLoad(){
        return isFirstLoad;
    }
}
