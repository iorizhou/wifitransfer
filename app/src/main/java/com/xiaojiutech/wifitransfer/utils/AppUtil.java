package com.xiaojiutech.wifitransfer.utils;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.xiaojiutech.wifitransfer.XiaojiuApplication;

public class AppUtil {

    /**
     * 获取版本号
     * @return 当前应用的版本号
     */
    public static String getVersion() {
        try {
            PackageManager manager = XiaojiuApplication.getInstace().getPackageManager();
            PackageInfo info = manager.getPackageInfo(XiaojiuApplication.getInstace().getPackageName(), 0);
            return info.versionCode + "";
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
