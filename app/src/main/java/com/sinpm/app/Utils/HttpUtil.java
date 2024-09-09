package com.sinpm.app.Utils;

import android.app.Activity;
import android.app.Dialog;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hjq.toast.ToastUtils;
import com.sinpm.app.R;
import com.sinpm.app.base.API;
import com.sinpm.app.base.ActivityManager;
import com.tencent.bugly.crashreport.CrashReport;

import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.ConnectionPool;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

public class HttpUtil {
    public static final MediaType MEDIA_TYPE
            = MediaType.get("application/json; charset=utf-8");
    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");


    public static OkHttpClient client;

    private static final HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor(new HttpLogger());//创建拦截对象

    static {

        logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);//这一句一定要记得写，否则没有数据输出
        OkHttpClient.Builder clientBuilde = new OkHttpClient.Builder();
        clientBuilde.connectTimeout(30, TimeUnit.SECONDS);
        clientBuilde.readTimeout(30, TimeUnit.SECONDS);
        clientBuilde.writeTimeout(30, TimeUnit.SECONDS);
        clientBuilde.addNetworkInterceptor(logInterceptor);
//        clientBuilde.addInterceptor(new HttpResponseConnectTimeout.OkhttpInterceptor(maxRentry)) //过滤器，设置最大重试次数

        clientBuilde.connectionPool(new ConnectionPool(32, 5, TimeUnit.MINUTES));
        client = clientBuilde.build();

    }

    private static String apiBase = API.Base_PRD_URL;


    public static <T> void request(Activity activity, String url, String httpMethod,
                                   Map<String, Object> params,
                                   Class<T> clazz,CallBackResult<T> callBack) {

        try {
            if (!API.Init.equals(url) && TextUtils.isEmpty(PropertiesUtils.getValue(Constants.LOCAL_CODE,""))) {
                return;
            }
            if (ActivityManager.isDebug(activity)) {
                apiBase = API.Base_URL;
            }
            url = apiBase + url;
            Request request = null;
            Headers.Builder builder = new Headers.Builder();


            builder.add("locale", LanguageUtil.getLocaleByLanguage().getLanguage());
            Headers headers = builder.build();

            switch (httpMethod) {
                case HttpMethod.GET:
                    StringBuilder paramsUrl = new StringBuilder("?");
                    for (String key : params.keySet()) {
                        paramsUrl.append(key + "=" + params.get(key) + "&");
                    }
                    String urlParams = paramsUrl.substring(0, paramsUrl.length() - 1);
                    request = new Request.Builder()
                            .url(url + urlParams)
                            .headers(headers)
                            .get()
                            .build();
                    break;
                case HttpMethod.POST:
                    RequestBody body = RequestBody.create(JSON.toJSONString(params), MEDIA_TYPE);
                    request = new Request.Builder()
                            .url(url)
                            .headers(headers)
                            .post(body)
                            .build();
                    break;
                case HttpMethod.UPLOAD_FILE:
                    String localPath = (String) params.get("file");
                    File file = new File(localPath);
                    if (!file.exists()) {
                        ToastUtils.show("文件不存在");
                        return;
                    }
                    RequestBody requestBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("file", file.getName(), MultipartBody.create(file, MediaType.parse("multipart/form-data")))
                            .addFormDataPart("isSave", String.valueOf(params.get("isSave")))
                            .build();
                    request = new Request.Builder()
                            .url(url)
                            .headers(headers)
                            .post(requestBody)
                            .build();
                    break;
            }

            String finalUrl = url;
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
//                    ToastUtils.show(activity.getString(R.string.sys_wifi));
                    if (activity == null || activity.isFinishing()) {
                        return;
                    }
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            boolean data = SharedPreferencesUtil.getData(activity, Constants.wifiSave,false);
                            if (data){
                                return;
                            }
                            callBack.fail("网络连接失败" + e.getMessage());
                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (activity == null || activity.isFinishing()) {
                        Log.d("kwwl", "activity 已经被销毁");
                        return;
                    }
                    if (response.isSuccessful()) {
                        String code = PropertiesUtils.getValue(Constants.LOCAL_CODE,"");
                        if (!code.equals("")) {
                            CheckMPushInit(code);
                        }
                        //回调的方法执行在子线程。
                        String responsStr = "";
                        try {
                            responsStr = response.body().string();
                        } catch (SocketTimeoutException e) {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    callBack.fail("请求超时");
                                }
                            });
                        } catch (Exception e) {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    callBack.fail("请求超时");
                                }
                            });
                        }
                        response.close();
                        if (TextUtils.isEmpty(responsStr)) {
                            return;
                        }
                        //  print(responsStr);
                        JSONObject jsonObject = JsonUtil.parseObject(responsStr);
//                    response.getRawResponse().body().close();
                        if (jsonObject!=null &&jsonObject.getInteger("code") == 0) {
                            String objectString = jsonObject.getString("data");

                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    callBack.success(JSON.parseObject(objectString, clazz));
                                }
                            });

                        } else {
                            String erroMsg =jsonObject==null?null:  jsonObject.getString("msg");
                            if (erroMsg != null && !TextUtils.isEmpty(erroMsg)) {
                                if (!"设备编号不能为空".equals(erroMsg)){
                                    ToastUtils.show(erroMsg);
                                }
                                Log.d("TAG", "url: "+finalUrl+",param:"+JSON.toJSONString(params)+",msg:"+erroMsg);

                            }
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    callBack.fail(erroMsg);
                                }
                            });
                            String erroContent = code + "/" + finalUrl + "/" + erroMsg;
                            CrashReport.postCatchedException(new Throwable(erroContent));
                        }
                    } else {
                        callBack.fail("获取服务器数据异常,错误码:"+response.code());
                    }

                }
            });
        } catch (Exception exception) {
            callBack.fail(exception.getMessage());
        }
    }

    public static <T> void requestNoActivity(String code,String url, String httpMethod,
                                   Map<String, Object> params,
                                   Class<T> clazz,CallBackResult<T> callBack) {

        try {
            if (!API.Init.equals(url)) {
                return;
            }
            apiBase = API.Base_URL;
            url = apiBase + url;

            Request request = null;
            Headers.Builder builder = new Headers.Builder();


            builder.add("locale", LanguageUtil.getLocaleByLanguage().getLanguage());
            Headers headers = builder.build();

            switch (httpMethod) {
                case HttpMethod.GET:
                    StringBuilder paramsUrl = new StringBuilder("?");
                    for (String key : params.keySet()) {
                        paramsUrl.append(key + "=" + params.get(key) + "&");
                    }
                    String urlParams = paramsUrl.substring(0, paramsUrl.length() - 1);
                    request = new Request.Builder()
                            .url(url + urlParams)
                            .headers(headers)
                            .get()
                            .build();
                    break;
                case HttpMethod.POST:
                    RequestBody body = RequestBody.create(JSON.toJSONString(params), MEDIA_TYPE);
                    request = new Request.Builder()
                            .url(url)
                            .headers(headers)
                            .post(body)
                            .build();
                    break;
                case HttpMethod.UPLOAD_FILE:
                    String localPath = (String) params.get("file");
                    File file = new File(localPath);
                    if (!file.exists()) {
                        ToastUtils.show("文件不存在");
                        return;
                    }
                    RequestBody requestBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("file", file.getName(), MultipartBody.create(file, MediaType.parse("multipart/form-data")))
                            .addFormDataPart("isSave", String.valueOf(params.get("isSave")))
                            .build();
                    request = new Request.Builder()
                            .url(url)
                            .headers(headers)
                            .post(requestBody)
                            .build();
                    break;
            }

            String finalUrl = url;
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        if (!code.equals("")) {
                            CheckMPushInit(code);
                        }
                        //回调的方法执行在子线程。
                        String responsStr = "";
                        try {
                            responsStr = response.body().string();
                        } catch (SocketTimeoutException e) {
                        } catch (Exception e) {
                        }
                        response.close();
                        if (TextUtils.isEmpty(responsStr)) {
                            return;
                        }
                        //  print(responsStr);
                        JSONObject jsonObject = JsonUtil.parseObject(responsStr);
//                    response.getRawResponse().body().close();
                        if (jsonObject!=null &&jsonObject.getInteger("code") == 0) {
                            String objectString = jsonObject.getString("data");
                            callBack.success(JSON.parseObject(objectString, clazz));
                        } else {
                            String erroMsg =jsonObject==null?null:  jsonObject.getString("msg");
                            if (erroMsg != null && !TextUtils.isEmpty(erroMsg)) {
                                if (!"设备编号不能为空".equals(erroMsg)){
                                    ToastUtils.show(erroMsg);
                                }
                                Log.d("TAG", "url: "+finalUrl+",param:"+JSON.toJSONString(params)+",msg:"+erroMsg);

                            }
                            callBack.fail(erroMsg);
                            String erroContent = code + "/" + finalUrl + "/" + erroMsg;
                            CrashReport.postCatchedException(new Throwable(erroContent));
                        }
                    } else {
                        callBack.fail("获取服务器数据异常,错误码:"+response.code());
                    }

                }
            });
        } catch (Exception exception) {
            callBack.fail(exception.getMessage());
        }
    }


    public static <T> void request(String url, String httpMethod, Boolean sign,
                                   Map<String, Object> params, CallBack callBack,
                                   Class<T> clazz, Boolean isArray, Dialog dialog, Activity activity) {
        try {
            if (!API.Init.equals(url) && TextUtils.isEmpty(PropertiesUtils.getValue(Constants.LOCAL_CODE,""))) {
                return;
            }
            if (ActivityManager.isDebug(activity)) {
                apiBase = API.Base_URL;
            }
            url = apiBase + url;
            if (dialog != null) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        dialog.show();
                    }
                });

            }


            Request request = null;
            Headers.Builder builder = new Headers.Builder();
            if (sign) {
                //head增加签名传递
                String timeStamp = String.valueOf(System.currentTimeMillis());
                builder.add("sign", ASignUtil.sign(params, timeStamp))
                        .add("timeStamp", timeStamp);
            }

            builder.add("locale", LanguageUtil.getLocaleByLanguage().getLanguage());
            Headers headers = builder.build();

            switch (httpMethod) {
                case HttpMethod.GET:
                    StringBuilder paramsUrl = new StringBuilder("?");
                    for (String key : params.keySet()) {
                        paramsUrl.append(key + "=" + params.get(key) + "&");
                    }
                    String urlParams = paramsUrl.substring(0, paramsUrl.length() - 1);
//                RequestBody body = RequestBody.create(json, JSON);
                    request = new Request.Builder()
                            .url(url + urlParams)
                            .headers(headers)
                            .get()
                            .build();
                    break;
                case HttpMethod.POST:
                    RequestBody body = RequestBody.create(JSON.toJSONString(params), MEDIA_TYPE);
                    request = new Request.Builder()
                            .url(url)
                            .headers(headers)
                            .post(body)
                            .build();
                    break;
                case HttpMethod.UPLOAD_FILE:
                    String localPath = (String) params.get("file");
                    File file = new File(localPath);
                    if (!file.exists()) {
                        ToastUtils.show("文件不存在");
                        return;
                    }
//                RequestBody body = RequestBody.create(JSON.toJSONString(params), MEDIA_TYPE);
                    //3.构建MultipartBody
                    RequestBody requestBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("file", file.getName(), MultipartBody.create(file, MediaType.parse("multipart/form-data")))
                            .addFormDataPart("isSave", String.valueOf(params.get("isSave")))
                            .build();
                    request = new Request.Builder()
                            .url(url)
                            .headers(headers)
                            .post(requestBody)
                            .build();
                    break;
            }

            String finalUrl = url;
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    Log.e("onFailure", e.getMessage());

                    boolean data = SharedPreferencesUtil.getData(activity, Constants.wifiSave,false);
                    if (data){
                        return;
                    }
//                    ToastUtils.show(activity.getString(R.string.sys_wifi));
                    if (activity == null || activity.isFinishing()) {
                        return;
                    }
                    if (dialog != null) {

                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (dialog.isShowing()) {
                                    dialog.dismiss();
                                }
                            }
                        });

                    }
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (activity == null || activity.isFinishing()) {
                        Log.d("kwwl", "activity 已经被销毁");
                        return;
                    }
                    if (response.isSuccessful()) {
                        String code = PropertiesUtils.getValue(Constants.LOCAL_CODE,"");
                        if (!code.equals("")) {
                            CheckMPushInit(code);
                        }
                        //回调的方法执行在子线程。
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Log.d("request", "-------------------------------");
                        Log.d("request", "读取数据【" + sdf.format(new Date()) + "】" + "当前线程【" + Thread.currentThread().getName() + "】");
                        Log.d("request", "-------------------------------");

                        String responsStr = "";

                        try {
                            responsStr = response.body().string();
                        } catch (SocketTimeoutException e) {
                            if (dialog != null) {
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ToastUtils.show("请求超时");
                                        if (dialog.isShowing()) {
                                            dialog.dismiss();
                                        }
                                    }
                                });
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        response.close();
                        if (TextUtils.isEmpty(responsStr)) {
                            return;
                        }
                        //  print(responsStr);
                        JSONObject jsonObject = JsonUtil.parseObject(responsStr);
//                    response.getRawResponse().body().close();
                        if (jsonObject!=null && jsonObject.getInteger("code") == 0) {
                            String objectString = jsonObject.getString("data");
                            if (isArray) {
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        callBack.setResult(JSON.parseArray(objectString, clazz));
                                    }
                                });
                            } else {
                                if (clazz.getName().equals(String.class.getName())) {
                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            callBack.setResult(objectString);
                                        }
                                    });
                                } else {

//                                    print(objectString);

                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                if (TextUtils.isEmpty(objectString)) {
                                                    callBack.setResult(null);
                                                } else {
                                                    callBack.setResult(JsonUtil.parseObject(objectString, clazz));
                                                }
                                            } catch (Exception e) {
                                                ToastUtils.show("数据解析异常" + e.getMessage());
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                }
                            }
                        } else {
                            String erroMsg =jsonObject==null ?null : jsonObject.getString("msg");
                            if (erroMsg != null && !TextUtils.isEmpty(erroMsg)) {
                                if (!"设备编号不能为空".equals(erroMsg)){
                                    ToastUtils.show(erroMsg);
                                }
                                Log.d(erroMsg, finalUrl + JSON.toJSONString(params));
                            }

                            String erroContent = code + "/" + finalUrl + "/" + erroMsg;
                            CrashReport.postCatchedException(new Throwable(erroContent));
                        }
                    } else {
                        ToastUtils.show("网络连接失败" + response.code());
                    }
                    if (dialog != null && !activity.isFinishing()) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (dialog.isShowing()) {
                                    dialog.dismiss();
                                }
                            }
                        });
                    }
                }
            });
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public interface CallBack<T> {
        void setResult(T t);
    }


    public interface CallBackResult<T> {
        void success(T t);


        void fail(String msg);
    }

    /**
     * 检查mpush是否初始化，没有则开启
     *
     * @param deviceCode
     */
    private static void CheckMPushInit(String deviceCode) {
//        if(Boolean.FALSE.equals(BaseApplication.getInstance().getConnectFlag().getValue())){
//            BaseApplication.getInstance().initMpush(deviceCode);
//        }
    }

    private static void print(String response) {
        if (TextUtils.isEmpty(response)) {
            return;
        }
        if (response.length() > 4000) {
            for (int i = 0; i < response.length(); i += 4000) {
                if (i + 4000 < response.length()) {
                    System.out.println(response.substring(i, i + 4000));
//                    Log.i("第" + i + "数据", response.substring(i, i + 4000));
                } else {
                    System.out.println(response.substring(i));
//                    Log.i("第" + i + "数据", response.substring(i, response.length()));
                }
            }
        } else {
            Log.i("全部数据", "************************ response = " + response);
        }
    }
}
