package com.xiaojiutech.wifitransfer.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.xiaojiutech.wifitransfer.ProgressDialog;
import com.xiaojiutech.wifitransfer.R;

public class CustomProgressDialog extends Dialog{
    private View mView;
    private Context mContext;
    private TextView mTip;
    private AdView mBannerAd;
    private ProgressBar mProgress;
    public CustomProgressDialog(@NonNull Context context) {

        super(context);
        this.mContext = context;
    }

    public CustomProgressDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.mContext = context;
    }


    public void setMessage(String msg){
        mTip.setText(msg);
    }

    public void setProgress(int progress){
        mProgress.setProgress(progress);
    }

    private void showBannerAd(AdListener listener){
        AdRequest request = new AdRequest.Builder().build();
        mBannerAd.setAdListener(listener);
        mBannerAd.loadAd(request);
    }

    public void show(Context context, CharSequence message, boolean cancelable, OnCancelListener cancelListener,AdListener adListener) {
        setTitle("");
        setContentView(R.layout.file_progress_view);
        mTip = (TextView)findViewById(R.id.tip);
        mBannerAd = new AdView(context);
        mBannerAd.setAdUnitId("ca-app-pub-4536670654281494/8674220650");
        mBannerAd.setAdSize(new AdSize(200,50));
        mProgress = (ProgressBar)findViewById(R.id.progress) ;
        mTip.setText(message);
        mProgress.setProgress(0);
        //开始加载广告
        showBannerAd(adListener);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.BOTTOM;
        addContentView(mBannerAd,params);
        // 按返回键是否取消
        setCancelable(cancelable);
        // 监听返回键处理
        setOnCancelListener(cancelListener);
        // 设置居中
        getWindow().getAttributes().gravity = Gravity.CENTER;
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        Window dialogWindow = getWindow();
        WindowManager m = ((Activity)mContext).getWindowManager();
        Display d = m.getDefaultDisplay();
        lp.width = (int) (d.getWidth() * 0.9);
        // 设置背景层透明度
        lp.dimAmount = 0.2f;
        getWindow().setAttributes(lp);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);

        show();
    }

}
