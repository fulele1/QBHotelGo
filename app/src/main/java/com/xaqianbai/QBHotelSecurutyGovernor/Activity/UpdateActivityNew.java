package com.xaqianbai.QBHotelSecurutyGovernor.Activity;
import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.xaqianbai.QBHotelSecurutyGovernor.R;
import com.xaqianbai.QBHotelSecurutyGovernor.Utils.LogUtils;
import com.xaqianbai.QBHotelSecurutyGovernor.Utils.SPUtils;
import com.xaqianbai.QBHotelSecurutyGovernor.Utils.StatuBarUtil;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class UpdateActivityNew extends BaseActivityNew {

    protected String FsUrl = "";
    protected String savePath = "";
    protected int FiDialogType = 0;//0：下载完成 1：发生错误 2：用户中断
    protected boolean FbRun = false;
    protected ProgressBar FoBar;
    protected DownFileThread FoThread;
    protected TextView FoText;
    protected Button FoBtn;
    private UpdateActivityNew instance;
    private String downUrl,newVersion;
    @BindView(R.id.layout_titlebar)
    FrameLayout titlebar;
    Unbinder unbinder;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void initViews() {
        setContentView(R.layout.activity_update_new);
        instance = this;
        unbinder = ButterKnife.bind(instance);
        StatuBarUtil.setStatuBarLightMode(instance,getResources().getColor(R.color.bag));//修改状态栏字体颜色为黑色
        titlebar.setBackgroundColor(getResources().getColor(R.color.bag));
        TextView tvTitle = (TextView) findViewById(R.id.tv_title);
        tvTitle.setText("检查更新");
        downUrl = SPUtils.get(instance,"urlDown","").toString();
        newVersion = SPUtils.get(instance,"newVersion","").toString()+".apk";
    }

    @Override
    public void onBackward(View backwardView) {
        quit();
    }

    @Override
    public void initData() {
        savePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
        File oFile = new File(savePath);
        if (!oFile.exists()) oFile.mkdir();
        FoBar = (ProgressBar) findViewById(R.id.pbprogress);
        FoBar.setMax(100);
        FoText = (TextView) findViewById(R.id.tvmessage);
        this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG);

        FoBtn = (Button) findViewById(R.id.buttonok);

        if (FoBtn != null)
            FoBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View oView) {
                    quit();
                }
            });
        start();
    }

    @Override
    public void addListener()  {

    }

    /**
     * 获得版本信息
     *
     * @return
     */

    public String getVersionName() {
        try {
            PackageInfo info = this.getPackageManager().getPackageInfo(this.getPackageName(), 0);

            // 当前应用的版本名称
            return info.versionName;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            return "无法获得应用版本号";
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onKeyDown(int iCode, KeyEvent oEvent) {
        if (iCode == KeyEvent.KEYCODE_BACK || iCode == KeyEvent.KEYCODE_HOME || iCode == KeyEvent.KEYCODE_MENU) {
            quit();
            return false;
        }
        return super.onKeyDown(iCode, oEvent);
    }

    //0:下载成功  1：发生错误
    Handler FoHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String sError = "";
            int iPercent = 0;
            switch (msg.what) {
                case 0: //成功完成

                    FoBtn.setText("立即安装");
                    SPUtils.put(instance,"total_apk","yes");
                    FoBtn.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });

                    FiDialogType = 0;
                    showDialog("确认信息", "将要安装app，是否确定？", "确定", "取消", 0);
                    FbRun = false;
                    break;
                case 1://错误信息
                    FiDialogType = 1;
                    sError = (String) msg.obj;
                    showDialog("错误信息", sError, "确定", "", 0);
                    FbRun = false;
                    FoText.setText(sError);
                    break;

                case 10://下载进度
                    iPercent = msg.arg1;
                    if (FoBar != null) FoBar.setProgress(iPercent);
                    if (FoText != null) FoText.setText("下载进度：" + Integer.toString(iPercent) + "%");
                    break;
            }
            super.handleMessage(msg);
        }
    };

    protected void quit() {
        if (FbRun) {
            FiDialogType = 2;
            showDialog("确认信息", "将要终止文件下载，是否确定？", "确定", "取消", 0);
            FoThread.pause();
        } else
            finish();
    }

    @Override
    protected void dialogCancel() {
        switch (FiDialogType) {

            case 2:
                FoThread.resum();
                break;
        }
    }

    @Override
    protected void dialogOk() {
        switch (FiDialogType) {
            case 0:
                //安装app
                Intent oInt = new Intent(Intent.ACTION_VIEW);
                oInt.setDataAndType(Uri.fromFile(new File(savePath + "/" + newVersion)), "application/vnd.android.package-archive");
                //关键点：
                //安装完成后执行打开
                oInt.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(oInt);
                break;
            case 1:
                FbRun = false;
                break;
            case 2:
                FoThread.over();
                FbRun = false;
                FoText.setText("终止下载");
                break;
        }
    }

    class DownFileThread extends Thread {
        private int FiState = 0;

        @Override
        public void start() {
            FiState = 2;
            super.start();
        }

        public void over() {
            FiState = 0;
        }

        public void pause() {
            FiState = 1;
        }

        public void resum() {
            if (FiState == 1) FiState = 2;
        }

        protected void send(int iWhat, int iProgress, String sError) {
            Message oMess = Message.obtain();
            oMess.what = iWhat;
            oMess.arg1 = iProgress;
            oMess.obj = sError;
            FoHandler.sendMessage(oMess);
        }
        private static final int REQUEST_EXTERNAL_STORAGE = 1;
        private  String[] PERMISSIONS_STORAGE = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE };

        public  void verifyStoragePermissions(Activity activity) {
            // Check if we have write permission
            int permission = ActivityCompat.checkSelfPermission(activity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // We don't have permission so prompt the user
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,
                        REQUEST_EXTERNAL_STORAGE);
            }
        }
        @Override
        public void run() {
            final File oFile = new File(savePath + "/" + newVersion);

            verifyStoragePermissions(instance);

            try {
                DefaultHttpClient client = new DefaultHttpClient();
                HttpGet oGet = new HttpGet(downUrl);//此处的URL为http://..../path?arg1=value&....argn=value
                LogUtils.e(FsUrl);
                HttpResponse oResponse = client.execute(oGet); //模拟请求
                int iCode = oResponse.getStatusLine().getStatusCode();//返回响应码
                if (iCode == 200) {
                    long iAll = oResponse.getEntity().getContentLength();
                    if (iAll <= 1000) {
                        send(1, 0, "文件数据大小错误");
                        FiState = 0;
                        return;
                    }
                    FileOutputStream oStream = new FileOutputStream(oFile);
                    InputStream oInput = oResponse.getEntity().getContent();
                    try {
                        byte[] aBuffer = new byte[1024];
                        int iLen = -1;
                        long iCount = 0;
                        int iProgress = 0;
                        int iSend = 0;
                        int iTimeout = 0;

                        while (FiState > 0)//0:停止
                        {
                            if (FiState > 1)//1：暂停
                            {
                                iLen = oInput.read(aBuffer);
                                if (iLen > 0) {
                                    oStream.write(aBuffer, 0, iLen);
                                    iCount += iLen;
                                    if (iAll > 0) {
                                        iProgress = (int) (((float) iCount / iAll) * 100);
                                        if (iProgress > iSend) {
                                            send(10, iProgress, "");
                                            iSend = iProgress;
                                        }
                                    }
                                    iTimeout = 0;
                                    if (iCount >= iAll) {
                                        send(0, 0, "");
                                        FiState = 0;
                                        break;
                                    }
                                    //Thread.sleep(100);
                                } else {
                                    Thread.sleep(100);
                                    iTimeout += 1;
                                    if (iTimeout > 600) {
                                        send(1, 0, "网络传输超时");
                                        break;
                                    }
                                }
                            } else Thread.sleep(1000);
                        } //while
                    } finally {
                        oInput.close();
                        oStream.close();
                    }


                } else {
                    send(1, 0, oResponse.getStatusLine().getReasonPhrase());
                }
                FiState = 0;
            } catch (Exception E) {
                send(1, 0, E.getMessage());

            }
        }
    }

    protected void start() {
        File oFile = new File(savePath + "/" + newVersion);
        if (oFile.exists()) oFile.delete();
        FoThread = new DownFileThread();
        FbRun = true;
        FoThread.start();
        FoText.setText("开始下载.....");
    }
}
