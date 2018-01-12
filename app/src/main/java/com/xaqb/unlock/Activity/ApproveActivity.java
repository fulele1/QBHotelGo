package com.xaqb.unlock.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.TextView;
import android.widget.Toast;

import com.jaeger.library.StatusBarUtil;
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
public class ApproveActivity extends BaseActivityNew {
    private static final String PHOTO_FILE_NAME = "temp_photo.jpg";
    String[] types = {"身份证", "驾照", "户口本", "军官证", "士兵证", "警官证", "国内护照", "港澳通行证", "其他"};
    String[] sex = {"男", "女"};

    String[] nations = {"汉族","蒙古族","回族","藏族","维吾尔族","苗族","彝族","壮族","布依族","朝鲜族"
            ,"满族","侗族","瑶族","白族","土家族","哈尼族","哈萨克族","傣族","黎族","僳僳族"
            ,"佤族","畲族","高山族","拉祜族","水族","东乡族","纳西族","景颇族","柯尔克孜族","土族"
            ,"达斡尔族","仫佬族","羌族","布朗族","撒拉族","毛南族","仡佬族","锡伯族","阿昌族","普米族"
            ,"塔吉克族","怒族","乌孜别克族","俄罗斯族","鄂温克族","德昂族","保安族","裕固族","京族","塔塔尔族"
            ,"独龙族","鄂伦春族","赫哲族","门巴族","珞巴族","基诺族"};
    private boolean isInde;

    private ApproveActivity instance;
    private EditText etRealName, etCardNum, etAge;
    private ImageView ivCertPic, ivFacePic,ivSign;
    private Button btSubmit;
    private TextView tvTitle;
    private WindowManager.LayoutParams params;
    private View layout, vPart; // pop的布局
    private LayoutInflater inflater;
    private Bitmap head;
    private RelativeLayout rlName, rlPicFromSdcard, rlTakePic, rlCancle;
    private boolean isShow;
    private String type, realName, cardNum, cardSex, cardNation, cardAge, certPicPath = "", facePicPath = "", sexx;
    private Spinner spType, spSex, spNation;
    private ArrayAdapter spinnerAdapter, spinnerSexAdapter, spinnerNationAdapter;
    private int requestCoede = 0;
    private File temp;


    @Override
    public void initViews() {
        StatusBarUtil.setTranslucent(this, 0);
        setContentView(R.layout.approve_real_name_activity);
        instance = this;
        assignViews();
        tvTitle.setText("实名认证");
    }

    private void assignViews() {
        params = getWindow().getAttributes();
        etRealName = (EditText) findViewById(R.id.et_real_name);
        etCardNum = (EditText) findViewById(R.id.et_card_num);
        spSex = (Spinner) findViewById(R.id.sp_sex);
        etAge = (EditText) findViewById(R.id.et_card_age);
        ivCertPic = (ImageView) findViewById(R.id.iv_cert_pic);
        ivFacePic = (ImageView) findViewById(R.id.iv_face_pic);
        btSubmit = (Button) findViewById(R.id.bt_submit);
//        if (readConfig("approveing").equals("yes")){
//            btSubmit.setClickable(false);
//            btSubmit.setText("正在认证中，请勿重复认证。");
//            btSubmit.setBackground(getResources().getDrawable(R.drawable.bg_button_gray));
//        }
        spType = (Spinner) findViewById(R.id.sp_type);
        spNation = (Spinner) findViewById(R.id.sp_nation);//选择民族
        ivSign = (ImageView) findViewById(R.id.iv_sign_approve);//电子签名
        tvTitle = (TextView) findViewById(R.id.tv_title);
    }

    @Override
    public void initData() {
        spinnerAdapter = new ArrayAdapter(instance, R.layout.item_spinner, types);
        spinnerSexAdapter = new ArrayAdapter(instance, R.layout.item_spinner, sex);
        spinnerNationAdapter = new ArrayAdapter(instance, R.layout.item_spinner, nations);
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


        spSex.setAdapter(spinnerSexAdapter);
        spSex.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ArrayAdapter<String> adapter = (ArrayAdapter<String>) adapterView.getAdapter();
                sexx = adapter.getItem(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                sexx = "";
            }
        });

        spNation.setAdapter(spinnerNationAdapter);
        spNation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ArrayAdapter<String> adapter = (ArrayAdapter<String>) adapterView.getAdapter();
//                cardNation = adapter.getItem(i);
                cardNation = "01";
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                cardNation = "";
            }
        });
    }

    @Override
    public void addListener() {
        ivFacePic.setOnClickListener(instance);
        ivCertPic.setOnClickListener(instance);
        btSubmit.setOnClickListener(instance);
        ivSign.setOnClickListener(instance);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_cert_pic:
                requestCoede = 0;
                checkPer(PermissionUtils.CODE_CAMERA);
                break;
            case R.id.iv_face_pic:
                requestCoede = 1;
                checkPer(PermissionUtils.CODE_CAMERA);
                break;
            case R.id.iv_sign_approve:
                Intent intent1 = new Intent(instance, SignActivity.class);
                startActivityForResult(intent1, 11);

                break;

            case R.id.bt_submit:
                realName = etRealName.getText().toString().trim();
                cardNum = etCardNum.getText().toString().trim();
                cardSex = spSex.getSelectedItem().toString().trim();
                cardAge = etAge.getText().toString().trim();
                if (type == null || type.equals("")) {
                    showToast("请选择证件类型");
                } else if (realName == null || realName.equals("")) {
                    showToast("请输入真实姓名");
                } else if (realName.length()>8 || realName.length()<2) {
                    showToast("请有效的长度");
                } else if (cardNum == null || cardNum.equals("")) {
                    showToast("请输入证件号码");
                } else if (sexx == null || sexx.equals("")) {
                    showToast("请输入性别");
                } else if (cardNation == null || cardNation.equals("")) {
                    showToast("请输入民族");
                } else if (cardAge == null || cardAge.equals("")) {
                    showToast("请输入年龄");}
                else if (cardAge.length()>3) {
                    showToast("请输入正确的年龄");
                } else if (!textNotEmpty(certPicPath)) {
                    showToast("请拍摄证件照片");
                } else if (!textNotEmpty(facePicPath)) {
                    showToast("请拍摄人脸照片");
                } else if (mBmSign == null) {
                    showToast("请添加电子签名");
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
        loadingDialog.show("正在提交");
        OkHttpUtils
                .post()
                .url(HttpUrlUtils.getHttpUrl().getRealNameUrl() + "?access_token=" + SPUtils.get(instance, "access_token", ""))
                .addParams("uid", SPUtils.get(instance, "userid", "").toString())
                .addParams("name", realName)
                .addParams("certtype", type)
                .addParams("certcode", cardNum)
                .addParams("sex", cardSex)
                .addParams("nation", cardNation)
                .addParams("age", cardAge)
                .addParams("certimg", Base64Utils.photoToBase64(BitmapFactory.decodeFile(certPicPath), 80))
                .addParams("faceimg", Base64Utils.photoToBase64(BitmapFactory.decodeFile(facePicPath), 80))
                .addParams("signimg", Base64Utils.photoToBase64(mBmSign,80))
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
                            if (map.get("state").toString().equals(Globals.httpSuccessState)) {
                                switch (map.get("state").toString()) {
                                    case "0":
                                        showToast("上传成功，请等待审核！");
                                        SPUtils.put(instance, "staff_is_real", "2");
                                        finish();
                                        break;
                                    case "1":
                                        showToast("认证成功");
                                        LogUtils.e("认证成功1");
                                        SPUtils.put(instance, "staff_is_real", "1");
                                        finish();
                                        break;
                                    case "2":
                                        showToast("认证中，请等待审核！");
                                        SPUtils.put(instance, "staff_is_real", "2");
                                        finish();
                                        break;
                                    case "3":
                                        showToast("认证失败！");
                                        SPUtils.put(instance, "staff_is_real", "3");
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

    private byte [] picByte;
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

            case 11://电子签名
                if (resultCode == RESULT_OK){
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        picByte = bundle.getByteArray("picByte");
                        mBmSign = BitmapFactory.decodeByteArray(picByte, 0, picByte.length);
                        ivSign.setImageBitmap(mBmSign);
                    }
                }
                break;

            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private Bitmap mBmSign;
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