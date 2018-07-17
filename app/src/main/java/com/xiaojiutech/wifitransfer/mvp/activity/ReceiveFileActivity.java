package com.xiaojiutech.wifitransfer.mvp.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.xiaojiutech.wifitransfer.FileBean;
import com.xiaojiutech.wifitransfer.ProgressDialog;
import com.xiaojiutech.wifitransfer.Wifip2pActionListener;
import com.xiaojiutech.wifitransfer.Wifip2pReceiver;
import com.xiaojiutech.wifitransfer.Wifip2pService;
import com.xiaojiutech.wifitransfer.socket.NewReceiveSocket;

import java.io.File;
import com.xiaojiutech.wifitransfer.R;
import com.xiaojiutech.wifitransfer.utils.AlertDialogUtil;
import com.xiaojiutech.wifitransfer.utils.CustomProgressDialog;


/**
 * 接收文件界面
 * 1、创建组群信息
 * 2、移除组群信息
 * 3、启动服务，创建serversocket，监听客户端端口，把信息写入文件
 */
public class ReceiveFileActivity extends BaseActivity implements NewReceiveSocket.ProgressReceiveListener, View.OnClickListener,Wifip2pActionListener {

    private static final String TAG = "ReceiveFileActivity";
    private Wifip2pService.MyBinder mBinder;
    private CustomProgressDialog mProgressDialog;
    private Intent mIntent;
    Button btnCreate;
    Button btnRemove;
    private int mGroupCreateRetryCount = 5;
    private int mTaskScheCount;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 10:
                    mGroupCreateRetryCount--;
                    if (mGroupCreateRetryCount>=0){
                        createGroup();
                    }else {
                        btnCreate.setVisibility(View.VISIBLE);
                        Toast.makeText(ReceiveFileActivity.this,getString(R.string.group_create_fail),Toast.LENGTH_SHORT).show();
                        mGroupCreateRetryCount = 5;
                    }
                    break;
            }
        }
    };


    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //调用服务里面的方法进行绑定
            mBinder = (Wifip2pService.MyBinder) service;
            mBinder.initListener(ReceiveFileActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            //服务断开重新绑定
            bindService(mIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_receive_file);
        btnCreate = (Button) findViewById(R.id.btn_create);
        btnRemove = (Button) findViewById(R.id.btn_remove);
        btnCreate.setOnClickListener(this);
        btnRemove.setOnClickListener(this);

        mIntent = new Intent(ReceiveFileActivity.this, Wifip2pService.class);
        startService(mIntent);
        bindService(mIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        createGroup();
        initInterstitialAd();
        showInterstitialAds(new AdListener(){
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
            }

            @Override
            public void onAdClicked() {
                super.onAdClicked();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_create:
                createGroup();

                break;
            case R.id.btn_remove:
                new AlertDialogUtil(ReceiveFileActivity.this).showAlertDialog(null, getString(R.string.end_receive_tip), getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        onBackPressed();
                    }
                },getString(R.string.cancel),null,true);
                break;
        }
    }

    /**
     * 创建组群，等待连接
     */
    public void createGroup() {
        Log.i(TAG,"createGroup call");
        if (mWifiP2pManager!=null){
            mWifiP2pManager.createGroup(mChannel, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    Log.e(TAG, "创建群组成功");
                    btnCreate.setVisibility(View.GONE);
                    btnRemove.setVisibility(View.VISIBLE);
                    mGroupCreateRetryCount = 5;
                }

                @Override
                public void onFailure(int reason) {
                    Log.e(TAG, "创建群组失败: " + reason);
                    removeGroup(false);
                    mHandler.sendEmptyMessage(10);
                }
            });
        }
    }

    /**
     * 移除组群
     */
    public void removeGroup(final boolean showTipMsg) {
        if (mWifiP2pManager!=null){
            mWifiP2pManager.removeGroup(mChannel, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    Log.e(TAG, "移除组群成功");
                    if (showTipMsg){
                        Toast.makeText(ReceiveFileActivity.this, getString(R.string.group_remove_success), Toast.LENGTH_SHORT).show();
                    }
                    btnCreate.setVisibility(View.VISIBLE);
                    btnRemove.setVisibility(View.GONE);
                }

                @Override
                public void onFailure(int reason) {
                    Log.e(TAG, "移除组群失败");
                    if (showTipMsg){
                        Toast.makeText(ReceiveFileActivity.this, getString(R.string.group_remove_fail), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    public void onSatrt(Boolean mShowErrorTip) {
        if (mProgressDialog == null){
            mTaskScheCount = 0;  //重置count
            mProgressDialog = new CustomProgressDialog(this);
            mProgressDialog.show(ReceiveFileActivity.this,getString(R.string.file_recv_preparing),false,null,new AdListener(){
                @Override
                public void onAdLoaded() {
                    Log.i(TAG,"PROGRESS onAdLoaded");
                    super.onAdLoaded();
                }

                @Override
                public void onAdFailedToLoad(int i) {
                    Log.i(TAG,"PROGRESS onAdFailedToLoad = "+i);
                    super.onAdFailedToLoad(i);
                }
            });
        }
    }

    @Override
    public void onProgressChanged(FileBean file, int progress, Boolean mShowErrorTip) {
//        Log.e(TAG, "接收进度：" + progress);
        mProgressDialog.setProgress(progress);
        mProgressDialog.setMessage(getString(R.string.cur_recv_file)+file.fileName +"\n"+getString(R.string.cur_recv_progress)+progress + "%\n"+getString(R.string.total_recv_progress)+file.index +"/"+file.totalCount);
    }

    @Override
    public void onDisconnection() {
//        onFaliure(null,true);
//        Log.i(TAG,"onDisconnection");
        if (mProgressDialog != null){
            mProgressDialog.dismiss();
            mProgressDialog = null;
            Toast.makeText(ReceiveFileActivity.this, getString(R.string.connection_interupt)+mTaskScheCount +getString(R.string.count_file), Toast.LENGTH_SHORT).show();
        }
        super.onDisconnection();
    }

    @Override
    public void onFinished(FileBean file,Boolean mShowErrorTip) {
        mTaskScheCount++;
        if (mTaskScheCount >= file.totalCount){
            mProgressDialog.dismiss();
            mProgressDialog = null;
            new AlertDialogUtil(ReceiveFileActivity.this).showAlertDialog(getString(R.string.app_name), file.totalCount + getString(R.string.count_file_success) + "\n\n" + getString(R.string.ad_tip), getString(R.string.confirm), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (mInterstitialAd.isLoaded()){
                        mInterstitialAd.show();
                    }
                }
            },null,null,false);
//            Toast.makeText(ReceiveFileActivity.this, file.totalCount + getString(R.string.count_file_success), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onFaliure(FileBean file,Boolean mShowErrorTip) {
        mTaskScheCount++;
        if (file!= null && mTaskScheCount >= file.totalCount){
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
        if (mShowErrorTip){
            Toast.makeText(ReceiveFileActivity.this, file.fileName +getString(R.string.send_fail), Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        clear();
    }

    /**
     * 释放资源
     */
    private void clear() {
        if (serviceConnection != null) {
            unbindService(serviceConnection);
        }
        if (mIntent != null) {
            stopService(mIntent);
        }
        removeGroup(false);
        mWifiP2pManager.cancelConnect(mChannel,null);
        mWifiP2pManager = null;
    }
}
