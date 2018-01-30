package com.xaqb.unlock.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.jdsjlzx.interfaces.OnItemClickListener;
import com.github.jdsjlzx.interfaces.OnItemLongClickListener;
import com.github.jdsjlzx.interfaces.OnLoadMoreListener;
import com.github.jdsjlzx.recyclerview.LuRecyclerView;
import com.github.jdsjlzx.recyclerview.LuRecyclerViewAdapter;
import com.github.jdsjlzx.util.LuRecyclerViewStateUtils;
import com.github.jdsjlzx.view.LoadingFooter;
import com.squareup.picasso.Picasso;
import com.xaqb.unlock.Activity.LoginActivity;
import com.xaqb.unlock.Activity.OrderDetailActivityNew;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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


/**
 * Created by fl on 2017/4/20.
 */
public class GotFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener{
    private static final String TAG = "lzx";
    private static final int REQUEST_COUNT = 10;//每一页展示多少条数据
    public static boolean needRefresh;//是否需要刷新列表
    private static int mCurrentCounter = 0;//已经获取到多少条数据了
    private View view;
    private Activity instance;
    private ArrayList<SendOrder> sendOrders = new ArrayList<>();
    private int TOTAL_COUNTER = 34;//服务器端一共多少条数据
    private int index = 1;//当前页数
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private LuRecyclerView mRecyclerView = null;
    private LuRecyclerViewAdapter mLuRecyclerViewAdapter = null;
    private boolean isRefresh = false;
    private GotFragment.MyAdapter mDataAdapter;
    private ImageView ivNoData;
    private View.OnClickListener mFooterClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            LuRecyclerViewStateUtils.setFooterViewState(instance, mRecyclerView, REQUEST_COUNT, LoadingFooter.State.Loading, null);
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_got, null);
        instance = this.getActivity();
        assignViews();
        return view;
    }

    private void assignViews() {
        ivNoData = (ImageView) view.findViewById(R.id.iv_no_data_fra_got);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout_fra_got);
        mRecyclerView = (LuRecyclerView) view.findViewById(R.id.list_fra_got);

        //设置刷新时动画的颜色，可以设置4个
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setProgressViewOffset(false, 0, ToolsUtils.dip2px(this.getActivity(), 48));
            mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);
            mSwipeRefreshLayout.setOnRefreshListener(this);
        }
        mDataAdapter = new GotFragment.MyAdapter(instance);
        mLuRecyclerViewAdapter = new LuRecyclerViewAdapter(mDataAdapter);
        mRecyclerView.setAdapter(mLuRecyclerViewAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        View noDataView = View.inflate(instance, R.layout.list_no_data, null);
        mRecyclerView.setEmptyView(noDataView);
        mLuRecyclerViewAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                SendOrder item = mDataAdapter.getDataList().get(position);
                Intent intent = new Intent(instance, OrderDetailActivityNew.class);
                intent.putExtra("or_id", item.getOrderID());
                startActivity(intent);
            }
        });

        mLuRecyclerViewAdapter.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, int position) {
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

    public void setData() {
        if (!checkNetwork()) {
            showToastB(getResources().getString(R.string.network_not_alive));
            return;
        }
        QBHttp.get(
                instance,
                HttpUrlUtils.getHttpUrl().getOrderList() +
                        "?p=" + index +
                        "&paystatus=" + "01" +
                        "&access_token=" + SPUtils.get(instance, "access_token", "")
                , null
                , new QBCallback() {
                    @Override
                    public void doWork(Map<?, ?> map) {
                        LogUtils.e("01"+map);
//                        mLoadingDialog.dismiss();
                        try {
                            if (map.get("state").toString().equals(Globals.httpSuccessState)) {
                                List<Map<String, Object>> data = GsonUtil.GsonToListMaps(GsonUtil.GsonString(map.get("table")));
                                if (data == null || data.size() == 0) {
                                    addItems(sendOrders);
                                    notifyDataSetChanged();
                                    ivNoData.setVisibility(View.VISIBLE);
                                    mSwipeRefreshLayout.setRefreshing(false);
                                    return;
                                }
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
                                    sendOrder.setPicUrl(data.get(j).get("or_lockimg").toString());
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
                                showToastB("登录失效，请重新登录");
                                startActivity(new Intent(instance, LoginActivity.class));
                            } else {
                                showToastB(map.get("mess").toString());
                                return;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void doError(Exception e) {
                        e.printStackTrace();
//                        mLoadingDialog.dismiss();
//                        showToastB("网络访问异常");
                        Toast.makeText(instance, "网络访问异常", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void reDoWork() {

                    }
                }

        );
    }

    //检查网络连接与否
    protected boolean checkNetwork() {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) this.getActivity()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
        if (mNetworkInfo != null) {
            return mNetworkInfo.isAvailable();
        }
        return false;
    }

    private class MyAdapter extends ListBaseAdapter<SendOrder> {

        private LayoutInflater mLayoutInflater;

        public MyAdapter(Context context) {
            mLayoutInflater = LayoutInflater.from(context);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new GotFragment.MyAdapter.ViewHolder(mLayoutInflater.inflate(R.layout.send_order_item, parent, false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            try {
                GotFragment.MyAdapter.ViewHolder viewHolder = (GotFragment.MyAdapter.ViewHolder) holder;
                //订单号
                viewHolder.tvContent.setText(mDataList.get(position).getOrderNo());
                //时间
                String time = mDataList.get(position).getOrderTime();
                if (!time.isEmpty()) {
                    viewHolder.tvTime.setText(ToolsUtils.getStrTime(time));
                }
                //地址
                viewHolder.tvAddress.setText(mDataList.get(position).getOrderAddress());

                //图片
                if (mDataList.get(position).getPicUrl() != null && !mDataList.get(position).getPicUrl().equals(""))
                    Picasso.with(getContext())
                            .load(mDataList.get(position).getPicUrl())
                            .placeholder(R.mipmap.nothing_pic)
                            .error(R.mipmap.failed_pic)
                            .fit()
                            .into(viewHolder.ivPic);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        private class ViewHolder extends RecyclerView.ViewHolder {

            private TextView tvTime, tvContent, tvAddress, tvprice;
            private ImageView ivPic;

            public ViewHolder(View view) {
                super(view);
                tvContent = (TextView) view.findViewById(R.id.tv_order_no_item_order);
                tvTime = (TextView) view.findViewById(R.id.tv_time_item_order);
                tvAddress = (TextView) view.findViewById(R.id.tv_address_item_order);
                tvprice = (TextView) view.findViewById(R.id.tv_price_item_order);//TODO
                ivPic = (ImageView) view.findViewById(R.id.img_pic_item_order);//TODO
            }
        }
    }

}
