package com.sinpm.app.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.hjq.toast.ToastUtils;
//import com.lawman.mpush.EventMsg;
//import com.lawman.mpush.MPush;
import com.sinpm.app.R;
import com.sinpm.app.Utils.Constants;
import com.sinpm.app.Utils.HttpMethod;
import com.sinpm.app.Utils.HttpUtil;
import com.sinpm.app.Utils.PropertiesUtils;
import com.sinpm.app.Utils.QRCodeUtil;
import com.sinpm.app.Utils.UiUtils;
import com.sinpm.app.base.API;
import com.sinpm.app.base.ActivityManager;
import com.sinpm.app.base.BaseActivityWithTask;
import com.sinpm.app.beans.CommandLogBean;
import com.sinpm.app.beans.CommandStatusBean;
import com.sinpm.app.beans.DeviceUnLockDTO;
import com.sinpm.app.beans.EnumFunction;
import com.sinpm.app.beans.EventBean;
import com.sinpm.app.beans.PayTimesBean;
import com.sinpm.app.beans.PayUrlBean;
import com.sinpm.app.beans.QrBean;
import com.sinpm.app.databinding.ActivityPayBinding;

//import org.greenrobot.eventbus.EventBus;
//import org.greenrobot.eventbus.Subscribe;
//import org.greenrobot.eventbus.ThreadMode;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.jessyan.autosize.AutoSizeConfig;

public class PayActivity extends BaseActivityWithTask {
    ActivityPayBinding binding;
    PayTimesBean payTimesBean;
    Integer payType = 2;
    String code;
    Integer cId;

    Boolean uploadFlag = false;

    Resources resources;

    private ChooseDialog chooseDialog;
    private Integer payId = 0;
    Long token = 0L;
    Double amount = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }
        binding = ActivityPayBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
        code = PropertiesUtils.getValue(Constants.LOCAL_CODE,"");

        resources = getResources();
        init();
        getAmount();
        initData();
        showConfirmDialog(false);
        this.taskReady = true;
    }

    private void init() {
//        Glide.with(this)
//                .load(R.mipmap.bg_main)
//                .into(binding.bg);
//        binding.back.setOnClickListener(view -> {
////                    super.closeMain();
//                    this.finish();
//                }
//        );
//        binding.amountBtn.setOnClickListener(view ->
//                amountPay(view)
//        );
//        binding.helpIv.setOnClickListener(view -> createPop());
//        binding.btnWifi.setOnClickListener(view -> {
//            UiUtils.startAnimator(view);
//            startActivity(new Intent(this,ActivityWifi.class));
//        });
//        binding.set.setOnClickListener(view -> {
//            UiUtils.startAnimator(view);
//            Intent intent = new Intent(PayActivity.this, ActivitySetting.class);
//            startActivity(intent);
//        });
//        binding.netRl.setOnClickListener(view -> {
//            if (BaseApplication.getInstance().getConnectFlag().getValue()) {
//                ToastUtils.show("已连接服务器");
//            } else {
//                ToastUtils.show("设备已经离线，正在重连");
//                CheckMPushInit(code);
//            }
//        });
//        BaseApplication.getInstance().getConnectFlag().observe(this, new Observer<Boolean>() {
//            @Override
//            public void onChanged(Boolean aBoolean) {
//                Glide.with(PayActivity.this)
//                        .load(aBoolean ? R.mipmap.connect : R.mipmap.disconnect)
//                        .into(binding.netIv);
////                binding.netState.setText(aBoolean?"在线":"离线");
//            }
//        });
    }


    @Override
    public Object setTask() {
        return null;
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventBean event) {
        if ("check_msg".equals(event.getType())){
            CommandStatusBean commandLogBean = (CommandStatusBean) event.getData();
            if (commandLogBean.getData()!=null){
                commandExecute(commandLogBean, PayActivity.this);
            }
        }
    };


    private void getTimes() {

    }

    private void initData() {
        Map<String, Object> map = new HashMap<>();
        map.put("code", code);
        map.put("payType", 2);
        HttpUtil.request(API.getPayTime, HttpMethod.GET, false, map, (HttpUtil.CallBack<List<PayTimesBean>>) data -> {
            // 复制并设置属性
            copyAndSetProperties(data);
        }, PayTimesBean.class, true, dialog, this);


        binding.layoutTop.btnWifi.setOnClickListener(v -> {
            Activity start = this;
            Intent intent = new Intent(start, ActivityWifi.class);
            startActivity(intent);
        });
        binding.layoutTop.set.setOnClickListener(v -> {

            Activity start = this;
            Intent intent = new Intent(start, ActivitySetting.class);
            startActivity(intent);
        });

//        binding.btnQuit.setOnClickListener(v -> showQuitDialog());
        binding.layoutTop.imgBack.setOnClickListener(v -> {
            this.finish();

        });
    }

    private void copyAndSetProperties(List<PayTimesBean> list) {

        // 初始化界面元素
        LinearLayout llMoneyList = findViewById(R.id.ll_money_list);

        // 使用LayoutInflater复制布局
        for (PayTimesBean timesBean : list) {
            LayoutInflater inflater = LayoutInflater.from(this);

            View copiedLayout = inflater.inflate(R.layout.item_money_list, llMoneyList, false);

            // 获取TextViews
            TextView txtTime = copiedLayout.findViewById(R.id.txt_time);
            TextView txtMoney = copiedLayout.findViewById(R.id.txt_money);
            TextView txtId = copiedLayout.findViewById(R.id.txt_pay_id);

            // 设置新的属性
            txtTime.setText(timesBean.getTime() + "分钟");
            txtMoney.setText("￥" + timesBean.getPayMoney());
            txtId.setText(String.valueOf(timesBean.getId()));
            // 添加到父LinearLayout
            llMoneyList.addView(copiedLayout);
        }
        setMoneyClick();

    }

    private void setMoneyClick() {
        LinearLayout llMoneyList = findViewById(R.id.ll_money_list);
        for (int i = 0; i < llMoneyList.getChildCount(); i++) {
            View child = llMoneyList.getChildAt(i);
            child.setOnClickListener(v1 -> {
                otherMoneyUncheck();
                TextView text = child.findViewById(R.id.txt_pay_id);
                this.payId = Integer.valueOf(text.getText().toString());

                TextView txtMoneySelect = child.findViewById(R.id.txt_money);
                this.amount = Double.valueOf(txtMoneySelect.getText().toString().replace("￥", ""));
                if (Double.valueOf(binding.txtAmount.getText().toString()) <= 0 ||
                        Double.valueOf(binding.txtAmount.getText().toString()) < this.amount) {
                    ToastUtils.show("当前余额不足，请联系品牌商进行充值");
                    return;
                }
                showConfirmDialog(true);


                child.setBackground(getResources().getDrawable(R.mipmap.money_select));
                ImageView img = child.findViewById(R.id.img_time_select);
                img.setImageResource(R.mipmap.money_time_select);

            });
        }
    }

    private void otherMoneyUncheck() {
        LinearLayout llMoneyList = findViewById(R.id.ll_money_list);
        for (int i = 0; i < llMoneyList.getChildCount(); i++) {
            View child = llMoneyList.getChildAt(i);
            child.setBackground(getResources().getDrawable(R.mipmap.money_default));
            ImageView img = child.findViewById(R.id.img_time_select);
            img.setImageResource(R.mipmap.money_time);

        }
    }


    private void removeMoneyClick() {
        LinearLayout llMoneyList = findViewById(R.id.ll_money_list);
        for (int i = 0; i < llMoneyList.getChildCount(); i++) {
            View v = llMoneyList.getChildAt(i);
            v.setOnClickListener(null);
        }
    }

    private void showConfirmDialog(Boolean show) {
        if (chooseDialog == null) {
            chooseDialog = new ChooseDialog.Builder(this).setTitle("提示").setContent("是否使用当前余额进行解锁？").setBack(view -> {
            }).setConfirm(view -> {
                chooseDialog.dismiss();
                ToastUtils.show("加载中,请稍后");
                removeMoneyClick();
                getToken();

            }).create();
        }
        if (show && !chooseDialog.isShowing() && !this.isFinishing()) {
            chooseDialog.show();
        }
    }

    /**
     * 显示提示弹窗
     */
    private PopupWindow popupWindow;

    private void createPop() {
        dismissPop();
        AutoSizeConfig.getInstance().restart();
        View view = LayoutInflater.from(this).inflate(R.layout.unm_pop_help_layout, null);
        popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setOutsideTouchable(true);
//        popupWindow.showAsDropDown(binding.helpIv);
        view.findViewById(R.id.freshBtn).setOnClickListener(view1 -> {
            dismissPop();
//            checkMsg();
        });
    }

    String direct;//指令

//    private void checkMsg() {
//        Map<String, Object> map = new HashMap<>();
//        map.put("code", code);
//        map.put("command", direct);
//        List<String> commands= Arrays.asList( "SYSTEM_DEVICE_ACCOUNT_UNLOCK","SYSTEM_DEVICE_PAY_UNLOCK","SYSTEM_DEVICE_PRODUCT_UNLOCK");
//        HttpUtil.request(API.checkMsg, HttpMethod.GET, false, map, (HttpUtil.CallBack<CommandLogBean>) data -> {
//            if (data != null) {
//                if (!commands.contains(data.getCommand())){
//                    return;
//                }
//                String content = data.getContent();
//                CommandBean.content content1 = JSON.parseObject(content, CommandBean.content.class);
//                String data1 = content1.getData();
//                Integer time = JSON.parseObject(data1).getInteger("time");
//                changeCommandState(Long.valueOf(data.getCommandId()), time);
//            }
//        }, CommandLogBean.class, false, dialog, this);
//    }

    /**
     * 关闭提示弹窗
     */
    private void dismissPop() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
    }

    /**
     * 余额抵扣
     */
    private void amountPay() {
//        view.setEnabled(false);
        Map<String, Object> map = new HashMap<>();
        map.put("code", code);
        map.put("payId", payId);
        map.put("token", token);
        HttpUtil.request(API.confirmAmountPay, HttpMethod.POST, false, map, new HttpUtil.CallBack<String>() {
            @Override
            public void setResult(String data) {
//                view.setEnabled(true);
            }
        }, String.class, false, dialog, this);
    }


    private void getToken() {
        Map<String, Object> map = new HashMap<>();
        map.put("code", code);
        HttpUtil.request(API.getAmountToken, HttpMethod.GET, false, map, (HttpUtil.CallBack<String>) data -> {
            JSONObject jsonObject = JSON.parseObject(data);
            token = jsonObject.getLong("token");
            BigDecimal amount = (BigDecimal) jsonObject.getBigDecimal("amount");
            amountPay();
            BigDecimal txtAmont = new BigDecimal(binding.txtAmount.getText().toString());
            BigDecimal amountPrice = new BigDecimal(this.amount);
            binding.txtAmount.setText(txtAmont.subtract(amountPrice).doubleValue()+"");
        }, String.class, false, dialog, this);
    }

    private void getAmount() {
        Map<String, Object> map = new HashMap<>();
        map.put("code", code);
        HttpUtil.request(API.getAmountToken, HttpMethod.GET, false, map, (HttpUtil.CallBack<String>) data -> {
            JSONObject jsonObject = JSON.parseObject(data);
            if (jsonObject == null) {
                return;
            }
            binding.txtAmount.setText(jsonObject.getString("amount"));

        }, String.class, false, dialog, this);
    }

    private void getPayUrl(String POST_URL) {
        Map<String, Object> map = new HashMap<>();
        map.put("code", code);
        map.put("payId", payTimesBean.getId());
        HttpUtil.request(POST_URL, HttpMethod.GET, false, map, (HttpUtil.CallBack<PayUrlBean>) data ->
                setQrView(data.getUrl()), PayUrlBean.class, false, dialog, this);
    }

    private void getUnlockUrl() {
        Map<String, Object> map = new HashMap<>();
        map.put("code", code);
        map.put("modelCode", Constants.modelCode);
        HttpUtil.request(API.getUnlockQrCode, HttpMethod.GET, false, map, (HttpUtil.CallBack<QrBean>) data ->
                setQrView(data.getUrl()), QrBean.class, false, dialog, this);
    }

    private void setQrView(String url) {
        Bitmap bitmap = QRCodeUtil.createQRCodeBitmap(url, 184, 184, versionName);
//        Glide.with(this)
//                .load(bitmap)
//                .into(binding.qr);
    }

    @Override
    protected void onPause() {
        super.onPause();
//        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onResume() {
//        initData();
//        EventBus.getDefault().register(this);
//        uploadUseStopTime();
//        ActivityManager.finishActivity(ActivityPart.class.getSimpleName());
        super.onResume();
    }

    /**
     * 收到指令处理
     *
     * @param
     */
    public void commandExecute(CommandStatusBean command, PayActivity activity) {
        if (command == null) {
            return;
        }
        Log.e("onReceiveMsg", command.toString());
        //初始化content和data对象
        switch (command.getData().getCommand()) {
//                余额抵扣
            case "SYSTEM_DEVICE_ACCOUNT_UNLOCK":
            case "SYSTEM_DEVICE_PAY_UNLOCK":
            case "SYSTEM_DEVICE_PRODUCT_UNLOCK":
                changeCommandState(command);
                break;
//                设备重置，恢复出厂设置，删除本地文件，然后重启
//            case "SYSTEM_DEVICE_RESET":
//                //reset();
//                break;
//                设备锁定
//            case "SYSTEM_DEVICE_LOCK":
//                lock();
//                break;
//                设备解除锁定
//            case "SYSTEM_DEVICE_UN_LOCK":
//                PropertiesUtils.setValue(Constants.LOCAL_IS_LOCK, Constants.FALSE);
//                initData();
//                break;
            default:

                break;
        }
    }
    private void changeCommandState(CommandStatusBean command){
        JSONObject comandData = JSON.parseObject(command.getData().getContent());
        ToastUtils.show("正在处理，请稍后");
        Map<String, Object> map = new HashMap<>();
        map.put("commandId", command.getData().getCommandId());
        //处理完毕之后修改状态
        Integer time = comandData.getJSONObject("data").getInteger("time");
        Long commandId = Long.valueOf(command.getData().getCommandId());

        // 创建 Handler 对象
        final Handler handler = new Handler(Looper.getMainLooper());

        // 定义 Runnable 任务
        final Runnable runnable = () -> changeCommandState(commandId, time);
        HttpUtil.request(this, API.changeCommandState, HttpMethod.GET, map, Boolean.class, new HttpUtil.CallBackResult<Boolean>() {
            @Override
            public void success(Boolean aBoolean) {
                handler.removeCallbacks(runnable); // 取消延迟任务
                changeCommandState(commandId, time);
            }

            @Override
            public void fail(String msg) {
                handler.removeCallbacks(runnable); // 取消延迟任务
                changeCommandState(commandId, time);
            }
        });
        // 使用 postDelayed 方法来延迟五秒后执行 Runnable
        handler.postDelayed(runnable, 1000); // 5000 毫秒 = 5 秒
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this);
        }
    }

    private void changeCommandState(Long commandId, Integer time) {
        Intent intent = new Intent(PayActivity.this, ActivityWorking.class);
        intent.putExtra("cid", cId);
        intent.putExtra("useTime", time);
        intent.putExtra("commandId", commandId.intValue());
        intent.putExtra("unlockType", payType);
        this.finish();
        startActivity(intent);
    }

    /**
     * 重置
     */
    private void reset() {
        //deletefile
        PropertiesUtils.cleanProFile();
//        MPush.I.unbindAccount();
//        BaseApplication.getInstance().setInitMpush(false);
//                restart
        ActivityManager.finishAllActivity();
        Intent intent1 = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
        intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent1);
    }


}