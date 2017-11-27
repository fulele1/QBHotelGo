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

import com.squareup.picasso.Picasso;
import com.xaqb.unlock.R;
import com.xaqb.unlock.Utils.ActivityController;
import com.xaqb.unlock.Utils.Base64Utils;
import com.xaqb.unlock.Utils.CircleTransform;
import com.xaqb.unlock.Utils.Globals;
import com.xaqb.unlock.Utils.GsonUtil;
import com.xaqb.unlock.Utils.HttpUrlUtils;
import com.xaqb.unlock.Utils.PermissionUtils;
import com.xaqb.unlock.Utils.QBCallback;
import com.xaqb.unlock.Utils.QBHttp;
import com.xaqb.unlock.Utils.SDCardUtils;
import com.xaqb.unlock.Utils.SPUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;


/**
 * Created by chengeng on 2016/12/2.
 * 用户信息页面
 */
public class UserInfoActivity extends BaseActivity {
    private UserInfoActivity instance;
    private ImageView ivPic, ivScanner;
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
    private PermissionUtils.PermissionGrant mPermissionGrant = new PermissionUtils.PermissionGrant() {
        @Override
        public void onPermissionGranted(int requestCode) {
            switch (requestCode) {
                case PermissionUtils.CODE_RECORD_AUDIO:
                    break;
                case PermissionUtils.CODE_GET_ACCOUNTS:
                    break;
                case PermissionUtils.CODE_READ_PHONE_STATE:
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
        ivScanner = (ImageView) findViewById(R.id.iv_scanner_user_info);
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
        if (!checkNetwork()) {
            showToast(getResources().getString(R.string.network_not_alive));
            return;
        }
        loadingDialog.show("加载中...");
        QBHttp.get(instance,
                HttpUrlUtils.getHttpUrl().getUserInfo() + SPUtils.get(instance, "userid", "") + "?access_token=" + SPUtils.get(instance, "access_token", "")
                , null,
                new QBCallback() {
                    @Override
                    public void doWork(Map<?, ?> map) {
                        try {
                            loadingDialog.dismiss();
                            if (map.get("state").toString().equals(Globals.httpSuccessState)) {
                                url = map.get("staff_headpic").toString();
                                nickname = map.get("staff_nickname").toString();
                                phone = map.get("staff_mp").toString();
                                company = map.get("staff_company").toString();
                                address = map.get("address").toString();
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
                    }

                    @Override
                    public void reDoWork() {

                    }
                });

//        LogUtils.i(HttpUrlUtils.getHttpUrl().getUserInfo() + SPUtils.get(instance, "userid", "") + "?access_token=" + SPUtils.get(instance, "access_token", ""));
//        loadingDialog.show("加载中...");
//        OkHttpUtils.get()
//                .url(HttpUrlUtils.getHttpUrl().getUserInfo() + SPUtils.get(instance, "userid", "") + "?access_token=" + SPUtils.get(instance, "access_token", ""))
//                .build().execute(new StringCallback() {
//            @Override
//            public void onError(Call call, Exception e, int i) {
//                e.printStackTrace();
//                loadingDialog.dismiss();
//                showToast("网络访问异常");
//            }
//
//            @Override
//            public void onResponse(String s, int i) {
//                loadingDialog.dismiss();
//                try {
//                    Map<String, Object> map = GsonUtil.JsonToMap(s);
//                    LogUtils.i(map.toString());
//                    if (map.get("state").toString().equals(Globals.httpSuccessState)) {
//                        url = map.get("staff_headpic").toString();
//                        nickname = map.get("staff_nickname").toString();
//                        phone = map.get("staff_mp").toString();
//                        company = map.get("staff_company").toString();
//                        address = map.get("address").toString();
//                        if (textNotEmpty(nickname)) {
//                            tvNickName.setText(nickname);
//                        }
//                        if (textNotEmpty(phone)) {
//                            tvPhone.setText(phone);
//                        }
//                        if (textNotEmpty(company)) {
//                            tvCompany.setText(company);
//                        }
//                        if (textNotEmpty(address)) {
//                            tvAddress.setText(address);
//                        }
//                        if (textNotEmpty(url)) {
//                            loadUserPic();
//                        }
//                    } else if (map.get("state").toString().equals(Globals.httpTokenFailure)) {
//                        ActivityController.finishAll();
//                        showToast("登录失效，请重新登录");
//                        startActivity(new Intent(instance, LoginActivity.class));
//                    } else {
//                        showToast(map.get("mess").toString());
//                        return;
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
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
        ivScanner.setOnClickListener(instance);
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
            case R.id.iv_scanner_user_info://二维码显示信息   http://www.ddkaisuo.net/home/staff/index?id=2
                startActivity(new Intent(instance, QRCodeActivity.class));
                break;
        }
    }

    /**
     * 加载图片
     */
    private void loadUserPic() {
        if (!checkNetwork()) return;
        if (url != null && !url.equals("")) {
            Picasso.with(instance)
                    .load(url)//图片链接
                    .transform(new CircleTransform())//设置为圆形图片
                    .placeholder(R.mipmap.nothing_pic)//占位图
                    .error(R.mipmap.failed_pic)//加载失败图
                    .into(ivPic);//设置给控件
        }
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
        if (!checkNetwork()) {
            showToast(getResources().getString(R.string.network_not_alive));
            return;
        }
        loadingDialog.show("正在修改");
        Map<String, String> map = new HashMap<>();
        map.put("headpic", s);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), GsonUtil.GsonString(map));
        QBHttp.put(instance,
                HttpUrlUtils.getHttpUrl().getUpdataUserinfoUrl() + SPUtils.get(instance, "userid", "") + "?access_token=" + SPUtils.get(instance, "access_token", "").toString()
                , body
                , new QBCallback() {
                    @Override
                    public void doWork(Map<?, ?> map) {
                        try {
                            loadingDialog.dismiss();
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
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void doError(Exception e) {

                    }

                    @Override
                    public void reDoWork() {

                    }
                });
    }

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
