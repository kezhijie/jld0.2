//package com.sinpm.app.ui;
//
//import android.Manifest;
//import android.app.Activity;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.content.res.Resources;
//import android.net.Uri;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.Environment;
//import android.provider.Settings;
//import android.text.TextUtils;
//import android.util.Log;
//import android.view.Gravity;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.PopupWindow;
//
//import androidx.annotation.NonNull;
//import androidx.core.app.ActivityCompat;
//import androidx.core.content.ContextCompat;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONObject;
//import com.bumptech.glide.Glide;
//import com.bumptech.glide.request.RequestOptions;
//import com.hjq.toast.ToastUtils;
//import com.sinpm.app.R;
//import com.sinpm.app.Utils.Constants;
//import com.sinpm.app.Utils.HttpMethod;
//import com.sinpm.app.Utils.HttpUtil;
//import com.sinpm.app.Utils.PreferencesUtils;
//import com.sinpm.app.Utils.PropertiesUtils;
//import com.sinpm.app.Utils.SharedPreferencesUtil;
//import com.sinpm.app.Utils.StringUtils;
//import com.sinpm.app.Utils.UiUtils;
//import com.sinpm.app.base.API;
//import com.sinpm.app.base.BaseActivityWithTask;
//import com.sinpm.app.beans.CommandLogBean;
//import com.sinpm.app.beans.DeviceUseLogBean;
//import com.sinpm.app.beans.GenerateResponseBean;
//import com.sinpm.app.beans.InitBean;
//import com.sinpm.app.beans.LoginCodeBean;
//import com.sinpm.app.beans.StatusBean;
//import com.sinpm.app.databinding.ActivityLoginBinding;
//import com.sinpm.app.databinding.TestPopLayoutBinding;
//
//import org.greenrobot.eventbus.Subscribe;
//import org.greenrobot.eventbus.ThreadMode;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Objects;
//
//public class LoginActivityCopy extends BaseActivityWithTask {
//    ActivityLoginBinding binding;
//    Integer cId;
//    RequestOptions requestOptions;
//    int REQUEST_CODE = 99;
//
//    String qrCodeUrl = "";
//    String customerUrl = "";
//    private boolean isLock = false;
//    private Boolean needSelectLockType;
//    Resources resources;
//    private String code;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        binding = ActivityLoginBinding.inflate(LayoutInflater.from(this));
//        setContentView(binding.getRoot());
//        dialog.show();
//        requestOptions = new RequestOptions()
//                .error(R.drawable.ic_erro_defalut)
//                .placeholder(R.drawable.loading_anim);
//        resources=getResources();
//
//        initView();
//        startTimer(this);
//        requestPermission();
//
//    }
//
//    @Override
//    protected void netWorkReady(boolean isReady, int state) {
//    }
//
//    @Override
//    protected void netWorkFail(int state) {
//
//    }
//
//    @Override
//    public Object setTask() {
//        getCommand();
//        if(!loading){
//            getStatus(null);
//        }
//        return null;
//    }
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onMessageEvent(String event) {
//        if ("command".equals(event)){
//            finish();
//        }
//    };
//
//    private void init() {
//        LoginActivityCopy activity = this;
//        startTimer(this);
//        if (StringUtils.isEmpty(code)) {
//            Log.e("======初始化code","111111");
//            Map<String, Object> map = new HashMap<>();
//            map.put("modelCode", Constants.modelCode);
//
//            HttpUtil.request(this, API.Init, HttpMethod.GET,
//                    map, InitBean.class, new HttpUtil.CallBackResult<InitBean>() {
//                        @Override
//                        public void success(InitBean initBean) {
//                            activity.code = initBean.getDeviceCode();
//                            PreferencesUtils.putString(LoginActivityCopy.this,Constants.LOCAL_CODE,code);
//                            getStatus(null);
//                        }
//
//                        @Override
//                        public void fail(String msg) {
//                            if (!"设备编号不能为空".equals(msg)){
//                                ToastUtils.show(msg);
//                            }
//                            isNetworkConnected(msg);
//                        }
//                    });
//            return;
//
//        }
//        getStatus(null);
//    }
//
//
//    private void requestPermission() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            // 先判断有没有权限
//            if (Environment.isExternalStorageManager()) {
//
//            } else {
//                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
//                intent.setData(Uri.parse("package:" + getPackageName()));
//                startActivity(intent);
//            }
//        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            // 先判断有没有权限
//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
//                    ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
//
//            } else {
//                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
//            }
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == REQUEST_CODE) {
//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
//                    ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
//            } else {
//                ToastUtils.show("存储权限获取失败");
//            }
//        }
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        loading = false;
//        code = PreferencesUtils.getString(this,Constants.LOCAL_CODE,"");
////        Log.e("===========qr",PreferencesUtils.getString(this,Constants.Qrcode));
////        if (!TextUtils.isEmpty(Constants.Qrcode)){
////            setBindView(PreferencesUtils.getString(this,Constants.Qrcode));
////        }
////        ActivityManager.finishActivity(MainActivity.class.getSimpleName());
////        uploadUseStopTime();
//        startTimer(this);
//        if (SharedPreferencesUtil.getData(LoginActivityCopy.this, Constants.wifiSave, true)){
//            init();
//        }
////        if (isNetworkConnected()){
////            init();
////        }
//    }
//
//    @Override
//    protected void onPause() {
////        EventBus.getDefault().unregister(this);
//        super.onPause();
//    }
//
//
//    @Override
//    protected void onDestroy() {
//        this.unRegistWifiRecever();
//        super.onDestroy();
//    }
//
//
//
//    private synchronized void initData() {
//        getStatus(null);
//        if (isLock()) {
////            binding.scanTv.setText(resources.getText(R.string.lock));
//            ToastUtils.show(resources.getText(R.string.lock));
////            CheckMPushInit(code);
//            Glide.with(this)
//                    .clear(binding.qr);
//            Glide.with(LoginActivityCopy.this)
//                    .load(R.drawable.qr_lock)
//                    .into(binding.qr);
//            return;
//        }
//        setCustomerLogin(null);
//        if (TextUtils.isEmpty(code) || !code.contains(Constants.modelCode)) {
//            Log.e("======初始化","111111");
//            initCode((code, qrCodeUrl) -> setBindView(qrCodeUrl));
//        }
//
//    }
//
//
//    private void setBindView(String qrCode) {
//
//        runOnUiThread(() -> {
//            dialog.dismiss();
//            this.qrCodeUrl = qrCode;
//            binding.llActive.setVisibility(View.VISIBLE);
//            binding.llCustomer.setVisibility(View.GONE);
//            binding.llLock.setVisibility(View.GONE);
//            binding.llRefresh.setVisibility(View.GONE);
//            Glide.with(LoginActivityCopy.this)
//                    .applyDefaultRequestOptions(requestOptions)
//                    .load(qrCode)
//                    .into(binding.qr);
//        });
//
//
//    }
//
//    private void setCustomerLogin(String qrCodeUrl) {
//        runOnUiThread(() -> {
//
//            binding.layoutTop.test.setVisibility(View.INVISIBLE);
//            this.customerUrl = qrCodeUrl;
//            binding.llActive.setVisibility(View.GONE);
//            binding.llLock.setVisibility(View.GONE);
//            binding.llCustomer.setVisibility(View.VISIBLE);
//            binding.llRefresh.setVisibility(View.GONE);
//
//            Glide.with(LoginActivityCopy.this)
//                    .applyDefaultRequestOptions(requestOptions)
//                    .load(qrCodeUrl)
//                    .into(binding.qrCustomer);
//        });
//
//    }
//
//    private void refreshActiveView(ImageView imageView, String url) {
//        LoginActivityCopy loginActivity = this;
//
//        Glide.with(loginActivity)
//                .applyDefaultRequestOptions(requestOptions)
//                .load(url)
//                .into(imageView);
//
//
//    }
//
//    private void initView() {
////        Glide.with(this)
////                .applyDefaultRequestOptions(requestOptions)
////                .load(R.mipmap.bg_main)
////                .into(binding.bg);
//        binding.layoutTop.imgBack.setVisibility(View.INVISIBLE);
//        binding.layoutTop.test.setOnClickListener(view -> showTestPop());
////        binding.backGround.setOnClickListener(v -> {
////
//////            ToastUtils.show("111");
////        });
//        binding.rlRefresh.setOnClickListener(v -> {
//            refreshActiveView(binding.imgRefresh, this.qrCodeUrl);
//            getStatus(null);
//        });
//        binding.qr.setOnClickListener(view -> {
//            if (isLock()) {
//                ToastUtils.show(resources.getText(R.string.lock));
//                return;
//            }
//            if (StringUtils.isEmpty(this.qrCodeUrl)) {
//                Map<String, Object> map = new HashMap<>();
//                map.put("dCode", code);
//                HttpUtil.request(this, API.BIND_QR, HttpMethod.GET,
//                        map, GenerateResponseBean.class, new HttpUtil.CallBackResult<GenerateResponseBean>() {
//                            @Override
//                            public void success(GenerateResponseBean data) {
//                                LoginActivityCopy.this.qrCodeUrl = data.getUrl();
//                                refreshActiveView(binding.qr, data.getUrl());
//                            }
//
//                            @Override
//                            public void fail(String msg) {
//
//                                isNetworkConnected(msg);
//                            }
//                        });
//                return;
//            }
//            refreshActiveView(binding.qr, this.qrCodeUrl);
//        });
//        binding.qrCustomer.setOnClickListener(view -> {
//            if (isLock()) {
//                ToastUtils.show(resources.getText(R.string.lock));
//                return;
//            }
//            if (StringUtils.isEmpty(this.customerUrl)) {
//                Map<String, Object> map = new HashMap<>();
//                map.put("code", code);
//                HttpUtil.request(API.LoginCode, HttpMethod.GET, false, map, (HttpUtil.CallBack<LoginCodeBean>) data -> {
//                    this.customerUrl = data.getUrl();
//                    cId = data.getCustomerId();
//                    setCustomerLogin(data.getUrl());
//                }, LoginCodeBean.class, false, dialog, this);
//                return;
//            }
//            refreshActiveView(binding.qrCustomer, this.customerUrl);
//        });
//        binding.layoutTop.btnWifi.setOnClickListener(view -> {
//            UiUtils.startAnimator(view);
////            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
//            openWifi();
//        });
//        binding.layoutTop.set.setOnClickListener(view -> {
//            UiUtils.startAnimator(view);
//            Intent intent = new Intent(LoginActivityCopy.this, ActivitySetting.class);
//            intent.putExtra("from", 0);
//            startActivity(intent);
//        });
//        binding.noUserLogin.setOnClickListener(view -> {
//            Class finalCls = PayActivity.class;
//            Intent intent = new Intent(this,finalCls);
//            intent.putExtra("defaultTime", defaultTime);
//            intent.putExtra("fromLogin", 1);
//            startActivity(intent);
//        });
////        binding.netRl.setOnClickListener(view -> {
////            if (BaseApplication.getInstance().getConnectFlag().getValue()) {
////                ToastUtils.show("已连接服务器");
////            } else {
////                ToastUtils.show("设备已经离线，正在重连");
////                CheckMPushInit(code);
////            }
////        });
//
////        BaseApplication.getInstance().getConnectFlag().observe(this, new Observer<Boolean>() {
////            @Override
////            public void onChanged(Boolean aBoolean) {
////                Glide.with(LoginActivity.this)
////                        .load(aBoolean ? R.mipmap.connect : R.mipmap.disconnect)
////                        .into(binding.netIv);
//////                binding.netState.setText(aBoolean?"在线":"离线");
////            }
////        });
//    }
//
//    PopupWindow popupWindow;
//    TestPopLayoutBinding testPopLayoutBinding;
//
//    private void showTestPop() {
//        if (popupWindow != null && popupWindow.isShowing()) {
//            return;
//        }
//        testPopLayoutBinding = TestPopLayoutBinding.inflate(LayoutInflater.from(this));
//        popupWindow = new PopupWindow(testPopLayoutBinding.getRoot(), ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        testPopLayoutBinding.popBg.setOnClickListener(view -> dismissPop());
//        testPopLayoutBinding.qr.setOnClickListener(view -> {
//            Glide.with(this)
//                    .clear(testPopLayoutBinding.qr);
//            Glide.with(this)
//                    .applyDefaultRequestOptions(requestOptions)
//                    .load(testQrUrl)
//                    .into(testPopLayoutBinding.qr);
//        });
//        Map<String, Object> map = new HashMap<>();
//        map.put("code", code);
//
//        Activity activity = this;
//        if (!TextUtils.isEmpty(testQrUrl)) {
//            Glide.with(activity)
//                    .applyDefaultRequestOptions(requestOptions)
//                    .load(testQrUrl)
//                    .into(testPopLayoutBinding.qr);
//
//
//        } else {
//            HttpUtil.request(API.TEST_QRCODE, HttpMethod.GET, false, map, new HttpUtil.CallBack<InitBean>() {
//                @Override
//                public void setResult(InitBean data) {
//                    testQrUrl = data.getUrl();
//                    Glide.with(activity)
//                            .applyDefaultRequestOptions(requestOptions)
//                            .load(testQrUrl)
//                            .into(testPopLayoutBinding.qr);
//
//                }
//            }, InitBean.class, false, null, this);
//        }
//
//        popupWindow.showAtLocation(getWindow().getDecorView(), Gravity.CENTER, 0, 0);
//    }
//
//    private void dismissPop() {
//        if (popupWindow != null && popupWindow.isShowing()) {
//            popupWindow.dismiss();
//        }
//    }
//
//    /**
//     * 获取顾客扫码登陆二维码
//     */
//    private void getLoginCode(Integer customerLogin) {
//        //支持游客登录
//        if (customerLogin == 2) {
//            binding.noUserLogin.setVisibility(View.VISIBLE);
//        }
//        Map<String, Object> map = new HashMap<>();
//        map.put("code", code);
//        if (!TextUtils.isEmpty(this.customerUrl)) {
//            setCustomerLogin(this.customerUrl);
//            return;
//        }
//        HttpUtil.request(API.LoginCode, HttpMethod.GET, false, map, (HttpUtil.CallBack<LoginCodeBean>) data -> {
////            this.customerUrl = data.getUrl();
//
//            cId = data.getCustomerId();
//            setCustomerLogin(data.getUrl());
//
//        }, LoginCodeBean.class, false, dialog, this);
//
//    }
//
//    private void uploadUseStopTime() {
//        String userStr = (String) SharedPreferencesUtil.getData(this, Constants.usetime, "");
//        DeviceUseLogBean deviceUseLogBean;
//        if (TextUtils.isEmpty(userStr)) {
//            return;
//        } else {
//            deviceUseLogBean = JSON.parseObject(userStr, DeviceUseLogBean.class);
//            if (deviceUseLogBean.getUpload()) {
//                return;
//            }
//            if (deviceUseLogBean.getType() == 1) {
//                deviceUseLogBean.setType(0);
//            }
//            if (deviceUseLogBean.getTotalDuration() == 0 && deviceUseLogBean.getStartTime() != null) {
//                deviceUseLogBean.setTotalDuration((int) ((System.currentTimeMillis() - deviceUseLogBean.getStartTime()) / 1000));
//            }
//        }
//        //uploadPush(deviceUseLogBean);
//    }
//
//    private void uploadPush(DeviceUseLogBean deviceUseLogBean) {
//        Map<String, Object> map = new HashMap<>();
//        if (Objects.isNull(deviceUseLogBean.getId())) {
//            return;
//        }
//        map.put("logId", deviceUseLogBean.getId());
//        map.put("code", deviceUseLogBean.getCode());
//        map.put("duration", deviceUseLogBean.getDuration());
//        map.put("totalDuration", deviceUseLogBean.getTotalDuration());
//        map.put("type", deviceUseLogBean.getType());
//        map.put("unlockType", deviceUseLogBean.getUnlockType());
//        if (deviceUseLogBean.getCmdId() != null && deviceUseLogBean.getCmdId() != 0) {
//            map.put("cmdId", deviceUseLogBean.getCmdId());
//        }
//        if (deviceUseLogBean.getcId() != 0) {
//            map.put("cid", deviceUseLogBean.getcId());
//        }
//        if (deviceUseLogBean.getDetailList() != null && deviceUseLogBean.getDetailList().size() > 0) {
//            map.put("detailList", deviceUseLogBean.getDetailList());
//        }
//    }
//
//
//    private String testQrUrl = "";
//
//    private void getTestQr() {
//        Map<String, Object> map = new HashMap<>();
//        map.put("code", code);
////        HttpUtil.request(API.TEST_QRCODE, HttpMethod.GET, false, map, new HttpUtil.CallBack<InitBean>() {
////            @Override
////            public void setResult(InitBean data) {
////                testQrUrl = data.getUrl();
////            }
////        }, InitBean.class, false, null, this);
//
//
//        HttpUtil.request(this, API.TEST_QRCODE, HttpMethod.GET,
//                map, InitBean.class, new HttpUtil.CallBackResult<InitBean>() {
//                    @Override
//                    public void success(InitBean data) {
//                        testQrUrl = data.getUrl();
//                    }
//
//                    @Override
//                    public void fail(String msg) {
//
//                        isNetworkConnected(msg);
//                    }
//                }
//        );
//
//    }
//
//    Integer defaultTime = 0;
//    private boolean loading=false;
//    private void getStatus(HttpUtil.CallBackResult<String> callBackResult) {
//        Map<String, Object> map = new HashMap<>();
//        map.put("code", code);
//        LoginActivityCopy activity=this;
//        Log.e("=======","GET_STATUS:"+code);
//        HttpUtil.request(this, API.GET_STATUS, HttpMethod.GET,
//                map, StatusBean.class, new HttpUtil.CallBackResult<StatusBean>() {
//                    @Override
//                    public void success(StatusBean data) {
//                        Log.e("=======",data.getStages()+"");
//                        loading=true;
//                        defaultTime = data.getUseTime();
//
//                        if (data.getNeedActive() == 1 && !data.getIsBind()) {
//                            setBindView(data.getBindQrcode());
//                            if (data.getRemainingCount() > 0) {//如果测试次数大于0
//                                binding.layoutTop.test.setVisibility(View.VISIBLE);
//                                getTestQr();
//                            } else {
//                                binding.layoutTop.test.setVisibility(View.INVISIBLE);
//                            }
//                            return;
//                        }
//                        switch (data.getStages()) {
//                            case 1:
//                                setBindView(data.getBindQrcode());
//                                break;
//                            case 4:
//
//                                Class cls = ActivityWorking.class;
//
//                                Intent intent = new Intent(LoginActivityCopy.this, cls);
//                                needSelectLockType = false;
//                                intent.putExtra("unlock",0);
//                                if (data.getUnLockList() != null && data.getUnLockList().size() != 0) {
//                                    needSelectLockType = true;
//                                    cls = PayActivity.class;
//                                    intent.setClass(activity,PayActivity.class);
//                                    intent.putExtra("unlock",1);
//
//                                }
//                                Class finalCls = cls;
//                                binding.noUserLogin.setOnClickListener(view -> {
//                                    intent.putExtra("defaultTime", defaultTime);
//                                    intent.putExtra("fromLogin", 1);
//                                    startActivity(intent);
//                                });
//
//                                PropertiesUtils.setValue(Constants.LOCAL_IS_BIND, Constants.TRUE);
//
//                                if (data.getCustomerLogin() > 0) {
//                                    getLoginCode(data.getCustomerLogin());
//                                    break;
//                                }
//
//                                if (callBackResult != null) {
//                                    callBackResult.success("");
//                                }
//                                intent.putExtra("defaultTime", defaultTime);
//                                intent.putExtra("fromLogin", 1);
//                                startActivity(intent);
//
//                                break;
//                            case 6:
//                                lock();
//                                break;
//                            case 8:
//                                reset();
//                                break;
//                        }
//
//                    }
//
//                    @Override
//                    public void fail(String msg) {
//                        Log.e("=======","GET_STATUS:"+msg);
//                        String value = PropertiesUtils.getValue(Constants.LOCAL_IS_BIND, Constants.FALSE);
//                        if (value.equals(Constants.TRUE)) {
//                            Integer customerLogin = Integer.parseInt(PropertiesUtils.getValue(Constants.customer_login, "0"));
//
//                            Integer hasLock = Integer.parseInt(PropertiesUtils.getValue(Constants.has_lock, "1"));
//                            //第二步如果需要顾客登录的情况
//                            if (customerLogin > 0) {
//                                setCustomerLogin(null);
//                            }
//
//                        }
//
////                        isNetworkConnected(msg);
////                        ToastUtils.show("网络连接失败,请检查网络连接状态");
//                    }
//                }
//        );
//    }
//
//    private boolean isActivityValid() {
//        return true;
////        return handler != null && handler.getReference() != null && handler.getReference().get() != null;
//    }
//
//    public void getCommand() {
//        if (!isActivityValid()) {
//            return;
//        }
//        Map<String, Object> map = new HashMap<>();
//        map.put("code", code);
//        LoginActivityCopy loginActivity = this;
//        HttpUtil.request(this, API.GET_STATUS, HttpMethod.GET, map, StatusBean.class, new HttpUtil.CallBackResult<StatusBean>() {
//            @Override
//            public void success(StatusBean statusBean) {
//                loginActivity.needSelectLockType = statusBean.getUnLockList() != null && statusBean.getUnLockList().size() > 0;
//                HttpUtil.request(loginActivity, API.checkMsg, HttpMethod.GET, map, CommandLogBean.class, new HttpUtil.CallBackResult<CommandLogBean>() {
//                    @Override
//                    public void success(CommandLogBean commandLogBean) {
//                        commandExecute(commandLogBean, loginActivity);
//                    }
//
//                    @Override
//                    public void fail(String msg) {
//
//                        isNetworkConnected(msg);
//                    }
//                });
//
//
//            }
//
//            @Override
//            public void fail(String msg) {
//
//            }
//        });
//
//
//    }
//
//    private void showLock() {
//        binding.llLock.setVisibility(View.VISIBLE);
//        binding.llCustomer.setVisibility(View.GONE);
//        binding.llActive.setVisibility(View.GONE);
//
//        binding.llRefresh.setVisibility(View.GONE);
//    }
//
//    public void commandExecute(CommandLogBean command, Activity activity) {
//        if (command == null) {
//            return;
//        }
//        Log.e("commandExecute", command.toString());
//        Class cls = ActivityWorking.class;
//        if (needSelectLockType != null && needSelectLockType) {
//            cls = PayActivity.class;
//        }
//        Intent mainIntent = new Intent(activity, cls);
//        mainIntent.putExtra("fromLogin", 1);
//        Boolean switchActivity = false;
//        switch (command.getCommand()) {
////            扫码绑定
//            case "SYSTEM_DEVICE_BIND":
//                if (isLock()) {
//                    showLock();
//                    ToastUtils.show(resources.getText(R.string.lock));
//                    return;
//                }
//                mainIntent.putExtra("userinfo", command.getContent());
//                mainIntent.putExtra("cid", cId);
//                mainIntent.putExtra("defaultTime", defaultTime);
//                switchActivity = false;
//                PropertiesUtils.setValue(Constants.LOCAL_IS_BIND, Constants.TRUE);
//                getStatus(null);
//                break;
////                扫码登录
//            case "SYSTEM_DEVICE_USER_AUTHORIZED":
//
//                if (isLock()) {
//                    ToastUtils.show(resources.getText(R.string.lock));
//                    return;
//                }
//                mainIntent.putExtra("userinfo", command.getContent());
//                mainIntent.putExtra("cid", cId);
//                mainIntent.putExtra("defaultTime", defaultTime);
//                switchActivity = true;
//                break;
//            //解锁测试
//            case "SYSTEM_DEVICE_TEST_UNLOCK":
//                if (isLock()) {
//                    ToastUtils.show(resources.getText(R.string.lock));
//                    return;
//                }
//                JSONObject data = JSON.parseObject(command.getContent());
//                Integer time = 5;
//                if (data.getJSONObject("data") != null && data.getJSONObject("data").getInteger("duration") != null) {
//                    time = data.getJSONObject("data").getInteger("duration");
//                }
//                Intent working = new Intent(activity, ActivityWorking.class);
//                working.putExtra("cid", cId);
//                working.putExtra("useTime", time);
//                working.putExtra("commandId", command.getCommandId().intValue());
//                working.putExtra("isTest", true);
//                startActivity(working);
//                dismissPop();
//                switchActivity = false;
//                break;
////                设备重置，恢复出厂设置，删除本地文件，然后重启
//            case "SYSTEM_DEVICE_RESET":
//                reset();
//                break;
////                设备锁定
//            case "SYSTEM_DEVICE_LOCK":
//                lock();
//                break;
////                设备解除锁定
//            case "SYSTEM_DEVICE_UN_LOCK":
//                isLock = false;
//                initData();
////                lock();
//                break;
//        }
////
//        Map<String, Object> map = new HashMap<>();
//        map.put("commandId", command.getCommandId());
//        //处理完毕之后修改状态
//
//
//        Boolean finalSwitchActivity = switchActivity;
//        HttpUtil.request(this, API.changeCommandState, HttpMethod.GET, map, Boolean.class, new HttpUtil.CallBackResult<Boolean>() {
//            @Override
//            public void success(Boolean aBoolean) {
//                if (finalSwitchActivity) {
//                    stopTimer();
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            startActivity(mainIntent);
//                        }
//                    });
//                }
//            }
//
//            @Override
//            public void fail(String msg) {
//
//                isNetworkConnected(msg);
//            }
//        });
//
//
//    }
//
//    /**
//     * 重置
//     */
//    private void reset() {
////        PropertiesUtils.cleanProFile();
////        ActivityManager.finishAllActivity();
////        Intent intent1 = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
////        intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
////        startActivity(intent1);
//        PropertiesUtils.setValue(Constants.LOCAL_IS_BIND, Constants.FALSE);
//        Map<String, Object> map = new HashMap<>();
//        map.put("dCode", code);
//        HttpUtil.request(this, API.BIND_QR, HttpMethod.GET,
//                map, GenerateResponseBean.class, new HttpUtil.CallBackResult<GenerateResponseBean>() {
//                    @Override
//                    public void success(GenerateResponseBean data) {
//                        setBindView(data.getUrl());
//                    }
//
//                    @Override
//                    public void fail(String msg) {
//
//                        isNetworkConnected(msg);
//                    }
//                });
//    }
//
//
//    /**
//     * 设备锁定
//     */
//    private void lock() {
//        LoginActivityCopy activity = this;
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
////                binding.scanTv.setText("设备已锁定");
////                Glide.with(activity)
////                        .clear(binding.qr);
////                Glide.with(LoginActivity.this)
////                        .load(R.drawable.ic_erro_defalut)
////                        .into(binding.qr);
//////                binding.noUserLogin.setVisibility(View.GONE);
////                activity.isLock = true;
//                showLock();
//            }
//        });
//
//    }
//
//    private Boolean isLock() {
//        if (this.isLock) {
//            showLock();
//        }
//        return this.isLock;
//
//    }
//
//    private ChooseDialog chooseDialog;
//
//}