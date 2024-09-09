package com.sinpm.app.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.sinpm.app.R;


public class SingleDialog extends Dialog {

    private SingleDialog(Context context, int themeResId) {
        super(context, themeResId);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);

    }

    public static class Builder {

        private final View mLayout;

        private TextView mTitle;
        private final TextView content;
        private final Button confirm;
//        private final TextView txtConfirm;
//        private final TextView txtCancel;

        private View.OnClickListener confirmListener;

        private final SingleDialog mDialog;

        public Builder(Context context) {
            mDialog = new SingleDialog(context, R.style.dialog);

            mDialog.setContentView(R.layout.single_dialog);
            LayoutInflater inflater =
                    (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //加载布局文件
            mLayout = inflater.inflate(R.layout.single_dialog, null, false);
            //添加布局文件到 Dialog
            mDialog.addContentView(mLayout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));

//            mTitle = mLayout.findViewById(R.id.dialog_title);
//            mTitle = mLayout.findViewById(R.id.title);
            content = mLayout.findViewById(R.id.content);
            confirm = mLayout.findViewById(R.id.confirm);

//            txtConfirm=mLayout.findViewById(R.id.txt_confirm);
//
//            txtCancel=mLayout.findViewById(R.id.txt_cancel);
//            ImageView imageView = mLayout.findViewById(R.id.tip);
//            if (imageView!=null){
//                imageView.setImageResource(R.mipmap.tip_warning);
//            }

        }

        /**
         * 设置 Dialog 标题
         */
        public Builder setTitle(@NonNull String title) {
            if (mTitle==null){return this; }
            mTitle.setText(title);
            mTitle.setVisibility(View.VISIBLE);
            return this;
        }

        public Builder setButtonText(String confirmText,String cancelText) {
            confirm.setText(confirmText);
            return this;
        }
        public Builder setContent(String str){
            content.setText(str);
            return this;
        }
        public Builder setBackVis(Boolean vis){
            return this;
        }
        public Builder setConfirm(View.OnClickListener listener){
            confirmListener = listener;
            return this;
        }

        public SingleDialog create() {
            confirm.setOnClickListener(view -> {
                mDialog.dismiss();
                confirmListener.onClick(view);
            });

            mDialog.setContentView(mLayout);
            mDialog.setCancelable(true);                //用户可以点击后退键关闭 Dialog
            mDialog.setCanceledOnTouchOutside(false);   //用户不可以点击外部来关闭 Dialog
            return mDialog;
        }
    }

    @Override
    public void show() {

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        super.show();
        fullScreenImmersive(getWindow().getDecorView());
//        this.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

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
