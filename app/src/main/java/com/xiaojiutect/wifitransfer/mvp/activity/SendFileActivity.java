package com.xiaojiutect.wifitransfer.mvp.activity;

import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.leon.lfilepickerlibrary.LFilePicker;
import com.leon.lfilepickerlibrary.utils.Constant;
import com.xiaojiutect.wifitransfer.FileBean;
import com.xiaojiutect.wifitransfer.utils.FileUtils;
import com.xiaojiutect.wifitransfer.utils.Md5Util;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.xiaojiutect.wifitransfer.R;


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
    Button mBtnConnectServer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_file);
        Button mBtnChoseFile = (Button) findViewById(R.id.btn_chosefile);
        mBtnConnectServer = (Button) findViewById(R.id.btn_connectserver);
        mTvDevice = (ListView) findViewById(R.id.lv_device);

        mBtnChoseFile.setOnClickListener(this);
        mBtnConnectServer.setOnClickListener(this);
        mBtnConnectServer.setVisibility(View.GONE);
        mBtnConnectServer.performClick();
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
                        mBtnConnectServer.setVisibility(View.VISIBLE);
                        Toast.makeText(SendFileActivity.this,"设备搜索失败，请确保另一台设备与本机连接于同一个WIFI网络环境下",Toast.LENGTH_SHORT).show();
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
                selectFile();
                break;
            case R.id.btn_connectserver:
                mDialog = new AlertDialog.Builder(this, R.style.Transparent).create();
                mDialog.show();
                mDialog.setCancelable(false);
                mDialog.setContentView(R.layout.loading_progressba);
                //搜索设备
                connectServer();
                break;
            default:
                break;

        }
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
                    mBtnConnectServer.setVisibility(View.GONE);
                }else {
                    mBtnConnectServer.setVisibility(View.VISIBLE);
                    Toast.makeText(SendFileActivity.this,"本次未查找到任何设备，请重试",Toast.LENGTH_SHORT).show();
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
                mBtnConnectServer.setVisibility(View.VISIBLE);
                Toast.makeText(SendFileActivity.this,"设备搜索失败，请确保另一台设备与本机连接于同一个WIFI网络环境下",Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(SendFileActivity.this, "连接成功", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(int reason) {
                    Log.e(TAG, "连接失败");
                    Toast.makeText(SendFileActivity.this, "连接失败", Toast.LENGTH_SHORT).show();
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
        new LFilePicker().withActivity(SendFileActivity.this).withRequestCode(10).withTitle("多选想要发送的文件").withMutilyMode(true).start();
    }

    /**
     * 客户端选择文件回调
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10) {
            if (resultCode == RESULT_OK) {
//                Uri uri = data.getData();
//                if (uri != null) {
//                    String path = FileUtils.getAbsolutePath(this, uri);
//                    if (path != null) {
//                        final File file = new File(path);
//                        if (!file.exists() || mWifiP2pInfo == null) {
//                            Log.i(TAG,"FILE exist ? "+file.exists());
//                            Toast.makeText(SendFileActivity.this,"文件路径找不到", Toast.LENGTH_SHORT).show();
//                            return;
//                        }
//                        String md5 = Md5Util.getMd5(file);
//                        FileBean fileBean = new FileBean(file.getPath(), file.length(), md5,FileUtils.getFileNameByPath(file.getPath()),1,1);
//                        String hostAddress = mWifiP2pInfo.groupOwnerAddress.getHostAddress();
//                        new SendTask(SendFileActivity.this, fileBean).execute(hostAddress);
//                    }
//                }

                    List<String> list = data.getStringArrayListExtra("paths");
                    Toast.makeText(getApplicationContext(), "选中了" + list.size() + "个文件", Toast.LENGTH_SHORT).show();

            }
        }
    }

    @Override
    public void onPeersInfo(Collection<WifiP2pDevice> wifiP2pDeviceList) {
        super.onPeersInfo(wifiP2pDeviceList);

        for (WifiP2pDevice device : wifiP2pDeviceList) {
            if (!mListDeviceName.contains(device.deviceName) && !mListDevice.contains(device)) {
                mListDeviceName.add("设备：" + device.deviceName);
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
