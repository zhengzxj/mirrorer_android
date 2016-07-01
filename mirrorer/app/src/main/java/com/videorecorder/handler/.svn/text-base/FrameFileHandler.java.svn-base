package com.videorecorder.handler;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.videorecorder.bean.Frame;
import com.videorecorder.util.FileUtils;

import android.content.Context;


public class FrameFileHandler {
    private static FrameFileHandler instance;
    private List<Frame> frames;
    private Map<Object,Frame> mapFrames;
    
    private FrameFileHandler() {
        frames = new ArrayList<Frame>();
        mapFrames = new LinkedHashMap<Object, Frame>();
    }
    
    public static FrameFileHandler getInstance() {
        if (instance == null) {
            instance = new FrameFileHandler();
        }
        return instance;
    }
    
    public void putFrame(Object key,Frame frame) {
        if (mapFrames != null) {
            mapFrames.put(key, frame);
        }
        frames.add(frame);
    }
    
    public Frame getFrameForKey(Object key) {
        if (mapFrames != null && mapFrames.size() > 0) {
            return mapFrames.get(key);
        }
        return null;
    }
    
    public List<Frame> getFrames() {
//        for (Entry<Object, Frame> entry : mapFrames.entrySet()) {
//            Frame frame = entry.getValue();
//            if (frame != null) {
//                frames.add(frame);
//            }
//        }
        return frames;
    }
    
    public void clear(Context context) {
        FileUtils.delDirectory(context, FileUtils.FILE_IMG_CACHE_DIR);
        frames.clear();
        mapFrames.clear();
    }
}
