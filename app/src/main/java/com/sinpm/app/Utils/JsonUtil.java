package com.sinpm.app.Utils;

import android.text.TextUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class JsonUtil {
    /**
     * 通过键值,获取jsonObject
     *
     * @return
     */
    public static JSONObject jsonObject() {
        return new JSONObject();
    }

    /**
     * 通过键值,获取jsonObject
     * 
     * @param key
     * @param value
     * @return
     */
    public static JSONObject jsonObject(String key, Object value) {
        JSONObject jo = new JSONObject();
        jo.put(key, value);
        return jo;
    }

    public static JSONArray jsonArray(Object... values) {
        JSONArray ja = new JSONArray();
        for (Object object : values) {
            ja.add(object);
        }
        return ja;
    }

    /**
     * @param key
     * @param value
     * @param values
     * @return
     */
    public static JSONObject jsonObject(String key, Object value, Object... values) {
        JSONObject jo = jsonObject(key, value);
        for (int i = 0; i < values.length; i += 2) {
            Object k = values[i];
            Object v = null;
            if (i + 1 < values.length) {
                v = values[i + 1];
            }
            jo.put(k.toString(), v);
        }
        return jo;
    }

    /**
     * string类型的json转换成map
     * 
     * @param str
     * @return
     */
    public static HashMap<String, Object> StringJsontoHashMap(String str) {
        if (TextUtils.isEmpty(str))
            return null;
        HashMap<String, Object> data = new HashMap<String, Object>();
        // 将json字符串转换成jsonObject
        JSONObject jsonObject = JSONObject.parseObject(str);
        Iterator it = jsonObject.keySet().iterator();
        // 遍历jsonObject数据，添加到Map对象
        while (it.hasNext()) {
            String key = String.valueOf(it.next());
            Object value = jsonObject.get(key);
            data.put(key, value);
        }
        return data;
    }

    public static List<HashMap<String, Object>> StringJsontoArray(String str) {
        List<HashMap<String, Object>> dataList = new ArrayList<HashMap<String, Object>>();
        // 将json字符串转换成jsonObject
        JSONArray jsonArray = JSONObject.parseArray(str);
        // 遍历jsonObject数据，添加到Map对象
        for (int i = 0; i < jsonArray.size(); i++) {
            dataList.add(StringJsontoHashMap(jsonArray.get(i).toString()));
        }
        return dataList;
    }

    /**
     * 是否是json字符串
     * 
     * @param str
     * @return
     */
    public static boolean isJson(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        try {
            JSONObject.parse(str);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    public static JSONObject parseObject(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        try {
            return JSONObject.parseObject(str);
        } catch (Exception e) {
            return null;
        }
    }
    public  static <T> T  parseObject(String str,Class<T> clazz) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        try {
            return JSONObject.parseObject(str,clazz);
        } catch (Exception e) {
            return null;
        }
    }


}