package com.xaqianbai.QBHotelSecurutyGovernor.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
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
import com.xaqianbai.QBHotelSecurutyGovernor.Activity.RLview.OrderAdapter;
import com.xaqianbai.QBHotelSecurutyGovernor.Entity.Order;
import com.xaqianbai.QBHotelSecurutyGovernor.R;
import com.xaqianbai.QBHotelSecurutyGovernor.Utils.ConditionUtil;
import com.xaqianbai.QBHotelSecurutyGovernor.Utils.DateUtil;
import com.xaqianbai.QBHotelSecurutyGovernor.Utils.GsonUtil;
import com.xaqianbai.QBHotelSecurutyGovernor.Utils.HttpUrlUtils;
import com.xaqianbai.QBHotelSecurutyGovernor.Utils.IdenTypeUtils;
import com.xaqianbai.QBHotelSecurutyGovernor.Utils.LogUtils;
import com.xaqianbai.QBHotelSecurutyGovernor.Utils.NullUtil;
import com.xaqianbai.QBHotelSecurutyGovernor.Utils.SPUtils;
import com.xaqianbai.QBHotelSecurutyGovernor.Utils.StatuBarUtil;
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

public class OrderListActivity extends BaseActivityNew {

    private Unbinder unbinder;
    private OrderListActivity instance;
    @BindView(R.id.tv_title)
    TextView title;
    @BindView(R.id.layout_titlebar)
    FrameLayout titlebar;
    @BindView(R.id.recycler_view)
    LRecyclerView list_r;
    @BindView(R.id.recycler_text)
    TextView txt_size;
    /**服务器端一共多少条数据*/
    private int TOTAL_COUNTER;//如果服务器没有返回总数据或者总页数，这里设置为最大值比如10000，什么时候没有数据了根据接口返回判断

    /**每一页展示多少条数据*/
    private int REQUEST_COUNT;

    /**已经获取到多少条数据了*/
    private static int mCurrentCounter = 0;
    private  int mCurrentpage = 1;


    private OrderAdapter mDataAdapter = null;

    private OrderListActivity.PreviewHandler mHandler = new OrderListActivity.PreviewHandler(this);
    private LRecyclerViewAdapter mLRecyclerViewAdapter = null;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void initViews() throws Exception {
        setContentView(R.layout.activity_recyclerview_list);
        instance = this;
        unbinder = ButterKnife.bind(instance);
        StatuBarUtil.setStatuBarLightMode(instance,getResources().getColor(R.color.white));//修改状态栏字体颜色为黑色
        titlebar.setBackgroundColor(getResources().getColor(R.color.white));
        title.setText("订单列表");


        mDataAdapter = new OrderAdapter(instance);
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
                    Intent intent = new Intent(instance,PassengerDetActivity.class);
                    LogUtils.e(mOrders.size()+"mOrder总数");
                    LogUtils.e(mDataAdapter.getDataList().size()+"总数");
                    LogUtils.e(position+"当前位置");
                    intent.putExtra("id",mOrders.get(position).getId());
                    intent.putExtra("type",mOrders.get(position).getPass_type());
                    intent.putExtra("name",mOrders.get(position).getPassenger());
                    intent.putExtra("idcode",mOrders.get(position).getIden());
                    intent.putExtra("address",mOrders.get(position).getAddress());
                    intent.putExtra("sex",mOrders.get(position).getSex());
                    intent.putExtra("idtype", mOrders.get(position).getIden_type());
                    intent.putExtra("dt_id", mOrders.get(position).getDt_id());
                    intent.putExtra("pic", mOrders.get(position).getPic());
                    startActivity(intent);
                }

            }

        });

    }

    @Override
    public void initData() throws Exception {

    }

    @Override
    public void addListener() throws Exception {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    String mOrg,mName,mPerName,mStart,mEnd,mType;
    public String  getIntentData(){

        HashMap map = new HashMap();
        Intent intent = getIntent();
        mType = intent.getStringExtra("type");
        mOrg = intent.getStringExtra("org");
        mName = intent.getStringExtra("hName");
        mPerName = intent.getStringExtra("perName");
        mStart = NullUtil.getString(intent.getStringExtra("start"));
        mEnd = NullUtil.getString(intent.getStringExtra("end"));

        map.put("\"psorgan\"", "\""+mOrg+"\"");//管辖机构
        map.put("\"hname\"", "\""+mName+"\"");//酒店名称
        map.put("\"name\"", "\""+mPerName+"\"");//旅客姓名
        if (!mStart.equals("")&&mStart !=null&&!mEnd.equals("")&&mEnd !=null) {
            map.put("\"ltime\"", "[[\">=\"," + DateUtil.data(mStart) + "],[\"<=\"," + DateUtil.data(mEnd) + "]]");//时间
        }
        return "?condition="+ ConditionUtil.getConditionString(map)+"&type="+mType;
    }


    private List<Order> mOrder;
    private List<Order> mOrders= new ArrayList<>();
    private void connecting(int p) {
//        QBHttp.get();

        LogUtils.e(HttpUrlUtils.getHttpUrl().OrderList()+getIntentData()+"&access_token="+ SPUtils.get(instance,"access_token",""));
        OkHttpUtils
                .get()
                .url(HttpUrlUtils.getHttpUrl().OrderList()+getIntentData()+"&access_token="+ SPUtils.get(instance,"access_token","")+"&p="+p)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        Toast.makeText(instance,e.toString(),Toast.LENGTH_SHORT).show();
                        mHandler.sendEmptyMessage(-3);
                    }

                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onResponse(String s, int i) {
LogUtils.e(s);
                        try{
                            mOrder = new ArrayList<>();

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
                                String ppp = "";
                                    if (pk.equals("dt_id")){
                                        ppp = "1001";
                                    }else if (pk.equals("ft_id")){
                                        ppp = "1002";
                                    }else if (pk.equals("mt_id")){
                                        ppp = "1003";
                                    }
                                List<Map<String, Object>> data = GsonUtil.GsonToListMaps(GsonUtil.GsonString(map.get("table")));//参数[{},{}]
                                for (int j = 0; j < data.size(); j++) {
                                    Order order = new Order();

                                    order.setId(NullUtil.getString(data.get(j).get("ccode")));//ID
                                    order.setPic(HttpUrlUtils.getHttpUrl().picInDel()+"/"+ppp+"/"+NullUtil.getString(data.get(j).get(pk))
                                            +"/"+NullUtil.getString(img)
                                            +"?access_token="+ SPUtils.get(instance,"access_token",""));//头像
                                    order.setPassenger(NullUtil.getString(data.get(j).get("name")));//旅客
                                    order.setSex(NullUtil.getString(data.get(j).get("sex")));//性别
                                    order.setAddress(NullUtil.getString(data.get(j).get("address")));//地址
                                    order.setIden_type(IdenTypeUtils.getIdenType(NullUtil.getString(data.get(j).get("idtype"))));//证件类型
                                    order.setIden(NullUtil.getString(data.get(j).get("idcode")));//证件号
                                    order.setDate(NullUtil.getString(data.get(j).get("ltime")));//时间
                                    order.setPass_type(NullUtil.getString(data.get(j).get("type")));//旅客类型
                                    order.setDt_id(NullUtil.getString(data.get(j).get(pk)));//旅客dt_id
                                    LogUtils.e(HttpUrlUtils.getHttpUrl().picInDel()+"/"+ppp+"/"+NullUtil.getString(data.get(j).get(pk))
                                            +"/"+NullUtil.getString(img)
                                            +"?access_token="+ SPUtils.get(instance,"access_token",""));
                                    mOrder.add(order);
                                    mOrders.add(order);
                                }
                                    String count = map.get("count").toString();
                                    String  num = map.get("num").toString();
                                    TOTAL_COUNTER = Integer.valueOf(count).intValue();
                                    REQUEST_COUNT = Integer.valueOf(num).intValue();
                                    txt_size.setText("共查询到"+count+"条数据");
                                } else if (map.get("count").toString().equals("0")){
                                    mHandler.sendEmptyMessage(-3);
//                                    Toast.makeText(instance, "未查询到有效数据", Toast.LENGTH_SHORT).show();
                                    txt_size.setVisibility(View.GONE);
                                }

                            } else if (map.get("state").toString().equals("19")) {
                                mHandler.sendEmptyMessage(-3);
                                //响应失败
//                                list_r.setBackground(instance.getDrawable(R.mipmap.nodata));
                                Toast.makeText(instance, map.get("mess").toString(), Toast.LENGTH_SHORT).show();
                            }else if (map.get("state").toString().equals("10")) {
                                mHandler.sendEmptyMessage(-3);
                                //响应失败
                                Toast.makeText(instance, map.get("mess").toString(), Toast.LENGTH_SHORT).show();
                                resetSprfMain();
                                startActivity(new Intent(instance,LoginActivity.class));
                                finish();
                            }else {
                                mHandler.sendEmptyMessage(-3);
                                Toast.makeText(instance, map.get("mess").toString(), Toast.LENGTH_SHORT).show();
                            }
                        }catch (Exception e){
                            mHandler.sendEmptyMessage(-3);
                            Toast.makeText(instance,e.toString(),Toast.LENGTH_SHORT).show();
                        }

                    }
                });

    }


    SharedPreferences sprfMain;
    SharedPreferences.Editor editorMain;
    public void resetSprfMain(){
        sprfMain= PreferenceManager.getDefaultSharedPreferences(instance);
        editorMain=sprfMain.edit();
        editorMain.putBoolean("main",false);
        editorMain.commit();
    }
    private void notifyDataSetChanged() {
        mLRecyclerViewAdapter.notifyDataSetChanged();
    }

    private void addItems(ArrayList<Order> list) {

        mDataAdapter.addAll(list);
        mCurrentCounter += list.size();

    }

    private class PreviewHandler extends Handler {

        private WeakReference<OrderListActivity> ref;

        PreviewHandler(OrderListActivity activity) {
            ref = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final OrderListActivity activity = ref.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }
            switch (msg.what) {

                case -1:

                    int currentSize = activity.mDataAdapter.getItemCount();

                    //模拟组装15个数据
                    ArrayList<Order> newList = new ArrayList<>();
                    for (int i = 0; i < mOrder.size(); i++) {
                        if (newList.size() + currentSize >= TOTAL_COUNTER) {
                            break;
                        }

                        Order item = new Order();
                        item.setId(mOrder.get(i).getId());
                        item.setPassenger(mOrder.get(i).getPassenger());
                        item.setPic(mOrder.get(i).getPic());
                        item.setSex(mOrder.get(i).getSex());
                        item.setDate(mOrder.get(i).getDate());
                        item.setIden(mOrder.get(i).getIden());
                        item.setAddress(mOrder.get(i).getAddress());
                        item.setIden_type(mOrder.get(i).getIden_type());
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




    private void connectingToken() {

        LogUtils.e( HttpUrlUtils.getHttpUrl().getToken()+"/"+SPUtils.get(instance,"refresh_token",""));
        OkHttpUtils
                .get()
                .url(HttpUrlUtils.getHttpUrl().getToken()+"/"+ SPUtils.get(instance,"refresh_token",""))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {

                    }

                    @Override
                    public void onResponse(String s, int i) {

                        try {
                            Map<String, Object> data = GsonUtil.JsonToMap(s);
                            if (data.get("state").toString().equals("1")) {
                                Toast.makeText(instance,data.get("mess").toString(),Toast.LENGTH_LONG).show();
                                return;
                            } else if (data.get("state").toString().equals("0")) {
                                SPUtils.put(instance, "access_token", NullUtil.getString(data.get("access_token")));//access_token
                                SPUtils.put(instance, "refresh_token", NullUtil.getString(data.get("refresh_token")));//refresh_token
                                connecting(1);
                            } else if (data.get("state").toString().equals("0")) {
                                //响应失败
                                Toast.makeText(instance, data.get("mess").toString(), Toast.LENGTH_SHORT).show();
                            }else if (data.get("state").toString().equals("10")) {
                                //响应失败
                                Toast.makeText(instance, data.get("mess").toString(), Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(instance,LoginActivity.class));
                                finish();
                            }
                        }catch (Exception e){
                            Toast.makeText(instance,e.toString(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }










}
