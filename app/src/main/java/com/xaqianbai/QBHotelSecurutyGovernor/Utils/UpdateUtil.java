package com.xaqianbai.QBHotelSecurutyGovernor.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.xaqianbai.QBHotelSecurutyGovernor.Activity.UpdateActivityNew;
import com.xaqianbai.QBHotelSecurutyGovernor.R;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.Map;

import okhttp3.Call;

/**
 * Created by fule on 2018/7/26.
 */

public class UpdateUtil {
    private Activity mContext;
    private String mNewVersion;
    private String urlDown;
    private String mAid;
    private AlertDialog alertDialog;

    public UpdateUtil(Activity context, String aid) {
        mContext = context;
        mAid = aid;
    }

    /**
     * 下载新版本
     */
    Handler FoHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0: //获取版本号
                    int newVersion = Integer.parseInt(mNewVersion.replace(".", ""));
                    int nowVersion = Integer.parseInt(getVersionName().replace(".", ""));

                    if (newVersion <= nowVersion) {
                        SPUtils.put(mContext,"late","true");
                        if (SPUtils.get(mContext,"isclickFragment","").equals("true")){
                            showAdialog(mContext,"提示", "已经是最新版本", "确定", "",View.GONE);
                            SPUtils.put(mContext,"isclickFragment","false");
                        }
                    } else if (newVersion > nowVersion) {
                        SPUtils.put(mContext,"late","false");
                        showAdialog(mContext,"发现新版本", SPUtils.get(mContext,"av_content","").toString(), "立刻更新","" ,View.VISIBLE);}
                    break;

                case 1:
                    downVersion();
                    break;

            }
            super.handleMessage(msg);
        }
    };




    /**
     * 获取版本号
     *
     * @return
     */
    public String getVersionName() {
        try {
            PackageInfo info = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);

            // 当前应用的版本名称
            return info.versionName;

        } catch (Exception e) {
            return "";
        }
    }




    //获取版本信息
    public void getVersion() {

        OkHttpUtils
                .get()
                .url("http://update.qbchoice.com/api/newVersion/"+mAid)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        Toast.makeText(mContext, e.toString(),Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        Map<String, Object> data = GsonUtil.JsonToMap(s);
                        if (data.get("state").toString().equals("1")) {
                            Toast.makeText(mContext, data.get("mess").toString(),Toast.LENGTH_SHORT).show();
                            return;
                        } else if (data.get("state").toString().equals("0")) {
                            mNewVersion = data.get("data").toString();
                            FoHandler.sendMessage(M(1));
                        }
                    }
                });
    }


    protected Message M(int iWhat) {
        Message oMess = new Message();
        oMess.what = iWhat;
        return oMess;
    }


    protected void downVersion() {
        OkHttpUtils
                .get()
                .url("http://update.qbchoice.com/api/version/"+mAid+"/"+ mNewVersion)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        Toast.makeText(mContext, e.toString(),Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        Map<String, Object> data = GsonUtil.JsonToMap(s);
                        if (data.get("state").toString().equals("1")) {
                            Toast.makeText(mContext, data.get("mess").toString(),Toast.LENGTH_SHORT).show();
                            return;
                        } else if (data.get("state").toString().equals("0")) {
                            urlDown = data.get("av_downurl").toString();
                            String content = data.get("av_content").toString();
                            LogUtils.e("content",content);
                            SPUtils.put(mContext,"urlDown",urlDown);
                            SPUtils.put(mContext,"newVersion",mNewVersion);
                            SPUtils.put(mContext,"av_content",content);
                            FoHandler.sendMessage(M(0));
                        }
                    }
                });
    }

    /**
     *
     * @param context
     * @param title
     * @param message
     * @param ok
     * @param view
     * @return
     */
    public AlertDialog showAdialog(final Context context, String title, String message, String ok, String no,int view) {
        alertDialog = new AlertDialog.Builder(context,R.style.update_dialog).create();
        alertDialog.setCancelable(false);// 不可以用“返回键”取消
        alertDialog.show();
        Window window = alertDialog.getWindow();
        window.setContentView(R.layout.my_dialog);
        TextView tvTitle = (TextView) window.findViewById(R.id.tv_dialog_title);
        tvTitle.setText(title);
        TextView tvMessage = (TextView) window.findViewById(R.id.tv_dialog_message);
        tvMessage.setText(message);
        Button btOk = (Button) window.findViewById(R.id.btn_dia_ok);
        btOk.setVisibility(view);
        btOk.setText(ok);
        btOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogOk();
                alertDialog.dismiss();

            }
        });
        Button btNo = (Button) window.findViewById(R.id.btn_dia_no);

        btNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        return alertDialog;
    }

    /**
     * 对话框单击确定按钮处理
     */
    protected void dialogOk() {
        mContext.startActivity(new Intent(mContext,UpdateActivityNew.class));
    }
}