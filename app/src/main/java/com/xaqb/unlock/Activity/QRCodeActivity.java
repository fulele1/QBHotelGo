package com.xaqb.unlock.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xaqb.unlock.R;
import com.xaqb.unlock.Utils.QRCodeUtil;
import com.xaqb.unlock.Utils.SPUtils;
import com.xaqb.unlock.Utils.StatuBarUtil;

import java.io.File;


public class QRCodeActivity extends BaseActivityNew {

    private QRCodeActivity instance;
    private ImageView mIvQRCode;
    private TextView mTvTitle;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void initViews() throws Exception {
        setContentView(R.layout.activity_qrcode);
        instance = this;
        StatuBarUtil.setStatusBarColor(this,getResources().getColor(R.color.main));
        mIvQRCode = (ImageView) findViewById(R.id.iv_qr_qrcode);
        mTvTitle = (TextView) findViewById(R.id.tv_title);
        mTvTitle.setText("用户二维码");

    }

    @Override
    public void initData() {

        final String filePath = getFileRoot(this) + File.separator
                + "qr_" + System.currentTimeMillis() + ".jpg";

        // 二维码图片较大时，生成图片、保存文件的时间可能较长，因此放在新线程中
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean success = QRCodeUtil.createQRImage("http://www.ddkaisuo.net/home/staff/index?id=" + SPUtils.get(instance, "userid", ""), 800, 800, BitmapFactory.decodeResource(getResources(), R.mipmap.icon), filePath);

                if (success) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mIvQRCode.setImageBitmap(BitmapFactory.decodeFile(filePath));
                        }
                    });
                }
            }
        }).start();

    }

    @Override
    public void addListener() throws Exception {

    }


    //文件存储根目录
    private String getFileRoot(Context context) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File external = context.getExternalFilesDir(null);
            if (external != null) {
                return external.getAbsolutePath();
            }
        }

        return context.getFilesDir().getAbsolutePath();
    }
}
