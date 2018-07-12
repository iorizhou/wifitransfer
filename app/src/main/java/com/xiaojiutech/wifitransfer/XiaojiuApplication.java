package com.xiaojiutech.wifitransfer;

import android.app.Application;
import android.content.Context;

public class XiaojiuApplication extends  Application{
    private static XiaojiuApplication sInstance;
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
    }

    public static XiaojiuApplication getInstace(){
        return sInstance;
    }
}
