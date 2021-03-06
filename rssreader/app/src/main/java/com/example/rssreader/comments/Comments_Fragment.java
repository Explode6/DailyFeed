package com.example.rssreader.comments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.DisplayMetrics;
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
import com.example.rssreader.util.ConfigUtil;
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
    private Button showAllComments;                                    //????????????
    private Button showPartComments;                                  //????????????
    private  ShowArticleWebView showArticleWebView;                  //webview??????
    private  View contentView;                                      //???????????????
    //popUpWindow???view
    private  View navigatioinView;                                //????????????view
    private  View progressView;                                  //?????????????????????view
    private  View root;
    private  View mainView;                                     //??????mainView
    private CustomPopWindow mListPopWindow;                     //???????????????popUpWindow
    private CustomPopWindow navigationWindow;                  //?????????????????????
    private CustomPopWindow progressWindow;                   //????????????????????????????????????
    private  View mView;                                     //??????popupwindow?????????View
    private RecyclerView recyclerView;                      //???????????????recyclerView
    private RecyclerView localRecyclerView;                //???????????????recyclerView
    private GlobalCommentsAdapter mAdapter;               //????????????recyclerView???adapter
    private LocalCommentsAdapter mLocalAdapter;          //????????????recyclerView???adapter
    private  Button closeWin;                            //??????popupwindow
    private  Button add_globe_comment;                   // ??????????????????
    private  Button add_global_comment;
    private  Button add_local_comment;                  // ??????????????????
    private  TextView firstC;                          //???????????????
    private  TextView secondC;                        //???????????????
    private  TextView date1;                         //???????????????
    private  TextView date2;                        //???????????????
    private CustomPopWindow localCommentWin;        //???????????????popupwindow
    private CustomPopWindow addLocalCommentWin;         //?????????????????????win
    private RecyclerView partrecyclerView;         //???????????????recyclerView
    private  PopupWindow popupwindow;            //????????????nestedscrollview
    private Button setting;                //??????????????????
    private Button sizeIncrease;          // ????????????
    private Button sizeDecrease;         // ????????????
    private Button spaceIncrease;       // ????????????
    private Button spaceDecrease;      // ????????????
    private Button share_photo;      // ????????????
    private View localCommentsView;   //??????local
    private View addLocalView;             //??????????????????
    private EditText editLocalComment;    //?????????????????????
    private EditText fakeAdd;            //?????????popupwindow?????????edittext
    private EditText editComment;       //?????????popupwindow?????????edittext
    private EditText editGlobalC;      //?????????popupwindow?????????edittext(?????????????????????
    private ProgressBar sizeprogressbar;
    private ProgressBar spaceprogressbar;
    private ArticleBrief mArticleBrief;
    private int textsize = 100;         //?????????????????????100
    private int textspacing = 150;      //???????????????0
    private LinearLayoutManager manager;
    private LinearLayoutManager localmanager;
    private NestedScrollView mnestedScrollView;
    private List<GlobalComment> mGlobalList; //?????????????????????????????????
    private List<LocalComment> mLocalList; //?????????????????????????????????
    private int tag = 1;
    private View editView;
    private CustomPopWindow editGlobalComment;         //????????????
    private TextView close_edit;
    private   String localContent;
    public static  CommentsActivity activity;


    //???????????? ???????????????activity?????????????????????fragment??????????????????fragment????????????????????????activity  ??????fragment?????????presenter??????
    public  android.view.ActionMode setData(android.view.ActionMode mode){
        List<String> actionList = new ArrayList<String>();
        actionList.add("??????");
        mode  = showArticleWebView.getActionMode(mode, actionList, new ShowArticleWebView.onActionClickListener() {
            @Override
            public void onActionClick(String title, String text) {
                switch (title){
                    case "??????":
                        mPresenter.showLocalAdd(text);
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
    //???presenter???commentsView??????
    public void setPresenter(@NonNull CommentsContract.CommentsPresenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Nullable
    //????????????????????????????????????
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

//         CommentsActivity.activity.setBackgroundAlpha();
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
                //?????? ?????? ?????????????????????

                if (scrollY - oldScrollY > 100 && popupwindow.isShowing() && tag==1) {
                    mPresenter.testClose();
                    tag--;
                    mPresenter.closeNavigationWin();

                }
                if (scrollY - oldScrollY > 100 && !popupwindow.isShowing() && tag==1) {
                    mPresenter.testClose();
                    tag--;
                }
                //?????? ????????????????????????
                if (scrollY - oldScrollY < -100 && !popupwindow.isShowing() && tag==0) {
                    mPresenter.testOpen();
                    tag++;
                    navigationWindow.showAtLocation(root.findViewById(R.id.show_allcomments), Gravity.BOTTOM, 0, 0);
                }

                if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())){
                }
            }
        });

        /**
         * ????????????????????????
         */
//        Button btn_share = (Button)root.findViewById(R.id.share_picture);
//        btn_share.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Bitmap bmp =  showArticleWebView.getPicture();
//                ShareUtil.shareImg(view.getContext(),bmp, true);
//            }
//        });

        showAllComments = root.findViewById(R.id.show_allcomments);

        showPartComments = root.findViewById(R.id.show_partcomments);

        closeWin = contentView.findViewById(R.id.close_pop);

        add_globe_comment = contentView.findViewById(R.id.add_globe_comment);

        add_global_comment = editView.findViewById(R.id.add_final);
        add_global_comment.setOnClickListener(this);

        close_edit = editView.findViewById(R.id.closeedit);
        close_edit.setOnClickListener(this);



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

        share_photo = navigatioinView.findViewById(R.id.n2);
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
                .enableBackgroundDark(false)
                .setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED)
                .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)//????????????????????????????????????
                .size(ViewGroup.LayoutParams.MATCH_PARENT,2116-300)//????????????
                .create();

         navigationWindow = new CustomPopWindow.PopupWindowBuilder(getContext())
                .setView(navigatioinView)
                .setFocusable(false)
                 .setOutsideTouchable(false)
                .size(ViewGroup.LayoutParams.MATCH_PARENT,160)//????????????
                .create();

        popupwindow = navigationWindow.getPopupWindow();

        progressWindow = new CustomPopWindow.PopupWindowBuilder(getContext())
                .setView(progressView)
                .setFocusable(true)
                .size(ViewGroup.LayoutParams.MATCH_PARENT,400)//????????????
                .create();

        localCommentWin = new CustomPopWindow.PopupWindowBuilder(getContext())
                .setView(localCommentsView)
                .setFocusable(true)
                .setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED)
                .size(ViewGroup.LayoutParams.MATCH_PARENT,1500)//????????????
                .create();

        //?????????????????????????????????
        addLocalCommentWin = new CustomPopWindow.PopupWindowBuilder(getContext())
                .setView(addLocalView)
                .setFocusable(true)
                .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)//??????PopupWindow??????????????????
                .size(400,80)//????????????
                .create();





        mView = root;
        return root;
    }
    @Override
    public void showEditpop() {
        //?????????????????????
        WindowManager manager = (WindowManager) CommentsActivity.activity.getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams layoutParams = CommentsActivity.activity.getWindow().getAttributes();
        layoutParams.alpha = 0.5f;
        CommentsActivity.activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        CommentsActivity.activity.getWindow().setAttributes(layoutParams);
        editGlobalComment = new CustomPopWindow.PopupWindowBuilder(getContext())
                .setView(editView)
                .setFocusable(true)        //???????????????????????????
                .setTouchable(true)
                .enableBackgroundDark(true)
//                .setBgDarkAlpha(0.7f)
                .setOnDissmissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        mPresenter.setSecondEdit();
                        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(editGlobalC.getWindowToken(), 0);
                    }
                })
                .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
                .size(ViewGroup.LayoutParams.MATCH_PARENT,960)//????????????
                .create();
        editGlobalComment.showAtLocation(mView.findViewById(R.id.show_allcomments), Gravity.BOTTOM, 0, 0);
        //????????????????????????????????????88ms???????????????
        editGlobalC.setFocusable(true);
        editGlobalC.setFocusableInTouchMode(true);
        editGlobalC.requestFocus();
//        InputMethodManager inputManager =(InputMethodManager) getContext().getSystemService(INPUT_METHOD_SERVICE);
//        inputManager.showSoftInput(editGlobalC, 0);
        Timer timer = new Timer();
        timer.schedule(new TimerTask()
        {
            public void run()
            {
                InputMethodManager inputManager =(InputMethodManager) getContext().getSystemService(INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(editGlobalC, 0);
            }
        },88);
    }

    @Override
    public void saveComment() {
        editComment.setText(editGlobalC.getText().toString());
    }

    @Override
    public void loadLocalAdd(String content) {
       localContent = content.replace("\\n","\n");
        addLocalCommentWin= new CustomPopWindow.PopupWindowBuilder(getContext())
                .setView(addLocalView)
                .setFocusable(true)
                .size(600,300)//????????????
                .create()
                .showAtLocation(mView.findViewById(R.id.show_allcomments), Gravity.RIGHT, 0, -100);
    }

    //??????webView
    @Override
    public void showWebview(String title, String author, String html) {
        showArticleWebView.initWebView(textsize,textspacing, ConfigUtil.getInstance(this.getContext()).isDarkMode());
        showArticleWebView.showContent(title, author, html);
    }

     //??????popupwindow
    @Override
    public void loadPopUpWindow(int height) {
        mListPopWindow = new CustomPopWindow.PopupWindowBuilder(getContext())
                .setView(contentView)
                .setFocusable(true)
                .enableBackgroundDark(false)
                .setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED)
                .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)//????????????????????????????????????
                .size(ViewGroup.LayoutParams.MATCH_PARENT,height)//????????????
                .create();
        mListPopWindow.showAtLocation(mView.findViewById(R.id.show_allcomments), Gravity.BOTTOM, 0, 0);

    }

    @Override
    public void loadFakePopUpWindow() {
        mListPopWindow.showAtLocation(mView.findViewById(R.id.show_allcomments), Gravity.BOTTOM, 0, 0);
        editGlobalComment = new CustomPopWindow.PopupWindowBuilder(getContext())
                .setView(editView)
                .setFocusable(true)        //???????????????????????????
                .setTouchable(true)
                .enableBackgroundDark(true)
                .setBgDarkAlpha(0.7f)
                .setOnDissmissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        mPresenter.setSecondEdit();
                        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(editGlobalC.getWindowToken(), 0);
                    }
                })
                .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
                .size(ViewGroup.LayoutParams.MATCH_PARENT,960)//????????????
                .create();
        editGlobalComment.showAtLocation(mView.findViewById(R.id.show_allcomments), Gravity.BOTTOM, 0, 0);
        editGlobalC.setFocusable(true);
        editGlobalC.setFocusableInTouchMode(true);
        editGlobalC.requestFocus();
//        //????????????????????????????????????66ms???????????????
        Timer timer = new Timer();
        timer.schedule(new TimerTask()
        {
            public void run()
            {
                InputMethodManager inputManager = (InputMethodManager) getContext().getSystemService(INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(editGlobalC, 0);
            }
        },66);
    }

    //???????????????????????????globalcommentlist
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

    //??????????????????
    @Override
    public void loadLocalWin() {
        localCommentWin.showAtLocation(mView.findViewById(R.id.show_allcomments), Gravity.BOTTOM, 0, 0);
    }

    @Override
    public void initialLocalAdapter() {
        localmanager.setOrientation(LinearLayoutManager.VERTICAL);

        partrecyclerView.setLayoutManager(localmanager);

       mLocalAdapter= new LocalCommentsAdapter(getContext(),mLocalList);

        //------------------------------??????????????????--------------------------------------------------------------//
       mLocalAdapter.setOnItemClickListener(new LocalCommentsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("??????");
                builder.setMessage("????????????????");
                builder.setPositiveButton("??????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //???????????????????????? ??????list?????????
                        mPresenter.deleteLocalComment(mLocalList.get(position));

                        mPresenter.deleteLocalCommentFromView(position);

                    }
                });
                builder.setNegativeButton("??????",null);
                builder.show();

            }
        });
         partrecyclerView.setAdapter(mLocalAdapter);

    }

    //?????????adapter????????????????????????myglobalcommentlist
    @Override
    public void initializeAdapter() {

        manager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(manager);

        mAdapter = new GlobalCommentsAdapter(getContext(),mGlobalList);

        //------------------------------??????????????????--------------------------------------------------------------//
        mAdapter.setOnItemClickListener(new GlobalCommentsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("??????");
                builder.setMessage("????????????????");
                builder.setPositiveButton("??????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                      //???????????????????????? ??????list?????????

                       mPresenter.deleteGlobalComment(mGlobalList.get(position));

                        //???adapter,adapter???????????????fragment?????????????????????fragment??????GlobalComment??????????????????????????????globalcomment??????
                       mPresenter.deleteGlobalCommentFromView(position);

                       //????????????
                       mPresenter.setTwoComments(date1,firstC,date2,secondC);

                    }
                });
                builder.setNegativeButton("??????",null);
                builder.show();

            }
        });
        recyclerView.setAdapter(mAdapter);
    }

    //??????Adapter????????????????????????list
    @Override
    public void onDeleteGlobalCommentFromView(int position) {
       mAdapter.deleteItem(position);
    }

    @Override
    public void onDeleteLocalCommentFromView(int position) {
        mLocalAdapter.deleteItem(position);
    }

    //????????????????????????
    @Override
    public void popWindowClose() {
        mListPopWindow.dissmiss();
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
                mPresenter.showPopUpWindow();
                break;

            case R.id.show_partcomments:
                mPresenter.showPartPopUpWindow(localCommentsView);
                break;

            //????????????????????????
            case R.id.close_pop:
                mPresenter.closePopWindow();
                break;

             //??????????????????
            case R.id.add_globe_comment:
                String mText = editComment.getText().toString();

                if(!mText.isEmpty()) {
                    //?????????????????????
                    mPresenter.addGlobalComment(mText);
                    mPresenter.createAdapter();
                    mPresenter.setTwoComments(date1,firstC,date2,secondC);
                    editComment.setText("");
                }
                else
                    Toast.makeText(getContext(),"????????????????????????",Toast.LENGTH_SHORT).show();

                //?????????????????????????????????
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
                mPresenter.fakeShowPopUpwindow();
                break;

            case R.id.comment_first:
                mPresenter.showPopUpWindow();
                break;

            case R.id.comment_second:
                mPresenter.showPopUpWindow();
                break;

            case R.id.edit_comment:
                mPresenter.showGlbC();
                break;

            case R.id.add_final:
                String content = editGlobalC.getText().toString();
                if(content != null){
                    mPresenter.addGlobalComment(content);
                    mPresenter.createAdapter();
                    mPresenter.setTwoComments(date1,firstC,date2,secondC);
                    editGlobalC.clearComposingText();
                    editGlobalC.setText("");
                    editGlobalComment.dissmiss();
                    InputMethodManager immc = (InputMethodManager) getContext().getSystemService(INPUT_METHOD_SERVICE);
                    immc.hideSoftInputFromWindow(editGlobalC.getWindowToken(), 0);
                    Toast.makeText(getContext(),"??????????????????",Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(getContext(),"????????????????????????",Toast.LENGTH_SHORT).show();

                break;

                //??????????????????
            case R.id.n2:
                Bitmap bmp =  showArticleWebView.getPicture();
                ShareUtil.shareImg(getContext(),bmp, true);
                break;

            case R.id.closeedit:
                editGlobalComment.dissmiss();

            default:
                break;
        }
    }
}
