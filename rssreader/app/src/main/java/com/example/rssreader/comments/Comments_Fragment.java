package com.example.rssreader.comments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rssreader.R;
import com.example.rssreader.model.datamodel.ArticleBrief;
import com.example.rssreader.model.datamodel.GlobalComment;
import com.example.rssreader.model.datamodel.LocalComment;
import com.example.rssreader.util.ShareUtil;
import com.example.zhouwei.library.CustomPopWindow;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static androidx.core.util.Preconditions.checkNotNull;

public class Comments_Fragment extends Fragment implements CommentsContract.CommentsView,View.OnClickListener {
    private CommentsContract.CommentsPresenter mPresenter;
    private Button showAllComments;                                    //全局评论
    private Button showPartComments;                                  //部分评论
    private  ShowArticleWebView showArticleWebView;                  //webview展示
    private  View contentView;                                      //第一个窗口
    //popUpWindow的view
    private  View navigatioinView;                                //导航栏的view
    private  View progressView;                                  //修改字体大小的view
    private  View root;
    private  View mainView;                                     //获取mainView
    private CustomPopWindow mListPopWindow;                     //全局评论的popUpWindow
    private CustomPopWindow navigationWindow;                  //底部导航栏弹窗
    private CustomPopWindow progressWindow;                   //调整字体大小弹窗与行间距
    private  View mView;                                     //用于popupwindow获取父View
    private RecyclerView recyclerView;                      //全局评论的recyclerView
    private RecyclerView localRecyclerView;                //局部评论的recyclerView
    private GlobalCommentsAdapter mAdapter;               //全局评论recyclerView的adapter
    private LocalCommentsAdapter mLocalAdapter;          //局部评论recyclerView的adapter
    private  Button closeWin;                            //关闭popupwindow
    private  Button add_globe_comment;                   // 添加全局评论
    private  Button add_global_comment;
    private  Button add_local_comment;                  // 添加本地评论
    private  TextView firstC;                          //第一条评论
    private  TextView secondC;                        //第二条评论
    private  TextView date1;                         //第一条时间
    private  TextView date2;                        //第二条时间
    private CustomPopWindow localCommentWin;        //部分评论的popupwindow
    private CustomPopWindow addLocalCommentWin;         //添加本地评论的win
    private RecyclerView partrecyclerView;         //部分评论的recyclerView
    private  PopupWindow popupwindow;            //用来监控nestedscrollview
    private Button setting;                //设置字体大小
    private Button sizeIncrease;          // 字体变大
    private Button sizeDecrease;         // 字体变小
    private Button spaceIncrease;       // 间距变大
    private Button spaceDecrease;      // 间距变小
    private Button share_photo;      // 间距变小
    private View localCommentsView;   //展示local
    private View addLocalView;             //添加本地评论
    private EditText editLocalComment;    //局部评论的编写
    private EditText fakeAdd;            //第一个popupwindow界面的edittext
    private EditText editComment;       //第二个popupwindow界面的edittext
    private EditText editGlobalC;      //第三个popupwindow界面的edittext(仅有这个生效）
    private ProgressBar sizeprogressbar;
    private ProgressBar spaceprogressbar;
    private CommentsActivity comments_activity;
    private ArticleBrief mArticleBrief;
    private int textsize = 100;         //初始字体大小为100
    private int textspacing = 150;      //初始间距为0
    private int textcolor = 0x000000; //白色
    private LinearLayoutManager manager;
    private LinearLayoutManager localmanager;
    private NestedScrollView mnestedScrollView;
    private List<GlobalComment> mGlobalList; //用以展示的全局评论列表
    private List<LocalComment> mLocalList; //用以展示的全局评论列表
    private String localContent;
    private int tag = 1;
    private View editView;
    private CustomPopWindow editGlobalComment;         //弹出输入

    public Comments_Fragment(){

    }

    //拦截事件 由于需要在activity重载又需要用到fragment的数据，先从fragment获得数据再返回给activity  同时fragment层调用presenter操作
    public  android.view.ActionMode setData(android.view.ActionMode mode){
        List<String> actionList = new ArrayList<String>();
        actionList.add("comment");
        mode  = showArticleWebView.getActionMode(mode, actionList, new ShowArticleWebView.onActionClickListener() {
            @Override
            public void onActionClick(String title, String text) {
                switch (title){
                    case "comment":
                        localContent = text.replace("\\n","\n");
                        addLocalCommentWin= new CustomPopWindow.PopupWindowBuilder(getContext())
                                .setView(addLocalView)
                                .setFocusable(true)
                                .size(600,300)//显示大小
                                .create()
                                .showAtLocation(mView.findViewById(R.id.show_allcomments), Gravity.RIGHT, 0, -100);
                        break;
                    default:
                        break;
                }
            }
        });
        return mode;
    }

    public static Comments_Fragment newInstance() {
        return new Comments_Fragment();
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onResume() {
        super.onResume();
        mPresenter.fill_webview();
        mPresenter.createAdapter();
        mPresenter.createLocalAdapter();
        mPresenter.setTwoComments(date1,firstC,date2,secondC);
        try {
            mPresenter.start();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    @SuppressLint("RestrictedApi")
    @Override
    //将presenter与commentsView绑定
    public void setPresenter(@NonNull CommentsContract.CommentsPresenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Nullable
    //负责初始化各种数据与控件
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.comment_fragment, container, false);

        showArticleWebView = (ShowArticleWebView) root.findViewById(R.id.web_View);

        contentView = (View) inflater.from(getContext()).inflate(R.layout.commentspopupwindow,null);

        addLocalView =  inflater.from(getContext()).inflate(R.layout.addpartcomments,null);

        localCommentsView = inflater.from(getContext()).inflate(R.layout.partcommentswindows,null);

        navigatioinView = inflater.from(getContext()).inflate(R.layout.bottompopup,null);

        progressView= inflater.from(getContext()).inflate(R.layout.probarpopupwindow,null);

        editView = inflater.from(getContext()).inflate(R.layout.editpopup,null);

        mnestedScrollView = root.findViewById(R.id.nested);

        mnestedScrollView.setOnScrollChangeListener( new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                //上滑 并且 正在显示底部栏

                if (scrollY - oldScrollY > 60 && popupwindow.isShowing() ) {
                    mPresenter.closeNavigationWin();

                }
                //下滑 并且没有正在显示
                if (scrollY - oldScrollY < -60 && !popupwindow.isShowing()) {

                    navigationWindow.showAtLocation(root.findViewById(R.id.show_allcomments), Gravity.BOTTOM, 0, 0);
                }
                if(scrollY - oldScrollY > 60 && tag==1){
                    mPresenter.testClose();
                    tag--;
                }
                if(scrollY - oldScrollY < -60 && tag==0){
                    mPresenter.testOpen();
                    tag++;
                }
                if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())){
                }
            }
        });

        /**
         * 测试分享图片功能
         */
        Button btn_share = (Button)root.findViewById(R.id.share_picture);
        btn_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap bmp =  showArticleWebView.getPicture();
                ShareUtil.shareImg(view.getContext(),bmp, true);
            }
        });

        showAllComments = root.findViewById(R.id.show_allcomments);

        showPartComments = root.findViewById(R.id.show_partcomments);

        closeWin = contentView.findViewById(R.id.close_pop);

        add_globe_comment = contentView.findViewById(R.id.add_globe_comment);

        add_global_comment = editView.findViewById(R.id.add_final);
        add_global_comment.setOnClickListener(this);

        add_local_comment = addLocalView.findViewById(R.id.add_local_comment);

        fakeAdd = root.findViewById(R.id.fake_add);
        fakeAdd.setFocusable(false);
        fakeAdd.setOnClickListener(this);

        editLocalComment = addLocalView.findViewById(R.id.edit_local_comment);

        editComment = contentView.findViewById(R.id.edit_comment);
        editComment.setFocusable(false);
        editComment.setOnClickListener(this);

        editGlobalC = editView.findViewById(R.id.edit_global_comment);
        editGlobalC.setFocusableInTouchMode(true);
        editGlobalC.setFocusable(true);

        firstC = root.findViewById(R.id.comment_first);
        firstC.setClickable(true);
        firstC.setOnClickListener(this);

        secondC = root.findViewById(R.id.comment_second);
        secondC.setClickable(true);
        secondC.setOnClickListener(this);

        date1 = root.findViewById(R.id.date_first);

        date2 = root.findViewById(R.id.date_second);

        showAllComments.setOnClickListener(this);

        showPartComments.setOnClickListener(this);

        setting = navigatioinView.findViewById(R.id.n1);
        setting.setOnClickListener(this);

        share_photo = navigatioinView.findViewById(R.id.share_photo);
        share_photo.setOnClickListener(this);

        sizeIncrease = progressView.findViewById(R.id.size_increase);
        sizeIncrease.setOnClickListener(this);


        sizeDecrease = progressView.findViewById(R.id.size_decrease);
        sizeDecrease.setOnClickListener(this);

        spaceIncrease = progressView.findViewById(R.id.space_increase);
        spaceIncrease.setOnClickListener(this);


        spaceDecrease = progressView.findViewById(R.id.space_decrease);
        spaceDecrease.setOnClickListener(this);

        sizeprogressbar = progressView.findViewById(R.id.size_progress);
        sizeprogressbar.setProgress(50);

        spaceprogressbar = progressView.findViewById(R.id.space_progress);
        spaceprogressbar.setProgress(50);

        closeWin.setOnClickListener(this);

        add_globe_comment.setOnClickListener(this);

        add_local_comment.setOnClickListener(this);

        recyclerView = (RecyclerView)contentView.findViewById(R.id.globe_comments_recycler) ;

        partrecyclerView = (RecyclerView)localCommentsView.findViewById(R.id.local_comments_recycler);

        manager= new LinearLayoutManager(getContext());

        localmanager= new LinearLayoutManager(getContext());


        mListPopWindow = new CustomPopWindow.PopupWindowBuilder(getContext())
                .setView(contentView)
                .setFocusable(true)
//                .setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED)
                .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)//软键盘弹出不影响任何布局
                .size(ViewGroup.LayoutParams.MATCH_PARENT,1600)//显示大小
                .create();

         navigationWindow = new CustomPopWindow.PopupWindowBuilder(getContext())
                .setView(navigatioinView)
                .setFocusable(false)
                .size(ViewGroup.LayoutParams.MATCH_PARENT,160)//显示大小
                .create();

        popupwindow = navigationWindow.getPopupWindow();

        progressWindow = new CustomPopWindow.PopupWindowBuilder(getContext())
                .setView(progressView)
                .setFocusable(true)
                .size(ViewGroup.LayoutParams.MATCH_PARENT,400)//显示大小
                .create();

        localCommentWin = new CustomPopWindow.PopupWindowBuilder(getContext())
                .setView(localCommentsView)
                .setFocusable(true)
                .setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED)
                .size(ViewGroup.LayoutParams.MATCH_PARENT,1500)//显示大小
                .create();

        //设置添加部分评论的窗口
        addLocalCommentWin = new CustomPopWindow.PopupWindowBuilder(getContext())
                .setView(addLocalView)
                .setFocusable(true)
                .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)//防止PopupWindow被软件盘挡住
                .size(400,80)//显示大小
                .create();

        //
        editGlobalComment = new CustomPopWindow.PopupWindowBuilder(getContext())
                .setView(editView)
//                .setFocusable(true)        //点击外部不触发事件
//                .setTouchable(true)
                .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
                .size(ViewGroup.LayoutParams.MATCH_PARENT,960)//显示大小
                .create();


        mView = root;
        return root;
    }
    @Override
    public void showEditpop(final EditText editText) {
        //不同的显示方式
        editGlobalComment.showAtLocation(mView.findViewById(R.id.show_allcomments), Gravity.BOTTOM, 0, 0);
        editText.setFocusable(true);
        //截断edittext获得焦点？
//        editText.setFocusableInTouchMode(true);
//        editText.requestFocus();
        //为了让数据加载完全，等待88ms打开软键盘
        Timer timer = new Timer();
        timer.schedule(new TimerTask()
        {
            public void run()
            {
                InputMethodManager inputManager =(InputMethodManager)editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(editText, 0);
            }
        },88);
    }

    //加载webView
    @Override
    public void showWebview(String title, String author, String html) {
        showArticleWebView.initWebView(textsize,textcolor,textspacing);
        showArticleWebView.showContent(title, author, html);
    }

     //加载popupwindow
    @Override
    public void loadPopUpWindow(View contentView) {

        mListPopWindow.showAtLocation(mView.findViewById(R.id.show_allcomments), Gravity.BOTTOM, 0, 0);

    }

    @Override
    public void loadFakePopUpWindow(final EditText editText) {
        mListPopWindow.showAtLocation(mView.findViewById(R.id.show_allcomments), Gravity.BOTTOM, 0, 0);
        editGlobalComment.showAtLocation(mView.findViewById(R.id.show_allcomments), Gravity.BOTTOM, 0, 0);
//  `      editText.setFocusable(true);
//        editText.setFocusableInTouchMode(true);`
        editText.requestFocus();
//        //为了让数据加载完全，等待66ms打开软键盘
        Timer timer = new Timer();
        timer.schedule(new TimerTask()
        {
            public void run()
            {
                InputMethodManager inputManager =(InputMethodManager)editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(editText, 0);
            }
        },1000);
    }

    //重新设置需要加载的globalcommentlist
    @Override
    public void setGlobalList(List<GlobalComment> globalList) {
        mGlobalList = globalList;
    }

    @Override
    public void setLocalList(List<LocalComment> localList) {
        mLocalList = localList;
    }

    @Override
    public void onSetTwoComments(TextView date1, TextView comment1, TextView date2, TextView comment2) {
        mAdapter.setTwoComments(date1,comment1,date2,comment2);
    }

    //展示局部评论
    @Override
    public void loadLocalWin() {
        localCommentWin.showAtLocation(mView.findViewById(R.id.show_allcomments), Gravity.BOTTOM, 0, 0);
    }

    @Override
    public void initialLocalAdapter() {
        localmanager.setOrientation(LinearLayoutManager.VERTICAL);

        partrecyclerView.setLayoutManager(localmanager);

       mLocalAdapter= new LocalCommentsAdapter(getContext(),mLocalList);

        //------------------------------点击删除评论--------------------------------------------------------------//
       mLocalAdapter.setOnItemClickListener(new LocalCommentsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("删除");
                builder.setMessage("确定删除吗?");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //先从数据库中删除 再到list中删除
                        mPresenter.deleteLocalComment(mLocalList.get(position));

                        mPresenter.deleteLocalCommentFromView(position);

                    }
                });
                builder.setNegativeButton("取消",null);
                builder.show();

            }
        });
         partrecyclerView.setAdapter(mLocalAdapter);

    }

    //初始化adapter，穿入加载完成的myglobalcommentlist
    @Override
    public void initializeAdapter() {

        manager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(manager);

        mAdapter = new GlobalCommentsAdapter(getContext(),mGlobalList);

        //------------------------------点击删除评论--------------------------------------------------------------//
        mAdapter.setOnItemClickListener(new GlobalCommentsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("删除");
                builder.setMessage("确定删除吗?");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                      //先从数据库中删除 再到list中删除

                       mPresenter.deleteGlobalComment(mGlobalList.get(position));

                        //在adapter,adapter中删除那么fragment中也会被删除，fragment中有GlobalComment是为了下次能够获得的globalcomment对象
                       mPresenter.deleteGlobalCommentFromView(position);

                       //设置评论
                       mPresenter.setTwoComments(date1,firstC,date2,secondC);

                    }
                });
                builder.setNegativeButton("取消",null);
                builder.show();

            }
        });
        recyclerView.setAdapter(mAdapter);
    }

    //使用Adapter删除一项，并刷新list
    @Override
    public void onDeleteGlobalCommentFromView(int position) {
       mAdapter.deleteItem(position);
    }

    @Override
    public void onDeleteLocalCommentFromView(int position) {
        mLocalAdapter.deleteItem(position);
    }

    //关闭全局评论窗口
    @Override
    public void popWindowClose() {
        mListPopWindow.dissmiss();
    }

    @Override
    public void loadLocalPopUpWindow(View contenView) {

    }

    @Override
    public void onAddGlobalComment(List<GlobalComment> newGlobeItemList) {
       mGlobalList = newGlobeItemList;
       mAdapter.notifyDataSetChanged();
    }

    @Override
    public void showPwinAndDisNwin() {
        navigationWindow.dissmiss();
        progressWindow.showAtLocation(mView.findViewById(R.id.show_allcomments), Gravity.BOTTOM, 0, 0);
    }

    @Override
    public void onCloseNavigationWin() {
        navigationWindow.dissmiss();
    }

    @Override
    public void increaseText() {
        if (textsize < 150) {
            textsize = textsize+10;
            showArticleWebView.setTextSize(textsize);
            int pro = sizeprogressbar.getProgress();
            pro +=10;
            sizeprogressbar.setProgress(pro);
        }
    }

    @Override
    public void decreaseText() {
        if(textsize>50){
            textsize = textsize - 10;
            showArticleWebView.setTextSize(textsize);
            int pro = sizeprogressbar.getProgress();
            pro = pro - 10;
            sizeprogressbar.setProgress(pro);
        }
    }

    @Override
    public void increaseSpace() {
        if(spaceprogressbar.getProgress()<100){
            textspacing = textspacing+10;
            showArticleWebView.setTextSpacing(textspacing);
            int pro = spaceprogressbar.getProgress();
            pro = pro + 10;
            spaceprogressbar.setProgress(pro);
        }
    }

    @Override
    public void decreaseSpace() {
        if(spaceprogressbar.getProgress()>0){
            textspacing = textspacing-10;
            showArticleWebView.setTextSpacing(textspacing);
            int pro = spaceprogressbar.getProgress();
            pro -=10;
            spaceprogressbar.setProgress(pro);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.show_allcomments:
                mPresenter.showPopUpWindow(contentView);
                break;

            case R.id.show_partcomments:
                mPresenter.showPartPopUpWindow(localCommentsView);
                break;

            //关闭全局评论窗口
            case R.id.close_pop:
                mPresenter.closePopWindow();
                break;

             //添加全局评论
            case R.id.add_globe_comment:
                String mText = editComment.getText().toString();

                if(!mText.isEmpty()) {
                    //在数据库中添加
                    mPresenter.addGlobalComment(mText);
                    mPresenter.createAdapter();
                    mPresenter.setTwoComments(date1,firstC,date2,secondC);
                    editComment.setText("");
                }
                else
                    Toast.makeText(getContext(),"请输入内容再添加",Toast.LENGTH_SHORT).show();

                //按下发送之后软键盘消失
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editComment.getWindowToken(), 0);
                break;

            case R.id.n1:
                mPresenter.showProgressWin();
                break;

            case R.id.size_increase:
                mPresenter.increaseTextSize();
                break;

            case R.id.size_decrease:
                mPresenter.decreaseTextsize();
                break;

            case R.id.space_increase:
                mPresenter.increaseSpaceSize();
                break;

            case R.id.space_decrease:
                mPresenter.decreaseSpaceSize();
                break;

            case R.id.add_local_comment:
                String localComment = editLocalComment.getText().toString();
                if(localComment!=null){
                mPresenter.addLocalComment(editLocalComment,localComment,localContent);
                mPresenter.createLocalAdapter();
                editLocalComment.clearComposingText();
                editLocalComment.setText("");
                }
                else
                    Toast.makeText(getContext(),"Please Enter sth",Toast.LENGTH_SHORT).show();

                InputMethodManager immL = (InputMethodManager) getContext().getSystemService(INPUT_METHOD_SERVICE);
                immL.hideSoftInputFromWindow(editLocalComment.getWindowToken(), 0);
                break;

            case R.id.fake_add:
                mPresenter.fakeShowPopUpwindow(editComment);
                break;

            case R.id.comment_first:
                mPresenter.showPopUpWindow(contentView);
                break;

            case R.id.comment_second:
                mPresenter.showPopUpWindow(contentView);
                break;

            case R.id.edit_comment:
                mPresenter.showGlbC(editGlobalC);
                break;

            case R.id.add_final:
                String content = editGlobalC.getText().toString();
                if(content!=null){
                    mPresenter.addGlobalComment(content);
                    mPresenter.createAdapter();
                    mPresenter.setTwoComments(date1,firstC,date2,secondC);
                    editGlobalC.clearComposingText();
                    editGlobalC.setText("");
                }
                else
                    Toast.makeText(getContext(),"请输入内容再添加",Toast.LENGTH_SHORT).show();

                break;

                //点击分享图片
            case R.id.share_photo:



                break;
            default:
                break;
        }
    }
}
