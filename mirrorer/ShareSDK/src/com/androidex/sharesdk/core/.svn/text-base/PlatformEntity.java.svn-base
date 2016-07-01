package com.androidex.sharesdk.core;

import com.androidex.sharesdk.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

//分享到平台的ID(必须).1.QQ空间分享,2.QQ好友分享,3.腾讯微博分享,4.新浪微博分享,5.微信好友分享,6.微信朋友圈分享,
//7.微信收藏,8.拷贝,9.邮件,10.短信,11.人人网,12.fackbook,13.twitter,14.google+,15.flickr

public enum PlatformEntity {
    //
    QQ(2, "QQ好友", 1, R.drawable.logo_qq, 0, true), //
    QZONE(1, "QQ空间", 1, R.drawable.logo_qzone, 1, true), //
    WECHAT(5, "微信好友", 1, R.drawable.logo_wechat, 2, true), //
    WECHAT_TIMELINE(6, "微信朋友圈", 1, R.drawable.logo_wechatfavorite, 3, true);

    public int id;
    public int icon;
    public String name;
    public int version;
    public boolean enable;
    public int index;
    public boolean isSel; //详情分享用，记录是否选择

    private PlatformEntity(int id, String name, int version, int icon, int index, boolean enable) {
        this.id = id;
        this.name = name;
        this.version = version;
        this.icon = icon;
        this.index = index;
        this.enable = enable;
    }

    public static PlatformEntity findPlatformById(int id) {
        final PlatformEntity[] values = PlatformEntity.values();
        for (int i = 0; i < values.length; i++) {
            if (values[i].id == id) {
                return values[i];
            }
        }
        return null;
    }

    public static List<PlatformEntity> getAllEnableEntity() {
        final PlatformEntity[] values = PlatformEntity.values();
        List<PlatformEntity> list = new ArrayList<PlatformEntity>();
        for (int i = 0; i < values.length; i++) {
            if (values[i].enable) {
                list.add(values[i]);
            }
        }
        //
        Collections.sort(list, new Comparator<PlatformEntity>() {
            @Override
            public int compare(PlatformEntity lhs, PlatformEntity rhs) {
                if (lhs.index == rhs.index) {
                    return 0;
                } else if (lhs.index > rhs.index) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });
        return list;
    }
}
