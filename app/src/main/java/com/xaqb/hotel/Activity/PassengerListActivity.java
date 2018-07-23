package com.xaqb.hotel.Activity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.jdsjlzx.ItemDecoration.DividerDecoration;
import com.github.jdsjlzx.interfaces.OnItemClickListener;
import com.github.jdsjlzx.interfaces.OnLoadMoreListener;
import com.github.jdsjlzx.interfaces.OnNetWorkErrorListener;
import com.github.jdsjlzx.interfaces.OnRefreshListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.github.jdsjlzx.recyclerview.ProgressStyle;
import com.xaqb.hotel.Activity.RLview.PassengerAdapter;
import com.xaqb.hotel.Entity.Order;
import com.xaqb.hotel.Entity.Passenger;
import com.xaqb.hotel.R;
import com.xaqb.hotel.Utils.GsonUtil;
import com.xaqb.hotel.Utils.HttpUrlUtils;
import com.xaqb.hotel.Utils.IdenTypeUtils;
import com.xaqb.hotel.Utils.LogUtils;
import com.xaqb.hotel.Utils.NullUtil;
import com.xaqb.hotel.Utils.SPUtils;
import com.xaqb.hotel.Utils.StatuBarUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.Call;

import static com.xaqb.hotel.R.layout.activity_staff_list;

public class PassengerListActivity extends AppCompatActivity{

    PassengerListActivity instance;
    Unbinder unbinder;
    @BindView(R.id.tv_title)
    TextView title;
    @BindView(R.id.layout_titlebar)
    FrameLayout titlebar;
    @BindView(R.id.txt_size_pass_list)
    TextView txt_size;
    @BindView(R.id.list_r_staff_list)
    LRecyclerView list_r;
    /**服务器端一共多少条数据*/
    private int TOTAL_COUNTER;//如果服务器没有返回总数据或者总页数，这里设置为最大值比如10000，什么时候没有数据了根据接口返回判断

    /**每一页展示多少条数据*/
    private int REQUEST_COUNT;

    /**已经获取到多少条数据了*/
    private static int mCurrentCounter = 0;
    private  int mCurrentpage = 1;


    private PassengerAdapter mDataAdapter = null;

    private PassengerListActivity.PreviewHandler mHandler = new PassengerListActivity.PreviewHandler(this);
    private LRecyclerViewAdapter mLRecyclerViewAdapter = null;



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_list);
        instance = this;
        unbinder = ButterKnife.bind(instance);
        StatuBarUtil.setStatuBarLightMode(instance,getResources().getColor(R.color.white));//修改状态栏字体颜色为黑色
        titlebar.setBackgroundColor(getResources().getColor(R.color.white));
        title.setText("旅客列表");

        mDataAdapter = new PassengerAdapter(instance);
        mLRecyclerViewAdapter = new LRecyclerViewAdapter(mDataAdapter);
        list_r.setAdapter(mLRecyclerViewAdapter);

        DividerDecoration divider = new DividerDecoration.Builder(this)
                .setHeight(R.dimen.list_line)
                .setPadding(R.dimen.default_divider_padding)
                .setColorResource(R.color.white)
                .build();

        //mRecyclerView.setHasFixedSize(true);
        list_r.addItemDecoration(divider);

        list_r.setLayoutManager(new LinearLayoutManager(this));

        list_r.setRefreshProgressStyle(ProgressStyle.LineSpinFadeLoader);
        list_r.setArrowImageView(R.drawable.ic_pulltorefresh_arrow);
        list_r.setLoadingMoreProgressStyle(ProgressStyle.BallSpinFadeLoader);

        //add a HeaderView
        final View header = LayoutInflater.from(this).inflate(R.layout.sample_header,(ViewGroup)findViewById(android.R.id.content), false);
        mLRecyclerViewAdapter.addHeaderView(header);

        list_r.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {

                mDataAdapter.clear();
                mLRecyclerViewAdapter.notifyDataSetChanged();//fix bug:crapped or attached views may not be recycled. isScrap:false isAttached:true
                mCurrentCounter = 0;
                connecting(1);
            }
        });

        //是否禁用自动加载更多功能,false为禁用, 默认开启自动加载更多功能
        list_r.setLoadMoreEnabled(true);

        list_r.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {

                if (mCurrentCounter < TOTAL_COUNTER) {
                    // loading more
                    mCurrentpage =mCurrentpage+1;
                    connecting(mCurrentpage);
                } else {
                    //the end
                    list_r.setNoMore(true);
                }
            }
        });

        list_r.setLScrollListener(new LRecyclerView.LScrollListener() {

            @Override
            public void onScrollUp() {
            }

            @Override
            public void onScrollDown() {
            }

            @Override
            public void onScrolled(int distanceX, int distanceY) {
            }

            @Override
            public void onScrollStateChanged(int state) {

            }

        });

        //设置头部加载颜色
        list_r.setHeaderViewColor(R.color.colorAccent, R.color.colorPrimary ,android.R.color.white);
        //设置底部加载颜色
        list_r.setFooterViewColor(R.color.colorAccent, R.color.colorPrimary ,android.R.color.white);
        //设置底部加载文字提示
        list_r.setFooterViewHint("拼命加载中","已经全部为你呈现了","网络不给力啊，点击再试一次吧");

        list_r.refresh();

        //子条目的点击事件
        mLRecyclerViewAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (mDataAdapter.getDataList().size() > position) {
                    LogUtils.e(mPassengers.size()+"mOrder总数");
                    LogUtils.e(mDataAdapter.getDataList().size()+"总数");
                    LogUtils.e(position+"当前位置");
                    Intent intent = new Intent(instance,PassengerDetActivity.class);
                    intent.putExtra("id",mPassengerss.get(position).getId());
                    intent.putExtra("type",mPassengerss.get(position).getPassType());
                    intent.putExtra("name",mPassengerss.get(position).getName());
                    intent.putExtra("idcode",mPassengerss.get(position).getIden());
                    intent.putExtra("address",mPassengerss.get(position).getAddress());
                    intent.putExtra("sex",mPassengerss.get(position).getSex());
                    intent.putExtra("idtype",mPassengerss.get(position).getIdenType());
                    intent.putExtra("dt_id",mPassengerss.get(position).getDt_id());
                    intent.putExtra("pic",mPassengerss.get(position).getPic());
                    startActivity(intent);
                }

            }

        });

    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
    public String  getIntentData(){
        Intent intent = getIntent();
        String nameType = intent.getStringExtra("nameType");
        String name = intent.getStringExtra("name");
        String idenType = intent.getStringExtra("idenType");
        String iden = intent.getStringExtra("iden");
        String tel = intent.getStringExtra("tel");
        String date = intent.getStringExtra("date");
        LogUtils.e("旅客参数"+nameType+name+idenType+iden+tel+date);
        return "?type="+nameType+
                "&name="+name+
                "&idtype="+idenType+
                "&idcode="+iden+
                "&tetphone="+tel+
                "&ltime="+date;
    }


    List<Passenger> mPassengers;
    private List<Passenger> mPassengerss= new ArrayList<>();
    private void connecting(int p) {

        LogUtils.e(HttpUrlUtils.getHttpUrl().passengerList()+getIntentData()+"&access_token="+ SPUtils.get(instance,"access_token",""));
        OkHttpUtils
                .get()
                .url(HttpUrlUtils.getHttpUrl().passengerList()+getIntentData()+"&access_token="+ SPUtils.get(instance,"access_token","")+"&p="+p)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        Toast.makeText(instance,e.toString(),Toast.LENGTH_SHORT).show();
                        mHandler.sendEmptyMessage(-3);
                    }

                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onResponse(String s, int i) {

                        try{
                            mPassengers = new ArrayList<>();
                            Map<String, Object> map = GsonUtil.JsonToMap(s);

                            if (map.get("state").toString().equals("1")) {
                                mHandler.sendEmptyMessage(-3);
                                Toast.makeText(instance,map.get("mess").toString(),Toast.LENGTH_SHORT).show();
                                return;
                            } else if (map.get("state").toString().equals("0")) {
                                if (!map.get("count").toString().equals("0")){
                                    list_r.setBackgroundColor(getResources().getColor(R.color.white));
                                    mHandler.sendEmptyMessage(-1);
                                    String pk = map.get("pk").toString();
                                    String ppp = "";
                                    String img = map.get("img").toString();
                                    if (pk.equals("dt_id")){
                                        ppp = "1001";
                                    }else if (pk.equals("ft_id")){
                                        ppp = "1002";
                                    }else if (pk.equals("mt_id")){
                                        ppp = "1003";
                                    }
                                    List<Map<String, Object>> data = GsonUtil.GsonToListMaps(GsonUtil.GsonString(map.get("table")));//参数[{},{}]
                                    for (int j = 0; j < data.size(); j++) {
                                        Passenger passenger = new Passenger();
                                        passenger.setId(NullUtil.getString(data.get(j).get("ccode")));//ID
                                        passenger.setPic(HttpUrlUtils.getHttpUrl().picInDel()+"/"+ppp+"/"+NullUtil.getString(data.get(j).get(pk))
                                                +"/"+NullUtil.getString(img)
                                                +"?access_token="+ SPUtils.get(instance,"access_token",""));//头像
                                        LogUtils.e(HttpUrlUtils.getHttpUrl().picInDel()+"/"+ppp+"/"+NullUtil.getString(data.get(j).get(pk))
                                                +"/"+NullUtil.getString(img)
                                                +"?access_token="+ SPUtils.get(instance,"access_token",""));
                                        passenger.setName(NullUtil.getString(data.get(j).get("name")));//姓名
                                        passenger.setSex(NullUtil.getString(data.get(j).get("sex")));//性别
                                        passenger.setIden(NullUtil.getString(data.get(j).get("idcode")));//身份证
                                        passenger.setAddress(NullUtil.getString(data.get(j).get("address")));//户籍地址
                                        passenger.setIdenType(IdenTypeUtils.getIdenType(NullUtil.getString(data.get(j).get("idtype"))));//证件类型
                                        passenger.setPassType(NullUtil.getString(data.get(j).get("type")));//旅客类型
                                        passenger.setDt_id(NullUtil.getString(data.get(j).get("dt_id")));//头像 dt_id
                                        mPassengers.add(passenger);
                                        mPassengerss.add(passenger);
                                    }
                                    String count = map.get("count").toString();
                                    String  num = map.get("num").toString();
                                    TOTAL_COUNTER = Integer.valueOf(count).intValue();
                                    REQUEST_COUNT = Integer.valueOf(num).intValue();
                                    txt_size.setText("共查询到"+count+"条数据");
                                }else{
                                    mHandler.sendEmptyMessage(-3);
                                    txt_size.setVisibility(View.GONE);
                                }



                            } else if (map.get("state").toString().equals("19")) {
                                mHandler.sendEmptyMessage(-3);
                                //响应失败
                                Toast.makeText(instance, map.get("mess").toString(), Toast.LENGTH_SHORT).show();
                            } else if (map.get("state").toString().equals("10")) {
                                mHandler.sendEmptyMessage(-3);
                                //响应失败
                                Toast.makeText(instance, map.get("mess").toString(), Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(instance,LoginActivity.class));
                                finish();
                            }
                        }catch (Exception e){
                            mHandler.sendEmptyMessage(-3);
                            Toast.makeText(instance,e.toString(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    public void onBackward(View view){
        this.finish();
    }

    private void notifyDataSetChanged() {
        mLRecyclerViewAdapter.notifyDataSetChanged();
    }

    private void addItems(ArrayList<Passenger> list) {

        mDataAdapter.addAll(list);
        mCurrentCounter += list.size();

    }

    private int size;

    private class PreviewHandler extends Handler {

        private WeakReference<PassengerListActivity> ref;

        PreviewHandler(PassengerListActivity activity) {
            ref = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final PassengerListActivity activity = ref.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }
            switch (msg.what) {

                case -1:

                    int currentSize = activity.mDataAdapter.getItemCount();

                    //模拟组装15个数据
                    ArrayList<Passenger> newList = new ArrayList<>();
                    for (int i = 0; i < mPassengers.size(); i++) {
                        if (newList.size() + currentSize >= TOTAL_COUNTER) {
                            break;
                        }

                        Passenger item = new Passenger();
                        item.setId(mPassengers.get(i).getId());
                        item.setName(mPassengers.get(i).getName());
                        item.setPic(mPassengers.get(i).getPic());
                        item.setSex(mPassengers.get(i).getSex());
                        item.setIden(mPassengers.get(i).getIden());
                        item.setAddress(mPassengers.get(i).getAddress());
                        item.setIdenType(mPassengers.get(i).getIdenType());
                        newList.add(item);
                    }

                    activity.addItems(newList);

                    activity.list_r.refreshComplete(REQUEST_COUNT);

                    break;
                case -3:
                    activity.list_r.refreshComplete(REQUEST_COUNT);
                    activity.notifyDataSetChanged();
                    activity.list_r.setOnNetWorkErrorListener(new OnNetWorkErrorListener() {
                        @Override
                        public void reload() {
//                            connecting(2);
                        }
                    });

                    break;
                default:
                    break;
            }
        }


    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_refresh, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else if (item.getItemId() == R.id.menu_refresh) {
            list_r.forceToRefresh();
        }
        return true;
    }



}
