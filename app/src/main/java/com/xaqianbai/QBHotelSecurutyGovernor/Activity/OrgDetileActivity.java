package com.xaqianbai.QBHotelSecurutyGovernor.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.LineData;
import com.xaqianbai.QBHotelSecurutyGovernor.R;
import com.xaqianbai.QBHotelSecurutyGovernor.Utils.ChartUtil;
import com.xaqianbai.QBHotelSecurutyGovernor.Utils.ConditionUtil;
import com.xaqianbai.QBHotelSecurutyGovernor.Utils.DateUtil;
import com.xaqianbai.QBHotelSecurutyGovernor.Utils.GsonUtil;
import com.xaqianbai.QBHotelSecurutyGovernor.Utils.HttpUrlUtils;
import com.xaqianbai.QBHotelSecurutyGovernor.Utils.LogUtils;
import com.xaqianbai.QBHotelSecurutyGovernor.Utils.NullUtil;
import com.xaqianbai.QBHotelSecurutyGovernor.Utils.SPUtils;
import com.xaqianbai.QBHotelSecurutyGovernor.Utils.StatuBarUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.Call;

public class OrgDetileActivity extends AppCompatActivity implements View.OnClickListener{

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
    @BindView(R.id.layout_fault_orgdel)
    LinearLayout layout_fault_orgdel;
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
        layout_fault_orgdel.setOnClickListener(instance);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    public String  getIntentData(){
        Intent intent = getIntent();
        HashMap map = new HashMap();
        String org = intent.getStringExtra("org");
        String start = intent.getStringExtra("start");
        String end = intent.getStringExtra("end");
        LogUtils.e(org+start+end);
        map.put("\"psorgan\"", "\""+org+"\"");//管辖机构
        if (!start.equals("")&&start !=null&&!end.equals("")&&end !=null) {
            map.put("\"inputtime\"", "[[\">=\"," + DateUtil.data(start) + "],[\"<=\"," + DateUtil.data(end) + "]]");//时间
        }
        return "?condition="+ ConditionUtil.getConditionString(map);
    }

    String psorgan = "";

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
                                txt_staff.setText(NullUtil.getString(data.get("fault_hotel_num")));
                                txt_passenger.setText(NullUtil.getString(data.get("check_num")));
                                psorgan = NullUtil.getString(data.get("psorgan"));
                                ArrayList<String> x = new ArrayList<>();
                                ArrayList<Double> y = new ArrayList<>();
                                try
                                {
                                    JSONArray jsonArray = new JSONArray(data.get("result").toString());
                                    for (int j=0; j < jsonArray.length(); j++){
                                        JSONObject jsonObject = jsonArray.getJSONObject(j);
                                        x.add(jsonObject.getInt("date")+"");
                                        y.add(jsonObject.getDouble("live_num"));
                                    }
                                    LineData mLineData = ChartUtil.makeLineData(instance,7, y, x);
                                    ChartUtil.setChartStyle(chart_line, mLineData, Color.WHITE);
                                }
                                catch (Exception e)
                                {
                                    e.printStackTrace();
                                }

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


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.layout_fault_orgdel:
                Intent i = new Intent(instance, HotelFaultListActivity.class);
                i.putExtra("psorgan", psorgan);
                startActivity(i);
                break;
        }
    }
}
