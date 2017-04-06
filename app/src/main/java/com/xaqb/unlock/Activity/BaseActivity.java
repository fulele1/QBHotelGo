package com.xaqb.unlock.Activity;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.xaqb.unlock.R;
import com.xaqb.unlock.Views.LoadingDialog;

import java.io.File;


public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView tv_title;
    private ImageView iv_backward;
    private TextView tv_forward;
    private FrameLayout mContentLayout;
    private LinearLayout llRoot;
    private FrameLayout layout_titlebar;
    protected String FsAppPath = "";
    protected String[] FaDialogList = {};//对话列表框列表的内容


    // 加载数据对话框
    public LoadingDialog loadingDialog;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
//        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        try {
            loadingDialog = new LoadingDialog(this);
            setupViews();
            initTitleBar();
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
        llRoot = (LinearLayout) findViewById(R.id.llRoot);
        layout_titlebar = (FrameLayout) findViewById(R.id.layout_titlebar);
        tv_title = (TextView) findViewById(R.id.tv_title);
        mContentLayout = (FrameLayout) findViewById(R.id.layout_content);
        iv_backward = (ImageView) findViewById(R.id.iv_backward);
        tv_forward = (TextView) findViewById(R.id.tv_forward);
    }

    /**
     * 初始化设置标题栏
     */
    public abstract void initTitleBar();

    /**
     * 设置标题栏是否可见
     *
     * @param visibility
     */
    public void setTitleBarVisible(int visibility) {
        layout_titlebar.setVisibility(visibility);
    }

    /**
     * 是否显示返回按钮
     *
     * @param show true则显示
     */
    protected void showBackwardView(boolean show) {
        if (iv_backward != null) {
            if (show) {
//                mBackwardbButton.setText(backwardResid);
                iv_backward.setVisibility(View.VISIBLE);
            } else {
                iv_backward.setVisibility(View.INVISIBLE);
            }
        } // else ignored
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
//        Toast.makeText(this, "点击返回，可在此处调用finish()", Toast.LENGTH_LONG).show();
        finish();
    }

    /**
     * 提交按钮点击后触发
     *
     * @param forwardView
     */
    public void onForward(View forwardView) {
        Toast.makeText(this, "点击了标题右上角按钮", Toast.LENGTH_LONG).show();
    }

    //设置标题内容
    @Override
    public void setTitle(int titleId) {
        tv_title.setText(titleId);
    }

    //设置标题内容
    @Override
    public void setTitle(CharSequence title) {
        tv_title.setText(title);
    }

    //设置标题文字颜色
    @Override
    public void setTitleColor(int textColor) {
        tv_title.setTextColor(textColor);
    }


    //取出FrameLayout并调用父类removeAllViews()方法
    @Override
    public void setContentView(int layoutResID) {
        mContentLayout.removeAllViews();
        View.inflate(this, layoutResID, mContentLayout);
        onContentChanged();
    }

    @Override
    public void setContentView(View view) {
        mContentLayout.removeAllViews();
        mContentLayout.addView(view);
        onContentChanged();
    }

    /* (non-Javadoc)
     * @see android.app.Activity#setContentView(android.view.View, android.view.ViewGroup.LayoutParams)
     */
    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        mContentLayout.removeAllViews();
        mContentLayout.addView(view, params);
        onContentChanged();
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
        Builder oBuilder = new Builder(this);
        FaDialogList = sList.split(",");
        oBuilder.setItems(FaDialogList, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface oDialog, int iWhich) {
                BaseActivity.this.dialogList(FaDialogList[iWhich]);
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
        Builder oBuilder = new Builder(this);
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
                    BaseActivity.this.dialogOk();
                    dialog.dismiss();
                }
            });
        }
        if (sCancel.length() > 0) {
            oBuilder.setNegativeButton(sCancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    BaseActivity.this.dialogCancel();
                    dialog.dismiss();
                }
            });
        }
        AlertDialog oDialog = oBuilder.create();
        oDialog.show();
        return oDialog;
    }

    protected void showToast(String sMess) {
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

}
