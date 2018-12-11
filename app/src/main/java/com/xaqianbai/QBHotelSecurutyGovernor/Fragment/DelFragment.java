package com.xaqianbai.QBHotelSecurutyGovernor.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xaqianbai.QBHotelSecurutyGovernor.Entity.Del;
import com.xaqianbai.QBHotelSecurutyGovernor.R;
import com.xaqianbai.QBHotelSecurutyGovernor.Utils.HttpUrlUtils;
import com.xaqianbai.QBHotelSecurutyGovernor.Utils.SPUtils;

/**
 * Created by fl on 2017/5/2.
 */

@SuppressLint("ValidFragment")
public class DelFragment extends Fragment{
    private View view;
    private Context instance;
    private TextView txt_com;
    private TextView txt_days;
    private TextView txt_go;
    private TextView txt_hotel;
    private TextView txt_room_num;
    private ImageView img_pic;
    private Del mDel;

    public DelFragment(Del del) {
        mDel = del;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_index_gallery_item,null);
        instance = DelFragment.this.getActivity();
        txt_com = (TextView) view.findViewById(R.id.txt_com_del);
        txt_days = (TextView) view.findViewById(R.id.txt_days_del);
        txt_go = (TextView) view.findViewById(R.id.txt_go_del);
        txt_hotel = (TextView) view.findViewById(R.id.txt_hotel_del);
        txt_room_num = (TextView) view.findViewById(R.id.txt_room_num_del);
        img_pic = (ImageView) view.findViewById(R.id.img_pic_del);

        txt_com.setText(mDel.getCome());
        txt_go.setText(mDel.getGo());
        txt_days.setText(mDel.getDays());
        txt_hotel.setText(mDel.getHotel());
        txt_room_num.setText(mDel.getRoomNum());
        String ppp = "";
        if (mDel.getPk().equals("dt_id")){
            ppp = "1001";
        }else if (mDel.getPk().equals("ft_id")){
            ppp = "1002";
        }else if (mDel.getPk().equals("mt_id")){
            ppp = "1003";
        }
        Glide.with(instance)
                .load(HttpUrlUtils.getHttpUrl().picInDel()+"/"+ppp+"/"+mDel.getPic_id()+"/"+mDel.getPic()+"?access_token="+ SPUtils.get(instance,"access_token",""))
//                .placeholder(R.mipmap.per)
                .error(R.mipmap.now_no_pic)
                .into(img_pic);
        return view;
    }

}