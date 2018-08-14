package com.xaqb.hotel.Activity.RLview;

import android.content.Context;
import android.content.IntentFilter;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xaqb.hotel.Entity.Hotel;
import com.xaqb.hotel.Entity.Passenger;
import com.xaqb.hotel.R;


/**
 * Created by fl on 2016/12/30.
 */

public class HotelAdapter extends ListBaseAdapter<Hotel> {
    Context mContext;
    public HotelAdapter(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public int getLayoutId() {
        return R.layout.hotel_item;
    }

    @Override
    public void onBindItemHolder(SuperViewHolder holder, int position) {
        Hotel item = mDataList.get(position);

        TextView tv_name = holder.getView(R.id.txt_name_hotel);
        ImageView tv_pic = holder.getView(R.id.tv_pic_hotel);
        TextView txt_manager = holder.getView(R.id.txt_manager_hotel);
        TextView tv_address = holder.getView(R.id.txt_address_hotel);
        TextView txt_tel = holder.getView(R.id.txt_tel_hotel);
        ImageView star_one = holder.getView(R.id.img_star_one);
        ImageView star_two = holder.getView(R.id.img_star_two);
        ImageView star_three = holder.getView(R.id.img_star_three);
        ImageView star_four = holder.getView(R.id.img_star_four);
        ImageView star_five = holder.getView(R.id.img_star_five);
        tv_name.setText(item.getName());
        txt_manager.setText("负责人:"+item.getManager());
        tv_address.setText(item.getAddress());
        txt_tel.setText(item.getTel());
        if(!item.getPic().equals("")&&item.getPic()!=null){
            Glide.with(mContext).load(item.getPic())
                    .placeholder(R.mipmap.hotel).error(R.mipmap.hotel).into(tv_pic);
        }

        if (item.getStars().equals("1")){
            star_one.setVisibility(View.VISIBLE);
        }else if (item.getStars().equals("2")){
            star_one.setVisibility(View.VISIBLE);
            star_two.setVisibility(View.VISIBLE);
        }else if (item.getStars().equals("3")){
            star_one.setVisibility(View.VISIBLE);
            star_two.setVisibility(View.VISIBLE);
            star_three.setVisibility(View.VISIBLE);
        }else if (item.getStars().equals("4")){
            star_one.setVisibility(View.VISIBLE);
            star_two.setVisibility(View.VISIBLE);
            star_three.setVisibility(View.VISIBLE);
            star_four.setVisibility(View.VISIBLE);
        }else if (item.getStars().equals("5")){
            star_one.setVisibility(View.VISIBLE);
            star_two.setVisibility(View.VISIBLE);
            star_three.setVisibility(View.VISIBLE);
            star_four.setVisibility(View.VISIBLE);
            star_five.setVisibility(View.VISIBLE);
        }


    }



}