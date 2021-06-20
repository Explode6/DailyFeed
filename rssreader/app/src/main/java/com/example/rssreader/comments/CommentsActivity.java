package com.example.rssreader.comments;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.app.SkinAppCompatDelegateImpl;

import com.example.rssreader.R;
import com.example.rssreader.model.datamodel.ArticleBrief;
import com.example.rssreader.model.parse.AidlBinder;
import com.example.rssreader.rssSource.RssSourceActivity;
import com.example.rssreader.util.ActivityUtil;

import org.jetbrains.annotations.NotNull;

//implements Comments_Fragment.CallBackInterface
public class CommentsActivity extends AppCompatActivity {

    private ShowCommentsPresenter  showCommentsPresentor; //presenter
    public static  CommentsActivity activity;


    @NonNull
    @NotNull
    @Override
    public AppCompatDelegate getDelegate() {
        return SkinAppCompatDelegateImpl.get(this, this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(savedInstanceState != null){
            finish();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comments_main);

        //获得屏幕的宽高
        DisplayMetrics outMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        int widthPixels = outMetrics.widthPixels;
        int heightPixels = outMetrics.heightPixels;
        
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

        showCommentsPresentor = new ShowCommentsPresenter(AidlBinder.getInstance(), comments_fragment,articleBrief,toolbar,heightPixels,widthPixels);

         //fragment使用activity
         activity = this;
    }
    @Override
    public void onActionModeStarted(android.view.ActionMode mode){
        Comments_Fragment mfragment = (Comments_Fragment) getSupportFragmentManager().getFragments().get(0);
        android.view.ActionMode mode1 = mfragment.setData(mode);
        super.onActionModeStarted(mode);
    }

    public void setBackgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = this.getWindow()
                .getAttributes();
        lp.alpha = bgAlpha;
        this.getWindow().setAttributes(lp);
    }





}