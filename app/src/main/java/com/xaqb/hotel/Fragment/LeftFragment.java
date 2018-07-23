package com.xaqb.hotel.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;
import com.xaqb.hotel.Activity.ClueListActivity;
import com.xaqb.hotel.Activity.LogListActivity;
import com.xaqb.hotel.Activity.LoginActivity;
import com.xaqb.hotel.Activity.ModifyPswActivity;
import com.xaqb.hotel.Activity.UpdateActivityNew;
import com.xaqb.hotel.Activity.UserNickNameActivity;
import com.xaqb.hotel.Activity.UserPicActivity;
import com.xaqb.hotel.Adapter.LeftMenuListAdapter;
import com.xaqb.hotel.R;
import com.xaqb.hotel.Utils.ApkTotalUtill;
import com.xaqb.hotel.Utils.CircleTransform;
import com.xaqb.hotel.Utils.GlideCircleTransform;
import com.xaqb.hotel.Utils.GlideRoundTransform;
import com.xaqb.hotel.Utils.PicUtil;
import com.xaqb.hotel.Utils.SPUtils;

import java.io.File;

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
                    case 0: // 修改密码
                        startActivity(new Intent(getActivity(), ModifyPswActivity.class));
                        break;
                    case 1: // 版本更新
                        f = new File(SPUtils.get(instance, "au_save_path", "") + "");
                        isExists = f.exists();

                        if (readConfig("late").equals("yes")) {
                            Toast.makeText(getContext(), "已是最新版本", Toast.LENGTH_SHORT).show();
                        } else {
                            if (isExists
                                    && ApkTotalUtill.getUninatllApkInfo(instance, SPUtils.get(instance, "au_save_path", "") + "")
                                    ) {
                                showDialogB(instance, "提示", 0, "新版本已下载成功是否直接安装", "立刻安装", "以后再说");

                            } else {
                                showDialogB(instance, "发现新版本", 0, "本次更新的内容有：\n" + SPUtils.get(instance, "au_info", ""), "立刻更新", "以后再说");
                            }
                        }
                        break;
                    case 2:// 线索信息
                        startActivity(new Intent(getActivity(), ClueListActivity.class));
                        break;
                    case 3: // 检查日志
                        startActivity(new Intent(getActivity(), LogListActivity.class));
                        break;
                    default:
                        break;
                }
            }
        });

    }
    private boolean isExists;
    private File f;
    @Override
    public void dialogOkB() {
        super.dialogOkB();
        if (isExists
                && ApkTotalUtill.getUninatllApkInfo(instance,SPUtils.get(instance,"au_save_path","")+"")
 ){
            //安装app
            Intent oInt1 = new Intent(Intent.ACTION_VIEW);
            oInt1.setDataAndType(Uri.fromFile(f), "application/vnd.android.package-archive");

            //关键点：
            //安装完成后执行打开
            oInt1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(oInt1);
        }else{
        startActivity(new Intent(getActivity(), UpdateActivityNew.class));
        }
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
                    .placeholder(R.mipmap.per)
                    .error(R.mipmap.per)
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
         if (!url.equals("")) {
            loadUserPic(url);
        } else {
             Glide.with(instance).load(R.mipmap.per).transform(new GlideCircleTransform(instance)).into(ivPic);
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
                showAdialog(LeftFragment.this.getActivity(),"提示","确定要退出登录吗?","确定",View.VISIBLE);
                break;
        }
    }

    @Override
    protected void dialogOk() {
        SPUtils.put(instance,"userPsw","");
        startActivity(new Intent(getActivity(), LoginActivity.class));
    }

}
