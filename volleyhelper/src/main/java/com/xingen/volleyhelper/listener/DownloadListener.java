package com.xingen.volleyhelper.listener;

import com.android.volley.Response;
import com.android.volley.VolleyError;

/**
 * Author by {xinGen}
 * Date on 2018/8/1 10:44
 */
public abstract class DownloadListener implements Response.ErrorListener {

    /**
     * 下载完成的方法
     *
     * @param downloadUrl
     * @param filePath
     */
    public abstract void downloadFinish(String downloadUrl, String filePath);

    /**
     * 下载失败的方法
     *
     * @param downloadUrl
     * @param volleyError
     */
    public abstract void downloadError(String downloadUrl, VolleyError volleyError);

    @Override
    public void onErrorResponse(VolleyError error) {

    }
}