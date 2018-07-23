package com.xaqb.hotel.Views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Created by lenovo on 2017/8/14.
 */

public class ListViewForScroollView extends ListView {

    public ListViewForScroollView(Context context) {
        super(context);
    }

    public ListViewForScroollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ListViewForScroollView(Context context, AttributeSet attrs,
                                  int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    /**
     * 重写该方法，达到使ListView适应ScrollView的效果
     */
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
