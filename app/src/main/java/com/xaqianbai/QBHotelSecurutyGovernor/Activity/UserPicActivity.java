package com.xaqianbai.QBHotelSecurutyGovernor.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.xaqianbai.QBHotelSecurutyGovernor.R;
import com.xaqianbai.QBHotelSecurutyGovernor.Utils.Base64Utils;
import com.xaqianbai.QBHotelSecurutyGovernor.Utils.GsonUtil;
import com.xaqianbai.QBHotelSecurutyGovernor.Utils.HttpUrlUtils;
import com.xaqianbai.QBHotelSecurutyGovernor.Utils.LogUtils;
import com.xaqianbai.QBHotelSecurutyGovernor.Utils.PermissionUtils;
import com.xaqianbai.QBHotelSecurutyGovernor.Utils.SDCardUtils;
import com.xaqianbai.QBHotelSecurutyGovernor.Utils.SPUtils;
import com.xaqianbai.QBHotelSecurutyGovernor.Utils.StatuBarUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.Map;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.Call;

public class UserPicActivity extends BaseActivityNew {
    @BindView(R.id.tv_title)
    TextView title;
    @BindView(R.id.img_pic_user_pic)
    ImageView img_pic;
    @BindView(R.id.tv_forward)
    TextView tv_forward;
    Uri uri;
    Bitmap mBitmap;
    private Unbinder unbinder;
    private UserPicActivity instance;
    private WindowManager.LayoutParams params;
    private PopupWindow popupWindow;
    private View layout, vPart; // pop的布局
    private LayoutInflater inflater;
    private Bitmap head;
    private RelativeLayout rlName, rlPicFromSdcard, rlTakePic, rlCancle;
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
//                    intent2.putExtra(MediaStore.EXTRA_OUTPUT,
//                            Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "head.jpg")));
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void initViews() throws Exception {
        setContentView(R.layout.activity_user_pic);
        instance = this;
        unbinder = ButterKnife.bind(instance);
        StatuBarUtil.setStatuBarLightMode(instance, getResources().getColor(R.color.white));//修改状态栏字体颜色为黑色
        title.setText("个人头像");
        params = getWindow().getAttributes();
        String url = SPUtils.get(instance, "ou_headpic", "http").toString();
        if (url != null && !url.equals("")) {
            Glide.with(instance)
                    .load(url)
                    .error(R.mipmap.per)
                    .placeholder(R.mipmap.ic_launcher)
                    .into(img_pic);
        }
        tv_forward.setVisibility(View.VISIBLE);
        tv_forward.setText("• • •");
        initContentsPop();
    }

    @Override
    public void initData() throws Exception {

    }

    @Override
    public void addListener() throws Exception {

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
        rlPicFromSdcard = (RelativeLayout) layout.findViewById(R.id.rl_picFromSdcard);//手机相册
        rlCancle = (RelativeLayout) layout.findViewById(R.id.rl_cancle);//取消
        rlTakePic = (RelativeLayout) layout.findViewById(R.id.rl_picTakePic);//拍照

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
//                PermissionUtils.requestPermission(instance, PermissionUtils.CODE_CAMERA, mPermissionGrant);
                Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent2, 2);// 采用ForResult打开



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
     * 4
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
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1://图库
                if (data != null) {
                    uri = data.getData();
                    img_pic.setImageURI(uri);
                    mBitmap = ((BitmapDrawable) ((ImageView) img_pic).getDrawable()).getBitmap();
                    resetUserPic(Base64Utils.photoToBase64(mBitmap, 100));
                    LogUtils.e(Base64Utils.photoToBase64(mBitmap, 100));
                    canclePopwindow();
                }

                break;

            case 2://相机
                if (data !=null){
                    Bundle bundle = data.getExtras();
                    mBitmap = (Bitmap) bundle.get("data");
                    img_pic.setImageBitmap(mBitmap);
                    resetUserPic(Base64Utils.photoToBase64(mBitmap, 100));
                    LogUtils.e(Base64Utils.photoToBase64(mBitmap, 100));
                    canclePopwindow();
                }

                break;
            default:
                break;
        }

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
        OkHttpUtils
                .post()
                .url(HttpUrlUtils.getHttpUrl().getUpdataUserinfoUrl() + SPUtils.get(instance, "ou_id", "") +
                        "?access_token=" + SPUtils.get(instance, "access_token", ""))
                .addParams("ou_headpic", s)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        loadingDialog.dismiss();
                        showToast("网络连接失败");
                        LogUtils.e(e.toString());
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        LogUtils.e("接口" + s);
                        try {
                            loadingDialog.dismiss();
                            Map<String, Object> map = GsonUtil.JsonToMap(s);
                            if (map.get("state").toString().equals("1")) {
                                showToast(map.get("mess").toString());
                                LogUtils.e("mess1" + map.get("mess").toString());
                                return;
                            } else if (map.get("state").toString().equals("0")) {
                                LogUtils.e("mess0" + map.get("mess").toString());
                                showToast("修改头像成功");
                                SPUtils.put(instance, "ou_headpic", HttpUrlUtils.getHttpUrl().getOuPic()+
                                        SPUtils.get(instance,"ou_id","")+"/ou_headpic"+
                                        "?access_token=" + SPUtils.get(instance, "access_token", "")+"&qq="+ new Random().nextInt());

                                LogUtils.e(HttpUrlUtils.getHttpUrl().getOuPic()+
                                        SPUtils.get(instance,"ou_id","")+"/ou_headpic"+
                                        "?access_token=" + SPUtils.get(instance, "access_token", "")+"&qq="+ new Random().nextInt());
                            }else if (map.get("state").toString().equals("10")) {
                                //响应失败
                                Toast.makeText(instance, map.get("mess").toString(), Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(instance,LoginActivity.class));
                                finish();
                            }
                        } catch (Exception e) {
                            showToast("网络连接异常");
                        }
                    }
                });
    }

    @Override
    public void onForward(View forwardView) {
        super.onForward(forwardView);
        // 弹出照片选择框
        params.alpha = 0.7f;
        getWindow().setAttributes(params);
        popupWindow.showAtLocation(findViewById(R.id.activity_user_pic), Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM,
                0, 0);
    }
}
