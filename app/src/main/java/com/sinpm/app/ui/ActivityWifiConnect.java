package com.sinpm.app.ui;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.hjq.toast.ToastUtils;
import com.proembed.service.MyService;
import com.sinpm.app.R;
import com.sinpm.app.Utils.Constants;
import com.sinpm.app.Utils.PropertiesUtils;
import com.sinpm.app.Utils.SharedPreferencesUtil;
import com.sinpm.app.Utils.UiUtils;
import com.sinpm.app.base.ActivityManager;
import com.sinpm.app.base.BaseActivity;
import com.sinpm.app.databinding.ActivityWifiConnectBinding;
import com.thanosfisherman.wifiutils.WifiUtils;
import com.thanosfisherman.wifiutils.wifiConnect.ConnectionErrorCode;
import com.thanosfisherman.wifiutils.wifiConnect.ConnectionSuccessListener;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityWifiConnect extends BaseActivity {

    private ActivityWifiConnectBinding binding;
    private String ssid;

    WifiManager wifiManager;

    MyService mXService;
    protected Dialog dialogWifi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWifiConnectBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        ssid = getIntent().getStringExtra("ssid");
        if (StringUtils.isNoneBlank(ssid)) {

            binding.txtWifiSsid.setText(String.format(getResources().getString(R.string.input_pwd), ssid));
        }
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        requestLocationPermission();
        init();
        mXService = new MyService(this);
    }

    private Map<String, String> ssIdMap = new HashMap<String, String>() {{
        put("BTWIFI-01C2", "sscz090619");
        put("TP-LINK_BAOBAO", "lxn12345678");
    }};
    private static final int PERMISSION_REQUEST_CODE = 1;

    private void init() {
//        binding.btnBack.setOnClickListener(v -> {
//            this.finish();
//        });

        dialogWifi = UiUtils.showLoadingDialog(this, R.string.msg_connecting);
        binding.layoutTop.imgBack.setVisibility(View.INVISIBLE);
        binding.layoutTop.btnWifi.setVisibility(View.INVISIBLE);
        binding.layoutTop.set.setVisibility(View.INVISIBLE);
        binding.confirm.setOnClickListener(view -> {

            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(binding.txtPassword.getWindowToken(), 0);

            binding.txtPassword.clearFocus();

            WifiConfiguration wifiConfig = new WifiConfiguration();

            // 设置要连接的网络的SSID和密码
            wifiConfig.SSID = ssid;
            wifiConfig.preSharedKey = binding.txtPassword.getText().toString();
            if (ActivityManager.isDebug(this) && ssIdMap.containsKey(ssid)) {
                wifiConfig.preSharedKey = ssIdMap.get(ssid);
            }
            dialogWifi.show();
            ;
            Context context = this;
            WifiUtils.withContext(this)
                    .connectWith(wifiConfig.SSID, wifiConfig.preSharedKey)
                    .onConnectionResult(new ConnectionSuccessListener() {
                        @Override
                        public void success() {
//                            ToastUtils.show("连接成功");
                            WifiUtils.enableLog(true);
                            dialogWifi.dismiss();
                            WifiConfiguration currentWifiConfig = wifiConfig;
                            if (currentWifiConfig != null) {
                                wifiManager.updateNetwork(currentWifiConfig);
                                wifiManager.saveConfiguration();
                            }

                            mXService.setWifiStaDhcp_Open(ssid, wifiConfig.preSharedKey);
                            if (!TextUtils.isEmpty(PropertiesUtils.getValue(Constants.WIFISSID,""))){
                                String arraySsid = PropertiesUtils.getValue(Constants.WIFISSID, "");
                                if (!arraySsid.contains(ssid)){
                                    PropertiesUtils.setValue(Constants.WIFISSID,arraySsid+","+ssid);
                                }
                            }else {
                                PropertiesUtils.setValue(Constants.WIFISSID,ssid);
                            }
                            SharedPreferencesUtil.saveData(context, Constants.wifiSave, true);
                            finish();

//                            new Thread(() -> {
//                                try {
//                                    Thread.sleep(2000);
//                                    finish();
//                                } catch (InterruptedException e) {
//                                    e.printStackTrace();
//                                }
//                            }).start();

                        }

                        @Override
                        public void failed(@NonNull ConnectionErrorCode errorCode) {
                            String msg = "连接失败,请检查密码是否正确或者网络是否通畅" + errorCode;
                            dialogWifi.dismiss();
                            ToastUtils.show(msg);
                            SharedPreferencesUtil.remove(context, Constants.wifiSave);

                        }

                    })
                    .start();
        });
        binding.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(binding.txtPassword.getWindowToken(), 0);

                binding.txtPassword.clearFocus();

                finish();
            }
        });

    }

    // 请求定位权限
    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_WIFI_STATE},
                PERMISSION_REQUEST_CODE
        );
    }

    // 处理权限请求结果
    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            // 检查用户是否授予了定位权限
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 用户授予了定位权限，执行获取 Wi-Fi 列表的操作

            } else {
                // 用户拒绝了定位权限，可以显示一个提示或采取其他措施
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
//        EventBus.getDefault().register(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    protected void netWorkReady(boolean isReady, int state) {

    }

    @Override
    protected void netWorkFail(int state) {

    }

    @Override
    protected void onPause() {
        super.onPause();

//        EventBus.getDefault().unregister(this);
    }


}