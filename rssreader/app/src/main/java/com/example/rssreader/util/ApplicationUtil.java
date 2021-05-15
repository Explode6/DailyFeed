package com.example.rssreader.util;

import android.app.Application;
import android.content.Context;

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
                .setSkinStatusBarColorEnable(true)                     // 关闭状态栏换肤，默认打开[可选]
                .setSkinWindowBackgroundEnable(true)                   // 关闭windowBackground换肤，默认打开[可选]
                .loadSkin();
    }
    public static Context getContext(){
        return context;
    }

    public static void setIsFirstLoad(boolean is){
        isFirstLoad = is;
    }

    public static boolean getIsFirstLoad(){
        return isFirstLoad;
    }
}
