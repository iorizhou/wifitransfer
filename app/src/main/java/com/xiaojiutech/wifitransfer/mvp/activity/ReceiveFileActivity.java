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

import com.xiaojiutech.wifitransfer.ProgressDialog;
import com.xiaojiutech.wifitransfer.Wifip2pActionListener;
import com.xiaojiutech.wifitransfer.Wifip2pReceiver;
import com.xiaojiutech.wifitransfer.Wifip2pService;
import com.xiaojiutech.wifitransfer.socket.NewReceiveSocket;

import java.io.File;
import com.xiaojiutech.wifitransfer.R;
import com.xiaojiutech.wifitransfer.utils.AlertDialogUtil;


/**
 * 接收文件界面
 * 1、创建组群信息
 * 2、移除组群信息
 * 3、启动服务，创建serversocket，监听客户端端口，把信息写入文件
 */
public class ReceiveFileActivity extends BaseActivity implements NewReceiveSocket.ProgressReceiveListener, View.OnClickListener,Wifip2pActionListener {

    private static final String TAG = "ReceiveFileActivity";
    private Wifip2pService.MyBinder mBinder;
    private ProgressDialog mProgressDialog;
    private Intent mIntent;
    Button btnCreate;
    Button btnRemove;
    private int mGroupCreateRetryCount = 5;
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
                        Toast.makeText(ReceiveFileActivity.this,"群组创建失败,请点击 创建群组 按钮重试",Toast.LENGTH_SHORT).show();
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
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_create:
                createGroup();

                break;
            case R.id.btn_remove:
                new AlertDialogUtil(ReceiveFileActivity.this).showAlertDialog(null, "确定结束并退出吗?", "确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        onBackPressed();
                    }
                },"取消",null,true);
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
                        Toast.makeText(ReceiveFileActivity.this, "移除组群成功", Toast.LENGTH_SHORT).show();
                    }
                    btnCreate.setVisibility(View.VISIBLE);
                    btnRemove.setVisibility(View.GONE);
                }

                @Override
                public void onFailure(int reason) {
                    Log.e(TAG, "移除组群失败");
                    if (showTipMsg){
                        Toast.makeText(ReceiveFileActivity.this, "移除组群失败,请创建组群重试", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    public void onSatrt(Boolean mShowErrorTip) {
        mProgressDialog = new ProgressDialog(this);
    }

    @Override
    public void onProgressChanged(File file, int progress,Boolean mShowErrorTip) {
//        Log.e(TAG, "接收进度：" + progress);
        mProgressDialog.setProgress(progress);
        mProgressDialog.setProgressText("当前接收文件 : "+file.getName() +" \n接收进度: "+progress + "%");
    }

    @Override
    public void onDisconnection() {
//        onFaliure(null,true);
//        Log.i(TAG,"onDisconnection");
        super.onDisconnection();
    }

    @Override
    public void onFinished(File file,Boolean mShowErrorTip) {
        Log.e(TAG, "接收完成");
        mProgressDialog.dismiss();
        if (mShowErrorTip){
            Toast.makeText(this, file.getName() + "接收完毕！", Toast.LENGTH_SHORT).show();
        }
        //接收完毕后再次启动服务等待下载一次连接，不启动只能接收一次，第二次无效，原因待尚不清楚
//        clear();
//        startService(mIntent);
//        bindService(mIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onFaliure(File file,Boolean mShowErrorTip) {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
        if (mShowErrorTip){
            Toast.makeText(this, "网络连接异常,接收失败，请重试！", Toast.LENGTH_SHORT).show();
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
