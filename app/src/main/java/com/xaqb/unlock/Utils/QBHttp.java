package com.xaqb.unlock.Utils;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.xaqb.unlock.Activity.LoginActivity;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.GetBuilder;
import com.zhy.http.okhttp.builder.OkHttpRequestBuilder;
import com.zhy.http.okhttp.builder.PostFormBuilder;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.Map;

import okhttp3.Call;

/**
 * Created by lenovo on 2017/4/19.
 * 网络请求封装
 */
public class QBHttp {
    protected static TokenManager tokenManager;
    protected static int count;
//    private void request(String method, String httpUrl, Map<String, Object> params, final QBCallback callback) {
//
//
//        if (method.equals("get")) {
//            GetBuilder builder = OkHttpUtils.get();
//
//        } else if (method.equals("post")) {
//            builder = OkHttpUtils.post();
//        }
//
//        if (params != null) {
//            for (int i = 0; i < params.size(); i++) {
//
//            }
//        } else {
//        }
//        builder = builder.url(httpUrl);
//
//        builder.url(httpUrl)
//                .build()
//                .execute(new StringCallback() {
//                    @Override
//                    public void onError(Call call, Exception e, int i) {
//                        callback.doError();
//                    }
//
//                    @Override
//                    public void onResponse(String s, int i) {
//                        try {
//                            Map<?, ?> map = GsonUtil.JsonToMap(s);
//                            callback.doWork(map);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });
//    }

    private static void request(final Context context, final OkHttpRequestBuilder builder, final QBCallback callback) {
        tokenManager = new TokenManager(context);
        //根据时间判断token是否失效
        if (!tokenManager.checkToken()) {
            if (tokenManager.checkRefreshToken()) {
                //   token 失效refreshtoken未失效 调用 refresh  refreshToken(context, callback);
                refreshToken(context, new QBCallback() {
                    @Override
                    public void doWork(Map<?, ?> map) {

                    }

                    @Override
                    public void doError() {

                    }

                    @Override
                    public void reDoWork() {
                        request(context, builder, callback);
                    }
                });
                return;
            }
        }
        //token未失效，直接调用接口
        builder.build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        callback.doError();
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        try {
                            Map<?, ?> map = GsonUtil.JsonToMap(s);
                            if (map.get("state").toString().equals(Globals.httpSuccessState)) {
                                callback.doWork(map);
                            } else if (map.get("state").toString().equals(Globals.httpTokenFailure)) {
//                                autoLogin();
                                autoLogin(context, new QBCallback() {

                                    @Override
                                    public void doWork(Map<?, ?> map) {

                                    }

                                    @Override
                                    public void doError() {

                                    }

                                    @Override
                                    public void reDoWork() {
                                        request(context, builder, callback);
                                    }
                                });
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    public static void post(Context context, String httpUrl, Map<String, Object> params, QBCallback callback) {
        PostFormBuilder builder = OkHttpUtils.post();
        builder = builder.url(httpUrl);
        if (params != null) {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                builder = builder.addParams(entry.getKey(), entry.getValue().toString());
            }
        }
        request(context, builder, callback);
    }


    public static void get(Context context, String httpUrl, Map<String, Object> params, QBCallback callback) {
        GetBuilder builder = OkHttpUtils.get();
        builder = builder.url(httpUrl);
        if (params != null) {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                builder = builder.addParams(entry.getKey(), entry.getValue().toString());
            }
        }
        request(context, builder, callback);
    }

    private static void refreshToken(final Context context, final QBCallback callback) {
        OkHttpUtils.get()
                .url(HttpUrlUtils.getHttpUrl().getrefreshToken() + tokenManager.getAccessToken())
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        autoLogin(context, callback);
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        try {
                            Map<?, ?> map = GsonUtil.JsonToMap(s);
                            if (map.get("state").toString().equals(Globals.httpSuccessState)) {
                                tokenManager.setAccessToken(map.get("access_token").toString());
                                tokenManager.setRefreshToken(map.get("refresh_token").toString());
                                tokenManager.setTokenTime(System.currentTimeMillis());
                                tokenManager.setRefreshTokenTime(System.currentTimeMillis());
                                callback.reDoWork();
                            } else {
                                autoLogin(context, callback);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

    }

    protected static void autoLogin(final Context context, final QBCallback callback) {
        //增加计数，访问多次则取消访问
        count++;
        if (count > 5) {
            count = 0;
            ActivityController.finishAll();
            Toast.makeText(context, "登录失效，请重新登录", Toast.LENGTH_SHORT).show();
            context.startActivity(new Intent(context, LoginActivity.class));
            return;
        }
        OkHttpUtils
                .post()
                .url(HttpUrlUtils.getHttpUrl().getLoginUrl())
                .addParams("name", SPUtils.get(context, "userName", "").toString())
                .addParams("pwd", SPUtils.get(context, "userPsw", "").toString())
                .addParams("deviceid", MyApplication.deviceId)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        try {
                            Map<?, ?> map = GsonUtil.JsonToMap(s);
                            if (map.get("state").toString().equals(Globals.httpSuccessState)) {
                                tokenManager.setAccessToken(map.get("access_token").toString());
                                tokenManager.setRefreshToken(map.get("refresh_token").toString());
                                tokenManager.setTokenTime(System.currentTimeMillis());
                                tokenManager.setRefreshTokenTime(System.currentTimeMillis());
                                callback.reDoWork();
                            } else {
                                ActivityController.finishAll();
                                Toast.makeText(context, "登录失效，请重新登录", Toast.LENGTH_SHORT).show();
                                context.startActivity(new Intent(context, LoginActivity.class));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                });
    }

}

