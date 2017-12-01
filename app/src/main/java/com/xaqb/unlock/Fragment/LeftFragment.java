package com.xaqb.unlock.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import com.squareup.picasso.Picasso;
import com.xaqb.unlock.Activity.AboutActivity;
import com.xaqb.unlock.Activity.AdviseActivity;
import com.xaqb.unlock.Activity.IncomeActivity;
import com.xaqb.unlock.Activity.LoginActivity;
import com.xaqb.unlock.Activity.MyOrderActivity;
import com.xaqb.unlock.Activity.ApproveActivity;
import com.xaqb.unlock.Activity.RealNameInfoActivity;
import com.xaqb.unlock.Activity.ResetPswActivity;
import com.xaqb.unlock.Activity.UpdateActivity;
import com.xaqb.unlock.Activity.UserInfoActivity;
import com.xaqb.unlock.Adapter.LeftMenuListAdapter;
import com.xaqb.unlock.R;
import com.xaqb.unlock.Utils.CircleTransform;
import com.xaqb.unlock.Utils.Globals;
import com.xaqb.unlock.Utils.SPUtils;
import java.io.File;

/**
 * 左滑菜单
 * Created by lenovo on 2016/11/28.
 */
public class LeftFragment extends BaseFragment implements View.OnClickListener {
    private ListView lvLeftMenu;
    private ImageView ivPic, ivEditor;
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
        ivEditor = (ImageView) view.findViewById(R.id.iv_editor);
        llSetting = (LinearLayout) view.findViewById(R.id.ll_setting);
        llCustomer = (LinearLayout) view.findViewById(R.id.ll_customer_left);
        tvNickName = (TextView) view.findViewById(R.id.tv_left_nick_name);
        ivPic.setOnClickListener(this);
        ivEditor.setOnClickListener(this);
        llSetting.setOnClickListener(this);
        llCustomer.setOnClickListener(this);


        lvLeftMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0: // 我的订单
                        startActivity(new Intent(getActivity(), MyOrderActivity.class));
                        break;
                    case 1: // 收入明细
                        startActivity(new Intent(getActivity(), IncomeActivity.class));
                        break;
                    case 2:// 修改密码
                        startActivity(new Intent(getActivity(), ResetPswActivity.class));
                        break;
                    case 3: // 意见反馈
                        startActivity(new Intent(getActivity(), AdviseActivity.class));
                        break;
                    case 4: // 实名认证
                        status = SPUtils.get(getActivity(), "staff_is_real", "").toString();
                        if (status.equals(Globals.staffIsRealNo) || status.equals(Globals.staffIsRealFaild)) {
                        Toast.makeText(getActivity(), "认证失败或未认证，请认证", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getActivity(), ApproveActivity.class));
                        } else if (status.equals(Globals.staffIsRealSuc)) {
                            Toast.makeText(getActivity(), "已经认证成功！在个人信息界面查看详情", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getActivity(), RealNameInfoActivity.class));
                        } else if (status.equals(Globals.staffIsRealIng)) {
                            Toast.makeText(getActivity(), "正在认证中！请耐心等待", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case 5: // 关于我们
                        startActivity(new Intent(getActivity(), AboutActivity.class));
                        break;
                    case 6://检查更新
                        if (readConfig("late").equals("yes")){
                            Toast.makeText(getContext(),"已是最新版本",Toast.LENGTH_SHORT).show();
                        }else{
                            showDialogB(instance,"更新提示",0,"检测到新版本，是否更新","立刻更新","以后再说");
                        }
                    default:
                        break;
                }
            }
        });
    }

    @Override
    public void dialogOkB() {
        super.dialogOkB();
        startActivity(new Intent(getActivity(), UpdateActivity.class));
    }

    /**
     * 加载用户头像
     *
     * @param url 头像链接
     */
    private void loadUserPic(String url) {
        if (url != null && !url.equals(""))
            Picasso.with(getContext())
                    .load(url)
                    .transform(new CircleTransform())//设置为圆形图片
                    .placeholder(R.mipmap.nothing_pic)
                    .error(R.mipmap.failed_pic)
                    .fit()
                    .into(ivPic);
    }


    @Override
    public void onResume() {
        super.onResume();
        lvLeftMenu.setAdapter(new LeftMenuListAdapter(getActivity()));
        url = SPUtils.get(getActivity(), "staff_headpic", "").toString();
        nickname = SPUtils.get(getActivity(), "staff_nickname", "").toString();
        if (nickname != null && !nickname.equals(""))
            tvNickName.setText(nickname);
        else tvNickName.setText("暂无昵称");
        if (SPUtils.get(getActivity(), "userPicLocal", "").toString().equals(Environment.getExternalStorageDirectory() + "/userHead/" + SPUtils.get(getActivity(), "userid", "").toString() + "userHead.jpg")) {
            File f = new File(Environment.getExternalStorageDirectory() + "/userHead/" + SPUtils.get(getActivity(), "userid", "").toString() + "userHead.jpg");
            if (f.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(SPUtils.get(getActivity(), "userPicLocal", "").toString());
                ivPic.setImageBitmap(bitmap);
            }
        } else if (!url.equals("")) {
            loadUserPic(url);
        } else {
            ivPic.setImageResource(R.mipmap.main_user);
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
            case R.id.iv_editor://编辑
                startActivity(new Intent(getActivity(), UserInfoActivity.class));
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
