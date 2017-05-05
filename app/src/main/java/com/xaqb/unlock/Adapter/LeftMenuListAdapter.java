package com.xaqb.unlock.Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xaqb.unlock.R;
import com.xaqb.unlock.Utils.Globals;
import com.xaqb.unlock.Utils.SPUtils;


/**
 * Created by lenovo on 2016/11/25.
 */
public class LeftMenuListAdapter extends BaseAdapter {
    private Context context;
    private String status = "";
    private String[] leftMenuTitles = {
            "我的订单","收入明细", "修改密码", "意见反馈",
//            "使用帮助",
            "实名认证", "关于我们"
    };
    private int[] icons = {
            R.mipmap.upload_64,
            R.mipmap.map_64,
            R.mipmap.gear_64,
            R.mipmap.document_64,
//            R.mipmap.flag_64,
            R.mipmap.user_info,
            R.mipmap.globe_64
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
            holder.tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.ivIcon.setImageResource(icons[i]);
        holder.tvTitle.setText(leftMenuTitles[i]);
        if (i == 4) {
            status = SPUtils.get(context, "staff_is_real", "").toString();
            if (status.equals(Globals.staffIsRealNo) || status.equals(Globals.staffIsRealFaild)) {
                holder.ivStatus.setImageResource(R.mipmap.error);
            } else if (status.equals(Globals.staffIsRealSuc)) {
                holder.ivStatus.setImageResource(R.mipmap.ok);
            } else if (status.equals(Globals.staffIsRealIng)) {
                holder.ivStatus.setImageResource(R.mipmap.warning);
            }
            holder.ivStatus.setVisibility(View.VISIBLE);
        }
        return convertView;
    }

    private class ViewHolder {
        private ImageView ivIcon, ivStatus;
        private TextView tvTitle;
    }
}
