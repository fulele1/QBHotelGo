package com.xaqianbai.QBHotelSecurutyGovernor.Activity.RLview;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xaqianbai.QBHotelSecurutyGovernor.Entity.Order;
import com.xaqianbai.QBHotelSecurutyGovernor.R;
import com.xaqianbai.QBHotelSecurutyGovernor.Utils.GlideRoundTransform;


/**
 * Created by fl on 2016/12/30.
 */

public class OrderAdapter extends ListBaseAdapter<Order> {
    Context mContext;
    public OrderAdapter(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public int getLayoutId() {
        return R.layout.order_item;
    }

    @Override
    public void onBindItemHolder(SuperViewHolder holder, int position) {
        Order item = mDataList.get(position);

        TextView tv_name = holder.getView(R.id.tv_name_order_list);
        ImageView tv_pic = holder.getView(R.id.tv_pic_order_list);
        ImageView tv_sex = holder.getView(R.id.img_sex_order_list);
        TextView tv_iden = holder.getView(R.id.tv_iden_list);
        TextView tv_address = holder.getView(R.id.tv_address_order_list);
        tv_name.setText(item.getPassenger());
        tv_iden.setText(item.getIden_type()+":"+item.getIden());
        if (!item.getAddress().equals("")){
            tv_address.setText("户籍地址:"+item.getAddress());
        }

        Glide.with(mContext).load(item.getPic()).transform(new GlideRoundTransform(mContext,15))
                .placeholder(R.mipmap.per).error(R.mipmap.ic_launcher).into(tv_pic);

        if(item.getSex().equals("1")){
            tv_sex.setImageResource(R.mipmap.man);
        }else {
            tv_sex.setImageResource(R.mipmap.woman);
        }
    }
}