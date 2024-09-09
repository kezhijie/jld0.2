package com.sinpm.app.Utils;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

public class MyActivityLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {
    private volatile boolean isForeground = false;

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    }

    @Override
    public void onActivityStarted(Activity activity) {
    }

    @Override
    public void onActivityResumed(Activity activity) {
        isForeground = true;
    }

    @Override
    public void onActivityPaused(Activity activity) {
        isForeground = false;
    }

    @Override
    public void onActivityStopped(Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
    }

    public boolean isAppInForeground() {
        return isForeground;
    }
}
