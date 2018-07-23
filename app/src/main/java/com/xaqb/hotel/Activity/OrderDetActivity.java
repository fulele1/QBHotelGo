package com.xaqb.hotel.Activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;
import com.xaqb.hotel.R;
import com.xaqb.hotel.Utils.CircleTransform;
import com.xaqb.hotel.Utils.GlideCircleTransform;
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

/**
 * 暂时没有用
 */
public class OrderDetActivity extends AppCompatActivity {

    private Unbinder unbinder;
    private OrderDetActivity instance;
    @BindView(R.id.tv_title)
    TextView title;
    @BindView(R.id.layout_titlebar)
    FrameLayout titlebar;
    @BindView(R.id.img_pic_passenger)
    ImageView img_pic;
    @BindView(R.id.img_sex_passenger)
    ImageView img_sex;
    @BindView(R.id.txt_name_passenger)
    TextView txt_name;
    @BindView(R.id.txt_times_passenger)
    TextView txt_times;
    @BindView(R.id.txt_iden_passenger)
    TextView txt_iden;
    @BindView(R.id.txt_address_passenger)
    TextView txt_address;
    private LinearLayout mGallery;
    private int[] mImgIds;
    private LayoutInflater mInflater;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_psaaenger_det);
        instance = this;
        unbinder = ButterKnife.bind(instance);
        StatuBarUtil.setStatuBarLightMode(instance,getResources().getColor(R.color.white));//修改状态栏字体颜色为黑色
        titlebar.setBackgroundColor(getResources().getColor(R.color.white));
        title.setText("详情");
        mInflater = LayoutInflater.from(this);
        initData();
        initView();
        getIntentData();
        connecting();
    }



    private void initData()
    {
        mImgIds = new int[] { R.mipmap.pic_test, R.mipmap.pic_test,
                R.mipmap.pic_test, R.mipmap.pic_test, R.mipmap.pic_test,
                R.mipmap.pic_test, R.mipmap.pic_test, R.mipmap.pic_test,
                R.mipmap.pic_test, R.mipmap.pic_test, R.mipmap.pic_test,
                R.mipmap.pic_test, R.mipmap.pic_test, R.mipmap.pic_test, R.mipmap.pic_test};
    }

    private void initView()
    {
        mGallery = (LinearLayout) findViewById(R.id.id_gallery);

        for (int i = 0; i < mImgIds.length; i++)
        {

            View view = mInflater.inflate(R.layout.activity_index_gallery_item,
                    mGallery, false);
            LinearLayout lay_gallery = (LinearLayout) view
                    .findViewById(R.id.lay_galler_item);

            ViewGroup.LayoutParams lp = lay_gallery.getLayoutParams();
            lp.width = instance.getWindowManager().getDefaultDisplay().getWidth();
            lp.height = instance.getWindowManager().getDefaultDisplay().getWidth();
            lay_gallery.setLayoutParams(lp);

            ImageView img = (ImageView) view
                    .findViewById(R.id.img_pic_del);
            Picasso.with(instance).load(mImgIds[i]).fit().into(img);
//            img.setImageResource(mImgIds[i]);
            TextView txt = (TextView) view
                    .findViewById(R.id.txt_hotel_del);
            txt.setText("香格里拉酒店 "+i);
            mGallery.addView(view);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    public void onBackward (View view){
        instance.finish();
    }


    private String id,type,name,idcode,address,sex,idtype;
    public String getIntentData() {
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        type = intent.getStringExtra("type");
        name = intent.getStringExtra("name");
        idcode = intent.getStringExtra("idcode");
        address = intent.getStringExtra("address");
        sex = intent.getStringExtra("sex");
        idtype = intent.getStringExtra("idtype");


        return "?ccode="+id+
                "&type="+type+
                "&name="+name+
                "&idcode="+idcode+
                "&address="+address+
                "&idtype="+idtype+
                "&sex="+sex;
    }

    private void connecting() {

        LogUtils.e(HttpUrlUtils.getHttpUrl().OrderDetil()+getIntentData()+"&access_token="+ SPUtils.get(instance,"access_token",""));
        OkHttpUtils
                .get()
                .url(HttpUrlUtils.getHttpUrl().OrderDetil()+getIntentData()+"&access_token="+ SPUtils.get(instance,"access_token",""))
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
                                txt_name.setText(NullUtil.getString(data.get("name")));//姓名
                                txt_iden.setText(NullUtil.getString(data.get("idtype"))+"："+NullUtil.getString(data.get("idcode")));//证件号
                                txt_address.setText("户籍地址："+NullUtil.getString(data.get("address")));//户籍地址
                                txt_times.setText("共入住"+NullUtil.getString(data.get("count"))+"次");//次数

                                if(!NullUtil.getString(data.get("image")).equals("")&&NullUtil.getString(data.get("image")) !=null){
                                    Glide.with(instance)
                                            .load(NullUtil.getString(data.get("img")))
                                            .transform(new GlideRoundTransform(instance,10))
                                            .placeholder(R.mipmap.per)
                                            .error(R.mipmap.ic_launcher)
                                            .into(img_pic);
                                }

                                if (NullUtil.getString(data.get("sex")).equals("1")){
                                    img_sex.setImageResource(R.mipmap.man);
                                }else if (NullUtil.getString(data.get("sex")).equals("0")){
                                    img_sex.setImageResource(R.mipmap.woman);
                                }

                            } else {
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
