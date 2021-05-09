package com.example.rssreader.lastactivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.rssreader.R;
import com.example.rssreader.model.datamodel.ArticleBrief;
import com.example.rssreader.model.datamodel.Channel;

public class LastActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_last);

        Intent intent = getIntent();
        ArticleBrief articleBrief = intent.getParcelableExtra("articleBrief");

        TextView textView = (TextView)findViewById(R.id.test_title);
        textView.setText(articleBrief.getTitle());
    }
}