package com.xingen.volleyhelper.hook;

import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;

/**
 * Author by {xinGen}
 * Date on 2018/8/1 10:54
 */
public class HookVolleyManager {
    private final  String TAG=HookVolleyManager.class.getSimpleName();
    public void init(Object requestQueue) {
        if (requestQueue == null) {
            return;
        }
        try {
            Log.i(TAG, " HookVolleyManager init() ");
            Class<?> requestQueueClass = requestQueue.getClass();
            Field networkField = requestQueueClass.getDeclaredField("mNetwork");
            networkField.setAccessible(true);
            Object network = networkField.get(requestQueue);
            //接下来，设置动态代理
            Object networkProxy = Proxy.newProxyInstance(requestQueue.getClass().getClassLoader(), network.getClass().getInterfaces(), new NetWorkHandler(network));
            networkField.set(requestQueue, networkProxy);
            Field mDispatchersField = requestQueueClass.getDeclaredField("mDispatchers");
            mDispatchersField.setAccessible(true);
            Object[] networkDispatchers = (Object[]) mDispatchersField.get(requestQueue);
            //代理掉网络线程中的network
            for (Object object : networkDispatchers) {
                Class<?> mClass = object.getClass();
                Field networkFields = mClass.getDeclaredField("mNetwork");
                networkFields.setAccessible(true);
                networkFields.set(object, networkProxy);
            }
            //替代传输层
            Class<?> baseNetworkClass = network.getClass();
            Field mBaseHttpStackField = null;
            try { //适配volley 1.1
                mBaseHttpStackField = baseNetworkClass.getDeclaredField("mBaseHttpStack");
                mBaseHttpStackField.setAccessible(true);
                mBaseHttpStackField.set(network, new MyHttpStack());
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (mBaseHttpStackField == null) { //适配volley 1.0
                Field httpStackField = baseNetworkClass.getDeclaredField("mHttpStack");
                httpStackField.setAccessible(true);
                httpStackField.set(network, new MyHttpStack());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
