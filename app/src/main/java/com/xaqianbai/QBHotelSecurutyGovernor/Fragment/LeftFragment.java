package com.xaqianbai.QBHotelSecurutyGovernor.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xaqianbai.QBHotelSecurutyGovernor.Activity.AboutActivity;
import com.xaqianbai.QBHotelSecurutyGovernor.Activity.CrimeListActivity;
import com.xaqianbai.QBHotelSecurutyGovernor.Activity.ClueListActivity;
import com.xaqianbai.QBHotelSecurutyGovernor.Activity.LogListActivity;
import com.xaqianbai.QBHotelSecurutyGovernor.Activity.LoginActivity;
import com.xaqianbai.QBHotelSecurutyGovernor.Activity.ModifyPswActivity;
import com.xaqianbai.QBHotelSecurutyGovernor.Activity.PunishmentListActivity;
import com.xaqianbai.QBHotelSecurutyGovernor.Activity.UpdateActivityNew;
import com.xaqianbai.QBHotelSecurutyGovernor.Activity.UserNickNameActivity;
import com.xaqianbai.QBHotelSecurutyGovernor.Activity.UserPicActivity;
import com.xaqianbai.QBHotelSecurutyGovernor.Adapter.LeftMenuListAdapter;
import com.xaqianbai.QBHotelSecurutyGovernor.R;
import com.xaqianbai.QBHotelSecurutyGovernor.Utils.GlideCircleTransform;
import com.xaqianbai.QBHotelSecurutyGovernor.Utils.SPUtils;
import com.xaqianbai.QBHotelSecurutyGovernor.Utils.UpdateUtil;


/**
 * 左滑菜单
 * Created by fl on 2016/11/28.
 */
public class LeftFragment extends BaseFragment implements View.OnClickListener {
    private ListView lvLeftMenu;
    private ImageView ivPic;
    private LinearLayout llSetting, llCustomer;
    private TextView tvNickName;
    private String url, nickname, status = "";
    private Activity instance;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_left_menu, null);
        initViews(view);
        instance = this.getActivity();
        return view;
    }

    public void initViews(View view) {
        lvLeftMenu = (ListView) view.findViewById(R.id.lv_left_menu);
        lvLeftMenu.setDividerHeight(0);
        ivPic = (ImageView) view.findViewById(R.id.iv_user_pic);
        llSetting = (LinearLayout) view.findViewById(R.id.ll_setting);
        llCustomer = (LinearLayout) view.findViewById(R.id.ll_customer_left);
        tvNickName = (TextView) view.findViewById(R.id.tv_left_nick_name);
        ivPic.setOnClickListener(this);
        llSetting.setOnClickListener(this);
        llCustomer.setOnClickListener(this);
        tvNickName.setOnClickListener(this);

        lvLeftMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {

                    case 0:// 线索信息
                        startActivity(new Intent(getActivity(), ClueListActivity.class));
                        break;
                    case 1: // 检查日志
                        startActivity(new Intent(getActivity(), LogListActivity.class));
                        break;
                    case 2: // 发案列表
                        startActivity(new Intent(getActivity(), CrimeListActivity.class));
                        break;
                    case 3: // 处罚列表
                        startActivity(new Intent(getActivity(), PunishmentListActivity.class));
                        break;
                    case 4: //关于我们
                        startActivity(new Intent(getActivity(), AboutActivity.class));
                        break;
                    case 5: // 修改密码
                        startActivity(new Intent(getActivity(), ModifyPswActivity.class));
                        break;
                    case 6: // 版本更新
                        SPUtils.put(getContext(), "isclickFragment", "true");
                        new UpdateUtil(getActivity(), "20").getVersion();
                        break;
                    default:
                        break;

                }
            }
        });

    }

    @Override
    public void dialogOkB() {
        super.dialogOkB();

        startActivity(new Intent(getActivity(), UpdateActivityNew.class));

    }

    /**
     * 加载用户头像
     *
     * @param url 头像链接
     */
    private void loadUserPic(String url) {
        if (url != null && !url.equals(""))
            Glide.with(instance)
                    .load(url)
                    .skipMemoryCache(false)//防止大图因为内存问题无法加载
                    .transform(new GlideCircleTransform(instance))//设置为圆形图片
                    .placeholder(R.mipmap.now_no_pic)
                    .error(R.mipmap.now_no_pic)
                    .into(ivPic);
    }

    @Override
    public void onResume() {
        super.onResume();
        lvLeftMenu.setAdapter(new LeftMenuListAdapter(getActivity()));
        url = SPUtils.get(getActivity(), "ou_headpic", "").toString();
        nickname = SPUtils.get(getActivity(), "ou_nickname", "").toString();

        if (nickname != null && !nickname.equals(""))
            tvNickName.setText(nickname);
        else tvNickName.setText("暂无昵称");
        if (!url.equals("")&& !url.equals("")) {
            loadUserPic(url);
        } else {
            Glide.with(instance)
                    .load(R.mipmap.now_no_pic)
                    .transform(new GlideCircleTransform(instance))
                    .into(ivPic);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_user_pic://更改用户头像
                startActivity(new Intent(getActivity(), UserPicActivity.class));
                break;
            case R.id.tv_left_nick_name://更改用户昵称
                startActivity(new Intent(getActivity(), UserNickNameActivity.class));
                break;
            case R.id.ll_setting://注销登录
                showAdialog(LeftFragment.this.getActivity(), "提示", "确定要退出登录吗?", "确定", View.VISIBLE);
                break;
        }
    }

    @Override
    protected void dialogOk() {
        resetSprfMain();
        SPUtils.put(instance, "userPsw", "");
        startActivity(new Intent(getActivity(), LoginActivity.class));
        instance.finish();

    }

    SharedPreferences sprfMain;
    SharedPreferences.Editor editorMain;
    public void resetSprfMain(){
        sprfMain= PreferenceManager.getDefaultSharedPreferences(instance);
        editorMain=sprfMain.edit();
        editorMain.putBoolean("main",false);
        editorMain.commit();
    }



}
