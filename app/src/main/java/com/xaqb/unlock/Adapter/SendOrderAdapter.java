package com.xaqb.unlock.Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xaqb.unlock.Entity.SendOrder;

import java.util.ArrayList;
import java.util.List;

/**
 * 已发数据适配器
 * Created by chengeng on 2017/04/01.
 */
public class SendOrderAdapter extends BaseAdapter {
    private Context context;
    private ViewHolder holder;
    private List<SendOrder> addresses;

    public SendOrderAdapter(Context context, List<SendOrder> addresses) {
        if (addresses != null) {
            this.addresses = addresses;
        } else {
            this.addresses = new ArrayList<>();
        }
        this.context = context;
    }

    @Override
    public int getCount() {
        return addresses.size();
    }

    @Override
    public Object getItem(int i) {
        return addresses.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
//        if (convertView == null) {
//            holder = new ViewHolder();
//            convertView = View.inflate(context, R.layout.send_order_item, null);
//            holder.tvName = (TextView) convertView.findViewById(R.id.tv_add_name);
//            holder.tvPhone = (TextView) convertView.findViewById(R.id.tv_add_phone);
//            holder.tvLocation = (TextView) convertView.findViewById(R.id.tv_add_location);
//            holder.cbDefault = (CheckBox) convertView.findViewById(R.id.cb_default_add);
//            holder.tvEditor = (TextView) convertView.findViewById(R.id.tv_add_editor);
////            holder.tvDelete = (TextView) convertView.findViewById(R.id.tv_add_delete);
//            convertView.setTag(holder);
//        } else {
//            holder = (ViewHolder) convertView.getTag();
//        }
//        final SendOrder  ad = addresses.get(i);
//        holder.tvName.setText(ad.getName());
//        holder.tvPhone.setText(ad.getPhone());
//        holder.tvLocation.setText(ad.getProvince() + ad.getCity() + ad.getDistrict() + ad.getLocation());
//        holder.cbDefault.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                LogUtils.i("点击checkbox");
//            }
//        });
//        holder.tvEditor.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                LogUtils.i("点击编辑");
//                Intent i = new Intent(context, AddressEditorActivity.class);
//                Bundle bundle = new Bundle();
//                bundle.putSerializable("address", ad);
//                i.putExtras(bundle);
//                context.startActivity(i);
//
//            }
//        });
        return convertView;
    }

    private class ViewHolder {
        private TextView tvOrderNo, tvOrderTime, tvOrderAddress;
    }
}
