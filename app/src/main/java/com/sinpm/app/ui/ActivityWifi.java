package com.sinpm.app.ui;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiNetworkSuggestion;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.hjq.toast.ToastUtils;
import com.sinpm.app.R;
import com.sinpm.app.Utils.Constants;
import com.sinpm.app.Utils.HttpMethod;
import com.sinpm.app.Utils.HttpUtil;
import com.sinpm.app.Utils.PropertiesUtils;
import com.sinpm.app.Utils.SharedPreferencesUtil;
import com.sinpm.app.Utils.UiUtils;
import com.sinpm.app.base.API;
import com.sinpm.app.base.ActivityManager;
import com.sinpm.app.base.BaseActivity;
import com.sinpm.app.databinding.ActivityWifiBinding;
import com.thanosfisherman.wifiutils.WifiConnectorBuilder;
import com.thanosfisherman.wifiutils.WifiUtils;
import com.thanosfisherman.wifiutils.wifiDisconnect.DisconnectionErrorCode;
import com.thanosfisherman.wifiutils.wifiDisconnect.DisconnectionSuccessListener;
import com.thanosfisherman.wifiutils.wifiRemove.RemoveErrorCode;
import com.thanosfisherman.wifiutils.wifiRemove.RemoveSuccessListener;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class ActivityWifi extends BaseActivity {

    private ActivityWifiBinding binding;
    private WifiManager wifiManager;
    private WifiReceiver wifiReceiver;

    private Dialog dialogConnect;

    WifiConnectorBuilder.WifiUtilsBuilder wifiUtil;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWifiBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//        dialogConnect = UiUtils.showLoadingDialog(this, R.string.msg_connecting);
        // 注册 Wi-Fi 扫描结果接收器
//        wifiReceiver = new WifiReceiver();
//        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        // 请求定位权限（Android 6.0及以上需要动态请求定位权限）
        requestLocationPermission();
        // 开始扫描 Wi-Fi
        if (!wifiManager.isWifiEnabled()){
            wifiManager.setWifiEnabled(true);
        }
        wifiManager.startScan();
        binding.layoutTop.imgBack.setOnClickListener(v -> {
            this.finish();
        });
       wifiUtil = WifiUtils.withContext(this);
        binding.layoutTop.btnWifi.setVisibility(View.INVISIBLE);
        Drawable drawable1 = getResources().getDrawable(R.mipmap.refresh, null);
        binding.layoutTop.imgSet.setImageDrawable(drawable1);
        binding.layoutTop.set.setOnClickListener(v -> {
            // 开始扫描 Wi-Fi
            refresh();
        });
        getConnection();
    }

    private void refresh() {

        clear();
        if (!wifiManager.isWifiEnabled()){
            wifiManager.setWifiEnabled(true);
        }
        wifiManager.startScan();
        getConnection();
        startWifiScan();
    }

    private static final int PERMISSION_REQUEST_CODE = 1;

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
                startWifiScan();
            } else {
                // 用户拒绝了定位权限，可以显示一个提示或采取其他措施
            }
        }
    }

    // 执行获取 Wi-Fi 列表的操作
    private void startWifiScan() {

        // 在这里添加获取 Wi-Fi 列表的逻辑
        wifiManager.startScan();
        List<ScanResult> scanResultsOrigin = wifiManager.getScanResults();

        Map<String, ScanResult> stringScanResultMap = new HashMap<>();
        for (ScanResult scanResult : scanResultsOrigin) {
            stringScanResultMap.put(scanResult.SSID, scanResult);
        }
        List<ScanResult> scanResults = new ArrayList<>(stringScanResultMap.values());

        getConnection();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        List<WifiConfiguration> configuredNetworks = wifiManager.getConfiguredNetworks();
        if (ActivityManager.isDebug(this)) {
            if (configuredNetworks != null && configuredNetworks.size() > 0 && scanResults != null && scanResults.size() > 0) {
                WifiConfiguration wifiConfiguration = configuredNetworks.get(0);
                WifiConfiguration copy = copy(wifiConfiguration);
                modifyField(copy, "SSID", "aaaa");
                modifyField(copy, "ssid", "aaaa");
                configuredNetworks.add(copy);
                ScanResult scanResult = copy(scanResults.get(0));
                modifyField(scanResult, "SSID", "aaaa");
                modifyField(scanResult, "ssid", "aaaa");
                scanResults.add(scanResult);
            }
        }
        wifiInit(scanResults, configuredNetworks);

    }

    public <T> T copy(T source) {
        // 获取源对象的类
        try {


            Class<?> sourceClass = source.getClass();
            // 创建新的对象
            T target = (T) sourceClass.newInstance();

            // 获取源对象的所有字段
            Field[] fields = sourceClass.getDeclaredFields();
            for (Field field : fields) {
                // 设置字段可访问（因为有些字段可能是私有的）
                field.setAccessible(true);

                // 获取字段的值并设置到新对象中
                field.set(target, field.get(source));
            }

            return target;
        } catch (Exception exception) {
            return null;
        }
    }

    public static void modifyField(Object object, String fieldName, Object newValue) {
        try {
            // 获取对象的类
            Class<?> objectClass = object.getClass();
            // 获取指定名称的字段
            Field field = objectClass.getDeclaredField(fieldName);
            // 设置字段可访问（因为有些字段可能是私有的）
            field.setAccessible(true);
            // 修改字段的值
            field.set(object, newValue);
        } catch (Exception ex) {

        }
    }

    //弹出确认框，暂停
    private void showWifiModel(ScanResult scanResult) {
        Intent intent = new Intent(this, ActivityWifiConnect.class);
        intent.putExtra("ssid", scanResult.SSID.replace("\"", ""));
        startActivity(intent);
    }

    private void getConnection() {
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int visible = View.VISIBLE;
        if (wifiInfo == null || StringUtils.isEmpty(wifiInfo.getSSID()) || StringUtils.contains(wifiInfo.getSSID(), "<unknown ssid>")) {
            visible = View.GONE;
        }
        binding.wifiConnected.setText(getSsid(wifiInfo.getSSID()));
        binding.llConected.setVisibility(visible);
    }


    private void clear() {
//        LinearLayout llWifiList = findViewById(R.id.ll_wifi_list);
        int childCount = llWifiList.getChildCount();

        // 移除所有子视图
        while (childCount > 0) {
            View childView = llWifiList.getChildAt(0);
            llWifiList.removeView(childView);
            childCount--;
        }

        // 如果已经没有子视图，则执行显示动画
        if (childCount == 0) {
            llWifiList.setVisibility(View.GONE);
            llWifiList.setAlpha(0f);
            llWifiList.setVisibility(View.VISIBLE);
            ObjectAnimator animator = ObjectAnimator.ofFloat(llWifiList, "alpha", 0f, 1f);
            animator.setDuration(500);
            animator.start();
        }
    }

    LinearLayout llWifiList;

    Map<String, WifiConfiguration> configurationMap = new HashMap<>();

    private void wifiInit(List<ScanResult> scanResults, List<WifiConfiguration> configurations) {
        if (!TextUtils.isEmpty(PropertiesUtils.getValue(Constants.WIFISSID,""))){
            Log.e("//////",PropertiesUtils.getValue(Constants.WIFISSID,""));
        }
        hideMsg();
        llWifiList = findViewById(R.id.ll_wifi_list);

        if (scanResults == null || scanResults.size() == 0) {
            showMsg("暂无可用wifi列表，请刷新或者稍后再试");
            llWifiList.setVisibility(View.VISIBLE);
            return;
        }
        if (configurations != null && configurations.size() > 0) {
            for (WifiConfiguration configuration : configurations) {

                configurationMap.put(getSsid(configuration.SSID), configuration);

//                 if (configuration.status == WifiConfiguration.Status.DISABLED
//                        && configuration == WifiConfiguration.DISABLED_AUTH_FAILURE) {
//                 }
            }
        }

        // 获取现有的 llWifiList
        // 复制源控件 ll_wifi_item，添加到 llWifiList 中
        // 获取 LayoutInflater
//        int time = scanResults.size() == 0 ? 1000 : 1000 / scanResults.size();
        LayoutInflater inflater = getLayoutInflater();
        String saveSsid = PropertiesUtils.getValue(Constants.WIFISSID, "");
        for (ScanResult scanResult : scanResults) {
            if (TextUtils.isEmpty(scanResult.SSID)) {
                continue;
            }
            // 使用原始 ll_wifi_item 创建新的 ll_wifi_item
            View llWifiItem = inflater.inflate(R.layout.ll_wifi_item, llWifiList, false);
            // 为了确保每个复制的 llWifiItem 都有独立的布局参数，需要重新设置 layoutParams
//            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
//                    ViewGroup.LayoutParams.MATCH_PARENT,
//                    28);
            TextView txtWifi = llWifiItem.findViewById(R.id.txt_wifi);
            LinearLayout linearLayout = llWifiItem.findViewById(R.id.ll_wifi_item_connect);

            ImageView imageView = llWifiItem.findViewById(R.id.img_wifi_item_lock);

            String ssid = getSsid(scanResult.SSID);
            // 注意：SSID 可能包含引号，需要处理一下
            txtWifi.setText(ssid); // 设置文本值

            llWifiItem.setTag("ll_wifi_item");
            if (!TextUtils.isEmpty(saveSsid) && saveSsid.contains(ssid)) {
                llWifiItem.setTag("ll_wifi_item_save");
                txtWifi.setText(ssid + "(" + getResources().getString(R.string.msg_saved) + ")"); // 设置文本值
//                imageView.setImageResource(getResources().g);
                imageView.setImageResource(R.mipmap.wifi_select);
                linearLayout.setOnLongClickListener(v -> {
                    PopupMenu popupMenu = new PopupMenu(this, v);
                    popupMenu.getMenuInflater().inflate(R.menu.wifi_reconnect, popupMenu.getMenu());

                    popupMenu.setOnMenuItemClickListener(item -> {
                        // 在这里处理菜单项的点击事件
                        switch (item.getItemId()) {
                            case R.id.menu_reconnect:
                                // 执行操作1
                                connectWifi(scanResult, null);
                                return true;
                            case R.id.menu_cancel_save:
                                // 执行操作2
//                                wifiManager.setWifiEnabled(false);
                                wifiUtil.disconnect(new DisconnectionSuccessListener() {
                                            @Override
                                            public void success() {
                                                wifiUtil.remove(ssid, new RemoveSuccessListener() {
                                                            @Override
                                                            public void success() {
                                                                removeConfig(scanResult);
                                                            }

                                                            @Override
                                                            public void failed(@NonNull RemoveErrorCode errorCode) {
                                                                ToastUtils.show(errorCode.toString());
                                                                removeConfig(scanResult);
                                                            }
                                                        }

                                                );
                                            }

                                            @Override
                                            public void failed(@NonNull DisconnectionErrorCode errorCode) {
                                                ToastUtils.show(errorCode.toString());
                                            }
                                        });


//                                wifiManager.removeNetwork(configurationMap.get(scanResult.SSID).networkId);
//                                wifiManager.saveConfiguration();
//                                wifiManager.disconnect();
//                                wifiManager.setWifiEnabled(false);
//                                wifiManager.setWifiEnabled(true);

                                ToastUtils.show(getResources().getString(R.string.msg_cancel));
                                refresh();
                                return true;
                            default:
                                return false;
                        }
                    });

                    popupMenu.show();
                    return true;
                });
            } else {
                linearLayout.setOnLongClickListener(v -> {
                    PopupMenu popupMenu = new PopupMenu(this, v);
                    popupMenu.getMenuInflater().inflate(R.menu.wifi_first, popupMenu.getMenu());

                    popupMenu.setOnMenuItemClickListener(item -> {
                        // 在这里处理菜单项的点击事件
                        switch (item.getItemId()) {
                            case R.id.menu_connect:
                                // 执行操作1

                                connectWifi(scanResult, null);
                                return true;
                            default:
                                return false;
                        }
                    });

                    popupMenu.show();
                    return true;
                });
            }

            linearLayout.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    connectWifi(scanResult, configurationMap.get(scanResult.SSID));
                }
            });
//            llWifiItem.setLayoutParams(layoutParams);
//            llWifiItem.setBackground();
            // 将复制的 llWifiItem 添加到 llWifiList 中
            llWifiList.setVisibility(View.VISIBLE);

            // 添加子控件并应用淡入动画
            llWifiList.addView(llWifiItem); // 在动画开始前先添加子控件

        }
    }

    private void removeConfig(ScanResult scanResult) {
        ToastUtils.show(getResources().getString(R.string.msg_cancel));
        if (!TextUtils.isEmpty(PropertiesUtils.getValue(Constants.WIFISSID,""))){
            String arraySsid = PropertiesUtils.getValue(Constants.WIFISSID, "");
            String newSsid;
            if (arraySsid.contains(",")){
                newSsid = arraySsid.replaceAll(scanResult.SSID+",","");
            }else {
                newSsid = arraySsid.replaceAll(scanResult.SSID+"","");
            }
            PropertiesUtils.setValue(Constants.WIFISSID,newSsid);
        }
        wifiManager.removeNetwork(configurationMap.get(scanResult.SSID).networkId);


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            WifiNetworkSuggestion suggestionToRemove = new WifiNetworkSuggestion.Builder()
                    .setSsid(scanResult.SSID)
                    .build();
            List<WifiNetworkSuggestion> suggestions = new ArrayList<>();
            suggestions.add(suggestionToRemove);
            wifiManager.removeNetworkSuggestions(suggestions);
        } else {

            wifiManager.saveConfiguration();
            wifiManager.disconnect();

            new Handler().postDelayed(() -> {
                wifiManager.setWifiEnabled(false);
                new Handler().postDelayed(() -> wifiManager.setWifiEnabled(true), 2000);
            }, 1000);

        }
        SharedPreferencesUtil.remove(this, Constants.wifiSave);

        wifiUtil.cancelAutoConnect();
    }

    @Nullable
    private String getSsid(String ssid) {
        if (ssid != null && ssid.startsWith("\"") && ssid.endsWith("\"")) {
            ssid = ssid.substring(1, ssid.length() - 1);
        }
        return ssid;
    }

    //如果是已保存的，传递wifiConfiguration
    private void connectWifi(ScanResult scanResult, WifiConfiguration wifiConfiguration) {

        if (wifiConfiguration != null) {
            // 使用enableNetwork方法连接到已配置的网络
            wifiManager.enableNetwork(wifiConfiguration.networkId, true);

            // 重新连接WiFi
            if (wifiManager.reconnect()) {
//                ToastUtils.show("连接成功");
                showWifiModel(scanResult);
                // 连接后退出循环
                return;
            }
            ToastUtils.show("连接失败，请检查网络设置");
            return;
        }


        showWifiModel(scanResult);

    }

    private void showMsg(String text) {
        binding.llNoWifi.setVisibility(View.VISIBLE);
        binding.llWifiList.setVisibility(View.GONE);
        binding.txtWifiError.setText(text);
    }

    private void hideMsg() {

        binding.llNoWifi.setVisibility(View.GONE);
    }

    private void toggleSoftInput(View view) {

//        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInputFromWindow(view.getWindowToken(), InputMethodManager.SHOW_FORCED, 0);


    }

    private ChooseDialog chooseDialog;

    private void showQuitDialog() {
        if (chooseDialog == null) {
            chooseDialog = new ChooseDialog.Builder(this).setTitle("提示").setContent("是否重新启动?").setBack(view -> {
            }).setConfirm(view -> {
                chooseDialog.dismiss();

                rsApp();
            }).create();
        }
        if (!chooseDialog.isShowing() && !this.isFinishing()) {
            chooseDialog.show();
        }
    }


    volatile String pingResult = "0ms";

    private void pingNet() {
        String delay1 = UiUtils.pingCmd("api.com.sinpm.com", 5, 200).getAvg();
        String delay2 = UiUtils.pingCmd("baidu.com", 5, 200).getAvg();

        pingResult = "com.sinpm:" + delay1 + "ms," + "baidu:" + delay2 + "ms";
    }

    @Override
    protected void onResume() {
        super.onResume();
//        EventBus.getDefault().register(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // 取消注册 Wi-Fi 扫描结果接收器
        if (wifiReceiver != null) {
            unregisterReceiver(wifiReceiver);
        }
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

    // 接收 Wi-Fi 扫描结果的 BroadcastReceiver
    private class WifiReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 获取扫描结果
            List<ScanResult> scanResults = wifiManager.getScanResults();

            // 遍历扫描结果
            for (ScanResult result : scanResults) {
                String ssid = result.SSID; // Wi-Fi 名称
                int level = result.level;  // 信号强度

                // 打印信号强度信息
                Log.d("WifiScan", "SSID: " + ssid + ", Level: " + level);
            }

        }
    }

    /**
     * 收到指令处理
     *
     * @param eventMsg
     */
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onReceiveMsg(EventMsg eventMsg){
//        if (eventMsg.getType()!=EventMsg.TYPE_CONTENT){
//            return;
//        }
//        Log.e("onReceiveMsg",eventMsg.getMessage());
//        testCommand = true;
//        if (count.getCount()>0){
//            count.countDown();
//        }
//    }
    Boolean testCommand = false;
    CountDownLatch count;

    private void getTestCommand(Activity activity) {
        dialog.show();
        testCommand = false;
        pingNet();
        Map<String, Object> map = new HashMap<>();
        map.put("code",   PropertiesUtils.getValue(Constants.LOCAL_CODE,""));
        map.put("send", 0);
        HttpUtil.request(API.TestCommand, HttpMethod.GET, false, map, new HttpUtil.CallBack<String>() {
            @Override
            public void setResult(String o) {
                testCommand = true;
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                if (testCommand) {
                    ToastUtils.show("服务器通讯正常,网络延迟" + pingResult);
                } else {
                    ToastUtils.show("服务器通讯故障,网络延迟" + pingResult);
                }
            }
        }, String.class, false, dialog, activity);
    }

    /**
     * 设置亮度
     *
     * @param progress
     */
    private void setScreenBright(float progress) {
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.screenBrightness = progress;

        SharedPreferencesUtil.saveData(this, Constants.app_bright, progress);
        super.getWindow().setAttributes(layoutParams);

    }

    /**
     * 重启app
     */
    private void rsApp() {
        ActivityManager.finishAllActivity();
        Intent intent = new Intent(this, LoadingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }


    /**
     * 重启设备
     */
    private void rsDevice() {

        try {
            Process process = Runtime.getRuntime().exec(new String[]{"su", "-c", "reboot "});
            process.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

}