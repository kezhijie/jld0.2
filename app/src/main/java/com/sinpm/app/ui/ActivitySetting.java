package com.sinpm.app.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;

import com.hjq.toast.ToastUtils;
import com.sinpm.app.R;
import com.sinpm.app.Utils.Constants;
import com.sinpm.app.Utils.CustomUpdateParser;
import com.sinpm.app.Utils.HttpMethod;
import com.sinpm.app.Utils.HttpUtil;
import com.sinpm.app.Utils.LanguageUtil;
import com.sinpm.app.Utils.PropertiesUtils;
import com.sinpm.app.Utils.SharedPreferencesUtil;
import com.sinpm.app.Utils.StringUtils;
import com.sinpm.app.Utils.UiUtils;
import com.sinpm.app.base.API;
import com.sinpm.app.base.ActivityManager;
import com.sinpm.app.base.BaseActivity;
import com.sinpm.app.beans.DeviceHandleBean;
import com.sinpm.app.beans.UpdateBean;
import com.sinpm.app.databinding.ActivitySettingsBinding;
import com.sinpm.app.ui.ChooseDialog;
import com.xuexiang.xupdate.XUpdate;
import com.xuexiang.xupdate.utils.UpdateUtils;

import org.apache.commons.lang3.ThreadUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class ActivitySetting extends BaseActivity {

    SeekBar seekBar;
    ActivitySettingsBinding binding;
    private Handler handler;
    private float bright = 0.8f;
    private Integer from;
    private Runnable updateTimeRunnable;

    private int clickCount = 0;
    private static final int MAX_CLICK_COUNT = 8; // 设置允许的最大点击次数
    private static final long TIME_INTERVAL = 1000; // 设置时间间隔（毫秒）
    private boolean isClickable = true;
    private boolean isStart = false;
    private String code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        initView();
//        initData();
        code  = PropertiesUtils.getValue(Constants.LOCAL_CODE,"");
        from = getIntent().getIntExtra("from", -1);
        handler = new Handler(Looper.getMainLooper());


    }

    private void initView() {
        binding.layoutTop.btnWifi.setVisibility(View.INVISIBLE);
        binding.layoutTop.test.setVisibility(View.INVISIBLE);

        Drawable drawable1 = getResources().getDrawable(R.mipmap.wifi, null);
        binding.layoutTop.imgSet.setImageDrawable(drawable1);
        binding.layoutTop.set.setOnClickListener(view -> {
//            UiUtils.startAnimator(view);
            openWifi();
        });
        seekBar = findViewById(R.id.sekb);
        ActivitySetting setting = this;
        seekBar.setMax(100);
        bright = (float) SharedPreferencesUtil.getData(ActivitySetting.this, Constants.app_bright, 0.8f);
        seekBar.setProgress((int) (bright * 100));
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                bright = (float) seekBar.getProgress();
                SharedPreferencesUtil.saveData(ActivitySetting.this, Constants.app_bright, (float) seekBar.getProgress());
                setScreenBrightness(bright);
            }
        });

//        binding.rsappLl.setOnClickListener(v -> {
//            UiUtils.startAnimator(v);
//            rsApp();
//        });
        binding.updateLl.setOnClickListener(v -> {
            v.setEnabled(false);
            this.checkUpdate(this, code, true, new HttpUtil.CallBackResult<UpdateBean>() {
                @Override
                public void success(UpdateBean updateBean) {
                    v.setEnabled(true);
                    if (updateBean == null || updateBean.getUpgradeDTO() == null) {
                        ToastUtils.show("已经是最新版本");
                    }
                }

                @Override
                public void fail(String msg) {
                    v.setEnabled(true);
                }
            });
        });


        binding.rsdeviceLl.setOnClickListener(v -> {
            dialog.show();
            v.setEnabled(false);
//        pingNet();
            Map<String, Object> map = new HashMap<>();
            map.put("code", code);
            map.put("send", 0);

            HttpUtil.request(this, API.TestCommand, HttpMethod.GET,
                    map, String.class, new HttpUtil.CallBackResult<String>() {
                        @Override
                        public void success(String checkUpdate) {
                            dialog.dismiss();
                            ToastUtils.show( "服务器通讯正常");
                            v.setEnabled(true);
                        }

                        @Override
                        public void fail(String msg) {
                            dialog.dismiss();
                            ToastUtils.show( "服务器通讯故障");
                            v.setEnabled(true);
                        }
                    });


        });
//
//        binding.btnEn.setChecked(false);
//        binding.btnZh.setChecked(true);
//        if (LanguageUtil.getLocaleByLanguage().getLanguage().equals(Locale.ENGLISH.getLanguage())) {
//            binding.btnEn.setChecked(true);
//            binding.btnZh.setChecked(false);
//        }
        Activity activity = this;
        binding.layoutTop.imgBack.setOnClickListener(v -> {
//            SharedPreferencesUtil.saveData(this, Constants.app_bright, this.bright);
//            if (binding.btnEn.isChecked()) {
//                LanguageUtil.changeAppLanguage(this, Locale.ENGLISH);
//
//            } else {
//                LanguageUtil.changeAppLanguage(this, Locale.CHINESE);
//            }
//            Class target = ActivityPart.class;
//            if (from == 0) {
//                target = LoginActivity.class;
//            }
//
//            LanguageUtil.refreshAppLanguage(this, target);
            finish();
        });


        binding.txtEn.setOnClickListener(v -> {
            binding.btnEn.toggle();
        });
        binding.txtZh.setOnClickListener(v -> {
            binding.btnZh.toggle();
        });
//        binding.btn.setOnCheckedChangeListener((v, isChecked) -> {
//            if (!isChecked) {
//                return;
//            }
//            //设置的语言、重启的类一般为应用主入口（微信也是到首页）
//            LanguageUtil.changeAppLanguage(this, Locale.ENGLISH, this.getClass());
//        });
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


    private void initData() {
        //获取版本号
        try {
            String versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            binding.versionTv.setText(versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        ActivitySetting activity = this;
        binding.idTv.setText(this.code);


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

        handler.post(updateTimeRunnable);
        initData();
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(updateTimeRunnable);

//        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void netWorkReady(boolean isReady, int i) {
        TextView txtNet = binding.txtNet;
        if (i == 0) {
            txtNet.setText("Disconnected");
            txtNet.setTextColor(getResources().getColor(android.R.color.holo_red_light));
            return;
        }

        if (i > -50) {
            txtNet.setText("Excellent");
            return;
        }

        if (i > -60) {
            txtNet.setText("Good");

            return;
        }
        if (i > -70) {
            txtNet.setText("Fair");
            return;
        } else {
            txtNet.setText("Poor");
        }
    }

    @Override
    protected void netWorkFail(int state) {

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

    private void getTestCommand() {
        dialog.show();
        testCommand = false;
//        pingNet();
        Map<String, Object> map = new HashMap<>();
        map.put("code", code);
        map.put("send", 0);

        HttpUtil.request(this, API.TestCommand, HttpMethod.GET,
                map, String.class, new HttpUtil.CallBackResult<String>() {
                    @Override
                    public void success(String checkUpdate) {
                        dialog.dismiss();
                        com.sinpm.app.Utils.ToastUtils.showMessage(ActivitySetting.this, "服务器通讯正常");

                    }

                    @Override
                    public void fail(String msg) {
                        dialog.dismiss();
                        com.sinpm.app.Utils.ToastUtils.showMessage(ActivitySetting.this, "服务器通讯故障");
                    }
                });


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