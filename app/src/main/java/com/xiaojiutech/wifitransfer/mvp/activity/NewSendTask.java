package com.xiaojiutech.wifitransfer.mvp.activity;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdView;
import com.xiaojiutech.wifitransfer.FileBean;
import com.xiaojiutech.wifitransfer.ProgressDialog;
import com.xiaojiutech.wifitransfer.R;
import com.xiaojiutech.wifitransfer.socket.NewSendSocket;
import com.xiaojiutech.wifitransfer.utils.CustomProgressDialog;

import java.util.List;


/**
 * date：2018/2/27 on 16:51
 * description: 客户端发送文件详情
 */

public class NewSendTask extends AsyncTask<String, Integer, Void> implements NewSendSocket.ProgressSendListener {

    private static final String TAG = "SendTask";

    private List<FileBean> mFileBeanList;
    private SendTaskProgressListener mListener;

    private Context mContext;
    private NewSendSocket mSendSocket;


    public NewSendTask(Context ctx, List<FileBean> fileBeanList,SendTaskProgressListener listener) {
        mFileBeanList = fileBeanList;
        mContext = ctx;
        mListener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (mListener!=null){
            mListener.onPrepared();
        }
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
    public void onProgressChanged(FileBean file, int progress) {
        Log.e(TAG, "当前发送文件 : "+file.fileName+"\n发送进度：" + progress);
        if (mListener!=null){
            mListener.onProgressChanged(file,progress);
        }
    }

    @Override
    public void onFinished(FileBean file) {
        Log.e(TAG, "发送完成");
        if (mListener!=null){
            mListener.onCompleted(file);
        }
    }

    @Override
    public void onFaliure(FileBean file) {
        Log.e(TAG, "发送失败");
        if (mListener!=null){
            mListener.onFailed(file);
        }
    }

    public interface SendTaskProgressListener{
        public void onPrepared();
        public void onProgressChanged(FileBean file, int progress);
        public void onCompleted(FileBean file);
        public void onFailed(FileBean file);
    }
}
