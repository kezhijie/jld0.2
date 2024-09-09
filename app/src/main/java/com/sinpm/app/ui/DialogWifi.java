package com.sinpm.app.ui;

import android.app.Dialog;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.hjq.toast.ToastUtils;
import com.sinpm.app.R;
import com.sinpm.app.base.ActivityManager;
import com.thanosfisherman.wifiutils.WifiUtils;
import com.thanosfisherman.wifiutils.wifiConnect.ConnectionErrorCode;
import com.thanosfisherman.wifiutils.wifiConnect.ConnectionSuccessListener;


public class DialogWifi extends Dialog {

    private final Window window;

    private DialogWifi(Context context, int themeResId) {
        super(context, themeResId);
        window = getWindow();
        window.setBackgroundDrawableResource(android.R.color.transparent);
        LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //加载布局文件
//        View inflate = inflater.inflate(R.layout.dialog_wifi, null, false);

//        inflate.findViewById(R.id.txt_password).requestFocus();
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);


    }

    public static class Builder {

        private final WifiManager wifiManager;
        private final View mLayout;

        private final TextView ssidText;
        private final TextView content;
        private final RelativeLayout cancel;
        private final RelativeLayout confirm;
        private final TextView txtConfirm;
        private final TextView txtCancel;
        private final TextView txtPassword;

        private View.OnClickListener cancleListener;
        private View.OnClickListener confirmListener;

        private final DialogWifi mDialog;
        private ScanResult scanResult;

        private Window window;
        private final Context context;

        public Builder(Context context) {
            this.context = context;
            mDialog = new DialogWifi(context, androidx.appcompat.R.style.Theme_AppCompat_Dialog);
            LayoutInflater inflater =
                    (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //加载布局文件
            mLayout = null;
            //添加布局文件到 Dialog
            mDialog.addContentView(mLayout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));

            ssidText = mLayout.findViewById(R.id.txt_wifi_ssid);
            content = mLayout.findViewById(R.id.content);
            cancel = mLayout.findViewById(R.id.cancel);
            confirm = mLayout.findViewById(R.id.confirm);
            txtPassword = mLayout.findViewById(R.id.txt_password);
            txtConfirm = mLayout.findViewById(R.id.txt_confirm);

            txtCancel = mLayout.findViewById(R.id.txt_cancel);

            ImageView imageView = mLayout.findViewById(R.id.tip);
            if (imageView != null) {
                imageView.setImageResource(R.mipmap.tip_warning);
            }
            txtPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        // 在这里执行完成操作
                        confirm.performClick();
                        return true;
                    }
                    return false;
                }
            });


            wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        }

        /**
         * 设置 Dialog 标题
         */
        public Builder setSsid(ScanResult ssid) {
            if (ssidText == null || ssid == null) {
                return this;
            }
            this.scanResult = ssid;
            ssidText.setText("请输入" + ssid.SSID + "密码");
            ssidText.setVisibility(View.VISIBLE);
            return this;
        }

        public Builder setButtonText(String confirm, String cancel) {
//            txtConfirm.setText(confirm);
//            txtCancel.setText(cancel);
            return this;
        }

        public Builder setContent(String str) {
            content.setText(str);
            return this;
        }

        /**
         * 设置按钮文字和监听
         */
        public Builder setBack(View.OnClickListener listener) {
            cancleListener = listener;
            return this;
        }

        public Builder setBackVis(Boolean vis) {
            cancel.setVisibility(vis ? View.VISIBLE : View.GONE);
            return this;
        }

        public Builder setConfirm(View.OnClickListener listener) {
            confirmListener = listener;
            return this;
        }

        private Object getSystemService(String inputMethodService) {
            return null;
        }


        public DialogWifi create() {
            cancel.setOnClickListener(view -> {
                mDialog.dismiss();
                cancleListener.onClick(view);
            });
            confirm.setOnClickListener(view -> {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);


                mDialog.dismiss();
                confirmListener.onClick(view);
                WifiConfiguration wifiConfig = new WifiConfiguration();

                // 设置要连接的网络的SSID和密码
                wifiConfig.SSID = scanResult.SSID.replace("\"", "");
                wifiConfig.preSharedKey = txtPassword.getText().toString();
                if (ActivityManager.isDebug(context)) {
                    wifiConfig.preSharedKey = "sscz090619";
                }
                WifiUtils.withContext(context)
                        .connectWith(wifiConfig.SSID, wifiConfig.preSharedKey)
                        .onConnectionResult(new ConnectionSuccessListener() {
                            @Override
                            public void success() {
//                                ToastUtils.show("连接成功");
                            }

                            @Override
                            public void failed(@NonNull ConnectionErrorCode errorCode) {
                                String msg = "连接失败,请检查密码是否正确或者网络是否通畅";
                                ToastUtils.show(msg);
                            }

                        })
                        .start();

            });
            mDialog.setContentView(mLayout);
            mDialog.setCancelable(true);                //用户可以点击后退键关闭 Dialog
            mDialog.setCanceledOnTouchOutside(true);   //用户不可以点击外部来关闭 Dialog
            return mDialog;
        }
    }

    @Override
    public void show() {
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        super.show();
        fullScreenImmersive(getWindow().getDecorView());
        this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
    }

    private void fullScreenImmersive(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_FULLSCREEN;
            view.setSystemUiVisibility(uiOptions);
        }
    }
}
