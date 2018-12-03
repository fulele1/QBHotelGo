package com.xaqianbai.QBHotelSecurutyGovernor.Activity.RLview;

import android.content.Context;
import android.widget.TextView;

import com.xaqianbai.QBHotelSecurutyGovernor.Entity.Crime;
import com.xaqianbai.QBHotelSecurutyGovernor.R;
import com.xaqianbai.QBHotelSecurutyGovernor.Utils.DateUtil;


/**
 * Created by fl on 2016/12/30.
 */

public class CrimeAdapter extends ListBaseAdapter<Crime> {
    Context mContext;
    public CrimeAdapter(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public int getLayoutId() {
        return R.layout.crime_item;
    }

    @Override
    public void onBindItemHolder(SuperViewHolder holder, int position) {
        Crime item = mDataList.get(position);
        TextView txt_hname_crime = holder.getView(R.id.txt_hname_crime);
        TextView txt_date_crime = holder.getView(R.id.txt_date_crime);
        TextView txt_type_one_crime = holder.getView(R.id.txt_type_one_crime);
        TextView txt_type_two_crime = holder.getView(R.id.txt_type_two_crime);
        TextView txt_del_crime = holder.getView(R.id.txt_del_crime);
        txt_hname_crime.setText(item.getHname());
        txt_date_crime.setText(DateUtil.getDate(item.getDate()));
        txt_type_one_crime.setText("#"+item.getType_one());
        txt_type_two_crime.setText("#"+item.getType_two());
        String del = item.getDel();
        if(del.equals("")){
            txt_del_crime.setText("暂无描述");
        }else {
            txt_del_crime.setText(del);

        }

    }
}