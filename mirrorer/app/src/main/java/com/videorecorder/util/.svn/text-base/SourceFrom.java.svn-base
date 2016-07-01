package com.videorecorder.util;


public class SourceFrom {
	private static SourceFrom instance;
    private boolean cameraOrLocal = true;//true:camera false:local
	private SourceFrom() {
       
	}
    public static SourceFrom getInstance() {
        if (instance == null) {
            instance = new SourceFrom();
        }
        return instance;
    }
    public boolean isCameraOrLocal() {
		return cameraOrLocal;
	}

	public void setCameraOrLocal(boolean cameraOrLocal) {
		this.cameraOrLocal = cameraOrLocal;
	}

}
