package com.videorecorder.bean;

import java.util.Arrays;

public class MultiFilter {
    private String outFileName;// 输出文件
    private int oRotate;
    private int oFormat;
    private String inFileName;// 输入文件
    private int iRotate;
    private int iFormat;
    private String[] inMarkFileName;
    private int filterNum;// 特效个数
    private int[] filterType;// 特效类型
    private int[] subType;// 特效子类型
    private byte[][] rcData;// 资源数据
    private int[] rcWidth;// 资源数据的宽
    private int[] rcHeight;// 资源数据的高
    private int[] pState;//
    private int[] pEncoder;
    private int[] reserve;// --reserve[0]=flagNum : 标记个数
    
    // --reserve[1]=muxingFlag : 是否生成文件的标记
    // --reserve[2]=videoCallBackFlag : 是否回调视频帧的标记
    // --reserve[3...N]=reserve : 其它
    public String getOutFileName() {
        return outFileName;
    }
    
    public void setOutFileName(String outFileName) {
        this.outFileName = outFileName;
    }
    
    public int getoRotate() {
        return oRotate;
    }
    
    public void setoRotate(int oRotate) {
        this.oRotate = oRotate;
    }
    
    public int getoFormat() {
        return oFormat;
    }
    
    public void setoFormat(int oFormat) {
        this.oFormat = oFormat;
    }
    
    public String getInFileName() {
        return inFileName;
    }
    
    public void setInFileName(String inFileName) {
        this.inFileName = inFileName;
    }
    
    public int getiRotate() {
        return iRotate;
    }
    
    public void setiRotate(int iRotate) {
        this.iRotate = iRotate;
    }
    
    public int getiFormat() {
        return iFormat;
    }
    
    public void setiFormat(int iFormat) {
        this.iFormat = iFormat;
    }
    
    public String[] getInMarkFileName() {
        return inMarkFileName;
    }
    
    public void setInMarkFileName(String[] inMarkFileName) {
        this.inMarkFileName = inMarkFileName;
    }
    
    public int getFilterNum() {
        return filterNum;
    }
    
    public void setFilterNum(int filterNum) {
        this.filterNum = filterNum;
    }
    
    public int[] getFilterType() {
        return filterType;
    }
    
    public void setFilterType(int[] filterType) {
        this.filterType = filterType;
    }
    
    public int[] getSubType() {
        return subType;
    }
    
    public void setSubType(int[] subType) {
        this.subType = subType;
    }
    
    public byte[][] getRcData() {
        return rcData;
    }
    
    public void setRcData(byte[][] rcData) {
        this.rcData = rcData;
    }
    
    public int[] getRcWidth() {
        return rcWidth;
    }
    
    public void setRcWidth(int[] rcWidth) {
        this.rcWidth = rcWidth;
    }
    
    public int[] getRcHeight() {
        return rcHeight;
    }
    
    public void setRcHeight(int[] rcHeight) {
        this.rcHeight = rcHeight;
    }
    
    public int[] getpState() {
        return pState;
    }
    
    public void setpState(int[] pState) {
        this.pState = pState;
    }
    
    public int[] getpEncoder() {
        return pEncoder;
    }
    
    public void setpEncoder(int[] pEncoder) {
        this.pEncoder = pEncoder;
    }
    
    public int[] getReserve() {
        return reserve;
    }
    
    public void setReserve(int[] reserve) {
        this.reserve = reserve;
    }

    @Override
    public String toString() {
        return "MultiFilter [outFileName=" + outFileName + ", oRotate=" + oRotate + ", oFormat=" + oFormat
                + ", inFileName=" + inFileName + ", iRotate=" + iRotate + ", iFormat=" + iFormat + ", inMarkFileName="
                + Arrays.toString(inMarkFileName) + ", filterNum=" + filterNum + ", filterType="
                + Arrays.toString(filterType) + ", subType=" + Arrays.toString(subType) + ", rcData="
                + Arrays.toString(rcData) + ", rcWidth=" + Arrays.toString(rcWidth) + ", rcHeight="
                + Arrays.toString(rcHeight) + ", pState=" + Arrays.toString(pState) + ", pEncoder="
                + Arrays.toString(pEncoder) + ", reserve=" + Arrays.toString(reserve) + "]";
    }
    
}
