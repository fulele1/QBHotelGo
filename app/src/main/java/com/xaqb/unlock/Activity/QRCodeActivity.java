package com.xaqb.unlock.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;

import com.xaqb.unlock.R;
import com.xaqb.unlock.Utils.QRCodeUtil;
import com.xaqb.unlock.Utils.SPUtils;

import java.io.File;


public class QRCodeActivity extends BaseActivity {

    private QRCodeActivity instance;
    private ImageView mIvQRCode;

    @Override
    public void initTitleBar() {
        setTitleBarVisible(View.GONE);
    }

    @Override
    public void initViews() throws Exception {
        setContentView(R.layout.activity_qrcode);
        instance = this;
        mIvQRCode = (ImageView) findViewById(R.id.iv_qr_qrcode);

    }

    @Override
    public void initData() {

        Intent intent = getIntent();
        String user = intent.getStringExtra("");
        String ide = intent.getStringExtra("");

        final String filePath = getFileRoot(this) + File.separator
                + "qr_" + System.currentTimeMillis() + ".jpg";

        // 二维码图片较大时，生成图片、保存文件的时间可能较长，因此放在新线程中
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean success = QRCodeUtil.createQRImage("http://www.ddkaisuo.net/home/staff/index?id="+ SPUtils.get(instance, "userid", ""), 800, 800, BitmapFactory.decodeResource(getResources(), R.mipmap.icon), filePath);

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
