package com.xaqb.unlock.Activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.jdsjlzx.interfaces.OnLoadMoreListener;
import com.github.jdsjlzx.recyclerview.LuRecyclerView;
import com.github.jdsjlzx.recyclerview.LuRecyclerViewAdapter;
import com.github.jdsjlzx.util.LuRecyclerViewStateUtils;
import com.github.jdsjlzx.view.LoadingFooter;
import com.xaqb.unlock.Entity.IncomeInfo;
import com.xaqb.unlock.R;
import com.xaqb.unlock.Utils.ActivityController;
import com.xaqb.unlock.Utils.Globals;
import com.xaqb.unlock.Utils.GsonUtil;
import com.xaqb.unlock.Utils.HttpUrlUtils;
import com.xaqb.unlock.Utils.LogUtils;
import com.xaqb.unlock.Utils.SPUtils;
import com.xaqb.unlock.Utils.ToolsUtils;
import com.xaqb.unlock.Views.LuRecycleView1229.ListBaseAdapter;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.Call;


/**
 * Created by chengeng on 2016/12/2.
 * 空activity，用于复制粘贴
 */
public class IncomeActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    private IncomeActivity instance;
    private ArrayList<IncomeInfo> incomeInfos = new ArrayList<>();
    public static boolean needRefresh;//是否需要刷新列表
    /**
     * 服务器端一共多少条数据
     */
    private int TOTAL_COUNTER = 34;

    /**
     * 每一页展示多少条数据
     */
    private static final int REQUEST_COUNT = 10;

    /**
     * 已经获取到多少条数据了
     */
    private static int mCurrentCounter = 0;
    /**
     * 当前页数
     */
    private int index = 1;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private LuRecyclerView mRecyclerView = null;
    private LuRecyclerViewAdapter mLuRecyclerViewAdapter = null;

    private boolean isRefresh = false;
    private MyAdapter mDataAdapter;

    private ImageView ivNoData;
    @Override
    public void initTitleBar() {
        setTitle("收入明细");
        showBackwardView(true);
    }

    @Override
    public void initViews() {
        setContentView(R.layout.income_activity);
        instance = this;
        assignViews();
    }

    private void assignViews() {
        ivNoData = (ImageView) findViewById(R.id.iv_no_data);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        mRecyclerView = (LuRecyclerView) findViewById(R.id.list);

        //设置刷新时动画的颜色，可以设置4个
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setProgressViewOffset(false, 0, ToolsUtils.dip2px(this, 48));
            mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);
            mSwipeRefreshLayout.setOnRefreshListener(this);
        }
        mDataAdapter = new MyAdapter(instance);
        mLuRecyclerViewAdapter = new LuRecyclerViewAdapter(mDataAdapter);
        mRecyclerView.setAdapter(mLuRecyclerViewAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//        View noDataView = View.inflate(instance, R.layout.list_no_data, null);
//        mRecyclerView.setEmptyView(noDataView);
//        mLuRecyclerViewAdapter.addHeaderView(new SampleHeader(this));
//        mLuRecyclerViewAdapter.setOnItemClickListener(new OnItemClickListener() {
//            @Override
//            public void onItemClick(View view, int position) {
//                SendOrder item = mDataAdapter.getDataList().get(position);
////                item.getOrderID();
////                showToast(item.toString());
//                Intent intent = new Intent(instance, OrderDetailActivity.class);
//                intent.putExtra("or_id", item.getOrderID());
//                startActivity(intent);
//            }
//        });

//        mLuRecyclerViewAdapter.setOnItemLongClickListener(new OnItemLongClickListener() {
//            @Override
//            public void onItemLongClick(View view, int position) {
////                SendOrder item = mDataAdapter.getDataList().get(position);
////                showToast("long----------" + item.toString());
//            }
//        });
        mRecyclerView.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                LoadingFooter.State state = LuRecyclerViewStateUtils.getFooterViewState(mRecyclerView);
                if (state == LoadingFooter.State.Loading) {
                    return;
                }
                index++;
                if (index <= TOTAL_COUNTER) {
                    // loading more
                    LuRecyclerViewStateUtils.setFooterViewState(instance, mRecyclerView, REQUEST_COUNT, LoadingFooter.State.Loading, null);
                    initData();
                } else {
                    //the end
                    LuRecyclerViewStateUtils.setFooterViewState(instance, mRecyclerView, REQUEST_COUNT, LoadingFooter.State.TheEnd, null);

                }
            }
        });
    }

    @Override
    public void initData() {
        if (!checkNetwork()) {
            showToast(getResources().getString(R.string.network_not_alive));
            return;
        }
        LogUtils.i(HttpUrlUtils.getHttpUrl().getPayDetail() + "?p=" + index + "&access_token=" + SPUtils.get(instance, "access_token", ""));
        loadingDialog.show("加载中...");
        OkHttpUtils.get()
                .url(HttpUrlUtils.getHttpUrl().getPayDetail() + "?p=" + index + "&access_token=" + SPUtils.get(instance, "access_token", ""))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        e.printStackTrace();
                        loadingDialog.dismiss();
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        loadingDialog.dismiss();
                        try {
                            Map<String, Object> map = GsonUtil.JsonToMap(s);
                            LogUtils.i(map.toString());
                            if (map.get("state").toString().equals(Globals.httpSuccessState)) {
                                LogUtils.i("senddata", "" + map.toString());
                                List<Map<String, Object>> data = GsonUtil.GsonToListMaps(GsonUtil.GsonString(map.get("table")));
                                if (data == null || data.size() == 0) {
                                    addItems(incomeInfos);
                                    notifyDataSetChanged();
                                    ivNoData.setVisibility(View.VISIBLE);
                                    mSwipeRefreshLayout.setRefreshing(false);
                                    LogUtils.i("暂无数据");
                                    return;
                                }
                                LogUtils.i("curr = ", map.get("curr").toString());
                                LogUtils.i("data = ", data.toString());
                                TOTAL_COUNTER = Integer.parseInt(map.get("page").toString());
                                incomeInfos = new ArrayList<>();
                                IncomeInfo info;
                                for (int j = 0; j < data.size(); j++) {
                                    info = new IncomeInfo();
                                    info.setId(data.get(j).get("sp_id").toString());
                                    info.setOrderTime(data.get(j).get("sp_createtime").toString());
                                    info.setOrderPrice(data.get(j).get("sp_price").toString());
                                    info.setOrderId(data.get(j).get("or_orderno").toString());
                                    info.setPayType(data.get(j).get("sp_paytype").toString());
                                    info.setSerialNumber(data.get(j).get("sp_serialnum").toString());
                                    incomeInfos.add(info);
                                }
                                if (incomeInfos.size() == 0) {
//                                    ivNoData.setVisibility(View.VISIBLE);
                                    return;
                                }

                                if (isRefresh) {
                                    mDataAdapter.clear();
                                    mCurrentCounter = 0;
                                }
                                int currentSize = mDataAdapter.getItemCount();
                                if (currentSize == currentSize + incomeInfos.size()) {
                                    LuRecyclerViewStateUtils.setFooterViewState(instance, mRecyclerView, REQUEST_COUNT, LoadingFooter.State.TheEnd, null);
                                    return;
                                }
                                addItems(incomeInfos);
                                if (isRefresh) {
                                    isRefresh = false;
                                    mSwipeRefreshLayout.setRefreshing(false);
                                }
                                LuRecyclerViewStateUtils.setFooterViewState(mRecyclerView, LoadingFooter.State.Normal);
                                notifyDataSetChanged();
                                needRefresh = false;
                            } else if (map.get("state").toString().equals(Globals.httpTokenFailure)) {
                                ActivityController.finishAll();
                                showToast("登录失效，请重新登录");
                                startActivity(new Intent(instance, LoginActivity.class));
                            } else {
                                showToast(map.get("mess").toString());
                                return;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

    }

    @Override
    public void addListener() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
    }

    @Override
    public void onRefresh() {
        mCurrentCounter = 0;
        isRefresh = true;
        index = 1;
        mSwipeRefreshLayout.setRefreshing(true);
        initData();
    }

    private class MyAdapter extends ListBaseAdapter<IncomeInfo> {

        private LayoutInflater mLayoutInflater;

        public MyAdapter(Context context) {
            mLayoutInflater = LayoutInflater.from(context);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(mLayoutInflater.inflate(R.layout.income_list_item, parent, false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            try {
                ViewHolder viewHolder = (ViewHolder) holder;
                String time = mDataList.get(position).getPayType();
                if (time.equals("wxpay")) {
                    viewHolder.tvPayType.setText("微信支付");
                } else if (time.equals("alipay")) {
                    viewHolder.tvPayType.setText("阿里支付");
                } else if (time.equals("offpay")) {
                    viewHolder.tvPayType.setText("线下支付");
                } else {
                    viewHolder.tvPayType.setText(mDataList.get(position).getPayType());
                }
                time = mDataList.get(position).getOrderTime();
                if (!time.isEmpty()) {
                    viewHolder.tvTime.setText(ToolsUtils.getStrTime(time));
                }
                viewHolder.tvPrice.setText(mDataList.get(position).getOrderPrice());
                viewHolder.tvOrderNum.setText(mDataList.get(position).getOrderId());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        private class ViewHolder extends RecyclerView.ViewHolder {

            private TextView tvTime, tvPrice, tvOrderNum, tvPayType;

            public ViewHolder(View view) {
                super(view);
                tvPayType = (TextView) view.findViewById(R.id.tv_order_pay_type);
                tvTime = (TextView) view.findViewById(R.id.tv_order_time);
                tvPrice = (TextView) view.findViewById(R.id.tv_order_price);
                tvOrderNum = (TextView) view.findViewById(R.id.tv_order_num);
            }
        }
    }

    private void notifyDataSetChanged() {
        mLuRecyclerViewAdapter.notifyDataSetChanged();
    }

    /**
     * 增加数据到集合
     *
     * @param list 需要增加的数据集合
     */
    private void addItems(ArrayList<IncomeInfo> list) {
        mDataAdapter.addAll(list);
        mCurrentCounter += list.size();
    }

    private View.OnClickListener mFooterClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            LuRecyclerViewStateUtils.setFooterViewState(instance, mRecyclerView, REQUEST_COUNT, LoadingFooter.State.Loading, null);
//            requestData();
        }
    };

}
