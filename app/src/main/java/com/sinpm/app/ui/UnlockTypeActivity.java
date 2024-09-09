package com.sinpm.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.sinpm.app.R;
import com.sinpm.app.Utils.Constants;
import com.sinpm.app.Utils.HttpMethod;
import com.sinpm.app.Utils.HttpUtil;
import com.sinpm.app.Utils.PropertiesUtils;
import com.sinpm.app.Utils.SpaceItemDecoration;
import com.sinpm.app.Utils.UiUtils;
import com.sinpm.app.base.API;
import com.sinpm.app.base.ActivityManager;
import com.sinpm.app.base.BaseActivity;
import com.sinpm.app.beans.DeviceUnLockDTO;
import com.sinpm.app.beans.PayTimesBean;
import com.sinpm.app.databinding.ActivityUnlockTypeBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.jessyan.autosize.internal.CustomAdapt;

public class UnlockTypeActivity extends BaseActivity {
    ActivityUnlockTypeBinding binding;
    TimeChooseAdapter timeChooseAdapter;
    PayWayAdapter payWayAdapter;
    List<PayTimesBean> timesBeanList = new ArrayList<>();
    List<DeviceUnLockDTO> paywaysList = new ArrayList<>();
    String code;
    Integer cId, defaultTime = 30;
    DeviceUnLockDTO deviceUnLockDTO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUnlockTypeBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
        code = PropertiesUtils.getValue(Constants.LOCAL_CODE,"");
        cId = getIntent().getIntExtra("cid", 0);
        defaultTime = getIntent().getIntExtra("defaultTime", 30);
        initView();
        getUnlockType(code);


//        ActivityManager.finishActivity(ActivityWorking.class.getSimpleName());
    }

    @Override
    protected boolean isNetworkConnected() {
        getUnlockType(this.code);
        return true;
    }

    @Override
    public void finish() {
        String value = PropertiesUtils.getValue(Constants.customer_login, "1");
        if (Integer.valueOf(value) == 0) {
            return;
        }
        super.finish();
    }

    private void initView() {
        binding.back.setOnClickListener(view -> {
            this.finish();

        });

        binding.rlRefresh.setOnClickListener(v -> {
           getUnlockType(this.code);
        });
        binding.set.setOnClickListener(view -> {
            UiUtils.startAnimator(view);
            Intent intent = new Intent(UnlockTypeActivity.this, ActivitySetting.class);
            startActivity(intent);
        });
        //时间选择
        timeChooseAdapter = new TimeChooseAdapter(this, timesBeanList);
        timeChooseAdapter.setOnItemClickListener(positon -> {
            Intent intent = new Intent(UnlockTypeActivity.this, PayActivity.class);
            intent.putExtra(Constants.INTENT_PAY_INFO, JSON.toJSONString(timesBeanList.get(positon)));
            intent.putExtra(Constants.INTENT_PAYUNLOCK_TYPE, JSON.toJSONString(deviceUnLockDTO));
            intent.putExtra("cid", cId);
            startActivity(intent);
        });
        binding.timeRecycleView.addItemDecoration(new SpaceItemDecoration(UiUtils.px2dp(this, 32)));
        binding.timeRecycleView.setAdapter(timeChooseAdapter);
        //解锁方式选择
        payWayAdapter = new PayWayAdapter(paywaysList, this);
        payWayAdapter.setOnItemClickListener(positon -> {
           switchUnlockType(positon);
        });
        if (paywaysList!=null && paywaysList.size()==1){
            switchUnlockType(0);
        }

        binding.paywaysRv.addItemDecoration(new SpaceItemDecoration(UiUtils.px2dp(this, 32)));
        binding.paywaysRv.setAdapter(payWayAdapter);
    }

    private void switchUnlockType(Integer index){
        deviceUnLockDTO = paywaysList.get(index);
        if (deviceUnLockDTO.getType() == 3) {//套盒解锁
            Intent intent = new Intent(UnlockTypeActivity.this, PayActivity.class);
            intent.putExtra(Constants.INTENT_PAYUNLOCK_TYPE, JSON.toJSONString(deviceUnLockDTO));
            intent.putExtra("cid", cId);
            startActivity(intent);
            return;
        }
        getTimes(deviceUnLockDTO.getType());
    }

    private void getUnlockType(String code) {
        Map<String, Object> map = new HashMap<>();
        map.put("code", code);
        HttpUtil.request(API.getUnlockType, HttpMethod.GET, false, map, (HttpUtil.CallBack<List<DeviceUnLockDTO>>) data -> {
            if (data==null || data.size()==0 ||  data.get(0).getType() == 0) {//无锁
                Intent intent = new Intent(UnlockTypeActivity.this, ActivityWorking.class);
                intent.putExtra("cid", cId);
                intent.putExtra("useTime", defaultTime);
                startActivity(intent);
                return;
            }
            binding.llRefresh.setVisibility(View.GONE);
            binding.paywaysRv.setLayoutManager(new GridLayoutManager(this, data.size() > 2 ? 3 : data.size()));
            paywaysList.clear();
            paywaysList.addAll(data);
            payWayAdapter.notifyDataSetChanged();
            if (paywaysList!=null && paywaysList.size()==1){
                switchUnlockType(0);
            }
            else {
                binding.paywaysRv.setVisibility(View.VISIBLE);
            }

        }, DeviceUnLockDTO.class, true, dialog, this);
    }

    private void getTimes(Integer type) {
        Map<String, Object> map = new HashMap<>();
        map.put("code", code);
        map.put("payType", type);
        HttpUtil.request(API.getPayTime, HttpMethod.GET, false, map, (HttpUtil.CallBack<List<PayTimesBean>>) data -> {
            binding.paywaysRv.setVisibility(View.GONE);
            binding.timeRecycleView.setVisibility(View.VISIBLE);
            binding.timeRecycleView.setLayoutManager(new GridLayoutManager(this, data.size() > 2 ? 3 : data.size()));
            timesBeanList.clear();
            timesBeanList.addAll(data);
            timeChooseAdapter.notifyDataSetChanged();
        }, PayTimesBean.class, true, dialog, this);
    }

    @Override
    protected void onResume() {
        binding.llRefresh.setVisibility(View.VISIBLE);
//        ActivityManager.finishActivity(ActivityWorking.class.getSimpleName());
        super.onResume();
        getUnlockType(this.code);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void netWorkReady(boolean isReady, int state) {

    }

    @Override
    protected void netWorkFail(int state) {

    }


}