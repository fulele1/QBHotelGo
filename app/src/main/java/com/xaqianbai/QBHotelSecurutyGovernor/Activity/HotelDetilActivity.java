package com.xaqianbai.QBHotelSecurutyGovernor.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.LineData;
import com.jaeger.library.StatusBarUtil;
import com.xaqianbai.QBHotelSecurutyGovernor.R;
import com.xaqianbai.QBHotelSecurutyGovernor.Utils.ChartUtil;
import com.xaqianbai.QBHotelSecurutyGovernor.Utils.GsonUtil;
import com.xaqianbai.QBHotelSecurutyGovernor.Utils.HttpUrlUtils;
import com.xaqianbai.QBHotelSecurutyGovernor.Utils.LogUtils;
import com.xaqianbai.QBHotelSecurutyGovernor.Utils.NullUtil;
import com.xaqianbai.QBHotelSecurutyGovernor.Utils.SPUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.Call;

public class HotelDetilActivity extends AppCompatActivity {

    HotelDetilActivity instance;
    Unbinder unbinder;

    @BindView(R.id.img_pic_del_hotel)
    ImageView img_pic;
    @BindView(R.id.txt_name_del_hotel)
    TextView txt_name;
    @BindView(R.id.txt_del_del_hotel)
    TextView txt_del;
    @BindView(R.id.txt_tel_del_hotel)
    TextView txt_tel;
    @BindView(R.id.chart_line_del_hotel)
    LineChart chart_line;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setTranslucent(this, 0);
        setContentView(R.layout.activity_hotel_detil);
        instance = this;
        unbinder = ButterKnife.bind(instance);
        connecting();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    public void onBack(View view) {
        finish();
    }

    public String getIntentData() {
        Intent intent = getIntent();
        String id = intent.getStringExtra("id");
        return id;
    }


    private void connecting() {
        LogUtils.e(HttpUrlUtils.getHttpUrl().HotelDel() + "/" + getIntentData() + "?access_token=" + SPUtils.get(instance, "access_token", ""));

        OkHttpUtils
                .get()
                .url(HttpUrlUtils.getHttpUrl().HotelDel() + "/" + getIntentData() + "?access_token=" + SPUtils.get(instance, "access_token", ""))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                    }

                    @Override
                    public void onResponse(String s, int i) {

                        try {
                            Map<String, Object> data = GsonUtil.JsonToMap(s);
                            LogUtils.e(data.toString());
                            String pk = NullUtil.getString(data.get("pk"));
                            String ho_id = NullUtil.getString(data.get(pk));
                            String img = NullUtil.getString(data.get("img"));
                            String pic = HttpUrlUtils.getHttpUrl().picInHotel() + ho_id
                                    + "/" + img
                                    + "?access_token=" + SPUtils.get(instance, "access_token", "");
                            LogUtils.e("图片" + pic);
                            if (data.get("state").toString().equals("1")) {
                                Toast.makeText(instance, data.get("mess").toString(), Toast.LENGTH_LONG).show();
                                return;
                            } else if (data.get("state").toString().equals("0")) {
                                txt_name.setText(NullUtil.getString(data.get("hname")));
                                txt_del.setText("从业人员：" + NullUtil.getString(data.get("staff_num")) +
                                        "|入住人数：" + NullUtil.getString(data.get("total_num")) +
                                        "|负责人：" + NullUtil.getString(data.get("principal")));
                                txt_tel.setText("酒店电话：" + NullUtil.getString(data.get("telphone")));
                                if (!pic.equals("") && pic != null) {
                                    Glide.with(instance)
                                            .load(pic)
                                            .fitCenter()
                                            .placeholder(R.mipmap.hotel_gag).error(R.mipmap.hotel_gag)
                                            .into(img_pic);
                                }

                                ArrayList<String> x = new ArrayList<String>();
                                ArrayList<Double> y = new ArrayList<Double>();
                                try {
                                    JSONArray jsonArray = new JSONArray(data.get("result").toString());
                                    LogUtils.e(jsonArray + "");


                                    for (int j = 0; j < jsonArray.length(); j++) {
                                        JSONObject jsonObject = jsonArray.getJSONObject(j);
                                        LogUtils.e(jsonObject + "");
                                        LogUtils.e(jsonObject.getInt("date") + "");

                                        x.add(jsonObject.getInt("date") + "");
                                        y.add(jsonObject.getDouble("live_num"));
                                    }

                                    LineData mLineData = ChartUtil.makeLineData(instance, 7, y, x);
                                    ChartUtil.setChartStyle(chart_line, mLineData, Color.WHITE);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            } else if (data.get("state").toString().equals("10")) {
                                //响应失败
                                Toast.makeText(instance, data.get("mess").toString(), Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(instance, LoginActivity.class));
                                finish();
                            } else {
                                //响应失败
                                Toast.makeText(instance, data.get("mess").toString(), Toast.LENGTH_LONG).show();

                            }


                        } catch (Exception e) {
                            Toast.makeText(instance, e.toString(), Toast.LENGTH_LONG).show();
                        }


                    }
                });

    }

    public void onBackward(View view) {
        this.finish();
    }


}
