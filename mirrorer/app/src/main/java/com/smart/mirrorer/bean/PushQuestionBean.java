package com.smart.mirrorer.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by lzm on 16/4/21.
 * 回答者端接收的推送匹配问题
 */
public class PushQuestionBean implements Parcelable {

    private String qid;
    private String action;
    private String source;
    private String question;
    private int type;
    private long ts; //时间戳
    private String headImgUrl;
    private int qCount;
    private float star;

    public float getStar() {
        return star;
    }

    public void setStar(float star) {
        this.star = star;
    }

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHeadImgUrl() {
        return headImgUrl;
    }

    public void setHeadImgUrl(String headImgUrl) {
        this.headImgUrl = headImgUrl;
    }

    public int getqCount() {
        return qCount;
    }

    public void setqCount(int qCount) {
        this.qCount = qCount;
    }

    public boolean isEnable;
    public boolean isFollowed;

    public String getQid() {
        return qid;
    }

    public void setQid(String qid) {
        this.qid = qid;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public long getTs() {
        return ts;
    }

    public void setTs(long ts) {
        this.ts = ts;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String content) {
        this.question = content;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.qid);
        dest.writeString(this.name);
        dest.writeFloat(this.star);
        dest.writeString(this.headImgUrl);
        dest.writeInt(this.qCount);
        dest.writeString(this.action);
        dest.writeString(this.source);
        dest.writeString(this.question);
        dest.writeInt(this.type);
        dest.writeLong(this.ts);
        dest.writeByte(isEnable ? (byte) 1 : (byte) 0);
        dest.writeByte(isFollowed ? (byte) 1 : (byte) 0);
    }

    public PushQuestionBean() {
    }

    protected PushQuestionBean(Parcel in) {
        this.qid = in.readString();
        this.name = in.readString();
        this.star = in.readInt();
        this.headImgUrl = in.readString();
        this.qCount = in.readInt();
        this.action = in.readString();
        this.source = in.readString();
        this.question = in.readString();
        this.type = in.readInt();
        this.ts = in.readLong();
        this.isEnable = in.readByte() != 0;
        this.isFollowed = in.readByte() != 0;
    }

    public static final Parcelable.Creator<PushQuestionBean> CREATOR = new Parcelable.Creator<PushQuestionBean>() {
        public PushQuestionBean createFromParcel(Parcel source) {
            return new PushQuestionBean(source);
        }

        public PushQuestionBean[] newArray(int size) {
            return new PushQuestionBean[size];
        }
    };

    @Override
    public String toString() {
        return "PushQuestionBean{" +
                "qid='" + qid + '\'' +
                ", action='" + action + '\'' +
                ", source='" + source + '\'' +
                ", content='" + question + '\'' +
                ", type=" + type +
                ", ts=" + ts +
                ", headImgUrl='" + headImgUrl + '\'' +
                ", qCount=" + qCount +
                ", star=" + star +
                ", name='" + name + '\'' +
                ", isEnable=" + isEnable +
                ", isFollowed=" + isFollowed +
                '}';
    }
}
