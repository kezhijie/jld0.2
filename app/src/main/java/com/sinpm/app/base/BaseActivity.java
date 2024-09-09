package com.sinpm.app.base;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.hjq.toast.ToastUtils;
import com.proembed.service.MyService;
import com.sinpm.app.R;
import com.sinpm.app.Utils.Constants;
import com.sinpm.app.Utils.CustomUpdateParser;
import com.sinpm.app.Utils.HttpMethod;
import com.sinpm.app.Utils.HttpUtil;
import com.sinpm.app.Utils.PropertiesUtils;
import com.sinpm.app.Utils.SharedPreferencesUtil;
import com.sinpm.app.Utils.UiUtils;
import com.sinpm.app.beans.InitBean;
import com.sinpm.app.beans.UpdateBean;
import com.sinpm.app.handle.CodeCallBack;
import com.sinpm.app.ui.ActivitySetting;
import com.sinpm.app.ui.ActivityWifi;
import com.sinpm.app.ui.ActivityWifiConnect;
import com.sinpm.app.ui.ChooseDialog;
import com.sinpm.app.ui.ActivityWorking;
import com.sinpm.app.ui.LoadingActivity;
import com.sinpm.app.ui.LoginActivity;
import com.sinpm.app.ui.PayActivity;
import com.sinpm.app.ui.SingleDialog;
import com.thanosfisherman.wifiutils.WifiUtils;
import com.xuexiang.xupdate.XUpdate;
import com.xuexiang.xupdate.utils.UpdateUtils;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import me.jessyan.autosize.internal.CustomAdapt;


/**
 * creat by yanmi  on 2021/9/2.
 * Describe:
 */
public abstract class BaseActivity extends AppCompatActivity implements CustomAdapt {
    protected Dialog dialog;
    protected String versionName;
    //    protected MyActivityLifecycleCallbacks lifecycleCallbacks;
    protected volatile AtomicBoolean shouldExecute = new AtomicBoolean(true);
    private Handler mHandler;
    private MyService mXService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 默认不弹出输入框
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        //拦截状态栏下拉滑动事件
        //preventStatusBarExpansion(this);
        create();
        dialog = UiUtils.showLoadingDialog(this);
        dialog.dismiss();
        ActivityManager.getInstance().putActivity(this.getClass().getSimpleName(), this);


        mXService = new MyService(this);
//        lifecycleCallbacks = new MyActivityLifecycleCallbacks();
//        getApplication().registerActivityLifecycleCallbacks(lifecycleCallbacks);

        setScreenBrightness(SharedPreferencesUtil.getData(this, Constants.app_bright, 100.f));
        String deviceName = Build.MODEL;
//        ToastUtils.show("设备编号:" + code+",设备名称:"+deviceName);
        try {
            versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


//        WifiUtils.withContext(getApplicationContext()).enableWifi();
        ImageView view = findViewById(getResources().getIdentifier("btn_wifi", "id", getPackageName()));
        if (view != null) {
            view.setOnClickListener(v -> {
                openWifi();
            });
        }
        initWifiDiaolog();
        if (!this.getClass().getSimpleName().contains("Wifi")) {

            registWifiReceiver();
        }
    }


    protected void openWifi() {
        Intent intent = new Intent(this, ActivityWifi.class);
        startActivity(intent);
    }


    protected void openWifisSy() {
        Intent intent = new Intent(this, ActivityWifi.class);
        startActivity(intent);
    }

    protected void openSettings() {
        Intent intent = new Intent(this, ActivitySetting.class);
        startActivity(intent);
    }

    protected void closeMain() {
        ActivityManager.finishActivity(ActivityWorking.class.getSimpleName());
        ActivityManager.finishActivity(this.getClass().getSimpleName());
    }

    @Override
    protected void onResume() {
        registWifiReceiver();
        super.onResume();
        if (!isNetworkConnected()){
            OnChangeWifiView(0);
        }
        setScreenBrightness(SharedPreferencesUtil.getData(this, Constants.app_bright, 100.f));
    }

    protected String getAndroidId() {
        return Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

    }

    @Override
    public void finish() {
//        if (this.getClass().getSimpleName().equals(LoginActivity.class.getSimpleName())) {
//            return;
//        }
        if (this.getClass().getSimpleName().equals(LoadingActivity.class.getSimpleName())) {
            return;
        }
        super.finish();
        // 设置转场动画，淡入淡出效果
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    protected void onPause() {

        unRegistWifiRecever();
        super.onPause();
    }

    @Override
    protected void onDestroy() {

        unRegistWifiRecever();
        super.onDestroy();
        ActivityManager.getInstance().removeActivity(this.getClass().getSimpleName());
    }

    protected void switchActivity(Class<?> activity) {
        if (shouldExecute.compareAndSet(true, false)) {
            Activity start = this;
            Intent intent = new Intent(start, activity);
            startActivity(intent);
        }

    }

    public void create() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        hideBottomUIMenu();
        requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);//去除标题栏


    }


    /**
     * 上传版本信息
     */
    private void uploadVersion() {

    }


    /**
     * 第一次打开设备，初始化设备编号
     */
    protected void initCode(CodeCallBack codeCallBack) {

        String code = PropertiesUtils.getValue(Constants.LOCAL_CODE,"");
        if (!TextUtils.isEmpty(code) && code.contains(Constants.modelCode)) {
            if (codeCallBack != null) {
                codeCallBack.getCode(code, "");
            }
            return;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("modelCode", Constants.modelCode);
        map.put("aid", getAndroidId());
        HttpUtil.request(API.Init, HttpMethod.GET, false, map, (HttpUtil.CallBack<InitBean>) data -> {
            PropertiesUtils.setValue(Constants.LOCAL_CODE, data.getDeviceCode());
            if (codeCallBack != null) {
                codeCallBack.getCode(data.getDeviceCode(), data.getUrl());
            }
        }, InitBean.class, false, dialog, this);

    }

    protected void hideBottomUIMenu() {
        //隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            android.view.Window _window = getWindow();
            WindowManager.LayoutParams params = _window.getAttributes();
            params.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE;
            _window.setAttributes(params);
        }
    }

    private static final int REQUEST_WRITE_SETTINGS_PERMISSION = 1;

    private void requestWriteSettingsPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, REQUEST_WRITE_SETTINGS_PERMISSION);
            } else {
                // 已经有权限，可以设置亮度
                setScreenBrightness(50); // 50 表示亮度百分比
            }
        }
    }

    public void setScreenBrightness(float level) {
        // 检查是否有权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(this)) {
                requestWriteSettingsPermission();
                return;
            }
        }

        // 计算亮度值
        float brightness = level / 100.0f;
        int brightnessMode = Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
        try {
            brightnessMode = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }

        // 设置亮度模式为手动
        if (brightnessMode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
            Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
        }

        // 设置亮度
        Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, (int) (255 * brightness));
    }


    private boolean mReceiverTag = false;   //广播接受者标识
    protected WifiCallBack wifiCallBack;


    /**
     * 动态注册WiFi监听广播
     */
    public void registWifiReceiver() {
        if (!mReceiverTag && wifiReceiver == null) {
            wifiReceiver = new WifiReceiver();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(WifiManager.RSSI_CHANGED_ACTION);
            intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
            intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
            registerReceiver(wifiReceiver, intentFilter);
            mReceiverTag = true;

        }
    }

    protected void unRegistWifiRecever() {
        if (mReceiverTag && wifiReceiver != null) {
            unregisterReceiver(wifiReceiver);
            mReceiverTag = false;
            wifiReceiver = null;
        }
    }

    protected void CheckMPushInit(String deviceCode) {
//        if(Boolean.FALSE.equals(BaseApplication.getInstance().getConnectFlag().getValue())){
//            BaseApplication.getInstance().initMpush(deviceCode);
//        }
    }


    protected WifiReceiver wifiReceiver;


    protected Boolean checkAndRequestPermissions() {
        // 检查应用是否已经被授予读写权限
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        // 如果权限已经被授予
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            // 可以进行文件的读写操作
            return true;
        } else {
            // 使用系统提供的权限请求对话框来请求权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 225);
            return false;
        }
    }

    protected abstract void netWorkReady(boolean isReady, int state);

    protected abstract void netWorkFail(int state);

    /**
     * wifi状态变化广播
     */
    public class WifiReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //wifi系统设置关闭
            WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

            WifiInfo wifiInfo = wifiManager.getConnectionInfo();

            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            boolean isReady = false;
            if (cm.getActiveNetworkInfo() != null){
                isReady = cm.getActiveNetworkInfo().isConnected();
            }
            int state = wifiInfo.getRssi();
            switch (intent.getAction()) {
                case WifiManager.RSSI_CHANGED_ACTION:
                    //信号强弱变化
                    checkWifiState(state);
                    netWorkReady(isReady, state);
                    break;
                case WifiManager.NETWORK_STATE_CHANGED_ACTION:
                    NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);

                    if (info.getState().equals(NetworkInfo.State.DISCONNECTED)) {
                        //WiFi断开
                        OnChangeWifiView(0);
                    } else if (info.getState().equals(NetworkInfo.State.CONNECTED)) {
                        //Wifi已连接
                        checkWifiState(state);
                        netWorkReady(isReady, state);
                    }
                    break;
                case WifiManager.WIFI_STATE_CHANGED_ACTION:
                    int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_DISABLED);
                    if (wifiState == WifiManager.WIFI_STATE_DISABLED) {
                        //wifi禁用
                        if (!ActivityManager.isDebug(context)) {
                            wifiManager.setWifiEnabled(true);
                        }
                    }
                    netWorkReady(isReady, state);
                    break;
            }
        }

    }

    /**
     * 判断wifi信号强弱
     */
    private void checkWifiState(int state) {

        OnChangeWifiView(state);

    }

    protected boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    protected boolean isNetworkConnected(String msg) {
        boolean isCheck = StringUtils.contains(msg, "网络连接失败");
        if (StringUtils.contains(msg, "获取服务器数据异常")) {
            isCheck = true;
        }
        if (StringUtils.contains(msg, "请求超时")) {
            isCheck = true;
        }
        if (StringUtils.contains(msg, "Unable to resolve host")) {

            isCheck = true;
        }

        if (StringUtils.contains(msg, "断开")) {
            isCheck = true;
        }
        if (!isCheck) {
            return false;
        }
        return isNetworkConnected();
    }


    protected SingleDialog wifiTipDialog;

    protected void initWifiDiaolog() {
        wifiTipDialog = new SingleDialog.Builder(this)
                .setTitle(getString(R.string.tip))
                .setContent(getString(R.string.sys_wifi))
                .setConfirm(view -> {
                    wifiTipDialog.dismiss();
                    openWifi();
                }).create();
    }

    protected void OnChangeWifiView(int i) {
        boolean data = SharedPreferencesUtil.getData(this, Constants.wifiSave, false);
        Activity activity = this;
        if (i == 0) {
            if (this.getClass().getSimpleName().equals(LoginActivity.class.getSimpleName())) {
                if (!data && !wifiTipDialog.isShowing() && !activity.isFinishing()) {
                    wifiTipDialog.show();
                }
            }
            //            if (this.getClass().getSimpleName().equals(LoadingActivity.class.getSimpleName()) || this.getClass().getSimpleName().equals(LoginActivity.class.getSimpleName())) {
//                if (!data && !wifiTipDialog.isShowing() && !activity.isFinishing()) {
//                    wifiTipDialog.show();
//                }
//            }
        } else {
            wifiTipDialog.dismiss();
            netWorkReady(true, 1);
        }

        ImageView imgWifi = findViewById(getResources().getIdentifier("img_wifi", "id", getPackageName()));

        if (imgWifi == null) {
            return;
        }

        if (i == 0) {
            if (wifiCallBack != null) {
                wifiCallBack.wifiError();
            }
            imgWifi.setImageResource(R.mipmap.wifi0);
            return;
        }
        if (wifiCallBack != null) {
            wifiCallBack.wifiSuccess();
        }
        if (i > -50) {
            imgWifi.setImageResource(R.mipmap.wifi4);
            return;
        }

        if (i > -60) {

            imgWifi.setImageResource(R.mipmap.wifi3);
            return;
        }
        if (i > -70) {
            imgWifi.setImageResource(R.mipmap.wifi2);
            return;
        } else {
            imgWifi.setImageResource(R.mipmap.wifi1);
        }

    }

    protected void checkUpdate(Context context, String deviceCode, Boolean force, HttpUtil.CallBackResult<UpdateBean> callBack) {
        dialog.show();
        Map<String, Object> map = new HashMap<>();
        map.put("code", deviceCode);
        map.put("version", UpdateUtils.getVersionName(this));
        map.put("force", force ? 1 : 0);
        HttpUtil.request(this, API.CheckUpdate, HttpMethod.GET,
                map, UpdateBean.class, new HttpUtil.CallBackResult<UpdateBean>() {
                    @Override
                    public void success(UpdateBean checkUpdate) {
                        dialog.dismiss();
                        if (checkUpdate == null || checkUpdate.getUpgradeDTO() == null) {
                            if (callBack != null) {
                                callBack.success(checkUpdate);
                            }
                            return;
                        }
//                        ActivityManager.finishWithOutActivity(context.getClass().getSimpleName());
                        XUpdate.newBuild(context)
                                .updateUrl(API.Base_PRD_URL + API.CheckUpdate + "?force=" + map.get("force") + "&code=" + deviceCode + "&version=" +
                                        UpdateUtils.getVersionName(context))
                                .updateParser(new CustomUpdateParser()) //设置自定义的版本更新解析器
                                .apkCacheDir(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download")

                                .update();

                    }

                    @Override
                    public void fail(String msg) {
                        dialog.dismiss();
                        ToastUtils.show(msg);
                    }
                });
        return;

    }

    @Override
    public void startActivity(Intent intent) {
        Set<String> notFinish = new HashSet<>();
        notFinish.add(ActivityWorking.class.getName());
        notFinish.add(LoginActivity.class.getName());
        notFinish.add(ActivityWifiConnect.class.getName());

        Set<String> target = new HashSet<>();
        target.add(ActivityWifi.class.getName());
        target.add(ActivitySetting.class.getName());

        // 如果当前 Activity 不在 notFinish 集合中并且目标 Activity 的类名也不在 target 集合中，则关闭当前 Activity
        if (!notFinish.contains(this.getClass().getName()) && !target.contains(intent.getComponent()==null ? intent.getClass().getName():intent.getComponent().getClassName())) {
            this.finish();
        }
        super.startActivity(intent);
        // 设置转场动画，淡入淡出效果
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

//        new Handler(Looper.getMainLooper()).post(() -> {
//            // 在主线程中启动新的 Activity
//            super.startActivity(intent);
//        });
//        runOnUiThread(() -> {
//            // 在主线程中启动新的 Activity
//            super.startActivity(intent);
//        });
    }

    @Override
    public boolean isBaseOnWidth() {
        return false;
    }

    @Override
    public float getSizeInDp() {
        // 数字越大，控件越小，边距越大，
        // 数字越小，完整性越好，边距越小
        if (ActivityManager.isDebug(this)) {
            return 380;
        }
        return 380;
    }
}
