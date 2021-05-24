package com.example.rssreader.comments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;

import com.example.rssreader.R;
import com.example.rssreader.model.datamodel.ArticleBrief;
import com.example.rssreader.model.parse.AidlBinder;
import com.example.rssreader.rssSource.RssSourceActivity;
import com.example.rssreader.util.ActivityUtil;

//implements Comments_Fragment.CallBackInterface
public class CommentsActivity extends AppCompatActivity {

    private ShowCommentsPresenter  showCommentsPresentor; //presenter

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(savedInstanceState != null){
            finish();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comments_main);
        //获得articleBrief
        Intent intent = getIntent();
        ArticleBrief articleBrief = intent.getParcelableExtra("articleBrief");

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.comments_toolbar);
        setSupportActionBar(toolbar);

        TextView textView = (TextView)findViewById(R.id.item_title);
        textView.setText(articleBrief.getTitle());


        Comments_Fragment comments_fragment = (Comments_Fragment) getSupportFragmentManager().
                 findFragmentById(R.id.comments_content_Frame);
         if(comments_fragment==null){
             comments_fragment = Comments_Fragment.newInstance();
             ActivityUtil.addFragmentToActivity(
                     getSupportFragmentManager(), comments_fragment, R.id.comments_content_Frame);
         }

        showCommentsPresentor = new ShowCommentsPresenter(AidlBinder.getInstance(), comments_fragment,articleBrief);

    }
    @Override
    public void onActionModeStarted(android.view.ActionMode mode){
        Comments_Fragment mfragment = (Comments_Fragment) getSupportFragmentManager().getFragments().get(0);
        android.view.ActionMode mode1 = mfragment.setData(mode);
        super.onActionModeStarted(mode);
    }




}