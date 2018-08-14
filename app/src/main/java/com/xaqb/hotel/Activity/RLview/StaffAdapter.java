package com.xaqb.hotel.Activity.RLview;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xaqb.hotel.Entity.Staff;
import com.xaqb.hotel.R;
import com.xaqb.hotel.Utils.GlideRoundTransform;


/**
 * Created by fl on 2016/12/30.
 */

public class StaffAdapter extends ListBaseAdapter<Staff> {
    Context mContext;
    public StaffAdapter(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public int getLayoutId() {
        return R.layout.staff_item;
    }

    @Override
    public void onBindItemHolder(SuperViewHolder holder, int position) {
        Staff item = mDataList.get(position);

        TextView tv_name = holder.getView(R.id.tv_name_staff_list);
        ImageView tv_pic = holder.getView(R.id.tv_pic_staff_list);
        ImageView img_sex = holder.getView(R.id.img_sex_staff_list);
        TextView tv_tel = holder.getView(R.id.tv_tel_staff_list);
        TextView tv_hotel = holder.getView(R.id.tv_hotel_staff_list);
        TextView tv_iden = holder.getView(R.id.tv_iden_staff_list);
        tv_name.setText(item.getName());
        tv_tel.setText(item.getTel());
        tv_hotel.setText(item.getHotel());
        tv_iden.setText(item.getIden());
        Glide.with(mContext)
                .load(item.getPic())
                .transform(new GlideRoundTransform(mContext,10))
                .placeholder(R.mipmap.per)
                .error(R.mipmap.ic_launcher)
                .into(tv_pic);
        if (item.getSex().equals("1")){
            img_sex.setImageResource(R.mipmap.man);
        }else{
            img_sex.setImageResource(R.mipmap.woman);
        }
    }
}