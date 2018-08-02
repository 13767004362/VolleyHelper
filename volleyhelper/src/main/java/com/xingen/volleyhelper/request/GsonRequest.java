package com.xingen.volleyhelper.request;

import android.text.TextUtils;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.xingen.volleyhelper.listener.GsonResultListener;
import com.xingen.volleyhelper.utils.GsonUtils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Author by {xinGen}
 * Date on 2018/8/1 10:47
 */
public  class GsonRequest<T> extends Request<T> {
    private static final String PROTOCOL_CHARSET = "utf-8";
    private static final String PROTOCOL_CONTENT_TYPE = String.format("application/json; charset=%s", PROTOCOL_CHARSET);
    private Map<String, String> headers;
    private final GsonResultListener<T> resultListener;
    private final String body;

    public GsonRequest(String url, GsonResultListener<T> resultListener) {
        this(Method.GET, url, (String) null, resultListener);
    }

    public GsonRequest(String url, Object body, GsonResultListener<T> resultListener) {
        this(Method.POST, url, GsonUtils.toJson(body), resultListener);
    }

    public GsonRequest(String url, JSONObject body, GsonResultListener<T> resultListener) {
        this(Method.POST, url, body.toString(), resultListener);
    }

    public GsonRequest(int method, String url, String body, GsonResultListener<T> resultListener) {
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

    /**
     * 重写Content-type格式
     *
     * @return
     */
    @Override
    public String getBodyContentType() {
        return PROTOCOL_CONTENT_TYPE;
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        byte[] bytes = null;
        if (body != null) {
            try {
                bytes = body.getBytes(PROTOCOL_CHARSET);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return bytes;
    }
}
