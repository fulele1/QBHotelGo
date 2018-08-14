package com.xaqb.hotel.Activity;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.xaqb.hotel.Entity.Del;
import com.xaqb.hotel.Entity.Log;
import com.xaqb.hotel.Fragment.DelFragment;
import com.xaqb.hotel.Fragment.FragmentAdapter;
import com.xaqb.hotel.R;
import com.xaqb.hotel.Utils.DateUtil;
import com.xaqb.hotel.Utils.GlideRoundTransform;
import com.xaqb.hotel.Utils.GsonUtil;
import com.xaqb.hotel.Utils.HttpUrlUtils;
import com.xaqb.hotel.Utils.LogUtils;
import com.xaqb.hotel.Utils.NullUtil;
import com.xaqb.hotel.Utils.SPUtils;
import com.xaqb.hotel.Utils.StatuBarUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.Call;

public class PassengerDetActivity extends AppCompatActivity {

    private Unbinder unbinder;
    private PassengerDetActivity instance;
    @BindView(R.id.tv_title)
    TextView title;
    @BindView(R.id.layout_titlebar)
    FrameLayout titlebar;
    @BindView(R.id.img_pic_passenger)
    ImageView img_pic;
    @BindView(R.id.img_sex_passenger)
    ImageView img_sex;
    @BindView(R.id.txt_name_passenger)
    TextView txt_name;
    @BindView(R.id.txt_times_passenger)
    TextView txt_times;
    @BindView(R.id.txt_iden_passenger)
    TextView txt_iden;
    @BindView(R.id.txt_address_passenger)
    TextView txt_address;
    @BindView(R.id.vpg_pass_del)
    ViewPager vpg;
    private LinearLayout mGallery;
    private int[] mImgIds;
    private LayoutInflater mInflater;
    private FragmentManager mFragmentManager;
    private List<Fragment> mFrags;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_psaaenger_det);
        instance = this;
        unbinder = ButterKnife.bind(instance);
        StatuBarUtil.setStatuBarLightMode(instance,getResources().getColor(R.color.white));//修改状态栏字体颜色为黑色
        titlebar.setBackgroundColor(getResources().getColor(R.color.white));
        title.setText("详情");
        mInflater = LayoutInflater.from(this);
        getIntentData();
        connecting(1);
        mFragmentManager = this.getSupportFragmentManager();
        vpg.setOnPageChangeListener(new pageChange());
    }

    private void initData(int times,List<Del> del) {
        mFrags = new ArrayList<>();
        for (int i = 0;i<times;i++){
            mFrags.add(new DelFragment(del.get(i)));
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }


    class pageChange implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            if (position ==0){
                LogUtils.e(Integer.parseInt(curr)+1+"");
                connecting(Integer.parseInt(curr)+1);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    }



    public void onBackward (View view){
        instance.finish();
    }


    private String id,type,name,idcode,address,sex,idtype,dt_id,pic;
    public String getIntentData() {
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        type = intent.getStringExtra("type");
        name = intent.getStringExtra("name");
        idcode = intent.getStringExtra("idcode");
        address = intent.getStringExtra("address");
        sex = intent.getStringExtra("sex");
        idtype = intent.getStringExtra("idtype");
        dt_id = intent.getStringExtra("dt_id");
        pic = intent.getStringExtra("pic");


        return "?ccode="+id+
                "&type="+type+
                "&name="+name+
                "&idcode="+idcode+
                "&address="+address+
                "&idtype="+idtype+
                "&sex="+sex;
    }

    List<Del> dels;
    List<Del> delss = new ArrayList<>();
    String curr;
    String page;

    private void connecting(final int p) {

        LogUtils.e(HttpUrlUtils.getHttpUrl().OrderDetil()+getIntentData()+"&access_token="+ SPUtils.get(instance,"access_token","")+"&p="+p);
        OkHttpUtils
                .get()
                .url(HttpUrlUtils.getHttpUrl().OrderDetil()+getIntentData()+"&access_token="+ SPUtils.get(instance,"access_token","")+"&p="+p)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        LogUtils.e("onError"+e.toString());
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        try {
                            Map<String, Object> data = GsonUtil.JsonToMap(s);
                            LogUtils.e("data"+data);
                            if (data.get("state").toString().equals("1")) {
                                Toast.makeText(instance,data.get("mess").toString(),Toast.LENGTH_LONG).show();
                                return;
                            } else if (data.get("state").toString().equals("0")) {
                                curr = NullUtil.getString(data.get("curr"));
                                LogUtils.e("curr"+curr);
                                page = NullUtil.getString(data.get("page"));
                                String pk = NullUtil.getString(data.get("pk"));
                                txt_name.setText(NullUtil.getString(data.get("name")));//姓名
                                txt_iden.setText(NullUtil.getString(data.get("idtype"))+"："+NullUtil.getString(data.get("idcode")));//证件号
                                if (pk.equals("dt_id")){
                                txt_address.setText("户籍地址："+NullUtil.getString(data.get("address")));//户籍地址
                                }
                                LogUtils.e("count"+NullUtil.getString(data.get("count")));

                                txt_times.setText("共入住"+NullUtil.getString(data.get("count"))+"次");//次数

                                if(!pic.equals("")&&pic !=null){
                                    Glide.with(instance)
                                            .load(pic)
                                            .transform(new GlideRoundTransform(instance,10))
                                            .placeholder(R.mipmap.per)
                                            .error(R.mipmap.ic_launcher)
                                            .into(img_pic);
                                }

                                if (NullUtil.getString(data.get("sex")).equals("1")){
                                    img_sex.setImageResource(R.mipmap.man);
                                }else if (NullUtil.getString(data.get("sex")).equals("0")){
                                    img_sex.setImageResource(R.mipmap.woman);
                                }


                                List<Map<String, Object>> lists = GsonUtil.GsonToListMaps(GsonUtil.GsonString(data.get("history")));
                                dels = new ArrayList<>();
                                LogUtils.e("你看pic_id"+pk);
                                LogUtils.e("你看pic_id"+lists.toString());
                                for (int j = 0; j < lists.size(); j++) {
                                    Del del = new Del();
                                    del.setCome(DateUtil.getDate(NullUtil.getString(lists.get(j).get("ltime"))));//入住时间
                                    String goTime = NullUtil.getString(lists.get(j).get("etime"));
                                    if (goTime.equals("")){
                                        goTime = "未离店";
                                    }else{
                                        goTime = DateUtil.getDate(goTime);
                                    }
//                                    del.setGo(goTime+curr+"--"+j);//离店时间
                                    del.setGo(goTime);//离店时间
                                    del.setDays("共"+NullUtil.getString(lists.get(j).get("day"))+"天");//入住天数
                                    del.setHotel(NullUtil.getString(lists.get(j).get("hname")));//酒店名称
                                    del.setRoomNum(NullUtil.getString(lists.get(j).get("noroom")));//房间号
                                    del.setPic(NullUtil.getString(data.get("imgs")));//图片(外层得到)
                                    del.setPic_id(NullUtil.getString(lists.get(j).get(pk)));//图片id
                                    del.setPk(pk);//图片id
                                    dels.add(del);

                                }
                                delss.addAll(dels);
                                Collections.reverse(delss);

                                initData(delss.size(),delss);
                                vpg.setAdapter(new FragmentAdapter(mFragmentManager,mFrags));
                                vpg.setCurrentItem(14*Integer.parseInt(curr));

                            }else if (data.get("state").toString().equals("10")) {
                                //响应失败
                                Toast.makeText(instance, data.get("mess").toString(), Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(instance,LoginActivity.class));
                                finish();
                            } else {
                                //响应失败
                                LogUtils.e("elsemess"+data.get("mess").toString());
                                Toast.makeText(instance,data.get("mess").toString(),Toast.LENGTH_LONG).show();

                            }

                        } catch (Exception e) {
                            LogUtils.e("catch"+e.toString());
                            Toast.makeText(instance,e.toString(),Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

}
