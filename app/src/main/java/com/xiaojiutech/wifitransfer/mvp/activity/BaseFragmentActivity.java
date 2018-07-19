package com.xiaojiutech.wifitransfer.mvp.activity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.google.android.gms.ads.InterstitialAd;
import com.xiaojiutech.wifitransfer.utils.ActivityHolder;

import java.util.Locale;

public class BaseFragmentActivity extends FragmentActivity {


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        ActivityHolder.getInstance().addActivity(this);
        super.onCreate(savedInstanceState, persistentState);
    }

    @Override
    protected void onDestroy() {
        ActivityHolder.getInstance().removeActivity(this);
        super.onDestroy();
    }
}
