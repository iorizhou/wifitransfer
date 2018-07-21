package com.xiaojiutech.wifitransfer.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.PowerManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadTask extends AsyncTask<String, Integer, String> {
    private Context context;
    private PowerManager.WakeLock mWakeLock;
    private DownloadListener mListener;
    public DownloadTask(Context context,DownloadListener listener) {
        this.context = context;
        this.mListener = listener;
    }

    @Override
    protected String doInBackground(String... sUrl) {
        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;
        File file = null;
        try {
            URL url = new URL(sUrl[0]);
            String filename = sUrl[1];
            File dir = new File(Environment.getExternalStorageDirectory()+"/wifitransfer");
            if (!dir.exists()){
                dir.mkdirs();
            }
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            // expect HTTP 200 OK, so we don't mistakenly save error report
            // instead of the file
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return "Server returned HTTP " + connection.getResponseCode()
                        + " " + connection.getResponseMessage();
            }
            // this will be useful to display download percentage
            // might be -1: server did not report the length
            int fileLength = connection.getContentLength();
            // download the file
            input = connection.getInputStream();
            file = new File(dir,filename);
            if (file.exists()){
                file.delete();
            }
            output = new FileOutputStream(file);
            byte data[] = new byte[4096];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                // allow canceling with back button
                if (isCancelled()) {
                    input.close();
                    return null;
                }
                total += count;
                // publishing the progress....
                if (fileLength > 0) // only if total length is known
                    mListener.onProgressChanged((int) (total * 100 / fileLength));
                output.write(data, 0, count);
            }

            mListener.onCompleted(file.getAbsolutePath());
        } catch (Exception e) {
            mListener.onFailed();
            return e.toString();
        } finally {
            try {
                if (output != null)
                    output.close();
                if (input != null)
                    input.close();
            } catch (IOException ignored) {
            }
            if (connection != null)
                connection.disconnect();

        }
        return null;
    }

    public interface DownloadListener{
        public void onProgressChanged(int progress);
        public void onCompleted(String filepath);
        public void onFailed();
    }
}
