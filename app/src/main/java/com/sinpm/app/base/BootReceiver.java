package com.sinpm.app.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        /*if("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())){
            Intent intent1 = new Intent(context, LoginActivity.class);
            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent1);
        }*/
    }
}
