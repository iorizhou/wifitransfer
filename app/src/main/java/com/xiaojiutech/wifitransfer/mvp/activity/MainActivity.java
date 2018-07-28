package com.xiaojiutech.wifitransfer.mvp.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
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

import com.alibaba.fastjson.JSONObject;
import com.xiaojiutech.wifitransfer.ProgressDialog;
import com.xiaojiutech.wifitransfer.R;
import com.xiaojiutech.wifitransfer.mvp.activity.fragment.BaseFragment;
import com.xiaojiutech.wifitransfer.mvp.activity.fragment.FileHistoryRecvFragment;
import com.xiaojiutech.wifitransfer.mvp.activity.fragment.FileHistorySendFragment;
import com.xiaojiutech.wifitransfer.mvp.activity.fragment.FunctionFragment;
import com.xiaojiutech.wifitransfer.utils.ActivityHolder;
import com.xiaojiutech.wifitransfer.utils.AlertDialogUtil;
import com.xiaojiutech.wifitransfer.utils.AppUtil;
import com.xiaojiutech.wifitransfer.utils.DownloadTask;
import com.xiaojiutech.wifitransfer.utils.FileOpenIntentUtil;
import com.xiaojiutech.wifitransfer.utils.Md5Util;
import com.xiaojiutech.wifitransfer.utils.OSSUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
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
    private String mUpgradeFileMd5;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 99:
                    try{

                        String jsonStr = (String)msg.obj;
                        final JSONObject jsonObject = JSONObject.parseObject(jsonStr);
                        if (jsonObject!=null){
                            //取versionCode
                            int version = Integer.parseInt(jsonObject.getString("versionCode"));
                            int curVersion = Integer.parseInt(AppUtil.getVersion());
                            mUpgradeFileMd5 = jsonObject.getString("md5");
                            Log.i(TAG,"version = "+version);
                            if (version>curVersion){
                                //有新版本
                                new AlertDialogUtil(MainActivity.this).showAlertDialog(getString(R.string.new_version_tip)+ "\nversion:"+jsonObject.getString("version"), jsonObject.getString("description"), getString(R.string.confirm), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        final ProgressDialog dialog = new ProgressDialog(MainActivity.this);

                                        DownloadTask task = new DownloadTask(MainActivity.this, new DownloadTask.DownloadListener() {
                                            @Override
                                            public void onProgressChanged(final int progress) {
                                                Log.i(TAG,"download = "+progress);
                                               runOnUiThread(new Runnable() {
                                                   @Override
                                                   public void run() {
                                                       dialog.setProgress(progress);
                                                       dialog.setProgressText(progress+"%");
                                                   }
                                               });
                                            }

                                            @Override
                                            public void onCompleted(final String filepath) {
                                                Log.i(TAG,"download complete");
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {

                                                        dialog.dismiss();
                                                    }
                                                });
                                                String fileMd5 = Md5Util.getMd5(new File(filepath));
                                                if (fileMd5.equals(mUpgradeFileMd5)){
                                                    FileOpenIntentUtil.openFile(filepath);
                                                }else {
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            Toast.makeText(MainActivity.this,getString(R.string.upgrade_file_error),Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                }

                                            }

                                            @Override
                                            public void onFailed() {
                                                Log.i(TAG,"download onFailed");
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        dialog.dismiss();
                                                    }
                                                });
                                            }
                                        });
                                        task.execute(jsonObject.getString("apkUrl"),jsonObject.getString("apkUrl").substring(jsonObject.getString("apkUrl").lastIndexOf("/")+1));

//                                        OSSUtil.getInstance().downloadOSSFile(jsonObject.getString("apkUrl"), new OSSUtil.OSSDownladCallback() {
//                                            @Override
//                                            public void onComplete(String url, String localFilePath) {
//                                                Log.i(TAG,"download complete");
//                                                runOnUiThread(new Runnable() {
//                                                    @Override
//                                                    public void run() {
//
//                                                        dialog.dismiss();
//                                                    }
//                                                });
//                                                String fileMd5 = Md5Util.getMd5(new File(localFilePath));
//                                                if (fileMd5.equals(mUpgradeFileMd5)){
//                                                    FileOpenIntentUtil.openFile(localFilePath);
//                                                }else {
//                                                    runOnUiThread(new Runnable() {
//                                                        @Override
//                                                        public void run() {
//                                                            Toast.makeText(MainActivity.this,getString(R.string.upgrade_file_error),Toast.LENGTH_SHORT).show();
//                                                        }
//                                                    });
//                                                }
//                                            }
//
//                                            @Override
//                                            public void onFailure(String url, int errorCode, String errorMsg) {
//                                                Log.i(TAG,"download onFailed");
//                                                runOnUiThread(new Runnable() {
//                                                    @Override
//                                                    public void run() {
//                                                        dialog.dismiss();
//                                                        Toast.makeText(MainActivity.this,getString(R.string.upgrade_file_error),Toast.LENGTH_SHORT).show();
//                                                    }
//                                                });
//                                            }
//
//                                            @Override
//                                            public void onProgress(final int progress) {
//                                                Log.i(TAG,"download = "+progress);
//                                                runOnUiThread(new Runnable() {
//                                                    @Override
//                                                    public void run() {
//                                                        dialog.setProgress(progress);
//                                                        dialog.setProgressText(progress+"%");
//                                                    }
//                                                });
//                                            }
//                                        });


                                    }
                                },jsonObject.getString("isForceUpdate").equals("0")?"取消":null,null,jsonObject.getString("isForceUpdate").equals("0")?true:false);
                            }else {
                                Log.i(TAG,"已是最新版本");
                            }
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };
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
        checkUpdate();
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
    }


    private void checkUpdate(){
        mHandler.removeMessages(99);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    URL url=new URL("http://idc.xiaojiutech.com/wifitransfer/upgrade/android_"+ AppUtil.getVersion()+"_upgrade.json");
                    Log.i(TAG,"http://idc.xiaojiutech.com/wifitransfer/upgrade/android_"+ AppUtil.getVersion()+"_upgrade.json");
                    URLConnection connection=url.openConnection();//获取互联网连接
                    InputStream is=connection.getInputStream();//获取输入流
                    InputStreamReader isr=new InputStreamReader(is,"utf-8");//字节转字符，字符集是utf-8
                    BufferedReader bufferedReader=new BufferedReader(isr);//通过BufferedReader可以读取一行字符串
                    String line;
                    StringBuilder sb = new StringBuilder("");
                    while ((line=bufferedReader.readLine())!=null){
                        sb.append(line);
                    }
                    Message msg = mHandler.obtainMessage(99);
                    msg.obj = sb.toString().trim();
                    mHandler.sendMessage(msg);
                    bufferedReader.close();
                    isr.close();
                    is.close();
                }catch (Exception e){
                    Log.i(TAG,"checkUpdate ERROR: "+e.getMessage());
                    e.printStackTrace();
                }
            }
        }).start();
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

