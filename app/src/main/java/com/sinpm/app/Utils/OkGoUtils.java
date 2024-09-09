package com.sinpm.app.Utils;


import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.HttpParams;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class OkGoUtils {
    /**
     * get请求获取数据
     *
     * @param url
     */
    public static void getParams(String url, HttpParams params, StringCallback callback) {
        OkGo.<String>get(url)
                .params(params)
                .execute(callback);
    }
    public static void getParamsToken(String url, HttpParams params, StringCallback callback) {
        OkGo.<String>get(url)
                .params(params)
                .execute(callback);
    }

    public static void postParams(String url, HttpParams params, StringCallback callback) {
        OkGo.<String>post(url)
                // .headers(httpHeaders)
                .params(params)
                .execute(callback);
    }
    public static void postParamsToken(String url, HttpParams params, StringCallback callback) {
        OkGo.<String>post(url)
                .params(params)
                .execute(callback);
    }

    public static void putParamsToken(String url, HttpParams params, StringCallback callback) {
        OkGo.<String>put(url)
                .params(params)
                .execute(callback);
    }

    public static void postbBody(String url, String data, StringCallback callback) {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, data);
        OkGo.<String>post(url)
                .upRequestBody(body)
                .execute(callback);
    }
}
