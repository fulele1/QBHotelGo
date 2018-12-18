
package com.xaqianbai.QBHotelSecurutyGovernor.Activity;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
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
import com.github.jdsjlzx.interfaces.OnItemLongClickListener;
import com.github.jdsjlzx.interfaces.OnLoadMoreListener;
import com.github.jdsjlzx.interfaces.OnNetWorkErrorListener;
import com.github.jdsjlzx.interfaces.OnRefreshListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.github.jdsjlzx.recyclerview.ProgressStyle;
import com.xaqianbai.QBHotelSecurutyGovernor.Activity.RLview.CrimeAdapter;
import com.xaqianbai.QBHotelSecurutyGovernor.Activity.RLview.PunishmentAdapter;
import com.xaqianbai.QBHotelSecurutyGovernor.Entity.Crime;
import com.xaqianbai.QBHotelSecurutyGovernor.Entity.Punishment;
import com.xaqianbai.QBHotelSecurutyGovernor.R;
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

public class PunishmentListActivity extends BaseActivityNew {

    private PunishmentListActivity instance;
    Unbinder unbinder;
    @BindView(R.id.tv_title)
    TextView title;
    @BindView(R.id.layout_titlebar)
    FrameLayout titlebar;
    @BindView(R.id.recycler_text)
    TextView txt_size;
    @BindView(R.id.recycler_view)
    LRecyclerView list_r;
    @BindView(R.id.floatingActionButton)
    FloatingActionButton floatingActionButton;
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
    private int mCurrentpage;


    private PunishmentAdapter mDataAdapter = null;

    private PunishmentListActivity.PreviewHandler mHandler = new PunishmentListActivity.PreviewHandler(this);
    private LRecyclerViewAdapter mLRecyclerViewAdapter = null;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void initViews() throws Exception {
        setContentView(R.layout.activity_recyclerview_list);
        instance = this;
        unbinder = ButterKnife.bind(instance);
        StatuBarUtil.setStatuBarLightMode(instance, getResources().getColor(R.color.white));//修改状态栏字体颜色为黑色
        titlebar.setBackgroundColor(getResources().getColor(R.color.white));
        title.setText("处罚");

        floatingActionButton.setVisibility(View.VISIBLE);
        floatingActionButton.setOnClickListener(instance);

        writeConfig("addSuccess", "no");
        initRecycle();
        mCurrentpage = 1;
        initList();

    }


    @Override
    public void onResume() {
        super.onResume();
        if (readConfig("addSuccess").equals("yes")){
            mLogs = new ArrayList<>();
            mCurrentpage = 1;
            initList();
            writeConfig("addSuccess", "no");
        }
    }

    /**
     * 初始化recycleview
     */
    private void initRecycle() {
        mDataAdapter = new PunishmentAdapter(instance);
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


    /**
     * 初始化recycleview数据
     */
    private void initList() {

        list_r.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                mLogs = new ArrayList<>();
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
                    mCurrentpage = mCurrentpage + 1;
                    connecting(mCurrentpage);
                } else {
                    //the end
                    list_r.setNoMore(true);
                }
            }
        });

        list_r.refresh();//刷新数据


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.floatingActionButton:
                Intent i = new Intent(instance, PunishmentAddActivity.class);
                startActivity(i);
                break;
        }
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


    List<Punishment> mLog;
    List<Punishment> mLogs;

    private void connecting(int p) {

        LogUtils.e(HttpUrlUtils.getHttpUrl().PunishmentList() + "?access_token=" + SPUtils.get(instance, "access_token", ""));
        OkHttpUtils
                .get()
                .url(HttpUrlUtils.getHttpUrl().PunishmentList() + "?access_token=" + SPUtils.get(instance, "access_token", "") + "&p=" + p)
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
                            mLog = new ArrayList<>();
                            Map<String, Object> map = GsonUtil.JsonToMap(s);
                            if (map.get("state").toString().equals("1")) {
                                mHandler.sendEmptyMessage(-3);
                                Toast.makeText(instance, map.get("mess").toString(), Toast.LENGTH_SHORT).show();
                                return;
                            } else if (map.get("state").toString().equals("0")) {
                                if (!map.get("count").toString().equals("0")) {
                                    list_r.setBackgroundColor(getResources().getColor(R.color.white));
                                    List<Map<String, Object>> data = GsonUtil.GsonToListMaps(GsonUtil.GsonString(map.get("table")));//参数[{},{}]
                                    for (int j = 0; j < data.size(); j++) {
                                        Punishment log = new Punishment();
                                        log.setId(NullUtil.getString(data.get(j).get("pu_id")));//ID
                                        log.setHname(NullUtil.getString(data.get(j).get("hname")));//ID
                                        log.setDate(NullUtil.getString(data.get(j).get("punishdate")));//ID
                                        log.setDel(NullUtil.getString(data.get(j).get("wgxq")));//ID
                                        mLog.add(log);
                                        mLogs.add(log);
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
                                                    Intent i = new Intent(instance, PunishmentDelActivity.class);
                                                    i.putExtra("id", mLogs.get(position).getId());
                                                    startActivity(i);

                                            }
                                        }

                                    });


                                    mLRecyclerViewAdapter.setOnItemLongClickListener(new OnItemLongClickListener() {
                                        @Override
                                        public void onItemLongClick(View view, int position) {
                                            showAdialog(instance,position,"删除","删除后不可找回，请再三确定","确定").show();

                                        }
                                    });

                                    mHandler.sendEmptyMessage(-1);


                                } else {
                                    txt_size.setVisibility(View.GONE);
                                    mHandler.sendEmptyMessage(-3);
                                }


                            } else if (map.get("state").toString().equals("19")) {
                                mHandler.sendEmptyMessage(-3);
                                txt_size.setVisibility(View.GONE);
                                //响应失败
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


    //删除列表自条目
    @Override
    protected void dialogdelectOk(final int position) {
        super.dialogdelectOk(position);

        OkHttpUtils
                .delete()
                .url(HttpUrlUtils.getHttpUrl().PunishmentList() + "/"+mLogs.get(position).getId()+ "?access_token="
                        + SPUtils.get(instance, "access_token", "")  )
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        Toast.makeText(instance, e.toString(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        LogUtils.e(s);
                        Map<String, Object> map = GsonUtil.JsonToMap(s);
                        if (map.get("state").toString().equals("0")) {
                            Toast.makeText(instance, "删除成功", Toast.LENGTH_SHORT).show();
                            mLogs.remove(position);
                            list_r.refresh();//刷新数据
                        }
                    }
                });
    }

    private void notifyDataSetChanged() {
        mLRecyclerViewAdapter.notifyDataSetChanged();
    }

    private void addItems(ArrayList<Punishment> list) {

        mDataAdapter.addAll(list);
        mCurrentCounter += list.size();

    }


    private class PreviewHandler extends Handler {

        private WeakReference<PunishmentListActivity> ref;

        PreviewHandler(PunishmentListActivity activity) {
            ref = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final PunishmentListActivity activity = ref.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }
            switch (msg.what) {

                case -1:

                    int currentSize = activity.mDataAdapter.getItemCount();

                    //模拟组装15个数据
                    ArrayList<Punishment> newList = new ArrayList<>();
                    for (int i = 0; i < mLog.size(); i++) {
                        if (newList.size() + currentSize >= TOTAL_COUNTER) {
                            break;
                        }

                        Punishment item = new Punishment();
                        item.setId(mLog.get(i).getId());
                        item.setHname(mLog.get(i).getHname());
                        item.setDate(mLog.get(i).getDate());
                        item.setDel(mLog.get(i).getDel());
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


