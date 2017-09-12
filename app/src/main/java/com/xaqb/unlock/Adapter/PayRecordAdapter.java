package com.xaqb.unlock.Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xaqb.unlock.Entity.IncomeInfo;
import com.xaqb.unlock.R;
import com.xaqb.unlock.Utils.ToolsUtils;

import java.util.List;

/**
 * Created by lenovo on 2017/8/14.
 */

public class PayRecordAdapter extends BaseAdapter {
    private Context context;
    private List<IncomeInfo> incomeInfos;
    private ViewHolder holder;

    public PayRecordAdapter(Context context, List<IncomeInfo> incomeInfos) {
        this.context = context;
        this.incomeInfos = incomeInfos;
    }

    @Override
    public int getCount() {
        return incomeInfos.size();
    }

    @Override
    public Object getItem(int i) {
        return incomeInfos.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        IncomeInfo incomeInfo = incomeInfos.get(i);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(context, R.layout.pay_record_listview_item, null);
            holder.tvPayType = (TextView) convertView.findViewById(R.id.tv_pay_type);
            holder.tvPrice = (TextView) convertView.findViewById(R.id.tv_pay_price);
            holder.tvOrderTime = (TextView) convertView.findViewById(R.id.tv_pay_time);
            holder.tvPayStatus = (TextView) convertView.findViewById(R.id.tv_pay_status);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        String payType = incomeInfo.getPayType();
        if (payType.equals("wxpay")) {
            holder.tvPayType.setText("微信支付");
        } else if (payType.equals("alipay")) {
            holder.tvPayType.setText("阿里支付");
        } else if (payType.equals("offpay")) {
            holder.tvPayType.setText("线下支付");
        } else {
            holder.tvPayType.setText(payType);
        }
        holder.tvPrice.setText(incomeInfo.getOrderPrice());

        holder.tvOrderTime.setText(ToolsUtils.getStrTime(incomeInfo.getOrderTime()));
        String payStatus = incomeInfo.getPayStatus();
        if (payStatus.equals("00")) {
            holder.tvPayStatus.setText("未支付");
        } else if (payStatus.equals("01")) {
            holder.tvPayStatus.setText("支付成功");
        } else if (payStatus.equals("03")) {
            holder.tvPayStatus.setText("未付清");
        }

        return convertView;
    }

    private class ViewHolder {
        private TextView tvPayType, tvPrice, tvOrderTime, tvPayStatus;
    }
}
