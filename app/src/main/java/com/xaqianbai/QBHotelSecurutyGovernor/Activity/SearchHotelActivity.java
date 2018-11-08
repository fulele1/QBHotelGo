package com.xaqianbai.QBHotelSecurutyGovernor.Activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xaqianbai.QBHotelSecurutyGovernor.Listview.BrandAdapter;
import com.xaqianbai.QBHotelSecurutyGovernor.Listview.BrandBean;
import com.xaqianbai.QBHotelSecurutyGovernor.Listview.LetterIndexView;
import com.xaqianbai.QBHotelSecurutyGovernor.Listview.PinnedSectionListView;
import com.xaqianbai.QBHotelSecurutyGovernor.R;
import com.xaqianbai.QBHotelSecurutyGovernor.Utils.GsonUtil;
import com.xaqianbai.QBHotelSecurutyGovernor.Utils.HttpUrlUtils;
import com.xaqianbai.QBHotelSecurutyGovernor.Utils.LogUtils;
import com.xaqianbai.QBHotelSecurutyGovernor.Utils.SPUtils;
import com.xaqianbai.QBHotelSecurutyGovernor.Utils.StatuBarUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;


public class SearchHotelActivity extends BaseActivityNew {

    /**
     * item标识为0
     */
    public static final int ITEM = 0;
    /**
     * item标题标识为1
     */
    public static final int TITLE = 1;
    public HashMap<String, Integer> map_IsHead;
    private SearchHotelActivity instance;
    private EditText edit_search;
    private PinnedSectionListView listView;
    private LetterIndexView letterIndexView;
    private TextView txt_center,mTxtTitle;
    private ArrayList<BrandBean> list_all;
    private ArrayList<BrandBean> list_show;
    private BrandAdapter adapter;
    @BindView(R.id.layout_titlebar)
    FrameLayout titlebar;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void initViews() {
        setContentView(R.layout.activity_search_staff);
        instance = this;
        ButterKnife.bind(instance);
        StatuBarUtil.setStatuBarLightMode(instance,getResources().getColor(R.color.white));//修改状态栏字体颜色为黑色
        titlebar.setBackgroundColor(getResources().getColor(R.color.white));
        edit_search = (EditText) findViewById(R.id.edit_search_org);
        listView = (PinnedSectionListView) findViewById(R.id.phone_listview_org);
        letterIndexView = (LetterIndexView) findViewById(R.id.phone_LetterIndexView_org);
        txt_center = (TextView) findViewById(R.id.phone_txt_center_org);
        mTxtTitle = (TextView) findViewById(R.id.tv_title);
        mTxtTitle.setText("酒店选择");
    }

    @Override
    public void initData() {
        list_all = new ArrayList<>();
        list_show = new ArrayList<>();
        map_IsHead = new HashMap<>();
        adapter = new BrandAdapter(this, list_show, map_IsHead);
        listView.setAdapter(adapter);
        okConnection();
    }


    public void okConnection() {
        LogUtils.e(HttpUrlUtils.getHttpUrl().HotelDelnew()+"?access_token="+ SPUtils.get(instance,"access_token","")+"&nopage=");
        OkHttpUtils
                .get()
                .url(HttpUrlUtils.getHttpUrl().HotelDelnew()+"?access_token="+ SPUtils.get(instance,"access_token","")+"&nopage=")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {

                    }

                    @Override
                    public void onResponse(String s, int i) {
                        Log.e("fule",s);

                        Map<String, Object> map = GsonUtil.JsonToMap(s);
                        if (map.get("state").toString().equals("1")) {
                            showToast(map.get("mess").toString());
                            return;
                        } else if (map.get("state").toString().equals("0")) {
                            if (!map.get("count").toString().equals("0")){
                                List<Map<String, Object>> data = GsonUtil.GsonToListMaps(GsonUtil.GsonString(map.get("table")));//参数[{},{}]
                                for (int j = 0; j < data.size(); j++) {
                                    BrandBean cityBean = new BrandBean();
                                    cityBean.setName(data.get(j).get("hname").toString());
                                    cityBean.setCode(data.get(j).get("hnohotel").toString());
                                    list_all.add(cityBean);
                                }
                            }else {
                                Toast.makeText(instance, "未查询到有效数据", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            //响应失败
                            Toast.makeText(instance, "未查询到有效数据", Toast.LENGTH_SHORT).show();
                        }
                        loadingDialog.dismiss();
                        getData();
                    }
                });
    }

    /**
     * 获取数据并进行排序
     */
    public void getData() {

        //按拼音排序
        SearchHotelActivity.MemberSortUtil sortUtil = new SearchHotelActivity.MemberSortUtil();
        Collections.sort(list_all, sortUtil);

        // 初始化数据，顺便放入把标题放入map集合
        for (int i = 0; i < list_all.size(); i++) {
            BrandBean cityBean = list_all.get(i);
            if (!map_IsHead.containsKey(cityBean.getHeadChar())) {// 如果不包含就添加一个标题
                BrandBean cityBean1 = new BrandBean();
                // 设置名字
                cityBean1.setName(cityBean.getName());
                // 设置标题type
                cityBean1.setType(SearchHotelActivity.TITLE);
                list_show.add(cityBean1);

                // map的值为标题的下标
                map_IsHead.put(cityBean1.getHeadChar(), list_show.size() - 1);
            }
            list_show.add(cityBean);
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    public void addListener() {

        edit_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {

                //显示和隐藏字母条
                if (!editable.toString().equals("")) {
                    letterIndexView.setVisibility(View.GONE);
                } else if (editable.toString().equals("")) {
                    letterIndexView.setVisibility(View.VISIBLE);
                }

                //重新获取需要现实的数据
                list_show.clear();
                map_IsHead.clear();
                //把输入的字符改成大写
                String search = editable.toString().trim().toUpperCase();

                if (TextUtils.isEmpty(search)) {
                    for (int i = 0; i < list_all.size(); i++) {
                        BrandBean bean = list_all.get(i);
                        //中文字符匹配首字母和英文字符匹配首字母
                        if (!map_IsHead.containsKey(bean.getHeadChar())) {// 如果不包含就添加一个标题
                            BrandBean bean1 = new BrandBean();
                            // 设置名字
                            bean1.setName(bean.getName());
                            // 设置标题type
                            bean1.setType(SearchHotelActivity.TITLE);
                            list_show.add(bean1);
                            // map的值为标题的下标
                            map_IsHead.put(bean1.getHeadChar(),
                                    list_show.size() - 1);
                        }
                        // 设置Item type
                        bean.setType(SearchHotelActivity.ITEM);
                        list_show.add(bean);
                    }
                } else {
                    for (int i = 0; i < list_all.size(); i++) {
                        BrandBean bean = list_all.get(i);
                        //中文字符匹配首字母和英文字符匹配首字母
                        if (bean.getName().indexOf(search) != -1 || bean.getName_en().indexOf(search) != -1) {
                            if (!map_IsHead.containsKey(bean.getHeadChar())) {// 如果不包含就添加一个标题
                                BrandBean bean1 = new BrandBean();
                                // 设置名字
                                bean1.setName(bean.getName());
                                // 设置标题type
                                bean1.setType(SearchHotelActivity.TITLE);
                                list_show.add(bean1);
                                // map的值为标题的下标
                                map_IsHead.put(bean1.getHeadChar(),
                                        list_show.size() - 1);
                            }
                            // 设置Item type
                            bean.setType(SearchHotelActivity.ITEM);
                            list_show.add(bean);
                        }
                    }
                }
                adapter.notifyDataSetChanged();

            }
        });


        // 右边字母竖排的初始化以及监听
        letterIndexView.init(new LetterIndexView.OnTouchLetterIndex() {
            //实现移动接口
            @Override
            public void touchLetterWitch(String letter) {
                // 中间显示的首字母
                txt_center.setVisibility(View.VISIBLE);
                txt_center.setText(letter);
                // 首字母是否被包含
                if (adapter.map_IsHead.containsKey(letter)) {
                    // 设置首字母的位置
                    listView.setSelection(adapter.map_IsHead.get(letter));
                }
            }

            //实现抬起接口 隐藏字母
            @Override
            public void touchFinish() {
                txt_center.setVisibility(View.GONE);
            }
        });


        /**子条目的点击事件 */
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (list_show.get(i).getType() == SearchHotelActivity.ITEM) {
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putString("hname", list_show.get(i).getName());
                    bundle.putString("hid", list_show.get(i).getCode());
                    intent.putExtras(bundle);
                    instance.setResult(RESULT_OK, intent);
                    SearchHotelActivity.this.finish();
                }
            }
        });
    }



    public class MemberSortUtil implements Comparator<BrandBean> {
        /**
         * 按拼音排序
         */
        @Override
        public int compare(BrandBean lhs, BrandBean rhs) {
            Comparator<Object> cmp = Collator
                    .getInstance(java.util.Locale.CHINA);
            return cmp.compare(lhs.getName_en(), rhs.getName_en());
        }
    }
}
