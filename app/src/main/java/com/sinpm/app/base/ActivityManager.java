package com.sinpm.app.base;

/**
 * creat by yanmi  on 2021/11/17.
 * Describe:
 * 公司： 赫拉科技
 */

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ApplicationInfo;

import java.util.HashMap;
import java.util.Map;

public class ActivityManager {
    private static final Map<String, Activity> activityMap = new HashMap<>();
    private static ActivityManager instance;

    private ActivityManager() {
    }

    public static boolean isDebug(Context context) {
        if (context == null) {
            return false;
        }
        if (context.getApplicationInfo()==null){
            return false;
        }
        //Debug 模式是打开状态
        return 0 != (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE);
    }

    public static ActivityManager getInstance() {
        if (instance == null) {
            synchronized (ActivityManager.class) {
                if (instance == null) {
                    instance = new ActivityManager();
                }
            }
        }
        return instance;
    }


    public static boolean isActivityOnTop(Context context, Class<?> activityClass) {
        android.app.ActivityManager activityManager = (android.app.ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null) {
            // 获取当前运行的任务栈信息
            android.app.ActivityManager.RunningTaskInfo taskInfo = activityManager.getRunningTasks(1).get(0);
            // 获取栈顶的 Activity
            ComponentName topActivity = taskInfo.topActivity;
            // 检查栈顶的 Activity 是否是要判断的 Activity
            return topActivity.getClassName().equals(activityClass.getName());
        }
        return false;
    }

    public void putActivity(String name, Activity activity) {
        activityMap.put(name, activity);
    }

    public static void finishAllActivity() {
        for (Map.Entry<String, Activity> entry : activityMap.entrySet()) {
            if (entry.getValue() != null) {
                entry.getValue().finish();
            }
        }
    }

    public boolean isActivityExist(String name) {
        return activityMap.get(name) != null;
    }

    public void removeActivity(String name) {
        activityMap.remove(name);
    }

    public static void finishActivity(Class cls) {
        if (activityMap.get(cls.getSimpleName()) != null) {
            activityMap.get(cls.getSimpleName()).finish();
        }
    }
    public static void finishActivity(String name) {
        if (activityMap.get(name) != null) {
            activityMap.get(name).finish();
        }
    }

    public static void finishWithOutActivity(String name) {
        for (Map.Entry<String, Activity> entry : activityMap.entrySet()) {
            Activity activity = entry.getValue();
            if (!entry.getKey().equals(name) && activity != null) {
                activity.finish();
            }
        }
    }
}
