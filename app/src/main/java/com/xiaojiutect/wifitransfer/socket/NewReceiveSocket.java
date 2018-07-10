package com.xiaojiutect.wifitransfer.socket;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.xiaojiutect.wifitransfer.FileBean;
import com.xiaojiutect.wifitransfer.TaskBean;
import com.xiaojiutect.wifitransfer.utils.FileUtils;
import com.xiaojiutect.wifitransfer.utils.Md5Util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;


/**
 * date：2018/2/24 on 16:59
 * description:服务端监听的socket
 */

public class NewReceiveSocket {

    public static final String TAG = "ReceiveSocket";
    public static final int PORT = 10000;
    private ServerSocket mServerSocket;

    private InputStream mInputStream;
    private ObjectInputStream mObjectInputStream;
    private FileOutputStream mFileOutputStream;
    private File mFile;
    private Boolean mShowErrorTip = true;
    private Handler mHandler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 40:
                    if (mListener != null) {
                        mListener.onSatrt(mShowErrorTip);
                    }
                    break;
                case 50:
                    int progress = (int) msg.obj;
                    if (mListener != null) {
                        mListener.onProgressChanged(mFile, progress,mShowErrorTip);
                    }
                    break;
                case 60:
                    if (mListener != null) {
                        mListener.onFinished(mFile,mShowErrorTip);
                    }
                    break;
                case 70:
                    if (mListener != null) {
                        mListener.onFaliure(mFile,mShowErrorTip);
                    }
                    break;
            }
        }
    };

    public void bindServerSocket(){
        try{
            mServerSocket = new ServerSocket();
            mServerSocket.setReuseAddress(true);
            mServerSocket.bind(new InetSocketAddress(PORT));
        }catch (Exception e){
            e.printStackTrace();
            Log.e(TAG,"bindServerSocket error");
        }
    }

    public void handleRequest(){
        Socket mSocket;
        try{
            while (true){
                mSocket = mServerSocket.accept();
                Log.e(TAG, "客户端IP地址 : " + mSocket.getRemoteSocketAddress());
                Log.e(TAG, "客户端IP地址 : " + mSocket.getRemoteSocketAddress());
                mInputStream = mSocket.getInputStream();
                mObjectInputStream = new ObjectInputStream(mInputStream);
                FileBean fileBean = (FileBean) mObjectInputStream.readObject();
                String name = new File(fileBean.filePath).getName();
                Log.e(TAG, "客户端传递的文件名称 : " + name);
                Log.e(TAG, "客户端传递的MD5 : " + fileBean.md5);
                Log.e(TAG, "文件进度数据 " + fileBean.index +" / "+fileBean.totalCount);
                mFile = new File(FileUtils.SdCardPath(name));
                mFileOutputStream = new FileOutputStream(mFile);
                //开始接收文件
                mHandler.sendEmptyMessage(40);
                byte bytes[] = new byte[1024];
                int len;
                long total = 0;
                int progress;
                while ((len = mInputStream.read(bytes)) != -1) {
                    mFileOutputStream.write(bytes, 0, len);
                    total += len;
                    progress = (int) ((total * 100) / fileBean.fileLength);
                    Log.e(TAG, "文件接收进度: " + progress);
                    Message message = Message.obtain();
                    message.what = 50;
                    message.obj = progress;
                    mHandler.sendMessage(message);
                }
                //新写入文件的MD5
                String md5New = Md5Util.getMd5(mFile);
                //发送过来的MD5
                String md5Old = fileBean.md5;
                if (md5New != null || md5Old != null) {
                    if (md5New.equals(md5Old)) {
                        mHandler.sendEmptyMessage(60);
                        Log.e(TAG, "文件接收成功");
                    }
                } else {
                    mHandler.sendEmptyMessage(70);
                }

//                mServerSocket.close();
                mInputStream.close();
                mObjectInputStream.close();
                mFileOutputStream.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void createServerSocket() {

//        try {
//
//            mSocket = mServerSocket.accept();
//            Log.e(TAG, "客户端IP地址 : " + mSocket.getRemoteSocketAddress());
//            mInputStream = mSocket.getInputStream();
//            mObjectInputStream = new ObjectInputStream(mInputStream);
//            //先取TaskBean
//            TaskBean taskBean = (TaskBean)mObjectInputStream.readObject();
//            if (taskBean==null||taskBean.totalCount==0){
//                Log.e(TAG, "taskBean非法");
//                return;
//            }
//
//            for (int i = 0; i < taskBean.totalCount; i++) {
////                if (mObjectInputStream!=null){
////                    mObjectInputStream.close();
////                    mObjectInputStream=null;
////                }
//                Log.i(TAG,"taskBean.totalCount = "+taskBean.totalCount);
//                mInputStream = mSocket.getInputStream();
//                mObjectInputStream = new ObjectInputStream(mInputStream);
//                FileBean fileBean = (FileBean) mObjectInputStream.readObject();
//                Log.i(TAG,"fileBean name = "+fileBean.fileName + " \n fileBean path = "+fileBean.filePath + " \n filelength = "+fileBean.fileLength);
//                String name = new File(fileBean.filePath).getName();
//                Log.e(TAG, "客户端传递的文件名称 : " + name);
//                Log.e(TAG, "客户端传递的MD5 : " + fileBean.md5);
//                Log.e(TAG, "文件进度数据 " + fileBean.index +" / "+fileBean.totalCount);
//                mFile = new File(FileUtils.SdCardPath(name));
//                mFileOutputStream = new FileOutputStream(mFile);
//                //开始接收文件
//                mHandler.sendEmptyMessage(40);
//                byte bytes[] = new byte[1024];
//                int len;
//                long total = 0;
//                int progress;
//                while ((len = mInputStream.read(bytes)) != -1) {
//                    mFileOutputStream.write(bytes, 0, len);
//                    total += len;
//                    progress = (int) ((total * 100) / fileBean.fileLength);
//
//                    Message message = Message.obtain();
//                    message.what = 50;
//                    message.obj = progress;
//                    mHandler.sendMessage(message);
//                    Log.e(TAG, "total = "+total + " fileBean.fileLength = "+fileBean.fileLength);
//                    if (total==fileBean.fileLength){
//                        Log.e(TAG, "break");
//                        break;
//                    }
//                }
//                //新写入文件的MD5
//                String md5New = Md5Util.getMd5(mFile);
//                //发送过来的MD5
//                String md5Old = fileBean.md5;
////                if (md5New != null || md5Old != null) {
////                    if (md5New.equals(md5Old)) {
////
////                        Log.e(TAG, "文件接收成功");
////                    }
////                } else {
////                    mHandler.sendEmptyMessage(70);
////                }
////                ois.close();
//                mFileOutputStream.flush();
//                mFileOutputStream.close();
//            }
//            mHandler.sendEmptyMessage(60);
//
////            mServerSocket.close();
//            mInputStream.close();
//            mObjectInputStream.close();
//            mFileOutputStream.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//            mHandler.sendEmptyMessage(70);
//            Log.e(TAG, "文件接收异常");
//        }
    }

    /**
     * 监听接收进度
     */
    private ProgressReceiveListener mListener;

    public void setOnProgressReceiveListener(ProgressReceiveListener listener) {
        mListener = listener;
    }

    public interface ProgressReceiveListener {

        //开始传输
        void onSatrt(Boolean showErrorTip);

        //当传输进度发生变化时
        void onProgressChanged(File file, int progress, Boolean showErrorTip);

        //当传输结束时
        void onFinished(File file, Boolean showErrorTip);

        //传输失败回调
        void onFaliure(File file, Boolean showErrorTip);
    }

    /**
     * 服务断开：释放内存
     */
    public void clean() {
        mShowErrorTip = false;
        if (mServerSocket != null) {
            try {
                mServerSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (mInputStream != null) {
            try {
                mInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (mObjectInputStream != null) {
            try {
                mObjectInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (mFileOutputStream != null) {
            try {
                mFileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
