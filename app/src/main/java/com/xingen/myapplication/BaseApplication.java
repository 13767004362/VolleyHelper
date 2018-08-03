package com.xingen.myapplication;

import android.app.Application;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.xingen.volleyhelper.VolleyHelper;


/**
 * Author by {xinGen}
 * Date on 2018/8/3 09:53
 */
public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        VolleyHelper.getInstance().init(requestQueue);
    }
}
