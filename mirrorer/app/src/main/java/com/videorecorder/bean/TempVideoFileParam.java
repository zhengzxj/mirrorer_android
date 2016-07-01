package com.videorecorder.bean;

public class TempVideoFileParam {
    private String fileName;
    private long timeNodes;
    
    public TempVideoFileParam() {
        
    }
    
    public TempVideoFileParam(long _timeNodes, String _fileName) {
        this.timeNodes = _timeNodes;
        this.fileName = _fileName;
    }
    
    public String getFileName() {
        return fileName;
    }
    
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    public long getTimeNodes() {
        return timeNodes;
    }
    
    public void setTimeNodes(long timeNodes) {
        this.timeNodes = timeNodes;
    }
    
}
