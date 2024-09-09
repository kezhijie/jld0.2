package com.sinpm.app.services;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.hjq.toast.ToastUtils;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.HttpParams;
import com.sinpm.app.R;
import com.sinpm.app.Utils.Constants;
import com.sinpm.app.Utils.HttpMethod;
import com.sinpm.app.Utils.HttpUtil;
import com.sinpm.app.Utils.OkGoUtils;
import com.sinpm.app.Utils.PropertiesUtils;
import com.sinpm.app.base.API;
import com.sinpm.app.base.BaseActivity;
import com.sinpm.app.base.BaseApplication;
import com.sinpm.app.beans.CommandLogBean;
import com.sinpm.app.beans.CommandStatusBean;
import com.sinpm.app.beans.EventBean;
import com.sinpm.app.beans.LockStatusBean;
import com.sinpm.app.beans.StatusBean;
import com.sinpm.app.ui.ActivityWorking;
import com.sinpm.app.ui.LoadingActivity;
import com.sinpm.app.ui.LockActivity;
import com.sinpm.app.ui.LoginActivity;
import com.sinpm.app.ui.PayActivity;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LockService extends Service {
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            getCommand();
            handler.postDelayed(this, 1500);
        }
    };
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        handler.postDelayed(runnable, 100);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (handler!=null){
            handler.removeCallbacks(runnable);
        }
    }

    public void getCommand() {
        String code = PropertiesUtils.getValue(Constants.LOCAL_CODE,"");
        if (!TextUtils.isEmpty(code)){
            HttpParams map = new HttpParams();
            map.put("code", code);
            String url = API.Base_URL + API.GET_STATUS;
            OkGoUtils.getParams(url, map, new StringCallback() {
                @Override
                public void onSuccess(com.lzy.okgo.model.Response<String> response) {
                    LockStatusBean statusBean = new Gson().fromJson(response.body(),LockStatusBean.class);
                    EventBus.getDefault().post(new EventBean("command",statusBean));
                    if (statusBean.getCode().equals("1")){
                        return;
                    }
                    if (statusBean.getData().getUnLockList()!=null && statusBean.getData().getUnLockList().size()>0){
                        String checkUrl = API.Base_URL +API.checkMsg;
                        OkGoUtils.getParams(checkUrl, map, new StringCallback() {
                            @Override
                            public void onSuccess(com.lzy.okgo.model.Response<String> response) {
                                CommandStatusBean commandLogBean = new Gson().fromJson(response.body(),CommandStatusBean.class);
                                EventBus.getDefault().post(new EventBean("check_msg",commandLogBean));
                                commandExecute(commandLogBean,code);
                            }

                            @Override
                            public void onError(com.lzy.okgo.model.Response<String> response) {
                                super.onError(response);
                                EventBus.getDefault().post(new EventBean("net_fail",response.body()));
                            }
                        });
                    }
                }

                @Override
                public void onError(com.lzy.okgo.model.Response<String> response) {
                    super.onError(response);
                    EventBus.getDefault().post(new EventBean("net_fail",response.body()));
//                MToastUtils.ShortToast("网络异常");
                }
            });
        }

    }

    public void commandExecute(CommandStatusBean command,String code) {
        if (command == null) {
            return;
        }
        if (command.getData() == null || command.getData().getCommand() == null) {
            return;
        }
        Log.e("commandExecute", command.toString());
        switch (command.getData().getCommand()) {
            case "SYSTEM_DEVICE_RESET":
//                reset();
                break;
//                设备锁定
            case "SYSTEM_DEVICE_LOCK":
                if ((!"com.sinpm.app.ui.WorkingActivity".equals(getCurrentActivity(this))) &&
                        (!"com.sinpm.app.ui.LoginActivity".equals(getCurrentActivity(this)))){
                    Intent intent = new Intent(this, LockActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
                break;
//                设备解除锁定
            case "SYSTEM_DEVICE_UN_LOCK":
//                isLock = false;
//                initData();
//                lock();
                EventBus.getDefault().post(new EventBean("finish",null));
                break;
        }
//
        HttpParams map = new HttpParams();
        map.put("commandId", command.getData().getCommandId());
        //处理完毕之后修改状态
        String checkUrl = API.Base_URL +API.changeCommandState;
        OkGoUtils.getParams(checkUrl, map, new StringCallback() {
            @Override
            public void onSuccess(com.lzy.okgo.model.Response<String> response) {
            }

            @Override
            public void onError(com.lzy.okgo.model.Response<String> response) {
                super.onError(response);
            }
        });
    }
    private String getCurrentActivity(Context context) {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskInfo = activityManager.getRunningTasks(1);
        String currentActivity = taskInfo.get(0).topActivity.getShortClassName();
        return currentActivity;
    }
}
