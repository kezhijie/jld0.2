package com.sinpm.app.ui;


import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.hjq.toast.ToastUtils;
import com.sinpm.app.R;
import com.sinpm.app.Utils.Constants;
import com.sinpm.app.Utils.CounterHandler;
import com.sinpm.app.Utils.HttpMethod;
import com.sinpm.app.Utils.HttpUtil;
import com.sinpm.app.Utils.PropertiesUtils;
import com.sinpm.app.Utils.ServiceUtils;
import com.sinpm.app.Utils.SharedPreferencesUtil;
import com.sinpm.app.Utils.StringUtils;
import com.sinpm.app.base.API;
import com.sinpm.app.base.BaseActivity;
import com.sinpm.app.beans.ActionDataGet;
import com.sinpm.app.beans.ActionDataSend;
import com.sinpm.app.base.ProcessData;
import com.sinpm.app.base.ProcessInfo;
import com.sinpm.app.base.ActivityManager;
import com.sinpm.app.base.TextInfo;
import com.sinpm.app.base.WifiCallBack;
import com.sinpm.app.beans.ConstantCmd;
import com.sinpm.app.beans.DeviceUseLogBean;
import com.sinpm.app.beans.DeviceUseLogDetailDTO;
import com.sinpm.app.beans.EnumFunction;
import com.sinpm.app.databinding.ActivityWorkingBinding;
import com.sinpm.app.services.LockService;
import com.sinpm.serialportlib.util.HexUtil;
import com.sinpm.serialportlibrary.OnSerialPortDataChangedListener;
import com.sinpm.serialportlibrary.SerialPortManager;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.dmoral.toasty.Toasty;


public class ActivityWorking extends BaseActivity implements CounterHandler.CounterListener{
    private ActionDataSend actionDataSend = new ActionDataSend();
    private static final String TAG = ActivityWorking.class.getSimpleName();
    ActivityWorkingBinding binding;
    private Boolean isStart = false;
    Integer singleUseDur = 0;
    private Float capacityTotal;
    /**
     * 可用容量
     */
    private Float capacityAvailable;

    private Float capacitySingle;

    private Integer timesTotal;
    private Integer timesUsed = 0;
    private Integer timesRemain = 0;

    int type = 0, cId = -1, unlockType = 3;
    Boolean isTest = false;
    private Integer duration = 0;
    private Long logId = null;
    private boolean isStop = false,initClick = false;
    private boolean isWorking = false;
    private int time;
    Toast toast;
    private Integer processIndex = 1;
    private Integer unlock = 1;
    private int cmdId;
    private Map<Integer, Integer> processType = new HashMap<>();
    private Map<String, Float> capacityMap = new HashMap<String, Float>() {{
        put("03", 0.02F);
        put("04", 0.04F);
        put("05", 0.06F);
        put("06", 0.08F);
        put("07", 0.1F);
    }};
    private String capacityTag = "03";


    public ActivityWorking() {
        if (this.wifiCallBack == null) {
            wifiCallBack = new WifiCallBack() {
                @Override
                public void wifiSuccess() {
                }

                @Override
                public void wifiError() {


                }
            };
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWorkingBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
//        startService();
        PropertiesUtils.setValue(Constants.serialId, "");
        cId = getIntent().getIntExtra("cid", -1);
        cmdId = getIntent().getIntExtra("commandId", 0);
        unlockType = getIntent().getIntExtra("unlockType", 3);
        isTest = getIntent().getBooleanExtra("isTest", false);
        unlock = getIntent().getIntExtra("unlock", 1);
        setTime(Math.round(getIntent().getIntExtra("useTime", 5) * 60));

        duration = 0;
        initView();
        initClick();
        uploadTestLog(0);
//        initCountTime();
        logId = null;
        startService();
        new CounterHandler.Builder()
                .incrementalView(binding.imgAddSt)  //增加按钮
                .decrementalView(binding.imgMinusSt)   //减少按钮
                .minRange(-50) // 区间下限
                .maxRange(50) // 区间上限
                .isCycle(true) // 是否在区间循环
                .counterDelay(100) // 长按增加速度
                .counterStep(2)  // 每次增加步长
                .listener(this) // 添加监听
                .build();
        initClick = true;
    }


    public void sendMsg() {
        String cmdAndCrc = actionDataSend.getCmdAndCrc();
        Toasty.normal(this, "[" + actionDataSend.getDesc() + "]" + cmdAndCrc).show();
        SerialPortManager.writeDataHex(cmdAndCrc);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 当活动处于后台时停止更新时间
        pauseTime();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ServiceUtils.isServiceRunning(this, LockService.class)){
            stopService(new Intent(this, LockService.class));
        }
        pauseTime();

    }


    private void uploadTestLog(int status) {
        if (!isTest) {
            return;
        }
        String code  = PropertiesUtils.getValue(Constants.LOCAL_CODE,"");
        Map<String, Object> map = new HashMap<>();
        map.put("code", code);
        map.put("status", status);
        HttpUtil.request(API.TEST_LOG, HttpMethod.GET, false, map, (HttpUtil.CallBack) data -> {

        }, String.class, false, null, this);
    }

    private void uploadPush() {
        if (!isWorking) {
            return;
        }
        if (duration % 10 > 0 && duration > 0) {
            return;
        }

        if (StringUtils.isEmpty( PropertiesUtils.getValue(Constants.LOCAL_CODE,""))) {
            return;
        }
//        String[] times = binding.txtTime.getText().toString().split(":");
        pushLog();
    }

    private void pushLog() {

        DeviceUseLogBean deviceUseLogBean = new DeviceUseLogBean(null, PropertiesUtils.getValue(Constants.LOCAL_CODE,""), 0, 0, 1, unlockType, false, cId);
        deviceUseLogBean.setCmdId(cmdId);
        Map<String, Object> map = new HashMap<>();
        map.put("code", deviceUseLogBean.getCode());
        map.put("unLockTime", duration);
        map.put("remainTime", 0);

        map.put("unlockType", deviceUseLogBean.getUnlockType());
        map.put("pushType", isStop ? 1 : 0);
//        map.put("dataList", deviceUseLogBean.getDetailList());
        if (cmdId != 0) {
            map.put("cmdId", cmdId);
        }
//        map.put("cmdId",);
        if (cId != 0) {
            map.put("cid", cId);
        }
        map.put("type", 1);
        if (logId != null) {
            map.put("logId", logId);
            map.put("type", 0);
        }
        HttpUtil.request(API.UPLOAD_USE_TIME_PUSH, HttpMethod.GET, false, map, (HttpUtil.CallBack<Long>) data -> {
            logId = data;
        }, Long.class, false, null, this);
    }


    private Handler handlerUse;
    private Runnable runnableUse;

    private void initCountTime() {
        // 开始计时
        if (!isStart) {
            return;
        }
        if (handlerUse == null) {
            handlerUse = new Handler(Looper.getMainLooper());
        }
        if (runnableUse == null) {
            // 定义一个Runnable对象，用于更新UI
            runnableUse = new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(() -> {


                        if (!isWorking) {
                            return;
                        }

                        uploadPush();
                        if (isStart) {
                            duration++;
                            time--;
                            runOnUiThread(() -> {
                                showTime();
                            });
                        }
                        if (isWorking && time <= 0) {
                            isWorking = false;
                            time = 0;
                            actionDataSend.setFunctionAndParam(EnumFunction.BACK);
                            sendMsg();
                            ToastUtils.show("当前护理已结束，请重新解锁");
                            finish();
//                            actionData.reset();
                            isStart = false;
                            binding.btnStart.setText("暂 停");
                            pushLog();
                        }
                    });


                    // 每隔一秒执行一次
                    handlerUse.postDelayed(this, 1000);
                }
            };
            // 开始计时
            handlerUse.post(runnableUse);
        }
    }

    private void pauseTime() {
        if (handlerUse != null && runnableUse != null) {
            handlerUse.removeCallbacks(runnableUse);
            runnableUse = null;
        }
    }

    private void saveUseInfo() {
        String userStr = (String) SharedPreferencesUtil.getData(this, Constants.usetime, "");
        if (TextUtils.isEmpty(userStr)) {
            return;
        }
        DeviceUseLogBean deviceUseLogBean = JSON.parseObject(userStr, DeviceUseLogBean.class);
        DeviceUseLogDetailDTO deviceUseLogDetailDTO = new DeviceUseLogDetailDTO(type, "基因枪", "基因枪", null, 1, 0, 20, singleUseDur);
        List<DeviceUseLogDetailDTO> detailList = deviceUseLogBean.getDetailList();
        if (detailList == null) {
            detailList = new ArrayList<>();
        }
        detailList.add(deviceUseLogDetailDTO);

        deviceUseLogBean.setDuration(deviceUseLogDetailDTO.getDuration());
        deviceUseLogBean.setDetailList(detailList);
        SharedPreferencesUtil.saveData(this, Constants.usetime, JSON.toJSONString(deviceUseLogBean));
        singleUseDur = 0;
    }

    int lastCheckedId = -1; // 保存上一次选中的RadioButton的ID

    private List<View> viewList = new ArrayList<>();
    private Integer model = 1;

    private void initView() {

        viewList.add(binding.llSelect);
        viewList.add(binding.llWork1);


        switchView(binding.llSelect);

//        setWordBackgroundColor(binding.txtTime, "23:03");

    }

    public void setTime(Integer time) {
        this.time = time;
        showTime();
    }

    private void showTime() {
        if (time <= 0) {
            time = 0;
        }
        Integer minutes = time / 60;
        Integer seconds = time % 60;
        String minutesStr = minutes >= 10 ? String.valueOf(minutes) : "0" + minutes;
        String secondsStr = seconds >= 10 ? String.valueOf(seconds) : "0" + seconds;


        char[] minutesArray = minutesStr.toCharArray();
        char[] secondsArray = secondsStr.toCharArray();
        this.binding.txtTime1.setText(String.valueOf(minutesArray[0]));
        this.binding.txtTime2.setText(String.valueOf(minutesArray[1]));
        this.binding.txtTime3.setText(String.valueOf(secondsArray[0]));
        this.binding.txtTime4.setText(String.valueOf(secondsArray[1]));


    }

    private void initClick() {
        binding.ckZhixian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (model==1){
                    actionDataSend.setFunctionAndParam(EnumFunction.MODE1_1);
                }else {
                    actionDataSend.setFunctionAndParam(EnumFunction.MODE2_1);
                }
                sendMsg();
            }
        });
        binding.ckZhengxian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionDataSend.setFunctionAndParam(EnumFunction.MODEL);
                if (model==1){
                    actionDataSend.setFunctionAndParam(EnumFunction.MODE1_2);
                }else {
                    actionDataSend.setFunctionAndParam(EnumFunction.MODE2_2);
                }
                sendMsg();
            }
        });
//        binding.radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
//            // 获取被选中的RadioButton的ID
//            actionDataSend.setFunctionAndParam(EnumFunction.MODEL);
//            if (binding.ckZhixian.getId() == checkedId) {
//                if (model==1){
//                    this.actionDataSend.setFunctionAndParam(EnumFunction.MODE1_1);
//                }else {
//                    this.actionDataSend.setFunctionAndParam(EnumFunction.MODE2_1);
//                }
////                this.actionDataSend.setParam4("0" + (value + model));
//            } else if (binding.ckZhengxian.getId() == checkedId) {
//                if (model==1){
//                    this.actionDataSend.setFunctionAndParam(EnumFunction.MODE1_2);
//                }else {
//                    this.actionDataSend.setFunctionAndParam(EnumFunction.MODE2_2);
//                }
//            }
//            sendMsg();
//
//        });
        // 从资源中获取Drawable
        Resources resources = getResources();
        Drawable drawable1 = resources.getDrawable(R.mipmap.logo1, null);
        Drawable drawable2 = resources.getDrawable(R.mipmap.logo2, null);

        // 设置Drawable到ImageView

        binding.llModel1.setOnClickListener(v -> {
            this.model = 2;
            binding.ckZhixian.setChecked(true);
            binding.txtSt.setText("0");
            this.actionDataSend.setFunctionAndParam(EnumFunction.HEAD_POWER);
            this.binding.imgWork.setImageDrawable(drawable2);
            this.binding.llTmpTxt.setVisibility(View.GONE);
            this.binding.llTmp.setVisibility(View.GONE);
            switchView(binding.llWork1);
            sendMsg();
        });

        binding.llModel2.setOnClickListener(v -> {
            this.model = 1;
            binding.ckZhixian.setChecked(true);
            binding.txtTmp.setText("0");
            binding.txtSt.setText("0");
            this.actionDataSend.setFunctionAndParam(EnumFunction.HEAD_TEMP);
//            if (choosePosition==1){
//                this.actionDataSend.setFunctionAndParam(EnumFunction.MODE2_1);
//            }else {
//                this.actionDataSend.setFunctionAndParam(EnumFunction.MODE2_2);
//            }
            this.binding.imgWork.setImageDrawable(drawable1);
            this.binding.llTmpTxt.setVisibility(View.VISIBLE);
            this.binding.llTmp.setVisibility(View.VISIBLE);
            switchView(binding.llWork1);
            sendMsg();
        });


        binding.imgMinusSt.setOnClickListener(v -> {
            Integer value = Integer.valueOf(binding.txtSt.getText().toString());
            value--;
            if (value < 0) {
                ToastUtils.show("强度最低为0");
                value = 0;
            }
            actionDataSend.setFunctionAndParam(EnumFunction.POWER);
            actionDataSend.setParam4(HexUtil.intToHex(value, 2, false));
            binding.txtSt.setText(String.valueOf(value));
            sendMsg();
        });

        binding.imgAddSt.setOnClickListener(v -> {
            Integer value = Integer.valueOf(binding.txtSt.getText().toString());
            value++;
            if (value > 90) {
                ToastUtils.show("强度最高为90");
                value = 90;
            }
            actionDataSend.setFunctionAndParam(EnumFunction.POWER);
            actionDataSend.setParam4(HexUtil.intToHex(value, 2, false));
            binding.txtSt.setText(String.valueOf(value));
            sendMsg();
        });


        binding.imgMinusTmp.setOnClickListener(v -> {
            Integer value = Integer.valueOf(binding.txtTmp.getText().toString());
            value--;
            if (value < 0) {
                ToastUtils.show("温度最低为0");
                value = 0;
            }

            actionDataSend.setFunctionAndParam(EnumFunction.TEMP);
            actionDataSend.setParam4(HexUtil.intToHex(value, 2, false));
            binding.txtTmp.setText(String.valueOf(value));
            sendMsg();
        });

        binding.imgAddTmp.setOnClickListener(v -> {
            Integer value = Integer.valueOf(binding.txtTmp.getText().toString());
            value++;
            if (value > 20) {
                ToastUtils.show("温度最高为20");
                value = 20;
            }
            actionDataSend.setFunctionAndParam(EnumFunction.TEMP);
            actionDataSend.setParam4(HexUtil.intToHex(value, 2, false));
            binding.txtTmp.setText(String.valueOf(value));
            sendMsg();
        });

        processChange(1, false);


        binding.layoutTop.btnWifi.setOnClickListener(v -> {
            if (isStart) {
                ToastUtils.show("请点击暂停后继续当前操作");
                return;
            }
            Activity start = this;
            Intent intent = new Intent(start, ActivityWifi.class);
            startActivity(intent);
        });
        binding.layoutTop.set.setOnClickListener(v -> {
            if (isStart) {
                ToastUtils.show("请点击暂停后继续当前操作");
                return;
            }
            Activity start = this;
            Intent intent = new Intent(start, ActivitySetting.class);
            startActivity(intent);
        });

        binding.btnQuit.setOnClickListener(v -> showQuitDialog());
        binding.layoutTop.imgBack.setOnClickListener(v -> {
            if (binding.llWork1.getVisibility() == View.VISIBLE) {
//                if (isStart) {
//                    ToastUtils.show("请点击暂停后继续当前操作");
//                    return;
//                }
                if (isStart){
                    isStart = !isStart;
                    this.actionDataSend.setFunctionAndParam(EnumFunction.PAUSE);
                    binding.btnStart.setText("开 始");
                    sendMsg();
                    pauseTime();
                }
                actionDataSend.setFunctionAndParam(EnumFunction.BACK);
                sendMsg();
                switchView(binding.llSelect);
                return;
            }

            showQuitDialog();

        });


        binding.submitButton.setOnClickListener(v -> {
            String cmdText = binding.myEditText.getText().toString().replace("  ", " ");
            getCmd(cmdText);
        });
        binding.btnHide.setOnClickListener(v -> binding.llTest.setVisibility(View.GONE));
//        binding.layoutTop.logo.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//
//                binding.llTest.setVisibility(View.VISIBLE);
//                return true;
//            }
//        });


        binding.btnStart.setOnClickListener(v -> {

            isStart = !isStart;
            if (time <= 0) {
                ToastUtils.show("当前使用时间使用完毕，请返回重新解锁");
                return;
            }
            if (isStart) {
                isWorking = true;
                binding.btnStart.setText("暂 停");
                initCountTime();
                this.actionDataSend.setFunctionAndParam(EnumFunction.START);
                sendMsg();

            } else {
//                actionData.stop();

                this.actionDataSend.setFunctionAndParam(EnumFunction.PAUSE);
                binding.btnStart.setText("开 始");
                sendMsg();
                pauseTime();
            }

        });
//        binding.btnStop.setOnClickListener(v -> {
//            actionDataSend.setStatus7("0B");
//            sendMsg();
//            isStart = false;
////            switchView(binding.llMain, binding.llStop);
//        });
//        binding.llStop.setOnClickListener(v -> {
//            showQuitDialog();
//        });
//        binding.llContinue.setOnClickListener(v -> {
//            continueWork();
//            processChange(3, false);
//        });
//        binding.llQuit.setOnClickListener(v -> {
////            stop(true);
//            showQuitDialog();
//        });
//        binding.btnSkip.setOnClickListener(v -> {
//            initByCapacity(1.0F);
//        });
//
//        binding.textViewTimes.setOnClickListener(v -> {
//            if (!ActivityManager.isDebug(this)) {
//                return;
//            }
//            setTimesAdd(true);
//        });

    }

    private boolean unlockLoading = false;


    private void processChange(Integer processIndex, boolean isComplete) {
        switch (processIndex) {
            case 6:
                isStart = false;
                break;
            case 5:
                //如果未工作，就走默认逻辑
                if (capacityAvailable.intValue() == capacityTotal.intValue()) {
                    break;
                }
                if (capacityAvailable <= 0) {
                    break;
                }
                if (capacityAvailable > 0) {
                    processIndex += 1;
                    break;
                }
        }
        this.processIndex = processIndex;

        int resourceId = getResources().getIdentifier("ll_process_" + processIndex, "id", getPackageName());
        LinearLayout process = findViewById(resourceId);
        //改变其他的控件
        if (process == null) {
            return;
        }
        for (int i = 1; i <= 6; i++) {
            int otherResourceId = getResources().getIdentifier("ll_process_" + i, "id", getPackageName());
            if (i < processIndex) {
                setSelectOther(findViewById(otherResourceId), true, true);
            } else if (i == processIndex) {
                setSelectOther(findViewById(otherResourceId), true, isComplete);
            } else {
                setSelectOther(findViewById(otherResourceId), false, false);
            }
        }
        setProcess(processIndex, 1);
        setSelect(process, isComplete);


        if (isComplete) {
            return;
        }
        actionDataSend.setEnd5("0" + processIndex);
//        sendMsg();


    }

    private void setWordBackgroundColor(TextView textView, String text) {
        Editable editableText = new Editable.Factory().newEditable(text);

        char[] words = text.toCharArray();

        for (int i = 0; i < words.length; i++) {
            // 设置背景颜色
            BackgroundColorSpan backgroundSpan = new BackgroundColorSpan(Color.parseColor("#492D35"));
            editableText.setSpan(backgroundSpan, i, i + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            // 设置文字颜色
            ForegroundColorSpan foregroundSpan = new ForegroundColorSpan(Color.parseColor("#fffdd000"));
            editableText.setSpan(foregroundSpan, i, i + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            // 增加字母大小，以增加字母之间的边距
            AbsoluteSizeSpan sizeSpan = new AbsoluteSizeSpan(35, true); // 字体大小，缩放模式
            editableText.setSpan(sizeSpan, i, i + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        textView.setText(editableText);
    }

    private void switchView(View show) {

        for (View view : viewList) {
            if (view == null) {
                continue;
            }
            if (show.getId() == view.getId()) {
                continue;
            }
            if (view.getVisibility() == View.GONE) {
                continue;
            }
            view.setVisibility(View.GONE);

        }

        if (show != null && show.getVisibility() == View.GONE) {
            show.setVisibility(View.VISIBLE);
        }


        binding.layoutTop.imgBack.setVisibility(binding.llWork1.getVisibility()==View.VISIBLE?View.VISIBLE:View.INVISIBLE);

    }


    private void setSelect(boolean isComplete) {
        int resourceId = getResources().getIdentifier("ll_process_" + processIndex, "id", getPackageName());
        setSelect(findViewById(resourceId), isComplete);
    }

    public void setSelect(View process, boolean isComplete) {
        LinearLayout ll_bg = process.findViewWithTag("ll_bg");
        TextView textView = process.findViewWithTag("txt_index");
        ImageView imageView = process.findViewWithTag("img_status");

        //select txt_index 颜色改成白色 ll_bg改成蓝色
        if (ll_bg != null) {
            ll_bg.setBackgroundResource(R.mipmap.bg_blue);
        }
        if (textView != null) {
            textView.setTextColor(Color.parseColor("#ffffffff"));
        }

        imageView.setVisibility(View.GONE);
        textView.setVisibility(View.VISIBLE);
        //isComplete txt_index 隐藏  img_status图片展示
        if (isComplete) {
            if (imageView != null) {
                imageView.setVisibility(View.VISIBLE);
            }
            if (textView != null) {
                textView.setVisibility(View.GONE);
            }
        }
    }


    public void setSelectOther(View process, boolean select, boolean isComplete) {
        LinearLayout ll_bg = process.findViewWithTag("ll_bg");
        TextView textView = process.findViewWithTag("txt_index");
        ImageView imageView = process.findViewWithTag("img_status");

        //select txt_index 颜色改成白色 ll_bg改成蓝色
        if (ll_bg != null) {
            ll_bg.setBackgroundResource(select ? R.mipmap.bg_blue : R.mipmap.bg_white);
        }
        if (textView != null) {
            textView.setTextColor(select ? Color.parseColor("#ffffffff") : Color.parseColor("#3A8CFA"));
        }

        imageView.setVisibility(View.GONE);
        textView.setVisibility(View.VISIBLE);
        //isComplete txt_index 隐藏  img_status图片展示
        if (isComplete) {
            if (imageView != null) {
                imageView.setVisibility(View.VISIBLE);
            }
            if (textView != null) {
                textView.setVisibility(View.GONE);
            }
        }
    }


    /**
     * @param process
     * @param type    0-默认信息 1-提示信息 2-错误消息 3-成功跳转
     */
    private void setProcess(Integer process, Integer type) {
        processType.put(process, type);
        this.processIndex = process;
        if (process <= 4) {
            actionDataSend.setEnd5("0" + process);
        }
        ProcessInfo processInfo = ProcessData.getProcess(process);

        // 使用SpannableStringBuilder来构建最终的文本内容
        SpannableStringBuilder ssb = new SpannableStringBuilder();
        List<TextInfo> textInfos = processInfo.getTextInfoDefault();
        if (type.equals(ConstantCmd.STATUS_TIP)) {
            textInfos = processInfo.getTextInfoTip();
            setSelect(false);
        } else if (type.equals(ConstantCmd.STATUS_ERROR)) {
            textInfos = processInfo.getTextInfoError();
            setSelect(false);
        } else if (type.equals(ConstantCmd.STATUS_SUCESS)) {
            setSelect(true);
        }
        int sentences = textInfos.size();
        for (int i = 0; i < sentences; i++) {
            // 添加句子到SpannableStringBuilder
            TextInfo textInfo = textInfos.get(i);

            String text = textInfo.getText();
            ssb.append(text);
            // 为当前句子设置颜色
            ForegroundColorSpan colorSpan = new ForegroundColorSpan(textInfo.getColor());
            ssb.setSpan(colorSpan, ssb.length() - text.length(), ssb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            // 如果不是最后一句，添加换行符
            if (i < sentences - 1) {
                ssb.append("\n");
            }
        }
    }

    // 强制刷新页面
    private void forceRefresh() {
        Intent intent = getIntent();
        finish(); // 结束当前Activity
        startActivity(intent); // 重新启动当前Activity
    }


    /**
     * 串口监听
     */
    private final OnSerialPortDataChangedListener onSerialPortDataChangedListener = (data, size) -> {

        byte[] cache = new byte[size];
        System.arraycopy(data, 0, cache, 0, size);
        String cmd = HexUtil.byteArrayToHexStr(cache);
        Log.d("TAG", "cache = " + cmd);

        getCmd(cmd);
//
//        CrashHandler.getInstance().saveCrashInfo2File("收到指令:" + cmd);

    };

    private void getCmd(String cmd) {
        String cmdStatus = cmd.trim().replaceAll(" ", "").substring(4, 8);
        switch (cmdStatus){
            case "B001"://档位加1
                Integer value = Integer.valueOf(binding.txtSt.getText().toString());
                value++;
                if (value > 90) {
                    ToastUtils.show("强度最高为90");
                    value = 90;
                }
                actionDataSend.setFunctionAndParam(EnumFunction.POWER);
                actionDataSend.setParam4(HexUtil.intToHex(value, 2, false));
                binding.txtSt.setText(String.valueOf(value));
                sendMsg();
                break;
            case "B002"://档位减1
                Integer value2 = Integer.valueOf(binding.txtSt.getText().toString());
                value2--;
                if (value2 < 0) {
                    ToastUtils.show("强度最低为0");
                    value2 = 0;
                }
                actionDataSend.setFunctionAndParam(EnumFunction.POWER);
                actionDataSend.setParam4(HexUtil.intToHex(value2, 2, false));
                binding.txtSt.setText(String.valueOf(value2));
                sendMsg();
                break;
            case "B003"://启动/停止
                isStart = !isStart;
                if (time <= 0) {
                    ToastUtils.show("当前使用时间使用完毕，请返回重新解锁");
                    return;
                }
                if (isStart) {
                    isWorking = true;
                    binding.btnStart.setText("暂 停");
                    initCountTime();
                    this.actionDataSend.setFunctionAndParam(EnumFunction.START);
                    sendMsg();

                } else {
//                actionData.stop();

                    this.actionDataSend.setFunctionAndParam(EnumFunction.PAUSE);
                    binding.btnStart.setText("开 始");
                    sendMsg();
                    pauseTime();
                }
                break;
            case "B004"://模式切换
                if (binding.ckZhixian.isChecked()){
                    binding.ckZhengxian.setChecked(true);
                    if (model==1){
                        actionDataSend.setFunctionAndParam(EnumFunction.MODE1_2);
                    }else {
                        actionDataSend.setFunctionAndParam(EnumFunction.MODE2_2);
                    }
                    sendMsg();
                }else {
                    binding.ckZhixian.setChecked(true);
                    if (model==1){
                        actionDataSend.setFunctionAndParam(EnumFunction.MODE1_1);
                    }else {
                        actionDataSend.setFunctionAndParam(EnumFunction.MODE2_1);
                    }
                    sendMsg();
                }
//                showWarn(false);
                break;
        }
        ToastUtils.show("收到指令:" + cmd);
    }

    /**
     * 总容量打完
     *
     * @param send
     */
    private void completeWork(boolean send) {
//        switchView(binding.llMain, binding.llStop);
        if (send) {
            actionDataSend.complete();
            sendMsg();
        }

        this.capacityAvailable = 0f;
        this.capacityTotal = 0f;
        this.timesRemain = 0;
        this.timesTotal = 0;
        this.timesUsed = 0;

        this.isStart = false;
        actionDataSend = new ActionDataSend();


    }

    /**
     * 停止工作
     *
     * @param
     */
    private void stopWork() {
        actionDataSend.setFunctionAndParam(EnumFunction.BACK);
        sendMsg();
        this.isStart = false;
        actionDataSend = new ActionDataSend();
    }


    private ChooseDialog chooseDialog;

    private void showQuitDialog() {
        if (chooseDialog == null) {
            chooseDialog = new ChooseDialog.Builder(this).setTitle("提示").setContent("是否退出当前护理？").setBack(view -> {
            }).setConfirm(view -> {
                this.actionDataSend.setFunctionAndParam(EnumFunction.BACK);
                sendMsg();
                chooseDialog.dismiss();
                close();
            }).create();
        }
        if (!chooseDialog.isShowing() && !this.isFinishing()) {
            chooseDialog.show();
        }
    }

    public void startService() {
        //实际接线方式
//        String serialPort = "/dev/tty";
        String serialPort = "/dev/ttyS1";
        Integer baudRate = 115200;
        if (ActivityManager.isDebug(this)) {
            //调试模式下的接线方式
            serialPort = "/dev/ttyS1";
        }

        SerialPortManager.setOnSerialPortDataChangedListener(onSerialPortDataChangedListener);
        boolean open = SerialPortManager.openSerialPort(serialPort, baudRate);
        if (!open) {
            ToastUtils.show("串口打开失败:" + serialPort + "(" + baudRate + ")");
        }
        actionDataSend = new ActionDataSend();
    }

    private void close() {
        isStop = true;
        if (handlerUse != null) {
            handlerUse.removeCallbacks(runnableUse);
        }
        stopWork();
        uploadPush();
        SerialPortManager.closeSerialPort();
        if (toast != null) {
            toast.cancel();
        }
        if (chooseDialog != null) {
            chooseDialog.dismiss();
            chooseDialog = null;
        }
        ToastUtils.show("正在退出，请稍后");
        // 延迟2秒后关闭当前Activity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 1000); // 延迟时间，单位为毫秒（这里是2秒）


    }

    @Override
    protected void onDestroy() {

        Log.d("onDestroy", "onDestroy: ");
        super.onDestroy();
        if (!ServiceUtils.isServiceRunning(this, LockService.class)){
            startService(new Intent(this, LockService.class));
        }
    }

    @Override
    protected void netWorkReady(boolean isReady, int state) {

    }

    @Override
    protected void netWorkFail(int state) {

    }


    @Override
    public void onIncrement(View view, float number) {
        Integer value = Integer.valueOf(binding.txtSt.getText().toString());
        value++;
        if (value > 90) {
            ToastUtils.show("强度最高为90");
            value = 90;
        }
        actionDataSend.setFunctionAndParam(EnumFunction.POWER);
        actionDataSend.setParam4(HexUtil.intToHex(value, 2, false));
        binding.txtSt.setText(String.valueOf(value));
        if (initClick){
            sendMsg();
        }
    }

    @Override
    public void onDecrement(View view, float number) {
        Integer value = Integer.valueOf(binding.txtSt.getText().toString());
        value--;
        if (value < 0) {
            ToastUtils.show("强度最低为0");
            value = 0;
        }
        actionDataSend.setFunctionAndParam(EnumFunction.POWER);
        actionDataSend.setParam4(HexUtil.intToHex(value, 2, false));
        binding.txtSt.setText(String.valueOf(value));
        if (initClick){
            sendMsg();
        }
    }
}