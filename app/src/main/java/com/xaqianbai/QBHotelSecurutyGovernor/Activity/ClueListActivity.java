package com.xaqianbai.QBHotelSecurutyGovernor.Activity;


import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
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
import com.xaqianbai.QBHotelSecurutyGovernor.Activity.RLview.ClueAdapter;
import com.xaqianbai.QBHotelSecurutyGovernor.Entity.Clue;
import com.xaqianbai.QBHotelSecurutyGovernor.R;
import com.xaqianbai.QBHotelSecurutyGovernor.Utils.DateUtil;
import com.xaqianbai.QBHotelSecurutyGovernor.Utils.GsonUtil;
import com.xaqianbai.QBHotelSecurutyGovernor.Utils.HttpUrlUtils;
import com.xaqianbai.QBHotelSecurutyGovernor.Utils.LogUtils;
import com.xaqianbai.QBHotelSecurutyGovernor.Utils.NullUtil;
import com.xaqianbai.QBHotelSecurutyGovernor.Utils.SPUtils;
import com.xaqianbai.QBHotelSecurutyGovernor.Utils.StatuBarUtil;
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

public class ClueListActivity extends BaseActivityNew {


    private ClueListActivity instance;
    Unbinder unbinder;
    @BindView(R.id.tv_title)
    TextView title;
    @BindView(R.id.layout_titlebar)
    FrameLayout titlebar;
    @BindView(R.id.recycler_text)
    TextView txt_size;
    @BindView(R.id.recycler_view)
    LRecyclerView list_r;
    @BindView(R.id.empty_view)
    RelativeLayout empty_view;
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


    private ClueAdapter mDataAdapter = null;

    private ClueListActivity.PreviewHandler mHandler = new ClueListActivity.PreviewHandler(this);
    private LRecyclerViewAdapter mLRecyclerViewAdapter = null;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void initViews() throws Exception {
        setContentView(R.layout.activity_recyclerview_list);
        instance = this;
        unbinder = ButterKnife.bind(instance);
        StatuBarUtil.setStatuBarLightMode(instance, getResources().getColor(R.color.white));//修改状态栏字体颜色为黑色
        titlebar.setBackgroundColor(getResources().getColor(R.color.white));

        title.setText("线索信息");

        initRecycle();
        mCurrentpage = 1;
        initList();
    }

    /**
     * 初始化recycleview数据
     */
    public void initList() {

        //上拉刷新
        list_r.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                mClues = new ArrayList<>();
                LogUtils.e("midnaksdbuzdhoa niao sahdajhf");
                mDataAdapter.clear();
                mLRecyclerViewAdapter.notifyDataSetChanged();//fix bug:crapped or attached views may not be recycled. isScrap:false isAttached:true
                mCurrentCounter = 0;
                connecting(1);
            }
        });

        //是否禁用自动加载更多功能,false为禁用, 默认开启自动加载更多功能
        list_r.setLoadMoreEnabled(true);

        //下拉加载
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

        list_r.refresh();
    }


    /**
     * 初始化recycleview
     */
    private void initRecycle() {

        mDataAdapter = new ClueAdapter(instance);
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

        //设置头部加载颜色
        list_r.setHeaderViewColor(R.color.colorAccent, R.color.colorPrimary, android.R.color.white);
        //设置底部加载颜色
        list_r.setFooterViewColor(R.color.colorAccent, R.color.colorPrimary, android.R.color.white);
        //设置底部加载文字提示
        list_r.setFooterViewHint("拼命加载中", "已经全部为你呈现了", "网络不给力啊，点击再试一次吧");


    }

    @Override
    public void initData() throws Exception {

    }

    @Override
    public void addListener() throws Exception {

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }


    List<Clue> mClue;
    List<Clue> mClues;

    private void connecting(int p) {

        LogUtils.e(HttpUrlUtils.getHttpUrl().clueList() + "?access_token=" + SPUtils.get(instance, "access_token", ""));
        OkHttpUtils
                .get()
                .url(HttpUrlUtils.getHttpUrl().clueList() + "?access_token=" + SPUtils.get(instance, "access_token", "") + "&p=" + p)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        Toast.makeText(instance, e.toString(), Toast.LENGTH_SHORT).show();
                        mHandler.sendEmptyMessage(-3);
                    }

                    @Override
                    public void onResponse(String s, int i) {

                        try {
                            mClue = new ArrayList<>();
                            Map<String, Object> map = GsonUtil.JsonToMap(s);

                            LogUtils.e(map.toString());
                            if (map.get("state").toString().equals("1")) {
                                mHandler.sendEmptyMessage(-3);
                                Toast.makeText(instance, map.get("mess").toString(), Toast.LENGTH_SHORT).show();
                                return;
                            } else if (map.get("state").toString().equals("0")) {
                                if (!map.get("count").toString().equals("0")) {
                                    mHandler.sendEmptyMessage(-1);
                                    List<Map<String, Object>> data = GsonUtil.GsonToListMaps(GsonUtil.GsonString(map.get("table")));//参数[{},{}]
                                    for (int j = 0; j < data.size(); j++) {
                                        Clue clue = new Clue();
                                        clue.setId(NullUtil.getString(data.get(j).get("sc_id")));//ID
                                        String pk = NullUtil.getString(data.get(j).get("pk"));
                                        String sc_id = NullUtil.getString(data.get(j).get(pk));
                                        String img = NullUtil.getString(data.get(j).get("img"));
                                        LogUtils.e("头像" + HttpUrlUtils.getHttpUrl().picInclue() + sc_id + "/" + img
                                                + "?access_token=" + SPUtils.get(instance, "access_token", ""));

                                        clue.setPic(HttpUrlUtils.getHttpUrl().picInclue() + sc_id + "/" + img
                                                + "?access_token=" + SPUtils.get(instance, "access_token", ""));
                                        clue.setName(NullUtil.getString(data.get(j).get("people")));//姓名
                                        clue.setDate(NullUtil.getString(data.get(j).get("createtime")));//
                                        clue.setTel(NullUtil.getString(data.get(j).get("mp")));
                                        clue.setAddress(NullUtil.getString(data.get(j).get("address")));//地址
                                        clue.setGood(NullUtil.getString(data.get(j).get("goods")));//地址

                                        mClue.add(clue);
                                        mClues.add(clue);
                                    }


                                    String count = map.get("count").toString();
                                    String num = map.get("num").toString();
                                    TOTAL_COUNTER = Integer.valueOf(count).intValue();
                                    REQUEST_COUNT = Integer.valueOf(num).intValue();
                                    txt_size.setText("共查询到" + count + "条数据");


                                    //子条目的点击事件
                                    mLRecyclerViewAdapter.setOnItemClickListener(new OnItemClickListener() {
                                        @Override
                                        public void onItemClick(View view, int position) {
                                            if (mDataAdapter.getDataList().size() > position) {
                                                Intent i = new Intent(instance, ClueDelActivity.class);
                                                i.putExtra("id", mClues.get(position).getId());
                                                startActivity(i);
                                            }
                                        }

                                    });


                                    empty_view.setVisibility(View.GONE);
                                    list_r.setVisibility(View.VISIBLE);
                                } else {
                                    mHandler.sendEmptyMessage(-3);
                                    txt_size.setVisibility(View.GONE);
                                    list_r.setEmptyView(empty_view);
                                }


                            } else if (map.get("state").toString().equals("19")) {
                                //响应失败
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
                            txt_size.setVisibility(View.GONE);
                            Toast.makeText(instance, e.toString(), Toast.LENGTH_SHORT).show();
                        }


                    }
                });
    }


    String first = "";

    private void notifyDataSetChanged() {
        mLRecyclerViewAdapter.notifyDataSetChanged();
    }

    private void addItems(ArrayList<Clue> list) {

        mDataAdapter.addAll(list);
        mCurrentCounter += list.size();

    }


    private class PreviewHandler extends Handler {

        private WeakReference<ClueListActivity> ref;

        PreviewHandler(ClueListActivity activity) {
            ref = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final ClueListActivity activity = ref.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }
            switch (msg.what) {

                case -1:

                    int currentSize = activity.mDataAdapter.getItemCount();

                    //模拟组装15个数据
                    ArrayList<Clue> newList = new ArrayList<>();
                    for (int i = 0; i < mClue.size(); i++) {
                        if (newList.size() + currentSize >= TOTAL_COUNTER) {
                            break;
                        }

                        Clue item = new Clue();
                        item.setName(mClue.get(i).getName());
                        item.setPic(mClue.get(i).getPic());
                        item.setAddress(mClue.get(i).getAddress());
                        item.setTel(mClue.get(i).getTel());
                        item.setGood(mClue.get(i).getGood());
                        item.setDate(DateUtil.getDate(mClue.get(i).getDate()));
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
