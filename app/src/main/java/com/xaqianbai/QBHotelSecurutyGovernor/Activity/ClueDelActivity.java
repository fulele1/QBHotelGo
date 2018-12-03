package com.xaqianbai.QBHotelSecurutyGovernor.Activity;

import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.xaqianbai.QBHotelSecurutyGovernor.R;
import com.xaqianbai.QBHotelSecurutyGovernor.Utils.Base64Utils;
import com.xaqianbai.QBHotelSecurutyGovernor.Utils.GsonUtil;
import com.xaqianbai.QBHotelSecurutyGovernor.Utils.HttpUrlUtils;
import com.xaqianbai.QBHotelSecurutyGovernor.Utils.LogUtils;
import com.xaqianbai.QBHotelSecurutyGovernor.Utils.NullUtil;
import com.xaqianbai.QBHotelSecurutyGovernor.Utils.SPUtils;
import com.xaqianbai.QBHotelSecurutyGovernor.Utils.StatuBarUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.Call;

import static com.xaqianbai.QBHotelSecurutyGovernor.R.id.img_per1_del_clue;
import static com.xaqianbai.QBHotelSecurutyGovernor.R.id.img_per2_del_clue;
import static com.xaqianbai.QBHotelSecurutyGovernor.R.id.img_per3_del_clue;


public class ClueDelActivity extends BaseActivityNew {

    private ClueDelActivity instance;
    Unbinder unbinder;
    @BindView(R.id.tv_title)
    TextView title;
    @BindView(R.id.edit_name_clue_del)
    EditText edit_name;
    @BindView(R.id.edit_tel_clue_del)
    EditText edit_tel;
    @BindView(R.id.edit_provence_clue_del)
    EditText edit_provence;
    @BindView(R.id.edit_street_clue_del)
    EditText edit_street;
    @BindView(R.id.edit_del_clue_del)
    EditText edit_del;
    @BindView(img_per1_del_clue)
    ImageView img_per1;
    @BindView(img_per2_del_clue)
    ImageView img_per2;
    @BindView(img_per3_del_clue)
    ImageView img_per3;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void initViews() throws Exception {
        setContentView(R.layout.activity_clue_del);
        instance = this;
        StatuBarUtil.setStatuBarLightMode(instance,getResources().getColor(R.color.white));//修改状态栏字体颜色为黑色
        unbinder = ButterKnife.bind(instance);
        title.setText("线索详情");
    }
private String id;
    @Override
    public void initData() throws Exception {
        id = getIntent().getStringExtra("id");
        connecting();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getPic();
            }
        }, 500);

    }

    private void getPic() {
        LogUtils.e(HttpUrlUtils.getHttpUrl().picInclueDel()+id+"/"+img+"?access_token="+ SPUtils.get(instance,"access_token",""));

        OkHttpUtils
                .get()
                .url(HttpUrlUtils.getHttpUrl().picInclueDel()+id+"/"+img+"?access_token="+ SPUtils.get(instance,"access_token",""))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {

                    }

                    @Override
                    public void onResponse(String s, int i) {
                        LogUtils.e(s);
                        try {
                            Map<String, Object> data = GsonUtil.JsonToMap(s);
                            if (data.get("state").toString().equals("1")) {
                                Toast.makeText(instance,data.get("mess").toString(),Toast.LENGTH_LONG).show();
                                return;
                            } else if (data.get("state").toString().equals("0")) {
                                String test=NullUtil.getString(data.get("table"));
                                urls=test.split(",");
                                ImageView [] imgs = {img_per1,img_per2,img_per3};
                                for(int j=0;j<urls.length;j++) {
                                    LogUtils.e(urls[j]);
                                    byte[] sss = Base64Utils.Base64ToString(urls[j]);
                                    Glide.with(instance)
                                            .load(sss)
//                                            .placeholder(R.mipmap.per)
                                            .error(R.mipmap.now_no_pic).into(imgs[j]);
                                    imgs[j].setVisibility(View.VISIBLE);
                                }
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

    String [] urls;

    @Override
    public void addListener() throws Exception {
        img_per1.setOnClickListener(instance);
        img_per2.setOnClickListener(instance);
        img_per3.setOnClickListener(instance);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    String img;
    private void connecting() {

        LogUtils.e("详情LS"+HttpUrlUtils.getHttpUrl().clueDetil()+id+"?access_token="+ SPUtils.get(instance,"access_token",""));
        OkHttpUtils
                .get()
                .url(HttpUrlUtils.getHttpUrl().clueDetil()+id+"?access_token="+ SPUtils.get(instance,"access_token",""))
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
                                edit_name.setText(NullUtil.getString(data.get("people")));
                                edit_tel.setText(NullUtil.getString(data.get("mp")));
                                edit_provence.setText(NullUtil.getString(data.get("address")));
                                edit_street.setText(NullUtil.getString(data.get("hname")));
                                edit_del.setText(NullUtil.getString(data.get("sketch")));
                                img = NullUtil.getString(data.get("img"));
                            }
                        }catch (Exception e){
                            Toast.makeText(instance,e.toString(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case img_per1_del_clue:
                intents(0);
                break;
            case img_per2_del_clue:
                intents(1);
                break;
            case img_per3_del_clue:
                intents(2);
                break;
        }
    }


    public void intents(int i){
        Intent intent = new Intent(instance,ShowBigPicActivity.class);
        intent.putExtra("url",urls[i]);
        startActivity(intent);
    }

}
