package com.example.rssreader.rssSource;

import android.util.Log;

import com.example.rssreader.R;
import com.example.rssreader.RssSource;

import java.util.ArrayList;
import java.util.List;

public class RssSourceModel {
    public List<RssSource> getAllRssSource(){
<<<<<<< HEAD
        List<RssSource> list = new ArrayList<>();
=======
        List<RssSource>list = new ArrayList<>();
>>>>>>> HaoHaoGe
        list.add(new RssSource("dssdbsbfbfb111", R.drawable.add_icon));
        list.add(new RssSource("dssdbsbfbfb222", R.drawable.add_icon));
        list.add(new RssSource("dssdbsbfbfb333", R.drawable.add_icon));
        list.add(new RssSource("dssdbsbfbfb444", R.drawable.add_icon));
<<<<<<< HEAD
        Log.d("TAGC", String.valueOf(list.size()));
=======
        Log.d("TAGC",String.valueOf(list.size()));
>>>>>>> HaoHaoGe
        return list;
    }

    public void delRssSource(){

    }
}
