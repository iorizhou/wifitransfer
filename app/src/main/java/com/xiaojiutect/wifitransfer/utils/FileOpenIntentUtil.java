package com.xiaojiutect.wifitransfer.utils;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;

import com.xiaojiutect.wifitransfer.BuildConfig;
import com.xiaojiutect.wifitransfer.XiaojiuApplication;

import java.io.File;

public class FileOpenIntentUtil {

    public static void openFile(String filepath){
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri =  null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            uri = FileProvider.getUriForFile(XiaojiuApplication.getInstace(), BuildConfig.APPLICATION_ID + ".fileProvider", new File(filepath));
        }else {
            uri =  Uri.fromFile(new File(filepath ));
        }
        intent.setDataAndType(uri, getContentType(filepath));
        XiaojiuApplication.getInstace().startActivity(intent);
    }

    public static String getContentType(String filepath){
        String result = "";
        filepath = filepath.toLowerCase();
        if (filepath.endsWith(".jpg")||filepath.endsWith(".jpeg")||filepath.endsWith(".png")||filepath.endsWith(".bmp")||filepath.endsWith(".gif")){
            result = "image/*";
        }else if (filepath.endsWith(".html")||filepath.endsWith(".htm")||filepath.endsWith(".shtml")){
            result = "text/html";
        }else if (filepath.endsWith(".pdf")){
            result = "application/pdf";
        }else if (filepath.endsWith(".txt")||filepath.endsWith(".log")){
            result = "text/plain";
        }else if (filepath.endsWith(".mp3")||filepath.endsWith(".wav")||filepath.endsWith(".flac")||filepath.endsWith(".acc")||filepath.endsWith(".ogg")){
            result = "audio/*";
        }else if (filepath.endsWith(".mp4")||filepath.endsWith(".mkv")||filepath.endsWith(".rm")||filepath.endsWith(".rmvb")||filepath.endsWith(".3gp")||filepath.endsWith(".flv")||filepath.endsWith(".avi")){
            result = "video/*";
        }else if (filepath.endsWith(".doc")||filepath.endsWith(".docx")){
            result = "application/msword";
        }else if (filepath.endsWith(".xls")||filepath.endsWith(".xlsx")){
            result = "application/vnd.ms-excel";
        }else if (filepath.endsWith(".ppt")||filepath.endsWith(".pptx")){
            result = "application/vnd.ms-powerpoint";
        }else {
            result = "*/*";
        }
        return result;
    }
}
