package com.xiaojiutect.wifitransfer.mvp.activity;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.xiaojiutect.wifitransfer.FileBean;
import com.xiaojiutect.wifitransfer.ProgressDialog;
import com.xiaojiutect.wifitransfer.socket.NewSendSocket;
import com.xiaojiutect.wifitransfer.socket.SendSocket;

import java.io.File;
import java.util.List;


/**
 * date：2018/2/27 on 16:51
 * description: 客户端发送文件详情
 */

public class NewSendTask extends AsyncTask<String, Integer, Void> implements NewSendSocket.ProgressSendListener {

    private static final String TAG = "SendTask";

    private List<FileBean> mFileBeanList;
    private Context mContext;
    private NewSendSocket mSendSocket;
    private ProgressDialog mProgressDialog;


    public NewSendTask(Context ctx, List<FileBean> fileBeanList) {
        mFileBeanList = fileBeanList;
        mContext = ctx;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //创建进度条
        mProgressDialog = new ProgressDialog(mContext);
    }

    @Override
    protected Void doInBackground(String... strings) {
        for (FileBean fileBean : mFileBeanList){
            mSendSocket = new NewSendSocket(mFileBeanList, strings[0], this);
            mSendSocket.sendFile(fileBean);
        }
        return null;
    }


    @Override
    public void onProgressChanged(File file, int progress) {
        Log.e(TAG, "发送进度：" + progress);
        mProgressDialog.setProgress(progress);
        mProgressDialog.setProgressText(progress + "%");;
    }

    @Override
    public void onFinished(File file) {
        Log.e(TAG, "发送完成");
        mProgressDialog.dismiss();
        Toast.makeText(mContext, file.getName() + "发送完毕！", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFaliure(File file) {
        Log.e(TAG, "发送失败");
        if (mProgressDialog!=null){
            mProgressDialog.dismiss();
        }
        Toast.makeText(mContext, "发送失败，请重试！", Toast.LENGTH_SHORT).show();
    }
}
