package com.xaqb.unlock.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushConfig;
import com.tencent.android.tpush.XGPushManager;
import com.umeng.analytics.MobclickAgent;
import com.xaqb.unlock.R;
import com.xaqb.unlock.Utils.ActivityController;
import com.xaqb.unlock.Utils.PermissionUtils;
import com.xaqb.unlock.Views.LoadingDialog;

import java.io.File;

/**
 * Created by fl on 2017/12/11.
 */

public abstract class BaseActivityNew extends AppCompatActivity implements View.OnClickListener{
    // 加载数据对话框
    public LoadingDialog loadingDialog;
    protected String FsAppPath = "";
    protected String[] FaDialogList = {};//对话列表框列表的内容
    /**
     * 自定义对话框
     *
     * @param context
     * @param title
     * @param message
     * @param ok
     * @param no
     * @return
     */
    AlertDialog alertDialog;
    private PermissionUtils.PermissionGrant mPermissionGrant = new PermissionUtils.PermissionGrant() {
        @Override
        public void onPermissionGranted(int requestCode) {
            requestPerPass(requestCode);
        }
    };

    public String readConfig(String sName) {

        SharedPreferences oConfig = getSharedPreferences("config", Activity.MODE_PRIVATE);
        return oConfig.getString(sName, "");
    }

    public void writeConfig(String sName, String sValue) {
        SharedPreferences oConfig = getSharedPreferences("config", Activity.MODE_PRIVATE);
        SharedPreferences.Editor oEdit = oConfig.edit();//获得编辑器
        oEdit.putString(sName, sValue);
        oEdit.commit();//提交内容

    }
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            this.getWindow().getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
//        }
        XGPushConfig.enableDebug(this,true);//信鸽开启debug日志数据

        //信鸽token注册
        XGPushManager.registerPush(this, new XGIOperateCallback() {
            @Override
            public void onSuccess(Object data, int flag) {
                //token在设备卸载重装的时候有可能会变
                Log.d("TPush", "注册成功，设备token为：" + data);
            }
            @Override
            public void onFail(Object data, int errCode, String msg) {
                Log.d("TPush", "注册失败，错误码：" + errCode + ",错误信息：" + msg);
            }
        });

        //信鸽设置账号
        XGPushManager.registerPush(getApplicationContext(), "XINGE");
        //信鸽设置标签
        XGPushManager.setTag(this,"XINGE");



        try {
            ActivityController.addActivity(this);
            loadingDialog = new LoadingDialog(this);
            setupViews();
            initViews();
            initData();
            addListener();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 加载 activity_title 布局 ，并获取标题及两侧按钮
     */
    private void setupViews() {
        super.setContentView(R.layout.ac_title);

    }


    /**
     * 初始化view控件
     */
    public abstract void initViews() throws Exception;

    /**
     * 初始化数据
     */
    public abstract void initData() throws Exception;

    /**
     * 给view添加事件监听
     */
    public abstract void addListener() throws Exception;

    /**
     * 返回按钮点击后触发
     *
     * @param backwardView
     */
    public void onBackward(View backwardView) {
        finish();
    }

    /**
     * 提交按钮点击后触发
     *
     * @param forwardView
     */
    public void onForward(View forwardView) {
        doThis();
    }

    public void doThis(){

    }


    /**
     * 待发数据页面
     * @param context
     * @param title
     * @param message
     * @param ok
     * @param view
     * @return
     */
    public AlertDialog showAdialog(final Context context, String title, String message, String ok, int view) {
        alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.show();
        Window window = alertDialog.getWindow();
        window.setContentView(R.layout.loading_my_layout);
        TextView tvTitle = (TextView) window.findViewById(R.id.tv_dialog_title);
        tvTitle.setText(title);
        TextView tvMessage = (TextView) window.findViewById(R.id.tv_dialog_message);
        tvMessage.setText(message);
        Button btOk = (Button) window.findViewById(R.id.btn_dia_ok);
        btOk.setText(ok);
        btOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BaseActivityNew.this.dialogOk();
                alertDialog.dismiss();

            }
        });
        Button btNo = (Button) window.findViewById(R.id.btn_dia_no);
        btNo.setVisibility(view);
        btNo.setText("取消");
        btNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        return alertDialog;
    }


    /**
     * 更新界面
     * @param context
     * @param title
     * @param message
     * @param ok
     * @return
     */
    public AlertDialog showAdialog(final Context context, String title, String message, String ok) {
        alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.show();
        Window window = alertDialog.getWindow();
        window.setContentView(R.layout.loading_my_layout);
        TextView tvTitle = (TextView) window.findViewById(R.id.tv_dialog_title);
        tvTitle.setText(title);
        TextView tvMessage = (TextView) window.findViewById(R.id.tv_dialog_message);
        tvMessage.setText(message);
        Button btOk = (Button) window.findViewById(R.id.btn_dia_ok);
        btOk.setText(ok);
        btOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BaseActivityNew.this.dialogOk();
                alertDialog.dismiss();

            }
        });
        return alertDialog;
    }

    //判断字符串是否为空
    protected boolean textNotEmpty(String text) {
        return text != null && !text.equals("");
    }

    //检查网络连接与否
    protected boolean checkNetwork() {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) this
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
        if (mNetworkInfo != null) {
            return mNetworkInfo.isAvailable();
        }
        return false;
    }

    //对话框单击确定按钮处理
    protected void dialogOk() {

    }

    //对话框单击取消按钮处理
    protected void dialogCancel() {

    }

    //列表选择对话框
    protected AlertDialog showListDialog(String sCaption, String sList) {
        AlertDialog.Builder oBuilder = new AlertDialog.Builder(this);
        FaDialogList = sList.split(",");
        oBuilder.setItems(FaDialogList, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface oDialog, int iWhich) {
                BaseActivityNew.this.dialogList(FaDialogList[iWhich]);
                oDialog.dismiss();
            }
        });
        oBuilder.setTitle(sCaption);
        AlertDialog oDialog = oBuilder.create();
        oDialog.show();
        return oDialog;
    }

    //对话框列表选择后的处理
    protected void dialogList(String sSelect) {

    }

    //对话框的调用
    protected AlertDialog showDialog(String sCaption,
                                     String sText,
                                     String sOk,
                                     String sCancel,
                                     int iLayout) {
        AlertDialog.Builder oBuilder = new AlertDialog.Builder(this);
        if (iLayout > 0) {
            LayoutInflater oInflater = getLayoutInflater();
            View oLayout = oInflater.inflate(iLayout, null, false);
            oBuilder.setView(oLayout);
        } else
            oBuilder.setMessage(sText);
        oBuilder.setTitle(sCaption);
        if (sOk.length() > 0) {
            oBuilder.setPositiveButton(sOk, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    BaseActivityNew.this.dialogOk();
                    dialog.dismiss();
                }
            });
        }
        if (sCancel.length() > 0) {
            oBuilder.setNegativeButton(sCancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    BaseActivityNew.this.dialogCancel();
                    dialog.dismiss();
                }
            });
        }
        AlertDialog oDialog = oBuilder.create();
        oDialog.show();
        return oDialog;
    }

    public void showToast(String sMess) {
        Toast.makeText(this, sMess, Toast.LENGTH_SHORT).show();
    }

    //app的私有存储空间路径
    protected String appPath() {
        if (FsAppPath.length() == 0) {
            Context oContext = this;//首先，在Activity里获取context
            File oFile = oContext.getFilesDir();
            FsAppPath = oFile.getAbsolutePath();
        }
        return FsAppPath;
    }

    @Override
    public void onClick(View v) {
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityController.removeActivity(this);
    }

    protected void checkPer(int requestCode) {
        PermissionUtils.requestPermission(this, requestCode, mPermissionGrant);
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        PermissionUtils.requestPermissionsResult(this, requestCode, permissions, grantResults, mPermissionGrant);
    }

    //申请权限成功后执行的方法
    protected void requestPerPass(int requestCode) {

    }

}
