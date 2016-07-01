package com.smart.mirrorer.util.mUtil;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;

import com.smart.mirrorer.R;
import com.smart.mirrorer.base.BaseApplication;

/**
 * Created by zhengfei on 16/6/7.
 */

public class SoundPoulManager {
    private static SoundPoulManager instance;
    private SoundPool sp;//声明一个SoundPool
    private int music;
    private SoundPoulManager() {
        sp = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);//第一个参数为同时播放数据流的最大个数，第二数据流类型，第三为声音质量
        music = sp.load(BaseApplication.getInstance().getApplicationContext(), R.raw.ring_01, 1); //把你的声音素材放到res/raw里，第2个参数即为资源文件，第3个为音乐的优先级
    }

    public static synchronized void btnClickSound() {
        if (instance == null) {
            instance = new SoundPoulManager();
        }
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                instance.sp.play(instance.music, 1, 1, 0, 0, 1);
//            }
//        }, 20);
        instance.sp.play(instance.music, 1, 1, 0, 0, 1);
    }
}
