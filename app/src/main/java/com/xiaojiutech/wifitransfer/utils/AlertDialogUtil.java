package com.xiaojiutech.wifitransfer.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;

import com.xiaojiutech.wifitransfer.R;

public class AlertDialogUtil {
    AlertDialog.Builder mDialog;
    private Context mContext;
    public AlertDialogUtil(Context context){
        if (mDialog==null){
            mContext = context;
            mDialog = new AlertDialog.Builder(context);
        }
    }

    public void showAlertDialog(String title, String tipMsg, String posMsg, DialogInterface.OnClickListener posListener,String negMsg,DialogInterface.OnClickListener negListener,Boolean cancelable){
        mDialog.setTitle(TextUtils.isEmpty(title)?mContext.getString(R.string.app_name):title);
        mDialog.setMessage(tipMsg);
        mDialog.setCancelable(cancelable);
        mDialog.setPositiveButton(posMsg,posListener);
        mDialog.setNegativeButton(negMsg,negListener);
        mDialog.create().show();
    }

}
