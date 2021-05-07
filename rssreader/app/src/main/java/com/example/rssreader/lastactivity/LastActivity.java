package com.example.rssreader.lastactivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.rssreader.R;

public class LastActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_last);

        Intent intent = getIntent();
        String title = intent.getStringExtra("title");

        TextView textView = (TextView)findViewById(R.id.test_title);
        textView.setText(title);
    }
}