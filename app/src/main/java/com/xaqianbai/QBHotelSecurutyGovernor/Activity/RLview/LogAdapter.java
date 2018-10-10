package com.xaqianbai.QBHotelSecurutyGovernor.Activity.RLview;

import android.content.Context;
import android.widget.TextView;

import com.xaqianbai.QBHotelSecurutyGovernor.Entity.Log;
import com.xaqianbai.QBHotelSecurutyGovernor.R;


/**
 * Created by fl on 2016/12/30.
 */

public class LogAdapter extends ListBaseAdapter<Log> {
    Context mContext;
    public LogAdapter(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public int getLayoutId() {
        return R.layout.log_item;
    }

    @Override
    public void onBindItemHolder(SuperViewHolder holder, int position) {
        Log item = mDataList.get(position);
        String dates = item.getDate();
        TextView txt_day = holder.getView(R.id.txt_day_log);
        TextView txt_date = holder.getView(R.id.txt_date_log);
        TextView txt_org = holder.getView(R.id.txt_org_log);
        TextView txt_det = holder.getView(R.id.txt_det_log);
        txt_day.setText(dates.substring(dates.length()-2)+"号");
        txt_date.setText(dates.substring(0,7));
        txt_org.setText(item.getOrg());
        txt_det.setText(item.getDet());

    }
}