package com.xaqianbai.QBHotelSecurutyGovernor.Utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.media.FaceDetector;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.tencent.bugly.crashreport.CrashReport;
import com.xaqianbai.QBHotelSecurutyGovernor.other.Tarea;
import com.xaqianbai.QBHotelSecurutyGovernor.other.TareaSet;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public final class ProcUnit {

    public static String formatDate(Date oDate, String sFormat) {
        SimpleDateFormat oFormat = new SimpleDateFormat(sFormat);
        return oFormat.format(oDate);
    }

    /**
     * [获取应用程序版本名称信息]
     *
     * @param context
     * @return 当前应用的版本名称
     */
    public static String getVersionName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            return packageInfo.versionName;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    //md5
    public static String md5(String string) {
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("不支持md5", e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("不支持utf-8", e);
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10) hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();
    }


    //图片的缩放
    public static Bitmap scalePhoto(Bitmap oSrc, float fRate) {
        Matrix oMatrix = new Matrix();
        oMatrix.postScale(fRate, fRate); //长和宽放大缩小的比例
        return Bitmap.createBitmap(oSrc, 0, 0, oSrc.getWidth(), oSrc.getHeight(), oMatrix, true);
    }

    //按最大边长缩放
    public static Bitmap scalePhoto(Bitmap oSrc, int iMax) {
        int iWidth = oSrc.getWidth();
        int iHeight = oSrc.getHeight();
        float fRate = 0;
        if (iWidth > iHeight) fRate = (float) iMax / (float) iWidth;
        else fRate = (float) iMax / (float) iHeight;
        return scalePhoto(oSrc, fRate);
    }

    //图片的旋转
    public static Bitmap rotatePhoto(Bitmap oSrc, int iAngle) {
        try {
            Matrix oMatrix = new Matrix();
            oMatrix.postRotate(iAngle); //旋转角度
            return Bitmap.createBitmap(oSrc, 0, 0, oSrc.getWidth(), oSrc.getHeight(), oMatrix, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return oSrc;
    }

    public static Bitmap rotateRect(Bitmap oSrc) {
        if (oSrc.getHeight() > oSrc.getWidth()) return rotatePhoto(oSrc, -90);
        else return oSrc;
    }

    //图片裁剪
    public static Bitmap cropPhoto(Bitmap oSrc, int iX, int iY, int iWidth, int iHeight) {

        return Bitmap.createBitmap(oSrc, iX, iY, iWidth, iHeight, null, false);
    }



    //------------------------------------------
    public static Bitmap certPhoto(Bitmap oBmp, EditText oCert) {

        float[] aFace = getFace(oBmp);
        if (aFace[0] > 0 && aFace[1] > 0 && aFace[2] > 0) {
            Bitmap oTmp = getOne(oBmp, aFace);
            if (oTmp != null) {
                int[] aPix;
                int iWidth = oTmp.getWidth();
                int iHeight = oTmp.getHeight();
                aPix = blackWhite(oTmp);
                int[] aBottom = getAngle(aPix, iWidth, iHeight);
                Log.w("angle", Integer.toString(aBottom[2]));
                if (aBottom[2] != 0) {
                    oBmp = rotatePhoto(oBmp, -1 * aBottom[2]);
                    oTmp = getOne(oBmp, aFace);
                    aPix = blackWhite(oTmp);

                }

                if (aFace[0] == 0) return oBmp;
                int iBottom = (int) (aFace[1] + aFace[2] * 2);
                if (iBottom < 0) iBottom = 0;
                aBottom = getAngle(aPix, iWidth, iHeight);
                if (aBottom[0] > 0 && aBottom[1] > 0)
                    iBottom = iBottom + aBottom[0] + 2 * aBottom[1];
                Log.w("bottom", Integer.toString(iBottom));
                oTmp = getTwo(oBmp, aFace);
                if (oTmp != null) {
                    aPix = blackWhite(oTmp);
                    int iTop = (int) (aFace[1] - aFace[2] * 4.5);
                    if (iTop < 0) iTop = 0;
                    iTop = iTop + getTop(aPix, oTmp.getWidth(), oTmp.getHeight());
                    Log.w("top", Integer.toString(iTop));
                    iWidth = oBmp.getWidth();
                    iHeight = oBmp.getHeight();
                    if (iTop >= 0 && iBottom > 0 && iBottom < iHeight && iTop < iHeight && iBottom > iTop) {
                        oTmp = getThree(oBmp, (int) aFace[0], (int) (iBottom - aBottom[1] * 3), (int) (aFace[2] * 4), (int) (aBottom[1] * 3));
                        if (oTmp == null) return oBmp;
                        aPix = blackWhite(oTmp);
                        int iRight = (int) aFace[0] + getRight(aPix, oTmp.getWidth(), oTmp.getHeight());
                        Log.w("right", Integer.toString(iRight));
                        int iLeft = iRight - (iBottom - iTop) * 856 / 540;
                        if (iLeft < 0) iLeft = 0;
                        if (iLeft >= 0 && iRight >= 0 && iRight > iLeft && iLeft < iWidth && iRight < iWidth) {
                            oTmp = getCertNo(oBmp, iLeft, (int) (iBottom - aBottom[1] * 3), iRight - iLeft, (int) (aBottom[1] * 3));
                            //oTmp=photoToCertNo(oTmp);
                            //if(oTmp!=null) return oTmp;
                            String sNo = photoToCertNo(oTmp, false);
                            oCert.setText(sNo);
                            Log.w("cert", sNo);
                            oTmp = cropPhoto(oBmp, iLeft, iTop, iRight - iLeft, iBottom - iTop);
                            if (oTmp.getWidth() > 800) return scalePhoto(oTmp, 800);
                            else return oTmp;
                        }
                    }
                }

            }
        }
        return scalePhoto(oBmp, 800);


    }

    public static String certNo(Bitmap oBmp) {
        return "";
    }

    public static Bitmap postPhoto(Bitmap oBmp) {
        Bitmap oTmp = scalePhoto(oBmp, 1000);
        return oTmp;
        //return rotateRect(oTmp);
    }

    public static Bitmap postNoPhoto(Bitmap oBmp) {
        Bitmap oTmp = scalePhoto(oBmp, 1000);
        return oTmp;
        //return oTmp;
        //return rotateRect(oTmp);
    }

    //----发送一个文件------------------------------
    public static String sendFile(String sUrl, String sFile) throws Exception {
        URL oUrl = new URL(sUrl);
        HttpURLConnection oHttp = (HttpURLConnection) oUrl.openConnection();
        oHttp.setReadTimeout(60000);
        oHttp.setDoInput(true);// 允许输入
        oHttp.setDoOutput(true);// 允许输出
        oHttp.setUseCaches(false);
        oHttp.setRequestMethod("POST"); // Post方式
        DataOutputStream oOutput = new DataOutputStream(oHttp.getOutputStream());
        FileInputStream oFile = new FileInputStream(sFile);
        byte[] aBuf = new byte[1024];
        int iLen = 0;
        while ((iLen = oFile.read(aBuf)) != -1) {
            oOutput.write(aBuf, 0, iLen);
        }
        oFile.close();
        oOutput.flush();
        if (oHttp.getResponseCode() == 200) {
            return procRet(readStream(oHttp.getInputStream()));

        }
        return "";
    }

    public static byte[] readStream(InputStream inStream) throws Exception {
        ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = -1;
        while ((len = inStream.read(buffer)) != -1) {
            outSteam.write(buffer, 0, len);
        }
        outSteam.close();
        inStream.close();
        return outSteam.toByteArray();

    }

    public static String readToFile(InputStream inStream, String sFile, long iAll, Handler oHandler) {
        try {
            File oFile = new File(sFile);
            FileOutputStream oStream = new FileOutputStream(oFile);
            byte[] buffer = new byte[1024];
            int len = -1;
            long iCount = 0;
            int iProgress = 0;
            int iSend = 0;
            Message oMess = new Message();
            Log.w("error", Long.toString(iAll));
            while ((len = inStream.read(buffer)) != -1) {
                oStream.write(buffer, 0, len);
                iCount += len;
                if (iAll > 0) {
                    iProgress = (int) (((float) iCount / iAll) * 100);
                    if (iProgress > iSend) {
                        //oMess.what=4;
                        // oMess.arg1=iProgress;
                        //oHandler.sendMessage(oMess);
                        //iSend=iProgress;
                    }
                }
            }
            oStream.close();
            inStream.close();
            return "";
        } catch (Exception E) {
            return E.getMessage();
        }

    }

    public static String procRet(byte[] aData) {
        Log.i("-------adata", new String(aData));
        if (aData.length == 0) return "1返回数据格式错误";
        int iEnd = 0, i;
        for (i = 2; i < aData.length; i++)
            if (aData[i] == 1) {
                iEnd = i;
                break;
            }
        if (iEnd == 0) return "1返回数据格式错误";
        {
            byte[] aTmp = new byte[iEnd - 1];
            aTmp[0] = aData[0];
            for (i = 2; i < iEnd; i++)
                aTmp[i - 1] = aData[i];
            return new String(aTmp);
        }

    }


    public static String httpGet(String sUrl) throws Exception {
        URL oUrl = new URL(sUrl);
        String sRet = "";
        HttpURLConnection oHttp = (HttpURLConnection) oUrl.openConnection();
        oHttp.setReadTimeout(60000);
        oHttp.setDoInput(true);// 允许输入
        oHttp.setDoOutput(true);// 允许输出
        oHttp.setRequestMethod("GET");
        oHttp.connect();
        if (oHttp.getResponseCode() == 200)
            sRet = procRet(readStream(oHttp.getInputStream()));
        else sRet = "1" + oHttp.getResponseMessage();
        //oHttp.disconnect();
        return sRet;
    }


    //直接获取无结构字符串
    /*
    public static String httpGetMore(String sUrl) throws Exception
	{
		URL oUrl = new URL(sUrl);
		String sRet="";
	    HttpURLConnection oHttp = (HttpURLConnection) oUrl.openConnection();
	    oHttp.setReadTimeout(60000);
	    oHttp.setDoInput(true);// 允许输入
	    oHttp.setDoOutput(true);// 允许输出
	    oHttp.setRequestMethod("GET");
	    oHttp.connect();
        if(oHttp.getResponseCode()==200)
           sRet="0"+(new String(readStream(oHttp.getInputStream())));
        else sRet="1"+oHttp.getResponseMessage();
        oHttp.disconnect();
		return sRet;
	}*/

    public static String httpGetMore(String sUrl) throws Exception {
        DefaultHttpClient client = new DefaultHttpClient();
        HttpGet oGet = new HttpGet(sUrl);//此处的URL为http://..../path?arg1=value&....argn=value
        HttpResponse oResponse = client.execute(oGet); //模拟请求
        int iCode = oResponse.getStatusLine().getStatusCode();//返回响应码
        if (iCode == 200) {
            return "0" + (new String(readStream(oResponse.getEntity().getContent())));//服务器返回的数据

        } else return "1" + oResponse.getStatusLine().getReasonPhrase();
    }

    public static String httpGetFile(String sUrl, String sFile, Handler oHandler) {
        try {
            DefaultHttpClient client = new DefaultHttpClient();
            HttpGet oGet = new HttpGet(sUrl);//此处的URL为http://..../path?arg1=value&....argn=value
            HttpResponse oResponse = client.execute(oGet); //模拟请求
            int iCode = oResponse.getStatusLine().getStatusCode();//返回响应码
            if (iCode == 200) {
                long iAll = oResponse.getEntity().getContentLength();
                String sRet = readToFile(oResponse.getEntity().getContent(), sFile, iAll, oHandler);//服务器返回的数据
                if (sRet.length() == 0) return "";
                else return sRet;

            } else return "1" + oResponse.getStatusLine().getReasonPhrase();

        } catch (Exception E) {
            return E.getMessage();
        }
    }

    public static String httpGetRight(String sUrl, String sUser, String sRight) throws Exception {
        try {
            DefaultHttpClient client = new DefaultHttpClient();
            String sUrlCode = sUrl + "/getCode.ashx";
            Log.i("chen", sUrlCode);
            HttpGet oGet = new HttpGet(sUrlCode);//此处的URL为http://..../path?arg1=value&....argn=value
            HttpResponse oResponse = client.execute(oGet); //模拟请求
            int iCode = oResponse.getStatusLine().getStatusCode();//返回响应码
            if (iCode == 200) {
                String sRet = procRet(readStream(oResponse.getEntity().getContent()));//服务器返回的数据
                if (!sRet.substring(0, 1).equals("0")) return sRet;
                String sCode = sRet.substring(1);
                SystemClock.sleep(2000);
                sUrlCode = sUrl + "/getRightByCode.ashx?user=" + sUser + "&code=" + sCode + "&right=" + sRight;
                Log.i("chen_surl", sUrlCode);
                oGet = new HttpGet(sUrlCode);//此处的URL为http://..../path?arg1=value&....argn=value
                oResponse = client.execute(oGet); //模拟请求
                iCode = oResponse.getStatusLine().getStatusCode();//返回响应码
                if (iCode == 200) {
                    sRet = procRet(readStream(oResponse.getEntity().getContent()));
                    Log.i("chen_surl", sRet);
                    return sRet;
                } else return "1获取授权失败";
            } else return "1获取挑战码失败";
        } catch (Throwable e) {
            CrashReport.postCatchedException(e);  // bugly会将这个throwable上报
            return "1" + e.getMessage();
        }

    }

    public static String httpCheckRight(String sUrl, String sUser, String sRight) throws Exception {
        try {
            DefaultHttpClient client = new DefaultHttpClient();
            String sUrlCode = sUrl + "/getCode.ashx";
            HttpGet oGet = new HttpGet(sUrlCode);//此处的URL为http://..../path?arg1=value&....argn=value
            HttpResponse oResponse = client.execute(oGet); //模拟请求
            int iCode = oResponse.getStatusLine().getStatusCode();//返回响应码
            if (iCode == 200) {
                String sRet = procRet(readStream(oResponse.getEntity().getContent()));//服务器返回的数据
                if (!sRet.substring(0, 1).equals("0")) return sRet;
                String sCode = sRet.substring(1);
                sCode = md5(sCode + sRight).toUpperCase();
                sUrlCode = sUrl + "/checkRight.ashx?user=" + sUser + "&code=" + sCode;
                Log.i("check_right", sUrlCode);
                oGet = new HttpGet(sUrlCode);//此处的URL为http://..../path?arg1=value&....argn=value
                oResponse = client.execute(oGet); //模拟请求
                iCode = oResponse.getStatusLine().getStatusCode();//返回响应码
                if (iCode == 200) {
                    sRet = procRet(readStream(oResponse.getEntity().getContent()));
                    Log.i("check_right_response", sRet);
                    return sRet;
                } else if (iCode == 404) {
                    return "2获取授权失败";
                } else {
                    return "1获取授权失败";
                }
            } else return "1获取挑战码失败";
        } catch (Throwable e) {
            CrashReport.postCatchedException(e);  // bugly会将这个throwable上报
            return "1" + e.getMessage();
        }
    }

    public static String httpSendSms(String sUrl, String sID, String sPhone) throws Exception {
        DefaultHttpClient client = new DefaultHttpClient();
        String sUrlCode = sUrl + "/getCode.ashx";
        HttpGet oGet = new HttpGet(sUrlCode);//此处的URL为http://..../path?arg1=value&....argn=value
        Log.i("url", sUrlCode);
        HttpResponse oResponse = client.execute(oGet); //模拟请求
        int iCode = oResponse.getStatusLine().getStatusCode();//返回响应码
        if (iCode == 200) {
            String sRet = procRet(readStream(oResponse.getEntity().getContent()));//服务器返回的数据
            if (!sRet.substring(0, 1).equals("0")) return sRet;
            String sCode = sRet.substring(1);
            SystemClock.sleep(2000);
            sUrlCode = sUrl + "/sendSmsForRight.ashx?id=" + sID + "&code=" + sCode + "&phone=" + sPhone;
            Log.i("chen_surl", sUrlCode);
            oGet = new HttpGet(sUrlCode);//此处的URL为http://..../path?arg1=value&....argn=value
            oResponse = client.execute(oGet); //模拟请求
            iCode = oResponse.getStatusLine().getStatusCode();//返回响应码
            if (iCode == 200) {
//                Log.i("chen_surl", procRet(readStream(oResponse.getEntity().getContent())));
                return sRet = procRet(readStream(oResponse.getEntity().getContent()));
            } else return "1数据核对失败";
        } else return "1获取挑战码失败";
    }

    public static String httpUploadFile(String sUrl, String sUser, String sPath, String sFile, String sRight) throws Exception {
        try {
            DefaultHttpClient client = new DefaultHttpClient();
            String sUrlCode = sUrl + "/getCode.ashx";
            HttpGet oGet = new HttpGet(sUrlCode);//此处的URL为http://..../path?arg1=value&....argn=value
//            oGet.setHeader("User-Agent", String.format("%s/%s (Linux; Android %s; %s Build/%s)", "qbunlock", MyApplication.versionName, Build.VERSION.RELEASE, Build.MANUFACTURER, Build.ID));
            oGet.setHeader("User-Agent", String.format("express/1.029 (Linux; Android 5.1.1; RaySco Build/LMY47V)", "qbunlock", MyApplication.versionName, Build.VERSION.RELEASE, Build.MANUFACTURER, Build.ID));
            HttpResponse oResponse = client.execute(oGet); //模拟请求
            int iCode = oResponse.getStatusLine().getStatusCode();//返回响应码
            if (iCode == 200) {
                String sRet = procRet(readStream(oResponse.getEntity().getContent()));//服务器返回的数据
                if (!sRet.substring(0, 1).equals("0")) return sRet;
                String sCode = sRet.substring(1);
                SystemClock.sleep(2000);
                sCode = md5(sCode + sRight).toUpperCase();
                Log.i("scode", sCode);
                sUrlCode = sUrl + "/upload.ashx?user=" + sUser + "&code=" + sCode + "&file=" + sFile;
                HttpPost oPost = new HttpPost(sUrlCode);//此处的URL为http://..../path?arg1=value&....argn=value
                File oFile = new File(sPath + "/" + sFile);
                FileInputStream oFileStream = new FileInputStream(oFile);
                InputStreamEntity reqEntity = new InputStreamEntity(oFileStream, oFile.length());
                oPost.setEntity(reqEntity);
//                reqEntity.setContentType("binary/octet-stream");
                //2017-01-23修改上传方法
                reqEntity.setContentType("multipart/form-data");
                oResponse = client.execute(oPost); //模拟请求
                iCode = oResponse.getStatusLine().getStatusCode();//返回响应码
                Log.i("0123", "-------" + iCode + "-------" + sFile);
                if (iCode == 200) {
                    sRet = procRet(readStream(oResponse.getEntity().getContent()));
                    Log.i("response", "" + sRet + "--------filesize" + oFile.getTotalSpace());
                    return sRet;
                } else return "1" + oResponse.getStatusLine().getReasonPhrase();
            } else return "1" + oResponse.getStatusLine().getReasonPhrase();
        } catch (Throwable e) {
            CrashReport.putUserData(MyApplication.instance, "user", sUser);
            CrashReport.postCatchedException(e);  // bugly会将这个throwable上报
            return "1" + e.getMessage();
        }

    }

    public static String getTimeStr(String sFormat) {
        SimpleDateFormat oFormat = new SimpleDateFormat(sFormat);
        return oFormat.format(new Date());
    }


    public static float[] getFace(Bitmap oSrc) {
        float[] aRet = new float[3];
        aRet[0] = 0;
        aRet[1] = 0;
        aRet[2] = 0;
        try {

            int iWidth, iHeight;
            int iMaxFace = 2;       //最大检测的人脸数
            FaceDetector oFaceDetect;  //人脸识别类的实例
            FaceDetector.Face[] aFace; //存储多张人脸的数组变量
            float fEyes;           //两眼之间的距离
            int iFaceNum = 0;       //实际检测到的人脸数
            BitmapFactory.Options oOptions = new BitmapFactory.Options();
            oOptions.inPreferredConfig = Bitmap.Config.RGB_565;  //构造位图生成的参数，必须为565。类名+enum
            ByteArrayOutputStream oOutput = new ByteArrayOutputStream();
            oSrc.compress(Bitmap.CompressFormat.JPEG, 100, oOutput);
            Bitmap oBmp = BitmapFactory.decodeByteArray(oOutput.toByteArray(), 0, oOutput.size(), oOptions);
            iWidth = oBmp.getWidth();
            iHeight = oBmp.getHeight();
            aFace = new FaceDetector.Face[iMaxFace];       //分配人脸数组空间
            oFaceDetect = new FaceDetector(iWidth, iHeight, iMaxFace);
            iFaceNum = oFaceDetect.findFaces(oBmp, aFace);    //FaceDetector 构造实例并解析人脸
            if (iFaceNum == 0) return aRet;
            PointF oMid = new PointF();
            aFace[0].getMidPoint(oMid);
            fEyes = aFace[0].eyesDistance();
            aRet[0] = oMid.x;
            aRet[1] = oMid.y;
            aRet[2] = fEyes;
            return aRet;

        } catch (Exception E) {
            return null;
        }
    }

    //对身份证图像进行处理，返回身份证图像
    public static Bitmap getOne(Bitmap oSrc, float[] aFace) {
        try {
            if (aFace[0] == 0) return null;
            int iX = (int) (aFace[0] - aFace[2] * 5);
            int iY = (int) (aFace[1] + aFace[2] * 2);
            int iW = (int) (aFace[2] * 6);
            int iH = (int) (aFace[2] * 5);
            int iWidth = oSrc.getWidth();
            int iHeight = oSrc.getHeight();
            if (iX < 0) iX = 0;
            if (iY < 0) iY = 0;
            if (iX >= iWidth || iY >= iHeight) return null;
            if (iX + iW > iWidth) iW = iWidth - iX - 1;
            if (iY + iH > iHeight) iH = iHeight - iY - 1;
            if (iW < 0 || iW > iWidth || iH < 0 || iH > iHeight) return null;
            Bitmap oBmp = cropPhoto(oSrc, iX, iY, iW, iH);
            return oBmp;
        } catch (Exception E) {
            return null;
        }



          /*

		  oBmp=cropPhoto(oSrc,(int)(oMid.x-fEyes),(int)(oMid.y-fEyes),(int)(fEyes*2),(int)(fEyes*2));
		  return oBmp;*/
        //return null;

    }


    //图像二值化化，返回二值化后的数组
    public static int[] blackWhite(Bitmap oSrc) {
        if (oSrc == null) return new int[0];
        int iWidth = oSrc.getWidth();
        int iHeight = oSrc.getHeight();
        int[] aPix = new int[iWidth * iHeight];
        oSrc.getPixels(aPix, 0, iWidth, 0, 0, iWidth, iHeight);

        int iGray;
        int i, j;

        int iMax = 0, iMin = 255;
        for (i = 0; i < aPix.length; i++) {
            iGray = (int) (0.30 * ((aPix[i] >> 16) & 0xFF) + 0.59 * ((aPix[i] >> 8) & 0xFF) + 0.11 * (aPix[i] & 0XFF));
            aPix[i] = iGray;
            if (iGray > iMax) iMax = iGray;
            if (iGray < iMin) iMin = iGray;
        }
        int iPart = iMin + (iMax - iMin) / 2;
        for (i = 0; i < aPix.length; i++)
            if (aPix[i] < iPart)
                aPix[i] = 0;//Color.rgb(0, 0, 0);
            else aPix[i] = 1;//Color.rgb(255, 255, 255);
        return aPix;
    }

    public static void saveBmp(Bitmap oSrc, String sFile) {
        try {
            File oFile = new File("/storage/sdcard1/expressTest/" + sFile);
            if (oFile.exists()) oFile.delete();
            oFile.createNewFile();
            FileOutputStream oOut = new FileOutputStream(oFile);
            oSrc.compress(Bitmap.CompressFormat.JPEG, 90, oOut);
            oOut.flush();
            oOut.close();

        } catch (Exception e) {
            Log.w("cert", e.getMessage());
        }
    }

    //图像二值化化，返回二值化后的数组
    public static int[] blackWhite(Bitmap oSrc, boolean bSeg) {
        if (oSrc == null) return new int[0];
        int iWidth = oSrc.getWidth();
        int iHeight = oSrc.getHeight();
        int[] aPix = new int[iWidth * iHeight];
        int[] aValue = new int[iWidth];
        oSrc.getPixels(aPix, 0, iWidth, 0, 0, iWidth, iHeight);
        ProcUnit.saveBmp(oSrc, "src.jpg");
        int iGray;
        int i, j, iPart, iPos, h, iFrom;
        boolean bFind;

        int iMax = 0, iMin = 255;
        for (i = 0; i < aPix.length; i++) {
            iGray = (int) (0.30 * ((aPix[i] >> 16) & 0xFF) + 0.59 * ((aPix[i] >> 8) & 0xFF) + 0.11 * (aPix[i] & 0XFF));
            aPix[i] = iGray;
            if (iGray > iMax) iMax = iGray;
            if (iGray < iMin) iMin = iGray;
        }
        if (bSeg) {
            for (i = 0; i < iWidth; i++) {
                iMin = 255;
                iMax = 0;
                for (j = 0; j < iHeight; j++) {
                    iPart = aPix[j * iWidth + i];
                    if (iPart > iMax) iMax = iPart;
                    if (iPart < iMin) iMin = iPart;
                }
                aValue[i] = iMax - iMin;
            }
            while (true) {
                iMax = 0;
                iPos = -1;
                for (i = 0; i < iWidth; i++)
                    if (aValue[i] < 255 && aValue[i] > iMax) {
                        iMax = aValue[i];
                        iPos = i;
                    }
                if (iPos == -1) break;
                iMin = iMax / 2;
                bFind = false;
                for (i = iPos; i < iWidth; i++) {
                    if (aValue[i] < iMin) {
                        aValue[i] = 0;
                        bFind = true;
                    } else {
                        if (bFind) break;
                        aValue[i] = 255;
                    }
                }
                bFind = false;
                for (i = iPos; i >= 0; i--) {
                    if (aValue[i] < iMin) {
                        aValue[i] = 0;
                        bFind = true;
                    } else {
                        if (bFind) break;
                        aValue[i] = 255;
                    }
                }
            }

            ArrayList<Integer> oPos = new ArrayList<Integer>();
            oPos.add(0);
            for (i = 0; i < iWidth; i++) {
                if ((i > 0 && aValue[i - 1] == 255 && aValue[i] == 0)) iPos = i;


                if (i < iWidth - 1 && aValue[i] == 0 && aValue[i + 1] == 255 && iPos > 0 && (i - iPos > 10)) {
                    oPos.add(iPos + (i - iPos) / 2);
                    iPos = -1;
                }

            }
            oPos.add(iWidth);
            String sTmp = "";
            for (i = 0; i < oPos.size(); i++)
                sTmp = sTmp + "," + Integer.toString(oPos.get(i));
            Log.w("cert", sTmp);
            for (h = 0; h < oPos.size() - 1; h++) {
                iFrom = oPos.get(h);
                iPos = oPos.get(h + 1);
                iMin = 255;
                iMax = 0;
                for (i = iFrom; i < iPos; i++)
                    for (j = 0; j < iHeight; j++) {
                        iGray = aPix[j * iWidth + i];
                        if (iGray > iMax) iMax = iGray;
                        if (iGray < iMin) iMin = iGray;
                    }
                iPart = iMin + (iMax - iMin) / 2;
                for (i = iFrom; i < iPos; i++)
                    for (j = 0; j < iHeight; j++) {
                        iGray = aPix[j * iWidth + i];
                        if (iGray < iPart)
                            aPix[j * iWidth + i] = 0;
                        else aPix[j * iWidth + i] = 1;
                    }

            }


        } else {
            iPart = iMin + (iMax - iMin) / 2;
            for (i = 0; i < aPix.length; i++)
                if (aPix[i] < iPart)
                    aPix[i] = 0;//Color.rgb(0, 0, 0);
                else aPix[i] = 1;//Color.rgb(255, 255, 255);
        }
        //ProcUnit.saveDataToBitmap(aPix, iWidth, iHeight, "black.jpg");
        return aPix;
    }

    public static void saveDataToBitmap(int[] aData, int iWidth, int iHeight, String sFile) {
        int i;
        for (i = 0; i < aData.length; i++)
            if (aData[i] == 1) aData[i] = 0xFFFFFF;
        Bitmap oBmp = Bitmap.createBitmap(aData, iWidth, iHeight, Bitmap.Config.ARGB_8888);
        ProcUnit.saveBmp(oBmp, sFile);
    }

    public static Bitmap BWBmp(Bitmap oSrc) {
        if (oSrc == null) return null;
        int iWidth = oSrc.getWidth();
        int iHeight = oSrc.getHeight();
        int[] aPix = new int[iWidth * iHeight];
        oSrc.getPixels(aPix, 0, iWidth, 0, 0, iWidth, iHeight);

        int iGray;
        int i, j;

        int iMax = 0, iMin = 255;
        for (i = 0; i < aPix.length; i++) {
            iGray = (int) (0.30 * ((aPix[i] >> 16) & 0xFF) + 0.59 * ((aPix[i] >> 8) & 0xFF) + 0.11 * (aPix[i] & 0XFF));
            aPix[i] = iGray;
            if (iGray > iMax) iMax = iGray;
            if (iGray < iMin) iMin = iGray;
        }
        int iPart = iMin + (iMax - iMin) / 2;
        for (i = 0; i < aPix.length; i++)
            if (aPix[i] < iPart)
                aPix[i] = Color.rgb(0, 0, 0);
            else aPix[i] = Color.rgb(255, 255, 255);
        return Bitmap.createBitmap(aPix, iWidth, iHeight, Bitmap.Config.ARGB_8888);
    }


    //iType:0返回下边界 iType=1:返回角度

    public static int[] getAngle(int[] aPix, int iWidth, int iHeight) {

        int[] aRet = new int[3];
        int iGray;
        int i, j;
        int[] aBlack = new int[iHeight];
        for (i = 0; i < aBlack.length; i++) aBlack[i] = 0;
        for (i = 0; i < iHeight; i++)
            for (j = 0; j < iWidth; j++)
                if (aPix[i * iWidth + j] == 0) aBlack[i]++;

        int iTop = 0;
        int iFrom = 0;
        int iBW = 0;
        for (i = 0; i < aBlack.length; i++)
            if (aBlack[i] == 0) {
                iTop = i;
                break;
            }
        for (i = iTop; i < aBlack.length; i++)
            if (aBlack[i] > 0) {
                iTop = i;
                break;
            }
        if (iTop < aBlack.length * 3 / 5) {
            iFrom = iTop;
            iBW = 1;
            for (i = iTop; i < aBlack.length; i++)
                if (aBlack[i] > 0) iBW++;
                else break;

        }

        aRet[0] = iFrom;
        aRet[1] = iBW;


        if (iFrom == 0 || iFrom + iBW >= iHeight) {
            aRet[0] = 0;
            aRet[1] = 0;
            aRet[2] = 0;
            return aRet;
        }
        int iTest = iWidth / 6;
        int iX1 = 0, iY1 = 0, iX2 = 0, iY2 = 0;
        for (i = iFrom; i < iFrom + iBW; i++)
            for (j = 0; j < iTest; j++)
                if (aPix[i * iWidth + j] == 0) {
                    iX1 = j;
                    iY1 = i;
                    break;
                }
        for (i = iFrom; i < iFrom + iBW; i++)
            for (j = iWidth - iTest; j < iWidth; j++)
                if (aPix[i * iWidth + j] == 0) {
                    iX2 = j;
                    iY2 = i;
                    break;
                }
        if (iY1 != iY2 && iX2 - iX1 != 0) {
            double fAngle = Math.atan((double) (iY2 - iY1) / (double) (iX2 - iX1));
            aRet[2] = (int) (Math.toDegrees(fAngle));
        } else aRet[2] = 0;
        return aRet;
    }

    public static Bitmap getTwo(Bitmap oSrc, float[] aFace) {
        try {

            if (aFace[0] == 0) return null;
            int iX = (int) (aFace[0] - aFace[2] * 8);
            int iY = (int) (aFace[1] - aFace[2] * 4.5);
            int iW = (int) (aFace[2] * 8);
            int iH = (int) (aFace[2] * 4);
            int iWidth = oSrc.getWidth();
            int iHeight = oSrc.getHeight();
            if (iX < 0) iX = 0;
            if (iY < 0) iY = 0;
            if (iX >= iWidth || iY >= iHeight) return null;
            if (iX + iW > iWidth) iW = iWidth - iX - 1;
            if (iY + iH > iHeight) iH = iHeight - iY - 1;
            if (iW < 0 || iW > iWidth || iH < 0 || iH > iHeight) return null;
            Bitmap oBmp = cropPhoto(oSrc, iX, iY, iW, iH);
            return oBmp;
        } catch (Exception E) {
            return null;
        }

    }

    public static int getTop(int[] aPix, int iWidth, int iHeight) {
        int iGray;
        int i, j;
        int[] aBlack = new int[iHeight];
        for (i = 0; i < aBlack.length; i++) aBlack[i] = 0;
        for (i = 0; i < iHeight; i++)
            for (j = 0; j < iWidth; j++)
                if (aPix[i * iWidth + j] == 0) aBlack[i]++;
        int iFrom = 0;
        int iWW = 0;
        int iUseFrom = 0;
        int iUseWW = 0;
        int iBottom = iHeight - 1;
        for (i = iHeight - 1; i >= 0; i--)
            if (aBlack[i] == 0) {
                iBottom = i;
                break;
            }
        for (i = iBottom; i >= 0; i--) {
            if (i == 0 || aBlack[i] > 0) {
                if (iUseWW > 0) {
                    if (iFrom > 0) {
                        if (iWW < iUseWW) {
                            iFrom = iUseFrom;
                            iWW = iUseWW;
                        }
                    } else {
                        iFrom = iUseFrom;
                        iWW = iUseWW;
                    }
                }
                iUseFrom = 0;
                iUseWW = 0;
            } else if (aBlack[i] == 0) {
                if (iUseFrom == 0) {
                    iUseFrom = i;
                    iUseWW = 1;
                } else iUseWW++;
            }
        }

        return iFrom - iWW > 0 ? (iFrom - iWW) : 0;
    }


    public static int getRight(int[] aPix, int iWidth, int iHeight) {
        int iGray;
        int i, j;

        int[] aBlack = new int[iWidth];
        for (i = 0; i < aBlack.length; i++) aBlack[i] = 0;
        for (i = 0; i < iWidth; i++)
            for (j = 0; j < iHeight; j++)
                if (aPix[j * iWidth + i] == 0) aBlack[i]++;

        int iFrom = 0;
        int iBW = 0;
        int iUseFrom = 0;
        int iUseBW = 0;
        for (i = 0; i < iWidth; i++) {
            if (aBlack[i] == 0) {
                if (iUseFrom > 0) iUseBW++;
                else {
                    iUseFrom = i;
                    iBW = 1;
                }
            }
            if (aBlack[i] > 0 || i == iWidth - 1) {
                if (iUseFrom > 0) {
                    if (iFrom > 0) {
                        if (iUseBW > iBW) {
                            iFrom = iUseFrom;
                            iBW = iUseBW;

                        }
                    } else {
                        iFrom = iUseFrom;
                        iBW = iUseBW;
                    }
                    iUseFrom = 0;
                    iUseBW = 0;
                }
            }
        }
        return iFrom + iBW;
    }


    public static Bitmap getThree(Bitmap oSrc, int iX, int iY, int iW, int iH) {
        int iWidth = oSrc.getWidth();
        int iHeight = oSrc.getHeight();
        if (iX < 0) return null;
        if (iY < 0) return null;
        if (iW <= 0 || iH <= 0) return null;
        if (iX >= iWidth) return null;
        if (iY >= iHeight) return null;
        if (iX + iW >= iWidth) iW = iWidth - iX - 1;
        if (iY + iH >= iHeight) iH = iHeight - iY - 1;
        return cropPhoto(oSrc, iX, iY, iW, iH);
    }

    public static Bitmap getCertNo(Bitmap oSrc, int iX, int iY, int iW, int iH) {
        Bitmap oCert;
        int iWidth = oSrc.getWidth();
        int iHeight = oSrc.getHeight();
        if (iX != 0 || iY != 0 || iW != iWidth || iH != iHeight) {
            if (iX < 0) return null;
            if (iY < 0) return null;
            if (iW <= 0 || iH <= 0) return null;
            if (iX >= iWidth) return null;
            if (iY >= iHeight) return null;
            if (iX + iW >= iWidth) iW = iWidth - iX - 1;
            if (iY + iH >= iHeight) iH = iHeight - iY - 1;
            oCert = cropPhoto(oSrc, iX, iY, iW, iH);
        } else oCert = oSrc;
        //if(oCert!=null) return oCert;
        iWidth = oCert.getWidth();
        iHeight = oCert.getHeight();
        int[] aPix = blackWhite(oCert);
        int[] aBlack = new int[iWidth];
        int i, j;
        for (i = 0; i < iWidth; i++) aBlack[i] = 0;
        for (i = 0; i < iWidth; i++)
            for (j = 0; j < iHeight; j++)
                if (aPix[j * iWidth + i] == 0) aBlack[i]++;
        int iAll = 0;
        int iAvgNum = 0;
        int iMid = iWidth / 2;
        int iFrom = 0;
        int iWW = 0;
        int iRight = 0;
        int iLeft = 0;
        while (aBlack[iMid] == 0 && iMid < iWidth - 1) iMid++;
        for (i = iMid; i < iWidth; i++) {
            if (aBlack[i] == 0) {
                if (iFrom > 0) iWW++;
                else {
                    iFrom = i;
                    iWW = 1;
                }

            }
            if (i == iWidth - 1 || aBlack[i] > 0) {
                if (iFrom > 0 || iWW > 0) {
                    if (iAvgNum > 8 && 3 * iAll / iAvgNum < iWW) {
                        iRight = iFrom;
                        break;
                    }
                    iAll += iWW;
                    iAvgNum++;
                }
                iFrom = 0;
                iWW = 0;
            }
        }
        iMid = iWidth / 2;
        while (aBlack[iMid] == 0 && iMid < iWidth - 1) iMid--;
        iFrom = 0;
        iWW = 0;
        for (i = iMid; i >= 0; i--) {
            if (aBlack[i] == 0) {
                if (iFrom > 0) iWW++;
                else {
                    iFrom = i;
                    iWW = 1;
                }

            }
            if (i == iWidth - 1 || aBlack[i] > 0) {
                if (iFrom > 0 || iWW > 0) {
                    if (iAvgNum > 8 && 3 * iAll / iAvgNum < iWW) {
                        iLeft = iFrom;
                        break;
                    }
                    iAll += iWW;
                    iAvgNum++;
                }
                iFrom = 0;
                iWW = 0;
            }
        }
        int iTop = 0;
        int iBottom = 0;
        if (iLeft > 0 && iRight > 0) {
            aBlack = new int[iHeight];
            for (i = 0; i < iHeight; i++) aBlack[i] = 0;
            for (i = 0; i < iHeight; i++)
                for (j = iLeft; j <= iRight; j++)
                    if (aPix[i * iWidth + j] == 0) aBlack[i]++;
            for (i = 0; i < iHeight; i++)
                if (aBlack[i] > 0) {
                    iTop = i;
                    break;
                }
            for (i = iHeight - 1; i >= 0; i--)
                if (aBlack[i] > 0) {
                    iBottom = i;
                    break;
                }
            if (iTop != iBottom) {
                if (iTop > 3) iTop -= 3;
                if (iBottom < iHeight - 4) iBottom += 3;
                if (iLeft > 3) iLeft -= 3;
                if (iRight < iWidth - 4) iRight += 3;
                return cropPhoto(oCert, iLeft, iTop, iRight - iLeft, iBottom - iTop);
            }

        }

        return null;

    }

    public static Bitmap cropCertNo(Bitmap oSrc) {
        int[] aPix = blackWhite(oSrc, true);
        int iLeft = -1;
        int iRight = -1;
        int iTop = -1;
        int iBottom = -1;
        int iWidth = oSrc.getWidth();
        int iHeight = oSrc.getHeight();
        int i, j;
        for (i = 0; i < iHeight; i++) {
            for (j = 0; j < iWidth; j++) {
                if (aPix[i * iWidth + j] == 0 && iTop == -1) iTop = i;
                if (aPix[(iHeight - i - 1) * iWidth + j] == 0 && iBottom == -1)
                    iBottom = iHeight - i;
            }
            if (iTop > -1 && iBottom > -1) break;

        }

        for (i = 0; i < iWidth; i++) {
            for (j = 0; j < iHeight; j++) {
                if (aPix[j * iWidth + i] == 0 && iLeft == -1) iLeft = i;
                if (aPix[j * iWidth + (iWidth - i - 1)] == 0 && iRight == -1) iRight = iWidth - i;
            }
            if (iLeft > -1 && iRight > -1) break;

        }

        if (iLeft >= 0 && iLeft < iWidth && iRight >= 0 && iRight < iWidth && iRight > iLeft && iTop >= 0 && iTop < iHeight && iBottom >= 0 && iBottom < iHeight && iBottom > iTop)
            return cropPhoto(oSrc, iLeft, iTop, iRight - iLeft + 1, iBottom - iTop + 1);
        else return null;


    }

    public static String photoToCertNo(Bitmap oSrc, boolean bSeg) {
        int iWidth = oSrc.getWidth();
        int iHeight = oSrc.getHeight();
        int[] aPix = blackWhite(oSrc, bSeg);
        int i, j, h;
        int[] aBlack = new int[iWidth];
        for (i = 0; i < iWidth; i++) aBlack[i] = 0;
        for (i = 0; i < iWidth; i++)
            for (j = 0; j < iHeight; j++)
                if (aPix[j * iWidth + i] == 0) aBlack[i]++;
        String sNo = "";
        int iLeft = 0;
        int iRight = 0;
        int iTop = 0;
        int iBottom = 0;
        int[] aFont;
        int iFW, iFH, iC = 0;
        for (i = 0; i < iWidth; i++) {
            if (aBlack[i] > 0) {
                if (iLeft > 0) iRight = i;
                else iLeft = i;
            } else {
                if (iLeft > 0 && iRight > 0 && iRight - iLeft > 4) {
                    iTop = -1;
                    for (j = 0; j < iHeight; j++) {
                        for (h = iLeft; h <= iRight; h++)
                            if (aPix[j * iWidth + h] == 0) {
                                iTop = j;
                                break;
                            }
                        if (iTop > -1) break;
                    }
                    iBottom = -1;
                    for (j = iHeight - 1; j >= 0; j--) {
                        for (h = iLeft; h <= iRight; h++)
                            if (aPix[j * iWidth + h] == 0) {
                                iBottom = j;
                                break;
                            }
                        if (iBottom > -1) break;
                    }
                    if (iTop > -1 && iBottom > -1) {
                        iFW = iRight - iLeft + 1;
                        iFH = iBottom - iTop + 1;
                        aFont = new int[iFW * iFH];
                        for (h = iTop; h <= iBottom; h++)
                            for (j = iLeft; j <= iRight; j++)
                                aFont[(h - iTop) * iFW + (j - iLeft)] = aPix[h * iWidth + j];
                        /*
                        for(h=iTop;h<=iBottom;h++)
							for(j=iLeft;j<iRight;j++)
								if(aFont[(h-iTop)*iFW+(j-iLeft)]==0) aFont[(h-iTop)*iFW+(j-iLeft)]=Color.rgb(0, 0, 0);
								else aFont[(h-iTop)*iFW+(j-iLeft)]=Color.rgb(255, 255, 255);

						if(iC==3)
						 return Bitmap.createBitmap(aFont, 0, iFW, iFW, iFH, Bitmap.Config.ARGB_8888);
						iC++;*/

                        sNo = sNo + getSingleNo(aFont, iFW, iFH);
                    }

                }
                iLeft = 0;
                iRight = 0;
            }
        }

        return sNo;
    }

    public static String getSingleNo(int[] aPix, int iWidth, int iHeight) {

        String sNo = "";

        sNo = isZero(aPix, iWidth, iHeight);
        if (sNo.length() == 0)
            sNo = isOne(aPix, iWidth, iHeight);
        if (sNo.length() == 0)
            sNo = isThree(aPix, iWidth, iHeight);
        if (sNo.length() == 0)
            sNo = isTwo(aPix, iWidth, iHeight);
        if (sNo.length() == 0)
            sNo = isFour(aPix, iWidth, iHeight);
        if (sNo.length() > 0) return sNo;
        else return "X";
    }

    public static String isZero(int[] aPix, int iWidth, int iHeight) {
        try {
            TareaSet oSet = new TareaSet();
            int i, j;
            int iLeft;
            for (i = 0; i < iHeight; i++) {
                iLeft = -1;
                for (j = 0; j < iWidth; j++) {
                    if (aPix[i * iWidth + j] == 0) {

                        if (iLeft > -1 && j > iLeft)
                            oSet.add(iLeft, j - 1, i, (i == 0 || i == iHeight - 1 || iLeft == 0 || iLeft == iWidth - 1 || j - 1 == 0 || j - 1 == iWidth - 1));
                        iLeft = -1;
                    } else {
                        if (iLeft == -1)
                            if (j == iWidth - 1)
                                oSet.add(j, j, i, true);
                            else iLeft = j;
                        else if (j == iWidth - 1) oSet.add(iLeft, j, i, true);
                    }
                }
            }
            oSet.Over();
            if (oSet.count() == 1) {
                Tarea oItem = oSet.get(0);
                int iTop = oItem.getTop();
                int iBottom = oItem.getBottom();
                int iMW = 0;
                int iMY = 0;
                int iC;
                for (i = 0; i < iHeight; i++) {
                    iC = 0;
                    for (j = 0; j < iWidth; j++)
                        if (aPix[i * iWidth + j] == 0) iC++;
                    if (iC >= iMW) {
                        iMW = iC;
                        iMY = i;
                    }
                }
                int iMH = 0;
                int iMX = 0;
                for (i = 0; i < iWidth; i++) {
                    iC = 0;
                    for (j = 0; j < iHeight; j++)
                        if (aPix[j * iWidth + i] == 0) iC++;
                    if (iC > iMH) {
                        iMH = iC;
                        iMX = i;
                    }
                }
                if (Math.abs(iMW - iWidth) < 2 && iMY > iHeight / 2 && Math.abs(iMH - iHeight) < 2 && iMX > iWidth / 2)
                    return "4";
                else if (iTop < iHeight / 3 && iBottom > iHeight * 2 / 3) return "0";
                else if (iTop + (iBottom - iTop) / 2 > iHeight / 2) return "6";
                else if (iTop + (iBottom - iTop) / 2 < iHeight / 2) return "9";

            } else if (oSet.count() == 2) return "8";

        } catch (Exception E) {
            Log.w("iszero", E.getMessage());
        }
        return "";
    }

    public static String isOne(int[] aPix, int iWidth, int iHeight) {
        try {
            int i, j;
            int iLeft = iWidth - 1, iRight = 0;
            for (i = iHeight / 2; i < iHeight; i++)
                for (j = 0; j < iWidth; j++)
                    if (aPix[i * iWidth + j] == 0) {
                        if (j < iLeft) iLeft = j;
                        if (j > iRight) iRight = j;
                    }


            if ((float) iWidth / (float) iHeight < 0.5) return "1";

        } catch (Exception E) {
            Log.w("isOne", E.getMessage());
        }
        return "";
    }

    public static String isTwo(int[] aPix, int iWidth, int iHeight) {
        try {
            int i, j;
            int iBL = 0;
            for (i = 0; i < iWidth; i++)
                if (aPix[(iHeight - 1) * iWidth + i] == 0 || aPix[(iHeight - 2) * iWidth + i] == 0)
                    iBL++;
            int iLW = 0;
            int iLY = 0;
            int iCount = 0;
            for (i = 0; i < iHeight; i++) {
                for (j = 0; j < iWidth; j++)
                    if (aPix[i * iWidth + j] > 0) iCount++;
                    else if (iCount > iLW) {
                        iLW = iCount;
                        iLY = i;
                        break;
                    }
                iCount = 0;
            }
            int iRW = 0;
            int iRY = 0;
            for (i = 0; i < iHeight; i++) {
                iCount = 0;
                for (j = iWidth - 1; j >= 0; j--)
                    if (aPix[i * iWidth + j] > 0) iCount++;
                    else if (iCount > iRW) {
                        iRW = iCount;
                        iRY = i;
                        break;
                    }

            }
            if (iLW > iWidth / 2 && iLY < iHeight / 2 && iRW > iWidth / 2 && iRY > iHeight / 2)
                return "2";

        } catch (Exception E) {
            Log.w("isTwo", E.getMessage());
        }
        return "";
    }

    public static String isThree(int[] aPix, int iWidth, int iHeight)  //3 5 7
    {
        try {
            int i, j;
            int iDL = 0;//下面 白色的zu
            int iC;
            for (i = iHeight / 2; i < iHeight; i++) {
                iC = 0;
                for (j = 0; j < iWidth; j++)
                    if (aPix[i * iWidth + j] == 0) break;
                    else iC++;
                if (iC > iDL) iDL = iC;
            }
            int iUL = 0;
            for (i = 0; i < iHeight / 3; i++) {
                iC = 0;
                for (j = 0; j < iWidth; j++)
                    if (aPix[i * iWidth + j] == 0) break;
                    else iC++;
                if (iC > iUL) iUL = iC;
            }
            int iTB = 0, iBB = 0;
            for (i = 0; i < 3; i++) {
                for (j = 0; j < iWidth; j++) {
                    if (aPix[i * iWidth + j] == 0) iTB++;
                    if (aPix[(iHeight - 1 - i) * iWidth + j] == 0) iBB++;
                }
            }

            int iLeft = iWidth - 1, iRight = 0;
            for (i = 0; i < iWidth; i++)
                if (aPix[(iHeight - 1) * iWidth + i] == 0 || aPix[(iHeight - 2) * iWidth + i] == 0) {
                    if (iLeft > i) iLeft = i;
                    if (iRight < i) iRight = i;
                }

            float iRate = (float) iTB / (float) iBB;
        /*
        int iRW=0;
		for(i=iHeight/2;i<iHeight;i++)
		{
			iC=0;
			for(j=iWidth-1;j>0;j--)
				if(aPix[i*iWidth+j]==0) break;
				else iC++;
			if(iRW<iC) iRW=iC;
		}*/

            float fLWI = 0, fTmp;
            int iLastW = 0;
            int iFrom = iHeight - 1;
            int iTo = iHeight - (int) Math.ceil((double) iHeight / 4) - 1;
            for (i = iFrom; i >= iTo; i--) {
                iC = 0;
                for (j = iWidth - 1; j > 0; j--)
                    if (aPix[i * iWidth + j] == 0) {
                        if (iC > iLastW && iLastW > 0) {
                            fTmp = iC / iLastW;
                            if (fTmp > fLWI) fLWI = fTmp;
                        }
                        break;
                    } else iC++;
                iLastW = iC;
            }


            if (iUL < (float) iWidth / 4.0 && iDL > iWidth / 2) return "5";
            else if (iRate > 2 && iUL > iWidth / 2 && (iRight - iLeft) < iWidth / 2) return "7";
            else if (iDL >= iWidth / 2 && (iRight - iLeft) >= iWidth / 2 && fLWI == 0) return "3";


        } catch (Exception E) {
            Log.w("isThree", E.getMessage());
        }
        return "";
    }

    public static String isFour(int[] aPix, int iWidth, int iHeight) {
        try {
            int i, j;
            int iBL = 0;
            for (i = 0; i < iWidth; i++)
                if (aPix[(iHeight - 1) * iWidth + i] == 0 || aPix[(iHeight - 2) * iWidth + i] == 0 || aPix[(iHeight - 3) * iWidth + i] == 0)
                    iBL++;

            int iMW = 0;
            int iMY = 0;
            int iCount = 0;
            for (i = 0; i < iHeight; i++) {
                for (j = 0; j < iWidth; j++)
                    if (aPix[i * iWidth + j] == 0) iCount++;
                    else if (iCount > iMW) {
                        iMW = iCount;
                        iMY = i;
                        break;
                    }
                iCount = 0;
            }


            if (iBL < iWidth / 3.0 && iMY > iHeight / 2) return "4";

        } catch (Exception E) {
            Log.w("isThree", E.getMessage());
        }
        return "";
    }

    public static int[] getPostBorder(int[] aPix, int iWidth, int iHeight) {
        int[] aRet = new int[4];
        int iUseW = iWidth / 3;
        int[] aBlack = new int[iWidth];
        int[] aWBlack = new int[iWidth];
        int i, j, iAll = 0, iRight;
        for (i = 0; i < iWidth; i++) {
            aBlack[i] = 0;
            aWBlack[i] = 0;
        }
        for (i = 0; i < iWidth; i++)
            for (j = 0; j < iHeight; j++)
                if (aPix[j * iWidth + i] == 0) aBlack[i]++;
        for (i = 0; i < iUseW; i++) iAll += aBlack[i];
        aWBlack[0] = iAll;
        for (i = 1; i < iWidth; i++) {
            iRight = i + iUseW;
            if (iRight < iWidth) iAll += aBlack[iRight];
            iAll -= aBlack[i - 1];
            aWBlack[i] = iAll;
        }

        return aRet;
    }

    //检测身份证
//    public static boolean checkCertNo(String sNo) {
//        if (sNo == null) return false;
//        if (sNo.length() != 18) return false;
//        int i;
//        String sTmp = "";
//        for (i = 0; i < 18; i++) {
//            sTmp = sNo.substring(i, i + 1);
//            if (sTmp.compareTo("0") < 0 || sTmp.compareTo("9") > 0)
//                if (i != 17 || sTmp.compareTo("X") != 0) return false;
//
//        }
//        sTmp = sNo.substring(6, 14);
//        SimpleDateFormat oFormat = new SimpleDateFormat("yyyyMMdd");
//        try {
//            Date oDate = oFormat.parse(sTmp);
//        } catch (Exception e) {
//            return false;
//        }
//        int[] aRight = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};//权值数组
//        String[] aCheck = {"1", "0", "X", "9", "8", "7", "6", "5", "4", "3", "2"};
//        int iAll = 0;
//        for (i = 0; i < 17; i++)
//            iAll += (Integer.parseInt(sNo.substring(i, i + 1)) * aRight[i]);
//        iAll = iAll % 11;
//        if (sNo.substring(17, 18).compareTo(aCheck[iAll]) != 0) return false;
//        return true;
//    }

    public static Rect getCertRect(int iWidth, int iHeight) {
        Rect oRC = new Rect();
        oRC.left = 0;
        oRC.right = iWidth;
        int iH = iWidth * 540 / (2 * 856);
        oRC.top = iHeight / 2 - iH;
        oRC.bottom = iHeight / 2 + iH;
        return oRC;
    }

    public static Rect getCertNoRect(int iWidth, int iHeight) {
        Rect oRC = new Rect();
        int iTop = iHeight / 2 - iWidth * 540 / (2 * 856);
        oRC.top = iTop + iWidth * 420 / 856;
        oRC.bottom = iTop + iWidth * 500 / 856;
        oRC.left = iWidth * 280 / 856;
        oRC.right = iWidth * 800 / 856;
        return oRC;

    }

    public static String keyValue(String sValue) {
        int i = sValue.indexOf("-");
        return (i > 0) ? sValue.substring(0, i) : sValue;
    }

    public static String keyShow(String sValue) {
        int i = sValue.indexOf("-");
        return (i > 0) ? sValue.substring(i + 1) : sValue;
    }

    public static void setSpinValue(Spinner oSpin, String sValue) {
        SpinnerAdapter oAdapter = oSpin.getAdapter();
        String sKey = keyValue(sValue);
        int i, iCount = oAdapter.getCount();
        for (i = 0; i < iCount; i++)
            if (sKey.compareTo(keyValue(oAdapter.getItem(i).toString())) == 0) {
                oSpin.setSelection(i, true);
                break;
            }

    }

    public static String stringInc(String sNo) {
        int i, iLen;
        if (sNo.length() == 0) return "";
        boolean bCarry = true;
        char[] aNo = sNo.toCharArray();
        iLen = aNo.length;
        for (i = iLen - 1; i >= 0; i--) {
            if (!bCarry) break;
            if (aNo[i] == '9') {
                aNo[i] = '0';
                bCarry = true;
            } else {
                switch (aNo[i]) {
                    case '0':
                        aNo[i] = '1';
                        break;
                    case '1':
                        aNo[i] = '2';
                        break;
                    case '2':
                        aNo[i] = '3';
                        break;
                    case '3':
                        aNo[i] = '4';
                        break;
                    case '4':
                        aNo[i] = '5';
                        break;
                    case '5':
                        aNo[i] = '6';
                        break;
                    case '6':
                        aNo[i] = '7';
                        break;
                    case '7':
                        aNo[i] = '8';
                        break;
                    case '8':
                        aNo[i] = '9';
                        break;
                }
                bCarry = false;
            }
        }
        return String.valueOf(aNo);
    }

    public static String checkTelNo(String sNo) {
        String sTmp = sNo.trim();
        if (sNo.length() == 0) return "";
        if (sTmp.substring(0, 1).compareTo("0") == 0) {
            if (sTmp.length() > 12) return "长度超出最大值";
        } else if (sTmp.substring(0, 1).compareTo("1") == 0)
            if (sTmp.length() > 11) return "长度超出最大值";
        return "";
    }

    /**
     * 手机号验证
     *
     * @param str
     * @return 验证通过返回true
     */
    public static boolean isMobile(String str) {
        Pattern p = null;
        Matcher m = null;
        boolean b = false;
        p = Pattern.compile("^[1][3,4,5,7,8][0-9]{9}$"); // 验证手机号
        m = p.matcher(str);
        b = m.matches();
        return b;
    }

    /**
     * 电话号码验证
     *
     * @param str
     * @return 验证通过返回true
     */
    public static boolean isPhone(String str) {
        Pattern p1 = null, p2 = null;
        Matcher m = null;
        boolean b = false;
        p1 = Pattern.compile("^[0][1-9]{2,3}-[0-9]{5,10}$");  // 验证带区号的
        p2 = Pattern.compile("^[1-9]{1}[0-9]{5,8}$");         // 验证没有区号的
        if (str.length() > 9) {
            m = p1.matcher(str);
            b = m.matches();
        } else {
            m = p2.matcher(str);
            b = m.matches();
        }
        return b;
    }

    /**
     * 画文字
     * @param oBack
     * @param sText
     * @return
     */
    public static Bitmap drawText(Bitmap oBack, String sText) {
        if (sText.length() == 0) return oBack;
        Bitmap oBmp = Bitmap.createBitmap(oBack.getWidth(), oBack.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas oCanvas = new Canvas();
        oCanvas.setBitmap(oBmp);
        oCanvas.drawBitmap(oBack, 0, 0, null);//画背景
        //-----计算板的大小----------------------------

        Paint oPaint = new Paint();
        oPaint.setColor(Color.rgb(246, 134, 68));
        oPaint.setAntiAlias(true);//去除锯齿
        oPaint.setFilterBitmap(true);//对位图进行滤波处理
        String familyName = "宋体";
        Typeface oFont = Typeface.create(familyName, Typeface.BOLD);
        oPaint.setTypeface(oFont);
        int iSize = oBack.getWidth() / (sText.length()) - 10;
        oPaint.setTextSize(iSize);
        Rect oRect = new Rect();
        oPaint.getTextBounds(sText, 0, sText.length(), oRect);
        int iX = (oBmp.getWidth() - oRect.width()) / 2;
        int iY = (oBmp.getHeight() - oRect.height()) / 2;
        oCanvas.drawText(sText, iX, iY, oPaint);//写项目名称
        return oBmp;
    }

    public static boolean isNumeric(String str) {
        for (int i = str.length(); --i >= 0; ) {
            int chr = str.charAt(i);
            if (chr >= 48 && chr <= 57) return true;
            if (chr >= 97 && chr <= 122) {
                return true;
            }
        }
        return false;
    }

    /**
     * 根据需求的宽和高以及图片实际的宽和高计算SampleSize
     *
     * @param path
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static Bitmap caculateInSampleSize(String path, int reqWidth,
                                              int reqHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        int width = options.outWidth;
        int height = options.outHeight;
        int inSampleSize = 1;
        // 获得图片的宽和高，并不把图片加载到内存中
        if (width > reqWidth || height > reqHeight) {
            int widthRadio = Math.round(width * 1.0f / reqWidth);
            int heightRadio = Math.round(height * 1.0f / reqHeight);

            inSampleSize = Math.max(widthRadio, heightRadio);
        }
        options.inJustDecodeBounds = false;
        options.inSampleSize = inSampleSize;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        return bitmap;
    }
}







