package com.example.rssreader.util;

import android.app.Application;
import android.content.Context;

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
