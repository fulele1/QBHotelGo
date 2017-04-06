package com.YinanSoft.CardReaders.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpDownloader {

	public static boolean isConnect(Context context) {
		// ��ȡ�ֻ��������ӹ�����󣨰�����wi-fi,net�����ӵĹ���
		try {
			ConnectivityManager connectivity = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (connectivity != null) {
				// ��ȡ�������ӹ���Ķ���
				NetworkInfo info = connectivity.getActiveNetworkInfo();
				if (info != null && info.isConnected()) {
					// �жϵ�ǰ�����Ƿ��Ѿ�����
					if (info.getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			Log.v("error", e.toString());
		}
		return false;
	}

	/**
	 * �ú����������� 1�������ļ��ɹ�
	 * 
	 * @param urlstr
	 * @param path
	 * @param fileName
	 * @return
	 */
	public static int downFile(String urlstr, String path, String fileName) {
		InputStream inputStream = null;
		FileUnits fileUtils = new FileUnits();

		inputStream = getInputStreamFormUrl(urlstr);
		if(inputStream == null)
			return -1;
		File resultFile = fileUtils.writeToSDfromInput(path, fileName, inputStream);
		if (resultFile == null) 
			return -1;
		return 1;
	}

	/**
	 * ����URL�õ�������
	 * 
	 * @param urlstr
	 * @return
	 */
	public static InputStream getInputStreamFormUrl(String urlstr) {
		InputStream inputStream = null;
		try {
			URL url = new URL(urlstr);
			HttpURLConnection urlConn = (HttpURLConnection) url
					.openConnection();
			inputStream = urlConn.getInputStream();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return inputStream;
	}
}