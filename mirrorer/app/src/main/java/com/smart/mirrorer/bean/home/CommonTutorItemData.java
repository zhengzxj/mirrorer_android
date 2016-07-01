package com.smart.mirrorer.bean.home;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by lzm on 16/4/30.
 */
public class CommonTutorItemData implements Parcelable {

    private float star;
    private String uid;
    private String title;
    private float startPrice;
    private String company;
    private float minutePrice;
    private String headImgUrl;
    private String nickName;
    private String videoUrl;

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public float getStar() {
        return star;
    }

    public void setStar(float star) {
        this.star = star;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public float getStartPrice() {
        return startPrice;
    }

    public void setStartPrice(float startPrice) {
        this.startPrice = startPrice;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public float getMinutePrice() {
        return minutePrice;
    }

    public void setMinutePrice(float minutePrice) {
        this.minutePrice = minutePrice;
    }

    public String getHeadImgUrl() {
        return headImgUrl;
    }

    public void setHeadImgUrl(String headImgUrl) {
        this.headImgUrl = headImgUrl;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeFloat(this.star);
        parcel.writeString(this.uid);
        parcel.writeString(this.title);
        parcel.writeFloat(this.startPrice);
        parcel.writeString(this.company);
        parcel.writeFloat(this.minutePrice);
        parcel.writeString(this.headImgUrl);
        parcel.writeString(this.nickName);
        parcel.writeString(this.videoUrl);
    }
    public CommonTutorItemData() {
    }

    /**
     * private float star;
     private String uid;
     private String title;
     private float startPrice;
     private String company;
     private float minutePrice;
     private String headImgUrl;
     private String nickName;
     private String videoUrl;
     * @param in
     */
    protected CommonTutorItemData(Parcel in) {
        this.star = in.readFloat();
        this.uid = in.readString();
        this.title = in.readString();
        this.startPrice = in.readFloat();
        this.company = in.readString();
        this.minutePrice = in.readFloat();
        this.headImgUrl = in.readString();
        this.nickName = in.readString();
        this.videoUrl = in.readString();
    }
    public static final Parcelable.Creator<CommonTutorItemData> CREATOR = new Parcelable.Creator<CommonTutorItemData>() {
        public CommonTutorItemData createFromParcel(Parcel source) {
            return new CommonTutorItemData(source);
        }

        public CommonTutorItemData[] newArray(int size) {
            return new CommonTutorItemData[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return "CommonTutorItemData{" +
                "star=" + star +
                ", uid='" + uid + '\'' +
                ", title='" + title + '\'' +
                ", startPrice=" + startPrice +
                ", company='" + company + '\'' +
                ", minutePrice=" + minutePrice +
                ", headImgUrl='" + headImgUrl + '\'' +
                ", nickName='" + nickName + '\'' +
                ", videoUrl='" + videoUrl + '\'' +
                '}';
    }
}
