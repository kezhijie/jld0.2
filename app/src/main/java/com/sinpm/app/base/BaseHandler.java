package com.sinpm.app.base;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.lang.ref.WeakReference;

public class BaseHandler extends Handler {
    private final WeakReference<BaseActivity> activityReference;

    public BaseHandler(BaseActivity activity) {
        super(Looper.getMainLooper());
        activityReference = new WeakReference<>(activity);
    }

    public WeakReference<BaseActivity> getReference() {
        return activityReference;
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        // 处理消息
    }
}