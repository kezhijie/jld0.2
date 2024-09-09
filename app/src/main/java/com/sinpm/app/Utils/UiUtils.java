package com.sinpm.app.Utils;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.widget.TextView;


import com.sinpm.app.R;
import com.sinpm.app.base.PingResult;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Locale;


public class UiUtils {
    /**
     * 控件点击缩放特效
     * @param view 要缩放的控件view
     */
    public static void startAnimator(View view){
        ObjectAnimator objectAnimatorX = new ObjectAnimator().ofFloat(view, "scaleX", 0.9f, 1.0f);
        ObjectAnimator objectAnimatorY = new ObjectAnimator().ofFloat(view, "scaleY", 0.9f, 1.0f);
        objectAnimatorX.setInterpolator(new AccelerateInterpolator());
        objectAnimatorY.setInterpolator(new AccelerateInterpolator());
        AnimatorSet set = new AnimatorSet();
        set.play(objectAnimatorX).with(objectAnimatorY);
        set.start();
    }
    public static int px2dp(Context context,float px){
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (px/scale+0.5f);
    }
    public static PingResult pingCmd(String address, int count, int interval) {
        String ip = address;
        /*try {
            ip = getIp(address);
        } catch (UnknownHostException e) {
//            Log.e(TAG, "parseResult", e);
        }*/
        /*if (ip == null) {
            sleepTime = 2000;
            return new PingResult("", address, "", 0);
        } else {
            sleepTime = 200;
        }*/
        String cmd = String.format(Locale.US, "ping -n -i %f -c %d %s", ((double) interval / 1000), count, ip);
        Process process = null;
        StringBuilder str = new StringBuilder();
        BufferedReader reader = null;
        BufferedReader errorReader = null;
        try {
            process = Runtime.getRuntime().exec(cmd);
            reader = new BufferedReader(new InputStreamReader(
                    process.getInputStream()));
            String line;
            errorReader = new BufferedReader(new InputStreamReader(
                    process.getErrorStream()));
            while ((line = reader.readLine()) != null) {
                str.append(line).append("\n");
            }
            while ((line = errorReader.readLine()) != null) {
                str.append(line);
            }
        } catch (IOException e) {
            Log.e("UIUTILS", "pingCmd", e);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
                if (errorReader != null) {
                    errorReader.close();
                }
                if (process != null) {
                    process.destroy();
                }
            } catch (Exception e) {
                Log.e("UIUTILS", "pingCmd", e);
            }
        }
        return new PingResult(str.toString(), address, ip, interval);
    }
    /**
     * [手机号码] 前三位，后两位，其他隐藏<例子:138********34>
     *
     * @param num
     * @return
     */
    public static String mobilePhone(String num) {

        if (TextUtils.isEmpty(num)) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(num.subSequence(0, 3));
        for (int i = 0; i < num.length() - 5; i++) {
            sb.append("*");
        }
        sb.append(num.substring(num.length() - 2));
        return sb.toString();
    }

    /**
     * 显示正在加载动画
     * @param context
     */
    public static Dialog showLoadingDialog(Context context){
        //创建Dialog并传递style文件
        final Dialog dialog = new Dialog(context, R.style.dialog);
        dialog.setCanceledOnTouchOutside(false);
        // 设置它的ContentView
        dialog.setContentView(R.layout.layout_loading);
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = dialog.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            Window _window = dialog.getWindow();
            WindowManager.LayoutParams params = _window.getAttributes();
            params.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE;
            _window.setAttributes(params);
        }
        return dialog;
    }


    /**
     * 显示正在加载动画
     *
     * @param context
     */
    public static Dialog showLoadingDialog(Context context, int id) {
        //创建Dialog并传递style文件
        final Dialog dialog = new Dialog(context, R.style.dialog);
        dialog.setCanceledOnTouchOutside(false);

        // 设置它的ContentView
        dialog.setContentView(R.layout.layout_loading);

        TextView loadingTextView = dialog.findViewById(R.id.loading_text);
        loadingTextView.setText(id);
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = dialog.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            Window _window = dialog.getWindow();
            WindowManager.LayoutParams params = _window.getAttributes();
            params.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE;
            _window.setAttributes(params);
        }
        return dialog;
    }
    /**
     * 高位补全0
     * @param value
     * @return
     */
    public static String intValueFixZero(int value){
        String result = String.valueOf(value);
        if (value<10&&value>0){
            result = "0"+value;
        }else if (value==0){
            result ="00";
        }
        return result;
    }

    public static String timeConversion(int time) {
        int minutes = 0;
        int sencond = 0;
        int temp = time % 3600;
        int hour = time/3600;
        if (time > 3600) {
            if (temp != 0) {
                if (temp > 60) {
                    minutes = temp / 60;
                    if (temp % 60 != 0) {
                        sencond = temp % 60;
                    }
                } else {
                    sencond = temp;
                }
            }
        } else {
            minutes = time / 60;
            if (time % 60 != 0) {
                sencond = time % 60;
            }
        }
        minutes = minutes + hour*60;
        return ( (minutes<10?("0"+minutes):minutes) + ":" + (sencond<10?("0"+sencond):sencond));
    }
}
