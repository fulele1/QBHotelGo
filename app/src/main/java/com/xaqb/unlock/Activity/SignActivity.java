package com.xaqb.unlock.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.xaqb.unlock.R;
import com.xaqb.unlock.Utils.PaintView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class SignActivity extends BaseActivity {

    private ImageView imageSign;
    private PaintView mView;
    private SignActivity instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initTitleBar() {
        setTitle("手写签名");
    }

    private CheckBox cbAgree;
    private FrameLayout frameLayout;
    @Override
    public void initViews() throws Exception {

        setContentView(R.layout.activity_sign);
        instance = this;
        imageSign = (ImageView) findViewById(R.id.iv_sign);
        cbAgree = (CheckBox) findViewById(R.id.cb_agree_sign);

        frameLayout = (FrameLayout) findViewById(R.id.tablet_view);

        mView = new PaintView(this);
        frameLayout.addView(mView);
        mView.requestFocus();

        final Button btnClear = (Button) findViewById(R.id.tablet_clear);
        btnClear.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mView.clear();
            }
        });

        Button btnOk = (Button) findViewById(R.id.tablet_ok);
        btnOk.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Bitmap imageBitmap = mView.getCachebBitmap();
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putByteArray("picByte",Bitmap2Bytes(imageBitmap));
                intent.putExtras(bundle);
                instance.setResult(RESULT_OK, intent);
                instance.finish();
            }
        });
    }

    @Override
    public void initData() throws Exception {

    }

    @Override
    public void addListener() throws Exception {


        cbAgree.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    frameLayout.setVisibility(View.VISIBLE);
                }else{
                    frameLayout.setVisibility(View.INVISIBLE);
                    mView.clear();
                }
            }
        });
    }

    //bitmap转为Bytes
        public  byte[] Bitmap2Bytes(Bitmap bm) {
            ByteArrayOutputStream baos =new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.PNG, 100, baos);

            return baos.toByteArray();
        }
}
