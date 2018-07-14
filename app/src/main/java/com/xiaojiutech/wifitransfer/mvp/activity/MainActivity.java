package com.xiaojiutech.wifitransfer.mvp.activity;

import android.Manifest;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTabHost;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.gms.ads.InterstitialAd;
import com.xiaojiutech.wifitransfer.R;
import com.xiaojiutech.wifitransfer.mvp.activity.fragment.BaseFragment;
import com.xiaojiutech.wifitransfer.mvp.activity.fragment.FileHistoryRecvFragment;
import com.xiaojiutech.wifitransfer.mvp.activity.fragment.FileHistorySendFragment;
import com.xiaojiutech.wifitransfer.mvp.activity.fragment.FunctionFragment;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends BaseFragmentActivity implements EasyPermissions.PermissionCallbacks{

    public static final String TAG = "MainActivity";
    private Fragment mCurFragment;
    private LinearLayout mLayout1,mLayout2,mLayout3;
    private BaseFragment mFuntion,mSend,mRecv;
    FragmentManager mFragmentManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLayout1 = (LinearLayout)findViewById(R.id.frag1);
        mLayout2 = (LinearLayout)findViewById(R.id.frag2);
        mLayout3 = (LinearLayout)findViewById(R.id.frag3);
        mFragmentManager = getSupportFragmentManager();
        mFuntion = new FunctionFragment();
        mSend = new FileHistorySendFragment();
        mRecv = new FileHistoryRecvFragment();
        mLayout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction mTransaction = mFragmentManager.beginTransaction();
                addOrShowFragment(mTransaction,mFuntion,R.id.details,"function");
            }
        });
        mLayout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction mTransaction = mFragmentManager.beginTransaction();
                addOrShowFragment(mTransaction,mSend,R.id.details,"send");
            }
        });
        mLayout3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction mTransaction = mFragmentManager.beginTransaction();
                addOrShowFragment(mTransaction,mRecv,R.id.details,"recv");
            }
        });
//        mCurFragment = mFuntion;
//        mLayout1.performClick();
        mFragmentManager.beginTransaction().add(R.id.details, mFuntion,"funtion").commitAllowingStateLoss();
        mCurFragment = mFuntion;
        requireSomePermission();
    }



    /**
     * 添加或者显示 fragment
     *
     * @param fragment
     */
    private void addOrShowFragment(FragmentTransaction transaction, BaseFragment fragment, int id,String tag) {


        if(mCurFragment == fragment)
            return;
        if (!fragment.isAdded()) { // 如果当前fragment未被添加，则添加到Fragment管理器中
            transaction.hide(mCurFragment).add(id, fragment,tag).commitAllowingStateLoss();
        } else {
            transaction.hide(mCurFragment).show(fragment).commitAllowingStateLoss();
        }
        if (mCurFragment!=null){
            mCurFragment.setUserVisibleHint(false);
        }
        mCurFragment =  fragment;
        if (mCurFragment!=null){
            mCurFragment.setUserVisibleHint(true);
        }
    }


    @AfterPermissionGranted(1000)
    private void requireSomePermission() {
        String[] perms = {
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
        };
        if (EasyPermissions.hasPermissions(this, perms)) {
            //有权限
        } else {
            //没权限
            EasyPermissions.requestPermissions(this, "需要文件读取权限",
                    1000, perms);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    /**
     * 权限申成功
     * @param i
     * @param list
     */
    @Override
    public void onPermissionsGranted(int i, @NonNull List<String> list) {
        Log.e(TAG,"权限申成功");
    }

    /**
     * 权限申请失败
     * @param i
     * @param list
     */
    @Override
    public void onPermissionsDenied(int i, @NonNull List<String> list) {
        Log.e(TAG,"权限申请失败");
    }
}

