package com.sinpm.app.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;

import androidx.annotation.Nullable;

import com.sinpm.app.base.BaseActivityWithTask;
import com.sinpm.app.beans.EventBean;
import com.sinpm.app.databinding.ActivityLockBinding;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class LockActivity extends BaseActivityWithTask {
    ActivityLockBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }
        binding = ActivityLockBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
        startTimer(this);
    }

    @Override
    public Object setTask() {
        return null;
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // 返回键被按下
            return true; // 禁用返回键
        } else if (keyCode == KeyEvent.KEYCODE_HOME) {
            // Home键被按下
            return true; // 禁用Home键
        }

        return super.onKeyDown(keyCode, event);
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventBean event) {
        if ("finish".equals(event.getType())){
            finish();
        }
    };
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this);
        }
    }
}
