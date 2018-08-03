package com.xingen.volleyhelper.request;

import android.text.TextUtils;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.xingen.volleyhelper.listener.GsonResultListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Author by {xinGen}
 * Date on 2018/8/1 10:48
 */
public  class FormRequest<T> extends Request<T> {
    private final GsonResultListener<T> resultListener;
    private Map<String, String> body;
    private Map<String, String> headers;
    public FormRequest(String url, GsonResultListener<T> resultListener) {
        this(Method.GET, url, null, resultListener);
    }
    public FormRequest(String url, Map<String, String> body, GsonResultListener<T> resultListener) {
        this(Method.POST, url, body, resultListener);
    }
    public FormRequest(int method, String url, Map<String, String> body, GsonResultListener<T> resultListener) {
        super(method, url, resultListener);
        this.headers = new HashMap<>();
        this.body = body;
        this.resultListener = resultListener;
    }
    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        return this.resultListener.parseResponse(response);
    }
    @Override
    protected void deliverResponse(T response) {
        this.resultListener.onResponse(response);
    }
    @Override
    public Map<String, String> getHeaders() {
        return headers;
    }
    public Map<String, String> setHeader(String key, String content) {
        if (!TextUtils.isEmpty(key) && !TextUtils.isEmpty(content)) {
            headers.put(key, content);
        }
        return headers;
    }
    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return this.body;
    }
}
