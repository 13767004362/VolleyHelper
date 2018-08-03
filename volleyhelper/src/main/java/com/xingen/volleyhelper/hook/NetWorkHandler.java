package com.xingen.volleyhelper.hook;

import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.xingen.volleyhelper.request.DownloadRequest;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Author by {xinGen}
 * Date on 2018/8/1 10:51
 */
public class NetWorkHandler implements InvocationHandler {
    private final String TAG = NetWorkHandler.class.getSimpleName();
    private Object network;
    public NetWorkHandler(Object network) {
        this.network = network;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getName().equals("performRequest")) {
            Object request = args[0];
            //  Log.i(TAG, " NetWorkHandler 代理 " + method.getName());
            if (request instanceof DownloadRequest) {//当执行下载任务，就直接IO写入磁盘，不走内存流
                Log.i(TAG, " NetWorkHandler  处理 DownloadRequest");
                try {
                    String url = ((DownloadRequest) request).getUrl();
                    HttpURLConnection httpURLConnection = (HttpURLConnection) (new URL(url)).openConnection();
                    httpURLConnection.connect();
                    if (httpURLConnection.getResponseCode() == 200) {
                        NetworkResponse networkResponse = writeFileStreamIfExist((DownloadRequest) request, httpURLConnection.getResponseCode(), httpURLConnection.getContentLength(), httpURLConnection.getInputStream());
                        return networkResponse;
                    } else {
                        String error = streamToString(httpURLConnection.getErrorStream());
                        throw new VolleyError(error);
                    }
                } catch (Exception e) {
                    Log.i(TAG, " NetWorkHandler 发生异常 " + e.getMessage());
                    throw new VolleyError(e);
                }
            } else {//当其他的Request，正常走法。
                try {
                    Object result = method.invoke(network, args);
                    return result;
                } catch (Exception error) {
                    if (error instanceof InvocationTargetException) {
                        throw ((InvocationTargetException) error).getTargetException();
                    }
                    throw error;
                }
            }
        } else {
            try {
                Object result = method.invoke(network, args);
                return result;
            } catch (Exception error) {
                if (error instanceof InvocationTargetException) {
                    throw ((InvocationTargetException) error).getTargetException();
                }
                throw error;
            }
        }
    }
    private static NetworkResponse writeFileStreamIfExist(DownloadRequest request, int status, long contentLength, InputStream inputStream) throws IOException {
        Log.i("DownloadRequest", "下载");
        File file = new File(request.getFilePath());
        if (file != null && file.exists()) {
            file.delete();
        }

        FileOutputStream outputStream = new FileOutputStream(file);
        long fileLength = contentLength;
        byte[] buffer = new byte[4096];
        int count;
        long total = 0;
        while ((count = inputStream.read(buffer)) != -1) {
            if (request.isCanceled()) {
                outputStream.close();
                inputStream.close();
                file.deleteOnExit();
                throw new IOException("DownloadRequest cancel 被取消");
            }
            outputStream.write(buffer, 0, count);
            total += count;
            if (fileLength > 0) {
                int progress = (int) ((float) total * 100 / fileLength);
                request.deliverProgress(progress);
            }
        }
        outputStream.flush();
        inputStream.close();
        outputStream.close();
        return new NetworkResponse(status, new byte[0], null, false);
    }
    private static String streamToString(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = null;
        BufferedInputStream bufferedInputStream = null;
        String result = null;

        try {
            bufferedInputStream = new BufferedInputStream(inputStream);
            byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] b = new byte[1024];

            int length;
            while ((length = bufferedInputStream.read(b)) > 0) {
                byteArrayOutputStream.write(b, 0, length);
            }
            result = byteArrayOutputStream.toString("utf-8");
            return result;
        } finally {
            try {
                if (byteArrayOutputStream != null) {
                    byteArrayOutputStream.close();
                }

                if (bufferedInputStream != null) {
                    bufferedInputStream.close();
                }
            } catch (Exception var11) {
                var11.printStackTrace();
            }

        }
    }
}
