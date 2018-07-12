package com.xiaojiutech.wifitransfer;

import android.app.Application;
import android.content.Context;

import com.google.android.gms.ads.MobileAds;

public class XiaojiuApplication extends  Application{
    private static XiaojiuApplication sInstance;
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        MobileAds.initialize(this, "ca-app-pub-4536670654281494~6611696922");
        sInstance = this;
    }

    public static XiaojiuApplication getInstace(){
        return sInstance;
    }
}
