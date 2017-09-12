package com.xaqb.unlock.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.xaqb.unlock.R;
import com.xaqb.unlock.Utils.ActivityController;
import com.xaqb.unlock.Utils.Base64Utils;
import com.xaqb.unlock.Utils.Globals;
import com.xaqb.unlock.Utils.GsonUtil;
import com.xaqb.unlock.Utils.HttpUrlUtils;
import com.xaqb.unlock.Utils.IDCardUtils;
import com.xaqb.unlock.Utils.ImageDispose;
import com.xaqb.unlock.Utils.LogUtils;
import com.xaqb.unlock.Utils.PermissionUtils;
import com.xaqb.unlock.Utils.SPUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import okhttp3.Call;


/**
 * Created by chengeng on 2016/12/2.
 * 实名认证页面
 */
public class RealNameActivity extends BaseActivity {
    private RealNameActivity instance;
    private EditText etRealName, etCardNum, etCardNation, etAge;
    //    private ImageView ivCardPic;
    private ImageView ivCertPic, ivFacePic;
    private Button btSubmit;
    private WindowManager.LayoutParams params;
    //    private PopupWindow popupWindow;
    private View layout, vPart; // pop的布局
    private LayoutInflater inflater;
    private Bitmap head;
    private RelativeLayout rlName, rlPicFromSdcard, rlTakePic, rlCancle;
    private boolean isShow;
    private String type, realName, cardNum, cardSex, cardNation, cardAge, certPicPath="", facePicPath="";
    private Spinner spType, spSex;
    private ArrayAdapter spinnerAdapter;
    String[] types = {"身份证", "驾照", "户口本", "军官证", "士兵证", "警官证", "国内护照", "港澳通行证", "其他"};
    private int requestCoede = 0;
    private File temp;
    private static final String PHOTO_FILE_NAME = "temp_photo.jpg";

    @Override
    public void initTitleBar() {
        setTitle("实名认证");
        showBackwardView(true);
    }

    @Override
    public void initViews() {
        setContentView(R.layout.approve_real_name_activity);
        instance = this;
        assignViews();
    }

    private void assignViews() {
        params = getWindow().getAttributes();
        etRealName = (EditText) findViewById(R.id.et_real_name);
        etCardNum = (EditText) findViewById(R.id.et_card_num);
        spSex = (Spinner) findViewById(R.id.sp_sex);
        etAge = (EditText) findViewById(R.id.et_card_age);
        etCardNation = (EditText) findViewById(R.id.et_card_nation);
//        ivCardPic = (ImageView) findViewById(R.id.iv_card_pic);
        ivCertPic = (ImageView) findViewById(R.id.iv_cert_pic);
        ivFacePic = (ImageView) findViewById(R.id.iv_face_pic);
        btSubmit = (Button) findViewById(R.id.bt_submit);
        spType = (Spinner) findViewById(R.id.sp_type);
//        initContentsPop();
    }

    @Override
    public void initData() {
        spinnerAdapter = new ArrayAdapter(instance, R.layout.item_spinner, types);
        spType.setAdapter(spinnerAdapter);
        spType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ArrayAdapter<String> adapter = (ArrayAdapter<String>) adapterView.getAdapter();
                type = adapter.getItem(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                type = "";
            }
        });
    }

    @Override
    public void addListener() {
        ivFacePic.setOnClickListener(instance);
        ivCertPic.setOnClickListener(instance);
        btSubmit.setOnClickListener(instance);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.iv_card_pic:
//                if (!isShow) {
//                    // 弹出照片选择
//                    params.alpha = 0.7f;
//                    getWindow().setAttributes(params);
//                    popupWindow.showAtLocation(findViewById(R.id.ll_approve_main), Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM,
//                            0, 0);
//                } else {
//                    showDialog("提示", "重新选择照片吗？", "确定", "取消", 0);
//                }
//                break;
            case R.id.iv_cert_pic:
                requestCoede = 0;
                checkPer(PermissionUtils.CODE_CAMERA);
                break;
            case R.id.iv_face_pic:
                requestCoede = 1;
                checkPer(PermissionUtils.CODE_CAMERA);
                break;

            case R.id.bt_submit:
                realName = etRealName.getText().toString().trim();
                cardNum = etCardNum.getText().toString().trim();
                cardSex = spSex.getSelectedItem().toString().trim();
                cardNation = etCardNation.getText().toString().trim();
                cardAge = etCardNation.getText().toString().trim();
                if (type == null || type.equals("")) {
                    showToast("请选择证件类型");
                } else if (realName == null || realName.equals("")) {
                    showToast("请输入真实姓名");
                } else if (cardNum == null || cardNum.equals("")) {
                    showToast("请输入证件号码");
                } else if (cardSex == null || cardSex.equals("")) {
                    showToast("请输入性别");
                } else if (cardNation == null || cardNation.equals("")) {
                    showToast("请输入民族");
                } else if (cardAge == null || cardAge.equals("")) {
                    showToast("请输入年龄");
                } else if (!textNotEmpty(certPicPath)) {
                    showToast("请拍摄证件照片");
                } else if (!textNotEmpty(facePicPath)) {
                    showToast("请拍摄人脸照片");
                } else {
                    try {
                        if (type.equals("身份证")) {
                            if (!IDCardUtils.IDCardValidate(cardNum).equals("")) {
                                showToast("请输入正确的证件号码");
                            } else {
                                submit();
                            }
                        } else {
                            submit();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    private void submit() {
        if (!checkNetwork()) {
            showToast(getResources().getString(R.string.network_not_alive));
            return;
        }
        switch (type) {
            case "身份证":
                type = "11";
                break;
            case "驾照":
                type = "94";
                break;
            case "警官证":
                type = "91";
                break;
            case "户口本":
                type = "13";
                break;
            case "士兵证":
                type = "92";
                break;
            case "国内护照":
                type = "93";
                break;
            case "军官证":
                type = "90";
                break;
            case "其他":
                type = "99";
                break;
        }
        if (cardSex.equals("男")) {
            cardSex = "1";
        } else {
            cardSex = "0";
        }
        btSubmit.setEnabled(false);
        LogUtils.i(HttpUrlUtils.getHttpUrl().getRealNameUrl() + "?access_token=" + SPUtils.get(instance, "access_token", ""));
        loadingDialog.show("正在提交");
        OkHttpUtils
                .post()
                .url(HttpUrlUtils.getHttpUrl().getRealNameUrl() + "?access_token=" + SPUtils.get(instance, "access_token", ""))
                .addParams("uid", SPUtils.get(instance, "userid", "").toString())
//                .addParams("type", "1")
                .addParams("name", realName)
                .addParams("certtype", type)
                .addParams("certcode", cardNum)
                .addParams("sex", cardSex)
                .addParams("nation", cardNation)
                .addParams("age", cardAge)
                .addParams("certimg", Base64Utils.photoToBase64(BitmapFactory.decodeFile(certPicPath), 80))
                .addParams("faceimg", Base64Utils.photoToBase64(BitmapFactory.decodeFile(facePicPath), 80))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        loadingDialog.dismiss();
                        showToast("网络访问异常");
                        e.printStackTrace();
                        btSubmit.setEnabled(true);
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        try {
                            loadingDialog.dismiss();
                            btSubmit.setEnabled(true);
                            Map<String, Object> map = GsonUtil.JsonToMap(s);
                            LogUtils.i(map.toString());
                            if (map.get("state").toString().equals(Globals.httpSuccessState)) {
                                switch (map.get("state").toString()) {
                                    case "0":
                                        showToast("上传成功，请等待审核！");
                                        SPUtils.put(instance, "staff_is_real", "2");
                                        finish();
                                        break;
                                    case "1":
                                        showToast(map.get("mess").toString());
//                                showToast("已经认证成功！");
//                                finish();
                                        break;
                                    case "102.0":
                                        showToast("认证中，请等待审核！");
//                                finish();
                                        break;
                                    case "101.0":
                                        showToast("已经认证成功！");
                                        finish();
                                        break;
                                    default:
                                        showToast("认证失败，请核对信息！");
                                        break;
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
                });

    }

    @Override
    protected void requestPerPass(int requestCode) {
        Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        temp = new File(Environment.getExternalStorageDirectory(), System.currentTimeMillis() + PHOTO_FILE_NAME);
        intent2.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(temp));
        startActivityForResult(intent2, 2);// 采用ForResult打开
    }

    //    private void initContentsPop() {
//        inflater = instance.getLayoutInflater();
//        layout = inflater.inflate(R.layout.pop_updateuserinfo, null);
//        popupWindow = new PopupWindow(layout, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        popupWindow.setBackgroundDrawable(new BitmapDrawable());
//        popupWindow.setOutsideTouchable(true);
//        popupWindow.setFocusable(true);
//        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
//            @Override
//            public void onDismiss() {
//                params.alpha = 1f;
//                getWindow().setAttributes(params);
//            }
//        });
//        popupWindow.setAnimationStyle(R.style.pop_updateuserinfo_anim_style);
//        rlPicFromSdcard = (RelativeLayout) layout.findViewById(R.id.rl_picFromSdcard);
//        rlCancle = (RelativeLayout) layout.findViewById(R.id.rl_cancle);
//        rlTakePic = (RelativeLayout) layout.findViewById(R.id.rl_picTakePic);
//
//        rlPicFromSdcard.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent1 = new Intent(Intent.ACTION_PICK, null);
//                intent1.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
//                startActivityForResult(intent1, 1);
//            }
//        });
//        rlTakePic.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                checkPer();
//            }
//        });
//        rlCancle.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                canclePopwindow();
//            }
//        });
//
//        vPart = layout.findViewById(R.id.view_popContents_hidePart);
//        vPart.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                canclePopwindow();
//            }
//        });
//    }

//    private void checkPer() {
//        PermissionUtils.requestPermission(this, PermissionUtils.CODE_CAMERA, mPermissionGrant);
//    }
//
//    private PermissionUtils.PermissionGrant mPermissionGrant = new PermissionUtils.PermissionGrant() {
//        @Override
//        public void onPermissionGranted(int requestCode) {
//            switch (requestCode) {
//                case PermissionUtils.CODE_RECORD_AUDIO:
////                    Toast.makeText(instance, "Result Permission Grant CODE_RECORD_AUDIO", Toast.LENGTH_SHORT).show();
//                    break;
//                case PermissionUtils.CODE_GET_ACCOUNTS:
////                    Toast.makeText(instance, "Result Permission Grant CODE_GET_ACCOUNTS", Toast.LENGTH_SHORT).show();
//                    break;
//                case PermissionUtils.CODE_READ_PHONE_STATE:
////                    Toast.makeText(instance, "Result Permission Grant CODE_READ_PHONE_STATE", Toast.LENGTH_SHORT).show();
//                    break;
//                case PermissionUtils.CODE_CALL_PHONE:
////                    Toast.makeText(instance, "Result Permission Grant CODE_CALL_PHONE", Toast.LENGTH_SHORT).show();
//                    break;
//                case PermissionUtils.CODE_CAMERA:
//                    Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                    intent2.putExtra(MediaStore.EXTRA_OUTPUT,
//                            Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "card.jpg")));
//                    startActivityForResult(intent2, 2);// 采用ForResult打开
////                    Toast.makeText(instance, "Result Permission Grant CODE_CAMERA", Toast.LENGTH_SHORT).show();
//                    break;
//                case PermissionUtils.CODE_ACCESS_FINE_LOCATION:
////                    Toast.makeText(instance, "Result Permission Grant CODE_ACCESS_FINE_LOCATION", Toast.LENGTH_SHORT).show();
//                    break;
//                case PermissionUtils.CODE_ACCESS_COARSE_LOCATION:
////                    Toast.makeText(instance, "Result Permission Grant CODE_ACCESS_COARSE_LOCATION", Toast.LENGTH_SHORT).show();
//                    break;
//                case PermissionUtils.CODE_READ_EXTERNAL_STORAGE:
////                    Toast.makeText(instance, "Result Permission Grant CODE_READ_EXTERNAL_STORAGE", Toast.LENGTH_SHORT).show();
//                    break;
//                case PermissionUtils.CODE_WRITE_EXTERNAL_STORAGE:
////                    Toast.makeText(instance, "Result Permission Grant CODE_WRITE_EXTERNAL_STORAGE", Toast.LENGTH_SHORT).show();
//                    break;
//                default:
//                    break;
//            }
//        }
//    };

//    /**
//     * Callback received when a permissions request has been completed.
//     */
//    @Override
//    public void onRequestPermissionsResult(final int requestCode, @NonNull String[] permissions,
//                                           @NonNull int[] grantResults) {
//        PermissionUtils.requestPermissionsResult(this, requestCode, permissions, grantResults, mPermissionGrant);
//    }

//    private void canclePopwindow() {
//        if (popupWindow != null && popupWindow.isShowing()) {
//            params.alpha = 1f;
//            getWindow().setAttributes(params);
//            popupWindow.dismiss();
//        }
//    }

//    @Override
//    protected void dialogOk() {
//        super.dialogOk();
//        // 弹出照片选择
//        params.alpha = 0.7f;
//        getWindow().setAttributes(params);
//        popupWindow.showAtLocation(findViewById(R.id.ll_approve_main), Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM,
//                0, 0);
//    }

    /**
     * 判断sdcard是否被挂载
     */

    private boolean hasSdcard() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    public String getMyFileNameDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        return sdf.format(new Date());
    }

    /**
     * 保存图片
     *
     * @param path 存储路径
     */
    private void savaImage(String path) {
        String fileName = getMyFileNameDate();
        String PHOTO_COMM_NAME = Environment.getExternalStorageDirectory() + "/unlock/" + fileName + ".jpg";
        File dir = new File(Environment.getExternalStorageDirectory() + "/unlock/");

        if (!dir.exists()) {
            dir.mkdir();
        }

        Bitmap bitmap = ImageDispose.caculateInSampleSize(path, 480, 800); //将图片的长和宽缩小

        /**
         * 把图片旋转为正的方向
         */
//        bitmap = ImageDispose.rotaingImageView(degree, bitmap);
//        Bitmap new_bitmap = ImageDispose.compressImage(bitmap);
        ImageDispose.saveBitmapFile(PHOTO_COMM_NAME, bitmap);

        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(new File(PHOTO_COMM_NAME));
        intent.setData(uri);
        instance.sendBroadcast(intent);//这个广播的目的就是更新图库，发了这个广播进入相册就可以找到你保存的图片了！，记得要传你更新的file哦
        if (bitmap != null) {
            bitmap.recycle();
        }
        File oldFile = new File(path);
        if (oldFile.exists()) {
//            Logger.d(" oldFile.delete()=" + oldFile.delete());
            Intent intent_delete = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri uri_delete = Uri.fromFile(oldFile);
            intent.setData(uri_delete);
            instance.sendBroadcast(intent_delete);//这个广播的目的就是更新图库，发了这个广播进入相册就可以找到你保存的图片了！，记得要传你更新的file哦
        }
        getImage(PHOTO_COMM_NAME);
//        if (file.length() / 1024 / 1024 >= 5) {
//            showToast("文件不能大于5M");
//            return;
//        }

    }

    public void getImage(String path) {
        switch (requestCoede) {
            case 0:
                certPicPath = path;
                ivCertPic.setImageBitmap(BitmapFactory.decodeFile(path));
                break;
            case 1:
                facePicPath = path;
                ivFacePic.setImageBitmap(BitmapFactory.decodeFile(path));
                break;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 2:
                if (resultCode == RESULT_OK) {
                    // 从相机返回的数据
                    if (hasSdcard()) {
                        savaImage(temp.getPath());
                    } else {
                        Toast.makeText(instance, "未找到存储卡，无法存储照片！", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
//            case 1:
//                if (resultCode == RESULT_OK) {
//                    cropPhoto(data.getData());// 裁剪图片
//                }
//                break;
//            case 2:
//                if (resultCode == RESULT_OK) {
////                    File temp = new File(Environment.getExternalStorageDirectory() + "/card.jpg");
//                    Bitmap bitmap = ImageDispose.caculateInSampleSize(Environment.getExternalStorageDirectory() + "/card.jpg", 480, 800);
//                    ImageDispose.saveBitmapFile(Environment.getExternalStorageDirectory() + "/card.jpg", bitmap);
//                    Uri uri = Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/card.jpg"));
//                    cropPhoto(uri);// 裁剪图片
//                }
//                break;
//            case 3:
//                if (data != null) {
//                    Bundle extras = data.getExtras();
//                    if (extras != null) {
//                        head = extras.getParcelable("data");
//                        if (head != null) {
//                            ivCardPic.setImageBitmap(head);// 用ImageView显示出来
//                            isShow = true;
//                        }
//                    }
//                }
//                canclePopwindow();
//                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

//    /**
//     * 调用系统的裁剪
//     *
//     * @param uri
//     */
//    public void cropPhoto(Uri uri) {
//        Intent intent = new Intent("com.android.camera.action.CROP");
//        intent.setDataAndType(uri, "image/*");
//        intent.putExtra("crop", "true");
//        // aspectX aspectY 是宽高的比例
//        intent.putExtra("aspectX", 1);
//        intent.putExtra("aspectY", 0.7);
//        // outputX outputY 是裁剪图片宽高
//        intent.putExtra("outputX", 100);
//        intent.putExtra("outputY", 70);
//        intent.putExtra("return-data", true);
//        startActivityForResult(intent, 3);
//    }

    @Override
    protected void onDestroy() {

        File f = new File(Environment.getExternalStorageDirectory() + "/card.jpg");
        if (f.exists()) {
            f.delete();
        }
        f = new File(facePicPath);
        if (f.exists()) {
            f.delete();
        }
        f = new File(certPicPath);
        if (f.exists()) {
            f.delete();
        }

        super.onDestroy();
    }
}
