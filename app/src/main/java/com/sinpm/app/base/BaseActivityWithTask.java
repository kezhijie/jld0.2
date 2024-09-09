package com.sinpm.app.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.sinpm.app.ui.ActivityWorking;
import com.sinpm.app.ui.LoginActivity;

import java.util.concurrent.ScheduledExecutorService;

public abstract class BaseActivityWithTask extends BaseActivity {

    private Runnable runnableTask;
    private Boolean isStop = false;
    private Boolean isRunning = false;
    private final Boolean isDestroy = false;
    protected Boolean taskReady = false;
    private Handler handlerTask;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopTimer();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void netWorkReady(boolean isReady, int state) {
    }

    @Override
    protected void netWorkFail(int state) {

    }

    @Override
    protected void onPause() {

//        this.stopTimer();
        super.onPause();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        startTimer(this);

    }

    protected void stopTimer() {
        Log.d("stopTimer", "停止执行 ");
        isStop = true;
        if (handlerTask != null) {
            handlerTask.removeCallbacks(runnableTask);
        }
    }

    public abstract Object setTask();


    public void startTimer(Activity activity) {
        startTimer(activity, 3);
    }

    public void startTimer(Activity activity, Integer time) {
        isStop = false;
        if (!ActivityManager.isActivityOnTop(activity, activity.getClass())) {
            stopTimer();
            return;
        }
        // 定义一个 Runnable 对象，该对象用于执行要定时执行的任务
        if (handlerTask == null) {
            handlerTask = new Handler(Looper.getMainLooper());
        }
        if (runnableTask == null) {
            runnableTask = new Runnable() {
                @Override
                public void run() {
                    if (!ActivityManager.isActivityOnTop(activity, activity.getClass())) {
                        Log.d("Timer", "当前Activity已经出栈");
                        stopTimer();
                        return;
                    }
                    // 在此处执行任务的代码
                    // 比如打印日志或者执行其他操作
                    Log.d("Timer", "Task executed every 3 seconds");
                    setTask();
                    isStop = false;
                    // 任务完成后，再次将自身 postDelayed 到消息队列中，实现循环执行
                    handlerTask.postDelayed(this, time * 1000); // 每 3 秒执行一次
                }
            };
        }
        if (!isStop) {

            // 第一次启动定时器
            handlerTask.postDelayed(runnableTask, 100); // 延迟 3 秒后执行
        }
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
    }
}
