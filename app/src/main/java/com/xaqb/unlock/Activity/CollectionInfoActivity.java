package com.xaqb.unlock.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.jaeger.library.StatusBarUtil;
import com.xaqb.unlock.CameraTool.CertCaptureActivity;
import com.xaqb.unlock.R;
import com.xaqb.unlock.Utils.Base64Utils;
import com.xaqb.unlock.Utils.Globals;
import com.xaqb.unlock.Utils.GsonUtil;
import com.xaqb.unlock.Utils.HttpUrlUtils;
import com.xaqb.unlock.Utils.ImageDispose;
import com.xaqb.unlock.Utils.LogUtils;
import com.xaqb.unlock.Utils.PaintView;
import com.xaqb.unlock.Utils.PermissionUtils;
import com.xaqb.unlock.Utils.PhoneFormatCheckUtils;
import com.xaqb.unlock.Utils.PriceUtil;
import com.xaqb.unlock.Utils.SDCardUtils;
import com.xaqb.unlock.Utils.SPUtils;
import com.xaqb.unlock.Utils.ToolsUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import okhttp3.Call;

import static com.xaqb.unlock.Utils.IDCardUtils.IDCardValidate;


/**
 * Created by chengeng on 2016/12/2.
 * 0801新建信息采集页面//修改读取身份证功能为扫描身份证
 */
public class CollectionInfoActivity extends BaseActivityNew {
    private static final String PHOTO_FILE_NAME = "temp_photo.jpg";
    /**
     * 高德地图相关
     */
    //声明mLocationOption对象
    public AMapLocationClientOption mLocationOption = null;
    private CollectionInfoActivity instance;
    private Button btComplete;
    private EditText etUserName, etUserPhone, etOtherName, etOtherPhone, etOtherRemark, etUnlockPay, etUnlockAddress;
    private TextView etUserCertNum, etLockType, etUnlcokTime, tvReadResult, tvTitle;
    private ImageView ivCertPic, ivFacePic, ivOtherFacePic, ivLockPic, ivZxing, ivCertScan;
    private String userName, userPhone, userCertNum, userSex, idAddress, userNation, unlockAddress,
            lockType, unlockPay, unlockTime, imagePath1, imagePath2, imagePath3, otherName, otherPhone, otherRemark;
    private Intent intent;
    private int requestCoede;
    private File temp;
    private List<String> images = new ArrayList<>();
    private Spinner lockTypeSpinner;
    private String goodsType;
    private boolean isReadCard, isConfigFace;
    private int permissionCode = 0;
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
    private ImageView imageSign;
    private PaintView mView;
    private Bitmap bitmapCert;
    private String[] lockTypes = {"门锁", "保险柜锁", "汽车锁", "电子锁", "汽车芯片"};
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    private ImageView ivSign;
    private byte[] picByte;

    @Override
    public void initViews() {
        StatusBarUtil.setTranslucent(this, 0);
        setContentView(R.layout.activity_collection_info);
        instance = this;
        assignViews();
        tvTitle.setText("信息采集");
    }

    private void assignViews() {
        btComplete = (Button) findViewById(R.id.bt_complete);
        etUserName = (EditText) findViewById(R.id.et_user_name);
        etUserPhone = (EditText) findViewById(R.id.et_user_phone);
        etOtherName = (EditText) findViewById(R.id.et_other_name);
        etOtherPhone = (EditText) findViewById(R.id.et_other_phone);
        etOtherRemark = (EditText) findViewById(R.id.et_other_remark);
        etUserCertNum = (TextView) findViewById(R.id.et_cert_num);
        etUnlockAddress = (EditText) findViewById(R.id.et_unlock_address);
        etUnlockPay = (EditText) findViewById(R.id.et_unlock_money);
        etUnlcokTime = (TextView) findViewById(R.id.et_unlock_time);
        tvReadResult = (TextView) findViewById(R.id.tv_read_result);
        ivCertPic = (ImageView) findViewById(R.id.iv_cert_pic);
        ivFacePic = (ImageView) findViewById(R.id.iv_user_face);
        ivOtherFacePic = (ImageView) findViewById(R.id.iv_other_face);
        ivLockPic = (ImageView) findViewById(R.id.iv_lock_pic);
        ivCertScan = (ImageView) findViewById(R.id.iv_cert_scan);
        lockTypeSpinner = (Spinner) findViewById(R.id.sp_lock_type);
        ivSign = (ImageView) findViewById(R.id.iv_sign_collection);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        /**
         * 初始化高德地图控件
         */
        mlocationClient = new AMapLocationClient(this);
        //初始化定位参1数
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
        checkPer(PermissionUtils.CODE_ACCESS_COARSE_LOCATION);
    }

    public boolean isSign;
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case 0://返回签字的图片
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        picByte = bundle.getByteArray("picByte");
                        isSign = true;
                        ivSign.setImageBitmap(BitmapFactory.decodeByteArray(picByte, 0, picByte.length));
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
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //设置身份证照片并加水印
    public void onReadCert(String sNo, Bitmap oCert) {
        userCertNum = sNo;
        etUserCertNum.setText(sNo);
//        etUserCertNum.setText(sNo.substring(0,3)+"********"+sNo.substring(11));
        oCert = ToolsUtils.drawText(oCert, getString(R.string.logo), 50);
        bitmapCert = oCert;
        ivCertPic.setImageBitmap(bitmapCert);
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
            Intent intent_delete = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri uri_delete = Uri.fromFile(oldFile);
            intent.setData(uri_delete);
            instance.sendBroadcast(intent_delete);//这个广播的目的就是更新图库，发了这个广播进入相册就可以找到你保存的图片了！，记得要传你更新的file哦
        }
        getImage(PHOTO_COMM_NAME);

    }

    public void getImage(String path) {
        switch (requestCoede) {
            case 1:
                imagePath1 = path;
                images.add(path);
                ivFacePic.setImageBitmap(ToolsUtils.drawText(BitmapFactory.decodeFile(path), getString(R.string.logo), 50));
                break;
            case 2:
                imagePath2 = path;
                images.add(path);
                ivLockPic.setImageBitmap(ToolsUtils.drawText(BitmapFactory.decodeFile(path), getString(R.string.logo), 50));
                break;
            case 3:
                imagePath3 = path;
                images.add(path);
                ivOtherFacePic.setImageBitmap(ToolsUtils.drawText(BitmapFactory.decodeFile(path), getString(R.string.logo), 50));
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
                            if (map.get("state").toString().equals(Globals.httpSuccessState)) {
                                List<Map<String, Object>> data = GsonUtil.GsonToListMaps(GsonUtil.GsonString(map.get("table")));
                                //然后通过比较器来实现排序
                                Collections.sort(data, new Comparator<Map<String, Object>>() {
                                    @Override
                                    public int compare(Map<String, Object> t1, Map<String, Object> t2) {
                                        return t1.get("lt_code").toString().compareTo(t2.get("lt_code").toString());
                                    }
                                });
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
        ivOtherFacePic.setOnClickListener(instance);
        ivLockPic.setOnClickListener(instance);
        ivCertScan.setOnClickListener(instance);
        ivSign.setOnClickListener(instance);
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
                case R.id.iv_user_face://客户照片
                    requestCoede = 1;
                    permissionCode = 0;
                    checkPer(PermissionUtils.CODE_CAMERA);
                    break;
                case R.id.iv_other_face://第三方照片
                    requestCoede = 3;
                    permissionCode = 0;
                    checkPer(PermissionUtils.CODE_CAMERA);
                    break;
                case R.id.iv_cert_pic://身份证照片
//                    readIDCard();
                    scanCert();
                    break;
                case R.id.iv_lock_pic://锁具照片
                    requestCoede = 2;
                    permissionCode = 0;
                    checkPer(PermissionUtils.CODE_CAMERA);
                    break;
                case R.id.iv_cert_scan://读取身份证
                    scanCert();
                    break;
                case R.id.iv_sign_collection://手写签名
                    Intent intent1 = new Intent(instance, SignActivity.class);
                    startActivityForResult(intent1, 0);
                    break;
                case R.id.bt_complete:
                    userName = etUserName.getText().toString().trim();
                    userPhone = etUserPhone.getText().toString().trim();
                    otherName = etOtherName.getText().toString().trim();
                    otherPhone = etOtherPhone.getText().toString().trim();
                    otherRemark = etOtherRemark.getText().toString().trim();
                    unlockAddress = etUnlockAddress.getText().toString().trim();
                    userCertNum = etUserCertNum.getText().toString().trim();//身份证号码
                    lockType = lockTypeSpinner.getSelectedItem().toString();
                    if (lockType != null && lockType.contains("-")) {
                        lockType = lockType.substring(0, lockType.indexOf("-"));
                    }
                    unlockPay = etUnlockPay.getText().toString().trim();
                    unlockTime = etUnlcokTime.getText().toString().trim();

                    if (!textNotEmpty(userName)) {
                        showToast("请输入客户姓名");
                    } else if (userName.length() < 2) {
                        showToast("姓名长度错误");
                    } else if (!textNotEmpty(userPhone)) {
                        showToast("请输入客户电话");
                    } else if (!PhoneFormatCheckUtils.isChinaPhoneLegal(userPhone)) {
                        showToast("电话号码格式错误");
                    } else if (!textNotEmpty(userCertNum)) {
                        showToast("请输入客户身份证号码");
                    } else if (!IDCardValidate(userCertNum).equals("")) {
                        showToast("请输入正确身份证号码");
                    } else if (!textNotEmpty(unlockAddress)) {
                        showToast("请输入开锁地址");
                    } else if (!textNotEmpty(lockType)) {
                        showToast("请输入锁具类型");
                    } else if (!textNotEmpty(unlockPay)) {
                        showToast("请输入开锁费用");
                    } else if (!Pattern.compile("^(([1-9]{1}\\d*)|([0]{1}))(\\.(\\d){0,2})?$").matcher(unlockPay).matches()) {
                        showToast("价格格式错误");
                    } else if (!textNotEmpty(imagePath1)) {
                        showToast("请拍摄人脸照片");
                    } else if (!textNotEmpty(imagePath2)) {
                        showToast("请拍摄门锁照片");
                    } else if (!isSign) {
                        showToast("请添加手写签名");
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

    private void scanCert() {
        permissionCode = 1;
        checkPer(PermissionUtils.CODE_CAMERA);
    }

    //下单方法
    private void order() {
        try {
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
                    .addParams("price", unlockPay)//价格
                    .addParams("staffid", SPUtils.get(instance, "userid", "").toString())//用户id
                    .addParams("username", userName)//用户姓名
                    .addParams("tpname", otherName)//第三方姓名
                    .addParams("tptel", otherPhone)//第三方电话
                    .addParams("remark", otherRemark)//第三方备注
                    .addParams("usertel", userPhone)//电话号码
                    .addParams("useraddress", unlockAddress)//地址
                    .addParams("locktype", lockType)//锁具类型
                    .addParams("certcode", userCertNum)//身份证号码
                    .addParams("certimg", Base64Utils.photoToBase64(bitmapCert, 60))//身份证照片
                    .addParams("faceimg", Base64Utils.photoToBase64(BitmapFactory.decodeFile(imagePath1), 60))//人脸照片
                    .addParams("lockimg", Base64Utils.photoToBase64(BitmapFactory.decodeFile(imagePath2), 60))//锁具照片
                    .addParams("tpimg", Base64Utils.photoToBase64(BitmapFactory.decodeFile(imagePath3), 60))//第三方照片
                    .addParams("usersex", "")
                    .addParams("idaddress", "")
                    .addParams("usernation", "")
                    .addParams("province", "")
                    .addParams("city", "")
                    .addParams("district", "")
                    .addParams("unlocktime", SDCardUtils.data(etUnlcokTime.getText().toString()))//开锁时间
                    .addParams("signimg", Base64Utils.photoToBase64(BitmapFactory.decodeByteArray(picByte, 0, picByte.length), 60))//手写签名照片
                    .build()
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int i) {
                            e.printStackTrace();
                            loadingDialog.dismiss();
                            showToast("网络访问失败");
                            btComplete.setEnabled(true);
                            saveJson();
                        }

                        @Override
                        public void onResponse(String s, int i) {
                            LogUtils.e("下单成功" + s);
                            try {
                                loadingDialog.dismiss();
                                btComplete.setEnabled(true);
                                Map<String, Object> map = GsonUtil.JsonToMap(s);
                                if (map.get("state").toString().equals(Globals.httpSuccessState)) {
                                    showToast("下单成功");
                                    Intent intent = new Intent(instance, PayActivityNew.class);
                                    intent.putExtra("or_id", map.get("table").toString());
                                    intent.putExtra("total_price", unlockPay);
                                    intent.putExtra("pay_price", "0");
                                    startActivity(intent);
                                    finish();
                                } else {
                                    saveJson();
                                    return;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
        } catch (Exception e) {
            loadingDialog.dismiss();
            showToast("网络访问异常");
            btComplete.setEnabled(true);
            e.printStackTrace();
        }
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
        datas.put("staffid", SPUtils.get(instance, "userid", "").toString());
        datas.put("username", userName);
        datas.put("usertel", userPhone);
        datas.put("tpname", otherName);
        datas.put("tptel", otherPhone);
        datas.put("remark", otherRemark);
        datas.put("useraddress", unlockAddress);
        datas.put("locktype", lockType);
        datas.put("certcode", userCertNum);
        datas.put("usersex", "");
        datas.put("idaddress", "");
        datas.put("usernation", "");
        datas.put("certimg", Base64Utils.photoToBase64(bitmapCert, 60));
        datas.put("faceimg", Base64Utils.photoToBase64(BitmapFactory.decodeFile(imagePath1), 60));
        datas.put("tpimg", Base64Utils.photoToBase64(BitmapFactory.decodeFile(imagePath3), 60));
        datas.put("lockimg", Base64Utils.photoToBase64(BitmapFactory.decodeFile(imagePath2), 60));
        datas.put("unlocktime", SDCardUtils.data(etUnlcokTime.getText().toString()));
        datas.put("signing", Base64Utils.photoToBase64(BitmapFactory.decodeByteArray(picByte, 0, picByte.length), 60));
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

    @Override
    public void onResume() {
        super.onResume();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        etUnlcokTime.setText(df.format(new Date()));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void dialogOk() {
        super.dialogOk();
        isReadCard = false;
    }

    @Override
    protected void requestPerPass(int requestCode) {
        if (requestCode == PermissionUtils.CODE_CAMERA) {
            if (permissionCode == 0) {
                Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                temp = new File(Environment.getExternalStorageDirectory(), System.currentTimeMillis() + PHOTO_FILE_NAME);
                intent2.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(temp));
                startActivityForResult(intent2, 2);// 采用ForResult打开
            } else if (permissionCode == 1) {
                Intent intent = new Intent(instance, CertCaptureActivity.class);
                startActivityForResult(intent, 100);
            }
        } else if (requestCode == PermissionUtils.CODE_ACCESS_COARSE_LOCATION) {
            mlocationClient.startLocation();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("CollectionInfo Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}