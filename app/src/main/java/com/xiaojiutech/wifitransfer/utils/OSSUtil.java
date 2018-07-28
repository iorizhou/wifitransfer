package com.xiaojiutech.wifitransfer.utils;

import android.os.Environment;
import android.util.Log;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.GetObjectRequest;
import com.alibaba.sdk.android.oss.model.GetObjectResult;
import com.xiaojiutech.wifitransfer.XiaojiuApplication;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class OSSUtil {
    private static OSSUtil sInstance;
    private String mEndPoint = "http://idc.xiaojiutech.com";
    private OSS mOSS;
    private OSSCredentialProvider mProvider;
    private OSSUtil(){
        mProvider = new OSSStsTokenCredentialProvider("LTAIFWoAbdjEKmVz","MbsvqaipHv1DENaqEGEjRQrYNrSHd5","LTAIFWoAbdjEKmVzMbsvqaipHv1DENaqEGEjRQrYNrSHd5");
        mOSS = new OSSClient(XiaojiuApplication.getInstace(),mEndPoint,mProvider);

    }

    public static synchronized OSSUtil getInstance(){
        if (sInstance==null){
            sInstance = new OSSUtil();
        }

        return sInstance;
    }

    public OSS getOSS(){
        return mOSS;
    }

    public void downloadOSSFile(final String fileName, final OSSDownladCallback listener){
        GetObjectRequest getObjectRequest = new GetObjectRequest("xiaojiutech-hk",fileName);
        OSSAsyncTask task = getOSS().asyncGetObject(getObjectRequest, new OSSCompletedCallback<GetObjectRequest, GetObjectResult>() {
            @Override
            public void onSuccess(GetObjectRequest request, GetObjectResult result) {
                FileOutputStream fileOutputStream = null;
                InputStream inputStream = null;
                try {
                File dir = new File(Environment.getExternalStorageDirectory() +"/wifitransfer/oss");
                if (!dir.exists()){
                    dir.mkdirs();
                }
                File file = new File(dir,fileName.substring(fileName.lastIndexOf("/")+1));
                if (file.exists()){
                    file.delete();
                }
                fileOutputStream = new FileOutputStream(file);
                inputStream = result.getObjectContent();
                byte[] buffer = new byte[2048];
                int len;
                    long total = 0;
                    int progress;
                    while ((len = inputStream.read(buffer))!= -1){
                        fileOutputStream.write(buffer,0,len);
                        total += len;
                        progress = (int) ((total * 100) / result.getContentLength());
                        if (listener!=null){
                            listener.onProgress(progress);
                        }
                    }
                    fileOutputStream.flush();
                    fileOutputStream.close();
                    inputStream.close();
                    if (listener != null){
                        listener.onComplete(fileName,file.getAbsolutePath());
                    }
                }catch (Exception e){
                    if (listener != null){
                        listener.onFailure(fileName,-1,"download failed");
                    }
                    e.printStackTrace();
                    Log.e("OSS","download : "+fileName +" error: "+e.getMessage());
                }


            }

            @Override
            public void onFailure(GetObjectRequest request, ClientException clientException, ServiceException serviceException) {
                if (listener != null){
                    listener.onFailure(fileName,-1,"download failed");
                }
            }
        });
    }

    public interface OSSDownladCallback{
        public void onComplete(String url,String localFilePath);
        public void onFailure(String url,int errorCode,String errorMsg);
        public void onProgress(int progress);
    }

}
