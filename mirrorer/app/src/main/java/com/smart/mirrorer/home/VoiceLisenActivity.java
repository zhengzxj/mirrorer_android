package com.smart.mirrorer.home;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.smart.mirrorer.R;
import com.smart.mirrorer.base.BaseActivity;
import com.smart.mirrorer.base.BaseApplication;
import com.smart.mirrorer.net.PerfectJsonObjectRequest;
import com.smart.mirrorer.util.GloabalRequestUtil;
import com.smart.mirrorer.util.KeyboardService;
import com.smart.mirrorer.util.MirrorSettings;
import com.smart.mirrorer.util.TipsUtils;
import com.smart.mirrorer.util.Urls;
import com.smart.mirrorer.util.VoiceJsonParser;
import com.smart.mirrorer.util.mUtil.L;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lzm on 16/3/29.
 */
public class VoiceLisenActivity extends BaseActivity implements View.OnClickListener {

    private TextView mResultTv;
    private EditText mResultEdit;
//    private TextView mEidtBtn;
    private boolean isEditType;

//    private ProgressBar mProgressBar;
//    private CountDownTimer mProgressTimer; //倒计时任务对象

    private static final int ANIMATIONEACHOFFSET = 500; // 每个动画的播放时间间隔
    private AnimationSet aniSet, aniSet2, aniSet3;
    private ImageView voice_layout_iv, wave1, wave2, wave3;

    //1.创建SpeechRecognizer对象,第二个参数:本地听写时传InitListener
    private SpeechRecognizer mIat;
    // 引擎类型
    private String mEngineType = SpeechConstant.TYPE_CLOUD;
    // 用HashMap存储听写结果  这样存，只能分次存，不能多次
//    private HashMap<String, String> mVoiceResults = new LinkedHashMap<String, String>();
    private StringBuffer mVoiceResultsSB = new StringBuffer();

    private boolean isTimeOver;

    private TextView btn_fasong;
    private ImageView voice_finish_iv;
    boolean isFirstInput = true;
    boolean isFirstClickDuiHao = true;

    private KeyboardService mKeyService;

    private int zishu;
    private TextView tv_zishu;
    private String mUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.alpha = 0.9f;
        window.setAttributes(wl);
        setContentView(R.layout.activity_voice_layout);

        initView();

        MirrorSettings settings = new MirrorSettings(this);
        mUid = settings.APP_UID.getValue();

        mKeyService = new KeyboardService(this);

//        initProgressTimer();
        startVoiceAnimation();
        startVoiceListsen();
    }

    private void initView() {
        tv_zishu = (TextView)findViewById(R.id.tv_zishu);
        mResultTv = (TextView) findViewById(R.id.voice_result_tv);
        mResultEdit = (EditText) findViewById(R.id.voice_result_edit);
        mResultEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String dest = mResultEdit.getText().toString();
                if(dest.length()>100)
                {
                    mResultEdit.setText(dest.substring(0,100));
                    TipsUtils.showShort(getApplicationContext(),"字数不能超过100");
                    mResultEdit.setSelection(mResultEdit.getText().toString().length());
                }
                tv_zishu.setText(mResultEdit.getText().toString().length()+"/100");
            }
        });
//        mEidtBtn = (TextView) findViewById(R.id.voice_edit_tv);
//        mEidtBtn.setOnClickListener(this);

//        mProgressBar = (ProgressBar) findViewById(R.id.voice_progress);
//        mProgressBar.setProgressDrawable(getResources().getDrawable(
//                R.drawable.voice_progress_color));
//        mProgressBar.setMax(60 * 1000);

        voice_layout_iv = (ImageView) findViewById(R.id.voice_layout_iv);
        wave1 = (ImageView) findViewById(R.id.voice_wave_one);
        wave2 = (ImageView) findViewById(R.id.voice_wave_two);
        wave3 = (ImageView) findViewById(R.id.voice_wave_three);

        ImageView cancelIv = (ImageView) findViewById(R.id.voice_cancle_iv);
        cancelIv.setOnClickListener(this);
        voice_finish_iv = (ImageView) findViewById(R.id.voice_finish_iv);
        voice_finish_iv.setOnClickListener(this);

        btn_fasong = (TextView) findViewById(R.id.voice_pub_iv);
        btn_fasong.setOnClickListener(this);

    }

    private void startVoiceListsen() {
        mIat = SpeechRecognizer.createRecognizer(this, mInitListener);
        setParam();
        int retCode = mIat.startListening(mRecoListener);
        if (retCode != ErrorCode.SUCCESS) {
            TipsUtils.showShort(getApplicationContext(), "听写失败,错误码：" + retCode);
        } else {
//            TipsUtils.showShort(getApplicationContext(), "请开始说话...");
        }
    }

    private InitListener mInitListener = new InitListener() {
        @Override
        public void onInit(int code) {
            if (code != ErrorCode.SUCCESS) {
                TipsUtils.showShort(VoiceLisenActivity.this, "初始化失败，错误码：" + code);
            }
        }
    };

    public void setParam() {
        // 清空参数
        mIat.setParameter(SpeechConstant.PARAMS, null);

        // 设置听写引擎
        mIat.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
        // 设置返回结果格式
        mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");
        mIat.setParameter(SpeechConstant.DOMAIN, "iat");
        // 设置语言
        mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        mIat.setParameter(SpeechConstant.ACCENT, "mandarin ");

        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        mIat.setParameter(SpeechConstant.VAD_BOS, "5000");

        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        mIat.setParameter(SpeechConstant.VAD_EOS, "5000");

        // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        mIat.setParameter(SpeechConstant.ASR_PTT, "0");
    }

    private boolean isNoresult = true;
    //听写监听器
    private RecognizerListener mRecoListener = new RecognizerListener() {
        //听写结果回调接口(返回Json格式结果,用户可参见附录12.1);
        // 一般情况下会通过onResults接口多次返回结果,完整的识别内容是多次结果的累加;
        // 关于解析Json的代码可参见MscDemo中JsonParser类;
        //isLast等于true时会话结束。
        public void onResult(RecognizerResult results, boolean isLast) {
            if(isFirstInput)
            {
                voice_finish_iv.setVisibility(View.VISIBLE);
                VoiceLisenActivity.this.findViewById(R.id.tv_lingting).setVisibility(View.GONE);
                isFirstInput = false;
            }
            Log.e("lzm", results.getResultString() + "__isLast=" + isLast);
            printResult(results);
            if (isLast) {
                if (!isTimeOver) {
                    setParam();
                    int retCode = mIat.startListening(mRecoListener);
                    if (retCode != ErrorCode.SUCCESS) {
                        Log.e("lzm", "再次————听写失败,错误码：" + retCode);
                    } else {
                        Log.e("lzm", "再次————请开始说话...");
                    }
                }
            }
        }

        //会话发生错误回调接口
        public void onError(SpeechError error) {
            error.getPlainDescription(true);
            Log.e("lzm", "error=" + error.getMessage() + "++code=" + error.getErrorCode());

            setParam();
            int retCode = mIat.startListening(mRecoListener);
            if (retCode != ErrorCode.SUCCESS) {
                Log.e("lzm", "onerror————听写失败,错误码：" + retCode);
            } else {
                Log.e("lzm", "onErorro————请开始说话...");
            }
            //获取错误码描述
        }

        //音量值0~30
        @Override
        public void onVolumeChanged(int i, byte[] bytes) {
//            Log.e("lzm", "onVolumeChanged...i=" + i);
        }

        // 开始录音
        public void onBeginOfSpeech() {
            Log.e("lzm", "begin__开始了");
        }

        //结束录音
        public void onEndOfSpeech() {
            Log.e("lzm", "end----jieshu");
        }

        //扩展用接口
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
        }
    };

    /**
     * 处理听写结果
     *
     * @param results
     */
    private void printResult(RecognizerResult results) {
        String text = VoiceJsonParser.parseIatResult(results.getResultString());

        if (!TextUtils.isEmpty(text)) {
            isNoresult = false;
            voice_finish_iv.setVisibility(View.VISIBLE);
        }

        String sn = null;
        // 读取json结果中的sn字段
        try {
            JSONObject resultJson = new JSONObject(results.getResultString());
            sn = resultJson.optString("sn");
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        mVoiceResults.put(sn, text);
//        StringBuffer resultBuffer = new StringBuffer();
//        for (String key : mVoiceResults.keySet()) {
//            resultBuffer.append(mVoiceResults.get(key));
//        }

        mVoiceResultsSB.append(text);
        zishu = mVoiceResultsSB.toString().length();
        if(zishu>100)
        {
            zishu = 100;
            mVoiceResultsSB.delete(100,mVoiceResultsSB.toString().length());
            TipsUtils.showShort(getApplicationContext(),"字数不能超过100");
        }
        tv_zishu.setText(zishu+"/100");
        mResultTv.setText(mVoiceResultsSB.toString());
        mResultEdit.setText(mVoiceResultsSB.toString());
    }

    private void startVoiceAnimation() {
        aniSet = getNewAnimationSet();
        aniSet2 = getNewAnimationSet();
        aniSet3 = getNewAnimationSet();
        showWaveAnimation();
    }

    /**
     * 初始化时间r任务
     */
//    private void initProgressTimer() {
//        mProgressTimer = new CountDownTimer(60 * 1000, 200) {
//            @Override
//            public void onTick(long l) {
//                int goneTime = (int) (60 * 1000 - l);
//                mProgressBar.setProgress(goneTime);
//            }
//
//            @Override
//            public void onFinish() {
//                mProgressBar.setProgress(60 * 1000);
//                isTimeOver = true;
//                finishVoice();
//            }
//        };
//
//        mProgressTimer.start();
//    }

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0x222) {
                wave2.startAnimation(aniSet2);
            } else if (msg.what == 0x333) {
                wave3.startAnimation(aniSet3);
            }
            super.handleMessage(msg);
        }

    };

    private AnimationSet getNewAnimationSet() {
        AnimationSet as = new AnimationSet(true);
        ScaleAnimation sa = new ScaleAnimation(1f, 2.3f, 1f, 2.3f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
        sa.setDuration(ANIMATIONEACHOFFSET * 3);
        sa.setRepeatCount(-1);// 设置循环
        AlphaAnimation aniAlp = new AlphaAnimation(1, 0.1f);
        aniAlp.setRepeatCount(-1);// 设置循环
        as.setDuration(ANIMATIONEACHOFFSET * 3);
        as.addAnimation(sa);
        as.addAnimation(aniAlp);
        return as;
    }

    private void showWaveAnimation() {
        wave1.startAnimation(aniSet);
        handler.sendEmptyMessageDelayed(0x222, ANIMATIONEACHOFFSET);
        handler.sendEmptyMessageDelayed(0x333, ANIMATIONEACHOFFSET * 2);
    }

    private void cancalWaveAnimation() {
        wave1.clearAnimation();
        wave2.clearAnimation();
        wave3.clearAnimation();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.voice_edit_tv:
//                if (isEditType) {
//                    mResultTv.setVisibility(View.VISIBLE);
//                    String editText = mResultEdit.getText().toString();
//                    if (!TextUtils.isEmpty(editText)) {
//                        mResultTv.setText(editText);
//                    }
//
//                    mKeyService.hideKeyboard(mResultEdit);
//                    mResultEdit.setVisibility(View.GONE);
//                    mEidtBtn.setText("点击编辑");
//                } else {
//                    mResultTv.setVisibility(View.GONE);
//
//                    mResultEdit.setVisibility(View.VISIBLE);
//                    String showText = mResultTv.getText().toString();
//                    if (!TextUtils.isEmpty(showText)) {
//                        mResultEdit.setText(showText);
//                        mResultEdit.setSelection(showText.length());
//                    }
//                    mKeyService.showKeyboard(mResultEdit);
//                    mEidtBtn.setText("完成编辑");
//                }
//                isEditType = !isEditType;
//                break;
            case R.id.voice_cancle_iv:
                finish();
                break;
            case R.id.voice_finish_iv:
                if(isFirstClickDuiHao)
                {
                    //对号改键盘
                    isFirstClickDuiHao = false;
                    voice_finish_iv.setImageResource(R.drawable.btn_jianpan);

                    //原来停止录音的逻辑
                    if (isEditType) {
                        TipsUtils.showShort(getApplicationContext(), "请完成内容编辑");
                        return;
                    }

                    isTimeOver = true;
                    finishVoice();
                }else
                {
                    //以后点这里就是编辑功能
//                    if (isEditType) {
////                        mResultTv.setVisibility(View.VISIBLE);
////                        String editText = mResultEdit.getText().toString();
////                        if (!TextUtils.isEmpty(editText)) {
////                            mResultTv.setText(editText);
////                        }
//
////                        mKeyService.hideKeyboard(mResultEdit);
////                        mResultEdit.setVisibility(View.GONE);
////                        mEidtBtn.setText("点击编辑");
//                    } else {
                        mResultTv.setVisibility(View.GONE);

                        mResultEdit.setVisibility(View.VISIBLE);
//                        String showText = mResultTv.getText().toString();
//                        if (!TextUtils.isEmpty(showText)) {
//                            mResultEdit.setText(showText);
//                            mResultEdit.setSelection(showText.length());
//                        }
                    mResultEdit.setSelection(mResultEdit.getText().toString().length());
                        mKeyService.showKeyboard(mResultEdit);
//                        mEidtBtn.setText("完成编辑");
//                    }
                    isEditType = !isEditType;
                }

                break;
            case R.id.voice_pub_iv:
                String resultText = mResultEdit.getText().toString();
                Log.e("lzm", "mResultEdit=" + resultText);
                if (TextUtils.isEmpty(resultText)) {
                    TipsUtils.showShort(getApplicationContext(), "没有内容");
                    return;
                }
                matchTutor(resultText);
                break;
        }
    }

    /**
     * 匹配回答者
     *
     * @param resultText
     */
    private void matchTutor(String resultText) {

        String tag_json_obj = "json_obj_tutor_match_req";
        showLoadDialog();

        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("question", resultText);
            paramObj.put("qid", "0");
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
                    if(TextUtils.isEmpty(qid)) {
                        TipsUtils.showShort(getApplicationContext(), "没有返回qid");
                        return;
                    }

                    Intent intent = new Intent(VoiceLisenActivity.this, VoiceVideoActivity.class);
                    intent.putExtra(VoiceVideoActivity.KEY_Q_ID, qid);
                    startActivity(intent);

                    finish();
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


    private void finishVoice() {
//        mProgressTimer.cancel();
//        mProgressBar.setVisibility(View.GONE);
        mIat.cancel();
        mIat.stopListening();
        cancalWaveAnimation();
//        mEidtBtn.setVisibility(View.VISIBLE);


        voice_layout_iv.setVisibility(View.GONE);
        btn_fasong.setVisibility(View.VISIBLE);
//        voice_finish_iv.setVisibility(View.GONE);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        finishVoice();
        isTimeOver = false;
        isEditType = false;
    }
}
