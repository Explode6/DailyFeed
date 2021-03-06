package com.example.rssreader.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.File;
import java.util.Calendar;

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
    private static final String filePath = "/data/data/com.example.rssreader/shared_prefs/";
    private static final String fileName = "rssConfig";
    private static final String fileType = ".xml";

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
        File f = new File(filePath + fileName + fileType);
        //创建配置文件
        if(!f.exists()){
            initConfig(context);
        }
        if(configUtil == null){
            configUtil = new ConfigUtil();
            //绑定对象
            configUtil.pref = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
            configUtil.editor = configUtil.pref.edit();
        }
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
        editor.putInt("hour", -1);
        editor.putInt("minute", -1);
        editor.putInt("textSize", 100);
        editor.putInt("lineHeight,", 120);
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
            editor.putBoolean("mode", false);
        }else if(mode == MODE_DARK){
            editor.putBoolean("mode", true);
        }else return false;
        //写入文件并清除editor对象
        editor.apply();
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
     * 设置定时更新的时间，初始值是-1
     *
     * @param hour 设定的小时数
     * @return 添加成功返回true，否则返回false
     */
    public boolean setHour(int hour){
        editor.putInt("hour", hour);
        editor.apply();
        return true;
    }

    /**
     * 获取上次设定的更新时间的小时数
     *
     * @return the int
     */
    public int getHour(){
        return pref.getInt("hour", -1);
    }

    /**
     * 设置定时更新的小时数，初始值是-1
     *
     * @param minute 设定的分钟数
     * @return 添加成功返回true，否则返回false
     */
    public boolean setMinute(int minute){
        editor.putInt("minute", minute);
        editor.apply();
        return true;
    }

    /**
     * 获取上次设定的更新时间的分钟数
     *
     * @return the int
     */
    public int getMinute(){
        return pref.getInt("minute", -1);
    }

    /**
     * 设定展示文章内容的文字大小，初始值是100%
     *
     * @param textSize 设定的文字大小
     * @return 设定成功返回true，否则返回false
     */
    public boolean setTextSize(int textSize){
        if(textSize <= 0) return false;
        editor.putInt("textSize", textSize);
        editor.apply();
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
     * 设定文章内容展示的行间距离，初始值为120
     *
     * @param lineHeight
     * @return 设定成功返回true，否则返回false
     */
    public boolean setLineHeight(int lineHeight){
        if(lineHeight<100) return false;
        editor.putInt("lineHeight", lineHeight);
        editor.apply();
        return true;
    }

    /**
     * 获取上次设定的行间距离
     *
     * @return the int
     */
    public int getLineHeight(){
        return pref.getInt("lineHeight", 120);
    }
}
