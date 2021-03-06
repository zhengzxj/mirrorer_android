package com.smart.mirrorer.net;

import android.util.Log;

import com.android.volley.Response;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.smart.mirrorer.util.GloabalRequestUtil;

import org.json.JSONObject;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by lzm on 16/3/28.
 */
public class GsonCallbackListener<Result> implements Response.Listener<JSONObject> {
    @Override
    public void onResponse(JSONObject response) {
        Log.e("lzm", "response.text="+response.toString());
        if(response == null) {
            onFailed("没有数据");
            return;
        }

        boolean isRequestOk = GloabalRequestUtil.isRequestOk(response);
        if(!isRequestOk) {
            onFailed(GloabalRequestUtil.getNetErrorMsg(response));
            return;
        }

        Result result = null;
        Type resultType = getSuperclassTypeParameter(getClass());
        Log.d("lzm","GsonCallBack Bean Type = "+resultType.toString());
        Gson gson = new Gson();
        try {
            result = gson.fromJson(response.toString(), getGsonTypeParameter(resultType));
        } catch (JsonSyntaxException ex) {
            onFailed("数据解析异常");
            Log.e("lzm", "ex.msg="+ex.getMessage());
            ex.printStackTrace();
        } catch (Exception e) {
            onFailed("数据解析异常");
            Log.e("lzm", "e.msg="+e.getMessage());
            e.printStackTrace();
        }
        onResultSuccess(result);
    }

    public void onResultSuccess(Result result) {

    }

   public void onFailed(String errorMsg){}

    /**
     * Returns the type from super class's type parameter in
     * // copy of com.google.gson.reflect.TypeToken.java .
     */
    static Type getSuperclassTypeParameter(Class<?> subclass) {
        Type superclass = subclass.getGenericSuperclass();
        if (superclass instanceof Class) {
            throw new RuntimeException("Missing type parameter.");
        }
        ParameterizedType parameterized = (ParameterizedType) superclass;
        return parameterized.getActualTypeArguments()[0];
    }

    static Type getGsonTypeParameter(Type type) {
        return com.google.gson.internal.$Gson$Types.canonicalize(type);
    }
}
