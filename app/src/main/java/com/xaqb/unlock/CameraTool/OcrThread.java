package com.xaqb.unlock.CameraTool;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;

import com.xaqb.unlock.Utils.CameraUtils;

import java.io.ByteArrayOutputStream;

public class OcrThread extends Thread {

    private Handler FoHandler = null;
    private int FiSucc;
    private int FiFail;
    private byte[] FaData;
    private int FiMax = 800;//���ͼ�񳤶�
    private int FiWidth;
    private int FiHeight;

    public void setData(byte[] aData) {
        FaData = aData;
    }

    public void setHanlder(Handler oHandler, int iSucc, int iFail) {
        FoHandler = oHandler;
        FiSucc = iSucc;
        FiFail = iFail;
    }

    public void setSize(int iWidth, int iHeight) {
        FiWidth = iWidth;
        FiHeight = iHeight;
    }

    protected void sendError(String sError) {
        if (FoHandler != null) {
            Message oMess = FoHandler.obtainMessage(FiFail, sError);
            oMess.sendToTarget();
        }
    }

    @Override
    public void run() {
        try {
            long iTick = SystemClock.uptimeMillis();
            //Bitmap oBmp=BitmapFactory.decodeByteArray(FaData, 0, FaData.length);
            Bitmap oBmp = getBitmap();
            if (oBmp == null) {
                sendError("解码错误");
                return;
            }
            oBmp = CameraUtils.rotatePhoto(oBmp, 90);
            int iWidth = oBmp.getWidth();
            int iHeight = oBmp.getHeight();

            Rect oCertNo = CameraUtils.getCertNoRect(iWidth, iHeight);


            Bitmap oTmp = CameraUtils.cropPhoto(oBmp, oCertNo.left, oCertNo.top, oCertNo.right - oCertNo.left, oCertNo.bottom - oCertNo.top);
            //oTmp=ProcUnit.cropCertNo(oTmp);
            /*
            Bundle oBundle=new Bundle();
			oBundle.putString("no", "");
			Message oMess=FoHandler.obtainMessage(FiSucc, oTmp);
			oMess.setData(oBundle);
			oMess.sendToTarget();
			Log.w("cert","over.");*/

            Log.w("cert", "start...");
            String sNo = "";
            sNo = CameraUtils.ReadCert(oTmp);
            Log.i("read_cert", "用的新方法");
            if (sNo.length() > 18) sNo = sNo.substring(sNo.length() - 18);
            Log.w("cert", Long.toString(SystemClock.uptimeMillis() - iTick));
            Log.w("cert", sNo);
            boolean bSucc = CameraUtils.checkCertNo(sNo);
            if (!bSucc) {
                    sNo = CameraUtils.ReadCert(oTmp);
                    Log.i("read_cert", "用的新方法");
                if (sNo.length() > 18) sNo = sNo.substring(sNo.length() - 18);
                bSucc = CameraUtils.checkCertNo(sNo);
            }

            if (bSucc) {
                oCertNo = CameraUtils.getCertRect(iWidth, iHeight);
                oTmp = CameraUtils.cropPhoto(oBmp, oCertNo.left, oCertNo.top, oCertNo.right - oCertNo.left, oCertNo.bottom - oCertNo.top);
                oTmp = CameraUtils.scalePhoto(oTmp, FiMax);
                Bundle oBundle = new Bundle();
                oBundle.putString("no", sNo);
                Message oMess = FoHandler.obtainMessage(FiSucc, oTmp);
                oMess.setData(oBundle);
                oMess.sendToTarget();
            } else
                sendError(sNo);
        } catch (Exception E) {

            sendError(E.getMessage());

        }
    }

    public Bitmap getBitmap() {
        Bitmap oBmp = null;
        try {
            YuvImage oImage = new YuvImage(FaData, ImageFormat.NV21, FiWidth, FiHeight, null);
            if (oImage != null) {
                ByteArrayOutputStream oStream = new ByteArrayOutputStream();
                oImage.compressToJpeg(new Rect(0, 0, FiWidth, FiHeight), 50, oStream);
                oBmp = BitmapFactory.decodeByteArray(oStream.toByteArray(), 0, oStream.size());
                oStream.flush();
                oStream.close();
            }
        } catch (Exception ex) {
            Log.e("Sys", "Error:" + ex.getMessage());
        }
        return oBmp;
    }

    public Rect getFacRect(int iW, int iH) {
        Rect oRC = new Rect();
        int iFactW = iH * FiWidth / FiHeight;
        if (iFactW > iW) {
            int iFactH = iW * FiHeight / FiWidth;
            oRC.left = 0;
            oRC.right = iW;
            oRC.top = (iH - iFactH) / 2;
            oRC.bottom = iH - (iH - iFactH) / 2;
        }
        return oRC;
    }
}
