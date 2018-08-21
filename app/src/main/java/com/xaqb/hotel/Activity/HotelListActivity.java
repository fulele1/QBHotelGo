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
import com.xaqb.hotel.Activity.RLview.HotelAdapter;
import com.xaqb.hotel.Activity.RLview.PassengerAdapter;
import com.xaqb.hotel.Entity.Hotel;
import com.xaqb.hotel.Entity.Passenger;
import com.xaqb.hotel.Entity.Staff;
import com.xaqb.hotel.R;
import com.xaqb.hotel.Utils.ConditionUtil;
import com.xaqb.hotel.Utils.DateUtil;
import com.xaqb.hotel.Utils.GsonUtil;
import com.xaqb.hotel.Utils.HttpUrlUtils;
import com.xaqb.hotel.Utils.LogUtils;
import com.xaqb.hotel.Utils.NullUtil;
import com.xaqb.hotel.Utils.SPUtils;
import com.xaqb.hotel.Utils.StatuBarUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.Call;

public class HotelListActivity extends AppCompatActivity{

    HotelListActivity instance;
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


    private HotelAdapter mDataAdapter = null;

    private HotelListActivity.PreviewHandler mHandler = new HotelListActivity.PreviewHandler(this);
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
        title.setText("酒店列表");

        mDataAdapter = new HotelAdapter(instance);
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
                    Intent intent = new Intent(instance,HotelDetilActivity.class);
                    intent.putExtra("id",mHotels.get(position).getId());
                    intent.putExtra("pic",mHotels.get(position).getPic());
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

    String mOrg,mName,mStart,mEnd;
    public String  getIntentData(){
        HashMap map = new HashMap();
        Intent intent = getIntent();
        mOrg = intent.getStringExtra("org");
        mName = intent.getStringExtra("name");
        mStart = intent.getStringExtra("start");
        mEnd = intent.getStringExtra("end");
        map.put("\"station\"", mOrg);//管辖机构
        map.put("\"hname\"", mName);//场站名称
        if (!mStart.equals("")&&mStart !=null&&!mEnd.equals("")&&mEnd !=null) {
            map.put("\"inputtime\"", "[[\">=\"," + DateUtil.data(mStart) + "],[\"<=\"," + DateUtil.data(mEnd) + "]]");//时间
        }
        return "?condition="+ConditionUtil.getConditionString(map);
    }


    public String getData() {
        HashMap map = new HashMap();

        Intent intent = instance.getIntent();
        String so_code = intent.getStringExtra("so_code");
        String entrepot = intent.getStringExtra("entrepot");
        String store = intent.getStringExtra("store");
        String createtime = intent.getStringExtra("createtime");

        LogUtils.e("so_code"+so_code);
        LogUtils.e("entrepot"+entrepot);
        LogUtils.e("store"+store);
        LogUtils.e("createtime"+ DateUtil.data(createtime));

        map.put("\"so_code\"", so_code);//管辖机构
        map.put("\"entrepot\"", entrepot);//场站名称
        map.put("\"store\"", store);//门店
        if (!createtime.equals("")&&createtime !=null){

            map.put("\"createtime\"", "[\">=\","+ DateUtil.data(createtime)+"]");//时间
        }
        LogUtils.e("condition"+ ConditionUtil.getConditionString(map));
        return "&condition="+ConditionUtil.getConditionString(map);
    }





    List<Hotel> mHotel;
    private List<Hotel> mHotels= new ArrayList<>();
    private void connecting(int p) {

        LogUtils.e(HttpUrlUtils.getHttpUrl().HotelList()+getIntentData()+"&access_token="+ SPUtils.get(instance,"access_token","")+"&p="+p);
        OkHttpUtils
                .get()
                .url(HttpUrlUtils.getHttpUrl().HotelList()+getIntentData()+"&access_token="+ SPUtils.get(instance,"access_token","")+"&p="+p)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        Toast.makeText(instance,e.toString(),Toast.LENGTH_SHORT).show();
                        mHandler.sendEmptyMessage(-3);
                    }

                    @Override
                    public void onResponse(String s, int i) {

                        try{
                            mHotel = new ArrayList<>();
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
                                    String img = map.get("img").toString();
                                    List<Map<String, Object>> data = GsonUtil.GsonToListMaps(GsonUtil.GsonString(map.get("table")));//参数[{},{}]
                                    for (int j = 0; j < data.size(); j++) {
                                        Hotel hotel = new Hotel();
                                        hotel.setId(NullUtil.getString(data.get(j).get("ho_id")));//ID
                                        hotel.setName(NullUtil.getString(data.get(j).get("hname")));//姓名
                                        hotel.setStars(NullUtil.getString(data.get(j).get("stars")));//星级
                                        hotel.setManager(NullUtil.getString(data.get(j).get("principal")));//旅馆负责人
                                        hotel.setAddress(NullUtil.getString(data.get(j).get("haddress")));//地址
                                        hotel.setTel(NullUtil.getString(data.get(j).get("telphone")));//联系电话
                                        hotel.setPic(HttpUrlUtils.getHttpUrl().picInHotel()+NullUtil.getString(data.get(j).get(pk))
                                                +"/"+NullUtil.getString(img)
                                                +"?access_token="+ SPUtils.get(instance,"access_token",""));//酒店全景图
                                        mHotel.add(hotel);
                                        mHotels.add(hotel);
                                    }
                                    String count = map.get("count").toString();
                                    String  num = map.get("num").toString();
                                    TOTAL_COUNTER = Integer.valueOf(count).intValue();
                                    REQUEST_COUNT = Integer.valueOf(num).intValue();
                                    txt_size.setText("共查询到"+count+"条数据");
                                }else {
                                    mHandler.sendEmptyMessage(-3);
                                    txt_size.setVisibility(View.GONE);
                                }


                            } else if (map.get("state").toString().equals("19")) {
                                mHandler.sendEmptyMessage(-3);
                                txt_size.setVisibility(View.GONE);
                            }else if (map.get("state").toString().equals("10")) {
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

    private void addItems(ArrayList<Hotel> list) {

        mDataAdapter.addAll(list);
        mCurrentCounter += list.size();

    }

    private class PreviewHandler extends Handler {

        private WeakReference<HotelListActivity> ref;

        PreviewHandler(HotelListActivity activity) {
            ref = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final HotelListActivity activity = ref.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }
            switch (msg.what) {

                case -1:

                    int currentSize = activity.mDataAdapter.getItemCount();

                    //模拟组装15个数据
                    ArrayList<Hotel> newList = new ArrayList<>();
                    for (int i = 0; i < mHotel.size(); i++) {
                        if (newList.size() + currentSize >= TOTAL_COUNTER) {
                            break;
                        }

                        Hotel item = new Hotel();
                        item.setId(i+"");
                        item.setName(mHotel.get(i).getName());
                        item.setStars(mHotel.get(i).getStars());
                        item.setManager(mHotel.get(i).getManager());
                        item.setPic(mHotel.get(i).getPic());
                        item.setAddress(mHotel.get(i).getAddress());
                        item.setTel(mHotel.get(i).getTel());
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
