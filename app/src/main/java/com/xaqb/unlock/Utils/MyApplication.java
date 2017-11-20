package com.xaqb.unlock.Utils;

import android.app.Application;
import android.content.Context;

import com.alibaba.sdk.android.push.CloudPushService;
import com.alibaba.sdk.android.push.CommonCallback;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
import com.tencent.bugly.crashreport.CrashReport;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.cookie.CookieJarImpl;
import com.zhy.http.okhttp.cookie.store.PersistentCookieStore;
import com.zhy.http.okhttp.log.LoggerInterceptor;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * Created by chengneg on 2017/3/13.
 */
public class MyApplication extends Application {
    public static Context instance;
    public static String deviceId;
    public static String versionName;


    @Override
    public void onCreate() {
        super.onCreate();
        initCloudChannel(this);
        versionName = ProcUnit.getVersionName(getApplicationContext());
        CrashReport.initCrashReport(getApplicationContext());
        instance = getApplicationContext();
        CookieJarImpl cookieJar = new CookieJarImpl(new PersistentCookieStore(getApplicationContext()));
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new LoggerInterceptor("unlock"))
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .readTimeout(10000L, TimeUnit.MILLISECONDS)
                //其他配置
                .cookieJar(cookieJar)
                .build();
        OkHttpUtils.initClient(okHttpClient);
    }

    /**
     * 初始化云推送通道
     *
     * @param applicationContext
     */
    private void initCloudChannel(Context applicationContext) {
        PushServiceFactory.init(applicationContext);
        String[] tags = {"dongdongkaisuo", "xianqianbai"};
        CloudPushService pushService = PushServiceFactory.getCloudPushService();
        deviceId = pushService.getDeviceId();
//        LogUtils.i("deviceid------", deviceId);
          pushService.bindTag(CloudPushService.DEVICE_TARGET, tags, "xaqb", new CommonCallback() {
            @Override
            public void onSuccess(String s) {
//                LogUtils.i("onSuccess------", "onSuccess");
            }

            @Override
            public void onFailed(String s, String s1) {
//                LogUtils.i("onFailed------", "onFailed");
            }
        });


        pushService.register(applicationContext, new CommonCallback() {
            @Override
            public void onSuccess(String response) {
//                Log.d("阿里云", "init cloudchannel success");
            }

            @Override
            public void onFailed(String errorCode, String errorMessage) {
//                Log.d("阿里云", "init cloudchannel failed -- errorcode:" + errorCode + " -- errorMessage:" + errorMessage);
            }
        });
    }

}
