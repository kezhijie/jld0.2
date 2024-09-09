package com.sinpm.app.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.hjq.toast.ToastUtils;
import com.sinpm.app.R;
import com.sinpm.app.Utils.Constants;
import com.sinpm.app.Utils.HttpMethod;
import com.sinpm.app.Utils.HttpUtil;
import com.sinpm.app.Utils.LanguageUtil;
import com.sinpm.app.Utils.PropertiesUtils;
import com.sinpm.app.Utils.ServiceUtils;
import com.sinpm.app.Utils.SharedPreferencesUtil;
import com.sinpm.app.Utils.StringUtils;
import com.sinpm.app.base.API;
import com.sinpm.app.base.ActivityManager;
import com.sinpm.app.base.BaseActivityWithTask;
import com.sinpm.app.beans.CommandLogBean;
import com.sinpm.app.beans.InitBean;
import com.sinpm.app.beans.StatusBean;
import com.sinpm.app.beans.UpdateBean;
import com.sinpm.app.databinding.ActivityLoadingBinding;
import com.sinpm.app.services.LockService;
import com.tbruyelle.rxpermissions3.RxPermissions;
import com.thanosfisherman.wifiutils.WifiUtils;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class LoadingActivity extends BaseActivityWithTask {

    ActivityLoadingBinding binding;
    private Integer defaultTime = 20;
    private boolean turnTo = false;
    private String code;
    private Integer prgStatus=0;
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            loading();
            handler.postDelayed(this, 1000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoadingBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
        binding.layoutTop.imgBack.setVisibility(View.INVISIBLE);
        initClick();
    }

    @Override
    protected void netWorkReady(boolean isReady, int state) {
//        Log.d("//////////isReady",state+"----");
//        if (isReady && !turnTo){
//            turnTo = true;
//            Log.d("//////////offlineCheck","offlineCheck----");
//            init();
//        }
    }

    @Override
    protected void netWorkFail(int state) {

    }

    @Override
    protected void onDestroy() {
        this.unRegistWifiRecever();
        if (runnable!=null){
            handler.removeCallbacks(runnable);
        }
        super.onDestroy();

    }


    @Override
    public Object setTask() {
        return null;
    }

    private void loading() {
        if (prgStatus < 100) {
            prgStatus = prgStatus + 10;
            if (ActivityManager.isDebug(this)) {
                prgStatus = prgStatus + 10;
            }
        }

        // 使用 runOnUiThread 确保在主线程中更新 UI
        binding.pgb.setProgress(prgStatus);
        binding.txtProgress.setText("loading…… " + prgStatus + "%");

        if (prgStatus >= 100) {
            prgStatus = 100;
            binding.pgb.setProgress(prgStatus);
            binding.txtProgress.setText("loading…… " + prgStatus + "%");
            binding.txtProgress.setVisibility(View.GONE);
            binding.btnStart.setVisibility(View.VISIBLE);
            handler.removeCallbacks(runnable);
            getStatus();
//            offlineCheck(false);
        }
//
    }

    private void initClick() {
        binding.btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shouldExecute.set(true);

                offlineCheck(false);
            }
        });
        binding.layoutTop.set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSettings();
            }
        });
        binding.layoutTop.btnWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWifi();
            }
        });
    }

    private void init() {
        this.defaultTime = Integer.valueOf(PropertiesUtils.getValue(Constants.defaut_time, "20"));
        handler.postDelayed(runnable, 100);
        if (StringUtils.isEmpty(code) || !code.contains(Constants.modelCode)) {
            Map<String, Object> map = new HashMap<>();
            map.put("modelCode", Constants.modelCode);
            map.put("aid", getAndroidId());
            HttpUtil.request(this, API.Init, HttpMethod.GET,
                    map, InitBean.class, new HttpUtil.CallBackResult<InitBean>() {
                        @Override
                        public void success(InitBean initBean) {
                            code = initBean.getDeviceCode();
                            PropertiesUtils.setValue(Constants.LOCAL_CODE,initBean.getDeviceCode());
//                            switchActivity(intent, false);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    startService();
                                    prgStatus = 100;
                                    getStatus();
                                }
                            },500);
                        }

                        @Override
                        public void fail(String msg) {
                            if (!"设备编号不能为空".equals(msg)){
                                ToastUtils.show(msg);
                            }
                            init();
                            isNetworkConnected(msg);
                        }
                    });
            return;

        }
        getStatus();
        startService();
//        if (offlineCheck()) return;
    }
    private void startService(){
        if (!ServiceUtils.isServiceRunning(this, LockService.class)){
            startService(new Intent(this, LockService.class));
        }
    }

    //在线判断和离线判断同时进行，以在线的为准，离线场景：1超时没有跳转，2超时了点击启动按钮进行跳转
    private boolean offlineCheck(Boolean offline) {
        Intent intent = new Intent();
        //第一步判断是否未绑定
        if (Constants.FALSE.equals(PropertiesUtils.getValue(Constants.LOCAL_IS_BIND, Constants.FALSE))) {
            if (!isNetworkConnected()) {
                OnChangeWifiView(0);
                return false;
            }
            intent.setClass(this, LoginActivity.class);
            switchActivity(intent, false);
            return true;
        }


        Integer customerLogin = Integer.parseInt(PropertiesUtils.getValue(Constants.customer_login, "0"));

        Integer hasLock = Integer.parseInt(PropertiesUtils.getValue(Constants.has_lock, "1"));
        intent.putExtra("unlock",hasLock);
        //第二步如果需要顾客登录的情况
        if (customerLogin > 0) {

            intent.setClass(this, LoginActivity.class);
            switchActivity(intent, false);
            return true;
        }

        //第三步不需要顾客登录，并且无需解锁的
        if (hasLock > 0) {

            intent.setClass(this, PayActivity.class);

            switchActivity(intent, false);
            return true;
        }

        intent.setClass(this, ActivityWorking.class);

        intent.putExtra("useTime", this.defaultTime);
        if (offline){
            switchActivityOffline(intent);
        } else {
            switchActivity(intent, false);
        }
        return true;

    }

    private synchronized void switchActivityOffline(Intent intent) {
        startActivity(intent);
    }


    private synchronized void switchActivity(Intent intent, boolean checkUpdate) {
//        if (!shouldExecute.compareAndSet(true, false)) {
//            Log.e("=======code","shouldExecute");
//            return;
//        }
//        if(true){
//            return;
//        }
        LoadingActivity start = this;
        binding.btnStart.setVisibility(View.VISIBLE);
        handler.removeCallbacks(runnable);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("useTime", this.defaultTime);
//            = new Intent(start, activity);
        if (!checkUpdate) {
            if (LanguageUtil.getLocaleByLanguage().getLanguage().equals(Locale.ENGLISH.getLanguage())) {

                LanguageUtil.switchLanguage(start, Locale.ENGLISH, intent);
            } else {

//                    Intent intent = new Intent(start, activity);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                startActivity(intent);
                finish();
            }
        }
        Activity context = this;

        this.checkUpdate(this, code,false, new HttpUtil.CallBackResult<UpdateBean>() {
            @Override
            public void success(UpdateBean checkUpdate) {
                if (checkUpdate != null && checkUpdate.getUpgradeDTO() != null) {
                    return;
                }
                if (LanguageUtil.getLocaleByLanguage().getLanguage().equals(Locale.ENGLISH.getLanguage())) {
                    LanguageUtil.switchLanguage(start, Locale.ENGLISH, intent);
                } else {

//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void fail(String msg) {
                Log.e("=======code",msg);
            }
        });
    }


    public void commandExecute(CommandLogBean command, StatusBean statusBean) {
        Class cls = ActivityWorking.class;
        if (statusBean.getUnLockList() != null && statusBean.getUnLockList().size() > 0) {
            cls = ActivityWorking.class;
        }
        Intent intent = new Intent(this, cls);
        intent.putExtra("useTime", this.defaultTime);
        switchActivity(intent, true);
        if (command != null && command.getCommand() != null && !TextUtils.isEmpty(command.getCommand())) {
            if (Objects.equals(command.getCommand(), "SYSTEM_TEST")) {

                switchActivity(intent, true);
            }
            intent.setClass(this, LoginActivity.class);
            switchActivity(intent, false);
            return;
        }

        switchActivity(intent, true);

    }

    public Boolean checkCommand(StatusBean statusBean) {
        Activity activity = this;
        Map<String, Object> map = new HashMap<>();
        map.put("code", code);
        HttpUtil.request(this, API.checkMsg, HttpMethod.GET, map, CommandLogBean.class, new HttpUtil.CallBackResult<CommandLogBean>() {
            @Override
            public void success(CommandLogBean commandLogBean) {
                commandExecute(commandLogBean, statusBean);
            }

            @Override
            public void fail(String msg) {
                if (!"设备编号不能为空".equals(msg)){
                    ToastUtils.show(msg);
                }
//                switchActivity(new Intent(activity, LoginActivity.class), false);
            }
        });


        return false;
    }

    private void getStatus() {
        Map<String, Object> map = new HashMap<>();
        map.put("code", PropertiesUtils.getValue(Constants.LOCAL_CODE,""));
        LoadingActivity activity = this;
        HttpUtil.request(this, API.GET_STATUS, HttpMethod.GET,
                map, StatusBean.class, new HttpUtil.CallBackResult<StatusBean>() {
                    @Override
                    public void success(StatusBean data) {
                        PropertiesUtils.setValue(Constants.LOCAL_IS_BIND, String.valueOf(data.getIsBind()));
                        PropertiesUtils.setValue(Constants.debug,String.valueOf(data.getDebug()));
//                        PropertiesUtils.setValue(Constants.Qrcode,data.getBindQrcode());
                        PropertiesUtils.setValue(Constants.defaut_time,String.valueOf(data.getUseTime()));
                        activity.defaultTime = data.getUseTime();
                        if (data.getNeedActive() == 1 && !data.getIsBind()) {
                            switchActivity(new Intent(activity, LoginActivity.class), true);
                            return;
                        }
                        if (data.getStages() != 4) {
                            switchActivity(new Intent(activity, LoginActivity.class), true);
                            return;
                        }

                        PropertiesUtils.setValue(Constants.customer_login, String.valueOf(data.getCustomerLogin()));
                        //如果需要顾客登录的情况
                        if (data.getCustomerLogin() > 0) {
                            switchActivity(new Intent(activity, LoginActivity.class), true);
                            return;
                        }
                        //不需要顾客登录，并且无需解锁的
                        if (data.getUnLockList() == null || data.getUnLockList().size() == 0) {
                            PropertiesUtils.setValue(Constants.has_lock, "0");
                            checkCommand(data);
                            switchActivity(new Intent(activity, LoginActivity.class), true);

                            return;
                        }
                        if (data.getUnLockList().size() > 0) {
                            PropertiesUtils.setValue(Constants.has_lock, "1");
                            Intent intent = new Intent(activity, PayActivity.class);
                            intent.putExtra("unlock",1);
                            switchActivity(intent, true);

                        }


                    }

                    @Override
                    public void fail(String msg) {
//                        switchActivity(LoginActivity.class, false);
                        getStatus();
                        dialog.dismiss();
                    }
                }
        );

    }

    @Override
    protected void onResume() {
        super.onResume();
        times = 5;
        loadInit = true;
        requestPermission();
        String cmdStatus = "AA 78 B0 04 CC 33 C3 3C".trim().replaceAll(" ", "").substring(4, 8);
        Log.e("//////////",cmdStatus);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("/////////onPause",System.currentTimeMillis()+"");
//        EventBus.getDefault().unregister(this);
    }

    @SuppressLint("AutoDispose")
    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivity(intent);
            }else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    allowRequestPermission();
                }else {
                    dialog.show();
                    code =PropertiesUtils.getValue(Constants.LOCAL_CODE,"");
                    pingNet();
                }
            }
        }else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                allowRequestPermission();
            }else {
                dialog.show();
                code =PropertiesUtils.getValue(Constants.LOCAL_CODE,"");
                pingNet();
            }
        }
    }

    @SuppressLint("AutoDispose")
    private void allowRequestPermission(){
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions
                .requestEachCombined(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe(permission -> {
                    if (permission.granted) {
                        dialog.show();
                        code =PropertiesUtils.getValue(Constants.LOCAL_CODE,"");
                        pingNet();
                    } else if (permission.shouldShowRequestPermissionRationale) {
                        dialog.show();
                        code =PropertiesUtils.getValue(Constants.LOCAL_CODE,"");
                        pingNet();

                    } else {
                        ToastUtils.show("存储权限获取失败");
                        finish();
                    }
                });
    }
    private boolean loadInit = true;
    private  int times = 5;
    private void pingNet(){
        if (!isNetworkConnected() && loadInit){
            times--;
            if (times>0){
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pingNet();
                    }
                },1000);
            }else {
                dialog.dismiss();
                loadInit = false;
                showWifiDialog();
            }
        }else {
            dialog.dismiss();
            init();
        }
    }
    private void showWifiDialog(){
        SingleDialog curWifiTipDialog = new SingleDialog.Builder(this)
                .setTitle(getString(R.string.tip))
                .setContent(getString(R.string.sys_wifi))
                .setConfirm(view -> {
                    wifiTipDialog.dismiss();
                    openWifi();
                }).create();
        curWifiTipDialog.show();
    }

}