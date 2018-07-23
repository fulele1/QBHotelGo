package com.xaqb.hotel.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.model.Text;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.LineData;
import com.squareup.picasso.Picasso;
import com.xaqb.hotel.Entity.Passenger;
import com.xaqb.hotel.R;
import com.xaqb.hotel.Utils.ChartUtil;
import com.xaqb.hotel.Utils.GsonUtil;
import com.xaqb.hotel.Utils.HttpUrlUtils;
import com.xaqb.hotel.Utils.LogUtils;
import com.xaqb.hotel.Utils.NullUtil;
import com.xaqb.hotel.Utils.SPUtils;
import com.xaqb.hotel.Utils.StatuBarUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.Call;

public class OrgDetileActivity extends AppCompatActivity {

    OrgDetileActivity instance;
    Unbinder unbinder;
    @BindView(R.id.tv_title)
    TextView title;
    @BindView(R.id.layout_titlebar)
    FrameLayout titlebar;
    @BindView(R.id.txt_org_del_org)
    TextView txt_org;
    @BindView(R.id.txt_hotel_del_org)
    TextView txt_hotel;
    @BindView(R.id.txt_staff_del_org)
    TextView txt_staff;
    @BindView(R.id.txt_passenger_del_org)
    TextView txt_passenger;
    @BindView(R.id.lineChart_del_org)
    LineChart chart_line;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_org_detile);
        instance = this;
        unbinder = ButterKnife.bind(instance);
        StatuBarUtil.setStatuBarLightMode(instance,getResources().getColor(R.color.bag));//修改状态栏字体颜色为黑色
         titlebar.setBackgroundColor(getResources().getColor(R.color.bag));
        title.setText("辖区统计查询");
        connecting();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    public String  getIntentData(){
        Intent intent = getIntent();
        String org = intent.getStringExtra("org");
        String start = intent.getStringExtra("start");
        String end = intent.getStringExtra("end");
        LogUtils.e(org+start+end);
        return "?code="+org+"&starttime="+start+"&endtime="+end;
    }


    private void connecting() {

        LogUtils.e(HttpUrlUtils.getHttpUrl().orgDel()+getIntentData()+"&access_token="+ SPUtils.get(instance,"access_token",""));
        OkHttpUtils
                .get()
                .url(HttpUrlUtils.getHttpUrl().orgDel()+getIntentData()+"&access_token="+ SPUtils.get(instance,"access_token",""))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {

                    }

                    @Override
                    public void onResponse(String s, int i) {
                        try {
                            Map<String, Object> data = GsonUtil.JsonToMap(s);
                            if (data.get("state").toString().equals("1")) {
                                Toast.makeText(instance,data.get("mess").toString(),Toast.LENGTH_LONG).show();
                                return;
                            } else if (data.get("state").toString().equals("0")) {
                                txt_org.setText(NullUtil.getString(data.get("so_name")));
                                txt_hotel.setText(NullUtil.getString(data.get("hotel_num")));
                                txt_staff.setText(NullUtil.getString(data.get("staff_num")));
                                txt_passenger.setText(NullUtil.getString(data.get("check_num")));


                                List<Map<String, Object>> table = GsonUtil.GsonToListMaps(data.get("result").toString());
                                ArrayList<String> x = new ArrayList<String>();
                                ArrayList<Double> y = new ArrayList<Double>();
                                for (int j = 0; j <table.size(); j++) {
                                    // x轴显示的数据
                                    x.add(table.get(j).get("date").toString());
                                    y.add(Double.parseDouble(table.get(j).get("live_num").toString()));
                                }

                                LineData mLineData = ChartUtil.makeLineData(instance,7, y, x);
                                ChartUtil.setChartStyle(chart_line, mLineData, Color.WHITE);

                            } else if (data.get("state").toString().equals("0")) {
                                //响应失败
                                Toast.makeText(instance, data.get("mess").toString(), Toast.LENGTH_SHORT).show();
                            }else if (data.get("state").toString().equals("10")) {
                                //响应失败
                                Toast.makeText(instance, data.get("mess").toString(), Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(instance,LoginActivity.class));
                                finish();
                            }
                        }catch (Exception e){
                            Toast.makeText(instance,e.toString(),Toast.LENGTH_SHORT).show();
                        }


                    }
                });

    }


    public void onBackward(View view){
        this.finish();
    }


}
