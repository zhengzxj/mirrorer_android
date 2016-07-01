package com.videorecorder.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import com.videorecorder.bean.MaterialParam;

import android.content.Context;
import android.content.res.XmlResourceParser;


public class XmlParseUtils {
    public static List<MaterialParam> parseXml(Context context, int xmlId) {
        List<MaterialParam> lists = null;
        XmlResourceParser xmlParser = context.getResources().getXml(xmlId);
        if (xmlParser != null) {
            try {
                int eventType = xmlParser.getEventType();
                MaterialParam param = null;
                while (eventType != XmlResourceParser.END_DOCUMENT) {
                    switch (eventType) {
                        case XmlResourceParser.START_DOCUMENT:
                        lists = new ArrayList<MaterialParam>();
                        break;
                        case XmlResourceParser.START_TAG:
                        String tagName = xmlParser.getName();
                        if ("material".equals(tagName)) {
                            param = new MaterialParam();
                        }
                        if ("id".equals(tagName)) {
                            String id = xmlParser.nextText();
                            param.setId(id);
                        }
                        if ("type".equals(tagName)) {
                            String type = xmlParser.nextText();
                            int _type = Integer.parseInt(type);
                            param.setType(_type);
                        }
                        if ("name".equals(tagName)) {
                            String name = xmlParser.nextText();
                            param.setName(name);
                        }
                        if("preview_resourcename".equals(tagName)){
                            String preview_resName = xmlParser.nextText();
                            param.setPreviewResName(preview_resName);
                        }
                        if ("resourcename".equals(tagName)) {
                            String resourceName = xmlParser.nextText();
                            param.setResourceName(resourceName);
                        }
                        if("format".equals(tagName)){
                            String format = xmlParser.nextText();
                            param.setFormat(format);
                        }
                        break;
                        case XmlResourceParser.END_TAG:
                        String endTagName = xmlParser.getName();
                        if ("material".equals(endTagName)) {
                            lists.add(param);
                        }
                        break;
                        default:
                        break;
                    }
                    eventType = xmlParser.next();
                }
            } catch (XmlPullParserException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return lists;
    }
}
