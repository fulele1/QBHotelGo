package com.xaqianbai.QBHotelSecurutyGovernor.Activity.RLview;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xaqianbai.QBHotelSecurutyGovernor.Entity.Passenger;
import com.xaqianbai.QBHotelSecurutyGovernor.R;
import com.xaqianbai.QBHotelSecurutyGovernor.Utils.GlideRoundTransform;


/**
 * Created by fl on 2016/12/30.
 */

public class PassengerAdapter extends ListBaseAdapter<Passenger> {
    Context mContext;
    public PassengerAdapter(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public int getLayoutId() {
        return R.layout.passenger_item;
    }

    @Override
    public void onBindItemHolder(SuperViewHolder holder, int position) {
        Passenger item = mDataList.get(position);

        TextView tv_name = holder.getView(R.id.tv_name_pass_list);
        ImageView tv_pic = holder.getView(R.id.img_pic_pass_list);
        ImageView tv_sex = holder.getView(R.id.img_sex_pass_list);
        TextView tv_iden = holder.getView(R.id.tv_iden_pass_list);
        TextView tv_address = holder.getView(R.id.tv_address_pass_list);
        tv_name.setText(item.getName());
        tv_iden.setText(item.getIdenType()+":"+item.getIden());

        if (!item.getAddress().equals("")){
            tv_address.setText("户籍地址:"+item.getAddress());
        }

        if(!item.getPic().equals("")&&item.getPic() !=null){
            Glide.with(mContext).load(item.getPic()).transform(new GlideRoundTransform(mContext,10))
                    .placeholder(R.mipmap.per).error(R.mipmap.per).into(tv_pic);
        }

               if(item.getSex().equals("1")){
            tv_sex.setImageResource(R.mipmap.man);
        }else {
            tv_sex.setImageResource(R.mipmap.woman);
        }
    }
}