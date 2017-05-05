package com.xaqb.unlock.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.xaqb.unlock.Entity.HelpInfo;
import com.xaqb.unlock.R;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerCompatAdapter extends PagerAdapter {
    List<HelpInfo> helpInfos = new ArrayList<HelpInfo>();
    private Context context;
    // 2种不同的布局
    public static final int VALUE_LEFT_ITEM = 0;// 左侧条目
    public static final int VALUE_RIGHT_ITEM = 1;// 右侧条目
    private LayoutInflater mInflater;
    private Dialog logoutDialog;
    private ListView lv;
    private ImageView iv;
    private int screenWidth;
    private TextView tvBeginTime, tvProgress, tvEmpTime, tvNeedPoint, tvLevelUpDay;
    private Button btStartStudy;

    public ViewPagerCompatAdapter(List<HelpInfo> list, Context context) {
        this.context = context;
        if (list == null) {
            this.helpInfos = new ArrayList<>();
        } else {
            this.helpInfos = list;
        }
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
//        return helpInfos.size();
        return 5;
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        ImageView ll = (ImageView) mInflater.inflate(R.layout.help_viewpager_item, null);
//        iv = (ImageView) ll.findViewById(R.id.iv_help_viewpager);
//		 iv.setImageResource(R.mipmap.titlebg);
//        iv.setBackgroundResource(R.drawable.titlebg);
        container.addView(ll);
        return ll;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        // super.destroyItem(container, position, object);
    }




}
