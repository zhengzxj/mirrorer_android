package com.videorecorder.bean;

import android.media.CamcorderProfile;

public class RecordParam {
    private CamcorderProfile profile;
    private int maxDuration;
    private String outputFileName;
    private String rawOutputFileName;
    
    public CamcorderProfile getProfile() {
        return profile;
    }
    
    public void setProfile(CamcorderProfile profile) {
        this.profile = profile;
    }
    
    public int getMaxDuration() {
        return maxDuration;
    }
    
    public void setMaxDuration(int maxDuration) {
        this.maxDuration = maxDuration;
    }
    
    public String getOutputFileName() {
        return outputFileName;
    }
    
    public void setOutputFileName(String outputFileName) {
        this.outputFileName = outputFileName;
    }
    
    public void setRawOutputFileName(String rawOutputFileName) {
        this.rawOutputFileName = rawOutputFileName;
    }
    
    public String getRawOutputFileName() {
        return rawOutputFileName;
    }
    
}
