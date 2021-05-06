package com.example.rssreader.rssSource;

import android.util.Log;

import com.example.rssreader.R;
import com.example.rssreader.RssSource;

import java.util.ArrayList;
import java.util.List;

public class RssSourceModel {
    public List<RssSource> getAllRssSource(){
        List<RssSource>list = new ArrayList<>();
        list.add(new RssSource("dssdbsbfbfb111", R.drawable.add_icon));
        list.add(new RssSource("dssdbsbfbfb222", R.drawable.add_icon));
        list.add(new RssSource("dssdbsbfbfb333", R.drawable.add_icon));
        list.add(new RssSource("dssdbsbfbfb444", R.drawable.add_icon));
        Log.d("TAGC",String.valueOf(list.size()));
        return list;
    }

    public void delRssSource(){

    }
}
