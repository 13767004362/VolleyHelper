package com.xingen.volleyhelper.request;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.xingen.volleyhelper.listener.FileProgressListener;
import com.xingen.volleyhelper.listener.GsonResultListener;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Author by {xinGen}
 * Date on 2018/8/1 10:48
 */
public  class SingleFileRequest<T> extends Request<T> {
    /**
     * 默认的名字
     */
    public static final String DEFAULT_NAME = "media";
    /**
     * 字符编码格式
     */
    private static final String PROTOCOL_CHARSET = "utf-8";


    private static final String BOUNDARY = "----------" + System.currentTimeMillis();
    /**
     * Content type for request.
     */
    private static final String PROTOCOL_CONTENT_TYPE = "multipart/form-data; boundary=" + BOUNDARY;
    /**
     * 主线程的Handler
     */
    private static final Handler mainHandler = new Handler(Looper.getMainLooper());
    /**
     * 结果监听器
     */
    private final GsonResultListener<T> resultListener;
    /**
     * 进度监听器
     */
    private final FileProgressListener progressListener;

    //每次读取的长度
    public static final int READ_SIZE = 4096;

    private final String name;
    /**
     * 文件进度
     */
    private final String filePath;
    /**
     * Header表头
     */
    private Map<String, String> headers;

    public SingleFileRequest(String url, File file, FileProgressListener progressListener, GsonResultListener<T> resultListener) {
        this(url, DEFAULT_NAME, file, progressListener, resultListener);
    }

    public SingleFileRequest(String url, String name, File file, FileProgressListener progressListener, GsonResultListener<T> resultListener) {
        this(url, name, file.getAbsolutePath(), progressListener, resultListener);
    }

    public SingleFileRequest(String url, String filePath, FileProgressListener progressListener, GsonResultListener<T> resultListener) {
        this(url, DEFAULT_NAME, filePath, progressListener, resultListener);
    }

    public SingleFileRequest(String url, String name, String filePath, FileProgressListener progressListener, GsonResultListener<T> resultListener) {
        super(Method.POST, url, resultListener);
        this.headers = new HashMap<>();
        this.filePath = filePath;
        this.name = name;
        this.resultListener = resultListener;
        this.progressListener = progressListener;
        this.setShouldCache(false);
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        return this.resultListener.parseResponse(response);
    }

    @Override
    protected void deliverResponse(T response) {
        this.resultListener.onResponse(response);
    }

    public Map<String, String> setHeader(String key, String content) {
        if (!TextUtils.isEmpty(key) && !TextUtils.isEmpty(content)) {
            headers.put(key, content);
        }
        return headers;
    }

    @Override
    public Map<String, String> getHeaders() {
        return headers;
    }

    @Override
    public String getBodyContentType() {
        return PROTOCOL_CONTENT_TYPE;
    }

    /**
     * 回调传递进度
     *
     * @param progress
     */
    public void deliverProgress(final int progress) {
        if (isCanceled()) {
            return;
        }
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (progressListener != null) {
                    progressListener.progress(progress);
                }
            }
        });
    }

    /**
     * 获取文件名
     *
     * @return
     */
    public String getFileName() {
        return filePath != null ? filePath.substring(filePath.lastIndexOf("/") + 1, filePath.length()) : null;
    }

    /**
     * 文件内容的头部
     */
    public String getContentHeader() {
        try {
            StringBuffer buffer = new StringBuffer();
            buffer.append("--");
            buffer.append(BOUNDARY);
            buffer.append("\r\n");
            buffer.append("Content-Disposition: form-data;name=\"");
            buffer.append(name);
            buffer.append("\";filename=\"");
            buffer.append(getFileName());
            buffer.append("\"\r\n");
            buffer.append("Content-Type:application/octet-stream\r\n\r\n");
            String s = buffer.toString();
            return s;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 文件内容的尾部
     */
    public String getContentFoot() {
        try {
            StringBuffer buffer = new StringBuffer();
            buffer.append("\r\n--");
            buffer.append(BOUNDARY);
            buffer.append("--\r\n");
            String s = buffer.toString();
            return s;
        } catch (Exception e) {
            return null;
        }
    }

    public String getFilePath() {
        return filePath;
    }
}
