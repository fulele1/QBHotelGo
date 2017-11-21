package com.xaqb.unlock.Service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.xaqb.unlock.Utils.Globals;
import com.xaqb.unlock.Utils.GsonUtil;
import com.xaqb.unlock.Utils.HttpUrlUtils;
import com.xaqb.unlock.Utils.SPUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

public class FileService extends Service {
    private boolean FbRun = true;
    private boolean isLoop = true;
    private int FiCounter = 600;
    private List<File> fileList;
    private Thread uploadThread;

    public FileService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        FbRun = false;
        isLoop = false;
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        fileList = new ArrayList();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (FbRun) {
                    try {
                        Thread.sleep(100);
                        if (FiCounter > 600) {
                            FiCounter = 0;
                            File file;
                            File[] oFiles = new File(FileService.this.getFilesDir().getAbsolutePath()).listFiles();
                            if (oFiles != null) {
                                for (int i = 0; i < oFiles.length; i++) {
                                    if (oFiles[i].getName().startsWith("咚咚开锁")) {
                                        try {
                                            file = oFiles[i];
                                            fileList.add(file);
                                            Log.i("times", "执行第" + i);
                                            try {
                                                synchronized (uploadThread) {
                                                    uploadThread.notify();
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        FiCounter++;
                    }
                }
            }
        }).start();

        uploadThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (isLoop) {
                    //如果集合不是空集
                    if (!fileList.isEmpty()) {
                        File file = fileList.remove(0);
                        uploadFile(file);
                    } else {
                        //空集合的话  工作线程等待
                        try {
                            synchronized (uploadThread) {
                                uploadThread.wait();
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        uploadThread.start();
        return super.onStartCommand(intent, flags, startId);
    }

    private void uploadFile(final File file) {
        try {
            InputStream inputStream = new FileInputStream(file);
            String address = GsonUtil.getString(inputStream);
            Map<String, Object> maps = GsonUtil.JsonToMap(address);
//            LogUtils.i("上传的文件file参数==", maps.toString());
//            LogUtils.i(maps.get("usersex").toString());
//            LogUtils.i(maps.get("idaddress").toString());
//            LogUtils.i(maps.get("usernation").toString());
            OkHttpUtils
                    .post()
                    .url(HttpUrlUtils.getHttpUrl().getOrderUrl() + "?access_token=" + SPUtils.get(FileService.this, "access_token", ""))
                    .addParams("longitude", maps.get("longitude").toString())
                    .addParams("latitude", maps.get("latitude").toString())
                    .addParams("price", maps.get("price").toString())
                    .addParams("remark", maps.get("remark").toString())
                    .addParams("staffid", maps.get("staffid").toString())
                    .addParams("username", maps.get("username").toString())
                    .addParams("usertel", maps.get("usertel").toString())
                    .addParams("useraddress", maps.get("useraddress").toString())
                    .addParams("locktype", maps.get("locktype").toString())
                    .addParams("certcode", maps.get("certcode").toString())
                    .addParams("certimg", maps.get("certimg").toString())
                    .addParams("lockimg", maps.get("lockimg").toString())
                    .addParams("usersex", maps.get("usersex").toString())
                    .addParams("idaddress", maps.get("idaddress").toString())
                    .addParams("usernation", maps.get("usernation").toString())
                    .addParams("province", maps.get("province").toString())
                    .addParams("city", maps.get("city").toString())
                    .addParams("district", maps.get("district").toString())
                    .addParams("unlocktime", maps.get("unlocktime").toString())
                    .addParams("faceimg", maps.get("faceimg").toString())
                    .build()
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int i) {
                            e.printStackTrace();
                        }

                        @Override
                        public void onResponse(String s, int i) {
                            Map<String, Object> map = GsonUtil.JsonToMap(s);
                            if (map.get("state").toString().equals(Globals.httpSuccessState)) {
                                file.delete();
                            } else {
                                return;
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
