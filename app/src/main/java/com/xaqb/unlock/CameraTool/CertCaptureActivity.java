package com.xaqb.unlock.CameraTool;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.xaqb.unlock.R;
import com.xaqb.unlock.Utils.CameraUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class CertCaptureActivity extends Activity implements SurfaceHolder.Callback {
    protected SurfaceView FoSurface = null;
    public Camera FoCamera = null;
    public int FiScreenWidth = 0;
    public int FiScreenHeight = 0;
    protected SurfaceHolder FoHolder = null;
    private CaptureHandler FoHandler;
    public int FiPreviewWidth = 0;
    public int FiPreviewHeight = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FoHandler = new CaptureHandler(this);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//ȥ��������
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        WindowManager oWM = this.getWindowManager();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        FiScreenWidth = oWM.getDefaultDisplay().getWidth();
        FiScreenHeight = oWM.getDefaultDisplay().getHeight();
        setContentView(R.layout.activity_cert_capture);
        FoSurface = (SurfaceView) this.findViewById(R.id.surfaceView1);
        SurfaceHolder FoHolder = FoSurface.getHolder();
        FoHolder.addCallback(this);
        FoHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        SurfaceView oSV = (SurfaceView) this.findViewById(R.id.surfaceView2);
        SurfaceHolder oHolder = oSV.getHolder();
        oHolder.setFormat(PixelFormat.TRANSPARENT);
        oSV.setZOrderOnTop(true);
        oSV.setZOrderMediaOverlay(true);
        /*
		oSV.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				FoHandler.start(FoCamera);
			}
		});*/


        oHolder.addCallback(new SurfaceHolder.Callback() {

            public void surfaceCreated(SurfaceHolder holder) {
                int iWidth = FiScreenWidth;
                int iHeight = FiScreenHeight;

                Rect oOut = new Rect();
                oOut = CameraUtils.getCertRect(iWidth, iHeight);
                Rect oIn = new Rect();
                oIn = CameraUtils.getCertNoRect(iWidth, iHeight);

                Canvas oCanvas = holder.lockCanvas();
                Paint oPaint = new Paint();

                oPaint.setColor(Color.RED);
                oPaint.setStrokeWidth(2);

                oCanvas.drawLine(oIn.left, oIn.top, oIn.right, oIn.top, oPaint);
                oCanvas.drawLine(oIn.left, oIn.bottom, oIn.right, oIn.bottom, oPaint);
                oCanvas.drawLine(oIn.left, oIn.top, oIn.left, oIn.bottom, oPaint);
                oCanvas.drawLine(oIn.right, oIn.top, oIn.right, oIn.bottom, oPaint);
                oPaint.setColor(Color.BLACK);
                oPaint.setAlpha(200);


                oCanvas.drawRect(0, 0, iWidth, oOut.top, oPaint);
                oCanvas.drawRect(0, oOut.bottom, iWidth, iHeight, oPaint);
                holder.unlockCanvasAndPost(oCanvas);
            }

            public void surfaceDestroyed(SurfaceHolder holder) {

            }

            public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {

            }
        });
    }

    protected void showMess(String sMess, boolean bLong) {
        Toast.makeText(this, sMess, bLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onDestroy() {

        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void surfaceCreated(SurfaceHolder holder) {
        try {

            if (FoCamera != null)
                FoCamera.setPreviewDisplay(holder);

        } catch (Exception E) {

        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    private static Size getOptimalPreviewSize(List<Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) h / w;

        if (sizes == null) return null;

        Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = 2000;

        for (Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        if (FoCamera == null) {
            iniCamera();
        }
        Camera.Parameters oParam = FoCamera.getParameters();
        oParam.setPictureFormat(PixelFormat.JPEG);
        // oParam.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);

        List<Size> oSize = oParam.getSupportedPreviewSizes();

        Size oOne = getOptimalPreviewSize(oSize, FiScreenWidth, FiScreenHeight);
        oParam.setPreviewSize(oOne.width, oOne.height);


        FoCamera.setParameters(oParam);
        FoCamera.startPreview();
        // FoCamera.cancelAutoFocus();
        TimerTask task = new TimerTask() {

            public void run() {

                FoHandler.start(FoCamera);

            }

        };

        Timer timer = new Timer();

        timer.schedule(task, 1000);

    }

    public void over(String sNo, Bitmap oBmp) {
        try {
            Intent oInt = new Intent();
            File oFile = new File(Environment.getExternalStorageDirectory(), "photo.jpg");
            if (oFile.exists()) oFile.delete();
            FileOutputStream oStream = new FileOutputStream(oFile);
            oBmp.compress(Bitmap.CompressFormat.JPEG, 40, oStream);
            oStream.flush();
            oStream.close();
            oInt.putExtra("no", sNo);
            setResult(2, oInt);
            finish();
        } catch (Exception E) {
            E.printStackTrace();
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        //1220修改，bugly报错camera在release后还被used
//        closeCamera();

    }

    @Override
    protected void onStop() {
        super.onStop();
        closeCamera();
    }

    @Override
    public void onResume() {
        super.onResume();
        iniCamera();

    }

    protected void iniCamera() {
        try {
            FoCamera = Camera.open(0);
            FoCamera.setDisplayOrientation(90);
            // FoCamera.setPreviewDisplay(holder);
            //if(FoHandler!=null) FoHandler.start(FoCamera);
        } catch (Exception E) {

        }
    }

    protected void closeCamera() {
        FoHandler.stop();
        FoCamera.stopPreview();
        FoCamera.release();
        FoCamera = null;
    }

    protected void showError(String sError) {
        TextView oText = (TextView) findViewById(R.id.tverror);
        if (oText != null) oText.setText(sError);
    }
}
