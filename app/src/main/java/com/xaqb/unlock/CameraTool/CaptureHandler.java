package com.xaqb.unlock.CameraTool;

import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;


public final class CaptureHandler extends Handler {

    private static final String TAG = CaptureHandler.class.getSimpleName();

    private AutoFocusCallback FoFocus;
    private TakePictureCallback FoTake;
    private PreviewCallback FoPreview;
    private Camera FoCamera;
    private boolean FbRun = false;
    private CertCaptureActivity FoContext;
    private int FiWidth = 0;
    private int FiHeight = 0;

    public CaptureHandler(CertCaptureActivity oContext) {
        FoFocus = new AutoFocusCallback();
        FoFocus.setHandler(this, 0);
        FoTake = new TakePictureCallback();
        FoTake.setHandler(this, 1);
        FoPreview = new PreviewCallback();
        FoPreview.setHandler(this, 1);
        FoContext = oContext;
    }

    public void start(Camera oCamera) {
        FoCamera = oCamera;
        if (oCamera != null) {
            FiWidth = oCamera.getParameters().getPreviewSize().width;
            FiHeight = oCamera.getParameters().getPreviewSize().height;
        }
        FbRun = true;
        FoFocus.setHandler(this, 0);
        // FoCamera.setPreviewCallback(FoPreview);
        FoCamera.autoFocus(FoFocus);
    }

    public void stop() {
        FbRun = false;
    }

    @Override
    public void handleMessage(Message message) {
        if (!FbRun) return;
        switch (message.what) {
            case 0:
                if (message.arg1 == 1) {
                    //FoCamera.takePicture(null, null, FoTake);
                    FoPreview.setHandler(this, 1);
                    FoCamera.setOneShotPreviewCallback(FoPreview);
                } else {
                    FoFocus.setHandler(this, 0);
                    FoCamera.autoFocus(FoFocus);
                }
                break;
            case 1:
                OcrThread oThread = new OcrThread();
                oThread.setHanlder(this, 2, 3);
                oThread.setData((byte[]) message.obj);
                oThread.setSize(FiWidth, FiHeight);
                oThread.start();
                FoFocus.setHandler(this, 0);
                FoCamera.autoFocus(FoFocus);
        /*
         FoCamera.startPreview();
    	 FoFocus.setHandler(this, 0);
    	 new Thread(new Runnable(){

    		    public void run(){
                  try
                  {
    		       Thread.sleep(1500);
     		        FoCamera.autoFocus(FoFocus);
                  }
                  catch(Exception E)
                  {

                  }

    		    }

    		}).start(); */
                break;
            case 2:
                FbRun = false;
                Bundle oBundle = message.getData();
                Bitmap oBmp = (Bitmap) message.obj;
                if (FoContext != null) {
                    stop();
                    FoContext.over(oBundle.getString("no"), oBmp);
                    //FoContext.finish();
                }
                break;
            case 3:
                // FoCamera.startPreview();
                //  FoFocus.setHandler(this, 0);
                // FoCamera.autoFocus(FoFocus);
                String sError = (String) message.obj;
                if (FoContext != null) FoContext.showError("识别错误" + sError + ",请适当调整证件距离");
                break;
        }
    }


}
