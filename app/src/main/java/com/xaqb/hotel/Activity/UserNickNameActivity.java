package com.xaqb.hotel.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xaqb.hotel.R;
import com.xaqb.hotel.Utils.Base64Utils;
import com.xaqb.hotel.Utils.Globals;
import com.xaqb.hotel.Utils.GsonUtil;
import com.xaqb.hotel.Utils.HttpUrlUtils;
import com.xaqb.hotel.Utils.LogUtils;
import com.xaqb.hotel.Utils.PermissionUtils;
import com.xaqb.hotel.Utils.QBCallback;
import com.xaqb.hotel.Utils.QBHttp;
import com.xaqb.hotel.Utils.SDCardUtils;
import com.xaqb.hotel.Utils.SPUtils;
import com.xaqb.hotel.Utils.StatuBarUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class UserNickNameActivity extends BaseActivityNew {
    private Unbinder unbinder;
    private UserNickNameActivity instance;
    @BindView(R.id.tv_title)
    TextView title;
    @BindView(R.id.et_user_nick_name)
    EditText et_user_nick_name;
    @BindView(R.id.tv_forward)
    TextView tv_forward;
    String name;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void initViews() throws Exception {
        setContentView(R.layout.activity_user_nick_name);
        instance = this;
        unbinder = ButterKnife.bind(instance);
        StatuBarUtil.setStatuBarLightMode(instance,getResources().getColor(R.color.white));//修改状态栏字体颜色为黑色
        title.setText("设置名称");
        tv_forward.setText("完成");
        name = SPUtils.get(instance, "ou_nickname", "").toString();
        et_user_nick_name.setText(name);

        et_user_nick_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tv_forward.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }


    @Override
    public void initData() throws Exception {

    }

    @Override
    public void addListener() throws Exception {

    }

    @Override
    public void onForward(View forwardView) {
        super.onForward(forwardView);
        resetUserPic();
    }

    /**
     * 更换头像
     *
     *
     */
    private void resetUserPic() {
        final String newName = et_user_nick_name.getText().toString().trim();
        if (name.equals(newName)){
            showToast("与原始昵称一致");
            return;
        }

        if (!checkNetwork()) {
            showToast(getResources().getString(R.string.network_not_alive));
            return;
        }
        loadingDialog.show("正在修改");
        OkHttpUtils
                .post()
                .url(HttpUrlUtils.getHttpUrl().getUpdataUserinfoUrl() +SPUtils.get(instance, "ou_id", "")+
                        "?access_token=" + SPUtils.get(instance, "access_token", ""))
                .addParams("ou_nickname",newName)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        loadingDialog.dismiss();
                        showToast(e.toString());
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        LogUtils.e("接口"+s);
                        try{
                            loadingDialog.dismiss();
                            Map<String, Object> map = GsonUtil.JsonToMap(s);
                            if (map.get("state").toString().equals("1")) {
                                showToast(map.get("mess").toString());
                                LogUtils.e("mess1"+map.get("mess").toString());
                                return;
                            } else if (map.get("state").toString().equals("0")) {
                                LogUtils.e("mess0"+map.get("mess").toString());
                                showToast("修改昵称成功");
                                SPUtils.put(instance,"ou_nickname",newName);
                            }else if (map.get("state").toString().equals("10")) {
                                //响应失败
                                Toast.makeText(instance, map.get("mess").toString(), Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(instance,LoginActivity.class));
                                finish();
                            }
                        }catch (Exception e){
                            showToast(e.toString());
                        }
                    }
                });
    }
}