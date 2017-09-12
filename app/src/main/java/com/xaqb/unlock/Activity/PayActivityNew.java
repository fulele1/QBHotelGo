package com.xaqb.unlock.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IdRes;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.xaqb.unlock.R;
import com.xaqb.unlock.Utils.ActivityController;
import com.xaqb.unlock.Utils.Globals;
import com.xaqb.unlock.Utils.GsonUtil;
import com.xaqb.unlock.Utils.HttpUrlUtils;
import com.xaqb.unlock.Utils.LogUtils;
import com.xaqb.unlock.Utils.SPUtils;
import com.xaqb.unlock.zxing.activity.CaptureActivity;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.Map;

import okhttp3.Call;


public class PayActivityNew extends BaseActivity implements View.OnClickListener {

    private PayActivityNew instance;
    private TextView[] tv;
    private TextView tvResult, tvAtPrice;
    private ImageView iv_del;
    private String strResult;     //
    private int currentIndex = -1;   //用于记录当前输入位置

    private int dialogType, payType = 0;
    private ProgressDialog progressDialog;
    private Button btPayOnline;
    private String scanResult, orderId, apId, price, atPrice, totalPrice;
    private int payNetWordTimes = 0;
    //    private EditText etPrice;
    private RadioGroup rbPayType;
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
                    showDialog("提示", "支付异常，请重新支付或在刷新支付状态", "确定", "", 0);
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    public void initTitleBar() {
        setTitle("订单支付");
        showBackwardView(true);
//        showForwardView(true, "跳过");
    }

    @Override
    public void initViews() throws Exception {
        setContentView(R.layout.activity_pay_new);
        instance = this;
        assignViews();
    }

//    @Override
//    public void onForward(View forwardView) {
////        super.onForward(forwardView);
//        dialogType = 0;
//        showDialog("提示", "确定跳过支付吗？", "确定", "取消", 0);
////        finish();
//    }

    private void assignViews() {
        tv = new TextView[13];
        tv[0] = (TextView) findViewById(R.id.pay_keyboard_zero);
        tv[1] = (TextView) findViewById(R.id.pay_keyboard_one);
        tv[2] = (TextView) findViewById(R.id.pay_keyboard_two);
        tv[3] = (TextView) findViewById(R.id.pay_keyboard_three);
        tv[4] = (TextView) findViewById(R.id.pay_keyboard_four);
        tv[5] = (TextView) findViewById(R.id.pay_keyboard_five);
        tv[6] = (TextView) findViewById(R.id.pay_keyboard_sex);
        tv[7] = (TextView) findViewById(R.id.pay_keyboard_seven);
        tv[8] = (TextView) findViewById(R.id.pay_keyboard_eight);
        tv[9] = (TextView) findViewById(R.id.pay_keyboard_nine);
        tv[10] = (TextView) findViewById(R.id.pay_keyboard_doublezero);
        tv[11] = (TextView) findViewById(R.id.pay_keyboard_point);
        tv[12] = (TextView) findViewById(R.id.pay_keyboard_submit);
        tvResult = (TextView) findViewById(R.id.tv_result);
        tvAtPrice = (TextView) findViewById(R.id.tv_at_price);
        iv_del = (ImageView) findViewById(R.id.pay_keyboard_del);

        strResult = "";

        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setTitle("支付状态");

        btPayOnline = (Button) findViewById(R.id.bt_pay_online);
        rbPayType = (RadioGroup) findViewById(R.id.rg_pay_type);
//        btPayCash = (Button) findViewById(R.id.bt_pay_money);
//        btYes = (Button) findViewById(R.id.bt_yes);
//        etPrice = (EditText) findViewById(R.id.et_price);

        /**
         * 5秒后自动查询下一次，查询30秒后，仍然失败的话，显示支付失败，等待用户手动刷新订单查看支付结果
         */
        payStatusThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!isPaySuc) {
                    try {
                        LogUtils.i("查询支付返回结果第-" + payNetWordTimes + "-次");
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
    public void initData() throws Exception {
        orderId = getIntent().getStringExtra("or_id");
        atPrice = getIntent().getStringExtra("pay_price");
        totalPrice = getIntent().getStringExtra("total_price");
        StringBuffer payResult = new StringBuffer();
        if (textNotEmpty(atPrice)) {
            payResult.append("已支付：" + atPrice + "元");
        }
        if (textNotEmpty(totalPrice)) {
            payResult.append("     总金额：" + totalPrice + "元");
        }
        tvAtPrice.setText(payResult);

        price = Float.parseFloat(totalPrice) - Float.parseFloat(atPrice) + "";

        if (textNotEmpty(price)) {
            tvResult.setText(price);
        }

    }

    @Override
    public void addListener() throws Exception {
        for (int i = 0; i < 13; i++) {
            tv[i].setOnClickListener(this);
        }
        iv_del.setOnClickListener(this);
        rbPayType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                if (i == R.id.rb_online_pay) {
                    payType = 0;
                } else if (i == R.id.rb_offline_pay) {
                    payType = 1;
                }
            }
        });
    }

    @Override
    protected void dialogOk() {
        super.dialogOk();
        switch (dialogType) {
            case 0:
                finish();
                break;
            case 2:
                setResult(100);
                finish();
                break;
            case 3:
                payCash();
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pay_keyboard_one:
                getPass("1");
                break;
            case R.id.pay_keyboard_two:
                getPass("2");
                break;
            case R.id.pay_keyboard_three:
                getPass("3");
                break;
            case R.id.pay_keyboard_four:
                getPass("4");
                break;
            case R.id.pay_keyboard_five:
                getPass("5");
                break;
            case R.id.pay_keyboard_sex:
                getPass("6");
                break;
            case R.id.pay_keyboard_seven:
                getPass("7");
                break;
            case R.id.pay_keyboard_eight:
                getPass("8");
                break;
            case R.id.pay_keyboard_nine:
                getPass("9");
                break;
            case R.id.pay_keyboard_zero:
                getPass("0");
                break;
            case R.id.pay_keyboard_doublezero:
                if (strResult.length() == 0) {
                    break;
                }
                getPass("00");
                break;
            case R.id.pay_keyboard_point:
                if (strResult.length() == 0 || strResult.contains(".")) {
                    break;
                }
                getPass(".");
                break;
            case R.id.pay_keyboard_submit:

                //执行支付扫描
                String inputPrice = tvResult.getText().toString();

                if (!textNotEmpty(inputPrice)) {
                    showToast("请输入支付金额");
                    return;
                }
                if (Double.parseDouble(inputPrice) == 0) {
                    showToast("输入金额格式有误");
                    return;
                }
                if (Float.parseFloat(inputPrice) - Float.parseFloat(price) > 0.0001) {
                    showToast("输入金额大于需支付金额");
                    return;
                }
                price = inputPrice;
//                char[] chars = price.toCharArray();
//                if (chars[0] == 0) {
//
//                }
//                if (scanResult == null) {
                if (payType == 0) {
                    Intent intent = new Intent(instance, CaptureActivity.class);
                    startActivityForResult(intent, 0);
                } else if (payType == 1) {
                    dialogType = 3;
                    showDialog("提示", "确定用户支付" + price + "元吗？", "确定", "取消", 0);
                }
//                } else {
//                    payOnline();
//                }
                break;
            case R.id.pay_keyboard_del:
                deleteStr();
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
                        LogUtils.i(scanResult);
//                        et.setText(scanResult);
                        progressDialog.setMessage("正在支付，请稍后...");
                        progressDialog.setCanceledOnTouchOutside(false);
                        progressDialog.show();
                        payOnline();
                    }
                }
                break;
        }
    }

    private void deleteStr() {
        strResult = tvResult.getText().toString().trim();
        if (strResult.length() > 0) {
            strResult = strResult.substring(0, strResult.length() - 1);
            tvResult.setText(strResult);
        }
    }

    public void getPass(String str) {
        strResult = strResult + str;
//        currentIndex = sbResult.length();
//        if (currentIndex >= -1 && currentIndex < 10) {
        tvResult.setText(strResult);
//        }
    }

    /**
     * 在线支付
     */
    private void payOnline() {
        if (!checkNetwork()) {
            showToast(getResources().getString(R.string.network_not_alive));
            return;
        }
        LogUtils.i(HttpUrlUtils.getHttpUrl().getPayOnline() + "orderid/" + orderId + "/barcode/" + scanResult + "/cash/" + price + "?access_token=" + SPUtils.get(instance, "access_token", ""));
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
                            LogUtils.i(map.toString());
                            if (map.get("state").toString().equals(Globals.httpSuccessState)) {
//                                btPayCash.setVisibility(View.GONE);
//                                btPayOnline.setText("已经支付");
//                                btPayOnline.setEnabled(false);
                                apId = map.get("id").toString();
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
        LogUtils.i(HttpUrlUtils.getHttpUrl().getPayResult() + "opid/" + apId + "?access_token=" + SPUtils.get(instance, "access_token", ""));
        OkHttpUtils.get()
                .url(HttpUrlUtils.getHttpUrl().getPayResult() + "opid/" + apId + "?access_token=" + SPUtils.get(instance, "access_token", ""))
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
                            LogUtils.i(map.toString());
                            if (map.get("state").toString().equals(Globals.httpSuccessState)) {
//                                btPayCash.setVisibility(View.GONE);
//                                btPayOnline.setVisibility(View.GONE);
//                                btPayOnline.setText("已经支付");
//                                btPayOnline.setEnabled(false);
//                                btYes.setVisibility(View.VISIBLE);
//                                tvPrice.setText("支付成功");
                                myHandler.sendEmptyMessage(101);
                                progressDialog.dismiss();
                                dialogType = 2;
                                showDialog("提示", "支付成功", "确定", "", 0);
                            } else if (map.get("state").toString().equals(Globals.httpTokenFailure)) {
                                ActivityController.finishAll();
                                showToast("登录失效，请重新登录");
                                startActivity(new Intent(instance, MainActivity.class));
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
        LogUtils.i(HttpUrlUtils.getHttpUrl().getPayCash() + "orderid/" + orderId + "/cash/" + price + "?access_token=" + SPUtils.get(instance, "access_token", ""));
        OkHttpUtils.get()
                .url(HttpUrlUtils.getHttpUrl().getPayCash() + "orderid/" + orderId + "/cash/" + price + "?access_token=" + SPUtils.get(instance, "access_token", ""))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        e.printStackTrace();
                        showToast("支付失败，请稍后再试");
                        loadingDialog.dismiss();
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        try {
                            loadingDialog.dismiss();
                            Map<String, Object> map = GsonUtil.JsonToMap(s);
                            LogUtils.i(map.toString());
                            if (map.get("state").toString().equals(Globals.httpSuccessState)) {
                                showToast("支付成功");
                                finish();
                            } else if (map.get("state").toString().equals(Globals.httpTokenFailure)) {
                                ActivityController.finishAll();
                                showToast("登录失效，请重新登录");
                                startActivity(new Intent(instance, LoginActivity.class));
                            } else {
                                showToast(map.get("mess").toString());
                                return;
                            }
                        } catch (Exception e) {
                            showToast("支付失败，请稍后再试");
                            e.printStackTrace();
                        }
                    }
                });
    }
}
