package com.xaqb.unlock.Activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.jdsjlzx.interfaces.OnItemClickListener;
import com.github.jdsjlzx.interfaces.OnItemLongClickListener;
import com.github.jdsjlzx.interfaces.OnLoadMoreListener;
import com.github.jdsjlzx.recyclerview.LuRecyclerView;
import com.github.jdsjlzx.recyclerview.LuRecyclerViewAdapter;
import com.github.jdsjlzx.util.LuRecyclerViewStateUtils;
import com.github.jdsjlzx.view.LoadingFooter;
import com.xaqb.unlock.Entity.SendOrder;
import com.xaqb.unlock.R;
import com.xaqb.unlock.Utils.ActivityController;
import com.xaqb.unlock.Utils.Globals;
import com.xaqb.unlock.Utils.GsonUtil;
import com.xaqb.unlock.Utils.HttpUrlUtils;
import com.xaqb.unlock.Utils.LogUtils;
import com.xaqb.unlock.Utils.QBCallback;
import com.xaqb.unlock.Utils.QBHttp;
import com.xaqb.unlock.Utils.SPUtils;
import com.xaqb.unlock.Utils.ToolsUtils;
import com.xaqb.unlock.Views.LuRecycleView1229.ListBaseAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Created by chengeng on 2016/12/2.
 * 已发数据页面
 */
public class SendDataActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    private SendDataActivity instance;
    private ArrayList<SendOrder> sendOrders = new ArrayList<>();
    public static boolean needRefresh;//是否需要刷新列表

    private static final String TAG = "lzx";
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
        setTitle("我的订单");
        showBackwardView(true);
    }

    @Override
    public void initViews() {
        setContentView(R.layout.send_data_activity);
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
        View noDataView = View.inflate(instance, R.layout.list_no_data, null);
        mRecyclerView.setEmptyView(noDataView);
//        mLuRecyclerViewAdapter.addHeaderView(new SampleHeader(this));
        mLuRecyclerViewAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                SendOrder item = mDataAdapter.getDataList().get(position);
//                item.getOrderID();
//                showToast(item.toString());
                Intent intent = new Intent(instance, OrderDetailActivity.class);
                intent.putExtra("or_id", item.getOrderID());
                startActivity(intent);
            }
        });

        mLuRecyclerViewAdapter.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, int position) {
//                SendOrder item = mDataAdapter.getDataList().get(position);
//                showToast("long----------" + item.toString());
            }
        });
        mRecyclerView.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                LoadingFooter.State state = LuRecyclerViewStateUtils.getFooterViewState(mRecyclerView);
                if (state == LoadingFooter.State.Loading) {
                    Log.d(TAG, "the state is Loading, just wait..");
                    return;
                }
                index++;
                if (index <= TOTAL_COUNTER) {
                    // loading more
                    LuRecyclerViewStateUtils.setFooterViewState(instance, mRecyclerView, REQUEST_COUNT, LoadingFooter.State.Loading, null);
                    setData();
                } else {
                    //the end
                    LuRecyclerViewStateUtils.setFooterViewState(instance, mRecyclerView, REQUEST_COUNT, LoadingFooter.State.TheEnd, null);

                }
            }
        });
    }

    @Override
    public void initData() {

    }

    public void setData() {
        if (!checkNetwork()) {
            showToast(getResources().getString(R.string.network_not_alive));
            return;
        }
        LogUtils.i(HttpUrlUtils.getHttpUrl().getOrderList() + "?id=" + SPUtils.get(instance, "userid", "") + "&p=" + index + "&access_token=" + SPUtils.get(instance, "access_token", ""));
        loadingDialog.show("加载中...");
        QBHttp.get(
                instance,
                HttpUrlUtils.getHttpUrl().getOrderList() + "?id=" + SPUtils.get(instance, "userid", "") + "&p=" + index + "&access_token=" + SPUtils.get(instance, "access_token", "")
                , null
                , new QBCallback() {
                    @Override
                    public void doWork(Map<?, ?> map) {
                        loadingDialog.dismiss();
                        try {
                            LogUtils.i(map.toString());
                            if (map.get("state").toString().equals(Globals.httpSuccessState)) {
                                LogUtils.i("senddata", "" + map.toString());
                                List<Map<String, Object>> data = GsonUtil.GsonToListMaps(GsonUtil.GsonString(map.get("table")));
                                if (data == null || data.size() == 0) {
                                    addItems(sendOrders);
                                    notifyDataSetChanged();
                                    ivNoData.setVisibility(View.VISIBLE);
                                    mSwipeRefreshLayout.setRefreshing(false);
                                    LogUtils.i("暂无数据");
                                    return;
                                }
                                LogUtils.i("curr = ", map.get("curr").toString());
                                LogUtils.i("data = ", data.toString());
                                TOTAL_COUNTER = Integer.parseInt(map.get("page").toString());
                                sendOrders = new ArrayList<>();
                                SendOrder sendOrder;
                                for (int j = 0; j < data.size(); j++) {
                                    sendOrder = new SendOrder();
                                    sendOrder.setOrderNo(data.get(j).get("or_orderno").toString());
                                    sendOrder.setOrderAddress(data.get(j).get("or_useraddress").toString());
                                    sendOrder.setOrderTime(data.get(j).get("or_createtime").toString());
                                    sendOrder.setOrderID(data.get(j).get("or_id").toString());
                                    sendOrder.setOrderPayStatus(data.get(j).get("or_paystatus").toString());
                                    sendOrders.add(sendOrder);
                                }
                                if (sendOrders.size() == 0) {
//                                    ivNoData.setVisibility(View.VISIBLE);
                                    return;
                                }

                                if (isRefresh) {
                                    mDataAdapter.clear();
                                    mCurrentCounter = 0;
                                }
                                int currentSize = mDataAdapter.getItemCount();
                                if (currentSize == currentSize + sendOrders.size()) {
                                    LuRecyclerViewStateUtils.setFooterViewState(instance, mRecyclerView, REQUEST_COUNT, LoadingFooter.State.TheEnd, null);
                                    return;
                                }
                                addItems(sendOrders);
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

                    @Override
                    public void doError(Exception e) {
                        e.printStackTrace();
                        loadingDialog.dismiss();
                        showToast("网络访问异常");
                    }

                    @Override
                    public void reDoWork() {

                    }
                }

        );
//        OkHttpUtils.get()
//                .url(HttpUrlUtils.getHttpUrl().getOrderList() + "?id=" + SPUtils.get(instance, "userid", "") + "&p=" + index + "&access_token=" + SPUtils.get(instance, "access_token", ""))
//                .build()
//                .execute(new StringCallback() {
//                    @Override
//                    public void onError(Call call, Exception e, int i) {
//                        e.printStackTrace();
//                        loadingDialog.dismiss();
//                        showToast("网络访问异常");
//                    }
//
//                    @Override
//                    public void onResponse(String s, int i) {
//                        loadingDialog.dismiss();
//                        try {
//                            Map<String, Object> map = GsonUtil.JsonToMap(s);
//                            LogUtils.i(map.toString());
//                            if (map.get("state").toString().equals(Globals.httpSuccessState)) {
//                                LogUtils.i("senddata", "" + map.toString());
//                                List<Map<String, Object>> data = GsonUtil.GsonToListMaps(GsonUtil.GsonString(map.get("table")));
//                                if (data == null || data.size() == 0) {
//                                    addItems(sendOrders);
//                                    notifyDataSetChanged();
//                                    ivNoData.setVisibility(View.VISIBLE);
//                                    mSwipeRefreshLayout.setRefreshing(false);
//                                    LogUtils.i("暂无数据");
//                                    return;
//                                }
//                                LogUtils.i("curr = ", map.get("curr").toString());
//                                LogUtils.i("data = ", data.toString());
//                                TOTAL_COUNTER = Integer.parseInt(map.get("page").toString());
//                                sendOrders = new ArrayList<>();
//                                SendOrder sendOrder;
//                                for (int j = 0; j < data.size(); j++) {
//                                    sendOrder = new SendOrder();
//                                    sendOrder.setOrderNo(data.get(j).get("or_orderno").toString());
//                                    sendOrder.setOrderAddress(data.get(j).get("or_useraddress").toString());
//                                    sendOrder.setOrderTime(data.get(j).get("or_createtime").toString());
//                                    sendOrder.setOrderID(data.get(j).get("or_id").toString());
//                                    sendOrder.setOrderPayStatus(data.get(j).get("or_paystatus").toString());
//                                    sendOrders.add(sendOrder);
//                                }
//                                if (sendOrders.size() == 0) {
////                                    ivNoData.setVisibility(View.VISIBLE);
//                                    return;
//                                }
//
//                                if (isRefresh) {
//                                    mDataAdapter.clear();
//                                    mCurrentCounter = 0;
//                                }
//                                int currentSize = mDataAdapter.getItemCount();
//                                if (currentSize == currentSize + sendOrders.size()) {
//                                    LuRecyclerViewStateUtils.setFooterViewState(instance, mRecyclerView, REQUEST_COUNT, LoadingFooter.State.TheEnd, null);
//                                    return;
//                                }
//                                addItems(sendOrders);
//                                if (isRefresh) {
//                                    isRefresh = false;
//                                    mSwipeRefreshLayout.setRefreshing(false);
//                                }
//                                LuRecyclerViewStateUtils.setFooterViewState(mRecyclerView, LoadingFooter.State.Normal);
//                                notifyDataSetChanged();
//                                needRefresh = false;
//                            } else if (map.get("state").toString().equals(Globals.httpTokenFailure)) {
//                                ActivityController.finishAll();
//                                showToast("登录失效，请重新登录");
//                                startActivity(new Intent(instance, LoginActivity.class));
//                            } else {
//                                showToast(map.get("mess").toString());
//                                return;
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });
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
    public void onResume() {
        super.onResume();
        onRefresh();
    }

    /**
     * 刷新数据
     */
    @Override
    public void onRefresh() {
        mCurrentCounter = 0;
        isRefresh = true;
        index = 1;
        mSwipeRefreshLayout.setRefreshing(true);
        setData();
    }


    private class MyAdapter extends ListBaseAdapter<SendOrder> {

        private LayoutInflater mLayoutInflater;

        public MyAdapter(Context context) {
            mLayoutInflater = LayoutInflater.from(context);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(mLayoutInflater.inflate(R.layout.send_order_item, parent, false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            try {
                ViewHolder viewHolder = (ViewHolder) holder;
                viewHolder.tvContent.setText(mDataList.get(position).getOrderNo());
                String time = mDataList.get(position).getOrderTime();
                if (!time.isEmpty()) {
                    viewHolder.tvTime.setText(ToolsUtils.getStrTime(time));
                }
                viewHolder.tvAddress.setText(mDataList.get(position).getOrderAddress());
                String payStatus = mDataList.get(position).getOrderPayStatus();
                if (payStatus.equals("00") || payStatus.equals("02")) {
                    viewHolder.tvPayStatus.setText("未付款");
                    viewHolder.ivPayStatus.setImageResource(R.mipmap.circle_delete_72px);
                } else if (payStatus.equals("01")) {
                    viewHolder.tvPayStatus.setText("已付款");
                    viewHolder.ivPayStatus.setImageResource(R.mipmap.circle_checked_72px);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        private class ViewHolder extends RecyclerView.ViewHolder {

            private TextView tvTime, tvContent, tvAddress, tvPayStatus;
            private ImageView ivPayStatus;

            public ViewHolder(View view) {
                super(view);
                tvContent = (TextView) view.findViewById(R.id.tv_order_no);
                tvTime = (TextView) view.findViewById(R.id.tv_order_time);
                tvAddress = (TextView) view.findViewById(R.id.tv_order_address);
                tvPayStatus = (TextView) view.findViewById(R.id.tv_pay_status);
                ivPayStatus = (ImageView) view.findViewById(R.id.iv_pay_status);
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
    private void addItems(ArrayList<SendOrder> list) {
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
