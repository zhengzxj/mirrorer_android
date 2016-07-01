package com.smart.mirrorer.home;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.smart.mirrorer.R;
import com.smart.mirrorer.adapter.HomeHeadAdapter;
import com.smart.mirrorer.adapter.HomeQuestionAdapter;
import com.smart.mirrorer.adapter.MidTutorMatchAdapter;
import com.smart.mirrorer.agora.CallingActivity;
import com.smart.mirrorer.base.BaseApplication;
import com.smart.mirrorer.base.BaseTitleActivity;
import com.smart.mirrorer.base.TutorInfoTypeInActivity;
import com.smart.mirrorer.bean.PushQuestionBean;
import com.smart.mirrorer.bean.home.ApkUpdateBean;
import com.smart.mirrorer.bean.home.CommonTutorItemData;
import com.smart.mirrorer.bean.home.HomeIndexBean;
import com.smart.mirrorer.bean.home.OrderDetailsBean;
import com.smart.mirrorer.bean.home.QuestionFocusTutorsBean;
import com.smart.mirrorer.bean.home.QuestionListBean;
import com.smart.mirrorer.bean.home.RecommonBean;
import com.smart.mirrorer.db.MatchQuestionProviderService;
import com.smart.mirrorer.event.BusProvider;
import com.smart.mirrorer.event.CallConnectEvent;
import com.smart.mirrorer.event.DeleteQuestionEvent;
import com.smart.mirrorer.event.LogOutEvent;
import com.smart.mirrorer.event.PushTutorInfoMsgEvent;
import com.smart.mirrorer.event.ReLoadQuestionEvent;
import com.smart.mirrorer.event.UpdateQuestionEvent;
import com.smart.mirrorer.history.HistoryActivity;
import com.smart.mirrorer.net.GsonCallbackListener;
import com.smart.mirrorer.net.PerfectJsonObjectRequest;
import com.smart.mirrorer.service.HeartbeatService;
import com.smart.mirrorer.service.QuestionService;
import com.smart.mirrorer.setting.SettingActivity;
import com.smart.mirrorer.setting.SuggestionBackActivity;
import com.smart.mirrorer.util.CommonUtils;
import com.smart.mirrorer.util.GloabalRequestUtil;
import com.smart.mirrorer.util.KeyboardService;
import com.smart.mirrorer.util.MirrorSettings;
import com.smart.mirrorer.util.TipsUtils;
import com.smart.mirrorer.util.UpdateAppManager;
import com.smart.mirrorer.util.Urls;
import com.smart.mirrorer.util.mUtil.L;
import com.smart.mirrorer.wallet.WalletActivity;

import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cn.jpush.android.api.JPushInterface;
import de.hdodenhof.circleimageview.CircleImageView;

public class NewMainActivity extends BaseTitleActivity implements View.OnClickListener, MidTutorMatchAdapter.ITutorMatchItemClickListener {

    private final int REQUEST_TUTOR_BACK_CODE = 22;
    private boolean isTutor;
    private RelativeLayout mTutorRootLayout;
    private RelativeLayout mStudentRootLayout;
    //    private LinearLayout mFollowLayout;
    private TextView mCallTimeTv;
    private TextView mTutorReceiverTv;
    private TextView mTutorSaleTv;
    private LinearLayout mToggleBtn;
    private DrawerLayout mDrawerLayout;
    private FrameLayout myDrawer;
    private RelativeLayout mNavigationView;

    private MirrorSettings mSettings;
    private String mUid;
    private Handler uiHandler = new Handler();
    private Intent mHeartServiceIntent;
    private HomeHeadAdapter mHeadAdapter;
    private HomeQuestionAdapter mQuestionAdapter;
    private MidTutorMatchAdapter mMatchAdapter;
    private List<PushQuestionBean> mTestMatcheList = new ArrayList<>();
    private TextView mChangeAppTv;
    private CountDownTimer mProgressTimer;
    private RelativeLayout mTimerProgressLayout;
    private TextView mTimerTv;
    private ExecutorService mSingleThreadExecutor = Executors.newSingleThreadExecutor();
    private boolean isCreate;
    private int versionCode = -1;
    private boolean isAutoUpdate;//onCreate中检测更新和右上角点开的手动更新区别标记 true:自动更新 false:手动更新
    private UpdateAppManager updateManager;
    private CircleImageView mUserHeadIv;
    private TextView mNickTv;
    private boolean isOpen;
    private ImageView voiceIv;
    private KeyboardService mKeyService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        L.d("onCreate");
        super.onCreate(savedInstanceState);

        //透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //透明导航栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        setContentView(R.layout.activity_new_main);
        //eventbus,jpush,muid,istutor
        BusProvider.getInstance().register(this);
        JPushInterface.clearAllNotifications(getApplicationContext());

        mKeyService = new KeyboardService(this);

        mSettings = BaseApplication.getSettings(this);
        mUid = mSettings.APP_UID.getValue();
        isTutor = mSettings.APP_IS_TUTOR_TYPE.getValue();
        //心跳
        mHeartServiceIntent = new Intent(this, HeartbeatService.class);
        mHeartServiceIntent.putExtra("app_uid_service", mUid);
        startService(mHeartServiceIntent);

        initView();
        initListener();

        startHeartLoop(2000);

        isCreate = true;
        checkAndUpdate();
    }
    private FrameLayout fl_for_dialog;
    private View v_blank;
    private TextView tv_cancel;
    private TextView tv_commite;
    private EditText et_question_content;
    private LinearLayout home_question_more;
    private ImageView iv_guanzhu;
    //1.==========界面==============
    private void initView() {
        L.d("initView");

        iv_guanzhu = (ImageView)findViewById(R.id.iv_guanzhu);
        iv_guanzhu.setOnClickListener(this);
        home_question_more = (LinearLayout)findViewById(R.id.home_question_more);
        home_question_more.setOnClickListener(this);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.main_drawer_layout);
        mNavigationView = (RelativeLayout) findViewById(R.id.main_navigation_view);
        //侧边栏
        View headerView = mNavigationView.findViewById(R.id.navigation_head_rl);
        headerView.setOnClickListener(this);

        mUserHeadIv = (CircleImageView) headerView.findViewById(R.id.navigation_head_iv);
        mNickTv = (TextView) headerView.findViewById(R.id.navigation_nick_tv);

        final LinearLayout walletItem = (LinearLayout) mNavigationView.findViewById(R.id.navigation_wallet_item);
        final LinearLayout historyItem = (LinearLayout) mNavigationView.findViewById(R.id.navigation_history_item);
//        LinearLayout updateItem = (LinearLayout) mNavigationView.findViewById(R.id.navigation_update_item);
//        LinearLayout recommonItem = (LinearLayout) mNavigationView.findViewById(R.id.navigation_recommon_item);
        final LinearLayout fankuiItem = (LinearLayout) mNavigationView.findViewById(R.id.navigation_fankui_item);
        final LinearLayout settingItem = (LinearLayout) mNavigationView.findViewById(R.id.navigation_setting_item);
//        RelativeLayout changeAppItem = (RelativeLayout) mNavigationView.findViewById(R.id.navigation_change_app_rl);
        mChangeAppTv = (TextView) mNavigationView.findViewById(R.id.navigation_change_app_tv);
        walletItem.setOnClickListener(this);
        historyItem.setOnClickListener(this);
//        updateItem.setOnClickListener(this);
//        recommonItem.setOnClickListener(this);
        fankuiItem.setOnClickListener(this);
        settingItem.setOnClickListener(this);
        mChangeAppTv.setOnClickListener(this);

        int colorFront = getResources().getColor(R.color.app_base_bg_color);
        int colorBack = Color.parseColor("#a0a0a0");

        CommonUtils.onToutchChangeBGColor(fankuiItem,colorBack,colorFront);
        CommonUtils.onToutchChangeBGColor(walletItem,colorBack,colorFront);
        CommonUtils.onToutchChangeBGColor(historyItem,colorBack,colorFront);
        CommonUtils.onToutchChangeBGColor(settingItem,colorBack,colorFront);
        CommonUtils.onToutchChangeBGColor(mChangeAppTv,colorBack,colorFront);

//        mToggleBtn = (LinearLayout) findViewById(R.id.main_navigation_btn);
        mToggleBtn.setOnClickListener(this);

//        mFollowLayout = (LinearLayout) findViewById(R.id.main_title_follow_layout);
//        mFollowLayout.setOnClickListener(this);

        //===========回答者:推送问题列表=========
        mTutorRootLayout = (RelativeLayout) findViewById(R.id.main_content_mid_root_tutor);
        mCallTimeTv = (TextView) findViewById(R.id.mid_tutor_call_time_tv);
        mTutorReceiverTv = (TextView) findViewById(R.id.mid_tutor_receive_count_tv);
        mTutorSaleTv = (TextView) findViewById(R.id.mid_tutor_money_tv);

        RecyclerView macthRecyclerView = (RecyclerView) findViewById(R.id.mid_tutor_match_question_listview);
        macthRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mMatchAdapter = new MidTutorMatchAdapter();
        mMatchAdapter.setItemListener(this);
        macthRecyclerView.setAdapter(mMatchAdapter);
        mMatchAdapter.setListData(mTestMatcheList);

        mTimerProgressLayout = (RelativeLayout) findViewById(R.id.mid_tutor_time_progress_layout);
        mTimerTv = (TextView) findViewById(R.id.mid_tutor_timer_tv);

        //===========提问者:问题列表=========
        mStudentRootLayout = (RelativeLayout) findViewById(R.id.main_content_mid_root);
        findViewById(R.id.home_heda_more).setOnClickListener(this);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.home_horizatal_listview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mHeadAdapter = new HomeHeadAdapter(this);
        recyclerView.setAdapter(mHeadAdapter);

        SwipeMenuListView swipQuestionView = (SwipeMenuListView) findViewById(R.id.home_question_listview);
        mQuestionAdapter = new HomeQuestionAdapter(this, R.layout.home_question_list_cell);

        //---------历史问题---改成了直接提问(qid传0)--------
        mQuestionAdapter.setCallListener(new HomeQuestionAdapter.ICallHistoryListener() {
            @Override
            public void matchHisitory(QuestionListBean.ResultBean.ListBean itemData) {
                if (itemData != null)
                    matchTutor(itemData.getQid(),itemData.getContent());
            }
        });
        //---------删除问题-----------
        swipQuestionView.setAdapter(mQuestionAdapter);
        swipQuestionView.setMenuCreator(mQuestionAdapter);
        swipQuestionView.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);
        swipQuestionView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                if (index==0)
                    deleteQuestion(position);
                return false;//关闭菜单
            }
        });

        //-----------问题详情页面----------
        swipQuestionView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                L.d( "position=" + position);
                QuestionListBean.ResultBean.ListBean itemData = mQuestionAdapter.getItem(position);
                if (itemData == null) {
                    return;
                }
                showAlertDialog(itemData);
//
            }
        });

        voiceIv = (ImageView) findViewById(R.id.home_voice_iv);
        voiceIv.setOnClickListener(this);
    }
    //========问题弹窗=======
    private boolean isEdit = true;
    private void showAlertDialog(final QuestionListBean.ResultBean.ListBean itemData)
    {

        final AlertDialog myDialog = new AlertDialog.Builder(NewMainActivity.this).create();
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        RelativeLayout layout = (RelativeLayout)inflater.inflate(R.layout.dialog_question, null);
        myDialog.setView(layout);
        myDialog.show();

        myDialog.getWindow().setContentView(R.layout.dialog_question);

        Window dialogView = myDialog.getWindow();

        final TextView tv_bianji = (TextView)dialogView.findViewById(R.id.tv_bianji);
        final TextView tv_cancel = (TextView)dialogView.findViewById(R.id.tv_cancel);
        final TextView tv_commit = (TextView)dialogView.findViewById(R.id.tv_commit);
        final EditText ev_question_content = (EditText)dialogView.findViewById(R.id.et_question_content);
        final RelativeLayout rl_bg = (RelativeLayout)dialogView.findViewById(R.id.rl_bg);
        final TextView tv_text_count = (TextView)dialogView.findViewById(R.id.tv_text_count);
        final TextView tv_question_content = (TextView)dialogView.findViewById(R.id.tv_question_content);

        tv_text_count.setText(itemData.getContent().length()+"/100");
        ev_question_content.setText(itemData.getContent());
        ev_question_content.setSelection(itemData.getContent().length());

        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
                isEdit = true;
            }
        });
        tv_commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isEdit)
                {
                    //变成再次发送界面
                    tv_bianji.setVisibility(View.INVISIBLE);
                    rl_bg.setBackgroundResource(R.drawable.shape_question_corner);
                    tv_cancel.setBackgroundResource(R.drawable.shape_question_btn_show);
                    tv_commit.setBackgroundResource(R.drawable.shape_question_btn_show);
                    tv_question_content.setText(ev_question_content.getText().toString());
                    tv_question_content.setVisibility(View.VISIBLE);
                    mKeyService.hideKeyboard(ev_question_content);
                    ev_question_content.setVisibility(View.INVISIBLE);
                    tv_commit.setText("再次提问");
                    isEdit = false;
                }else
                {
                    myDialog.dismiss();
                    isEdit = true;
                    String currentDesc = tv_question_content.getText().toString();
                    String qid = "0";
                    if(!TextUtils.isEmpty(currentDesc)&&currentDesc.equals(itemData.getContent()))qid = itemData.getQid();
                    matchTutor(qid,currentDesc);
                }
            }
        });
//        TODO:可能要改成myTextUtils
        ev_question_content.addTextChangedListener(new TextWatcher() {

            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {
                String dest = ev_question_content.getText().toString();
                if(dest.length()>100)
                {
                    ev_question_content.setText(dest.substring(0,100));
                    TipsUtils.showShort(getApplicationContext(),"字数不能超过100");
                    ev_question_content.setSelection(ev_question_content.getText().toString().length());
                }
                tv_text_count.setText(ev_question_content.getText().toString().length()+"/100");
            }
        });

    }
    //============抽屉监听========
    private void initListener() {
        L.d("initListener");
        mDrawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {}
            @Override
            public void onDrawerOpened(View drawerView) {
                isOpen = true;
            }
            @Override
            public void onDrawerClosed(View drawerView) {
                isOpen = false;
            }
            @Override
            public void onDrawerStateChanged(int newState) {}});
    }

    //==========问题列表============
    private void getQuestionData() {
        L.d("getQuestionData");
        if (TextUtils.isEmpty(mUid)) {return;}
        String tag_json_obj = "json_obj_home_question_req";
        showLoadDialog();
        JSONObject paramObj = new JSONObject();
        try {paramObj.put("uid", mUid);} catch (JSONException e) {e.printStackTrace();}
        PerfectJsonObjectRequest jsonObjReq = new PerfectJsonObjectRequest(mUid, Request.Method.POST,
                Urls.URL_HOME_QUESTION, paramObj, mQuestionCallBack, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                L.d( "error:" + error.getMessage());
            }
        });
        BaseApplication.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }
    private GsonCallbackListener<QuestionListBean> mQuestionCallBack = new GsonCallbackListener<QuestionListBean>() {
        @Override
        public void onResultSuccess(QuestionListBean questionListBean) {
            super.onResultSuccess(questionListBean);
            if (questionListBean != null) refreshQuestionUI(questionListBean);
        }

        @Override
        public void onFailed(String errorMsg) {
            super.onFailed(errorMsg);
        }
    };
    private void refreshQuestionUI(QuestionListBean questionBean) {
        QuestionListBean.ResultBean questionResult = questionBean.getResult();
        if (questionResult != null&&questionResult.getList() != null || !questionResult.getList().isEmpty())
            mQuestionAdapter.setData(questionResult.getList());
    }
    //==============推荐回答者列表==============
    private void getRecommondList() {
        L.d("getRecommondList");
        String tag_json_obj = "json_obj_home_recommond_req";
        JSONObject paramObj = new JSONObject();
        try {paramObj.put("limit", "4");paramObj.put("page","1");} catch (JSONException e) {e.printStackTrace();}
        L.i("推荐回答者列表 = "+mUid);
        PerfectJsonObjectRequest jsonObjReq = new PerfectJsonObjectRequest(mUid, Request.Method.POST,
                Urls.URL_TUTOR_RECOMMOND, paramObj, mRecommondCallBack, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                dismissLoadDialog();
                TipsUtils.showShort(getApplicationContext(), error.getMessage());
                L.d( "error:" + error.getMessage());
            }
        });
        BaseApplication.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }
    private GsonCallbackListener<RecommonBean> mRecommondCallBack = new GsonCallbackListener<RecommonBean>() {
        @Override
        public void onResultSuccess(RecommonBean recommonBean) {
            super.onResultSuccess(recommonBean);
            dismissLoadDialog();
            if (recommonBean != null)
                refreshCommondUI(recommonBean);
        }

        @Override
        public void onFailed(String errorMsg) {
            super.onFailed(errorMsg);
            dismissLoadDialog();
            TipsUtils.showShort(getApplicationContext(), errorMsg);
        }
    };
    private void refreshCommondUI(RecommonBean recommonBean) {
        L.d("");
        RecommonBean.ResultBean recommondResult = recommonBean.getResult();
        if (recommondResult != null&&recommondResult.getList() != null && !recommondResult.getList().isEmpty())
            mHeadAdapter.setListData(recommondResult.getList());
    }

    //===========删除提问记录===========
    private void deleteQuestion(int position) {
        L.d("deleteQuestion");

        if (mQuestionAdapter == null) {return;}
        final QuestionListBean.ResultBean.ListBean itemData = mQuestionAdapter.getItem(position);
        if (itemData == null) {return;}
        String qid = itemData.getQid();
        if (TextUtils.isEmpty(qid)) {return;}

        showLoadDialog();

        String tag_json_obj = "json_obj_delete_question_req";
        JSONObject paramObj = new JSONObject();
        try {paramObj.put("qid", qid);} catch (JSONException e) {e.printStackTrace();}

        PerfectJsonObjectRequest jsonObjReq = new PerfectJsonObjectRequest(mUid, Request.Method.POST,
                Urls.URL_HOME_QUESTION_DELETE, paramObj, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                L.d( "delet_quesiton res=" + response);
                dismissLoadDialog();
                boolean isRequestOk = GloabalRequestUtil.isRequestOk(response);
                if (isRequestOk) {
                    mQuestionAdapter.remove(itemData);
                    mQuestionAdapter.notifyDataSetChanged();
                } else {TipsUtils.showShort(getApplicationContext(), GloabalRequestUtil.getNetErrorMsg(response));}
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {dismissLoadDialog();}
        });
        BaseApplication.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);

    }
    //改重复提问 到 提新问题
    private void matchTutor(String qid,final String resultText) {

        String tag_json_obj = "json_obj_tutor_match_req";
        showLoadDialog();

        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("question", resultText);
            paramObj.put("qid", qid);
            L.i("呼叫:question = "+resultText);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        PerfectJsonObjectRequest jsonObjReq = new PerfectJsonObjectRequest(mUid, Request.Method.POST,
                Urls.URL_TUTOR_MATCH, paramObj, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                dismissLoadDialog();

                Log.e("lzm", "text="+response.toString());

                boolean isOk = GloabalRequestUtil.isRequestOk(response);
                if (isOk) {
                    String qid = GloabalRequestUtil.getQId(response);
                    String qdesc = GloabalRequestUtil.getQuestionDesc(response);
                    if(TextUtils.isEmpty(qid)) {
                        TipsUtils.showShort(getApplicationContext(), "没有返回qid");
                        return;
                    }

                    Intent intent = new Intent(NewMainActivity.this, VoiceVideoActivity.class);
                    intent.putExtra(VoiceVideoActivity.KEY_Q_ID, qid);
                    intent.putExtra(VoiceVideoActivity.KEY_Q_DESC,resultText);
                    startActivity(intent);
                } else {
                    TipsUtils.showShort(getApplicationContext(), GloabalRequestUtil.getNetErrorMsg(response));
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                TipsUtils.showShort(getApplicationContext(), error.getMessage());
                dismissLoadDialog();
            }
        });

        // Adding request to request queue
        BaseApplication.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }
    //============主线功能入口1:提问:送qid和qdesc到等待回答者页面============
//    private void matchTutor(String qid, final String questionDesc) {
//        L.d("matchTutor");
//
//        String tag_json_obj = "json_obj_tutor_match_req";
//        showLoadDialog();
//        JSONObject paramObj = new JSONObject();
//        try {paramObj.put("qid", qid);} catch (JSONException e) {e.printStackTrace();}
//        PerfectJsonObjectRequest jsonObjReq = new PerfectJsonObjectRequest(mUid, Request.Method.POST,
//                Urls.URL_TUTOR_MATCH, paramObj, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                dismissLoadDialog();
//                L.d( "match_id_text=" + response.toString());
//                boolean isOk = GloabalRequestUtil.isRequestOk(response);
//                if (isOk&&!TextUtils.isEmpty(GloabalRequestUtil.getQId(response))) {
//                    Intent intent = new Intent(NewMainActivity.this, VoiceVideoActivity.class);
//                    intent.putExtra(VoiceVideoActivity.KEY_Q_ID, GloabalRequestUtil.getQId(response));
//                    intent.putExtra(VoiceVideoActivity.KEY_Q_DESC,questionDesc);
//                    startActivity(intent);
//                }
//            }
//        }, new Response.ErrorListener() {
//
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                TipsUtils.showShort(getApplicationContext(), error.getMessage());
//                TipsUtils.showShort(BaseApplication.getInstance().getApplicationContext(),"网络不稳定");
//                L.d( "error:" + error.getMessage());
//                dismissLoadDialog();
//            }
//        });
//        BaseApplication.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
//    }

    @Override
    public void onClick(View v) {
        L.d("onClick");
        switch (v.getId()) {
            case R.id.iv_guanzhu:
                getFollowedList();
                break;
            case R.id.home_question_more:
                L.i("点击了问题-更多");
                processCleanIntent(MoreQuestionActivity.class);
                break;
//            case R.id.main_navigation_btn:
//                if (isOpen) {
//                    mDrawerLayout.closeDrawers();
//                } else {
//                    mDrawerLayout.openDrawer(Gravity.LEFT);
//                }
//                break;
            case R.id.home_heda_more://推荐回答者
//                TipsUtils.showShort(getApplicationContext(),"点击了更多按钮");
                Intent reCommondIntent = new Intent(NewMainActivity.this, MoreToturActivity.class);
                reCommondIntent.putExtra(MoreToturActivity.KEY_IS_FOLLOW, false);
                startActivity(reCommondIntent);
                break;
            case R.id.navigation_wallet_item://我的钱包
                mDrawerLayout.closeDrawers();
                processCleanIntent(WalletActivity.class);
                break;
            case R.id.navigation_history_item://历史记录
                mDrawerLayout.closeDrawers();
                processCleanIntent(HistoryActivity.class);
                break;
//            case R.id.navigation_recommon_item://推荐有奖
//                mDrawerLayout.closeDrawers();
//                processCleanIntent(RecommondActivity.class);
//                break;
            case R.id.navigation_fankui_item://反馈
                mDrawerLayout.closeDrawers();
                processCleanIntent(SuggestionBackActivity.class);
                break;
            case R.id.navigation_setting_item://设置
                mDrawerLayout.closeDrawers();
                processCleanIntent(SettingActivity.class);
                break;
            case R.id.navigation_head_rl:
                mDrawerLayout.closeDrawers();
                Intent tutorIntent = new Intent(this, TutorFileActivity.class);
                tutorIntent.putExtra(TutorFileActivity.KEY_USE_UID, "");
                tutorIntent.putExtra(TutorFileActivity.KEY_IS_SELF,true);
                if (isTutor) {
                    tutorIntent.putExtra(TutorFileActivity.KEY_ISNOT_TUTOR, false);//回答者档案
                } else {
                    tutorIntent.putExtra(TutorFileActivity.KEY_ISNOT_TUTOR, true);//我的档案
                }
                startActivity(tutorIntent);
                break;
            case R.id.navigation_change_app_tv://切换开关
                L.d( "change_isTutor=" + isTutor);
                switchTutorStatus(!isTutor);
                mDrawerLayout.closeDrawers();
                break;
            case R.id.home_voice_iv://语音提问按钮

                boolean isUnPay = mSettings.APP_IS_UN_PAY.getValue();
                if (isUnPay) {
                    TipsUtils.showShort(getApplicationContext(), "您有订单未支付,请支付完成再寻求帮助");
                    getHomeIndexData();
                    return;
                }else
                {
                    L.i("您没有未支付订单");
                }
                processCleanIntent(VoiceLisenActivity.class);
                break;
        }
    }

    //===========切换开关==========
    private void switchTutorStatus(final boolean isOpen) {
        L.d("switchTutorStatus");

        showLoadDialog();
        String tag_json_obj = "json_obj_tutor_switch_req";
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("switch", isOpen ? "1" : "0");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        PerfectJsonObjectRequest jsonObjReq = new PerfectJsonObjectRequest(mUid, Request.Method.POST,
                Urls.URL_TUTOR_SWITCH, paramObj, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                L.d( "switch___ =" + response);
                dismissLoadDialog();
                boolean isRequestOk = GloabalRequestUtil.isRequestOk(response);
                if (isRequestOk) {
                    if (isOpen) {
                        JSONObject resultObj = response.optJSONObject("result");
                        if (resultObj == null) {
                            TipsUtils.showShort(getApplicationContext(), "解析异常");
                            return;
                        }

                        //0:没有登记回答者信息；1:审核通过正常的回答者；2:禁用的回答者 3:待审核
                        int flag = resultObj.optInt("flag");
                        if (flag == 0) {
                            Intent skipIntent = new Intent(NewMainActivity.this, TutorInfoTypeInActivity.class);
                            startActivityForResult(skipIntent, REQUEST_TUTOR_BACK_CODE);
//                            mTutorSaleTv.setText(getString(R.string.tutor_sale_money_text, 0 + ""));
//                            mCallTimeTv.setText(getString(R.string.tutor_call_time_text, "0"));
//                            mTutorReceiverTv.setText(getString(R.string.tutor_receiver_count_text, 0.00 + ""));
                        } else if (flag == 1) {
                            changeToTutor();
                            int helpCount = resultObj.optInt("helpCount");
                            float amountTotal = (float) resultObj.optDouble("amountTotal");
                            int callTime = resultObj.optInt("talkTime");
                            String timeText = CommonUtils.getFormatTime(callTime);
                            Log.i("amountTotal",amountTotal+"");
                            mTutorSaleTv.setText(getString(R.string.tutor_sale_money_text, amountTotal + ""));
                            mCallTimeTv.setText(getString(R.string.tutor_call_time_text, timeText));
                            mTutorReceiverTv.setText(getString(R.string.tutor_receiver_count_text, helpCount + ""));
                        } else if (flag == 2) {
                            TipsUtils.showShort(getApplicationContext(), "您的回答者回答者资格已被系统禁用");
                        } else if (flag == 3) {//等待审核
                            TipsUtils.showShort(getApplicationContext(), "您的回答者信息待审核，请耐心等待");
                        }
//                        mTutorOverBtn.setVisibility(View.VISIBLE);
//                        mTutorReceiverBtn.setText("接单中");
//                        mTutorReceiverBtn.setEnabled(false);
                    } else {
                        mTestMatcheList.clear();
                        mMatchAdapter.notifyDataSetChanged();

                        clearQuestionDB();
                        changeToStudent();
//                        mTutorOverBtn.setVisibility(View.GONE);
//                        mTutorReceiverBtn.setText("接单");
//                        mTutorReceiverBtn.setEnabled(true);
                    }
                } else {
                    String netErrorText = GloabalRequestUtil.getNetErrorMsg(response);
                    TipsUtils.showShort(getApplicationContext(), netErrorText);
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                dismissLoadDialog();
                TipsUtils.showShort(getApplicationContext(), error.getMessage());
            }
        });

        BaseApplication.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    private String mRobQid;
    //===========回答者抢单============
    private void robItem(int positon) {
        L.d("robItem");
        if (mTestMatcheList.isEmpty()) {return;}
        PushQuestionBean itemData = mTestMatcheList.get(positon);
        if (itemData == null) {return;}

        this.mRobQid = itemData.getQid();

        showLoadDialog();

        String tag_json_obj = "json_obj_tutor_rob_req";
        JSONObject paramObj = new JSONObject();
        try {
//            paramObj.put("source", itemData.getSource());
            paramObj.put("qid", itemData.getQid());
        } catch (JSONException e) {e.printStackTrace();}

        PerfectJsonObjectRequest jsonObjReq = new PerfectJsonObjectRequest(mUid, Request.Method.POST,
                Urls.URL_TUTOR_ROB, paramObj, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                L.d( "rob res=" + response);
                dismissLoadDialog();
                boolean isRequestOk = GloabalRequestUtil.isRequestOk(response);
                if (isRequestOk) {
                    //获取是否已过期问题
                    boolean isPastDate = GloabalRequestUtil.isPastDate(response);
                    L.d( "isPastDate=" + isPastDate);
                    if(isPastDate) {
                        deletQDBWithQid(mRobQid);
                    } else {
                        try {
                            JSONObject result = response.getJSONObject("result");
                            mTimerProgressLayout.setVisibility(View.VISIBLE);
                            int count_down_time = result.getInt("count_down_time");
                            L.d("count_down_time = "+count_down_time);
                            startRobTimerDown(count_down_time,mUid);
                            for (PushQuestionBean testItem : mTestMatcheList) {
                                testItem.isEnable = false;
                            }
                        } catch (JSONException e) {e.printStackTrace();}
                        mMatchAdapter.notifyDataSetChanged();
                    }
                } else {
                    TipsUtils.showShort(getApplicationContext(), GloabalRequestUtil.getNetErrorMsg(response));
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                dismissLoadDialog();
                TipsUtils.showShort(getApplicationContext(), error.getMessage());
            }
        });
        BaseApplication.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }
    //===========抢单中的倒计时==========
    private void startRobTimerDown(int leftLiftTime, final String qid) {
        L.d("startRobTimerDown");
        if (mProgressTimer == null) {
            mProgressTimer = new CountDownTimer(leftLiftTime * 1000, 1000) {
                @Override//更新秒数
                public void onTick(long l) {
//                    L.d("l = "+l);
                    mTimerTv.setText(getResources().getString(R.string.app_senconds_text, (l / 1000) + ""));
                }
                @Override
                public void onFinish() {
                    robItemFailed();}
            };

        }
        mProgressTimer.start();
    }
    //===========抢单失败==========
    private void robItemFailed() {
        L.d("robItemFailed");
        mTimerTv.setText(getResources().getString(R.string.app_senconds_text, "0"));
        mTimerProgressLayout.setVisibility(View.GONE);
        for (PushQuestionBean testItem : mTestMatcheList) {
            testItem.isEnable = true;
        }
        mMatchAdapter.notifyDataSetChanged();
        TipsUtils.showShort(BaseApplication.getInstance().getBaseContext(),"抢单失败");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        L.d("onNewIntent");
        super.onNewIntent(intent);
        setIntent(intent);
    }

    //===========关注=========
    private void goGuanzhuActivity()
    {
        Intent intent = new Intent(this,GuangZhuActivity.class);
        intent.putParcelableArrayListExtra(GuangZhuActivity.FOLLOW_LIST_KEY,itemList);
        startActivity(intent);
    }
    /**
     * 获取关注列表
     */
    private void getFollowedList() {
//        TipsUtils.showShort(getApplicationContext(),"进入页面getFollowedList方法");
        String tag_json_obj = "json_obj_followed_list_req";
        showLoadDialog();

        PerfectJsonObjectRequest jsonObjReq = new PerfectJsonObjectRequest(mUid, Request.Method.POST,
                Urls.URL_FOLLOWED_TUTOR_LIST, null, mFollowedListCallback, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                TipsUtils.showShort(getApplicationContext(), error.getMessage());
                dismissLoadDialog();
                goGuanzhuActivity();

            }
        });

        // Adding request to request queue
        BaseApplication.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    ArrayList<CommonTutorItemData> itemList;
    private GsonCallbackListener<QuestionFocusTutorsBean> mFollowedListCallback = new GsonCallbackListener<QuestionFocusTutorsBean>() {
        @Override
        public void onResultSuccess(QuestionFocusTutorsBean questionFocusTutorsBean) {
            super.onResultSuccess(questionFocusTutorsBean);
            L.e("导师关注列表 = "+questionFocusTutorsBean);
            dismissLoadDialog();
            goGuanzhuActivity();
            if (questionFocusTutorsBean == null) {
                return;
            }

            itemList = (ArrayList<CommonTutorItemData>)questionFocusTutorsBean.getResult();

//            if (itemList == null || itemList.isEmpty()) {
//                return;
//            }

//            mAdapter.setListData(itemList);
        }

        @Override
        public void onFailed(String errorMsg) {
            super.onFailed(errorMsg);
            TipsUtils.showShort(getApplicationContext(), errorMsg);
            dismissLoadDialog();
            goGuanzhuActivity();
        }
    };
    @Override
    protected void onResume() {
        L.i("XXXYYY is_un_pay = "+mSettings.APP_IS_UN_PAY.getValue());
        super.onResume();

        TranslateAnimation mVisibleTraslateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF,0.0f,Animation.RELATIVE_TO_SELF,0.0f,Animation.RELATIVE_TO_SELF,-1.0f,Animation.RELATIVE_TO_SELF,0.0f);
        mVisibleTraslateAnimation.setDuration(500);

        AlphaAnimation mVisibleAlphaAnimation = new AlphaAnimation(0.0f,1.0f);

        AnimationSet set=new AnimationSet(true);    //创建动画集对象
        set.addAnimation(mVisibleTraslateAnimation);
        set.addAnimation(mVisibleAlphaAnimation);
        voiceIv.setAnimation(set);                    //设置动画
        set.startNow();

        voiceIv.setVisibility(View.VISIBLE);
        getHomeIndexData();
    }

    //=========获取首页数据===========
    private void getHomeIndexData() {
        L.i("XXXYYY获取主页详情使用的参数: uid = "+mUid);
        String tag_json_obj = "json_obj_home_index_req";
        L.i("未支付 使用的uid = "+mUid);
        PerfectJsonObjectRequest jsonObjReq = new PerfectJsonObjectRequest(mUid, Request.Method.POST,
                Urls.URL_HOME_INDEX, null, mIndexCallbak, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                TipsUtils.showShort(getApplicationContext(), error.getMessage());
            }
        });
        BaseApplication.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }
    private GsonCallbackListener<HomeIndexBean> mIndexCallbak = new GsonCallbackListener<HomeIndexBean>() {
        @Override
        public void onResultSuccess(HomeIndexBean homeIndexBean) {
            super.onResultSuccess(homeIndexBean);
            L.i("XXXYYY获取主页详情得到的结果: homeIndexBean = "+homeIndexBean);
            if (homeIndexBean != null&&homeIndexBean.getResult()!=null)
            {
                L.d("主页信息:"+homeIndexBean);
                refreshUserUI(homeIndexBean.getResult());
            }
        }
        @Override
        public void onFailed(String errorMsg) {
            super.onFailed(errorMsg);
            L.d( "首页信息获取失败");
        }
    };

    private void refreshUserUI(HomeIndexBean.ResultBean resultBean) {
        L.d("refreshUserUI");
        String avarUrl = resultBean.getHeadImgUrl();
        if (!TextUtils.isEmpty(avarUrl)) {
            BaseApplication.mImageLoader.displayImage(avarUrl, mUserHeadIv, BaseApplication.headOptions);
        }

        String nickText = resultBean.getNickName();
        if (TextUtils.isEmpty(nickText)) {
            nickText = mSettings.USER_NICK.getValue();
        }

        if (!TextUtils.isEmpty(nickText)) {
            mNickTv.setText(nickText);
        }

        String timeText = CommonUtils.getFormatTime(resultBean.getTalkTime());
        Log.i("amountTotal2",resultBean.getAmountTotal()+"");
        mTutorSaleTv.setText(getString(R.string.tutor_sale_money_text, resultBean.getAmountTotal() + ""));
        mCallTimeTv.setText(getString(R.string.tutor_call_time_text, timeText));
        mTutorReceiverTv.setText(getString(R.string.tutor_receiver_count_text, resultBean.getHelpCount() + ""));

        int flag = resultBean.getFlag();
        if(isCreate) { // 只有走oncreate进来才会需要判断是否自动切换到回答者端
            /**
             * flag=1说明是有回答者,产品需求默认打开回答者界面，调用switch打开回答者开关
             * flag不等于1，说明非正常回答者注册，直接请求问题页面，显示求问模式UI
             */
            if (flag == 1) {
                switchTutorStatus(true);
            }
        }

        if(flag != 1) {
            changeToStudent();
        }
        isCreate = false;
        /**
         * 如果有未支付订单，就跳转支付界面
         */
        List<String> unPayOrderList = resultBean.getOrderList();

        if (unPayOrderList != null && !unPayOrderList.isEmpty()) {
            L.i("未支付 获取到的订单列表长度 :"+unPayOrderList.size());
            L.i("XXXYYY 未支付订单列表 = "+resultBean);
            String unPayOrder = unPayOrderList.get(0);
            if (!TextUtils.isEmpty(unPayOrder)) {
                mSettings.APP_IS_UN_PAY.setValue(true);
                CommonUtils.getOrderDetails(NewMainActivity.this, mUid, unPayOrder, "1", mOrderDetailCallback);
            } else {
                mSettings.APP_IS_UN_PAY.setValue(false);
            }
        } else {
            L.i("未支付 订单列表为空");
            mSettings.APP_IS_UN_PAY.setValue(false);
        }
    }

    private GsonCallbackListener<OrderDetailsBean> mOrderDetailCallback = new GsonCallbackListener<OrderDetailsBean>() {

        @Override
        public void onResultSuccess(OrderDetailsBean orderDetailsBean) {
            super.onResultSuccess(orderDetailsBean);
            dismissLoadDialog();
            if (orderDetailsBean != null&&orderDetailsBean.getResult()!=null) {
                L.i("XXXYYY获取订单详情返回:"+orderDetailsBean);
                L.i("XXXYYY,PayOrderConfirmAcitivity 订单页面暂时屏蔽 订单号为:"+orderDetailsBean.getResult().getOrderId());
                Intent payIntent = new Intent(NewMainActivity.this, PayOrderConfirmAcitivity.class);
                payIntent.putExtra(PayOrderConfirmAcitivity.KEY_DATA_ORDER, orderDetailsBean.getResult());
                L.i("弹出订单页面");
                startActivity(payIntent);
            }
        }
        @Override
        public void onFailed(String errorMsg) {
            super.onFailed(errorMsg);
            dismissLoadDialog();
            TipsUtils.showShort(getApplicationContext(), errorMsg);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        L.d("onActivityResult");
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case REQUEST_TUTOR_BACK_CODE:
                TipsUtils.showShort(getApplicationContext(), "提交成功，待审核");
                break;
        }
    }

    //=========切换至回答者端========
    private void changeToTutor() {
        L.d("changeToTutor");
//        mFollowLayout.setVisibility(View.GONE);

        isTutor = true;
        mSettings.APP_IS_TUTOR_TYPE.setValue(isTutor);

        mChangeAppTv.setText("切换求问者模式");
        mStudentRootLayout.setVisibility(View.GONE);
        mTutorRootLayout.setVisibility(View.VISIBLE);

        updateMatchUI(null, false);
    }

    private void changeToStudent() {
        L.d("changeToStudent");
//        mFollowLayout.setVisibility(View.VISIBLE);

        isTutor = false;
        mSettings.APP_IS_TUTOR_TYPE.setValue(isTutor);

        mChangeAppTv.setText("切换回答者模式");
        mTutorRootLayout.setVisibility(View.GONE);
        mStudentRootLayout.setVisibility(View.VISIBLE);
        getQuestionData();
        getRecommondList();
    }

    @Override
    protected void onStop() {
        L.d("onStop");
        super.onStop();
    }

    @Subscribe
    public void onEventLogout(LogOutEvent event) {
        L.d("onEventLogout");
        finish();
    }

    @Subscribe
    public void onCallConnectEvent(CallConnectEvent event) {
        L.d("NewMainActivity-onCallConnectEvent");
        //取消问题
        Intent serviceIntent = new Intent(this, QuestionService.class);
        serviceIntent.putExtra(QuestionService.KEY_ACTION, QuestionService.ACTIOIN_DELETE);
        PushQuestionBean pushQuestionBean = new PushQuestionBean();
        pushQuestionBean.setQid(event.getQid());
        serviceIntent.putExtra(QuestionService.KEY_DATA, pushQuestionBean);
        startService(serviceIntent);
    }

    @Subscribe//重新提问
    public void onEventReLoadQuestion(ReLoadQuestionEvent event) {
        L.d("onEventReLoadQuestion");
        matchTutor(event.getmQid(),event.getmQDesc());
    }

    private MatchQuestionProviderService mQuestionService;

    @Subscribe//更新推送过来的问题
    public void onEventUpdateMatchQuestion(UpdateQuestionEvent event) {
        L.d("onEventUpdateMatchQuestion");
        JPushInterface.clearAllNotifications(getApplicationContext());
        L.d( "service__通知..." + event.isDelete + "--qid=" + event.qid);
        updateMatchUI(event.qid, event.isDelete);
    }

    @Subscribe
    public void onDeleteQuestionEvent(DeleteQuestionEvent event) {
        L.d("onDeleteQuestionEvent");
    }

    @Subscribe
    public void onPushTutorInfoMsgEvent(PushTutorInfoMsgEvent event) {
        L.d("onPushTutorInfoMsgEvent = "+event.pushTutorInfoMsgObj);
        String action = "";
        try {
            action = event.pushTutorInfoMsgObj.getString("action");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //准备接听电话
        if("notice-call".equals(action))
            goCallingActivity(event);
    }

    //==========获取匹配问题数据库操作对象=========
    private MatchQuestionProviderService getQuestionDBService() {
        L.d("MatchQuestionProviderService");
        if (mQuestionService == null) {
            mQuestionService = new MatchQuestionProviderService(getApplicationContext());
        }
        return mQuestionService;
    }


    //=========更新推送的问题数据========
    private void updateMatchUI(String qid, boolean isDelete) {
        L.d("updateMatchUI");
        mTestMatcheList.clear();
        mMatchAdapter.notifyDataSetChanged();
        //获取当前问题集合
        getQlistByDB();
        /**
         * 抢单中,删除事件调整状态
         */
        if (isDelete && !TextUtils.isEmpty(mRobQid) && mRobQid.equals(qid)) {
            if (mProgressTimer != null) {
                mProgressTimer.cancel();
            }
            mTimerProgressLayout.setVisibility(View.GONE);
        }
    }

    //=============主要删除通话结束的问题列表项==========
    private BroadcastReceiver mCallReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            L.d("");
            String action = intent.getAction();
            boolean isTutorStatus = mSettings.APP_IS_TUTOR_TYPE.getValue();
            L.d( "main_action=" + action + "_isTutor=" + isTutorStatus);
            if (!isTutorStatus) {
                return;
            }

//            if (MtcCallConstants.MtcCallTalkingNotification.equals(action)) { //通话接通
//                Log.i("lzm-zf","通话回调");
//                mProgressTimerDismiss();
//            } else if (MtcCallConstants.MtcCallDidTermNotification.equals(action)) {
//                mProgressTimerDismiss();
//                //通话结束事件，主动挂断
//                delTutorQuestionList();
//            } else if (MtcCallConstants.MtcCallTermedNotification.equals(action)) {
//                mProgressTimerDismiss();
//                // 通话结束事件，被动挂断
//
//            }
        }
    };

    //==========删除回答者推送问题列表===========
    private void delTutorQuestionList() {
        L.d("delTutorQuestionList");
        if (TextUtils.isEmpty(mRobQid)) {
            return;
        }

        if (mTestMatcheList.isEmpty()) {
            return;
        }

        Iterator<PushQuestionBean> iterator = mTestMatcheList.iterator();
        while (iterator.hasNext()) {
            PushQuestionBean itemData = iterator.next();
            itemData.isEnable = true;
            if (mRobQid.equals(itemData.getQid())) {
                iterator.remove();
                deletQDBWithQid(mRobQid);
            }
        }

        mMatchAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        L.d("onDestroy");
        super.onDestroy();
        isTutor = false;
        isCreate = true;
        if(mSingleThreadExecutor != null) {
            mSingleThreadExecutor.shutdown();
            mSingleThreadExecutor = null;
        }

        if (mProgressTimer != null) {
            mProgressTimer.cancel();
        }
        HeartbeatService.stopLoop();
        stopService(mHeartServiceIntent);
        BusProvider.getInstance().unregister(this);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mCallReceiver);
    }

//    @Override
//    public void mtcLoginOk() {
//        L.d("");
//        mSettings.JUSTALK_IS_LOGINED.setValue(true);
//        L.d( "main_登录成功...uid=" + mUid);
//    }

//    @Override
//    public void mtcLoginDidFail() {}
//
//    //登出成功
//    @Override
//    public void mtcLogoutOk() {
//        mSettings.JUSTALK_IS_LOGINED.setValue(false);
//    }
//
//    //被登出
//    @Override
//    public void mtcLogouted() {
//        mSettings.JUSTALK_IS_LOGINED.setValue(false);
////        LoginDelegate.login(mUid, mUid, "sudp:ae.justalkcloud.com:9851");
////        TipsUtils.showShort(getApplicationContext(), "justalk账户被登出...重新登录");
////        mSettings.loginoutUser();
////        MiPush.stop(getApplicationContext());
////        Intent i = new Intent(this, LoginActivity.class);
////        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
////        startActivity(i);
////        finish();
//    }
//
//    @Override
//    public void mtcAuthRequire(String s, String s1) {
//
//    }

    @Override
    public void clickAction(int whatAction, int positon) {
        L.d("clickAction");
        L.d( "ation=" + whatAction + "__position=" + positon);
        switch (whatAction) {
            case MidTutorMatchAdapter.ITutorMatchItemClickListener.WAHT_ACTION_ROB:
                robItem(positon);
                break;
            case MidTutorMatchAdapter.ITutorMatchItemClickListener.WAHT_ACTION_FOLLOW:
                followQuestion(positon);
                break;
            case MidTutorMatchAdapter.ITutorMatchItemClickListener.WAHT_ACTION_ITEM:

                break;
            default:
                break;
        }
    }

    //============回答者关注问题==========
    private void followQuestion(int positon) {
        L.d("followQuestion");
        if (mTestMatcheList.isEmpty()) {
            TipsUtils.showShort(getApplicationContext(), "没有问题数据");
            return;
        }

        PushQuestionBean itemData = mTestMatcheList.get(positon);
        if (itemData == null) {
            TipsUtils.showShort(getApplicationContext(), "没有问题数据");
            return;
        }

        final String tempQId = itemData.getQid();
        L.d( "tempQid =" + tempQId);
        showLoadDialog();

        String tag_json_obj = "json_obj_tutor_follow_req";
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("qid", tempQId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        PerfectJsonObjectRequest jsonObjReq = new PerfectJsonObjectRequest(mUid, Request.Method.POST,
                Urls.URL_TUTOR_FOLLOW_QUESTION, paramObj, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                L.d( "follow res=" + response);
                dismissLoadDialog();
                boolean isRequestOk = GloabalRequestUtil.isRequestOk(response);
                if (isRequestOk) {
                    for (PushQuestionBean testItem : mTestMatcheList) {
                        if (testItem.getQid().equals(tempQId)) {
                            testItem.isFollowed = true;
                        }
                    }
                    mMatchAdapter.notifyDataSetChanged();
                } else {
                    String netErrorText = GloabalRequestUtil.getNetErrorMsg(response);
                    TipsUtils.showShort(getApplicationContext(), netErrorText);
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                dismissLoadDialog();
                TipsUtils.showShort(getApplicationContext(), error.getMessage());
            }
        });

        // Adding request to request queue
        BaseApplication.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    //==========清除问题数据库=======
    private void clearQuestionDB() {
        L.d("clearQuestionDB");
        mSingleThreadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                getQuestionDBService().clearHistorys();
            }
        });
    }
    //==========删除某个问题=========
    private void deletQDBWithQid(final String qid) {
        L.d("deletQDBWithQid");
        mSingleThreadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                getQuestionDBService().deleteHistorysWithQid(qid);
            }
        });
    }
    //==========获取问题列表==========
    private void getQlistByDB() {
        L.d("getQlistByDB");
        mSingleThreadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                List<PushQuestionBean> dBQuestionListData = getQuestionDBService().getAllDate();
                L.d( "dBQuestionListData=" + dBQuestionListData);
                if(dBQuestionListData!= null && !dBQuestionListData.isEmpty()) {
                    L.d( "db_q_size=" + dBQuestionListData.size());

                    List<PushQuestionBean> tempList = new ArrayList<>();

                    long endTime = System.currentTimeMillis();

                    for (PushQuestionBean testItem : dBQuestionListData) {
                        testItem.isEnable = true;
                        long qTime = testItem.getTs();

                        long diff = endTime - qTime;
                        L.d( "db_ts="+qTime+"_sysTIME="+endTime+"_diff="+diff);
                        if(diff < (60*1000)) { //清除60秒前的数据(可能离线后,提问端取消问题)
                            tempList.add(testItem);
                        } else {
                            deletQDBWithQid(testItem.getQid());
                        }
                    }
                    L.d( "cu_lihoud list.size=" + tempList.size());
                    L.d( "send....");
                    Message msg = mHandler.obtainMessage();
                    msg.obj = tempList;
                    msg.what = 0;
                    mHandler.sendMessage(msg);
                }

            }
        });
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(mHandler.hasMessages(0)) {return;}
            L.d( "hanld=" + msg.what);
            switch (msg.what) {
                case 0:
                    List<PushQuestionBean> msgList = (List<PushQuestionBean>) msg.obj;
                    mTestMatcheList.addAll(msgList);
                    mMatchAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    //========版本更新 点击按钮用的==============
    public void autoUpdateApp() {
        //初始化当前apk版本号
        updateManager = new UpdateAppManager(NewMainActivity.this);
        versionCode = updateManager.getVersionCode();

        if(versionCode==-1)return;

        //访问服务器询问是否有新版本:request
        String tag_json_obj = "apk_update_request";

        PerfectJsonObjectRequest jsonObjReq = new PerfectJsonObjectRequest(mUid, Request.Method.POST,
                Urls.URL_UPDATE, null, mVersionUpdateCallBack, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });

        BaseApplication.getInstance().addToRequestQueue(jsonObjReq, "tag_json_obj");
    }

    //===========版本更新onCreate中调用的=========
    private void checkAndUpdate() {
        //自动更新
        isAutoUpdate = true;
        autoUpdateApp();
    }

    //版本更新:callback
    private GsonCallbackListener<ApkUpdateBean> mVersionUpdateCallBack = new GsonCallbackListener<ApkUpdateBean>() {
        @Override
        public void onResultSuccess(ApkUpdateBean updateBean) {
            super.onResultSuccess(updateBean);

            ApkUpdateBean.ResultBean result = updateBean.getResult();

            if(updateBean==null||result==null)//服务器返回空数据
                return;
            else if(Integer.parseInt(result.getVer())>versionCode)//存在新版本
                updateManager.checkUpdateInfo(result.getUrl(),result.getTitle(),result.getDesc());
            else if(!isAutoUpdate)//没有新版本
                TipsUtils.showShort(NewMainActivity.this,"当前已经是最新版本");
        }

        @Override
        public void onFailed(String errorMsg) {
            super.onFailed(errorMsg);
            L.i("版本更新回调error");
        }
    };

    //==========点击返回键不退出程序,只是最小化程序==========
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {

        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            L.d("点击了MainActvity页面的返回键");
            Intent intent = new Intent();
            intent.setAction("android.intent.action.MAIN");
            intent.addCategory("android.intent.category.HOME");
            startActivity(intent);
        }
        return false;
    }
    //===========跳转到拨打视频界面
    private void goCallingActivity(PushTutorInfoMsgEvent event) {
        L.d("goCallingActivity");
        JSONObject pushTutorInfoMsgObj = event.pushTutorInfoMsgObj;
        try
        {
//            String qid = pushTutorInfoMsgObj.getString("qid");
            String action = pushTutorInfoMsgObj.getString("action");
            String headImgUrl = pushTutorInfoMsgObj.getString("headImgUrl");
            String dynamicKey = pushTutorInfoMsgObj.getString("dynamicKey");
            String channelName = pushTutorInfoMsgObj.getString("channelName");
            String question = pushTutorInfoMsgObj.getString("question");
            String type = pushTutorInfoMsgObj.getString("type");
            String realName = pushTutorInfoMsgObj.getString("realName");
            String targetId = pushTutorInfoMsgObj.getString("targetId");

            Intent intent = new Intent(NewMainActivity.this,CallingActivity.class);

//            intent.putExtra(VoiceVideoActivity.KEY_Q_ID,qid);
            intent.putExtra(VoiceVideoActivity.KEY_ACTION,action);
            intent.putExtra(VoiceVideoActivity.KEY_REALNAME,realName);
            intent.putExtra(VoiceVideoActivity.KEY_QUESTION,question);
            intent.putExtra(VoiceVideoActivity.KEY_TYPE,type);
            intent.putExtra(VoiceVideoActivity.KEY_CHANNEL_NAME,channelName);
            intent.putExtra(VoiceVideoActivity.KEY_DYNAMIC_KEY,dynamicKey);
            intent.putExtra(VoiceVideoActivity.KEY_HEADPATH_URL,headImgUrl);
            intent.putExtra(VoiceVideoActivity.KEY_TARGETID,targetId);
            startActivity(intent);
            mProgressTimerDismiss();
        } catch (JSONException e){e.printStackTrace();}
    }

    //===========循环启动心跳服务。=========

    private void startHeartLoop(int delayTime) {
        // 循环启动心跳服务。
        uiHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                boolean isStart = HeartbeatService.startLoop();
                if (!isStart) {
                    startHeartLoop(1000);
                }
            }
        }, delayTime);
    }


    //=======关闭倒计时对话框======
    private void mProgressTimerDismiss() {
        L.d("mProgressTimerDismiss");
        if (mProgressTimer != null) {
            mProgressTimer.cancel();
        }
        mTimerProgressLayout.setVisibility(View.GONE);
    }

    @Override
    protected void onPause() {
        L.i("XXXYYY is_un_pay = "+mSettings.APP_IS_UN_PAY.getValue());
        TranslateAnimation mGoneTranslateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF,0.0f,Animation.RELATIVE_TO_SELF,0.0f,Animation.RELATIVE_TO_SELF,0.0f,Animation.RELATIVE_TO_SELF,-1.0f);
        mGoneTranslateAnimation.setDuration(500);
        AlphaAnimation mGoneAlphaAnimation = new AlphaAnimation(1.0f,0.0f);

        AnimationSet set=new AnimationSet(true);    //创建动画集对象
        set.addAnimation(mGoneTranslateAnimation);
        set.addAnimation(mGoneAlphaAnimation);
        voiceIv.setAnimation(set);                    //设置动画
        set.startNow();
        voiceIv.setVisibility(View.GONE);
        super.onPause();
    }
}
