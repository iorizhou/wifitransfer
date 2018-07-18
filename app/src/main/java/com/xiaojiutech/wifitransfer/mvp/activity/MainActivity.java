package com.xiaojiutech.wifitransfer.mvp.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Process;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.autoupdatesdk.AppUpdateInfo;
import com.baidu.autoupdatesdk.AppUpdateInfoForInstall;
import com.baidu.autoupdatesdk.BDAutoUpdateSDK;
import com.baidu.autoupdatesdk.CPCheckUpdateCallback;
import com.baidu.autoupdatesdk.CPUpdateDownloadCallback;
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
    private long mPressBackTime =0;
    private TextView mText1,mText2,mText3;
    private ImageView mImg1,mImg2,mImg3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLayout1 = (LinearLayout)findViewById(R.id.frag1);
        mLayout2 = (LinearLayout)findViewById(R.id.frag2);
        mLayout3 = (LinearLayout)findViewById(R.id.frag3);
        mText1 = (TextView)findViewById(R.id.text1);
        mText2 = (TextView)findViewById(R.id.text2);
        mText3 = (TextView)findViewById(R.id.text3);
        mImg1 = (ImageView)findViewById(R.id.img1);
        mImg2 = (ImageView)findViewById(R.id.img2);
        mImg3 = (ImageView)findViewById(R.id.img3);
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
        mFragmentManager.beginTransaction().add(R.id.details, mFuntion,"function").commitAllowingStateLoss();
        mCurFragment = mFuntion;
        updateDocker();
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
        updateDocker();
    }


    public void updateDocker(){
        if (mCurFragment.getTag().equals("function")){
            mText1.setTextColor(getResources().getColor(R.color.netease_white));
            mText2.setTextColor(getResources().getColor(R.color.netease_gray));
            mText3.setTextColor(getResources().getColor(R.color.netease_gray));
            mImg1.setVisibility(View.VISIBLE);
            mImg2.setVisibility(View.GONE);
            mImg3.setVisibility(View.GONE);
        }else if (mCurFragment.getTag().equals("send")){
            mText2.setTextColor(getResources().getColor(R.color.netease_white));
            mText1.setTextColor(getResources().getColor(R.color.netease_gray));
            mText3.setTextColor(getResources().getColor(R.color.netease_gray));
            mImg2.setVisibility(View.VISIBLE);
            mImg1.setVisibility(View.GONE);
            mImg3.setVisibility(View.GONE);
        }else if (mCurFragment.getTag().equals("recv")){
            mText3.setTextColor(getResources().getColor(R.color.netease_white));
            mText1.setTextColor(getResources().getColor(R.color.netease_gray));
            mText2.setTextColor(getResources().getColor(R.color.netease_gray));
            mImg3.setVisibility(View.VISIBLE);
            mImg1.setVisibility(View.GONE);
            mImg2.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //update check
        BDAutoUpdateSDK.cpUpdateCheck(this, new BDUpdateCallback(), false);
    }

    public class BDUpdateCallback implements CPCheckUpdateCallback {

        @Override
        public void onCheckUpdateCallback(final AppUpdateInfo info, AppUpdateInfoForInstall infoForInstall) {
            if (infoForInstall != null && !TextUtils.isEmpty(infoForInstall.getInstallPath())) {

                BDAutoUpdateSDK.cpUpdateInstall(MainActivity.this, infoForInstall.getInstallPath());
            } else if (info != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                long size = info.getAppPathSize() > 0 ? info.getAppPathSize() : info.getAppSize();
                builder.setTitle(info.getAppVersionCode() + ", " + byteToMb(size))
                        .setMessage(Html.fromHtml(info.getAppChangeLog()))
                        .setNeutralButton("立即升级", null)
                        .setCancelable(info.getForceUpdate() != 1)
                        .setOnKeyListener(new DialogInterface.OnKeyListener() {
                            @Override
                            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                                if (keyCode == KeyEvent.KEYCODE_BACK) {
                                    return true;
                                }
                                return false;
                            }
                        });
                if (info.getForceUpdate() != 1) {
                    builder.setNegativeButton("暂不升级", null);
                }
                AlertDialog dialog = builder.show();

                dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        BDAutoUpdateSDK.cpUpdateDownload(MainActivity.this, info, new CPUpdateDownloadCallback(){
                            @Override
                            public void onDownloadComplete(String apkPath) {
                                Log.i("update_check","onDownloadComplete : "+apkPath);
                                BDAutoUpdateSDK.cpUpdateInstall(getApplicationContext(), apkPath);
                            }

                            @Override
                            public void onStart() {

                            }

                            @Override
                            public void onPercent(int i, long l, long l1) {

                            }

                            @Override
                            public void onFail(Throwable throwable, String s) {

                            }

                            @Override
                            public void onStop() {

                            }
                        });
                    }
                });
            } else {
                Log.i("update_check","no new version");
            }
        }

        private String byteToMb(long fileSize) {
            float size = ((float) fileSize) / (1024f * 1024f);
            return String.format("%.2fMB", size);
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
            EasyPermissions.requestPermissions(this, getString(R.string.file_permission_tip),
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



    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode){
            case KeyEvent.KEYCODE_BACK:
                long secondTime=System.currentTimeMillis();
                if(secondTime- mPressBackTime >2000){
                    Toast.makeText(MainActivity.this,getString(R.string.double_click_exit_app),Toast.LENGTH_SHORT).show();
                    mPressBackTime =secondTime;
                    return true;
                }else{
                    finish();
                    android.os.Process.killProcess(Process.myPid());
                }
                break;
        }
        return super.onKeyUp(keyCode, event);
    }
}

