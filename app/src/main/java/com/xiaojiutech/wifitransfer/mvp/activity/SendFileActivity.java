package com.xiaojiutech.wifitransfer.mvp.activity;

import android.content.Intent;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.leon.lfilepickerlibrary.LFilePicker;
import com.xiaojiutech.wifitransfer.FileBean;
import com.xiaojiutech.wifitransfer.utils.CustomProgressDialog;
import com.xiaojiutech.wifitransfer.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.xiaojiutech.wifitransfer.R;


/**
 * 发送文件界面
 *
 * 1、搜索设备信息
 * 2、选择设备连接服务端组群信息
 * 3、选择要传输的文件路径
 * 4、把该文件通过socket发送到服务端
 */
public class SendFileActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "SendFileActivity";
    private ListView mTvDevice;
    private ArrayList<String> mListDeviceName = new ArrayList();
    private ArrayList<WifiP2pDevice> mListDevice = new ArrayList<>();
    private AlertDialog mDialog;
    private Timer mTimer;
    private TimerTask mTimerTask;
    private Button mSearchBtn;
    private Button mBtnChoseFile;
    private CustomProgressDialog mProgressDialog;
    private int mTaskScheCount;
    private boolean mConnectSuccess;
    private NewSendTask.SendTaskProgressListener mSendProgressListener = new NewSendTask.SendTaskProgressListener() {
        @Override
        public void onPrepared() {
            //创建进度条
            mProgressDialog = new CustomProgressDialog(SendFileActivity.this);
            mProgressDialog.show(SendFileActivity.this,getString(R.string.file_send_preparing),false,null,new AdListener(){
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

        @Override
        public void onProgressChanged(FileBean file, int progress) {
            mProgressDialog.setProgress(progress);
            mProgressDialog.setMessage(getString(R.string.cur_send_file)+file.fileName +"\n"+getString(R.string.cur_send_progress)+progress + "%\n"+getString(R.string.total_recv_progress)+file.index +"/"+file.totalCount);
        }

        @Override
        public void onCompleted(FileBean file) {
            mTaskScheCount++;
            if (mTaskScheCount >= file.totalCount){
                mProgressDialog.dismiss();
                Toast.makeText(SendFileActivity.this, file.totalCount + getString(R.string.count_file_success), Toast.LENGTH_SHORT).show();
            }

//
        }

        @Override
        public void onFailed(FileBean file) {
            mTaskScheCount++;
            if (mTaskScheCount >= file.totalCount){
                mProgressDialog.dismiss();
            }
            Toast.makeText(SendFileActivity.this, file.fileName +getString(R.string.send_fail), Toast.LENGTH_SHORT).show();
        }
    };


    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    if (mDialog!=null && mDialog.isShowing()){
                        mDialog.dismiss();
                        mDialog = null;
                    }
                    List<FileBean> fileBeanList = (List<FileBean>)msg.obj;
                    if (fileBeanList.size()<=0){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(SendFileActivity.this,getString(R.string.file_not_exists),Toast.LENGTH_SHORT).show();
                            }
                        });
                        return;
                    }
                    mTaskScheCount = 0;
                    new NewSendTask(SendFileActivity.this, fileBeanList,mSendProgressListener).execute(mWifiP2pInfo.groupOwnerAddress.getHostAddress());
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_file);
        mBtnChoseFile = (Button) findViewById(R.id.btn_chosefile);
        mSearchBtn = (Button) findViewById(R.id.btn_connectserver);
        mTvDevice = (ListView) findViewById(R.id.lv_device);

        mBtnChoseFile.setOnClickListener(this);
        mSearchBtn.setOnClickListener(this);
        mSearchBtn.setVisibility(View.GONE);
        mSearchBtn.performClick();
        checkTimeout();
    }

    private void checkTimeout(){
        if (mTimerTask!=null){
            mTimerTask.cancel();
            mTimerTask = null;
        }
        if (mTimer!=null){
            mTimer.cancel();
            mTimer = null;
        }
        mTimer = new Timer();
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mDialog!=null&&mDialog.isShowing()){
                            mDialog.dismiss();
                            mDialog = null;
                        }
                        mWifiP2pManager.stopPeerDiscovery(mChannel,null);
                        mSearchBtn.setVisibility(View.VISIBLE);
                        Toast.makeText(SendFileActivity.this,getString(R.string.device_search_error),Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };
        mTimer.schedule(mTimerTask,8000);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_chosefile:
                if (!mConnectSuccess){
                    Toast.makeText(SendFileActivity.this,getString(R.string.no_device_detect),Toast.LENGTH_LONG).show();
                    return;
                }
                selectFile();
                break;
            case R.id.btn_connectserver:
                showProgressDialog();
                //搜索设备
                connectServer();
                break;
            default:
                break;

        }
    }

    private void showProgressDialog(){
        mDialog = new AlertDialog.Builder(this, R.style.Transparent).create();
        mDialog.show();
        mDialog.setCancelable(false);
        mDialog.setContentView(R.layout.loading_progressba);
    }

    /**
     * 搜索设备
     */
    private void connectServer() {
        mWifiP2pManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                // WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION 广播，此时就可以调用 requestPeers 方法获取设备列表信息
                Log.e(TAG, "搜索设备成功");

                if (mDeviceList!=null&&mDeviceList.size()>0){
                    mSearchBtn.setVisibility(View.GONE);
                }else {
                    mSearchBtn.setVisibility(View.VISIBLE);
                    Toast.makeText(SendFileActivity.this,getString(R.string.no_device_found),Toast.LENGTH_SHORT).show();
                }

                if (mDialog!=null&&mDialog.isShowing()){
                    mDialog.dismiss();
                    mDialog = null;
                }
                mTimer.cancel();
                mTimerTask.cancel();
            }

            @Override
            public void onFailure(int reasonCode) {
                Log.e(TAG, "搜索设备失败");
                if (mDialog!=null&&mDialog.isShowing()){
                    mDialog.dismiss();
                    mDialog = null;
                }
                mWifiP2pManager.stopPeerDiscovery(mChannel,null);
                mSearchBtn.setVisibility(View.VISIBLE);
                Toast.makeText(SendFileActivity.this,getString(R.string.device_search_error),Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 连接设备
     */
    private void connect(WifiP2pDevice wifiP2pDevice) {
        WifiP2pConfig config = new WifiP2pConfig();
        if (wifiP2pDevice != null) {
            config.deviceAddress = wifiP2pDevice.deviceAddress;
            config.wps.setup = WpsInfo.PBC;
            mWifiP2pManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    Log.e(TAG, "连接成功");
                    mConnectSuccess = true;
                    Toast.makeText(SendFileActivity.this, getString(R.string.connect_success), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(int reason) {
                    Log.e(TAG, "连接失败");
                    mConnectSuccess = false;
                    Toast.makeText(SendFileActivity.this, getString(R.string.connect_failed), Toast.LENGTH_SHORT).show();
                    mSearchBtn.performClick();
                }
            });
        }
    }


    /**
     * 客户端进行选择文件
     */
    private void chooseFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, 10);
    }

    private void selectFile(){
        new LFilePicker().withActivity(SendFileActivity.this).withRequestCode(10).withTitle(getString(R.string.select_file_to_send)).withMutilyMode(true).start();
    }

    private void handleFileSelect(final List<String> list){
       new Thread(new Runnable() {
           @Override
           public void run() {
               List<FileBean> fileBeanList = new ArrayList<>();
               for (int i = 0; i < list.size(); i++) {
                   String path = list.get(i);
                   if (!TextUtils.isEmpty(path)) {
                       final File file = new File(path);
                       if (!file.exists()) {
                           continue;
                       }else {
                           FileBean fileBean = new FileBean(file.getPath(), file.length(), "",FileUtils.getFileNameByPath(file.getPath()),list.size(),i+1);
                           fileBeanList.add(fileBean);
                       }
                   }
               }

               Message msg = mHandler.obtainMessage();
               msg.what = 0;
               msg.obj = fileBeanList;
               mHandler.sendMessage(msg);
           }
       }).start();
    }

    /**
     * 客户端选择文件回调
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10) {
            if (resultCode == RESULT_OK) {
                List<String> list = data.getStringArrayListExtra("paths");
                if (list==null||list.size()==0){
                    Toast.makeText(SendFileActivity.this,getString(R.string.no_file_selected),Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mWifiP2pInfo == null||mWifiP2pInfo.groupOwnerAddress==null||TextUtils.isEmpty(mWifiP2pInfo.groupOwnerAddress.getHostAddress())){
                    Toast.makeText(SendFileActivity.this,getString(R.string.no_available_device),Toast.LENGTH_SHORT).show();
                    return;
                }
                showProgressDialog();
                handleFileSelect(list);
            }
        }
    }

    @Override
    public void onPeersInfo(Collection<WifiP2pDevice> wifiP2pDeviceList) {
        super.onPeersInfo(wifiP2pDeviceList);

        for (WifiP2pDevice device : wifiP2pDeviceList) {
            if (!mListDeviceName.contains(device.deviceName) && !mListDevice.contains(device)) {
                mListDeviceName.add(getString(R.string.device) + device.deviceName);
                mListDevice.add(device);
            }
        }

        //进度条消失
        if (mDialog!=null&&mDialog.isShowing()){
            mDialog.dismiss();
            mDialog = null;
        }
        showDeviceInfo();
    }


    /**
     * 展示设备信息
     */
    private void showDeviceInfo() {
        ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, mListDeviceName);
        mTvDevice.setAdapter(adapter);
        mTvDevice.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                WifiP2pDevice wifiP2pDevice = mListDevice.get(i);
                connect(wifiP2pDevice);
            }
        });
    }

}
