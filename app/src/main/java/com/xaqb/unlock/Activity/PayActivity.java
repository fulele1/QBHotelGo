package com.xaqb.unlock.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.xaqb.unlock.R;
import com.xaqb.unlock.Utils.ActivityController;
import com.xaqb.unlock.Utils.Globals;
import com.xaqb.unlock.Utils.GsonUtil;
import com.xaqb.unlock.Utils.HttpUrlUtils;
import com.xaqb.unlock.Utils.SPUtils;
import com.xaqb.unlock.zxing.activity.CaptureActivity;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.Map;

import okhttp3.Call;


/**
 * 支付页面
 */
public class PayActivity extends BaseActivity {
    private PayActivity instance;
    private int dialogType;
    private ProgressDialog progressDialog;
    private Button btPayOnline, btPayCash, btYes;
    private String scanResult, orderId, price, payId = "";
    private int payNetWordTimes = 0;
    private TextView tvPrice;
    //是否支付成功
    private boolean isPaySuc = false, isFirstGetPayStatus = false, isQuery = false;
    private Thread payStatusThread;
    private Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                //查询到支付状态成功
                case 101:
                    isPaySuc = true;
                    break;
                //查询到支付状态失败
                case 102:
                    if (!payStatusThread.isAlive()) {
                        payStatusThread.start();
                    }
                    break;
                case 103:
                    progressDialog.dismiss();
                    dialogType = 1;
                    showDialog("提示", "支付异常，请重新支付或在订单列表刷新支付状态", "确定", "", 0);
                    break;
            }
            super.handleMessage(msg);
        }
    };


    @Override
    public void initTitleBar() {
        setTitle("订单支付");
        showBackwardView(true);
    }

    @Override
    public void initViews() {
        setContentView(R.layout.pay_activity);
        instance = this;
        assignViews();
    }

    private void assignViews() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setTitle("支付状态");

        btPayOnline = (Button) findViewById(R.id.bt_pay_online);
        btPayCash = (Button) findViewById(R.id.bt_pay_money);
        btYes = (Button) findViewById(R.id.bt_yes);
        tvPrice = (TextView) findViewById(R.id.tv_price);

        /**
         * 5秒后自动查询下一次，查询30秒后，仍然失败的话，显示支付失败，等待用户手动刷新订单查看支付结果
         */
        payStatusThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!isPaySuc) {
                    try {
                        //访问接口次数大于5，认为支付失败，待用户手动刷新订单信息
                        if (payNetWordTimes > 5) {
                            isPaySuc = true;
                            myHandler.sendEmptyMessage(103);
                            return;
                        }
                        payNetWordTimes++;
                        Thread.sleep(5000);
                        isFirstGetPayStatus = false;
                        getPayResult();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }

    @Override
    public void initData() {
        Intent intent = getIntent();
        orderId = intent.getStringExtra("or_id");
        price = intent.getStringExtra("price");
        if (textNotEmpty(price)) {
            tvPrice.setText("需要支付的费用:" + price + "元");
        }

    }

    @Override
    public void addListener() {
        btPayCash.setOnClickListener(instance);
        btPayOnline.setOnClickListener(instance);
        btYes.setOnClickListener(instance);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_pay_money:
                dialogType = 0;
                showDialog("提示", "确定客户已经使用现金支付吗？", "确定", "取消", 0);
                break;
            case R.id.bt_pay_online:
                if (scanResult == null) {
                    Intent intent = new Intent(instance, CaptureActivity.class);
                    startActivityForResult(intent, 0);
                } else {
                    payOnline();
                }
                break;
            case R.id.bt_yes:
                finish();
                break;
        }
    }

    @Override
    protected void dialogOk() {
        switch (dialogType) {
            case 0:
                payCash();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 0:
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        scanResult = bundle.getString("result");
                        progressDialog.setMessage("正在支付，请稍后...");
                        progressDialog.setCanceledOnTouchOutside(false);
                        progressDialog.show();
                        payOnline();
                    }
                }
                break;
        }
    }


    /**
     * 在线支付
     */
    private void payOnline() {
        if (!checkNetwork()) {
            showToast(getResources().getString(R.string.network_not_alive));
            return;
        }
        OkHttpUtils.get()
                .url(HttpUrlUtils.getHttpUrl().getPayOnline() + "orderid/" + orderId + "/barcode/" + scanResult + "/cash/" + price + "?access_token=" + SPUtils.get(instance, "access_token", ""))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        try {
                            Map<String, Object> map = GsonUtil.JsonToMap(s);
                            if (map.get("state").toString().equals(Globals.httpSuccessState)) {
                                payId = map.get("id").toString();
                                isFirstGetPayStatus = true;
                                isQuery = false;
                                getPayResult();
                            } else if (map.get("state").toString().equals(Globals.httpTokenFailure)) {
                                ActivityController.finishAll();
                                showToast("登录失效，请重新登录");
                                startActivity(new Intent(instance, LoginActivity.class));
                            } else {
                                showToast(map.get("mess").toString());
                                progressDialog.dismiss();
                                dialogType = 1;
                                showDialog("提示", "支付失败，请稍后再试", "确定", "", 0);
                                return;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

    }

    /**
     * 获取支付结果
     */
    private void getPayResult() {
        if (!checkNetwork()) {
            showToast(getResources().getString(R.string.network_not_alive));
            return;
        }
        OkHttpUtils.get()
                .url(HttpUrlUtils.getHttpUrl().getPayResult() + "opid/" + payId + "?access_token=" + SPUtils.get(instance, "access_token", ""))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        if (isFirstGetPayStatus) {
                            myHandler.sendEmptyMessage(102);
                        } else if (isQuery) {
                            progressDialog.dismiss();
                            dialogType = 1;
                            showDialog("提示", "支付失败", "确定", "", 0);
                        }
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        try {
                            Map<String, Object> map = GsonUtil.JsonToMap(s);
                            if (map.get("state").toString().equals(Globals.httpSuccessState)) {
                                btPayCash.setVisibility(View.GONE);
                                btPayOnline.setVisibility(View.GONE);
                                btYes.setVisibility(View.VISIBLE);
                                tvPrice.setText("支付成功");
                                myHandler.sendEmptyMessage(101);
                                progressDialog.dismiss();
                                dialogType = 1;
                                showDialog("提示", "支付成功", "确定", "", 0);
                            } else if (map.get("state").toString().equals(Globals.httpTokenFailure)) {
                                ActivityController.finishAll();
                                showToast("登录失效，请重新登录");
                                startActivity(new Intent(instance, LoginActivity.class));
                            } else {
                                if (isFirstGetPayStatus) {
                                    myHandler.sendEmptyMessage(102);
                                } else if (isQuery) {
                                    progressDialog.dismiss();
                                    dialogType = 1;
                                    showDialog("提示", map.get("mess").toString(), "确定", "", 0);
                                }
                                return;
                            }
                        } catch (Exception e) {
                            if (isFirstGetPayStatus) {
                                myHandler.sendEmptyMessage(102);
                            } else if (isQuery) {
                                progressDialog.dismiss();
                                dialogType = 1;
                                showDialog("提示", "支付失败", "确定", "", 0);
                            }
                            e.printStackTrace();
                        }
                    }
                });
    }

    /**
     * 现金支付
     */
    private void payCash() {
        if (!checkNetwork()) {
            showToast(getResources().getString(R.string.network_not_alive));
            return;
        }
        loadingDialog.show("正在支付...");
        OkHttpUtils.get()
                .url(HttpUrlUtils.getHttpUrl().getPayCash() + "orderid/" + orderId + "?access_token=" + SPUtils.get(instance, "access_token", ""))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        e.printStackTrace();
                        loadingDialog.dismiss();
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        try {
                            loadingDialog.dismiss();
                            Map<String, Object> map = GsonUtil.JsonToMap(s);
                            if (map.get("state").toString().equals(Globals.httpSuccessState)) {
                                btPayCash.setVisibility(View.GONE);
                                btPayOnline.setVisibility(View.GONE);
                                btYes.setVisibility(View.VISIBLE);
                                tvPrice.setText("支付成功");
                            } else if (map.get("state").toString().equals(Globals.httpTokenFailure)) {
                                ActivityController.finishAll();
                                showToast("登录失效，请重新登录");
                                startActivity(new Intent(instance, LoginActivity.class));
                            } else {
                                showToast(map.get("mess").toString());
                                return;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
}
