package com.xaqianbai.QBHotelSecurutyGovernor.Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xaqianbai.QBHotelSecurutyGovernor.R;


/**
 * Created by fule on 2016/11/25.
 */
public class LeftMenuListAdapter extends BaseAdapter {
    private Context context;
    private String status = "";
    private String[] leftMenuTitles = {
            "线索信息", "联合检查","发案登记","处罚登记","关于我们","修改密码","版本更新"
    };
    private int[] icons = {

            R.mipmap.more_icon_message,
            R.mipmap.more_icon_journal,
            R.mipmap.more_icon_about,
            R.mipmap.more_icon_faan,
            R.mipmap.more_icon_regist,
            R.mipmap.more_icon_password,
            R.mipmap.more_icon_update,
    };
    private ViewHolder holder;

    public LeftMenuListAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return icons.length;
    }

    @Override
    public Object getItem(int i) {
        return icons[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(context, R.layout.left_listview_item, null);
            holder.ivStatus = (ImageView) convertView.findViewById(R.id.iv_card_status);
            holder.ivIcon = (ImageView) convertView.findViewById(R.id.iv_icon);
            holder.tvTitle = (TextView) convertView.findViewById(R.id.tv_title_left);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.ivIcon.setImageResource(icons[i]);
        holder.tvTitle.setText(leftMenuTitles[i]);
        return convertView;
    }

    private class ViewHolder {
        private ImageView ivIcon, ivStatus;
        private TextView tvTitle;
    }
}
