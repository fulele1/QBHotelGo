package com.xaqb.unlock.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.xaqb.unlock.R;
import com.xaqb.unlock.Utils.Globals;
import com.xaqb.unlock.Utils.GsonUtil;
import com.xaqb.unlock.Utils.HttpUrlUtils;
import com.xaqb.unlock.Utils.LogUtils;
import com.xaqb.unlock.Utils.SPUtils;
import com.xaqb.unlock.Utils.ToolsUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.BitmapCallback;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.Map;

import okhttp3.Call;


/**
 * Created by chengeng on 2017/04/06.
 * 订单详情activity
 */
public class OrderDetailActivity extends BaseActivity {
    private OrderDetailActivity instance;
    private TextView tvOrderId, tvOrderName, tvOrderPhone, tvOrderAddress, tvOrderLockType, tvOrderPay, tvOrderTime;
    private ImageView ivFace, ivLock;
    private Button btPayOnline, btPayCash;
    private String payStatus;

    @Override
    public void initTitleBar() {
        setTitle("订单详情");
        showBackwardView(true);
    }

    @Override
    public void initViews() {
        setContentView(R.layout.order_detail_activity);
        instance = this;
        assignViews();
    }

    private void assignViews() {
        tvOrderId = (TextView) findViewById(R.id.tv_order_id);
        tvOrderName = (TextView) findViewById(R.id.tv_order_name);
        tvOrderPhone = (TextView) findViewById(R.id.tv_order_phone);
        tvOrderAddress = (TextView) findViewById(R.id.tv_order_address);
        tvOrderLockType = (TextView) findViewById(R.id.tv_lock_type);
        tvOrderPay = (TextView) findViewById(R.id.tv_order_pay);
        tvOrderTime = (TextView) findViewById(R.id.tv_order_time);
        ivFace = (ImageView) findViewById(R.id.iv_user_face);
        ivLock = (ImageView) findViewById(R.id.iv_lock_pic);
        btPayOnline = (Button) findViewById(R.id.bt_pay_online);
        btPayCash = (Button) findViewById(R.id.bt_pay_money);
    }

    @Override
    public void initData() {
        Intent intent = getIntent();
        String orderId = intent.getStringExtra("or_id");
        getOrderDetail(orderId);
    }

    private void getOrderDetail(String orderId) {
        if (!checkNetwork()) return;
        LogUtils.i(HttpUrlUtils.getHttpUrl().getOrderDetail() + "/" + orderId + "?access_token=" + SPUtils.get(instance, "access_token", ""));
        OkHttpUtils.get()
                .url(HttpUrlUtils.getHttpUrl().getOrderDetail() + "/" + orderId + "?access_token=" + SPUtils.get(instance, "access_token", ""))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        try {
//                            LogUtils.i(s);
                            Map<String, Object> map = GsonUtil.JsonToMap(s);
                            LogUtils.i(map.toString());
                            if (map.get("state").toString().equals(Globals.httpSuccessState)) {
//                                List<Map<String, Object>> data = GsonUtil.GsonToListMaps(GsonUtil.GsonString(map.get("table")));
                                tvOrderId.setText(map.get("or_orderno").toString());
                                tvOrderName.setText(map.get("or_username").toString());
                                tvOrderPhone.setText(map.get("or_usertel").toString());
                                tvOrderAddress.setText(map.get("or_useraddress").toString());
                                tvOrderPay.setText(map.get("or_price").toString());
                                tvOrderLockType.setText(map.get("or_locktype").toString());
                                tvOrderTime.setText(ToolsUtils.getStrTime(map.get("or_createtime").toString()));
                                payStatus = map.get("or_paystatus").toString();
                                if (payStatus.equals("00") || payStatus.equals("02")) {
//                                    btPayCash.setVisibility(View.VISIBLE);
                                } else if (payStatus.equals("01")) {
                                    btPayCash.setVisibility(View.GONE);
                                    btPayOnline.setText("已经支付");
                                    btPayOnline.setEnabled(false);
                                }
                                String imageUrl = map.get("or_faceimg").toString();
                                if (textNotEmpty(imageUrl)) {
                                    loadImg(ivFace, imageUrl);
                                }
                                imageUrl = map.get("or_lockimg").toString();
                                if (textNotEmpty(imageUrl)) {
                                    loadImg(ivLock, imageUrl);
                                }
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

    private void loadImg(final ImageView iv, String url) {
        if (url != null && !url.equals(""))
            OkHttpUtils
                    .get()
                    .url(url)
                    .build()
                    .execute(new BitmapCallback() {
                        @Override
                        public void onError(Call call, Exception e, int i) {
                            e.printStackTrace();
                        }

                        @Override
                        public void onResponse(Bitmap bitmap, int i) {
                            try {
                                iv.setImageBitmap(bitmap);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
    }

    @Override
    public void addListener() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
    }
}
