package com.xaqb.hotel.Activity;

import android.content.Intent;
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
import com.xaqb.hotel.Activity.RLview.StaffAdapter;
import com.xaqb.hotel.Entity.Staff;
import com.xaqb.hotel.R;
import com.xaqb.hotel.Utils.ConditionUtil;
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

public class StaffListActivity extends AppCompatActivity {
    @BindView(R.id.tv_title)
    TextView title;
    @BindView(R.id.layout_titlebar)
    FrameLayout titlebar;
    @BindView(R.id.recycler_view)
    LRecyclerView list_r;
    @BindView(R.id.recycler_text)
    TextView txt_size;
    /**
     * 服务器端一共多少条数据
     */
    private int TOTAL_COUNTER;//如果服务器没有返回总数据或者总页数，这里设置为最大值比如10000，什么时候没有数据了根据接口返回判断

    /**
     * 每一页展示多少条数据
     */
    private int REQUEST_COUNT;

    /**
     * 已经获取到多少条数据了
     */
    private static int mCurrentCounter = 0;
    private int mCurrentpage = 1;


    private StaffAdapter mDataAdapter = null;

    private StaffListActivity.PreviewHandler mHandler = new StaffListActivity.PreviewHandler(this);
    private LRecyclerViewAdapter mLRecyclerViewAdapter = null;

    private Unbinder unbinder;
    private StaffListActivity instance;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recyclerview_list);
        instance = this;
        unbinder = ButterKnife.bind(instance);
        StatuBarUtil.setStatuBarLightMode(instance, getResources().getColor(R.color.white));//修改状态栏字体颜色为白色
        titlebar.setBackgroundColor(getResources().getColor(R.color.white));
        title.setText("从业人员查询");
        mDataAdapter = new StaffAdapter(instance);
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
        final View header = LayoutInflater.from(this).inflate(R.layout.sample_header, (ViewGroup) findViewById(android.R.id.content), false);
        mLRecyclerViewAdapter.addHeaderView(header);

        list_r.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {

                mDataAdapter.clear();
                mLRecyclerViewAdapter.notifyDataSetChanged();//fix bug:crapped or attached views may not be recycled. isScrap:false isAttached:true
                mCurrentCounter = 0;
                connecting(mCurrentpage);
            }
        });

        //是否禁用自动加载更多功能,false为禁用, 默认开启自动加载更多功能
        list_r.setLoadMoreEnabled(true);

        list_r.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {

                if (mCurrentCounter < TOTAL_COUNTER) {
                    // loading more
                    mCurrentpage = mCurrentpage + 1;
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
        list_r.setHeaderViewColor(R.color.colorAccent, R.color.colorPrimary, android.R.color.white);
        //设置底部加载颜色
        list_r.setFooterViewColor(R.color.colorAccent, R.color.colorPrimary, android.R.color.white);
        //设置底部加载文字提示
        list_r.setFooterViewHint("拼命加载中", "已经全部为你呈现了", "网络不给力啊，点击再试一次吧");

        list_r.refresh();

        //子条目的点击事件
        mLRecyclerViewAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (mDataAdapter.getDataList().size() > position) {
                    Intent intent = new Intent(instance, StaffDetailActivity.class);
                    intent.putExtra("id", mStaffss.get(position).getId());
                    intent.putExtra("pic", mStaffss.get(position).getPic());
                    startActivity(intent);
                }

            }

        });

    }

    private List<Staff> mStaffs;
    private List<Staff> mStaffss = new ArrayList<>();

    private void connecting(int p) {

        LogUtils.e(HttpUrlUtils.getHttpUrl().getStaffList() + getIntentData() + "&access_token=" + SPUtils.get(instance, "access_token", "") + "&p=" + p);
        OkHttpUtils
                .get()
                .url(HttpUrlUtils.getHttpUrl().getStaffList() + getIntentData() + "&access_token=" + SPUtils.get(instance, "access_token", "") + "&p=" + p)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        Toast.makeText(instance, e.toString(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String s, int i) {

                        try {
                            mStaffs = new ArrayList<>();
                            Map<String, Object> map = GsonUtil.JsonToMap(s);

                            if (map.get("state").toString().equals("1")) {
                                mHandler.sendEmptyMessage(-3);
                                Toast.makeText(instance, map.get("mess").toString(), Toast.LENGTH_SHORT).show();
                                return;
                            } else if (map.get("state").toString().equals("0")) {
                                if (!map.get("count").toString().equals("0")) {
                                    list_r.setBackgroundColor(getResources().getColor(R.color.white));
                                    mHandler.sendEmptyMessage(-1);
                                    String pk = map.get("pk").toString();
                                    String img = map.get("img").toString();
                                    List<Map<String, Object>> data = GsonUtil.GsonToListMaps(GsonUtil.GsonString(map.get("table")));//参数[{},{}]
                                    for (int j = 0; j < data.size(); j++) {
                                        Staff staff = new Staff();
                                        staff.setName(NullUtil.getString(data.get(j).get("xm")));//姓名
                                        staff.setHotel(NullUtil.getString(data.get(j).get("hname")));//酒店
                                        staff.setPic(HttpUrlUtils.getHttpUrl().picInPer() + NullUtil.getString(data.get(j).get(pk))
                                                + "/" + NullUtil.getString(img)
                                                + "?access_token=" + SPUtils.get(instance, "access_token", ""));//图片

                                        LogUtils.e(HttpUrlUtils.getHttpUrl().picInPer() + NullUtil.getString(data.get(j).get(pk))
                                                + "/" + NullUtil.getString(img)
                                                + "?access_token=" + SPUtils.get(instance, "access_token", ""));

                                        staff.setTel(NullUtil.getString(data.get(j).get("lxfs1")));//电话
                                        staff.setIden(NullUtil.getString(data.get(j).get("zjhm")));//身份证
                                        staff.setSex(NullUtil.getString(data.get(j).get("xb")));//性别
                                        staff.setId(NullUtil.getString(data.get(j).get("em_id")));//ID
                                        mStaffs.add(staff);
                                        mStaffss.add(staff);

                                    }
                                    String count = map.get("count").toString();
                                    String num = map.get("num").toString();
                                    TOTAL_COUNTER = Integer.valueOf(count).intValue();
                                    REQUEST_COUNT = Integer.valueOf(num).intValue();
                                    txt_size.setText("共查询到" + count + "条数据");
                                } else {
                                    txt_size.setVisibility(View.GONE);
                                    mHandler.sendEmptyMessage(-3);
                                }
                            } else if (map.get("state").toString().equals("19")) {
                                mHandler.sendEmptyMessage(-3);
                                txt_size.setVisibility(View.GONE);
                            } else if (map.get("state").toString().equals("10")) {
                                mHandler.sendEmptyMessage(-3);
                                //响应失败
                                Toast.makeText(instance, map.get("mess").toString(), Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(instance, LoginActivity.class));
                                finish();
                            }
                        } catch (Exception e) {
                            mHandler.sendEmptyMessage(-3);
                            Toast.makeText(instance, e.toString(), Toast.LENGTH_SHORT).show();
                        }


                    }
                });

    }


    public void onBackward(View view) {
        this.finish();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    private String hotel;
    private String name;
    private String tel;
    private String iden;
    private String org;

    public String getIntentData() {
        HashMap map = new HashMap();
        Intent i = getIntent();
        hotel = i.getStringExtra("hotel");
        name = i.getStringExtra("name");
        tel = i.getStringExtra("tel");
        iden = i.getStringExtra("iden");
        org = i.getStringExtra("org");
        map.put("\"psorgan\"", "\"" + org + "\"");//管辖机构
        map.put("\"hname\"", "\"" + hotel + "\"");//酒店名称
        map.put("\"xm\"", "\"" + name + "\"");//姓名
        map.put("\"lxfs1\"", "\"" + tel + "\"");//电话号码
        map.put("\"zjhm\"", "\"" + iden + "\"");//证件号码

        return "?condition=" + ConditionUtil.getConditionString(map);
    }


    private void notifyDataSetChanged() {
        mLRecyclerViewAdapter.notifyDataSetChanged();
    }

    private void addItems(ArrayList<Staff> list) {

        mDataAdapter.addAll(list);
        mCurrentCounter += list.size();

    }

    private class PreviewHandler extends Handler {

        private WeakReference<StaffListActivity> ref;

        PreviewHandler(StaffListActivity activity) {
            ref = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final StaffListActivity activity = ref.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }
            switch (msg.what) {

                case -1:

                    int currentSize = activity.mDataAdapter.getItemCount();

                    //模拟组装15个数据
                    ArrayList<Staff> newList = new ArrayList<>();
                    for (int i = 0; i < mStaffs.size(); i++) {
                        if (newList.size() + currentSize >= TOTAL_COUNTER) {
                            break;
                        }
                        Staff item = new Staff();
                        item.setId(mStaffs.get(i).getId());
                        item.setName(mStaffs.get(i).getName());
                        item.setPic(mStaffs.get(i).getPic());
                        item.setTel(mStaffs.get(i).getTel());
                        item.setHotel(mStaffs.get(i).getHotel());
                        item.setIden(mStaffs.get(i).getIden());
                        item.setSex(mStaffs.get(i).getSex());
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
//                            connecting();
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
