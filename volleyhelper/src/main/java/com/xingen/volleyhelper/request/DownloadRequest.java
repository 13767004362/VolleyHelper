package com.xingen.volleyhelper.request;

import android.os.Handler;
import android.os.Looper;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.xingen.volleyhelper.listener.DownloadListener;
import com.xingen.volleyhelper.listener.FileProgressListener;

/**
 * Author by {xinGen}
 * Date on 2018/8/1 10:49
 */
public  class DownloadRequest extends Request<Object> {
    /**
     * 主线程的Handler
     */
    private static final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final DownloadListener downloadListener;
    private final FileProgressListener progressListener;
    private final String downloadUrl, filePath;

    public DownloadRequest(String url, String filePath, FileProgressListener progressListener, DownloadListener downloadListener) {
        super(Method.GET, url, downloadListener);
        this.downloadUrl = url;
        this.filePath = filePath;
        this.downloadListener = downloadListener;
        this.progressListener = progressListener;
        this.setShouldCache(false);
    }

    @Override
    protected Response<Object> parseNetworkResponse(NetworkResponse response) {
        return Response.success(null, null);
    }

    @Override
    protected void deliverResponse(Object response) {
        if (downloadListener != null) {
            downloadListener.downloadFinish(this.downloadUrl, this.filePath);
        }
    }

    @Override
    public void deliverError(VolleyError error) {
        if (downloadListener != null) {
            downloadListener.downloadError(this.downloadUrl, error);
        }
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

    public String getFilePath() {
        return filePath;
    }
}
