package com.xaqb.unlock.Utils;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.StrictMode;
import android.support.v4.content.FileProvider;

import com.alibaba.sdk.android.push.CloudPushService;
import com.alibaba.sdk.android.push.CommonCallback;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
import com.tencent.bugly.crashreport.CrashReport;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.cookie.CookieJarImpl;
import com.zhy.http.okhttp.cookie.store.PersistentCookieStore;
import com.zhy.http.okhttp.log.LoggerInterceptor;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.os.Build.VERSION.SDK;

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
        //fl解决7.0以上版本我发安装本地安装包的问题
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            builder.detectFileUriExposure();
        }

        versionName = ProcUnit.getVersionName(getApplicationContext());
        CrashReport.initCrashReport(getApplicationContext());
        instance = getApplicationContext();
        CookieJarImpl cookieJar = new CookieJarImpl(new PersistentCookieStore(getApplicationContext()));
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new LoggerInterceptor("qbunlock"))
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request newRequest = chain.request().newBuilder()
                                .addHeader("User-Agent",String.format("XAQianbai Android qbunlock %s","V"+MyApplication.versionName))
                                .build();
                        return chain.proceed(newRequest);
                    }
                })
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .readTimeout(10000L, TimeUnit.MILLISECONDS)
                //其他配置
                .cookieJar(cookieJar)
                .build();
        OkHttpUtils.initClient(okHttpClient);
    }


//    // SDK>24 和<24的解决方案
//    public static void openFile(Context context, File file) {
//        Intent intent = new Intent();
//        intent.setAction(android.content.Intent.ACTION_VIEW);
//        Uri uri;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//            Uri contentUri = FileProvider.getUriForFile(context,
//                    context.getApplicationContext().getPackageName() + ".provider",
//                    file);
//            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
//        } else {
//            uri = Uri.fromFile(file);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            intent.setDataAndType(uri, "application/vnd.android.package-archive");
//        }
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        context.startActivity(intent);
//    }



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
