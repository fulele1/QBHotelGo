package com.xaqb.unlock.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.ImageView;
import android.widget.Toast;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.holder.Holder;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;
import com.jaeger.library.StatusBarUtil;
import com.squareup.picasso.Picasso;
import com.xaqb.unlock.R;
import com.xaqb.unlock.Utils.GsonUtil;
import com.xaqb.unlock.Utils.HttpUrlUtils;
import com.xaqb.unlock.Utils.LogUtils;
import com.xaqb.unlock.Utils.SPUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * Created by lenovo on 2018/1/19.
 */

public class TestActivity extends Activity{

    private TestActivity instance;

    //轮播下面的小点（小圆点是本地的，自己导入的图片）
    private int[] indicator = {R.mipmap.point_gary, R.mipmap.point_red};
    private ConvenientBanner convenientBanner;
    //图片加载地址的集合
    private List<String> bean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setTranslucent(this, 0);
        setContentView(R.layout.activity_main);
        instance = this;
        checkPic();
    }
    private String[] images;
    private String[] url;

    private void checkPic() {
        //  请求连接网络 解析后 拿到版本号和版本名
        OkHttpUtils.get()
                .url(HttpUrlUtils.getHttpUrl().getPic() + "?access_token=" + SPUtils.get(instance, "access_token", "").toString())
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        LogUtils.e("轮播图" + s);
                        s = "{\"state\":0,\"mess\":\"success\",\"table\":\n" +
                                " [{\"art_id\":1,\"art_img\":\"http:\\/\\/kaisuo.qbchoice.cn\\/\\/uploads\\/20171214\\/cbfdb45b59a8c3864e342251278dd6e8.gif\",\"url\":\"http:\\/\\/kaisuo.qbchoice.cn\\/home\\/index\\/news_detail\\/id\\/1\"},\n" +
                                " {\"art_id\":1,\"art_img\":\"http:\\/\\/kaisuo.qbchoice.cn\\/\\/uploads\\/20171214\\/cbfdb45b59a8c3864e342251278dd6e8.gif\",\"url\":\"http:\\/\\/kaisuo.qbchoice.cn\\/home\\/index\\/news_detail\\/id\\/1\"}]}";





                        Map<String, Object> map = GsonUtil.GsonToMaps(s);
                        if (map.get("state").toString().equals("1.0")) {
                            return;
                        } else if (map.get("state").toString().equals("0.0")) {

                            List<Map<String, Object>> data = GsonUtil.GsonToListMaps(GsonUtil.GsonString(map.get("table")));
                             images = new String [data.size()] ;
                             url = new String [data.size()] ;

                            for (int j = 0; j < data.size(); j++) {
                                images[j] = data.get(j).get("art_img").toString();
                                url[j] = data.get(j).get("url").toString();
                            }

                            convenientBanner = (ConvenientBanner) findViewById(R.id.cb_main);

                            bean = Arrays.asList(images);
                            convenientBanner.setPointViewVisible(true)
                                    //设置小点
                                    .setPageIndicator(indicator);
                            //允许手动轮播
                            convenientBanner.setManualPageable(true);
                            //设置自动轮播的时间
                            convenientBanner.startTurning(2000);
                            //设置点击事件
                            //泛型为具体实现类ImageLoaderHolder
                            convenientBanner.setPages(new CBViewHolderCreator<NetImageLoadHolder>() {
                                @Override
                                public NetImageLoadHolder createHolder() {
                                    return new NetImageLoadHolder();
                                }
                            }, bean);

                            //设置每个pager的点击事件
                            convenientBanner.setOnItemClickListener(new OnItemClickListener() {
                                @Override
                                public void onItemClick(int position) {
                                    LogUtils.e("轮播图"+url[convenientBanner.getCurrentItem()]);
                                    Uri uri = Uri.parse(url[convenientBanner.getCurrentItem()]);
                                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                    startActivity(intent);
                                }
                            });

                        }
                    }
                });
    }



    public class NetImageLoadHolder implements Holder<String> {
        private ImageView image_lv;

        //可以是一个布局也可以是一个Imageview
        @Override
        public ImageView createView(Context context) {
            image_lv = new ImageView(context);
            image_lv.setScaleType(ImageView.ScaleType.FIT_XY);

            return image_lv;
        }

        @Override
        public void UpdateUI(Context context, int position, String data) {
            //Picasso
            Picasso.with(context)
                    .load(data)
                    .placeholder(R.mipmap.main_pic1)
                    .error(R.mipmap.main_pic1)
                    .into(image_lv);

        }

    }
}
