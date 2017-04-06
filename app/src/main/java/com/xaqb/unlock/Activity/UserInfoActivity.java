package com.xaqb.unlock.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xaqb.unlock.R;
import com.xaqb.unlock.Utils.Base64Utils;
import com.xaqb.unlock.Utils.Globals;
import com.xaqb.unlock.Utils.GsonUtil;
import com.xaqb.unlock.Utils.HttpUrlUtils;
import com.xaqb.unlock.Utils.LogUtils;
import com.xaqb.unlock.Utils.PermissionUtils;
import com.xaqb.unlock.Utils.SDCardUtils;
import com.xaqb.unlock.Utils.SPUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.BitmapCallback;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.RequestBody;


/**
 * Created by chengeng on 2016/12/2.
 * 用户信息页面
 */
public class UserInfoActivity extends BaseActivity {
    private UserInfoActivity instance;
    private ImageView ivPic;
    private TextView tvPhone, tvNickName, tvCompany, tvAddress, tvMessage;
    private LinearLayout llUserPic, llNickName;
    private WindowManager.LayoutParams params;
    private PopupWindow popupWindow;
    private View layout, vPart; // pop的布局
    private LayoutInflater inflater;
    private Bitmap head;
    private RelativeLayout rlName, rlPicFromSdcard, rlTakePic, rlCancle;
    private String nickname, url, phone, company, address, message;
    private boolean isPicChange = false;

    @Override
    public void initTitleBar() {
        setTitle("个人信息");
        showBackwardView(true);
    }

    @Override
    public void initViews() {
        setContentView(R.layout.userinfo_activity);
        instance = this;
        assignViews();
    }

    private void assignViews() {
        params = getWindow().getAttributes();
        ivPic = (ImageView) findViewById(R.id.iv_user_pic);
        tvPhone = (TextView) findViewById(R.id.tv_userinfo_phone);
        tvNickName = (TextView) findViewById(R.id.tv_userinfo_name);
        tvCompany = (TextView) findViewById(R.id.tv_userinfo_company);
        tvAddress = (TextView) findViewById(R.id.tv_userinfo_address);
        tvMessage = (TextView) findViewById(R.id.tv_userinfo_message);
        llUserPic = (LinearLayout) findViewById(R.id.ll_user_pic);
        llNickName = (LinearLayout) findViewById(R.id.ll_myinfo_nick_name);
        initContentsPop();
    }

    @Override
    public void initData() {
        LogUtils.i(HttpUrlUtils.getHttpUrl().getUserInfo() + SPUtils.get(instance, "userid", "") + "?access_token=" + SPUtils.get(instance, "access_token", ""));
        OkHttpUtils.get()
                .url(HttpUrlUtils.getHttpUrl().getUserInfo() + SPUtils.get(instance, "userid", "") + "?access_token=" + SPUtils.get(instance, "access_token", ""))
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int i) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(String s, int i) {
                Map<String, Object> map = GsonUtil.JsonToMap(s);
                LogUtils.i(map.toString());
                if (map.get("state").toString().equals(Globals.httpSuccessState)) {
                    Map<String, Object> data = GsonUtil.JsonToMap(GsonUtil.GsonString(map.get("table")));
                    LogUtils.i(data.toString());
                    url = data.get("staff_headpic").toString();
                    nickname = data.get("staff_nickname").toString();
                    phone = data.get("staff_mp").toString();
                    company = data.get("staff_company").toString();
                    address = data.get("address").toString();
                    if (textNotEmpty(nickname)) {
                        tvNickName.setText(nickname);
                    }
                    if (textNotEmpty(phone)) {
                        tvPhone.setText(phone);
                    }
                    if (textNotEmpty(company)) {
                        tvCompany.setText(company);
                    }
                    if (textNotEmpty(address)) {
                        tvAddress.setText(address);
                    }
                    if (textNotEmpty(url)) {
                        loadUserPic();
                    }
                } else {
                    showToast(map.get("mess").toString());
                    return;
                }

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        nickname = SPUtils.get(instance, "staff_nickname", "").toString();
        if (nickname != null && !nickname.equals(""))
            tvNickName.setText(nickname);
        else tvNickName.setText("暂无昵称");
        if (isPicChange) {
            url = SPUtils.get(instance, "staff_headpic", "").toString();
            if (SPUtils.get(instance, "userPicLocal", "").toString().equals(Environment.getExternalStorageDirectory() + "/userHead/" + SPUtils.get(instance, "userid", "").toString() + "userHead.jpg")) {
                File f = new File(Environment.getExternalStorageDirectory() + "/userHead/" + SPUtils.get(instance, "userid", "").toString() + "userHead.jpg");
                if (f.exists()) {
                    ivPic.setImageBitmap(BitmapFactory.decodeFile(SPUtils.get(instance, "userPicLocal", "").toString()));
                }
            } else if (!url.equals("")) {
                loadUserPic();
            } else {
                ivPic.setImageResource(R.mipmap.ic_launcher);
            }
            isPicChange = false;
        }
    }

    @Override
    public void addListener() {
        llUserPic.setOnClickListener(instance);
        llNickName.setOnClickListener(instance);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_user_pic:
                // 弹出照片选择框
                params.alpha = 0.7f;
                getWindow().setAttributes(params);
                popupWindow.showAtLocation(findViewById(R.id.ll_main_my_info), Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM,
                        0, 0);
                break;
            case R.id.ll_myinfo_nick_name:
                Intent i = new Intent(instance, ResetNickNameActivity.class);
                i.putExtra("nickName", nickname);
                startActivity(i);
                break;
        }
    }


    private void loadUserPic() {
        if (url != null && !url.equals(""))

            OkHttpUtils
                    .get()
                    .url(url)
                    .build()
                    .execute(new BitmapCallback() {
                        @Override
                        public void onError(Call call, Exception e, int i) {

                        }

                        @Override
                        public void onResponse(Bitmap bitmap, int i) {
                            try {
                                ivPic.setImageBitmap(bitmap);
                            } catch (Exception e) {
                                e.printStackTrace();
                                ivPic.setImageResource(R.mipmap.ic_launcher);
                            }
                        }
                    });

    }

    /**
     * 初始化照片弹窗
     */
    private void initContentsPop() {
        inflater = instance.getLayoutInflater();
        layout = inflater.inflate(R.layout.pop_updateuserinfo, null);
        popupWindow = new PopupWindow(layout, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                params.alpha = 1f;
                getWindow().setAttributes(params);
            }
        });
        popupWindow.setAnimationStyle(R.style.pop_updateuserinfo_anim_style);
        rlPicFromSdcard = (RelativeLayout) layout.findViewById(R.id.rl_picFromSdcard);
        rlCancle = (RelativeLayout) layout.findViewById(R.id.rl_cancle);
        rlTakePic = (RelativeLayout) layout.findViewById(R.id.rl_picTakePic);

        rlPicFromSdcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(Intent.ACTION_PICK, null);
                intent1.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent1, 1);
            }
        });
        rlTakePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PermissionUtils.requestPermission(instance, PermissionUtils.CODE_CAMERA, mPermissionGrant);

            }
        });
        rlCancle.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                canclePopwindow();
            }
        });

        vPart = layout.findViewById(R.id.view_popContents_hidePart);
        vPart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                canclePopwindow();
            }
        });
    }

    /**
     * 取消照片弹窗
     */
    private void canclePopwindow() {
        if (popupWindow != null && popupWindow.isShowing()) {
            params.alpha = 1f;
            getWindow().setAttributes(params);
            popupWindow.dismiss();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    cropPhoto(data.getData());// 裁剪图片
                }
                break;
            case 2:
                if (resultCode == RESULT_OK) {
                    File temp = new File(Environment.getExternalStorageDirectory() + "/head.jpg");
                    cropPhoto(Uri.fromFile(temp));// 裁剪图片
                }
                break;
            case 3:
                if (data != null) {
                    Bundle extras = data.getExtras();
                    if (extras != null) {
                        head = extras.getParcelable("data");
                        if (head != null) {
                            resetUserPic(Base64Utils.photoToBase64(head, 40));
                        }
                    }
                }
                canclePopwindow();
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    /**
     * 更换头像
     *
     * @param s 头像base64
     */
    private void resetUserPic(String s) {
        LogUtils.i(HttpUrlUtils.getHttpUrl().getUpdataUserinfoUrl() + SPUtils.get(instance, "userid", "").toString() + "?access_token=" + SPUtils.get(instance, "access_token", "").toString());
        loadingDialog.show("正在修改");
        Map<String, String> map = new HashMap<>();
        map.put("headpic", s);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), GsonUtil.GsonString(map));
        OkHttpUtils
                .put()
                .url(HttpUrlUtils.getHttpUrl().getUpdataUserinfoUrl() + SPUtils.get(instance, "userid", "") + "?access_token=" + SPUtils.get(instance, "access_token", "").toString())
                .requestBody(body)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        loadingDialog.dismiss();
                        showToast("网络访问异常");
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        loadingDialog.dismiss();
                        Map<String, Object> map = GsonUtil.JsonToMap(s);
                        LogUtils.i(map.toString());
                        if (map.get("state").toString().equals(Globals.httpSuccessState)) {
                            ivPic.setImageBitmap(head);// 用ImageView显示出来
                            PermissionUtils.requestPermission(instance, PermissionUtils.CODE_WRITE_EXTERNAL_STORAGE, mPermissionGrant);
                            isPicChange = true;
//                        Map<String, Object> map1 = GsonUtil.GsonToMaps(GsonUtil.GsonString(map.get("mess")));
//                        SPUtils.put(instance, "userheadpic", map1.get("userheadpic"));
//                        showToast("同步头像到服务器成功");
                            //再次请求网络，重新获取最新的头像和昵称
                            initData();
                        } else {
                            showToast(map.get("mess").toString());
                            return;
                        }

                    }
                });
    }


    private PermissionUtils.PermissionGrant mPermissionGrant = new PermissionUtils.PermissionGrant() {
        @Override
        public void onPermissionGranted(int requestCode) {
            switch (requestCode) {
                case PermissionUtils.CODE_RECORD_AUDIO:
//                    Toast.makeText(instance, "Result Permission Grant CODE_RECORD_AUDIO", Toast.LENGTH_SHORT).show();
                    break;
                case PermissionUtils.CODE_GET_ACCOUNTS:
//                    Toast.makeText(instance, "Result Permission Grant CODE_GET_ACCOUNTS", Toast.LENGTH_SHORT).show();
                    break;
                case PermissionUtils.CODE_READ_PHONE_STATE:
//                    Toast.makeText(instance, "Result Permission Grant CODE_READ_PHONE_STATE", Toast.LENGTH_SHORT).show();
                    break;
                case PermissionUtils.CODE_CALL_PHONE:
//                    Toast.makeText(instance, "Result Permission Grant CODE_CALL_PHONE", Toast.LENGTH_SHORT).show();
                    break;
                case PermissionUtils.CODE_CAMERA:
                    Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent2.putExtra(MediaStore.EXTRA_OUTPUT,
                            Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "head.jpg")));
                    startActivityForResult(intent2, 2);// 采用ForResult打开
//                    Toast.makeText(instance, "Result Permission Grant CODE_CAMERA", Toast.LENGTH_SHORT).show();
                    break;
                case PermissionUtils.CODE_ACCESS_FINE_LOCATION:
//                    Toast.makeText(instance, "Result Permission Grant CODE_ACCESS_FINE_LOCATION", Toast.LENGTH_SHORT).show();
                    break;
                case PermissionUtils.CODE_ACCESS_COARSE_LOCATION:
//                    Toast.makeText(instance, "Result Permission Grant CODE_ACCESS_COARSE_LOCATION", Toast.LENGTH_SHORT).show();
                    break;
                case PermissionUtils.CODE_READ_EXTERNAL_STORAGE:
//                    Toast.makeText(instance, "Result Permission Grant CODE_READ_EXTERNAL_STORAGE", Toast.LENGTH_SHORT).show();
                    break;
                case PermissionUtils.CODE_WRITE_EXTERNAL_STORAGE:
                    SDCardUtils.saveBmp(head, SPUtils.get(instance, "userid", "").toString() + "userHead");
//                    SDCardUtils.saveImageToGallery(instance, head);
                    SPUtils.put(instance, "userPicLocal", Environment.getExternalStorageDirectory() + "/userHead/" + SPUtils.get(instance, "userid", "").toString() + "userHead.jpg");
//                    Toast.makeText(instance, "Result Permission Grant CODE_WRITE_EXTERNAL_STORAGE", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        PermissionUtils.requestPermissionsResult(this, requestCode, permissions, grantResults, mPermissionGrant);
    }


    /**
     * 调用系统的裁剪
     *
     * @param uri
     */
    public void cropPhoto(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 100);
        intent.putExtra("outputY", 100);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, 3);
    }
}
