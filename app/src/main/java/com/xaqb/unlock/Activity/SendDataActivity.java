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


    @Override
    public void initTitleBar() {
        setTitle("已发数据");
        showBackwardView(true);
    }

    @Override
    public void initViews() {
        setContentView(R.layout.send_data_activity);
        instance = this;
        assignViews();
    }

    private void assignViews() {
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
//        mLuRecyclerViewAdapter.addHeaderView(new SampleHeader(this));
        mLuRecyclerViewAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                SendOrder item = mDataAdapter.getDataList().get(position);
//                item.getOrderID();
//                showToast(item.toString());
                Intent intent = new Intent(instance,OrderDetailActivity.class);
                intent.putExtra("or_id",item.getOrderID());
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
        if (!checkNetwork()) return;
        LogUtils.i(HttpUrlUtils.getHttpUrl().getOrderList() + "?id=" + SPUtils.get(instance, "userid", "") + "&p=" + index);
        OkHttpUtils.get()
                .url(HttpUrlUtils.getHttpUrl().getOrderList() + "?id=" + SPUtils.get(instance, "userid", "") + "&p=" + index)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        try {
                            Map<String, Object> map = GsonUtil.JsonToMap(s);
                            LogUtils.i(map.toString());
                            if (map.get("state").toString().equals(Globals.httpSuccessState)) {
                                LogUtils.i("senddata", "" + map.toString());
                                List<Map<String, Object>> data = GsonUtil.GsonToListMaps(GsonUtil.GsonString(map.get("table")));
                                if (data == null || data.size() == 0) {
                                    addItems(sendOrders);
                                    notifyDataSetChanged();
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

    /**
     * 刷新数据
     */
    @Override
    public void onRefresh() {
        mCurrentCounter = 0;
        isRefresh = true;
        index = 1;
        mSwipeRefreshLayout.setRefreshing(true);
        initData();
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
            ViewHolder viewHolder = (ViewHolder) holder;
            viewHolder.tvContent.setText(mDataList.get(position).getOrderNo());
            String time = mDataList.get(position).getOrderTime();
            if (!time.isEmpty()) {
                viewHolder.tvTime.setText(ToolsUtils.getStrTime(time));
            }
            viewHolder.tvAddress.setText(mDataList.get(position).getOrderAddress());
        }


        private class ViewHolder extends RecyclerView.ViewHolder {

            private TextView tvTime, tvContent, tvAddress;

            public ViewHolder(View view) {
                super(view);
                tvContent = (TextView) view.findViewById(R.id.tv_order_no);
                tvTime = (TextView) view.findViewById(R.id.tv_order_time);
                tvAddress = (TextView) view.findViewById(R.id.tv_order_address);
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
