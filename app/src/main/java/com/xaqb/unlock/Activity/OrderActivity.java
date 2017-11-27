package com.xaqb.unlock.Activity;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
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
import android.widget.TextView;
import android.widget.Toast;

import com.YinanSoft.CardReaders.A9LReader;
import com.YinanSoft.CardReaders.IDCardInfo;
import com.YinanSoft.CardReaders.IDCardReader;
import com.YinanSoft.CardReaders.Utils.CertImgDisposeUtils;
import com.YinanSoft.CardReaders.Utils.FileUnits;
import com.YinanSoft.CardReaders.Utils.ToastUtil;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.xaqb.unlock.R;
import com.xaqb.unlock.Utils.Base64Utils;
import com.xaqb.unlock.Utils.Globals;
import com.xaqb.unlock.Utils.GsonUtil;
import com.xaqb.unlock.Utils.HttpUrlUtils;
import com.xaqb.unlock.Utils.ImageDispose;
import com.xaqb.unlock.Utils.PermissionUtils;
import com.xaqb.unlock.Utils.SDCardUtils;
import com.xaqb.unlock.Utils.SPUtils;
import com.xaqb.unlock.Utils.ToolsUtils;
import com.xaqb.unlock.zxing.activity.CaptureActivity;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;

/**
 * Created by lenovo on 2016/11/22.
 * 下单页面
 */
public class OrderActivity extends BaseActivity {
    private static final String PHOTO_FILE_NAME = "temp_photo.jpg";
    /**
     * 高德地图相关
     */
    //声明mLocationOption对象
    public AMapLocationClientOption mLocationOption = null;
    //读身份证相关方法
    String path = "YinanSofttt";
    String filename = "armidse.bin";
    byte[] bt2 = new byte[96];
    private WindowManager.LayoutParams params;
    //    private PopupWindow popupWindow;
    private View layout, vPart; // pop的布局
    private LayoutInflater inflater;
    private OrderActivity instance;
    private Button btComplete;
    //    private String username, psw;
    private EditText etUserName, etUserPhone, etUnlockPay, etUnlockAddress;
    private TextView etUserCertNum, etLockType, etUnlcokTime, tvReadResult;
    private ImageView ivCertPic, ivFacePic, ivLockPic, ivZxing, ivCertScan;
    private RelativeLayout rlPicFromSdcard, rlTakePic, rlCancle;
    private String userName, userPhone, userCertNum, userSex, idAddress, userNation, unlockAddress, lockType, unlockPay, unlockTime, imagePath1, imagePath2;
    private Intent intent;
    private int requestCoede;
    private File temp;
    private List<String> images = new ArrayList<>();
    private Spinner lockTypeSpinner;
    private String goodsType;
    private boolean isReadCard, isConfigFace;
    private ProgressDialog progressDialog;
    private AMapLocationClient mlocationClient;
    private double longitude, latitude;
    /**
     * 定位监听
     */
    AMapLocationListener locationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation amapLocation) {
            if (amapLocation != null) {
                if (amapLocation.getErrorCode() == 0) {
                    //定位成功回调信息，设置相关消息
                    amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                    latitude = amapLocation.getLatitude();//获取纬度
                    longitude = amapLocation.getLongitude();//获取经度
                    amapLocation.getAccuracy();//获取精度信息
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = new Date(amapLocation.getTime());
                    df.format(date);//定位时间
                    etUnlockAddress.setText(amapLocation.getAddress());
                    etUnlockAddress.setEnabled(false);
                    if (mlocationClient.isStarted())
                        mlocationClient.stopLocation();
                } else {
                    etUnlockAddress.setText("定位失败，请手动输入地址");
                    //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                    Log.e("AmapError", "location Error, ErrCode:"
                            + amapLocation.getErrorCode() + ", errInfo:"
                            + amapLocation.getErrorInfo());
                }
            }
        }
    };
    /**
     * 身份证读取相关
     */
    private IDCardReader idReader = null;
    private IDCardInfo idCardInfo;
    private Bitmap bitmapCert;
    private Bitmap bitmapFace;
    private Bitmap bitmapRealFace;
    private PermissionUtils.PermissionGrant mPermissionGrant = new PermissionUtils.PermissionGrant() {
        @Override
        public void onPermissionGranted(int requestCode) {
            switch (requestCode) {
                case PermissionUtils.CODE_RECORD_AUDIO:
//                    Toast.makeText(instance, "Result Permission Grant CODE_RECORD_AUDIO", Toast.LENGTH_SHORT).show();
                    break;
                case PermissionUtils.CODE_GET_ACCOUNTS:
                    break;
                case PermissionUtils.CODE_READ_PHONE_STATE:
                    break;
                case PermissionUtils.CODE_CALL_PHONE:
                    break;
                case PermissionUtils.CODE_CAMERA:
                    Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    temp = new File(Environment.getExternalStorageDirectory(), System.currentTimeMillis() + PHOTO_FILE_NAME);
                    intent2.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(temp));
                    startActivityForResult(intent2, 2);// 采用ForResult打开
                    break;
                case PermissionUtils.CODE_ACCESS_FINE_LOCATION:
                    break;
                case PermissionUtils.CODE_ACCESS_COARSE_LOCATION:
                    mlocationClient.startLocation();
                    break;
                case PermissionUtils.CODE_READ_EXTERNAL_STORAGE:
                    break;
                case PermissionUtils.CODE_WRITE_EXTERNAL_STORAGE:
                    break;
                default:
                    break;
            }
        }
    };
    //    private String[] typeNum = {"01", "02", "03", "04", "05"};
    private String[] lockTypes = {"门锁", "保险柜锁", "汽车锁", "电子锁", "汽车芯片"};
    private String sStatus;
    private Handler cardHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                idCardInfo = idReader.ReadAllCardInfo(new String[1]);

                if (idCardInfo != null) {
                    isReadCard = true;
                    sStatus += "读卡成功\n";
                    try {
                        progressDialog.dismiss();
                        etUserName.setText(idCardInfo.getName());
//                }
                        bitmapCert = new CertImgDisposeUtils(instance).creatBitmap(idCardInfo, true);
                        bitmapFace = idCardInfo.getPhoto();
                        if (bitmapCert != null) {
                            bitmapCert = ToolsUtils.drawText(bitmapCert, getString(R.string.logo), 50);
                            ivCertPic.setImageBitmap(bitmapCert);
                        }
                        bitmapCert = new CertImgDisposeUtils(instance).creatBitmap(idCardInfo, false);
                        if (bitmapCert != null) {
                            bitmapCert = ToolsUtils.drawText(bitmapCert, getString(R.string.logo), 50);
                        }
                        userCertNum = idCardInfo.getCardNum();
                        userSex = idCardInfo.getGender();
                        if (userSex.equals("男")) {
                            userSex = "1";
                        } else {
                            userSex = "0";
                        }
                        idAddress = idCardInfo.getAddress();
                        userNation = idCardInfo.getNation();
                        etUserCertNum.setText(ToolsUtils.certNumEncryption(userCertNum));
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

    @Override
    public void initTitleBar() {
        setTitle("信息采集");
        showBackwardView(true);
    }

    @Override
    public void initViews() {
        setContentView(R.layout.order_activity);
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
        btComplete = (Button) findViewById(R.id.bt_complete);
        etUserName = (EditText) findViewById(R.id.et_user_name);
        etUserPhone = (EditText) findViewById(R.id.et_user_phone);
        etUserCertNum = (TextView) findViewById(R.id.et_cert_num);
        etUnlockAddress = (EditText) findViewById(R.id.et_unlock_address);
        etUnlockPay = (EditText) findViewById(R.id.et_unlock_money);
        etUnlcokTime = (TextView) findViewById(R.id.et_unlock_time);
        tvReadResult = (TextView) findViewById(R.id.tv_read_result);
        ivCertPic = (ImageView) findViewById(R.id.iv_cert_pic);
        ivFacePic = (ImageView) findViewById(R.id.iv_user_face);
        ivLockPic = (ImageView) findViewById(R.id.iv_lock_pic);
        ivZxing = (ImageView) findViewById(R.id.iv_zxing);
        ivCertScan = (ImageView) findViewById(R.id.iv_cert_scan);
        lockTypeSpinner = (Spinner) findViewById(R.id.sp_lock_type);


        /**
         * 初始化高德地图控件
         */
        mlocationClient = new AMapLocationClient(this);
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置定位监听
        mlocationClient.setLocationListener(locationListener);
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(10000);
        //设置定位参数
        mlocationClient.setLocationOption(mLocationOption);
//         此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
//         注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
//         在定位结束后，在合适的生命周期调用onDestroy()方法
//         在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
//        启动定位
        PermissionUtils.requestPermission(this, PermissionUtils.CODE_ACCESS_COARSE_LOCATION, mPermissionGrant);
    }

    private void checkPer() {
        PermissionUtils.requestPermission(this, PermissionUtils.CODE_CAMERA, mPermissionGrant);
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        PermissionUtils.requestPermissionsResult(this, requestCode, permissions, grantResults, mPermissionGrant);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 0:
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        String scanResult = bundle.getString("result");
//                        et.setText(scanResult);
                    }
                }
                break;

            case 1:
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        // 得到图片的全路径
                        Uri uri = data.getData();
                        String[] proj = {MediaStore.Images.Media.DATA};

                        //好像是android多媒体数据库的封装接口，具体的看Android文档
                        Cursor cursor = managedQuery(uri, proj, null, null, null);
                        //按我个人理解 这个是获得用户选择的图片的索引值
                        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                        //将光标移至开头 ，这个很重要，不小心很容易引起越界
                        cursor.moveToFirst();
                        //最后根据索引值获取图片路径
                        String path = cursor.getString(column_index);
                        getImage(path);
                    }
                }
                break;
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
            case 100:
                if (data != null) {
                    Intent oInt = data;
                    Bitmap oCert = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + "/photo.jpg");
                    String sNo = oInt.getStringExtra("no");
                    onReadCert(sNo, oCert);
                }
                break;
            case 1111:
                if (data == null) return;
                idCardInfo = null;
                if (!TextUtils.isEmpty(data.getStringExtra("face_picture"))) {
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

    public void onReadCert(String sNo, Bitmap oCert) {
        etUserCertNum.setText(sNo);
        oCert = ToolsUtils.drawText(oCert, getString(R.string.logo), 50);
        ivCertPic.setImageBitmap(oCert);
    }

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
            case 2:
                imagePath2 = path;
                images.add(path);
                ivLockPic.setImageBitmap(BitmapFactory.decodeFile(path));
                break;
        }
    }

    @Override
    public void initData() {
        if (!checkNetwork()) {
            showToast(getResources().getString(R.string.network_not_alive));
            return;
        }
        OkHttpUtils.get().url(HttpUrlUtils.getHttpUrl().getLockType() + "?access_token=" + SPUtils.get(instance, "access_token", "")).build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        try {
                            Map<String, Object> map = GsonUtil.JsonToMap(s);
//                            LogUtils.i(map.toString());
                            if (map.get("state").toString().equals(Globals.httpSuccessState)) {
//                                LogUtils.i("login===", "" + map.toString());
                                List<Map<String, Object>> data = GsonUtil.GsonToListMaps(GsonUtil.GsonString(map.get("table")));
                                //然后通过比较器来实现排序
                                Collections.sort(data, new Comparator<Map<String, Object>>() {
                                    @Override
                                    public int compare(Map<String, Object> t1, Map<String, Object> t2) {
                                        return t1.get("lt_code").toString().compareTo(t2.get("lt_code").toString());
                                    }
                                });
//                                LogUtils.i(data.toString());
                                for (int j = 0; j < data.size(); j++) {
                                    lockTypes[j] = data.get(j).get("lt_code").toString() + "-" + data.get(j).get("lt_name").toString();

                                }
                            }
                            ArrayAdapter adapter = new ArrayAdapter(instance, R.layout.item_spinner, lockTypes);
                            lockTypeSpinner.setAdapter(adapter);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });


    }

    @Override
    public void addListener() {
        btComplete.setOnClickListener(instance);
        ivCertPic.setOnClickListener(instance);
        ivFacePic.setOnClickListener(instance);
        ivLockPic.setOnClickListener(instance);
        ivZxing.setOnClickListener(instance);
        ivCertScan.setOnClickListener(instance);

        lockTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ArrayAdapter<String> adapter = (ArrayAdapter<String>) adapterView.getAdapter();
                switch (adapter.getItem(i)) {
                    case "门锁":
                        goodsType = "01";
                        break;
                    case "保险柜锁":
                        goodsType = "02";
                        break;
                    case "汽车锁":
                        goodsType = "03";
                        break;
                    case "电子锁":
                        goodsType = "04";
                        break;
                    case "汽车芯片":
                        goodsType = "05";
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.iv_user_face:
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
                    break;
                case R.id.iv_cert_pic:
                    readIDCard();
                    break;
                case R.id.iv_lock_pic:
                    requestCoede = 2;
                    checkPer();
                    break;
                case R.id.iv_zxing:
                    intent = new Intent(instance, CaptureActivity.class);
                    startActivityForResult(intent, 0);
                    break;
                case R.id.iv_cert_scan:

                    readIDCard();
                    break;
                case R.id.bt_complete:
                    String weightPoint = "";
                    userName = etUserName.getText().toString().trim();
                    userPhone = etUserPhone.getText().toString().trim();
                    unlockAddress = etUnlockAddress.getText().toString().trim();


                    lockType = lockTypeSpinner.getSelectedItem().toString();
                    if (lockType != null && lockType.contains("-")) {
                        lockType = lockType.substring(0, lockType.indexOf("-"));
                    }
                    unlockPay = etUnlockPay.getText().toString().trim();
                    unlockTime = etUnlcokTime.getText().toString().trim();

                    if (!textNotEmpty(userName)) {
                        showToast("请输入客户姓名");
                    } else if (!textNotEmpty(userPhone)) {
                        showToast("请输入客户电话");
                    } else if (!textNotEmpty(userCertNum)) {
                        showToast("请输入客户身份证号码");
                    } else if (!textNotEmpty(unlockAddress)) {
                        showToast("请输入开锁地址");
                    } else if (!textNotEmpty(lockType)) {
                        showToast("请输入锁具类型");
                    } else if (!textNotEmpty(unlockPay)) {
                        showToast("请输入开锁费用");
                    } else if (bitmapCert == null) {
                        showToast("请拍摄身份证照片");
                    } else if (bitmapRealFace == null) {
                        showToast("请进行人脸识别");
                    } else if (!textNotEmpty(imagePath2)) {
                        showToast("请拍摄门锁照片");
                    } else {
                        try {
                            order();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //下单方法
    private void order() {

        btComplete.setEnabled(false);
        loadingDialog.show("正在下单");
        StringBuffer imageString = new StringBuffer("");
        for (int i = 0; i < images.size(); i++) {
            imageString.append(Base64Utils.photoToBase64(BitmapFactory.decodeFile(images.get(i)), 80) + ",");
        }
        if (imageString.toString().endsWith(",")) {
            imageString.deleteCharAt(imageString.length() - 1);
        }
        OkHttpUtils
                .post()
                .url(HttpUrlUtils.getHttpUrl().getOrderUrl() + "?access_token=" + SPUtils.get(instance, "access_token", ""))
                .addParams("longitude", longitude + "")
                .addParams("latitude", latitude + "")
                .addParams("price", unlockPay)
                .addParams("remark", "")
                .addParams("staffid", SPUtils.get(instance, "userid", "").toString())
                .addParams("username", userName)
                .addParams("usertel", userPhone)
                .addParams("useraddress", unlockAddress)
                .addParams("locktype", lockType)
                .addParams("certcode", userCertNum)
                .addParams("certimg", Base64Utils.photoToBase64(bitmapCert, 80))
                .addParams("faceimg", Base64Utils.photoToBase64(bitmapRealFace, 80))
                .addParams("lockimg", Base64Utils.photoToBase64(BitmapFactory.decodeFile(imagePath2), 80))
                .addParams("usersex", userSex)
                .addParams("idaddress", idAddress)
                .addParams("usernation", userNation)
                .addParams("province", "")
                .addParams("city", "")
                .addParams("district", "")
                .addParams("unlocktime", SDCardUtils.data(etUnlcokTime.getText().toString()))
                .addParams("faceimg", Base64Utils.photoToBase64(bitmapRealFace, 80))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        e.printStackTrace();
                        loadingDialog.dismiss();
                        showToast("网络访问异常");
                        btComplete.setEnabled(true);
                        saveJson();
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        try {
                            loadingDialog.dismiss();
                            btComplete.setEnabled(true);
                            Map<String, Object> map = GsonUtil.JsonToMap(s);
                            if (map.get("state").toString().equals(Globals.httpSuccessState)) {
                                showToast("下单成功");
                                Intent intent = new Intent(instance, PayActivity.class);
                                intent.putExtra("or_id", map.get("table").toString());
                                intent.putExtra("price", unlockPay);
                                startActivity(intent);
//                                LogUtils.i(map.get("table").toString());
                                finish();
                            } else {
//                            showToast(map.get("mess").toString());
                                saveJson();
                                return;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

    }

    /**
     * 以文件形式保存json字符串
     */
    private void saveJson() {
        //0330增加生成文件上传文件方法
        Map<String, Object> datas = new HashMap<>();
        datas.put("longitude", longitude + "");
        datas.put("latitude", latitude + "");
        datas.put("price", unlockPay);
        datas.put("remark", "");
        datas.put("staffid", SPUtils.get(instance, "userid", "").toString());
        datas.put("username", userName);
        datas.put("usertel", userPhone);
        datas.put("useraddress", unlockAddress);
        datas.put("locktype", lockType);
        datas.put("certcode", userCertNum);
        datas.put("usersex", userSex);
        datas.put("idaddress", idAddress);
        datas.put("usernation", userNation);
        datas.put("certimg", Base64Utils.photoToBase64(bitmapCert, 80));
        datas.put("faceimg", Base64Utils.photoToBase64(bitmapRealFace, 80));
        datas.put("lockimg", Base64Utils.photoToBase64(BitmapFactory.decodeFile(imagePath2), 80));
        datas.put("unlocktime", SDCardUtils.data(etUnlcokTime.getText().toString()));
        datas.put("province", "");
        datas.put("city", "");
        datas.put("district", "");
        String jsonStr = GsonUtil.GsonString(datas);
        String fileName = "咚咚开锁 -" + userName + "-" + unlockAddress + "-" + etUnlcokTime.getText().toString() + ".txt";
        if (SDCardUtils.writeNewFile(instance.getFilesDir().getAbsolutePath() + "/" + fileName, jsonStr)) {
            showToast("保存数据成功，等待上传");
            finish();
        } else {
            showToast("保存数据失败");
        }

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

    //读卡
    private void readIDCard() {
        if (idReader == null) return;
        sStatus = "";
        if (!isReadCard) {
            startTimerTask();
        } else {
            showDialog("提示", "重新扫描身份证吗？", "确定", "取消", 0);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        etUnlcokTime.setText(df.format(new Date()));

    }

    @Override
    protected void onDestroy() {
        releaseCardReader(true);// 释放读卡
        super.onDestroy();
        stopTimerTask();
    }

    @Override
    protected void dialogOk() {
        super.dialogOk();
        isReadCard = false;
        startTimerTask();
    }
}