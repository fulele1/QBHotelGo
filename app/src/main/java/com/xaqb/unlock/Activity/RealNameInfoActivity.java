package com.xaqb.unlock.Activity;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.xaqb.unlock.R;
import com.xaqb.unlock.Utils.ActivityController;
import com.xaqb.unlock.Utils.Globals;
import com.xaqb.unlock.Utils.GsonUtil;
import com.xaqb.unlock.Utils.HttpUrlUtils;
import com.xaqb.unlock.Utils.SPUtils;
import com.xaqb.unlock.Utils.StatuBarUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.Map;

import okhttp3.Call;


/**
 * Created by chengeng on 2016/12/2.
 * 空activity，用于复制粘贴
 */
public class RealNameInfoActivity extends BaseActivityNew {
    private RealNameInfoActivity instance;
    private TextView tvCertName, tvCertType, tvCertNum, tvSex, tvNation,tvTitle;
    private ImageView ivCert, ivFace,ivSign;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void initViews() {
        setContentView(R.layout.real_name_info_activity);
        instance = this;
        StatuBarUtil.setStatusBarColor(this,getResources().getColor(R.color.main));

        assignViews();
    }

    private void assignViews() {
        tvCertName = (TextView) findViewById(R.id.tv_cert_name);
        tvCertType = (TextView) findViewById(R.id.tv_cert_type);
        tvCertNum = (TextView) findViewById(R.id.tv_cert_num);
        ivCert = (ImageView) findViewById(R.id.iv_cert_info);
        ivFace = (ImageView) findViewById(R.id.iv_face_info);
        ivSign = (ImageView) findViewById(R.id.iv_ra_signing);
        tvSex = (TextView) findViewById(R.id.tv_sex_info);
        tvNation = (TextView) findViewById(R.id.tv_nation_info);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvTitle.setText("实名认证信息");
    }

    @Override
    public void initData() {
        if (!checkNetwork()) {
            showToast(getResources().getString(R.string.network_not_alive));
            return;
        }
        loadingDialog.show("加载中...");
        OkHttpUtils.get()
                .url(HttpUrlUtils.getHttpUrl().getRealNameInfo() + SPUtils.get(instance, "userid", "") + "?access_token=" + SPUtils.get(instance, "access_token", ""))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        e.printStackTrace();
                        showToast("网络访问异常");
                        loadingDialog.dismiss();
                    }

                    @Override
                    public void onResponse(String s, int i) {

                        /*{"state":0,"mess":"","table":{"ra_id":5,"ra_name":"马*","ra_certcode":"6****************5",
                                "ra_certimg":"http:\/\/kaisuo.qbchoice.cn\/v1\/staff\/privite\/106\/5\/ra_certimg",
                                "ra_faceimg":"http:\/\/kaisuo.qbchoice.cn\/v1\/staff\/privite\/106\/5\/ra_faceimg",
                                "ra_state":1,"ra_age":18,"ra_sex":"女","ra_nation":"01","ct_name":"身份证",
                                "ra_signimg":"http:\/\/kaisuo.qbchoice.cn\/v1\/staff\/privite\/106\/5\/ra_signimg"}}*/

                        loadingDialog.dismiss();
                        try {
                            Map<String, Object> map = GsonUtil.JsonToMap(s);
                            if (map.get("state").toString().equals(Globals.httpSuccessState)) {
                                tvCertName.setText(map.get("ra_name").toString());
                                tvCertType.setText(map.get("ct_name").toString());
                                tvCertNum.setText(map.get("ra_certcode").toString());
                                tvSex.setText(map.get("ra_sex").toString());
                                tvNation.setText(nationUtil(map.get("ra_nation").toString()) );
                                setPic(map.get("ra_certimg").toString() + "?access_token=" + SPUtils.get(instance, "access_token", ""),ivCert);
                                setPic(map.get("ra_faceimg").toString() + "?access_token=" + SPUtils.get(instance, "access_token", ""),ivFace);
                                setPic(map.get("ra_signimg").toString() + "?access_token=" + SPUtils.get(instance, "access_token", ""),ivSign);

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


    public String nationUtil(String a){
        switch (a){
            case "01":
                return "汉族";
        }
        return "汉族";
    }

    @Override
    public void addListener() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
    }


    /**
     * 设置图片
     * @param s
     * @param view
     */
    public void setPic(String s,ImageView view){
        Picasso.with(instance)
                .load(s)
                .placeholder(R.mipmap.nothing_pic)
                .error(R.mipmap.failed_pic)
                .into(view);
    }

}
