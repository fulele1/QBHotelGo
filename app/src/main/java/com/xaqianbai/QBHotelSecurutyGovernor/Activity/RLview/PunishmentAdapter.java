package com.xaqianbai.QBHotelSecurutyGovernor.Activity.RLview;

import android.content.Context;
import android.widget.TextView;

import com.xaqianbai.QBHotelSecurutyGovernor.Entity.Punishment;
import com.xaqianbai.QBHotelSecurutyGovernor.R;


/**
 * Created by fl on 2016/12/30.
 */

public class PunishmentAdapter extends ListBaseAdapter<Punishment> {
    Context mContext;
    public PunishmentAdapter(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public int getLayoutId() {
        return R.layout.punishment_item;
    }

    @Override
    public void onBindItemHolder(SuperViewHolder holder, int position) {
        Punishment item = mDataList.get(position);
        TextView txt_hname_crime = holder.getView(R.id.txt_hname_pu);
        TextView txt_date_pu = holder.getView(R.id.txt_date_pu);
        TextView txt_del_pu = holder.getView(R.id.txt_del_pu);
        txt_hname_crime.setText(item.getHname());
//        txt_date_pu.setText(DateUtil.getDate(item.getDate()));
//        txt_del_pu.setText(item.getDel());

        String del = item.getDel();
        if(del.equals("")){
            txt_del_pu.setText("暂无描述");
        }else {
            txt_del_pu.setText(del);

        }


    }
}