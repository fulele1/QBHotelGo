package com.xaqb.unlock.banner;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.flyco.banner.widget.Banner.BaseIndicatorBanner;
import com.xaqb.unlock.R;

public class SimpleImageBanner extends BaseIndicatorBanner<BannerItem, SimpleImageBanner> {
    private ColorDrawable colorDrawable;

    public SimpleImageBanner(Context context) {
        this(context, null, 0);
    }

    public SimpleImageBanner(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SimpleImageBanner(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        colorDrawable = new ColorDrawable(Color.parseColor("#555555"));
    }

    @Override
    public void onTitleSlect(TextView tv, int position) {
        final BannerItem item = mDatas.get(position);
        tv.setText(item.title);
    }

    @Override
    public View onCreateItemView(int position) {
        View inflate = View.inflate(mContext, R.layout.adapter_simple_image, null);
        ImageView iv = ViewFindUtils.find(inflate, R.id.iv);

        final BannerItem item = mDatas.get(position);
        int itemWidth = mDisplayMetrics.widthPixels;
        int itemHeight = (int) (itemWidth * 360 * 1.0f / 640);


//        int itemHeight = mDisplayMetrics.heightPixels;
//        iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
//        iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
//        iv.setLayoutParams(new LinearLayout.LayoutParams(itemWidth, itemHeight));

        String imgUrl = item.imgUrl;

        //使用本地图片显示广告
        int[] images = {R.mipmap.news_test5,
                R.mipmap.news_test6,
                R.mipmap.news_test7,
                R.mipmap.news_test8,
        };

        if (!TextUtils.isEmpty(imgUrl)) {
            Glide.with(mContext)
                    //网络图片写法
//                    .load(imgUrl)
                    //本地图片写法
                    .load(images[position])
                    .override(itemWidth, itemHeight)
                    .centerCrop()
                    .placeholder(colorDrawable)
                    .into(iv);
        } else {
            iv.setImageDrawable(colorDrawable);
        }

        return inflate;
    }
}
