package com.example.testapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.testapplication.datamodel.ArticleBrief;
import com.example.testapplication.datamodel.Channel;
import com.example.testapplication.datamodel.DataBaseHelper;

import java.util.List;

public class DisplayMessageActivity extends AppCompatActivity {

    private String TAG = "DisplayMessageActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);

        Intent intent = getIntent();
        //String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        String message = "";

        DataBaseHelper dataBaseHelper = new DataBaseHelper();

        List<Channel> channels =  dataBaseHelper.getChannel(0,10);

        for (Channel channel:channels){
            message += "channel:" + channel.getTitle()+ '\n';
            Log.d(TAG,"channel:"+channel.getTitle());
            List <ArticleBrief> articleBriefs = dataBaseHelper.getArticleBriefsFromChannel(channel,0,10);
            for(ArticleBrief articleBrief:articleBriefs){
                Log.d(TAG,"article:"+articleBrief.getTitle());
            }
        }

        TextView textView = findViewById(R.id.textView);
        textView.setText(message);
    }

}
