package com.smart.mirrorer.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by lzm on 16/4/21.
 * 推送的匹配回答者信息
 */
public class PushTutoBean implements Parcelable {

    private String qid;
    private String uid;
    private String nick;
    private String icon;
    private float startPrice;
    private float minutePrice;
    private float star;
    private String company;
    private String title;

    public String getQid() {
        return qid;
    }

    public void setQid(String qid) {
        this.qid = qid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public float getStartPrice() {
        return startPrice;
    }

    public void setStartPrice(float startPrice) {
        this.startPrice = startPrice;
    }

    public float getMinutePrice() {
        return minutePrice;
    }

    public void setMinutePrice(float minutePrice) {
        this.minutePrice = minutePrice;
    }

    public float getStar() {
        return star;
    }

    public void setStar(float star) {
        this.star = star;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public PushTutoBean() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.qid);
        dest.writeString(this.uid);
        dest.writeString(this.nick);
        dest.writeString(this.icon);
        dest.writeFloat(this.startPrice);
        dest.writeFloat(this.minutePrice);
        dest.writeFloat(this.star);
        dest.writeString(this.company);
        dest.writeString(this.title);
    }

    protected PushTutoBean(Parcel in) {
        this.qid = in.readString();
        this.uid = in.readString();
        this.nick = in.readString();
        this.icon = in.readString();
        this.startPrice = in.readFloat();
        this.minutePrice = in.readFloat();
        this.star = in.readFloat();
        this.company = in.readString();
        this.title = in.readString();
    }

    public static final Creator<PushTutoBean> CREATOR = new Creator<PushTutoBean>() {
        public PushTutoBean createFromParcel(Parcel source) {
            return new PushTutoBean(source);
        }

        public PushTutoBean[] newArray(int size) {
            return new PushTutoBean[size];
        }
    };
}
