package com.xaqb.hotel.Activity.RLview;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xaqb.hotel.Entity.Clue;
import com.xaqb.hotel.Entity.Hotel;
import com.xaqb.hotel.R;
import com.xaqb.hotel.Utils.DateUtil;
import com.xaqb.hotel.Utils.GlideRoundTransform;
import com.xaqb.hotel.Utils.LogUtils;


/**
 * Created by fl on 2016/12/30.
 */

public class ClueAdapter extends ListBaseAdapter<Clue> {
    Context mContext;
    public ClueAdapter(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public int getLayoutId() {
        return R.layout.clue_item;
    }
    @Override
    public void onBindItemHolder(SuperViewHolder holder, int position) {
        final Clue item = mDataList.get(position);

        ImageView tv_pic = holder.getView(R.id.tv_pic_clue);
        ImageView img_tel = holder.getView(R.id.tv_tel_clue);

        img_tel.setOnClickListener((View.OnClickListener) mContext);
        TextView txt_date = holder.getView(R.id.txt_date_clue);
        TextView txt_name = holder.getView(R.id.txt_name_clue);
        TextView txt_address = holder.getView(R.id.txt_address_hotel);
        TextView txt_goods = holder.getView(R.id.txt_goods_clue);
        txt_date.setText("上报日期:"+item.getDate());
        txt_name.setText("可疑人员:"+item.getName());
        txt_address.setText("地址:"+item.getAddress());
        LogUtils.e("联系电话:"+item.getTel());
        txt_goods.setText("可疑物品:"+item.getGood());
        if(!item.getPic().equals("")&&item.getPic()!=null){
            Glide.with(mContext).load(item.getPic()).transform(new GlideRoundTransform(mContext,10))
                    .placeholder(R.mipmap.per).error(R.mipmap.ic_launcher).into(tv_pic);
        }

        img_tel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent dialIntent =  new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + item.getTel()));//跳转到拨号界面，同时传递电话号码
                mContext.startActivity(dialIntent);
            }
        });

    }
}