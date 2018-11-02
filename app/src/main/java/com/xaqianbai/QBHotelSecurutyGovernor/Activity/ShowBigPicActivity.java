package com.xaqianbai.QBHotelSecurutyGovernor.Activity;


import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.xaqianbai.QBHotelSecurutyGovernor.R;
import com.xaqianbai.QBHotelSecurutyGovernor.Utils.Base64Utils;
import com.xaqianbai.QBHotelSecurutyGovernor.Utils.StatuBarUtil;

public class ShowBigPicActivity extends BaseActivityNew {



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void initViews() throws Exception {
        StatuBarUtil.setStatuBarLightMode(this,getResources().getColor(R.color.white));//修改状态栏字体颜色为黑色
        setContentView(R.layout.activity_show_big_pic);

    }

    @Override
    public void initData() throws Exception {
        Intent intent = getIntent();
        String url = intent.getStringExtra("url");
        ImageView imageView = (ImageView) findViewById(R.id.img_pic_show);
        byte[] sss = Base64Utils.Base64ToString(url);
        Glide.with(this)
                .load(sss)
//                .placeholder(R.mipmap.per)
//                .error(R.mipmap.ic_launcher)
                .into(imageView);
    }

    @Override
    public void addListener() throws Exception {

    }
}
