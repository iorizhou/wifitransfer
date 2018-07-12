package com.xiaojiutech.wifitransfer.mvp.activity;

import android.Manifest;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.os.Bundle;
import android.util.Log;

import com.xiaojiutech.wifitransfer.R;
import com.xiaojiutech.wifitransfer.mvp.activity.fragment.FileHistoryRecvFragment;
import com.xiaojiutech.wifitransfer.mvp.activity.fragment.FileHistorySendFragment;
import com.xiaojiutech.wifitransfer.mvp.activity.fragment.FunctionFragment;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends FragmentActivity implements EasyPermissions.PermissionCallbacks{

    public static final String TAG = "MainActivity";
    FragmentTabHost mTabHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTabHost = (FragmentTabHost)findViewById(R.id.tab);
        mTabHost.setup(this,getSupportFragmentManager(),R.id.content);
        mTabHost.addTab(mTabHost.newTabSpec("func_tab").setIndicator("文件传送",getResources().getDrawable(R.drawable.ic_launcher_background)), FunctionFragment.class,null);
        mTabHost.addTab(mTabHost.newTabSpec("file_send_tab").setIndicator("文件发送记录",getResources().getDrawable(R.drawable.ic_launcher_background)), FileHistorySendFragment.class,null);
        mTabHost.addTab(mTabHost.newTabSpec("file_recv_tab").setIndicator("文件接收记录",getResources().getDrawable(R.drawable.ic_launcher_background)), FileHistoryRecvFragment.class,null);
        requireSomePermission();
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

