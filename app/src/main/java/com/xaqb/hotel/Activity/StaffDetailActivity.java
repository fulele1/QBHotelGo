package com.xaqb.hotel.Activity;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;
import com.xaqb.hotel.R;
import com.xaqb.hotel.Utils.GlideRoundTransform;
import com.xaqb.hotel.Utils.GsonUtil;
import com.xaqb.hotel.Utils.HttpUrlUtils;
import com.xaqb.hotel.Utils.LogUtils;
import com.xaqb.hotel.Utils.NullUtil;
import com.xaqb.hotel.Utils.SPUtils;
import com.xaqb.hotel.Utils.StatuBarUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.Call;

public class StaffDetailActivity extends AppCompatActivity {

    @BindView(R.id.tv_title)
    TextView tv_title;
    @BindView(R.id.layout_titlebar)
    FrameLayout titlebar;
    @BindView(R.id.img_pic_staff_del)
    ImageView img_pic;
    @BindView(R.id.txt_name_staff_del)
    TextView txt_name;
    @BindView(R.id.txt_detail_staff_tal)
    TextView txt_detail;
    @BindView(R.id.txt_age_staff_del)
    TextView txt_age;
    @BindView(R.id.txt_tall_staff_tal)
    TextView txt_tall;
    @BindView(R.id.txt_blood_staff_tel)
    TextView txt_blood;
    @BindView(R.id.txt_tel_staff_tel)
    TextView txt_tel;
    @BindView(R.id.txt_country_staff_tel)
    TextView txt_country;
    @BindView(R.id.txt_marry_staff_tel)
    TextView txt_marry;
    @BindView(R.id.txt_dang_staff_tel)
    TextView txt_dang;
    @BindView(R.id.txt_conncet_staff_tel)
    TextView txt_conncet;
    @BindView(R.id.txt_tel_connecte_tel_tel)
    TextView txt_tel_connecte;
    @BindView(R.id.txt_home_staff_tel)
    TextView txt_home;
    @BindView(R.id.txt_now_home_staff_tel)
    TextView txt_now_home;
    @BindView(R.id.txt_work_place_staff_tel)
    TextView txt_work_place;
    private StaffDetailActivity instance;
    private Unbinder unbinder;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_detail);
        instance = this;
        unbinder = ButterKnife.bind(instance);
        StatuBarUtil.setStatuBarLightMode(instance,getResources().getColor(R.color.bag));//修改状态栏字体颜色为黑色
        titlebar.setBackgroundColor(getResources().getColor(R.color.bag));
        tv_title.setText("从业人员详细信息");
        getIntentData();
        connecting();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    public void onBackward (View view){
        instance.finish();
    }


    private String id,pic;
    public String getIntentData() {
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        pic = intent.getStringExtra("pic");
        return id;
    }

    private void connecting() {

        LogUtils.e(HttpUrlUtils.getHttpUrl().getStaffDetail()+getIntentData()+"&access_token="+ SPUtils.get(instance,"access_token",""));
        OkHttpUtils
                .get()
                .url(HttpUrlUtils.getHttpUrl().getStaffDetail()+getIntentData()+"&access_token="+ SPUtils.get(instance,"access_token",""))
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
                                txt_name.setText(NullUtil.getString(data.get("name")));
                                String sex = NullUtil.getString(data.get("sex"));
                                if (sex.equals("1")){
                                    sex = "男";
                                }else {
                                    sex = "女";
                                }
                                String hname = NullUtil.getString(data.get("hname"));
                                String quarters = NullUtil.getString(data.get("quarters"));
                                txt_detail.setText(sex+"|"+hname+"|"+quarters);
                                txt_age.setText(NullUtil.getString(data.get("age")));
                                txt_tall.setText(NullUtil.getString(data.get("height")));
                                txt_blood.setText(NullUtil.getString(data.get("blood")));
                                txt_tel.setText(NullUtil.getString(data.get("telphone")));
                                txt_country.setText(NullUtil.getString(data.get("na_name")));
                                String marry = NullUtil.getString(data.get("marry"));
                                if (marry.equals("0")){
                                    marry = "未婚";
                                }else {
                                    marry = "已婚";
                                }
                                txt_marry.setText(marry);
                                txt_dang.setText(NullUtil.getString(data.get("political")));
                                txt_conncet.setText(NullUtil.getString(data.get("people")));
                                txt_tel_connecte.setText(NullUtil.getString(data.get("peoplephone")));
                                txt_home.setText(NullUtil.getString(data.get("houseadd")));
                                txt_now_home.setText(NullUtil.getString(data.get("address")));
                                 txt_work_place.setText(NullUtil.getString(data.get("political")));
                                if(!pic.equals("")&&pic !=null){
                                    Glide.with(instance)
                                            .load(NullUtil.getString(pic))
                                            .transform(new GlideRoundTransform(instance,10))
                                            .placeholder(R.mipmap.per)
                                            .error(R.mipmap.ic_launcher)
                                            .into(img_pic);
                                }
                            }else if (data.get("state").toString().equals("19")){

                                Toast.makeText(instance,"未查询到有效数据",Toast.LENGTH_LONG).show();
                            }else if (data.get("state").toString().equals("10")) {
                                //响应失败
                                Toast.makeText(instance, data.get("mess").toString(), Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(instance,LoginActivity.class));
                                finish();
                            }

                            else {
                                //响应失败
                                Toast.makeText(instance,data.get("mess").toString(),Toast.LENGTH_LONG).show();

                            }


                        } catch (Exception e) {
                            Toast.makeText(instance,e.toString(),Toast.LENGTH_LONG).show();
                        }



                    }
                });

    }

}
