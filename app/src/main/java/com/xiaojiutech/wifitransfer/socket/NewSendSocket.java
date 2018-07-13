package com.xiaojiutech.wifitransfer.socket;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.xiaojiutech.wifitransfer.FileBean;
import com.xiaojiutech.wifitransfer.TaskBean;
import com.xiaojiutech.wifitransfer.XiaojiuApplication;
import com.xiaojiutech.wifitransfer.db.model.HistoryFile;
import com.xiaojiutech.wifitransfer.db.util.DBUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;


/**
 * date：2018/2/24 on 18:10
 * description: 客户端发送的socket
 */

public class NewSendSocket {

    public static final String TAG = "SendSocket";
    public static final int PORT = 10000;
    private List<FileBean> mFileBeanList;
    private String mAddress;
    private File mFile;

    private Handler mHandler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            FileBean fileBean = (FileBean)msg.obj;
            switch (msg.what) {
                case 10:
                    int progress = (int) msg.arg1;
                    if (mlistener != null) {
                        mlistener.onProgressChanged(fileBean, progress);
                    }
                    break;
                case 20:
                    if (mlistener != null) {
                        mlistener.onFinished(fileBean);
                    }
                    break;
                case 30:
                    if (mlistener != null) {
                        mlistener.onFaliure(fileBean);
                    }
                    break;
            }
        }
    };

    public NewSendSocket(List<FileBean> fileBeanList, String address, ProgressSendListener listener) {
        mFileBeanList = fileBeanList;
        mAddress = address;
        mlistener = listener;
    }


    public void sendFile(FileBean fileBean){
        try {
            Socket socket = new Socket();
            InetSocketAddress inetSocketAddress = new InetSocketAddress(mAddress, PORT);
            socket.connect(inetSocketAddress);
            OutputStream outputStream = socket.getOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(fileBean);
            mFile = new File(fileBean.filePath);
            FileInputStream inputStream = new FileInputStream(mFile);
            long size = fileBean.fileLength;
            long total = 0;
            byte bytes[] = new byte[1024*1024];
            int len;
            while ((len = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, len);
                total += len;
                int progress = (int) ((total * 100) / size);
                Log.e(TAG, "文件发送进度：" + progress);
                Message message = Message.obtain();
                message.what = 10;
                message.arg1 = progress;
                message.obj = fileBean;
                mHandler.sendMessage(message);
            }
            outputStream.close();
            objectOutputStream.close();
            inputStream.close();
            socket.close();
            Message finishMsg = mHandler.obtainMessage(20);
            finishMsg.obj = fileBean;
            mHandler.sendMessage(finishMsg);
            // add db record
            HistoryFile dbEntity = new HistoryFile(mFile.getName(),mFile.getAbsolutePath(),System.currentTimeMillis(),0);
            DBUtil.getInstance(XiaojiuApplication.getInstace()).getDaoSession().getHistoryFileDao().insert(dbEntity);
            Log.e(TAG, "文件发送成功");
        } catch (Exception e) {
            e.printStackTrace();
            Message failMsg = mHandler.obtainMessage(30);
            failMsg.obj = fileBean;
            mHandler.sendMessage(failMsg);
            Log.e(TAG, "文件发送异常");
        }
    }


    public void createSendSocket() {
        try {
            Socket socket = new Socket();
            InetSocketAddress inetSocketAddress = new InetSocketAddress(mAddress, PORT);
            socket.connect(inetSocketAddress);
            if (mFileBeanList==null||mFileBeanList.size()==0){
                mHandler.sendEmptyMessage(30);
                return;
            }
            //写TaskBean 告诉对方此次传多少个
            TaskBean taskBean = new TaskBean(mFileBeanList.size());
            OutputStream taskOutStream = socket.getOutputStream();
            ObjectOutputStream taskbjectOutputStream = new ObjectOutputStream(taskOutStream);
            taskbjectOutputStream.writeObject(taskBean);
            taskbjectOutputStream.flush();
            taskOutStream.flush();
            for (FileBean fileBean : mFileBeanList){
                Log.i(TAG,"FOR ONCE");
                OutputStream outputStream = socket.getOutputStream();
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
                objectOutputStream.writeObject(fileBean);
                objectOutputStream.flush();
//                outputStream.flush();
                mFile = new File(fileBean.filePath);
                FileInputStream inputStream = new FileInputStream(mFile);
                long size = fileBean.fileLength;
                long total = 0;
                byte bytes[] = new byte[1024];
                int len;
                while ((len = inputStream.read(bytes)) != -1) {
                    outputStream.write(bytes, 0, len);
                    total += len;
                    int progress = (int) ((total * 100) / size);
                    Log.e(TAG, "文件发送进度：" + progress);
                    Message message = Message.obtain();
                    message.what = 10;
                    message.obj = progress;
                    mHandler.sendMessage(message);
                    if (total==size){
                        break;
                    }
                }
                outputStream.flush();
                objectOutputStream.flush();
                inputStream.close();
            }
            taskbjectOutputStream.close();
            taskOutStream.close();
            socket.close();
            mHandler.sendEmptyMessage(20);
            Log.e(TAG, "文件发送成功");
        } catch (Exception e) {
            e.printStackTrace();
            mHandler.sendEmptyMessage(30);
            Log.e(TAG, "文件发送异常");
        }
    }

    /**
     * 监听发送进度
     */
    private ProgressSendListener mlistener;

    public interface ProgressSendListener {

        //当传输进度发生变化时
        void onProgressChanged(FileBean file, int progress);

        //当传输结束时
        void onFinished(FileBean file);

        //传输失败时
        void onFaliure(FileBean file);
    }
}

