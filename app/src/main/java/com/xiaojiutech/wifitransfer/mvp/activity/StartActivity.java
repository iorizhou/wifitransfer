package com.xiaojiutech.wifitransfer.mvp.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;

import com.google.android.gms.ads.MobileAds;
import com.xiaojiutech.wifitransfer.R;

public class StartActivity extends Activity {

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            startActivity(new Intent(StartActivity.this,MainActivity.class));
            finish();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        MobileAds.initialize(this, "ca-app-pub-4536670654281494~6611696922");
        mHandler.sendEmptyMessageDelayed(0,1500);
    }
}
