package com.xuemeng.xmdevtools.utils;


import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JsonUtils {

    public static final <T> T parseObject(@NonNull String text, @NonNull Class<T> clazz) {
        if (text == null || clazz == null) return null;
        try {
            return JSON.parseObject(text, clazz);
        } catch (JSONException jexp) {
            return null;
        }
    }

    public static <T> String serialize(T object) {
        return JSON.toJSONString(object);
    }

    public static final <T> List<T> parseArray(@NonNull String text, @NonNull Class<T> clazz) {
        if (text == null || clazz == null) return null;
        try {
            return JSON.parseArray(text, clazz);
        } catch (JSONException jexp) {
            return null;
        }
    }

    /**
     * @param text
     * @return
     */
    public static final List<String> parseStringArray(@NonNull String text) {
        if (text == null) return null;
        List<String> jsonArrayList = new ArrayList<>();
        JSONArray cartList = null;//jsonObject.getJSONArray("noneActivityGoods");
        try {
            cartList = new JSONArray(text);
        } catch (org.json.JSONException e) {
            e.printStackTrace();
        }
        if (cartList != null) {
            for (int i = 0; i < cartList.length(); i++) {
                String item = cartList.optString(i);
                jsonArrayList.add(item);
            }
        }
        return jsonArrayList;
    }

    //组件直接从jsonArray中获取JsonObject
    public static JSONObject getJsonObj(JSONArray jsonArray, int pos) {
        JSONObject object = null;
        try {
            object = jsonArray.getJSONObject(pos);
        } catch (org.json.JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    //组件直接获取响应的字段
    public static String optString(JSONObject jsonObject, String key) {
        String returnStr = "";
        try {
            returnStr = jsonObject.getString(key);
        } catch (org.json.JSONException e) {
            e.printStackTrace();
        }
        return returnStr;
    }

    //获取JsonObject中的JasonArray
    public static JSONArray getJsonArray(JSONObject jsonObject, String key) {
        JSONArray jsonArray = null;
        try {
            jsonArray = jsonObject.getJSONArray(key);
        } catch (org.json.JSONException e) {
            e.printStackTrace();
        }
        return jsonArray;
    }

    //向JsonObject中存放字符串
    public static void putString(JSONObject jsonObject, String key, String value) {
        if (jsonObject != null) {
            try {
                jsonObject.put(key, value);
            } catch (org.json.JSONException e) {
                e.printStackTrace();
            }
        }
    }

    //向JsonArray中存放JsonObject
    public static void putJsonObj(JSONArray jsonArray, int pos, JSONObject jsonObject) {
        if (Preconditions.isNullOrEmpty(jsonArray)) {
            return;
        }
        if (Preconditions.isNullOrEmpty(jsonObject)) {
            return;
        }
        try {
            jsonArray.put(pos, jsonObject);
        } catch (org.json.JSONException e) {
            e.printStackTrace();
        }
    }

    //向JsonObject中存放JsonArray
    public static JSONObject putJsonArray(JSONObject jsonObject, String key, JSONArray jsonArray) {
        JSONObject obj = null;
        if (Preconditions.isNullOrEmpty(jsonObject)) {
            return obj;
        }
        if (Preconditions.isNullOrEmpty(jsonArray)) {
            return obj;
        }
        try {
            jsonObject.put(key, jsonArray);
            obj = jsonObject;
        } catch (org.json.JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }

    //将jsonStr转换成JSONArray
    public static JSONArray parseStringToJSONArray(String jsonStr) {
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(jsonStr);
        } catch (org.json.JSONException e) {
            e.printStackTrace();
        }
        return jsonArray;
    }
}
