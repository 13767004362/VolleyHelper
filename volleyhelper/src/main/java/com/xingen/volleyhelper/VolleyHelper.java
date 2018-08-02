package com.xingen.volleyhelper;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.xingen.volleyhelper.hook.HookVolleyManager;
import com.xingen.volleyhelper.listener.GsonResultListener;
import com.xingen.volleyhelper.request.FormRequest;
import com.xingen.volleyhelper.request.GsonRequest;

import org.json.JSONObject;

/**
 * Author by {xinGen}
 * Date on 2018/8/1 10:55
 */
public class VolleyHelper {
    private RequestQueue requestQueue;
    private static VolleyHelper instance;
    private HookVolleyManager hookVolleyManager;

    static {
        instance = new VolleyHelper();
    }

    private VolleyHelper() {
        hookVolleyManager = new HookVolleyManager();
    }

    public static VolleyHelper getInstance() {
        return instance;
    }

    public void init(RequestQueue requestQueue) {
        this.requestQueue = requestQueue;
        this.hookVolleyManager.init(requestQueue);
    }

    public <T> void sendGsonRequest(String url, JSONObject body, GsonResultListener<T> gsonResultListener) {
        sendGsonRequest(new GsonRequest(url, body, gsonResultListener));
    }

    public <T> void sendFormRequest(String url, GsonResultListener<T> gsonResultListener) {
        sendFormRequest(new FormRequest(url, gsonResultListener));
    }

    public void sendGsonRequest(GsonRequest gsonRequest) {
        sendRequest(gsonRequest);
    }

    public void sendFormRequest(FormRequest formRequest) {
        sendRequest(formRequest);
    }

    public Request sendRequest(Request request) {
        if (requestQueue == null) {
            return request;
        }
        this.requestQueue.add(request);
        return request ;
    }
    public void cancelRequest(String tag) {
        if (requestQueue == null) {
            return;
        }
        this.requestQueue.cancelAll(tag);
    }
}
