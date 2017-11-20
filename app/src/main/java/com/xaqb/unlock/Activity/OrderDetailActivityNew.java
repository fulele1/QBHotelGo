package com.xaqb.unlock.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.xaqb.unlock.Adapter.PayRecordAdapter;
import com.xaqb.unlock.Entity.IncomeInfo;
import com.xaqb.unlock.R;
import com.xaqb.unlock.Utils.ActivityController;
import com.xaqb.unlock.Utils.Globals;
import com.xaqb.unlock.Utils.GsonUtil;
import com.xaqb.unlock.Utils.HttpUrlUtils;
import com.xaqb.unlock.Utils.LogUtils;
import com.xaqb.unlock.Utils.QBCallback;
import com.xaqb.unlock.Utils.QBHttp;
import com.xaqb.unlock.Utils.SPUtils;
import com.xaqb.unlock.Utils.ToolsUtils;
import com.xaqb.unlock.Views.ListViewForScroollView;
import com.xaqb.unlock.zxing.activity.CaptureActivity;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.BitmapCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.Call;


/**
 * Created by chengeng on 2017/04/06.
 * 订单详情activity
 */
public class OrderDetailActivityNew extends BaseActivity {
    private OrderDetailActivityNew instance;
    private TextView tvOrderId, tvOrderName, tvOrderPhone, tvOrderAddress, tvOrderLockType, tvOrderPay, tvOrderTime;
    private ImageView ivFace, ivLock;
    //    private Button btPayOnline, btPayCash,
    private Button btPayStatus;
    private String payStatus, orderId, scanResult, price, payPrice;
    private int dialogType;
    private ProgressDialog progressDialog;
    //是否支付成功
    private boolean isPaySuc = false, isFirstGetPayStatus = false, isQuery = false;
    private int payNetWordTimes = 0;
    private Thread payStatusThread;
    private ListViewForScroollView lvPayRecord;
    private ArrayList<IncomeInfo> incomeInfos;
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
        setTitle("订单详情");
        showBackwardView(true);
    }

    @Override
    public void initViews() {
        setContentView(R.layout.order_detail_activity_new);
        instance = this;
        assignViews();
    }

    private void assignViews() {

        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setTitle("支付状态");
        tvOrderId = (TextView) findViewById(R.id.tv_order_id);
        tvOrderName = (TextView) findViewById(R.id.tv_order_name);
        tvOrderPhone = (TextView) findViewById(R.id.tv_order_phone);
        tvOrderAddress = (TextView) findViewById(R.id.tv_order_address);
        tvOrderLockType = (TextView) findViewById(R.id.tv_lock_type);
        tvOrderPay = (TextView) findViewById(R.id.tv_order_pay);
        tvOrderTime = (TextView) findViewById(R.id.tv_order_time);
        ivFace = (ImageView) findViewById(R.id.iv_user_face);
        ivLock = (ImageView) findViewById(R.id.iv_lock_pic);
        btPayStatus = (Button) findViewById(R.id.bt_pay_status);
        lvPayRecord = (ListViewForScroollView) findViewById(R.id.lv_pay_record);

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
    public void initData() {
        Intent intent = getIntent();
        orderId = intent.getStringExtra("or_id");
        getOrderDetail(orderId);
    }


    private void getOrderDetail(String orderId) {
        if (!checkNetwork()) {
            showToast(getResources().getString(R.string.network_not_alive));
            return;
        }
        loadingDialog.show("加载中...");
        LogUtils.i(HttpUrlUtils.getHttpUrl().getOrderDetail() + "/" + orderId + "?access_token=" + SPUtils.get(instance, "access_token", ""));
        QBHttp.get(instance,
                HttpUrlUtils.getHttpUrl().getOrderDetail() + "/" + orderId + "?access_token=" + SPUtils.get(instance, "access_token", ""),
                null,
                new QBCallback() {
                    @Override
                    public void doWork(Map<?, ?> map) {
                        try {
                            loadingDialog.dismiss();
                            LogUtils.i(map.toString());
                            if (map.get("state").toString().equals(Globals.httpSuccessState)) {
                                List<Map<String, Object>> paydetail = GsonUtil.GsonToListMaps(GsonUtil.GsonString(map.get("paydetail")));
                                tvOrderId.setText(map.get("or_orderno").toString());
                                tvOrderName.setText(map.get("or_username").toString());
                                tvOrderPhone.setText(map.get("or_usertel").toString());
                                tvOrderAddress.setText(map.get("or_useraddress").toString());
                                price = map.get("or_price").toString();
                                payPrice = map.get("or_cash").toString();
                                tvOrderPay.setText(price);
                                tvOrderLockType.setText(ToolsUtils.getLockType(map.get("or_locktype").toString()));
                                tvOrderTime.setText(ToolsUtils.getStrTime(map.get("or_createtime").toString()));
                                payStatus = map.get("or_paystatus").toString();


                                //支付记录listview
                                incomeInfos = new ArrayList<>();
                                IncomeInfo info;
                                for (int j = 0; j < paydetail.size(); j++) {
                                    info = new IncomeInfo();
                                    info.setId(paydetail.get(j).get("op_id").toString());
                                    info.setOrderTime(paydetail.get(j).get("op_createtime").toString());
                                    info.setOrderPrice(paydetail.get(j).get("op_price").toString());
                                    info.setPayStatus(paydetail.get(j).get("op_paystatus").toString());
                                    info.setOrderId(paydetail.get(j).get("op_pid").toString());
                                    info.setPayType(paydetail.get(j).get("op_paytype").toString());
                                    info.setSerialNumber(paydetail.get(j).get("op_serialnum").toString());
                                    incomeInfos.add(info);
                                }
                                if (incomeInfos.size() != 0) {
                                    PayRecordAdapter adapter = new PayRecordAdapter(instance, incomeInfos);
                                    lvPayRecord.setAdapter(adapter);
                                }
                                LogUtils.i("incomeinfo == ", incomeInfos.toString());

                                if (payStatus.equals("00")) {
                                    payStatus = "00";
                                    btPayStatus.setText("未支付");
                                } else if (payStatus.equals("01")) {
                                    payStatus = "01";
                                    btPayStatus.setText("已经支付");
                                } else if (payStatus.equals("03")) {
                                    payStatus = "03";
                                    btPayStatus.setText("未付清");
                                }

                                String imageUrl = HttpUrlUtils.getHttpUrl().getBaseUrl()+map.get("or_faceimg").toString();
                                if (textNotEmpty(imageUrl)) {
                                    loadImg(ivFace, imageUrl);
                                }
                                imageUrl = HttpUrlUtils.getHttpUrl().getBaseUrl()+map.get("or_lockimg").toString();
                                if (textNotEmpty(imageUrl)) {
                                    loadImg(ivLock, imageUrl);
                                }
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

                    @Override
                    public void doError(Exception e) {
                        loadingDialog.dismiss();

                    }

                    @Override
                    public void reDoWork() {

                    }
                }

        );
    }

    private void loadImg(final ImageView iv, String url) {
        if (url != null && !url.equals("")){
            Picasso.with(instance)
                    .load(url)
                    .placeholder(R.mipmap.nothing_pic)
                    .error(R.mipmap.failed_pic)
                    .into(iv);
        }
    }

    @Override
    public void addListener() {
        btPayStatus.setOnClickListener(instance);
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
            case R.id.bt_pay_status:
                Intent intent = new Intent(instance, PayActivityNew.class);
                intent.putExtra("or_id", orderId);
                intent.putExtra("total_price", price);
                intent.putExtra("pay_price", payPrice);
                switch (payStatus) {
                    case "00":
                        startActivity(intent);
                        break;
                    case "01":
                        showToast("已经支付成功");
                        btPayStatus.setClickable(false);
                        break;
                    case "03":
                        startActivity(intent);
                        break;
                }
                break;
        }
    }

    @Override
    protected void dialogOk() {
        switch (dialogType) {
            case 0:
                payCash();
                break;
            case 1:
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

    /**
     * 在线支付
     */
    private void payOnline() {
        if (!checkNetwork()) {
            showToast(getResources().getString(R.string.network_not_alive));
            return;
        }
        LogUtils.i(HttpUrlUtils.getHttpUrl().getPayOnline() + "orderid/" + orderId + "/barcode/" + scanResult + "?access_token=" + SPUtils.get(instance, "access_token", ""));
        QBHttp.get(instance,
                HttpUrlUtils.getHttpUrl().getPayOnline() + "orderid/" + orderId + "/barcode/" + scanResult + "?access_token=" + SPUtils.get(instance, "access_token", ""),
                null,
                new QBCallback() {
                    @Override
                    public void doWork(Map<?, ?> map) {
                        try {
                            LogUtils.i(map.toString());
                            if (map.get("state").toString().equals(Globals.httpSuccessState)) {
//                                btPayCash.setVisibility(View.GONE);
//                                btPayOnline.setText("已经支付");
//                                btPayOnline.setEnabled(false);
                                isFirstGetPayStatus = true;
                                isQuery = false;
                                getPayResult();
                            } else if (map.get("state").toString().equals(Globals.httpTokenFailure)) {
                                progressDialog.dismiss();
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

                    @Override
                    public void doError(Exception e) {

                    }

                    @Override
                    public void reDoWork() {

                    }
                }

        );


    }

    /**
     * 获取支付结果
     */
    private void getPayResult() {
        if (!checkNetwork()) {
            showToast(getResources().getString(R.string.network_not_alive));
            return;
        }
        LogUtils.i(HttpUrlUtils.getHttpUrl().getPayResult() + "opid/" + orderId + "?access_token=" + SPUtils.get(instance, "access_token", ""));
        QBHttp.get(instance,
                HttpUrlUtils.getHttpUrl().getPayResult() + "opid/" + orderId + "?access_token=" + SPUtils.get(instance, "access_token", ""),
                null,
                new QBCallback() {
                    @Override
                    public void doWork(Map<?, ?> map) {
                        try {
                            LogUtils.i(map.toString());
                            if (map.get("state").toString().equals(Globals.httpSuccessState)) {
                                myHandler.sendEmptyMessage(101);
                                progressDialog.dismiss();
                                dialogType = 1;
                                showDialog("提示", "支付成功", "确定", "", 0);
                            } else if (map.get("state").toString().equals(Globals.httpTokenFailure)) {
                                progressDialog.dismiss();
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

                    @Override
                    public void doError(Exception e) {
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
                    public void reDoWork() {
                    }
                }
        );
    }

    /**
     * 现金支付
     */
    private void payCash() {
        if (!checkNetwork()) {
            showToast(getResources().getString(R.string.network_not_alive));
            return;
        }
        LogUtils.i(HttpUrlUtils.getHttpUrl().getPayCash() + "orderid/" + orderId + "?access_token=" + SPUtils.get(instance, "access_token", ""));
        QBHttp.get(instance,
                HttpUrlUtils.getHttpUrl().getPayCash() + "orderid/" + orderId + "?access_token=" + SPUtils.get(instance, "access_token", ""),
                null,
                new QBCallback() {
                    @Override
                    public void doWork(Map<?, ?> map) {
                        try {
                            LogUtils.i(map.toString());
                            if (map.get("state").toString().equals(Globals.httpSuccessState)) {
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

                    @Override
                    public void doError(Exception e) {

                    }

                    @Override
                    public void reDoWork() {

                    }
                }


        );
    }
}
