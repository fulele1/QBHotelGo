package com.xaqb.unlock.Activity;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.YinanSoft.CardReaders.A9LReader;
import com.YinanSoft.CardReaders.IDCardInfo;
import com.YinanSoft.CardReaders.IDCardReader;
import com.YinanSoft.CardReaders.Utils.CertImgDisposeUtils;
import com.YinanSoft.CardReaders.Utils.FileUnits;
import com.YinanSoft.CardReaders.Utils.ToastUtil;
import com.xaqb.unlock.R;
import com.xaqb.unlock.Utils.ActivityController;
import com.xaqb.unlock.Utils.Base64Utils;
import com.xaqb.unlock.Utils.Globals;
import com.xaqb.unlock.Utils.GsonUtil;
import com.xaqb.unlock.Utils.HttpUrlUtils;
import com.xaqb.unlock.Utils.ImageDispose;
import com.xaqb.unlock.Utils.LogUtils;
import com.xaqb.unlock.Utils.PermissionUtils;
import com.xaqb.unlock.Utils.SPUtils;
import com.xaqb.unlock.Utils.ToolsUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;


/**
 * Created by chengeng on 2016/12/2.
 * 实名认证页面
 */
public class RealNameActivityNew extends BaseActivity {
    private RealNameActivityNew instance;
    private EditText etRealName, etCardNum;
    private ImageView ivCardPic, ivCertPic, ivFacePhoto, ivFacePic;
    private Button btSubmit, btRead, btFace;
    private WindowManager.LayoutParams params;
    private PopupWindow popupWindow;
    private View layout, vPart; // pop的布局
    private LayoutInflater inflater;
    private Bitmap head;
    private RelativeLayout rlName, rlPicFromSdcard, rlTakePic, rlCancle;
    private boolean isShow;
    private String type, realName, cardNum, sex, nation;
    private Spinner spType;
    private ArrayAdapter spinnerAdapter;
    private int score;

    /**
     * 身份证读取相关
     */
    private IDCardReader idReader = null;
    private IDCardInfo idCardInfo;
    private Bitmap bitmapCert;
    private Bitmap bitmapFace;
    private Bitmap bitmapRealFace;
    private String path = "YinanSofttt";
    private String filename = "armidse.bin";
    private String sStatus;
    private byte[] bt2 = new byte[96];
    private boolean isReadCard, isConfigFace;
    private ProgressDialog progressDialog;
    private TextView tvReadResult;
    private int dialogType = 0;

    String[] types = {"身份证", "驾照", "户口本", "军官证", "士兵证", "警官证", "国内护照", "港澳通行证", "其他"};

    @Override
    public void initTitleBar() {
        setTitle("实名认证");
        showBackwardView(true);
    }

    @Override
    public void initViews() {
        setContentView(R.layout.approve_real_name_activity_new);
        instance = this;
        assignViews();
        initVar();
        initCardReaderAndFingerPrinter();
    }

    private void assignViews() {

        progressDialog = new ProgressDialog(instance);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("正在读取，请将身份证放置到扫描区域");
        progressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                stopTimerTask();
            }
        });
        params = getWindow().getAttributes();
        etRealName = (EditText) findViewById(R.id.et_real_name);
        etCardNum = (EditText) findViewById(R.id.et_card_num);
//        ivCardPic = (ImageView) findViewById(R.id.iv_card_pic);
        ivCertPic = (ImageView) findViewById(R.id.iv_cert_pic);
        ivFacePhoto = (ImageView) findViewById(R.id.iv_face_photo);
        ivFacePic = (ImageView) findViewById(R.id.iv_Face_pic);
        btSubmit = (Button) findViewById(R.id.bt_submit);
        btRead = (Button) findViewById(R.id.btn_readIDCard);
        btFace = (Button) findViewById(R.id.btn_faceReg);
        spType = (Spinner) findViewById(R.id.sp_type);
        tvReadResult = (TextView) findViewById(R.id.tv_result);
        initContentsPop();
    }

    @Override
    public void initData() {
//        spinnerAdapter = new ArrayAdapter(instance, R.layout.item_spinner, types);
//        spType.setAdapter(spinnerAdapter);
//        spType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                ArrayAdapter<String> adapter = (ArrayAdapter<String>) adapterView.getAdapter();
//                type = adapter.getItem(i);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//                type = "";
//            }
//        });
    }

    @Override
    public void addListener() {
//        ivCardPic.setOnClickListener(instance);
        btSubmit.setOnClickListener(instance);
        btRead.setOnClickListener(instance);
        btFace.setOnClickListener(instance);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_card_pic:
                if (!isShow) {
                    // 弹出照片选择
                    params.alpha = 0.7f;
                    getWindow().setAttributes(params);
                    popupWindow.showAtLocation(findViewById(R.id.ll_approve_main), Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM,
                            0, 0);
                } else {
                    dialogType = 0;
                    showDialog("提示", "重新选择照片吗？", "确定", "取消", 0);
                }
                break;
            case R.id.bt_submit:

                if (!isReadCard) {
                    showToast("请先读取身份证信息");
                } else if (!isConfigFace) {
                    showToast("请先识别人脸信息");
                } else {
                    submit();
                }


//                realName = etRealName.getText().toString().trim();
//                cardNum = etCardNum.getText().toString().trim();
//                if (type == null || type.equals("")) {
//                    showToast("请选择证件类型");
//                } else if (realName == null || realName.equals("")) {
//                    showToast("请输入真实姓名");
//                } else if (cardNum == null || cardNum.equals("")) {
//                    showToast("请输入证件号码");
//                } else if (!isShow) {
//                    showToast("请拍摄证件照片");
//                } else {
//                    try {
//                        if (type.equals("身份证")) {
//                            if (!IDCardUtils.IDCardValidate(cardNum).equals("")) {
//                                showToast("请输入正确的证件号码");
//                            } else {
//                                submit();
//                            }
//                        } else {
//                            submit();
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
                break;
            case R.id.btn_readIDCard:
                readIDCard();
                break;
            case R.id.btn_faceReg:
                faceReg();
                break;
        }
    }

    public void faceReg() {
        if (!isReadCard) {
            ToastUtil.showToast(instance, "请先读卡");
            return;
        }
        if (bitmapFace == null) {
            ToastUtil.showToast(instance, "获取身份图片错误...");
            return;
        }
        try {
            ComponentName componetName = new ComponentName(
                    //这个是另外一个应用程序的包名
                    "com.yinan.facerecognition",
                    //这个参数是要启动的Activity
                    "com.yinan.facerecognition.activity.FaceTestActivity");
            Intent intent = new Intent();
            intent.putExtra("send_picture", CertImgDisposeUtils.bitmaptoString(bitmapFace));
            intent.putExtra("camera_id", 0);
            intent.setComponent(componetName);
            startActivityForResult(intent, 1111);
        } catch (ActivityNotFoundException e) {
            ToastUtil.showToast(instance, "请安装人脸识别应用。");
            return;
        } catch (Exception e) {
            ToastUtil.showToast(instance, "打开应用错误。");
            return;
        }
    }


    //读卡
    private void readIDCard() {
        if (idReader == null) return;
        sStatus = "";
        if (!isReadCard) {
            startTimerTask();
        } else {
            dialogType = 1;
            showDialog("提示", "重新扫描身份证吗？", "确定", "取消", 0);
        }
//        if (!idReader.InitReader(bt2)) {
//            ToastUtil.showToast(MainActivity.this, "授权失败");
//            return;
//        }
    }

    private Handler cardHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                LogUtils.i("执行一次身份证读取");
                idCardInfo = idReader.ReadAllCardInfo(new String[1]);

                if (idCardInfo != null) {
                    isReadCard = true;
                    sStatus += "读卡成功\n";
                    try {
                        progressDialog.dismiss();
                        LogUtils.i(idCardInfo.getName());
                        realName = idCardInfo.getName();
                        sex = idCardInfo.getGender();
                        nation = idCardInfo.getNation();
                        cardNum = idCardInfo.getCardNum();
//                        etUserName.setText(idCardInfo.getName());
                        bitmapCert = new CertImgDisposeUtils(instance).creatBitmap(idCardInfo, true);
                        bitmapFace = idCardInfo.getPhoto();
                        ivFacePhoto.setImageBitmap(bitmapFace);
                        if (bitmapCert != null) {
                            bitmapCert = ToolsUtils.drawText(bitmapCert, getString(R.string.logo), 50);
                            ivCertPic.setImageBitmap(bitmapCert);
                        }
//                        bitmapCert = new CertImgDisposeUtils(instance).creatBitmap(idCardInfo, false);
//                        if (bitmapCert != null) {
//                            bitmapCert = ToolsUtils.drawText(bitmapCert, getString(R.string.logo), 50);
//                        }
//                        userCertNum = idCardInfo.getCardNum();
//                        userSex = idCardInfo.getGender();
                        if (sex.equals("男")) {
                            sex = "1";
                        } else {
                            sex = "0";
                        }
//                        idAddress = idCardInfo.getAddress();
//                        userNation = idCardInfo.getNation();
//                        etUserCertNum.setText(ToolsUtils.certNumEncryption(userCertNum));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    if (!isReadCard) {
                        if (!progressDialog.isShowing())
                            progressDialog.show();
                    }
                }
                tvReadResult.setTextColor(Color.BLUE);
                tvReadResult.setText(sStatus);
            }
        }

    };
    private Timer timer;

    //任务
    private TimerTask task;

    //停止timertask任务运行
    private void stopTimerTask() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    //开始timertask任务运行
    private void startTimerTask() {
        timer = new Timer(true);
        task = new TimerTask() {
            public void run() {
                Message msg = new Message();
                msg.what = 1;
                cardHandler.sendMessage(msg);
            }
        };
        timer.schedule(task, 0, 1000);
    }

    private void submit() {
        if (!checkNetwork()) {
            showToast(getResources().getString(R.string.network_not_alive));
            return;
        }
//        switch (type) {
//            case "身份证":
//                type = "11";
//                break;
//            case "驾照":
//                type = "94";
//                break;
//            case "警官证":
//                type = "91";
//                break;
//            case "户口本":
//                type = "13";
//                break;
//            case "士兵证":
//                type = "92";
//                break;
//            case "国内护照":
//                type = "93";
//                break;
//            case "军官证":
//                type = "90";
//                break;
//            case "其他":
//                type = "99";
//                break;
//        }
        btSubmit.setEnabled(false);
        LogUtils.i(HttpUrlUtils.getHttpUrl().getRealNameUrl() + "?access_token=" + SPUtils.get(instance, "access_token", ""));
        loadingDialog.show("正在提交");
        OkHttpUtils
                .post()
                .url(HttpUrlUtils.getHttpUrl().getRealNameUrl() + "?access_token=" + SPUtils.get(instance, "access_token", ""))
                .addParams("uid", SPUtils.get(instance, "userid", "").toString())
//                .addParams("type", "1")
                .addParams("name", realName)
                .addParams("certtype", "11")
                .addParams("certcode", cardNum)
                .addParams("sex", sex)
                .addParams("nation", nation)
                .addParams("certimg", Base64Utils.photoToBase64(bitmapCert, 80))
                .addParams("faceimg", Base64Utils.photoToBase64(bitmapRealFace, 80))
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
                checkPer();
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

    private void checkPer() {
        PermissionUtils.requestPermission(this, PermissionUtils.CODE_CAMERA, mPermissionGrant);
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
                            Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "card.jpg")));
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

    private void canclePopwindow() {
        if (popupWindow != null && popupWindow.isShowing()) {
            params.alpha = 1f;
            getWindow().setAttributes(params);
            popupWindow.dismiss();
        }
    }

    @Override
    protected void dialogOk() {
        super.dialogOk();
        switch (dialogType) {
            case 0:
                // 弹出照片选择
                params.alpha = 0.7f;
                getWindow().setAttributes(params);
                popupWindow.showAtLocation(findViewById(R.id.ll_approve_main), Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM,
                        0, 0);
                break;
            case 1:
                isReadCard = false;
                startTimerTask();
                break;
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
//                    File temp = new File(Environment.getExternalStorageDirectory() + "/card.jpg");
                    Bitmap bitmap = ImageDispose.caculateInSampleSize(Environment.getExternalStorageDirectory() + "/card.jpg", 480, 800);
                    ImageDispose.saveBitmapFile(Environment.getExternalStorageDirectory() + "/card.jpg", bitmap);
                    Uri uri = Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/card.jpg"));
                    cropPhoto(uri);// 裁剪图片
                }
                break;
            case 3:
                if (data != null) {
                    Bundle extras = data.getExtras();
                    if (extras != null) {
                        head = extras.getParcelable("data");
                        if (head != null) {
                            ivCardPic.setImageBitmap(head);// 用ImageView显示出来
                            isShow = true;
                        }
                    }
                }
                canclePopwindow();
                break;
            case 1111:
                if (data == null) return;
                idCardInfo = null;
                if (!TextUtils.isEmpty(data.getStringExtra("face_picture"))) {
//                img_pic.setImageBitmap(CertImgDisposeUtils.convertStringToIcon(data.getStringExtra("face_picture")));
                    String picPath = data.getStringExtra("face_picture");
                    Log.i("", "图片路径： " + picPath);
                    File file = new File(picPath);
                    File fff = new File("/sdcard/YinAnFace/bmp.jpg");
                    if (fff.exists()) {
                        Log.i("fff", fff.toString());
                    }
                    if (file.exists()) {
                        Bitmap bm = BitmapFactory.decodeFile(picPath);
                        bm = ToolsUtils.drawText(bm, getString(R.string.logo), 100);
                        ivFacePic.setImageBitmap(bm);
                        bitmapRealFace = bm;
                    }
                }
                if (data.getIntExtra("back_info", -3) != -3) {
                    int score = data.getIntExtra("back_info", -3);
                    ToastUtil.showToast(instance, "识别分数: " + score);
                    if (score == -1) {
                        sStatus = "人脸识别超时";
                        isConfigFace = false;
                        tvReadResult.setTextColor(Color.RED);
                    } else if (score >= 55) {   //50可自定义（推荐45,55,70，分别对应宽松、正常、严格）
                        sStatus = "人脸识别成功(" + score + "分)";
                        tvReadResult.setTextColor(Color.BLUE);
                        isConfigFace = true;
                    } else {
                        isConfigFace = false;
                        sStatus = "人脸识别失败(" + score + "分)";
                        tvReadResult.setTextColor(Color.RED);
                    }
                    tvReadResult.setText(sStatus);
                } else {
                    ToastUtil.showToast(instance, "识别分数:0 ");
                }
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
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
        intent.putExtra("aspectY", 0.7);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 100);
        intent.putExtra("outputY", 70);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, 3);
    }

    @Override
    protected void onDestroy() {
        File f = new File(Environment.getExternalStorageDirectory() + "/card.jpg");
        if (f.exists()) {
            f.delete();
        }
        super.onDestroy();
    }

    private void initVar() {
        //初始化授权信息
        initLicFile();
        FileUnits units = new FileUnits();
        //直接读卡文件
        bt2 = units.readSDFile(path, filename);
        if (bt2 == null) {
            Toast.makeText(instance, "授权文件读取失败，请保证联网更新授权，或者重新安装本应用", Toast.LENGTH_SHORT).show();
        }
    }

    //加载本地授权码
    private void initLicFile() {
        SharedPreferences settings = getSharedPreferences("com.YinanSoft.www",
                MODE_PRIVATE);
        String arm = settings.getString("armidse", "-1");
        if (arm.equals("-1")) {
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("armidse", "1");
            editor.commit();
            try {
                InputStream is = this.getResources().openRawResource(
                        R.raw.armidse);
                FileUnits unit = new FileUnits();
                unit.writeToSDfromInput(path, filename, is);
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void initCardReaderAndFingerPrinter() {
        final long start = System.currentTimeMillis();
        new Thread(new Runnable() {
            @Override
            public void run() {
                initCardReader();
            }
        }).start();
    }

    //初始化读卡器
    private void initCardReader() {
        if (idReader == null) {
            idReader = new A9LReader(instance);
        }
        idReader.PowerOnReader();
        idReader.InitReader(null);
    }

    //释放读卡器
    public void releaseCardReader(boolean poweroff) {
        if (idReader != null) {
            if (poweroff) idReader.PowerOffReader();
            idReader.ReleaseReader();
        }
    }
}
