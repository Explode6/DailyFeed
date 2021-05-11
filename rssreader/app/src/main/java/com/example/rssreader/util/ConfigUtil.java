package com.example.rssreader.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import java.io.File;

/**
 * @ClassName: ConfigUtil
 * @Author: Von
 * @Date: 2021/5/9
 * @Description: 用于设置和读取配置文件
 */
public class ConfigUtil {
    //夜间模式与日间模式
    public static final int MODE_DARK = 1;
    public static final int MODE_LIGHT = 0;

    //文件存储的路径和文件名
    @SuppressLint("SdCardPath")
    public static final String filePath = "/data/data/com.example.rssreader/shared_prefs/";
    public static final String fileName = "rssConfig.xml";

    //用于读取和写入的对象
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    public static ConfigUtil configUtil;

    /**
     * 获取ConfigUtil的单例对象，如果是初次获取将初始化配置文件
     *
     * @param context
     * @return the ConfigUtil
     */
    public static ConfigUtil getInstance(Context context){
        //单例模式
        //检查文件是否存在
        File f = new File(filePath + fileName);
        //创建配置文件
        if(!f.exists()){
            initConfig(context);
        }
        if(configUtil == null){
            configUtil = new ConfigUtil();
        }
        //绑定对象
        configUtil.pref = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        configUtil.editor = configUtil.pref.edit();
        return configUtil;
    }

    /**
     * 创建初始配置文件
     *
     * @param context
     */
    private static void initConfig(Context context){
        //创建配置文件
        SharedPreferences.Editor editor = context.getSharedPreferences(fileName, Context.MODE_PRIVATE).edit();
        //填入初始化对象
        editor.putBoolean("mode", false);
        editor.putLong("time", 0);
        editor.putInt("textSize", 100);
        editor.putInt("textSpacing,", 0);
        editor.apply();
    }


    /**
     * 设置应用的色调模式，MODE_DARK或MODE_LIGHT
     *
     * @param mode 类内定义的类型模式
     * @return 设定成功返回true，否则返回false
     */
    public boolean setMode(int mode){
        if(mode == MODE_LIGHT){
            editor.putBoolean("mode", true);
        }else if(mode == MODE_DARK){
            editor.putBoolean("mode", false);
        }else return false;
        //写入文件并清除editor对象
        editor.apply();
        editor.clear();
        return true;
    }

    /**
     * 判断现在是否处于夜间模式
     *
     * @return the boolean
     */
    public boolean isDarkMode(){
        return pref.getBoolean("mode", false);
    }

    /**
     * 设置定时更新的时间，初始值是0
     *
     * @param time 设定的时间
     * @return 添加成功返回true，否则返回false
     */
    public boolean setTime(long time){
        editor.putLong("time", time);
        editor.apply();
        editor.clear();
        return true;
    }

    /**
     * 获取上次设定的更新时间
     *
     * @return the long
     */
    public long getTime(){
        return pref.getLong("time", 0);
    }

    /**
     * 设定展示文章内容的文字大小，初始值是100
     *
     * @param textSize 设定的文字大小
     * @return 设定成功返回true，否则返回false
     */
    public boolean setTextSize(int textSize){
        if(textSize <= 0) return false;
        editor.putInt("textSize", textSize);
        editor.apply();
        editor.clear();
        return true;
    }

    /**
     * 获取上次设定的文字大小
     *
     * @return the int
     */
    public int getTextSize(){
        return pref.getInt("textSize", 100);
    }

    /**
     * 设定文章内容展示的文字间隔，初始值为0
     *
     * @param textSpacing
     * @return 设定成功返回true，否则返回false
     */
    public boolean setTextSpacing(int textSpacing){
        if(textSpacing<0) return false;
        editor.putInt("textSpacing", textSpacing);
        editor.apply();
        editor.clear();
        return true;
    }

    /**
     * 获取上次设定的文字间隔
     *
     * @return the int
     */
    public int getTextSpacing(){
        return pref.getInt("textSpacing", 0);
    }
}
