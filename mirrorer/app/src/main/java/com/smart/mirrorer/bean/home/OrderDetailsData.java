package com.smart.mirrorer.bean.home;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by lzm on 16/4/29.
 */
public class OrderDetailsData implements Parcelable {

    private String guiderId;
    private float payMoney;//最终价格
    private String guiderName;
    private int callTalkDuration;//总时间
    private float guiderStar;
    private float minutePrice;//单价/分钟
    private String question;
    private String guiderHeadUrl;
    private float startPrice;//起步价
    private String orderId;
    private String prePayId;
    private String company;
    private String title;
    private String createdAt;



    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public static Creator<OrderDetailsData> getCREATOR() {
        return CREATOR;
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

    public String getGuiderId() {
        return guiderId;
    }

    public void setGuiderId(String guiderId) {
        this.guiderId = guiderId;
    }

    public float getPayMoney() {
        return payMoney;
    }

    public void setPayMoney(float payMoney) {
        this.payMoney = payMoney;
    }

    public String getGuiderName() {
        return guiderName;
    }

    public void setGuiderName(String guiderName) {
        this.guiderName = guiderName;
    }

    public int getCallTalkDuration() {
        return callTalkDuration;
    }

    public void setCallTalkDuration(int callTalkDuration) {
        this.callTalkDuration = callTalkDuration;
    }

    public float getGuiderStar() {
        return guiderStar;
    }

    public void setGuiderStar(float guiderStar) {
        this.guiderStar = guiderStar;
    }

    public float getMinutePrice() {
        return minutePrice;
    }

    public void setMinutePrice(float minutePrice) {
        this.minutePrice = minutePrice;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getGuiderHeadUrl() {
        return guiderHeadUrl;
    }

    public void setGuiderHeadUrl(String guiderHeadUrl) {
        this.guiderHeadUrl = guiderHeadUrl;
    }

    public float getStartPrice() {
        return startPrice;
    }

    public void setStartPrice(float startPrice) {
        this.startPrice = startPrice;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getPrePayId() {
        return prePayId;
    }

    public void setPrePayId(String prePayId) {
        this.prePayId = prePayId;
    }

    public OrderDetailsData() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.guiderId);
        dest.writeFloat(this.payMoney);
        dest.writeString(this.guiderName);
        dest.writeInt(this.callTalkDuration);
        dest.writeFloat(this.guiderStar);
        dest.writeFloat(this.minutePrice);
        dest.writeString(this.question);
        dest.writeString(this.guiderHeadUrl);
        dest.writeFloat(this.startPrice);
        dest.writeString(this.orderId);
        dest.writeString(this.prePayId);
        dest.writeString(this.company);
        dest.writeString(this.title);
        dest.writeString(this.createdAt);
    }

    protected OrderDetailsData(Parcel in) {
        this.guiderId = in.readString();
        this.payMoney = in.readFloat();
        this.guiderName = in.readString();
        this.callTalkDuration = in.readInt();
        this.guiderStar = in.readFloat();
        this.minutePrice = in.readFloat();
        this.question = in.readString();
        this.guiderHeadUrl = in.readString();
        this.startPrice = in.readFloat();
        this.orderId = in.readString();
        this.prePayId = in.readString();
        this.company = in.readString();
        this.title = in.readString();
        this.createdAt = in.readString();
    }

    public static final Creator<OrderDetailsData> CREATOR = new Creator<OrderDetailsData>() {
        public OrderDetailsData createFromParcel(Parcel source) {
            return new OrderDetailsData(source);
        }

        public OrderDetailsData[] newArray(int size) {
            return new OrderDetailsData[size];
        }
    };

    @Override
    public String toString() {
        return "OrderDetailsData{" +
                "guiderId='" + guiderId + '\'' +
                ", payMoney=" + payMoney +
                ", guiderName='" + guiderName + '\'' +
                ", callTalkDuration=" + callTalkDuration +
                ", guiderStar=" + guiderStar +
                ", minutePrice=" + minutePrice +
                ", question='" + question + '\'' +
                ", guiderHeadUrl='" + guiderHeadUrl + '\'' +
                ", startPrice=" + startPrice +
                ", orderId='" + orderId + '\'' +
                ", prePayId='" + prePayId + '\'' +
                ", company='" + company + '\'' +
                ", title='" + title + '\'' +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }
}
