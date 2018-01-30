package com.xaqb.unlock.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jaeger.library.StatusBarUtil;
import com.xaqb.unlock.R;

import java.io.ByteArrayOutputStream;

public class SignActivity extends BaseActivityNew {

    private ImageView imageSign;
    private TextView tvTitle;
    private PaintView mView;
    private SignActivity instance;
    private CheckBox cbAgree;
    private FrameLayout frameLayout;
    private Button btnOk;
    private Button btnClear;
    int into;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initViews() throws Exception {
        StatusBarUtil.setTranslucent(this, 0);

        setContentView(R.layout.activity_sign);
        instance = this;
        imageSign = (ImageView) findViewById(R.id.iv_sign);
        cbAgree = (CheckBox) findViewById(R.id.cb_agree_sign);

        frameLayout = (FrameLayout) findViewById(R.id.tablet_view);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvTitle.setText("手写签名");

        mView = new PaintView(this);
        frameLayout.addView(mView);
        mView.requestFocus();

         btnClear = (Button) findViewById(R.id.tablet_clear);
        btnClear.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mView.clear();
            }
        });

        btnOk = (Button) findViewById(R.id.tablet_ok);



    }

    @Override
    public void initData() throws Exception {

    }

    @Override
    public void addListener() throws Exception {


        cbAgree.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    frameLayout.setVisibility(View.VISIBLE);
                } else {
                    frameLayout.setVisibility(View.INVISIBLE);
                    mView.clear();

                }
            }
        });
    }

    //bitmap转为Bytes
    public byte[] Bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);

        return baos.toByteArray();
    }

    class PaintView extends View {
        private Paint paint;
        private Canvas cacheCanvas;
        private Bitmap cachebBitmap;
        private Path path;

        public Bitmap getCachebBitmap() {
            return cachebBitmap;
        }

        public PaintView(Context context) {
            super(context);
            init();
        }

        private void init() {
            paint = new Paint();
            paint.setAntiAlias(true);
            paint.setStrokeWidth(8);
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.BLACK);
            path = new Path();
            cachebBitmap = Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888);
            cacheCanvas = new Canvas(cachebBitmap);
            cacheCanvas.drawColor(Color.WHITE);
        }

        public void clear() {
            if (cacheCanvas != null) {
                paint.setColor(Color.WHITE);
                cacheCanvas.drawPaint(paint);
                paint.setColor(Color.BLACK);
                cacheCanvas.drawColor(Color.WHITE);
                invalidate();
                into=0;
                if (into<=1){
                    btnOk.setEnabled(false);
                }
            }
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawBitmap(cachebBitmap, 0, 0, null);
            canvas.drawPath(path, paint);
            into = into+1;
            if (into>1){
                btnOk.setEnabled(true);
            }
            if (into>1){
                btnOk.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Bitmap imageBitmap = mView.getCachebBitmap();
                        Intent intent = new Intent();
                        Bundle bundle = new Bundle();
                        bundle.putByteArray("picByte", Bitmap2Bytes(imageBitmap));
                        intent.putExtras(bundle);
                        instance.setResult(RESULT_OK, intent);
                        instance.finish();
                    }
                });
            }else {
                Toast.makeText(instance, "请在空白处签名...", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {

            int curW = cachebBitmap != null ? cachebBitmap.getWidth() : 0;
            int curH = cachebBitmap != null ? cachebBitmap.getHeight() : 0;
            if (curW >= w && curH >= h) {
                return;
            }

            if (curW < w)
                curW = w;
            if (curH < h)
                curH = h;

            Bitmap newBitmap = Bitmap.createBitmap(curW, curH, Bitmap.Config.ARGB_8888);
            Canvas newCanvas = new Canvas();
            newCanvas.setBitmap(newBitmap);
            if (cachebBitmap != null) {
                newCanvas.drawBitmap(cachebBitmap, 0, 0, null);
            }
            cachebBitmap = newBitmap;
            cacheCanvas = newCanvas;
        }

        private float cur_x, cur_y;

        @Override
        public boolean onTouchEvent(MotionEvent event) {

            float x = event.getX();
            float y = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    cur_x = x;
                    cur_y = y;
                    path.moveTo(cur_x, cur_y);
                    break;
                }
                case MotionEvent.ACTION_MOVE: {
                    path.quadTo(cur_x, cur_y, x, y);
                    cur_x = x;
                    cur_y = y;
                    break;
                }
                case MotionEvent.ACTION_UP: {
                    cacheCanvas.drawPath(path, paint);
                    path.reset();
                    break;
                }
            }
            invalidate();
            return true;
        }
    }


}
